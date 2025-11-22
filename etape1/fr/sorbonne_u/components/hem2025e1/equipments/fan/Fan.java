package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserJava4InboundPort;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

@OfferedInterfaces(offered={FanUserJava4CI.class,
							FanInternalControlCI.class,
							FanExternalControlJava4CI.class})
public class			Fan
extends		AbstractComponent
implements	FanUserI,
			FanInternalControlI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String		REFLECTION_INBOUND_PORT_URI = "Fan-RIP-URI";	
	public static final String		USER_INBOUND_PORT_URI = "FAN-USER-INBOUND-PORT-URI";
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-INTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	protected static final double	CONSUMPTION_LOW = 20.0;
	protected static final double	CONSUMPTION_HIGH = 60.0;

	protected FanUserJava4InboundPort			fip;
	protected FanInternalControlInboundPort		ficip;
	protected FanExternalControlJava4InboundPort	fecip;

	public static boolean			VERBOSE = false;
	public static int				X_RELATIVE_POSITION = 0;
	public static int				Y_RELATIVE_POSITION = 0;

	protected FanSpeed				currentSpeed;
	protected SignalData<Double>	currentPowerLevel;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(Fan h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentSpeed != null,
				Fan.class, h,
				"h.currentSpeed != null");
		
		return ret;
	}

	protected static boolean	invariants(Fan h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= FanExternalControlI.invariants(h); // Vérifie les constantes externes
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h, "REFLECTION_INBOUND_PORT_URI valid");
		ret &= AssertionChecking.checkInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h, "USER_INBOUND_PORT_URI valid");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h, "INTERNAL_CONTROL_INBOUND_PORT_URI valid");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				Fan.class, h, "EXTERNAL_CONTROL_INBOUND_PORT_URI valid");
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

		this.currentSpeed = FanSpeed.OFF;
		this.currentPowerLevel = new SignalData<>(new Measure<>(0.0, POWER_UNIT));

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
				new ImplementationInvariantException("Fan.implementationInvariants(this)");
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
	// User Implementation
	// -------------------------------------------------------------------------

	@Override
	public boolean		on() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its state (Speed: " + this.currentSpeed + ").\n");
		}
		return this.currentSpeed != FanSpeed.OFF;
	}

	@Override
	public void			switchOn() throws Exception
	{
		if (Fan.VERBOSE) { this.traceMessage("Fan switches on (Default LOW).\n"); }
		assert	!this.on() : new PreconditionException("!on()");

		this.updateSpeed(FanSpeed.LOW);

		assert	this.on() : new PostconditionException("on()");
		assert  this.getSpeed() == FanSpeed.LOW : new PostconditionException("Speed is LOW");
	}

	@Override
	public void			switchOff() throws Exception
	{
		if (Fan.VERBOSE) { this.traceMessage("Fan switches off.\n"); }
		assert	this.on() : new PreconditionException("on()");

		this.updateSpeed(FanSpeed.OFF);

		assert	!this.on() : new PostconditionException("!on()");
	}

	@Override
	public void			setLowSpeed() throws Exception
	{
		if (Fan.VERBOSE) { this.traceMessage("Fan sets to LOW speed.\n"); }
		assert	this.on() : new PreconditionException("on()");
		
		this.updateSpeed(FanSpeed.LOW);
		
		assert	this.getSpeed() == FanSpeed.LOW : new PostconditionException("Speed is LOW");
	}

	@Override
	public void			setHighSpeed() throws Exception
	{
		if (Fan.VERBOSE) { this.traceMessage("Fan sets to HIGH speed.\n"); }
		assert	this.on() : new PreconditionException("on()");
		
		this.updateSpeed(FanSpeed.HIGH);
		
		assert	this.getSpeed() == FanSpeed.HIGH : new PostconditionException("Speed is HIGH");
	}

	@Override
	public FanSpeed		getSpeed() throws Exception
	{
		return this.currentSpeed;
	}

	// Helper interne pour mettre à jour vitesse et puissance
	private void updateSpeed(FanSpeed s) {
		this.currentSpeed = s;
		double power = 0.0;
		switch(s) {
			case LOW: power = CONSUMPTION_LOW; break;
			case HIGH: power = CONSUMPTION_HIGH; break;
			case OFF: default: power = 0.0; break;
		}
		this.currentPowerLevel = new SignalData<>(new Measure<>(power, POWER_UNIT));
	}

	// -------------------------------------------------------------------------
	// Internal Control (Not used for simple appliance, but required by interface)
	// -------------------------------------------------------------------------
	
	@Override
	public boolean		running() throws Exception {
		return on();
	}

	// -------------------------------------------------------------------------
	// External Control
	// -------------------------------------------------------------------------

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its max power level " + MAX_POWER_LEVEL + ".\n");
		}
		return MAX_POWER_LEVEL;
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan sets its power level to " + powerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");
		assert	powerLevel != null && powerLevel.getData() >= 0.0 &&
							powerLevel.getMeasurementUnit().equals(POWER_UNIT) :
				new PreconditionException("powerLevel valid");

		/*if (powerLevel.getData() <= 0.001) {
			if (Fan.VERBOSE) { this.traceMessage("Power request is 0. Switching off.\n"); }
			this.switchOff();
			return;
		}*/

		// Sinon, on met a jour le niveau de puissance
		if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
			this.currentPowerLevel = new SignalData<>(powerLevel);
		} else {
			this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		}
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its current power level " + this.currentPowerLevel + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		if (!this.running()) {
			return new SignalData<>(new Measure<>(0.0, POWER_UNIT));
		}

		SignalData<Double> ret = this.currentPowerLevel;

		assert	ret != null && ret.getMeasure().getMeasurementUnit().equals(POWER_UNIT) :
				new PostconditionException("return valid unit");
		
		return ret;
	}
}