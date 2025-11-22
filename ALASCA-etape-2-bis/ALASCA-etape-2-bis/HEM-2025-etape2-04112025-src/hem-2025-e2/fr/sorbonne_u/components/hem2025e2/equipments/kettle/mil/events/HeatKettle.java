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
 * The class <code>HeatKettle</code> defines the simulation event of the kettle
 * starting to heat.
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
public class			HeatKettle
extends		Event
implements	KettleEventI
{
	private static final long serialVersionUID = 1L;

	public				HeatKettle(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOnKettle || e instanceof DoNotHeatKettle) {
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
			assert	kettle.getState() == KettleState.ON || kettle.getState() == KettleState.KEEP_WARM:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "ON or KEEP_WARM but is "
							+ kettle.getState());
			kettle.setState(KettleState.HEATING, this.getTimeOfOccurrence());
		} else if (model instanceof KettleTemperatureModel) {
			KettleTemperatureModel kettle = (KettleTemperatureModel)model;
			assert	kettle.getState() == KettleState.ON || kettle.getState() == KettleState.KEEP_WARM:
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "ON or KEEP_WARM but is "
							+ kettle.getState());
			kettle.setState(KettleState.HEATING);
		}
	}
}
// -----------------------------------------------------------------------------