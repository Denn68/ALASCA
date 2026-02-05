package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(imported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SetDelayedStart.class,
		SuspendWashing.class,
		ResumeWashing.class
}, exported = {
		HeatingFinished.class
})
// HeatingFinished is emitted when water reaches target temperature
public class WashingMachineTemperatureSILModel
		extends AtomicHIOA
		implements SIL_WashingMachineOperationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String URI = "WashingMachineTemperatureModel";
	public static boolean VERBOSE = true;

	/** Température initiale de l'eau froide (en Celsius). */
	protected static final double INITIAL_WATER_TEMPERATURE = 15.0;

	/** Température de l'eau froide pour le refroidissement. */
	protected static final double COLD_WATER_TEMPERATURE = 15.0;

	/** Température de l'élément chauffant (en Celsius). */
	protected static final double HEATING_ELEMENT_TEMP = 100.0;

	/**
	 * Constante de transfert thermique pour le chauffage.
	 * Note: Le SIL utilise HOURS, le MIL utilise MINUTES.
	 * Réduit à 2 min pour chauffer de 15°C à 40°C en ~7 minutes de simulation
	 */
	protected static final double HEATING_TRANSFER_CONSTANT = 2.0 / 60.0; // 2 min en heures

	/**
	 * Constante de refroidissement quand la machine est OFF ou ON sans lavage.
	 * Plus grande = refroidissement plus lent.
	 */
	protected static final double COOLING_TRANSFER_CONSTANT = 30.0 / 60.0; // 30 min en heures

	/**
	 * Constante de refroidissement pendant le lavage.
	 * Plus grande car le tambour est fermé et l'eau est brassée.
	 * Réaliste: une machine à laver perd ~5°C par heure pendant le lavage.
	 */
	protected static final double WASHING_COOLING_CONSTANT = 5.0; // 5 heures (très lent)

	/** Seuil minimum de puissance pour considérer le chauffage effectif. */
	protected static final double POWER_HEAT_TRANSFER_TOLERANCE = 0.0001;

	/** Pas d'intégration (1 minute = 1/60 heure). */
	protected static final double STEP = 1.0 / 60.0; // 1 minute en heures

	protected static final double TEMPERATURE_TOLERANCE = 0.5;

	protected WashingMachineState currentState = WashingMachineState.OFF;
	protected double targetTemperature = 40.0;
	protected boolean heatingFinished = false;

	/** Durée du lavage en minutes (définie par startWashing). */
	protected double washingDurationMinutes = 0.0;
	/** Temps simulé de fin du lavage. */
	protected double washingEndTime = -1.0;
	/** Flag pour émettre l'événement WashingEnded. */
	protected boolean washingEnded = false;

	// Variables pour le départ différé (delayedStart)
	/**
	 * Temps simulé de début du chauffage pour le départ différé (-1 si pas de
	 * départ différé).
	 */
	protected double delayedStartTime = -1.0;
	/** Durée du lavage programmée pour le départ différé (en minutes). */
	protected double delayedWashingDurationMinutes = 0.0;
	/** Température cible pour le départ différé. */
	protected double delayedTargetTemperature = 0.0;

	protected final Duration integrationStep;
	protected Time start;
	protected double temperatureAcc;
	protected double meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	// Puissance de chauffage actuelle - simple double comme dans
	// KettleTemperatureSILModel
	protected double currentHeatingPower;

	/** current water temperature in Celsius - variable HIOA interne. */
	@InternalVariable(type = Double.class)
	protected final DerivableValue<Double> currentWaterTemperature = new DerivableValue<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineTemperatureSILModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * Compute the current heat transfer constant based on heating power.
	 * Same logic as MIL.
	 *
	 * @return the current heat transfer constant.
	 */
	protected double currentHeatTransferConstant() {
		// In HEATINGWATER state, assume full power heating regardless of variable
		// binding
		// This handles the case where ElectricitySILModel is not in the architecture
		if (this.currentState == WashingMachineState.HEATINGWATER) {
			// Fast heating: reach 40°C from 15°C in about 2-3 simulated minutes
			return HEATING_TRANSFER_CONSTANT; // Use base constant for fast heating
		}

		if (this.currentHeatingPower < POWER_HEAT_TRANSFER_TOLERANCE) {
			return HEATING_TRANSFER_CONSTANT * 10; // Very slow heating if no power
		}
		// Higher power = faster heating (lower constant)
		double powerRatio = this.currentHeatingPower / 2000.0; // Assuming 2000W max
		return HEATING_TRANSFER_CONSTANT / Math.max(0.1, powerRatio);
	}

	/**
	 * Compute the current derivative of the water temperature.
	 * Adapted from MIL.
	 *
	 * @param currentTemp current temperature of the water.
	 * @return the current derivative.
	 */
	protected double computeDerivatives(double currentTemp) {
		double derivative = 0.0;

		if (this.currentState == WashingMachineState.HEATINGWATER) {
			// Use effective target slightly above target for faster approach
			double effectiveTarget = Math.min(HEATING_ELEMENT_TEMP, this.targetTemperature + 5.0);
			derivative = (effectiveTarget - currentTemp) / this.currentHeatTransferConstant();
		} else if (this.currentState == WashingMachineState.WASHING) {
			// Refroidissement TRÈS lent pendant le lavage (tambour fermé, eau brassée)
			// Perte d'environ 5°C par heure
			derivative = (COLD_WATER_TEMPERATURE - currentTemp) / WASHING_COOLING_CONSTANT;
		} else {
			// Refroidissement normal quand OFF ou ON sans lavage
			derivative = (COLD_WATER_TEMPERATURE - currentTemp) / COOLING_TRANSFER_CONSTANT;
		}

		return derivative;
	}

	/**
	 * Compute the new temperature after a time step.
	 *
	 * @param deltaT elapsed time in hours
	 * @return the new temperature
	 */
	protected double computeNewTemperature(double deltaT) {
		double oldTemp = this.currentWaterTemperature.getValue();
		double derivative = this.currentWaterTemperature.getFirstDerivative();
		double newTemp = oldTemp + derivative * deltaT;

		this.temperatureAcc += ((oldTemp + newTemp) / 2.0) * deltaT;

		return newTemp;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams)
			throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
					AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	@Override
	public void initialiseState(Time initialTime) {
		this.temperatureAcc = 0.0;
		this.start = initialTime;
		this.currentState = WashingMachineState.OFF;
		this.targetTemperature = 40.0;
		this.heatingFinished = false;
		this.washingEnded = false;
		this.washingDurationMinutes = 0.0;
		this.washingEndTime = -1.0;
		this.currentHeatingPower = 2000.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		super.initialiseState(initialTime);
	}

	@Override
	public boolean useFixpointInitialiseVariables() {
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables() {
		int justInitialised = 0;
		int notInitialisedYet = 0;

		// Initialiser currentWaterTemperature - pas de dépendance externe
		if (!this.currentWaterTemperature.isInitialised()) {
			double derivative = this.computeDerivatives(INITIAL_WATER_TEMPERATURE);
			this.currentWaterTemperature.initialise(INITIAL_WATER_TEMPERATURE, derivative);
			justInitialised++;
		}

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = null;

		if (this.heatingFinished) {
			ret = new ArrayList<>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ret.add(new HeatingFinished(t));
			if (VERBOSE) {
				this.logMessage("Emitting HeatingFinished event - target temperature reached.");
			}
			this.heatingFinished = false;
		}

		if (this.washingEnded) {
			if (VERBOSE) {
				this.logMessage("WashingEnded state reached.");
			}
			this.washingEnded = false;
		}

		return ret;
	}

	@Override
	public Duration timeAdvance() {
		// If heatingFinished, need to emit event immediately
		if (this.heatingFinished) {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
		// Use integration step for continuous temperature simulation
		return this.integrationStep;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		double currentTime = this.getCurrentStateTime().getSimulatedTime();
		double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		double newDerivative = this.computeDerivatives(newTemp);

		Time t = new Time(currentTime, this.getSimulatedTimeUnit());
		this.currentWaterTemperature.setNewValue(newTemp, newDerivative, t);

		// Log de la température comme Kettle
		if (VERBOSE) {
			String mark = "";
			if (this.currentState == WashingMachineState.HEATINGWATER)
				mark = " (heating)";
			else if (this.currentState == WashingMachineState.WASHING)
				mark = " (washing)";
			else if (this.currentState == WashingMachineState.ON)
				mark = " (idle)";
			else
				mark = " (off)";

			this.logMessage(String.format("%.2f", currentTime) + mark + " : " +
					String.format("%.2f", newTemp) + " °C");
		}

		// Vérifier si le départ différé doit démarrer
		if (this.delayedStartTime > 0 && currentTime >= this.delayedStartTime) {
			if (this.currentState == WashingMachineState.ON) {
				if (VERBOSE) {
					this.logMessage("DelayedStart: delay elapsed, starting heating to " +
							this.delayedTargetTemperature + "°C");
				}
				this.targetTemperature = this.delayedTargetTemperature;
				this.washingDurationMinutes = this.delayedWashingDurationMinutes;
				this.currentState = WashingMachineState.HEATINGWATER;
				this.delayedStartTime = -1.0; // Reset pour ne pas redéclencher
			}
		}

		// Vérifier si le chauffage est terminé
		if (this.currentState == WashingMachineState.HEATINGWATER) {
			if (newTemp >= this.targetTemperature - TEMPERATURE_TOLERANCE) {
				if (VERBOSE) {
					this.logMessage("Target temperature " + this.targetTemperature +
							"°C reached (current: " + String.format("%.2f", newTemp) + "°C)");
				}
				this.heatingFinished = true;
				this.currentState = WashingMachineState.WASHING;

				// Calculer le temps de fin du lavage
				// washingDurationMinutes est en minutes, currentTime est en heures
				this.washingEndTime = currentTime + (this.washingDurationMinutes / 60.0);
				if (VERBOSE) {
					this.logMessage("Washing will end at simulated time " +
							String.format("%.2f", this.washingEndTime) + " hours");
				}
			}
		}

		// Vérifier si le lavage est terminé
		if (this.currentState == WashingMachineState.WASHING && this.washingEndTime > 0) {
			if (currentTime >= this.washingEndTime) {
				if (VERBOSE) {
					this.logMessage("Washing cycle completed at " +
							String.format("%.2f", currentTime) + " hours");
				}
				this.washingEnded = true;
				this.currentState = WashingMachineState.ON;
				this.washingEndTime = -1.0;
			}
		}

		if (VERBOSE) {
			String mark = this.currentState == WashingMachineState.HEATINGWATER ? " (heating)"
					: this.currentState == WashingMachineState.WASHING ? " (washing)"
							: this.currentState == WashingMachineState.ON ? " (on)" : " (-)";
			this.logMessage(String.format("%.4f%s : %.2f°C",
					currentTime, mark, newTemp));
		}

		super.userDefinedInternalTransition(elapsedTime);
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		if (VERBOSE) {
			this.logMessage("executing external event: " + ce.eventAsString());
		}

		// Update temperature first
		if (elapsedTime.getSimulatedDuration() > 0.0001) {
			double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
			double newDerivative = this.computeDerivatives(newTemp);
			Time t = new Time(
					this.getCurrentStateTime().getSimulatedTime() + elapsedTime.getSimulatedDuration(),
					this.getSimulatedTimeUnit());
			this.currentWaterTemperature.setNewValue(newTemp, newDerivative, t);
		}

		// Execute event
		ce.executeOn(this);

		if (ce instanceof WashingEnded) {
			this.currentState = WashingMachineState.ON;
			if (VERBOSE) {
				this.logMessage("Received WashingEnded. State forced to ON (Idle).");
			}
		}

		super.userDefinedExternalTransition(elapsedTime);
	}

	@Override
	public void endSimulation(Time endTime) {
		Duration d = endTime.subtract(this.start);
		if (d.getSimulatedDuration() > 0.0) {
			this.meanTemperature = this.temperatureAcc / d.getSimulatedDuration();
		}

		if (VERBOSE) {
			this.logMessage("simulation ends. Mean water temperature: " +
					String.format("%.2f", this.meanTemperature) + "°C");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Model state access for SIL
	// -------------------------------------------------------------------------

	/**
	 * For software-in-the-loop tests with simulation, return the current value
	 * of the {@code currentWaterTemperature} variable.
	 * 
	 * @return the current value of the water temperature.
	 */
	public VariableValue<Double> getCurrentTemperature() {
		return new VariableValue<Double>(
				this.currentWaterTemperature.getValue(),
				this.currentWaterTemperature.getTime());
	}

	// -------------------------------------------------------------------------
	// Operations (called by events)
	// -------------------------------------------------------------------------

	@Override
	public void switchOn() {
		this.currentState = WashingMachineState.ON;
	}

	@Override
	public void switchOff() {
		this.currentState = WashingMachineState.OFF;
		this.heatingFinished = false;
	}

	@Override
	public void startWashing(long duration, double targetTemp) {
		if (this.currentState == WashingMachineState.ON ||
				this.currentState == WashingMachineState.OFF) {
			this.currentState = WashingMachineState.HEATINGWATER;
			this.targetTemperature = targetTemp;
			this.washingDurationMinutes = duration; // Stocker la durée en minutes
			this.washingEndTime = -1.0; // Sera calculé quand le chauffage sera terminé
			this.heatingFinished = false;
			this.washingEnded = false;

			if (VERBOSE) {
				this.logMessage("Starting to heat water to " + targetTemp + "°C, " +
						"washing duration will be " + duration + " minutes");
			}

			// Check if already at target temperature
			if (this.currentWaterTemperature.isInitialised() &&
					this.currentWaterTemperature.getValue() >= targetTemp - TEMPERATURE_TOLERANCE) {
				if (VERBOSE) {
					this.logMessage("Already at target temperature, will emit HeatingFinished");
				}
				this.heatingFinished = true;
			}
		}
	}

	@Override
	public void setDelayedStart(long delay, long duration, double targetTemp) {
		// Stocker les paramètres du départ différé
		// delay et duration sont en minutes (convertis depuis ms dans
		// WashingMachineCyPhy)
		double currentTime = this.getCurrentStateTime().getSimulatedTime();

		// delay est en minutes, on le convertit en heures pour le temps simulé
		this.delayedStartTime = currentTime + (delay / 60.0);
		this.delayedWashingDurationMinutes = duration;
		this.delayedTargetTemperature = targetTemp;

		if (VERBOSE) {
			this.logMessage("DelayedStart programmed: delay=" + delay + " min, " +
					"washing=" + duration + " min, target=" + targetTemp + "°C");
			this.logMessage("Heating will start at simulated time " +
					String.format("%.2f", this.delayedStartTime) + " hours");
		}
	}

	@Override
	public void suspendWashing() {
		if (this.currentState == WashingMachineState.HEATINGWATER ||
				this.currentState == WashingMachineState.WASHING) {
			this.currentState = WashingMachineState.ON;
		}
	}

	@Override
	public void resumeWashing() {
		if (this.currentState == WashingMachineState.ON) {
			// Resume heating if not at target temperature
			if (this.currentWaterTemperature.getValue() < this.targetTemperature - TEMPERATURE_TOLERANCE) {
				this.currentState = WashingMachineState.HEATINGWATER;
			} else {
				this.currentState = WashingMachineState.WASHING;
			}
		}
	}

	@Override
	public void setCurrentPowerLevel(double power) {
		// Power is handled via imported variable
	}

	@Override
	public WashingMachineState getState() {
		return this.currentState;
	}

	@Override
	public void heatingFinished() {
		// This model generates HeatingFinished, doesn't receive it
		// The state transition is handled in userDefinedInternalTransition
		if (VERBOSE) {
			this.logMessage("heatingFinished() called - state is now WASHING");
		}
		this.currentState = WashingMachineState.WASHING;
	}

	/**
	 * Called when WashingEnded event is received from ElectricityModel.
	 * This method is called via executeOn from the event.
	 */
	public void washingEnded() {
		if (VERBOSE) {
			this.logMessage("WashingEnded received - returning to ON state");
		}
		this.currentState = WashingMachineState.ON;
	}

	// -------------------------------------------------------------------------
	// Report
	// -------------------------------------------------------------------------

	public static class WashingMachineTemperatureReport
			implements SimulationReportI, GlobalReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double meanTemperature;

		public WashingMachineTemperatureReport(String modelURI, double meanTemperature) {
			this.modelURI = modelURI;
			this.meanTemperature = meanTemperature;
		}

		@Override
		public String getModelURI() {
			return this.modelURI;
		}

		@Override
		public String printout(String indent) {
			return indent + "WashingMachine Water Temperature Report:\n" +
					indent + "  Mean temperature: " + String.format("%.2f", this.meanTemperature) + "°C\n";
		}
	}

	@Override
	public SimulationReportI getFinalReport() {
		return new WashingMachineTemperatureReport(this.getURI(), this.meanTemperature);
	}
}
