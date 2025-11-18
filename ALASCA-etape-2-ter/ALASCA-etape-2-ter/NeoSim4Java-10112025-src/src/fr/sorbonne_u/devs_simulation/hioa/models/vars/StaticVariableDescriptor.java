package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>StaticVariableDescriptor</code> contains all of the static
 * information related to a HIOA model variable.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The information is gathered mainly from the annotations
 * <code>@ImportedVariable</code>, <code>@ExportedVariable</code> and
 * <code>@InternalVariable</code> put by the user on the variables
 * declared by the Java class representing the model (subclass of
 * <code>AtomicHIOA</code>).
 * </p>
 * <p>
 * An internal variable <code>x</code> of type double is declared as in
 * the following:
 * </p>
 * <pre>
 *     &#64;ExportedVariable(type = Double.class)
 *     protected final Value&#60;Double&#62; x = new Value&#60;Double&#62;(this, 10.0);
 * </pre>
 * <p>
 * Notice that exported variables are always declared as final, hence
 * the placeholder for its values, instance of {@code Value<Type>},
 * will remain the same throughout the simulation and will be shared
 * with the other atomic HIOA models that import the variable.
 * </p>
 * <p>
 * An internal variable <code>y</code> of type double is declared as in
 * the following:
 * </p>
 * <pre>
 *     &#64;InternalVariable(type = Double.class)
 *     protected final Value&#60;Double&#62; y = new Value&#60;Double&#62;(this, 10.0);
 * </pre>
 * <p>
 * For internal variable, the placeholder is used for the sake of homogeneity
 * in the treatment of model variables.
 * </p>
 * <p>
 * An imported variable <code>z</code> of type double is declared as in
 * the following:
 * </p>
 * <pre>
 *     &#64;ImportedVariable(type = Double.class)
 *     protected Value&#60;Double&#62; z ;
 * </pre>
 * <p>
 * An imported variable is not final nor initialised because they will be
 * linked to the exported one during the HIOA model composition process.
 * It would be interesting to force them to be assigned only once but
 * not at initialisation time, something that is rather difficult to impose
 * with Java mechanisms.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant    {@code name != null}
 * invariant    {@code type != null}
 * invariant    {@code visibility != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StaticVariableDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** the name of the variable.										*/
	protected final String				name;
	/** the type of the variable.										*/
	protected final Class<?>			type;
	/** the visibility of the model variable: internal, exported or
	 *  imported.														*/
	protected final VariableVisibility	visibility;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	// The invariants are trivially  observed by the fact that all variables
	// are final and the invariant expressions also appears as preconditions
	// on the corresponding parameters in the constructor.

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a variable descriptor from the given name, type and visibility.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code name != null}
	 * pre	{@code type != null}
	 * pre	{@code visibility != null}
	 * post {@code getName().equals(name)}
	 * post	{@code getType().equals(type)}
	 * post	{@code getVisibility().equals(visibility)}
	 * </pre>
	 *
	 * @param name			name of the variable..
	 * @param type			type of the model variable.
	 * @param visibility	visibility of the variable (imported, exported, internal).
	 */
	public				StaticVariableDescriptor(
		String name,
		Class<?> type,
		VariableVisibility visibility
		)
	{
		super();

		assert	name != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "name != null");
		assert	type != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "type != null");
		assert	visibility != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "visibility != null");

		this.name = name;
		this.type = type;
		this.visibility = visibility;

		assert	getName().equals(name) :
				new NeoSim4JavaException("Postcondition violation: "
										+ "getName().equals(name)");
		assert	getType().equals(type) :
				new NeoSim4JavaException("Postcondition violation: "
										+ "getType().equals(type)");
		assert	getVisibility().equals(visibility) :
				new NeoSim4JavaException("Postcondition violation: "
										+ "getVisibility().equals(visibility)");
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * return the name of the variable.
	 * 
	 * @return	the name of the variable.
	 */
	public String		getName()
	{
		return name;
	}

	/**
	 * return the type of the variable.
	 * 
	 * @return	the type of the variable.
	 */
	public Class<?>		getType()
	{
		return type;
	}

	/**
	 * return the visibility of the variable.
	 * 
	 * @return	the visibility of the variable.
	 */
	public VariableVisibility	getVisibility()
	{
		return visibility;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean		equals(Object obj)
	{
		return 	(obj instanceof StaticVariableDescriptor ?
					this.name.equals(((StaticVariableDescriptor)obj).name)
				:	false
				);
	}

	/**
	 * return true if this variable can be assigned a value from the
	 * variable <code>vd</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vd != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param vd	variable descriptor to which this one is compared.
	 * @return		true if this variable can be assigned a value from the variable <code>vd</code>.
	 */
	public boolean 		isAssignableFrom(StaticVariableDescriptor vd)
	{
		assert	vd != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "vd != null");

		return this.type.isAssignableFrom(vd.type);
	}
}
// -----------------------------------------------------------------------------
