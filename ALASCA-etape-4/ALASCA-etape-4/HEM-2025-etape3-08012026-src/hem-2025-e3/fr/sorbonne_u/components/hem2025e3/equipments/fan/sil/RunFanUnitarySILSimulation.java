package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.ArrayList;
import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanUnitTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
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
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.VerboseException;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunFanUnitarySILSimulation</code> creates a simulator
 * for the fan and then runs a typical SIL simulation.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The simulation architecture for the fan contains three atomic models
 * composed under a coupled model:
 * </p>
 * <ol>
 * <li>{@code FanUnitTesterModel} - emits user events towards the state
 * model</li>
 * <li>{@code FanStateSILModel} - tracks fan state and relays events</li>
 * <li>{@code FanElectricitySILModel} - simulates electricity consumption</li>
 * </ol>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class RunFanUnitarySILSimulation {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** the acceleration factor used in the real time SIL simulations. */
    public static final double ACCELERATION_FACTOR = 3600.0;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= FanSimulationConfigurationI.staticInvariants();
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
            Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

            atomicModelDescriptors.put(
                    FanStateSILModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanStateSILModel.class,
                            FanStateSILModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    FanElectricitySILModel.URI,
                    RTAtomicHIOA_Descriptor.create(
                            FanElectricitySILModel.class,
                            FanElectricitySILModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));
            atomicModelDescriptors.put(
                    FanUnitTesterModel.URI,
                    RTAtomicModelDescriptor.create(
                            FanUnitTesterModel.class,
                            FanUnitTesterModel.URI,
                            FanSimulationConfigurationI.TIME_UNIT,
                            null,
                            ACCELERATION_FACTOR));

            Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

            Set<String> submodels = new HashSet<String>();
            submodels.add(FanStateSILModel.URI);
            submodels.add(FanElectricitySILModel.URI);
            submodels.add(FanUnitTesterModel.URI);

            Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

            connections.put(
                    new EventSource(FanUnitTesterModel.URI, SwitchOnFan.class),
                    new EventSink[] {
                            new EventSink(FanStateSILModel.URI, SwitchOnFan.class)
                    });
            connections.put(
                    new EventSource(FanUnitTesterModel.URI, SwitchOffFan.class),
                    new EventSink[] {
                            new EventSink(FanStateSILModel.URI, SwitchOffFan.class)
                    });
            connections.put(
                    new EventSource(FanUnitTesterModel.URI, SetHighSpeedFan.class),
                    new EventSink[] {
                            new EventSink(FanStateSILModel.URI, SetHighSpeedFan.class)
                    });
            connections.put(
                    new EventSource(FanUnitTesterModel.URI, SetLowSpeedFan.class),
                    new EventSink[] {
                            new EventSink(FanStateSILModel.URI, SetLowSpeedFan.class)
                    });

            connections.put(
                    new EventSource(FanStateSILModel.URI, SwitchOnFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricitySILModel.URI,
                                    SwitchOnFan.class)
                    });
            connections.put(
                    new EventSource(FanStateSILModel.URI, SwitchOffFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricitySILModel.URI,
                                    SwitchOffFan.class)
                    });
            connections.put(
                    new EventSource(FanStateSILModel.URI, SetHighSpeedFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricitySILModel.URI,
                                    SetHighSpeedFan.class)
                    });
            connections.put(
                    new EventSource(FanStateSILModel.URI, SetLowSpeedFan.class),
                    new EventSink[] {
                            new EventSink(FanElectricitySILModel.URI,
                                    SetLowSpeedFan.class)
                    });

            coupledModelDescriptors.put(
                    FanCoupledModel.URI,
                    new RTCoupledModelDescriptor(
                            FanCoupledModel.class,
                            FanCoupledModel.URI,
                            submodels,
                            null,
                            null,
                            connections,
                            null,
                            ACCELERATION_FACTOR));

            ArchitectureI architecture = new RTArchitecture(
                    FanCoupledModel.URI,
                    atomicModelDescriptors,
                    coupledModelDescriptors,
                    FanSimulationConfigurationI.TIME_UNIT);

            SimulatorI se = architecture.constructSimulator();
            SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

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
                    FanSimulationConfigurationI.TIME_UNIT.toMillis(1)
                            * (d.getSimulatedDuration() / ACCELERATION_FACTOR))
                    .longValue();
            Thread.sleep(executionDuration + 2000L);
            System.out.println(classical.endMessage());
            System.exit(0);
        } catch (Exception e) {
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

    protected static TestScenarioWithSimulation classical()
            throws VerboseException {
        return new TestScenarioWithSimulation(
                "-----------------------------------------------------\n" +
                        "Classical\n\n" +
                        "  Gherkin specification\n\n" +
                        "    Feature: fan operation\n\n" +
                        "      Scenario: fan switched on\n" +
                        "        Given a fan that is off\n" +
                        "        When it is switched on\n" +
                        "        Then it is on at low speed\n" +
                        "      Scenario: fan set to high speed\n" +
                        "        Given a fan that is on at low speed\n" +
                        "        When it is set to high speed\n" +
                        "        Then it is on at high speed\n" +
                        "      Scenario: fan set to low speed\n" +
                        "        Given a fan that is on at high speed\n" +
                        "        When it is set to low speed\n" +
                        "        Then it is on at low speed\n" +
                        "      Scenario: fan switched off\n" +
                        "        Given a fan that is on\n" +
                        "        When it is switched off\n" +
                        "        Then it is off\n" +
                        "-----------------------------------------------------\n",
                "\n-----------------------------------------------------\n" +
                        "End Classical\n" +
                        "-----------------------------------------------------",
                "fake-clock-URI",
                START_INSTANT,
                END_INSTANT,
                FanCoupledModel.URI,
                START_TIME,
                (ts, simParams) -> {
                    simParams.put(
                            ModelI.createRunParameterName(
                                    FanUnitTesterModel.URI,
                                    FanUnitTesterModel.TEST_SCENARIO_RP_NAME),
                            ts);
                },
                new SimulationTestStep[] {
                        new SimulationTestStep(
                                FanUnitTesterModel.URI,
                                Instant.parse("2025-10-20T13:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SwitchOnFan(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                FanUnitTesterModel.URI,
                                Instant.parse("2025-10-20T14:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SetHighSpeedFan(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                FanUnitTesterModel.URI,
                                Instant.parse("2025-10-20T15:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SetLowSpeedFan(t));
                                    return ret;
                                },
                                (m, t) -> {
                                }),
                        new SimulationTestStep(
                                FanUnitTesterModel.URI,
                                Instant.parse("2025-10-20T16:00:00.00Z"),
                                (m, t) -> {
                                    ArrayList<EventI> ret = new ArrayList<>();
                                    ret.add(new SwitchOffFan(t));
                                    return ret;
                                },
                                (m, t) -> {
                                })
                });
    }
}
// -----------------------------------------------------------------------------
