package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.VerboseException;
import java.time.Instant;
import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunVacuumCleanerUnitarySILSimulator</code> is the main class
 * used to run real time simulations on the software-in-the-loop models of the
 * vacuum cleaner in isolation based on test scenarios.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * This simulation execution class creates a simulation architecture with
 * three atomic models (VacuumCleanerElectricitySILModel,
 * VacuumCleanerStateSILModel, VacuumCleanerUnitTesterSILModel) under a
 * VacuumCleanerCoupledModel and then executes it for one run.
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
 * VacuumCleanerSimulationConfigurationI.staticInvariants()
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class RunVacuumCleanerUnitarySILSimulator {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time SIL simulations. */
    public static final double ACCELERATION_FACTOR = 3600.0;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    /**
     * return true if the static invariants are observed, false otherwise.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * instance != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @return true if the invariants are observed, false otherwise.
     */
    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= VacuumCleanerSimulationConfigurationI.staticInvariants();
        return ret;
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        staticInvariants();
        Time.setPrintPrecision(4);
        Duration.setPrintPrecision(4);

        try {
            // map that will contain the atomic model descriptors to construct
            // the simulation architecture
            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

            // the vacuum cleaner model simulating its electricity consumption,
            // an atomic HIOA model hence we use an RTAtomicHIOA_Descriptor
            atomicModelDescriptors.put(
                    VacuumCleanerElectricitySILModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            VacuumCleanerElectricitySILModel.class,
                            VacuumCleanerElectricitySILModel.URI,
                            VacuumCleanerSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            // for atomic model, we use an RTAtomicModelDescriptor
            atomicModelDescriptors.put(
                    VacuumCleanerStateSILModel.URI,
                    RTAtomicModelDescriptor.create(
                            VacuumCleanerStateSILModel.class,
                            VacuumCleanerStateSILModel.URI,
                            VacuumCleanerSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    VacuumCleanerUnitTesterSILModel.URI,
                    RTAtomicModelDescriptor.create(
                            VacuumCleanerUnitTesterSILModel.class,
                            VacuumCleanerUnitTesterSILModel.URI,
                            VacuumCleanerSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            // map that will contain the coupled model descriptors to construct
            // the simulation architecture
            Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

            // the set of submodels of the coupled model, given by their URIs
            Set<String> submodels = new HashSet<String>();
            submodels.add(VacuumCleanerElectricitySILModel.URI);
            submodels.add(VacuumCleanerStateSILModel.URI);
            submodels.add(VacuumCleanerUnitTesterSILModel.URI);

            // event exchanging connections between exporting and importing
            // models
            Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

            connections.put(
                    new EventSource(VacuumCleanerUnitTesterSILModel.URI,
                            SwitchOnVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerStateSILModel.URI,
                                    SwitchOnVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUnitTesterSILModel.URI,
                            SwitchOffVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerStateSILModel.URI,
                                    SwitchOffVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUnitTesterSILModel.URI,
                            SetHighVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerStateSILModel.URI,
                                    SetHighVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerUnitTesterSILModel.URI,
                            SetLowVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerStateSILModel.URI,
                                    SetLowVacuumCleaner.class)
                    });

            connections.put(
                    new EventSource(VacuumCleanerStateSILModel.URI,
                            SwitchOnVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricitySILModel.URI,
                                    SwitchOnVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerStateSILModel.URI,
                            SwitchOffVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricitySILModel.URI,
                                    SwitchOffVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerStateSILModel.URI,
                            SetHighVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricitySILModel.URI,
                                    SetHighVacuumCleaner.class)
                    });
            connections.put(
                    new EventSource(VacuumCleanerStateSILModel.URI,
                            SetLowVacuumCleaner.class),
                    new EventSink[] {
                            new EventSink(VacuumCleanerElectricitySILModel.URI,
                                    SetLowVacuumCleaner.class)
                    });

            // coupled model descriptor
            coupledModelDescriptors.put(
                    VacuumCleanerCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            VacuumCleanerCoupledModel.class,
                            VacuumCleanerCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            // simulation architecture
            ArchitectureI architecture = new RTArchitecture(
                    VacuumCleanerCoupledModel.URI,
                    atomicModelDescriptors,
                    coupledModelDescriptors,
                    VacuumCleanerSimulationConfigurationI.TIME_UNIT);

            // Simulation run configuration

            // this add additional time at each simulation step in
            // standard simulations (useful when debugging)
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

            VacuumCleanerElectricitySILModel.VERBOSE = true;
            VacuumCleanerElectricitySILModel.DEBUG = false;
            VacuumCleanerStateSILModel.VERBOSE = true;
            VacuumCleanerStateSILModel.DEBUG = false;
            VacuumCleanerUnitTesterSILModel.VERBOSE = true;
            VacuumCleanerUnitTesterSILModel.DEBUG = false;

            // create the simulator from the simulation architecture
            SimulatorI se = architecture.constructSimulator();

            // run a CLASSICAL test scenario
            TestScenarioWithSimulation classical = classical();
            System.out.println(classical.beginMessage());
            Map<String, Object> classicalRunParameters = new HashMap<String, Object>();
            classical.addToRunParameters(classicalRunParameters);
            se.setSimulationRunParameters(classicalRunParameters);
            Time startTime = classical.getStartTime();
            Duration d = classical.getEndTime().subtract(startTime);
            long realTimeStart = System.currentTimeMillis() + 200;
            se.startRTSimulation(realTimeStart,
                    startTime.getSimulatedTime(),
                    d.getSimulatedDuration());
            long executionDuration = new Double(
                    VacuumCleanerSimulationConfigurationI.TIME_UNIT.toMillis(1)
                            * (d.getSimulatedDuration() / ACCELERATION_FACTOR))
                    .longValue();
            Thread.sleep(executionDuration + 2000L);
            SimulationReportI sr = se.getSimulatedModel().getFinalReport();
            System.out.println(sr);
            System.out.println(classical.endMessage());
            System.exit(0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------------------------------------------------
    // Test scenarios
    // -------------------------------------------------------------------------

    /** the start instant used in the test scenarios. */
    protected static Instant START_INSTANT = Instant.parse("2025-10-20T12:00:00.00Z");
    /** the end instant used in the test scenarios. */
    protected static Instant END_INSTANT = Instant.parse("2025-10-20T18:00:00.00Z");
    /**
     * the start time in simulated time, corresponding to
     * {@code START_INSTANT}.
     */
    protected static Time START_TIME = new Time(0.0, TimeUnit.HOURS);

    /**
     * standard test scenario, see Gherkin specification.
     * 
     * @throws VerboseException
     */
    protected static TestScenarioWithSimulation classical()
            throws VerboseException {
        return new TestScenarioWithSimulation(
                "-----------------------------------------------------\n" +
                        "Classical\n\n" +
                        "  Gherkin specification\n\n" +
                        "    Feature: vacuum cleaner operation\n\n" +
                        "      Scenario: vacuum cleaner switched on\n" +
                        "        Given a vacuum cleaner that is off\n" +
                        "        When it is switched on\n" +
                        "        Then it is on and low\n" +
                        "      Scenario: vacuum cleaner set high\n" +
                        "        Given a vacuum cleaner that is on\n" +
                        "        When it is set high\n" +
                        "        Then it is on and high\n" +
                        "      Scenario: vacuum cleaner set low\n" +
                        "        Given a vacuum cleaner that is on\n" +
                        "        When it is set low\n" +
                        "        Then it is on and low\n" +
                        "      Scenario: vacuum cleaner switched off\n" +
                        "        Given a vacuum cleaner that is on\n" +
                        "        When it is switched of\n" +
                        "        Then it is off\n" +
                        "-----------------------------------------------------\n",
                "\n-----------------------------------------------------\n" +
                        "End Classical\n" +
                        "-----------------------------------------------------",
                "fake-clock-URI", // no clock needed for purely simulation test scenario
                START_INSTANT,
                END_INSTANT,
                VacuumCleanerCoupledModel.URI, // no real global architecture in this
                                               // scenario, use the root model URI
                START_TIME,
                (ts, simParams) -> {
                    simParams.put(
                            ModelI.createRunParameterName(
                                    VacuumCleanerUnitTesterSILModel.URI,
                                    VacuumCleanerUnitTesterSILModel.TEST_SCENARIO_RP_NAME),
                            ts);
                },
                new SimulationTestStep[] {
                        new SimulationTestStep(
                                VacuumCleanerUnitTesterSILModel.URI,
                                Instant.parse("2025-10-20T13:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SwitchOnVacuumCleaner(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                VacuumCleanerUnitTesterSILModel.URI,
                                Instant.parse("2025-10-20T14:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SetHighVacuumCleaner(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                VacuumCleanerUnitTesterSILModel.URI,
                                Instant.parse("2025-10-20T15:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SetLowVacuumCleaner(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                VacuumCleanerUnitTesterSILModel.URI,
                                Instant.parse("2025-10-20T16:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SwitchOffVacuumCleaner(t));
                                    return ret;
                                },
                                (m, t) -> {
                                })
                });
    }
}
// -----------------------------------------------------------------------------
