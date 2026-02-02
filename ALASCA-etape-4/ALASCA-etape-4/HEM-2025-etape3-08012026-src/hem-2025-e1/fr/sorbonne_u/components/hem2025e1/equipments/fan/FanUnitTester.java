package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserOutboundPort;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanUnitTester</code> implements a component performing unit
 * tests for the <code>Fan</code> component.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}
 * </pre>
 * * @author	Team DeMoh
 */
@RequiredInterfaces(required={FanUserCI.class,
							  FanInternalControlCI.class,
							  FanExternalControlCI.class,
							  ClocksServerCI.class})
public class			FanUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final int		SWITCH_ON_DELAY = 2;
	public static final int		SWITCH_OFF_DELAY = 9;

	public static boolean		VERBOSE = false;
	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;

	protected final boolean		isUnitTest;
	protected String			fanUserInboundPortURI;
	protected String			fanInternalControlInboundPortURI;
	protected String			fanExternalControlInboundPortURI;

	protected FanUserOutboundPort			fop;
	protected FanInternalControlOutboundPort	ficop;
	protected FanExternalControlOutboundPort	fecop;

	protected TestsStatistics	statistics;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(FanUnitTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.fanUserInboundPortURI != null &&
									!ht.fanUserInboundPortURI.isEmpty(),
				FanUnitTester.class, ht,
				"ht.fanUserInboundPortURI != null && "
							+ "!ht.fanUserInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.fanInternalControlInboundPortURI != null &&
							!ht.fanInternalControlInboundPortURI.isEmpty(),
				FanUnitTester.class, ht,
				"ht.fanInternalControlInboundPortURI != null && "
						+ "!ht.fanInternalControlInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.fanExternalControlInboundPortURI != null &&
							!ht.fanExternalControlInboundPortURI.isEmpty(),
				FanUnitTester.class, ht,
				"ht.fanExternalControlInboundPortURI != null &&"
						+ "!ht.fanExternalControlInboundPortURI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(FanUnitTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				FanUnitTester.class, ht,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				FanUnitTester.class, ht,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			FanUnitTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 Fan.USER_INBOUND_PORT_URI,
			 Fan.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 Fan.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	protected			FanUnitTester(
		boolean isUnitTest,
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(fanUserInboundPortURI,
						fanInternalControlInboundPortURI,
						fanExternalControlInboundPortURI);
	}

	protected			FanUnitTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
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
		this.fanUserInboundPortURI = fanUserInboundPortURI;
		this.fop = new FanUserOutboundPort(this);
		this.fop.publishPort();
		this.fanInternalControlInboundPortURI =
									fanInternalControlInboundPortURI;
		this.ficop = new FanInternalControlOutboundPort(this);
		this.ficop.publishPort();
		this.fanExternalControlInboundPortURI =
									fanExternalControlInboundPortURI;
		this.fecop = new FanExternalControlOutboundPort(this);
		this.fecop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Fan tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();

		assert	FanUnitTester.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanTester.implementationInvariants(this)");
		assert	FanUnitTester.invariants(this) :
				new InvariantException("FanTester.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	protected void		testOff()
	{
		this.logMessage("Feature: getting the state of the fan");
		this.logMessage("  Scenario: getting the state of the fan when off");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan has not been used yet");
		try {
			this.logMessage("    When I test the state of the fan");
			boolean result = !this.fop.on();
			if (result) {
				this.logMessage("    Then the state of the fan is off");
			} else {
				this.logMessage("     but was: on");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	protected void		testSwitchOnSwitchOff()
	{
		this.logMessage("Feature: switching on and off the fan");

		this.logMessage("  Scenario: switching on the fan when off");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan has not been used yet");
		boolean result;
		try {
			this.logMessage("    When I switch on the fan");
			this.fop.switchOn();
			result = this.fop.on();
			if (result) {
				this.logMessage("    Then the state of the fan is on");
			} else {
				this.logMessage("     but was: off");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: switching off the fan when on");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan is on");
		try {
			this.logMessage("    When I switch off the fan");
			this.fop.switchOff();
			result = !this.fop.on();
			if (result) {
				this.logMessage("    Then the state of the fan is off");
			} else {
				this.logMessage("     but was: on");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	protected void		testSpeeds()
	{
		this.logMessage("Feature: changing fan speeds");

		this.logMessage("  Scenario: switching on default speed");
		try {
			this.fop.switchOn();
			if (this.fop.getSpeed() == FanSpeed.LOW) {
				this.logMessage("    Then the default speed is LOW");
			} else {
				this.logMessage("     but was: " + this.fop.getSpeed());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but exception " + e + " raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting HIGH speed");
		try {
			this.fop.setHighSpeed();
			if (this.fop.getSpeed() == FanSpeed.HIGH) {
				this.logMessage("    Then the speed is HIGH");
			} else {
				this.logMessage("     but was: " + this.fop.getSpeed());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but exception " + e + " raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting LOW speed");
		try {
			this.fop.setLowSpeed();
			if (this.fop.getSpeed() == FanSpeed.LOW) {
				this.logMessage("    Then the speed is LOW");
			} else {
				this.logMessage("     but was: " + this.fop.getSpeed());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but exception " + e + " raised");
		}
		this.statistics.updateStatistics();

		try {
			this.fop.switchOff();
		} catch (Exception e) {}
	}
	
	protected void		testPowerLevel()
	{
		this.logMessage("Feature: getting and setting the power level of the"
						+ " fan");

		this.logMessage("  Scenario: getting the maximum power level through "
						+ "the user interface");
		this.logMessage("    Given the fan is initialised");
		Measure<Double> powerLevel = null;
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " user interface");
			powerLevel = this.fop.getMaxPowerLevel();
			if (powerLevel.getData() == Fan.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Fan.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the fan maximum "
								+ "power level");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " + powerLevel.getData());
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: getting the maximum power level through the"
						+ " external control interface");
		this.logMessage("    Given the fan is initialised");
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " external control interface");
			powerLevel = this.fecop.getMaxPowerLevel();
			if (powerLevel.getData() == Fan.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Fan.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the fan maximum "
								+ "power level");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " + powerLevel.getData());
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: getting the current power level through "
						+ "the user interface when just initialised");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan has not been used yet");
		this.logMessage("    And the fan is on");
		boolean result;
		SignalData<Double> powerLevelSignal = null;
		try {
			this.fop.switchOn();
			result = this.fop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current power level through the"
							+ " user interface");
			// Note: Default ON is LOW speed, so power should be LOW consumption
			powerLevelSignal =  this.fop.getCurrentPowerLevel();
			// Here we check if power > 0, exact value depends on implementation constant
			if (powerLevelSignal.getMeasure().getData() > 0.0 &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Fan.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the fan is consuming power");
			} else {
				this.logMessage("     but was: " +
									powerLevelSignal.getMeasure().getData());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	protected void		runAllUnitTests()
	{
		this.testOff();
		this.testSwitchOnSwitchOff();
		this.testSpeeds();
		this.testPowerLevel();

		this.statistics.statisticsReport(this);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.fop.getPortURI(),
					this.fanUserInboundPortURI,
					FanUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.ficop.getPortURI(),
					fanInternalControlInboundPortURI,
					FanInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.fecop.getPortURI(),
					fanExternalControlInboundPortURI,
					FanExternalControlConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	@Override
	public synchronized void	execute() throws Exception
	{
		if (this.isUnitTest) {
			this.runAllUnitTests();
		} else {
			ClocksServerOutboundPort clocksServerOutboundPort =
											new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.traceMessage("Fan tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
						clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant fanSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant fanSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Fan tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Fan tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(fanSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(fanSwitchOff);

			AbstractComponent o = this;

			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Fan switches on.\n");
								fop.switchOn();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);

			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Fan switches off.\n");
								fop.switchOff();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		}
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.fop.getPortURI());
		this.doPortDisconnection(this.ficop.getPortURI());
		this.doPortDisconnection(this.fecop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fop.unpublishPort();
			this.ficop.unpublishPort();
			this.fecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------