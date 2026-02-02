package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOffFan</code> defines the switch off event of the fan.
 *
 * <p><strong>Description</strong></p>
 * * @author	Team DeMoh
 */
public class			SwitchOffFan
extends		ES_Event
implements	FanEventI
{
	private static final long serialVersionUID = 1L;

	public				SwitchOffFan(Time timeOfOccurrence)
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

		((FanElectricityModel)model).setSpeed(FanSpeed.OFF, this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------