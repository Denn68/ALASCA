package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineTemperatureI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineController.ControlMode;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

/**
 * The class <code>CVMUnitTest</code> performs unit tests for the
 * WashingMachineCyPhy component.
 */
public class CVMUnitTest
		extends AbstractCVM {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** Delay before starting the test scenarios. */
	public static long DELAY_TO_START = 8000L; // Increased for initialization with controller
	/** Duration of the sleep at the end of the execution. */
	public static long END_SLEEP_DURATION = 10000L;

	/** Time unit for simulation. */
	public static TimeUnit SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/** Start time of the simulation. */
	public static Time SIMULATION_START_TIME = new Time(0.0, SIMULATION_TIME_UNIT);
	/**
	 * Duration of the simulation (4h suffisent: cycle1 ~0.7h + cycle2 ~3h + marge).
	 */
	public static Duration SIMULATION_DURATION = new Duration(4.0, SIMULATION_TIME_UNIT);
	/** Acceleration factor. */
	public static double ACCELERATION_FACTOR = 1200.0;
	/** Duration of the execution. */
	public static long EXECUTION_DURATION = DELAY_TO_START +
			TimeUnit.NANOSECONDS.toMillis(
					TimeUtils.toNanos(
							SIMULATION_DURATION.getSimulatedDuration() /
									ACCELERATION_FACTOR,
							SIMULATION_DURATION.getTimeUnit()));

	/** Execution mode for the washing machine component. */
	public static ExecutionMode WASHING_MACHINE_EXECUTION_MODE =
			// ExecutionMode.STANDARD;
			// ExecutionMode.UNIT_TEST;
			ExecutionMode.UNIT_TEST_WITH_SIL_SIMULATION;

	/** Execution mode for the tester component. */
	// Note: Tester uses UNIT_TEST because it doesn't need simulation itself
	// Only WashingMachineCyPhy runs the simulation
	public static ExecutionMode TESTER_EXECUTION_MODE =
			// ExecutionMode.STANDARD;
			ExecutionMode.UNIT_TEST;

	/** Clock URI for synchronization. */
	public static String CLOCK_URI = "washing-machine-test-clock";
	/** Start instant - Use a date in 2026 to avoid epoch time issues. */
	public static String START_INSTANT = "2026-02-03T08:00:00.00Z";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMUnitTest() throws Exception {
		WashingMachineTesterCyPhy.VERBOSE = true;
		WashingMachineTesterCyPhy.X_RELATIVE_POSITION = 0;
		WashingMachineTesterCyPhy.Y_RELATIVE_POSITION = 1;
		WashingMachineCyPhy.VERBOSE = true;
		WashingMachineCyPhy.X_RELATIVE_POSITION = 1;
		WashingMachineCyPhy.Y_RELATIVE_POSITION = 1;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void deploy() throws Exception {
		if (WASHING_MACHINE_EXECUTION_MODE.isStandard()) {
			// Standard mode
			AbstractComponent.createComponent(
					WashingMachineCyPhy.class.getCanonicalName(),
					new Object[] {});

			AbstractComponent.createComponent(
					WashingMachineTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI
					});

		} else if (WASHING_MACHINE_EXECUTION_MODE.isTestWithoutSimulation()) {
			// Test without simulation
			long current = System.currentTimeMillis();
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			Instant startInstant = Instant.parse(START_INSTANT);
			TestScenario testScenario = unitTestScenario();

			AbstractComponent.createComponent(
					WashingMachineCyPhy.class.getCanonicalName(),
					new Object[] {
							WASHING_MACHINE_EXECUTION_MODE,
							testScenario.getClockURI()
					});

			AbstractComponent.createComponent(
					WashingMachineTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							TESTER_EXECUTION_MODE,
							testScenario
					});

			AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[] {
							CLOCK_URI,
							TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
							startInstant,
							ACCELERATION_FACTOR
					});

		} else {
			// Test with SIL simulation
			assert WASHING_MACHINE_EXECUTION_MODE.isSimulationTest();

			long current = System.currentTimeMillis();
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			Instant startInstant = Instant.parse(START_INSTANT);
			TestScenario testScenario = unitTestScenarioWithSimulation();

			AbstractComponent.createComponent(
					WashingMachineCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.REFLECTION_INBOUND_PORT_URI,
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.SENSOR_INBOUND_PORT_URI,
							WashingMachineCyPhy.ACTUATOR_INBOUND_PORT_URI,
							WASHING_MACHINE_EXECUTION_MODE,
							testScenario,
							WashingMachineCyPhy.UNIT_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});

			// Create the WashingMachine controller that connects to sensor/actuator ports
			AbstractComponent.createComponent(
					WashingMachineController.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.SENSOR_INBOUND_PORT_URI,
							WashingMachineCyPhy.ACTUATOR_INBOUND_PORT_URI,
							WashingMachineController.STANDARD_CONTROL_PERIOD,
							ControlMode.PULL,
							WASHING_MACHINE_EXECUTION_MODE,
							ACCELERATION_FACTOR
					});

			AbstractComponent.createComponent(
					WashingMachineTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							TESTER_EXECUTION_MODE,
							testScenario
					});

			AbstractComponent.createComponent(
					ClocksServerWithSimulation.class.getCanonicalName(),
					new Object[] {
							CLOCK_URI,
							TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
							startInstant,
							ACCELERATION_FACTOR,
							DELAY_TO_START,
							SIMULATION_START_TIME,
							SIMULATION_DURATION
					});
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

	public static TestScenario unitTestScenario() throws VerboseException {
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
				TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		Instant switchOnInstant = startInstant.plusSeconds(5);
		Instant startWashingInstant = startInstant.plusSeconds(10);
		Instant switchOffInstant = startInstant.plusSeconds(d - 60);

		return new TestScenario(
				CLOCK_URI,
				startInstant,
				endInstant,
				new TestStepI[] {
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								switchOnInstant,
								owner -> {
									try {
										((WashingMachineTesterCyPhy) owner).getWmop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								startWashingInstant,
								owner -> {
									try {
										((WashingMachineTesterCyPhy) owner).getWmop()
												.startWashing(
														30L,
														new Measure<Double>(40.0,
																WashingMachineTemperatureI.TEMPERATURE_UNIT));
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								switchOffInstant,
								owner -> {
									try {
										((WashingMachineTesterCyPhy) owner).getWmop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								})
				});
	}

	public static TestScenarioWithSimulation unitTestScenarioWithSimulation()
			throws VerboseException {
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
				TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		// Scénario complet avec startWashing + delayedStart:
		// CYCLE 1 (startWashing direct):
		// - switchOn à t=1min
		// - startWashing à t=2min (30 min lavage, 40°C)
		// - Chauffage ~10min, lavage 30min → fin vers t=42min
		//
		// CYCLE 2 (delayedStart):
		// - delayedStart à t=60min (délai 10min, 20min lavage, 50°C)
		// - Le lavage démarre à t=70min
		// - Chauffage ~10min, lavage 20min → fin vers t=100min (~1.7h)
		//
		// - switchOff à t=d-1min (fin de simulation)

		Instant switchOnInstant = startInstant.plusSeconds(60); // 1 min simulée
		Instant startWashingInstant = startInstant.plusSeconds(120); // 2 min simulées
		Instant delayedStartInstant = startInstant.plusSeconds(3600); // 1h simulée (après fin cycle 1)
		Instant switchOffInstant = startInstant.plusSeconds(d - 60); // 1 min avant fin

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
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								switchOnInstant,
								owner -> {
									try {
										System.out.println(">>> TEST STEP 1: switchOn");
										((WashingMachineTesterCyPhy) owner).getWmop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								startWashingInstant,
								owner -> {
									try {
										System.out.println(">>> TEST STEP 2: startWashing (30 min, 40°C)");
										// 30 minutes de lavage, 40°C
										((WashingMachineTesterCyPhy) owner).getWmop().startWashing(
												30 * 60 * 1000L, // 30 minutes en ms
												new Measure<Double>(
														40.0,
														WashingMachineTemperatureI.TEMPERATURE_UNIT));
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								delayedStartInstant,
								owner -> {
									try {
										System.out.println(
												">>> TEST STEP 3: delayedStart (délai 10min, 50°C, lavage 20min)");
										// delayedStart(delayMS, target, washingTimeMS)
										((WashingMachineTesterCyPhy) owner).getWmop().delayedStart(
												10 * 60 * 1000L, // délai 10 minutes en ms
												new Measure<Double>(
														50.0,
														WashingMachineTemperatureI.TEMPERATURE_UNIT),
												20 * 60 * 1000L); // lavage 20 minutes en ms
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								switchOffInstant,
								owner -> {
									try {
										System.out.println(">>> TEST STEP 4: switchOff");
										((WashingMachineTesterCyPhy) owner).getWmop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								})
				});
	}
}
