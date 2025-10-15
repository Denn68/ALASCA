package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserJava4InboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

@OfferedInterfaces(offered={FanUserJava4CI.class,
							FanInternalControlCI.class,
							FanExternalControlJava4CI.class})
public class			Fan
extends		AbstractComponent
implements	FanUserI,
			FanInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	protected static enum	FanState
	{
		/** fan is on.													*/
		ON,
		/** fan is running.												*/
		RUNNING,
		/** fan is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	// BCM4Java information

	/** URI of the fan inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"Fan-RIP-URI";	

	/** URI of the fan port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"FAN-USER-INBOUND-PORT-URI";
	/** URI of the fan port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"FAN-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the fan port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"FAN-EXTERNAL-CONTROL-INBOUND-PORT-URI";


	/** inbound port offering the <code>FanUserCI</code> interface.		*/
	protected FanUserJava4InboundPort			fip;
	/** inbound port offering the <code>FanInternalControlCI</code>
	 *  interface.															*/
	protected FanInternalControlInboundPort		ficip;
	/** inbound port offering the <code>FanExternalControlCI</code>
	 *  interface.															*/
	protected FanExternalControlJava4InboundPort	fecip;

	/** when true, methods trace their actions.								*/
	public static boolean			VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int				X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int				Y_RELATIVE_POSITION = 0;

	// Appliance information

	/** current state (on, off) of the fan.								*/
	protected FanState						currentState;
	/**	current power level of the fan.									*/
	protected SignalData<Double>				currentPowerLevel;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(Fan h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentState != null,
				Fan.class, h,
				"h.currentState != null");
		
		return ret;
	}

	protected static boolean	invariants(Fan h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h,
				"USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
							+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				Fan.class, h,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				Fan.class, h,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			Fan() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	protected			Fan(
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(fanUserInboundPortURI,
						fanInternalControlInboundPortURI,
						fanExternalControlInboundPortURI);
	}

	protected			Fan(
		String reflectionInboundPortURI,
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(fanUserInboundPortURI,
						fanInternalControlInboundPortURI,
						fanExternalControlInboundPortURI);
	}

	protected void		initialise(
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		assert	fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty();
		assert	fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty();
		assert	fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty();

		this.currentState = FanState.OFF;
		this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);

		this.fip = new FanUserJava4InboundPort(fanUserInboundPortURI, this);
		this.fip.publishPort();
		this.ficip = new FanInternalControlInboundPort(
									fanInternalControlInboundPortURI, this);
		this.ficip.publishPort();
		this.fecip = new FanExternalControlJava4InboundPort(
									fanExternalControlInboundPortURI, this);
		this.fecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Fan component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}

		assert	Fan.implementationInvariants(this) :
				new ImplementationInvariantException(
						"Fan.implementationInvariants(this)");
		assert	Fan.invariants(this) :
				new InvariantException("Fan.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fip.unpublishPort();
			this.ficip.unpublishPort();
			this.fecip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == FanState.ON ||
									this.currentState == FanState.RUNNING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = FanState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = FanState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI#running()
	 */
	@Override
	public boolean		running() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its running status " + 
						(this.currentState == FanState.RUNNING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == FanState.RUNNING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI#startRunning()
	 */
	@Override
	public void			startRunning() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan starts running.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.running() : new PreconditionException("!running()");

		this.currentState = FanState.RUNNING;

		assert	this.running() : new PostconditionException("running()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI#stopRunning()
	 */
	@Override
	public void			stopRunning() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan stops running.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.running() : new PreconditionException("running()");

		this.currentState = FanState.ON;

		assert	!this.running() : new PostconditionException("!running()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan sets its power level to " + 
														powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel != null && powerLevel.getData() >= 0.0 &&
							powerLevel.getMeasurementUnit().equals(POWER_UNIT) :
				new PreconditionException(
						"powerLevel != null && powerLevel.getData() >= 0.0 && "
						+ "powerLevel.getMeasurementUnit().equals(POWER_UNIT)");

		if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
			this.currentPowerLevel = new SignalData<>(powerLevel);
		} else {
			this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		}

		assert	powerLevel.getData() > getMaxPowerLevel().getData() ||
						getCurrentPowerLevel().getMeasure().getData() ==
														powerLevel.getData() :
				new PostconditionException(
						"powerLevel.getData() > getMaxPowerLevel().getData() "
						+ "|| getCurrentPowerLevel().getData() == "
						+ "powerLevel.getData()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its current power level " + 
					this.currentPowerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		SignalData<Double> ret = this.currentPowerLevel;

		assert	ret != null && ret.getMeasure().getMeasurementUnit().
															equals(POWER_UNIT) :
				new PreconditionException(
						"return != null && return.getMeasure()."
						+ "getMeasurementUnit().equals(POWER_UNIT)");
		assert	ret.getMeasure().getData() >= 0.0 &&
					ret.getMeasure().getData() <= getMaxPowerLevel().getData() :
				new PostconditionException(
							"return.getMeasure().getData() >= 0.0 && "
							+ "return.getMeasure().getData() <= "
							+ "getMaxPowerLevel().getData()");

		return ret;
	}
}
// -----------------------------------------------------------------------------
