package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleTemperatureModel;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>DoNotHeatKettle</code> defines the simulation event of the
 * kettle stopping to heat (going to Keep Warm or Idle).
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
public class			DoNotHeatKettle
extends		Event
implements	KettleEventI
{
	private static final long serialVersionUID = 1L;

	public				DoNotHeatKettle(Time timeOfOccurrence)
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
		assert	model instanceof KettleElectricityModel ||
				model instanceof KettleTemperatureModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "KettleElectricityModel || "
						+ "model instanceof KettleTemperatureModel");

		if (model instanceof KettleElectricityModel) {
			KettleElectricityModel kettle = (KettleElectricityModel)model;
			assert	kettle.getState() == KettleState.HEATING:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "HEATING but is "
							+ kettle.getState());
			// Default behavior: go to KEEP_WARM or ON. Let's assume KEEP_WARM for realism.
			kettle.setState(KettleState.KEEP_WARM, this.getTimeOfOccurrence());
		} else if (model instanceof KettleTemperatureModel) {
			KettleTemperatureModel kettle = (KettleTemperatureModel)model;
			assert	kettle.getState() == KettleState.HEATING:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "HEATING but is "
							+ kettle.getState());
			kettle.setState(KettleState.KEEP_WARM);
		}
	}
}
// -----------------------------------------------------------------------------