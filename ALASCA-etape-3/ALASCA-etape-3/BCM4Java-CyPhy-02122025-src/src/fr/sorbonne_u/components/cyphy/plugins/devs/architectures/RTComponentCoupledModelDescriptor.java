package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPluginFactoryI;
import fr.sorbonne_u.devs_simulation.models.StandardRTCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTComponentCoupledModelDescriptor</code> implements a
 * descriptor of a DEVS coupled models that is held by a BCM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * If Java allowed multiple inheritance, this class would inherit from both
 * <code>ComponentCoupledModelDescriptor</code> and
 * <code>RTCoupledModelDescriptor</code>. Without this possibility, the choice
 * here is to inherit from <code>ComponentCoupledModelDescriptor</code> and
 * copy the code from <code>RTCoupledModelDescriptor</code>. See both for more
 * documentation applying to this class.
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
 * <p>Created on : 2020-12-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTComponentCoupledModelDescriptor
extends		ComponentCoupledModelDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the acceleration factor used to control the pace of the simulation
	 *  when converting the simulated time to real time.					*/
	protected double			accelerationFactor;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new real time component coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compReflIBPURI != null}
	 * post	{@code ComponentCoupledModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer for this component coupled model composition.
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 */
	public				RTComponentCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ComponentModelComposer mc,
		String compReflIBPURI,
		Class<? extends CoordinatorPlugin> pluginClass,
		CoordinatorPluginFactoryI pluginFactory
		)
	{
		this(modelClass, modelURI, submodelURIs,
			 imported, reexported, connections,
			 cmFactory, mc, compReflIBPURI, pluginClass, pluginFactory,
			 AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a new real time component coupled model creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compReflIBPURI != null && !compReflIBPURI.isEmpty()}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code ComponentCoupledModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer for this component coupled model composition.
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 */
	public				RTComponentCoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ComponentModelComposer mc,
		String compReflIBPURI,
		Class<? extends CoordinatorPlugin> pluginClass,
		CoordinatorPluginFactoryI pluginFactory,
		double accelerationFactor
		)
	{
		super(modelClass, modelURI, submodelURIs,
			  imported, reexported, connections,
			  (cmFactory == null ?
				  new StandardRTCoupledModelFactory(modelClass)
			  :	  cmFactory),
			  mc, compReflIBPURI, pluginClass, pluginFactory);

		assert	compReflIBPURI != null && !compReflIBPURI.isEmpty() :
				new PreconditionException(
						"compReflIBPURI != null && !compReflIBPURI.isEmpty()");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;
	}

	/**
	 * create a real time component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code compReflIBPURI != null}
	 * post	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
	 * post	{@code ComponentCoupledModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer for this component coupled model composition.
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 * @return						the real time component coupled model descriptor.
	 */
	public static RTComponentCoupledModelDescriptor	create(
			Class<? extends CoupledModelI> modelClass,
			String modelURI,
			Set<String> submodelURIs,
			Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported,
			Map<EventSource, EventSink[]> connections,
			CoupledModelFactoryI cmFactory,
			ComponentModelComposer mc,
			String compReflIBPURI,
			Class<? extends CoordinatorPlugin> pluginClass,
			CoordinatorPluginFactoryI pluginFactory
			)
	{
		return RTComponentCoupledModelDescriptor.create(
					modelClass, modelURI, submodelURIs,
					imported, reexported, connections,
					cmFactory, mc, compReflIBPURI, pluginClass, pluginFactory,
					AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a real time component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code compReflIBPURI != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
	 * post	{@code ComponentCoupledModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 * @return						the real time component coupled model descriptor.
	 */
	public static RTComponentCoupledModelDescriptor	create(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		String compReflIBPURI,
		Class<? extends CoordinatorPlugin> pluginClass,
		CoordinatorPluginFactoryI pluginFactory,
		double accelerationFactor
		)
	{
		return new RTComponentCoupledModelDescriptor(
								modelClass, modelURI, submodelURIs,
								imported, reexported, connections,
								cmFactory, new ComponentModelComposer(),
								compReflIBPURI, pluginClass, pluginFactory,
								accelerationFactor);
	}

	/**
	 * create a real time component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code compReflIBPURI != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code CoupledModelDescriptor.checkInternalConsistency(this)}
	 * post	{@code ComponentCoupledModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled model factory allowing to create the coupled model or null if none.
	 * @param mc					model composer.	
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulcation clock and the real time.
	 * @return						the real time component coupled model descriptor.
	 */
	public static RTComponentCoupledModelDescriptor	create(
			Class<? extends CoupledModelI> modelClass,
			String modelURI,
			Set<String> submodelURIs,
			Map<Class<? extends EventI>, EventSink[]> imported,
			Map<Class<? extends EventI>, ReexportedEvent> reexported,
			Map<EventSource, EventSink[]> connections,
			CoupledModelFactoryI cmFactory,
			ComponentModelComposer mc,
			String compReflIBPURI,
			Class<? extends CoordinatorPlugin> pluginClass,
			CoordinatorPluginFactoryI pluginFactory,
			double accelerationFactor
			)
	{
		if (imported == null) {
			imported = new HashMap<Class<? extends EventI>, EventSink[]>();
		}
		if (reexported == null) {
			reexported =
				new HashMap<Class<? extends EventI>, ReexportedEvent>();
		}
		if (connections == null) {
			connections = new HashMap<EventSource, EventSink[]>();
		}
		if (mc == null) {
			mc = new ComponentModelComposer();
		}
		return new RTComponentCoupledModelDescriptor(
									modelClass, modelURI, submodelURIs,
									imported, reexported, connections,
									cmFactory, mc, compReflIBPURI,
									pluginClass, pluginFactory,
									accelerationFactor);
	}
}
// -----------------------------------------------------------------------------
