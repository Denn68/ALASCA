package fr.sorbonne_u.components.hem2025e1.equipments.meter;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ElectricMeterImplementationI</code> defines the services
 * implemented by an electric meter component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ElectricMeterImplementationI
{
	/**
	 * return the tension in the electric circuits of this meter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return.getData() > 0.0}
	 * post	{@code return.getMeasurementUnit().equals(MeasurementUnit.VOLTS)}
	 * </pre>
	 *
	 * @return				the tension in the electric circuits of this meter.
	 * @throws Exception	<i>to do</i>.
	 */
	public Measure<Double>	getTension() throws Exception;

	/**
	 * return the current total electric consumption in amperes.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * post	{@code return.isSingle()}
	 * post	{@code return.getMeasure().getData() >= 0.0}
	 * post	{@code return.getMeasure().getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * </pre>
	 *
	 * @return				the current total electric consumption in watts.
	 * @throws Exception	<i>to do</i>.
	 */
	public SignalData<Double>	getCurrentConsumption() throws Exception;

	/**
	 * return the current total electric power production in watts.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * post	{@code return.isSingle()}
	 * post	{@code return.getMeasure().getData() >= 0.0}
	 * post	{@code return.getMeasure().getMeasurementUnit().equals(MeasurementUnit.AMPERES)}
	 * </pre>
	 *
	 * @return				the current total electric power production in amperes.
	 * @throws Exception	<i>to do</i>.
	 */
	public SignalData<Double>	getCurrentProduction() throws Exception;
}
// -----------------------------------------------------------------------------
