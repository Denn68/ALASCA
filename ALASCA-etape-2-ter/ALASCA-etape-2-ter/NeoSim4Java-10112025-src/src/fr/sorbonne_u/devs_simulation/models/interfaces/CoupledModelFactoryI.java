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

import java.io.Serializable;
import java.util.Map;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>CoupledModelFactoryI</code> defines the shared
 * behaviours among coupled model factories.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An coupled model factory provides three principal methods, one that
 * creates coupled models represented as instances of a given model
 * representation class, one that creates atomic simulation engine and
 * another that creates a coordination engine, the last both as instances
 * of given classes that have been designed to run the coupled models
 * created by this factory.
 * </p>
 * <p>
 * When a programmer defines a type of coupled model, if he/she wants
 * to use a creation of simulation architectures through their description
 * as instances of <code>ModelArchitecture</code>, he/she will have the
 * possibility to provide a factory implementing this interface.
 * </p>
 * <p>
 * The protocol to use a coupled model factory first creates an instance
 * of factory, then sets the creation parameters for the coupled model,
 * and then the method <code>createCoupledModel</code> can be called to create a
 * model instance.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-05-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		CoupledModelFactoryI
extends		Serializable
{
	/**
	 * set the parameters needed to create a coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null and models.length > 0}
	 * pre	{@code Stream.of(models).allMatch(m -> m != null)}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code { tu = models[0].getSimulatedTimeUnit(); return Stream.of(models).allMatch(m -> m.getSimulatedTimeUnit().equals(tu));}}
	 * post	{@code modelParametersSet()}
	 * </pre>
	 *
	 * @param models			models to be composed.
	 * @param modelURI			URI of the resulting coupled model.
	 * @param imported			imported events and their conversion to submodels imported ones.
	 * @param reexported		exported events from submodels and their conversion to exported ones.
	 * @param connections		connections between exported and imported events among submodels.
	 * @param importedVars		variables imported by the coupled model.
	 * @param reexportedVars	variables exported by the coupled model.
	 * @param bindings			bindings of variables exported and imported by submodels.
	 */
	public void			setCoupledModelCreationParameters(
		ModelI[] models,
		String modelURI,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		);

	/**
	 * return true if the creation parameters have been set on this factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the creation parameters have been set on this factory.
	 */
	public boolean		modelParametersSet();

	/**
	 * create a coupled model from the already set creation parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelParametersSet()}
	 * post	{@code true}	// TODO
	 * </pre>
	 *
	 * @return				coupled model resulting from the composition.
	 */
	public CoupledModelI	createCoupledModel();

	/**
	 * create a coordination engine appropriate to run simulations
	 * on the coupled models created using this factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}		// TODO
	 * </pre>
	 *
	 * @return	a coordination engine appropriate to run simulations on the coupled models created using this factory.
	 */
	public CoordinatorI	createCoordinationEngine();
}
// -----------------------------------------------------------------------------
