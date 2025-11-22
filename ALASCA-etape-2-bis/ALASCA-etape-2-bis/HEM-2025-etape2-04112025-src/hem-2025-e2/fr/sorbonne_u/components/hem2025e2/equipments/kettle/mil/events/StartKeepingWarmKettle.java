package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleTemperatureModel;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

/**
 * Simulation event: the smart kettle enters KEEP_WARM mode.
 */
public class StartKeepingWarmKettle
extends		Event
implements	KettleEventI
{
	private static final long serialVersionUID = 1L;

	public				StartKeepingWarmKettle(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// En cas de concurrence :
		// - passe après un éventuel SwitchOnKettle,
		// - mais avant un éventuel SwitchOffKettle.
		if (e instanceof SwitchOnKettle) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof KettleElectricityModel
					|| model instanceof KettleTemperatureModel :
				new NeoSim4JavaException(
						"Precondition violation: model must be "
						+ "KettleElectricityModel or KettleTemperatureModel");

		if (model instanceof KettleElectricityModel) {
			KettleElectricityModel kettle = (KettleElectricityModel) model;
			KettleState s = kettle.getState();

			// Ici, on choisit : KEEP_WARM seulement depuis ON
			assert	s == KettleState.ON :
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "ON but is " + s);

			kettle.setState(KettleState.KEEP_WARM,
							this.getTimeOfOccurrence());
		} else {
			KettleTemperatureModel kettle = (KettleTemperatureModel) model;
			KettleState s = kettle.getState();

			assert	s == KettleState.ON :
					new NeoSim4JavaException(
							"model not in the right state, should be "
							+ "ON but is " + s);

			kettle.setState(KettleState.KEEP_WARM);
		}
	}
}
