package fr.sorbonne_u.components.hem2025e3;

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

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e2.equipments.batteries.mil.BatteriesSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunIntensityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunRiseAndSetModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunIntensityModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunRiseAndSetModelI;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.BatteriesCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesUnitTesterSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.GeneratorCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorFuelSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorUnitTesterSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.HairDryerCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.HairDryerTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.sil.HairDryerElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.sil.HairDryerStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterController;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterController.ControlMode;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.ExternalTemperatureSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.HeaterElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.HeaterStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.HeaterTemperatureSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.hem.HEMCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.ElectricMeterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.sil.ElectricMeterElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.SolarPanelCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil.SolarPanelPowerSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil.SolarPanelStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineController;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil.WashingMachineElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil.WashingMachineTemperatureSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleController;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.KettleElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.KettleTemperatureSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanController;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanElectricitySILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.VacuumCleanerCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.VacuumCleanerTesterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil.VacuumCleanerStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil.VacuumCleanerElectricitySILModel;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;

// -----------------------------------------------------------------------------
/**
 * The class <code>CVMIntegrationTest</code> defines the integration test
 * for the household energy management example.
 *
 * <p>
 * <strong>Description</strong>
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
 * CLOCK_URI != null && !CLOCK_URI.isEmpty()
 * }
 * invariant	{@code
 * DELAY_TO_START_IN_MILLIS >= 0
 * }
 * invariant	{@code
 * ACCELERATION_FACTOR > 0.0
 * }
 * invariant	{@code
 * START_INSTANT != null
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2021-09-10
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class CVMIntegrationTest
		extends AbstractCVM {
	/**
	 * delay before starting the test scenarios, leaving time to build
	 * and initialise the components and their simulators; this delay is
	 * estimated given the complexity of the initialisation (including the
	 * creation of the application simulator if simulation is used). It
	 * could need to be revised if the computer on which the application
	 * is run is less powerful.
	 */
	public static long DELAY_TO_START = 5000L;
	/**
	 * duration of the sleep at the end of the execution before exiting
	 * the JVM.
	 */
	public static long END_SLEEP_DURATION = 10000000L;

	/** time unit in which {@code SIMULATION_DURATION} is expressed. */
	public static TimeUnit SIMULATION_TIME_UNIT = TimeUnit.HOURS;
	/**
	 * start time of the simulation, in simulated logical time, if
	 * relevant.
	 */
	public static Time SIMULATION_START_TIME = new Time(0.0, SIMULATION_TIME_UNIT);
	/** duration of the simulation, in simulated time. */
	public static Duration SIMULATION_DURATION = new Duration(6.0, SIMULATION_TIME_UNIT);
	/**
	 * for real time simulations, the acceleration factor applied to the
	 * the simulated time to get the execution time of the simulations.
	 */
	public static double ACCELERATION_FACTOR = 360.0;
	/** duration of the execution. */
	public static long EXECUTION_DURATION = DELAY_TO_START +
			TimeUnit.NANOSECONDS.toMillis(
					TimeUtils.toNanos(
							SIMULATION_DURATION.getSimulatedDuration() /
									ACCELERATION_FACTOR,
							SIMULATION_DURATION.getTimeUnit()));

	public static ExecutionMode GLOBAL_EXECUTION_MODE =
			// ExecutionMode.INTEGRATION_TEST;
			ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION;

	/**
	 * for unit tests and SIL simulation unit tests, a {@code Clock} is
	 * used to get a time-triggered synchronisation of the actions of
	 * the components in the test scenarios.
	 */
	public static String CLOCK_URI = "integration-test-clock";
	/** start instant in test scenarios, as a string to be parsed. */
	public static Instant START_INSTANT = Instant.parse("2025-12-02T06:00:00.00Z");

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * cvm != null
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @param cvm instance to be tested.
	 * @return true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean implementationInvariants(CVMIntegrationTest cvm) {
		assert cvm != null : new PreconditionException("cvm != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @return true if the static invariants are observed, false otherwise.
	 */
	public static boolean staticInvariants() {
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				CLOCK_URI != null && !CLOCK_URI.isEmpty(),
				CVMIntegrationTest.class,
				"CLOCK_URI != null && !CLOCK_URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				DELAY_TO_START >= 0,
				CVMIntegrationTest.class,
				"DELAY_TO_START >= 0");
		ret &= AssertionChecking.checkStaticInvariant(
				ACCELERATION_FACTOR > 0.0,
				CVMIntegrationTest.class,
				"ACCELERATION_FACTOR > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
				START_INSTANT != null,
				CVMIntegrationTest.class,
				"START_INSTANT != null");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * cvm != null
	 * }
	 * post	{@code
	 * true
	 * }	// no postcondition.
	 * </pre>
	 *
	 * @param cvm instance to be tested.
	 * @return true if the invariants are observed, false otherwise.
	 */
	protected static boolean invariants(CVMIntegrationTest cvm) {
		assert cvm != null : new PreconditionException("cvm != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public CVMIntegrationTest() throws Exception {
		// Trace and trace window positions
		ClocksServer.VERBOSE = true;
		ClocksServer.X_RELATIVE_POSITION = 0;
		ClocksServer.Y_RELATIVE_POSITION = 0;
		HEMCyPhy.VERBOSE = true;
		HEMCyPhy.X_RELATIVE_POSITION = 0;
		HEMCyPhy.Y_RELATIVE_POSITION = 1;
		ElectricMeterCyPhy.VERBOSE = true;
		ElectricMeterCyPhy.X_RELATIVE_POSITION = 1;
		ElectricMeterCyPhy.Y_RELATIVE_POSITION = 0;
		BatteriesCyPhy.VERBOSE = true;
		BatteriesCyPhy.X_RELATIVE_POSITION = 1;
		BatteriesCyPhy.Y_RELATIVE_POSITION = 1;
		SolarPanelCyPhy.VERBOSE = true;
		SolarPanelCyPhy.X_RELATIVE_POSITION = 2;
		SolarPanelCyPhy.Y_RELATIVE_POSITION = 1;
		GeneratorCyPhy.VERBOSE = true;
		GeneratorCyPhy.X_RELATIVE_POSITION = 3;
		GeneratorCyPhy.Y_RELATIVE_POSITION = 1;
		HairDryerTesterCyPhy.VERBOSE = true;
		HairDryerTesterCyPhy.X_RELATIVE_POSITION = 0;
		HairDryerTesterCyPhy.Y_RELATIVE_POSITION = 2;
		HairDryerCyPhy.VERBOSE = true;
		HairDryerCyPhy.X_RELATIVE_POSITION = 1;
		HairDryerCyPhy.Y_RELATIVE_POSITION = 2;
		HeaterTesterCyPhy.VERBOSE = true;
		HeaterTesterCyPhy.X_RELATIVE_POSITION = 0;
		HeaterTesterCyPhy.Y_RELATIVE_POSITION = 3;
		HeaterCyPhy.VERBOSE = true;
		HeaterCyPhy.X_RELATIVE_POSITION = 1;
		HeaterCyPhy.Y_RELATIVE_POSITION = 3;
		HeaterController.VERBOSE = true;
		HeaterController.X_RELATIVE_POSITION = 2;
		HeaterController.Y_RELATIVE_POSITION = 3;

		// WashingMachine configuration
		WashingMachineTesterCyPhy.VERBOSE = true;
		WashingMachineTesterCyPhy.X_RELATIVE_POSITION = 0;
		WashingMachineTesterCyPhy.Y_RELATIVE_POSITION = 4;
		WashingMachineCyPhy.VERBOSE = true;
		WashingMachineCyPhy.X_RELATIVE_POSITION = 1;
		WashingMachineCyPhy.Y_RELATIVE_POSITION = 4;
		WashingMachineController.VERBOSE = true;
		WashingMachineController.X_RELATIVE_POSITION = 2;
		WashingMachineController.Y_RELATIVE_POSITION = 4;

		// Kettle configuration
		KettleTesterCyPhy.VERBOSE = true;
		KettleTesterCyPhy.X_RELATIVE_POSITION = 3;
		KettleTesterCyPhy.Y_RELATIVE_POSITION = 3;
		KettleCyPhy.VERBOSE = true;
		KettleCyPhy.X_RELATIVE_POSITION = 2;
		KettleCyPhy.Y_RELATIVE_POSITION = 2;
		KettleController.VERBOSE = true;
		KettleController.X_RELATIVE_POSITION = 3;
		KettleController.Y_RELATIVE_POSITION = 2;

		// Fan configuration
		FanTesterCyPhy.VERBOSE = true;
		FanTesterCyPhy.X_RELATIVE_POSITION = 0;
		FanTesterCyPhy.Y_RELATIVE_POSITION = 5;
		FanCyPhy.VERBOSE = true;
		FanCyPhy.X_RELATIVE_POSITION = 1;
		FanCyPhy.Y_RELATIVE_POSITION = 5;

		// VacuumCleaner configuration
		VacuumCleanerTesterCyPhy.VERBOSE = true;
		VacuumCleanerTesterCyPhy.X_RELATIVE_POSITION = 2;
		VacuumCleanerTesterCyPhy.Y_RELATIVE_POSITION = 5;
		VacuumCleanerCyPhy.VERBOSE = true;
		VacuumCleanerCyPhy.X_RELATIVE_POSITION = 3;
		VacuumCleanerCyPhy.Y_RELATIVE_POSITION = 5;

		BatteriesStateSILModel.VERBOSE = false;
		BatteriesStateSILModel.DEBUG = false;
		BatteriesPowerSILModel.VERBOSE = false;
		BatteriesPowerSILModel.DEBUG = false;
		GeneratorStateSILModel.VERBOSE = false;
		GeneratorStateSILModel.DEBUG = false;
		GeneratorFuelSILModel.VERBOSE = false;
		GeneratorFuelSILModel.DEBUG = false;
		GeneratorPowerSILModel.VERBOSE = false;
		GeneratorPowerSILModel.DEBUG = false;
		HairDryerElectricitySILModel.VERBOSE = false;
		HairDryerElectricitySILModel.DEBUG = false;
		HairDryerStateSILModel.VERBOSE = false;
		HairDryerStateSILModel.DEBUG = false;
		HeaterStateSILModel.VERBOSE = false;
		HeaterStateSILModel.DEBUG = false;
		HeaterElectricitySILModel.VERBOSE = false;
		HeaterElectricitySILModel.DEBUG = false;
		HeaterTemperatureSILModel.VERBOSE = false;
		HeaterTemperatureSILModel.DEBUG = false;
		ExternalTemperatureSILModel.VERBOSE = false;
		ExternalTemperatureSILModel.DEBUG = false;
		ElectricMeterElectricitySILModel.VERBOSE = false;
		ElectricMeterElectricitySILModel.DEBUG = true;
		DeterministicSunRiseAndSetModel.VERBOSE = false;
		DeterministicSunRiseAndSetModel.DEBUG = false;
		DeterministicSunIntensityModel.VERBOSE = false;
		DeterministicSunIntensityModel.DEBUG = false;
		SolarPanelStateSILModel.VERBOSE = false;
		SolarPanelStateSILModel.DEBUG = false;
		SolarPanelPowerSILModel.VERBOSE = false;
		SolarPanelPowerSILModel.DEBUG = false;
		// WashingMachine SIL models
		WashingMachineElectricitySILModel.VERBOSE = false;
		WashingMachineTemperatureSILModel.VERBOSE = false;
		// Kettle SIL models
		KettleElectricitySILModel.VERBOSE = false;
		KettleTemperatureSILModel.VERBOSE = false;
		// Fan SIL models
		FanStateSILModel.VERBOSE = false;
		FanElectricitySILModel.VERBOSE = false;
		// VacuumCleaner SIL models
		VacuumCleanerStateSILModel.VERBOSE = false;
		VacuumCleanerElectricitySILModel.VERBOSE = false;

		assert CVMIntegrationTest.implementationInvariants(this) : new InvariantException(
				"CVMIntegrationTest.glassBoxInvariants(this)");
		assert CVMIntegrationTest.invariants(this) : new InvariantException(
				"CVMIntegrationTest.blackBoxInvariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void deploy() throws Exception {
		TestScenario testScenario;

		if (ExecutionMode.INTEGRATION_TEST.equals(GLOBAL_EXECUTION_MODE)) {

			testScenario = integrationWithoutSimulation();
			// start time in Unix epoch time in nanoseconds.
			long unixEpochStartTimeInMillis = System.currentTimeMillis() + DELAY_TO_START;

			AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[] {
							// URI of the clock to retrieve it
							CLOCK_URI,
							// start time in Unix epoch time
							TimeUnit.MILLISECONDS.toNanos(unixEpochStartTimeInMillis),
							START_INSTANT,
							ACCELERATION_FACTOR });

			AbstractComponent.createComponent(
					ElectricMeterCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							CLOCK_URI
					});

			AbstractComponent.createComponent(
					BatteriesCyPhy.class.getCanonicalName(),
					new Object[] {
							BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS,
							BatteriesSimulationConfiguration.NUMBER_OF_CELL_GROUPS_IN_SERIES,
							ExecutionMode.INTEGRATION_TEST,
							CLOCK_URI
					});

			AbstractComponent.createComponent(
					SolarPanelCyPhy.class.getCanonicalName(),
					new Object[] { SolarPanelSimulationConfigurationI.NB_SQUARE_METERS });

			AbstractComponent.createComponent(
					GeneratorCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							CLOCK_URI
					});

			AbstractComponent.createComponent(
					HairDryerCyPhy.class.getCanonicalName(),
					new Object[] { ExecutionMode.INTEGRATION_TEST });
			AbstractComponent.createComponent(
					HairDryerTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							HairDryerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			AbstractComponent.createComponent(
					HeaterCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							testScenario.getClockURI()
					});
			AbstractComponent.createComponent(
					HeaterTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							HeaterCyPhy.USER_INBOUND_PORT_URI,
							HeaterCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							HeaterCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// WashingMachine components
			AbstractComponent.createComponent(
					WashingMachineCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							testScenario.getClockURI()
					});
			AbstractComponent.createComponent(
					WashingMachineController.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.SENSOR_INBOUND_PORT_URI,
							WashingMachineCyPhy.ACTUATOR_INBOUND_PORT_URI
					});
			AbstractComponent.createComponent(
					WashingMachineTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// Kettle components
			AbstractComponent.createComponent(
					KettleCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							testScenario.getClockURI()
					});
			AbstractComponent.createComponent(
					KettleController.class.getCanonicalName(),
					new Object[] {
							KettleCyPhy.SENSOR_INBOUND_PORT_URI,
							KettleCyPhy.ACTUATOR_INBOUND_PORT_URI,
							KettleController.STANDARD_CONTROL_PERIOD,
							KettleController.ControlMode.PULL,
							true // keepWarmAfterBoiling - use StartKeepingWarm
					});
			AbstractComponent.createComponent(
					KettleTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							KettleCyPhy.USER_INBOUND_PORT_URI,
							KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// Fan components
			AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[] {
							FanCyPhy.USER_INBOUND_PORT_URI,
							FanCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							FanCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST
					});
			AbstractComponent.createComponent(
					FanTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							FanCyPhy.USER_INBOUND_PORT_URI,
							FanCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							FanCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// VacuumCleaner components
			AbstractComponent.createComponent(
					VacuumCleanerCyPhy.class.getCanonicalName(),
					new Object[] {
							VacuumCleanerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST
					});
			AbstractComponent.createComponent(
					VacuumCleanerTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							VacuumCleanerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			AbstractComponent.createComponent(
					HEMCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

		} else if (ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION.equals(
				GLOBAL_EXECUTION_MODE)) {

			testScenario = integrationWithSimulation();
			// start time in Unix epoch time in nanoseconds.
			long unixEpochStartTimeInMillis = System.currentTimeMillis() + DELAY_TO_START;

			AbstractComponent.createComponent(
					ClocksServerWithSimulation.class.getCanonicalName(),
					new Object[] {
							// URI of the clock to retrieve it
							CLOCK_URI,
							// start time in Unix epoch time
							TimeUnit.MILLISECONDS.toNanos(
									unixEpochStartTimeInMillis),
							START_INSTANT,
							ACCELERATION_FACTOR,
							DELAY_TO_START,
							SIMULATION_START_TIME,
							SIMULATION_DURATION });

			AbstractComponent.createComponent(
					GlobalSupervisor.class.getCanonicalName(),
					new Object[] {
							testScenario,
							GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI
					});
			AbstractComponent.createComponent(
					CoordinatorComponent.class.getCanonicalName(),
					new Object[] {});

			AbstractComponent.createComponent(
					HEMCyPhy.class.getCanonicalName(),
					new Object[] {
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario
					});
			AbstractComponent.createComponent(
					ElectricMeterCyPhy.class.getCanonicalName(),
					new Object[] {
							ElectricMeterCyPhy.REFLECTION_INBOUND_PORT_URI,
							ElectricMeterCyPhy.ELECTRIC_METER_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							ElectricMeterCyPhy.LOCAL_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});

			AbstractComponent.createComponent(
					BatteriesCyPhy.class.getCanonicalName(),
					new Object[] {
							BatteriesCyPhy.REFLECTION_INBOUND_PORT_URI,
							BatteriesCyPhy.STANDARD_INBOUND_PORT_URI,
							BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS,
							BatteriesSimulationConfiguration.NUMBER_OF_CELL_GROUPS_IN_SERIES,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							BatteriesCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR });

			AbstractComponent.createComponent(
					SolarPanelCyPhy.class.getCanonicalName(),
					new Object[] {
							SolarPanelCyPhy.REFLECTION_INBOUND_PORT_URI,
							SolarPanelCyPhy.STANDARD_INBOUND_PORT_URI,
							SolarPanelSimulationConfigurationI.NB_SQUARE_METERS,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							BatteriesCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});

			AbstractComponent.createComponent(
					GeneratorCyPhy.class.getCanonicalName(),
					new Object[] {
							GeneratorCyPhy.STANDARD_INBOUND_PORT_URI,
							GeneratorCyPhy.MAX_POWER,
							GeneratorCyPhy.TANK_CAPACITY,
							GeneratorCyPhy.MIN_FUEL_CONSUMPTION,
							GeneratorCyPhy.MAX_FUEL_CONSUMPTION,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							GeneratorCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR });

			AbstractComponent.createComponent(
					HairDryerCyPhy.class.getCanonicalName(),
					new Object[] {
							HairDryerCyPhy.REFLECTION_INBOUND_PORT_URI,
							HairDryerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							HairDryerCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					HairDryerTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							HairDryerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			AbstractComponent.createComponent(
					HeaterCyPhy.class.getCanonicalName(),
					new Object[] {
							HeaterCyPhy.REFLECTION_INBOUND_PORT_URI,
							HeaterCyPhy.USER_INBOUND_PORT_URI,
							HeaterCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							HeaterCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							HeaterCyPhy.SENSOR_INBOUND_PORT_URI,
							HeaterCyPhy.ACTUATOR_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							HeaterCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					HeaterController.class.getCanonicalName(),
					new Object[] {
							HeaterCyPhy.SENSOR_INBOUND_PORT_URI,
							HeaterCyPhy.ACTUATOR_INBOUND_PORT_URI,
							HeaterController.STANDARD_HYSTERESIS,
							HeaterController.STANDARD_CONTROL_PERIOD,
							ControlMode.PULL,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					HeaterTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							HeaterCyPhy.USER_INBOUND_PORT_URI,
							HeaterCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							HeaterCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// WashingMachine components
			AbstractComponent.createComponent(
					WashingMachineCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.REFLECTION_INBOUND_PORT_URI,
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.SENSOR_INBOUND_PORT_URI,
							WashingMachineCyPhy.ACTUATOR_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							WashingMachineCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					WashingMachineController.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.SENSOR_INBOUND_PORT_URI,
							WashingMachineCyPhy.ACTUATOR_INBOUND_PORT_URI,
							WashingMachineController.STANDARD_CONTROL_PERIOD,
							WashingMachineController.ControlMode.PULL,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					WashingMachineTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							WashingMachineCyPhy.USER_INBOUND_PORT_URI,
							WashingMachineCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							WashingMachineCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// Kettle components
			AbstractComponent.createComponent(
					KettleCyPhy.class.getCanonicalName(),
					new Object[] {
							KettleCyPhy.REFLECTION_INBOUND_PORT_URI,
							KettleCyPhy.USER_INBOUND_PORT_URI,
							KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							KettleCyPhy.SENSOR_INBOUND_PORT_URI,
							KettleCyPhy.ACTUATOR_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							KettleCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					KettleController.class.getCanonicalName(),
					new Object[] {
							KettleCyPhy.SENSOR_INBOUND_PORT_URI,
							KettleCyPhy.ACTUATOR_INBOUND_PORT_URI,
							KettleController.STANDARD_CONTROL_PERIOD,
							KettleController.ControlMode.PULL,
							true,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					KettleTesterCyPhy.class.getCanonicalName(),
					new Object[] {
							KettleCyPhy.USER_INBOUND_PORT_URI,
							KettleCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							KettleCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST,
							testScenario
					});

			// Fan components
			AbstractComponent.createComponent(
					FanCyPhy.class.getCanonicalName(),
					new Object[] {
							FanCyPhy.REFLECTION_INBOUND_PORT_URI,
							FanCyPhy.USER_INBOUND_PORT_URI,
							FanCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
							FanCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
							FanCyPhy.SENSOR_INBOUND_PORT_URI,
							FanCyPhy.ACTUATOR_INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							FanCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			AbstractComponent.createComponent(
					FanController.class.getCanonicalName(),
					new Object[] {
							FanCyPhy.SENSOR_INBOUND_PORT_URI,
							FanCyPhy.ACTUATOR_INBOUND_PORT_URI,
							FanController.STANDARD_CONTROL_PERIOD,
							FanController.ControlMode.PULL,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							ACCELERATION_FACTOR
					});
			// AbstractComponent.createComponent(
			// FanTesterCyPhy.class.getCanonicalName(),
			// new Object[] {
			// FanCyPhy.USER_INBOUND_PORT_URI,
			// FanCyPhy.INTERNAL_CONTROL_INBOUND_PORT_URI,
			// FanCyPhy.EXTERNAL_CONTROL_INBOUND_PORT_URI,
			// ExecutionMode.INTEGRATION_TEST,
			// testScenario
			// });

			// VacuumCleaner components
			AbstractComponent.createComponent(
					VacuumCleanerCyPhy.class.getCanonicalName(),
					new Object[] {
							VacuumCleanerCyPhy.REFLECTION_INBOUND_PORT_URI,
							VacuumCleanerCyPhy.INBOUND_PORT_URI,
							ExecutionMode.INTEGRATION_TEST_WITH_SIL_SIMULATION,
							testScenario,
							VacuumCleanerCyPhy.INTEGRATION_TEST_ARCHITECTURE_URI,
							ACCELERATION_FACTOR
					});
			// integrationWithSimulation() ne contient pas de TestSteps pour le VC
			// AbstractComponent.createComponent(
			// VacuumCleanerTesterCyPhy.class.getCanonicalName(),
			// new Object[] {
			// VacuumCleanerCyPhy.INBOUND_PORT_URI,
			// ExecutionMode.INTEGRATION_TEST,
			// testScenario
			// });

		}

		super.deploy();
	}

	// -------------------------------------------------------------------------
	// Executing
	// -------------------------------------------------------------------------

	public static void main(String[] args) {
		VerboseException.VERBOSE = true;
		VerboseException.PRINT_STACK_TRACE = true;
		try {
			CVMIntegrationTest cvm = new CVMIntegrationTest();
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
	 * return a test scenario for the integration testing without simulation of
	 * the HEM application.
	 * 
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * 
	 * </p>
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * true
	 * }	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return a test scenario for the integration testing of the HEM application.
	 * @throws Exception <i>to do</i>.
	 */
	public static TestScenario integrationWithoutSimulation()
			throws Exception {
		long d = TimeUnit.NANOSECONDS.toSeconds(
				TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = START_INSTANT.plusSeconds(d);

		Instant heaterSwitchOn = START_INSTANT.plusSeconds(60);

		Instant hemTestMeter = START_INSTANT.plusSeconds(120);
		Instant hemTestBatteries = START_INSTANT.plusSeconds(180);
		Instant hemTestSolarPanel = START_INSTANT.plusSeconds(240);
		Instant hemTestGenerator = START_INSTANT.plusSeconds(300);

		// WashingMachine test instants
		Instant washingMachineSwitchOn = START_INSTANT.plusSeconds(360);
		Instant washingMachineStartWashing = START_INSTANT.plusSeconds(420);
		Instant hemTestWashingMachine = START_INSTANT.plusSeconds(500); // After startWashing, before switchOff
		Instant washingMachineSwitchOff = START_INSTANT.plusSeconds(540);

		// Kettle test instants
		Instant kettleSwitchOn = START_INSTANT.plusSeconds(1800);
		Instant hemTestKettle = START_INSTANT.plusSeconds(2000);
		Instant kettleSwitchOff = START_INSTANT.plusSeconds(2400);

		// Fan test instants
		Instant fanSwitchOn = START_INSTANT.plusSeconds(2700);
		Instant fanSetHigh = START_INSTANT.plusSeconds(2760);
		Instant hemTestFan = START_INSTANT.plusSeconds(2800);
		Instant fanSetLow = START_INSTANT.plusSeconds(2820);
		Instant fanSwitchOff = START_INSTANT.plusSeconds(2880);

		// VacuumCleaner test instants
		Instant vacuumCleanerTurnOn = START_INSTANT.plusSeconds(3000);
		Instant vacuumCleanerSetHigh = START_INSTANT.plusSeconds(3060);
		Instant vacuumCleanerSetLow = START_INSTANT.plusSeconds(3120);
		Instant vacuumCleanerTurnOff = START_INSTANT.plusSeconds(3180);

		Instant hairDryerTurnOn = START_INSTANT.plusSeconds(600);
		Instant hairDryerSetHigh = START_INSTANT.plusSeconds(660);
		Instant hairDryerSetLow = START_INSTANT.plusSeconds(900);
		Instant hairDryerTurnOff = START_INSTANT.plusSeconds(1200);

		Instant hemTestHeater = START_INSTANT.plusSeconds(1500);

		Instant heaterSwitchOff = START_INSTANT.plusSeconds(d - 60);

		return new TestScenario(
				CLOCK_URI,
				START_INSTANT,
				endInstant,
				new TestStepI[] {
						new TestStep(
								CLOCK_URI,
								HeaterTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								heaterSwitchOn,
								owner -> {
									try {
										((HeaterTesterCyPhy) owner).getHop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// HEM test the meter
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestMeter,
								owner -> {
									try {
										((HEMCyPhy) owner).testMeter();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the batteries
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestBatteries,
								owner -> {
									try {
										((HEMCyPhy) owner).testBatteries();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the solar panel
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestSolarPanel,
								owner -> {
									try {
										((HEMCyPhy) owner).testSolarPanel();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the generator
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestGenerator,
								owner -> {
									try {
										((HEMCyPhy) owner).testGenerator();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// WashingMachine test steps
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								washingMachineSwitchOn,
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
								washingMachineStartWashing,
								owner -> {
									try {
										// Start washing for 10 minutes (600000 ms) at 40°C
										((WashingMachineTesterCyPhy) owner).getWmop().startWashing(
												600000L,
												new Measure<Double>(40.0, MeasurementUnit.CELSIUS));
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the washing machine (while machine is ON)
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestWashingMachine,
								owner -> {
									try {
										((HEMCyPhy) owner).testWashingMachine();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								washingMachineSwitchOff,
								owner -> {
									try {
										((WashingMachineTesterCyPhy) owner).getWmop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// Hair dryer test steps
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerTurnOn,
								owner -> {
									try {
										((HairDryerTesterCyPhy) owner).turnOnHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerSetHigh,
								owner -> {
									try {
										((HairDryerTesterCyPhy) owner).setHighHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerSetLow,
								owner -> {
									try {
										((HairDryerTesterCyPhy) owner).setLowHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerTurnOff,
								owner -> {
									try {
										((HairDryerTesterCyPhy) owner).turnOffHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// HEM test the heater
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestHeater,
								owner -> {
									try {
										((HEMCyPhy) owner).testHeater();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// Kettle test steps
						new TestStep(
								CLOCK_URI,
								KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								kettleSwitchOn,
								owner -> {
									try {
										((KettleTesterCyPhy) owner).getKop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the kettle (while kettle is ON)
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestKettle,
								owner -> {
									try {
										((HEMCyPhy) owner).testKettle();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								kettleSwitchOff,
								owner -> {
									try {
										((KettleTesterCyPhy) owner).getKop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// Fan test steps
						new TestStep(
								CLOCK_URI,
								FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								fanSwitchOn,
								owner -> {
									try {
										((FanTesterCyPhy) owner).switchOnFan();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								fanSetHigh,
								owner -> {
									try {
										((FanTesterCyPhy) owner).setHighSpeedFan();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// HEM test the fan
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								hemTestFan,
								owner -> {
									try {
										((HEMCyPhy) owner).testFan();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								fanSetLow,
								owner -> {
									try {
										((FanTesterCyPhy) owner).setLowSpeedFan();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								FanTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								fanSwitchOff,
								owner -> {
									try {
										((FanTesterCyPhy) owner).switchOffFan();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// VacuumCleaner test steps
						new TestStep(
								CLOCK_URI,
								VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								vacuumCleanerTurnOn,
								owner -> {
									try {
										((VacuumCleanerTesterCyPhy) owner).turnOnVacuumCleaner();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								vacuumCleanerSetHigh,
								owner -> {
									try {
										((VacuumCleanerTesterCyPhy) owner).setHighVacuumCleaner();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								vacuumCleanerSetLow,
								owner -> {
									try {
										((VacuumCleanerTesterCyPhy) owner).setLowVacuumCleaner();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						new TestStep(
								CLOCK_URI,
								VacuumCleanerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								vacuumCleanerTurnOff,
								owner -> {
									try {
										((VacuumCleanerTesterCyPhy) owner).turnOffVacuumCleaner();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						new TestStep(
								CLOCK_URI,
								HeaterTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								heaterSwitchOff,
								owner -> {
									try {
										((HeaterTesterCyPhy) owner).getHop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								})
				});
	}

	/**
	 * return a test scenario for the integration testing with simulation of the
	 * HEM application.
	 * 
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * 
	 * </p>
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * true
	 * }	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return a test scenario for the integration testing with simulation of the
	 *         HEM application.
	 * @throws Exception <i>to do</i>.
	 */
	public static TestScenarioWithSimulation integrationWithSimulation()
			throws Exception {
		// START_INSTANT = "2025-12-02T06:00:00.00Z"
		long d = TimeUnit.NANOSECONDS.toSeconds(
				TimeUtils.toNanos(SIMULATION_DURATION));
		Instant endInstant = START_INSTANT.plusSeconds(d);

		// ====================================================================
		// SCÉNARIO DE TEST manageEnergy() — Suspend / Resume WashingMachine
		// ====================================================================
		// Objectif : démontrer que le HEM peut suspendre la WashingMachine
		// quand la consommation dépasse la production, et la reprendre
		// quand la production redevient suffisante.
		//
		// Timeline :
		// 06:01 WashingMachine ON
		// 06:02 startWashing (3h, 50°C) → HEATING_WATER (9.09A)
		// 06:15 manageEnergy #1 → ~9A conso vs ~9.5A prod → OK
		// 06:30 Heater ON → +10A → total ~19A vs ~10.5A → DEFICIT
		// 06:35 manageEnergy #2 → SUSPEND WM
		// 06:50 manageEnergy #3 → WM toujours suspendue, Heater seul ~10A
		// 07:00 Heater OFF → conso chute
		// 07:05 manageEnergy #4 → surplus → RESUME WM
		// 07:30 HairDryer ON HIGH → +5A
		// 07:31 Kettle ON → +10A → total ~17A vs ~14A → DEFICIT
		// 07:35 manageEnergy #5 → SUSPEND WM
		// 07:50 HairDryer OFF
		// 07:51 Kettle OFF → conso chute
		// 07:55 manageEnergy #6 → surplus → RESUME WM
		// 09:00 WashingMachine OFF
		// ====================================================================

		// WashingMachine : long cycle de 06:01 à 09:00
		Instant washingMachineSwitchOn = Instant.parse("2025-12-02T06:01:00.00Z");
		Instant washingMachineStartWashing = Instant.parse("2025-12-02T06:02:00.00Z");
		Instant washingMachineSwitchOff = Instant.parse("2025-12-02T09:00:00.00Z");

		// manageEnergy appels planifiés
		Instant manageEnergy1 = Instant.parse("2025-12-02T06:15:00.00Z");
		Instant manageEnergy2 = Instant.parse("2025-12-02T06:35:00.00Z");
		Instant manageEnergy3 = Instant.parse("2025-12-02T06:50:00.00Z");
		Instant manageEnergy4 = Instant.parse("2025-12-02T07:05:00.00Z");
		Instant manageEnergy5 = Instant.parse("2025-12-02T07:35:00.00Z");
		Instant manageEnergy6 = Instant.parse("2025-12-02T07:55:00.00Z");

		// Phase 1 de déficit : Heater ON de 06:30 à 07:00
		Instant heaterSwitchOn = Instant.parse("2025-12-02T06:30:00.00Z");
		Instant heaterSwitchOff = Instant.parse("2025-12-02T07:00:00.00Z");

		// Phase 2 de déficit : HairDryer + Kettle de 07:30 à 07:50
		Instant hairDryerTurnOn1 = Instant.parse("2025-12-02T07:30:00.00Z");
		Instant hairDryerSetHigh1 = Instant.parse("2025-12-02T07:30:20.00Z");
		Instant kettleSwitchOn = Instant.parse("2025-12-02T07:31:00.00Z");
		Instant hairDryerTurnOff1 = Instant.parse("2025-12-02T07:50:00.00Z");
		Instant kettleSwitchOff = Instant.parse("2025-12-02T07:51:00.00Z");

		return new TestScenarioWithSimulation(
				CLOCK_URI,
				START_INSTANT,
				endInstant,
				GlobalSupervisor.SIL_SIM_ARCHITECTURE_URI,
				new Time(0.0, TimeUnit.HOURS),
				(ts, simParams) -> {
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesPowerSILModel.URI,
									BatteriesPowerSILModel.CAPACITY_RP_NAME),
							BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
									* BatteriesSimulationConfiguration.NUMBER_OF_CELL_GROUPS_IN_SERIES
									* Batteries.CAPACITY_PER_UNIT.getData());
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesPowerSILModel.URI,
									BatteriesPowerSILModel.IN_POWER_RP_NAME),
							BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
									* Batteries.IN_POWER_PER_CELL.getData());
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesPowerSILModel.URI,
									BatteriesPowerSILModel.MAX_OUT_POWER_RP_NAME),
							BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
									* Batteries.MAX_OUT_POWER_PER_CELL.getData());
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesPowerSILModel.URI,
									BatteriesPowerSILModel.LEVEL_QUANTUM_RP_NAME),
							BatteriesSimulationConfiguration.STANDARD_LEVEL_INTEGRATION_QUANTUM);
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesPowerSILModel.URI,
									BatteriesPowerSILModel.INITIAL_LEVEL_RATIO_RP_NAME),
							BatteriesSimulationConfiguration.INITIAL_BATTERIES_LEVEL_RATIO);
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesStateSILModel.URI,
									BatteriesPowerSILModel.INITIAL_LEVEL_RATIO_RP_NAME),
							BatteriesSimulationConfiguration.INITIAL_BATTERIES_LEVEL_RATIO);
					simParams.put(
							ModelI.createRunParameterName(
									BatteriesUnitTesterSILModel.URI,
									BatteriesUnitTesterSILModel.TEST_SCENARIO_RP_NAME),
							ts);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunRiseAndSetModel.URI,
									SunRiseAndSetModelI.LATITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LATITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunRiseAndSetModel.URI,
									SunRiseAndSetModelI.LONGITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LONGITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunRiseAndSetModel.URI,
									SunRiseAndSetModelI.START_INSTANT_RP_NAME),
							START_INSTANT);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunRiseAndSetModel.URI,
									SunRiseAndSetModelI.ZONE_ID_RP_NAME),
							SolarPanelSimulationConfigurationI.ZONE);

					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.LATITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LATITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.LONGITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LONGITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.START_INSTANT_RP_NAME),
							START_INSTANT);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.ZONE_ID_RP_NAME),
							SolarPanelSimulationConfigurationI.ZONE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.SLOPE_RP_NAME),
							SolarPanelSimulationConfigurationI.SLOPE);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.ORIENTATION_RP_NAME),
							SolarPanelSimulationConfigurationI.ORIENTATION);
					simParams.put(
							ModelI.createRunParameterName(
									DeterministicSunIntensityModel.URI,
									SunIntensityModelI.COMPUTATION_STEP_RP_NAME),
							0.25);

					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.LATITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LATITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.LONGITUDE_RP_NAME),
							SolarPanelSimulationConfigurationI.LONGITUDE);
					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.START_INSTANT_RP_NAME),
							START_INSTANT);
					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.ZONE_ID_RP_NAME),
							SolarPanelSimulationConfigurationI.ZONE);
					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.MAX_POWER_RP_NAME),
							SolarPanelSimulationConfigurationI.NB_SQUARE_METERS *
									SolarPanel.CAPACITY_PER_SQUARE_METER.getData());
					simParams.put(
							ModelI.createRunParameterName(
									SolarPanelPowerSILModel.URI,
									SolarPanelPowerSILModel.COMPUTATION_STEP_RP_NAME),
							0.10);

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
							GeneratorSimulationConfiguration.STANDARD_LEVEL_INTEGRATION_QUANTUM);
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
						// ============================================================
						// SCÉNARIO manageEnergy() — Test Suspend / Resume
						// ============================================================

						// 06:01 - WashingMachine ON
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								washingMachineSwitchOn,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 06:01 - WashingMachine switchOn");
										((WashingMachineTesterCyPhy) owner).getWmop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 06:02 - WashingMachine startWashing (3h = 10800000ms, 50°C)
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								washingMachineStartWashing,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 06:02 - WashingMachine startWashing (3h, 50°C)");
										((WashingMachineTesterCyPhy) owner).getWmop().startWashing(
												10800000L,
												new Measure<Double>(50.0, MeasurementUnit.CELSIUS));
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 06:15 - manageEnergy #1 : WM heating seule (~9A) vs prod (~9.5A) → OK
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy1,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 06:15 - manageEnergy #1 (WM seule, devrait être OK)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 06:30 - Heater ON → ajoute ~10A → déficit
						new TestStep(
								CLOCK_URI,
								HeaterTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								heaterSwitchOn,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 06:30 - Heater switchOn (+10A → DEFICIT attendu)");
										((HeaterTesterCyPhy) owner).getHop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 06:35 - manageEnergy #2 : ~19A vs ~10.7A → DEFICIT → SUSPEND WM ★
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy2,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 06:35 - manageEnergy #2 (Heater+WM → devrait SUSPEND)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 06:50 - manageEnergy #3 : WM suspendue, Heater seul ~10A vs ~11.5A
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy3,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 06:50 - manageEnergy #3 (vérif WM toujours suspendue)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:00 - Heater OFF → consommation chute
						new TestStep(
								CLOCK_URI,
								HeaterTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								heaterSwitchOff,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 07:00 - Heater switchOff (consommation chute)");
										((HeaterTesterCyPhy) owner).getHop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:05 - manageEnergy #4 : surplus → RESUME WM ★
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy4,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 07:05 - manageEnergy #4 (surplus → devrait RESUME)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:30 - HairDryer ON HIGH → +5A
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerTurnOn1,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 07:30 - HairDryer ON");
										((HairDryerTesterCyPhy) owner).turnOnHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
						// 07:30:20 - HairDryer setHigh
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerSetHigh1,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 07:30:20 - HairDryer setHigh (+5A)");
										((HairDryerTesterCyPhy) owner).setHighHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:31 - Kettle ON → +10A → gros déficit
						new TestStep(
								CLOCK_URI,
								KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								kettleSwitchOn,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 07:31 - Kettle ON (+10A → gros DEFICIT attendu)");
										((KettleTesterCyPhy) owner).getKop().switchOn();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:35 - manageEnergy #5 : ~16A+ vs ~14A → DEFICIT → SUSPEND WM ★
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy5,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 07:35 - manageEnergy #5 (HairDryer+Kettle+WM → devrait SUSPEND)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:50 - HairDryer OFF
						new TestStep(
								CLOCK_URI,
								HairDryerTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								hairDryerTurnOff1,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 07:50 - HairDryer OFF");
										((HairDryerTesterCyPhy) owner).turnOffHairDryer();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:51 - Kettle OFF → consommation chute
						new TestStep(
								CLOCK_URI,
								KettleTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								kettleSwitchOff,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 07:51 - Kettle OFF (consommation chute)");
										((KettleTesterCyPhy) owner).getKop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 07:55 - manageEnergy #6 : surplus → RESUME WM ★
						new TestStep(
								CLOCK_URI,
								HEMCyPhy.REFLECTION_INBOUND_PORT_URI,
								manageEnergy6,
								owner -> {
									try {
										System.out.println(
												">>>>>> [SCENARIO] 07:55 - manageEnergy #6 (surplus → devrait RESUME)");
										((HEMCyPhy) owner).manageEnergy();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),

						// 09:00 - WashingMachine OFF (fin)
						new TestStep(
								CLOCK_URI,
								WashingMachineTesterCyPhy.REFLECTION_INBOUND_PORT_URI,
								washingMachineSwitchOff,
								owner -> {
									try {
										System.out.println(">>>>>> [SCENARIO] 09:00 - WashingMachine switchOff (FIN)");
										((WashingMachineTesterCyPhy) owner).getWmop().switchOff();
									} catch (Exception e) {
										throw new BCMRuntimeException(e);
									}
								}),
				});
	}
}
// -----------------------------------------------------------------------------
