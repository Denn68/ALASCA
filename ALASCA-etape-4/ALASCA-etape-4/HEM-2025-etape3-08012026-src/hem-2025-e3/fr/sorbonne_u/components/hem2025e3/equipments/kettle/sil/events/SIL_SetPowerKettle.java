package fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.KettleEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.SIL_KettleOperationI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SIL_SetPowerKettle</code> defines the simulation event of the
 * kettle power being set to some level (in watts) for the software-in-the-loop
 * simulator of the kettle.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 *
 * @author	Team DeMoh
 */
public class			SIL_SetPowerKettle
extends		Event
implements	KettleEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PowerValue</code> represent a power value to be passed
	 * as an {@code EventInformationI} when creating a {@code SIL_SetPowerKettle}
	 * event.
	 */
	public static class	PowerValue
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		protected final double	power;

		public			PowerValue(double power)
		{
			super();

			assert	power >= 0.0 &&
						power <= KettleExternalControlI.MAX_POWER_LEVEL.getData() :
					new NeoSim4JavaException(
							"power >= 0.0 && power <= KettleExternalControlI."
							+ "MAX_POWER_LEVEL.getData()");

			this.power = power;
		}

		public double	getPower()	{ return this.power; }

		@Override
		public String	toString()
		{
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('[');
			sb.append(this.power);
			sb.append(']');
			return sb.toString();
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	protected final PowerValue	powerValue;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				SIL_SetPowerKettle(
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

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if (e instanceof SwitchOffKettle) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model instanceof SIL_KettleOperationI :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "SIL_KettleOperationI");

		SIL_KettleOperationI kettle = (SIL_KettleOperationI)model;
		assert	kettle.getState() == KettleState.HEATING ||
				kettle.getState() == KettleState.KEEP_WARM :
				new NeoSim4JavaException(
						"model not in the right state, should be "
						+ "HEATING or KEEP_WARM but is " + kettle.getState());
		kettle.setCurrentHeatingPower(this.powerValue.getPower(),
									  this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------
