package fr.sorbonne_u.components.cyphy.interfaces;

import fr.sorbonne_u.components.exceptions.BCMException;

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

import fr.sorbonne_u.devs_simulation.models.time.Time;

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
 * would be given by sensor measures in actual executions. In SIL, a fake sensor
 * must access the model value rather than an actual measured value.
 * </p>
 * <p>
 * This interface is meant to be implemented by simulation plug-ins that would
 * be called by services (methods) in BCM4Java-CyPhy components.
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

	/**
	 * The class <code>VariableValue</code> implements an instantaneous
	 * representation of a variable value for NeoSim4Java HIOA models variables;
	 * used to exchange values with BCM4Javav-CyPhy cyber-physical components
	 * without exposing the actual representation of variable values in
	 * NeoSim4Java.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * A HIOA variable is updated at discrete instants during a simulation.
	 * The variable value is meant to get a value with the time at which the
	 * simulator last updated it.
	 * </p>
	 * <p>
	 * To compare, with an actual sensor, the time would be the one at which the
	 * value has been retrieved from the sensor (physical access or computed),
	 * not the one at which it is read to be used in further computations.
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
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2026-01-02</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	VariableValue<T>
	{
		/** a value of a HIOA model variable.								*/
		protected T 	value;
		/** simulated time at which the value has been computed and stored.	*/
		protected Time	time;

		/**
		 * create a variable value.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code value != null}
		 * pre	{@code time != null}
		 * post	{@code getValue().equals(value)}
		 * post	{@code getTime().equals(time)}
		 * </pre>
		 *
		 * @param value	a value of a HIOA model variable.
		 * @param time	simulated time at which the value has been computed and stored.
		 */
		public VariableValue(T value, Time time) {
			super();
			this.value = value;
			this.time = time;
		}

		/**
		 * return the value of this variable value.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code return != null}
		 * </pre>
		 *
		 * @return	the value of this variable value.
		 */
		public T		getValue()
		{
			return this.value;
		}

		/**
		 * return the simulated time at which the value has been computed and
		 * stored.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @return	the simulated time at which the value has been computed and stored.
		 */
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
	 * @param modelURI		URI of the model that is targeted by the request.
	 * @param name			name of the model state value sought.
	 * @return				the current value corresponding to <code>name</code> in the associated simulation model.
	 * @throws Exception	<i>to do</i>.
	 */
	default Object		getModelStateValue(String modelURI, String name)
	throws Exception
	{
		throw new BCMException(
				"The method getModelStateValue called on a " +
				"simulator plug-in must be defined by the user in a " +
				"subclass to use the approriate way to access the " +
				"values in models.");
	}

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
	 * @param modelURI		URI of the model that is targeted by the request.
	 * @param name			name of the model state value sought.
	 * @return				the current value and time of computation corresponding to <code>name</code> in the associated simulation model.
	 * @throws Exception	<i>to do</i>.
	 */
	default VariableValue<?>	getModelVariableValue(
		String modelURI,
		String name
		) throws Exception
	{
		throw new BCMException(
				"The method getModelVariableValue called on a " +
				"simulator plug-in must be defined by the user in a " +
				"subclass to use the approriate way to access the " +
				"values in models.");
	}
}
// -----------------------------------------------------------------------------
