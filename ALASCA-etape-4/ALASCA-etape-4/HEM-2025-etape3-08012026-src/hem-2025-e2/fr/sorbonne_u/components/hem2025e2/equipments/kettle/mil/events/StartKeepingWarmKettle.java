package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleOperationI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

/**
 * Simulation event: the smart kettle enters KEEP_WARM mode.
 */
public class StartKeepingWarmKettle
		extends Event
		implements KettleEventI {
	private static final long serialVersionUID = 1L;

	public StartKeepingWarmKettle(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOnKettle) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModelI model) {
		assert model instanceof KettleOperationI : new NeoSim4JavaException(
				"Precondition violation: model instanceof KettleOperationI");

		KettleOperationI kettle = (KettleOperationI) model;
		KettleState s = kettle.getState();

		// Accept ON or KEEP_WARM (already in keep warm mode is ok)
		assert s == KettleState.ON || s == KettleState.KEEP_WARM : new NeoSim4JavaException(
				"model not in the right state, should be "
						+ "ON or KEEP_WARM but is " + s);

		kettle.setState(KettleState.KEEP_WARM);
	}
}
