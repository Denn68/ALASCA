package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
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
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(imported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SuspendWashing.class,
		ResumeWashing.class,
		SetPowerWashingMachine.class
	})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
public class			WashingMachineElectricitySILModel
extends		AtomicHIOA
implements	SIL_WashingMachineOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String	URI = "WashingMachineElectricityModel";
	public static boolean		VERBOSE = true;

	protected static final double TENSION = 220.0; 

	protected static final double	STANDBY_CONSUMPTION = 5.0; 
	protected static final double	WASHING_CONSUMPTION = 400.0; 
	protected static final double	HEATING_CONSUMPTION = 2000.0; 

	protected WashingMachineState	currentState = WashingMachineState.OFF;
	protected boolean				consumptionHasChanged = false;
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				WashingMachineElectricitySILModel(
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
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = WashingMachineState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.\n");
		}
	}

	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		Pair<Integer, Integer> ret = null;

		if (!this.currentIntensity.isInitialised()) {
			this.currentIntensity.initialise(0.0);
			ret = new Pair<>(1, 0);
		} else {
			ret = new Pair<>(0, 0);
		}
		return ret;
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.consumptionHasChanged) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);
		
		double durationInHours = elapsedTime.getSimulatedDuration(); 
		double power = this.currentIntensity.getValue() * TENSION;
		this.totalConsumption += (power * durationInHours) / 1000.0;

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		ce.executeOn(this); // C'est l'appel des méthodes Operations qui mettra à jour consumptionHasChanged
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		if (this.consumptionHasChanged) {
			Time t = this.getCurrentStateTime();
			double newIntensity = 0.0;

			switch (this.currentState) {
				case OFF: 
					newIntensity = 0.0; 
					break;
				case ON: 
					newIntensity = STANDBY_CONSUMPTION / TENSION; 
					break;
				case WASHING: 
					newIntensity = WASHING_CONSUMPTION / TENSION; 
					break;
				case HEATINGWATER: 
					newIntensity = HEATING_CONSUMPTION / TENSION; 
					break;
			}
			
			this.currentIntensity.setNewValue(newIntensity, t);
			this.consumptionHasChanged = false;
			
			if (VERBOSE) {
				this.logMessage("Electricity Intensity Update: " + newIntensity + " A\n");
			}
		}
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		double power = this.currentIntensity.getValue() * TENSION;
		this.totalConsumption += (power * d.getSimulatedDuration()) / 1000.0;
		
		if (VERBOSE) {
			this.logMessage("simulation ends.\n");
		}
		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new WashingMachineElectricityReport(URI, this.totalConsumption);
	}

	// -------------------------------------------------------------------------
	// Operations (Called by Events)
	// -------------------------------------------------------------------------

	@Override
	public void switchOn() {
		if (this.currentState != WashingMachineState.ON) {
			this.currentState = WashingMachineState.ON;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void switchOff() {
		if (this.currentState != WashingMachineState.OFF) {
			this.currentState = WashingMachineState.OFF;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void startWashing(long duration, double targetTemperature) {
		if (this.currentState != WashingMachineState.HEATINGWATER) {
			this.currentState = WashingMachineState.HEATINGWATER;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void setDelayedStart(long delay, long washingDuration, double targetTemperature) {
		// Pas de changement de consommation immédiat
	}

	@Override
	public void suspendWashing() {
		if (this.currentState != WashingMachineState.ON) {
			this.currentState = WashingMachineState.ON;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void resumeWashing() {
		if (this.currentState != WashingMachineState.WASHING) {
			this.currentState = WashingMachineState.WASHING;
			this.consumptionHasChanged = true;
		}
	}

	@Override
	public void setCurrentPowerLevel(double power) {
		// Si on gérait la puissance variable, on mettrait consumptionHasChanged = true ici
	}

	@Override
	public WashingMachineState getState() {
		return this.currentState;
	}

	// -------------------------------------------------------------------------
	// Report Class
	// -------------------------------------------------------------------------
	public static class WashingMachineElectricityReport implements SimulationReportI, GlobalReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption;

		public WashingMachineElectricityReport(String modelURI, double totalConsumption) {
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}
		
		@Override public String getModelURI() { return this.modelURI; }
		@Override public String printout(String indent) {
			return indent + "WashingMachine Consumption: " + this.totalConsumption + " kWh\n";
		}
	}
}