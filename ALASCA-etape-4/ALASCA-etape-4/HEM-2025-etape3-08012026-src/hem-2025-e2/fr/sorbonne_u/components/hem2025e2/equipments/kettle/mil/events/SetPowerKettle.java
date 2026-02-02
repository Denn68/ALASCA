package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events;

import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleElectricityModel;
import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>SetPowerKettle</code> defines the simulation event of the
 * kettle power being set to some level (in watts).
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
public class			SetPowerKettle
extends		ES_Event
implements	KettleEventI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

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

	public				SetPowerKettle(
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
		assert	model instanceof KettleElectricityModel :
				new NeoSim4JavaException(
						"Precondition violation: model instanceof "
						+ "KettleElectricityModel");

		KettleElectricityModel kettle = (KettleElectricityModel)model;
		assert	kettle.getState() == KettleState.HEATING || kettle.getState() == KettleState.KEEP_WARM :
				new NeoSim4JavaException(
						"model not in the right state, should be "
						+ "HEATING or KEEP_WARM but is " + kettle.getState());
		kettle.setCurrentHeatingPower(this.powerValue.getPower(),
									  this.getTimeOfOccurrence());
	}
}
// -----------------------------------------------------------------------------
