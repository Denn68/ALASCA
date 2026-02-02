package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserJava4InboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.alasca.physical_data.TimedMeasure;

@RequiredInterfaces(required = { ClocksServerCI.class })
@OfferedInterfaces(offered = { WashingMachineUserJava4CI.class, WashingMachineInternalControlCI.class,
		WashingMachineExternalControlJava4CI.class })
public class WashingMachine extends AbstractComponent implements WashingMachineUserI, WashingMachineInternalControlI {
	public static enum WashingMachineState {
		/** washing machine is on. */
		ON,
		/** washing machine is heating water. */
		HEATINGWATER,
		/** washing machine is washing. */
		WASHING,
		/** washing machine is off. */
		OFF
	}

	public static final String REFLECTION_INBOUND_PORT_URI = "Washing-Machine-RIP-URI";

	public static final String USER_INBOUND_PORT_URI = "WASHING-MACHINE-USER-INBOUND-PORT-URI";

	public static final String INTERNAL_CONTROL_INBOUND_PORT_URI = "WASHING-MACHINE-INTERNAL-CONTROL-INBOUND-PORT-URI";

	public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI = "WASHING-MACHINE-EXTERNAL-CONTROL-INBOUND-PORT-URI";

	protected WashingMachineUserJava4InboundPort wmip;

	protected WashingMachineInternalControlInboundPort wmicip;

	protected WashingMachineExternalControlJava4InboundPort wmecip;
	
	protected AcceleratedClock clock;
	
	protected ClocksServerOutboundPort csop;

	public static boolean VERBOSE = false;

	public static int X_RELATIVE_POSITION = 0;

	public static int Y_RELATIVE_POSITION = 0;

	protected static final Measure<Double> STANDARD_TARGET_TEMPERATURE = new Measure<>(40.0, TEMPERATURE_UNIT);

	public static final SignalData<Double> FAKE_CURRENT_TEMPERATURE = new SignalData<>(
			new Measure<>(10.0, TEMPERATURE_UNIT));

	protected WashingMachineState currentState;
	protected SignalData<Double> currentPowerLevel;
	protected TimedMeasure<Double> targetTemperature;
	
	protected final ScheduledExecutorService scheduler;

	protected ScheduledFuture<?> washingFuture;

	protected ScheduledFuture<?> delayFuture;

	protected long remainingDelayMS = 0L;

	protected long remainingWashingTimeMS = 0L;

	protected long delayStartInstantMS = 0L;
	protected long washingStartInstantMS = 0L;

	protected Measure<Double> programmedTargetTemperature;

	protected static boolean implementationInvariants(WashingMachine h) {
		assert h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(h.currentState != null, WashingMachine.class, h,
				"h.currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData()
						&& h.targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData(),
				WashingMachine.class, h, "targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() && "
						+ "targetTemperature.getData() <= MIN_TARGET_TEMPERATURE.getData()");
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentPowerLevel.getMeasure().getData() >= 0.0
						&& h.currentPowerLevel.getMeasure().getData() <= MAX_POWER_LEVEL.getData(),
				WashingMachine.class, h, "currentPowerLevel.getMeasure().getData() >= 0.0 && "
						+ "currentPowerLevel.getMeasure().getData() <= " + "MAX_POWER_LEVEL.getData()");
		return ret;
	}

	protected static boolean invariants(WashingMachine h) {
		assert h != null : new PreconditionException("h != null");

		boolean ret = true;
		ret &= WashingMachineTemperatureI.invariants(h);
		ret &= WashingMachineExternalControlI.invariants(h);
		ret &= AssertionChecking.checkInvariant(
				REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty(), WashingMachine.class, h,
				"REFLECTION_INBOUND_PORT_URI != null && " + "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h, "USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"INTERNAL_CONTROL_INBOUND_PORT_URI != null && " + "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(
				EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
				WashingMachine.class, h,
				"EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&" + "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
		ret &= AssertionChecking.checkInvariant(X_RELATIVE_POSITION >= 0, WashingMachine.class, h,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(Y_RELATIVE_POSITION >= 0, WashingMachine.class, h,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	protected WashingMachine() throws Exception {
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI, EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	protected WashingMachine(String washingMachineUserInboundPortURI,
			String washingMachineInternalControlInboundPortURI, String washingMachineExternalControlInboundPortURI)
			throws Exception {
		super(1, 0);
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.initialise(washingMachineUserInboundPortURI, washingMachineInternalControlInboundPortURI,
				washingMachineExternalControlInboundPortURI);
	}

	protected WashingMachine(String reflectionInboundPortURI, String washingMachineUserInboundPortURI,
			String washingMachineInternalControlInboundPortURI, String washingMachineExternalControlInboundPortURI)
			throws Exception {
		super(reflectionInboundPortURI, 1, 0);
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.initialise(washingMachineUserInboundPortURI, washingMachineInternalControlInboundPortURI,
				washingMachineExternalControlInboundPortURI);
	}

	protected void initialise(String washingMachineUserInboundPortURI,
			String washingMachineInternalControlInboundPortURI, String washingMachineExternalControlInboundPortURI)
			throws Exception {
		assert washingMachineUserInboundPortURI != null && !washingMachineUserInboundPortURI.isEmpty();
		assert washingMachineInternalControlInboundPortURI != null
				&& !washingMachineInternalControlInboundPortURI.isEmpty();
		assert washingMachineExternalControlInboundPortURI != null
				&& !washingMachineExternalControlInboundPortURI.isEmpty();

		this.currentState = WashingMachineState.OFF;
		this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		this.targetTemperature =
				new TimedMeasure<>(
						STANDARD_TARGET_TEMPERATURE.getData(),
						STANDARD_TARGET_TEMPERATURE.getMeasurementUnit());

		this.wmip = new WashingMachineUserJava4InboundPort(washingMachineUserInboundPortURI, this);
		this.wmip.publishPort();
		this.wmicip = new WashingMachineInternalControlInboundPort(washingMachineInternalControlInboundPortURI, this);
		this.wmicip.publishPort();
		this.wmecip = new WashingMachineExternalControlJava4InboundPort(washingMachineExternalControlInboundPortURI,
				this);
		this.wmecip.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("WashingMachine component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert WashingMachine.implementationInvariants(this)
				: new ImplementationInvariantException("WashingMachine.implementationInvariants(this)");
		assert WashingMachine.invariants(this) : new InvariantException("WashingMachine.invariants(this)");
	}
	
	@Override
	public synchronized void start() throws ComponentStartException {
	    super.start();
	    try {
	        this.csop = new ClocksServerOutboundPort(this);
	        this.csop.publishPort();
	        this.doPortConnection(
	                this.csop.getPortURI(),
	                ClocksServer.STANDARD_INBOUNDPORT_URI,
	                ClocksServerConnector.class.getCanonicalName());

	        this.clock = this.csop.getClock("hem-clock"); 
	    } catch (Exception e) {
	        throw new ComponentStartException(e);
	    }
	}
	
	@Override
	public synchronized void execute() throws Exception {
	    System.out.println("WashingMachine: execute() started. Waiting for clock..."); // DEBUG
	    
	    if (this.clock != null) {
	        this.clock.waitUntilStart();
	    }
	    
	    System.out.println("WashingMachine: Clock started ! Simulation begins."); // DEBUG
	}
	
	@Override
	public synchronized void finalise() throws Exception {
	    if (this.csop != null && this.csop.connected()) {
	        this.doPortDisconnection(this.csop.getPortURI());
	    }
	    super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
	    try {
	        if (this.csop != null && this.csop.isPublished()) {
	            this.csop.unpublishPort();
	        }
	        
	        try {
				this.wmip.unpublishPort();
				this.wmicip.unpublishPort();
				this.wmecip.unpublishPort();
				this.scheduler.shutdownNow();
			} catch (Throwable e) {
				throw new ComponentShutdownException(e);
			}
	        
	    } catch (Throwable e) {
	        throw new ComponentShutdownException(e);
	    }
	    super.shutdown();
	}

	@Override
	public boolean on() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its state: " + this.currentState + ".\n");
		}
		return this.currentState == WashingMachineState.ON || this.currentState == WashingMachineState.WASHING
				|| this.currentState == WashingMachineState.HEATINGWATER;
	}

	@Override
	public void switchOn() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine switches on.\n");
		}

		assert !this.on() : new PreconditionException("!on()");

		this.currentState = WashingMachineState.ON;

		assert this.on() : new PostconditionException("on()");
	}

	@Override
	public void switchOff() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine switches off.\n");
		}

		assert this.on() : new PreconditionException("on()");

		this.currentState = WashingMachineState.OFF;

		assert !this.on() : new PostconditionException("!on()");
	}

	@Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets a new target " + "temperature: " + target + ".\n");
		}

		assert target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit()) : new PreconditionException(
				"target != null && TEMPERATURE_UNIT.equals(" + "target.getMeasurementUnit())");
		assert target.getData() >= MIN_TARGET_TEMPERATURE.getData()
				&& target.getData() <= MAX_TARGET_TEMPERATURE.getData()
				: new PreconditionException("target.getData() >= MIN_TARGET_TEMPERATURE.getData() "
						+ "&& target.getData() <= MAX_TARGET_TEMPERATURE.getData()");

		this.targetTemperature =
				new TimedMeasure<Double>(target.getData(),
						 target.getMeasurementUnit());

		assert getTargetTemperature().getMeasure().equals(target)
				: new PostconditionException("getTargetTemperature().equals(target)");
	}

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its target" + " temperature " + this.targetTemperature + ".\n");
		}

		SignalData<Double> ret = new SignalData<Double>(this.targetTemperature);

		assert ret != null && TEMPERATURE_UNIT.equals(ret.getMeasure().getMeasurementUnit()) : new PostconditionException(
				"return != null && TEMPERATURE_UNIT.equals(" + "return.getMeasurementUnit())");
		assert ret.getMeasure().getData() >= MIN_TARGET_TEMPERATURE.getData() && ret.getMeasure().getData() <= MAX_TARGET_TEMPERATURE.getData()
				: new PostconditionException("return.getData() >= MIN_TARGET_TEMPERATURE.getData() "
						+ "&& return.getData() <= MAX_TARGET_TEMPERATURE.getData()");

		return ret;
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		assert this.on() : new PreconditionException("on()");

		// Temporary implementation; would need a temperature sensor.
		SignalData<Double> currentTemperature = FAKE_CURRENT_TEMPERATURE;
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns the current" + " temperature " + currentTemperature + ".\n");
		}

		return currentTemperature;
	}

	@Override
	public boolean heatWater() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its heating status "
					+ (this.currentState == WashingMachineState.HEATINGWATER) + ".\n");
		}

		assert this.on() : new PreconditionException("on()");

		return this.currentState == WashingMachineState.HEATINGWATER;
	}

	@Override
	public void startHeatingWater() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine starts heating.\n");
		}
		assert this.on() : new PreconditionException("on()");
		assert !this.heatWater() : new PreconditionException("!heatWater()");

		this.currentState = WashingMachineState.HEATINGWATER;

		assert this.heatWater() : new PostconditionException("heatWater()");
	}

	@Override
	public void stopHeatingWater() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine stops heating.\n");
		}
		assert this.on() : new PreconditionException("on()");
		assert this.heatWater() : new PreconditionException("heating()");

		// Arrêt de la chauffe.
		this.currentState = WashingMachineState.ON;


		assert !this.heatWater() : new PostconditionException("!heating()");
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its max power level " + MAX_POWER_LEVEL + ".\n");
		}

		return MAX_POWER_LEVEL;
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine sets its power level to " + powerLevel + ".\n");
		}

		assert this.on() : new PreconditionException("on()");
		assert powerLevel != null && powerLevel.getData() >= 0.0 && powerLevel.getMeasurementUnit().equals(POWER_UNIT)
				: new PreconditionException("powerLevel != null && powerLevel.getData() >= 0.0 && "
						+ "powerLevel.getMeasurementUnit().equals(POWER_UNIT)");

		if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
			this.currentPowerLevel = new SignalData<>(powerLevel);
		} else {
			this.currentPowerLevel = new SignalData<>(MAX_POWER_LEVEL);
		}

		assert powerLevel.getData() > getMaxPowerLevel().getData()
				|| getCurrentPowerLevel().getMeasure().getData() == powerLevel.getData()
				: new PostconditionException("powerLevel.getData() > getMaxPowerLevel().getData() "
						+ "|| getCurrentPowerLevel().getData() == " + "powerLevel.getData()");
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its current power level " + this.currentPowerLevel + ".\n");
		}

		assert this.on() : new PreconditionException("on()");

		SignalData<Double> ret = this.currentPowerLevel;

		assert ret != null && ret.getMeasure().getMeasurementUnit().equals(POWER_UNIT) : new PreconditionException(
				"return != null && return.getMeasure()." + "getMeasurementUnit().equals(POWER_UNIT)");
		assert ret.getMeasure().getData() >= 0.0 && ret.getMeasure().getData() <= getMaxPowerLevel().getData()
				: new PostconditionException("return.getMeasure().getData() >= 0.0 && "
						+ "return.getMeasure().getData() <= " + "getMaxPowerLevel().getData()");

		return ret;
	}

	@Override
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine starts washing.\n");
		}
		assert this.on() : new PreconditionException("on()");
		assert this.currentState == WashingMachineState.ON : new PreconditionException("currentState == ON");
		assert !this.heatWater() : new PreconditionException("!heatWater()");
		assert !this.isWashing() : new PreconditionException("!isWashing()");

		this.setTargetTemperature(target);

		double current = this.getCurrentTemperature().getMeasure().getData();

		this.programmedTargetTemperature = target;
		this.remainingWashingTimeMS = washingTimeMS;

		this.remainingDelayMS = 0L;
		if (this.delayFuture != null && !this.delayFuture.isDone()) {
			this.delayFuture.cancel(false);
		}


		// NOTE: la chauffe est simulée au niveau des modèles de simulation (TemperatureModel),
		// le composant démarre le cycle de lavage immédiatement.
		this.washingStartInstantMS = this.clock.currentInstant().toEpochMilli();
		this.currentState = WashingMachineState.WASHING;

		// on annule une éventuelle ancienne tâche
		if (this.washingFuture != null && !this.washingFuture.isDone()) {
			this.washingFuture.cancel(false);
		}

		long realDelay = toRealTime(washingTimeMS);

		this.washingFuture = scheduler.schedule(() -> {
			try {
				this.currentState = WashingMachineState.ON;
				this.remainingWashingTimeMS = 0L;
				this.programmedTargetTemperature = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, realDelay, TimeUnit.MILLISECONDS);
}

	@Override
	public void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception {
		assert this.on() : new PreconditionException("on()");

		// on annule un éventuel ancien délai
		if (this.delayFuture != null && !this.delayFuture.isDone()) {
			this.delayFuture.cancel(false);
		}

		this.remainingDelayMS = delayMS;
		this.remainingWashingTimeMS = washingTimeMS;
		this.programmedTargetTemperature = target;
		this.delayStartInstantMS = this.clock.currentInstant().toEpochMilli();

        long realDelay = Math.max(0L, toRealTime(delayMS) - 1L);

		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine will start in " + delayMS + " ms.\n");
		}

		this.delayFuture = scheduler.schedule(() -> {
			try {
				// au moment où le délai finit, on lance un lavage
				this.remainingDelayMS = 0L;
					this.remainingDelayMS = 0L;
					startWashing(this.remainingWashingTimeMS, this.programmedTargetTemperature);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, realDelay, TimeUnit.MILLISECONDS);
	}
	
	public void suspendCycle() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine suspends current cycle.\n");
		}

		assert this.on() : new PreconditionException("on()");

		long now = this.clock.currentInstant().toEpochMilli();

		// 1) Si un lavage est en cours, on le met en pause
		if (this.currentState == WashingMachineState.WASHING &&
				this.washingFuture != null &&
				!this.washingFuture.isDone()) {

			long elapsed = now - this.washingStartInstantMS;
			this.remainingWashingTimeMS = Math.max(0L, this.remainingWashingTimeMS - elapsed);

			this.washingFuture.cancel(false);
			this.currentState = WashingMachineState.ON; // machine toujours allumée mais à l'arrêt
			this.remainingDelayMS = 0L;

			if (WashingMachine.VERBOSE) {
				this.traceMessage("Washing paused, remaining time = " + this.remainingWashingTimeMS + " ms.\n");
			}
		}

		// 2) Si un départ différé est en cours, on le met en pause
		if (this.delayFuture != null && !this.delayFuture.isDone()) {
			long elapsedDelay = now - this.delayStartInstantMS;
			this.remainingDelayMS = Math.max(0L, this.remainingDelayMS - elapsedDelay);

			this.delayFuture.cancel(false);

			if (WashingMachine.VERBOSE) {
				this.traceMessage("Delayed start paused, remaining delay = " + this.remainingDelayMS + " ms.\n");
			}
		}
	}
	
	public void resumeCycle() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine resumes current cycle.\n");
		}

		assert this.on() : new PreconditionException("on()");

		long now = this.clock.currentInstant().toEpochMilli();

		// 1) S'il reste encore un délai à consommer, on reprend le compte à rebours
		if (this.remainingDelayMS > 0L &&
				(this.delayFuture == null || this.delayFuture.isDone())) {

			this.delayStartInstantMS = now;
			long realDelay = Math.max(0L, toRealTime(this.remainingDelayMS) - 1L);

			if (WashingMachine.VERBOSE) {
				this.traceMessage("Resuming delayed start with remaining delay = " + realDelay + " ms.\n");
			}

			this.delayFuture = scheduler.schedule(() -> {
				try {
					this.remainingDelayMS = 0L;
					startWashing(this.remainingWashingTimeMS, this.programmedTargetTemperature);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, realDelay, TimeUnit.MILLISECONDS);

			return;
		}

		// 2) Sinon, si on a un lavage en pause, on reprend le lavage
		if (this.remainingWashingTimeMS > 0L &&
				(this.washingFuture == null || this.washingFuture.isDone())) {

			this.currentState = WashingMachineState.WASHING;
			this.washingStartInstantMS = now;
			long realWashingDelay = toRealTime(this.remainingWashingTimeMS);

			if (WashingMachine.VERBOSE) {
				this.traceMessage("Resuming washing, remaining time = " + realWashingDelay + " ms.\n");
			}

			this.washingFuture = scheduler.schedule(() -> {
				try {
					this.currentState = WashingMachineState.ON;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, realWashingDelay, TimeUnit.MILLISECONDS);
		}
	}



	@Override
	public boolean isWashing() throws Exception {
		if (WashingMachine.VERBOSE) {
			this.traceMessage("Washing Machine returns its state: " + this.currentState + ".\n");
		}
		return this.currentState == WashingMachineState.WASHING;
	}
	
	protected long toRealTime(long simulatedDelayMS) {
        if (this.clock == null) return simulatedDelayMS;
        return (long) (simulatedDelayMS / this.clock.getAccelerationFactor());
    }
}
// -----------------------------------------------------------------------------
