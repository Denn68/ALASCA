package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
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

@ModelExternalEvents(imported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SuspendWashing.class,
		ResumeWashing.class,
		WashingEnded.class
	}, exported = {
		HeatingFinished.class
	})
@ModelImportedVariable(name = "currentHeatingPower", type = Double.class)
public class WashingMachineTemperatureSILModel
extends		AtomicHIOA
implements	SIL_WashingMachineOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String URI = "WashingMachineTemperatureModel";
	public static boolean VERBOSE = true;

	protected static final double INITIAL_WATER_TEMPERATURE = 15.0;

	protected static final double HEATING_ELEMENT_TEMP = 100.0;

	protected static final double HEATING_TRANSFER_CONSTANT = 50.0;

	protected static final double HEAT_LOSS_CONSTANT = 200.0;

	protected static final double ROOM_TEMPERATURE = 20.0;

	protected static final double STEP = 60.0 / 3600.0; // 60 seconds

	protected static final double TEMPERATURE_TOLERANCE = 0.5;

	protected WashingMachineState currentState = WashingMachineState.OFF;
	protected double targetTemperature = 40.0;
	protected boolean heatingFinished = false;

	protected final Duration integrationStep;
	protected Time start;
	protected double temperatureAcc;
	protected double meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ImportedVariable(type = Double.class)
	protected Value<Double> currentHeatingPower;

	@InternalVariable(type = Double.class)
	protected final DerivableValue<Double> currentWaterTemperature =
												new DerivableValue<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineTemperatureSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	protected double computeDerivatives(double current)
	{
		double derivative = 0.0;

		if (this.currentState == WashingMachineState.HEATINGWATER &&
				this.currentHeatingPower != null &&
				this.currentHeatingPower.getValue() > 0.0) {
			derivative = (HEATING_ELEMENT_TEMP - current) / HEATING_TRANSFER_CONSTANT;
		}

		derivative += (ROOM_TEMPERATURE - current) / HEAT_LOSS_CONSTANT;

		return derivative;
	}

	/**
	 * Compute the new temperature after a time step.
	 *
	 * @param deltaT elapsed time in hours
	 * @return the new temperature
	 */
	protected double computeNewTemperature(double deltaT)
	{
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
		this.temperatureAcc = 0.0;
		this.start = initialTime;
		this.currentState = WashingMachineState.OFF;
		this.targetTemperature = 40.0;
		this.heatingFinished = false;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		super.initialiseState(initialTime);
	}

	@Override
	public boolean useFixpointInitialiseVariables()
	{
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;

		if (!this.currentWaterTemperature.isInitialised()) {
			if (this.currentHeatingPower == null || this.currentHeatingPower.isInitialised()) {
				double derivative = this.computeDerivatives(INITIAL_WATER_TEMPERATURE);
				this.currentWaterTemperature.initialise(INITIAL_WATER_TEMPERATURE, derivative);
				justInitialised++;
			} else {
				notInitialisedYet++;
			}
		}

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	@Override
	public ArrayList<EventI> output()
	{
		ArrayList<EventI> ret = null;

		if (this.heatingFinished) {
			ret = new ArrayList<>();
			Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
			ret.add(new HeatingFinished(t));

			if (VERBOSE) {
				this.logMessage("Emitting HeatingFinished event.");
			}
			this.heatingFinished = false;
		}

		return ret;
	}

	@Override
	public Duration timeAdvance()
	{
		if (this.heatingFinished) {
			return Duration.zero(this.getSimulatedTimeUnit());
		}
		return this.integrationStep;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime)
	{
		double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		double newDerivative = this.computeDerivatives(newTemp);

		this.currentWaterTemperature.setNewValue(
				newTemp,
				newDerivative,
				new Time(this.getCurrentStateTime().getSimulatedTime(),
						 this.getSimulatedTimeUnit()));

		/*if (this.currentState == WashingMachineState.HEATINGWATER) {
			if (newTemp >= this.targetTemperature - TEMPERATURE_TOLERANCE) {
				if (VERBOSE) {
					this.logMessage("Target temperature " + this.targetTemperature +
							"°C reached (current: " + String.format("%.2f", newTemp) + "°C)");
				}
				this.heatingFinished = true;
			}
		}*/
		if (this.currentState == WashingMachineState.HEATINGWATER) {
	        if (newTemp >= this.targetTemperature - TEMPERATURE_TOLERANCE) {
	        	if (VERBOSE) {
					this.logMessage("Target temperature " + this.targetTemperature +
							"°C reached (current: " + String.format("%.2f", newTemp) + "°C)");
				}
	            this.heatingFinished = true;
	            
	            this.currentState = WashingMachineState.WASHING; 
	            
	        }
	    }

		if (VERBOSE) {
			String mark = this.currentState == WashingMachineState.HEATINGWATER ? " (heating)" :
						  this.currentState == WashingMachineState.WASHING ? " (washing)" :
							  this.currentState == WashingMachineState.ON ? " (on)" : " (-)";
			this.logMessage(String.format("%.4f%s : %.2f°C",
					this.getCurrentStateTime().getSimulatedTime(), mark, newTemp));
		}

		super.userDefinedInternalTransition(elapsedTime);
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
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
			this.currentWaterTemperature.setNewValue(
					newTemp,
					newDerivative,
					new Time(this.getCurrentStateTime().getSimulatedTime()
								+ elapsedTime.getSimulatedDuration(),
							 this.getSimulatedTimeUnit()));
		}

		// Execute event
		ce.executeOn(this);
		
		if(ce instanceof WashingEnded) {
			this.currentState = WashingMachineState.ON;
            if (VERBOSE) {
                this.logMessage("Received WashingEnded. State forced to ON (Idle).");
            }
		}

		super.userDefinedExternalTransition(elapsedTime);
	}

	@Override
	public void endSimulation(Time endTime)
	{
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
	// Operations (called by events)
	// -------------------------------------------------------------------------

	@Override
	public void switchOn()
	{
		this.currentState = WashingMachineState.ON;
	}

	@Override
	public void switchOff()
	{
		this.currentState = WashingMachineState.OFF;
		this.heatingFinished = false;
	}

	@Override
	public void startWashing(long duration, double targetTemp)
	{
		if (this.currentState == WashingMachineState.ON ||
			this.currentState == WashingMachineState.OFF) {
			this.currentState = WashingMachineState.HEATINGWATER;
			this.targetTemperature = targetTemp;
			this.heatingFinished = false;

			if (VERBOSE) {
				this.logMessage("Starting to heat water to " + targetTemp + "°C");
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
	public void setDelayedStart(long delay, long duration, double targetTemp)
	{
		// Temperature model doesn't handle delayed start directly
		// It will receive StartWashing from ElectricityModel when delay ends
	}

	@Override
	public void suspendWashing()
	{
		if (this.currentState == WashingMachineState.HEATINGWATER ||
			this.currentState == WashingMachineState.WASHING) {
			this.currentState = WashingMachineState.ON;
		}
	}

	@Override
	public void resumeWashing()
	{
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
	public void setCurrentPowerLevel(double power)
	{
		// Power is handled via imported variable
	}

	@Override
	public WashingMachineState getState()
	{
		return this.currentState;
	}

	/**
	 * Called when WashingEnded event is received from ElectricityModel.
	 * This method is called via executeOn from the event.
	 */
	public void washingEnded()
	{
		if (VERBOSE) {
			this.logMessage("WashingEnded received - returning to ON state");
		}
		this.currentState = WashingMachineState.ON;
	}

	// -------------------------------------------------------------------------
	// Report
	// -------------------------------------------------------------------------

	public static class WashingMachineTemperatureReport
	implements SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double meanTemperature;

		public WashingMachineTemperatureReport(String modelURI, double meanTemperature)
		{
			this.modelURI = modelURI;
			this.meanTemperature = meanTemperature;
		}

		@Override
		public String getModelURI() { return this.modelURI; }

		@Override
		public String printout(String indent)
		{
			return indent + "WashingMachine Water Temperature Report:\n" +
				   indent + "  Mean temperature: " + String.format("%.2f", this.meanTemperature) + "°C\n";
		}
	}

	@Override
	public SimulationReportI getFinalReport()
	{
		return new WashingMachineTemperatureReport(this.getURI(), this.meanTemperature);
	}
}
