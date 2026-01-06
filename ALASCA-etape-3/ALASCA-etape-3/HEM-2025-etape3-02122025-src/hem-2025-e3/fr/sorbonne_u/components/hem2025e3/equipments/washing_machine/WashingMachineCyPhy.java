package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
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

// Interfaces du composant
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineTemperatureI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlI;

// Ports
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserJava4InboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlInboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlJava4InboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil.Local_SIL_SimulationArchitectures;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineActuatorInboundPort;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections.WashingMachineSensorDataInboundPort;

// Sensor Data
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineStateSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingPhaseSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.RemainingTimeSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.DelayedStartSensorData;

// Events (MIL/SIL)
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.StartWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SetDelayedStart;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SuspendWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.ResumeWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SetPowerWashingMachine;

import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.MeasureI;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.alasca.physical_data.TimedMeasure;

// -----------------------------------------------------------------------------
/**
 * The class <code>WashingMachineCyPhy</code> implements a washing machine component
 * using the DEVS simulation standard for Java (CyPhy).
 */
//-----------------------------------------------------------------------------
@SIL_Simulation_Architectures({
	@LocalArchitecture(
		uri = "silUnitTests",
		rootModelURI = "WashingMachineCoupledModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents = @ModelExternalEvents()
		),
	@LocalArchitecture(
		uri = "silIntegrationTests",
		rootModelURI = "WashingMachineCoupledModel",
		simulatedTimeUnit = TimeUnit.HOURS,
		externalEvents =
			@ModelExternalEvents(
				exported = {
					SwitchOnWashingMachine.class,
					SwitchOffWashingMachine.class,
					StartWashing.class,
					SetDelayedStart.class,
					SuspendWashing.class,
					ResumeWashing.class,
					SetPowerWashingMachine.class
				}
			)
		)
})
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered={WashingMachineUserJava4CI.class,
							WashingMachineInternalControlCI.class,
							WashingMachineExternalControlJava4CI.class,
							WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI.class,
							WashingMachineActuatorCI.class})
//-----------------------------------------------------------------------------
public class			WashingMachineCyPhy
extends		AbstractCyPhyComponent
implements	WashingMachineUserI,
			WashingMachineInternalControlI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static final String		REFLECTION_INBOUND_PORT_URI =
															"WashingMachine-RIP-URI";	
	public static final String		USER_INBOUND_PORT_URI =
												"WASHING-MACHINE-USER-INBOUND-PORT-URI";
	public static final String		INTERNAL_CONTROL_INBOUND_PORT_URI =
									"WASHING-MACHINE-INTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String		EXTERNAL_CONTROL_INBOUND_PORT_URI =
									"WASHING-MACHINE-EXTERNAL-CONTROL-INBOUND-PORT-URI";
	public static final String		SENSOR_INBOUND_PORT_URI =
											"WASHING-MACHINE-SENSOR-INBOUND-PORT-URI";
	public static final String		ACTUATOR_INBOUND_PORT_URI =
											"WASHING-MACHINE-ACTUATOR-INBOUND-PORT-URI";

	// URI du modèle SIL
	public static final String 		WASHING_MACHINE_STATE_MODEL_URI = "WashingMachineStateModel";

	// Ports
	protected WashingMachineUserJava4InboundPort			wmip;
	protected WashingMachineInternalControlInboundPort		wmicip;
	protected WashingMachineExternalControlJava4InboundPort	wmecip;
	
	protected WashingMachineSensorDataInboundPort	sensorInboundPort;
	protected WashingMachineActuatorInboundPort		actuatorInboundPort;

	// Appliance information
	protected static final Measure<Double>	STANDARD_TARGET_TEMPERATURE =
												new Measure<>(
														40.0,
														TEMPERATURE_UNIT);
	public static final SignalData<Double>	FAKE_CURRENT_TEMPERATURE =
												new SignalData<>(
													new Measure<>(
															20.0,
															TEMPERATURE_UNIT));

	protected WashingMachineState		currentState;
	protected TimedMeasure<Double>		currentPowerLevel;
	protected TimedMeasure<Double>		targetTemperature;
	
	// Variables d'état spécifiques
	protected double					remainingWashingTime;
	protected double					remainingDelay;

	// Execution/Simulation
	public static boolean			VERBOSE = true;
	public static boolean			DEBUG = false;
	public static int				X_RELATIVE_POSITION = 0;
	public static int				Y_RELATIVE_POSITION = 0;

	protected static int			NUMBER_OF_STANDARD_THREADS = 2;
	protected static int			NUMBER_OF_SCHEDULABLE_THREADS = 0;

	public static final String		UNIT_TEST_ARCHITECTURE_URI =
														"silUnitTests";
	public static final String		INTEGRATION_TEST_ARCHITECTURE_URI =
														"silIntegrationTests";
	protected static final String	CURRENT_TEMPERATURE_NAME =
														"currentTemperature";

	protected AtomicSimulatorPlugin	asp;
	protected final String			localArchitectureURI;
	protected final double			accelerationFactor;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(WashingMachineCyPhy h)
	{
		assert	h != null : new PreconditionException("h != null");
		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				h.currentState != null,
				WashingMachineCyPhy.class, h,
				"h.currentState != null");
		return ret;
	}

	protected static boolean	invariants(WashingMachineCyPhy h)
	{
		assert	h != null : new PreconditionException("h != null");
		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				WashingMachineCyPhy.class, h,
				"X_RELATIVE_POSITION >= 0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected			WashingMachineCyPhy() throws Exception
	{
		this(USER_INBOUND_PORT_URI, INTERNAL_CONTROL_INBOUND_PORT_URI,
			 EXTERNAL_CONTROL_INBOUND_PORT_URI, SENSOR_INBOUND_PORT_URI,
			 ACTUATOR_INBOUND_PORT_URI);
	}

	protected			WashingMachineCyPhy(
		String wmUserInboundPortURI,
		String wmInternalControlInboundPortURI,
		String wmExternalControlInboundPortURI,
		String wmSensorInboundPortURI,
		String wmActuatorInboundPortURI
		) throws Exception
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 wmUserInboundPortURI,
			 wmInternalControlInboundPortURI,
			 wmExternalControlInboundPortURI,
			 wmSensorInboundPortURI,
			 wmActuatorInboundPortURI
			 );
	}

	protected			WashingMachineCyPhy(
		String reflectionInboundPortURI,
		String wmUserInboundPortURI,
		String wmInternalControlInboundPortURI,
		String wmExternalControlInboundPortURI,
		String wmSensorInboundPortURI,
		String wmActuatorInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS);

		this.localArchitectureURI = null;
		this.accelerationFactor = 0.0;

		this.initialise(wmUserInboundPortURI,
						wmInternalControlInboundPortURI,
						wmExternalControlInboundPortURI,
						wmSensorInboundPortURI,
						wmActuatorInboundPortURI);

		assert	WashingMachineCyPhy.implementationInvariants(this) :
				new ImplementationInvariantException(
						"WashingMachineCyPhy.implementationInvariants(this)");
		assert	WashingMachineCyPhy.invariants(this) :
				new InvariantException("WashingMachineCyPhy.invariants(this)");
	}

	protected			WashingMachineCyPhy(
		String reflectionInboundPortURI,
		String wmUserInboundPortURI,
		String wmInternalControlInboundPortURI,
		String wmExternalControlInboundPortURI,
		String wmSensorInboundPortURI,
		String wmActuatorInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario,
		String localArchitectureURI,
		double accelerationFactor
		) throws Exception
	{
		super(reflectionInboundPortURI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  AssertionChecking.assertTrueAndReturnOrThrow(
				testScenario != null,
				testScenario.getClockURI(),
				() -> new PreconditionException("testScenario != null")),
			  testScenario,
			  ((Supplier<Set<String>>)() ->
			  		{ HashSet<String> hs = new HashSet<>();
			  		  hs.add(UNIT_TEST_ARCHITECTURE_URI);
			  		  hs.add(INTEGRATION_TEST_ARCHITECTURE_URI);
			  		  return hs;
			  		}).get(),
			  accelerationFactor
			 );

		assert	executionMode != null && executionMode.isSimulationTest() :
				new PreconditionException(
						"executionMode != null && "
						+ "executionMode.isSimulationTest()");

		this.localArchitectureURI = localArchitectureURI;
		this.accelerationFactor = accelerationFactor;

		this.initialise(wmUserInboundPortURI,
						wmInternalControlInboundPortURI,
						wmExternalControlInboundPortURI,
						wmSensorInboundPortURI,
						wmActuatorInboundPortURI);
	}

	// -------------------------------------------------------------------------
	// Initialisation methods
	// -------------------------------------------------------------------------

	protected void		initialise(
		String wmUserInboundPortURI,
		String wmInternalControlInboundPortURI,
		String wmExternalControlInboundPortURI,
		String wmSensorInboundPortURI,
		String wmActuatorInboundPortURI
		) throws Exception
	{
		this.currentState = WashingMachineState.OFF;

		this.wmip = new WashingMachineUserJava4InboundPort(wmUserInboundPortURI, this);
		this.wmip.publishPort();
		this.wmicip = new WashingMachineInternalControlInboundPort(
									wmInternalControlInboundPortURI, this);
		this.wmicip.publishPort();
		this.wmecip = new WashingMachineExternalControlJava4InboundPort(
									wmExternalControlInboundPortURI, this);
		this.wmecip.publishPort();
		this.sensorInboundPort = new WashingMachineSensorDataInboundPort(
											wmSensorInboundPortURI, this);
		this.sensorInboundPort.publishPort();
		this.actuatorInboundPort = new WashingMachineActuatorInboundPort(
											wmActuatorInboundPortURI, this);
		this.actuatorInboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("WashingMachine component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}
	}

	@Override
	protected RTArchitecture	createLocalSimulationArchitecture(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
			new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");
	
		RTArchitecture ret = null;
		if (architectureURI.equals(UNIT_TEST_ARCHITECTURE_URI)) {
			ret = Local_SIL_SimulationArchitectures.
						createWashingMachineSILArchitecture(
									architectureURI,
									rootModelURI,
									simulatedTimeUnit,
									accelerationFactor);
		} else if (architectureURI.equals(INTEGRATION_TEST_ARCHITECTURE_URI)) {
			ret = Local_SIL_SimulationArchitectures.
						cr(
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

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();

		try {
			switch (this.getExecutionMode()) {
			case STANDARD:
			case UNIT_TEST:
			case INTEGRATION_TEST:
				break;
			case UNIT_TEST_WITH_SIL_SIMULATION:
			case INTEGRATION_TEST_WITH_SIL_SIMULATION:
				RTArchitecture architecture =
					(RTArchitecture) this.localSimulationArchitectures.
												get(this.localArchitectureURI);
				this.asp = new RTAtomicSimulatorPlugin() {
					private static final long serialVersionUID = 1L;
					@Override
					public VariableValue<Double>	getModelVariableValue(
						String modelURI,
						String name
						) throws Exception
					{
						return null; 
					}
				};
				((RTAtomicSimulatorPlugin)this.asp).
								setPluginURI(architecture.getRootModelURI());
				((RTAtomicSimulatorPlugin)this.asp).
										setSimulationArchitecture(architecture);
				this.installPlugin(this.asp);
				this.asp.createSimulator();
				this.asp.setSimulationRunParameters(
								(TestScenarioWithSimulation)this.testScenario,
								new HashMap<>());
				break;
			default:
			}		
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}		
	}

	@Override
	public void			execute() throws Exception
	{
		if (VERBOSE) this.traceMessage("WashingMachine CyPhy executes.\n");

		switch (this.getExecutionMode()) {
		case STANDARD:
			this.currentPowerLevel =
					new TimedMeasure<Double>(
							MAX_POWER_LEVEL.getData(),
							MAX_POWER_LEVEL.getMeasurementUnit());
			this.targetTemperature =
					new TimedMeasure<Double>(
							STANDARD_TARGET_TEMPERATURE.getData(),
							STANDARD_TARGET_TEMPERATURE.getMeasurementUnit());
			break;
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.currentPowerLevel =
					new TimedMeasure<Double>(
							MAX_POWER_LEVEL.getData(),
							MAX_POWER_LEVEL.getMeasurementUnit(),
							this.getClock(),
							this.getClock().getStartInstant());
			this.targetTemperature =
					new TimedMeasure<Double>(
							STANDARD_TARGET_TEMPERATURE.getData(),
							STANDARD_TARGET_TEMPERATURE.getMeasurementUnit(),
							this.getClock(),
							this.getClock().getStartInstant());
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServerWithSimulation.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.asp.initialiseSimulation(
					this.getClock4Simulation().getSimulatedStartTime(),
					this.getClock4Simulation().getSimulatedDuration());
			this.asp.startRTSimulation(
					TimeUnit.NANOSECONDS.toMillis(
							this.getClock4Simulation().getStartEpochNanos()),
					this.getClock4Simulation().getSimulatedStartTime().
														getSimulatedTime(),
					this.getClock4Simulation().getSimulatedDuration().
														getSimulatedDuration());
			
			this.currentPowerLevel =
					new TimedMeasure<Double>(
							MAX_POWER_LEVEL.getData(),
							MAX_POWER_LEVEL.getMeasurementUnit(),
							this.getClock4Simulation(),
							this.getClock4Simulation().getStartInstant());
			this.targetTemperature =
					new TimedMeasure<Double>(
							STANDARD_TARGET_TEMPERATURE.getData(),
							STANDARD_TARGET_TEMPERATURE.getMeasurementUnit(),
							this.getClock4Simulation(),
							this.getClock4Simulation().getStartInstant());
			
			this.getClock4Simulation().waitUntilEnd();
			Thread.sleep(200L);
			this.logMessage(this.asp.getFinalReport().toString());
			break;
		default:
		}		
	}

	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.wmip.unpublishPort();
			this.wmicip.unpublishPort();
			this.wmecip.unpublishPort();
			this.sensorInboundPort.unpublishPort();
			this.actuatorInboundPort.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	@Override
	public boolean		on() throws Exception
	{
		if (VERBOSE) this.traceMessage("WashingMachine state: " + this.currentState + ".\n");
		return this.currentState != WashingMachineState.OFF;
	}

	@Override
	public void			switchOn() throws Exception
	{
		if (VERBOSE) this.traceMessage("WashingMachine switches on.\n");
		assert	!this.on() : new PreconditionException("!on()");

		this.currentState = WashingMachineState.ON;
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												WASHING_MACHINE_STATE_MODEL_URI,
												t -> new SwitchOnWashingMachine(t));
			this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
		} else {
			this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
		}
	}

	@Override
	public void			switchOff() throws Exception
	{
		if (VERBOSE) this.traceMessage("WashingMachine switches off.\n");
		assert	this.on() : new PreconditionException("on()");

		this.currentState = WashingMachineState.OFF;

		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
												WASHING_MACHINE_STATE_MODEL_URI,
												t -> new SwitchOffWashingMachine(t));
			this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
		} else {
			this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
		}
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target) throws Exception
	{
		if (VERBOSE) this.traceMessage("Set target temperature: " + target + ".\n");
		
		assert	target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit()) :
				new PreconditionException("target != null && TEMPERATURE_UNIT.equals(target.getMeasurementUnit())");
		
		if (this.executionMode.isStandard() || this.executionMode.isTestWithoutSimulation()) {
			this.targetTemperature = new TimedMeasure<Double>(target.getData(), target.getMeasurementUnit());
		} else {
			assert	this.executionMode.isSimulationTest() :
					new BCMException("executionMode.isSimulationTest()");

			this.targetTemperature =
					new TimedMeasure<Double>(target.getData(),
											 target.getMeasurementUnit(),
											 this.getClock4Simulation());
		}

		assert	getTargetTemperature().getMeasure().getData().equals(target.getData()) :
				new PostconditionException("getTargetTemperature().equals(target)");
	}

	@Override
    public SignalData<Double> getTargetTemperature() throws Exception {
        if (VERBOSE) {
            this.traceMessage("WashingMachine returns its target temperature " + this.targetTemperature + ".\n");
        }
        
        // Exactement comme le Heater : on enveloppe dans un SignalData avec le temps
        if (this.getExecutionMode().isStandard()) {
            return new SignalData<Double>(this.targetTemperature);
        } else {
            // En simulation, on attache l'horloge simulée
            return new SignalData<Double>(this.getClock4Simulation(), this.targetTemperature);
        }
    }

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		if (VERBOSE) {
			this.traceMessage("WashingMachine returns its current temperature.\n");
		}

		assert	this.on() : new PreconditionException("on()");

		SignalData<Double> currentTemperature = null;

		if (this.executionMode.isSILTest()) {
			VariableValue<Double> v = this.computeCurrentTemperature();
			currentTemperature =
				new SignalData<>(
					this.getClock4Simulation(),
					new TimedMeasure<Double>(
						v.getValue(),
						TEMPERATURE_UNIT,
						this.getClock4Simulation(),
						this.getClock4Simulation().instantOfSimulatedTime(v.getTime())));
		} else {
			assert	this.executionMode.isStandard() || this.executionMode.isTestWithoutSimulation();
			currentTemperature = FAKE_CURRENT_TEMPERATURE;
		}

		return currentTemperature;
	}
	
	protected VariableValue<Double>	computeCurrentTemperature() throws Exception
	{
		return ((RTAtomicSimulatorPlugin)this.asp).getModelVariableValue(
								WASHING_MACHINE_STATE_MODEL_URI,
								CURRENT_TEMPERATURE_NAME);
	}

	@Override
	public boolean isWashing() throws Exception {
		return this.currentState == WashingMachineState.WASHING;
	}

	@Override
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		if (VERBOSE) this.traceMessage("Start Washing.\n");
		assert this.on();
		
		this.currentState = WashingMachineState.WASHING;
		this.remainingWashingTime = washingTimeMS;
		this.setTargetTemperature(target);
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					WASHING_MACHINE_STATE_MODEL_URI,
					t -> new StartWashing(t, washingTimeMS, target.getData()));
		}
		this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
	}

	@Override
	public void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception {
		if (VERBOSE) this.traceMessage("Delayed Start " + delayMS + "ms.\n");
		assert this.on();
		
		this.remainingDelay = delayMS;
		this.remainingWashingTime = washingTimeMS;
		this.setTargetTemperature(target);
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					WASHING_MACHINE_STATE_MODEL_URI,
					t -> new SetDelayedStart(t, delayMS, washingTimeMS, target.getData()));
		}
	}

	@Override
	public void suspendCycle() throws Exception {
		if (VERBOSE) this.traceMessage("Suspend Cycle.\n");
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					WASHING_MACHINE_STATE_MODEL_URI,
					t -> new SuspendWashing(t));
		}
	}

	@Override
	public void resumeCycle() throws Exception {
		if (VERBOSE) this.traceMessage("Resume Cycle.\n");
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					WASHING_MACHINE_STATE_MODEL_URI,
					t -> new ResumeWashing(t));
		}
	}
	
	@Override
	public boolean heatWater() throws Exception {
		return this.currentState == WashingMachineState.HEATINGWATER;
	}

	@Override
	public void startHeatingWater() throws Exception {
		this.currentState = WashingMachineState.HEATINGWATER;
	}

	@Override
	public void stopHeatingWater() throws Exception {
		if(this.currentState == WashingMachineState.HEATINGWATER) {
			this.currentState = WashingMachineState.WASHING; 
		}
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return MAX_POWER_LEVEL;
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel) throws Exception {
		if (VERBOSE) this.traceMessage("Set Power Level: " + powerLevel + ".\n");
		
		this.currentPowerLevel = new TimedMeasure<Double>(powerLevel.getData(), powerLevel.getMeasurementUnit());
		
		if (this.getExecutionMode().isSILTest()) {
			((RTAtomicSimulatorPlugin)this.asp).triggerExternalEvent(
					WASHING_MACHINE_STATE_MODEL_URI,
					t -> new SetPowerWashingMachine(t, new SetPowerWashingMachine.PowerValue(powerLevel.getData())));
		}
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return new SignalData<Double>(this.currentPowerLevel);
	}

	// -------------------------------------------------------------------------
	// Component sensors (Pull & Push)
	// -------------------------------------------------------------------------
	
	public WashingMachineState getState() throws Exception {
		return this.currentState;
	}

	public WashingMachineProgramSensorData getProgramData() throws Exception {
		WashingPhaseSensorData phase = new WashingPhaseSensorData(this.currentState);
		RemainingTimeSensorData time = new RemainingTimeSensorData(this.remainingWashingTime);
		DelayedStartSensorData delay = new DelayedStartSensorData(this.remainingDelay);
		
		if (this.getExecutionMode().isStandard()) {
			return new WashingMachineProgramSensorData(phase, time, delay);
		} else {
			return new WashingMachineProgramSensorData(phase, time, delay, this.getClock4Simulation());
		}
	}

	public void startStatePushSensor() throws Exception {
		this.sensorInboundPort.send(new WashingMachineStateSensorData(this.currentState));
	}

	/**
	 * Corrected method signature to match the InboundPort call.
	 * Takes period and time unit, converts to ms, and schedules task.
	 */
	public void startProgramDataPushSensor(long controlPeriod, TimeUnit tu) throws Exception {
		// Convert the period to milliseconds as required by the task logic
		long actualControlPeriod = TimeUnit.MILLISECONDS.convert(controlPeriod, tu);
		this.programDataPushSensorTask(actualControlPeriod);
	}
	
	public void stopPushing() throws Exception {
		// Stop push tasks (to implement if needed)
	}
	
	protected void programDataPushSensorTask(long actualControlPeriod) throws Exception {
		if (this.currentState != WashingMachineState.OFF) {
			this.sensorInboundPort.send(this.getProgramData());
			
			if (this.executionMode.isStandard() || this.executionMode.isSILTest()) {
				this.scheduleTaskOnComponent(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								programDataPushSensorTask(actualControlPeriod);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					},
					actualControlPeriod,
					TimeUnit.MILLISECONDS);
			}
		}
	}

	public WashingMachineProgramSensorData programSensor() throws Exception {
		return this.getProgramData();
	}
}