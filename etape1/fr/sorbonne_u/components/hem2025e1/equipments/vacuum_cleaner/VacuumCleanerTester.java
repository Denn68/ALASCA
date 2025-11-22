package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.hem2025.tests_utils.TestsStatistics;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerMode;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest;

@RequiredInterfaces(required = {VacuumCleanerUserCI.class, ClocksServerCI.class})
public class			VacuumCleanerTester
extends		AbstractComponent
{

	public static boolean				VERBOSE = false;
	
	public static int					X_RELATIVE_POSITION = 0;
	
	public static int					Y_RELATIVE_POSITION = 0;

	protected final boolean				isUnitTest;
	
	protected VacuumCleanerOutboundPort		vcdop;
	
	protected String					vacuumCleanerInboundPortURI;

	protected TestsStatistics			statistics;
	
	protected static boolean	implementationInvariants(VacuumCleanerTester hdt)
	{
		assert	hdt != null : new PreconditionException("vct != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				hdt.vacuumCleanerInboundPortURI != null &&
										!hdt.vacuumCleanerInboundPortURI.isEmpty(),
				VacuumCleanerTester.class, hdt,
				"vcdt.vacuumCleanerInboundPortURI != null && "
								+ "!vcdt.vacuumCleanerInboundPortURI.isEmpty()");
		return ret;
	}
	
	protected static boolean	invariants(VacuumCleanerTester hdt)
	{
		assert	hdt != null : new PreconditionException("vcdt != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				X_RELATIVE_POSITION >= 0,
				VacuumCleanerTester.class, hdt,
				"X_RELATIVE_POSITION >= 0");
		ret &= AssertionChecking.checkInvariant(
				Y_RELATIVE_POSITION >= 0,
				VacuumCleanerTester.class, hdt,
				"Y_RELATIVE_POSITION >= 0");
		return ret;
	}
	
	protected			VacuumCleanerTester(boolean isUnitTest) throws Exception
	{
		this(isUnitTest, VacuumCleaner.INBOUND_PORT_URI);
	}
	
	protected			VacuumCleanerTester(
		boolean isUnitTest,
		String vacuumCleanerInboundPortURI
		) throws Exception
	{
		super(1, 0);

		assert	vacuumCleanerInboundPortURI != null &&
										!vacuumCleanerInboundPortURI.isEmpty() :
				new PreconditionException(
						"vacuumCleanerInboundPortURI != null && "
						+ "!vacuumCleanerInboundPortURI.isEmpty()");

		this.isUnitTest = isUnitTest;
		this.initialise(vacuumCleanerInboundPortURI);
	}

	protected			VacuumCleanerTester(
		boolean isUnitTest,
		String vacuumCleanerInboundPortURI,
		String reflectionInboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, 1, 0);

		this.isUnitTest = isUnitTest;
		this.initialise(vacuumCleanerInboundPortURI);
	}
	
	protected void		initialise(
		String vacuumCleanerInboundPortURI
		) throws Exception
	{
		this.vacuumCleanerInboundPortURI = vacuumCleanerInboundPortURI;
		this.vcdop = new VacuumCleanerOutboundPort(this);
		this.vcdop.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Vacuum cleaner tester component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();
		}

		this.statistics = new TestsStatistics();

		assert	implementationInvariants(this) :
				new ImplementationInvariantException(
						"VacuumCleanerTester.implementationInvariants(this)");
		assert	invariants(this) :
				new InvariantException("VacuumCleanerTester.invariants(this)");
	}
	
	public void			testGetState()
	{
		this.logMessage("Feature: Getting the state of the vacuum cleaner");
		this.logMessage("  Scenario: getting the state when off");
		this.logMessage("    Given the vacuum cleaner is initialised");
		this.logMessage("    And the vacuum cleaner has not been used yet");
		VacuumCleanerState result = null;
		try {
			this.logMessage("    When I test the state of the vacuum cleaner");
			result = this.vcdop.getState();
			this.logMessage("    Then the state of the vacuum cleaner is off");
			if (!VacuumCleanerState.OFF.equals(result)) {
				this.statistics.incorrectResult();
				this.logMessage("     but was: " + result);
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}
	
	public void			testGetMode()
	{
		this.logMessage("Feature: Getting the mode of the vacuum cleaner");
		this.logMessage("  Scenario: getting the mode when off");
		this.logMessage("    Given the vacuum cleaner is initialised");
		VacuumCleanerMode result = null;
		try {
			this.logMessage("    When the vacuum cleaner has not been used yet");
			result = this.vcdop.getMode();
			this.logMessage("    Then the vacuum cleaner is medium");
			if (!VacuumCleanerMode.MEDIUM.equals(result)) {
				this.logMessage("     but was: " + result);	
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
	}
	
	public void			testTurnOnOff()
	{
		this.logMessage("Feature: turning the vacuum cleaner on and off");
		this.logMessage("  Scenario: turning on when off");
		VacuumCleanerState resultState = null;
		VacuumCleanerMode resultMode = null;
		try {
			this.logMessage("    Given the vacuum cleaner is off");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
			this.logMessage("    When the vacuum cleaner is turned on");
			this.vcdop.turnOn();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
			this.logMessage("    And the vacuum cleaner is in medium mode");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning on when on");
		this.logMessage("    Given the vacuum cleaner is on");
		try {
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the vacuum cleaner is turned on");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.vcdop.turnOn();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch(Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when on");
		this.logMessage("    Given the vacuum cleaner is on");
		try {
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the vacuum cleaner is turned off");
		try {
			this.vcdop.turnOff();
			this.logMessage("    Then the vacuum cleaner is off");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: turning off when off");
		this.logMessage("    Given the vacuum cleaner is off");
		try {
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.OFF.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		this.logMessage("    When the vacuum cleaner is turned off");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.vcdop.turnOff();
			this.logMessage("     but the precondition exception was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();
	}

	
	public void			testSetMediumLowHigh()
	{
		this.logMessage("Feature: switching the vacuum cleaner medium, low and high.");
		this.logMessage("  Scenario: set the vacuum cleaner high from medium");
		this.logMessage("    Given the vacuum cleaner is on");
		VacuumCleanerState resultState = null;
		VacuumCleanerMode resultMode = null;
		try {
			this.vcdop.turnOn();
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is medium");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.failedCondition();
			}
		} catch (Throwable e) {
			this.statistics.failedCondition();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    When the vacuum cleaner is set high");
			this.logMessage("    Then the vacuum cleaner is on");
			this.vcdop.setHigh();
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And  the vacuum cleaner is high");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.HIGH.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the vacuum cleaner high from high");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is high");
		this.logMessage("    When the vacuum cleaner is set high");
		this.logMessage("    Then a precondition exception is thrown");
		boolean old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.vcdop.setHigh();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the vacuum cleaner medium from high");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is high");
		this.logMessage("    When the vacuum cleaner is set medium");
		try {
			this.vcdop.setMedium();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is medium");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		this.logMessage("  Scenario: set the vacuum cleaner medium from medium");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is medium");
		this.logMessage("    When the vacuum cleaner is set medium");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.vcdop.setMedium();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the vacuum cleaner low from medium");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is medium");
		this.logMessage("    When the vacuum cleaner is set low");
		try {
			this.vcdop.setLow();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is low");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the vacuum cleaner low from low");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is low");
		this.logMessage("    When the vacuum cleaner is set low");
		this.logMessage("    Then a precondition exception is thrown");
		old = BCMException.VERBOSE;
		try {
			BCMException.VERBOSE = false;
			this.vcdop.setLow();
			this.logMessage("     but it was not thrown");
			this.statistics.incorrectResult();
		} catch (Throwable e) {
			
		} finally {
			BCMException.VERBOSE = old;
		}

		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the vacuum cleaner high from low");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is low");
		this.logMessage("    When the vacuum cleaner is set high");
		try {
			this.vcdop.setHigh();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is high");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.HIGH.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the vacuum cleaner low from high");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is high");
		this.logMessage("    When the vacuum cleaner is set low");
		try {
			this.vcdop.setLow();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is low");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.LOW.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();
		
		this.logMessage("  Scenario: set the vacuum cleaner medium from low");
		this.logMessage("    Given the vacuum cleaner is on");
		this.logMessage("    And the vacuum cleaner is low");
		this.logMessage("    When the vacuum cleaner is set medium");
		try {
			this.vcdop.setMedium();
			this.logMessage("    Then the vacuum cleaner is on");
			resultState = this.vcdop.getState();
			if (!VacuumCleanerState.ON.equals(resultState)) {
				this.logMessage("     but was: " + resultState);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}
		try {
			this.logMessage("    And the vacuum cleaner is medium");
			resultMode = this.vcdop.getMode();
			if (!VacuumCleanerMode.MEDIUM.equals(resultMode)) {
				this.logMessage("     but was: " + resultMode);
				this.statistics.incorrectResult();
			}
		} catch (Throwable e) {
			this.statistics.incorrectResult();
			this.logMessage("     but the exception " + e + " has been raised");
		}

		this.statistics.updateStatistics();

		try {
			this.vcdop.turnOff();
		} catch (Throwable e) {
			assertTrue(false);
		}
	}
	
	protected void			runAllUnitTests()
	{
		this.testGetState();
		this.testGetMode();
		this.testTurnOnOff();
		this.testSetMediumLowHigh();

		this.statistics.statisticsReport(this);
	}
	
	@Override
	public synchronized void	start()
	throws ComponentStartException
	{
		super.start();

		try {
			this.doPortConnection(
							this.vcdop.getPortURI(),
							vacuumCleanerInboundPortURI,
							VacuumCleanerConnector.class.getCanonicalName());
		} catch (Throwable e) {
			throw new ComponentStartException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#execute()
	 */
	@Override
	public synchronized void execute() throws Exception
	{
		if (!this.isUnitTest) {
			ClocksServerOutboundPort clocksServerOutboundPort =
											new ClocksServerOutboundPort(this);
			clocksServerOutboundPort.publishPort();
			this.doPortConnection(
					clocksServerOutboundPort.getPortURI(),
					ClocksServer.STANDARD_INBOUNDPORT_URI,
					ClocksServerConnector.class.getCanonicalName());
			this.traceMessage("vacuum cleaner Tester gets the clock.\n");
			AcceleratedClock ac =
					clocksServerOutboundPort.getClock(
										CVMIntegrationTest.CLOCK_URI);
			this.doPortDisconnection(clocksServerOutboundPort.getPortURI());
			clocksServerOutboundPort.unpublishPort();
			clocksServerOutboundPort = null;

			this.traceMessage("vacuum cleaner Tester waits until start.\n");
			ac.waitUntilStart();
		}
		this.traceMessage("vacuum cleaner Tester starts the tests.\n");
		this.runAllUnitTests();
		this.traceMessage("vacuum cleaner Tester ends.\n");
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public synchronized void	finalise() throws Exception
	{
		this.doPortDisconnection(this.vcdop.getPortURI());
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public synchronized void	shutdown() throws ComponentShutdownException
	{
		try {
			this.vcdop.unpublishPort();
		} catch (Throwable e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
}
// -----------------------------------------------------------------------------
