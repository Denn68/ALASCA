package fr.sorbonne_u.components.hem2025e3.equipments.kettle;

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

import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleTemperatureI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections.KettleUserJava4InboundPort;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.RegistrationConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.hem.RegistrationOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.hem.HEMCyPhy;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StartKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StopKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleActuatorInboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleSensorDataInboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.HeatingSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.KettleStateSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.KettleTemperatureSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.KettleStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.KettleTemperatureSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events.SIL_SetPowerKettle;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasureI;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.alasca.physical_data.TimedMeasure;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleCyPhy</code> implements a kettle cyber-physical
 * component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The kettle has four possible states: OFF, ON, HEATING, and KEEP_WARM.
 * Unlike the heater which has a target temperature, the kettle heats water
 * to boiling point (100°C) and can optionally keep it warm at around 80°C.
 * </p>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * currentState != null
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-02-03
 * </p>
 * 
 * @author Team DeMoh
 */
// -----------------------------------------------------------------------------
@SIL_Simulation_Architectures({
                @LocalArchitecture(uri = "silUnitTests", rootModelURI = "KettleCoupledModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents()),
                @LocalArchitecture(uri = "silIntegrationTests", rootModelURI = "KettleCoupledModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents(exported = {
                                SwitchOnKettle.class,
                                SIL_SetPowerKettle.class,
                                SwitchOffKettle.class,
                                HeatKettle.class,
                                DoNotHeatKettle.class,
                                StartKeepingWarmKettle.class,
                                StopKeepingWarmKettle.class }))
})
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = { KettleUserJava4CI.class,
                KettleInternalControlCI.class,
                KettleExternalControlJava4CI.class,
                KettleSensorDataCI.KettleSensorOfferedPullCI.class,
                KettleActuatorCI.class })
@RequiredInterfaces(required = { RegistrationCI.class })
// -----------------------------------------------------------------------------
public class KettleCyPhy
                extends AbstractCyPhyComponent
                implements KettleUserI,
                KettleInternalControlI {
        // -------------------------------------------------------------------------
        // Inner interfaces and types
        // -------------------------------------------------------------------------

        /**
         * The enumeration <code>KettleState</code> describes the operation
         * states of the kettle.
         */
        public static enum KettleState {
                /** kettle is off. */
                OFF,
                /** kettle is on, but not heating or keeping warm. */
                ON,
                /** kettle is heating water. */
                HEATING,
                /** kettle is keeping water warm. */
                KEEP_WARM
        }

        // -------------------------------------------------------------------------
        // Constants and variables
        // -------------------------------------------------------------------------

        /** URI of the kettle inbound port used in tests. */
        public static final String REFLECTION_INBOUND_PORT_URI = "Kettle-RIP-URI";

        /** URI of the kettle port for user interactions. */
        public static final String USER_INBOUND_PORT_URI = "KETTLE-USER-INBOUND-PORT-URI";
        /** URI of the kettle port for internal control. */
        public static final String INTERNAL_CONTROL_INBOUND_PORT_URI = "KETTLE-INTERNAL-CONTROL-INBOUND-PORT-URI";
        /** URI of the kettle port for external control. */
        public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI = "KETTLE-EXTERNAL-CONTROL-INBOUND-PORT-URI";
        /** URI of the kettle sensor data inbound port. */
        public static final String SENSOR_INBOUND_PORT_URI = "KETTLE-SENSOR-INBOUND-PORT-URI";
        /** URI of the kettle actuator inbound port. */
        public static final String ACTUATOR_INBOUND_PORT_URI = "KETTLE-ACTUATOR-INBOUND-PORT-URI";

        // ---- Dynamic registration fields ----

        /** UID unique de la bouilloire pour l'enregistrement auprès du HEM. */
        protected static final String KETTLE_UID = "KT10000";
        /** Chemin vers le descripteur XML pour le connecteur dynamique. */
        protected static final String XML_KETTLE_DESCRIPTOR = "hem-adapter/kettleci-descriptor.xml";
        /** Port sortant pour l'enregistrement auprès du HEM. */
        protected RegistrationOutboundPort registrationPort;
        /** Indicateur de connexion effective au port d'enregistrement. */
        protected boolean registrationConnected = false;

        /** temperature unit used in this component. */
        public static final MeasurementUnit TEMPERATURE_UNIT = MeasurementUnit.CELSIUS;
        /** power unit used in this component. */
        public static final MeasurementUnit POWER_UNIT = MeasurementUnit.WATTS;
        /** max power level of the kettle. */
        public static final Measure<Double> MAX_POWER_LEVEL = new Measure<>(2200.0, POWER_UNIT);
        /** boiling temperature of water. */
        public static final double BOILING_TEMPERATURE = 100.0;
        /** keep warm temperature. */
        public static final double KEEP_WARM_TEMPERATURE = 80.0;
        /** initial water temperature. */
        public static final double INITIAL_WATER_TEMPERATURE = 20.0;

        /** fake current temperature, used when testing without simulation. */
        public static final SignalData<Double> FAKE_CURRENT_TEMPERATURE = new SignalData<>(
                        new Measure<>(
                                        20.0,
                                        TEMPERATURE_UNIT));

        /** inbound port offering the <code>KettleUserCI</code> interface. */
        protected KettleUserJava4InboundPort kup;
        /**
         * inbound port offering the <code>KettleInternalControlCI</code>
         * interface.
         */
        protected KettleInternalControlInboundPort kicip;
        /**
         * inbound port offering the <code>KettleExternalControlCI</code>
         * interface.
         */
        protected KettleExternalControlJava4InboundPort kecip;
        /** the inbound port through which the sensors are called. */
        protected KettleSensorDataInboundPort sensorInboundPort;
        /** the inbound port through which the actuators are called. */
        protected KettleActuatorInboundPort actuatorInboundPort;

        /** current state of the kettle. */
        protected KettleState currentState;
        /** current power level of the kettle. */
        protected TimedMeasure<Double> currentPowerLevel;
        /** target temperature for the kettle (for interface compliance). */
        protected TimedMeasure<Double> targetTemperature;

        /** standard target temperature for kettle (boiling point). */
        public static final Measure<Double> STANDARD_TARGET_TEMPERATURE = new Measure<>(BOILING_TEMPERATURE,
                        TEMPERATURE_UNIT);

        /** when true, methods trace their actions. */
        public static boolean VERBOSE = true;
        /** when true, methods provides debugging traces. */
        public static boolean DEBUG = false;
        /** when tracing, x coordinate of the window relative position. */
        public static int X_RELATIVE_POSITION = 0;
        /** when tracing, y coordinate of the window relative position. */
        public static int Y_RELATIVE_POSITION = 0;

        protected static int NUMBER_OF_STANDARD_THREADS = 2;
        protected static int NUMBER_OF_SCHEDULABLE_THREADS = 0;

        public static final String UNIT_TEST_ARCHITECTURE_URI = "silUnitTests";
        public static final String INTEGRATION_TEST_ARCHITECTURE_URI = "silIntegrationTests";
        protected static final String CURRENT_TEMPERATURE_NAME = "currentWaterTemperature";

        /** plug-in holding the local simulation architecture and simulators. */
        protected AtomicSimulatorPlugin asp;
        protected final String localArchitectureURI;
        protected final double accelerationFactor;

        // -------------------------------------------------------------------------
        // Invariants
        // -------------------------------------------------------------------------

        /**
         * return true if the implementation invariants are observed, false
         * otherwise.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * k != null
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @param k instance to be tested.
         * @return true if the implementation invariants are observed, false otherwise.
         */
        protected static boolean implementationInvariants(KettleCyPhy k) {
                assert k != null : new PreconditionException("k != null");

                boolean ret = true;
                ret &= AssertionChecking.checkImplementationInvariant(
                                k.currentState != null,
                                KettleCyPhy.class, k,
                                "k.currentState != null");
                ret &= AssertionChecking.checkImplementationInvariant(
                                k.targetTemperature == null ||
                                                k.targetTemperature.getData() >= MIN_TARGET_TEMPERATURE.getData() &&
                                                                k.targetTemperature.getData() <= MAX_TARGET_TEMPERATURE
                                                                                .getData(),
                                KettleCyPhy.class, k,
                                "targetTemperature == null || targetTemperature.getData() >= "
                                                + "MIN_TARGET_TEMPERATURE.getData() && "
                                                + "targetTemperature.getData() <= MAX_TARGET_TEMPERATURE.getData()");
                ret &= AssertionChecking.checkImplementationInvariant(
                                k.currentPowerLevel == null ||
                                                k.currentPowerLevel.getData() >= 0.0 &&
                                                                k.currentPowerLevel.getData() <= MAX_POWER_LEVEL
                                                                                .getData(),
                                KettleCyPhy.class, k,
                                "currentPowerLevel == null || currentPowerLevel.getData() >= 0.0"
                                                + " && currentPowerLevel.getData() <= MAX_POWER_LEVEL.getData()");
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
         * pre	{@code
         * true
         * }	// no precondition.
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
                                REFLECTION_INBOUND_PORT_URI != null &&
                                                !REFLECTION_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "REFLECTION_INBOUND_PORT_URI != null && "
                                                + "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
                                                !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
                                                + "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
                                                !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&"
                                                + "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                SENSOR_INBOUND_PORT_URI != null &&
                                                !SENSOR_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "SENSOR_INBOUND_PORT_URI != null && "
                                                + "!SENSOR_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                ACTUATOR_INBOUND_PORT_URI != null &&
                                                !ACTUATOR_INBOUND_PORT_URI.isEmpty(),
                                KettleCyPhy.class,
                                "ACTUATOR_INBOUND_PORT_URI != null && "
                                                + "!ACTUATOR_INBOUND_PORT_URI.isEmpty()");
                ret &= AssertionChecking.checkStaticInvariant(
                                X_RELATIVE_POSITION >= 0,
                                KettleCyPhy.class,
                                "X_RELATIVE_POSITION >= 0");
                ret &= AssertionChecking.checkStaticInvariant(
                                Y_RELATIVE_POSITION >= 0,
                                KettleCyPhy.class,
                                "Y_RELATIVE_POSITION >= 0");
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
         * k != null
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @param k instance to be tested.
         * @return true if the invariants are observed, false otherwise.
         */
        protected static boolean invariants(KettleCyPhy k) {
                assert k != null : new PreconditionException("k != null");

                boolean ret = true;
                ret &= staticInvariants();
                return ret;
        }

        // -------------------------------------------------------------------------
        // Constructors
        // -------------------------------------------------------------------------

        protected KettleCyPhy() throws Exception {
                this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
                                EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
                                ACTUATOR_INBOUND_PORT_URI);
        }

        protected KettleCyPhy(
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI) throws Exception {
                this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
                                kettleUserInboundPortURI,
                                kettleInternalControlInboundPortURI,
                                kettleExternalControlInboundPortURI,
                                kettleSensorInboundPortURI,
                                kettleActuatorInboundPortURI);
        }

        protected KettleCyPhy(
                        String reflectionInboundPortURI,
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI) throws Exception {
                super(reflectionInboundPortURI,
                                NUMBER_OF_STANDARD_THREADS,
                                NUMBER_OF_SCHEDULABLE_THREADS);

                this.localArchitectureURI = null;
                this.accelerationFactor = 0.0;

                this.initialise(kettleUserInboundPortURI,
                                kettleInternalControlInboundPortURI,
                                kettleExternalControlInboundPortURI,
                                kettleSensorInboundPortURI,
                                kettleActuatorInboundPortURI);

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");
        }

        // Test execution without simulation

        protected KettleCyPhy(
                        ExecutionMode executionMode,
                        String clockURI) throws Exception {
                this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
                                EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
                                ACTUATOR_INBOUND_PORT_URI, executionMode, clockURI);
        }

        protected KettleCyPhy(
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI,
                        ExecutionMode executionMode,
                        String clockURI) throws Exception {
                this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
                                kettleUserInboundPortURI,
                                kettleInternalControlInboundPortURI,
                                kettleExternalControlInboundPortURI,
                                kettleSensorInboundPortURI,
                                kettleActuatorInboundPortURI,
                                executionMode,
                                clockURI);
        }

        protected KettleCyPhy(
                        String reflectionInboundPortURI,
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI,
                        ExecutionMode executionMode,
                        String clockURI) throws Exception {
                super(reflectionInboundPortURI,
                                NUMBER_OF_STANDARD_THREADS,
                                NUMBER_OF_SCHEDULABLE_THREADS,
                                executionMode,
                                clockURI,
                                null);

                assert executionMode != null &&
                                executionMode.isTestWithoutSimulation()
                                : new PreconditionException(
                                                "executionMode != null && executionMode."
                                                                + "isTestWithoutSimulation()");

                this.localArchitectureURI = null;
                this.accelerationFactor = 0.0;

                this.initialise(kettleUserInboundPortURI,
                                kettleInternalControlInboundPortURI,
                                kettleExternalControlInboundPortURI,
                                kettleSensorInboundPortURI,
                                kettleActuatorInboundPortURI);

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");
        }

        // Tests with simulation

        protected KettleCyPhy(
                        String reflectionInboundPortURI,
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI,
                        ExecutionMode executionMode,
                        TestScenario testScenario,
                        String localArchitectureURI,
                        double accelerationFactor) throws Exception {
                super(reflectionInboundPortURI,
                                NUMBER_OF_STANDARD_THREADS,
                                NUMBER_OF_SCHEDULABLE_THREADS,
                                executionMode,
                                AssertionChecking.assertTrueAndReturnOrThrow(
                                                testScenario != null,
                                                testScenario.getClockURI(),
                                                () -> new PreconditionException("testScenario != null")),
                                testScenario,
                                ((Supplier<Set<String>>) () -> {
                                        HashSet<String> hs = new HashSet<>();
                                        hs.add(UNIT_TEST_ARCHITECTURE_URI);
                                        hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
                                        return hs;
                                }).get(),
                                accelerationFactor);

                assert executionMode != null && executionMode.isSimulationTest() : new PreconditionException(
                                "executionMode != null && "
                                                + "executionMode.isSimulationTest()");

                this.localArchitectureURI = localArchitectureURI;
                this.accelerationFactor = accelerationFactor;

                this.initialise(kettleUserInboundPortURI,
                                kettleInternalControlInboundPortURI,
                                kettleExternalControlInboundPortURI,
                                kettleSensorInboundPortURI,
                                kettleActuatorInboundPortURI);

                if (DEBUG) {
                        this.logMessage("KettleCyPhy local simulation architectures: "
                                        + this.localSimulationArchitectures);
                }

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");
        }

        // -------------------------------------------------------------------------
        // Initialisation methods
        // -------------------------------------------------------------------------

        protected void initialise(
                        String kettleUserInboundPortURI,
                        String kettleInternalControlInboundPortURI,
                        String kettleExternalControlInboundPortURI,
                        String kettleSensorInboundPortURI,
                        String kettleActuatorInboundPortURI) throws Exception {
                this.currentState = KettleState.OFF;
                // Initialize target temperature to boiling point (100°C)
                this.targetTemperature = new TimedMeasure<Double>(100.0, TEMPERATURE_UNIT);

                this.kup = new KettleUserJava4InboundPort(kettleUserInboundPortURI, this);
                this.kup.publishPort();
                this.kicip = new KettleInternalControlInboundPort(
                                kettleInternalControlInboundPortURI, this);
                this.kicip.publishPort();
                this.kecip = new KettleExternalControlJava4InboundPort(
                                kettleExternalControlInboundPortURI, this);
                this.kecip.publishPort();
                this.sensorInboundPort = new KettleSensorDataInboundPort(
                                kettleSensorInboundPortURI, this);
                this.sensorInboundPort.publishPort();
                this.actuatorInboundPort = new KettleActuatorInboundPort(
                                kettleActuatorInboundPortURI, this);
                this.actuatorInboundPort.publishPort();

                if (VERBOSE) {
                        this.tracer.get().setTitle("Kettle component");
                        this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                                        Y_RELATIVE_POSITION);
                        this.toggleTracing();
                }

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");
        }

        @Override
        protected RTArchitecture createLocalSimulationArchitecture(
                        String architectureURI,
                        String rootModelURI,
                        TimeUnit simulatedTimeUnit,
                        double accelerationFactor) throws Exception {
                assert architectureURI != null && !architectureURI.isEmpty() : new PreconditionException(
                                "architectureURI != null && !architectureURI.isEmpty()");
                assert rootModelURI != null && !rootModelURI.isEmpty() : new PreconditionException(
                                "rootModelURI != null && !rootModelURI.isEmpty()");
                assert simulatedTimeUnit != null : new PreconditionException("simulatedTimeUnit != null");
                assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

                RTArchitecture ret = null;
                if (architectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
                        ret = Local_SIL_SimulationArchitectures.createKettleSIL_Architecture4UnitTest(
                                        architectureURI,
                                        rootModelURI,
                                        simulatedTimeUnit,
                                        accelerationFactor);
                } else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
                        ret = Local_SIL_SimulationArchitectures.createKettle_SIL_LocalArchitecture4IntegrationTest(
                                        architectureURI,
                                        rootModelURI,
                                        simulatedTimeUnit,
                                        accelerationFactor);
                } else {
                        throw new BCMException("Unknown local simulation architecture "
                                        + "URI: " + architectureURI);
                }

                return ret;
        }

        // -------------------------------------------------------------------------
        // Component internal methods
        // -------------------------------------------------------------------------

        /**
         * return true if the current power level is equal to {@code powerLevel},
         * otherwise false.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * powerLevel != null
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @param powerLevel a power level to be tested.
         * @return true if the current power level is equal to {@code powerLevel},
         *         otherwise false.
         */
        public boolean isCurrentPowerLevel(MeasureI<Double> powerLevel) {
                assert powerLevel != null : new PreconditionException("powerLevel != null");

                return this.currentPowerLevel.equals(powerLevel);
        }

        /**
         * return true if the current temperature is equal to {@code temperature},
         * otherwise false.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * temperature != null
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @param temperature a temperature to be tested.
         * @return true if the current temperature is equal to {@code temperature},
         *         otherwise false.
         */
        public boolean isCurrentTemperature(Measure<Double> temperature) {
                assert temperature != null : new PreconditionException("temperature != null");

                try {
                        return this.getCurrentTemperature().getMeasure().equals(temperature);
                } catch (Exception e) {
                        throw new BCMRuntimeException(e);
                }
        }

        // -------------------------------------------------------------------------
        // Component life-cycle
        // -------------------------------------------------------------------------

        @Override
        public synchronized void start() throws ComponentStartException {
                super.start();

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");

                try {
                        if (AbstractCVM.isPublishedInLocalRegistry(
                                        HEMCyPhy.REGISTRATION_INBOUND_PORT_URI)) {
                                this.registrationPort = new RegistrationOutboundPort(this);
                                this.registrationPort.publishPort();
                                this.doPortConnection(
                                                this.registrationPort.getPortURI(),
                                                HEMCyPhy.REGISTRATION_INBOUND_PORT_URI,
                                                RegistrationConnector.class
                                                                .getCanonicalName());
                                this.registrationConnected = true;
                        }

                        switch (this.getExecutionMode()) {
                                case STANDARD:
                                case UNIT_TEST:
                                case INTEGRATION_TEST:
                                        break;
                                case UNIT_TEST_WITH_SIL_SIMULATION:
                                case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                                        RTArchitecture architecture = (RTArchitecture) this.localSimulationArchitectures
                                                        .get(this.localArchitectureURI);
                                        this.asp = new RTAtomicSimulatorPlugin() {
                                                private static final long serialVersionUID = 1L;

                                                @Override
                                                public VariableValue<Double> getModelVariableValue(
                                                                String modelURI,
                                                                String name) throws Exception {
                                                        assert modelURI.equals(KettleTemperatureSILModel.URI);
                                                        assert name.equals(CURRENT_TEMPERATURE_NAME);

                                                        return ((KettleTemperatureSILModel) this.atomicSimulators
                                                                        .get(modelURI).getSimulatedModel())
                                                                        .getCurrentTemperature();
                                                }
                                        };
                                        ((RTAtomicSimulatorPlugin) this.asp)
                                                        .setPluginURI(architecture.getRootModelURI());
                                        ((RTAtomicSimulatorPlugin) this.asp).setSimulationArchitecture(architecture);
                                        this.installPlugin(this.asp);
                                        this.asp.createSimulator();
                                        this.asp.setSimulationRunParameters(
                                                        (TestScenarioWithSimulation) this.testScenario,
                                                        new HashMap<>());
                                        break;
                                case UNIT_TEST_WITH_HIL_SIMULATION:
                                case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                                        throw new BCMException("HIL simulation not implemented yet!");
                                default:
                        }
                } catch (Exception e) {
                        throw new ComponentStartException(e);
                }

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");
        }

        @Override
        public void execute() throws Exception {
                this.traceMessage("Kettle CyPhy executes.\n");

                assert KettleCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                                "KettleCyPhy.implementationInvariants(this)");
                assert KettleCyPhy.invariants(this) : new InvariantException("KettleCyPhy.invariants(this)");

                switch (this.getExecutionMode()) {
                        case STANDARD:
                                this.currentPowerLevel = new TimedMeasure<Double>(
                                                MAX_POWER_LEVEL.getData(),
                                                MAX_POWER_LEVEL.getMeasurementUnit());
                                break;
                        case UNIT_TEST:
                        case INTEGRATION_TEST:
                                this.initialiseClock(
                                                ClocksServer.STANDARD_INBOUNDPORT_URI,
                                                this.clockURI);
                                this.currentPowerLevel = new TimedMeasure<Double>(
                                                MAX_POWER_LEVEL.getData(),
                                                MAX_POWER_LEVEL.getMeasurementUnit(),
                                                this.getClock(),
                                                this.getClock().getStartInstant());
                                break;
                        case UNIT_TEST_WITH_SIL_SIMULATION:
                                this.initialiseClock4Simulation(
                                                ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                                                this.clockURI);
                                this.asp.initialiseSimulation(
                                                this.getClock4Simulation().getSimulatedStartTime(),
                                                this.getClock4Simulation().getSimulatedDuration());
                                this.asp.startRTSimulation(
                                                TimeUnit.NANOSECONDS.toMillis(
                                                                this.getClock4Simulation().getStartEpochNanos()),
                                                this.getClock4Simulation().getSimulatedStartTime().getSimulatedTime(),
                                                this.getClock4Simulation().getSimulatedDuration()
                                                                .getSimulatedDuration());
                                this.currentPowerLevel = new TimedMeasure<Double>(
                                                MAX_POWER_LEVEL.getData(),
                                                MAX_POWER_LEVEL.getMeasurementUnit(),
                                                this.getClock4Simulation(),
                                                this.getClock4Simulation().getStartInstant());
                                this.getClock4Simulation().waitUntilEnd();
                                Thread.sleep(200L);
                                this.logMessage(this.asp.getFinalReport().toString());
                                break;
                        case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                                this.initialiseClock4Simulation(
                                                ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                                                this.clockURI);
                                this.currentPowerLevel = new TimedMeasure<Double>(
                                                MAX_POWER_LEVEL.getData(),
                                                MAX_POWER_LEVEL.getMeasurementUnit(),
                                                this.getClock4Simulation(),
                                                this.getClock4Simulation().getStartInstant());
                                break;
                        case UNIT_TEST_WITH_HIL_SIMULATION:
                        case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                                throw new BCMException("HIL simulation not implemented yet!");
                        default:
                }
        }

        @Override
        public synchronized void finalise() throws Exception {
                if (this.registrationConnected) {
                        this.doPortDisconnection(
                                        this.registrationPort.getPortURI());
                        this.registrationConnected = false;
                }
                super.finalise();
        }

        @Override
        public synchronized void shutdown() throws ComponentShutdownException {
                try {
                        this.kup.unpublishPort();
                        this.kicip.unpublishPort();
                        this.kecip.unpublishPort();
                        this.sensorInboundPort.unpublishPort();
                        this.actuatorInboundPort.unpublishPort();
                        if (this.registrationPort != null) {
                                if (this.registrationConnected) {
                                        this.doPortDisconnection(
                                                        this.registrationPort
                                                                        .getPortURI());
                                        this.registrationConnected = false;
                                }
                                if (this.registrationPort.isPublished()) {
                                        this.registrationPort.unpublishPort();
                                }
                        }
                } catch (Throwable e) {
                        throw new ComponentShutdownException(e);
                }
                super.shutdown();
        }

        // -------------------------------------------------------------------------
        // Component services implementation
        // -------------------------------------------------------------------------

        @Override
        public boolean on() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns its state: " +
                                        this.currentState + ".\n");
                }
                return this.currentState != KettleState.OFF;
        }

        @Override
        public void switchOn() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle switches on.\n");
                }

                assert !this.on() : new PreconditionException("!on()");

                this.currentState = KettleState.ON;

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new SwitchOnKettle(t));
                        this.sensorInboundPort.send(
                                        new KettleStateSensorData(this.currentState));
                }

                if (this.registrationConnected) {
                        this.registrationPort.register(
                                        KETTLE_UID,
                                        this.kecip.getPortURI(),
                                        XML_KETTLE_DESCRIPTOR);
                }

                assert this.on() : new PostconditionException("on()");
        }

        @Override
        public void switchOff() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle switches off.\n");
                }

                assert this.on() : new PreconditionException("on()");

                if (this.registrationConnected) {
                        this.registrationPort.unregister(KETTLE_UID);
                }

                if (this.getExecutionMode().isSILTest()) {
                        this.sensorInboundPort.send(
                                        new KettleStateSensorData(KettleState.OFF));
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new SwitchOffKettle(t));
                }

                this.currentState = KettleState.OFF;

                assert !this.on() : new PostconditionException("!on()");
        }

        @Override
        public boolean heating() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns heating status: " +
                                        (this.currentState == KettleState.HEATING) + ".\n");
                }
                return this.currentState == KettleState.HEATING;
        }

        @Override
        public void startHeating() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle starts heating.\n");
                }

                assert this.on() : new PreconditionException("on()");
                assert !this.heating() : new PreconditionException("!heating()");

                this.currentState = KettleState.HEATING;

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new HeatKettle(t));
                }

                assert this.heating() : new PostconditionException("heating()");
        }

        @Override
        public void stopHeating() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle stops heating.\n");
                }

                assert this.on() : new PreconditionException("on()");
                assert this.heating() : new PreconditionException("heating()");

                this.currentState = KettleState.ON;

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new DoNotHeatKettle(t));
                }

                assert !this.heating() : new PostconditionException("!heating()");
        }

        @Override
        public boolean keepingWarm() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns keeping warm status: " +
                                        (this.currentState == KettleState.KEEP_WARM) + ".\n");
                }
                return this.currentState == KettleState.KEEP_WARM;
        }

        @Override
        public void startKeepingWarm() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle starts keeping warm.\n");
                }

                assert this.on() : new PreconditionException("on()");
                assert !this.keepingWarm() : new PreconditionException("!keepingWarm()");

                this.currentState = KettleState.KEEP_WARM;

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new StartKeepingWarmKettle(t));
                }

                assert this.keepingWarm() : new PostconditionException("keepingWarm()");
        }

        @Override
        public void stopKeepingWarm() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle stops keeping warm.\n");
                }

                assert this.on() : new PreconditionException("on()");
                assert this.keepingWarm() : new PreconditionException("keepingWarm()");

                this.currentState = KettleState.ON;

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new StopKeepingWarmKettle(t));
                }

                assert !this.keepingWarm() : new PostconditionException("!keepingWarm()");
        }

        // -------------------------------------------------------------------------
        // KettleUserI - setTargetTemperature
        // -------------------------------------------------------------------------

        @Override
        public void setTargetTemperature(Measure<Double> target)
                        throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle sets a new target "
                                        + "temperature: " + target + ".\n");
                }

                assert target != null &&
                                TEMPERATURE_UNIT.equals(target.getMeasurementUnit())
                                : new PreconditionException(
                                                "target != null && TEMPERATURE_UNIT.equals("
                                                                + "target.getMeasurementUnit())");
                assert target.getData() >= KettleTemperatureI.MIN_TARGET_TEMPERATURE.getData() &&
                                target.getData() <= KettleTemperatureI.MAX_TARGET_TEMPERATURE.getData()
                                : new PreconditionException(
                                                "target.getData() >= MIN_TARGET_TEMPERATURE.getData() "
                                                                + "&& target.getData() <= MAX_TARGET_TEMPERATURE.getData()");

                if (this.executionMode.isStandard() ||
                                this.executionMode.isTestWithoutSimulation()) {
                        this.targetTemperature = new TimedMeasure<Double>(target.getData(),
                                        target.getMeasurementUnit());
                } else {
                        assert this.executionMode.isSimulationTest()
                                        : new BCMException("executionMode.isSimulationTest()");

                        this.targetTemperature = new TimedMeasure<Double>(target.getData(),
                                        target.getMeasurementUnit(),
                                        this.getClock4Simulation());
                }

                assert getTargetTemperature().getMeasure().equals(target) : new PostconditionException(
                                "getTargetTemperature().getMeasure().equals(target)");
        }

        // -------------------------------------------------------------------------
        // KettleTemperatureI - getTargetTemperature
        // -------------------------------------------------------------------------

        @Override
        public SignalData<Double> getTargetTemperature() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns its target temperature "
                                        + this.targetTemperature + ".\n");
                }

                SignalData<Double> ret = null;
                if (this.getExecutionMode().isStandard()) {
                        ret = new SignalData<Double>(this.targetTemperature);
                } else if (this.getExecutionMode().isTestWithoutSimulation()) {
                        assert this.getClock() != null;
                        ret = new SignalData<Double>(this.getClock(),
                                        this.targetTemperature);
                } else {
                        ret = new SignalData<Double>(this.getClock4Simulation(),
                                        this.targetTemperature);
                }

                assert ret != null && TEMPERATURE_UNIT.equals(
                                ret.getMeasure().getMeasurementUnit()) : new PostconditionException(
                                                "return != null && TEMPERATURE_UNIT.equals("
                                                                + "return.getMeasure().getMeasurementUnit())");
                assert ret.getMeasure().getData() >= KettleTemperatureI.MIN_TARGET_TEMPERATURE.getData() &&
                                ret.getMeasure().getData() <= KettleTemperatureI.MAX_TARGET_TEMPERATURE.getData()
                                : new PostconditionException(
                                                "return.getMeasure().getData() >= "
                                                                + "MIN_TARGET_TEMPERATURE.getData() "
                                                                + "&& return.getMeasure().getData() <= "
                                                                + "MAX_TARGET_TEMPERATURE.getData()");

                return ret;
        }

        // -------------------------------------------------------------------------
        // KettleExternalControlI - Power level methods
        // -------------------------------------------------------------------------

        @Override
        public Measure<Double> getMaxPowerLevel() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns its max power level " +
                                        MAX_POWER_LEVEL + ".\n");
                }

                return MAX_POWER_LEVEL;
        }

        @Override
        public void setCurrentPowerLevel(Measure<Double> powerLevel)
                        throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle sets its power level to " +
                                        powerLevel + ".\n");
                }

                assert this.on() : new PreconditionException("on()");
                assert powerLevel != null && powerLevel.getData() >= 0.0 &&
                                powerLevel.getMeasurementUnit().equals(POWER_UNIT)
                                : new PreconditionException(
                                                "powerLevel != null && powerLevel.getData() >= 0.0 && "
                                                                + "powerLevel.getMeasurementUnit().equals(POWER_UNIT)");

                if (powerLevel.getData() <= getMaxPowerLevel().getData()) {
                        this.currentPowerLevel = new TimedMeasure<Double>(
                                        powerLevel.getData(),
                                        powerLevel.getMeasurementUnit());
                } else {
                        this.currentPowerLevel = new TimedMeasure<Double>(
                                        MAX_POWER_LEVEL.getData(),
                                        MAX_POWER_LEVEL.getMeasurementUnit());
                }

                if (this.getExecutionMode().isSILTest()) {
                        ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                                        KettleStateSILModel.URI,
                                        t -> new SIL_SetPowerKettle(t,
                                                        new SIL_SetPowerKettle.PowerValue(powerLevel.getData())));
                }

                assert powerLevel.getData() > getMaxPowerLevel().getData() ||
                                getCurrentPowerLevel().getMeasure().getData() == powerLevel.getData()
                                : new PostconditionException(
                                                "powerLevel.getData() > getMaxPowerLevel().getData() "
                                                                + "|| getCurrentPowerLevel().getData() == "
                                                                + "powerLevel.getData()");
        }

        @Override
        public SignalData<Double> getCurrentPowerLevel() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns its current power level " +
                                        this.currentPowerLevel + ".\n");
                }

                assert this.on() : new PreconditionException("on()");

                SignalData<Double> ret = new SignalData<Double>(this.currentPowerLevel);

                assert ret != null && ret.getMeasure().getMeasurementUnit().equals(POWER_UNIT)
                                : new PreconditionException(
                                                "return != null && return.getMeasure()."
                                                                + "getMeasurementUnit().equals(POWER_UNIT)");
                assert ret.getMeasure().getData() >= 0.0 &&
                                ret.getMeasure().getData() <= getMaxPowerLevel().getData()
                                : new PostconditionException(
                                                "return.getMeasure().getData() >= 0.0 && "
                                                                + "return.getMeasure().getData() <= "
                                                                + "getMaxPowerLevel().getData()");

                return ret;
        }

        @Override
        public SignalData<Double> getCurrentTemperature() throws Exception {
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns its current temperature.\n");
                }

                assert this.on() : new PreconditionException("on()");

                SignalData<Double> currentTemperature = null;
                // Check if we are in SIL mode AND the simulation clock is ready
                if (this.executionMode.isSILTest() &&
                                this.getClock4Simulation() != null &&
                                !this.getClock4Simulation().startTimeNotReached()) {
                        // retrieve the current temperature from the simulator
                        VariableValue<Double> v = this.computeCurrentTemperature();
                        currentTemperature = new SignalData<>(
                                        this.getClock4Simulation(),
                                        new TimedMeasure<Double>(
                                                        v.getValue(),
                                                        TEMPERATURE_UNIT,
                                                        this.getClock4Simulation(),
                                                        this.getClock4Simulation()
                                                                        .instantOfSimulatedTime(v.getTime())));
                } else {
                        // Use fake temperature when not in SIL mode or clock not ready
                        currentTemperature = FAKE_CURRENT_TEMPERATURE;
                }

                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle returns current temperature "
                                        + currentTemperature.getMeasure().getData() + ".\n");
                }

                return currentTemperature;
        }

        @SuppressWarnings("unchecked")
        protected VariableValue<Double> computeCurrentTemperature()
                        throws Exception {
                return (VariableValue<Double>) this.asp.getModelVariableValue(
                                KettleTemperatureSILModel.URI,
                                CURRENT_TEMPERATURE_NAME);
        }

        // -------------------------------------------------------------------------
        // Sensor methods
        // -------------------------------------------------------------------------

        public HeatingSensorData heatingPullSensor() throws Exception {
                return new HeatingSensorData(this.heating());
        }

        public KettleTemperatureSensorData currentTemperaturePullSensor()
                        throws Exception {
                SignalData<Double> sd = this.getCurrentTemperature();
                // Check if SIL mode AND clock is ready
                if (this.executionMode.isSILTest() &&
                                this.getClock4Simulation() != null &&
                                !this.getClock4Simulation().startTimeNotReached()) {
                        return new KettleTemperatureSensorData(
                                        this.getClock4Simulation(),
                                        (TimedMeasure<Double>) sd.getMeasure());
                } else {
                        return new KettleTemperatureSensorData(
                                        (TimedMeasure<Double>) sd.getMeasure());
                }
        }

        public KettleTemperatureSensorData temperatureSensor() throws Exception {
                return this.currentTemperaturePullSensor();
        }

        public void startTemperaturePushSensor(
                        long controlPeriod,
                        TimeUnit tu) throws Exception {
                // Push mode implementation - to be completed as needed
                if (KettleCyPhy.VERBOSE) {
                        this.traceMessage("Kettle starts temperature push sensor with period "
                                        + controlPeriod + " " + tu + ".\n");
                }
        }
}
// -----------------------------------------------------------------------------
