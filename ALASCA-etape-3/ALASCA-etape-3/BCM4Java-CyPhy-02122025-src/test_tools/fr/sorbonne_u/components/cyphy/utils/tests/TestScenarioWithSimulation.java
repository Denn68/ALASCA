package fr.sorbonne_u.components.cyphy.utils.tests;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.components.utils.tests.TestScenario;
import fr.sorbonne_u.components.utils.tests.TestStep;
import fr.sorbonne_u.components.utils.tests.TestStepI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

// -----------------------------------------------------------------------------
/**
 * The class <code>TestScenarioWithSimulation</code> implements a test scenario
 * to be executed in cooperation between BCM4Java components and simulators
 * defined in the DEVS simulation framework NeoSim4Java. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A test scenario describe a simulation run were some of the actions taken by
 * simulation models are imposed by the scenario. Hence, at the heart of a test
 * scenario, there is a sequence of test steps performed by a set of simulation
 * models included in the simulator and identified by their URI. Each test step
 * is defined by the class {@code SimulationTestStep}.
 * </p>
 * <p>
 * The time management used in test steps and test scenarios is based on two
 * time lines. Test steps are planned using the class {@code Instant}. A test
 * scenario has a start instant and an end instant. The time of occurrence of a
 * test step is an instant between the start and end instants. This time line of
 * instants is mapped to the time line of the simulation clock represented by
 * the class {@code Time} of NeoSim4Java. The test scenario aligns the two time
 * lines by having as input the start instant, the end instant and the start
 * time in simulated time. The end time in simulated time as well as the
 * simulation duration are deduced from former input parameters and the
 * alignment of the two time lines.
 * </p>
 * <p>
 * Each test step represents an action to be taken by some simulation model as
 * an internal transition, perhaps exporting some external events. Steps are
 * provided by an array of {@code SimulationTestStep} that must be ordered by
 * ascending instants of occurrence. To help constructing complex scenarios
 * intertwining actions performed by different simulation models, the array
 * of steps contains all of the actions in a scenario, including all simulation
 * models the scenario will involve. However, the execution of the test scenario
 * makes advances per simulation models.
 * </p>
 * <p>
 * As a test scenario is a simulation run, some set up may be necessary, mainly
 * to define simulation run parameters for the simulation models appearing in
 * the simulator. {@code  TestScenario} proposes two set up types of functions,
 * among which one must be provide at creation time:
 * </p>
 * <ul>
 * <li>a function of two parameters taking as arguments the reference to the
 *   simulator and the test scenario about to be executed, and</li>
 * <li>a function of three parameters taking the same two arguments plus
 *   a map of run parameters already created and that must be initialised
 *   on the simulator models.</li>
 * </ul>
 * <p>
 * Optional beginning and ending messages can also be provided. To execute a
 * test scenario, the standard DEVS protocol methods in models having steps to
 * performed call this class methods. First, in the class implementing the
 * simulation run, the simulator must be constructed as usual and then the
 * set up function provided by the test scenario must be called. Then, models
 * that do not perform steps defined in the scenario executes as usual. Models
 * that must perform steps defined in the scenario uses the following methods
 * of this class:
 * </p>
 * <ul>
 * <li>The methods {@code setUpSimulator} are called to set up the simulation
 *   run parameters and other configuration options.</li>
 * <li>The methods {@code getStartTime()}, to retrieve the simulated start time,
 *   and {@code getEndTime()}, to retrieve the simulated end time, to start the
 *   simulation by calling the chosen start method of NeoSim4Java.</li>
 * <li>Models having to perform test steps can retrieve the time of their
 *   next step by calling the methods {@code simulatedTimeOfNextStep} or
 *   {@code simulatedDelayToNextStep}, typically in their method
 *   {@code timeAdvance} to know whether their next internal transition in their
 *   next test step or another internal transition.</li>
 * <li>When a model must execute a test step, the method {@code generateOutput}
 *   is called in their {@code output} method to generate and export the
 *   external events of the test step.</li>
 * <li>Next, the method {@code performInternalTransition} is called by their
 *   method {@code userDefinedInternalTransition} to perform the internal
 *   transition of the test step.</li>
 * <li>Finally, the method {@code advanceToNextStep} is called to advance the
 *   test scenario for that model to its next test step. The method
 *   {@code scenarioTerminated} allows to check if the scenario is terminated
 *   for the model.</li>
 * </ul>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code testSteps != null && testSteps.length > 0}
 * invariant	{@code participantsURIs != null && participantsURIs.size() > 0}
 * invariant	{@code simulationOnly(testSteps) || clockURI != null && !clockURI.isEmpty()}
 * invariant	{@code componentsOnly(testSteps) || (!(setUp2 != null && setUp3 != null) && !(setUp2 == null && setUp3 == null))}
 * invariant	{@code startInstant != null}
 * invariant	{@code endInstant != null}
 * invariant	{@code startInstant.isBefore(endInstant)}
 * invariant	{@code componentsOnly(testSteps) || startTime != null}
 * invariant	{@code nextSteps != null}
 * invariant	{@code nextSteps.size() == participantsURIs.size()}
 * invariant	{@code nextSteps.keySet().stream().allMatch(uri -> participantsURIs.contains(uri))}
 * invariant	{@code nextSteps.values().stream().allMatch(index -> index >= 0 && index <= instance.testSteps.length)}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-10-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			TestScenarioWithSimulation
extends		TestScenario
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** when true, trace the test scenario.									*/
	public static boolean					VERBOSE = true;
	/** when true, print debugging information on the test scenario.		*/
	public static boolean					DEBUG = true;
	
	/** URI of the global simulation architecture to be created.			*/
	protected final String					globalArchitectureURI;

	/** start time of the simulation run in simulated time.					*/
	protected final Time					startTime;
	/** add the scenario run parameters to its argument.					*/
	protected final BiConsumer<TestScenario, Map<String, Object>>
											addToRunParameters;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(TestScenario instance)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(TestScenario instance)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Test scenarios with component steps only

	/**
	 * create a components only test scenario with the given test steps.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code startInstant != null}
	 * pre	{@code endInstant != null}
	 * pre	{@code startInstant.isBefore(endInstant)}
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * pre	{@code componentsOnly(testSteps)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param clockURI		URI of the clock providing the time reference for this scenario.
	 * @param startInstant	start instant of the simulation run.
	 * @param endInstant	end instant of the simulation run.
	 * @param testSteps		test steps in the scenario.
	 */
	public				TestScenarioWithSimulation(
		String clockURI,
		Instant startInstant,
		Instant endInstant,
		TestStepI[] testSteps
		) throws VerboseException
	{
		this(null,			// no beginning message
			 null,			// no ending message
			 AssertionChecking.assertTrueAndReturnOrThrow(
				clockURI != null && !clockURI.isEmpty(),
				clockURI, 
				() -> new PreconditionException(
								"clockURI != null && !clockURI.isEmpty()")),
			 startInstant,
			 endInstant,
			 null,			// no simulation architecture
			 null,			// no simulation start time
			 null,			// no simulation run parameters
			 AssertionChecking.assertTrueAndReturnOrThrow(
				testSteps != null && testSteps.length > 0 &&
					 								componentsOnly(testSteps),
				testSteps,
				() -> new PreconditionException(
								"testSteps != null && testSteps.length > 0 && "
								+ "componentsOnly(testSteps)")));
	}

	/**
	 * create a components only test scenario with the given messages, set up
	 * and test steps.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code startInstant != null}
	 * pre	{@code endInstant != null}
	 * pre	{@code startInstant.isBefore(endInstant)}
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * pre	{@code componentsOnly(testSteps)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param beginingMessage	message to be output on sysout at the beginning of the scenario.
	 * @param endingMessage		message to be output on sysout at the end of the scenario.
	 * @param clockURI			URI of the clock providing the time reference for this scenario.
	 * @param startInstant		start instant of the simulation run.
	 * @param endInstant		end instant of the simulation run.
	 * @param testSteps			test simulation steps in the scenario.
	 * @throws Exception		<i>to do</i>.
	 */
	public				TestScenarioWithSimulation(
		String beginingMessage,
		String endingMessage,
		String clockURI,
		Instant startInstant,
		Instant endInstant,
		TestStepI[] testSteps
		) throws VerboseException
	{
		this(beginingMessage,
			 endingMessage,
			 AssertionChecking.assertTrueAndReturnOrThrow(
				clockURI != null && !clockURI.isEmpty(),
				clockURI, 
				() -> new PreconditionException(
								"clockURI != null && !clockURI.isEmpty()")),
			 startInstant,
			 endInstant,
			 null,			// no simulation architecture
			 null,			// no simulation start time
			 null,			// no simulation run parameters
			 AssertionChecking.assertTrueAndReturnOrThrow(
				testSteps != null && testSteps.length > 0 &&
					 								componentsOnly(testSteps),
				testSteps,
				() -> new PreconditionException(
								"testSteps != null && testSteps.length > 0 && "
							 	+ "componentsOnly(testSteps)")));
	}

	// Test scenario with simulation

	/**
	 * create a test scenario maybe including simulation steps with the given
	 * messages, set up and test steps.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code startInstant != null}
	 * pre	{@code endInstant != null}
	 * pre	{@code startInstant.isBefore(endInstant)}
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * pre	{@code componentsOnly(testSteps) || globalSimulationArchitectureURI != null && !globalSimulationArchitectureURI.isEmpty()}
	 * pre	{@code componentsOnly(testSteps) || startTime != null}
	 * pre	{@code componentsOnly(testSteps) || addToRunParameters != null}
	 * pre	{@code simulationOnly(testSteps) || clockURI != null && !clockURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param clockURI							URI of the clock providing the time reference for this scenario.
	 * @param startInstant						start instant of the simulation run.
	 * @param endInstant						end instant of the simulation run.
	 * @param globalSimulationArchitectureURI	URI of the simulation architecture to be used in this test scenario.
	 * @param startTime							start time of the simulation run in simulated time.
	 * @param addToRunParameters				function that adds test scenario specific run parameters to an existing map of run parameters.
	 * @param testSteps							test steps in the scenario.
	 */
	public				TestScenarioWithSimulation(
		String clockURI,
		Instant startInstant,
		Instant endInstant,
		String globalSimulationArchitectureURI,
		Time startTime,
		BiConsumer<TestScenario, Map<String, Object>> addToRunParameters,
		TestStepI[] testSteps
		)
	{
		this(null, null, clockURI, startInstant, endInstant,
			 globalSimulationArchitectureURI, startTime, addToRunParameters,
			 testSteps);
	}

	/**
	 * create a test scenario maybe including simulation steps with the given
	 * messages, set up and test steps.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code startInstant != null}
	 * pre	{@code endInstant != null}
	 * pre	{@code startInstant.isBefore(endInstant)}
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * pre	{@code componentsOnly(testSteps) || globalSimulationArchitectureURI != null && !globalSimulationArchitectureURI.isEmpty()}
	 * pre	{@code componentsOnly(testSteps) || startTime != null}
	 * pre	{@code componentsOnly(testSteps) || addToRunParameters != null}
	 * pre	{@code simulationOnly(testSteps) || clockURI != null && !clockURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param beginingMessage					message to be output on sysout at the beginning of the scenario.
	 * @param endingMessage						message to be output on sysout at the end of the scenario.
	 * @param clockURI							URI of the clock providing the time reference for this scenario.
	 * @param startInstant						start instant of the simulation run.
	 * @param endInstant						end instant of the simulation run.
	 * @param globalSimulationArchitectureURI	URI of the simulation architecture to be used in this test scenario.
	 * @param startTime							start time of the simulation run in simulated time.
	 * @param addToRunParameters				function that adds test scenario specific run parameters to an existing map of run parameters.
	 * @param testSteps							test steps in the scenario.
	 */
	public				TestScenarioWithSimulation(
		String beginingMessage,
		String endingMessage,
		String clockURI,
		Instant startInstant,
		Instant endInstant,
		String globalSimulationArchitectureURI,
		Time startTime,
		BiConsumer<TestScenario, Map<String, Object>> addToRunParameters,
		TestStepI[] testSteps
		)
	{
		super(beginingMessage, endingMessage, clockURI,
			  startInstant, endInstant, testSteps);

		// Preconditions checking
		assert	startInstant != null :
				new PreconditionException("startInstant != null");
		assert	endInstant != null :
				new PreconditionException("endInstant != null");
		assert	startInstant.isBefore(endInstant) :
				new PreconditionException("startInstant.isBefore(endInstant)");
		assert	testSteps != null && testSteps.length > 0 :
				new PreconditionException(
						"testSteps != null && testSteps.length > 0");
		assert	componentsOnly(testSteps) ||
						globalSimulationArchitectureURI != null &&
								!globalSimulationArchitectureURI.isEmpty() :
				new PreconditionException(
						"componentsOnly(testSteps) || "
						+ "globalSimulationArchitectureURI != null && "
						+ "!globalSimulationArchitectureURI.isEmpty()");
		assert	componentsOnly(testSteps) || startTime != null :
				new PreconditionException(
						"componentsOnly(testSteps) || startTime != null");
		assert	componentsOnly(testSteps) || addToRunParameters != null :
				new PreconditionException(
						"componentsOnly(testSteps) || "
						+ "addToRunParameters != null");
		assert	simulationOnly(testSteps) ||
									(clockURI != null && !clockURI.isEmpty()) :
				new PreconditionException(
						"simulationOnly(testSteps) || "
						+ "(clockURI != null && !clockURI.isEmpty())");
		assert	ordered(testSteps) :
				new PreconditionException("ordered(simulationTestSteps)");

		this.globalArchitectureURI = globalSimulationArchitectureURI;
		this.startTime = startTime;
		this.addToRunParameters = addToRunParameters;

		// Invariant checking
		assert	TestScenario.implementationInvariants(this) :
				new ImplementationInvariantException(
						"TestScenario.implementationInvariants(this)");
		assert	TestScenario.invariants(this) :
				new InvariantException("TestScenario.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods common to all types of steps
	// -------------------------------------------------------------------------

	/**
	 * return true if the steps in {@code testSteps} appear in increasing order
	 * of instant of occurrence, otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param testSteps	test steps in a scenario.
	 * @return			true if the steps in {@code testSteps} appear in increasing order of instant of occurrence, otherwise false.
	 */
	public static boolean	ordered(TestStepI[] testSteps)
	{
		assert	testSteps != null && testSteps.length > 0 :
				new PreconditionException(
						"testSteps != null && testSteps.length > 0");

		boolean ret = true;
		Instant old = testSteps[0].getInstantOfOccurrence();
		for (int i = 1 ; ret && i < testSteps.length ; i++) {
			Instant current = testSteps[i].getInstantOfOccurrence();
			ret &= old.isBefore(current) || old.equals(current);
			old = current;
		}
		return ret;
	}

	/**
	 * return true if all steps are simulation tests steps, otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param testSteps	test steps in a scenario.
	 * @return			true if all steps are simulation tests steps, otherwise false.
	 */
	public static boolean	simulationOnly(TestStepI[] testSteps)
	{
		assert	testSteps != null && testSteps.length > 0 :
				new PreconditionException(
						"testSteps != null && testSteps.length > 0");

		boolean ret = true;
		for (int i = 0 ; ret  && i < testSteps.length ; i++) {
			ret = ret && (testSteps[i] instanceof SimulationTestStep);
		}
		return ret;
	}

	/**
	 * return true if all steps are component tests steps, otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code testSteps != null && testSteps.length > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param testSteps	test steps in a scenario.
	 * @return			true if all steps are simulation tests steps, otherwise false.
	 */
	public static boolean	componentsOnly(TestStepI[] testSteps)
	{
		assert	testSteps != null && testSteps.length > 0 :
				new PreconditionException(
						"testSteps != null && testSteps.length > 0");

		boolean ret = true;
		for (int i = 0 ; ret  && i < testSteps.length ; i++) {
			ret = ret && testSteps[i] instanceof TestStep &&
							!(testSteps[i] instanceof SimulationTestStep);
		}
		return ret;
	}

	/**
	 * return true if the test scenario contains simulation steps only,
	 * otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the test scenario contains simulation steps only, otherwise false.
	 */
	public boolean		isSimulationOnly()
	{
		return simulationOnly(this.testSteps);
	}

	/**
	 * return true if the test scenario contains component steps only,
	 * otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the test scenario contains component steps only, otherwise false.
	 */
	public boolean		isComponentsOnly()
	{
		return componentsOnly(this.testSteps);
	}

	/**
	 * return the simulated start time of this test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the simulated start time of this test scenario.
	 */
	public Time			getStartTime()
	{
		return this.startTime;
	}

	/**
	 * return the simulated end time of this test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the simulated end time of this test scenario.
	 */
	public Time			getEndTime()
	{
		return this.startTime.add(
					TimeUtils.betweenInDuration(startInstant,
												endInstant,
												this.startTime.getTimeUnit()));
	}

	// -------------------------------------------------------------------------
	// Methods used in simulation test steps
	// -------------------------------------------------------------------------

	/**
	 * return true if the scenario has a {@code addToRunParameters} function,
	 * otherwise false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the scenario has a {@code setUp3} set up function, otherwise false.
	 */
	public boolean		hasRunParameters()
	{
		return this.addToRunParameters != null;
	}

	/**
	 * add the run parameters for this test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param simParams	map of simulation run parameters.
	 */
	public void			addToRunParameters(
		Map<String, Object> simParams
		)
	{
		assert	simParams != null :
				new PreconditionException("simParams != null");

		this.addToRunParameters.accept(this, simParams);
	}

	/**
	 * return the delay until the next test step for {@code m} in simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code occurrence != null && (this.getStartInstant().isBefore(occurrence) || this.getStartInstant().equals(occurrence))}
	 * post	{@code return != null && getStartTime().lessThanOrEqual(return)}
	 * </pre>
	 *
	 * @param occurrence	an instant in the simulation.
	 * @return				the simulated time corresponding to {@code occurrence}.
	 */
	protected Time		toSimulatedTime(Instant occurrence)
	{
		assert	occurrence != null &&
					(this.getStartInstant().isBefore(occurrence)
							|| this.getStartInstant().equals(occurrence)) :
				new PreconditionException(
						"occurrence != null && (this.getStartInstant()."
						+ "isBefore(occurrence) || this.getStartInstant()."
						+ "equals(occurrence))");

		Duration d =
				TimeUtils.betweenInDuration(this.startInstant,
											occurrence,
											this.getStartTime().getTimeUnit());

		return this.startTime.add(d);
	}

	/**
	 * return the simulated time at which the next test step for {@code m}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null && atomicModelAppearsIn(m.getURI())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	an atomic model performing actions in the current test scenario.
	 * @return	the time at which the next test step for {@code m} in simulated time.
	 */
	public Time			simulatedTimeOfNextStep(AtomicModel m)
	{
		if (!this.scenarioTerminated(m.getURI())) {
			return this.toSimulatedTime(this.getInstantOfNextStep(m.getURI()));
		} else {
			return Time.INFINITY;
		}
	}

	/**
	 * return the delay until the next test step for {@code m} in simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null && atomicModelAppearsIn(m.getURI())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m	an atomic model performing actions in the current test scenario.
	 * @return	the delay until the next test step for {@code m} in simulated time.
	 */
	public Duration		simulatedDelayToNextStep(AtomicModel m)
	{
		assert	m != null && this.entityAppearsIn(m.getURI()) :
				new PreconditionException(
						"m != null && atomicModelAppearsIn(m.getURI())");

		if (!this.scenarioTerminated(m.getURI())) {
			return this.simulatedTimeOfNextStep(m).
											subtract(m.getCurrentStateTime());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * generate the events to be output by the atomic simulation model {@code m}
	 * at the time {@code m.getTimeOfNextEvent()} in this test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null && entityAppearsIn(m.getURI())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m		the atomic model on which the internal transition must be executed.
	 * @return		a list of events to be output by the simulation model.
	 */
	public ArrayList<EventI>	generateOutput(AtomicModel m)
	{
		assert	m != null && this.entityAppearsIn(m.getURI()) :
				new PreconditionException(
						"m != null && entityAppearsIn(m.getURI())");
		TestStepI testStep = this.testSteps[this.nextSteps.get(m.getURI())];
		assert	testStep instanceof SimulationTestStep :
				new NeoSim4JavaException(
						"Precondition violation: testStep instanceof "
						+ "SimulationTestStep");

		return ((SimulationTestStep)testStep).generateOutput(m);
	}

	/**
	 * perform the next internal transition for the atomic model {@code m} in
	 * this test scenario.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code m != null && entityAppearsIn(m.getURI())}
	 * pre	{@code !scenarioTerminated(m.getURI())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param m			the atomic model on which the internal transition must be executed.
	 */
	public void			performInternalTransition(AtomicModel m)
	{
		assert	m != null && this.entityAppearsIn(m.getURI()) :
				new PreconditionException(
						"m != null && entityAppearsIn(m.getURI())");
		assert	!this.scenarioTerminated(m.getURI()) :
				new PreconditionException("!scenarioTerminated(m.getURI())");

		TestStepI testStep = this.testSteps[this.nextSteps.get(m.getURI())];
		assert	testStep instanceof SimulationTestStep :
				new NeoSim4JavaException(
						"Precondition violation: testStep instanceof "
						+ "SimulationTestStep");

		((SimulationTestStep)testStep).performInternalTransition(m);
	}
}
// -----------------------------------------------------------------------------
