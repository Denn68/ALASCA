package fr.sorbonne_u.components.hem2025e3.equipments.kettle;

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
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestsStatistics;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleTesterCyPhy</code> implements a component performing
 * tests for the class <code>KettleCyPhy</code> as a BCM component.
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
 * kettleUserInboundPortURI != null && !kettleUserInboundPortURI.isEmpty()
 * }
 * invariant	{@code
 * kettleInternalControlInboundPortURI != null && !kettleInternalControlInboundPortURI.isEmpty()
 * }
 * invariant	{@code
 * kettleExternalControlInboundPortURI != null && !kettleExternalControlInboundPortURI.isEmpty()
 * }
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-02-03
 * </p>
 * 
 * @author Team DeMoh
 */
@RequiredInterfaces(required = { KettleUserCI.class,
        KettleInternalControlCI.class,
        KettleExternalControlCI.class })
public class KettleTesterCyPhy
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

    /**
     * standard reflection, inbound port URI for the
     * {@code KettleTesterCyPhy} component.
     */
    public static final String REFLECTION_INBOUND_PORT_URI = "kettle-unit-tester-RIP-URI";

    /** URI of the user component interface inbound port. */
    protected String kettleUserInboundPortURI;
    /** URI of the internal control component interface inbound port. */
    protected String kettleInternalControlInboundPortURI;
    /** URI of the external control component interface inbound port. */
    protected String kettleExternalControlInboundPortURI;

    /** user component interface outbound port. */
    protected KettleUserOutboundPort kop;
    /** internal control component interface outbound port. */
    protected KettleInternalControlOutboundPort kicop;
    /** external control component interface outbound port. */
    protected KettleExternalControlOutboundPort kecop;

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

    protected static boolean implementationInvariants(KettleTesterCyPhy kt) {
        assert kt != null : new PreconditionException("kt != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                kt.kettleUserInboundPortURI != null &&
                        !kt.kettleUserInboundPortURI.isEmpty(),
                KettleTesterCyPhy.class, kt,
                "kt.kettleUserInboundPortURI != null && "
                        + "!kt.kettleUserInboundPortURI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                kt.kettleInternalControlInboundPortURI != null &&
                        !kt.kettleInternalControlInboundPortURI.isEmpty(),
                KettleTesterCyPhy.class, kt,
                "kt.kettleInternalControlInboundPortURI != null && "
                        + "!kt.kettleInternalControlInboundPortURI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                kt.kettleExternalControlInboundPortURI != null &&
                        !kt.kettleExternalControlInboundPortURI.isEmpty(),
                KettleTesterCyPhy.class, kt,
                "kt.kettleExternalControlInboundPortURI != null &&"
                        + "!kt.kettleExternalControlInboundPortURI.isEmpty()");
        return ret;
    }

    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= AssertionChecking.checkStaticInvariant(
                X_RELATIVE_POSITION >= 0,
                KettleTesterCyPhy.class,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkStaticInvariant(
                Y_RELATIVE_POSITION >= 0,
                KettleTesterCyPhy.class,
                "Y_RELATIVE_POSITION >= 0");
        return ret;
    }

    protected static boolean invariants(KettleTesterCyPhy kt) {
        assert kt != null : new PreconditionException("kt != null");

        boolean ret = true;
        ret &= staticInvariants();
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution for manual tests (no test scenario and no simulation)

    protected KettleTesterCyPhy(
            String kettleUserInboundPortURI,
            String kettleInternalControlInboundPortURI,
            String kettleExternalControlInboundPortURI) throws Exception {
        super(REFLECTION_INBOUND_PORT_URI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS);

        this.initialise(kettleUserInboundPortURI,
                kettleInternalControlInboundPortURI,
                kettleExternalControlInboundPortURI);
    }

    // Test execution with test scenario but no simulation

    protected KettleTesterCyPhy(
            String kettleUserInboundPortURI,
            String kettleInternalControlInboundPortURI,
            String kettleExternalControlInboundPortURI,
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

        this.initialise(kettleUserInboundPortURI,
                kettleInternalControlInboundPortURI,
                kettleExternalControlInboundPortURI);
    }

    protected void initialise(
            String kettleUserInboundPortURI,
            String kettleInternalControlInboundPortURI,
            String kettleExternalControlInboundPortURI) throws Exception {
        this.kettleUserInboundPortURI = kettleUserInboundPortURI;
        this.kop = new KettleUserOutboundPort(this);
        this.kop.publishPort();
        this.kettleInternalControlInboundPortURI = kettleInternalControlInboundPortURI;
        this.kicop = new KettleInternalControlOutboundPort(this);
        this.kicop.publishPort();
        this.kettleExternalControlInboundPortURI = kettleExternalControlInboundPortURI;
        this.kecop = new KettleExternalControlOutboundPort(this);
        this.kecop.publishPort();

        if (VERBOSE) {
            this.tracer.get().setTitle("Kettle tester component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        this.statistics = new TestsStatistics();

        assert KettleTesterCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "KettleTester.implementationInvariants(this)");
        assert KettleTesterCyPhy.invariants(this) : new InvariantException("KettleTester.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Test action helper methods
    // -------------------------------------------------------------------------

    public KettleUserOutboundPort getKop() {
        return this.kop;
    }

    public KettleInternalControlOutboundPort getKicop() {
        return this.kicop;
    }

    public KettleExternalControlOutboundPort getKecop() {
        return this.kecop;
    }

    // -------------------------------------------------------------------------
    // Tests implementations
    // -------------------------------------------------------------------------

    protected void testOff() {
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

    protected void testSwitchOnSwitchOff() {
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

    protected void testHeating() {
        this.logMessage("Feature: heating water in the kettle");

        this.logMessage("  Scenario: starting heating when kettle is on");
        this.logMessage("    Given the kettle is initialised");
        this.logMessage("    And the kettle is on");
        boolean result;
        try {
            this.kop.switchOn();
            result = this.kop.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I start heating");
            this.kicop.startHeating();
            result = this.kicop.heating();
            if (result) {
                this.logMessage("    Then the kettle is heating");
            } else {
                this.logMessage("     but was: not heating");
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: stopping heating when kettle is heating");
        this.logMessage("    Given the kettle is initialised");
        this.logMessage("    And the kettle is heating");
        try {
            this.logMessage("    When I stop heating");
            this.kicop.stopHeating();
            result = !this.kicop.heating();
            if (result) {
                this.logMessage("    Then the kettle is not heating");
            } else {
                this.logMessage("     but was: heating");
                this.statistics.incorrectResult();
            }
            this.kop.switchOff();
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    protected void testKeepingWarm() {
        this.logMessage("Feature: keeping water warm in the kettle");

        this.logMessage("  Scenario: starting keeping warm when kettle is on");
        this.logMessage("    Given the kettle is initialised");
        this.logMessage("    And the kettle is on");
        boolean result;
        try {
            this.kop.switchOn();
            result = this.kop.on();
            if (!result) {
                this.logMessage("     but was: off");
                this.statistics.failedCondition();
            }
            this.logMessage("    When I start keeping warm");
            this.kicop.startKeepingWarm();
            result = this.kicop.keepingWarm();
            if (result) {
                this.logMessage("    Then the kettle is keeping warm");
            } else {
                this.logMessage("     but was: not keeping warm");
                this.statistics.incorrectResult();
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();

        this.logMessage("  Scenario: stopping keeping warm when kettle is keeping warm");
        this.logMessage("    Given the kettle is initialised");
        this.logMessage("    And the kettle is keeping warm");
        try {
            this.logMessage("    When I stop keeping warm");
            this.kicop.stopKeepingWarm();
            result = !this.kicop.keepingWarm();
            if (result) {
                this.logMessage("    Then the kettle is not keeping warm");
            } else {
                this.logMessage("     but was: keeping warm");
                this.statistics.incorrectResult();
            }
            this.kop.switchOff();
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    protected void testCurrentTemperature() {
        this.logMessage("Feature: getting the current water temperature in the kettle");

        this.logMessage("  Scenario: getting the current temperature when on");
        this.logMessage("    Given the kettle is initialised");
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
            this.logMessage("    When I get the current temperature of the kettle");
            temperature = this.kicop.getCurrentTemperature();
            if (temperature.getMeasure().getData() == KettleCyPhy.FAKE_CURRENT_TEMPERATURE.getMeasure().getData() &&
                    temperature.getMeasure().getMeasurementUnit().equals(
                            KettleCyPhy.FAKE_CURRENT_TEMPERATURE.getMeasure().getMeasurementUnit())) {
                this.logMessage("    Then the current temperature is the kettle"
                        + " standard current temperature");
            } else {
                this.logMessage("     but was: " + temperature.getMeasure().getData());
                this.statistics.incorrectResult();
            }
            this.kop.switchOff();
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    protected void testPowerLevel() {
        this.logMessage("Feature: getting and setting the power level of the kettle");

        this.logMessage("  Scenario: getting the maximum power level through the"
                + " external control interface");
        this.logMessage("    Given the kettle is initialised");
        Measure<Double> powerLevel = null;
        try {
            this.logMessage("    When I get the maximum power level through the"
                    + " external control interface");
            powerLevel = this.kecop.getMaxPowerLevel();
            if (powerLevel.getData() == KettleCyPhy.MAX_POWER_LEVEL.getData() &&
                    powerLevel.getMeasurementUnit().equals(
                            KettleCyPhy.MAX_POWER_LEVEL.getMeasurementUnit())) {
                this.logMessage("    Then the result is the kettle maximum power level");
            } else {
                this.statistics.incorrectResult();
                this.logMessage("     but was: " + powerLevel.getData());
            }
        } catch (Throwable e) {
            this.statistics.incorrectResult();
            this.logMessage("     but the exception " + e + " has been raised");
        }

        this.statistics.updateStatistics();
    }

    /**
     * run all tests of the kettle component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
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
     */
    protected void runAllTests() {
        this.logMessage("---------------------------------------------");
        this.logMessage("Running all tests of the kettle component...");
        this.logMessage("---------------------------------------------");

        this.testOff();
        this.testSwitchOnSwitchOff();
        this.testHeating();
        this.testKeepingWarm();
        this.testCurrentTemperature();
        this.testPowerLevel();

        this.logMessage("---------------------------------------------");
        this.logMessage("Kettle tests completed.");
        this.statistics.statisticsReport(this);
        this.logMessage("---------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        try {
            this.doPortConnection(
                    this.kop.getPortURI(),
                    this.kettleUserInboundPortURI,
                    KettleUserConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.kicop.getPortURI(),
                    this.kettleInternalControlInboundPortURI,
                    KettleInternalControlConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.kecop.getPortURI(),
                    this.kettleExternalControlInboundPortURI,
                    KettleExternalControlConnector.class.getCanonicalName());
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public void execute() throws Exception {
        this.traceMessage("Kettle Unit Tester begins execution.\n");

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
                this.traceMessage("Kettle Unit Tester starts the tests.\n");
                this.runAllTests();
                this.traceMessage("Kettle Unit Tester ends.\n");
                break;
            default:
        }

        this.traceMessage("Kettle Unit Tester ends execution.\n");
    }

    @Override
    public synchronized void finalise() throws Exception {
        this.doPortDisconnection(this.kop.getPortURI());
        this.doPortDisconnection(this.kicop.getPortURI());
        this.doPortDisconnection(this.kecop.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.kop.unpublishPort();
            this.kicop.unpublishPort();
            this.kecop.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }
}
// -----------------------------------------------------------------------------
