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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.StandardRTAtomicHIOA_Factory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor.RTSchedulerProviderFI;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptorI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.RTModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.lang.reflect.InvocationTargetException;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTAtomicHIOA_Descriptor</code> describes real time atomic
 * HIOA in DEVS simulation architectures.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In addition to the standard model descriptors, RT model descriptors ensure
 * the coherence of the RT-related declarations and manage the acceleration
 * factor as well as the access to a RT-enabled scheduler.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTAtomicHIOA_Descriptor
extends		AtomicHIOA_Descriptor
implements	RTAtomicModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** the real time scheduler provider attached to this model.			*/
	protected RTSchedulerProviderFI	schedulerProvider;
	/** the acceleration factor used to control the pace of the simulation
	 *  when converting the simulated time to real time.					*/
	protected double				accelerationFactor;

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
		RTAtomicHIOA_Descriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.accelerationFactor > 0.0,
					RTAtomicHIOA_Descriptor.class,
					instance,
					"accelerationFactor > 0.0");
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
	protected static boolean	invariants(
		RTAtomicHIOA_Descriptor instance
		)
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
	 * create a real time atomic HIOA model descriptor.
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
	 * pre	{@code accelerationFactor > 0.0}
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
	 * @param schedulerProvider		lambda-expression which execution provides the real time scheduler to be set on the simulaiton engine of the model.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 */
	@SuppressWarnings("unchecked")
	public				RTAtomicHIOA_Descriptor(
		Class<? extends AtomicModel> modelClass,
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		StaticVariableDescriptor[] importedVariables,
		StaticVariableDescriptor[] exportedVariables,
		RTSchedulerProviderFI schedulerProvider,
		double accelerationFactor
		)
	{
		super(modelClass, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit,
			  (AtomicModelFactoryI)
			  		((RTModelFactoryI)(amFactory == null ?
			  			new StandardRTAtomicHIOA_Factory(
								(Class<? extends AtomicHIOA>) modelClass)
					:	amFactory)).setAccelerationFactor(accelerationFactor),
			  importedVariables, exportedVariables);

		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
										+ "accelerationFactor > 0.0");

		this.schedulerProvider = schedulerProvider;
		this.accelerationFactor = accelerationFactor;

		assert	RTAtomicHIOA_Descriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"RTAtomicHIOA_Descriptor.implementationInvariants(this)");
		assert	RTAtomicHIOA_Descriptor.invariants(this) :
				new NeoSim4JavaException(
						"RTAtomicHIOA_Descriptor.invariants(this)");
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
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @return						the model descriptor.
	 */
	@SuppressWarnings("unchecked")
	public static RTAtomicHIOA_Descriptor	create(
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
		assert	amFactory == null ||
							AtomicHIOA.class.isAssignableFrom(
												amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "AtomicHIOA.class.isAssignableFrom("
							+ "amFactory.getModelClass())");

		return RTAtomicHIOA_Descriptor.create(
							(Class<? extends AtomicHIOA>) modelClass,
							modelURI, simulatedTimeUnit,
							amFactory,
							AtomicRTEngine.STD_SCHEDULER_PROVIDER,
							AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

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
	 * pre	{@code schedulerProvider != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param schedulerProvider		lambda-expression which execution provides the real time scheduler to be set on the simulaiton engine of the model.
	 * @return						the model descriptor.
	 */
	public static RTAtomicHIOA_Descriptor	create(
		Class<? extends AtomicHIOA> modelClass,
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		RTSchedulerProviderFI schedulerProvider
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
		assert	amFactory == null ||
							AtomicHIOA.class.isAssignableFrom(
												amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "AtomicHIOA.class.isAssignableFrom("
							+ "amFactory.getModelClass())");
		assert	schedulerProvider != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "schedulerProvider != null");

		return RTAtomicHIOA_Descriptor.create(
							modelClass,
							modelURI, simulatedTimeUnit,
							amFactory,
							schedulerProvider,
							AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

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
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 * @return						the model descriptor.
	 */
	public static RTAtomicHIOA_Descriptor	create(
		Class<? extends AtomicHIOA> modelClass,
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		double accelerationFactor
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
		assert	amFactory == null ||
							AtomicHIOA.class.isAssignableFrom(
												amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
							+ "amFactory == null || "
							+ "AtomicHIOA.class.isAssignableFrom("
							+ "amFactory.getModelClass())");
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
							+ "accelerationFactor > 0.0");

		return RTAtomicHIOA_Descriptor.create(
							modelClass,
							modelURI, simulatedTimeUnit,
							amFactory,
							AtomicRTEngine.STD_SCHEDULER_PROVIDER,
							accelerationFactor);
	}

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
	 * pre	{@code schedulerProvider != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the model.
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param amFactory				atomic model factory creating the model or null if none.
	 * @param schedulerProvider		lambda-expression which execution provides the real time scheduler to be set on the simulaiton engine of the model.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 * @return						the model descriptor.
	 */
	@SuppressWarnings("unchecked")
	public static RTAtomicHIOA_Descriptor	create(
		Class<? extends AtomicHIOA> modelClass,
		String modelURI,
		TimeUnit simulatedTimeUnit,
		AtomicModelFactoryI amFactory,
		RTSchedulerProviderFI schedulerProvider,
		double accelerationFactor
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
		assert	amFactory == null ||
								AtomicHIOA.class.isAssignableFrom(
													amFactory.getModelClass()) :
				new NeoSim4JavaException("Precondition violation: "
								+ "amFactory == null || "
								+ "AtomicHIOA.class.isAssignableFrom("
								+ "amFactory.getModelClass())");
		assert	schedulerProvider != null :
				new NeoSim4JavaException("Precondition violation: "
								+ "schedulerProvider != null");
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
								+ "accelerationFactor > 0.0");

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
			importedEvents =
				(Class<? extends EventI>[])
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
				 SecurityException e) {
			// Shall never happen; sought methods appear by default.
			throw new RuntimeException(e) ;
		}

		return new RTAtomicHIOA_Descriptor(
					modelClass, modelURI, importedEvents,
					exportedEvents, simulatedTimeUnit, amFactory,
					importedVariables, exportedVariables,
					schedulerProvider, accelerationFactor);
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.RTModelDescriptorI#getAccelerationFactor()
	 */
	@Override
	public double		getAccelerationFactor()
	{
		return this.accelerationFactor;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptorI#isSchedulerProviderSet()
	 */
	@Override
	public boolean		isSchedulerProviderSet()
	{
		return this.schedulerProvider != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptorI#setSchedulerProvider(fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor.RTSchedulerProviderFI)
	 */
	@Override
	public void			setSchedulerProvider(
		RTSchedulerProviderFI schedulerProvider
		)
	{
		assert	schedulerProvider != null :
				new NeoSim4JavaException("Precondition violation: "
											+ "schedulerProvider != null");

		this.schedulerProvider = schedulerProvider;
	}

	/**
	 * .
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSchedulerProviderSet()}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor#createAtomicModel()
	 */
	@Override
	public AtomicModelI		createAtomicModel()
	{
		assert	this.isSchedulerProviderSet() :
				new NeoSim4JavaException("Precondition violation: "
												+ "isSchedulerProviderSet()");

		AtomicHIOA hioa = (AtomicHIOA) this.amFactory.createAtomicModel();
		hioa.staticInitialiseVariables();
		RTAtomicSimulatorI rte =(RTAtomicSimulatorI) hioa.getSimulationEngine();
		rte.setRTScheduler(this.schedulerProvider.provide());
		return hioa;
	}
}
// -----------------------------------------------------------------------------
