package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner;

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
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerMode;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerUserCI;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleanerTesterCyPhy</code> implements the cyber-physical
 * component performing tests for the class <code>VacuumCleanerCyPhy</code> as
 * a BCM4Java-CyPhy component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
 * }
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
@RequiredInterfaces(required = { VacuumCleanerUserCI.class })
public class VacuumCleanerTesterCyPhy
        extends AbstractCyPhyComponent {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** when true, methods trace their actions. */
    public static boolean VERBOSE = false;
    /** when tracing, x coordinate of the window relative position. */
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position. */
    public static int Y_RELATIVE_POSITION = 0;

    /** standard reflection inbound port URI for the tester component. */
    public static final String REFLECTION_INBOUND_PORT_URI = "vacuum-cleaner-unit-tester-RIP-URI";

    /** outbound port connecting to the vacuum cleaner component. */
    protected VacuumCleanerOutboundPort vcop;
    /** URI of the vacuum cleaner inbound port to connect to. */
    protected String vacuumCleanerInboundPortURI;

    // Execution/Simulation

    /** one thread for the method execute. */
    protected static int NUMBER_OF_STANDARD_THREADS = 1;
    /** one thread to schedule this component test actions. */
    protected static int NUMBER_OF_SCHEDULABLE_THREADS = 1;

    /** collector of test statistics. */
    protected TestsStatistics statistics;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     * return true if the implementation invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vct != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param vct instance to be tested.
     * @return true if the implementation invariants are observed, false otherwise.
     */
    protected static boolean implementationInvariants(
            VacuumCleanerTesterCyPhy vct) {
        assert vct != null : new PreconditionException("vct != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                vct.vacuumCleanerInboundPortURI != null &&
                        !vct.vacuumCleanerInboundPortURI.isEmpty(),
                VacuumCleanerTesterCyPhy.class, vct,
                "vct.vacuumCleanerInboundPortURI != null && "
                        + "!vct.vacuumCleanerInboundPortURI.isEmpty()");
        return ret;
    }

    /**
     * return true if the static invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @return true if the static invariants are observed, false otherwise.
     */
    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= AssertionChecking.checkStaticInvariant(
                REFLECTION_INBOUND_PORT_URI != null &&
                        !REFLECTION_INBOUND_PORT_URI.isEmpty(),
                VacuumCleanerTesterCyPhy.class,
                "REFLECTION_INBOUND_PORT_URI != null && "
                        + "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                X_RELATIVE_POSITION >= 0,
                VacuumCleanerTesterCyPhy.class,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkStaticInvariant(
                Y_RELATIVE_POSITION >= 0,
                VacuumCleanerTesterCyPhy.class,
                "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    /**
     * return true if the invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vct != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param vct instance to be tested.
     * @return true if the invariants are observed, false otherwise.
     */
    protected static boolean invariants(VacuumCleanerTesterCyPhy vct) {
        assert vct != null : new PreconditionException("vct != null");

        boolean ret = true;
        ret &= staticInvariants();
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution for manual tests

    /**
     * create a vacuum cleaner tester component for manual tests without test
     * scenario or simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * post	{@code
     * getCurrentExecutionMode().isStandard()
     * }
     * </pre>
     *
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port to
     *                                    connect to.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerTesterCyPhy(
            String vacuumCleanerInboundPortURI) throws Exception {
        super(REFLECTION_INBOUND_PORT_URI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS);

        this.initialise(vacuumCleanerInboundPortURI);
    }

    // Test execution with test scenario

    /**
     * create a vacuum cleaner tester component for tests (unit or integration)
     * with a test scenario but no simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && !executionMode.isStandard()
     * }
     * pre	{@code
     * testScenario != null
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     *
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port to
     *                                    connect to.
     * @param executionMode               execution mode for the next run.
     * @param testScenario                test scenario to be executed.
     * @throws Exception <i>to do</i>.
     */
    protected VacuumCleanerTesterCyPhy(
            String vacuumCleanerInboundPortURI,
            ExecutionMode executionMode,
            TestScenario testScenario) throws Exception {
        super(REFLECTION_INBOUND_PORT_URI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS,
                AssertionChecking.assertTrueAndReturnOrThrow(
                        executionMode != null && !executionMode.isStandard(),
                        executionMode,
                        () -> new PreconditionException(
                                "currentExecutionMode != null && "
                                        + "!currentExecutionMode.isStandard()")),
                AssertionChecking.assertTrueAndReturnOrThrow(
                        testScenario != null,
                        testScenario.getClockURI(),
                        () -> new PreconditionException("testScenario != null")),
                testScenario);

        this.initialise(vacuumCleanerInboundPortURI);
    }

    /**
     * initialise a vacuum cleaner tester component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * vacuumCleanerInboundPortURI != null && !vacuumCleanerInboundPortURI.isEmpty()
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param vacuumCleanerInboundPortURI URI of the vacuum cleaner inbound port to
     *                                    connect to.
     * @throws Exception <i>to do</i>.
     */
    protected void initialise(
            String vacuumCleanerInboundPortURI) throws Exception {
        this.vacuumCleanerInboundPortURI = vacuumCleanerInboundPortURI;
        this.vcop = new VacuumCleanerOutboundPort(this);
        this.vcop.publishPort();

        if (VERBOSE) {
            this.tracer.get().setTitle("Vacuum cleaner tester component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        if (this.getExecutionMode().isTestWithoutSimulation()) {
            this.statistics = new TestsStatistics();
        }

        assert VacuumCleanerTesterCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "VacuumCleanerTesterCyPhy.implementationInvariants(this)");
        assert VacuumCleanerTesterCyPhy.invariants(this) : new InvariantException(
                "VacuumCleanerTesterCyPhy.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Test action methods
    // -------------------------------------------------------------------------

    public void turnOnVacuumCleaner() throws Exception {
        this.vcop.turnOn();
    }

    public void turnOffVacuumCleaner() throws Exception {
        this.vcop.turnOff();
    }

    public void setLowVacuumCleaner() throws Exception {
        this.vcop.setLow();
    }

    public void setHighVacuumCleaner() throws Exception {
        this.vcop.setHigh();
    }

    // -------------------------------------------------------------------------
    // Tests implementations
    // -------------------------------------------------------------------------

    public void testGetState() {
        this.logMessage("Feature: Getting the state of the vacuum cleaner");
        this.logMessage("  Scenario: getting the state when off");
        this.logMessage("    Given the vacuum cleaner has not been used yet");
        VacuumCleanerState result = null;
        try {
            this.logMessage("    When I test the state of the vacuum cleaner");
            result = this.vcop.getState();
            this.logMessage("    Then the vacuum cleaner is in its initial state");
            if (!VacuumCleanerCyPhy.INITIAL_STATE.equals(result)) {
                this.statistics.incorrectResult();
                this.logMessage("     but was: " + result);
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    public void testGetMode() {
        this.logMessage("Feature: Getting the mode of the vacuum cleaner");
        this.logMessage("  Scenario: getting the mode when off");
        this.logMessage("    Given the vacuum cleaner is off");
        VacuumCleanerState sResult = null;
        try {
            sResult = this.vcop.getState();
            if (!VacuumCleanerState.OFF.equals(sResult)) {
                this.statistics.failedCondition();
                this.logMessage("     but was: " + sResult);
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        VacuumCleanerMode mResult = null;
        try {
            this.logMessage("    When I test the mode of the vacuum cleaner");
            mResult = this.vcop.getMode();
            this.logMessage("    Then the vacuum cleaner is in its initial mode");
            if (!VacuumCleanerCyPhy.INITIAL_MODE.equals(mResult)) {
                this.logMessage("     but was: " + mResult);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    public void testTurnOnOff() {
        this.logMessage("Feature: turning the vacuum cleaner on and off");
        this.logMessage("  Scenario: turning on when off");
        VacuumCleanerState resultState = null;
        VacuumCleanerMode resultMode = null;
        try {
            this.logMessage("    Given the vacuum cleaner is off");
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.OFF.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.failedCondition();
            }
            this.logMessage("    When the vacuum cleaner is turned on");
            this.vcop.turnOn();
            this.logMessage("    Then the vacuum cleaner is on");
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.incorrectResult();
            }
            this.logMessage("    And the vacuum cleaner is in mode low");
            resultMode = this.vcop.getMode();
            if (!VacuumCleanerMode.LOW.equals(resultMode)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: turning on when on");
        this.logMessage("    Given the vacuum cleaner is on");
        try {
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.failedCondition();
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.logMessage("    When the vacuum cleaner is turned on");
        this.logMessage("    Then a precondition exception is thrown");
        boolean old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false;
            this.vcop.turnOn();
            this.logMessage("     but it was not thrown");
            this.statistics.incorrectResult();
        } catch (Throwable e) {

        } finally {
            BCMException.VERBOSE = old;
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: turning off when on");
        this.logMessage("    Given the vacuum cleaner is on");
        try {
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.failedCondition();
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.logMessage("    When the vacuum cleaner is turned off");
        try {
            this.vcop.turnOff();
            this.logMessage("    Then the vacuum cleaner is off");
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.OFF.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: turning off when off");
        this.logMessage("    Given the vacuum cleaner is off");
        try {
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.OFF.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.failedCondition();
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        this.logMessage("    When the vacuum cleaner is turned off");
        this.logMessage("    Then a precondition exception is thrown");
        old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false;
            this.vcop.turnOff();
            this.logMessage("     but the precondition exception was not thrown");
            this.statistics.incorrectResult();
        } catch (Throwable e) {

        } finally {
            BCMException.VERBOSE = old;
        }

        this.statistics.updateStatistics();
    }

    public void testSetLowHigh() {
        this.logMessage("Feature: switching the vacuum cleaner low and high.");
        this.logMessage("  Scenario: set the vacuum cleaner high from low");
        this.logMessage("    Given the vacuum cleaner is on");
        VacuumCleanerState resultState = null;
        VacuumCleanerMode resultMode = null;
        try {
            this.vcop.turnOn();
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.failedCondition();
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        try {
            this.logMessage("    And the vacuum cleaner is low");
            resultMode = this.vcop.getMode();
            if (!VacuumCleanerMode.LOW.equals(resultMode)) {
                this.logMessage("     but was: " + resultMode);
                this.statistics.failedCondition();
            }
        } catch (Throwable e) {
            this.statistics.failedCondition();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        try {
            this.logMessage("    When the vacuum cleaner is set high");
            this.logMessage("    Then the vacuum cleaner is on");
            this.vcop.setHigh();
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        try {
            this.logMessage("    And  the vacuum cleaner is high");
            resultMode = this.vcop.getMode();
            if (!VacuumCleanerMode.HIGH.equals(resultMode)) {
                this.logMessage("     but was: " + resultMode);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: set the vacuum cleaner high from high");
        this.logMessage("    Given the vacuum cleaner is on");
        this.logMessage("    And the vacuum cleaner is high");
        this.logMessage("    When the vacuum cleaner is set high");
        this.logMessage("    Then a precondition exception is thrown");
        boolean old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false;
            this.vcop.setHigh();
            this.logMessage("     but it was not thrown");
            this.statistics.incorrectResult();
        } catch (Throwable e) {

        } finally {
            BCMException.VERBOSE = old;
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: set the vacuum cleaner low from high");
        this.logMessage("    Given the vacuum cleaner is on");
        this.logMessage("    And the vacuum cleaner is high");
        this.logMessage("    When the vacuum cleaner is set low");
        try {
            this.vcop.setLow();
            this.logMessage("    Then the vacuum cleaner is on");
            resultState = this.vcop.getState();
            if (!VacuumCleanerState.ON.equals(resultState)) {
                this.logMessage("     but was: " + resultState);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }
        try {
            this.logMessage("    And the vacuum cleaner is low");
            resultMode = this.vcop.getMode();
            if (!VacuumCleanerMode.LOW.equals(resultMode)) {
                this.logMessage("     but was: " + resultMode);
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: set the vacuum cleaner low from low");
        this.logMessage("    Given the vacuum cleaner is on");
        this.logMessage("    And the vacuum cleaner is low");
        this.logMessage("    When the vacuum cleaner is set low");
        this.logMessage("    Then a precondition exception is thrown");
        old = BCMException.VERBOSE;
        try {
            BCMException.VERBOSE = false;
            this.vcop.setLow();
            this.logMessage("     but it was not thrown");
            this.statistics.incorrectResult();
        } catch (Throwable e) {

        } finally {
            BCMException.VERBOSE = old;
        }

        this.statistics.updateStatistics();

        // turn off at the end of the tests
        try {
            this.vcop.turnOff();
        } catch (Throwable e) {
        }
    }

    /**
     * run all unit tests.
     */
    protected void runAllUnitTests() {
        this.testGetState();
        this.testGetMode();
        this.testTurnOnOff();
        this.testSetLowHigh();

        this.statistics.statisticsReport(this);
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        try {
            this.doPortConnection(
                    this.vcop.getPortURI(),
                    vacuumCleanerInboundPortURI,
                    VacuumCleanerConnector.class.getCanonicalName());
        } catch (Throwable e) {
            throw new ComponentStartException(e);
        }
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public synchronized void execute() throws Exception {
        this.traceMessage("Vacuum Cleaner Unit Tester begins execution.\n");

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
                        ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                        this.clockURI);
                this.executeTestScenario(testScenario);
                break;
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
            case UNIT_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation not implemented yet!");
            case STANDARD:
                this.statistics = new TestsStatistics();
                this.traceMessage("Vacuum Cleaner Unit Tester starts the tests.\n");
                this.runAllUnitTests();
                this.traceMessage("Vacuum Cleaner Unit Tester ends.\n");
                break;
            default:
        }
        this.traceMessage("Vacuum Cleaner Unit Tester ends execution.\n");
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#finalise()
     */
    @Override
    public synchronized void finalise() throws Exception {
        this.doPortDisconnection(this.vcop.getPortURI());
        super.finalise();
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.vcop.unpublishPort();
        } catch (Throwable e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }
}
// -----------------------------------------------------------------------------
