package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.heater.connections.HeaterUserOutboundPort;
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

@RequiredInterfaces(required={KettleUserCI.class,
							  HeaterInternalControlCI.class,
							  HeaterExternalControlCI.class,
							  ClocksServerCI.class})
public class			KettleUnitTester
extends		AbstractComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the kettle is switched on.											*/
	public static final int		SWITCH_ON_DELAY = 2;
	/**	in clock-driven scenario, the delay from the start instant at which
	 *  the kettle is switched off.											*/
	public static final int		SWITCH_OFF_DELAY = 9;

	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** true if the component must perform unit tests, otherwise it
	 *  executes integration tests actions.									*/
	protected final boolean		isUnitTest;
	/** URI of the user component interface inbound port.					*/
	protected String			kettleUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			kettleInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			kettleExternalControlInboundPortURI;

	/** user component interface inbound port.								*/
	protected KettleUserOutboundPort			kop;
	/** internal control component interface inbound port.					*/
	protected KettleInternalControlOutboundPort	kicop;
	/** external control component interface inbound port.					*/
	protected KettleExternalControlOutboundPort	kecop;

	/** collector of test statistics.										*/
	protected TestsStatistics	statistics;

	protected static boolean	implementationInvariants(KettleUnitTester kt)
	{
		assert	kt != null : new PreconditionException("ht != null");

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
		this.kicop = new HeaterInternalControlOutboundPort(this);
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
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

		this.logMessage("  Scenario: switching off the heater when on");
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

	/**
	 * test getting and setting the target temperature of the heater.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting and setting the target temperature of the heater");
	 *   Scenario: getting the target temperature through the user interface when just initialised
	 *     Given the heater is initialised
	 *     And the heater has not been used yet
	 *     And the heater is on
	 *     When I get the target temperature through the user interface
	 *     Then the target temperature of the heater is the heater standard target temperature
	 *   Scenario: getting the target temperature through the internal control interface when just initialised
	 *     Given the heater is initialised
	 *     And the heater has not been used yet
	 *     And the heater is on
	 *     When I get the target temperature through the internal control interface
	 *     Then the target temperature of the heater is the heater standard target temperature
	 *   Scenario: setting the target temperature of the heater when on
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the temperature at any given temperature between -50 and 50 Celsius inclusive
	 *     Then the target temperature of the heater is the given temperature
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		testTargetTemperature()
	{
		this.logMessage("Feature: getting and setting the target temperature"
						+ " of the heater");

		this.logMessage("  Scenario: getting the target temperature through the"
						+ " user interface when just initialised");
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		boolean result;
		Measure<Double> temperature = null;
		try {
			this.hop.switchOn();
			result = this.hop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the target temperature through the "
							+ "user interface");
			temperature = this.hop.getTargetTemperature();
			if (temperature.getData() ==
									Kettle.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the heater"
								+ " is the heater standard target temperature");
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I get the target temperature through the internal control interface");
			temperature = this.hicop.getTargetTemperature();
			if (temperature.getData() ==
									Kettle.STANDARD_TARGET_TEMPERATURE.getData()
				&& temperature.getMeasurementUnit().equals(
													MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the heater"
								+ " is the heater standard target temperature");
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
						+ "heater when on");
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater is on");
		try {
			result = this.hop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I set the temperature at any given "
							+ "temperature between -50 and 50 Celsius inclusive");
			this.hop.setTargetTemperature(
					new Measure<Double>(21.0, Kettle.TEMPERATURE_UNIT));
			temperature = this.hop.getTargetTemperature();
			if (temperature.getData() == 21.0 &&
				temperature.getMeasurementUnit().equals(MeasurementUnit.CELSIUS)) {
				this.logMessage("    Then the target temperature of the heater"
								+ " is the given temperature");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was not: " + temperature.getData());
			}
			this.hop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	/**
	 * test getting the current temperature in the room of the heater.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting the current temperature in the room of the heater");
	 *   Scenario: getting the current temperature when on");
	 *     Given the heater is initialised");
	 *     And the heater has not been used yet");
	 *     And the heater is on");
	 *     When I get the current temperature of the heater");
	 *     Then the current temperature is the heater standard current temperature");
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		testCurrentTemperature()
	{
		this.logMessage("Feature: getting the current temperature"
						+ " in the room of the heater");

		this.logMessage("  Scenario: getting the current temperature through "
						+ "the user interface when on");
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		boolean result;
		SignalData<Double> temperature = null;
		try {
			this.hop.switchOn();
			result = this.hop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current temperature of the "
							+ "heater through the user interface");
			temperature = this.hop.getCurrentTemperature();
			if (temperature.getMeasure().getData() == 
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
				temperature.getMeasure().getMeasurementUnit().equals(
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().
														getMeasurementUnit())) {
				this.logMessage("    Then the current temperature is the heater"
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I get the current temperature of the "
							+ "heater through the user interface");
			temperature = this.hicop.getCurrentTemperature();
			if (temperature.getMeasure().getData() == 
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
				temperature.getMeasure().getMeasurementUnit().equals(
					Kettle.FAKE_CURRENT_TEMPERATURE.getMeasure().
														getMeasurementUnit())) {
				this.logMessage("    Then the current temperature is the heater"
								+ " standard current temperature");
			} else {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " +
										temperature.getMeasure().getData());
			}
			this.hop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	/**
	 * test getting and setting the power level of the heater.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting and setting the power level of the heater
	 *   Scenario: getting the maximum power level through the user interface
	 *     Given the heater is initialised
	 *     When I get the maximum power level through the user interface
	 *     Then the result is the heater maximum power level
	 *   Scenario: getting the maximum power level through the external control interface
	 *     Given the heater is initialised
	 *     When I get the maximum power level through the external control interface
	 *     Then the result is the heater maximum power level
	 *   Scenario: getting the current power level through the user interface when just initialised
	 *     Given the heater is initialised
	 *     And the heater has not been used yet
	 *     And the heater is on
	 *     When I get the current power level through the user interface
	 *     Then the result is the heater maximum power level
	 *   Scenario: getting the current power level through the external control interface when just initialised
	 *     Given the heater is initialised
	 *     And the heater has not been used yet
	 *     And the heater is on
	 *     When I get the current power level through the external control interface
	 *     Then the result is the heater maximum power level
	 *   Scenario: setting the power level to a given level between 0 and the maximum power level through the user interface
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the current power level through the user interface to a given level between 0 and the maximum power level
	 *     Then the current power level is the given power level
	 *   Scenario: setting the power level to a given level over the maximum power level through the user interface
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the current power level through the user interface to a given level bover the maximum power level
	 *     Then the current power level is the maximum power level
	 *   Scenario: setting the power level to a given level between 0 and the maximum power level through the external control interface
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the current power level through the external control interface to a given level between 0 and the maximum power level
	 *     Then the current power level is the given power level
	 *   Scenario: setting the power level to a given level over the maximum power level through the external control interface
	 *     Given the heater is initialised
	 *     And the heater is on
	 *     When I set the current power level through the external control interface to a given level over the maximum power level
	 *     Then the current power level is the maximum power level
	 * </pre>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		testPowerLevel()
	{
		this.logMessage("Feature: getting and setting the power level of the"
						+ " heater");

		this.logMessage("  Scenario: getting the maximum power level through "
						+ "the user interface");
		this.logMessage("    Given the heater is initialised");
		Measure<Double> powerLevel = null;
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " user interface");
			powerLevel = this.hop.getMaxPowerLevel();
			if (powerLevel.getData() == Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the heater maximum "
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
		this.logMessage("    Given the heater is initialised");
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " external control interface");
			powerLevel = this.hecop.getMaxPowerLevel();
			if (powerLevel.getData() == Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the heater maximum "
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		boolean result;
		SignalData<Double> powerLevelSignal = null;
		try {
			this.hop.switchOn();
			result = this.hop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the current power level through the"
							+ " user interface");
			powerLevelSignal =  this.hop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
											Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the heater maximum "
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater has not been used yet");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I get the current power level through the"
							+ " external control interface");
			powerLevelSignal =  this.hecop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() == 
									Kettle.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								Kettle.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the result is the heater maximum "
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level between 0 and"
							+ " the maximum power level");
			this.hop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData()/2.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.hop.getCurrentPowerLevel();
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " user interface to a given level over the maximum"
							+ " power level");
			this.hop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData() + 1.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.hop.getCurrentPowerLevel();
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level "
							+ "between 0 and the maximum power level");
			this.hop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData()/2.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.hecop.getCurrentPowerLevel();
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
		this.logMessage("    Given the heater is initialised");
		this.logMessage("    And the heater is on");
		try {
			this.logMessage("    When I set the current power level through the"
							+ " external control interface to a given level over"
							+ " the maximum power level");
			this.hop.setCurrentPowerLevel(
					new Measure<Double>(Kettle.MAX_POWER_LEVEL.getData() + 1.0,
										Kettle.POWER_UNIT));
			powerLevelSignal = this.hecop.getCurrentPowerLevel();
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

	/**
	 * run all unit tests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		runAllUnitTests()
	{
		this.testOff();
		this.testSwitchOnSwitchOff();
		this.testTargetTemperature();
		this.testCurrentTemperature();
		this.testPowerLevel();

		this.statistics.statisticsReport(this);
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.hop.getPortURI(),
					this.heaterUserInboundPortURI,
					HeaterUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.hicop.getPortURI(),
					heaterInternalControlInboundPortURI,
					HeaterInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.hecop.getPortURI(),
					heaterExternalControlInboundPortURI,
					HeaterExternalControlConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
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
			this.traceMessage("Heater tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(
						clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			Instant startInstant = ac.getStartInstant();
			Instant heaterSwitchOn = startInstant.plusSeconds(SWITCH_ON_DELAY);
			Instant heaterSwitchOff = startInstant.plusSeconds(SWITCH_OFF_DELAY);
			this.traceMessage("Heater tester waits until start.\n");
			ac.waitUntilStart();
			this.traceMessage("Heater tester schedules switch on and off.\n");
			long delayToSwitchOn = ac.nanoDelayUntilInstant(heaterSwitchOn);
			long delayToSwitchOff = ac.nanoDelayUntilInstant(heaterSwitchOff);

			// This is to avoid mixing the 'this' of the task object with the 'this'
			// representing the component object in the code of the next methods run
			AbstractComponent o = this;

			// schedule the switch on heater
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches on.\n");
								hop.switchOn();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOn, TimeUnit.NANOSECONDS);

			// to be completed with a more covering scenario

			// schedule the switch off heater
			this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								o.traceMessage("Heater switches off.\n");
								hop.switchOff();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					}, delayToSwitchOff, TimeUnit.NANOSECONDS);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.hop.getPortURI());
		this.doPortDisconnection(this.hicop.getPortURI());
		this.doPortDisconnection(this.hecop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.hop.unpublishPort();
			this.hicop.unpublishPort();
			this.hecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
