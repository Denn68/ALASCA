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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
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

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code> performs unit tests on the vacuum cleaner
 * component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class CVMUnitTest
        extends AbstractCVM {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    public static long DELAY_TO_START = 3000L;
    public static long END_SLEEP_DURATION = 10000L;

    public static TimeUnit SIMULATION_TIME_UNIT = TimeUnit.HOURS;
    public static Time SIMULATION_START_TIME = new Time(0.0, SIMULATION_TIME_UNIT);
    public static Duration SIMULATION_DURATION = new Duration(0.5, SIMULATION_TIME_UNIT);
    public static double ACCELERATION_FACTOR = 360.0;
    public static long EXECUTION_DURATION = DELAY_TO_START +
            TimeUnit.NANOSECONDS.toMillis(
                    TimeUtils.toNanos(
                            SIMULATION_DURATION.getSimulatedDuration() /
                                    ACCELERATION_FACTOR,
                            SIMULATION_DURATION.getTimeUnit()));

    public static ExecutionMode VACUUM_CLEANER_EXECUTION_MODE =
            // ExecutionMode.STANDARD;
            ExecutionMode.UNIT_TEST;
    // ExecutionMode.
    // UNIT_TEST_WITH_SIL_SIMULATION;

    public static ExecutionMode VACUUM_CLEANER_TESTER_EXECUTION_MODE =
            // ExecutionMode.STANDARD;
            ExecutionMode.UNIT_TEST;

    public static String CLOCK_URI = "vacuum-cleaner-test-clock";
    public static String START_INSTANT = "2025-11-22T08:00:00.00Z";

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CVMUnitTest() throws Exception {
        VacuumCleanerTesterCyPhy.VERBOSE = true;
        VacuumCleanerTesterCyPhy.X_RELATIVE_POSITION = 0;
        VacuumCleanerTesterCyPhy.Y_RELATIVE_POSITION = 1;
        VacuumCleanerCyPhy.VERBOSE = true;
        VacuumCleanerCyPhy.X_RELATIVE_POSITION = 1;
        VacuumCleanerCyPhy.Y_RELATIVE_POSITION = 1;
    }

    // -------------------------------------------------------------------------
    // CVM life-cycle
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
     */
    @Override
    public void deploy() throws Exception {
        if (VACUUM_CLEANER_EXECUTION_MODE.isStandard()) {

            AbstractComponent.createComponent(
                    VacuumCleanerCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.REFLECTION_INBOUND_PORT_URI,
                            VacuumCleanerCyPhy.INBOUND_PORT_URI
                    });

            AbstractComponent.createComponent(
                    VacuumCleanerTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.INBOUND_PORT_URI
                    });

        } else if (VACUUM_CLEANER_EXECUTION_MODE.isTestWithoutSimulation()) {

            long current = System.currentTimeMillis();
            long unixEpochStartTimeInMillis = current + DELAY_TO_START;
            Instant startInstant = Instant.parse(START_INSTANT);
            TestScenario testScenario = unitTestScenario();

            AbstractComponent.createComponent(
                    VacuumCleanerCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.REFLECTION_INBOUND_PORT_URI,
                            VacuumCleanerCyPhy.INBOUND_PORT_URI,
                            VACUUM_CLEANER_EXECUTION_MODE
                    });

            AbstractComponent.createComponent(
                    VacuumCleanerTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.INBOUND_PORT_URI,
                            VACUUM_CLEANER_TESTER_EXECUTION_MODE,
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

        } else if (VACUUM_CLEANER_EXECUTION_MODE.isSILTest()) {

            long current = System.currentTimeMillis();
            long unixEpochStartTimeInMillis = current + DELAY_TO_START;
            Instant startInstant = Instant.parse(START_INSTANT);
            TestScenario testScenario = unitTestScenarioWithSimulation();

            AbstractComponent.createComponent(
                    VacuumCleanerCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.REFLECTION_INBOUND_PORT_URI,
                            VacuumCleanerCyPhy.INBOUND_PORT_URI,
                            VACUUM_CLEANER_EXECUTION_MODE,
                            testScenario,
                            VacuumCleanerCyPhy.UNIT_TEST_ARCHITECTURE_URI,
                            ACCELERATION_FACTOR
                    });

            AbstractComponent.createComponent(
                    VacuumCleanerTesterCyPhy.class.getCanonicalName(),
                    new Object[] {
                            VacuumCleanerCyPhy.INBOUND_PORT_URI,
                            VACUUM_CLEANER_TESTER_EXECUTION_MODE,
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

    // -------------------------------------------------------------------------
    // Executing
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        BCMException.VERBOSE = true;
        try {
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

    public static TestScenario unitTestScenario() throws VerboseException {
        Instant startInstant = Instant.parse(START_INSTANT);
        long d = TimeUnit.NANOSECONDS.toSeconds(
                TimeUtils.toNanos(SIMULATION_DURATION));
        Instant endInstant = startInstant.plusSeconds(d);

        Instant turnOnInstant = startInstant.plusSeconds(300);
        Instant setHighInstant = startInstant.plusSeconds(600);
        Instant setLowInstant = startInstant.plusSeconds(900);
        Instant turnOffInstant = startInstant.plusSeconds(1200);

        return new TestScenario(
                CLOCK_URI,
                startInstant,
                endInstant,
                new TestStepI[] {
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                turnOnInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).turnOnVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                setHighInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).setHighVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                setLowInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).setLowVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                turnOffInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).turnOffVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                });
    }

    public static TestScenarioWithSimulation unitTestScenarioWithSimulation()
            throws VerboseException {
        Instant startInstant = Instant.parse(START_INSTANT);
        long d = TimeUnit.NANOSECONDS.toSeconds(
                TimeUtils.toNanos(SIMULATION_DURATION));
        Instant endInstant = startInstant.plusSeconds(d);

        Instant turnOnInstant = startInstant.plusSeconds(300);
        Instant setHighInstant = startInstant.plusSeconds(600);
        Instant setLowInstant = startInstant.plusSeconds(900);
        Instant turnOffInstant = startInstant.plusSeconds(1200);

        return new TestScenarioWithSimulation(
                CLOCK_URI,
                startInstant,
                endInstant,
                "global-archi",
                SIMULATION_START_TIME,
                (ts, simParams) -> {
                },
                new TestStepI[] {
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                turnOnInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).turnOnVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                setHighInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).setHighVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                setLowInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).setLowVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                }),
                        new TestStep(
                                CLOCK_URI,
                                VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
                                turnOffInstant,
                                owner -> {
                                    try {
                                        ((VacuumCleanerTesterCyPhy) owner).turnOffVacuumCleaner();
                                    } catch (Exception e) {
                                        throw new BCMRuntimeException(e);
                                    }
                                })
                });
    }
}
// -----------------------------------------------------------------------------
