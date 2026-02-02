package fr.sorbonne_u.components.hem2025e1.equipments.kettle;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

public interface KettleTemperatureI {
	public static final MeasurementUnit TEMPERATURE_UNIT = MeasurementUnit.CELSIUS;

	public static final Measure<Double> MIN_TARGET_TEMPERATURE = new Measure<>(
			10.0,
			TEMPERATURE_UNIT);

	public static final Measure<Double> MAX_TARGET_TEMPERATURE = new Measure<>(
			100.0,
			TEMPERATURE_UNIT);

	public static boolean invariants(KettleTemperatureI h) {
		assert h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				MIN_TARGET_TEMPERATURE != null &&
						MIN_TARGET_TEMPERATURE.getMeasurementUnit().equals(
								TEMPERATURE_UNIT),
				KettleTemperatureI.class, h,
				"MIN_TARGET_TEMPERATURE != null && MIN_TARGET_TEMPERATURE."
						+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkInvariant(
				MAX_TARGET_TEMPERATURE != null &&
						MAX_TARGET_TEMPERATURE.getMeasurementUnit().equals(
								TEMPERATURE_UNIT),
				KettleTemperatureI.class, h,
				"MAX_TARGET_TEMPERATURE != null && MAX_TARGET_TEMPERATURE."
						+ "getMeasurementUnit().equals(TEMPERATURE_UNIT)");
		return ret;
	}

	public SignalData<Double> getTargetTemperature() throws Exception;

	public SignalData<Double> getCurrentTemperature() throws Exception;
}
// -----------------------------------------------------------------------------