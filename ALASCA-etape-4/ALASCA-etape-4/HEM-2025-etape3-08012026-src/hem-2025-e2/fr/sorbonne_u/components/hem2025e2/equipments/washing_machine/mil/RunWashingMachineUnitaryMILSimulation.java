package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.cyphy.utils.tests.SimulationTestStep;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

public class RunWashingMachineUnitaryMILSimulation {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** l'instant de démarrage de la simulation (pour la synchronisation). */
	protected static final Instant START_INSTANT = Instant.parse("2025-10-20T12:00:00.00Z");

	/** l'instant de fin de la simulation (180 minutes pour 2 cycles complets). */
	protected static final Instant END_INSTANT = Instant.parse("2025-10-20T15:30:00.00Z");

	/** le temps de démarrage dans le temps simulé (t=0). */
	protected static final Time START_TIME = new Time(0.0, TimeUnit.MINUTES);

	// -------------------------------------------------------------------------
	// Main
	// -------------------------------------------------------------------------

	public static void main(String[] args) {
		try {
			// 1. Description des modèles atomiques
			Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

			// Le Modèle Électrique est un HIOA (hérite de AtomicHIOA)
			atomicModelDescriptors.put(
					WashingMachineElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							WashingMachineElectricityModel.class,
							WashingMachineElectricityModel.URI,
							WashingMachineSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					WashingMachineTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							WashingMachineTemperatureModel.class,
							WashingMachineTemperatureModel.URI,
							WashingMachineSimulationConfigurationI.TIME_UNIT,
							null));

			// Le Tester est un AtomicModel standard (hérite de
			// AbstractTestScenarioBasedAtomicModel)
			atomicModelDescriptors.put(
					WashingMachineUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							WashingMachineUnitTesterModel.class,
							WashingMachineUnitTesterModel.URI,
							WashingMachineSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

			Set<String> submodels = new HashSet<>();
			submodels.add(WashingMachineElectricityModel.URI);
			submodels.add(WashingMachineTemperatureModel.URI);
			submodels.add(WashingMachineUnitTesterModel.URI);

			Map<EventSource, EventSink[]> connections = new HashMap<>();

			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							SwitchOnWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SwitchOnWashingMachine.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									SwitchOnWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							SwitchOffWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SwitchOffWashingMachine.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									SwitchOffWashingMachine.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									StartWashing.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									StartWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							SetDelayedStart.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SetDelayedStart.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									SetDelayedStart.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							SuspendWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SuspendWashing.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									SuspendWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI, ResumeWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									ResumeWashing.class),
							new EventSink(WashingMachineTemperatureModel.URI,
									ResumeWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineElectricityModel.URI, WashingEnded.class),
					new EventSink[] {
							new EventSink(WashingMachineTemperatureModel.URI,
									WashingEnded.class) });
			connections.put(
					new EventSource(WashingMachineTemperatureModel.URI, HeatingFinished.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									HeatingFinished.class) });
			connections.put(
					new EventSource(WashingMachineTemperatureModel.URI, WashingEnded.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									WashingEnded.class) });
			connections.put(
					new EventSource(WashingMachineTemperatureModel.URI, StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI, StartWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineElectricityModel.URI, StartWashing.class),
					new EventSink[] {
							new EventSink(WashingMachineTemperatureModel.URI, StartWashing.class)
					});
			connections.put(
					new EventSource(WashingMachineUnitTesterModel.URI,
							SetPowerWashingMachine.class),
					new EventSink[] {
							new EventSink(WashingMachineElectricityModel.URI,
									SetPowerWashingMachine.class)
					});

			coupledModelDescriptors.put(
					WashingMachineCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							WashingMachineCoupledModel.class,
							WashingMachineCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							null));

			// 3. Construction de l'architecture
			ArchitectureI architecture = new Architecture(
					WashingMachineCoupledModel.URI,
					atomicModelDescriptors,
					coupledModelDescriptors,
					WashingMachineSimulationConfigurationI.TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;

			// 4. Récupération et Injection du Scénario
			TestScenarioWithSimulation scenario = standardScenario();

			Map<String, Object> simParams = new HashMap<>();
			scenario.addToRunParameters(simParams);
			se.setSimulationRunParameters(simParams);

			// 5. Lancement
			System.out.println("Starting Simulation...");
			// Calcul de la durée basée sur les instants définis
			long durationInMillis = java.time.Duration.between(START_INSTANT, END_INSTANT).toMillis();
			// Conversion en minutes pour le simulateur (car TimeUnit.MINUTES)
			double durationInMinutes = durationInMillis / 60000.0;

			se.doStandAloneSimulation(0.0, durationInMinutes);
			System.out.println("Simulation Ended.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------
	// Scénarios
	// -------------------------------------------------------------------------

	protected static TestScenarioWithSimulation standardScenario() {
		return new TestScenarioWithSimulation(
				"-----------------------------------------------------\n" +
						"Washing Machine Unit Test Scenario\n\n" +
						"  Gherkin specification\n\n" +
						"    Feature: washing machine operation (2 cycles)\n\n" +
						"    --- CYCLE 1: Direct Start ---\n" +
						"      Scenario: switch on\n" +
						"        Given a washing machine that is off\n" +
						"        When it is switched on -> Then it is on\n" +
						"      Scenario: start washing directly\n" +
						"        Given a washing machine that is on\n" +
						"        When StartWashing(10min, 40°C) is called\n" +
						"        Then it heats water and washes\n" +
						"      Scenario: washing ends\n" +
						"        Given the washing cycle finishes\n" +
						"        Then WashingEnded is emitted\n\n" +
						"    --- CYCLE 2: Delayed Start ---\n" +
						"      Scenario: program delayed start\n" +
						"        Given a washing machine that is on\n" +
						"        When SetDelayedStart(5min delay, 10min wash, 60°C)\n" +
						"        Then it waits for the delay\n" +
						"      Scenario: delayed cycle runs\n" +
						"        Given the delay has elapsed\n" +
						"        Then it heats to 60°C and washes\n" +
						"      Scenario: switch off\n" +
						"        Given the machine is done\n" +
						"        When switched off -> Then it is off\n" +
						"-----------------------------------------------------\n",
				"\n-----------------------------------------------------\n" +
						"End Washing Machine Test (2 cycles)\n" +
						"-----------------------------------------------------",
				"fake-clock-uri",
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
						// ============== CYCLE 1 : Lancement direct ==============

						// T=0 min : Allumer la machine
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								START_INSTANT,
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new SwitchOnWashingMachine(t));
									return ret;
								}, (m, t) -> {
								}),
						// T=1 min : Lancer un cycle direct (10 min, 40°C)
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								START_INSTANT.plusSeconds(1 * 60),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new StartWashing(t, 10L, 40.0));
									return ret;
								}, (m, t) -> {
								}),
						// Cycle 1 se termine vers T=96 (chauffage ~85 min + lavage 10 min)
						// -> WashingEnded émis automatiquement

						// ============== CYCLE 2 : Départ différé ==============

						// T=100 min : Programmer un départ différé (machine est idle depuis T=96)
						// delay=5min, washingDuration=10min, targetTemp=60°C
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								START_INSTANT.plusSeconds(100 * 60),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new SetDelayedStart(t, 5L, 10L, 60.0));
									return ret;
								}, (m, t) -> {
								}),
						// T=105 min : Le délai s'écoule, StartWashing émis automatiquement
						// Chauffage à 60°C + lavage (10 min)
						// -> WashingEnded émis automatiquement

						// T=170 min : Extinction de la machine
						new SimulationTestStep(
								WashingMachineUnitTesterModel.URI,
								START_INSTANT.plusSeconds(170 * 60),
								(m, t) -> {
									ArrayList<EventI> ret = new ArrayList<>();
									ret.add(new SwitchOffWashingMachine(t));
									return ret;
								}, (m, t) -> {
								})
				});
	}
}