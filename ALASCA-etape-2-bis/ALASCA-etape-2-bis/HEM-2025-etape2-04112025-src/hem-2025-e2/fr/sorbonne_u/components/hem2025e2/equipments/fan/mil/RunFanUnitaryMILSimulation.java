package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.ArrayList;

import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
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
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunFanUnitaryMILSimulation</code> creates a simulator
 * for the fan and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 * <p>Created on : 2023-11-14</p>
 * * @author	Team DeMoh
 */
public class			RunFanUnitaryMILSimulation
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void main(String[] args)
	{
		staticInvariants();

		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					FanElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			atomicModelDescriptors.put(
					FanUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							FanUnitTesterModel.class,
							FanUnitTesterModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.URI);
			submodels.add(FanUnitTesterModel.URI);
			
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SwitchOnFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOnFan.class)
					});

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SwitchOffFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOffFan.class)
					});
			
			connections.put(
					new EventSource(FanUnitTesterModel.URI, SetLowSpeedFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetLowSpeedFan.class)
					});

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SetHighSpeedFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetHighSpeedFan.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					FanCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							FanCoupledModel.class,
							FanCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							null));

			ArchitectureI architecture =
					new Architecture(
							FanCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							FanSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			
			// run the test scenario
			CLASSICAL.setUpSimulator(se);
			Time startTime = CLASSICAL.getStartTime();
			Duration d = CLASSICAL.getEndTime().subtract(startTime);
			se.doStandAloneSimulation(startTime.getSimulatedTime(),
									  d.getSimulatedDuration());
			System.exit(0);
			
		} catch (Exception e) {
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
	protected static Time		START_TIME = new Time(0.0, TimeUnit.HOURS);

	protected static TestScenario	CLASSICAL =
		new TestScenario(
			"-----------------------------------------------------\n" +
			"Classical Fan Test\n" +
			"  Gherkin specification\n\n" +
			"    Feature: fan operation\n\n" +
			"      Scenario: fan switched on\n" +
			"        Given a fan that is off\n" +
			"        When it is switched on\n" +
			"        Then it is on and starts at LOW speed\n" +
			"      Scenario: fan set to high speed\n" +
			"        Given a fan that is on LOW\n" +
			"        When it is set to HIGH speed\n" +
			"        Then it runs at HIGH speed\n" +
			"      Scenario: fan set to low speed\n" +
			"        Given a fan that is on HIGH\n" +
			"        When it is set to LOW speed\n" +
			"        Then it runs at LOW speed\n" +
			"      Scenario: fan switched off\n" +
			"        Given a fan that is on\n" +
			"        When it is switched off\n" +
			"        Then it is off\n" +
			"-----------------------------------------------------\n",
			"\n-----------------------------------------------------\n" +
			"End Classical Fan Test\n" +
			"-----------------------------------------------------",
			START_INSTANT,
			END_INSTANT,
			START_TIME,
			(se, ts) -> { 
				HashMap<String, Object> simParams = new HashMap<>();
				simParams.put(
					ModelI.createRunParameterName(
						FanUnitTesterModel.URI,
						FanUnitTesterModel.TEST_SCENARIO_RP_NAME),
					ts);
				se.setSimulationRunParameters(simParams);
			},
			new SimulationTestStep[]{
				new SimulationTestStep(
					FanUnitTesterModel.URI,
					Instant.parse("2025-10-20T12:30:00.00Z"),
					(m, t) -> {
						ArrayList<EventI> ret = new ArrayList<>();
						ret.add(new SwitchOnFan(t));
						return ret;
					},
					(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T13:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetHighSpeedFan(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetLowSpeedFan(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						FanUnitTesterModel.URI,
						Instant.parse("2025-10-20T15:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffFan(t));
							return ret;
						},
						(m, t) -> {})
			});
}
// -----------------------------------------------------------------------------