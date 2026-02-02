package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleOperationI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

/**
 * Simulation event: the smart kettle stops KEEP_WARM mode
 * and goes back to state ON.
 */
public class StopKeepingWarmKettle
extends		Event
implements	KettleEventI
{
	private static final long serialVersionUID = 1L;

	public				StopKeepingWarmKettle(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnKettle) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof KettleOperationI :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof KettleOperationI");

		KettleOperationI kettle = (KettleOperationI)model;
		KettleState s = kettle.getState();

		assert	s == KettleState.KEEP_WARM :
				new NeoSim4JavaException(
						"model not in the right state, should be "
						+ "KEEP_WARM but is " + s);

		kettle.setState(KettleState.ON);
	}
}
