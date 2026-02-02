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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoordinationEngine</code> defines the basic behaviours
 * of a DEVS coordinator declared by the interface
 * <code>CoordinationI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * A DEVS coordination engine is first linked to a DEVS simulation model through
 * the method <code>setSimulatedModel</code>. It then can do simulations by
 * calling <code>doStandAloneSimulation</code> or
 * <code>startCollaborativeSimulation</code>. Simulations end
 * either by reaching their end simulation time or by calling
 * <code>stopSimulation</code>.
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
 * Note: the implementation in non-reentrant, hence simulation runs using
 * this one instantiation of a composed model must be executed in sequence.
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
 * <p><strong>Implementatio Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code !coordinatedEnginesSet() || Stream.of(coordinatedEngines).allMatch(s -> ((CoupledModelI)simulatedModel).isSubmodel(s.getSimulatedModel().getURI()) & coordinatedURI2index.containsKey(s.getSimulatedModel().getURI()))}
 * invariant	{@code !coordinatedEnginesSet() || coordinatedURI2index.entrySet().stream().allMatch(e -> ((CoupledModelI)simulatedModel).isSubmodel(e.getKey()) && Stream.of(coordinatedEngines).anyMatch(s -> s.getSimulatedModel().getURI().equals(e.getKey()) && e.getValue() >= 0 && e.getValue() < coordinatedEngines.length))}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoordinationEngine
extends		SimulationEngine
implements	CoordinatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	// Composition time information

	private static final long		serialVersionUID = 1L;
	/** Simulation engines coordinated by this coordination engine.			*/
	protected SimulatorI[]			coordinatedEngines;
	/** Map from URI of the models enacted by coordinated engines to the
	 *  index of the engine in the array <code>coordinatedEngines</code>. 	*/
	protected Map<String,Integer>	coordinatedURI2index;

	// Run time information

	/** Elapsed times of subengines since their last internal event (i.e.,
	 *  <code>currentStateTime</code>; conceptually, the time of the
	 *  last internal event plus the elapsed time corresponds to the
	 *  current global simulation time.										*/
	protected Duration[]			elapsedTimes;
	/** The URI of the submodel that will need to execute the next event.	*/
	protected String				submodelOfNextEventURI;
	/** Set of URIs of submodels that have external events waiting
	 * 	executed.															*/
	protected Set<String>			activeModelURIs;
	/** Random number generator used to break the ties when several
	 *  submodels can perform an internal transition at the same time of
	 *  next internal transition simulation time. 							*/
	protected final Random			randomGenerator;

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
	protected static boolean	implementationInvariants(CoordinationEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					!instance.coordinatedEnginesSet() ||
						Stream.of(instance.coordinatedEngines).allMatch(
							s -> ((CoupledModelI)instance.simulatedModel).
									isSubmodel(s.getSimulatedModel().getURI())
								 && instance.coordinatedURI2index.containsKey(
										 	s.getSimulatedModel().getURI())),
					CoordinationEngine.class,
					instance,
					"!coordinatedEnginesSet() || Stream.of(coordinatedEngines)."
					+ "allMatch(s -> ((CoupledModelI)simulatedModel).isSubmodel("
					+ "s.getSimulatedModel().getURI()) & coordinatedURI2index."
					+ "containsKey(s.getSimulatedModel().getURI()))");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.coordinatedEnginesSet() ||
					instance.coordinatedURI2index.entrySet().stream().allMatch(
						e -> ((CoupledModelI)instance.simulatedModel).
									isSubmodel(e.getKey()) 
							 && Stream.of(instance.coordinatedEngines).anyMatch(
									s -> s.getSimulatedModel().getURI().
										 					equals(e.getKey())
										 && e.getValue() >= 0
										 && e.getValue() <
										 	instance.coordinatedEngines.length)),
					CoordinationEngine.class,
					instance,
					"!coordinatedEnginesSet() || coordinatedURI2index.entrySet()."
					+ "stream().allMatch(e -> ((CoupledModelI)simulatedModel)."
					+ "isSubmodel(e.getKey()) && Stream.of(coordinatedEngines)."
					+ "anyMatch(s -> s.getSimulatedModel().getURI().equals("
					+ "e.getKey()) && e.getValue() >= 0 && e.getValue() < "
					+ "coordinatedEngines.length))");
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
	protected static boolean	invariants(CoordinationEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a coordination engine waiting for static initialisation by
	 * setting its associated coupled model and its set of coordinated
	 * engines.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public 				CoordinationEngine()
	{
		super();

		// Coupled model initialisation
		this.randomGenerator = new Random(System.currentTimeMillis());
		this.coordinatedEngines = null;

		// Invariant checking
		assert	CoordinationEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"CoordinationEngine.implementationInvariants(this)");
		assert	CoordinationEngine.invariants(this) :
				new NeoSim4JavaException("CoordinationEngine.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Static information related methods
	// -------------------------------------------------------------------------

	/**
	 * set the simulated model, ensuring that it is a coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedModel instanceof CoupledModelI}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	{
		assert	simulatedModel instanceof CoupledModelI :
				new NeoSim4JavaException("Precondition violation: ");

		super.setSimulatedModel(simulatedModel);
	}

	// -------------------------------------------------------------------------
	// Composition related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#setCoordinatedEngines(fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI[])
	 */
	@Override
	public void			setCoordinatedEngines(SimulatorI[] coordinatedEngines)
	{
		assert	!coordinatedEnginesSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!coordinatedEnginesSet()");
		assert	isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	coordinatedEngines != null && coordinatedEngines.length > 1 :
				new NeoSim4JavaException("Precondition violation: "
						+ "coordinatedEngines != null && "
						+ "coordinatedEngines.length > 1");
		assert	Stream.of(coordinatedEngines).allMatch(
					ce -> ce != null && ((CoupledModelI)simulatedModel).
											isSubmodel(ce.getSimulatedModel().
																	getURI())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Stream.of(coordinatedEngines).allMatch("
						+ "ce -> ce != null && ((CoupledModelI)simulatedModel)."
						+ "isSubmodel(ce.getSimulatedModel().getURI()))");

		this.coordinatedEngines = new SimulatorI[coordinatedEngines.length];
		this.coordinatedURI2index = new HashMap<String,Integer>();
		for (int i = 0 ; i < coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i] = coordinatedEngines[i];
			this.coordinatedURI2index.put(
						coordinatedEngines[i].getSimulatedModel().getURI(), i);
		}

		assert	coordinatedEnginesSet() :
				new NeoSim4JavaException("Postcondition violation: "
											+ "coordinatedEnginesSet()");

		// Invariant checking
		assert	CoordinationEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"CoordinationEngine.implementationInvariants(this)");
		assert	CoordinationEngine.invariants(this) :
				new NeoSim4JavaException("CoordinationEngine.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#coordinatedEnginesSet()
	 */
	@Override
	public boolean		coordinatedEnginesSet()
	{
		return this.coordinatedEngines != null;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * provides a way for the subclass to call {@code SimulationEngine}
	 * bypassing this level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param startTime				time at which the simulation must start.
	 * @param simulationDuration	duration of the simulation to be launched.
	 */
	protected void		initialiseSimulationBypass(
		Time startTime,
		Duration simulationDuration
		)
	{
		super.initialiseSimulation(startTime, simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		)
	{
		for(int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].
						initialiseSimulation(startTime, simulationDuration);
		}
		if (this.simulatedModel.isRoot() &&
						this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.fixpointInitialiseVariables();
		}

		this.elapsedTimes = new Duration[this.coordinatedEngines.length];
		TimeUnit tu =  this.simulatedModel.getSimulatedTimeUnit();
		for (int i = 0 ; i < this.elapsedTimes.length ; i++) {
				this.elapsedTimes[i] = Duration.zero(tu);
		}

		this.activeModelURIs = new HashSet<String>();
		this.timeOfNextEvent =
						this.coordinatedEngines[0].getTimeOfNextEvent();
		this.submodelOfNextEventURI =
				this.coordinatedEngines[0].getSimulatedModel().getURI();
		for (int i = 1 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i].getTimeOfNextEvent().
											lessThan(this.timeOfNextEvent)) {
				this.timeOfNextEvent =
						this.coordinatedEngines[i].getTimeOfNextEvent();
				this.submodelOfNextEventURI =
						this.coordinatedEngines[i].getSimulatedModel().getURI();
			}
		}
		this.nextTimeAdvance =
						this.timeOfNextEvent.subtract(startTime);

		super.initialiseSimulation(startTime, simulationDuration);

		// Implementation postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	this.coordinatedEngines[i].getTimeOfLastEvent().
												add(this.elapsedTimes[i]).
									equals(this.getTimeOfLastEvent());
		}
		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised()
	{
		boolean ret = super.isSimulationInitialised();
		ret &= this.elapsedTimes != null;
		for (int i = 0 ; ret && i < this.coordinatedEngines.length ; i++) {
			ret &= this.coordinatedEngines[i].isSimulationInitialised();
			ret &= this.elapsedTimes[i] != null;
		}
		ret &= this.submodelOfNextEventURI != null;
		ret &= this.activeModelURIs != null;
		ret &= this.getTimeOfNextEvent() != null;
		ret &= this.getNextTimeAdvance() != null;
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep()
	{
		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
				"CoordinationEngine#internalEventStep "
							+ this.getSimulatedModel().getURI() + " "
							+ this.submodelOfNextEventURI + " "
							+ this.getTimeOfNextEvent());
		}

		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getNextTimeAdvance().equals("
						+ "getTimeOfNextEvent().subtract("
						+ "getTimeOfLastEvent()))");

		int index = this.coordinatedURI2index.get(this.submodelOfNextEventURI);

		assert	getTimeOfNextEvent().equals(
							coordinatedEngines[index].getTimeOfNextEvent()) :
				new NeoSim4JavaException("Implementation condition violation: "
						+ "getTimeOfNextEvent().equals("
						+ "coordinatedEngines[index].getTimeOfNextEvent())");

		// Perform the internal transition of the submodel of next event
		TimeUnit tu = this.getSimulatedModel().getSimulatedTimeUnit();
		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getTimeOfLastEvent());
		this.timeOfLastEvent = this.getTimeOfNextEvent();
		Set<String> updated = new HashSet<String>();
		this.coordinatedEngines[index].internalEventStep();

		// update the elapsed time for the model of next event to zero as
		// it just advanced its time to the current time and note in updated
		// that this value has been updated in the current internal event step
		this.elapsedTimes[index] = Duration.zero(tu);
		updated.add(this.submodelOfNextEventURI);

		// Active submodels are the ones that have received external events
		// from the execution of the current internal event step. Make each of
		// them execute these external events, if any.
		if (this.activeModelURIs.size() > 0) {
			// inform the parent model that this coupled model and its
			// descendant all have executed their external events of the current
			// internal event step
			HashSet<String> toPerformExternalStep =
										new HashSet<>(this.activeModelURIs);
			for (String uri : toPerformExternalStep) {
				// perform the external event step for each of the 
				int i = this.coordinatedURI2index.get(uri);
				// the execution of the external step by a submodel will
				// notify this coordination engine to remove its URI from
				// this.activeModelURIs
				this.coordinatedEngines[i].externalEventStep(
								(updated.contains(uri) ?
									Duration.zero(tu)
								:	this.elapsedTimes[i].add(elapsedTime)
								));
				// update the elapsed time of these models to zero, as they
				// just performed an external event step and note in updated
				// that this value has been updated in the current internal
				// event step
				this.elapsedTimes[i] = Duration.zero(tu);
				updated.add(uri);
			}
		}
		// all submodels have notified their external step done, removing their
		// URI from this.activeModelURIs
		assert	this.activeModelURIs.isEmpty() :
				new NeoSim4JavaException("activeModelURIs.isEmpty()");

		// for all submodels that did not perform an internal or external event
		// step, add the current elapsed time to the local elapsed time kept by
		// this coordination engine
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			String uri =
					this.coordinatedEngines[i].getSimulatedModel().getURI();
			if (!updated.contains(uri)) {
				this.elapsedTimes[i] = this.elapsedTimes[i].add(elapsedTime);
			}
		}
		// prepare for the next event to be performed
		this.computeNextEventToBeSimulated();

		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
					"CoordinationEngine#internalEventStep 2 "
						+ this.getSimulatedModel().getURI() + " "
						+ this.getTimeOfLastEvent() + " "
						+ this.getTimeOfNextEvent());
		}

		// Implementation postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	coordinatedEngines[i].getTimeOfLastEvent().
														add(elapsedTimes[i]).
						equals(getTimeOfLastEvent());
		}
		if (!this.getTimeOfNextEvent().equals(Time.INFINITY)) {
			assert	getTimeOfNextEvent().subtract(getTimeOfLastEvent()).
												equals(getNextTimeAdvance()) :
					new NeoSim4JavaException("Postcondition violation: "
									+ "getTimeOfNextEvent().subtract("
									+ "getTimeOfLastEvent())."
									+ "equals(getNextTimeAdvance())");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
					"CoordinationEngine#hasReceivedExternalEvents "
					+ this.getSimulatedModel().getURI() + " from " + modelURI);
		}

		this.activeModelURIs.add(modelURI);
		if (!this.getSimulatedModel().isRoot()) {
			this.parent.hasReceivedExternalEvents(
										this.getSimulatedModel().getURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	{
		assert	this.coordinatedURI2index.keySet().contains(modelURI);
		assert	this.activeModelURIs.contains(modelURI);

		this.activeModelURIs.remove(modelURI);
		if (!this.getSimulatedModel().isRoot() &&
											this.activeModelURIs.isEmpty()) {
			this.parent.hasPerformedExternalEvents(
											this.getSimulatedModel().getURI());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		int index =
			this.coordinatedURI2index.get(this.submodelOfNextEventURI);
		this.coordinatedEngines[index].produceOutput(current);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#computeNextEventToBeSimulated()
	 */
	@Override
	protected void		computeNextEventToBeSimulated()
	{
		Duration ta = this.coordinatedEngines[0].getNextTimeAdvance().
											subtract(this.elapsedTimes[0]);
		Vector<Integer> currentSet = new Vector<Integer>();
		currentSet.add(0);
		for (int i = 1 ; i < this.coordinatedEngines.length ; i++) {
			Duration tmp =
				this.coordinatedEngines[i].getNextTimeAdvance().
											subtract(this.elapsedTimes[i]);
			if (tmp.equals(ta)) {
				currentSet.add(i);
			} else if (tmp.lessThan(ta)) {
				currentSet.clear();
				ta = tmp;
				currentSet.add(i);
			}
		}
		this.nextTimeAdvance = ta;
		this.submodelOfNextEventURI = null;
		if (ta.lessThan(Duration.INFINITY)) {
			if (currentSet.size() > 1) {
				String[] candidates = new String[currentSet.size()];
				for (int i = 0 ; i < currentSet.size() ; i++) {
					candidates[i] =
						this.coordinatedEngines[currentSet.get(i)].
												getSimulatedModel().getURI();
				}
				this.submodelOfNextEventURI =
					((CoupledModel)this.simulatedModel).select(candidates);
			} else {
				this.submodelOfNextEventURI =
						this.coordinatedEngines[currentSet.get(0)].
												getSimulatedModel().getURI();
			}
			this.timeOfNextEvent = this.timeOfLastEvent.add(ta);
		} else {
			this.timeOfNextEvent = Time.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	{
		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
									"CoordinationEngine>>externalEventStep "
									+ this.simulatedModel.getURI());
		}

		// If a coordination engine is called to execute external events but
		// its list of active models is empty, it means that it is the
		// coordination engine that just run its internal step so the external
		// transitions of its submodels have been executed as part of it.

		Set<String> updated = new HashSet<String>();
		if (!this.activeModelURIs.isEmpty()) {
			this.timeOfLastEvent = this.timeOfLastEvent.add(elapsedTime);
			for (String uri : this.activeModelURIs) {
				int index = this.coordinatedURI2index.get(uri);
				this.coordinatedEngines[index].externalEventStep(
							(updated.contains(uri) ?
								Duration.zero(
									this.simulatedModel.getSimulatedTimeUnit())
							:	this.elapsedTimes[index].add(elapsedTime)
							));
				this.elapsedTimes[index] =
							Duration.zero(
									this.simulatedModel.getSimulatedTimeUnit());
				updated.add(uri);
			}
			for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
				if (!updated.contains(this.coordinatedEngines[i].
												getSimulatedModel().getURI())) {
					this.elapsedTimes[i] =
									this.elapsedTimes[i].add(elapsedTime);
				}
			}
			this.activeModelURIs.clear();
			this.computeNextEventToBeSimulated();
		} else {
			// This happens when a submodel had to perform an external
			// event, notified its ancestor models which call for an
			// an external transition that was already performed by their
			// parent model.
			if (this.hasDebugLevel(2)) {
				this.simulatedModel.logMessage(
							"CoupledModel>>externalTransition " +
							this.getSimulatedModel().getURI() +
							" has no active submodel.\n");
			}
		}

		// Implementation postconditions
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			assert	((SimulatorI)this.coordinatedEngines[i]).
							getTimeOfLastEvent().add(this.elapsedTimes[i]).
						equals(this.getTimeOfLastEvent());
		}
		assert	getNextTimeAdvance().equals(
					getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].endSimulation(endTime);
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation()
	{
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			if (this.coordinatedEngines[i] instanceof SimulationManagementI) {
				this.coordinatedEngines[i].finaliseSimulation();
			}
		}
		super.finaliseSimulation();
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString()
	{
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		String ret = name + "[";
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			ret += this.coordinatedEngines[i].simulatorAsString();
			if (i < this.coordinatedEngines.length - 1) {
				ret += ", ";
			}
		}
		return ret + "]";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		System.out.println(indent + "---------------------------------");
		System.out.println(indent + name + " "
										+ this.getSimulatedModel().getURI());
		System.out.println(indent + "---------------------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].
				showCurrentState(indent + "    ", this.elapsedTimes[i]);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void		showCurrentStateContent(String indent, Duration elapsedTime)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "submodel of next event = " +
											this.submodelOfNextEventURI);
	}
}
// -----------------------------------------------------------------------------
