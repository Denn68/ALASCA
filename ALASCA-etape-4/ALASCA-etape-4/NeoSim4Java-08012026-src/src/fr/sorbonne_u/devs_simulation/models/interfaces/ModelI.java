package fr.sorbonne_u.devs_simulation.models.interfaces;

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

import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableDefinitionsAndSharingI;
import fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelI</code> is the root of the type hierarchy defining
 * variants of DEVS simulation models for a component plug-in implementing a
 * family of DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * DEVS is a simulation protocol that implements modular discrete event
 * simulation models which can then be simulated in a centralised or
 * distributed way.
 * </p>
 * <p>
 * B.P. Zeigler, H. Praehofer et T.G. Kim, Theory of Modeling and Simulation,
 * Academic Press, 2nd edition, 2000.
 * </p>
 * 
 * 
 * <p><i>Debugging facilities</i></p>
 * 
 * <p>
 * DEVS models all share the fact that they have a unique identifier (URI), a
 * simulation time unit used by their simulation clock and a simulation engine
 * enacting them. Besides these standard services, models can be traced at
 * execution for debugging purposes. The interface defines methods to control
 * this debugging facility.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-03-25</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ModelI
extends		VariableDefinitionsAndSharingI,
			VariableInitialisationI
{
	// -------------------------------------------------------------------------
	// Model manipulation related methods (e.g., definition, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * return the unique identifier of this simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && !return.isEmpty()}
	 * </pre>
	 *
	 * @return	the unique identifier of this simulation model.
	 */
	public String		getURI();

	/**
	 * return true if {@code uri} is the URI of this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	an URI to be tested as equal to the URI of this model.
	 * @return		true if {@code uri} is the URI of this model.
	 */
	public boolean		hasURI(String uri);

	/**
	 * return the time unit of the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time unit of the simulation clock.
	 */
	public TimeUnit		getSimulatedTimeUnit();

	/**
	 * return true if this model is atomic and false if it is a coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this model is atomic and false if it is a coupled model.
	 */
	public boolean		isAtomic();

	/**
	 * return true if the parent reference is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the parent reference is set.
	 */
	public boolean		isParentSet();

	/**
	 * set the parent of this model to <code>p</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code p != null}
	 * post	{@code isParentSet()}
	 * post	{@code getParentURI().equals(p.getURI())}
	 * </pre>
	 *
	 * @param p	the new parent model reference.
	 */
	public void			setParent(CoupledModelI p);

	/**
	 * return the URI of the parent model or null if no one exists.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the parent model or null if no one exists.
	 */
	public String		getParentURI();

	/**
	 * return true if this model is the root of a composition.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this model is the root of a composition.
	 */
	public boolean		isRoot();

	/**
	 * return true if the model is closed.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * A model is closed if it has no imported event. A model can be
	 * considered as closed even if it exports events because they can
	 * be executed with their exported events not consumed by any other
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return		true if the model is closed (no imported event).
	 */
	public boolean		closed();

	/**
	 * return true if <code>ec</code> is an imported event type of
	 * this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ec != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ec	event type to be tested.
	 * @return		true if <code>ec</code> is an imported event type of this model.
	 */
	public boolean		isImportedEventType(Class<? extends EventI> ec);

	/**
	 * return an array of event types imported by the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	an array of event types imported by the model.
	 */
	public Class<? extends EventI>[]	getImportedEventTypes();

	/**
	 * return true if <code>ec</code> is an exported event type of
	 * this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ec != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ec	event type to be tested.
	 * @return		true if <code>ec</code> is an exported event type of this model.
	 */
	public boolean		isExportedEventType(Class<? extends EventI> ec);

	/**
	 * return an array of event types exported by the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	an array of event types exported by the model.
	 */
	public Class<? extends EventI>[]	getExportedEventTypes();

	// -------------------------------------------------------------------------
	// Model composition through events exchanges
	// -------------------------------------------------------------------------

	/**
	 * return the atomic event source (description of an exporting model)
	 * for the given exported event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: what if more than one model is a source for an event?
	 * 
	 * <pre>
	 * pre	{@code ce != null && isExportedEventType(ce)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ce	an event type.
	 * @return		the atomic event source for the given event type.
	 */
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		);

	/**
	 * return the set of atomic event sinks (description of an importing
	 * model) for the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ce != null && isImportedEventType(ce)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param ce	an event type.
	 * @return		the set of atomic event sinks for the given imported event type.
	 */
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		);

	/**
	 * add the given influencees (models that import events exported by this
	 * model) to the ones of the given model during in a composition.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getURI().equals(modelURI) || isDescendent(modelURI)}
	 * pre	{@code ce != null && isExportedEventType(ce)}
	 * pre	{@code influencees != null && influencees.size() > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the atomic model to which the influencees must be added.
	 * @param ce			type of event exported by this model.
	 * @param influencees	atomic models influenced by this model through <code>ce</code>.
	 */
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		);

	/**
	 * return the set of URIs of models that are influenced by this model
	 * through the given type of events.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getURI().equals(modelURI) || isDescendent(modelURI)}
	 * pre	{@code ce != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of the model to be queried.
	 * @param ce		the type of events for which influencees are sought.
	 * @return			the set of event sinks describing the models that are influenced by this model through the given type of events.
	 */
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		);

	/**
	 * return true if all of the models with the given URIs are influenced by
	 * this model through the exported events of the class <code>ce</code> in
	 * the current composition; this method should be called after composing
	 * the model as it tests for models importing events that are exported by
	 * this model modulo a translation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationModelURIs != null && destinationModelURIs.size() > 0}
	 * pre	{@code destinationModelURIs.stream().allMatch(uri -> uri != null && !uri.isEmpty())}
	 * pre	{@code ce != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be queried.
	 * @param destinationModelURIs	set of model URIs to be tested.
	 * @param ce					class of events exported by this model through which the influence is tested.
	 * @return						true if all of the given models are influenced by this model through the events of the class <code>ce</code>.
	 */
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		);

	/**
	 * return true if the given model is influenced by this model through the
	 * events of the class <code>ce</code> in the current composition; this
	 * method should be called after composing the model as it tests for
	 * models importing events that are exported by this model modulo a
	 * translation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationModelURI != null && !destinationModelURI.isEmpty()}
	 * pre	{@code ce != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be queried.
	 * @param destinationModelURI	URI of the model to be tested.
	 * @param ce					class of events exported by this model through which the influence is tested.
	 * @return						true if the given model is influenced by this model through the events of the class <code>ce</code>.
	 */
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		);

	/**
	 * return true if the HIOA model is a TIOA i.e., has no exported or
	 * imported variables (TIOA imports and exports only events).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the HIOA model is a TIOA i.e., has no exported or imported variables.
	 */
	public boolean		isTIOA();

	// -------------------------------------------------------------------------
	// Model-simulator relationships
	// -------------------------------------------------------------------------

	/**
	 * return the simulation engine instance currently running this atomic
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the simulation engine instance currently running this model.
	 */
	public SimulatorI	getSimulationEngine();

	/**
	 * return true if the simulation engine is coordinated by a parent.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * In DEVS, simulation can operate in different mode. The traditional mode
	 * confers to the coupled models and their coordination engine the role to
	 * pilot the advance of their submodels keeping a global synchronisation
	 * between the simulation clocks of their submodels. In real time, atomic
	 * models and their simulation engine operate on their own without being
	 * coordinated by parent coupled models and their coordination engines.
	 * </p>
	 * <p>
	 * This method allows to know when a model is coordinated and when it is
	 * not.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulation engine is coordinated by a parent.
	 */
	default boolean		isCoordinated()
	{
		return true;
	}

	// -------------------------------------------------------------------------
	// Simulation runs related methods
	// -------------------------------------------------------------------------

	/**
	 * create a fully qualified model instance specific run parameter name from
	 * the model URI and a parameter name used by all instances of the same
	 * model class.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code paramName != null && !paramName.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	URI of the model to which a run parameter has to be passed.
	 * @param paramName	name of the run parameter to be used by all instances of the same model class.
	 * @return			a composed parameter name unique to the model and the parameter to be passed.
	 */
	public static String	createRunParameterName(
		String modelURI,
		String paramName
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
			new NeoSim4JavaException("Precondition violation: "
					+ "modelURI != null && !modelURI.isEmpty()");
		assert	paramName != null && !paramName.isEmpty() :
			new NeoSim4JavaException("Precondition violation: "
					+ "paramName != null && !paramName.isEmpty()");

		return modelURI + ":" + paramName;
	}

	/**
	 * set the simulation parameters for a simulation run.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * This method is the same as
	 * fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map).
	 * It could be factored out in a separate interface, but maybe a too
	 * complicated solution for just one method.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && simParams.size() > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param simParams						map from parameters names to their values.
	 * @throws MissingRunParameterException if an expected run parameter does not appear in {@code simParams} (including {@code simParams} being {@code null}).
	 */
	default void		setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		// By default, do nothing
	}

	/**
	 * return true if the model is initialised and ready to start a
	 * simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the model is initialised.
	 */
	public boolean		isStateInitialised();

	/**
	 * initialise the state of the model to make it ready to execute a
	 * simulation run with default initial time set to 0.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * Specific models rarely redefine this method; they should rather
	 * redefine the method <code>initialiseState(Time initialTime)</code>
	 * with the initial time argument.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code isInitialised()}
	 * </pre>
	 *
	 */
	public void			initialiseState();

	/**
	 * initialise the state of the model to make it ready to execute a
	 * simulation run.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The standard predefined state initialisation will set the time to
	 * the next event. Hence, if a specific model has to perform some
	 * initialisations which the first internal transition depends upon,
	 * it must first perform them and then call the inherited method
	 * using <code>super</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code initialTime != null}
	 * pre	{@code initialTime.greaterThanOrEqual(Time.zero(getSimulatedTimeUnit()))}
	 * post	{@code isInitialised()}
	 * </pre>
	 *
	 * @param initialTime	time of the simulation when it will start.
	 */
	public void			initialiseState(Time initialTime);

	/**
	 * return true if all the clocks in all the models are synchronised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if all the clocks in all the models are synchronised.
	 */
	default boolean		clockSynchronised()
	{
		// TODO: not implemented yet...
		return true;
	}

	/**
	 * return the simulation report of the last simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation report of the last simulation run.
	 */
	default SimulationReportI	getFinalReport()
	{
		// by default, return null; otherwise redefine in models.
		return null;
	}

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * return the current simulation time at which the model executed its
	 * last event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the current simulation time at which the model executed its last event.
	 */
	public Time			getCurrentStateTime();

	/**
	 * return the duration until the next internal event as previously
	 * computed by <code>timeAdvance()</code> but in a side effect free manner.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the DEVS protocol, the function <code>timeAdvance</code> return the
	 * duration until the next internal event, but it is assumed to be a
	 * pure function. In an object-oriented implementation, the user (who
	 * implements <code>timeAdvance</code>) may have to implement it with
	 * some side effects on the object representing the model. Hence, it
	 * should be called only once each time an internal event is executed.
	 * If the user need to access this value again, he/seh must be provided
	 * with a side effect free way to do so. This method does exactly that.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the duration until the next internal event as previously computed by <code>timeAdvance()</code>.
	 */
	public Duration		getNextTimeAdvance();

	/**
	 * return the time at which the next internal event will happen in a side
	 * effect free manner.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard purely functional definition of the DEVS protocol, this
	 * method would not be required, as it is the addition of the result of
	 * the function <code>timeAdvance</code> and the current simulated time of
	 * the model. In this object-oriented implementation, the user may
	 * implement <code>timeAdvance</code> with some side effects, hence if
	 * he/she needs to access this information elsewhere, a side effect free
	 * way to do so must be provided. This method does exactly that.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time at which the next internal event will happen.
	 */
	public Time			getTimeOfNextEvent();

	/**
	 * return the time the simulated system remains in the current state
	 * before making a transition to the next sequential state; the method
	 * must be implemented by the user in each atomic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the time the simulated system remains in the current state before making an internal transition to the next state.
	 */
	public Duration		timeAdvance();

	/**
	 * perform the next internal transition, both calling the method
	 * <code>userDefinedInternalTransition</code> and doing all of the
	 * necessary internal catering work.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard DEVS protocol, this method is assumed to be defined by
	 * users in their atomic models. However, having only one method to do so
	 * makes it difficult to include common behaviours for models. Moreover,
	 * DEVS allows to define new types of models where specific common
	 * behaviours must be implemented. With the traditional DEVS vision, this
	 * would end up asking the users to cater for these common behaviours in
	 * each of their implementations the method, an error-prone burden put on
	 * their shoulders. In this library, we prefer to split transitions in two
	 * parts:
	 * </p>
	 * <ul>
	 * <li>a library defined part, here <code>internalTransition</code>, which
	 *   implementations in abstract classes include their common behaviours
	 *   and, for atomic models, calls the user model specific part;</li>
	 * <li>a user model-specific part, here
	 *   <code>userDefinedInternalTransition</code>, which implements the
	 *   execution internal events of the user-defined models
	 *   <i>per se</i>.</li>
	 * </ul> 
	 * <p>
	 * After the execution of the method, the relationships between
	 * <code>timeAdvance</code>, <code>getTimeOfNextEvent</code>,
	 * <code>getNextTimeAdvance</code> and <code>getCurrentStateTime()</code>
	 * is restored to reflect the wait for the next internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getTimeOfNextEvent().lessThan(Time.INFINITY)}
	 * pre	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getCurrentStateTime()))}
	 * post	{@code getTimeOfNextEvent().equals(Time.INFINITY) || getTimeOfNextEvent().subtract(getCurrentStateTime()).equals(getNextTimeAdvance())}
	 * </pre>
	 *
	 */
	public void			internalTransition();

	/**
	 * perform an internal transition; this method is user model-dependent
	 * hence must be implemented by the user for atomic models; the user
	 * implementation should not change any of the implementation related
	 * variables.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The current simulation time when this method is called can be accessed
	 * by calling the method <code>getCurrentStateTime</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code getCurrentStateTime().equals(getTimeOfNextEvent())}
	 * pre	{@code elapsedTime != null}
	 * pre	{@code elapsedTime.lessThan(Duration.INFINITY)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @param elapsedTime	time since the last event.
	 */
	public void			userDefinedInternalTransition(Duration elapsedTime);

	/**
	 * do the next external transition, both calling the method
	 * <code>userDefinedExternalTransition</code> and doing all of the
	 * necessary internal catering work.

	 * <p>Description</p>
	 * 
	 * <p>
	 * In the standard DEVS protocol, this method is assumed to be defined by
	 * users in their atomic models. However, having only one method to do so
	 * makes it difficult to include common behaviours for models. Moreover,
	 * DEVS allows to define new types of models where specific common
	 * behaviours must be implemented. With the traditional DEVS vision, this
	 * would end up asking the users to cater for these common behaviours in
	 * each of their implementations the method, an error-prone burden put on
	 * their shoulders. In this library, we prefer to split transitions in two
	 * parts:
	 * </p>
	 * <ul>
	 * <li>a library defined part, here <code>externalTransition</code>, which
	 *   implementations in abstract classes include their common behaviours
	 *   and, for atomic models, calls the user model specific part;</li>
	 * <li>a user model-specific part, here
	 *   <code>userDefinedExternalTransition</code>, which implements the
	 *   execution internal events of the user-defined models
	 *   <i>per se</i>.</li>
	 * </ul> 
	 * <p>
	 * After the execution of the method, the relationships between
	 * <code>timeAdvance</code>, <code>getTimeOfNextEvent()</code>,
	 * <code>getNextTimeAdvance</code> and <code>getCurrentStateTime()</code>
	 * is restored to reflect the wait for the next internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code elapsedTime != null}
	 * pre	{@code elapsedTime.lessThanOrEqual(getNextTimeAdvance())}
	 * pre	{@code getCurrentStateTime().lessThanOrEqual(getTimeOfNextEvent())}
	 * post	{@code getTimeOfNextEvent().equals(Time.INFINITY) || getTimeOfNextEvent().subtract(getCurrentStateTime()).equals(getNextTimeAdvance())}
	 * </pre>
	 *
	 * @param elapsedTime	elapsed time since the last transition.
	 */
	public void			externalTransition(Duration elapsedTime);

	/**
	 * do an external transition using a previously stored external input
	 * event; this method is user-model-dependent hence must be implemented
	 * by the user for atomic models; the user implementation should not change
	 * any of the implementation related variables.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The current simulation time when this method is called can be accessed
	 * by calling the method <code>getCurrentStateTime</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code elapsedTime != null}
	 * pre	{@code elapsedTime.lessThanOrEqual(getNextTimeAdvance())}
	 * pre	{@code getCurrentStateTime().lessThanOrEqual(getTimeOfNextEvent())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	public void			userDefinedExternalTransition(Duration elapsedTime);

	/**
	 * do a confluent transition i.e., processing internal and external
	 * transitions occurring at the same simulation time using previously
	 * stored external input events.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code elapsedTime != null}
	 * pre	{@code elapsedTime.lessThanOrEqual(timeAdvance())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	default void		confluentTransition(Duration elapsedTime)
	{
		// TODO: not implemented yet...
	}

	/**
	 * do a confluent transition i.e., processing internal and external
	 * transitions occurring at the same simulation time using previously
	 * stored external input events; this method is user-model-dependent
	 * hence must be implemented by the user for atomic models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code elapsedTime != null}
	 * pre	{@code elapsedTime.lessThanOrEqual(timeAdvance())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	simulation time since the last processed event.
	 */
	default void		userDefinedConfluentTransition(Duration elapsedTime)
	{
		// TODO: not implemented yet...
	}

	/**
	 * force the model to forward externals event generated before their
	 * next internal transition step.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * The implementation follows the principle of the "peer message
	 * exchanging implementation" i.e., the simulators exchanges directly
	 * with each others the exported and imported events without passing
	 * by their parent (and ancestors).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time at which external events may be output.
	 */
	public void			produceOutput(Time current);

	/**
	 * terminate the current simulation.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * This method can be user-defined, allowing to perform some catering work
	 * like gathering data or computing statistics that depends upon the end
	 * time of the simulation (and that would likely be included in the
	 * simulation report of the model).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code endTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param endTime	time at which the simulation ends.
	 */
	default void			endSimulation(Time endTime)
	{
		// By default, do nothing
	}

	/**
	 * finalise the execution of the model, to be called after the end of the
	 * current simulation run to prepare for another run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	default void			finalise()
	{
		// By default, do nothing
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * log a message through the logger or do nothing if no logger
	 * is set.
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
	public void			logMessage(String message);

	/**
	 * return the model information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent	indenting string.
	 * @return			the model information as a string.
	 */
	public String		modelAsString(String indent);

	/**
	 * show the current state of the model during the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		spacing added  before all printed outputs.
	 * @param elapsedTime	parents knowledge of the elapsed time since the last transition for this model.
	 */
	public void			showCurrentState(String indent, Duration elapsedTime);

	/**
	 * show the current state of the model during the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		spacing added  before all printed outputs.
	 * @param elapsedTime	parents knowledge of the elapsed time since the last transition for this model.
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		);
}
// -----------------------------------------------------------------------------
