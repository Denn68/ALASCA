package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineOperationI;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class			StartWashing
extends		ES_Event
implements	WashingMachineEventI
{
	private static final long serialVersionUID = 1L;

	protected final long duration;
	protected final double targetTemperature;

	public				StartWashing(
		Time timeOfOccurrence,
		long duration,
		double targetTemperature
		)
	{
		super(timeOfOccurrence, null);
		this.duration = duration;
		this.targetTemperature = targetTemperature;
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// if many events occur at the same time, StartWashing 
		// executed after SwitchOn but before SwitchOff
		if (e instanceof SwitchOnWashingMachine) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof WashingMachineOperationI :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "model instanceof WashingMachineOperationI ");

		WashingMachineOperationI wm = (WashingMachineOperationI)model;
		// Accept ON or OFF states - SIL models may receive events before state is synchronized
		assert	wm.getState() == WashingMachineState.ON ||
				wm.getState() == WashingMachineState.OFF :
				new NeoSim4JavaException(
						"model not in the right state, should be "
						+ "WashingMachineState.ON or OFF but is " + wm.getState());

		wm.startWashing(this.duration, this.targetTemperature);
	}
}