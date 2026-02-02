package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineOperationI;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class			SetPowerWashingMachine
extends		ES_Event
implements	WashingMachineEventI
{
	private static final long serialVersionUID = 1L;

	public static class		PowerValue
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		protected final double	power;

		public			PowerValue(double power)
		{
			assert	power >= 0.0 :
					new NeoSim4JavaException(
							"Precondition violation: power >= 0.0");
			this.power = power;
		}

		public double	getPower() { return this.power; }

		@Override
		public String	toString()
		{
			return this.getClass().getSimpleName() + "[" + this.power + "]";
		}
	}

	protected final PowerValue	powerValue;

	public				SetPowerWashingMachine(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);

		assert	content != null && content instanceof PowerValue :
				new NeoSim4JavaException(
						"Precondition violation: event content is null or"
						+ " not a PowerValue " + content);

		this.powerValue = (PowerValue) content;
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOffWashingMachine) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof WashingMachineOperationI :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "WashingMachineOperationI");

		WashingMachineOperationI wm = (WashingMachineOperationI)model;
		assert	wm.getState() != WashingMachineState.OFF :
				new NeoSim4JavaException(
						"model not in the right state, should not be "
						+ "WashingMachineState.OFF but is " + wm.getState());
		
		wm.setCurrentPowerLevel(this.powerValue.getPower());
	}
}