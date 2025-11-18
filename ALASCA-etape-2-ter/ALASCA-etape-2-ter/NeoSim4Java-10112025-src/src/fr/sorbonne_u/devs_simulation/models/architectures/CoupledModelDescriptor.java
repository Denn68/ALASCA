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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.StandardCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoupledModelDescriptor</code> defines coupled models in
 * simulation architectures, associating their URI, their static information,
 * as well as an optional factory that can be used to instantiate a coupled
 * model object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code modelClass != null || cmFactory != null}
 * invariant	{@code modelURI != null && !modelURI.isEmpty()}
 * invariant	{@code submodelURIs != null && submodelURIs.size() > 1}
 * invariant	{@code submodelURIs.stream().allMatch(uri -> uri != null && !uri.isEmpty())}
 * invariant	{@code imported != null}
 * invariant	{@code imported.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
 * invariant	{@code reexported != null}
 * invariant	{@code reexported.values().stream().allMatch(re -> submodelURIs.contains(re.exportingModelURI))}
 * invariant	{@code connections != null}
 * invariant	{@code connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
 * invariant	{@code connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
 * invariant	{@code connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
 * invariant	{@code mc != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoupledModelDescriptor
implements	Serializable,
			ModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L;
	/** class defining the coupled model.									*/
	public final Class<? extends CoupledModelI>			modelClass;
	/** URI of the model to be created.										*/
	public final String									modelURI;
	/** Set of URIs of the submodels of this coupled model.					*/
	public final Set<String>							submodelURIs;
	/** Map from imported event types to the internal sinks importing
	 *  them.																*/
	public final Map<Class<? extends EventI>,EventSink[]>		imported;
	/** Map from event types exported by submodels to event types exported
	 *  by this coupled model.												*/
	public final Map<Class<? extends EventI>,ReexportedEvent>	reexported;
	/** Map connecting submodel exported event types to submodels imported
	 *  ones which will consume them.										*/
	public final Map<EventSource,EventSink[]>			connections;
	/** Coupled model factory allowing to create the model.					*/
	public final CoupledModelFactoryI					cmFactory;
	/**	composition algorithm applicable to the submodels to create this
	 *  parent coupled model.												*/
	public final ModelComposer							mc;

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
		CoupledModelDescriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.modelClass != null || instance.cmFactory != null,
					CoupledModelDescriptor.class,
					instance,
					"modelClass != null || cmFactory != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.modelURI != null && !instance.modelURI.isEmpty(),
					CoupledModelDescriptor.class,
					instance,
					"modelURI != null && !modelURI.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.submodelURIs != null && instance.submodelURIs.size() > 1,
					CoupledModelDescriptor.class,
					instance,
					"submodelURIs != null && submodelURIs.size() > 1");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.submodelURIs.stream().allMatch(
										uri -> uri != null && !uri.isEmpty()),
					CoupledModelDescriptor.class,
					instance,
					"submodelURIs.stream().allMatch("
					+ "uri -> uri != null && !uri.isEmpty())");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.imported != null,
					CoupledModelDescriptor.class,
					instance,
					"imported != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.imported.values().stream().allMatch(
						ess -> Stream.of(ess).allMatch(
								es -> instance.submodelURIs.contains(
														es.importingModelURI))),
					CoupledModelDescriptor.class,
					instance,
					"imported.values().stream().allMatch(ess -> Stream.of(ess)."
					+ "allMatch(es -> submodelURIs.contains(es."
					+ "importingModelURI)))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.reexported != null,
					CoupledModelDescriptor.class,
					instance,
					"reexported != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.reexported.values().stream().allMatch(
						re -> instance.submodelURIs.contains(
														re.exportingModelURI)),
					CoupledModelDescriptor.class,
					instance,
					"reexported.values().stream().allMatch("
					+ "re -> submodelURIs.contains(re.exportingModelURI))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.connections != null,
					CoupledModelDescriptor.class,
					instance,
					"connections != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.connections.keySet().stream().allMatch(
						es -> instance.submodelURIs.contains(
														es.exportingModelURI)),
					CoupledModelDescriptor.class,
					instance,
					"connections.keySet().stream().allMatch("
					+ "es -> submodelURIs.contains(es.exportingModelURI))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.connections.values().stream().allMatch(
						ess -> Stream.of(ess).allMatch(
								es -> instance.submodelURIs.contains(
														es.importingModelURI))),
					CoupledModelDescriptor.class,
					instance,
					"connections.values().stream().allMatch("
					+ "ess -> Stream.of(ess).allMatch(es -> submodelURIs."
					+ "contains(es.importingModelURI)))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.connections.entrySet().stream().allMatch(
						e -> Stream.of(e.getValue()).allMatch(
								es -> es.sourceEventType.isAssignableFrom(
												e.getKey().sinkEventType))),
					CoupledModelDescriptor.class,
					instance,
					"connections.entrySet().stream().allMatch("
					+ "e -> Stream.of(e.getValue()).allMatch("
					+ "es -> es.sourceEventType.isAssignableFrom("
					+ "e.getKey().sinkEventType)))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.mc != null,
					CoupledModelDescriptor.class,
					instance,
					"mc != null");
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
		CoupledModelDescriptor instance
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
	 * create a new coupled model creation descriptor.
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
	 * pre	{@code connections != null || connections.keySet().stream().allMatch(es -> submodelURIs.contains(es.exportingModelURI))}
	 * pre	{@code connections != null || connections.values().stream().allMatch(ess -> Stream.of(ess).allMatch(es -> submodelURIs.contains(es.importingModelURI)))}
	 * pre	{@code connections != null || connections.entrySet().stream().allMatch(e -> Stream.of(e.getValue()).allMatch(es -> es.sourceEventType.isAssignableFrom(e.getKey().sinkEventType)))}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass	class defining the coupled model.
	 * @param modelURI		URI of the coupled model to be created.
	 * @param submodelURIs	URIs of the submodels of the coupled model.
	 * @param imported		map from imported event types to the submodels importing them.
	 * @param reexported	map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections	connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory		coupled model factory allowing to create the coupled model or null if none.
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory, new ModelComposer());
	}

	/**
	 * create a new coupled model creation descriptor.
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
	 * pre	{@code mc != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelClass	class defining the coupled model.
	 * @param modelURI		URI of the coupled model to be created.
	 * @param submodelURIs	URIs of the submodels of the coupled model.
	 * @param imported		map from imported event types to the submodels importing them.
	 * @param reexported	map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections	connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory		coupled model factory allowing to create the coupled model or null if none.
	 * @param mc			model composer to be used.
	 */
	public				CoupledModelDescriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		ModelComposer mc
		)
	{
		super();

		assert	modelClass != null || cmFactory != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelClass != null"
											+ " || cmFactory != null");
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null");
		assert	submodelURIs != null && submodelURIs.size() > 1 :
				new NeoSim4JavaException("Precondition violation: "
											+ "submodelURIs != null && "
											+ "submodelURIs.size() > 1");
		assert	submodelURIs.stream().allMatch(t -> t != null && !t.isEmpty()) :
				new NeoSim4JavaException("Precondition violation: "
								+ "submodelURIs.stream()."
								+ "allMatch(t -> t != null && !t.isEmpty())");
		assert	imported == null || imported.values().stream().allMatch(
					ess -> Stream.of(ess).allMatch(
							es -> submodelURIs.contains(es.importingModelURI))) :
				new NeoSim4JavaException("Precondition violation: "
						+ "imported == null || imported.values().stream()."
						+ "allMatch(ess -> Stream.of(ess).allMatch(es -> "
						+ "submodelURIs.contains(es.importingModelURI)))");
		assert	reexported == null || reexported.values().stream().allMatch(
					re -> submodelURIs.contains(re.exportingModelURI)) :
				new NeoSim4JavaException("Precondition violation: "
						+ "reexported == null || reexported.values().stream()."
						+ "allMatch(re -> submodelURIs.contains("
						+ "re.exportingModelURI))");
		assert	connections == null || connections.keySet().stream().allMatch(
					es -> submodelURIs.contains(es.exportingModelURI)) :
				new NeoSim4JavaException("Precondition violation: "
						+ "connections == null"
						+ " || connections.keySet().stream().allMatch("
						+ "es -> submodelURIs.contains(es.exportingModelURI))");
		assert	connections == null || connections.values().stream().allMatch(
					ess -> Stream.of(ess).allMatch(
							es -> submodelURIs.contains(es.importingModelURI))) :
				new NeoSim4JavaException("Precondition violation: "
						+ "connections == null || connections.values().stream()"
						+ ".allMatch(ess -> Stream.of(ess).allMatch("
						+ "es -> submodelURIs.contains(es.importingModelURI)))");
		assert	connections == null || connections.entrySet().stream().allMatch(
					e -> Stream.of(e.getValue()).allMatch(
							es -> es.sourceEventType.isAssignableFrom(
												e.getKey().sinkEventType))) :
				new NeoSim4JavaException("Precondition violation: "
						+ "connections == null || connections.entrySet()."
						+ "stream().allMatch(e -> Stream.of(e.getValue())."
						+ "allMatch(es -> es.sourceEventType.isAssignableFrom("
						+ "e.getKey().sinkEventType)))");
		assert	mc != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "mc != null");

		this.modelClass = modelClass;
		this.modelURI = modelURI;
		this.submodelURIs = submodelURIs;
		if (imported != null) {
			this.imported = imported;
		} else {
			this.imported = new HashMap<Class<? extends EventI>,EventSink[]>();
		}
		if (reexported != null) {
			this.reexported = reexported;	
		} else {
			this.reexported =
				new HashMap<Class<? extends EventI>,ReexportedEvent>();
		}
		if (connections != null) {
			this.connections = connections;	
		} else {
			this.connections = new HashMap<EventSource,EventSink[]>();
		}
		if (cmFactory == null) {
			this.cmFactory = new StandardCoupledModelFactory(this.modelClass);
		} else {
			this.cmFactory = cmFactory;
		}
		this.mc = mc;

		// Invariants checking
		assert	CoupledModelDescriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"CoupledModelDescriptor.implementationInvariants(this)");
		assert	CoupledModelDescriptor.invariants(this) :
				new NeoSim4JavaException(
						"CoupledModelDescriptor.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * check both the consistency of the coupled model in the context of a
	 * complete architecture i.e., when information about the submodels are
	 * known.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * This method checks the following conditions:
	 * <ol>
	 * <li>
	 *   <p>Every submodel is defined either as atomic or as coupled model in
	 *      the architecture:</p>
	 *   <p>{@code submodelURIs.stream().allMatch(uri -> 
	 *                atomicModelDescriptors.containsKey(uri) || 
	 *                  coupledModelDescriptors.containsKey(uri))}</p>
	 * </li>
	 * <li>
	 *   <p>Every imported event correspond to an existing submodel consuming
	 *      it:</p>
	 *   <p>{@code imported.values().stream().allMatch(
	 *               sinks -> Stream.of(sinks).allMatch(
	 *                          sink -> CoupledModelDescriptor.
	 *                                     isSubmodelImportingEventType(
	 *                                          sink.importingModelURI,
	 *                                          sink.sinkEventType,
	 *                                          atomicModelDescriptors,
	 *                                          coupledModelDescriptors)))}</p>
	 * </li>
	 * <li>
	 *   <p>Every reexported event correspond to an existing submodel producing
	 *      it:</p>
	 *   <p>{@code reexported.values().stream().allMatch(
	 *               re -> CoupledModelDescriptor.
	 *                                  isSubmodelExportingEventType(
	 *                                          re.exportingModelURI,
	 *                                          re.sourceEventType,
	 *                                          atomicModelDescriptors,
	 *                                          coupledModelDescriptors))}</p>
	 * </li>
	 * <li>
	 *   <p>All connections are among existing submodels that export and import
	 *      the corresponding events:</p>
	 *   <p>{@code connections.entrySet().stream().allMatch(
	 *              e -> CoupledModelDescriptor.isSubmodelExportingEventType(
	 *                                            e.getKey().exportingModelURI,
	 *                                            e.getKey().sourceEventType,
	 *                                            atomicModelDescriptors,
	 *                                            coupledModelDescriptors)
	 *                   && Stream.of(e.getValue()).allMatch(
	 *                        es -> !es.importingModelURI.equals(
	 *                                           e.getKey().exportingModelURI)
	 *                              && CoupledModelDescriptor.
	 *                                   isSubmodelImportingEventType(
	 *                                       es.importingModelURI,
	 *                                       es.sinkEventType,
	 *                                       atomicModelDescriptors,
	 *                                       coupledModelDescriptors)))}</p>
	 * </li>
	 * </ol>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelDescriptors	descriptors of the atomic models including the ones that are submodels.
	 * @param coupledModelDescriptors	descriptors of the coupled models including the ones that are submodels.
	 * @return							true if the descriptor satisfies the consistency constraints.
	 */
	public boolean		checkArchitecturalConsistency(
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	atomicModelDescriptors != null;
		assert	coupledModelDescriptors != null;

		boolean ret = true;
		boolean previous = false;
		// Every submodel is defined either as atomic or as coupled model in the
		// architecture
		ret &= this.submodelURIs.stream().allMatch(
					uri -> atomicModelDescriptors.containsKey(uri) ||
									coupledModelDescriptors.containsKey(uri));
		if (!ret) {
			previous = true;
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some of the "
				+ "submodel does not exist.");
		}
		// All imported event sinks correspond to an existing submodel consuming
		// them
		ret &= this.imported.values().stream().allMatch(
					sinks -> Stream.of(sinks).allMatch(
							   sink ->  CoupledModelDescriptor.
							   				isSubmodelImportingEventType(
												sink.importingModelURI,
												sink.sinkEventType,
												atomicModelDescriptors,
												coupledModelDescriptors)));
		if (!ret && !previous) {
			previous = true;
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some imported"
				+ " event is not imported by an existing submodel or the"
				+ " types are not compatible.");
		}
		// Every reexported event corresponds to an existing submodel producing
		// it
		ret &= this.reexported.values().stream().allMatch(
					re -> CoupledModelDescriptor.
										isSubmodelExportingEventType(
												re.exportingModelURI,
												re.sourceEventType,
												atomicModelDescriptors,
												coupledModelDescriptors));
		if (!ret && !previous) {
			previous = true;
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some reexported"
				+ " event is not exported by an existing submodel or the"
				+ " types are not compatible.");
		}
		// All connections are among existing submodels that export and import
		// the corresponding events
		ret &= this.connections.entrySet().stream().allMatch(
					e -> CoupledModelDescriptor.isSubmodelExportingEventType(
												e.getKey().exportingModelURI,
												e.getKey().sourceEventType,
												atomicModelDescriptors,
												coupledModelDescriptors)
						&& Stream.of(e.getValue()).allMatch(
								es -> !es.importingModelURI.
										equals(e.getKey().exportingModelURI) &&
									  CoupledModelDescriptor.
									  	isSubmodelImportingEventType(
												es.importingModelURI,
												es.sinkEventType,
												atomicModelDescriptors,
												coupledModelDescriptors)));
		if (!ret && !previous) {
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some connection"
				+ " has a source event not exported by an existing submodel"
				+ " or some sink event not imported by an existing submodel"
				+ " or the types are not compatible.");
		}
		return ret;
	}

	/**
	 * return true if the given submodel imports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code submodelURI != null && !submodelURI.isEmpty()}
	 * pre	{@code et != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(submodelURI) || coupledModelDescriptors.containsKey(submodelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel imports the given event type.
	 */
	protected static boolean	isSubmodelImportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null && !submodelURI.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
						+ "submodelURI != null && !submodelURI.isEmpty()");
		assert	et != null:
				new NeoSim4JavaException("Precondition violation: "
						+ "et != null");
		assert	atomicModelDescriptors != null:
				new NeoSim4JavaException("Precondition violation: "
										+ "atomicModelDescriptors != null");
		assert	coupledModelDescriptors != null:
				new NeoSim4JavaException("Precondition violation: "
										+ "coupledModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
					coupledModelDescriptors.containsKey(submodelURI):
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors.containsKey(submodelURI) || "
						+ "coupledModelDescriptors.containsKey(submodelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] importedEvents =
					atomicModelDescriptors.get(submodelURI).importedEvents;
			for (int i = 0 ; !ret && i < importedEvents.length ; i++) {
				ret = et.equals(importedEvents[i]);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI);
			ret = coupledModelDescriptors.get(submodelURI).
											  imported.keySet().contains(et);
		}
		return ret;
	}

	/**
	 * return true if the given submodel exports the given event type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code submodelURI != null && !submodelURI.isEmpty()}
	 * pre	{@code et != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(submodelURI) || coupledModelDescriptors.containsKey(submodelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param submodelURI				URI of the submodel to be checked.
	 * @param et						event type to be checked.
	 * @param atomicModelDescriptors	atomic model descriptors.
	 * @param coupledModelDescriptors	coupled model descriptors.
	 * @return							true if the given submodel exports the given event type.
	 */
	protected static boolean	isSubmodelExportingEventType(
		String submodelURI,
		Class<? extends EventI> et,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	submodelURI != null && !submodelURI.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
							+ "submodelURI != null && !submodelURI.isEmpty()");
		assert	et != null:
				new NeoSim4JavaException("Precondition violation: "
						+ "et != null");
		assert	atomicModelDescriptors != null:
				new NeoSim4JavaException("Precondition violation: "
							+ "atomicModelDescriptors != null");
		assert	coupledModelDescriptors != null:
				new NeoSim4JavaException("Precondition violation: "
							+ "coupledModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(submodelURI) ||
						coupledModelDescriptors.containsKey(submodelURI):
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors.containsKey(submodelURI) || "
						+ "coupledModelDescriptors.containsKey(submodelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(submodelURI)) {
			Class<? extends EventI>[] exportedEvents =
					atomicModelDescriptors.get(submodelURI).exportedEvents;
			for (int i = 0 ; !ret && i < exportedEvents.length ; i++) {
				ret = et.equals(exportedEvents[i]);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(submodelURI);
			for (ReexportedEvent re : coupledModelDescriptors.get(submodelURI).
													reexported.values()) {
				ret = et.equals(re.sinkEventType);
				if (ret){
					break;
				}
			}
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#getModelURI()
	 */
	@Override
	public String		getModelURI()
	{
		return this.modelURI;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI#isCoupledModelDescriptor()
	 */
	@Override
	public boolean		isCoupledModelDescriptor()
	{
		return true;
	}

	/**
	 * return true if all predefined submodels are defined in {@code models}
	 * and no more than them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param models	array of submodels to be tested.
	 * @return			true if all predefined submodels are defined in {@code models} and no more than them.
	 */
	public boolean		definesExpectedSubmodels(ModelI[] models)
	{
		assert	models != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "models != null");
		boolean ret = true;
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0 ; ret && i < models.length ; i++) {
			ret = this.submodelURIs.contains(models[i].getURI());
			hs.add(models[i].getURI());
		}
		if (ret) {
			for (String uri : this.submodelURIs) {
				ret = hs.contains(uri);
				if (!ret) {
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * create a coupled model from this descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	{@code }
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param models	model descriptions for the submodels of this coupled model.
	 * @return			the new coupled model as a model description.
	 */
	public CoupledModelI	createCoupledModel(ModelI[] models)
	{
		assert	models != null && models.length > 1 :
				new NeoSim4JavaException("Precondition violation: "
									+ "models != null && models.length > 1");
		assert	this.definesExpectedSubmodels(models) :
				new NeoSim4JavaException(
					"Precondition violation: definesExpectedSubmodels(models)");

		return this.mc.compose(models,
							   this.modelURI,
							   cmFactory,
							   this.imported,
							   this.reexported,
							   this.connections);
	}
}
// -----------------------------------------------------------------------------
