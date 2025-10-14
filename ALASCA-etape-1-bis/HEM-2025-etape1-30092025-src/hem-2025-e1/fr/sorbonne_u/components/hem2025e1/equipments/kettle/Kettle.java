package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserJava4InboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

@OfferedInterfaces(offered={KettleUserJava4CI.class,
							KettleInternalControlCI.class,
							KettleExternalControlJava4CI.class})
public class			Kettle
extends		AbstractComponent
implements	KettleUserI,
			KettleInternalControlI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	protected static enum	KettleState
	{
		/** kettle is on.													*/
		ON,
		/** kettle is heating.												*/
		HEATING,
		/** kettle keeps warm.												*/
		KEEP_WARM,
		/** kettle is off.													*/
		OFF
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	// BCM4Java information

	/** URI of the kettle inbound port used in tests.						*/
	public static final String		REFLECTION_INBOUND_PORT_URI =
															"KETTLE-RIP-URI";	

	/** URI of the kettle port for user interactions.						*/
	public static final String		USER_INBOUND_PORT_URI =
												"KETTLE-USER-INBOUND-PORT-URI";
	/** URI of the kettle port for internal control.						*/
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"KETTLE-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the kettle port for internal control.						*/
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"KETTLE-EXTERNAL-CONTROL-INBOUND-PORT-URI";


	/** inbound port offering the <code>KettleUserCI</code> interface.		*/
	protected KettleUserJava4InboundPort			kip;
	/** inbound port offering the <code>KettleInternalControlCI</code>
	 *  interface.															*/
	protected KettleInternalControlInboundPort		kicip;
	/** inbound port offering the <code>KettleExternalControlCI</code>
	 *  interface.															*/
	protected KettleExternalControlJava4InboundPort	kecip;

	/** when true, methods trace their actions.								*/
	public static boolean			VERBOSE = true;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int				X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int				Y_RELATIVE_POSITION = 0;

	// Appliance information
	
	/** standard target temperature for the water in celsius.				*/
	protected static final Measure<Double>	STANDARD_TARGET_TEMPERATURE =
												new Measure<>(
														19.0,
														TEMPERATURE_UNIT);
	/** fake current temperature, used when testing without simulation. 	*/
	public static final SignalData<Double>	FAKE_CURRENT_TEMPERATURE =
												new SignalData<>(
													new Measure<>(
															10.0,
															TEMPERATURE_UNIT));

	/** current state (on, off) of the kettle.								*/
	protected KettleState						currentState;
	/**	current power level of the kettle.									*/
	protected SignalData<Double>				currentPowerLevel;
	/** target temperature for the heating.									*/
	protected Measure<Double>					targetTemperature;

	protected static boolean	implementationInvariants(Kettle h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentState != null,
				Kettle.class, h,
				"h.currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.targetTemperature.getData() >=
							MIN_TARGET_TEMPERATURE.getData() &&
					h.targetTemperature.getData() <=
								MAX_TARGET_TEMPERATURE.getData(),
				Kettle.class, h,
				"targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && "
				+ "targetTemperature.getData() <= MIN_TARGET_TEMPERATURE.getData()");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentPowerLevel.getMeasure().getData() >= 0.0 &&
							h.currentPowerLevel.getMeasure().getData() <=
													MAX_POWER_LEVEL.getData(),
				Kettle.class, h,
				"currentPowerLevel.getMeasure().getData() >= 0.0 && "
				+ "currentPowerLevel.getMeasure().getData() <= "
				+ "MAX_POWER_LEVEL.getData()");
		return ret;
	}

	protected static boolean	invariants(Kettle h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= KettleTemperatureI.invariants(h);
		ret &= KettleExternalControlI.invariants(h);
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Kettle.class, h,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				Kettle.class, h,
				"USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Kettle.class, h,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Kettle.class, h,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
							+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				Kettle.class, h,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				Kettle.class, h,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	protected			Kettle() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	protected			Kettle(
		String kettleUserInboundPortURI,
		String kettleInternalControlInboundPortURI,
		String kettleExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(kettleUserInboundPortURI,
						kettleInternalControlInboundPortURI,
						kettleExternalControlInboundPortURI);
	}

	protected			Kettle(
		String reflectionInboundPortURI,
		String kettleUserInboundPortURI,
		String kettleInternalControlInboundPortURI,
		String kettleExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(kettleUserInboundPortURI,
						kettleInternalControlInboundPortURI,
						kettleExternalControlInboundPortURI);
	}

	protected void		initialise(
		String kettleUserInboundPortURI,
		String kettleInternalControlInboundPortURI,
		String kettleExternalControlInboundPortURI
		) throws Exception
	{
		assert	kettleUserInboundPortURI != null && !kettleUserInboundPortURI.isEmpty();
		assert	kettleInternalControlInboundPortURI != null && !kettleInternalControlInboundPortURI.isEmpty();
		assert	kettleExternalControlInboundPortURI != null && !kettleExternalControlInboundPortURI.isEmpty();

		this.currentState = KettleState.OFF;
		this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.kip = new KettleUserJava4InboundPort(kettleUserInboundPortURI, this);
		this.kip.publishPort();
		this.kicip = new KettleInternalControlInboundPort(
									kettleInternalControlInboundPortURI, this);
		this.kicip.publishPort();
		this.kecip = new KettleExternalControlJava4InboundPort(
									kettleExternalControlInboundPortURI, this);
		this.kecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Kettle component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}

		assert	Kettle.implementationInvariants(this) :
				new ImplementationInvariantException(
						"Kettle.implementationInvariants(this)");
		assert	Kettle.invariants(this) :
				new InvariantException("Kettle.invariants(this)");
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.kip.unpublishPort();
			this.kicip.unpublishPort();
			this.kecip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	@Override
	public boolean		on() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == KettleState.ON ||
									this.currentState == KettleState.HEATING ||
									this.currentState == KettleState.KEEP_WARM;
	}

	@Override
	public void			switchOn() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = KettleState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	@Override
	public void			switchOff() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = KettleState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle sets a new target "
										+ "temperature: " + target + ".\n");
		}

		assert	target != null &&
						TEMPERATURE_UNIT.equals(target.getMeasurementUnit()) :
				new PreconditionException(
						"target != null && TEMPERATURE_UNIT.equals("
						+ "target.getMeasurementUnit())");
		assert	target.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
						target.getData() <= MAX_TARGET_TEMPERATURE.getData() :
				new PreconditionException(
						"target.getData() >= MIN_TARGET_TEMPERATURE.getData() "
						+ "&& target.getData() <= MAX_TARGET_TEMPERATURE.getData()");

		this.targetTemperature = target;

		assert	getTargetTemperature().equals(target) :
				new PostconditionException(
						"getTargetTemperature().equals(target)");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleTemperatureI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns water target"
							+ " temperature " + this.targetTemperature + ".\n");
		}

		Measure<Double> ret = this.targetTemperature;

		assert	ret != null && TEMPERATURE_UNIT.equals(ret.getMeasurementUnit()) :
				new PostconditionException(
						"return != null && TEMPERATURE_UNIT.equals("
						+ "return.getMeasurementUnit())");
		assert	ret.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
							ret.getData() <= MAX_TARGET_TEMPERATURE.getData() :
				new PostconditionException(
						"return.getData() >= MIN_TARGET_TEMPERATURE.getData() "
						+ "&& return.getData() <= MAX_TARGET_TEMPERATURE.getData()");

		return ret;
	}


	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		SignalData<Double> currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns the current"
							+ " water temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	@Override
	public boolean		heating() throws Exception
	{
		System.out.println("Is HEATING ?");
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns its heating status " + 
						(this.currentState == KettleState.HEATING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == KettleState.HEATING;
	}

	@Override
	public void			startHeating() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		this.currentState = KettleState.HEATING;

		assert	this.heating() : new PostconditionException("heating()");
	}

	@Override
	public void			stopHeating() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		this.currentState = KettleState.KEEP_WARM;

		assert	!this.heating() : new PostconditionException("!heating()");
	}

	@Override
	public boolean		keepingWarm() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns its keeping warm status " + 
						(this.currentState == KettleState.KEEP_WARM) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == KettleState.KEEP_WARM;
	}

	@Override
	public void			startKeepingWarm() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle starts keeping warm.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.keepingWarm() : new PreconditionException("!keepingWarm()");

		this.currentState = KettleState.KEEP_WARM;

		assert	this.keepingWarm() : new PostconditionException("keepingWarm()");
	}

	@Override
	public void			stopKeepingWarm() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle stops keeping warm.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.keepingWarm() : new PreconditionException("keepingWarm()");

		this.currentState = KettleState.ON;

		assert	!this.keepingWarm() : new PostconditionException("!keepingWarm()");
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle sets its power level to " + 
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

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		if (Kettle.VERBOSE) {
			this.traceMessage("Kettle returns its current power level " + 
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
