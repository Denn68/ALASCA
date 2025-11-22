package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleaner;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnVacuumCleaner</code> defines the simulation event of the
 * vacuum cleaner being switched on.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * @author	Team DeMoh
 */
public class			SwitchOnVacuumCleaner
extends		AbstractVacuumCleanerEvent
{
	private static final long serialVersionUID = 1L;

	public				SwitchOnVacuumCleaner(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean			hasPriorityOver(EventI e)
	{
		return true;
	}

	@Override
	public void				executeOn(AtomicModelI model)
	{
		assert	model instanceof VacuumCleanerElectricityModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "VacuumCleanerElectricityModel");

		VacuumCleanerElectricityModel m = (VacuumCleanerElectricityModel)model;
		if (m.getState() == VacuumCleanerState.OFF) {
			m.setStateMode(VacuumCleanerState.ON, VacuumCleaner.INITIAL_MODE);
			m.toggleConsumptionHasChanged();
		}
	}
}
// -----------------------------------------------------------------------------