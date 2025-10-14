package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserOutboundPort;
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

@RequiredInterfaces(required={WashingMachineUserCI.class,
							  WashingMachineInternalControlCI.class,
							  WashingMachineExternalControlCI.class,
							  ClocksServerCI.class})
public class			WashingMachineUnitTester
extends		AbstractComponent
{
	public static final int		SWITCH_ON_DELAY = 2;
	
	public static final int		SWITCH_OFF_DELAY = 9;

	public static boolean		VERBOSE = false;
	
	public static int			X_RELATIVE_POSITION = 0;
	
	public static int			Y_RELATIVE_POSITION = 0;

	protected final boolean		isUnitTest;
	
	protected String			washingMachineUserInboundPortURI;
	
	protected String			washingMachineInternalControlInboundPortURI;
	
	protected String			washingMachineExternalControlInboundPortURI;

	
	protected WashingMachineUserOutboundPort			wmop;
	
	protected WashingMachineInternalControlOutboundPort	wmicop;
	
	protected WashingMachineExternalControlOutboundPort	wmecop;

	
	protected TestsStatistics	statistics;

	
	protected static boolean	implementationInvariants(WashingMachineUnitTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.washingMachineUserInboundPortURI != null &&
									!ht.washingMachineUserInboundPortURI.isEmpty(),
				WashingMachineUnitTester.class, ht,
				"ht.washingMachineUserInboundPortURI != null && "
							+ "!ht.washingMachineUserInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.washingMachineInternalControlInboundPortURI != null &&
							!ht.washingMachineInternalControlInboundPortURI.isEmpty(),
				WashingMachineUnitTester.class, ht,
				"ht.washingMachineInternalControlInboundPortURI != null && "
						+ "!ht.washingMachineInternalControlInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ht.washingMachineExternalControlInboundPortURI != null &&
							!ht.washingMachineExternalControlInboundPortURI.isEmpty(),
				WashingMachineUnitTester.class, ht,
				"ht.washingMachineExternalControlInboundPortURI != null &&"
						+ "!ht.washingMachineExternalControlInboundPortURI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(WashingMachineUnitTester ht)
	{
		assert	ht != null : new PreconditionException("ht != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				WashingMachineUnitTester.class, ht,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				WashingMachineUnitTester.class, ht,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}
	
	protected			WashingMachineUnitTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest,
			 WashingMachine.USER_INBOUND_PORT_URI,
			 WashingMachine.INTERNAL_CONTROL_INBOUND_PORT_URI,
			 WashingMachine.EXTERNAL_CONTROL_INBOUND_PORT_URI);
	}

	protected			WashingMachineUnitTester(
		boolean isUnitTest,
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		super(1, 1);
		this.isUnitTest = isUnitTest;
		this.initialise(washingMachineUserInboundPortURI,
						washingMachineInternalControlInboundPortURI,
						washingMachineExternalControlInboundPortURI);
	}
	
	protected			WashingMachineUnitTester(
		boolean isUnitTest,
		String reflectionInboundPortURI,
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 1);
		this.isUnitTest = isUnitTest;
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
		this.washingMachineUserInboundPortURI = washingMachineUserInboundPortURI;
		this.wmop = new WashingMachineUserOutboundPort(this);
		this.wmop.publishPort();
		this.washingMachineInternalControlInboundPortURI =
									washingMachineInternalControlInboundPortURI;
		this.wmicop = new WashingMachineInternalControlOutboundPort(this);
		this.wmicop.publishPort();
		this.washingMachineExternalControlInboundPortURI =
									washingMachineExternalControlInboundPortURI;
		this.wmecop = new WashingMachineExternalControlOutboundPort(this);
		this.wmecop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Washing Machine tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();

		assert	WashingMachineUnitTester.implementationInvariants(this) :
				new ImplementationInvariantException(
						"WashingMachineTester.implementationInvariants(this)");
		assert	WashingMachineUnitTester.invariants(this) :
				new InvariantException("WashingMachineTester.invariants(this)");
	}
	
	protected void		testOff()
	{
		this.logMessage("Feature: getting the state of the washingMachine");
		this.logMessage("  Scenario: getting the state of the washingMachine when off");
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		try {
			this.logMessage("    When I test the state of the washingMachine");
			boolean result = !this.wmop.on();
			if (result) {
				this.logMessage("    Then the state of the washingMachine is off");
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
		this.logMessage("Feature: switching on and off the washingMachine");

		this.logMessage("  Scenario: switching on the washingMachine when off");
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		boolean result;
		try {
			this.logMessage("    When I switch on the washingMachine");
			this.wmop.switchOn();
			result = this.wmop.on();
			if (result) {
				this.logMessage("    Then the state of the washingMachine is on");
			} else {
				this.logMessage("     but was: off");
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: switching off the washingMachine when on");
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I switch off the washingMachine");
			this.wmop.switchOff();
			result = !this.wmop.on();
			if (result) {
				this.logMessage("    Then the state of the washingMachine is off");
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
						+ " of the washingMachine");

		this.logMessage("  Scenario: getting the target temperature through the"
						+ " user interface when just initialised");
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		this.logMessage("    And the washingMachine is on");
		boolean result;
		Measure<Double> temperature = null;
		try {
			this.wmop.switchOn();
			result = this.wmop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the target temperature through the "
							+ "user interface");
			temperature = this.wmop.getTargetTemperature();
			if (temperature.getData() ==
									WashingMachine.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the washingMachine"
								+ " is the washingMachine standard target temperature");
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I get the target temperature through the internal control interface");
			temperature = this.wmicop.getTargetTemperature();
			if (temperature.getData() ==
									WashingMachine.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the washingMachine"
								+ " is the washingMachine standard target temperature");
			} else {
				this.logMessage("     but was: " + temperature.getData());
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the target temperature of the water in the "
						+ "washingMachine when on");
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			result = this.wmop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I set the temperature at any given "
							+ "temperature between 15 and 90 Celsius inclusive");
			this.wmop.setTargetTemperature(
					new Measure<Double>(15.0, WashingMachine.TEMPERATURE_UNIT));
			temperature = this.wmop.getTargetTemperature();
			if (temperature.getData() == 15.0 &&
				temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the water in the washingMachine"
								+ " is the given temperature");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was not: " + temperature.getData());
			}
			this.wmop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}
	
	protected void testCurrentTemperature() {
	    this.logMessage("Feature: getting the current temperature of the water in the washingMachine");

	    // User
	    this.logMessage("  Scenario: getting the current temperature through the user interface when on");
	    this.logMessage("    Given the washingMachine is initialised");
	    this.logMessage("    And the washingMachine has not been used yet");
	    this.logMessage("    And the washingMachine is on");
	    try {
	        this.wmop.switchOn();
	        if (!this.wmop.on()) { this.logMessage("     but was: off"); this.statistics.failedCondition(); }
	        this.logMessage("    When I get the current temperature through the user interface");
	        SignalData<Double> temperature = this.wmop.getCurrentTemperature();
	        if (temperature.getMeasure().getData().equals(WashingMachine.FAKE_CURRENT_TEMPERATURE.getMeasure().getData()) &&
	            temperature.getMeasure().getMeasurementUnit().equals(WashingMachine.FAKE_CURRENT_TEMPERATURE.getMeasure().getMeasurementUnit())) {
	            this.logMessage("    Then the water temperature is at the standard current temperature");
	        } else {
	            this.logMessage("     but was: " + temperature.getMeasure().getData());
	            this.statistics.incorrectResult();
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();

	    // Internal
	    this.logMessage("  Scenario: getting the current temperature through the internal control interface when on");
	    this.logMessage("    Given the washingMachine is initialised");
	    this.logMessage("    And the washingMachine has not been used yet");
	    this.logMessage("    And the washingMachine is on");
	    try {
	        this.logMessage("    When I get the current temperature through the internal control interface");
	        SignalData<Double> temperature = this.wmicop.getCurrentTemperature();
	        if (temperature.getMeasure().getData().equals(WashingMachine.FAKE_CURRENT_TEMPERATURE.getMeasure().getData()) &&
	            temperature.getMeasure().getMeasurementUnit().equals(WashingMachine.FAKE_CURRENT_TEMPERATURE.getMeasure().getMeasurementUnit())) {
	            this.logMessage("    Then the current temperature is the washingMachine standard current temperature");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: " + temperature.getMeasure().getData());
	        }
	        this.wmop.switchOff();
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();
	}
	
	protected void		testPowerLevel()
	{
		this.logMessage("Feature: getting and setting the power level of the"
						+ " washingMachine");

		this.logMessage("  Scenario: getting the maximum power level through "
						+ "the user interface");
		this.logMessage("    Given the washingMachine is initialised");
		Measure<Double> powerLevel = null;
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " user interface");
			powerLevel = this.wmop.getMaxPowerLevel();
			if (powerLevel.getData() == WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the washingMachine maximum "
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
		this.logMessage("    Given the washingMachine is initialised");
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " external control interface");
			powerLevel = this.wmecop.getMaxPowerLevel();
			if (powerLevel.getData() == WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the washingMachine maximum "
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		this.logMessage("    And the washingMachine is on");
		boolean result;
		SignalData<Double> powerLevelSignal = null;
		try {
			this.wmop.switchOn();
			result = this.wmop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current power level through the"
							+ " user interface");
			powerLevelSignal =  this.wmop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
											WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the washingMachine maximum "
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine has not been used yet");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I get the current power level through the"
							+ " external control interface");
			powerLevelSignal =  this.wmecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
									WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the washingMachine maximum "
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level between 0 and"
							+ " the maximum power level");
			this.wmop.setCurrentPowerLevel(
					new Measure<Double>(WashingMachine.MAX_POWER_LEVEL.getData()/2.0,
										WashingMachine.POWER_UNIT));
			powerLevelSignal = this.wmop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
										WashingMachine.MAX_POWER_LEVEL.getData()/2.0 &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level over the maximum"
							+ " power level");
			this.wmop.setCurrentPowerLevel(
					new Measure<Double>(WashingMachine.MAX_POWER_LEVEL.getData() + 1.0,
										WashingMachine.POWER_UNIT));
			powerLevelSignal = this.wmop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
											WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level "
							+ "between 0 and the maximum power level");
			this.wmop.setCurrentPowerLevel(
					new Measure<Double>(WashingMachine.MAX_POWER_LEVEL.getData()/2.0,
										WashingMachine.POWER_UNIT));
			powerLevelSignal = this.wmecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
										WashingMachine.MAX_POWER_LEVEL.getData()/2.0 &&
					powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
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
		this.logMessage("    Given the washingMachine is initialised");
		this.logMessage("    And the washingMachine is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level over"
							+ " the maximum power level");
			this.wmop.setCurrentPowerLevel(
					new Measure<Double>(WashingMachine.MAX_POWER_LEVEL.getData() + 1.0,
										WashingMachine.POWER_UNIT));
			powerLevelSignal = this.wmecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
											WashingMachine.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								WashingMachine.MAX_POWER_LEVEL.getMeasurementUnit())) {
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
	
	protected void testHeatingWater() {
	    this.logMessage("Feature: starting/stopping water heating");

	    this.logMessage("  Scenario: starting heating water when on");
	    this.logMessage("    Given the washingMachine is on and not heating");
	    try {
	        this.wmop.switchOn();
	        if (this.wmicop.heatWater()) this.wmicop.stopHeatingWater();
	        this.logMessage("    When I start heating water");
	        this.wmicop.startHeatingWater();
	        if (this.wmicop.heatWater()) {
	            this.logMessage("    Then the washingMachine is heating water");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: not heating");
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();

	    this.logMessage("  Scenario: stopping heating water when heating");
	    this.logMessage("    Given the washingMachine is heating water");
	    try {
	        if (!this.wmicop.heatWater()) this.wmicop.startHeatingWater();
	        this.logMessage("    When I stop heating water");
	        this.wmicop.stopHeatingWater();
	        if (!this.wmicop.heatWater()) {
	            this.logMessage("    Then the washingMachine is no longer heating water");
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
	
	protected void testStartWashing() {
	    this.logMessage("Feature: starting a washing cycle immediately");

	    this.logMessage("  Scenario: startWashing transitions and completion");
	    this.logMessage("    Given the washingMachine is on and not heating");
	    try {
	        this.wmop.switchOn();
	        if (this.wmicop.heatWater()) this.wmicop.stopHeatingWater();

	        long cycle = 60L; 
	        this.logMessage("    When I call startWashing(" + cycle + " ms)");
	        this.wmop.startWashing(cycle);

	        Thread.sleep(10L);
	        boolean washingNow = this.wmop.isWashing();
	        if (washingNow && this.wmop.on()) {
	            this.logMessage("    Then the washingMachine reports isWashing=true and remains on");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: isWashing=" + washingNow + ", on=" + this.wmop.on());
	        }

	        Thread.sleep(cycle + 30L);
	        if (this.wmop.on() && !this.wmop.isWashing()) {
	            this.logMessage("    And after completion, isWashing=false and machine is ON");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: on=" + this.wmop.on() + ", isWashing=" + this.wmop.isWashing());
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();

	    this.logMessage("  Scenario: startWashing while heating (should fail precondition)");
	    this.logMessage("    Given the washingMachine is on and heating");
	    try {
	        if (!this.wmop.on()) this.wmop.switchOn();
	        this.wmicop.startHeatingWater();
	        boolean failed = false;
	        try {
	            this.logMessage("    When I call startWashing(10 ms) while heating");
	            this.wmop.startWashing(10L);
	        } catch (Throwable pe) {
	            failed = true;
	            this.logMessage("    Then a precondition failure/exception is raised: " + pe);
	        } finally {
	            if (!failed) {
	                this.statistics.incorrectResult();
	                this.logMessage("     but no exception was raised while heating");
	            }
	            if (this.wmicop.heatWater()) this.wmicop.stopHeatingWater();
	        }
	    } catch (Throwable e) {
	        this.statistics.incorrectResult();
	        this.logMessage("     but the exception " + e + " has been raised");
	    }
	    this.statistics.updateStatistics();
	}
	
	protected void testDelayedStart() {
	    this.logMessage("Feature: delayedStart schedules a future washing cycle");

	    this.logMessage("  Scenario: delayedStart waits then runs a short cycle");
	    this.logMessage("    Given the washingMachine is initialised and on");
	    try {
	        this.wmop.switchOn();
	        if (!this.wmop.on()) { this.statistics.failedCondition(); this.logMessage("     but was: off"); }

	        long delay = 80L;     
	        long washing = 50L;   
	        Measure<Double> target = new Measure<>(
	            WashingMachine.STANDARD_TARGET_TEMPERATURE.getData(),
	            WashingMachine.TEMPERATURE_UNIT);

	        this.logMessage("    When I call delayedStart(delay=80 ms, target=STANDARD, washing=50 ms)");
	        this.wmop.delayedStart(delay, target, washing);

	        Thread.sleep(30L);
	        if (!this.wmop.isWashing() && this.wmop.on()) {
	            this.logMessage("    Then before the delay expires, isWashing=false and machine is ON");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: isWashing=" + this.wmop.isWashing() + ", on=" + this.wmop.on());
	        }

	        Thread.sleep(delay + washing + 40L);
	        if (this.wmop.on() && !this.wmop.isWashing()) {
	            this.logMessage("    And after completion, isWashing=false and machine is ON");
	        } else {
	            this.statistics.incorrectResult();
	            this.logMessage("     but was: on=" + this.wmop.on() + ", isWashing=" + this.wmop.isWashing());
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
		this.testHeatingWater();
		this.testStartWashing();
		this.testDelayedStart();

		this.statistics.statisticsReport(this);
	}
	
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.wmop.getPortURI(),
					this.washingMachineUserInboundPortURI,
					WashingMachineUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.wmicop.getPortURI(),
					washingMachineInternalControlInboundPortURI,
					WashingMachineInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.wmecop.getPortURI(),
					washingMachineExternalControlInboundPortURI,
					WashingMachineExternalControlConnector.class.getCanonicalName());
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
			this.traceMessage("Washing Machine tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
						clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant washingMachineSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant washingMachineSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Washing Machine tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Washing Machine tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(washingMachineSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(washingMachineSwitchOff);

			AbstractComponent o = this;

			// schedule the switch on washingMachine
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches on.\n");
								wmop.switchOn();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);

			// schedule the switch off washingMachine
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches off.\n");
								wmop.switchOff();
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
		this.doPortDisconnection(this.wmop.getPortURI());
		this.doPortDisconnection(this.wmicop.getPortURI());
		this.doPortDisconnection(this.wmecop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wmop.unpublishPort();
			this.wmicop.unpublishPort();
			this.wmecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
