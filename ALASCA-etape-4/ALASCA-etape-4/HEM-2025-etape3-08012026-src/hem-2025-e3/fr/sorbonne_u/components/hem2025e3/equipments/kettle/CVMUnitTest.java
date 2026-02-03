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

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleController.ControlMode;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code> performs unit tests for the kettle
 * component.
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
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * Created on : 2026-02-03
 * </p>
 * 
 * @author Team DeMoh
 */
public class CVMUnitTest
        extends AbstractCVM {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /**
     * delay before starting the test scenarios, leaving time to build
     * and initialise the components and their simulators.
     */
    public static long DELAY_TO_START = 3000L;
    /**
     * duration of the sleep at the end of the execution before exiting
     * the JVM.
     */
    public static long END_SLEEP_DURATION = 1000000L;

    /** time unit in which {@code SIMULATION_DURATION} is expressed. */
    public static TimeUnit SIMULATION_TIME_UNIT = TimeUnit.HOURS;
    /** start time of the simulation, in simulated logical time. */
    public static Time SIMULATION_START_TIME = new Time(0.0, SIMULATION_TIME_UNIT);
    /** duration of the simulation, in simulated time. */
    public static Duration SIMULATION_DURATION = new Duration(1.0, SIMULATION_TIME_UNIT);
    /**
     * for real time simulations, the acceleration factor applied to the
     * the simulated time to get the execution time of the simulations.
     */
    public static double ACCELERATION_FACTOR = 1200.0;
    /** duration of the execution. */
    public static long EXECUTION_DURATION = DELAY_TO_START +
            TimeUnit.NANOSECONDS.toMillis(
                    TimeUtils.toNanos(
                            SIMULATION_DURATION.getSimulatedDuration() /
                                    ACCELERATION_FACTOR,
                            SIMULATION_DURATION.getTimeUnit()));

    /** the execution mode for the kettle component. */
    public static ExecutionMode KETTLE_EXECUTION_MODE =
            // ExecutionMode.STANDARD;
            // ExecutionMode.UNIT_TEST;
            ExecutionMode.UNIT_TEST_WITH_SIL_SIMULATION;

    /** the execution mode for the kettle tester component. */
    public static ExecutionMode KETTLE_TESTER_EXECUTION_MODE =
            // ExecutionMode.STANDARD;
            ExecutionMode.UNIT_TEST;

    /**
     * for unit tests and SIL simulation unit tests, a {@code Clock} is
     * used to get a time-triggered synchronisation of the actions of
     * the components in the test scenarios.
     */
    public static String CLOCK_URI = "kettle-test-clock";
    /** start instant in test scenarios, as a string to be parsed. */
    public static String START_INSTANT = "2025-11-22T08:00:00.00Z";

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CVMUnitTest() throws Exception {
        KettleTesterCyPhy.VERBOSE = true;
        KettleTesterCyPhy.X_RELATIVE_POSITION = 0;
        KettleTesterCyPhy.Y_RELATIVE_POSITION = 1;
        KettleCyPhy.VERBOSE = true;
        KettleCyPhy.X_RELATIVE_POSITION = 1;
        KettleCyPhy.Y_RELATIVE_POSITION = 1;
        KettleController.VERBOSE = true;
        KettleController.X_RELATIVE_POSITION = 2;
        KettleController.Y_RELATIVE_POSITION = 1;
    }

    // -------------------------------------------------------------------------
    // CVM life-cycle
    // -------------------------------------------------------------------------

    @Override
    public void deploy() throws Exception {
        if (KETTLE_EXECUTION_MODE.isStandard()) {

            AbstractComponent.createComponent(
                    KettleCyPhy.class.getCanonicalName(),
                    new Object[] {});

            AbstractComponent.createComponent(
                    KettleTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            KettleCyPhy.USER_INBOUND_PORT_URI,
                            KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
                            KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI
                    });

        } else if (KETTLE_EXECUTION_MODE.isTestWithoutSimulation()) {

            long current = System.currentTimeMillis();
            long unixEpochStartTimeInMillis = current + DELAY_TO_START;
            Instant startInstant = Instant.parse(START_INSTANT);
            TestScenario testScenario = unitTestScenario();

            AbstractComponent.createComponent(
                    KettleCyPhy.class.getCanonicalName(),
                    new Object[] {
                            KETTLE_EXECUTION_MODE,
                            testScenario.getClockURI()
                    });

            AbstractComponent.createComponent(
                    KettleTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            KettleCyPhy.USER_INBOUND_PORT_URI,
                            KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
                            KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
                            KETTLE_TESTER_EXECUTION_MODE,
                            testScenario
                    });

            AbstractComponent.createComponent(
                    ClocksServer.class.getCanonicalName(),
                    new Object[] {
                            CLOCK_URI,
                            TimeUnit.MILLISECONDS.toNanos(
                                    unixEpochStartTimeInMillis),
                            startInstant,
                            ACCELERATION_FACTOR
                    });

        } else {
            assert KETTLE_EXECUTION_MODE.isSimulationTest();

            long current = System.currentTimeMillis();
            long unixEpochStartTimeInMillis = current + DELAY_TO_START;
            Instant startInstant = Instant.parse(START_INSTANT);
            TestScenario testScenario = unitTestScenarioWithSimulation();

            AbstractComponent.createComponent(
                    KettleCyPhy.class.getCanonicalName(),
                    new Object[] {
                            KettleCyPhy.REFLECTION_INBOUND_PORT_URI,
                            KettleCyPhy.USER_INBOUND_PORT_URI,
                            KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
                            KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
                            KettleCyPhy.SENSOR_INBOUND_PORT_URI,
                            KettleCyPhy.ACTUATOR_INBOUND_PORT_URI,
                            KETTLE_EXECUTION_MODE,
                            testScenario,
                            KettleCyPhy.UNIT_TEST_ARCHITECTURE_URI,
                            ACCELERATION_FACTOR
                    });

            AbstractComponent.createComponent(
                    KettleController.class.getCanonicalName(),
                    new Object[] {
                            KettleCyPhy.SENSOR_INBOUND_PORT_URI,
                            KettleCyPhy.ACTUATOR_INBOUND_PORT_URI,
                            KettleController.STANDARD_CONTROL_PERIOD,
                            ControlMode.PULL,
                            false, // keepWarmAfterBoiling
                            KETTLE_EXECUTION_MODE,
                            ACCELERATION_FACTOR
                    });

            AbstractComponent.createComponent(
                    KettleTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            KettleCyPhy.USER_INBOUND_PORT_URI,
                            KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
                            KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
                            KETTLE_TESTER_EXECUTION_MODE,
                            testScenario
                    });

            AbstractComponent.createComponent(
                    ClocksServerWithSimulation.class.getCanonicalName(),
                    new Object[] {
                            CLOCK_URI,
                            TimeUnit.MILLISECONDS.toNanos(
                                    unixEpochStartTimeInMillis),
                            startInstant,
                            ACCELERATION_FACTOR,
                            DELAY_TO_START,
                            SIMULATION_START_TIME,
                            SIMULATION_DURATION });
        }

        super.deploy();
    }

    public static void main(String[] args) {
        try {
            VerboseException.VERBOSE = true;
            VerboseException.PRINT_STACK_TRACE = true;

            CVMUnitTest cvm = new CVMUnitTest();
            cvm.startStandardLifeCycle(EXECUTION_DURATION);
            Thread.sleep(END_SLEEP_DURATION);
            System.exit(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Test scenarios
    // -------------------------------------------------------------------------

    /**
     * return a test scenario without simulation for testing the kettle
     * component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * true
     * }	// no precondition.
     * post	{@code return != null}
     * </pre>
     *
     * @return a test scenario for the unit testing of the kettle component.
     * @throws VerboseException <i>to do</i>.
     */
    public static TestScenario unitTestScenario() throws VerboseException {
        Instant startInstant = Instant.parse(START_INSTANT);
        long d = TimeUnit.NANOSECONDS.toSeconds(
                TimeUtils.toNanos(SIMULATION_DURATION));
        Instant endInstant = startInstant.plusSeconds(d);

        Instant switchOnInstant = startInstant.plusSeconds(60);
        Instant startHeatingInstant = startInstant.plusSeconds(120);
        Instant stopHeatingInstant = startInstant.plusSeconds(600);
        Instant startKeepingWarmInstant = startInstant.plusSeconds(660);
        Instant stopKeepingWarmInstant = startInstant.plusSeconds(900);
        Instant switchOffInstant = startInstant.plusSeconds(d - 60);

        return new TestScenario(
                CLOCK_URI,
                startInstant,
                endInstant,
                new TestStepI[] {
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                switchOnInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKop().switchOn();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                startHeatingInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKicop().startHeating();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                stopHeatingInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKicop().stopHeating();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                startKeepingWarmInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKicop().startKeepingWarm();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                stopKeepingWarmInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKicop().stopKeepingWarm();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                switchOffInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKop().switchOff();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                })
                });
    }

    /**
     * return a test scenario for testing with SIL simulation the kettle
     * component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * true
     * }	// no precondition.
     * post	{@code return != null}
     * </pre>
     *
     * @return a test scenario for the unit testing of the kettle component.
     * @throws VerboseException <i>to do</i>.
     */
    public static TestScenarioWithSimulation unitTestScenarioWithSimulation()
            throws VerboseException {
        Instant startInstant = Instant.parse(START_INSTANT);
        long d = TimeUnit.NANOSECONDS.toSeconds(
                TimeUtils.toNanos(SIMULATION_DURATION));
        Instant endInstant = startInstant.plusSeconds(d);

        Instant switchOnInstant = startInstant.plusSeconds(60);
        Instant switchOffInstant = startInstant.plusSeconds(d - 60);

        return new TestScenarioWithSimulation(
                CLOCK_URI,
                startInstant,
                endInstant,
                "global-archi", // no global archi in fact
                SIMULATION_START_TIME,
                (ts, simParams) -> {
                },
                new TestStepI[] {
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                switchOnInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKop().switchOn();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                switchOffInstant,
                                owner -> {
                                    try {
                                        ((KettleTesterCyPhy) owner).getKop().switchOff();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                })
                });
    }
}
// -----------------------------------------------------------------------------
