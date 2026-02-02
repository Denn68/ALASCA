package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>FanSimulationConfigurationI</code> defines common
 * constants and configuration parameters for the fan simulator.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code MeasurementUnit.AMPERES.equals(ElectricMeterImplementationI.POWER_UNIT)}
 * invariant	{@code MeasurementUnit.VOLTS.equals(ElectricMeterImplementationI.TENSION_UNIT)}
 * invariant	{@code (new Measure<Double>(220.0, ElectricMeterImplementationI.TENSION_UNIT)).equals(ElectricMeter.TENSION)}
 * invariant	{@code MeasurementUnit.WATTS.equals(FanExternalControlI.POWER_UNIT)}
 * invariant	{@code ElectricMeterImplementationI.TENSION_UNIT.equals(FanExternalControlI.TENSION_UNIT)}
 * invariant	{@code ElectricMeter.TENSION.equals(FanExternalControlI.VOLTAGE)}
 * invariant	{@code TIME_UNIT != null}
 * </pre>
 * * <p>Created on : 2023-11-14</p>
 * * @author	Team DeMoh
 */
public interface		FanSimulationConfigurationI
{
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** time unit used in the fan simulator.								*/
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
		// Le Fan n'a pas de modèle de température dans cette version simple, 
		// donc on ne vérifie pas HeaterTemperatureI.
		
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.AMPERES.equals(
									ElectricMeterImplementationI.POWER_UNIT),
				FanSimulationConfigurationI.class,
				"MeasurementUnit.AMPERES.equals("
				+ "ElectricMeterImplementationI.POWER_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.VOLTS.equals(
									ElectricMeterImplementationI.TENSION_UNIT),
				FanSimulationConfigurationI.class,
				"MeasurementUnit.VOLTS.equals("
				+ "ElectricMeterImplementationI.TENSION_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				(new Measure<Double>(220.0,
									 ElectricMeterImplementationI.TENSION_UNIT)).
						equals(ElectricMeter.TENSION),
				FanSimulationConfigurationI.class,
				"(new Measure<Double>(220.0, ElectricMeterImplementationI."
				+ "TENSION_UNIT)).equals(ElectricMeter.TENSION)");

		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.WATTS.equals(FanCoupledModel.POWER_UNIT),
				FanSimulationConfigurationI.class,
				"MeasurementUnit.WATTS.equals(FanExternalControlI.POWER_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				ElectricMeterImplementationI.TENSION_UNIT.equals(
						FanCoupledModel.TENSION_UNIT),
				FanSimulationConfigurationI.class,
				"ElectricMeterImplementationI.TENSION_UNIT.equals("
				+ "FanExternalControlI.TENSION_UNIT)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				ElectricMeter.TENSION.equals(FanCoupledModel.VOLTAGE),
				FanSimulationConfigurationI.class,
				"ElectricMeter.TENSION.equals(FanExternalControlI.VOLTAGE)");
		
		ret &= AssertionChecking.checkStaticInvariant(
				TIME_UNIT != null,
				FanSimulationConfigurationI.class,
				"TIME_UNIT != null");
		
		return ret;
	}
}
// -----------------------------------------------------------------------------