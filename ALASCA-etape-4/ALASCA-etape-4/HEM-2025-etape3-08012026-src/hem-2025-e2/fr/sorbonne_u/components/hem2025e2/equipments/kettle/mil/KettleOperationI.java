package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;

// -----------------------------------------------------------------------------
/**
 * The interface <code>KettleOperationI</code> defines the common operations
 * used by events to execute on the kettle models.
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
public interface		KettleOperationI
{
	/**
	 * return the state of the kettle.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the current state.
	 */
	public KettleState		getState();

	/**
	 * set the state of the kettle.
	 *
	 * <p><strong>Contract</strong></p>
	 *
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s		the new state.
	 */
	public void			setState(KettleState s);
}
// -----------------------------------------------------------------------------
