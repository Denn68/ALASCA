package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.ArrayList;
import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SetPowerKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SetPowerKettle.PowerValue;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
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
 * The class <code>RunKettleUnitaryMILSimulation</code> creates a simulator
 * for the kettle and then runs a typical simulation.
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
public class			RunKettleUnitaryMILSimulation
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= KettleSimulationConfigurationI.staticInvariants();
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
					KettleElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							KettleElectricityModel.class,
							KettleElectricityModel.URI,
							KettleSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					KettleTemperatureModel.URI,
					AtomicHIOA_Descriptor.create(
							KettleTemperatureModel.class,
							KettleTemperatureModel.URI,
							KettleSimulationConfigurationI.TIME_UNIT,
							null));
			atomicModelDescriptors.put(
					KettleUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							KettleUnitTesterModel.class,
							KettleUnitTesterModel.URI,
							KettleSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(KettleElectricityModel.URI);
			submodels.add(KettleTemperatureModel.URI);
			submodels.add(KettleUnitTesterModel.URI);
			
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(KettleUnitTesterModel.URI,
									SetPowerKettle.class),
					new EventSink[] {
							new EventSink(KettleElectricityModel.URI,
										  SetPowerKettle.class)
					});
			connections.put(
					new EventSource(KettleUnitTesterModel.URI,
									SwitchOnKettle.class),
					new EventSink[] {
							new EventSink(KettleElectricityModel.URI,
										  SwitchOnKettle.class),
							new EventSink(KettleTemperatureModel.URI,
										  SwitchOnKettle.class)
					});
			connections.put(
					new EventSource(KettleUnitTesterModel.URI,
									SwitchOffKettle.class),
					new EventSink[] {
							new EventSink(KettleElectricityModel.URI,
										  SwitchOffKettle.class),
							new EventSink(KettleTemperatureModel.URI,
										  SwitchOffKettle.class)
					});
			connections.put(
					new EventSource(KettleUnitTesterModel.URI, HeatKettle.class),
					new EventSink[] {
							new EventSink(KettleElectricityModel.URI,
										  HeatKettle.class),
							new EventSink(KettleTemperatureModel.URI,
										  HeatKettle.class)
					});
			connections.put(
					new EventSource(KettleUnitTesterModel.URI, DoNotHeatKettle.class),
					new EventSink[] {
							new EventSink(KettleElectricityModel.URI,
										  DoNotHeatKettle.class),
							new EventSink(KettleTemperatureModel.URI,
										  DoNotHeatKettle.class)
					});

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("currentHeatingPower",
											Double.class,
											KettleElectricityModel.URI),
						 new VariableSink[] {
								 new VariableSink("currentHeatingPower",
										 		  Double.class,
										 		  KettleTemperatureModel.URI)
						 });

			coupledModelDescriptors.put(
					KettleCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							KettleCoupledModel.class,
							KettleCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings));

			ArchitectureI architecture =
					new Architecture(
							KettleCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							KettleSimulationConfigurationI.TIME_UNIT);

			SimulatorI se = architecture.constructSimulator();
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

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
									Instant.parse("2025-10-20T10:00:00.00Z");
	protected static Instant	END_INSTANT =
									Instant.parse("2025-10-20T10:15:00.00Z"); // 15 min scenario
	protected static Time		START_TIME = new Time(0.0, TimeUnit.HOURS);

	protected static TestScenario	CLASSICAL =
		new TestScenario(
			"-----------------------------------------------------\n" +
			"Classical Kettle Test\n" +
			"-----------------------------------------------------\n",
			"\n-----------------------------------------------------\n" +
			"End Classical Kettle Test\n" +
			"-----------------------------------------------------",
			START_INSTANT,
			END_INSTANT,
			START_TIME,
			(se, ts) -> { 
				HashMap<String, Object> simParams = new HashMap<>();
				simParams.put(
					ModelI.createRunParameterName(
						KettleUnitTesterModel.URI,
						KettleUnitTesterModel.TEST_SCENARIO_RP_NAME),
					ts);
				se.setSimulationRunParameters(simParams);
			},
			new SimulationTestStep[]{
				new SimulationTestStep(
					KettleUnitTesterModel.URI,
					Instant.parse("2025-10-20T10:00:00.00Z"),
					(m, t) -> {
						ArrayList<EventI> ret = new ArrayList<>();
						ret.add(new SwitchOnKettle(t));
						return ret;
					},
					(m, t) -> {}),
				new SimulationTestStep(
						KettleUnitTesterModel.URI,
						Instant.parse("2025-10-20T10:01:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new HeatKettle(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						KettleUnitTesterModel.URI,
						Instant.parse("2025-10-20T10:05:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new DoNotHeatKettle(t)); // Triggers Keep Warm
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						KettleUnitTesterModel.URI,
						Instant.parse("2025-10-20T10:10:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffKettle(t));
							return ret;
						},
						(m, t) -> {})
			});
}
// -----------------------------------------------------------------------------