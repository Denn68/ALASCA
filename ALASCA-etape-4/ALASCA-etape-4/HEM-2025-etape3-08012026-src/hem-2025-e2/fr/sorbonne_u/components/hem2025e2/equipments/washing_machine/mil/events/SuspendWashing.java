package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineOperationI;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class			SuspendWashing
extends		ES_Event
implements	WashingMachineEventI
{
	private static final long serialVersionUID = 1L;

	public				SuspendWashing(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return true;
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof WashingMachineOperationI :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "model instanceof WashingMachineOperationI ");

		WashingMachineOperationI wm = (WashingMachineOperationI)model;
		
		assert	wm.getState() != WashingMachineState.OFF :
				new NeoSim4JavaException(
						"model not in the right state, should not be "
						+ "WashingMachineState.OFF but is " + wm.getState());

		wm.suspendWashing();
	}
}