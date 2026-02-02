package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ExecutionMode;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineUserOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineInternalControlOutboundPort;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections.WashingMachineExternalControlOutboundPort;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

@RequiredInterfaces(required = {
		WashingMachineUserCI.class,
		WashingMachineInternalControlCI.class,
		WashingMachineExternalControlCI.class
})
public class WashingMachineTesterCyPhy
extends AbstractCyPhyComponent
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static boolean VERBOSE = false;
	public static int X_RELATIVE_POSITION = 0;
	public static int Y_RELATIVE_POSITION = 0;

	public static final String REFLECTION_INBOUND_PORT_URI =
			"WashingMachineTester-RIP-URI";

	protected static int NUMBER_OF_STANDARD_THREADS = 1;
	protected static int NUMBER_OF_SCHEDULABLE_THREADS = 1;

	protected String washingMachineUserInboundPortURI;
	protected String washingMachineInternalControlInboundPortURI;
	protected String washingMachineExternalControlInboundPortURI;

	protected WashingMachineUserOutboundPort wmop;
	protected WashingMachineInternalControlOutboundPort wmicop;
	protected WashingMachineExternalControlOutboundPort wmecop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	protected WashingMachineTesterCyPhy(
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS);

		this.initialise(washingMachineUserInboundPortURI,
						washingMachineInternalControlInboundPortURI,
						washingMachineExternalControlInboundPortURI);
	}

	protected WashingMachineTesterCyPhy(
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI,
		ExecutionMode executionMode,
		TestScenario testScenario
		) throws Exception
	{
		super(REFLECTION_INBOUND_PORT_URI,
			  NUMBER_OF_STANDARD_THREADS,
			  NUMBER_OF_SCHEDULABLE_THREADS,
			  executionMode,
			  testScenario.getClockURI(),
			  testScenario);

		this.initialise(washingMachineUserInboundPortURI,
						washingMachineInternalControlInboundPortURI,
						washingMachineExternalControlInboundPortURI);
	}

	protected void initialise(
		String washingMachineUserInboundPortURI,
		String washingMachineInternalControlInboundPortURI,
		String washingMachineExternalControlInboundPortURI
		) throws Exception
	{
		this.washingMachineUserInboundPortURI = washingMachineUserInboundPortURI;
		this.wmop = new WashingMachineUserOutboundPort(this);
		this.wmop.publishPort();

		this.washingMachineInternalControlInboundPortURI =
				washingMachineInternalControlInboundPortURI;
		this.wmicop = new WashingMachineInternalControlOutboundPort(this);
		this.wmicop.publishPort();

		this.washingMachineExternalControlInboundPortURI =
				washingMachineExternalControlInboundPortURI;
		this.wmecop = new WashingMachineExternalControlOutboundPort(this);
		this.wmecop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("WashingMachine tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}
	}

	// -------------------------------------------------------------------------
	// Accessors for test actions
	// -------------------------------------------------------------------------

	public WashingMachineUserOutboundPort getWmop()
	{
		return this.wmop;
	}

	public WashingMachineInternalControlOutboundPort getWmicop()
	{
		return this.wmicop;
	}

	public WashingMachineExternalControlOutboundPort getWmecop()
	{
		return this.wmecop;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public synchronized void start() throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
					this.wmop.getPortURI(),
					this.washingMachineUserInboundPortURI,
					WashingMachineUserConnector.class.getCanonicalName());
			this.doPortConnection(
					this.wmicop.getPortURI(),
					this.washingMachineInternalControlInboundPortURI,
					WashingMachineInternalControlConnector.class.getCanonicalName());
			this.doPortConnection(
					this.wmecop.getPortURI(),
					this.washingMachineExternalControlInboundPortURI,
					WashingMachineExternalControlConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e);
		}
	}

	@Override
	public synchronized void execute() throws Exception
	{
		this.traceMessage("WashingMachine Tester begins execution.\n");

		switch (this.getExecutionMode()) {
		case UNIT_TEST:
		case INTEGRATION_TEST:
			this.initialiseClock(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case UNIT_TEST_WITH_SIL_SIMULATION:
		case INTEGRATION_TEST_WITH_SIL_SIMULATION:
			this.initialiseClock4Simulation(
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					this.clockURI);
			this.executeTestScenario(testScenario);
			break;
		case INTEGRATION_TEST_WITH_HIL_SIMULATION:
		case UNIT_TEST_WITH_HIL_SIMULATION:
			throw new BCMException("HIL simulation not implemented yet!");
		case STANDARD:
			this.traceMessage("WashingMachine Tester in standard mode.\n");
			break;
		default:
		}

		this.traceMessage("WashingMachine Tester ends execution.\n");
	}

	@Override
	public synchronized void finalise() throws Exception
	{
		this.doPortDisconnection(this.wmop.getPortURI());
		this.doPortDisconnection(this.wmicop.getPortURI());
		this.doPortDisconnection(this.wmecop.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException
	{
		try {
			this.wmop.unpublishPort();
			this.wmicop.unpublishPort();
			this.wmecop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}
