package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SwitchOnFan</code> defines the switch on event of the fan.
 *
 * <p><strong>Description</strong></p>
 * * @author	Team DeMoh
 */
public class			SwitchOnFan
extends		ES_Event
implements	FanEventI
{
	private static final long serialVersionUID = 1L;

	public				SwitchOnFan(Time timeOfOccurrence)
	{
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		// SwitchOn overrides SwitchOff if they happen at the same time (convention)
		if (e instanceof SwitchOffFan) {
			return true;
		}
		return false;
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof FanElectricityModel;

		// On switch ON -> Default speed is LOW
		((FanElectricityModel)model).setSpeed(FanSpeed.LOW, this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------