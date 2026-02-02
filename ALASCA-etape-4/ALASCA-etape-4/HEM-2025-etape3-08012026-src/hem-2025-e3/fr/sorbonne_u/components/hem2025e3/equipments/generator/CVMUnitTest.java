package fr.sorbonne_u.components.hem2025e3.equipments.generator;

import fr.sorbonne_u.alasca.physical_data.Measure;

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
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorFuelSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorUnitTesterSILModel;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMUnitTest</code> performs unit tests on the hair dryer
 * component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-09-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CVMUnitTest
extends		AbstractCVM
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** delay before starting the test scenarios, leaving time to build
	 *  and initialise the components and their simulators; this delay is
	 *  estimated given the complexity of the initialisation (including the
	 *  creation of the application simulator if simulation is used). It
	 *  could need to be revised if the computer on which the application
	 *  is run is less powerful.											*/
	public static long			DELAY_TO_START = 3000L;
	/** duration of the sleep at the end of the execution before exiting
	 *  the JVM.															*/
	public static long			END_SLEEP_DURATION = 1000000L;

	/** time unit in which {@code SIMULATION_DURATION} is expressed.		*/
	public static TimeUnit		SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/** start time of the simulation, in simulated logical time, if
	 *  relevant.															*/
	public static Time 			SIMULATION_START_TIME =
										new Time(0.0, SIMULATION_TIME_UNIT);
	/** duration  of the simulation, in simulated time.						*/
	public static Duration		SIMULATION_DURATION =
										new Duration(6.0, SIMULATION_TIME_UNIT);
	/** for real time simulations, the acceleration factor applied to the
	 *  the simulated time to get the execution time of the simulations. 	*/
	public static double		ACCELERATION_FACTOR = 360.0;
	/** duration of the execution.											*/
	public static long			EXECUTION_DURATION =
			DELAY_TO_START +
				TimeUnit.NANOSECONDS.toMillis(
						TimeUtils.toNanos(
								SIMULATION_DURATION.getSimulatedDuration()/
													ACCELERATION_FACTOR,
								SIMULATION_DURATION.getTimeUnit()));

	/** the execution mode for the generator component, to select among
	 *  the values of the enumeration {@code ExecutionMode}.				*/
	public static ExecutionMode	GENERATOR_EXECUTION_MODE =
//											ExecutionMode.STANDARD;
//											ExecutionMode.UNIT_TEST;
											ExecutionMode.
												UNIT_TEST_WITH_SIL_SIMULATION;

	/** the execution mode for the generator tester component, to select
	 *  among the values of the enumeration {@code ExecutionMode}.			*/
	public static ExecutionMode	GENERATOR_TESTER_EXECUTION_MODE =
//											ExecutionMode.STANDARD;
											ExecutionMode.UNIT_TEST;

	/** for unit tests and SIL simulation unit tests, a {@code Clock} is
	 *  used to get a time-triggered synchronisation of the actions of
	 *  the components in the test scenarios.								*/
	public static String		CLOCK_URI = "generator-test-clock";
	/** start instant in test scenarios, as a string to be parsed.			*/
	public static String		START_INSTANT = "2026-01-02T08:00:00.00Z";

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				CVMUnitTest() throws Exception
	{
		GeneratorUnitTesterCyphy.VERBOSE = true;
		GeneratorUnitTesterCyphy.X_RELATIVE_POSITION = 0;
		GeneratorUnitTesterCyphy.Y_RELATIVE_POSITION = 1;
		GeneratorCyPhy.VERBOSE = true;
		GeneratorCyPhy.X_RELATIVE_POSITION = 1;
		GeneratorCyPhy.Y_RELATIVE_POSITION = 1;
	}

	// -------------------------------------------------------------------------
	// CVM life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		if (GENERATOR_EXECUTION_MODE.isStandard()) {

			AbstractComponent.createComponent(
				GeneratorCyPhy.class.getCanonicalName(),
				new Object[]{
						GENERATOR_EXECUTION_MODE,
						CLOCK_URI
				});

			AbstractComponent.createComponent(
				GeneratorUnitTesterCyphy.class.getCanonicalName(),
				new Object[]{GeneratorCyPhy.STANDARD_INBOUND_PORT_URI});

		} else if (GENERATOR_EXECUTION_MODE.isTestWithoutSimulation()) {

			long current = System.currentTimeMillis();
			// start time of the components in Unix epoch time in milliseconds.
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			// start instant used for time-triggered synchronisation in unit tests
			// and SIL simulation runs.
			Instant	startInstant = Instant.parse(START_INSTANT);
			// test scenario to be executed for unit tests with simulation
			TestScenario testScenario = unitTestScenario();

			AbstractComponent.createComponent(
					GeneratorCyPhy.class.getCanonicalName(),
					new Object[]{
							GENERATOR_EXECUTION_MODE,
							CLOCK_URI
					});

				AbstractComponent.createComponent(
					GeneratorUnitTesterCyphy.class.getCanonicalName(),
					new Object[]{
							GeneratorCyPhy.STANDARD_INBOUND_PORT_URI,
							GENERATOR_TESTER_EXECUTION_MODE,
							testScenario
					});

				AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[]{
						// URI of the clock to retrieve it
						CLOCK_URI,
						// start time in Unix epoch time
						TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
						// start instant synchronised with the start time
						startInstant,
						ACCELERATION_FACTOR
						});

		} else if (GENERATOR_EXECUTION_MODE.isSILTest()) {

			long current = System.currentTimeMillis();
			// start time of the components in Unix epoch time in milliseconds.
			long unixEpochStartTimeInMillis = current + DELAY_TO_START;
			// start instant used for time-triggered synchronisation in unit tests
			// and SIL simulation runs.
			Instant	startInstant = Instant.parse(START_INSTANT);
			// test scenario to be executed for unit tests with simulation
			TestScenario testScenario = unitTestScenarioWithSimulation();

			AbstractComponent.createComponent(
					GeneratorCyPhy.class.getCanonicalName(),
					new Object[]{
							GeneratorCyPhy.STANDARD_INBOUND_PORT_URI,
							GeneratorCyPhy.MAX_POWER,
							GeneratorCyPhy.TANK_CAPACITY,
							GeneratorCyPhy.MIN_FUEL_CONSUMPTION,
							GeneratorCyPhy.MAX_FUEL_CONSUMPTION,
							GENERATOR_EXECUTION_MODE,
							testScenario,
							GeneratorCyPhy.UNIT_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});

				AbstractComponent.createComponent(
					GeneratorUnitTesterCyphy.class.getCanonicalName(),
					new Object[]{
							GeneratorCyPhy.STANDARD_INBOUND_PORT_URI,
							GENERATOR_TESTER_EXECUTION_MODE,
							testScenario
					});

				AbstractComponent.createComponent(
					ClocksServerWithSimulation.class.getCanonicalName(),
					new Object[]{
							// URI of the clock to retrieve it
							CLOCK_URI,
							// start time in Unix epoch time
							TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
							// start instant synchronised with the start time
							startInstant,
							ACCELERATION_FACTOR,
							DELAY_TO_START,
							SIMULATION_START_TIME,
							SIMULATION_DURATION
							});

		}
		super.deploy();
	}

	public static void		main(String[] args)
	{
		GeneratorCyPhy.VERBOSE = true;
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

	/**
	 * return a test scenario without simulation for testing the generator
	 * component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	a test scenario for the unit testing of the generator component.
	 * @throws VerboseException	<i>to do</i>.
	 */
	public static TestScenario	unitTestScenario() throws VerboseException
	{
		// START_INSTANT = "2026-01-02T08:00:00.00Z"
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
							TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		Instant start = 		Instant.parse("2026-01-02T08:30:00.00Z");
		Instant duringStarted =	Instant.parse("2026-01-02T09:00:00.00Z");
		Instant stop =			Instant.parse("2026-01-02T09:30:00.00Z");
		Instant afterStop =		Instant.parse("2026-01-02T10:00:00.00Z");

		return new TestScenario(
			CLOCK_URI,
			startInstant,
			endInstant,
			new TestStepI[] {
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					start,
					owner ->  {
						try {
							((GeneratorUnitTesterCyphy)owner).getPort().
															startGenerator();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					duringStarted,
					owner ->  {
						try {
							GeneratorUnitTesterCyphy gt =
											(GeneratorUnitTesterCyphy)owner;
							gt.logMessage("state = " + gt.getPort().getState());
							gt.logMessage("power = " + gt.getPort().currentPowerProduction());
							gt.logMessage("fuel level = " + gt.getPort().currentTankLevel());
							gt.logMessage("fuel consumption = " + gt.getPort().currentFuelConsumption());
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					stop,
					owner ->  {
						try {
							((GeneratorUnitTesterCyphy)owner).getPort().
																stopGenerator();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					afterStop,
					owner ->  {
						try {
							GeneratorUnitTesterCyphy gt =
									(GeneratorUnitTesterCyphy)owner;
							gt.logMessage("state = " + gt.getPort().getState());
							gt.logMessage("power = " + gt.getPort().currentPowerProduction());
							gt.logMessage("fuel level = " + gt.getPort().currentTankLevel());
							gt.logMessage("fuel consumption = " + gt.getPort().currentFuelConsumption());
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})
			});
	}

	/**
	 * return a test scenario for testing with SIL simulation the hair dryer
	 * component.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	a test scenario for the unit testing of the hair dryer component.
	 * @throws VerboseException	<i>to do</i>.
	 */
	public static TestScenarioWithSimulation	unitTestScenarioWithSimulation()
	throws VerboseException
	{
		// START_INSTANT = "2026-01-02T08:00:00.00Z"
		Instant startInstant = Instant.parse(START_INSTANT);
		long d = TimeUnit.NANOSECONDS.toSeconds(
							TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = startInstant.plusSeconds(d);

		Instant start = 		Instant.parse("2026-01-02T08:30:00.00Z");
		Instant duringStarted =	Instant.parse("2026-01-02T09:00:00.00Z");
		Instant stop =			Instant.parse("2026-01-02T09:30:00.00Z");
		Instant afterStop =		Instant.parse("2026-01-02T10:00:00.00Z");
		Instant refill =		Instant.parse("2026-01-02T10:30:00.00Z");
		Instant afterRefill =	Instant.parse("2026-01-02T11:00:00.00Z");

		return new TestScenarioWithSimulation(
			CLOCK_URI,
			startInstant,
			endInstant,
			"global-archi", // no global archi in fact
			SIMULATION_START_TIME,
			(ts, simParams) -> {
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.CAPACITY_RP_NAME),
					GeneratorSimulationConfiguration.TANK_CAPACITY);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.INITIAL_LEVEL_RP_NAME),
					GeneratorSimulationConfiguration.INITIAL_TANK_LEVEL);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MIN_FUEL_CONSUMPTION_RP_NAME),
					Generator.MIN_FUEL_CONSUMPTION.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MAX_FUEL_CONSUMPTION_RP_NAME),
					Generator.MAX_FUEL_CONSUMPTION.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.LEVEL_QUANTUM_RP_NAME),
					GeneratorSimulationConfiguration.
											STANDARD_LEVEL_INTEGRATION_QUANTUM);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorFuelSILModel.URI,
						GeneratorFuelSILModel.MAX_OUT_POWER_RP_NAME),
					Generator.MAX_POWER.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorPowerSILModel.URI,
						GeneratorPowerSILModel.MAX_OUT_POWER_RP_NAME),
					Generator.MAX_POWER.getData());
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorUnitTesterSILModel.URI,
						GeneratorUnitTesterSILModel.TEST_SCENARIO_RP_NAME),
					ts);
				simParams.put(
					ModelI.createRunParameterName(
						GeneratorUnitTesterSILModel.URI,
						GeneratorUnitTesterSILModel.INITIAL_LEVEL_RP_NAME),
					GeneratorSimulationConfiguration.INITIAL_TANK_LEVEL);
			},
			new TestStepI[] {
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					start,
					owner ->  {
						try {
							((GeneratorUnitTesterCyphy)owner).getPort().
															startGenerator();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					duringStarted,
					owner ->  {
						try {
							GeneratorUnitTesterCyphy gt =
											(GeneratorUnitTesterCyphy)owner;
							gt.logMessage("state = " + gt.getPort().getState());
							gt.logMessage("power = " + gt.getPort().currentPowerProduction());
							gt.logMessage("fuel level = " + gt.getPort().currentTankLevel());
							gt.logMessage("fuel consumption = " + gt.getPort().currentFuelConsumption());
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					stop,
					owner ->  {
						try {
							((GeneratorUnitTesterCyphy)owner).getPort().
															stopGenerator();
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					afterStop,
					owner ->  {
						try {
							GeneratorUnitTesterCyphy gt =
												(GeneratorUnitTesterCyphy)owner;
							gt.logMessage("state = " + gt.getPort().getState());
							gt.logMessage("power = " + gt.getPort().currentPowerProduction());
							gt.logMessage("fuel level = " + gt.getPort().currentTankLevel());
							gt.logMessage("fuel consumption = " + gt.getPort().currentFuelConsumption());
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					refill,
					owner ->  {
						try {
							((GeneratorUnitTesterCyphy)owner).getPort().
										refillTank(new Measure<Double>(
											10.0, GeneratorCyPhy.CAPACITY_UNIT));
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					}),
				new TestStep(
					CLOCK_URI,
					GeneratorUnitTesterCyphy.REFLECTION_INBOUND_PORT_URI,
					afterRefill,
					owner ->  {
						try {
							GeneratorUnitTesterCyphy gt =
													(GeneratorUnitTesterCyphy)owner;
							gt.logMessage("state = " + gt.getPort().getState());
							gt.logMessage("power = " + gt.getPort().currentPowerProduction());
							gt.logMessage("fuel level = " + gt.getPort().currentTankLevel());
							gt.logMessage("fuel consumption = " + gt.getPort().currentFuelConsumption());
						} catch (Exception e) {
							throw new BCMRuntimeException(e) ;
						}
					})
			});
	}
}
// -----------------------------------------------------------------------------
