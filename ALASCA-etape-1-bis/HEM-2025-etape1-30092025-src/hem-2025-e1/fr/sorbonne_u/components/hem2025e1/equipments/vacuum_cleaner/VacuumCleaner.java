package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;

@OfferedInterfaces(offered={VacuumCleanerUserCI.class})
public class			VacuumCleaner
extends		AbstractComponent
implements	VacuumCleanerImplementationI
{
	public static final String			REFLECTION_INBOUND_PORT_URI =
														"VACUUM-CLEANER-RIP-URI";	
	
	
	public static final String			INBOUND_PORT_URI =
												"VACUUM-CLEANER-INBOUND-PORT-URI";

	/** when true, methods trace their actions.								*/
	public static boolean				VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int					X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int					Y_RELATIVE_POSITION = 0;

	public static final Measure<Double>	HIGH_POWER_IN_WATTS =
											new Measure<Double>(
														1100.0,
														MeasurementUnit.WATTS);
	public static final Measure<Double>	LOW_POWER_IN_WATTS =
											new Measure<Double>(
														660.0,
														MeasurementUnit.WATTS);
	public static final Measure<Double>	VOLTAGE =
											new Measure<Double>(
														220.0,
														MeasurementUnit.VOLTS);

	/** initial state of the hair dryer.									*/
	protected static final HairDryerState	INITIAL_STATE = HairDryerState.OFF;
	/** initial mode of the hair dryer.										*/
	protected static final HairDryerMode	INITIAL_MODE = HairDryerMode.LOW;

	/** current state (on, off) of the hair dryer.							*/
	protected HairDryerState			currentState;
	/** current mode of operation (low, high) of the hair dryer.			*/
	protected HairDryerMode				currentMode;

	/** inbound port offering the <code>HairDryerCI</code> interface.		*/
	protected HairDryerInboundPort		hdip;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hd	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(VacuumCleaner hd)
	{
		assert	hd != null : new PreconditionException("hd != null");

		boolean ret = true;

		ret &= AssertionChecking.checkInvariant(
				INITIAL_STATE != null,
				VacuumCleaner.class, hd,
				"INITIAL_STATE != null");
		ret &= AssertionChecking.checkInvariant(
				INITIAL_MODE != null,
				VacuumCleaner.class, hd,
				"INITIAL_MODE != null");
		ret &= AssertionChecking.checkInvariant(
				hd.currentState != null,
				VacuumCleaner.class, hd,
				"hd.currentState != null");
		ret &= AssertionChecking.checkInvariant(
				hd.currentMode != null,
				VacuumCleaner.class, hd,
				"hd.currentMode != null");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param hd	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(VacuumCleaner hd)
	{
		assert	hd != null : new PreconditionException("hd != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, hd,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty(),
				VacuumCleaner.class, hd,
				"INBOUND_PORT_URI != null && !INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				X_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, hd,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkImplementationInvariant(
				Y_RELATIVE_POSITION >= 0,
				VacuumCleaner.class, hd,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @throws Exception	<i>to do</i>.
	 */
	protected			VacuumCleaner() throws Exception
	{
		this(INBOUND_PORT_URI);
	}

	/**
	 * create a hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null && !hairDryerInboundPortURI.isEmpty()}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			VacuumCleaner(String hairDryerInboundPortURI)
	throws Exception
	{
		this(REFLECTION_INBOUND_PORT_URI, hairDryerInboundPortURI);
	}

	/**
	 * create a hair dryer component with the given reflection innbound port
	 * URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code hairDryerInboundPortURI != null && !hairDryerInboundPortURI.isEmpty()}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection innbound port of the component.
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			VacuumCleaner(
		String reflectionInboundPortURI,
		String hairDryerInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(hairDryerInboundPortURI);
	}

	/**
	 * initialise the hair dryer component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code hairDryerInboundPortURI != null && !hairDryerInboundPortURI.isEmpty()}
	 * post	{@code getState() == HairDryerState.OFF}
	 * post	{@code getMode() == HairDryerMode.LOW}
	 * </pre>
	 * 
	 * @param hairDryerInboundPortURI	URI of the hair dryer inbound port.
	 * @throws Exception				<i>to do</i>.
	 */
	protected void		initialise(String hairDryerInboundPortURI)
	throws Exception
	{
		assert	hairDryerInboundPortURI != null :
					new PreconditionException(
										"hairDryerInboundPortURI != null");
		assert	!hairDryerInboundPortURI.isEmpty() :
					new PreconditionException(
										"!hairDryerInboundPortURI.isEmpty()");

		this.currentState = INITIAL_STATE;
		this.currentMode = INITIAL_MODE;
		this.hdip = new HairDryerInboundPort(hairDryerInboundPortURI, this);
		this.hdip.publishPort();

		if (VacuumCleaner.VERBOSE) {
			this.tracer.get().setTitle("Hair dryer component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	VacuumCleaner.implementationInvariants(this) :
				new ImplementationInvariantException(
						"HairDryer.implementationInvariants(this)");
		assert	VacuumCleaner.invariants(this) :
				new InvariantException("HairDryer.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hdip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#getState()
	 */
	@Override
	public HairDryerState	getState() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer returns its state : " +
													this.currentState + ".\n");
		}

		return this.currentState;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#getMode()
	 */
	@Override
	public HairDryerMode	getMode() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer returns its mode : " +
													this.currentMode + ".\n");
		}

		return this.currentMode;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#turnOn()
	 */
	@Override
	public void			turnOn() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer is turned on.\n");
		}

		assert	this.getState() == HairDryerState.OFF :
				new PreconditionException("getState() == HairDryerState.OFF");

		this.currentState = HairDryerState.ON;
		this.currentMode = HairDryerMode.LOW;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#turnOff()
	 */
	@Override
	public void			turnOff() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer is turned off.\n");
		}

		assert	this.getState() == HairDryerState.ON :
				new PreconditionException("getState() == HairDryerState.ON");

		this.currentState = HairDryerState.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#setHigh()
	 */
	@Override
	public void			setHigh() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer is set high.\n");
		}

		assert	this.getState() == HairDryerState.ON :
				new PreconditionException("getState() == HairDryerState.ON");
		assert	this.getMode() == HairDryerMode.LOW :
				new PreconditionException("getMode() == HairDryerMode.LOW");

		this.currentMode = HairDryerMode.HIGH;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.hairdryer.HairDryerImplementationI#setLow()
	 */
	@Override
	public void			setLow() throws Exception
	{
		if (VacuumCleaner.VERBOSE) {
			this.traceMessage("Hair dryer is set low.\n");
		}

		assert	this.getState() == HairDryerState.ON :
				new PreconditionException("getState() == HairDryerState.ON");
		assert	this.getMode() == HairDryerMode.HIGH :
				new PreconditionException("getMode() == HairDryerMode.HIGH");

		this.currentMode = HairDryerMode.LOW;
	}

	@Override
	public void setMedium() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
// -----------------------------------------------------------------------------
