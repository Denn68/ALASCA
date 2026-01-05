package fr.sorbonne_u.components.cyphy.interfaces;

import fr.sorbonne_u.devs_simulation.models.time.Time;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>ModelStateAccess</code> defines how a component embedding
 * a simulation model can access a value that resides in the simulation model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When performing software-in-the-loop (SIL) simulations, the software must
 * be able to access values that resides in the simulation models. Typically,
 * the simulation will provide values of variables that replace values that
 * would be given by measures in actual executions. In SIL, a fake sensor must
 * access the model value rather than an actual measured value.
 * </p>
 * <p>
 * This interface is meant to be implemented by simulation plug-ins that would
 * be called by services (methods) in BCM components.
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2019-10-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ModelStateAccessI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	public static class	VariableValue<T>
	{
		protected T 	value;
		protected Time	time;
		public VariableValue(T value, Time time) {
			super();
			this.value = value;
			this.time = time;
		}

		public T		getValue()
		{
			return this.value;
		}

		public Time		getTime()
		{
			return this.time;
		}
	}

	// -------------------------------------------------------------------------
	// Signatures
	// -------------------------------------------------------------------------

	/**
	 * get the current value corresponding to <code>name</code> in the
	 * associated simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code name != null && !name.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <T>			type of the value to be retrieved.
	 * @param modelURI		URI of the model that is targeted by the request.
	 * @param name			name of the model state value sought.
	 * @return				the current value corresponding to <code>name</code> in the associated simulation model.
	 * @throws Exception	<i>to do</i>.
	 */
	public <T>	T		getModelStateValue(String modelURI, String name)
	throws Exception;

	/**
	 * get the current variable value corresponding to <code>name</code> in the
	 * associated simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code name != null && !name.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param <T>			type of the value of the variable to be retrieved.
	 * @param modelURI		URI of the model that is targeted by the request.
	 * @param name			name of the model state value sought.
	 * @return				the current value and time of computation corresponding to <code>name</code> in the associated simulation model.
	 * @throws Exception	<i>to do</i>.
	 */
	public <T>	VariableValue<T>	getModelVariableValue(
		String modelURI,
		String name
		) throws Exception;
}
// -----------------------------------------------------------------------------
