package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.ArrayList;

import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetMediumVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunVacuumCleanerUnitaryMILSimulation</code> runs a unit
 * simulation of the vacuum cleaner with a test scenario.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * @author	Team DeMoh
 */
public class			RunVacuumCleanerUnitaryMILSimulation
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= VacuumCleanerSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void	main(String[] args)
	{
		staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			atomicModelDescriptors.put(
					VacuumCleanerElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							VacuumCleanerElectricityModel.class,
							VacuumCleanerElectricityModel.URI,
							VacuumCleanerSimulationConfigurationI.TIME_UNIT,
							null));

			atomicModelDescriptors.put(
					VacuumCleanerUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							VacuumCleanerUnitTesterModel.class,
							VacuumCleanerUnitTesterModel.URI,
							VacuumCleanerSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(VacuumCleanerElectricityModel.URI);
			submodels.add(VacuumCleanerUnitTesterModel.URI);

			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(VacuumCleanerUnitTesterModel.URI,
									SwitchOnVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SwitchOnVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerUnitTesterModel.URI,
									SwitchOffVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SwitchOffVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerUnitTesterModel.URI,
									SetHighVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetHighVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerUnitTesterModel.URI,
									SetMediumVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetMediumVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerUnitTesterModel.URI,
									SetLowVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetLowVacuumCleaner.class)
					});

			coupledModelDescriptors.put(
					VacuumCleanerCoupledModel.URI,
					new CoupledModelDescriptor(
							VacuumCleanerCoupledModel.class,
							VacuumCleanerCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));

			ArchitectureI architecture =
					new Architecture(
							VacuumCleanerCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							VacuumCleanerSimulationConfigurationI.TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			// run a CLASSICAL test scenario
			CLASSICAL.setUpSimulator(se);
			Time startTime = CLASSICAL.getStartTime();
			Duration d = CLASSICAL.getEndTime().subtract(startTime);
			se.doStandAloneSimulation(startTime.getSimulatedTime(),
									  d.getSimulatedDuration());
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			System.exit(0);
		} catch (Throwable e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Test scenarios
	// -------------------------------------------------------------------------

	protected static Instant	START_INSTANT =
									Instant.parse("2025-10-20T12:00:00.00Z");
	protected static Instant	END_INSTANT =
									Instant.parse("2025-10-20T18:00:00.00Z");
	protected static Time		START_TIME =
									new Time(0.0, TimeUnit.HOURS);

	protected static TestScenario	CLASSICAL =
		new TestScenario(
			"-----------------------------------------------------\n" +
			"Classical Vacuum Cleaner Test\n\n" +
			"  Gherkin specification\n\n" +
			"    Feature: vacuum cleaner operation\n\n" +
			"      Scenario: vacuum cleaner switched on\n" +
			"        Given a vacuum cleaner that is off\n" +
			"        When it is switched on\n" +
			"        Then it is on and medium\n" +
			"      Scenario: vacuum cleaner set high\n" +
			"        Given a vacuum cleaner that is on\n" +
			"        When it is set high\n" +
			"        Then it is on and high\n" +
			"      Scenario: vacuum cleaner set medium\n" +
			"        Given a vacuum cleaner that is on\n" +
			"        When it is set medium\n" +
			"        Then it is on and medium\n" +
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
			START_INSTANT,
			END_INSTANT,
			START_TIME,
			(se, ts) -> { 
				HashMap<String, Object> simParams = new HashMap<>();
				simParams.put(
					ModelI.createRunParameterName(
						VacuumCleanerUnitTesterModel.URI,
						VacuumCleanerUnitTesterModel.TEST_SCENARIO_RP_NAME),
					ts);
				se.setSimulationRunParameters(simParams);
			},
			new SimulationTestStep[]{
				new SimulationTestStep(
					VacuumCleanerUnitTesterModel.URI,
					Instant.parse("2025-10-20T13:00:00.00Z"),
					(m, t) -> {
						ArrayList<EventI> ret = new ArrayList<>();
						ret.add(new SwitchOnVacuumCleaner(t));
						return ret;
					},
					(m, t) -> {}),
				new SimulationTestStep(
						VacuumCleanerUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetHighVacuumCleaner(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						VacuumCleanerUnitTesterModel.URI,
						Instant.parse("2025-10-20T15:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetMediumVacuumCleaner(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						VacuumCleanerUnitTesterModel.URI,
						Instant.parse("2025-10-20T16:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetLowVacuumCleaner(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						VacuumCleanerUnitTesterModel.URI,
						Instant.parse("2025-10-20T17:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffVacuumCleaner(t));
							return ret;
						},
						(m, t) -> {})
			});
}
// -----------------------------------------------------------------------------