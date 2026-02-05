package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
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
		SetDelayedStart.class,
		SuspendWashing.class,
		ResumeWashing.class,
		SetPowerWashingMachine.class,
		HeatingFinished.class
}, exported = {
		SwitchOnWashingMachine.class,
		SwitchOffWashingMachine.class,
		StartWashing.class,
		SetDelayedStart.class,
		SuspendWashing.class,
		ResumeWashing.class,
		SetPowerWashingMachine.class
})
public class WashingMachineStateSILModel
		extends AtomicModel
		implements SIL_WashingMachineOperationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static final String URI = "WashingMachineStateModel";

	protected WashingMachineState currentState;
	protected EventI toBeReemitted;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public WashingMachineStateSILModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// DEVS Simulation Protocol
	// -------------------------------------------------------------------------

	@Override
	public void initialiseState(Time initialTime) {
		super.initialiseState(initialTime);
		this.currentState = WashingMachineState.OFF;
		this.toBeReemitted = null;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins.\n");
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		// Exécution de l'événement sur ce modèle pour mettre à jour l'état
		ce.executeOn(this);

		// HeatingFinished is an internal event - don't re-emit it
		// Only re-emit events that come from the component
		if (!(ce instanceof HeatingFinished)) {
			this.toBeReemitted = ce;
		}

		this.logMessage("performing an external transition on " + ce.getClass().getSimpleName()
				+ " -> New State: " + this.currentState + "\n");
	}

	@Override
	public ArrayList<EventI> output() {
		ArrayList<EventI> ret = null;
		if (this.toBeReemitted != null) {
			ret = new ArrayList<EventI>();
			ret.add(this.toBeReemitted);
			this.logMessage("output sends " + ret + "\n");
			this.toBeReemitted = null;
		}
		return ret;
	}

	@Override
	public Duration timeAdvance() {
		if (this.toBeReemitted != null) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	// -------------------------------------------------------------------------
	// Methods from SIL_WashingMachineOperationI
	// -------------------------------------------------------------------------

	@Override
	public void switchOn() {
		if (this.currentState == WashingMachineState.OFF) {
			this.currentState = WashingMachineState.ON;
		}
	}

	@Override
	public void switchOff() {
		this.currentState = WashingMachineState.OFF;
	}

	@Override
	public void startWashing(long duration, double targetTemp) {
		if (this.currentState == WashingMachineState.ON) {
			// Simplification : on passe directement en HEATING ou WASHING selon logique
			// Ici on simule que ça commence par chauffer
			this.currentState = WashingMachineState.HEATINGWATER;
		}
	}

	@Override
	public void setDelayedStart(long delay, long duration, double targetTemp) {
		// Change juste des variables internes, ne change pas l'état visible (reste ON)
	}

	@Override
	public WashingMachineState getState() {
		return this.currentState;
	}

	@Override
	public void suspendWashing() {
		if (this.currentState == WashingMachineState.WASHING ||
				this.currentState == WashingMachineState.HEATINGWATER) {
			this.currentState = WashingMachineState.ON; // Retour en veille/pause
		}
	}

	@Override
	public void resumeWashing() {
		if (this.currentState == WashingMachineState.ON) {
			this.currentState = WashingMachineState.WASHING; // Reprise
		}
	}

	@Override
	public void setCurrentPowerLevel(double power) {
		// Not used in state model
	}

	/**
	 * Called when heating is finished and washing can begin.
	 */
	@Override
	public void heatingFinished() {
		if (this.currentState == WashingMachineState.HEATINGWATER) {
			this.currentState = WashingMachineState.WASHING;
			this.logMessage("HeatingFinished received -> New State: WASHING\n");
		}
	}

	@Override
	public void endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time endTime) {
		this.logMessage("simulation ends.\n");
		super.endSimulation(endTime);
	}
}