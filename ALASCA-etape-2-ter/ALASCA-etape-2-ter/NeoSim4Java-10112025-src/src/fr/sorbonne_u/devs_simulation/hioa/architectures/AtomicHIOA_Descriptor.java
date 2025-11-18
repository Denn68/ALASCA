package fr.sorbonne_u.devs_simulation.hioa.architectures;

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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.StandardAtomicHIOA_Factory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.lang.reflect.InvocationTargetException;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicHIOA_Descriptor</code> describes atomic HIOA models in
 * DEVS simulation architectures, associating their URI, their imported and
 * exported event types, their imported and exported variables as well as a
 * factory that can be used to instantiate a model object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code modelClass == null || AtomicHIOA.class.isAssignableFrom(modelClass)}
 * invariant	{@code importedVariables != null && exportedVariables != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-07-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicHIOA_Descriptor
extends		AtomicModelDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 					serialVersionUID = 1L;
	/** Variables imported by this atomic HIOA.								*/
	public final StaticVariableDescriptor[]		importedVariables;
	/** Variables exported by this atomic HIOA.								*/
	public final StaticVariableDescriptor[]		exportedVariables;

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
	protected static boolean	implementationInvariants(AtomicHIOA_Descriptor instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.modelClass == null ||
						AtomicHIOA.class.isAssignableFrom(instance.modelClass),
					AtomicHIOA_Descriptor.class,
					instance,
					"modelClass == null || AtomicHIOA.class."
					+ "isAssignableFrom(modelClass)");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.importedVariables != null &&
										instance.exportedVariables != null,
					AtomicHIOA_Descriptor.class,
					instance,
					"importedVariables != null && exportedVariables != null");
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
	protected static boolean	invariants(AtomicHIOA_Descriptor instance)
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
	 * create an atomic HIOA model descriptor with given sets of imported and
	 * exported variables added to the imported and exported events of
	 * basic discrete event based models.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || amFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code importedEvents != null}
	 * pre	{@code exportedEvents != null}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code modelClass == null || AtomicHIOA.class.isAssignableFrom(modelClass)}
	 * pre	{@code amFactory == null || AtomicHIOA.class.isAssignableFrom(amFactory.getModelClass())}
	 * pre	{@code importedVariables != null}
	 * pre	{@code exportedVariables != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param importedEvents		array of imported event types.
	 * @param exportedEvents		array of exported event types.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param importedVariables		variables imported by this HIOA.
	 * @param exportedVariables		variables exported by this HIOA.
	 */
	@SuppressWarnings("unchecked")
	protected			AtomicHIOA_Descriptor(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		StaticVariableDescriptor[] importedVariables,
		StaticVariableDescriptor[] exportedVariables
		)
	{
		super(modelClass, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit,
			  (amFactory == null ?
					new StandardAtomicHIOA_Factory(
								(Class<? extends AtomicHIOA>)modelClass)
			  :		amFactory));

		assert	modelClass == null ||
								AtomicHIOA.class.isAssignableFrom(modelClass) :
				new NeoSim4JavaException("Precondition violation: "
							+ "modelClass == null || "
							+ "AtomicHIOA.class.isAssignableFrom(modelClass)");
		assert	amFactory == null || amFactory.getModelClass() != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "amFactory.getModelClass() != null");
		assert	amFactory == null || AtomicHIOA.class.isAssignableFrom(
													amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "AtomicHIOA.class.isAssignableFrom("
							+ "amFactory.getModelClass())");
		assert	importedVariables != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "importedVariables != null");
		assert	exportedVariables != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "exportedVariables != null");

		this.importedVariables = importedVariables;
		if (Architecture.DEBUG) {
			for (int i = 0 ; i < this.importedVariables.length ; i++) {
				System.out.println("AtomicHIOA_Descriptor+imp " + modelURI + " " 
								   + this.importedVariables[i].getName());
			}
		}

		this.exportedVariables = exportedVariables;
		if (Architecture.DEBUG) {
			for (int i = 0 ; i < this.exportedVariables.length ; i++) {
				System.out.println("AtomicHIOA_Descriptor+exp " + modelURI + " " 
								   + this.exportedVariables[i].getName());
			}
		}

		assert	AtomicHIOA_Descriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"AtomicHIOA_Descriptor.implementationInvariants(this)");
		assert	AtomicHIOA_Descriptor.invariants(this) :
				new NeoSim4JavaException("AtomicHIOA_Descriptor.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * create an atomic HIOA descriptor extracting the sets of imported and
	 * exported variables as well as the imported and exported events from
	 * the model classes.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || amFactory != null}
	 * pre	{@code modelClass == null || AtomicHIOA.class.isAssignableFrom(modelClass)}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code amFactory == null || AtomicHIOA.class.isAssignableFrom(amFactory.getModelClass())}
	 * post	{@code AtomicHIOA_Descriptor.checkInvariant(ret)}
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @return						the model descriptor.
	 */
	@SuppressWarnings("unchecked")
	public static AtomicHIOA_Descriptor		create(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory
		)
	{
		assert	modelClass != null || amFactory != null :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelClass != null || amFactory != null");
		assert	modelClass == null ||
								AtomicHIOA.class.isAssignableFrom(modelClass) :
				new NeoSim4JavaException("Precondition violation: "
							+ "modelClass == null || "
							+ "AtomicHIOA.class.isAssignableFrom(modelClass)");
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "modelURI != null && !modelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "simulatedTimeUnit != null");
		assert	amFactory == null || amFactory.getModelClass() != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "amFactory.getModelClass() != null");
		assert	amFactory == null || AtomicHIOA.class.isAssignableFrom(
													amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "AtomicHIOA.class.isAssignableFrom("
							+ "amFactory.getModelClass())");

		Class<? extends AtomicHIOA> actualModelClass = null;
		if (modelClass != null) {
			actualModelClass = (Class<? extends AtomicHIOA>) modelClass;
		} else {
			assert	amFactory != null;
			actualModelClass =
					(Class<? extends AtomicHIOA>) amFactory.getModelClass();
		}

		Class<? extends EventI>[] importedEvents;
		Class<? extends EventI>[] exportedEvents;
		StaticVariableDescriptor[] importedVariables;
		StaticVariableDescriptor[] exportedVariables;
		try {
			importedEvents = (Class<? extends EventI>[])
					actualModelClass.getMethod(
								"getImportedEventTypes",
								new Class<?>[] {Class.class}).
						invoke(null, new Object[] {modelClass});
			exportedEvents =
				(Class<? extends EventI>[])
					actualModelClass.getMethod(
								"getExportedEventTypes",
								new Class<?>[] {Class.class}).
						invoke(null, new Object[] {modelClass});

			importedVariables =
				(StaticVariableDescriptor[])
					actualModelClass.getMethod(
								"staticGetImportedVars",
								new Class<?>[] {Class.class}).
						invoke(null, new Object[] {modelClass});
			exportedVariables =
				(StaticVariableDescriptor[])
					actualModelClass.getMethod(
								"staticGetExportedVars",
								new Class<?>[] {Class.class}).
						invoke(null, new Object[] {modelClass});
		} catch (IllegalAccessException | IllegalArgumentException |
				 InvocationTargetException | NoSuchMethodException |
				 SecurityException e)
		{
			// Shall never happen, sought methods appear by default
			throw new RuntimeException(e) ;
		}

		return new AtomicHIOA_Descriptor(
					modelClass, modelURI, importedEvents,
					exportedEvents, simulatedTimeUnit, amFactory,
					importedVariables, exportedVariables);
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor#createAtomicModel()
	 */
	@Override
	public AtomicModelI	createAtomicModel()
	{
		AtomicHIOA hioa = (AtomicHIOA) amFactory.createAtomicModel();
		hioa.staticInitialiseVariables();
		return hioa;
	}
}
// -----------------------------------------------------------------------------
