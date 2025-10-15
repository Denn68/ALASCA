package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserJava4InboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

@OfferedInterfaces(offered={WashingMachineUserJava4CI.class,
							WashingMachineInternalControlCI.class,
							WashingMachineExternalControlJava4CI.class})
public class			WashingMachine
extends		AbstractComponent
implements	WashingMachineUserI,
			WashingMachineInternalControlI
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

		this.wmip = new WashingMachineUserJava4InboundPort(washingMachineUserInboundPortURI, this);
		this.wmip.publishPort();
		this.wmicip = new WashingMachineInternalControlInboundPort(
					washingMachineInternalControlInboundPortURI, this);
		this.wmicip.publishPort();
		this.wmecip = new WashingMachineExternalControlJava4InboundPort(
					 washingMachineExternalControlInboundPortURI, this);
		this.wmecip.publishPort();

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
	@Override
	public boolean		on() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == WashingMachineState.ON ||
									this.currentState == WashingMachineState.WASHING || 
											this.currentState == WashingMachineState.HEATINGWATER;
	}
	
	@Override
	public void			switchOn() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine switches on.\n");
		}

		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = WashingMachineState.ON;

		assert	 this.on() : new PostconditionException("on()");
	}
	
	@Override
	public void			switchOff() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine switches off.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		this.currentState = WashingMachineState.OFF;

		assert	 !this.on() : new PostconditionException("!on()");
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target "
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

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its target"
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
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns the current"
							+ " temperature " + currentTemperature + ".\n");
		}

		return  currentTemperature;
	}

	@Override
	public boolean	heatWater() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its heating status " + 
						(this.currentState == WashingMachineState.HEATINGWATER) + ".\n");
		}

		assert	this.on() : new PreconditionException("on()");

		return this.currentState == WashingMachineState.HEATINGWATER;
	}
	
	@Override
	public void	startHeatingWater() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine starts heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	!this.heatWater() : new PreconditionException("!heatWater()");

		this.currentState = WashingMachineState.HEATINGWATER;

		assert	this.heatWater() : new PostconditionException("heatWater()");
	}

	@Override
	public void	stopHeatingWater() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine stops heating.\n");
		}
		assert	this.on() : new PreconditionException("on()");
		assert	this.heatWater() : new PreconditionException("heating()");

		this.currentState = WashingMachineState.ON;

		assert	!this.heatWater() : new PostconditionException("!heating()");
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its max power level " + 
					MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets its power level to " + 
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
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its current power level " + 
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
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine starts washing.\n");
		}
		assert this.on() : new PreconditionException("on()");
		assert this.currentState == WashingMachineState.ON :
			new PreconditionException("currentState == ON");
		assert !this.heatWater() : new PreconditionException("!heatWater()");
		assert !this.isWashing() : new PreconditionException("!isWashing()");

		this.setTargetTemperature(target);

		double current = this.getCurrentTemperature().getMeasure().getData();
		if (current < target.getData()) {
			this.startHeatingWater();
			// ici on simule que la température est atteinte immédiatement
			this.stopHeatingWater();
		}

		this.currentState = WashingMachineState.WASHING;
		
		ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
		sch.schedule(() -> {
			try {
				this.currentState = WashingMachineState.ON;
			} catch (Exception e) {
				e.printStackTrace();
			}
			sch.shutdown();
		}, washingTimeMS, TimeUnit.MILLISECONDS);
	}
	@Override
	public void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception {
		ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
		
		assert	this.on() : new PreconditionException("on()");
		
	    sch.schedule(() -> {
	    	try {
				startWashing(washingTimeMS, target);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        sch.shutdown();
	    }, delayMS, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean isWashing() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its state: " +
											this.currentState + ".\n");
		}
		return this.currentState == WashingMachineState.WASHING;
	}
}
// -----------------------------------------------------------------------------
