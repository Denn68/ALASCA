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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.StandardRTCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.architectures.RTModelDescriptorI;
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
 * The class <code>RTCoupledHIOA_Descriptor</code> describes real time coupled
 * HIOA in model architectures.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The need for this class is to cope with the case when a coupled model is
 * created with a real time atomic simulation engine which acceleration
 * factor must be initialised.
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
 * <p>Created on : 2020-11-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTCoupledHIOA_Descriptor
extends		CoupledHIOA_Descriptor
implements	RTModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the acceleration factor used to control the pace of the simulation
	 *  when converting the simulated time to real time.					*/
	protected double			accelerationFactor;

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
		RTCoupledHIOA_Descriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.accelerationFactor > 0.0,
				RTCoupledHIOA_Descriptor.class,
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
		RTCoupledHIOA_Descriptor instance
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
	 * create a new real time coupled HIOA creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code imported == null || imported.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code reexported == null || reexported.values().stream().allMatch(re -> submodelURIs.contains(re.exportingModelURI))}
	 * pre	{@code connections == null || connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
	 * pre	{@code connections == null || connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code connections == null || connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
	 * pre	{@code importedVars == null || importedVars.keySet().stream().allMatch(vd -> !reexportedVars.values().contains(vd) && Stream.of(importedVars.get(vd)).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(vd.getType())))}
	 * pre	{@code reexportedVars == null || reexportedVars.keySet().stream().allMatch(vs -> submodelURIs.contains(vs.exportingModelURI) && !importedVars.keySet().contains(reexportedVars.get(vs)) && reexportedVars.get(vs).getType().isAssignableFrom(vs.type))}
	 * pre	{@code bindings == null || bindings.entrySet().stream().allMatch(e -> submodelURIs.contains(e.getKey().exportingModelURI) && Stream.of(e.getValue()).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(e.getKey().type)))}
	 * post {@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 */
	public			RTCoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		)
	{
		this(modelClass, modelURI, submodelURIs,
			 imported, reexported, connections,
			 cmFactory, importedVars, reexportedVars, bindings,
			 AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a new real time coupled HIOA creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code imported == null || imported.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code reexported == null || reexported.values().stream().allMatch(re -> submodelURIs.contains(re.exportingModelURI))}
	 * pre	{@code connections == null || connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
	 * pre	{@code connections == null || connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code connections == null || connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
	 * pre	{@code importedVars == null || importedVars.keySet().stream().allMatch(vd -> !reexportedVars.values().contains(vd) && Stream.of(importedVars.get(vd)).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(vd.getType())))}
	 * pre	{@code reexportedVars == null || reexportedVars.keySet().stream().allMatch(vs -> submodelURIs.contains(vs.exportingModelURI) && !importedVars.keySet().contains(reexportedVars.get(vs)) && reexportedVars.get(vs).getType().isAssignableFrom(vs.type))}
	 * pre	{@code bindings == null || bindings.entrySet().stream().allMatch(e -> submodelURIs.contains(e.getKey().exportingModelURI) && Stream.of(e.getValue()).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(e.getKey().type)))}
	 * pre	{@code accelerationFactor > 0.0}
	 * post {@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulation clock and the real time.
	 */
	public			RTCoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings,
		double accelerationFactor)
	{
		this(modelClass, modelURI, submodelURIs,
			 imported, reexported, connections,
			 cmFactory, importedVars, reexportedVars, bindings,
			 new HIOA_Composer(), accelerationFactor);
	}

	/**
	 * create a new real time coupled HIOA creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code imported == null || imported.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code reexported == null || reexported.values().stream().allMatch(re -> submodelURIs.contains(re.exportingModelURI))}
	 * pre	{@code connections == null || connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
	 * pre	{@code connections == null || connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code connections == null || connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
	 * pre	{@code importedVars == null || importedVars.keySet().stream().allMatch(vd -> !reexportedVars.values().contains(vd) && Stream.of(importedVars.get(vd)).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(vd.getType())))}
	 * pre	{@code reexportedVars == null || reexportedVars.keySet().stream().allMatch(vs -> submodelURIs.contains(vs.exportingModelURI) && !importedVars.keySet().contains(reexportedVars.get(vs)) && reexportedVars.get(vs).getType().isAssignableFrom(vs.type))}
	 * pre	{@code bindings == null || bindings.entrySet().stream().allMatch(e -> submodelURIs.contains(e.getKey().exportingModelURI) && Stream.of(e.getValue()).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(e.getKey().type)))}
	 * pre	{@code hioa_mc != null}
	 * post {@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 * @param hioa_mc				model composer to be used.
	 */
	public				RTCoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings,
		HIOA_Composer hioa_mc
		)
	{
		this(modelClass, modelURI, submodelURIs,
			 imported, reexported, connections,
			 cmFactory, importedVars, reexportedVars, bindings, hioa_mc,
			 AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * create a new real time coupled HIOA creation descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelClass != null || cmFactory != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code submodelURIs != null && submodelURIs.size() > 1}
	 * pre	{@code submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty())}
	 * pre	{@code imported == null || imported.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code reexported == null || reexported.values().stream().allMatch(re -> submodelURIs.contains(re.exportingModelURI))}
	 * pre	{@code connections == null || connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
	 * pre	{@code connections == null || connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code connections == null || connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
	 * pre	{@code importedVars == null || importedVars.keySet().stream().allMatch(vd -> !reexportedVars.values().contains(vd) && Stream.of(importedVars.get(vd)).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(vd.getType())))}
	 * pre	{@code reexportedVars == null || reexportedVars.keySet().stream().allMatch(vs -> submodelURIs.contains(vs.exportingModelURI) && !importedVars.keySet().contains(reexportedVars.get(vs)) && reexportedVars.get(vs).getType().isAssignableFrom(vs.type))}
	 * pre	{@code bindings == null || bindings.entrySet().stream().allMatch(e -> submodelURIs.contains(e.getKey().exportingModelURI) && Stream.of(e.getValue()).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(e.getKey().type)))}
	 * pre	{@code hioa_mc != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * post {@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass			class defining the coupled model.
	 * @param modelURI				URI of the coupled model to be created.
	 * @param submodelURIs			URIs of the submodels of the coupled model.
	 * @param imported				map from imported event types to the submodels importing them.
	 * @param reexported			map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections			connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory				coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars			map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars		map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings				map between variables exported by submodels to ones imported by others.
	 * @param hioa_mc				model composer to be used.
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulation clock and the real time.
	 */
	public				RTCoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings,
		HIOA_Composer hioa_mc,
		double accelerationFactor)
	{
		super(modelClass, modelURI, submodelURIs,
			  imported, reexported, connections,
			  (cmFactory == null ?
				   (CoupledModelFactoryI)
					   		new StandardRTCoupledModelFactory(modelClass)
			  :	   cmFactory),
			  importedVars, reexportedVars, bindings, hioa_mc);

		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
											+ "accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;

		assert	RTCoupledHIOA_Descriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"RTCoupledHIOA_Descriptor.implementationInvariants(this)");
		assert	RTCoupledHIOA_Descriptor.invariants(this) :
				new NeoSim4JavaException(
						"RTCoupledHIOA_Descriptor.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.RTModelDescriptorI#getAccelerationFactor()
	 */
	@Override
	public double			getAccelerationFactor()
	{
		return this.accelerationFactor;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor#createCoupledModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI[])
	 */
	@Override
	public CoupledModelI	createCoupledModel(ModelI[] models)
	{
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0 ; i < models.length ; i++) {
			hs.add(models[i].getURI());
		}
		assert	this.submodelURIs.containsAll(hs) &&
											hs.containsAll(this.submodelURIs);
		assert	this.cmFactory != null :
				new NeoSim4JavaException("cmFactory != null");

		((RTModelFactoryI)this.cmFactory).
							setAccelerationFactor(this.accelerationFactor);
		return ((HIOA_Composer)this.mc).compose(
										models,
										this.modelURI,
										cmFactory,
										this.imported,
										this.reexported,
										this.connections,
										this.importedVars,
										this.reexportedVars,
										this.bindings);
	}
}
// -----------------------------------------------------------------------------
