package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleTemperatureModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffKettle</code> defines the simulation event of the
 * kettle being switched off.
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
public class			SwitchOffKettle
extends		ES_Event
implements	KettleEventI
{
	private static final long serialVersionUID = 1L;

	public				SwitchOffKettle(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return false;
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
			assert	kettle.getState() != KettleState.OFF :
					new NeoSim4JavaException(
							"model not in the right state, should not be "
							+ "KettleElectricityModel.State.OFF but is "
							+ kettle.getState());
			kettle.setState(KettleState.OFF, this.getTimeOfOccurrence());
		} else if (model instanceof KettleTemperatureModel) {
			KettleTemperatureModel kettle = (KettleTemperatureModel)model;
			kettle.setState(KettleState.OFF);
		}
	}
}
// -----------------------------------------------------------------------------