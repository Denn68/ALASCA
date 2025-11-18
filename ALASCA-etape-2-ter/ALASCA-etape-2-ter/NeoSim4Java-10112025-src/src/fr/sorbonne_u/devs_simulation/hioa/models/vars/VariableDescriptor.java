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

import java.lang.reflect.Field;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>VariableDescriptor</code> extends the class
 * <code>StaticVariableDescriptor</code> with dynamic information about
 * variables: the owner model and the Java field that defines the variable
 * in the owner model.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant    {@code owner != null}
 * invariant    {@code f != null}
 * invariant    {@code name.equals(f.getName())}
 * invariant	{@code Value.class.isAssignableFrom(f.getType())}
 * invariant	{@code f.getDeclaringClass().isAssignableFrom(owner.getClass())}
 * invariant	{@code ((Predicate<String>) gtn -> ((BiPredicate<Integer,Integer>)(b, e) -> b >= 0 && b < gtn.length() && e >= b && e < gtn.length() && gtn.substring(b, e).equals(type.getCanonicalName())).test(gtn.indexOf(\"<\") + 1, gtn.indexOf(\">\"))).test(f.getGenericType().getTypeName().replace("$", "."))}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			VariableDescriptor
extends		StaticVariableDescriptor
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	/** the model owning the variable.								*/
	protected final AtomicHIOA		owner;
	/** the Java field which represents the variable in the model. 	*/
	protected final Field			f;

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
	protected static boolean	implementationInvariants(
		VariableDescriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.owner != null,
					VariableDescriptor.class,
					instance,
					"owner != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.f != null,
					VariableDescriptor.class,
					instance,
					"f != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.name.equals(instance.f.getName()),
					VariableDescriptor.class,
					instance,
					"name.equals(f.getName())");
		ret &= AssertionChecking.checkImplementationInvariant(
					Value.class.isAssignableFrom(instance.f.getType()),
					VariableDescriptor.class,
					instance,
					"Value.class.isAssignableFrom(f.getType())");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.f.getDeclaringClass().isAssignableFrom(
												instance.owner.getClass()),
					VariableDescriptor.class,
					instance,
					"f.getDeclaringClass().isAssignableFrom(owner.getClass())");
		ret &= AssertionChecking.checkImplementationInvariant(
					((Predicate<String>) gtn ->
						((BiPredicate<Integer,Integer>)(b, e) ->
							b >= 0 && b < gtn.length() && e >= b &&
							e < gtn.length() &&
							gtn.substring(b, e).equals(
											instance.type.getCanonicalName())).
					    test(gtn.indexOf("<") + 1, gtn.indexOf(">"))).
					  test(instance.f.getGenericType().getTypeName().
							  							replace("$", ".")),
					VariableDescriptor.class,
					instance,
					"((Predicate<String>) gtn -> ((BiPredicate<Integer,Integer>) "
					+ "(b, e) -> b >= 0 && b < gtn.length() && e >= b && "
					+ "e < gtn.length() && gtn.substring(b, e).equals("
					+ "type.getCanonicalName())).test(gtn.indexOf(\"<\") + 1, "
					+ "gtn.indexOf(\">\"))).test(f.getGenericType()."
					+ "getTypeName().replace(\"$\", \".\"))");
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
	protected static boolean	invariants(VariableDescriptor instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * create a complete variable descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code AtomicHIOA.class.isAssignableFrom(owner.getClass())}
	 * pre	{@code f != null}
	 * pre	{@code f.getDeclaringClass().isAssignableFrom(owner.getClass())}
	 * pre	{@code AtomicHIOA.class.isAssignableFrom(f.getDeclaringClass())}
	 * pre	{@code Value.class.isAssignableFrom(f.getType())}
	 * pre	{@code ((Predicate<String>) gtn -> ((BiPredicate<Integer,Integer>)(b, e) -> b >= 0 && b < gtn.length() && e >= b && e < gtn.length() && gtn.substring(b, e).equals(type.getCanonicalName())).test(gtn.indexOf("<") + 1, gtn.indexOf(">"))).test(f.getGenericType().getTypeName().replace("$", "."))}
	 * post	{@code owner.equals(getOwner())}
	 * post	{@code f.equals(getField()}
	 * </pre>
	 *
	 * @param owner			model that owns this variable.
	 * @param f				field declared to represent the variable.
	 * @param type			actual type of the model variable.
	 * @param visibility	visibility of the variable (imported, exported, internal).
	 */
	public				VariableDescriptor(
		AtomicHIOA owner,
		Field f,
		Class<?> type,
		VariableVisibility visibility
		)
	{
		super(f.getName(), type, visibility);

		assert	owner != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "owner != null");
		assert	AtomicHIOA.class.isAssignableFrom(owner.getClass()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "AtomicHIOA.class.isAssignableFrom(owner.getClass())");
		assert	f != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "f != null");
		assert	f.getDeclaringClass().isAssignableFrom(owner.getClass()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "f.getDeclaringClass().isAssignableFrom("
						+ "owner.getClass())");
		assert	Value.class.isAssignableFrom(f.getType()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Value.class.isAssignableFrom(f.getType())");
		assert	((Predicate<String>) gtn ->
					((BiPredicate<Integer,Integer>)(b, e) ->
						b >= 0 && b < gtn.length() && e >= b &&
						e < gtn.length() &&
						gtn.substring(b, e).equals(type.getCanonicalName())).
							test(gtn.indexOf("<") + 1, gtn.indexOf(">"))).
					test(f.getGenericType().getTypeName().replace("$", ".")) :
				new NeoSim4JavaException("Precondition violation:"
						+ "((Predicate<String>) gtn ->"
						+ "((BiPredicate<Integer,Integer>)(b, e) -> "
						+ "b >= 0 && b < gtn.length() && e >= b && "
						+ "e < gtn.length() && gtn.substring(b, e)."
						+ "equals(type.getCanonicalName())).test("
						+ "gtn.indexOf(\"<\") + 1, gtn.indexOf(\">\")))."
						+ "test(f.getGenericType().getTypeName()."
						+ "replace(\"$\", \".\"))");

		
		this.owner = owner;
		this.f = f;

		assert	this.getOwner().equals(owner) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getOwner().equals(owner)");
		assert	this.getField().equals(f) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getField().equals(f)");

		// Invariants checking
		assert	VariableDescriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"VariableDescriptor.implementationInvariants(this)");
		assert	VariableDescriptor.invariants(this) :
				new NeoSim4JavaException("VariableDescriptor.invariants(this)");
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * return the model owner of the variable.
	 * 
	 * @return	the model owner of the variable.
	 */
	public AtomicHIOA	getOwner()
	{
		return owner;
	}

	/**
	 * return the Java field defining the variable.
	 * 
	 * @return	the Java field defining the variable.
	 */
	public Field		getField()
	{
		return f;
	}
}
// -----------------------------------------------------------------------------
