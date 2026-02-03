package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.ArrayList;

import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineUnitTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.VerboseException;

public class RunWashingMachineUnitarySILSimulation {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** Time unit used in the simulation. */
	public static final TimeUnit TIME_UNIT = TimeUnit.HOURS;

	/** Acceleration factor for real-time simulation. */
	public static final double ACCELERATION_FACTOR = 3600.0;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void main(String[] args) {
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			atomicModelDescriptors.put(
					WashingMachineStateSILModel.URI,
					RTAtomicModelDescriptor.create(
							WashingMachineStateSILModel.class,
							WashingMachineStateSILModel.URI,
							TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			atomicModelDescriptors.put(
					WashingMachineElectricitySILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							WashingMachineElectricitySILModel.class,
							WashingMachineElectricitySILModel.URI,
							TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			atomicModelDescriptors.put(
					WashingMachineTemperatureSILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							WashingMachineTemperatureSILModel.class,
							WashingMachineTemperatureSILModel.URI,
							TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			atomicModelDescriptors.put(
					WashingMachineUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							WashingMachineUnitTesterModel.class,
							WashingMachineUnitTesterModel.URI,
							TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

			Set<String> submodels = new HashSet<>();
			submodels.add(WashingMachineStateSILModel.URI);
			submodels.add(WashingMachineElectricitySILModel.URI);
			submodels.add(WashingMachineTemperatureSILModel.URI);
			submodels.add(WashingMachineUnitTesterModel.URI);

			Map<EventSource, EventSink[]> connections = new HashMap<>();

			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, SwitchOnWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, SwitchOffWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, StartWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, SetDelayedStart.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, SetDelayedStart.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, SuspendWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, SuspendWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, ResumeWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, ResumeWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, SetPowerWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineStateSILModel.URI, SetPowerWashingMachine.class)
					});

			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, SwitchOnWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, SwitchOffWashingMachine.class),
							new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, StartWashing.class),
							new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, SetDelayedStart.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, SetDelayedStart.class),
							new EventSink(WashingMachineTemperatureSILModel.URI, SetDelayedStart.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, SuspendWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, SuspendWashing.class),
							new EventSink(WashingMachineTemperatureSILModel.URI, SuspendWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, ResumeWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, ResumeWashing.class),
							new EventSink(WashingMachineTemperatureSILModel.URI, ResumeWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineStateSILModel.URI, SetPowerWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, SetPowerWashingMachine.class)
					});

			connections.put(
					new EventSource(WashingMachineTemperatureSILModel.URI, HeatingFinished.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricitySILModel.URI, HeatingFinished.class)
					});

			connections.put(
					new EventSource(WashingMachineElectricitySILModel.URI, WashingEnded.class),
					new EventSink[] {
							new EventSink(WashingMachineTemperatureSILModel.URI, WashingEnded.class)
					});
			connections.put(
					new EventSource(WashingMachineElectricitySILModel.URI, StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
					});

			Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

			bindings.put(
					new VariableSource("currentHeatingPower", Double.class,
							WashingMachineElectricitySILModel.URI),
					new VariableSink[] {
							new VariableSink("currentHeatingPower", Double.class,
									WashingMachineTemperatureSILModel.URI)
					});

			coupledModelDescriptors.put(
					WashingMachineCoupledModel.URI,
					new RTCoupledHIOA_Descriptor(
							WashingMachineCoupledModel.class,
							WashingMachineCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings,
							ACCELERATION_FACTOR));

			ArchitectureI architecture = new RTArchitecture(
					WashingMachineCoupledModel.URI,
					atomicModelDescriptors,
					coupledModelDescriptors,
					TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			TestScenarioWithSimulation testScenario = createTestScenario();
			System.out.println(testScenario.beginMessage());

			Map<String, Object> runParameters = new HashMap<>();
			testScenario.addToRunParameters(runParameters);
			se.setSimulationRunParameters(runParameters);

			Time startTime = testScenario.getStartTime();
			Duration d = testScenario.getEndTime().subtract(startTime);

			long realTimeStart = System.currentTimeMillis() + 200;
			se.startRTSimulation(realTimeStart,
					startTime.getSimulatedTime(),
					d.getSimulatedDuration());

			long executionDuration = new Double(TIME_UNIT.toMillis(1)
					* (d.getSimulatedDuration() / ACCELERATION_FACTOR)).longValue();

			Thread.sleep(executionDuration + 2000L);
			System.out.println(testScenario.endMessage());
			System.exit(0);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// -------------------------------------------------------------------------
	// Test scenarios
	// -------------------------------------------------------------------------

	protected static Instant START_INSTANT = Instant.parse("2025-10-20T08:00:00.00Z");
	protected static Instant END_INSTANT = Instant.parse("2025-10-20T13:00:00.00Z");
	protected static Time START_TIME = new Time(0.0, TimeUnit.HOURS);

	/**
	 * Create a test scenario for the washing machine simulation.
	 *
	 * Scenario:
	 * 1. SwitchOn at 08:05
	 * 2. StartWashing (30 min duration, 40C target) at 08:10
	 * 3. SwitchOff at 09:50
	 */
	protected static TestScenarioWithSimulation createTestScenario() throws VerboseException {
		return new TestScenarioWithSimulation(
				"-----------------------------------------------------\n" +
						"WashingMachine SIL Test\n\n" +
						"  Scenario:\n" +
						"    1. Switch on the washing machine\n" +
						"    2. Start washing (2 min, 17C)\n" +
						"    3. Wait for heating + washing cycle\n" +
						"    4. Switch off\n" +
						"-----------------------------------------------------\n",
				"\n-----------------------------------------------------\n" +
						"End WashingMachine SIL Test\n" +
						"-----------------------------------------------------",
				"fake-clock-URI",
				START_INSTANT,
				END_INSTANT,
				WashingMachineCoupledModel.URI,
				START_TIME,
				(ts, simParams) -> {
					simParams.put(
							ModelI.createRunParameterName(
									WashingMachineUnitTesterModel.URI,
									WashingMachineUnitTesterModel.TEST_SCENARIO_RP_NAME),
							ts);
				},
				new SimulationTestStep[] {
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								Instant.parse("2025-10-20T08:05:00.00Z"),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new SwitchOnWashingMachine(t));
									return ret;
								},
								(m, t) -> {
								}),
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								Instant.parse("2025-10-20T08:10:00.00Z"),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new StartWashing(t, 2, 17.0));
									return ret;
								},
								(m, t) -> {
								}),
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								Instant.parse("2025-10-20T12:50:00.00Z"),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new SwitchOffWashingMachine(t));
									return ret;
								},
								(m, t) -> {
								})
				});
	}
}
