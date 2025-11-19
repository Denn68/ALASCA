package fr.sorbonne_u.devs_simulation.hioa.models.interfaces;

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

import fr.sorbonne_u.devs_simulation.utils.Pair;

// -----------------------------------------------------------------------------
/**
 * The interface <code>VariableInitialisationI</code> defines methods used to
 * implement the initialisation of variables in HIOA models, including a fix
 * point initialisation protocol to cater for dependencies among distinct models
 * variables.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * TODO
 * </p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no invariant
 * </pre>
 * 
 * <p>Created on : 2022-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		VariableInitialisationI
{
	/**
	 * initialise the internal data structures of the atomic HIOA model
	 * from the static information about the model variables; this method
	 * <b>must</b> be called immediately after creating an atomic HIOA model
	 * and before composing it with other models i.e., is an integral part
	 * of the model creation protocol.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	default void		staticInitialiseVariables()
	{
		// by default, do nothing
	}

	/**
	 * initialise the model variables with a simple protocol when external model
	 * variables are fully independent of each others, hence each model can
	 * initialise them in any order; this method must be called after the
	 * method {@code initialiseState}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !allModelVariablesInitialised()}
	 * pre	{@code !useFixpointInitialiseVariables()}
	 * post	{@code allModelVariablesInitialised()}
	 * </pre>
	 *
	 */
	default void		initialiseVariables()
	{
		// by default, do nothing
	}

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
	 * @return	true if the model uses the fixpoint algorithm to initialise its model variables, false otherwise.
	 */
	default boolean		useFixpointInitialiseVariables()
	{
		// by default, don't use the fixpoint variables initialisation protocol
		return false;
	}

	/**
	 * return true if all model variables have their associated time
	 * initialised  (relevant for HIOA models).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if all model variables have their associated time initialised.
	 */
	default boolean		allModelVariablesTimeInitialised()
	{
		return true;
	}

	/**
	 * return true if all model variables are fully initialised  (relevant for
	 * HIOA models).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if all model variables are fully initialised.
	 */
	default boolean		allModelVariablesInitialised()
	{
		return true;
	}

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
	 */
	default Pair<Integer,Integer>	fixpointInitialiseVariables()
	{
		// by default, return a pair indicating that all model variables are
		// fully initialised
		return new Pair<Integer,Integer>(0, 0);
	}
}
// -----------------------------------------------------------------------------
