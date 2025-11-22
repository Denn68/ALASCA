package fr.sorbonne_u.components.hem2025e1.equipments.heater;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>HeaterUserI</code> declares the signature of the heater
 * component services corresponding to the actions a user can perform on the
 * heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		HeaterUserI
extends		HeaterExternalControlI
{
	/**
	 * return true if the heater is currently running.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the heater is currently running.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		on() throws Exception;

	/**
	 * switch on the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !on()}
	 * post	{@code on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOn() throws Exception;

	/**
	 * switch off the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code !on()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOff() throws Exception;

	/**
	 * set the target temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit())}
	 * pre	{@code target.getData() >= MIN_TARGET_TEMPERATURE.getData() && target.getData() <= MAX_TARGET_TEMPERATURE.getData()}
	 * post	{@code getTargetTemperature().equals(target)}
	 * </pre>
	 *
	 * @param target		the new target temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception;
}
// -----------------------------------------------------------------------------
