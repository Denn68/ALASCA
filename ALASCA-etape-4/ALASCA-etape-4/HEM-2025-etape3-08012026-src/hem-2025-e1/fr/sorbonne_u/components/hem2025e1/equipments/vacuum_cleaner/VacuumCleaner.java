package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleaner</code> implements the vacuum cleaner component.
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
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * INITIAL_STATE != null
 * }
 * invariant	{@code
 * INITIAL_MODE != null
 * }
 * invariant	{@code
 * currentState != null
 * }
 * invariant	{@code
 * currentMode != null
 * }
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * HIGH_POWER != null && HIGH_POWER.getData() > 0.0 && HIGH_POWER.getMeasurementUnit().equals(POWER_UNIT)
 * }
 * invariant	{@code
 * LOW_POWER != null && LOW_POWER.getData() > 0.0 && LOW_POWER.getMeasurementUnit().equals(POWER_UNIT)
 * }
 * invariant	{@code
 * TENSION != null && TENSION.getData() == 220.0 && TENSION.getMeasurementUnit().equals(TENSION_UNIT)
 * }
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * @author Team
 */
@OfferedInterfaces(offered = { VacuumCleanerUserCI.class })
public class VacuumCleaner
		extends AbstractComponent
		implements VacuumCleanerImplementationI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the vacuum cleaner inbound port used in tests. */
	public static final String REFLECTION_INBOUND_PORT_URI = "VACUUM-CLEANER-RIP-URI";
	/** URI of the vacuum cleaner inbound port used in tests. */
	public static final String INBOUND_PORT_URI = "VACUUM-CLEANER-INBOUND-PORT-URI";

	/** when true, methods trace their actions. */
	public static boolean VERBOSE = false;
	/** when tracing, x coordinate of the window relative position. */
	public static int X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position. */
	public static int Y_RELATIVE_POSITION = 0;

	/**
	 * power consumption when in mode HIGH in the power unit used by
	 * the vacuum cleaner.
	 */
	public static final Measure<Double> HIGH_POWER = new Measure<Double>(
			2000.0,
			POWER_UNIT);
	/**
	 * power consumption when in mode LOW in the power unit used by
	 * the vacuum cleaner.
	 */
	public static final Measure<Double> LOW_POWER = new Measure<Double>(
			650.0,
			POWER_UNIT);
	/** tension required by the vacuum cleaner in the tension unit. */
	public static final Measure<Double> TENSION = new Measure<Double>(
			220.0,
			TENSION_UNIT);

	/** initial state of the vacuum cleaner. */
	public static final VacuumCleanerState INITIAL_STATE = VacuumCleanerState.OFF;
	/** initial mode of the vacuum cleaner. */
	public static final VacuumCleanerMode INITIAL_MODE = VacuumCleanerMode.LOW;

	/** current state (on, off) of the vacuum cleaner. */
	protected VacuumCleanerState currentState;
	/** current mode of operation (low, high) of the vacuum cleaner. */
	protected VacuumCleanerMode currentMode;

	/** inbound port offering the <code>VacuumCleanerUserCI</code> interface. */
	protected VacuumCleanerInboundPort vcip;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean implementationInvariants(VacuumCleaner vc) {
		assert vc != null : new PreconditionException("vc != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				INITIAL_STATE != null,
				VacuumCleaner.class, vc,
				"INITIAL_STATE != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				INITIAL_MODE != null,
				VacuumCleaner.class, vc,
				"INITIAL_MODE != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				vc.currentState != null,
				VacuumCleaner.class, vc,
				"vc.currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				vc.currentMode != null,
				VacuumCleaner.class, vc,
				"vc.currentMode != null");
		return ret;
	}

	protected static boolean invariants(VacuumCleaner vc) {
		assert vc != null : new PreconditionException("vc != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
						!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, vc,
				"REFLECTION_INBOUND_PORT_URI != null && "
						+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, vc,
				"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, vc,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, vc,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected VacuumCleaner() throws Exception {
		this(INBOUND_PORT_URI);
	}

	protected VacuumCleaner(String vacuumCleanerInboundPortURI)
			throws Exception {
		this(REFLECTION_INBOUND_PORT_URI, vacuumCleanerInboundPortURI);
	}

	protected VacuumCleaner(
			String reflectionInboundPortURI,
			String vacuumCleanerInboundPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(vacuumCleanerInboundPortURI);
	}

	protected void initialise(String vacuumCleanerInboundPortURI)
			throws Exception {
		assert vacuumCleanerInboundPortURI != null : new PreconditionException(
				"vacuumCleanerInboundPortURI != null");
		assert !vacuumCleanerInboundPortURI.isEmpty() : new PreconditionException(
				"!vacuumCleanerInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.vcip = new VacuumCleanerInboundPort(vacuumCleanerInboundPortURI, this);
		this.vcip.publishPort();

		if (VacuumCleaner.VERBOSE) {
			this.tracer.get().setTitle("Vacuum cleaner component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
					Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert VacuumCleaner.implementationInvariants(this) : new ImplementationInvariantException(
				"VacuumCleaner.implementationInvariants(this)");
		assert VacuumCleaner.invariants(this) : new InvariantException("VacuumCleaner.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.vcip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public VacuumCleanerState getState() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner returns its state: " +
					this.currentState + ".\n");
		}

		return this.currentState;
	}

	@Override
	public VacuumCleanerMode getMode() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner returns its mode: " +
					this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	@Override
	public void turnOn() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is turned on.\n");
		}

		assert this.getState() == VacuumCleanerState.OFF
				: new PreconditionException("getState() == VacuumCleanerState.OFF");

		this.currentState = VacuumCleanerState.ON;
		this.currentMode = VacuumCleanerMode.LOW;
	}

	@Override
	public void turnOff() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is turned off.\n");
		}

		assert this.getState() == VacuumCleanerState.ON
				: new PreconditionException("getState() == VacuumCleanerState.ON");

		this.currentState = VacuumCleanerState.OFF;
	}

	@Override
	public void setHigh() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is set high.\n");
		}

		assert this.getState() == VacuumCleanerState.ON
				: new PreconditionException("getState() == VacuumCleanerState.ON");
		assert this.getMode() == VacuumCleanerMode.LOW
				: new PreconditionException("getMode() == VacuumCleanerMode.LOW");

		this.currentMode = VacuumCleanerMode.HIGH;
	}

	@Override
	public void setLow() throws Exception {
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is set low.\n");
		}

		assert this.getState() == VacuumCleanerState.ON
				: new PreconditionException("getState() == VacuumCleanerState.ON");
		assert this.getMode() == VacuumCleanerMode.HIGH
				: new PreconditionException("getMode() == VacuumCleanerMode.HIGH");

		this.currentMode = VacuumCleanerMode.LOW;
	}
}
// -----------------------------------------------------------------------------
