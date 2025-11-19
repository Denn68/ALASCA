package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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

import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>VariableSink</code> describes how imported variables are
 * used in a model i.e., either a variable directly imported by an atomic
 * model or a variable imported by a coupled model and then reimported by
 * a submodel; note that in a coupled model, a single imported variable
 * can be reimported by many submodels possibly using different names and
 * types.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class is used to described how imported variables of a coupled
 * model are connected to its submodels.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code importedVariableName != null && !importedVariableName.isEmpty()}
 * invariant	{@code importedVariableType != null}
 * invariant	{@code sinkVariableName != null && !sinkVariableName.isEmpty()}
 * invariant	{@code sinkVariableType != null}
 * invariant	{@code sinkVariableType.isAssignableFrom(importedVariableType)}
 * invariant	{@code sinkModelURI != null && sinkModelURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			VariableSink
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** name of the variable imported by the coupled model.					*/
	public final String		importedVariableName;
	/** type of the variable imported by the coupled model.					*/
	public final Class<?>	importedVariableType;
	/** name of the variable imported by the submodel.						*/
	public final String		sinkVariableName;
	/** type of the variable imported by the submodel.						*/
	public final Class<?>	sinkVariableType;
	/** URI of the importing submodel.										*/
	public final String		sinkModelURI;

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
	protected static boolean	implementationInvariants(VariableSink instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.importedVariableName != null &&
									!instance.importedVariableName.isEmpty(),
					VariableSink.class,
					instance,
					"importedVariableName != null && "
					+ "!importedVariableName.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.importedVariableType != null,
					VariableSink.class,
					instance,
					"importedVariableType != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.sinkVariableName != null &&
									!instance.sinkVariableName.isEmpty(),
					VariableSink.class,
					instance,
					"sinkVariableName != null && !sinkVariableName.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.sinkVariableType != null,
					VariableSink.class,
					instance,
					"sinkVariableType != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.sinkVariableType.isAssignableFrom(
												instance.importedVariableType),
					VariableSink.class,
					instance,
					"sinkVariableType.isAssignableFrom(importedVariableType)");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.sinkModelURI != null &&
									!instance.sinkModelURI.isEmpty(),
					VariableSink.class,
					instance,
					"sinkModelURI != null && sinkModelURI.isEmpty()");
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
	protected static boolean	invariants(VariableSink instance)
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
	 * create a variable sink for an atomic model or a coupled model where
	 * the imported variable has the same name and the same type as the one
	 * of the sink model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code variableName != null}
	 * pre	{@code variableType != null}
	 * pre	{@code sinkModelURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param variableName	name of the variable imported by the coupled and the submodel.
	 * @param variableType	type of the variable imported by the coupled and the submodel.
	 * @param modelURI		URI of the sink model.
	 */
	public				VariableSink(
		String variableName,
		Class<?> variableType,
		String modelURI
		)
	{
		this(variableName, variableType, variableName, variableType,
			 modelURI);
	}

	/**
	 * create a variable sink for a coupled model where the imported
	 * variable has a different name or a different type from the one
	 * of the sink model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importedVariableName != null}
	 * pre	{@code importedVariableType != null}
	 * pre	{@code sinkVariableName != null}
	 * pre	{@code sinkVariableType != null}
	 * pre	{@code sinkVariableType.isAssignableFrom(importedVariableType)}
	 * pre	{@code sinkModelURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param importedVariableName	name of the variable imported by the coupled model.
	 * @param importedVariableType	type of the variable imported by the coupled model.
	 * @param sinkVariableName		name of the variable imported by the submodel.
	 * @param sinkVariableType		type of the variable imported by the submodel.
	 * @param sinkModelURI			URI of the importing submodel.
	 */
	public				VariableSink(
		String importedVariableName,
		Class<?> importedVariableType,
		String sinkVariableName,
		Class<?> sinkVariableType,
		String sinkModelURI
		)
	{
		super();

		assert	importedVariableName != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "importedVariableName != null");
		assert	importedVariableType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "importedVariableType != null");
		assert	sinkVariableName != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkVariableName != null");
		assert	sinkVariableType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkVariableType != null");
		assert	sinkVariableType.isAssignableFrom(importedVariableType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkVariableType.isAssignableFrom(importedVariableType)");
		assert	sinkModelURI != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkModelURI != null");

		this.importedVariableName = importedVariableName;
		this.importedVariableType = importedVariableType;
		this.sinkVariableName = sinkVariableName;
		this.sinkVariableType = sinkVariableType;
		this.sinkModelURI = sinkModelURI;

		assert	VariableSink.implementationInvariants(this) :
				new NeoSim4JavaException(
						"VariableSink.implementationInvariants(this)");
		assert	VariableSink.invariants(this) :
				new NeoSim4JavaException("VariableSink.invariants(this)");
	}
}
// -----------------------------------------------------------------------------
