package fr.sorbonne_u.components.hem2025e1.equipments.kettle;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserOutboundPort;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleUnitTester</code> implements a component performing 
 * unit tests for the <code>Kettle</code> component.
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
@RequiredInterfaces(required={KettleUserCI.class,
							  KettleInternalControlCI.class,
							  KettleExternalControlCI.class,
							  ClocksServerCI.class})
public class			KettleUnitTester
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
	protected String			kettleUserInboundPortURI;
	protected String			kettleInternalControlInboundPortURI;
	protected String			kettleExternalControlInboundPortURI;

	protected KettleUserOutboundPort			kop;
	protected KettleInternalControlOutboundPort	kicop;
	protected KettleExternalControlOutboundPort	kecop;

	protected TestsStatistics	statistics;

	protected static boolean	implementationInvariants(KettleUnitTester kt)
	{
		assert	kt != null : new PreconditionException("kt != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				kt.kettleUserInboundPortURI != null &&
									!kt.kettleUserInboundPortURI.isEmpty(),
				KettleUnitTester.class, kt,
				"kt.kettleUserInboundPortURI != null && "
							+ "!kt.kettleUserInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				kt.kettleInternalControlInboundPortURI != null &&
							!kt.kettleInternalControlInboundPortURI.isEmpty(),
				KettleUnitTester.class, kt,
				"kt.kettleInternalControlInboundPortURI != null && "
						+ "!kt.kettleInternalControlInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				kt.kettleExternalControlInboundPortURI != null &&
							!kt.kettleExternalControlInboundPortURI.isEmpty(),
				KettleUnitTester.class, kt,
				"kt.kettleExternalControlInboundPortURI != null &&"
						+ "!kt.kettleExternalControlInboundPortURI.isEmpty()");
		return ret;
	}
	protected static boolean	invariants(KettleUnitTester kt)
	{
		assert	kt != null : new PreconditionException("kt != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				KettleUnitTester.class, kt,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				KettleUnitTester.class, kt,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}
	protected			KettleUnitTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 Kettle.USER_INBOUND_PORT_URI,
			 Kettle.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 Kettle.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}
	
	protected			KettleUnitTester(
		boolean isUnitTest,
		String kettleUserInboundPortURI,
		String kettleInternalControlInboundPortURI,
		String kettleExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(kettleUserInboundPortURI,
						kettleInternalControlInboundPortURI,
						kettleExternalControlInboundPortURI);
	}
	
	protected			KettleUnitTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String kettleUserInboundPortURI,
		String kettleInternalControlInboundPortURI,
		String kettleExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
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
		this.kettleUserInboundPortURI = kettleUserInboundPortURI;
		this.kop = new KettleUserOutboundPort(this);
		this.kop.publishPort();
		this.kettleInternalControlInboundPortURI =
									kettleInternalControlInboundPortURI;
		this.kicop = new KettleInternalControlOutboundPort(this);
		this.kicop.publishPort();
		this.kettleExternalControlInboundPortURI =
									kettleExternalControlInboundPortURI;
		this.kecop = new KettleExternalControlOutboundPort(this);
		this.kecop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Kettle tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();

		assert	KettleUnitTester.implementationInvariants(this) :
				new ImplementationInvariantException(
						"KettleTester.implementationInvariants(this)");
		assert	KettleUnitTester.invariants(this) :
				new InvariantException("KettleTester.invariants(this)");
	}
	
	protected void		testOff()
	{
		this.logMessage("Feature: getting the state of the kettle");
		this.logMessage("  Scenario: getting the state of the kettle when off");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		try {
			this.logMessage("    When I test the state of the kettle");
			boolean result = !this.kop.on();
			if (result) {
				this.logMessage("    Then the state of the kettle is off");
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
		this.logMessage("Feature: switching on and off the kettle");

		this.logMessage("  Scenario: switching on the kettle when off");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		boolean result;
		try {
			this.logMessage("    When I switch on the kettle");
			this.kop.switchOn();
			result = this.kop.on();
			if (result) {
				this.logMessage("    Then the state of the kettle is on");
			} else {
				this.logMessage("     but was: off");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: switching off the kettle when on");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I switch off the kettle");
			this.kop.switchOff();
			result = !this.kop.on();
			if (result) {
				this.logMessage("    Then the state of the kettle is off");
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
	
	protected void		testTargetTemperature()
	{
		this.logMessage("Feature: getting and setting the target temperature"
						+ " of the kettle");

		this.logMessage("  Scenario: getting the target temperature through the"
						+ " user interface when just initialised");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		boolean result;
		Measure<Double> temperature = null;
		try {
			this.kop.switchOn();
			result = this.kop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the target temperature through the "
							+ "user interface");
			temperature = this.kop.getTargetTemperature();
			if (temperature.getData() ==
									Kettle.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the kettle"
								+ " is the kettle standard target temperature");
			} else {
				this.logMessage("     but was: " + temperature.getData());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: getting the target temperature through the internal control interface when just initialised");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I get the target temperature through the internal control interface");
			temperature = this.kicop.getTargetTemperature();
			if (temperature.getData() ==
									Kettle.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the kettle"
								+ " is the kettle standard target temperature");
			} else {
				this.logMessage("     but was: " + temperature.getData());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the target temperature of the "
						+ "kettle when on");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			result = this.kop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}

			this.logMessage("    When I set the temperature at any given "
							+ "temperature between 10 and 100 Celsius inclusive");
			this.kop.setTargetTemperature(
					new Measure<Double>(85.0, Kettle.TEMPERATURE_UNIT));
			temperature = this.kop.getTargetTemperature();
			if (temperature.getData() == 85.0 &&
				temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the kettle"
								+ " is the given temperature");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was not: " + temperature.getData());
			}
			this.kop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	protected void		testCurrentTemperature()
	{
		this.logMessage("Feature: getting the current temperature"
						+ " in the room of the kettle");

		this.logMessage("  Scenario: getting the current temperature through "
						+ "the user interface when on");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		
		boolean result;
		
		SignalData<Double> temperature = null;
		try {
			this.kop.switchOn();
			result = this.kop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current temperature of the "
							+ "kettle through the user interface");
			
			temperature = this.kop.getCurrentTemperature();
			if (temperature.getMeasure().getData() == 
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
				temperature.getMeasure().getMeasurementUnit().equals(
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().
														getMeasurementUnit())) {
				this.logMessage("    Then the current temperature is the kettle"
								+ " standard current temperature");
			} else {
				this.logMessage("     but was: " + temperature.getMeasure().getData());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: getting the current temperature through "
						+ "the internal control interface when on");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I get the current temperature of the "
							+ "kettle through the user interface");
			temperature = this.kicop.getCurrentTemperature();
			if (temperature.getMeasure().getData() == 
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
				temperature.getMeasure().getMeasurementUnit().equals(
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().
														getMeasurementUnit())) {
				this.logMessage("    Then the current temperature is the kettle"
								+ " standard current temperature");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " +
										temperature.getMeasure().getData());
			}
			this.kop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}
	
	protected void		testPowerLevel()
	{
		this.logMessage("Feature: getting and setting the power level of the"
						+ " kettle");

		this.logMessage("  Scenario: getting the maximum power level through "
						+ "the user interface");
		this.logMessage("    Given the kettle is initialised");
		Measure<Double> powerLevel = null;
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " user interface");
			powerLevel = this.kop.getMaxPowerLevel();
			if (powerLevel.getData() == Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the kettle maximum "
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
		this.logMessage("    Given the kettle is initialised");
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " external control interface");
			powerLevel = this.kecop.getMaxPowerLevel();
			if (powerLevel.getData() == Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the kettle maximum "
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
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		boolean result;
		SignalData<Double> powerLevelSignal = null;
		try {
			this.kop.switchOn();
			result = this.kop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current power level through the"
							+ " user interface");
			powerLevelSignal =  this.kop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
											Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the kettle maximum "
								+ "power level");
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

		this.logMessage("  Scenario: getting the current power level through "
						+ "the external control interface when just initialised");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle has not been used yet");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I get the current power level through the"
							+ " external control interface");
			powerLevelSignal =  this.kecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
									Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the kettle maximum "
								+ "power level");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " +
									powerLevelSignal.getMeasure().getData());
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the power level through the user "
						+ "interface to a given level between 0 and the maximum"
						+ " power level");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level between 0 and"
							+ " the maximum power level");
			this.kop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData()/2.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.kop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
										Kettle.MAX_POWER_LEVEL.getData()/2.0 &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the current power level is the given"
								+ " level");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " +
									powerLevelSignal.getMeasure().getData());
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the power level through the user "
						+ "interface to a given level over the maximum"
						+ " power level");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level over the maximum"
							+ " power level");
			this.kop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData() + 1.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.kop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
											Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the current power level is the maximum"
								+ " power level");
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

		this.logMessage("  Scenario: setting the power level through the "
						+ "external control interface to a given level between "
						+ "0 and the maximum power level");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level "
							+ "between 0 and the maximum power level");
			this.kop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData()/2.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.kecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
										Kettle.MAX_POWER_LEVEL.getData()/2.0 &&
					powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the current power level is the given"
								+ " level");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " +
									powerLevelSignal.getMeasure().getData());
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the power level through the "
						+ "external control interface to a given level over the"
						+ " maximum power level");
		this.logMessage("    Given the kettle is initialised");
		this.logMessage("    And the kettle is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level over"
							+ " the maximum power level");
			this.kop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData() + 1.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.kecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
											Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the current power level is the maximum"
								+ " power level");
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
	
	protected void testHeating() {
	    this.logMessage("Feature: starting/stopping heating");

	    this.logMessage("  Scenario: starting heating when on");
	    this.logMessage("    Given the kettle is on and not heating");
	    try {
	        this.kop.switchOn();
	        if (this.kicop.heating()) this.kicop.stopHeating();
	        this.logMessage("    When I start heating water");
	        this.kicop.startHeating();
	        if (this.kicop.heating()) {
	            this.logMessage("    Then the kettle is heating");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: not heating");
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();

	    this.logMessage("  Scenario: stopping heating when heating");
	    this.logMessage("    Given the kettle is heating");
	    try {
	        if (!this.kicop.heating()) this.kicop.startHeating();
	        this.logMessage("    When I stop heating");
	        this.kicop.stopHeating();
	        if (!this.kicop.heating()) {
	            this.logMessage("    Then the kettle is no longer heating");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: still heating");
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();
	}

	protected void testKeepWarm() {
		this.logMessage("Feature: starting/stopping keeping warm");

		this.logMessage("  Scenario: start keeping warm");
		this.logMessage("    Given the kettle is on");
		try {
			if (!this.kop.on()) this.kop.switchOn();
			if (this.kicop.heating()) this.kicop.stopHeating();
			
			this.logMessage("    When I start keeping warm");
			this.kicop.startKeepingWarm();
			if (this.kicop.keepingWarm()) {
				this.logMessage("    Then the kettle is keeping warm");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: not keeping warm");
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.statistics.updateStatistics();

		this.logMessage("  Scenario: stop keeping warm");
		this.logMessage("    Given the kettle is keeping warm");
		try {
			this.logMessage("    When I stop keeping warm");
			this.kicop.stopKeepingWarm();
			if (!this.kicop.keepingWarm()) {
				this.logMessage("    Then the kettle is no longer keeping warm (ON)");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: still keeping warm");
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
		this.testTargetTemperature();
		this.testCurrentTemperature();
		this.testPowerLevel();
		this.testHeating();
		this.testKeepWarm();

		this.statistics.statisticsReport(this);
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.kop.getPortURI(),
					this.kettleUserInboundPortURI,
					KettleUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.kicop.getPortURI(),
					kettleInternalControlInboundPortURI,
					KettleInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.kecop.getPortURI(),
					kettleExternalControlInboundPortURI,
					KettleExternalControlConnector.class.getCanonicalName());
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
			this.traceMessage("Kettle tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
						clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant kettleSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant kettleSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Kettle tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Kettle tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(kettleSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(kettleSwitchOff);

			AbstractComponent o = this;

			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Kettle switches on.\n");
								kop.switchOn();
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
								o.traceMessage("Kettle switches off.\n");
								kop.switchOff();
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
		this.doPortDisconnection(this.kop.getPortURI());
		this.doPortDisconnection(this.kicop.getPortURI());
		this.doPortDisconnection(this.kecop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.kop.unpublishPort();
			this.kicop.unpublishPort();
			this.kecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------