package fr.sorbonne_u.components.hem2025e3.equipments.fan;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanTesterCyPhy</code> implements a component performing
 * tests for the class <code>FanCyPhy</code> as a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()}
 * invariant	{@code fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()}
 * invariant	{@code fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code X_RELATIVE_POSITION >= 0}
 * invariant	{@code Y_RELATIVE_POSITION >= 0}
 * </pre>
 * 
 * <p>Created on : 2026-06-06</p>
 * 
 * @author	Team
 */
@RequiredInterfaces(required={FanUserCI.class,
							  FanInternalControlCI.class,
							  FanExternalControlCI.class})
public class			FanTesterCyPhy
extends		AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, methods trace their actions.								*/
	public static boolean		VERBOSE = false;
	/** when tracing, x coordinate of the window relative position.			*/
	public static int			X_RELATIVE_POSITION = 0;
	/** when tracing, y coordinate of the window relative position.			*/
	public static int			Y_RELATIVE_POSITION = 0;

	/** standard reflection inbound port URI for the
	 *  {@code FanTesterCyPhy} component.									*/
	public static final String	REFLECTION_INBOUND_PORT_URI =
											"fan-tester-RIP-URI";

	/** URI of the user component interface inbound port.					*/
	protected String			fanUserInboundPortURI;
	/** URI of the internal control component interface inbound port.		*/
	protected String			fanInternalControlInboundPortURI;
	/** URI of the external control component interface inbound port.		*/
	protected String			fanExternalControlInboundPortURI;

	/** user component interface outbound port.								*/
	protected FanUserOutboundPort					fuop;
	/** internal control component interface outbound port.					*/
	protected FanInternalControlOutboundPort			ficop;
	/** external control component interface outbound port.					*/
	protected FanExternalControlOutboundPort			fecop;

	// Execution/Simulation

	/** one thread for the method execute.									*/
	protected static int		NUMBER_OF_STANDARD_THREADS = 1;
	/** one thread to schedule this component test actions.					*/
	protected static int		NUMBER_OF_SCHEDULABLE_THREADS = 1;

	/** collector of test statistics.										*/
	protected TestsStatistics	statistics;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ft != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ft	instance to be tested.
	 * @return		true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(FanTesterCyPhy ft)
	{
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				ft.fanUserInboundPortURI != null &&
									!ft.fanUserInboundPortURI.isEmpty(),
				FanTesterCyPhy.class, ft,
				"ft.fanUserInboundPortURI != null && "
							+ "!ft.fanUserInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ft.fanInternalControlInboundPortURI != null &&
							!ft.fanInternalControlInboundPortURI.isEmpty(),
				FanTesterCyPhy.class, ft,
				"ft.fanInternalControlInboundPortURI != null && "
						+ "!ft.fanInternalControlInboundPortURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
				ft.fanExternalControlInboundPortURI != null &&
							!ft.fanExternalControlInboundPortURI.isEmpty(),
				FanTesterCyPhy.class, ft,
				"ft.fanExternalControlInboundPortURI != null && "
						+ "!ft.fanExternalControlInboundPortURI.isEmpty()");
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				X_RELATIVE_POSITION >= 0,
				FanTesterCyPhy.class,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				Y_RELATIVE_POSITION >= 0,
				FanTesterCyPhy.class,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ft != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ft	instance to be tested.
	 * @return		true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(FanTesterCyPhy ft)
	{
		assert	ft != null : new PreconditionException("ft != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Standard execution for manual tests (no test scenario and no simulation)

	/**
	 * create a fan unit tester component for manual tests without test
	 * scenario or simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * post	{@code getExecutionMode().isStandard()}
	 * </pre>
	 *
	 * @param fanUserInboundPortURI				URI of the user component interface inbound port.
	 * @param fanInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param fanExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception						<i>to do</i>.
	 */
	protected			FanTesterCyPhy(
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS);

		this.initialise(fanUserInboundPortURI,
						fanInternalControlInboundPortURI,
						fanExternalControlInboundPortURI);
	}

	// Test execution with test scenario but no simulation

	/**
	 * create a fan unit tester component with test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()}
	 * pre	{@code fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()}
	 * pre	{@code executionMode != null && !executionMode.isStandard()}
	 * pre	{@code testScenario != null}
	 * post	{@code getExecutionMode().equals(executionMode)}
	 * </pre>
	 *
	 * @param fanUserInboundPortURI				URI of the user component interface inbound port.
	 * @param fanInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param fanExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @param executionMode						execution mode for the next run.
	 * @param testScenario						test scenario to be executed with this component.
	 * @throws Exception						<i>to do</i>.
	 */
	protected			FanTesterCyPhy(
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  AssertionChecking.assertTrueAndReturnOrThrow(
				executionMode != null && !executionMode.isStandard(),
				executionMode,
				() -> new PreconditionException(
								"executionMode != null && "
								+ "!executionMode.isStandard()")),
			  AssertionChecking.assertTrueAndReturnOrThrow(
				testScenario != null,
				testScenario.getClockURI(),
				() -> new PreconditionException("testScenario != null")),
			  testScenario);

		this.initialise(fanUserInboundPortURI,
						fanInternalControlInboundPortURI,
						fanExternalControlInboundPortURI);
	}

	/**
	 * initialise a fan test component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()}
	 * pre	{@code fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()}
	 * pre	{@code fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param fanUserInboundPortURI				URI of the user component interface inbound port.
	 * @param fanInternalControlInboundPortURI	URI of the internal control component interface inbound port.
	 * @param fanExternalControlInboundPortURI	URI of the external control component interface inbound port.
	 * @throws Exception						<i>to do</i>.
	 */
	protected void		initialise(
		String fanUserInboundPortURI,
		String fanInternalControlInboundPortURI,
		String fanExternalControlInboundPortURI
		) throws Exception
	{
		this.fanUserInboundPortURI = fanUserInboundPortURI;
		this.fuop = new FanUserOutboundPort(this);
		this.fuop.publishPort();
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

		assert	FanTesterCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"FanTesterCyPhy.implementationInvariants(this)");
		assert	FanTesterCyPhy.invariants(this) :
				new InvariantException("FanTesterCyPhy.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Test action helper methods
	// -------------------------------------------------------------------------

	/**
	 * return the fan user outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return.isConnected()}
	 * </pre>
	 *
	 * @return	the fan user outbound port.
	 */
	public FanUserOutboundPort		getFuop()
	{
		return this.fuop;
	}

	/**
	 * return the fan internal control outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return.isConnected()}
	 * </pre>
	 *
	 * @return	the fan internal control outbound port.
	 */
	public FanInternalControlOutboundPort	getFicop()
	{
		return this.ficop;
	}

	/**
	 * return the fan external control outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return.isConnected()}
	 * </pre>
	 *
	 * @return	the fan external control outbound port.
	 */
	public FanExternalControlOutboundPort	getFecop()
	{
		return this.fecop;
	}

	/**
	 * switch on the fan through the user interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOnFan() throws Exception
	{
		this.fuop.switchOn();
	}

	/**
	 * switch off the fan through the user interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			switchOffFan() throws Exception
	{
		this.fuop.switchOff();
	}

	/**
	 * set the fan to high speed through the user interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setHighSpeedFan() throws Exception
	{
		this.fuop.setHighSpeed();
	}

	/**
	 * set the fan to low speed through the user interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setLowSpeedFan() throws Exception
	{
		this.fuop.setLowSpeed();
	}

	// -------------------------------------------------------------------------
	// Tests implementations
	// -------------------------------------------------------------------------

	/**
	 * test getting the state of the fan.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting the state of the fan
	 *   Scenario: getting the state of the fan when off
	 *     Given the fan is initialised
	 *     And the fan has not been used yet
	 *     When I test the state of the fan
	 *     Then the state of the fan is off
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
	protected void		testOff()
	{
		this.logMessage("Feature: getting the state of the fan");
		this.logMessage("  Scenario: getting the state of the fan when off");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan has not been used yet");
		try {
			this.logMessage("    When I test the state of the fan");
			boolean result = !this.fuop.on();
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

	/**
	 * test switching on and off the fan.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: switching on and off the fan
	 *   Scenario: switching on the fan when off
	 *     Given the fan is initialised
	 *     And the fan has not been used yet
	 *     When I switch on the fan
	 *     Then the state of the fan is on
	 *   Scenario: switching off the fan when on
	 *     Given the fan is initialised
	 *     And the fan is on
	 *     When I switch off the fan
	 *     Then the state of the fan is off
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
	protected void		testSwitchOnSwitchOff()
	{
		this.logMessage("Feature: switching on and off the fan");

		this.logMessage("  Scenario: switching on the fan when off");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan has not been used yet");
		boolean result;
		try {
			this.logMessage("    When I switch on the fan");
			this.fuop.switchOn();
			result = this.fuop.on();
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
			this.fuop.switchOff();
			result = !this.fuop.on();
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

	/**
	 * test getting and setting the speed of the fan.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting and setting the speed of the fan
	 *   Scenario: getting the initial speed when just switched on
	 *     Given the fan is initialised
	 *     And the fan is on
	 *     When I get the speed of the fan
	 *     Then the speed of the fan is LOW
	 *   Scenario: setting the speed to HIGH
	 *     Given the fan is on
	 *     When I set the speed to HIGH
	 *     Then the speed of the fan is HIGH
	 *   Scenario: setting the speed back to LOW
	 *     Given the fan is on and at HIGH speed
	 *     When I set the speed to LOW
	 *     Then the speed of the fan is LOW
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
	protected void		testSpeed()
	{
		this.logMessage("Feature: getting and setting the speed of the fan");

		this.logMessage("  Scenario: getting the initial speed when just"
						+ " switched on");
		this.logMessage("    Given the fan is initialised");
		this.logMessage("    And the fan is on");
		boolean result;
		FanSpeed speed = null;
		try {
			this.fuop.switchOn();
			result = this.fuop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I get the speed of the fan");
			speed = this.fuop.getSpeed();
			if (speed == FanSpeed.LOW) {
				this.logMessage("    Then the speed of the fan is LOW");
			} else {
				this.logMessage("     but was: " + speed);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the speed to HIGH");
		this.logMessage("    Given the fan is on");
		try {
			this.logMessage("    When I set the speed to HIGH");
			this.fuop.setHighSpeed();
			speed = this.fuop.getSpeed();
			if (speed == FanSpeed.HIGH) {
				this.logMessage("    Then the speed of the fan is HIGH");
			} else {
				this.logMessage("     but was: " + speed);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: setting the speed back to LOW");
		this.logMessage("    Given the fan is on and at HIGH speed");
		try {
			this.logMessage("    When I set the speed to LOW");
			this.fuop.setLowSpeed();
			speed = this.fuop.getSpeed();
			if (speed == FanSpeed.LOW) {
				this.logMessage("    Then the speed of the fan is LOW");
			} else {
				this.logMessage("     but was: " + speed);
				this.statistics.incorrectResult();
			}
			this.fuop.switchOff();
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}

	/**
	 * test getting and setting the power level of the fan.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>Gherkin specification</p>
	 * <p></p>
	 * <pre>
	 * Feature: getting and setting the power level of the fan
	 *   Scenario: getting the maximum power level through the user interface
	 *     Given the fan is initialised
	 *     When I get the maximum power level through the user interface
	 *     Then the result is the fan maximum power level
	 *   Scenario: getting the maximum power level through the external control interface
	 *     Given the fan is initialised
	 *     When I get the maximum power level through the external control interface
	 *     Then the result is the fan maximum power level
	 *   Scenario: setting the power level to a given level between 0 and max
	 *     Given the fan is on
	 *     When I set the current power level to a given level between 0 and max
	 *     Then the current power level is the given power level
	 *   Scenario: setting the power level to a given level over the max
	 *     Given the fan is on
	 *     When I set the current power level to a given level over the max
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
						+ " fan");

		this.logMessage("  Scenario: getting the maximum power level through "
						+ "the user interface");
		this.logMessage("    Given the fan is initialised");
		Measure<Double> powerLevel = null;
		try {
			this.logMessage("    When I get the maximum power level through the"
							+ " user interface");
			powerLevel = this.fuop.getMaxPowerLevel();
			if (powerLevel.getData() == FanCyPhy.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							FanCyPhy.MAX_POWER_LEVEL.getMeasurementUnit())) {
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
			if (powerLevel.getData() == FanCyPhy.MAX_POWER_LEVEL.getData() &&
				powerLevel.getMeasurementUnit().equals(
							FanCyPhy.MAX_POWER_LEVEL.getMeasurementUnit())) {
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

		this.logMessage("  Scenario: setting the power level to a given level"
						+ " between 0 and the maximum power level");
		this.logMessage("    Given the fan is on");
		boolean result;
		SignalData<Double> powerLevelSignal = null;
		try {
			this.fuop.switchOn();
			result = this.fuop.on();
			if (!result) {
				this.logMessage("     but was: off");
				this.statistics.failedCondition();
			}
			this.logMessage("    When I set the current power level to a"
							+ " given level between 0 and max");
			this.fuop.setCurrentPowerLevel(
					new Measure<Double>(FanCyPhy.MAX_POWER_LEVEL.getData()/2.0,
										FanCyPhy.POWER_UNIT));
			powerLevelSignal = this.fuop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
										FanCyPhy.MAX_POWER_LEVEL.getData()/2.0 &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								FanCyPhy.MAX_POWER_LEVEL.getMeasurementUnit())) {
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

		this.logMessage("  Scenario: setting the power level to a given level"
						+ " over the maximum power level");
		this.logMessage("    Given the fan is on");
		try {
			this.logMessage("    When I set the current power level to a"
							+ " given level over the maximum power level");
			this.fuop.setCurrentPowerLevel(
					new Measure<Double>(FanCyPhy.MAX_POWER_LEVEL.getData() + 1.0,
										FanCyPhy.POWER_UNIT));
			powerLevelSignal = this.fuop.getCurrentPowerLevel();
			if (powerLevelSignal.getMeasure().getData() ==
											FanCyPhy.MAX_POWER_LEVEL.getData() &&
				powerLevelSignal.getMeasure().getMeasurementUnit().equals(
								FanCyPhy.MAX_POWER_LEVEL.getMeasurementUnit())) {
				this.logMessage("    Then the current power level is the maximum"
								+ " power level");
			} else {
				this.logMessage("     but was: " +
									powerLevelSignal.getMeasure().getData());
				this.statistics.incorrectResult();
			}
			this.fuop.switchOff();
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
		this.testSpeed();
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
					this.fuop.getPortURI(),
					this.fanUserInboundPortURI,
					FanUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.ficop.getPortURI(),
					this.fanInternalControlInboundPortURI,
					FanInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.fecop.getPortURI(),
					this.fanExternalControlInboundPortURI,
					FanExternalControlConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void	execute() throws Exception
	{
		this.traceMessage("Fan Unit Tester begins execution.\n");

		switch (this.getExecutionMode()) {
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case INTEGRATION_TEST_WITH_HIL_SIMULATION:
		case UNIT_TEST_WITH_HIL_SIMULATION:
			throw new BCMException("HIL simulation not implemented yet!");
		case STANDARD:
			this.statistics = new TestsStatistics();
			this.traceMessage("Fan Unit Tester starts the tests.\n");
			this.runAllUnitTests();
			this.traceMessage("Fan Unit Tester ends.\n");
			break;
		default:
		}

		this.traceMessage("Fan Unit Tester ends execution.\n");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.fuop.getPortURI());
		this.doPortDisconnection(this.ficop.getPortURI());
		this.doPortDisconnection(this.fecop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.fuop.unpublishPort();
			this.ficop.unpublishPort();
			this.fecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
