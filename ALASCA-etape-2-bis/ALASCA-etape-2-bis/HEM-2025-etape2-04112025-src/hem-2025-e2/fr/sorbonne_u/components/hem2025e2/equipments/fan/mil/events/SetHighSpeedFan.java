package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events;

import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanCoupledModel.FanSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetHighSpeedFan</code> defines the event to set fan speed to HIGH.
 *
 * <p><strong>Description</strong></p>
 * * @author	Team DeMoh
 */
public class			SetHighSpeedFan
extends		ES_Event
implements	FanEventI
{
	private static final long serialVersionUID = 1L;

	public				SetHighSpeedFan(Time timeOfOccurrence)
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
		assert	model instanceof FanElectricityModel;

		((FanElectricityModel)model).setSpeed(FanSpeed.HIGH, this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------