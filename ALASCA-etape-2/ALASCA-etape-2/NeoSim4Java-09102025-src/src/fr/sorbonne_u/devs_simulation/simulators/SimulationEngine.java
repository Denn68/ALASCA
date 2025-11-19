package fr.sorbonne_u.devs_simulation.simulators;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.MessageLoggingI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;
import fr.sorbonne_u.devs_simulation.utils.Pair;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationEngine</code> defines the basic behaviour
 * of a DEVS simulator declared by the interface <code>SimulatorI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A DEVS simulation engine provides an implementation of a simulation algorithm
 * that complies with the DEVS protocol. It is linked to a DEVS simulation model
 * through the method <code>setSimulatedModel</code>. It then can perform
 * simulation runs by calling <code>doStandAloneSimulation</code> when its model
 * is self-contained or is a coupled model at the root of a DEVS models
 * hierarchy and the simulation algorithm is based on the DEVS standard
 * top-down coordination approach. Simulation runs can also be launched by
 * calling <code>startCollaborativeSimulation</code> when the
 * simulation model is a coupled one and the coordination algorithm rather use
 * a peer-to-peer approach like the ones based on using the real time as the
 * simulation time. Simulations end either by reaching their end simulation
 * time or by calling <code>stopSimulation</code>.
 * </p>
 * 
 * <p>
 * The implementation provided here follows the spirit of the description
 * appearing in:
 * </p>
 * <p>
 * H. Vangheluwe, Discrete Event System Specification (DEVS) formalism,
 * courseware, 2001.
 * </p>
 * 
 * <p>
 * Note: the implementation in non-reentrant, hence simulation runs must be
 * executed in sequence.
 * </p>
 * 
 * <p>
 * A baseline DEVS simulator observe a protocol built around the following
 * operations:
 * </p>
 * <ul>
 * <li>internal event step: processing an internal event at a given simulated
 *   time;</li>
 * <li>external event step: processing a given external event at a given
 *   simulated time.</li>
 * </ul>
 * 
 * <p>
 * See <code>AtomicModelI</code> for a description of internal and external
 * steps. A stand alone simulation is run by iterating internal steps until the
 * end of the simulation. A modular concurrent or distributed simulation
 * requires the exchange of external events, the coordination of clock advances
 * to always execute the next event that occurs and the execution of external
 * event steps when required.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code SIMULATION_STEP_SLEEP_TIME >= 0}
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	SimulationEngine
implements	SimulatorI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	// Creation time information

	/** model to be executed by the simulator.								*/
	protected ModelI				simulatedModel;

	// Composition time information

	/** Local coordination engine for this simulator, or null if
	 *  it stands alone or the parent is remote.							*/
	protected CoordinatorI			parent;

	// Run time information

	/** true if the simulation has been started and has not ended.			*/
	protected boolean				isRunning;
	/** time of the beginning of the simulation.							*/
	protected Time					simulationStartTime;
	/** time at which the simulation must end.								*/
	protected Time					simulationEndTime;
	/** true if the simulation has been stopped (from outside).				*/
	protected boolean				stoppedSimulation;
	/** time at which the last event has been executed.						*/
	protected Time					timeOfLastEvent;
	/** time at which the next event, internal, external or confluent,
	 *  must be executed.													*/
	protected Time					timeOfNextEvent;
	/** Duration until the time of next event, i.e. the time of next
	 *  event minus the time of the current state.							*/
	protected Duration				nextTimeAdvance;
	/** sleep time slowing down the simulation steps for demos.		 		*/
	public static long				SIMULATION_STEP_SLEEP_TIME = 0L;

	// Logging, tracing and debugging

	/** logger for this simulation engine.									*/
	protected MessageLoggingI 		logger;
	/** Debugging level controlling the execution traces.					*/
	protected int					debugLevel;

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
	protected static boolean	implementationInvariants(SimulationEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
					SIMULATION_STEP_SLEEP_TIME >= 0,
					SimulationEngine.class,
					"SIMULATION_STEP_SLEEP_TIME >= 0");		
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
	protected static boolean	invariants(SimulationEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = SimulatorI.invariants(instance);
		ret &= staticInvariants();		
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a DEVS simulation engine either standalone or collaborative
	 * and, if collaborative, using a generated external inbound port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				SimulationEngine()
	{
		super();

		this.initialise();

		assert	SimulationEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"SimulationEngine.implementationInvariants(this)");
		assert	SimulationEngine.invariants(this) :
				new NeoSim4JavaException("SimulationEngine.invariants(this)");
	}

	/**
	 * initialise the simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		initialise()
	{
		this.parent = null;
		this.isRunning = false;
		this.simulatedModel = null;
		this.simulationStartTime = null;
		this.simulationEndTime = null;
		this.stoppedSimulation = false;
		this.timeOfLastEvent = null;
		this.timeOfNextEvent = null;
		this.nextTimeAdvance = null;
		this.debugLevel = 0;
	}

	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isParentSet()
	 */
	@Override
	public boolean		isParentSet()
	{
		return this.parent != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setParent(fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI)
	 */
	@Override
	public void			setParent(CoordinatorI c)
	{
		if (isParentSet()) {
			System.out.println("****************** SimulationEngine#setParent: "
							   + this.simulatedModel.getURI());
		}
//		assert	!isParentSet() :
//				new NeoSim4JavaAssertionError("Precondition violation: !isParentSet()");
		this.parent = c;
		assert	isParentSet() :
				new NeoSim4JavaException("Postcondition violation: "
						+ "isParentSet()");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	{
		assert	simulatedModel != null :
				new NeoSim4JavaException("Precondition violation: "
										+ "simulatedModel != null");
		assert	!this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isModelSet()");

		this.simulatedModel = simulatedModel;

		assert	SimulationEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"SimulationEngine.implementationInvariants(this)");
		assert	SimulationEngine.invariants(this) :
				new NeoSim4JavaException("SimulationEngine.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isModelSet()
	 */
	@Override
	public boolean		isModelSet()
	{
		return this.simulatedModel != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getSimulatedModel()
	 */
	@Override
	public ModelI		getSimulatedModel()
	{
		return this.simulatedModel;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	!this.isSimulationInitialised() :
				new NeoSim4JavaException("Precondition violation: "
					+ "!isSimulationInitialised()");
		TimeUnit tu = this.getSimulatedModel().getSimulatedTimeUnit();
		assert	simulationDuration != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration != null");
		assert	simulationDuration.getTimeUnit().equals(tu) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration.getTimeUnit().equals("
						+ "getSimulatedModel().getSimulatedTimeUnit())");
		assert	simulationDuration.greaterThan(Duration.zero(tu)) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration.greaterThan("
						+ "Duration.zero(getSimulatedModel()."
						+ "getSimulatedTimeUnit()))");

		this.initialiseSimulation(Time.zero(tu), simulationDuration);

		assert	!this.getSimulatedModel().isRoot() || this.isSimulationInitialised();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		)
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	!this.isSimulationInitialised() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isSimulationInitialised()");
		TimeUnit tu = this.getSimulatedModel().getSimulatedTimeUnit();
		assert	simulationStartTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime != null");
		assert	simulationStartTime.getTimeUnit().equals(tu) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime.getTimeUnit().equals("
						+ "getSimulatedModel().getSimulatedTimeUnit())");
		assert	simulationStartTime.greaterThanOrEqual(Time.zero(tu)) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime.greaterThanOrEqual("
						+ "Time.zero(getSimulatedModel()."
						+ "getSimulatedTimeUnit()))");
		assert	simulationDuration != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration != null");
		assert	simulationDuration.getTimeUnit().equals(tu) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration.getTimeUnit().equals("
						+ "getSimulatedModel().getSimulatedTimeUnit())");
		assert	simulationDuration.greaterThan(Duration.zero(tu)) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration.greaterThan("
						+ "Duration.zero(getSimulatedModel()."
						+ "getSimulatedTimeUnit()))");

		this.stoppedSimulation = false;
		this.simulationStartTime = simulationStartTime;
		this.timeOfLastEvent = this.simulationStartTime;
		this.simulationEndTime =
						this.simulationStartTime.add(simulationDuration);
		this.isRunning = true;

		assert	!this.getSimulatedModel().isRoot() || this.isSimulationInitialised();
		// The postcondition getNextTimeAdvance().equals(getTimeOfNextEvent().
		// subtract(getTimeOfLastEvent())) is enforced by subclasses that must
		// override this method to complete it with their implementation
		// dependent operations and conditions

		assert	SimulationEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"SimulationEngine.implementationInvariants(this)");
		assert	SimulationEngine.invariants(this) :
				new NeoSim4JavaException("SimulationEngine.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised()
	{
		return	this.isModelSet() &&
				this.getTimeOfStart() != null &&
				this.getTimeOfLastEvent() != null &&
				this.getSimulationEndTime() != null &&
				this.isRunning;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesTimeInitialised()
	 */
	@Override
	public boolean		allModelVariablesTimeInitialised()
	{
		return this.simulatedModel.allModelVariablesTimeInitialised();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesInitialised()
	 */
	@Override
	public boolean		allModelVariablesInitialised()
	{
		return this.simulatedModel.allModelVariablesInitialised();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return this.simulatedModel.useFixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		return this.simulatedModel.fixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent()
	{
		return this.timeOfLastEvent;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent()
	{
		return this.timeOfNextEvent;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance()
	{
		return this.nextTimeAdvance;
	}

	// -------------------------------------------------------------------------
	// Simulation run management methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart()
	{
		return this.simulationStartTime;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime()
	{
		return this.simulationEndTime;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		assert	simParams != null && simParams.size() > 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "simParams != null && simParams.size() > 0");
		assert	!isSimulationRunning() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isSimulationRunning()");

		getSimulatedModel().setSimulationRunParameters(simParams);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double simulationStartTime,
		double simulationDuration
		)
	{
		assert	simulationStartTime >= 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime >= 0.0");
		assert	simulationStartTime < Double.POSITIVE_INFINITY :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime < Double.POSITIVE_INFINITY");
		assert	simulationDuration > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration > 0.0");
		assert	simulationDuration < Double.POSITIVE_INFINITY :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration < Double.POSITIVE_INFINITY");

		TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit();
		this.initialiseSimulation(new Time(simulationStartTime, tu),
								  new Duration(simulationDuration, tu));

		if (this.hasDebugLevel(2)) {
			System.out.println("#######################################");
			this.showCurrentState("", Duration.zero(tu));
			System.out.println("#######################################");
		}
		if (this.hasDebugLevel(1)) {
			System.out.println("------------------------------------------"
							   + "--------------------------------------");
		}
		while (!this.stoppedSimulation && this.timeOfNextEvent != null &&
				this.timeOfNextEvent.lessThanOrEqual(this.simulationEndTime)) {
			// loop invariant
			assert	this.nextTimeAdvance.equals(Duration.INFINITY) ||
						this.nextTimeAdvance.equals(this.timeOfNextEvent.
											subtract(this.timeOfLastEvent)) :
					new NeoSim4JavaException("loop invariant: "
							+ "this.nextTimeAdvance.equals(Duration.INFINITY) ||"
							+ "this.nextTimeAdvance.equals(this.timeOfNextEvent."
							+ "subtract(this.timeOfLastEvent))");
			this.produceOutput(this.timeOfNextEvent);
			this.internalEventStep();
			if (this.hasDebugLevel(2)) {
				System.out.println("#######################################");
				this.showCurrentState("", Duration.zero(tu));
				System.out.println("#######################################");
			}
			try {
				Thread.sleep(SimulationEngine.SIMULATION_STEP_SLEEP_TIME);
			} catch (InterruptedException e) {
				throw new RuntimeException(e) ;
			}
			if (this.hasDebugLevel(1)) {
				System.out.println("-----------------------------------------"
								+ "---------------------------------------");
			}
		}
		this.endSimulation(this.simulationEndTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		)
	{
		throw new RuntimeException("The method startRTSimulation must be defined "
								   + "for real time simulation engines.");
	}

	/**
	 * compute the next event to be simulated and update the internal state
	 * accordingly.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		computeNextEventToBeSimulated()
	{
		// Default is to do nothing.
		// Concerns coordination engines but this definition avoids to test
		// what type of engines is called.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.isRunning = false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning()
	{
		return this.isRunning;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation()
	{
		assert	this.isSimulationRunning() :
				new NeoSim4JavaException("Precondition violation: "
												+ "isSimulationRunning()");

		this.isRunning = false;
		this.stoppedSimulation = true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation()
	{
		assert	!this.isSimulationRunning() :
				new NeoSim4JavaException("Precondition violation: "
												+ "!isSimulationRunning()");

		this.simulationStartTime = null;
		this.timeOfLastEvent = null;
		this.simulationEndTime = null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return this.simulatedModel.getFinalReport();
	}

	// -------------------------------------------------------------------------
	// Logging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isLoggerSet()
	 */
	@Override
	public boolean		isLoggerSet()
	{
		return this.logger != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setLogger(fr.sorbonne_u.devs_simulation.simulators.interfaces.MessageLoggingI)
	 */
	@Override
	public void			setLogger(MessageLoggingI logger)
	{
		assert	logger != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "logger != null");

		this.logger = logger;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#logMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void			logMessage(String modelURI, String message)
	{
		if (this.isLoggerSet()) {
			this.logger.logMessage(modelURI, message);
		}
	}

	/**
	 * log a message through the logger or do nothing if no logger is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param message	message to be logged.
	 */
	protected void		logMessage(String message)
	{
		try {
			this.logMessage(this.simulatedModel.getURI(), message);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode()
	{
		if (this.debugLevel > 0) {
			this.debugLevel = 0;
		} else {
			this.debugLevel = 1;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn()
	{
		return this.debugLevel > 0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel)
	{
		assert	newDebugLevel >= 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "newDebugLevel >= 0");

		this.debugLevel = newDebugLevel;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel)
	{
		assert	debugLevel >= 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "debugLevel >= 0");

		return this.debugLevel >= debugLevel;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	{
		if (this.isModelSet()) {
			return this.simulatedModel.modelAsString(indent);
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString()
	{
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		if (this.simulatedModel == null) {
			return name + "(" + null + ")";
		} else {
			return name + "(" + this.simulatedModel.getURI() + ")";
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		)
	{
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		System.out.println(indent + "---------------------------------");
		System.out.println(indent + name + " " + this.simulatedModel.getURI());
		System.out.println(indent + "---------------------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "---------------------------------");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void		showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		System.out.println(indent + "isRunning = " + this.isRunning);
		System.out.println(indent + "timeOfStart = " +
								this.simulationStartTime.getSimulatedTime());
		System.out.println(indent + "simulationEndTime = " +
								this.simulationEndTime.getSimulatedTime());
		System.out.println(indent + "stoppedSimulation = " +
								this.stoppedSimulation);
		System.out.println(indent + "timeOfLastEvent = " +
								this.timeOfLastEvent.getSimulatedTime());
		System.out.println(indent + "elapsedTime = " +
								elapsedTime.getSimulatedDuration());
		System.out.println(indent + "timeOfNextEvent = " +
								this.timeOfNextEvent.getSimulatedTime());
		System.out.println(indent + "nextTimeAdvance = " +
								this.nextTimeAdvance.getSimulatedDuration());

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String	toString()
	{
		return this.getClass().getCanonicalName() + "("
				+ (this.getSimulatedModel() != null ?
						this.getSimulatedModel().getURI() :
						"unknown model")
				+ ")";
	}
}
// -----------------------------------------------------------------------------
