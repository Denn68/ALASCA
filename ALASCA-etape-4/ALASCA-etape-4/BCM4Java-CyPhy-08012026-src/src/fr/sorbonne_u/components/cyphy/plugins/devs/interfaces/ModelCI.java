package fr.sorbonne_u.components.cyphy.plugins.devs.interfaces;

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

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.utils.Pair;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>ModelCI</code> allows to interact with simulation models directly
 * or through their simulation engine or any other proxy reference.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This component interface corresponds to the Java interface
 * <code>ModelI</code> in the simulation library.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ModelCI
extends		OfferedCI,
			RequiredCI
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
	 * post	{@code ret != null && !ret.isEmpty()}
	 * </pre>
	 *
	 * @return	the unique identifier of this simulation model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getURI()
	 */
	public String		getURI() throws Exception;

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
	 * @param uri			an URI to be tested as equal to the URI of this model.
	 * @return				true if {@code uri} is the URI of this model.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		hasURI(String uri) throws Exception;

	/**
	 * return the time unit of the simulation clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return				the time unit of the simulation clock.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulatedTimeUnit()
	 */
	public TimeUnit		getSimulatedTimeUnit() throws Exception;

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
	 * @return				true if this model is atomic and false if it is a coupled model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isAtomic()
	 */
	public boolean		isAtomic() throws Exception;

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
	 * @return				true if the parent reference is set.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isParentSet()
	 */
	public boolean		isParentSet() throws Exception;

	/**
	 * set the reference to the parent through a connection between the
	 * component holding this model and the one holding the parent model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * post	{@code isParentSet()}
	 * </pre>
	 *
	 * @param inboundPortURI	URI of the inbound port in the coordinator component.
	 * @throws Exception		<i>to do</i>.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setParent(fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI)
	 */
	public void			setParent(String inboundPortURI) throws Exception;

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
	 * @return				the URI of the parent model or null if no one exists.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getParentURI()
	 */
	public String		getParentURI() throws Exception;

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
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isRoot()
	 */
	public boolean		isRoot() throws Exception;

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
	 * @return				true if the model is closed (no imported event).
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#closed()
	 */
	public boolean		closed() throws Exception;

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
	 * @param ec			event type to be tested.
	 * @return				true if <code>ec</code> is an imported event type of this model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isImportedEventType(java.lang.Class)
	 */
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception;

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
	 * @return				an array of event types imported by the model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getImportedEventTypes()
	 */
	public Class<? extends EventI>[]	getImportedEventTypes()
	throws Exception;

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
	 * @param ec			event type to be tested.
	 * @return				true if <code>ec</code> is an exported event type of this model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isExportedEventType(java.lang.Class)
	 */
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception;

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
	 * @return				an array of event types exported by the model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getExportedEventTypes()
	 */
	public Class<? extends EventI>[]	getExportedEventTypes()
	throws Exception;

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
	 * @param ce			an event type.
	 * @return				the atomic event source for the given event type.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSource(java.lang.Class)
	 */
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception;

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
	 * @param ce			an event type.
	 * @return				the set of atomic event sinks for the given imported event type.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSinks(java.lang.Class)
	 */
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI#isDescendant(java.lang.String)
	 */
	public boolean		isDescendant(String uri) throws Exception;

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
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception;

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
	 * @param modelURI		URI of the model to be queried.
	 * @param ce			the type of events for which influencees are sought.
	 * @return				the set of event sinks describing the models that are influenced by this model through the given type of events.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception;

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
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception;

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
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception;

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
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isTIOA()
	 */
	public boolean		isTIOA() throws Exception;

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

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
	 * @throws Exception	<i>to do</i>.
	 * @throws MissingRunParameterException if an expected run parameter does not appear in {@code simParams} (including {@code simParams} being {@code null}).
	 */
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception;

	/**
	 * return true if the model uses the fixpoint algorithm to initialise its
	 * model variables, false otherwise; when true, the method
	 * {@code fixpointInitialiseVariables} must be implemented by the model, and
	 * when false, it is the method {@code initialiseVariables} that must be.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the model uses the fixpoint algorithm to initialise its model variables, false otherwise.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		useFixpointInitialiseVariables() throws Exception;

	/**
	 * initialise the model variables with a more elaborated protocol when
	 * external model variables depend of each others, hence models importing
	 * variables have to wait until these variables are themselves initialised
	 * so this protocol will allow to call repetitively the method until all
	 * variables are initialised without having to respect any order among
	 * models (but models cannot have circular dependencies); this method must
	 * be called after the method {@code initialiseState}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code useFixpointInitialiseVariables()}
	 * pre	{@code allModelVariablesTimeInitialised()}
	 * post	{@code ret.getFirst() >= 0 && ret.getSecond() >= 0}
	 * post	{@code !(ret.getFirst() >= 0 || ret.getSecond() == 0) || allModelVariablesInitialised()}
	 * </pre>
	 *
	 * @return	a pair which first element is the number of newly initialised variables (<i>i.e.</i>) and the second the number of non initialised yet variables.
	 * @throws Exception	<i>to do</i>.
	 */
	public Pair<Integer,Integer>	fixpointInitialiseVariables() throws Exception;

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

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
	 * @param indent		indenting string.
	 * @return				the model information as a string.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#modelAsString(java.lang.String)
	 */
	public String		modelAsString(String indent) throws Exception;
}
// -----------------------------------------------------------------------------
