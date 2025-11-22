package fr.sorbonne_u.components.hem2025e1.equipments.heater;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterTemperatureI;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The interface <code>HeaterExternalControlI</code> declares the
 * signatures of service implementations accessible to the external controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code POWER_UNIT != null}
 * invariant	{@code TENSION_UNIT != null}
 * invariant	{@code MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0}
 * invariant	{@code VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) && VOLTAGE.getData() == 220.0}
 * invariant	{@code getCurrentPowerLevel().getData() <= getMaxPowerLevel().getData()}
 * </pre>
 * 
 * <p>Created on : 2023-09-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		HeaterExternalControlI
extends		HeaterTemperatureI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** measurement unit for power used in this appliance.					*/
	public static final MeasurementUnit	POWER_UNIT = MeasurementUnit.WATTS;
	/** measurement unit for tension used in this appliance.				*/
	public static final MeasurementUnit	TENSION_UNIT = MeasurementUnit.VOLTS;

	/** max power level of the heater, in the power measurement unit used
	 *  by the heater.														*/
	public static final Measure<Double>	MAX_POWER_LEVEL =
											new Measure<>(2200.0, POWER_UNIT);
	/** operating voltage of the heater, in the tension measurement unit
	 *  used by the heater.													*/
	public static final Measure<Double>	VOLTAGE =
											new Measure<>(220.0, TENSION_UNIT);

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
	public static boolean	invariants(HeaterExternalControlI h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				POWER_UNIT != null,
				HeaterExternalControlI.class, h,
				"POWER_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				TENSION_UNIT != null,
				HeaterExternalControlI.class, h,
				"TENSION_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				MAX_POWER_LEVEL != null &&
					MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) &&
					MAX_POWER_LEVEL.getData() > 0.0,
				HeaterExternalControlI.class, h,
				"MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit()."
				+ "equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0");
		ret &= AssertionChecking.checkInvariant(
				VOLTAGE != null &&
					VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) &&
					VOLTAGE.getData() == 220.0,
				HeaterExternalControlI.class, h,
				"VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals("
				+ "TENSION_UNIT) && VOLTAGE.getData() == 220.0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	/**
	 * return the maximum power of the heater in the power unit used by the
	 * heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return.getData() > 0.0 && return.getMeasurementUnit().equals(POWER_UNIT)}
	 * </pre>
	 *
	 * @return				the maximum power of the heater in the power unit used by the heater.
	 * @throws Exception	<i>to do</i>.
	 */
	public Measure<Double>	getMaxPowerLevel() throws Exception;

	/**
	 * set the power level of the heater; if
	 * {@code powerLevel.getData() > getMaxPowerLevel().getData()} then set the
	 * power level to the maximum.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * pre	{@code powerLevel != null && powerLevel.getData() >= 0.0 && powerLevel.getMeasurementUnit().equals(POWER_UNIT)}
	 * post	{@code powerLevel.getData() > getMaxPowerLevel().getData() || getCurrentPowerLevel().getData() == powerLevel.getData()}
	 * post	{@code powerLevel.getData() <= getMaxPowerLevel().getData() || getCurrentPowerLevel().getData() == Heater.MAX_POWER_LEVEL.getData()}
	 * </pre>
	 *
	 * @param powerLevel	the powerLevel to be set.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	/**
	 * return the current power level of the heater in the power unit used by
	 * the heater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code on()}
	 * post	{@code return != null && return.getMeasure().getMeasurementUnit().equals(POWER_UNIT)}
	 * post	{@code return.getMeasure().getData() >= 0.0 && return.getMeasure().getData() <= getMaxPowerLevel().getData()}
	 * </pre>
	 *
	 * @return				the current power level of the heater.
	 * @throws Exception	<i>to do</i>.
	 */
	public SignalData<Double>	getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
