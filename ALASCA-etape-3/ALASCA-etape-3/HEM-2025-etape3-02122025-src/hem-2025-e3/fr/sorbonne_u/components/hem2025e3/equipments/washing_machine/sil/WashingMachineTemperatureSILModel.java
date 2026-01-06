package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

@ModelExternalEvents(imported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SuspendWashing.class,
		ResumeWashing.class
	})
public class			WashingMachineTemperatureSILModel
extends		AtomicModel
implements	SIL_WashingMachineOperationI
{
	private static final long serialVersionUID = 1L;
	public static final String	URI = "WashingMachineTemperatureModel";

	protected double				currentWaterTemperature;
	protected WashingMachineState	currentState;
	protected double				targetTemp;
	/** integration step as a duration, including the time unit.			*/
	protected final Duration	integrationStep;
	protected static double		STEP = 60.0/3600.0;	// 60 seconds

	public				WashingMachineTemperatureSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.currentState = WashingMachineState.OFF;
		this.currentWaterTemperature = 20.0; // Temp ambiante eau froide
		this.targetTemp = 20.0;
		this.getSimulationEngine().toggleDebugMode();
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		// Update temperature based on elapsed time and state BEFORE executing new event
		double hours = elapsedTime.getSimulatedDuration();
		
		if (this.currentState == WashingMachineState.HEATINGWATER) {
			// Chauffe : +10 degrés par 5 mins (exemple arbitraire)
			this.currentWaterTemperature += (120.0 * hours); 
			if (this.currentWaterTemperature > 90.0) this.currentWaterTemperature = 90.0;
		} else {
			// Refroidissement lent vers 20°C
			double delta = this.currentWaterTemperature - 20.0;
			this.currentWaterTemperature -= (delta * 0.1 * hours);
		}

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		Event ce = (Event) currentEvents.get(0);
		ce.executeOn(this);
		
		this.logMessage("Water Temp Update: " + String.format("%.2f", this.currentWaterTemperature) + "°C\n");
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("Final Water Temp: " + this.currentWaterTemperature + "\n");
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Operations
	// -------------------------------------------------------------------------

	@Override public void switchOn() { this.currentState = WashingMachineState.ON; }
	@Override public void switchOff() { this.currentState = WashingMachineState.OFF; }
	
	@Override 
	public void startWashing(long duration, double targetTemp) { 
		this.currentState = WashingMachineState.HEATINGWATER;
		this.targetTemp = targetTemp;
	}
	
	@Override public void setDelayedStart(long d, long du, double t) {}
	@Override public void suspendWashing() { this.currentState = WashingMachineState.ON; }
	@Override public void resumeWashing() { this.currentState = WashingMachineState.WASHING; } // Ou Heating
	@Override public void setCurrentPowerLevel(double power) {}
	@Override public WashingMachineState getState() { return this.currentState; }

	@Override
	public Duration timeAdvance() {
		return this.integrationStep;
	}
}