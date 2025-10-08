package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

public interface		WashingMachineExternalControlI
extends		WashingMachineTemperatureI
{
	public static final MeasurementUnit	POWER_UNIT = MeasurementUnit.WATTS;
	
	public static final MeasurementUnit	TENSION_UNIT = MeasurementUnit.VOLTS;

	public static final Measure<Double>	MAX_POWER_LEVEL =
											new Measure<>(2200.0, POWER_UNIT);
	
	public static final Measure<Double>	VOLTAGE =
											new Measure<>(230.0, TENSION_UNIT);
	
	public static boolean	invariants(WashingMachineExternalControlI h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				POWER_UNIT != null,
				WashingMachineExternalControlI.class, h,
				"POWER_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				TENSION_UNIT != null,
				WashingMachineExternalControlI.class, h,
				"TENSION_UNIT != null");
		ret &= AssertionChecking.checkInvariant(
				MAX_POWER_LEVEL != null &&
					MAX_POWER_LEVEL.getMeasurementUnit().equals(POWER_UNIT) &&
					MAX_POWER_LEVEL.getData() > 0.0,
					WashingMachineExternalControlI.class, h,
				"MAX_POWER_LEVEL != null && MAX_POWER_LEVEL.getMeasurementUnit()."
				+ "equals(POWER_UNIT) && MAX_POWER_LEVEL.getData() > 0.0");
		ret &= AssertionChecking.checkInvariant(
				VOLTAGE != null &&
					VOLTAGE.getMeasurementUnit().equals(TENSION_UNIT) &&
					VOLTAGE.getData() == 220.0,
					WashingMachineExternalControlI.class, h,
				"VOLTAGE != null && VOLTAGE.getMeasurementUnit().equals("
				+ "TENSION_UNIT) && VOLTAGE.getData() == 220.0");
		return ret;
	}
	
	public Measure<Double>	getMaxPowerLevel() throws Exception;
	
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;
	
	public SignalData<Double>	getCurrentPowerLevel() throws Exception;
}