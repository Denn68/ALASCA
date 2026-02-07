package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.HEM;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.RegistrationConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.RegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserJava4InboundPort;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Fan</code> implements a fan component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * *
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * currentSpeed != null
 * }
 * invariant	{@code
 * currentPowerLevel != null
 * }
 * </pre>
 * 
 * *
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * * @author Team DeMoh
 */
@OfferedInterfaces(offered = { FanUserJava4CI.class,
		FanInternalControlCI.class,
		FanExternalControlJava4CI.class })
@RequiredInterfaces(required = { RegistrationCI.class })
public class Fan
		extends AbstractComponent
		implements FanUserI,
		FanInternalControlI {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** URI of the fan inbound port used in tests. */
	public static final String REFLECTION_INBOUND_PORT_URI = "Fan-RIP-URI";
	/** URI of the fan port for user interactions. */
	public static final String USER_INBOUND_PORT_URI = "FAN-USER-INBOUND-PORT-URI";
	/** URI of the fan port for internal control. */
	public static final String INTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-INTERNAL-CONTROL-INBOUND-PORT-URI";
	/** URI of the fan port for internal control. */
	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	// === Enregistrement dynamique auprès du HEM ===
	/** identifiant unique du ventilateur pour l'enregistrement. */
	protected static final String FAN_UID = "FN10000";
	/** chemin vers le descripteur XML du connecteur adapté. */
	protected static final String XML_FAN_DESCRIPTOR = "hem-adapter/fanci-descriptor.xml";
	/** port sortant pour l'enregistrement auprès du HEM. */
	protected RegistrationOutboundPort registrationPort;
	protected boolean registrationConnected = false;

	public static final Measure<Double> LOW_POWER_IN_WATTS = new Measure<>(20.0, MeasurementUnit.WATTS);
	public static final Measure<Double> HIGH_POWER_IN_WATTS = new Measure<>(60.0, MeasurementUnit.WATTS);
	public static final Measure<Double> VOLTAGE = new Measure<>(220.0, MeasurementUnit.VOLTS);

	/** inbound port offering the <code>FanUserCI</code> interface. */
	protected FanUserJava4InboundPort fip;
	/** inbound port offering the <code>FanInternalControlCI</code> interface. */
	protected FanInternalControlInboundPort ficip;
	/** inbound port offering the <code>FanExternalControlCI</code> interface. */
	protected FanExternalControlJava4InboundPort fecip;

	/** when true, methods trace their actions. */
	public static boolean VERBOSE = false;
	/** when tracing, x coordinate of the window relative position. */
	public static int X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position. */
	public static int Y_RELATIVE_POSITION = 0;

	/** current speed of the fan. */
	protected FanSpeed currentSpeed;
	/** current power level of the fan. */
	protected SignalData<Double> currentPowerLevel;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * h != null
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @param h instance to be tested.
	 * @return true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean implementationInvariants(Fan h) {
		assert h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentSpeed != null,
				Fan.class, h,
				"h.currentSpeed != null");

		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * h != null
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @param h instance to be tested.
	 * @return true if the invariants are observed, false otherwise.
	 */
	protected static boolean invariants(Fan h) {
		assert h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= FanExternalControlI.invariants(h);
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

	/**
	 * create a new fan.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * true
	 * }	// no precondition.
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 * 
	 * * @throws Exception <i>to do</i>.
	 */
	protected Fan() throws Exception {
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
				EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	/**
	 * create a new fan.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 * 
	 * * @param fanUserInboundPortURI URI of the inbound port to call the fan
	 * component for user interactions.
	 * 
	 * @param fanInternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for internal control.
	 * @param fanExternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for external control.
	 * @throws Exception <i>to do</i>.
	 */
	protected Fan(
			String fanUserInboundPortURI,
			String fanInternalControlInboundPortURI,
			String fanExternalControlInboundPortURI) throws Exception {
		super(1, 0);
		this.initialise(fanUserInboundPortURI,
				fanInternalControlInboundPortURI,
				fanExternalControlInboundPortURI);
	}

	/**
	 * create a new fan.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 * 
	 * * @param reflectionInboundPortURI URI of the reflection inbound port of the
	 * component.
	 * 
	 * @param fanUserInboundPortURI            URI of the inbound port to call the
	 *                                         fan component for user interactions.
	 * @param fanInternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for internal control.
	 * @param fanExternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for external control.
	 * @throws Exception <i>to do</i>.
	 */
	protected Fan(
			String reflectionInboundPortURI,
			String fanUserInboundPortURI,
			String fanInternalControlInboundPortURI,
			String fanExternalControlInboundPortURI) throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.initialise(fanUserInboundPortURI,
				fanInternalControlInboundPortURI,
				fanExternalControlInboundPortURI);
	}

	/**
	 * initialise the fan component.
	 * *
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * *
	 * 
	 * <pre>
	 * pre	{@code
	 * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
	 * }
	 * pre	{@code
	 * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @param fanUserInboundPortURI            URI of the inbound port to call the
	 *                                         fan component for user interactions.
	 * @param fanInternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for internal control.
	 * @param fanExternalControlInboundPortURI URI of the inbound port to call the
	 *                                         fan component for external control.
	 * @throws Exception <i>to do</i>.
	 */
	protected void initialise(
			String fanUserInboundPortURI,
			String fanInternalControlInboundPortURI,
			String fanExternalControlInboundPortURI) throws Exception {
		assert fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty();
		assert fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty();
		assert fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty();

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

		assert Fan.implementationInvariants(this)
				: new ImplementationInvariantException("Fan.implementationInvariants(this)");
		assert Fan.invariants(this) : new InvariantException("Fan.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			if (AbstractCVM.isPublishedInLocalRegistry(
					HEM.REGISTRATION_INBOUND_PORT_URI)) {
				this.registrationPort = new RegistrationOutboundPort(this);
				this.registrationPort.publishPort();
				this.doPortConnection(
						this.registrationPort.getPortURI(),
						HEM.REGISTRATION_INBOUND_PORT_URI,
						RegistrationConnector.class.getCanonicalName());
				this.registrationConnected = true;
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void finalise() throws Exception {
		if (this.registrationConnected) {
			this.doPortDisconnection(this.registrationPort.getPortURI());
			this.registrationConnected = false;
		}
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.fip.unpublishPort();
			this.ficip.unpublishPort();
			this.fecip.unpublishPort();
			if (this.registrationPort != null) {
				if (this.registrationConnected) {
					this.doPortDisconnection(this.registrationPort.getPortURI());
					this.registrationConnected = false;
				}
				if (this.registrationPort.isPublished()) {
					this.registrationPort.unpublishPort();
				}
			}
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// User Implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#on()
	 */
	@Override
	public boolean on() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its state (Speed: " + this.currentSpeed + ").\n");
		}
		return this.currentSpeed != FanSpeed.OFF;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOn()
	 */
	@Override
	public void switchOn() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan switches on (Default LOW).\n");
		}
		assert !this.on() : new PreconditionException("!on()");

		this.currentSpeed = FanSpeed.LOW;
		this.currentPowerLevel = new SignalData<>(LOW_POWER_IN_WATTS);

		if (this.registrationConnected) {
			this.registrationPort.register(
					FAN_UID,
					this.fecip.getPortURI(),
					XML_FAN_DESCRIPTOR);
			if (Fan.VERBOSE) {
				this.traceMessage("Fan registered with HEM as "
						+ FAN_UID + ".\n");
			}
		}

		assert this.on() : new PostconditionException("on()");
		assert this.getSpeed() == FanSpeed.LOW : new PostconditionException("Speed is LOW");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOff()
	 */
	@Override
	public void switchOff() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan switches off.\n");
		}
		assert this.on() : new PreconditionException("on()");

		if (this.registrationConnected) {
			try {
				this.registrationPort.unregister(FAN_UID);
				if (Fan.VERBOSE) {
					this.traceMessage("Fan unregistered from HEM.\n");
				}
			} catch (Exception e) {
				if (Fan.VERBOSE) {
					this.traceMessage("Fan unregister warning: "
							+ e.getMessage() + "\n");
				}
			}
		}

		this.currentSpeed = FanSpeed.OFF;
		this.currentPowerLevel = new SignalData<>(new Measure<>(0.0, POWER_UNIT));

		assert !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#setLowSpeed()
	 */
	@Override
	public void setLowSpeed() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan sets to LOW speed.\n");
		}
		assert this.on() : new PreconditionException("on()");

		this.currentSpeed = FanSpeed.LOW;
		this.currentPowerLevel = new SignalData<>(LOW_POWER_IN_WATTS);

		assert this.getSpeed() == FanSpeed.LOW : new PostconditionException("Speed is LOW");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#setHighSpeed()
	 */
	@Override
	public void setHighSpeed() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan sets to HIGH speed.\n");
		}
		assert this.on() : new PreconditionException("on()");

		this.currentSpeed = FanSpeed.HIGH;
		this.currentPowerLevel = new SignalData<>(HIGH_POWER_IN_WATTS);

		assert this.getSpeed() == FanSpeed.HIGH : new PostconditionException("Speed is HIGH");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#getSpeed()
	 */
	@Override
	public FanSpeed getSpeed() throws Exception {
		return this.currentSpeed;
	}

	// -------------------------------------------------------------------------
	// Internal Control
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI#running()
	 */
	@Override
	public boolean running() throws Exception {
		return on();
	}

	// -------------------------------------------------------------------------
	// External Control
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its max power level " + MAX_POWER_LEVEL + ".\n");
		}
		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel)
			throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan sets its power level to " + powerLevel + ".\n");
		}

		assert this.on() : new PreconditionException("on()");
		assert powerLevel != null && powerLevel.getData() >= 0.0 &&
				powerLevel.getMeasurementUnit().equals(POWER_UNIT) : new PreconditionException("powerLevel valid");

		// Sinon, on met a jour le niveau de puissance
		if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
			this.currentPowerLevel = new SignalData<>(powerLevel);
		} else {
			this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		if (Fan.VERBOSE) {
			this.traceMessage("Fan returns its current power level " + this.currentPowerLevel + ".\n");
		}

		assert this.on() : new PreconditionException("on()");

		if (!this.running()) {
			return new SignalData<>(new Measure<>(0.0, POWER_UNIT));
		}

		SignalData<Double> ret = this.currentPowerLevel;

		assert ret != null && ret.getMeasure().getMeasurementUnit().equals(POWER_UNIT)
				: new PostconditionException("return valid unit");

		return ret;
	}
}