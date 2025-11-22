package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

public interface		WashingMachineTemperatureI
{
	public static final MeasurementUnit	TEMPERATURE_UNIT =
													MeasurementUnit.CELSIUS;

	public static final Measure<Double>	MIN_TARGET_TEMPERATURE =
												new Measure<>(
														15.0,
														TEMPERATURE_UNIT);
	
	public static final Measure<Double>	MAX_TARGET_TEMPERATURE =
												new Measure<>(
														90.0,
														TEMPERATURE_UNIT);

	public static boolean	invariants(WashingMachineTemperatureI h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				MIN_TARGET_TEMPERATURE != null &&
					MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				WashingMachineTemperatureI.class, h,
				"MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkInvariant(
				MAX_TARGET_TEMPERATURE != null &&
					MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(
															TEMPERATURE_UNIT),
				WashingMachineTemperatureI.class, h,
				"MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE."
				+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		return ret;
	}
	
	public Measure<Double>	getTargetTemperature() throws Exception;
	
	public SignalData<Double>	getCurrentTemperature() throws Exception;
}
