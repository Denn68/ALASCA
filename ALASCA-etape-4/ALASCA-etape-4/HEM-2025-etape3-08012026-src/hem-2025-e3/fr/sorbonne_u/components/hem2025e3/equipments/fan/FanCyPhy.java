package fr.sorbonne_u.components.hem2025e3.equipments.fan;

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
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.RTAtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulation;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.connections.FanUserJava4InboundPort;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanActuatorInboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanSensorDataInboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.FanStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.alasca.physical_data.TimedMeasure;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanCyPhy</code> implements the cyber-physical component
 * for a fan in the HEM application.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The fan is a controllable appliance that can be switched on and off, with
 * low and high speed modes. It provides sensor data (speed state) and
 * actuator interfaces for external control by a {@code FanController}.
 * </p>
 * 
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * currentSpeed != null
 * }
 * invariant	{@code
 * currentPowerLevel == null || currentPowerLevel.getMeasurementUnit().equals(POWER_UNIT)
 * }
 * invariant	{@code
 * currentPowerLevel == null
 *         || currentPowerLevel.getData() >= 0.0 && currentPowerLevel.getData() <= MAX_POWER_LEVEL.getData()
 * }
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * REFLECTION_INBOUND_PORT_URI != null && !REFLECTION_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * USER_INBOUND_PORT_URI != null && !USER_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * INTERNAL_CONTROL_INBOUND_PORT_URI != null && !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * EXTERNAL_CONTROL_INBOUND_PORT_URI != null && !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()
 * }
 * invariant	{@code
 * X_RELATIVE_POSITION >= 0
 * }
 * invariant	{@code
 * Y_RELATIVE_POSITION >= 0
 * }
 * </pre>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
// -----------------------------------------------------------------------------
@SIL_Simulation_Architectures({
        @LocalArchitecture(uri = "silUnitTests", rootModelURI = "FanCoupledModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents()),
        @LocalArchitecture(uri = "silIntegrationTests", rootModelURI = "FanStateSILModel", simulatedTimeUnit = TimeUnit.HOURS, externalEvents = @ModelExternalEvents(exported = {
                SwitchOnFan.class,
                SwitchOffFan.class,
                SetHighSpeedFan.class,
                SetLowSpeedFan.class }))
})
// -----------------------------------------------------------------------------
@OfferedInterfaces(offered = { FanUserJava4CI.class,
        FanInternalControlCI.class,
        FanExternalControlJava4CI.class,
        FanSensorDataCI.FanSensorOfferedPullCI.class,
        FanActuatorCI.class })
// -----------------------------------------------------------------------------
public class FanCyPhy
        extends AbstractCyPhyComponent
        implements FanUserI,
        FanInternalControlI {
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    // BCM4Java information

    /** URI of the fan reflection inbound port used in tests. */
    public static final String REFLECTION_INBOUND_PORT_URI = "Fan-RIP-URI";

    /** URI of the fan port for user interactions. */
    public static final String USER_INBOUND_PORT_URI = "FAN-USER-INBOUND-PORT-URI";
    /** URI of the fan port for internal control. */
    public static final String INTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-INTERNAL-CONTROL-INBOUND-PORT-URI";
    /** URI of the fan port for external control. */
    public static final String EXTERNAL_CONTROL_INBOUND_PORT_URI = "FAN-EXTERNAL-CONTROL-INBOUND-PORT-URI";
    /** URI of the fan sensor data inbound port. */
    public static final String SENSOR_INBOUND_PORT_URI = "FAN-SENSOR-INBOUND-PORT-URI";
    /** URI of the fan actuator inbound port. */
    public static final String ACTUATOR_INBOUND_PORT_URI = "FAN-ACTUATOR-INBOUND-PORT-URI";

    /** inbound port offering the <code>FanUserCI</code> interface. */
    protected FanUserJava4InboundPort fip;
    /**
     * inbound port offering the <code>FanInternalControlCI</code>
     * interface.
     */
    protected FanInternalControlInboundPort ficip;
    /**
     * inbound port offering the <code>FanExternalControlCI</code>
     * interface.
     */
    protected FanExternalControlJava4InboundPort fecip;

    // Appliance information

    /** power level when the fan is on low speed. */
    public static final Measure<Double> LOW_POWER = new Measure<Double>(20.0, POWER_UNIT);
    /** power level when the fan is on high speed. */
    public static final Measure<Double> HIGH_POWER = new Measure<Double>(60.0, POWER_UNIT);
    /** nominal tension of the fan. */
    public static final Measure<Double> TENSION = new Measure<Double>(220.0, TENSION_UNIT);

    /** current speed of the fan. */
    protected FanSpeed currentSpeed;
    /** current power level of the fan. */
    protected TimedMeasure<Double> currentPowerLevel;

    // Sensors/actuators

    /** the inbound port through which the sensors are called. */
    protected FanSensorDataInboundPort sensorInboundPort;
    /** the inbound port through which the actuators are called. */
    protected FanActuatorInboundPort actuatorInboundPort;

    // Execution/Simulation

    /** when true, methods trace their actions. */
    public static boolean VERBOSE = true;
    /** when true, methods provides debugging traces of their actions. */
    public static boolean DEBUG = false;
    /** when tracing, x coordinate of the window relative position. */
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position. */
    public static int Y_RELATIVE_POSITION = 0;

    /**
     * one thread for the method execute, which starts the local SIL
     * simulator and wait until the end of the simulation to get the
     * simulation report, and one to answer the calls to the component
     * services.
     */
    protected static int NUMBER_OF_STANDARD_THREADS = 2;
    /** no need for statically defined schedulable threads. */
    protected static int NUMBER_OF_SCHEDULABLE_THREADS = 0;

    /** URI of the local simulation architecture for SIL unit tests. */
    public static final String UNIT_TEST_ARCHITECTURE_URI = "silUnitTests";
    /** URI of the local simulation architecture for SIL integration tests. */
    public static final String INTEGRATION_TEST_ARCHITECTURE_URI = "silIntegrationTests";

    /** plug-in holding the local simulation architecture and simulators. */
    protected AtomicSimulatorPlugin asp;
    /**
     * URI of the local simulation architecture used to compose the global
     * simulation architecture or the empty string if the component does
     * not execute as a simulation.
     */
    protected final String localArchitectureURI;
    /**
     * acceleration factor to be used when running the real time
     * simulation.
     */
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
     * f != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param f instance to be tested.
     * @return true if the implementation invariants are observed, false otherwise.
     */
    protected static boolean implementationInvariants(FanCyPhy f) {
        assert f != null : new PreconditionException("f != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                f.currentSpeed != null,
                FanCyPhy.class, f,
                "f.currentSpeed != null");
        ret &= AssertionChecking.checkImplementationInvariant(
                f.currentPowerLevel == null ||
                        f.currentPowerLevel.getData() >= 0.0 &&
                                f.currentPowerLevel.getData() <= MAX_POWER_LEVEL.getData(),
                FanCyPhy.class, f,
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
     * @return true if the invariants are observed, false otherwise.
     */
    public static boolean staticInvariants() {
        boolean ret = true;
        ret &= AssertionChecking.checkStaticInvariant(
                REFLECTION_INBOUND_PORT_URI != null &&
                        !REFLECTION_INBOUND_PORT_URI.isEmpty(),
                FanCyPhy.class,
                "REFLECTION_INBOUND_PORT_URI != null && "
                        + "!REFLECTION_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                USER_INBOUND_PORT_URI != null &&
                        !USER_INBOUND_PORT_URI.isEmpty(),
                FanCyPhy.class,
                "USER_INBOUND_PORT_URI != null && "
                        + "!USER_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                INTERNAL_CONTROL_INBOUND_PORT_URI != null &&
                        !INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
                FanCyPhy.class,
                "INTERNAL_CONTROL_INBOUND_PORT_URI != null && "
                        + "!INTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                EXTERNAL_CONTROL_INBOUND_PORT_URI != null &&
                        !EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty(),
                FanCyPhy.class,
                "EXTERNAL_CONTROL_INBOUND_PORT_URI != null && "
                        + "!EXTERNAL_CONTROL_INBOUND_PORT_URI.isEmpty()");
        ret &= AssertionChecking.checkStaticInvariant(
                X_RELATIVE_POSITION >= 0,
                FanCyPhy.class,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkStaticInvariant(
                Y_RELATIVE_POSITION >= 0,
                FanCyPhy.class,
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
     * f != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param f instance to be tested.
     * @return true if the invariants are observed, false otherwise.
     */
    protected static boolean invariants(FanCyPhy f) {
        assert f != null : new PreconditionException("f != null");

        boolean ret = true;
        ret &= staticInvariants();
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution

    /**
     * create a new fan.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     * 
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy() throws Exception {
        this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
                EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
                ACTUATOR_INBOUND_PORT_URI);
    }

    /**
     * create a new fan.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     * 
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI) throws Exception {
        this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
                fanUserInboundPortURI,
                fanInternalControlInboundPortURI,
                fanExternalControlInboundPortURI,
                fanSensorInboundPortURI,
                fanActuatorInboundPortURI);
    }

    /**
     * create a new fan.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().isStandard()
     * }
     * </pre>
     * 
     * @param reflectionInboundPortURI         URI of the reflection inbound port of
     *                                         the component.
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            String reflectionInboundPortURI,
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI) throws Exception {
        super(reflectionInboundPortURI,
                NUMBER_OF_STANDARD_THREADS,
                NUMBER_OF_SCHEDULABLE_THREADS);

        this.localArchitectureURI = null;
        this.accelerationFactor = 0.0;

        this.initialise(fanUserInboundPortURI,
                fanInternalControlInboundPortURI,
                fanExternalControlInboundPortURI,
                fanSensorInboundPortURI,
                fanActuatorInboundPortURI);

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");
    }

    // Tests without simulation execution

    /**
     * create a new fan for test executions without simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * pre	{@code
     * clockURI != null && !clockURI.isEmpty()
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param executionMode execution mode for the next run.
     * @param clockURI      URI of a clock used to synchronise components.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            ExecutionMode executionMode,
            String clockURI) throws Exception {
        this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
                EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
                ACTUATOR_INBOUND_PORT_URI, executionMode, clockURI);
    }

    /**
     * create a new fan for test executions without simulation with the given
     * inbound port URIs.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * pre	{@code
     * clockURI != null && !clockURI.isEmpty()
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @param executionMode                    execution mode for the next run.
     * @param clockURI                         URI of a clock used to synchronise
     *                                         components.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI,
            ExecutionMode executionMode,
            String clockURI) throws Exception {
        this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
                fanUserInboundPortURI,
                fanInternalControlInboundPortURI,
                fanExternalControlInboundPortURI,
                fanSensorInboundPortURI,
                fanActuatorInboundPortURI,
                executionMode,
                clockURI);
    }

    /**
     * create a new fan for test executions without simulation with the given
     * inbound port URIs.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isTestWithoutSimulation()
     * }
     * pre	{@code
     * clockURI != null && !clockURI.isEmpty()
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param reflectionInboundPortURI         URI of the reflection inbound port of
     *                                         the component.
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @param executionMode                    execution mode for the next run.
     * @param clockURI                         URI of a clock used to synchronise
     *                                         components.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            String reflectionInboundPortURI,
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI,
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

        this.initialise(fanUserInboundPortURI,
                fanInternalControlInboundPortURI,
                fanExternalControlInboundPortURI,
                fanSensorInboundPortURI,
                fanActuatorInboundPortURI);

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");
    }

    // Tests with simulation

    /**
     * create a new fan for test executions with simulation.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * !(this instanceof ComponentInterface)
     * }
     * pre	{@code
     * reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * executionMode != null && executionMode.isSimulationTest()
     * }
     * pre	{@code
     * testScenario != null
     * }
     * pre	{@code
     * localArchitectureURI != null && !localArchitectureURI.isEmpty()
     * }
     * pre	{@code
     * accelerationFactor > 0.0
     * }
     * post	{@code
     * !on()
     * }
     * post	{@code
     * getExecutionMode().equals(executionMode)
     * }
     * </pre>
     * 
     * @param reflectionInboundPortURI         URI of the reflection inbound port of
     *                                         the component.
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @param executionMode                    execution mode for the next run.
     * @param testScenario                     test scenario to be executed with
     *                                         this component.
     * @param localArchitectureURI             URI of the local simulation
     *                                         architecture.
     * @param accelerationFactor               acceleration factor for the
     *                                         simulation.
     * @throws Exception <i>to do</i>.
     */
    protected FanCyPhy(
            String reflectionInboundPortURI,
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI,
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

        this.initialise(fanUserInboundPortURI,
                fanInternalControlInboundPortURI,
                fanExternalControlInboundPortURI,
                fanSensorInboundPortURI,
                fanActuatorInboundPortURI);

        if (DEBUG) {
            this.logMessage("FanCyPhy local simulation architectures: "
                    + this.localSimulationArchitectures);
        }

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");
    }

    // -------------------------------------------------------------------------
    // Initialisation methods
    // -------------------------------------------------------------------------

    /**
     * initialise a new fan component.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * fanUserInboundPortURI != null && !fanUserInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanInternalControlInboundPortURI != null && !fanInternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanExternalControlInboundPortURI != null && !fanExternalControlInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanSensorInboundPortURI != null && !fanSensorInboundPortURI.isEmpty()
     * }
     * pre	{@code
     * fanActuatorInboundPortURI != null && !fanActuatorInboundPortURI.isEmpty()
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param fanUserInboundPortURI            URI of the inbound port to call the
     *                                         fan component for user interactions.
     * @param fanInternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for internal control.
     * @param fanExternalControlInboundPortURI URI of the inbound port to call the
     *                                         fan component for external control.
     * @param fanSensorInboundPortURI          URI of the inbound port to call the
     *                                         fan component sensors.
     * @param fanActuatorInboundPortURI        URI of the inbound port to call the
     *                                         fan component actuators.
     * @throws Exception <i>to do</i>.
     */
    protected void initialise(
            String fanUserInboundPortURI,
            String fanInternalControlInboundPortURI,
            String fanExternalControlInboundPortURI,
            String fanSensorInboundPortURI,
            String fanActuatorInboundPortURI) throws Exception {
        this.currentSpeed = FanSpeed.OFF;

        this.fip = new FanUserJava4InboundPort(fanUserInboundPortURI, this);
        this.fip.publishPort();
        this.ficip = new FanInternalControlInboundPort(
                fanInternalControlInboundPortURI, this);
        this.ficip.publishPort();
        this.fecip = new FanExternalControlJava4InboundPort(
                fanExternalControlInboundPortURI, this);
        this.fecip.publishPort();
        this.sensorInboundPort = new FanSensorDataInboundPort(
                fanSensorInboundPortURI, this);
        this.sensorInboundPort.publishPort();
        this.actuatorInboundPort = new FanActuatorInboundPort(
                fanActuatorInboundPortURI, this);
        this.actuatorInboundPort.publishPort();

        if (VERBOSE) {
            this.tracer.get().setTitle("Fan component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent#createLocalSimulationArchitecture(java.lang.String,
     *      java.lang.String, java.util.concurrent.TimeUnit, double)
     */
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
            ret = Local_SIL_SimulationArchitectures.createFanSIL_Architecture4UnitTest(
                    architectureURI,
                    rootModelURI,
                    simulatedTimeUnit,
                    accelerationFactor);
        } else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
            ret = Local_SIL_SimulationArchitectures.createFanSIL_Architecture4IntegrationTest(
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
    // Component life-cycle
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");

        try {
            switch (this.getExecutionMode()) {
                case STANDARD:
                case UNIT_TEST:
                case INTEGRATION_TEST:
                    break;
                case UNIT_TEST_WITH_SIL_SIMULATION:
                case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                    RTArchitecture architecture = (RTArchitecture) this.localSimulationArchitectures
                            .get(this.localArchitectureURI);
                    this.asp = new RTAtomicSimulatorPlugin();
                    ((RTAtomicSimulatorPlugin) this.asp).setPluginURI(architecture.getRootModelURI());
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

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public void execute() throws Exception {
        this.traceMessage("Fan CyPhy executes.\n");

        assert FanCyPhy.implementationInvariants(this) : new ImplementationInvariantException(
                "FanCyPhy.implementationInvariants(this)");
        assert FanCyPhy.invariants(this) : new InvariantException("FanCyPhy.invariants(this)");

        switch (this.getExecutionMode()) {
            case STANDARD:
                this.currentPowerLevel = new TimedMeasure<Double>(0.0, POWER_UNIT);
                break;
            case UNIT_TEST:
            case INTEGRATION_TEST:
                this.initialiseClock(
                        ClocksServer.STANDARD_INBOUNDPORT_URI,
                        this.clockURI);
                this.currentPowerLevel = new TimedMeasure<Double>(
                        0.0,
                        POWER_UNIT,
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
                        this.getClock4Simulation().getSimulatedDuration().getSimulatedDuration());
                this.currentPowerLevel = new TimedMeasure<Double>(
                        0.0,
                        POWER_UNIT,
                        this.getClock4Simulation(),
                        this.getClock4Simulation().getStartInstant());
                // wait until the simulation ends
                this.getClock4Simulation().waitUntilEnd();
                // give some time for the end of simulation catering tasks
                Thread.sleep(200L);
                // get and print the simulation report
                this.logMessage(this.asp.getFinalReport().toString());
                break;
            case INTEGRATION_TEST_WITH_SIL_SIMULATION:
                this.initialiseClock4Simulation(
                        ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
                        this.clockURI);
                this.currentPowerLevel = new TimedMeasure<Double>(
                        0.0,
                        POWER_UNIT,
                        this.getClock4Simulation(),
                        this.getClock4Simulation().getStartInstant());
                break;
            case UNIT_TEST_WITH_HIL_SIMULATION:
            case INTEGRATION_TEST_WITH_HIL_SIMULATION:
                throw new BCMException("HIL simulation not implemented yet!");
            default:
        }
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.fip.unpublishPort();
            this.ficip.unpublishPort();
            this.fecip.unpublishPort();
            this.sensorInboundPort.unpublishPort();
            this.actuatorInboundPort.unpublishPort();
        } catch (Throwable e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    // -------------------------------------------------------------------------
    // Component services implementation
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#on()
     */
    @Override
    public boolean on() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan returns its state: " +
                    this.currentSpeed + ".\n");
        }
        return this.currentSpeed != FanSpeed.OFF;
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOn()
     */
    @Override
    public void switchOn() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan switches on.\n");
        }

        assert !this.on() : new PreconditionException("!on()");

        this.currentSpeed = FanSpeed.LOW;

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    FanStateSILModel.URI,
                    t -> new SwitchOnFan(t));
            this.sensorInboundPort.send(
                    new FanStateSensorData(this.currentSpeed));
        }

        assert this.on() : new PostconditionException("on()");
        assert this.getSpeed() == FanSpeed.LOW : new PostconditionException("getSpeed() == FanSpeed.LOW");
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#switchOff()
     */
    @Override
    public void switchOff() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan switches off.\n");
        }

        assert this.on() : new PreconditionException("on()");

        if (this.getExecutionMode().isSILTest()) {
            this.sensorInboundPort.send(
                    new FanStateSensorData(FanSpeed.OFF));
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    FanStateSILModel.URI,
                    t -> new SwitchOffFan(t));
        }

        this.currentSpeed = FanSpeed.OFF;

        assert !this.on() : new PostconditionException("!on()");
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#setLowSpeed()
     */
    @Override
    public void setLowSpeed() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan sets to LOW speed.\n");
        }

        assert this.on() : new PreconditionException("on()");

        this.currentSpeed = FanSpeed.LOW;

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    FanStateSILModel.URI,
                    t -> new SetLowSpeedFan(t));
        }

        assert this.getSpeed() == FanSpeed.LOW : new PostconditionException("getSpeed() == FanSpeed.LOW");
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#setHighSpeed()
     */
    @Override
    public void setHighSpeed() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan sets to HIGH speed.\n");
        }

        assert this.on() : new PreconditionException("on()");

        this.currentSpeed = FanSpeed.HIGH;

        if (this.getExecutionMode().isSILTest()) {
            ((RTAtomicSimulatorPlugin) this.asp).triggerExternalEvent(
                    FanStateSILModel.URI,
                    t -> new SetHighSpeedFan(t));
        }

        assert this.getSpeed() == FanSpeed.HIGH : new PostconditionException("getSpeed() == FanSpeed.HIGH");
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI#getSpeed()
     */
    @Override
    public FanSpeed getSpeed() throws Exception {
        return this.currentSpeed;
    }

    // -------------------------------------------------------------------------
    // Component services implementation - FanInternalControlI
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI#running()
     */
    @Override
    public boolean running() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan returns its running status " +
                    (this.currentSpeed != FanSpeed.OFF) + ".\n");
        }

        return this.on();
    }

    // -------------------------------------------------------------------------
    // Component services implementation - FanExternalControlI
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getMaxPowerLevel()
     */
    @Override
    public Measure<Double> getMaxPowerLevel() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan returns its max power level " +
                    MAX_POWER_LEVEL + ".\n");
        }

        return MAX_POWER_LEVEL;
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#setCurrentPowerLevel(fr.sorbonne_u.alasca.physical_data.Measure)
     */
    @Override
    public void setCurrentPowerLevel(Measure<Double> powerLevel)
            throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan sets its power level to " +
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

        assert powerLevel.getData() > getMaxPowerLevel().getData() ||
                getCurrentPowerLevel().getMeasure().getData() == powerLevel.getData()
                : new PostconditionException(
                        "powerLevel.getData() > getMaxPowerLevel().getData() "
                                + "|| getCurrentPowerLevel().getData() == "
                                + "powerLevel.getData()");
    }

    /**
     * @see fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI#getCurrentPowerLevel()
     */
    @Override
    public SignalData<Double> getCurrentPowerLevel() throws Exception {
        if (FanCyPhy.VERBOSE) {
            this.traceMessage("Fan returns its current power level " +
                    this.currentPowerLevel + ".\n");
        }

        assert this.on() : new PreconditionException("on()");

        SignalData<Double> ret = new SignalData<Double>(this.currentPowerLevel);

        assert ret != null && ret.getMeasure().getMeasurementUnit().equals(POWER_UNIT) : new PostconditionException(
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

    // -------------------------------------------------------------------------
    // Component sensors
    // -------------------------------------------------------------------------

    /**
     * return the speed state of the fan as a sensor data.
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
     * @return the speed state of the fan as a sensor data.
     * @throws Exception <i>to do</i>.
     */
    public FanStateSensorData speedPullSensor()
            throws Exception {
        return new FanStateSensorData(this.currentSpeed);
    }

    /**
     * start a sequence of speed pushes with the given period.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * controlPeriod > 0
     * }
     * pre	{@code
     * tu != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param controlPeriod period at which the pushes must be made.
     * @param tu            time unit in which {@code controlPeriod} is expressed.
     * @throws Exception <i>to do</i>.
     */
    public void startSpeedPushSensor(
            long controlPeriod,
            TimeUnit tu) throws Exception {
        long actualControlPeriod = -1L;
        if (this.executionMode.isStandard()) {
            actualControlPeriod = (long) (controlPeriod * tu.toNanos(1));
        } else {
            AcceleratedClock ac = this.clock.get();
            actualControlPeriod = (long) ((controlPeriod * tu.toNanos(1)) /
                    ac.getAccelerationFactor());
            if (actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
                System.out.println(
                        "Warning: accelerated control period is "
                                + "too small ("
                                + actualControlPeriod +
                                "), unexpected scheduling problems may"
                                + " occur!");
            }
        }
        this.speedPushSensorTask(actualControlPeriod);
    }

    /**
     * if the fan is not off, perform one push and schedule the next.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * actualControlPeriod > 0
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param actualControlPeriod period at which the push sensor must be triggered.
     * @throws Exception <i>to do</i>.
     */
    protected void speedPushSensorTask(long actualControlPeriod)
            throws Exception {
        assert actualControlPeriod > 0 : new PreconditionException("actualControlPeriod > 0");

        if (this.currentSpeed != FanSpeed.OFF) {
            this.traceMessage("Fan performs a new speed push.\n");
            this.speedPushSensor();
            if (this.executionMode.isStandard()
                    || this.executionMode.isSILTest()
                    || this.executionMode.isHILTest()) {
                this.scheduleTaskOnComponent(
                        new AbstractComponent.AbstractTask() {
                            @Override
                            public void run() {
                                try {
                                    speedPushSensorTask(actualControlPeriod);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        actualControlPeriod,
                        TimeUnit.NANOSECONDS);
            }
        }
    }

    /**
     * sends the current speed state through the push sensor interface.
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
     * @throws Exception <i>to do</i>.
     */
    protected void speedPushSensor() throws Exception {
        this.sensorInboundPort.send(this.speedPullSensor());
    }
}
// -----------------------------------------------------------------------------
