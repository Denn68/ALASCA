package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineActuatorCI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineActuatorConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineActuatorOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineSensorDataConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineSensorDataOutboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>WashingMachineController</code> implements a controller
 * component for the washing machine.
 * Adapted strictly from HeaterController.
 */
//-----------------------------------------------------------------------------
@RequiredInterfaces(required={WashingMachineActuatorCI.class,
							  DataRequiredCI.PullCI.class, // Requis pour le sensor port
							  ClocksServerWithSimulationCI.class})
@OfferedInterfaces(offered={DataRequiredCI.PushCI.class})
//-----------------------------------------------------------------------------
public class			WashingMachineController
extends		AbstractComponent
implements	WashingMachinePushImplementationI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	public static enum	ControlMode {
		PULL,
		PUSH
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static int			X_RELATIVE_POSITION = 0;
	public static int			Y_RELATIVE_POSITION = 0;
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = true;

	/** standard control period in seconds.									*/
	public static final double	STANDARD_CONTROL_PERIOD = 60.0;
	
	// Default program settings
	public static final double	STANDARD_WASHING_TIME_MIN = 60.0; 
	public static final double	STANDARD_WASHING_TEMP = 40.0;

	protected String							sensorIBP_URI;
	protected String							actuatorIBPURI;
	
	// Like Heater, we need BOTH ports
	protected WashingMachineSensorDataOutboundPort		sensorOutboundPort;
	protected WashingMachineActuatorOutboundPort		actuatorOutboundPort;

	protected double							controlPeriod;
	protected ControlMode						controlMode;
	protected long								actualControlPeriod;
	
	protected WashingMachineState				currentState;
	protected Object							stateLock;

	protected ExecutionMode						executionMode;
	
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(
		WashingMachineController instance
		)
	{
		assert	instance != null : new PreconditionException("instance != null");
		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.controlPeriod > 0,
					WashingMachineController.class, instance, "controlPeriod > 0");
		return ret;
	}

	protected static boolean	invariants(WashingMachineController instance)
	{
		assert	instance != null : new PreconditionException("instance != null");
		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					STANDARD_CONTROL_PERIOD > 0,
					WashingMachineController.class, instance, "STANDARD_CONTROL_PERIOD > 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			WashingMachineController(
		String sensorIBP_URI,
		String actuatorIBP_URI
		) throws Exception
	{
		this(sensorIBP_URI, actuatorIBP_URI,
			 STANDARD_CONTROL_PERIOD, ControlMode.PULL);
	}

	protected			WashingMachineController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double controlPeriod,
		ControlMode controlMode
		) throws Exception
	{
		super(3, 1);
		this.initialise(sensorIBP_URI, actuatorIBP_URI, controlPeriod, controlMode);

		this.executionMode = ExecutionMode.STANDARD;
		this.actualControlPeriod =
				(long)((this.controlPeriod * TimeUnit.SECONDS.toNanos(1)));
		
		if (this.actualControlPeriod < TimeUnit.MILLISECONDS.toNanos(10)) {
			System.out.println("Warning: accelerated control period is too small.");
		}
	}

	protected			WashingMachineController(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double controlPeriod,
		ControlMode controlMode,
		ExecutionMode executionMode,
		double accelerationFactor
		) throws Exception
	{
		super(3, 1);
		this.initialise(sensorIBP_URI, actuatorIBP_URI, controlPeriod, controlMode);

		this.executionMode = executionMode;
		this.actualControlPeriod =
			(long)((this.controlPeriod * TimeUnit.SECONDS.toNanos(1))/accelerationFactor);

		if (VERBOSE || DEBUG) {
			this.tracer.get().setTitle("WashingMachine controller");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION, Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		assert	WashingMachineController.implementationInvariants(this);
		assert	WashingMachineController.invariants(this);
	}

	protected void		initialise(
		String sensorIBP_URI,
		String actuatorIBP_URI,
		double controlPeriod,
		ControlMode controlMode
		) throws Exception
	{
		this.sensorIBP_URI = sensorIBP_URI;
		this.actuatorIBPURI = actuatorIBP_URI;
		this.controlPeriod = controlPeriod;
		this.controlMode = controlMode;
		this.stateLock = new Object();
		
		// Un-commented because essential for Pull Control (like Heater)
		this.sensorOutboundPort = new WashingMachineSensorDataOutboundPort(this);
		this.sensorOutboundPort.publishPort();
		
		this.actuatorOutboundPort = new WashingMachineActuatorOutboundPort(this);
		this.actuatorOutboundPort.publishPort();
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			this.doPortConnection(
					this.sensorOutboundPort.getPortURI(),
					sensorIBP_URI,
					WashingMachineSensorDataConnector.class.getCanonicalName());
			this.doPortConnection(
					this.actuatorOutboundPort.getPortURI(),
					this.actuatorIBPURI,
					WashingMachineActuatorConnector.class.getCanonicalName());

			synchronized (this.stateLock) {
				this.currentState = WashingMachineState.OFF;
			}
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.sensorOutboundPort.getPortURI());
		this.doPortDisconnection(this.actuatorOutboundPort.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.sensorOutboundPort.unpublishPort();
			this.actuatorOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	@Override
	public void			processWashingMachineState(WashingMachineState state)
	{
		assert	state != null : new PreconditionException("state != null");

		if (DEBUG) {
			this.traceMessage("receives washing machine state: " + state + ".\n");
		}

		synchronized (this.stateLock) {
			WashingMachineState oldState = this.currentState;
			this.currentState = state;	

			// Start Control loop if machine is switched ON
			if (state != WashingMachineState.OFF && oldState == WashingMachineState.OFF) {
				if (this.controlMode == ControlMode.PULL) {
					if (VERBOSE) this.traceMessage("start pull control.\n");
					
					if (this.executionMode.isStandard() || this.executionMode.isTestWithoutSimulation()) {
						this.pullControLoop();
					} else {
						this.scheduleTaskOnComponent(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										((WashingMachineController)this.getTaskOwner()).pullControLoop();
									}
								},
								this.actualControlPeriod, TimeUnit.NANOSECONDS);
					}
				} else {
					if (VERBOSE) this.traceMessage("start push control.\n");
					long cp = (long) (TimeUnit.SECONDS.toMillis(1) * this.controlPeriod);
					try {
						// Trigger push mode on sensor port
						this.sensorOutboundPort.startProgramDataPushSensor(cp, TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	/**
	 * Implementation of the Push interface method.
	 * This replaces the old processSensorData.
	 */
	@Override
	public void processProgramData(WashingMachineProgramSensorData programData) {
		assert programData != null : new PreconditionException("programData != null");

		if (DEBUG) {
			this.traceMessage("Receives program data push.\n");
		}
		
		try {
			WashingMachineState s;
			synchronized (this.stateLock) {
				s = this.currentState;
			}
			if (s != WashingMachineState.OFF) {
				// Unpack the composite data, exactly like Heater unpacks Temperatures
				WashingMachineState currentPhase = programData.getPhase().getMeasure().getData();
				double remainingTime = programData.getRemainingPhaseTime().getMeasure().getData();
				double remainingDelay = programData.getRemainingDelay().getMeasure().getData();
				
				this.oneControlStep(currentPhase, remainingTime, remainingDelay);
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * Control logic step.
	 * Adapted from Heater: checks state and decides action.
	 */
	protected void		oneControlStep(
		WashingMachineState currentPhase,
		double remainingTime,
		double remainingDelay
		) throws Exception
	{
		// Logic: If machine is ON but not washing (IDLE), start the cycle.
		// Equivalent to Heater's "If temp < target, startHeating"
		
		if (currentPhase == WashingMachineState.ON) {
			if (VERBOSE) {
				this.traceMessage("Controller decides to start washing cycle.\n");
			}
			
			// Start standard cycle
			long duration = (long) (STANDARD_WASHING_TIME_MIN * 60 * 1000);
			Measure<Double> targetTemp = new Measure<>(STANDARD_WASHING_TEMP, MeasurementUnit.CELSIUS);
			
			this.actuatorOutboundPort.startWashing(duration, targetTemp);
			
			// Update local state to avoid spamming commands before next sensor update
			synchronized (this.stateLock) {
				this.currentState = WashingMachineState.WASHING;
			}
			
		} else if (currentPhase == WashingMachineState.WASHING) {
			if (VERBOSE) {
				this.traceMessage("Machine is washing. Time left: " + remainingTime + " ms.\n");
			}
		}
	}

	protected void		pullControLoop()
	{
		try {
			WashingMachineState currentStateLocal;
			synchronized (this.stateLock) {
				currentStateLocal = this.currentState;
			}
			
			if (currentStateLocal != WashingMachineState.OFF) {
				// Pull data from sensor (returns the Composite data)
				WashingMachineProgramSensorData data = 
						(WashingMachineProgramSensorData) this.sensorOutboundPort.request();
				
				WashingMachineState phase = data.getPhase().getMeasure().getData();
				double time = data.getRemainingPhaseTime().getMeasure().getData();
				double delay = data.getRemainingDelay().getMeasure().getData();
				
				this.oneControlStep(phase, time, delay);

				this.scheduleTask(
						o -> ((WashingMachineController)o).pullControLoop(),
						this.actualControlPeriod, 
						TimeUnit.NANOSECONDS);
			} else {
				if (VERBOSE) this.traceMessage("exit the control.\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}