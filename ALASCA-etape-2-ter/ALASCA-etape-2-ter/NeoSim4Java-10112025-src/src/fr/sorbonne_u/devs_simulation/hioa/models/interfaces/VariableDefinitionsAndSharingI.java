package fr.sorbonne_u.devs_simulation.hioa.models.interfaces;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

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

import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;

// -----------------------------------------------------------------------------
/**
 * The interface <code>VariableDefinitionsAndSharingI</code> defines the
 * signatures of methods for processing variables and their sharing among
 * models.
 *  
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-06-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		VariableDefinitionsAndSharingI
{
	// -------------------------------------------------------------------------
	// Variables definition and visibility
	// -------------------------------------------------------------------------

	/**
	 * return true if this model exports the designated variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code name != null && !name.isEmpty()}
	 * pre	{@code type != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param name	name of the variable.
	 * @param type	type of the variable.
	 * @return		true if this model exports the designated variable.
	 */
	default boolean		isExportedVariable(String name, Class<?> type)
	{
		assert	name != null && !name.isEmpty() :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "name != null && !name.isEmpty()");
		assert	type != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "type != null");

		// by default, atomic model do not have variables
		return false;
	}

	/**
	 * return true if this model imports the designated variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code name != null && !name.isEmpty()}
	 * pre	{@code type != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param name	name of the variable.
	 * @param type	type of the variable.
	 * @return		true if this model imports the designated variable.
	 */
	default boolean		isImportedVariable(String name, Class<?> type)
	{
		assert	name != null && !name.isEmpty() :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "name != null && !name.isEmpty()");
		assert	type != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "type != null");

		// by default, atomic model do not have variables
		return false;
	}

	/**
	 * return the variables imported by this HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the variables imported by this HIOA.
	 */
	default StaticVariableDescriptor[]	getImportedVariables()
	{
		throw new RuntimeException("Standard atomic DEVS models do not have "
								   + "model variables.");
	}

	/**
	 * return the variables exported by this HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the variables exported by this HIOA.
	 */
	default StaticVariableDescriptor[]	getExportedVariables()
	{
		throw new RuntimeException("Standard atomic DEVS models do not have "
								   + "model variables.");
	}

	// -------------------------------------------------------------------------
	// Model composition through variables sharing
	// -------------------------------------------------------------------------

	/**
	 * search among descendant HIOA of the model with URI
	 * <code>modelURI</code> the atomic one that defines the designated
	 * variable or its alias and return the reference to the placeholder
	 * for its value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code sourceVariableName != null && !sourceVariableName.isEmpty()}
	 * pre	{@code sourceVariableType != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model that exports the designated variable.
	 * @param sourceVariableName	name of the sought variable.
	 * @param sourceVariableType	type of the sought variable.
	 * @return						the reference to the placeholder for the value of the designated exported variable.
	 */
	default Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		throw new RuntimeException("Standard atomic DEVS models do not have "
								   + "model variables.");
	}


	/**
	 * search among descendant HIOA of the model with URI
	 * <code>modelURI</code> all of the the atomic ones that use the
	 * designated variable or its aliases and set in them the reference
	 * to the placeholder for its value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code sinkVariableName != null && !sinkVariableName.isEmpty()}
	 * pre	{@code sinkVariableType != null}
	 * pre	{@code value != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI			URI of the model that imports the designated variable.
	 * @param sinkVariableName	name of the sought variable.
	 * @param sinkVariableType	type of the sought variable.
	 * @param value				the placeholder for the value of the designated imported variable.
	 */
	default void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		)
	{
		throw new RuntimeException("Standard atomic DEVS models do not have "
								   + "model variables.");
	}
}
// -----------------------------------------------------------------------------
