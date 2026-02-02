package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleTemperatureI;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>KettleSimulationConfigurationI</code> defines common
 * constants and configuration parameters for the kettle simulator.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code MeasurementUnit.AMPERES.equals(ElectricMeterImplementationI.POWER_UNIT)}
 * invariant	{@code MeasurementUnit.VOLTS.equals(ElectricMeterImplementationI.TENSION_UNIT)}
 * invariant	{@code (new Measure<Double>(220.0, ElectricMeterImplementationI.TENSION_UNIT)).equals(ElectricMeter.TENSION)}
 * invariant	{@code MeasurementUnit.CELSIUS.equals(KettleTemperatureI.TEMPERATURE_UNIT)}
 * invariant	{@code MeasurementUnit.WATTS.equals(KettleExternalControlI.POWER_UNIT)}
 * invariant	{@code ElectricMeterImplementationI.TENSION_UNIT.equals(KettleExternalControlI.TENSION_UNIT)}
 * invariant	{@code ElectricMeter.TENSION.equals(KettleExternalControlI.VOLTAGE)}
 * invariant	{@code TIME_UNIT != null}
 * </pre>
 * * @author	Team DeMoh
 */
public interface		KettleSimulationConfigurationI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** time unit used in the kettle simulator.								*/
	public static final TimeUnit	TIME_UNIT = TimeUnit.HOURS;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return			true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= ElectricMeterImplementationI.staticInvariants();
		
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.AMPERES.equals(
									ElectricMeterImplementationI.POWER_UNIT),
				KettleSimulationConfigurationI.class,
				"MeasurementUnit.AMPERES.equals("
				+ "ElectricMeterImplementationI.POWER_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.VOLTS.equals(
									ElectricMeterImplementationI.TENSION_UNIT),
				KettleSimulationConfigurationI.class,
				"MeasurementUnit.VOLTS.equals("
				+ "ElectricMeterImplementationI.TENSION_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				(new Measure<Double>(220.0,
									 ElectricMeterImplementationI.TENSION_UNIT)).
						equals(ElectricMeter.TENSION),
				KettleSimulationConfigurationI.class,
				"(new Measure<Double>(220.0, ElectricMeterImplementationI."
				+ "TENSION_UNIT)).equals(ElectricMeter.TENSION)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.CELSIUS.equals(
										KettleTemperatureI.TEMPERATURE_UNIT),
				KettleSimulationConfigurationI.class,
				"MeasurementUnit.CELSIUS.equals("
				+ "KettleTemperatureI.TEMPERATURE_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.WATTS.equals(KettleExternalControlI.POWER_UNIT),
				KettleSimulationConfigurationI.class,
				"MeasurementUnit.WATTS.equals(KettleExternalControlI.POWER_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				ElectricMeterImplementationI.TENSION_UNIT.equals(
										KettleExternalControlI.TENSION_UNIT),
				KettleSimulationConfigurationI.class,
				"ElectricMeterImplementationI.TENSION_UNIT.equals("
				+ "KettleExternalControlI.TENSION_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				ElectricMeter.TENSION.equals(KettleExternalControlI.VOLTAGE),
				KettleSimulationConfigurationI.class,
				"ElectricMeter.TENSION.equals(KettleExternalControlI.VOLTAGE)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				TIME_UNIT != null,
				KettleSimulationConfigurationI.class,
				"TIME_UNIT != null");
		return ret;
	}
}
// -----------------------------------------------------------------------------