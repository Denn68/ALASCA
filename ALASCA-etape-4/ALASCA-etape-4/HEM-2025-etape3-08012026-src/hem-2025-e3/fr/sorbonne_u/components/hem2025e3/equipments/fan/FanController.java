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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanSensorDataCI.FanSensorRequiredPullCI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanActuatorConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanActuatorOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanSensorDataConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.connections.FanSensorDataOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanController</code> implements a controller component
 * for the fan.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The fan controller monitors the fan state and manages its speed
 * based on power consumption requirements. Unlike the heater/kettle
 * controllers, the fan controller does not use temperature-based
 * hysteresis control. It simply monitors the fan state and can
 * adjust speed when needed.
 * </p>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
// -----------------------------------------------------------------------------
@RequiredInterfaces(required = { FanSensorRequiredPullCI.class,
        FanActuatorCI.class,
        ClocksServerWithSimulationCI.class })
@OfferedInterfaces(offered = { DataRequiredCI.PushCI.class })
// -----------------------------------------------------------------------------
public class FanController
        extends AbstractComponent
        implements FanPushImplementationI {
    // -------------------------------------------------------------------------
    // Inner types and classes
    // -------------------------------------------------------------------------

    public static enum ControlMode {
        PULL,
        PUSH
    }

    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    /** when tracing, x coordinate of the window relative position. */
    public static int X_RELATIVE_POSITION = 0;
    /** when tracing, y coordinate of the window relative position. */
    public static int Y_RELATIVE_POSITION = 0;
    /** when true, some methods trace their actions. */
    public static boolean VERBOSE = true;
    /** when true, some methods trace their actions. */
    public static boolean DEBUG = true;

    /** standard control period in seconds. */
    public static final double STANDARD_CONTROL_PERIOD = 60.0;

    /** URI of the sensor inbound port on the {@code Fan}. */
    protected String sensorIBP_URI;
    /** URI of the actuator inbound port on the {@code Fan}. */
    protected String actuatorIBPURI;
    /** sensor data outbound port connected to the {@code Fan}. */
    protected FanSensorDataOutboundPort sensorOutboundPort;
    /** actuator outbound port connected to the {@code Fan}. */
    protected FanActuatorOutboundPort actuatorOutboundPort;

    /* user set control period in seconds. */
    protected double controlPeriod;
    /** control mode (push or pull) for the current execution. */
    protected ControlMode controlMode;
    /** actual control period in nanoseconds. */
    protected long actualControlPeriod;
    /** the current speed of the fan as perceived through sensor data. */
    protected FanSpeed currentSpeed;
    /** lock controlling the access to {@code currentSpeed}. */
    protected Object stateLock;

    /** the current execution mode of the component. */
    protected ExecutionMode executionMode;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    protected static boolean implementationInvariants(
            FanController instance) {
        assert instance != null : new PreconditionException("instance != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.controlPeriod > 0,
                FanController.class,
                instance,
                "controlPeriod > 0");
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.sensorIBP_URI != null &&
                        !instance.sensorIBP_URI.isEmpty(),
                FanController.class, instance,
                "sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.actuatorIBPURI != null &&
                        !instance.actuatorIBPURI.isEmpty(),
                FanController.class, instance,
                "actuatorIBPURI != null && !actuatorIBPURI.isEmpty()");
        return ret;
    }

    protected static boolean invariants(FanController instance) {
        assert instance != null : new PreconditionException("instance != null");

        boolean ret = true;
        ret &= AssertionChecking.checkInvariant(
                X_RELATIVE_POSITION >= 0,
                FanController.class, instance,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkInvariant(
                Y_RELATIVE_POSITION >= 0,
                FanController.class, instance,
                "Y_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkInvariant(
                STANDARD_CONTROL_PERIOD > 0,
                FanController.class,
                instance,
                "STANDARD_CONTROL_PERIOD > 0");
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution

    protected FanController(
            String sensorIBP_URI,
            String actuatorIBP_URI) throws Exception {
        this(sensorIBP_URI, actuatorIBP_URI,
                STANDARD_CONTROL_PERIOD, ControlMode.PULL);
    }

    protected FanController(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode) throws Exception {
        super(3, 1);

        this.initialise(sensorIBP_URI, actuatorIBP_URI, controlPeriod,
                controlMode);

        this.executionMode = ExecutionMode.STANDARD;
        this.actualControlPeriod = (long) ((this.controlPeriod * TimeUnit.SECONDS.toNanos(1)));
        // sanity checking
        if (this.actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
            System.out.println(
                    "Warning: control period is too small ("
                            + this.actualControlPeriod +
                            "), unexpected scheduling problems may occur!");
        }
    }

    // Test executions, with or without simulation

    protected FanController(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode,
            ExecutionMode executionMode,
            double accelerationFactor) throws Exception {
        super(3, 1);

        assert sensorIBP_URI != null && !sensorIBP_URI.isEmpty() : new PreconditionException(
                "sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
        assert actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty() : new PreconditionException(
                "actuatorIBP_URI != null && !actuatorIBP_URI.isEmpty()");
        assert controlPeriod > 0 : new PreconditionException("controlPeriod > 0");
        assert controlMode != null : new PreconditionException("controlMode != null");
        assert executionMode.isSimulationTest() : new PreconditionException("executionMode.isSimulationTest()");
        assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

        this.initialise(sensorIBP_URI, actuatorIBP_URI, controlPeriod,
                controlMode);

        this.executionMode = executionMode;
        this.actualControlPeriod = (long) ((this.controlPeriod * TimeUnit.SECONDS.toNanos(1)) /
                accelerationFactor);
        // sanity checking
        if (this.actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
            System.out.println(
                    "Warning: accelerated control period is too small ("
                            + this.actualControlPeriod +
                            "), unexpected scheduling problems may occur!");
        }

        if (VERBOSE || DEBUG) {
            this.tracer.get().setTitle("Fan controller component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        // Invariant checking
        assert FanController.implementationInvariants(this) : new ImplementationInvariantException(
                "FanController.implementationInvariants(this)");
        assert FanController.invariants(this) : new InvariantException("FanController.invariants(this)");
    }

    protected void initialise(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode) throws Exception {
        this.sensorIBP_URI = sensorIBP_URI;
        this.actuatorIBPURI = actuatorIBP_URI;
        this.controlPeriod = controlPeriod;
        this.controlMode = controlMode;
        this.stateLock = new Object();

        this.sensorOutboundPort = new FanSensorDataOutboundPort(this);
        this.sensorOutboundPort.publishPort();
        this.actuatorOutboundPort = new FanActuatorOutboundPort(this);
        this.actuatorOutboundPort.publishPort();
    }

    // -------------------------------------------------------------------------
    // Component life-cycle
    // -------------------------------------------------------------------------

    @Override
    public synchronized void start() throws ComponentStartException {
        super.start();

        try {
            this.doPortConnection(
                    this.sensorOutboundPort.getPortURI(),
                    sensorIBP_URI,
                    FanSensorDataConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.actuatorOutboundPort.getPortURI(),
                    this.actuatorIBPURI,
                    FanActuatorConnector.class.getCanonicalName());

            synchronized (this.stateLock) {
                this.currentSpeed = FanSpeed.OFF;
            }

            if (VERBOSE) {
                this.traceMessage("Fan controller starts.\n");
            }
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public synchronized void finalise() throws Exception {
        if (VERBOSE) {
            this.traceMessage("Fan controller ends.\n");
        }
        this.doPortDisconnection(this.sensorOutboundPort.getPortURI());
        this.doPortDisconnection(this.actuatorOutboundPort.getPortURI());
        super.finalise();
    }

    @Override
    public synchronized void shutdown() throws ComponentShutdownException {
        try {
            this.sensorOutboundPort.unpublishPort();
            this.actuatorOutboundPort.unpublishPort();
        } catch (Exception e) {
            throw new ComponentShutdownException(e);
        }
        super.shutdown();
    }

    // -------------------------------------------------------------------------
    // Component internal methods
    // -------------------------------------------------------------------------

    @Override
    public void processFanState(FanSpeed fanSpeed) {
        assert fanSpeed != null : new PreconditionException("fanSpeed != null");

        if (DEBUG) {
            this.traceMessage("receives fan speed: " + fanSpeed + ".\n");
        }

        synchronized (this.stateLock) {
            FanSpeed oldSpeed = this.currentSpeed;
            this.currentSpeed = fanSpeed;

            // Start control when fan is switched on
            if (fanSpeed != FanSpeed.OFF && oldSpeed == FanSpeed.OFF) {
                if (this.controlMode == ControlMode.PULL) {
                    if (VERBOSE) {
                        this.traceMessage("start pull control.\n");
                    }
                    if (this.executionMode.isStandard() ||
                            this.executionMode.isTestWithoutSimulation()) {
                        this.pullControlLoop();
                    } else {
                        this.scheduleTaskOnComponent(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        ((FanController) this.getTaskOwner()).pullControlLoop();
                                    }
                                },
                                this.actualControlPeriod,
                                TimeUnit.NANOSECONDS);
                    }
                } else {
                    if (VERBOSE) {
                        this.traceMessage("start push control.\n");
                    }
                    long cp = (long) (TimeUnit.SECONDS.toMillis(1)
                            * this.controlPeriod);
                    try {
                        this.sensorOutboundPort.startSpeedPushSensor(
                                cp, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    protected void oneControlStep(
            FanStateSensorData speedData,
            FanSpeed priorSpeed) throws Exception {
        FanSpeed currentSpd = speedData.getSpeed();
        StringBuffer sb = new StringBuffer();

        if (currentSpd != FanSpeed.OFF) {
            if (VERBOSE) {
                sb.append("Fan is running at ");
                sb.append(currentSpd);
                sb.append(" speed.\n");
                this.traceMessage(sb.toString());
            }
        } else {
            if (VERBOSE) {
                this.traceMessage("Fan is off.\n");
            }
        }
    }

    protected void pullControlLoop() {
        try {
            // execute the control as long as the fan is ON
            FanSpeed priorSpeed = FanSpeed.OFF;
            synchronized (this.stateLock) {
                priorSpeed = this.currentSpeed;
            }
            if (priorSpeed != FanSpeed.OFF) {
                // get the speed data from the fan
                FanStateSensorData sd = (FanStateSensorData) this.sensorOutboundPort.request();

                if (DEBUG) {
                    this.traceMessage(
                            "executes a new pull control step on " + sd + "\n");
                }

                this.oneControlStep(sd, priorSpeed);

                // schedule the next execution of the loop
                this.scheduleTask(
                        o -> ((FanController) o).pullControlLoop(),
                        this.actualControlPeriod,
                        TimeUnit.NANOSECONDS);
            } else {
                // when the fan is OFF, exit the control loop
                if (VERBOSE) {
                    this.traceMessage("exit the control.\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
// -----------------------------------------------------------------------------
