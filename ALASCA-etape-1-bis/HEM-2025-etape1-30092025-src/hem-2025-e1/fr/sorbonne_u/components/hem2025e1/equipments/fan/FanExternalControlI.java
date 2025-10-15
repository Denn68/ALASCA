package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

public interface		FanExternalControlI
//extends		HeaterTemperatureI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** measurement unit for power used in this appliance.					*/
	public static final MeasurementUnit	POWER_UNIT = MeasurementUnit.WATTS;
	/** measurement unit for tension used in this appliance.				*/
	public static final MeasurementUnit	TENSION_UNIT = MeasurementUnit.VOLTS;

	/** max power level of the fan, in the power measurement unit used
	 *  by the fan.														*/
	public static final Measure<Double>	MAX_POWER_LEVEL =
											new Measure<>(2200.0, POWER_UNIT);
	/** operating voltage of the fan, in the tension measurement unit
	 *  used by the fan.													*/
	public static final Measure<Double>	VOLTAGE =
											new Measure<>(220.0, TENSION_UNIT);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	invariants(FanExternalControlI h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				POWER_UNIT != null,
				FanExternalControlI.class, h,
				"POWER_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				TENSION_UNIT != null,
				FanExternalControlI.class, h,
				"TENSION_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				MAX_POWER_LEVEL != null &&
					MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) &&
					MAX_POWER_LEVEL.getData() > 0.0,
				FanExternalControlI.class, h,
				"MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit()."
				+ "equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0");
		ret &= AssertionChecking.checkInvariant(
				VOLTAGE != null &&
					VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) &&
					VOLTAGE.getData() == 220.0,
				FanExternalControlI.class, h,
				"VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals("
				+ "TENSION_UNIT) && VOLTAGE.getData() == 220.0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	public Measure<Double>	getMaxPowerLevel() throws Exception;

	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	public SignalData<Double>	getCurrentPowerLevel() throws Exception;
}
// -----------------------------------------------------------------------------
