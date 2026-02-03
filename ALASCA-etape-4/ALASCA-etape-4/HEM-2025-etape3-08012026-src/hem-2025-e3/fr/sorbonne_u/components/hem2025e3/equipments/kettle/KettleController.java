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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleCyPhy.KettleState;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleSensorDataCI.KettleSensorRequiredPullCI;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleActuatorConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleActuatorOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleSensorDataConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.connections.KettleSensorDataOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.KettleTemperatureSensorData;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleController</code> implements a controller
 * component for the kettle.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The controller is a simple fixed period threshold-based controller.
 * Unlike the heater controller, the kettle controller does not have a target
 * temperature setting - it simply heats water to boiling point (100°C) and
 * then can optionally switch to keeping warm mode (around 80°C).
 * </p>
 * <p>
 * The controller has two control modes:
 * </p>
 * <ul>
 * <li>In pull mode, it periodically polls the kettle sensors to get the
 * current water temperature and make control decisions.</li>
 * <li>In push mode, the kettle pushes temperature data to the controller
 * at regular intervals.</li>
 * </ul>
 * 
 * <p>
 * <strong>Glass-box Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * controlPeriod > 0
 * }
 * invariant	{@code
 * sensorIBP_URI != null && !sensorIBP_URI.isEmpty()
 * }
 * invariant	{@code
 * actuatorIBPURI != null && !actuatorIBPURI.isEmpty()
 * }
 * </pre>
 * 
 * <p>
 * <strong>Black-box Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * STANDARD_CONTROL_PERIOD > 0
 * }
 * invariant	{@code
 * BOILING_TEMPERATURE > 0.0
 * }
 * invariant	{@code
 * KEEP_WARM_TEMPERATURE > 0.0
 * }
 * invariant	{@code
 * BOILING_TEMPERATURE > KEEP_WARM_TEMPERATURE
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
@RequiredInterfaces(required = { KettleSensorRequiredPullCI.class,
        KettleActuatorCI.class,
        ClocksServerWithSimulationCI.class })
@OfferedInterfaces(offered = { DataRequiredCI.PushCI.class })
// -----------------------------------------------------------------------------
public class KettleController
        extends AbstractComponent
        implements KettlePushImplementationI {
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
    /** boiling temperature of water in Celsius. */
    public static final double BOILING_TEMPERATURE = 100.0;
    /** keep warm temperature in Celsius. */
    public static final double KEEP_WARM_TEMPERATURE = 80.0;
    /** hysteresis for temperature control. */
    public static final double HYSTERESIS = 2.0;

    /** URI of the sensor inbound port on the {@code Kettle}. */
    protected String sensorIBP_URI;
    /** URI of the actuator inbound port on the {@code Kettle}. */
    protected String actuatorIBPURI;
    /** sensor data outbound port connected to the {@code Kettle}. */
    protected KettleSensorDataOutboundPort sensorOutboundPort;
    /** actuator outbound port connected to the {@code Kettle}. */
    protected KettleActuatorOutboundPort actuatorOutboundPort;

    /* user set control period in seconds. */
    protected double controlPeriod;
    /** control mode (push or pull) for the current execution. */
    protected ControlMode controlMode;
    /*
     * actual control period, either in pure real time (not under test)
     * or in accelerated time (under test), expressed in nanoseconds;
     * used for scheduling the control task.
     */
    protected long actualControlPeriod;
    /**
     * the current state of the kettle as perceived through the sensor
     * data received from the {@code Kettle}.
     */
    protected KettleState currentState;
    /** lock controlling the access to {@code currentState}. */
    protected Object stateLock;
    /** whether to switch to keep warm mode after boiling. */
    protected boolean keepWarmAfterBoiling;

    /**
     * the current execution mode of the component: standard, test or
     * test with simulation SIL or HIL.
     */
    protected ExecutionMode executionMode;

    // -------------------------------------------------------------------------
    // Invariants
    // -------------------------------------------------------------------------

    protected static boolean implementationInvariants(
            KettleController instance) {
        assert instance != null : new PreconditionException("instance != null");

        boolean ret = true;
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.controlPeriod > 0,
                KettleController.class,
                instance,
                "controlPeriod > 0");
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.sensorIBP_URI != null &&
                        !instance.sensorIBP_URI.isEmpty(),
                KettleController.class, instance,
                "sensorIBP_URI != null && !sensorIBP_URI.isEmpty()");
        ret &= AssertionChecking.checkImplementationInvariant(
                instance.actuatorIBPURI != null &&
                        !instance.actuatorIBPURI.isEmpty(),
                KettleController.class, instance,
                "actuatorIBPURI != null && !actuatorIBPURI.isEmpty()");
        return ret;
    }

    protected static boolean invariants(KettleController instance) {
        assert instance != null : new PreconditionException("instance != null");

        boolean ret = true;
        ret &= AssertionChecking.checkInvariant(
                X_RELATIVE_POSITION >= 0,
                KettleController.class, instance,
                "X_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkInvariant(
                Y_RELATIVE_POSITION >= 0,
                KettleController.class, instance,
                "Y_RELATIVE_POSITION >= 0");
        ret &= AssertionChecking.checkInvariant(
                STANDARD_CONTROL_PERIOD > 0,
                KettleController.class,
                instance,
                "STANDARD_CONTROL_PERIOD > 0");
        return ret;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    // Standard execution

    protected KettleController(
            String sensorIBP_URI,
            String actuatorIBP_URI) throws Exception {
        this(sensorIBP_URI, actuatorIBP_URI,
                STANDARD_CONTROL_PERIOD, ControlMode.PULL, false);
    }

    protected KettleController(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode,
            boolean keepWarmAfterBoiling) throws Exception {
        // two standard threads in case the thread that runs the method execute
        // can be prevented to run by the thread running receiveRunningState
        // the schedulable thread pool is used to run the control task
        super(3, 1);

        this.initialise(sensorIBP_URI, actuatorIBP_URI, controlPeriod,
                controlMode, keepWarmAfterBoiling);

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

    protected KettleController(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode,
            boolean keepWarmAfterBoiling,
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
                controlMode, keepWarmAfterBoiling);

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
            this.tracer.get().setTitle("Kettle controller component");
            this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
                    Y_RELATIVE_POSITION);
            this.toggleTracing();
        }

        // Invariant checking
        assert KettleController.implementationInvariants(this) : new ImplementationInvariantException(
                "KettleController.implementationInvariants(this)");
        assert KettleController.invariants(this) : new InvariantException("KettleController.invariants(this)");
    }

    protected void initialise(
            String sensorIBP_URI,
            String actuatorIBP_URI,
            double controlPeriod,
            ControlMode controlMode,
            boolean keepWarmAfterBoiling) throws Exception {
        this.sensorIBP_URI = sensorIBP_URI;
        this.actuatorIBPURI = actuatorIBP_URI;
        this.controlPeriod = controlPeriod;
        this.controlMode = controlMode;
        this.keepWarmAfterBoiling = keepWarmAfterBoiling;
        this.stateLock = new Object();

        this.sensorOutboundPort = new KettleSensorDataOutboundPort(this);
        this.sensorOutboundPort.publishPort();
        this.actuatorOutboundPort = new KettleActuatorOutboundPort(this);
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
                    KettleSensorDataConnector.class.getCanonicalName());
            this.doPortConnection(
                    this.actuatorOutboundPort.getPortURI(),
                    this.actuatorIBPURI,
                    KettleActuatorConnector.class.getCanonicalName());

            synchronized (this.stateLock) {
                this.currentState = KettleState.OFF;
            }

            if (VERBOSE) {
                this.traceMessage("Kettle controller starts.\n");
            }
        } catch (Exception e) {
            throw new ComponentStartException(e);
        }
    }

    @Override
    public synchronized void finalise() throws Exception {
        if (VERBOSE) {
            this.traceMessage("Kettle controller ends.\n");
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
    public void processKettleState(KettleState kettleState) {
        assert kettleState != null : new PreconditionException("kettleState != null");

        if (DEBUG) {
            this.traceMessage("receives kettle state: " + kettleState + ".\n");
        }

        synchronized (this.stateLock) {
            KettleState oldState = this.currentState;
            this.currentState = kettleState;

            // Start control when kettle is switched on and starts heating
            if ((kettleState == KettleState.HEATING ||
                    kettleState == KettleState.ON) &&
                    oldState == KettleState.OFF) {
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
                                        ((KettleController) this.getTaskOwner()).pullControlLoop();
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
                        this.sensorOutboundPort.startTemperaturePushSensor(
                                cp, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void processTemperature(KettleTemperatureSensorData temperature) {
        assert temperature != null : new PreconditionException("temperature != null");

        try {
            // execute the control only if the kettle is still ON
            KettleState s = KettleState.OFF;
            synchronized (this.stateLock) {
                s = this.currentState;
            }
            if (s != KettleState.OFF) {
                this.oneControlStep(temperature, s);
            } else {
                if (VERBOSE) {
                    this.traceMessage("control is off.\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute one control step based on the current water temperature.
     * 
     * <p>
     * <strong>Contract</strong>
     * </p>
     * 
     * <pre>
     * pre	{@code
     * temperature != null
     * }
     * pre	{@code
     * priorState != null
     * }
     * post	{@code
     * true
     * }	// no postcondition.
     * </pre>
     *
     * @param temperature current water temperature sensor data.
     * @param priorState  state of the kettle before this control step.
     * @throws Exception <i>to do</i>.
     */
    protected void oneControlStep(
            KettleTemperatureSensorData temperature,
            KettleState priorState) throws Exception {
        double currentTemp = temperature.getMeasure().getData();
        StringBuffer sb = new StringBuffer();

        if (priorState == KettleState.HEATING) {
            // Check if water has reached boiling point
            if (currentTemp >= BOILING_TEMPERATURE - HYSTERESIS) {
                if (this.keepWarmAfterBoiling) {
                    // Switch to keep warm mode
                    this.actuatorOutboundPort.stopHeating();
                    this.actuatorOutboundPort.startKeepingWarm();
                    this.currentState = KettleState.KEEP_WARM;
                    if (VERBOSE) {
                        sb.append("Water boiled (");
                        sb.append(currentTemp);
                        sb.append("°C), switching to keep warm mode.\n");
                        this.traceMessage(sb.toString());
                    }
                } else {
                    // Just stop heating
                    this.actuatorOutboundPort.stopHeating();
                    this.currentState = KettleState.ON;
                    if (VERBOSE) {
                        sb.append("Water boiled (");
                        sb.append(currentTemp);
                        sb.append("°C), stopping heating.\n");
                        this.traceMessage(sb.toString());
                    }
                }
            } else {
                // Continue heating
                if (VERBOSE) {
                    sb.append("Still heating, current temperature: ");
                    sb.append(currentTemp);
                    sb.append("°C, target: ");
                    sb.append(BOILING_TEMPERATURE);
                    sb.append("°C.\n");
                    this.traceMessage(sb.toString());
                }
            }
        } else if (priorState == KettleState.KEEP_WARM) {
            // Maintain temperature around KEEP_WARM_TEMPERATURE
            if (currentTemp < KEEP_WARM_TEMPERATURE - HYSTERESIS) {
                // Temperature dropped too low, start heating
                this.actuatorOutboundPort.stopKeepingWarm();
                this.actuatorOutboundPort.startHeating();
                this.currentState = KettleState.HEATING;
                if (VERBOSE) {
                    sb.append("Temperature dropped to ");
                    sb.append(currentTemp);
                    sb.append("°C, reheating to keep warm.\n");
                    this.traceMessage(sb.toString());
                }
            } else if (currentTemp > KEEP_WARM_TEMPERATURE + HYSTERESIS) {
                // Temperature is fine, just monitoring
                if (VERBOSE) {
                    sb.append("Keeping warm at ");
                    sb.append(currentTemp);
                    sb.append("°C.\n");
                    this.traceMessage(sb.toString());
                }
            } else {
                if (VERBOSE) {
                    sb.append("Temperature stable at ");
                    sb.append(currentTemp);
                    sb.append("°C in keep warm mode.\n");
                    this.traceMessage(sb.toString());
                }
            }
        } else if (priorState == KettleState.ON) {
            // Kettle is on but not actively heating or keeping warm
            // Start heating automatically if temperature is below boiling point
            if (currentTemp < BOILING_TEMPERATURE - HYSTERESIS) {
                this.actuatorOutboundPort.startHeating();
                this.currentState = KettleState.HEATING;
                if (VERBOSE) {
                    sb.append("Kettle is on, starting to heat water from ");
                    sb.append(currentTemp);
                    sb.append("°C to ");
                    sb.append(BOILING_TEMPERATURE);
                    sb.append("°C.\n");
                    this.traceMessage(sb.toString());
                }
            } else {
                if (VERBOSE) {
                    sb.append("Kettle is on, water already at ");
                    sb.append(currentTemp);
                    sb.append("°C.\n");
                    this.traceMessage(sb.toString());
                }
            }
        }
    }

    /**
     * Implement the pull control loop.
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
     */
    protected void pullControlLoop() {
        try {
            // execute the control as long as the kettle is ON
            KettleState priorState = KettleState.OFF;
            synchronized (this.stateLock) {
                priorState = this.currentState;
            }
            if (priorState != KettleState.OFF) {
                // get the temperature data from the kettle
                KettleTemperatureSensorData td = (KettleTemperatureSensorData) this.sensorOutboundPort.request();

                if (DEBUG) {
                    this.traceMessage(
                            "executes a new pull control step on " + td + "\n");
                }

                this.oneControlStep(td, priorState);

                // schedule the next execution of the loop
                this.scheduleTask(
                        o -> ((KettleController) o).pullControlLoop(),
                        this.actualControlPeriod,
                        TimeUnit.NANOSECONDS);
            } else {
                // when the kettle is OFF, exit the control loop
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
