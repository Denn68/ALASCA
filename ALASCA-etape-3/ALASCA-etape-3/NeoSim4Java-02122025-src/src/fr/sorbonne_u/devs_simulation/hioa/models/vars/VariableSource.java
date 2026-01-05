package fr.sorbonne_u.devs_simulation.hioa.models.vars;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>VariableSource</code> describes exported variables of
 * submodels either to define the connections among submodels when
 * creating a coupled model or to define how a coupled model reexports
 * the exported variables of its submodels.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code name != null  && !name.isEmpty()}
 * invariant	{@code type != null}
 * invariant	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
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
public class			VariableSource
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** name of the exported variable.										*/
	public final String		name;
	/** type of the exported variable.										*/
	public final Class<?>	type;
	/** URI of the model exporting the variable.							*/
	public final String		exportingModelURI;

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
	protected static boolean	implementationInvariants(VariableSource instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.name != null  && !instance.name.isEmpty(),
					VariableSource.class,
					instance,
					"name != null  && !name.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.type != null,
					VariableSource.class,
					instance,
					"type != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.exportingModelURI != null &&
										!instance.exportingModelURI.isEmpty(),
					VariableSource.class,
					instance,
					"exportingModelURI != null && !exportingModelURI.isEmpty()");
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
	protected static boolean	invariants(VariableSource instance)
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
	 * create a variable source description.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code name != null}
	 * pre	{@code type != null}
	 * pre	{@code exportingModelURI != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param name				name of the exported variable.
	 * @param type				type of the exported variable.
	 * @param exportingModelURI	URI of the model exporting the variable.
	 */
	public				VariableSource(
		String name,
		Class<?> type,
		String exportingModelURI
		)
	{
		super();

		assert	name != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "name != null");
		assert	type != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "type != null");
		assert	exportingModelURI != null :
				new NeoSim4JavaException("Precondition violation: "
											+ "exportingModelURI != null");

		this.name = name;
		this.type = type;
		this.exportingModelURI = exportingModelURI;

		assert	VariableSource.implementationInvariants(this) :
				new NeoSim4JavaException(
						"VariableSource.implementationInvariants(this)");
		assert	VariableSource.invariants(this) :
				new NeoSim4JavaException("VariableSource.invariants(this)");
	}
}
// -----------------------------------------------------------------------------
