package fr.sorbonne_u.components.cyphy;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cyphy.annotations.LocalArchitecture;
import fr.sorbonne_u.components.cyphy.annotations.SIL_Simulation_Architectures;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionInboundPort;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.utils.aclocks.AcceleratedAndSimulationClock;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationConnector;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationOutboundPort;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.exceptions.BCMRuntimeException;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>AbstractCyPhyComponent</code> add the necessary properties
 * and methods required to turn a standard BCM component into a cyber-physical
 * one.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Local simulation architectures in {@code simulationArchitectures} are plain
 * architectures producing simulators that can run in isolation within the
 * component.
 * </p>
 * <p>
 * Local simulation architectures can also be part of global inter-component
 * simulation architectures where they appear as atomic models (thanks to the
 * closure of model composition in DEVS).
 * </p>
 * <p>
 * <i>Work in progress...</i>
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code currentExecutionMode != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-06-18</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractCyPhyComponent
extends		AbstractComponent
implements	CyPhyComponentI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** the current execution mode of the component: standard, test or
	 *  test with simulation SIL or HIL.									*/
	protected ExecutionMode		executionMode;

	// Data used in tests

	/** URI of the clock used in tests.										*/
	protected String			clockURI;
	/** optional test scenario to be executed.								*/
	protected TestScenario		testScenario;

	// Data used in simulation modes

	/** set of URIs of local simulators to be created from the
	 *  {@code SIL_Simulation_Architectures} annotation.					*/
	protected final Set<String>				localSimulationArchitecturesURIs;
	/** map from local architectures URIs to local architecture
	 *  descriptions.														*/
	protected final Map<String,ArchitectureI>	localSimulationArchitectures;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Standard execution

	/**
	 * create a cyber-physical component for standard execution, like plain
	 * {@code AbstractComponent}, with a generated reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * post	{@code getExecutionMode().isStandard()}
	 * </pre>
	 *
	 * @param nbThreads				number of standard threads.
	 * @param nbSchedulableThreads	number of schedulable threads.
	 */
	protected			AbstractCyPhyComponent(
		int nbThreads,
		int nbSchedulableThreads
		)
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 nbThreads, nbSchedulableThreads);
	}

	/**
	 * create a cyber-physical component for standard execution, like plain
	 * {@code AbstractComponent}, with the given reflection inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * post	{@code getExecutionMode().isStandard()}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 * @param nbThreads					number of standard threads.
	 * @param nbSchedulableThreads		number of schedulable threads.
	 */
	protected			AbstractCyPhyComponent(
		String reflectionInboundPortURI,
		int nbThreads,
		int nbSchedulableThreads
		)
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		this.executionMode = ExecutionMode.STANDARD;
		this.clockURI = null;
		this.testScenario = null;
		this.localSimulationArchitectures = null;
		this.localSimulationArchitecturesURIs = null;
	}

	// Test execution without simulation

	/**
	 * create a cyber-physical component with a test execution mode and
	 * a clock URI and but no test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code executionMode != null && executionMode.isTestWithoutSimulation()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * post	{@code getExecutionMode().isTestWithoutSimulation()}
	 * </pre>
	 *
	 * @param nbThreads				number of standard threads.
	 * @param nbSchedulableThreads	number of schedulable threads.
	 * @param executionMode			the execution mode for the next run.
	 * @param clockURI				URI of a clock used to synchronise components.
	 * @throws VerboseException		<i>to do</i>.
	 */
	protected			AbstractCyPhyComponent(
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode executionMode,
		String clockURI
		) throws VerboseException
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 nbThreads,
			 nbSchedulableThreads,
			 AssertionChecking.assertTrueAndReturnOrThrow(
					 executionMode != null &&
					 					executionMode.isTestWithoutSimulation(),
					 executionMode,
					 () -> new PreconditionException(
							 		"executionMode != null && "
							 		+ "executionMode."
							 		+ "isTestWithoutSimulation()")),
			 clockURI,
			 null);
	}

	/**
	 * create a cyber-physical component with a test execution mode and
	 * a clock URI but no test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code executionMode != null && executionMode.isTestWithoutSimulation()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * post	{@code getExecutionMode().isTestWithoutSimulation()}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 * @param nbThreads					number of standard threads.
	 * @param nbSchedulableThreads		number of schedulable threads.
	 * @param executionMode				the execution mode for the next run.
	 * @param clockURI					URI of a clock used to synchronise components.
	 * @throws VerboseException			<i>to do</i>.
	 */
	protected			AbstractCyPhyComponent(
		String reflectionInboundPortURI,
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode executionMode,
		String clockURI
		) throws VerboseException
	{
		this(reflectionInboundPortURI,
			 nbThreads,
			 nbSchedulableThreads,
			 AssertionChecking.assertTrueAndReturnOrThrow(
					 executionMode != null &&
					 				executionMode.isTestWithoutSimulation(),
					 executionMode,
					 () -> new PreconditionException(
							 		"executionMode != null && "
							 		+ "executionMode."
							 		+ "isTestWithoutSimulation()")),
			 clockURI,
			 null);
	}

	/**
	 * create a cyber-physical component with a test execution mode and
	 * a clock URI as well as a test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code executionMode != null && executionMode.isTestWithoutSimulation()}
	 * post	{@code getExecutionMode().isTestWithoutSimulation()}
	 * </pre>
	 *
	 * @param nbThreads				number of standard threads.
	 * @param nbSchedulableThreads	number of schedulable threads.
	 * @param executionMode			the execution mode for the next run.
	 * @param clockURI				URI of a clock used to synchronise components.
	 * @param testScenario			optional test scenario to be executed.
	 * @throws VerboseException		<i>to do</i>.
	 */
	protected			AbstractCyPhyComponent(
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode executionMode,
		String clockURI,
		TestScenario testScenario
		) throws VerboseException
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 nbThreads,
			 nbSchedulableThreads,
			 AssertionChecking.assertTrueAndReturnOrThrow(
					 executionMode != null &&
					 				executionMode.isTestWithoutSimulation(),
					 executionMode,
					 () -> new PreconditionException(
							 		"executionMode != null && "
							 		+ "executionMode."
							 		+ "isTestWithoutSimulation()")),
			 clockURI,
			 testScenario);
	}

	/**
	 * create a cyber-physical component with a test execution mode and
	 * a clock URI as well as a test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code executionMode != null && executionMode.isTestWithoutSimulation()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code testScenario == null || clockURI.equals(testScenario.getClockURI())}
	 * post	{@code getExecutionMode().isTestWithoutSimulation()}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port of the created component.
	 * @param nbThreads					number of standard threads.
	 * @param nbSchedulableThreads		number of schedulable threads.
	 * @param executionMode				the execution mode for the next run.
	 * @param clockURI					URI of a clock used to synchronise components.
	 * @param testScenario				optional test scenario to be executed.
	 */
	protected			AbstractCyPhyComponent(
		String reflectionInboundPortURI,
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode executionMode,
		String clockURI,
		TestScenario testScenario
		)
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		assert	executionMode != null &&
								executionMode.isTestWithoutSimulation() :
				new PreconditionException(
						"executionMode != null && "
						+ "executionMode.isTestWithoutSimulation()");
		assert	clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"clockURI != null && !clockURI.isEmpty()");
		assert	testScenario == null || clockURI.equals(testScenario.getClockURI()) :
				new PreconditionException(
						"testScenario == null || clockURI.equals("
						+ "testScenario.getClockURI())");

		this.executionMode = executionMode;
		this.clockURI = clockURI;
		this.testScenario = testScenario;
		this.localSimulationArchitecturesURIs = null;
		this.localSimulationArchitectures = null;
	}

	// Tests with simulation execution

	/**
	 * create a cyber-physical component with a test in simulation execution
	 * mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code currentExecutionMode != null && currentExecutionMode.isSimulationTest()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * ore	{@code testScenario == null || clockURI.equals(testScenario.getClockURI())}
	 * pre	{@code localSimulationArchitecturesURIs != null && localSimulationArchitecturesURIs.size() > 0 && localSimulationArchitecturesURIs.stream().allMatch(uri -> uri != null && !uri.isEmpty())}
	 * post	{@code getExecutionMode().isSimulationTest()}
	 * </pre>
	 *
	 * @param nbThreads							number of standard threads.
	 * @param nbSchedulableThreads				number of schedulable threads.
	 * @param currentExecutionMode				the execution mode for the next run.
	 * @param clockURI							URI of a clock used to synchronise components.
	 * @param testScenario						optional test scenario to be executed.
	 * @param localSimulationArchitecturesURIs	set of URIs of the local simulator to be created and that are described in the annotations.
	 * @param accelerationFactor				acceleration factor for the simulation.
	 * @throws Exception						<i>to do</i>.
	 */
	protected			AbstractCyPhyComponent(
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode currentExecutionMode,
		String clockURI,
		TestScenario testScenario,
		Set<String>	localSimulationArchitecturesURIs,
		double accelerationFactor
		) throws Exception
	{
		this(AbstractPort.generatePortURI(CyPhyReflectionCI.class),
			 nbThreads, nbSchedulableThreads, currentExecutionMode, clockURI,
			 testScenario, localSimulationArchitecturesURIs, accelerationFactor);
	}

	/**
	 * create a cyber-physical component with a test in simulation execution
	 * mode.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !(this instanceof ComponentInterface)}
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code nbThreads >= 0}
	 * pre	{@code nbSchedulableThreads >= 0}
	 * pre	{@code executionMode != null && executionMode.isSimulationTest()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code testScenario == null || clockURI.equals(testScenario.getClockURI())}
	 * pre	{@code localSimulationArchitecturesURIs != null && localSimulationArchitecturesURIs.size() > 0 && localSimulationArchitecturesURIs.stream().allMatch(uri -> uri != null && !uri.isEmpty())}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code getExecutionMode().isSimulationTest()}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI			URI of the reflection inbound port of the created component.
	 * @param nbThreads							number of standard threads.
	 * @param nbSchedulableThreads				number of schedulable threads.
	 * @param executionMode						the execution mode for the next run.
	 * @param clockURI							URI of a clock used to synchronise components.
	 * @param testScenario						optional test scenario to be executed.
	 * @param localSimulationArchitecturesURIs	set of URIs of the local simulator to be created and that are described in the annotations.
	 * @param accelerationFactor				acceleration factor for the simulation.
	 * @throws Exception						<i>to do</i>.
	 */
	protected			AbstractCyPhyComponent(
		String reflectionInboundPortURI,
		int nbThreads,
		int nbSchedulableThreads,
		ExecutionMode executionMode,
		String clockURI,
		TestScenario testScenario,
		Set<String>	localSimulationArchitecturesURIs,
		double accelerationFactor
		) throws Exception
	{
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);

		assert	executionMode != null && executionMode.isSimulationTest() :
				new PreconditionException(
						"executionMode != null && "
						+ "executionMode.isSimulationTest()");
		assert	clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"clockURI != null && !clockURI.isEmpty()");
		assert	testScenario == null ||
								clockURI.equals(testScenario.getClockURI()) :
				new PreconditionException(
						"testScenario == null || "
						+ "clockURI.equals(testScenario.getClockURI())");
		assert	localSimulationArchitecturesURIs != null &&
					localSimulationArchitecturesURIs.size() > 0 &&
						localSimulationArchitecturesURIs.stream().
								allMatch(uri -> uri != null && !uri.isEmpty()) :
				new PreconditionException(
						"localSimulationArchitecturesURIs != null && "
						+ "localSimulationArchitecturesURIs.size() > 0 && "
						+ "localSimulationArchitecturesURIs.stream()."
						+ "allMatch(uri -> uri != null && !uri.isEmpty())");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		this.executionMode = executionMode;
		this.clockURI = clockURI;
		this.testScenario = testScenario;
		this.localSimulationArchitecturesURIs = localSimulationArchitecturesURIs;
		this.localSimulationArchitectures = new HashMap<>();

		// create the local simulation architectures from the
		// SIL_Simulation_Architectures annotation
		this.createLocalSimulationArchitecturesFromAnnotation(
															accelerationFactor);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#configureReflection(java.lang.String)
	 */
	@Override
	protected void		configureReflection(String reflectionInboundPortURI)
	throws Exception
	{
		this.addOfferedInterface(CyPhyReflectionCI.class);
		try {
			CyPhyReflectionInboundPort rip =
				new CyPhyReflectionInboundPort(reflectionInboundPortURI, this);
			rip.publishPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		assert	isOfferedInterface(CyPhyReflectionCI.class) :
				new PostconditionException(
					"isOfferedInterface(CyPhyReflectionCI.class)");
		assert	findInboundPortURIsFromInterface(CyPhyReflectionCI.class)
																	!= null :
				new PostconditionException(
					"findInboundPortURIsFromInterface(CyPhyReflectionCI.class)"
					+ " != null");
		assert	findInboundPortURIsFromInterface(CyPhyReflectionCI.class).length
																		== 1 :
				new PostconditionException(
					"findInboundPortURIsFromInterface(CyPhyReflectionCI.class)"
					+ ".length == 1");
		assert	findInboundPortURIsFromInterface(ReflectionCI.class)[0].
											equals(reflectionInboundPortURI) :
				new PostconditionException(
					"findInboundPortURIsFromInterface(ReflectionCI.class)[0]."
					+ "equals(reflectionInboundPortURI)");
	}

	// -------------------------------------------------------------------------
	// Internal methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the clock can be used for simulation, otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the clock can be used for simulation, otherwise false.
	 */
	protected boolean		isClock4Simulation()
	{
		assert	isClockInitialised() :
				new PreconditionException("isClockInitialised()");

		try {
			return this.clock.get() instanceof AcceleratedAndSimulationClock;
		} catch (InterruptedException | ExecutionException e) {
			// should not happen after the clock has been initialised
			throw new BCMRuntimeException(e) ;
		}
	}

	/**
	 * return the clock as an {@code AcceleratedAndSimulationClock}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getExecutionMode().isTest()}
	 * pre	{@code isClockInitialised()}
	 * pre	{@code isClock4Simulation()}
	 * post	{@code return != null}	// no postcondition.
	 * </pre>
	 *
	 * @return	the clock as an {@code AcceleratedAndSimulationClock}.
	 */
	protected AcceleratedAndSimulationClock	getClock4Simulation()
	{
		assert	getExecutionMode().isSimulationTest() :
				new PreconditionException(
						"getExecutionMode().isSimulationTest()");
		assert	isClockInitialised() :
				new PreconditionException("isClockInitialised()");
		assert	isClock4Simulation() :
				new PreconditionException("isClock4Simulation()");

		try {
			return (AcceleratedAndSimulationClock) this.clock.get();
		} catch (InterruptedException | ExecutionException e) {
			// should not happen after the clock has been initialised
			throw new BCMRuntimeException(e) ;
		}
	}

	/**
	 * initialise the clock from the clock server.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isClockInitialised()}
	 * pre	{@code clockServerInboundPortURI != null && !clockServerInboundPortURI.isEmpty()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * post	{@code isClockInitialised()}
	 * post	{@code !getExecutionMode().isSimulationTest() || isClock4Simulation()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		initialiseClock4Simulation(
		String clockServerInboundPortURI,
		String clockURI
		) throws Exception
	{
		assert	!getExecutionMode().isStandard() :
				new PreconditionException(
						"!getExecutionMode().isStandard()");
		assert	!isClockInitialised() :
				new PreconditionException("!isClockInitialised()");

		this.addRequiredInterface(ClocksServerWithSimulationCI.class);
		ClocksServerWithSimulationOutboundPort csop =
							new ClocksServerWithSimulationOutboundPort(this);
		csop.publishPort();
		this.doPortConnection(
				csop.getPortURI(),
				clockServerInboundPortURI,
				ClocksServerWithSimulationConnector.class.getCanonicalName());
		this.clock.complete(csop.getClockWithSimulation(this.clockURI));
		this.doPortDisconnection(csop.getPortURI());
		csop.unpublishPort();
		csop.destroyPort();
		this.removeRequiredInterface(ClocksServerWithSimulationCI.class);
	}

	/**
	 * return the test scenario to be executed by this component, or null if
	 * none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the test scenario to be executed by this component, or null if none.
	 */
	protected TestScenario	getTestScenario()
	{
		return this.testScenario;
	}

	/**
	 * return the scheduled executor service of this component with the given
	 * index.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code index >= 0}
	 * pre	{@code isSchedulable(index)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param index	index of a scheduled executor service of this component.
	 * @return		the scheduled executor service of this component with the given index.
	 */
	protected ScheduledExecutorService	getScheduledExecutorService(int index)
	{
		assert	isSchedulable(index) :
				new PreconditionException("isSchedulable(index)");
		return (ScheduledExecutorService) super.getExecutorService(index);
	}

	/**
	 * create the local simulation architectures for the given URIs from
	 * the {@code SIL_Simulation_Architectures} annotation and store them in the
	 * component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getClass().isAnnotationPresent(SIL_Simulation_Architectures.class)}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	acceleration factor to be used in the simulation runs.
	 * @throws Exception			<i>to do</i>.
	 */
	protected void		createLocalSimulationArchitecturesFromAnnotation(
		double accelerationFactor
		) throws Exception
	{
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");
		assert	this.getClass().isAnnotationPresent(
										SIL_Simulation_Architectures.class):
				new PreconditionException(
						"getClass().isAnnotationPresent("
						+ "SIL_Simulation_Architectures.class)");
	
		Set<String> workingSet =
				new HashSet<>(this.localSimulationArchitecturesURIs);
		SIL_Simulation_Architectures architecturesDescriptors =
			this.getClass().getAnnotation(SIL_Simulation_Architectures.class);
		for (int i = 0 ; i < architecturesDescriptors.value().length ; i++) {
			LocalArchitecture localArchi = architecturesDescriptors.value()[i];
			if (workingSet.contains(localArchi.uri())) {
				RTArchitecture architecture =
					this.createLocalSimulationArchitecture(
							localArchi.uri(), 
							localArchi.rootModelURI(),
							localArchi.simulatedTimeUnit(), 
							accelerationFactor);
				this.localSimulationArchitectures.put(
										architecture.getArchitectureURI(),
										architecture);
				workingSet.remove(localArchi.uri());
			} else {
				throw new BCMException("Unknown local simulaiton architecture: "
									   + localArchi.uri());
			}
		}
		assert	workingSet.isEmpty() : new BCMException("workingSet.isEmpty()");

		assert	localSimulationArchitecturesURIs.stream().allMatch(
						uri -> { try {
									return this.isLocalSimulator(uri);
								 } catch (Exception e) {
									throw new BCMRuntimeException(e);
								 }}) :
				new PostconditionException(
						"localArchitecturesURIS.stream().allMatch(uri -> { "
						+ "try { return this.isLocalSimulator(uri); } "
						+ "catch (Exception e) { "
						+ "throw new BCMRuntimeException(e) ; }})");
	}

	/**
	 * subclasses that use local simulation architectures must implement this
	 * method to create the actual local simulation architectures from their URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * pre	{@code rootModelURI != null && !rootModelURI.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param rootModelURI			URI to be given to the root model of the created simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used in the run.
	 * @return						the local simulation architecture for the component.
	 * @throws Exception			<i>to do</i>.
	 */
	protected RTArchitecture	createLocalSimulationArchitecture(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		throw new BCMException(
					"This method must not be called, it must be redefined!");
	}

	// -------------------------------------------------------------------------
	// Component services
	// -------------------------------------------------------------------------

	/**
	 * return the execution mode of the component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the current execution mode of the component.
	 */
	public ExecutionMode	getExecutionMode()
	{
		return this.executionMode;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isLocalSimulator(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulator(String uri)
	throws Exception
	{
		return this.localSimulationArchitectures.containsKey(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isLocalSimulatorInstalled(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulatorInstalled(String uri)
	throws Exception
	{
		assert	this.isLocalSimulator(uri) :
				new PreconditionException("isLocalSimulator(uri)");

		return this.isInstalled(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSimulationArchitecture(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitecture(String architectureURI)
	throws Exception
	{
		return this.localSimulationArchitecturesURIs.contains(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSimulationArchitectureInstalled(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitectureInstalled(String architectureURI)
	throws Exception
	{
		return this.isLocalSimulatorInstalled(architectureURI);
	}

//	/**
//	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#addSimulationArchitecture(java.lang.String, java.lang.String)
//	 */
//	@Override
//	public void		addSimulationArchitecture(
//		String architectureURI,
//		String localSimulatorURI
//		) throws Exception
//	{
//		assert	architectureURI != null && !architectureURI.isEmpty() :
//				new PreconditionException(
//						"globalArchitectureURI != null && "
//						+ "!globalArchitectureURI.isEmpty()");
//		assert	localSimulatorURI != null && !localSimulatorURI.isEmpty() :
//				new PreconditionException(
//						"localSimulatorURI != null && "
//						+ "!localSimulatorURI.isEmpty()");
//		assert	!this.isSimulationArchitecture(architectureURI) :
//				new PreconditionException(
//						"!isGlobalArchitecture(globalArchitectureURI)");
//		assert	this.isLocalSimulator(localSimulatorURI) :
//				new PreconditionException(
//						"isLocalSimulator(localSimulatorURI)");
//
//		this.global2localSimulationArchitectureURIS.
//								put(architectureURI, localSimulatorURI);
//	}
//
//	/**
//	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#removeSimulationArchitecture(java.lang.String)
//	 */
//	@Override
//	public void		removeSimulationArchitecture(
//		String globalArchitectureURI
//		) throws Exception
//	{
//		assert	globalArchitectureURI != null && !globalArchitectureURI.isEmpty() :
//				new PreconditionException(
//						"globalArchitectureURI != null && "
//						+ "!globalArchitectureURI.isEmpty()");
//		assert	!this.isSimulationArchitecture(globalArchitectureURI) :
//				new PreconditionException(
//						"!isGlobalArchitecture(globalArchitectureURI)");
//
//		this.global2localSimulationArchitectureURIS.
//											remove(globalArchitectureURI);
//	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isCoordinatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isCoordinatorComponent(String architectureURI)
	throws Exception
	{
		for (PluginI p : this.installedPlugins.get().values()) {
			if (p instanceof CoordinatorPlugin &&
					((CoordinatorPlugin)p).isArchitecture(architectureURI)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isAtomicSimulatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isAtomicSimulatorComponent(String architectureURI)
	throws Exception
	{
		return this.isLocalSimulator(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#isSupervisorComponent(java.lang.String)
	 */
	@Override
	public boolean		isSupervisorComponent(String architectureURI)
	throws Exception
	{
		// the URI of the supervisor plug-in must be the URI of the simulation
		// architecture they supervises.
		return this.isInstalled(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getSimulationManagementInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulationManagementInboundPortURI(
		String modelURI
		) throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
										getSimulationManagementInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getModelInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getModelInboundPortURI(String modelURI)
	throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
													getModelInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyComponentI#getSimulatorInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulatorInboundPortURI(String modelURI)
	throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	isInstalled(modelURI) :
				new PreconditionException("isInstalled(modelURI)");

		return ((AbstractSimulatorPlugin)this.getPlugin(modelURI)).
												getSimulatorInboundPortURI();
	}
}
// -----------------------------------------------------------------------------
