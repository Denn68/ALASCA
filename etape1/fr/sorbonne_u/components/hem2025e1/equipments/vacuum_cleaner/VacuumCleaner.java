package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={VacuumCleanerUserCI.class})
public class			VacuumCleaner
extends		AbstractComponent
implements	VacuumCleanerImplementationI
{
	public static final String			REFLECTION_INBOUND_PORT_URI =
														"VACUUM-CLEANER-RIP-URI";	
	
	
	public static final String			INBOUND_PORT_URI =
												"VACUUM-CLEANER-INBOUND-PORT-URI";

	public static boolean				VERBOSE = false;
	public static int					X_RELATIVE_POSITION = 0;
	public static int					Y_RELATIVE_POSITION = 0;

	public static final Measure<Double>	HIGH_POWER_IN_WATTS =
											new Measure<Double>(
														2000.0,
														MeasurementUnit.WATTS);
	
	public static final Measure<Double>	MEDIUM_POWER_IN_WATTS =
					new Measure<Double>(
								1000.0,
								MeasurementUnit.WATTS);
	
	public static final Measure<Double>	LOW_POWER_IN_WATTS =
											new Measure<Double>(
														650.0,
														MeasurementUnit.WATTS);
	public static final Measure<Double>	VOLTAGE =
											new Measure<Double>(
														220.0,
														MeasurementUnit.VOLTS);

	/** initial state of the vacuum cleaner								*/
	protected static final VacuumCleanerState	INITIAL_STATE = VacuumCleanerState.OFF;
	/** initial mode of the vacuum cleaner									*/
	protected static final VacuumCleanerMode	INITIAL_MODE = VacuumCleanerMode.MEDIUM;

	/** current state (on, off) of the vacuum cleaner.							*/
	protected VacuumCleanerState			currentState;
	/** current mode of operation (low, high) of the vacuum cleaner.			*/
	protected VacuumCleanerMode				currentMode;

	/** inbound port offering the <code>VacuumCleanerCI</code> interface.		*/
	protected VacuumCleanerInboundPort		vcdip;

	protected static boolean	implementationInvariants(VacuumCleaner vc)
	{
		assert	vc != null : new PreconditionException("vc != null");

		boolean ret = true;

		ret &= AssertionChecking.checkInvariant(
				INITIAL_STATE != null,
				VacuumCleaner.class, vc,
				"INITIAL_STATE != null");
		ret &= AssertionChecking.checkInvariant(
				INITIAL_MODE != null,
				VacuumCleaner.class, vc,
				"INITIAL_MODE != null");
		ret &= AssertionChecking.checkInvariant(
				vc.currentState != null,
				VacuumCleaner.class, vc,
				"hd.currentState != null");
		ret &= AssertionChecking.checkInvariant(
				vc.currentMode != null,
				VacuumCleaner.class, vc,
				"hd.currentMode != null");
		return ret;
	}
	
	protected static boolean	invariants(VacuumCleaner vc)
	{
		assert	vc != null : new PreconditionException("hd != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, vc,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, vc,
				"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				X_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, vc,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkImplementationInvariant(
				Y_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, vc,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}
	
	protected			VacuumCleaner() throws Exception
	{
		this(INBOUND_PORT_URI);
	}
	
	protected			VacuumCleaner(String vacuumCleanerInboundPortURI)
	throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, vacuumCleanerInboundPortURI);
	}


	protected			VacuumCleaner(
		String reflectionInboundPortURI,
		String vacuumCleanerInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(vacuumCleanerInboundPortURI);
	}

	protected void		initialise(String vacuumCleanerInboundPortURI)
	throws Exception
	{
		assert	vacuumCleanerInboundPortURI != null :
					new PreconditionException(
										"vacuumCleanerInboundPortURI != null");
		assert	!vacuumCleanerInboundPortURI.isEmpty() :
					new PreconditionException(
										"!vacuumCleanerInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.vcdip = new VacuumCleanerInboundPort(vacuumCleanerInboundPortURI, this);
		this.vcdip.publishPort();

		if (VacuumCleaner.VERBOSE) {
			this.tracer.get().setTitle("Vacuum cleaner component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	VacuumCleaner.implementationInvariants(this) :
				new ImplementationInvariantException(
						"VacuumCleaner.implementationInvariants(this)");
		assert	VacuumCleaner.invariants(this) :
				new InvariantException("VacuumCleaner.invariants(this)");
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.vcdip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	
	@Override
	public VacuumCleanerMode	getMode() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	@Override
	public void			turnOn() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is turned on.\n");
		}

		assert	this.getState() == VacuumCleanerState.OFF :
				new PreconditionException("getState() == VacuumCleaner.OFF");

		this.currentState = VacuumCleanerState.ON;
		this.currentMode = VacuumCleanerMode.MEDIUM;
	}

	@Override
	public void			turnOff() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is turned off.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
				new PreconditionException("getState() == VacuumCleanerState.ON");

		this.currentState = VacuumCleanerState.OFF;
	}

	@Override
	public void			setHigh() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is set high.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
				new PreconditionException("getState() == VacuumCleanerState.ON");
		assert	(this.getMode() == VacuumCleanerMode.LOW) ||  (this.getMode() == VacuumCleanerMode.MEDIUM):
				new PreconditionException("(getMode() == VacuumCleanerMode.LOW) || (getMode() == VacuumCleanerMode.MEDIUM)");

		this.currentMode = VacuumCleanerMode.HIGH;
	}

	@Override
	public void			setMedium() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is set medium.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
				new PreconditionException("getState() == VacuumCleanerState.ON");
		assert	(this.getMode() == VacuumCleanerMode.HIGH) ||  (this.getMode() == VacuumCleanerMode.LOW):
				new PreconditionException("(getMode() == VacuumCleanerMode.HIGH) || (getMode() == VacuumCleanerMode.MEDIUM)");

		this.currentMode = VacuumCleanerMode.MEDIUM;
	}

	@Override
	public void			setLow() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Vacuum cleaner is set low.\n");
		}

		assert	this.getState() == VacuumCleanerState.ON :
				new PreconditionException("getState() == VacuumCleanerState.ON");
		assert	(this.getMode() == VacuumCleanerMode.HIGH) ||  (this.getMode() == VacuumCleanerMode.MEDIUM):
				new PreconditionException("(getMode() == VacuumCleanerMode.HIGH) || (getMode() == VacuumCleanerMode.MEDIUM)");

		this.currentMode = VacuumCleanerMode.LOW;
	}
}
// -----------------------------------------------------------------------------
