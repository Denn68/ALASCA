package fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil;

import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleOperationI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SIL_KettleOperationI</code> defines the common operations
 * used by events to execute on the kettle SIL models.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Black-box Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 *
 * @author	Team DeMoh
 */
public interface		SIL_KettleOperationI
extends		KettleOperationI
{
	/**
	 * set the current heating power of the kettle to {@code newPower}.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code newPower >= 0.0 && newPower <= MAX_HEATING_POWER}
	 * post	{@code getCurrentHeatingPower() == newPower}
	 * </pre>
	 *
	 * @param newPower	the new power in watts to be set on the kettle.
	 * @param t			time at which the new power is set.
	 */
	public void			setCurrentHeatingPower(double newPower, Time t);
}
// -----------------------------------------------------------------------------
