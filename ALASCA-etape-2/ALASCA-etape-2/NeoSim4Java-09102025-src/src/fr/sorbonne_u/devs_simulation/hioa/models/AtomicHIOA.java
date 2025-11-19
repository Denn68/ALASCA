package fr.sorbonne_u.devs_simulation.hioa.models;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AtomicHIOA</code> implements the base level
 * hybrid input/output automata, having imported, internal and exported
 * variables as well as events which implementation is based on and
 * inherited from DEVS atomic models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * See the package <code>fr.sorbonne_u.devs_simulation.hioa</code>
 * for more information about Hybrid Input/Output Automata and their
 * overall implementation as simulation models.
 * </p>
 * <p>
 * Concrete atomic HIOA are defined as by the Java class (subclasses of
 * <code>AtomicHIOA</code>) by their imported, internal and exported
 * variables as well as their trajectories which are expressed by
 * equations within the simulation protocol methods like
 * <code>userDefinedInternalTransition</code>. Variables are declared
 * as fields in user-defined models and indicated by annotations put on
 * these fields (@see fr.sorbonne_u.devs_simulation.hioa.annotations).
 * Thus, the required information to define the variables of the HIOA
 * is gathered at run time from these annotations:
 * <code>@ImportedVariable</code>, <code>@ExportedVariable</code> and
 * <code>@InternalVariable</code> put by the user on the declared
 * fields.
 * </p>
 * <p>
 * For example, an exported variable <code>x</code> of type double is
 * declared as follows:
 * </p>
 * <pre>
 *     {@code @ExportedVariable(type = Double.class)}
 *     {@code protected final Value<Double> x = new Value<Double>(this);}
 * </pre>
 * <p>
 * The type defined in the annotation is the one used as the generic
 * parameter of the class {@code Value<T>}. Notice that
 * exported variables are declared as <code>final</code>, hence the
 * placeholder for its values, instance of {@code Value<Double>},
 * will remain the same throughout the simulation and will be shared
 * with the other atomic HIOA models that import the variable.
 * </p>
 * <p>
 * An internal variable <code>y</code> of type double is declared as
 * follows:
 * </p>
 * <pre>
 *     {@code @InternalVariable(type = Double.class)}
 *     {@code protected final Value<Double> y = new Value<Double>(this);}
 * </pre>
 * <p>
 * For internal variables, the placeholder is used for the sake of
 * homogeneity in the treatment of model variables, as they can't be
 * shared with other models but rather hidden in the declaring one.
 * </p>
 * <p>
 * An imported variable <code>z</code> of type double is declared as in
 * the following:
 * </p>
 * <pre>
 *     {@code @ImportedVariable(type = Double.class)}
 *     {@code protected Value<Double> z;}
 * </pre>
 * <p>
 * An imported variable is not <code>final</code> nor initialised because
 * they will be linked to the exported one during the HIOA simulation
 * models composition process.
 * </p>
 * <p>
 * Exported and internal variables can also be initialised in the model class
 * constructor but no later for the composition of models to work properly.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * TODO: check declared variables with annotations
 * 
 * <pre>
 * invariant	{@code importedVariables != null}
 * invariant	{@code exportedVariables != null}
 * invariant	{@code internalVariables != null}
 * invariant	{@code variables2values != null}
 * invariant	{@code variables2values.isEmpty() || Stream.of(exportedVariables).allMatch(v -> variables2values.containsKey(v) && variables2values.get(v) != null)}
 * invariant	{@code variables2values.isEmpty() || Stream.of(internalVariables).allMatch(v -> variables2values.containsKey(v) && variables2values.get(v) != null)}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * TODO: check imported/exported variables with annotations
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AtomicHIOA
extends		AtomicModel
{
	//--------------------------------------------------------------------------
	// Constants and instance variables
	//--------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L;
	/** variable descriptors for this model imported variables.				*/
	protected final VariableDescriptor[]				importedVariables;
	/** variable descriptors for this model exported variables.				*/
	protected final VariableDescriptor[]				exportedVariables;
	/** variable descriptors for this model internal variables.				*/
	protected final VariableDescriptor[]				internalVariables;
	/** map from variable descriptors to their assigned value
	 *  placeholder {@code Value<?>} object.								*/
	protected final Map<VariableDescriptor,Value<?>>	variables2values;

	/** true when all variables have their associated time initialised.		*/
	protected boolean							allVariablesTimeInitialised;

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
	protected static boolean	implementationInvariants(AtomicHIOA instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.importedVariables != null,
					AtomicHIOA.class,
					instance,
					"importedVariables != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.exportedVariables != null,
					AtomicHIOA.class,
					instance,
					"exportedVariables != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.internalVariables != null,
					AtomicHIOA.class,
					instance,
					"internalVariables != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.variables2values != null,
					AtomicHIOA.class,
					instance,
					"variables2values != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.variables2values.isEmpty() ||
						Stream.of(instance.exportedVariables).allMatch(
								v -> instance.variables2values.containsKey(v) &&
									 instance.variables2values.get(v) != null),
					AtomicHIOA.class,
					instance,
					"variables2values.isEmpty() || Stream.of(exportedVariables)."
					+ "allMatch(v -> variables2values.containsKey(v) && "
					+ "variables2values.get(v) != null)");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.variables2values.isEmpty() ||
						Stream.of(instance.internalVariables).allMatch(
								v -> instance.variables2values.containsKey(v) &&
									 instance.variables2values.get(v) != null),
					AtomicHIOA.class,
					instance,
					"variables2values.isEmpty() || Stream.of(internalVariables)."
					+ "allMatch(v -> variables2values.containsKey(v) && "
					+ "variables2values.get(v) != null)");
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
	protected static boolean	invariants(AtomicHIOA instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: instance != null");

		boolean ret = true;
		return ret;
	}

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------

	/**
	 * create an atomic hybrid input/output model with the given URI (if null,
	 * one will be generated) and to be run by the given simulator (or by the
	 * one of an ancestor coupled model if null) using the given time unit for
	 * its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				unique identifier of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param simulationEngine	simulation engine enacting the model.
	 */
	public				AtomicHIOA(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.importedVariables = this.getImportedVars(this.getClass());
		this.exportedVariables = this.getExportedVars(this.getClass());
		this.internalVariables = this.getInternalVars(this.getClass());
		this.variables2values = new HashMap<VariableDescriptor,Value<?>>();
		this.allVariablesTimeInitialised = false;

		// Invariants checking
		assert	AtomicHIOA.implementationInvariants(this) :
				new NeoSim4JavaException(
						"AtomicHIOA.implementationInvariants(this)");
		assert	AtomicHIOA.invariants(this) :
				new NeoSim4JavaException("AtomicHIOA.invariants(this)");
	}

	//--------------------------------------------------------------------------
	// Internal methods
	//--------------------------------------------------------------------------

	/**
	 * get the imported variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the array of imported variables descriptors.
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getImportedVars(
		Class<? extends AtomicHIOA> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Class<? extends AtomicHIOA> current = c;
		VariableDescriptor vd = null;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>();
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields();
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true);
				if (AtomicHIOA.isImportedField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.IMPORTED);
					ret.add(vd);
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass();
		}
		return ret.toArray(new VariableDescriptor[0]);
	}

	/**
	 * get the imported variable static descriptors from the class definition
	 * of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return 				the imported variable static descriptors.
	 */
	@SuppressWarnings("unchecked")
	public static StaticVariableDescriptor[]	staticGetImportedVars(
		Class<? extends AtomicHIOA> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Class<? extends AtomicHIOA> current = c;
		StaticVariableDescriptor vd = null;
		Vector<StaticVariableDescriptor> ret =
								new Vector<StaticVariableDescriptor>();
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields();
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true);
				if (AtomicHIOA.isImportedField(allFields[i])) {
					vd = new StaticVariableDescriptor(
								allFields[i].getName(),
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.IMPORTED);
					ret.add(vd);
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass();
		}
		return ret.toArray(new StaticVariableDescriptor[0]);
	}

	/**
	 * get the internal variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the variable descriptors.
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getInternalVars(
		Class<? extends AtomicHIOA> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Class<? extends AtomicHIOA> current = c;
		VariableDescriptor vd = null;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>();
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields();
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true);
				if (AtomicHIOA.isInternalField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.INTERNAL);
					if (this.simulationEngine.hasDebugLevel(2)) {
						this.logMessage("AtomicHIOA#getInternalVars " +
							"name = " + vd.getName() + ", " +
							"type = " + vd.getType().getName() + ", " +
							"visibility = " + vd.getVisibility() + ", " +
							"owner URI = " + vd.getOwner().getURI() + ", " +
							"field = " + vd.getField());
					}
					ret.add(vd);
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass();
		}
		return ret.toArray(new VariableDescriptor[0]);
	}

	/**
	 * get the exported variable descriptors from the class definition and a
	 * given instance of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the exported variable static descriptors
	 */
	@SuppressWarnings("unchecked")
	protected VariableDescriptor[]	getExportedVars(
		Class<? extends AtomicHIOA> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Class<? extends AtomicHIOA> current = c;
		VariableDescriptor vd = null;
		Vector<VariableDescriptor> ret = new Vector<VariableDescriptor>();
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields();
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true);
				if (AtomicHIOA.isExportedField(allFields[i])) {
					vd = new VariableDescriptor(
								this,
								allFields[i],
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.EXPORTED);
					ret.add(vd);
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass();
		}
		return ret.toArray(new VariableDescriptor[0]);
	}

	/**
	 * get the exported variable static descriptors from the class definition
	 * of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c				Java class defining the model.
	 * @return				the array of exported variable descriptors.
	 */
	@SuppressWarnings("unchecked")
	public static StaticVariableDescriptor[]	staticGetExportedVars(
		Class<? extends AtomicHIOA> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Class<? extends AtomicHIOA> current = c;
		StaticVariableDescriptor vd = null;
		Vector<StaticVariableDescriptor> ret =
								new Vector<StaticVariableDescriptor>();
		while (!current.equals(AtomicHIOA.class)) {
			Field[] allFields = current.getDeclaredFields();
			for (int i = 0 ; i < allFields.length ; i++) {
				allFields[i].setAccessible(true);
				if (AtomicHIOA.isExportedField(allFields[i])) {
					vd = new StaticVariableDescriptor(
								allFields[i].getName(),
								AtomicHIOA.getDeclaredType(allFields[i]),
								VariableVisibility.EXPORTED);
					ret.add(vd);
				}
			}
			current = (Class<? extends AtomicHIOA>) current.getSuperclass();
		}
		return ret.toArray(new StaticVariableDescriptor[0]);
	}

	/**
	 * return true if <code>f</code> represents an imported variable in
	 * the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an imported variable in the model.
	 */
	protected static boolean	isImportedField(Field f)
	{
		assert	f != null :
				new NeoSim4JavaException("Precondition violation: f != null");

		Annotation a = f.getAnnotation(ImportedVariable.class);
		return a != null;
	}

	/**
	 * return true if <code>f</code> represents an exported variable in the
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an exported variable in the model.
	 */
	protected static boolean	isExportedField(Field f)
	{
		assert	f != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "f != null");

		Annotation a = f.getAnnotation(ExportedVariable.class);
		return a != null;
	}

	/**
	 * return true if <code>f</code> represents an internal variable in the
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	the field representing a model variable.
	 * @return	true if <code>f</code> represents an internal variable in the model.
	 */
	protected static boolean	isInternalField(Field f)
	{
		assert	f != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "f != null");

		Annotation a = f.getAnnotation(InternalVariable.class);
		return a != null;
	}

	/**
	 * return the type declared by the variable annotation of <code>f</code>
	 * or null of none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param f	a Java field.
	 * @return	the type declared by the variable annotation of <code>f</code> or null of none.
	 */
	protected static Class<?>	getDeclaredType(Field f)
	{
		assert	f != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "f != null");

		if (AtomicHIOA.isImportedField(f)) {
			return ((ImportedVariable)f.getAnnotation(
											ImportedVariable.class)).type();
		} else if (AtomicHIOA.isExportedField(f)) {
			return ((ExportedVariable)f.getAnnotation(
											ExportedVariable.class)).type();
		} else if (AtomicHIOA.isInternalField(f)) {
			return ((InternalVariable)f.getAnnotation(
											InternalVariable.class)).type();
		} else {
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// Composition protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#staticInitialiseVariables()
	 */
	@Override
	public void			staticInitialiseVariables()
	{
		// This method is needed because, after testing, I found that the
		// static initialisation of fields is not visible to the reflective
		// accesses through the class java.lang.reflect.Field until after the
		// the constructor has terminated its execution. Hence, copying the
		// Value<?> instances to the variables2values data structure is
		// only possible after the constructor has terminated.

		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			try {
				Field f = this.exportedVariables[i].getField();
				f.setAccessible(true);
				this.variables2values.put(this.exportedVariables[i],
										  (Value<?>) f.get(this));
				((Value<?>)f.get(this)).
						setVariableDescriptor(this.exportedVariables[i]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		for (int i = 0 ; i < this.internalVariables.length ; i++) {
			try {
				Field f = this.internalVariables[i].getField();
				f.setAccessible(true);
				this.variables2values.put(this.internalVariables[i],
										  (Value<?>) f.get(this));
				((Value<?>)f.get(this)).
						setVariableDescriptor(this.internalVariables[i]);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// Invariants checking
		assert	AtomicHIOA.implementationInvariants(this) :
				new NeoSim4JavaException(
						"AtomicHIOA.implementationInvariants(this)");
		assert	AtomicHIOA.invariants(this) :
				new NeoSim4JavaException("AtomicHIOA.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Simulator manipulation related methods (e.g., definition, composition,
	// ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#isTIOA()
	 */
	@Override
	public boolean		isTIOA()
	{
		return this.importedVariables.length == 0 &&
								this.exportedVariables.length == 0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#isExportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isExportedVariable(
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		assert	sourceVariableName != null && !sourceVariableName.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
						+ "sourceVariableName != null && "
						+ "!sourceVariableName.isEmpty()");
		assert	sourceVariableType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sourceVariableType != null");

		boolean ret = false;
		for (StaticVariableDescriptor vd : this.exportedVariables) {
			ret = ret || (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType()));
			if (ret) break;
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#isImportedVariable(java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isImportedVariable(
		String sinkVariableName,
		Class<?> sinkVariableType
		)
	{
		assert	sinkVariableName != null && !sinkVariableName.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkVariableName != null && "
						+ "!sinkVariableName.isEmpty()");
		assert	sinkVariableType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkVariableType != null");

		boolean ret = false;
		for (StaticVariableDescriptor vd : this.importedVariables) {
			ret = ret || (vd.getName().equals(sinkVariableName) &&
							sinkVariableType.isAssignableFrom(vd.getType()));
			if (ret) break;
		}
		return ret;
	}

	/**
	 * return true if the variable is internal for this model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sinkVariableName != null && !sinkVariableName.isEmpty()}
	 * pre	{@code sinkVariableType != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param variableName	variable name.
	 * @param variableType	variable type.
	 * @return					true if the variable is internal for this model.
	 */
	protected boolean	isInternalVariable(
		String variableName,
		Class<?> variableType
		)
	{
		assert	variableName != null && !variableName.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
						+ "variableName != null && "
						+ "!variableName.isEmpty()");
		assert	variableType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "variableType != null");

		boolean ret = false;
		for (StaticVariableDescriptor vd : this.internalVariables) {
			ret = ret || (vd.getName().equals(variableName) &&
							variableType.isAssignableFrom(vd.getType()));
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getImportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getImportedVariables()
	{
		StaticVariableDescriptor[] ret =
				new StaticVariableDescriptor[this.importedVariables.length];
		for (int i = 0 ; i <this.importedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
							this.importedVariables[i].getName(),
							this.importedVariables[i].getType(),
							this.importedVariables[i].getVisibility());
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getExportedVariables()
	 */
	@Override
	public StaticVariableDescriptor[]	getExportedVariables()
	{
		StaticVariableDescriptor[] ret =
			new StaticVariableDescriptor[this.exportedVariables.length];
		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			ret[i] = new StaticVariableDescriptor(
							this.exportedVariables[i].getName(),
							this.exportedVariables[i].getType(),
							this.exportedVariables[i].getVisibility());
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#getActualExportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Value<?>		getActualExportedVariableValueReference(
		String modelURI,
		String sourceVariableName,
		Class<?> sourceVariableType
		)
	{
		assert	modelURI != null && modelURI.equals(this.getURI()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && modelURI.equals(this.getURI())");
		assert	isExportedVariable(sourceVariableName, sourceVariableType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isExportedVariable(sourceVariableName, sourceVariableType)");

		Value<?> ret = null;
		for (VariableDescriptor vd : this.variables2values.keySet()) {
			if (vd.getName().equals(sourceVariableName) &&
						sourceVariableType.isAssignableFrom(vd.getType())) {
				ret = this.variables2values.get(vd);
				break;
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#setImportedVariableValueReference(java.lang.String, java.lang.String, java.lang.Class, fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	public void			setImportedVariableValueReference(
		String modelURI,
		String sinkVariableName,
		Class<?> sinkVariableType,
		Value<?> value
		)
	{
		assert	modelURI != null && modelURI.equals(this.getURI()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && modelURI.equals(this.getURI())");
		assert	isImportedVariable(sinkVariableName, sinkVariableType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "isImportedVariable(sinkVariableName, sinkVariableType)");

		VariableDescriptor vd = null;
		for (VariableDescriptor temp : this.importedVariables) {
			if (temp.getName().equals(sinkVariableName) &&
						sinkVariableType.isAssignableFrom(temp.getType())) {
				vd = temp;
				break;
			}
		}
		assert	vd != null;

		vd.getField().setAccessible(true);
		try {
			vd.getField().set(this, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
					"The field " + vd.getField().getName() +
					" is not declared or inherited from the class " +
							this.getClass().getCanonicalName(), e) ;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"The field " + vd.getField().getName() + " of class " +
					this.getClass().getCanonicalName() +
					" is not reflectively accessible", e) ;
		}
		this.variables2values.put(vd, value);
	}

	// -------------------------------------------------------------------------
	// Simulation protocol related methods
	// -------------------------------------------------------------------------

	/**
	 * return true if all model variables have their associated time
	 * initialised.
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
	public boolean		allModelVariablesTimeInitialised()
	{
		return this.allVariablesTimeInitialised;
	}

	/**
	 * return true if all model variables are fully initialised.
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
	public boolean		allModelVariablesInitialised()
	{
		boolean ret = true;
		for (VariableDescriptor avd : this.variables2values.keySet()) {
			ret = ret && this.variables2values.get(avd).isInitialised();
			if (!ret) { break; }
		}
		return ret;
	}

	/**
	 * initialise the state of the model, including the time associated with
	 * all model variables.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code initialTime != null}
	 * pre	{@code !allModelVariablesTimeInitialised()}
	 * post	{@code allModelVariablesTimeInitialised()}
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		assert	initialTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "initialTime != null");
		assert	!allModelVariablesTimeInitialised() :
				new NeoSim4JavaException("Precondition violation: "
									+ "!allModelVariablesTimeInitialised()");

		super.initialiseState(initialTime);

		for (VariableDescriptor avd : this.variables2values.keySet()) {
			try {
				this.variables2values.get(avd).initialiseTime(initialTime);
			} catch (Exception e) {
				throw new RuntimeException(e) ;
			}
		}
		this.allVariablesTimeInitialised = true;
	}

	/**
	 * end the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code !allModelVariablesInitialised()}
	 * post	{@code !allModelVariablesTimeInitialised()}
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		for (int i = 0 ; i < this.exportedVariables.length ; i++) {
			this.variables2values.get(this.exportedVariables[i]).reinitialise();
		}
		for (int i = 0 ; i < this.internalVariables.length ; i++) {
			this.variables2values.get(this.internalVariables[i]).reinitialise();
		}
		this.allVariablesTimeInitialised = false;

		super.endSimulation(endTime);
	}

	//--------------------------------------------------------------------------
	// Debugging
	//--------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		System.out.println(indent + "--------------------");
		try {
			System.out.println(indent + "AtomicHIOA " + this.getURI() +
				" " + this.currentStateTime.getSimulatedTime()
				+ " " + elapsedTime.getSimulatedDuration());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println(indent + "--------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "--------------------");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		StringBuffer sb = new StringBuffer();
		try {
			this.modelContentAsString(indent, sb);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}	
		System.out.print(sb.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#modelContentAsString(java.lang.String, java.lang.StringBuffer)
	 */
	@Override
	protected void		modelContentAsString(String indent, StringBuffer sb)
	{
		super.modelContentAsString(indent, sb);
		sb.append(indent);
		sb.append("Imported variables:\n");
		for (VariableDescriptor vd : this.importedVariables) {
			sb.append(indent);
			sb.append("  ");
			sb.append(
				this.modelVariableAsString(vd, this.variables2values.get(vd)));
			sb.append('\n');
		}
		sb.append(indent);
		sb.append("Exported variables:\n");
		for (VariableDescriptor vd : this.exportedVariables) {
			sb.append(indent);
			sb.append("  ");
			sb.append(
				this.modelVariableAsString(vd, this.variables2values.get(vd)));
			sb.append('\n');
		}
		sb.append(indent);
		sb.append("Internal variables:\n");
		for (VariableDescriptor vd : this.internalVariables) {
			sb.append(indent);
			sb.append("  ");
			sb.append(
				this.modelVariableAsString(vd, this.variables2values.get(vd)));
			sb.append('\n');
		}
	}

	/**
	 * return a string representing the information about the model variable. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code vd != null}
	 * post	{@code ret != null && ret.length() > 0}
	 * </pre>
	 *
	 * @param vd			the descriptor of the model variable.
	 * @param v				the value object attached to the model variable.
	 * @return				a string representing the information about the model variable. 
	 */
	protected String		modelVariableAsString(
		VariableDescriptor vd,
		Value<?> v
		)
	{
		assert	vd != null;

		StringBuffer ret = new StringBuffer("Variable[VariableDescriptor(");
		ret.append("name = "); ret.append(vd.getName()); ret.append(", ");
		ret.append("uri = "); ret.append(vd.getOwner().getURI()); ret.append(", ");
		ret.append("type = "); ret.append(vd.getType()); ret.append(", ");
		ret.append("visibility = "); ret.append(vd.getVisibility()); ret.append(", ");
		ret.append("field name = "); ret.append(vd.getField().getName());
		ret.append("), Value(");
		if (v != null) {
			ret.append("uri = "); ret.append(v.getOwner().getURI()); ret.append(", ");
			ret.append("time unit = "); ret.append(v.getTimeUnit()); ret.append(", ");
			ret.append("descriptor = "); ret.append(v.getDescriptor().getName()); ret.append(", ");
			ret.append("v = "); ret.append(v.toString()); ret.append(")]");
		} else {
			ret.append("null)]");
		}
		return ret.toString();
	}
}
// -----------------------------------------------------------------------------
