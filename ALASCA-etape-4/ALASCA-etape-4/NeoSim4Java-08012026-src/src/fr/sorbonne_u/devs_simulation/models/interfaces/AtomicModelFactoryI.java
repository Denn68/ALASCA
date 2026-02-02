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

import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AtomicModelFactoryI</code> defines the shared
 * behaviours among atomic model factories.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An atomic model factory provides two principal methods, one that creates
 * atomic models represented as instances of a given model representation
 * class and another that creates a simulation engine instances of a given
 * class that has been designed to run the atomic models created by this
 * factory.
 * </p>
 * <p>
 * When a programmer defines a type of atomic model, if he/she wants
 * to use a creation of simulation architectures through their description
 * as instances of <code>ModelArchitecture</code>, he/she will have the
 * possibility to provide a factory implementing this interface.
 * </p>
 * <p>
 * The protocol to use an atomic model factory first creates an instance
 * of factory, then sets the creation parameters for the model, and then
 * the method <code>createAtomicModel</code> can be called to create a
 * model instance.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		AtomicModelFactoryI
extends		Serializable
{
	/**
	 * return the Java class from which the atomic model is created.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the Java class from which the atomic model is created.
	 */
	public Class<? extends AtomicModelI>	getModelClass();

	/**
	 * set the creation parameters for an atomic model; implement a cascade
	 * pattern by returning {@code this}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !modelParametersSet()}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * post	{@code modelParametersSet()}
	 * post	{@code return == this}
	 * </pre>
	 *
	 * @param modelURI			URI of the model to be created.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @return					the same atomic model factory, implementing the cascading message pattern.
	 */
	public AtomicModelFactoryI	setAtomicModelCreationParameters(
		String modelURI,
		TimeUnit simulatedTimeUnit
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
	 * create an atomic model from the already set creation parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelParametersSet()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	an atomic model associated with the given simulation engine.
	 */
	public AtomicModelI	createAtomicModel();

	/**
	 * create an atomic simulation engine appropriate to run simulations
	 * on the atomic models created using this factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	an atomic simulation engine appropriate to run simulations on the atomic models created using this factory.
	 */
	public AtomicSimulatorI	createAtomicEngine();

}
// -----------------------------------------------------------------------------
