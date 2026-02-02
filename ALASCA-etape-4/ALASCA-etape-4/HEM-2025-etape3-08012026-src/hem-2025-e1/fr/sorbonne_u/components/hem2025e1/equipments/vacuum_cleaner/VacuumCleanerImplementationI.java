package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The interface <code>VacuumCleanerImplementationI</code> defines the
 * signatures
 * of services implemented by the vacuum cleaner component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The vacuum cleaner is an <b>uncontrollable</b> appliance, hence it does not
 * connect with the household energy manager. However, it will connect later to
 * the electric panel to take its (simulated) electricity consumption into
 * account.
 * </p>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * POWER_UNIT != null && TENSION_UNIT != null
 * }
 * </pre>
 * 
 * @author Team
 */
public interface VacuumCleanerImplementationI {
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>VacuumCleanerState</code> describes the operation
	 * states of the vacuum cleaner.
	 */
	public static enum VacuumCleanerState {
		/** vacuum cleaner is on. */
		ON,
		/** vacuum cleaner is off. */
		OFF
	}

	/**
	 * The enumeration <code>VacuumCleanerMode</code> describes the operation
	 * modes of the vacuum cleaner.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * The vacuum cleaner can be either in <code>LOW</code> mode (gentle suction)
	 * or in <code>HIGH</code> mode (strong suction).
	 * </p>
	 */
	public static enum VacuumCleanerMode {
		/** low mode is when the vacuum sucks gently. */
		LOW,
		/** high mode is when the vacuum sucks strongly. */
		HIGH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** measurement unit for power used in this appliance. */
	public static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;
	/** measurement unit for tension used in this appliance. */
	public static final MeasurementUnit TENSION_UNIT = MeasurementUnit.VOLTS;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 */
	public static boolean staticInvariants() {
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				POWER_UNIT != null && TENSION_UNIT != null,
				VacuumCleanerImplementationI.class,
				"POWER_UNIT != null && TENSION_UNIT != null");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Component services signatures
	// -------------------------------------------------------------------------

	/**
	 * return the current state of the vacuum cleaner.
	 */
	public VacuumCleanerState getState() throws Exception;

	/**
	 * return the current operation mode of the vacuum cleaner.
	 */
	public VacuumCleanerMode getMode() throws Exception;

	/**
	 * turn on the vacuum cleaner, put in the low suction mode.
	 * 
	 * <pre>
	 * pre	{@code
	 * getState() == VacuumCleanerState.OFF
	 * }
	 * post	{@code
	 * getMode() == VacuumCleanerMode.LOW
	 * }
	 * post	{@code
	 * getState() == VacuumCleanerState.ON
	 * }
	 * </pre>
	 */
	public void turnOn() throws Exception;

	/**
	 * turn off the vacuum cleaner.
	 * 
	 * <pre>
	 * pre	{@code
	 * getState() == VacuumCleanerState.ON
	 * }
	 * post	{@code
	 * getState() == VacuumCleanerState.OFF
	 * }
	 * </pre>
	 */
	public void turnOff() throws Exception;

	/**
	 * set the vacuum cleaner in high mode.
	 * 
	 * <pre>
	 * pre	{@code
	 * getState() == VacuumCleanerState.ON
	 * }
	 * pre	{@code
	 * getMode() == VacuumCleanerMode.LOW
	 * }
	 * post	{@code
	 * getMode() == VacuumCleanerMode.HIGH
	 * }
	 * </pre>
	 */
	public void setHigh() throws Exception;

	/**
	 * set the vacuum cleaner in low mode.
	 * 
	 * <pre>
	 * pre	{@code
	 * getState() == VacuumCleanerState.ON
	 * }
	 * pre	{@code
	 * getMode() == VacuumCleanerMode.HIGH
	 * }
	 * post	{@code
	 * getMode() == VacuumCleanerMode.LOW
	 * }
	 * </pre>
	 */
	public void setLow() throws Exception;
}
// -----------------------------------------------------------------------------
