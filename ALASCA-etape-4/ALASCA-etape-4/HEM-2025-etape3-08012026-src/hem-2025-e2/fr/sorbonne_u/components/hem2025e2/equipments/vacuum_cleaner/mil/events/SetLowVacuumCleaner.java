package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerMode;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerElectricityModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetLowVacuumCleaner</code> defines the simulation event of the
 * vacuum cleaner being set to low mode.
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
public class			SetLowVacuumCleaner
extends		AbstractVacuumCleanerEvent
{
	private static final long serialVersionUID = 1L;

	public				SetLowVacuumCleaner(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean			hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnVacuumCleaner) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void				executeOn(AtomicModelI model)
	{
		assert	model instanceof VacuumCleanerElectricityModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "VacuumCleanerElectricityModel");

		VacuumCleanerElectricityModel m = (VacuumCleanerElectricityModel)model;
		if (m.getState() == VacuumCleanerState.ON) {
			if (m.getMode() != VacuumCleanerMode.LOW) {
				m.setStateMode(VacuumCleanerState.ON, VacuumCleanerMode.LOW);
				m.toggleConsumptionHasChanged();
			}
		}
	}
}
// -----------------------------------------------------------------------------