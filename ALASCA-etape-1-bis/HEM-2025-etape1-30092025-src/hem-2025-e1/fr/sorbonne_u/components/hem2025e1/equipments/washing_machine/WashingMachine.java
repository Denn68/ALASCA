package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

@OfferedInterfaces(offered={WashingMachineUserJava4CI.class,
							WashingMachineInternalControlCI.class,
							WashingMachineExternalControlJava4CI.class})
public class			WashingMachine
extends		AbstractComponent
implements	WashingMachineUserI,
			WashingMachineControlI
{
	protected static enum	WashingMachineState
	{
		/** washing machine is on.													*/
		ON,
		/** washing machine is heating water.												*/
		HEATINGWATER,
		/** washing machine is washing.												*/
		WASHING,
		/** washing machine is off.													*/
		OFF
	}

	public static final String		REFLECTION_INBOUND_PORT_URI =
															"Washing-Machine-RIP-URI";	

	public static final String		USER_INBOUND_PORT_URI =
												"WASHING-MACHINE-USER-INBOUND-PORT-URI";
	
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"WASHING-MACHINE-INTERNAL-CONTROL-INBOUND-PORT-URI";
	
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"WASHING-MACHINE-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	protected WashingMachineUserJava4InboundPort	wmip;
	
	protected WashingMachineInternalControlInboundPort		wmicip;
	
	protected WashingMachineExternalControlJava4InboundPort	wmecip;

	public static boolean			VERBOSE = false;
	
	public static int				X_RELATIVE_POSITION = 0;
	
	public static int				Y_RELATIVE_POSITION = 0;
	
	protected static final Measure<Double>	STANDARD_TARGET_TEMPERATURE =
												new Measure<>(
														40.0,
														TEMPERATURE_UNIT);
	
	public static final SignalData<Double>	FAKE_CURRENT_TEMPERATURE =
												new SignalData<>(
													new Measure<>(
															10.0,
															TEMPERATURE_UNIT));

	protected WashingMachineState						currentState;
	protected SignalData<Double>				currentPowerLevel;
	protected Measure<Double>					targetTemperature;

	protected static boolean	implementationInvariants(WashingMachine h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentState != null,
				WashingMachine.class, h,
				"h.currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.targetTemperature.getData() >=
							MIN_TARGET_TEMPERATURE.getData() &&
					h.targetTemperature.getData() <=
								MAX_TARGET_TEMPERATURE.getData(),
				WashingMachine.class, h,
				"targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && "
				+ "targetTemperature.getData() <= MIN_TARGET_TEMPERATURE.getData()");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentPowerLevel.getMeasure().getData() >= 0.0 &&
							h.currentPowerLevel.getMeasure().getData() <=
													MAX_POWER_LEVEL.getData(),
				WashingMachine.class, h,
				"currentPowerLevel.getMeasure().getData() >= 0.0 && "
				+ "currentPowerLevel.getMeasure().getData() <= "
				+ "MAX_POWER_LEVEL.getData()");
		return ret;
	}

	protected static boolean	invariants(WashingMachine h)
	{
		assert	h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= WashingMachineTemperatureI.invariants(h);
		ret &= WashingMachineExternalControlI.invariants(h);
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null &&
									!REFLECTION_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"REFLECTION_INBOUND_PORT_URI != null && "
								+ "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
							+ "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
								!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
							+ "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				WashingMachine.class, h,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				WashingMachine.class, h,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	protected			WashingMachine() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}
	
	protected			WashingMachine(
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 0);
		this.initialise(washingMachineUserInboundPortURI,
						washingMachineInternalControlInboundPortURI,
						washingMachineExternalControlInboundPortURI);
	}
	
	protected			WashingMachine(
		String reflectionInboundPortURI,
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.initialise(washingMachineUserInboundPortURI,
						washingMachineInternalControlInboundPortURI,
						washingMachineExternalControlInboundPortURI);
	}
	
	protected void		initialise(
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		assert	washingMachineUserInboundPortURI != null && !washingMachineUserInboundPortURI.isEmpty();
		assert	washingMachineInternalControlInboundPortURI != null && !washingMachineInternalControlInboundPortURI.isEmpty();
		assert	washingMachineExternalControlInboundPortURI != null && !washingMachineExternalControlInboundPortURI.isEmpty();

		this.currentState = WashingMachineState.OFF;
		this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		this.targetTemperature = STANDARD_TARGET_TEMPERATURE;

		this.hip = new WashingMachineUserJava4InboundPort(washingMachineUserInboundPortURI, this);
		this.hip.publishPort();
		this.hicip = new HeaterInternalControlInboundPort(
					washingMachineInternalControlInboundPortURI, this);
		this.hicip.publishPort();
		this.hecip = new HeaterExternalControlJava4InboundPort(
					 washingMachineExternalControlInboundPortURI, this);
		this.hecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("WashingMachine component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}

		assert	WashingMachine.implementationInvariants(this) :
				new ImplementationInvariantException(
						"WashingMachine.implementationInvariants(this)");
		assert	WashingMachine.invariants(this) :
				new InvariantException("WashingMachine.invariants(this)");
	}
	
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wmip.unpublishPort();
			this.wmicip.unpublishPort();
			this.wmecip.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserI#on()
	 */
	@Override
	public boolean		on() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == HeaterState.ON ||
									this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserI#switchOn()
	 */
	@Override
	public void			switchOn() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = HeaterState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserI#switchOff()
	 */
	@Override
	public void			switchOff() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = HeaterState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserI#setTargetTemperature(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater sets a new target "
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
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterTemperatureI#getTargetTemperature()
	 */
	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns its target"
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

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterTemperatureI#getCurrentTemperature()
	 */
	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		assert	this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		SignalData<Double> currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterInternalControlI#heating()
	 */
	@Override
	public boolean		heating() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns its heating status " + 
						(this.currentState == HeaterState.HEATING) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == HeaterState.HEATING;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterInternalControlI#startHeating()
	 */
	@Override
	public void			startHeating() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heating() : new PreconditionException("!heating()");

		this.currentState = HeaterState.HEATING;

		assert	this.heating() : new PostconditionException("heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterInternalControlI#stopHeating()
	 */
	@Override
	public void			stopHeating() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heating() : new PreconditionException("heating()");

		this.currentState = HeaterState.ON;

		assert	!this.heating() : new PostconditionException("!heating()");
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlI#getMaxPowerLevel()
	 */
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
	 */
	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater sets its power level to " + 
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
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterExternalControlI#getCurrentPowerLevel()
	 */
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Heater returns its current power level " + 
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

	@Override
	public boolean heatWater() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startHeatingWater() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopHeatingWater() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delayedStart() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
// -----------------------------------------------------------------------------
