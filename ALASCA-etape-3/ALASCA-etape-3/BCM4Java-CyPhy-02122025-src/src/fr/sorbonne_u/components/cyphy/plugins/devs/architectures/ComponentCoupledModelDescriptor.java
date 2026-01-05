package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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
import java.util.HashMap;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPluginFactoryI;
import fr.sorbonne_u.components.cyphy.plugins.devs.StandardCoordinatorPluginFactory;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentCoupledModelDescriptor</code> implements a
 * descriptor of a DEVS coupled models that is held by a BCM component in a BCM
 * component assembly, associating their URI, their static information including
 * the mapping to the holding component in the assembly, as well as an optional
 * factory that can be used to instantiate a coupled model object to run
 * simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When integrating DEVS simulation models with BCM components, a simulation
 * architecture (DEVS models composition) is mapped onto a component assembly.
 * Cyber-physical components have simulation models implementing their
 * behavioural model. At the component assembly level, a global simulation
 * architecture describes the way component simulation models are composed to
 * obtain a system-wide simulation architecture. The present class is used in
 * global architectures to describe coupled simulation models that will be
 * attached to components playing the role of simulation coordinators (in terms
 * of DEVS simulation execution).
 * </p>
 * <p>
 * Contrary to cyber-physical components, components having a coordinator role
 * do not create neither their coupled model nor the BCM DEVS coordination
 * plug-in factoring the coordination code. The present class provides methods
 * to create them on the coordinator component. Hence, as a descriptor, it is a
 * serializable to be sent by RMI to the coordinator component by the
 * supervisor one, and when received on the coordinator component, the creator
 * component can be set on the descriptor before calling the method
 * <code>compose</code> that will connect the coordinator component to the
 * components holding the submodels and then create the coupled model.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code componentReflectionInboundPortURI != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-06-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentCoupledModelDescriptor
extends		CoupledModelDescriptor
implements	ComponentCoupledModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** URI of the reflection inbound port of the component holding the
	 *  model.																*/
	protected final String					componentReflectionInboundPortURI;
	protected final Class<? extends CoordinatorPlugin>		pluginClass;
	protected final CoordinatorPluginFactoryI				pluginFactory;
	/** the coordination plug-in installed in the creator component.		*/
	transient protected CoordinatorPlugin	creatorCoordinatorPlugin;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * instantiate a component coupled model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compReflIBPURI != null && !compReflIBPURI.isEmpty()}
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
	protected			ComponentCoupledModelDescriptor(
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
		super(modelClass, modelURI, submodelURIs, imported, reexported,
			  connections, cmFactory, mc);

		assert	compReflIBPURI != null && !compReflIBPURI.isEmpty() :
				new PreconditionException(
						"compReflIBPURI != null && !compReflIBPURI.isEmpty()");

		this.componentReflectionInboundPortURI = compReflIBPURI;
		this.pluginClass = pluginClass;
		if (pluginFactory == null) {
			Class<? extends CoordinatorPlugin> pc = null;
			if (pluginClass == null) {
				pc = CoordinatorPlugin.class;
			} else {
				pc = pluginClass;
			}
			this.pluginFactory = new StandardCoordinatorPluginFactory(pc);
		} else {
			this.pluginFactory = pluginFactory;
		}

		assert	ComponentCoupledModelDescriptor.checkInvariant(this) :
				new PostconditionException(
						"ComponentCoupledModelDescriptor.checkInvariant(this)");
	}

	/**
	 * check the invariant on the part of the descriptor implemented in this
	 * class (a similar method in the superclass addresses the invariant at
	 * this level).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}		// no postcondition.
	 * </pre>
	 *
	 * @param d	descriptor to be checked.
	 * @return	true if the descriptor is conform to its invariant.
	 */
	public static boolean	checkInvariant(ComponentCoupledModelDescriptor d)
	{
		assert	d != null;

		boolean hasHostComponent = d.componentReflectionInboundPortURI != null;
		assert	hasHostComponent :
					new InvariantException(
							"component reflection inbound port URI is null!");
		return hasHostComponent;
	}

	/**
	 * create a component coupled model descriptor.
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
	 * @param compReflIBPURI		URI of the coupled model holding coordinator component reflection inbound port.
	 * @param pluginClass			instantiation class of the coordination plug-in associated to the coupled model.
	 * @param pluginFactory			factory creating the instantiation class of the coordination plug-in associated to the coupled model.
	 * @return						the new component coupled model descriptor.
	 */
	public static ComponentCoupledModelDescriptor	create(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		String compReflIBPURI,
		Class<? extends CoordinatorPlugin> pluginClass,
		CoordinatorPluginFactoryI pluginFactory
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
		return new ComponentCoupledModelDescriptor(
								modelClass, modelURI, submodelURIs,
								imported, reexported, connections,
								cmFactory, new ComponentModelComposer(),
								compReflIBPURI, pluginClass, pluginFactory);
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#getComponentReflectionInboundPortURI()
	 */
	@Override
	public String		getComponentReflectionInboundPortURI()
	{
		return this.componentReflectionInboundPortURI;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptorI#createCoordinatorPlugin()
	 */
	@Override
	public CoordinatorPlugin createCoordinatorPlugin()
	{
		try {
			CoordinatorPlugin ret =
								this.pluginFactory.createCoordinatorPlugin();
			ret.setPluginURI(getModelURI());
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
