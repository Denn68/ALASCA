package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

/**
 * The class <code>WashingMachineElectricitySILModel</code> defines a simulation
 * model for the electricity consumption of a washing machine.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * This model tracks the electricity consumption based on the washing machine state.
 * It exports currentIntensity and currentHeatingPower variables.
 * It handles HeatingFinished events to transition from HEATINGWATER to WASHING.
 * It emits WashingEnded when washing duration is complete, and StartWashing
 * when delayed start triggers.
 * </p>
 *
 * <ul>
 * <li>Imported events: SwitchOnWashingMachine, SwitchOffWashingMachine, StartWashing,
 *     SetDelayedStart, SuspendWashing, ResumeWashing, SetPowerWashingMachine,
 *     HeatingFinished</li>
 * <li>Exported events: WashingEnded, StartWashing</li>
 * <li>Exported variables: currentIntensity, currentHeatingPower</li>
 * </ul>
 */
@ModelExternalEvents(imported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SetDelayedStart.class,
		SuspendWashing.class,
		ResumeWashing.class,
		SetPowerWashingMachine.class,
		HeatingFinished.class
	}, exported = {
		WashingEnded.class,
		StartWashing.class
	})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
public class WashingMachineElectricitySILModel
extends		AtomicHIOA
implements	SIL_WashingMachineOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String URI = "WashingMachineElectricityModel";
	public static boolean VERBOSE = true;

	// Power constants
	protected static final double TENSION = 220.0;
	protected static final double STANDBY_CONSUMPTION = 5.0;    // Watts
	protected static final double WASHING_CONSUMPTION = 400.0;  // Watts
	protected static final double HEATING_CONSUMPTION = 2000.0; // Watts

	// State variables
	protected WashingMachineState currentState = WashingMachineState.OFF;
	protected WashingMachineState stateBeforeSuspension = null;
	protected boolean consumptionHasChanged = false;
	protected double totalConsumption;

	// Timing for delayed start and washing duration
	protected Duration remainingTimeInCurrentPhase;
	protected Duration washingDuration;
	protected boolean isDelayRunning = false;
	protected boolean delayedStartTriggered = false;
	protected boolean washingEnded = false;

	// Programmed values for delayed start
	protected long programmedDuration;
	protected double programmedTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this);

	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentHeatingPower = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS Simulation Protocol
	// -------------------------------------------------------------------------

	@Override
	public void setSimulationRunParameters(Map<String, Object> simParams)
			throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
					AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	@Override
	public void initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = WashingMachineState.OFF;
		this.stateBeforeSuspension = null;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;
		this.remainingTimeInCurrentPhase = null;
		this.washingDuration = null;
		this.isDelayRunning = false;
		this.delayedStartTriggered = false;
		this.washingEnded = false;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	@Override
	public boolean useFixpointInitialiseVariables()
	{
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		int initialised = 0;

		if (!this.currentIntensity.isInitialised()) {
			this.currentIntensity.initialise(0.0);
			initialised++;
		}
		if (!this.currentHeatingPower.isInitialised()) {
			this.currentHeatingPower.initialise(0.0);
			initialised++;
		}

		return new Pair<>(initialised, 0);
	}

	@Override
	public ArrayList<EventI> output()
	{
		ArrayList<EventI> ret = null;

		// Case 1: Delayed start triggered -> emit StartWashing
		if (this.delayedStartTriggered) {
			ret = new ArrayList<>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ret.add(new StartWashing(t, this.programmedDuration, this.programmedTemperature));

			if (VERBOSE) {
				this.logMessage("Delay elapsed. Emitting StartWashing event.");
			}
			this.delayedStartTriggered = false;
		}

		// Case 2: Washing ended -> emit WashingEnded
		if (this.washingEnded) {
			if (ret == null) ret = new ArrayList<>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ret.add(new WashingEnded(t));

			if (VERBOSE) {
				this.logMessage("Emitting WashingEnded event.");
			}
			this.washingEnded = false;
		}

		return ret;
	}

	@Override
	public Duration timeAdvance()
	{
		// Immediate transition for consumption update or event emission
		if (this.consumptionHasChanged || this.delayedStartTriggered || this.washingEnded) {
			return Duration.zero(this.getSimulatedTimeUnit());
		}

		// Waiting for delay or washing duration
		if (this.remainingTimeInCurrentPhase != null) {
			return this.remainingTimeInCurrentPhase;
		}

		return Duration.INFINITY;
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// Update consumption based on elapsed time
		double durationInHours = elapsedTime.getSimulatedDuration();
		double power = this.currentIntensity.getValue() * TENSION;
		this.totalConsumption += (power * durationInHours) / 1000.0;

		// Update remaining time if applicable
		if (this.remainingTimeInCurrentPhase != null &&
			!this.remainingTimeInCurrentPhase.equals(Duration.INFINITY)) {
			this.remainingTimeInCurrentPhase =
					this.remainingTimeInCurrentPhase.subtract(elapsedTime);
		}

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		if (VERBOSE) {
			this.logMessage("executing external event: " + ce.eventAsString());
		}

		// Handle HeatingFinished specially
		if (ce instanceof HeatingFinished) {
			if (this.currentState == WashingMachineState.HEATINGWATER) {
				if (VERBOSE) {
					this.logMessage("HeatingFinished received. Switching to WASHING.");
				}
				this.currentState = WashingMachineState.WASHING;

				// Start the washing timer
				this.remainingTimeInCurrentPhase = this.washingDuration;
				this.washingDuration = null;

				this.consumptionHasChanged = true;
			}
		} else {
			// Execute other events normally
			ce.executeOn(this);
		}
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		boolean timeElapsed = elapsedTime.getSimulatedDuration() > 0.000001;

		if (timeElapsed && this.remainingTimeInCurrentPhase != null) {
			// Case 1: Delay ended
			if (this.isDelayRunning) {
				if (VERBOSE) {
					this.logMessage("Delay ended. Triggering StartWashing.");
				}
				this.isDelayRunning = false;
				this.remainingTimeInCurrentPhase = null;
				this.delayedStartTriggered = true;

				// Also update local state
				this.currentState = WashingMachineState.HEATINGWATER;
				this.washingDuration = new Duration((double) this.programmedDuration,
						this.getSimulatedTimeUnit());
				this.consumptionHasChanged = true;
			}
			// Case 2: Washing duration ended
			else if (this.currentState == WashingMachineState.WASHING) {
				if (VERBOSE) {
					this.logMessage("Washing finished. Returning to ON state.");
				}
				this.currentState = WashingMachineState.ON;
				this.remainingTimeInCurrentPhase = null;
				this.washingEnded = true;
				this.consumptionHasChanged = true;
			}
		}

		// Update intensity and heating power based on current state
		if (this.consumptionHasChanged) {
			Time t = this.getCurrentStateTime();
			double newIntensity = 0.0;
			double heatingPower = 0.0;

			switch (this.currentState) {
				case OFF:
					newIntensity = 0.0;
					heatingPower = 0.0;
					break;
				case ON:
					newIntensity = STANDBY_CONSUMPTION / TENSION;
					heatingPower = 0.0;
					break;
				case HEATINGWATER:
					newIntensity = HEATING_CONSUMPTION / TENSION;
					heatingPower = HEATING_CONSUMPTION;
					break;
				case WASHING:
					newIntensity = WASHING_CONSUMPTION / TENSION;
					heatingPower = 0.0;
					break;
			}

			this.currentIntensity.setNewValue(newIntensity, t);
			this.currentHeatingPower.setNewValue(heatingPower, t);
			this.consumptionHasChanged = false;

			if (VERBOSE) {
				this.logMessage("Intensity: " + String.format("%.2f", newIntensity) +
						" A, Heating Power: " + heatingPower + " W");
			}
		}
	}

	@Override
	public void endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		double power = this.currentIntensity.getValue() * TENSION;
		this.totalConsumption += (power * d.getSimulatedDuration()) / 1000.0;

		if (VERBOSE) {
			this.logMessage("simulation ends. Total consumption: " +
					String.format("%.4f", this.totalConsumption) + " kWh");
		}
		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI getFinalReport()
	{
		return new WashingMachineElectricityReport(URI, this.totalConsumption);
	}

	// -------------------------------------------------------------------------
	// Operations (Called by Events)
	// -------------------------------------------------------------------------

	@Override
	public void switchOn()
	{
		if (this.currentState == WashingMachineState.OFF) {
			this.currentState = WashingMachineState.ON;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void switchOff()
	{
		this.currentState = WashingMachineState.OFF;
		this.remainingTimeInCurrentPhase = null;
		this.washingDuration = null;
		this.isDelayRunning = false;
		this.consumptionHasChanged = true;
	}

	@Override
	public void startWashing(long duration, double targetTemperature)
	{
		if (this.currentState == WashingMachineState.ON) {
			// Cancel any delay
			if (this.isDelayRunning) {
				if (VERBOSE) {
					this.logMessage("Manual start: cancelling delayed start.");
				}
				this.isDelayRunning = false;
			}

			this.currentState = WashingMachineState.HEATINGWATER;

			// Wait for HeatingFinished from temperature model
			this.remainingTimeInCurrentPhase = Duration.INFINITY;

			// Store washing duration for later (after heating)
			this.washingDuration = new Duration((double) duration, this.getSimulatedTimeUnit());

			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void setDelayedStart(long delay, long washingDuration, double targetTemperature)
	{
		if (this.currentState == WashingMachineState.ON && !this.isDelayRunning) {
			if (VERBOSE) {
				this.logMessage("Delayed start programmed for " + delay + " minutes.");
			}

			this.isDelayRunning = true;
			this.programmedDuration = washingDuration;
			this.programmedTemperature = targetTemperature;

			// Wait for delay
			this.remainingTimeInCurrentPhase = new Duration((double) delay,
					this.getSimulatedTimeUnit());
		}
	}

	@Override
	public void suspendWashing()
	{
		if (this.currentState == WashingMachineState.WASHING ||
			this.currentState == WashingMachineState.HEATINGWATER) {
			this.stateBeforeSuspension = this.currentState;
			this.currentState = WashingMachineState.ON;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void resumeWashing()
	{
		if (this.currentState == WashingMachineState.ON && this.stateBeforeSuspension != null) {
			this.currentState = this.stateBeforeSuspension;
			this.stateBeforeSuspension = null;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void setCurrentPowerLevel(double power)
	{
		// Not used in this simple model
	}

	@Override
	public WashingMachineState getState()
	{
		return this.currentState;
	}

	// -------------------------------------------------------------------------
	// Report Class
	// -------------------------------------------------------------------------

	public static class WashingMachineElectricityReport implements SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption;

		public WashingMachineElectricityReport(String modelURI, double totalConsumption)
		{
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String getModelURI() { return this.modelURI; }

		@Override
		public String printout(String indent)
		{
			return indent + "WashingMachine Electricity Report:\n" +
				   indent + "  Total consumption: " + String.format("%.4f", this.totalConsumption) + " kWh\n";
		}
	}
}
