package fr.sorbonne_u.components.hem2025e1.equipments.heater;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterTemperatureI;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The interface <code>HeaterTemperatureI</code> declares the signatures of
 * the services accessing the current and target temperatures on the heater.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * invariant	{@code MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(TEMPERATURE_UNIT)}
 * </pre>
 * 
 * <p>Created on : 2023-09-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		HeaterTemperatureI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	public static final MeasurementUnit	TEMPERATURE_UNIT =
													MeasurementUnit.CELSIUS;

	/** minimal target temperature for the heater in celsius.				*/
	public static final Measure<Double>	MIN_TARGET_TEMPERATURE =
												new Measure<>(
														-50.0,
														TEMPERATURE_UNIT);
	/** maximal target temperature for the heater in celsius.				*/
	public static final Measure<Double>	MAX_TARGET_TEMPERATURE =
												new Measure<>(
														50.0,
														TEMPERATURE_UNIT);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code h != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param h	instance to be tested.
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	invariants(HeaterTemperatureI h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				MIN_TARGET_TEMPERATURE != null &&
					MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				HeaterTemperatureI.class, h,
				"MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkInvariant(
				MAX_TARGET_TEMPERATURE != null &&
					MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				HeaterTemperatureI.class, h,
				"MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	/**
	 * get the current target temperature.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && Heater.TEMPERATURE_UNIT.equals(return.getMeasurementUnit())}
	 * post	{@code return.getData() >= Heater.MIN_TARGET_TEMPERATURE.getData() && return.getData() <= Heater.MAX_TARGET_TEMPERATURE.getData()}
	 * </pre>
	 *
	 * @return				the current target temperature.
	 * @throws Exception	<i>to do</i>.
	 */
	public Measure<Double>	getTargetTemperature() throws Exception;

	/**
	 * return the current temperature measured by the thermostat.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return				the current temperature measured by the thermostat.
	 * @throws Exception	<i>to do</i>.
	 */
	public SignalData<Double>	getCurrentTemperature() throws Exception;
}
// -----------------------------------------------------------------------------
