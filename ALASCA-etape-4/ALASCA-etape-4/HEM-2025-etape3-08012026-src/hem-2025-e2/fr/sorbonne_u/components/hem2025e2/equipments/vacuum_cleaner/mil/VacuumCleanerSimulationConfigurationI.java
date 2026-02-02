package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeter;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleaner;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>VacuumCleanerSimulationConfigurationI</code> defines
 * configuration parameters for the vacuum cleaner simulator.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * *
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * MeasurementUnit.AMPERES.equals(ElectricMeterImplementationI.POWER_UNIT)
 * }
 * invariant	{@code
 * MeasurementUnit.VOLTS.equals(ElectricMeterImplementationI.TENSION_UNIT)
 * }
 * invariant	{@code
 * (new Measure<Double>(220.0, ElectricMeterImplementationI.TENSION_UNIT)).equals(ElectricMeter.TENSION)
 * }
 * invariant	{@code
 * ElectricMeter.TENSION.equals(VacuumCleaner.TENSION)
 * }
 * invariant	{@code
 * TIME_UNIT != null
 * }
 * </pre>
 * 
 * * @author Team DeMoh
 */
public interface VacuumCleanerSimulationConfigurationI {
	// -------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------

	/** time unit used in the vacuum cleaner simulator. */
	public static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * instance != null
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @return true if the invariants are observed, false otherwise.
	 */
	public static boolean staticInvariants() {
		boolean ret = true;
		ret &= ElectricMeterImplementationI.staticInvariants();

		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.AMPERES.equals(
						ElectricMeterImplementationI.POWER_UNIT),
				VacuumCleanerSimulationConfigurationI.class,
				"MeasurementUnit.AMPERES.equals("
						+ "ElectricMeterImplementationI.POWER_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				MeasurementUnit.VOLTS.equals(
						ElectricMeterImplementationI.TENSION_UNIT),
				VacuumCleanerSimulationConfigurationI.class,
				"MeasurementUnit.VOLTS.equals("
						+ "ElectricMeterImplementationI.TENSION_UNIT)");
		ret &= AssertionChecking.checkStaticInvariant(
				(new Measure<Double>(220.0,
						ElectricMeterImplementationI.TENSION_UNIT)).equals(ElectricMeter.TENSION),
				VacuumCleanerSimulationConfigurationI.class,
				"(new Measure<Double>(220.0, ElectricMeterImplementationI."
						+ "TENSION_UNIT)).equals(ElectricMeter.TENSION)");

		ret &= AssertionChecking.checkStaticInvariant(
				ElectricMeter.TENSION.equals(VacuumCleaner.TENSION),
				VacuumCleanerSimulationConfigurationI.class,
				"ElectricMeter.TENSION.equals(VacuumCleaner.TENSION)");

		ret &= AssertionChecking.checkStaticInvariant(
				TIME_UNIT != null,
				VacuumCleanerSimulationConfigurationI.class,
				"TIME_UNIT != null");
		return ret;
	}
}
// -----------------------------------------------------------------------------