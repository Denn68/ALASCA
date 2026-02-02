package fr.sorbonne_u.devs_simulation.models.architectures;

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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.StandardRTCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor.RTSchedulerProviderFI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.RTModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTCoupledModelDescriptor</code> defines real time coupled
 * models in model architectures.
 *
 * <p><strong>Description</strong></p>
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
 * <p>Created on : 2020-11-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTCoupledModelDescriptor
extends		CoupledModelDescriptor
implements	RTModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 			serialVersionUID = 1L;
	/** the real time scheduler provider attached to this model when the
	 *  simulation engine is atomic.										*/
	protected RTSchedulerProviderFI		schedulerProvider;
	/** the acceleration factor used to control the pace of the simulation
	 *  when converting the simulated time to real time.					*/
	protected double					accelerationFactor;

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
		RTCoupledModelDescriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.accelerationFactor > 0.0, 
					RTCoupledModelDescriptor.class,
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
		RTCoupledModelDescriptor instance
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
	 * create a new real time coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 */
	public				RTCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory,
			 new ModelComposer(), AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a new real time coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 */
	public				RTCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		double accelerationFactor
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory,
			 new ModelComposer(), accelerationFactor);
	}

	/**
	 * create a new real time coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code mc != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer to be used.
	 */
	public				RTCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ModelComposer mc
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory,
			 mc, AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a new real time coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code mc != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer to be used.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 */
	public				RTCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ModelComposer mc,
		double accelerationFactor
		)
	{
		super(modelClass, modelURI, submodelURIs, imported, reexported,
			  connections,
			  (cmFactory == null ?
				   (CoupledModelFactoryI)
				   		new StandardRTCoupledModelFactory(modelClass)
			  :	   cmFactory),
			  mc);

		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;

		assert	RTCoupledModelDescriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"RTCoupledModelDescriptor.implementationInvariants(this)");
		assert	RTCoupledModelDescriptor.invariants(this) :
				new NeoSim4JavaException(
						"RTCoupledModelDescriptor.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
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
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor#createCoupledModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI[])
	 */
	@Override
	public CoupledModelI	createCoupledModel(ModelI[] models)
	{
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0 ; i < models.length ; i++) {
			hs.add(models[i].getURI());
		}
		assert	this.submodelURIs.containsAll(hs) &&
											hs.containsAll(this.submodelURIs) :
				new NeoSim4JavaException(
						"submodelURIs.containsAll(hs) && "
						+ "hs.containsAll(submodelURIs)");
		assert	this.cmFactory != null :
				new NeoSim4JavaException("cmFactory != null");

		((RTModelFactoryI)this.cmFactory).
								setAccelerationFactor(this.accelerationFactor);
		return this.mc.compose(models,
							   this.modelURI,
							   cmFactory,
							   this.imported,
							   this.reexported,
							   this.connections);
	}
}
// -----------------------------------------------------------------------------
