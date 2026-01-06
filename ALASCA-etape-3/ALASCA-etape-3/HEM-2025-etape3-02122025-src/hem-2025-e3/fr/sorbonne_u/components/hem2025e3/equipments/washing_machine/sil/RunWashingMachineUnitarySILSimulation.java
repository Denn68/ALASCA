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
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterUnitTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.events.SIL_SetPowerHeater;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.events.SIL_SetPowerHeater.PowerValue;
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

public class			RunWashingMachineUnitarySILSimulation
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the acceleration factor used in the real time MIL simulations.	 	*/
	public static final double		ACCELERATION_FACTOR = 3600.0;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= HeaterSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void main(String[] args)
	{
		staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			// the heater state model only exchanges event, an atomic model
			// hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					HeaterStateSILModel.URI,
					RTAtomicModelDescriptor.create(
							HeaterStateSILModel.class,
							HeaterStateSILModel.URI,
							HeaterSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			// the heater models simulating its electricity consumption, its
			// temperatures and the external temperature are atomic HIOA models
			// hence we use an AtomicHIOA_Descriptor(s)
			atomicModelDescriptors.put(
					HeaterElectricitySILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterElectricitySILModel.class,
							HeaterElectricitySILModel.URI,
							HeaterSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HeaterTemperatureSILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterTemperatureSILModel.class,
							HeaterTemperatureSILModel.URI,
							HeaterSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ExternalTemperatureSILModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ExternalTemperatureSILModel.class,
							ExternalTemperatureSILModel.URI,
							HeaterSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			// the heater unit tester model only exchanges event, an
			// atomic model hence we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					HeaterUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							HeaterUnitTesterModel.class,
							HeaterUnitTesterModel.URI,
							HeaterSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(HeaterStateSILModel.URI);
			submodels.add(HeaterElectricitySILModel.URI);
			submodels.add(HeaterTemperatureSILModel.URI);
			submodels.add(ExternalTemperatureSILModel.URI);
			submodels.add(HeaterUnitTesterModel.URI);
			
			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, SIL_SetPowerHeater.class),
				new EventSink[] {
					new EventSink(HeaterStateSILModel.URI, SIL_SetPowerHeater.class)
					});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, SwitchOnHeater.class),
				new EventSink[] {
					new EventSink(HeaterStateSILModel.URI, SwitchOnHeater.class)
					});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, SwitchOffHeater.class),
				new EventSink[] {
					new EventSink(HeaterStateSILModel.URI, SwitchOffHeater.class)
					});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, Heat.class),
				new EventSink[] {
					new EventSink(HeaterStateSILModel.URI, Heat.class)
					});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, DoNotHeat.class),
				new EventSink[] {
					new EventSink(HeaterStateSILModel.URI, DoNotHeat.class)
					});

			connections.put(
				new EventSource(HeaterStateSILModel.URI, SIL_SetPowerHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricitySILModel.URI,
								  SIL_SetPowerHeater.class),
					new EventSink(HeaterTemperatureSILModel.URI,
								  SIL_SetPowerHeater.class)
					});
			connections.put(
				new EventSource(HeaterStateSILModel.URI, SwitchOnHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricitySILModel.URI,
								  SwitchOnHeater.class)
					});
			connections.put(
				new EventSource(HeaterStateSILModel.URI, SwitchOffHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricitySILModel.URI,
								  SwitchOffHeater.class),
					new EventSink(HeaterTemperatureSILModel.URI,
								  SwitchOffHeater.class)
					});
			connections.put(
				new EventSource(HeaterStateSILModel.URI, Heat.class),
				new EventSink[] {
					new EventSink(HeaterElectricitySILModel.URI, Heat.class),
					new EventSink(HeaterTemperatureSILModel.URI, Heat.class)
					});
			connections.put(
				new EventSource(HeaterStateSILModel.URI, DoNotHeat.class),
				new EventSink[] {
					new EventSink(HeaterElectricitySILModel.URI, DoNotHeat.class),
					new EventSink(HeaterTemperatureSILModel.URI, DoNotHeat.class)
					});

			// variable bindings between exporting and importing models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			bindings.put(new VariableSource("externalTemperature",
											Double.class,
											ExternalTemperatureSILModel.URI),
						 new VariableSink[] {
								 new VariableSink("externalTemperature",
										 		  Double.class,
										 		  HeaterTemperatureSILModel.URI)
						 });

			// coupled model descriptor
			coupledModelDescriptors.put(
					HeaterCoupledModel.URI,
					new RTCoupledHIOA_Descriptor(
							HeaterCoupledModel.class,
							HeaterCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings,
							ACCELERATION_FACTOR));

			// simulation architecture
			ArchitectureI architecture =
					new RTArchitecture(
							HeaterCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							HeaterSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;

			// run a CLASSICAL test scenario
			TestScenarioWithSimulation classical = classical();
			System.out.println(classical.beginMessage());
			Map<String, Object> classicalRunParameters =
												new HashMap<String, Object>();
			classical.addToRunParameters(classicalRunParameters);
			se.setSimulationRunParameters(classicalRunParameters);
			Time startTime = classical.getStartTime();
			Duration d = classical.getEndTime().subtract(startTime);
			long realTimeStart = System.currentTimeMillis() + 200;
			se.startRTSimulation(realTimeStart,
								 startTime.getSimulatedTime(),
								 d.getSimulatedDuration());
			long executionDuration =					
				new Double(
						HeaterSimulationConfigurationI.TIME_UNIT.toMillis(1)
							* (d.getSimulatedDuration()/ACCELERATION_FACTOR)).
																	longValue();
			Thread.sleep(executionDuration + 2000L);
			System.out.println(classical.endMessage());
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Test scenarios
	// -------------------------------------------------------------------------

	/** the start instant used in the test scenarios.						*/
	protected static Instant	START_INSTANT =
									Instant.parse("2025-10-20T12:00:00.00Z");
	/** the end instant used in the test scenarios.							*/
	protected static Instant	END_INSTANT =
									Instant.parse("2025-10-20T18:00:00.00Z");
	/** the start time in simulated time, corresponding to
	 *  {@code START_INSTANT}.												*/
	protected static Time		START_TIME = new Time(0.0, TimeUnit.HOURS);

	/** standard test scenario, see Gherkin specification.				 	
	 * @throws VerboseException */
	protected static TestScenarioWithSimulation	classical() throws VerboseException
	{
		return new TestScenarioWithSimulation(
			"-----------------------------------------------------\n" +
			"Classical\n\n" +
			"  Gherkin specification\n\n" +
			"    Feature: heater operation\n\n" +
			"      Scenario: heater switched on\n" +
			"        Given a heater that is off\n" +
			"        When it is switched on\n" +
			"        Then it is on but not heating though set at the highest power level\n" +
			"      Scenario: heater heats\n" +
			"        Given a heater that is on and not heating\n" +
			"        When it is asked to heat\n" +
			"        Then it is on and it heats at the current power level\n" +
			"      Scenario: heater stops heating\n" +
			"        Given a hair dryer that is heating\n" +
			"        When it is asked not to heat\n" +
			"        Then it is on but it stops heating\n" +
			"      Scenario: heater heats\n" +
			"        Given a heater that is on and not heating\n" +
			"        When it is asked to heat\n" +
			"        Then it is on and it heats at the current power level\n" +
			"      Scenario: heater set a different power level\n" +
			"        Given a heater that is heating\n" +
			"        When it is set to a new power level\n" +
			"        Then it is on and it heats at the new power level\n" +
			"      Scenario: hair dryer switched off\n" +
			"        Given a hair dryer that is on\n" +
			"        When it is switched of\n" +
			"        Then it is off\n" +
			"-----------------------------------------------------\n",
			"\n-----------------------------------------------------\n" +
			"End Classical\n" +
			"-----------------------------------------------------",
			"fake-clock-URI",	// for simulation only test scenario, no clock needed
			START_INSTANT,
			END_INSTANT,
			HeaterCoupledModel.URI,
			START_TIME,
			(ts, simParams) -> {
				simParams.put(
					ModelI.createRunParameterName(
						HeaterUnitTesterModel.URI,
						HeaterUnitTesterModel.TEST_SCENARIO_RP_NAME),
					ts);
			},
			new SimulationTestStep[]{
				new SimulationTestStep(
					HeaterUnitTesterModel.URI,
					Instant.parse("2025-10-20T12:30:00.00Z"),
					(m, t) -> {
						ArrayList<EventI> ret = new ArrayList<>();
						ret.add(new SwitchOnHeater(t));
						return ret;
					},
					(m, t) -> {}),
				new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T13:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Heat(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T13:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new DoNotHeat(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Heat(t));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SIL_SetPowerHeater(t,
									   				   new PowerValue(880.0)));
							return ret;
						},
						(m, t) -> {}),
				new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T16:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffHeater(t));
							return ret;
						},
						(m, t) -> {})
			});
	}
}
// -----------------------------------------------------------------------------
