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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableVisibility;
import fr.sorbonne_u.devs_simulation.models.StandardCoupledModelFactory;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
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
 * The class <code>CoupledHIOA_Descriptor</code> describes coupled
 * HIOA in model architectures, associating their URI, their static
 * information, as well as a factory that can be used to instantiate
 * a coupled HIOA object to run simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * // for every imported variables v, v is not a re-exported variable of the
 * // current model and for all sink variable s to which v is connected, s
 * // identifies a submode of the current model and the type of s is compatible
 * // with the type of v
 * invariant	{@code importedVars.keySet().stream().allMatch(vd -> !reexportedVars.values().contains(vd) && Stream.of(importedVars.get(vd)).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(vd.getType())))}
 * // for every re-exported variable v, the model re-exporting v is a submodel
 * // of the current model, v is not an imported variable of the current model
 * // and the type of the variable re-exported by the submodel is compatible with the type of v
 * invariant	{@code reexportedVars.keySet().stream().allMatch(vs -> submodelURIs.contains(vs.exportingModelURI) && !importedVars.keySet().contains(reexportedVars.get(vs)) && reexportedVars.get(vs).getType().isAssignableFrom(vs.type))}
 * // for every bindings between a source variable v and a set of sink variables
 * // ss, v is exported by a submodel of the current model, all of the sink
 * // variables s in ss are imported by submodels of the current model and the
 * // type of s is compatible with the type of v
 * invariant	{@code bindings.entrySet().stream().allMatch(e -> submodelURIs.contains(e.getKey().exportingModelURI) && Stream.of(e.getValue()).allMatch(sink -> submodelURIs.contains(sink.sinkModelURI) && sink.importedVariableType.isAssignableFrom(e.getKey().type)))}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant.
 * </pre>
 * 
 * <p>Created on : 2018-07-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoupledHIOA_Descriptor
extends		CoupledModelDescriptor
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** Map from variables imported by this coupled HIOA and the submodels
	 *  consuming them.														*/
	public final Map<StaticVariableDescriptor,VariableSink[]>	importedVars;
	/** Map from variables exported by this coupled HIOA and the submodels
	 *  producing them.														*/
	public final Map<VariableSource,StaticVariableDescriptor>	reexportedVars;
	/** 	Map between variables exported by submodels to ones imported by
	 *  others.																*/
	public final Map<VariableSource,VariableSink[]>				bindings;

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
		CoupledHIOA_Descriptor instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		// for every imported variables v, v is not a re-exported variable of
		// the current model and for all sink variable s to which v is connected,
		// s identifies a submode of the current model and the type of s is
		// compatible with the type of v
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.importedVars.keySet().stream().allMatch(
					vd -> !instance.reexportedVars.values().contains(vd) &&
						  Stream.of(instance.importedVars.get(vd)).allMatch(
								  sink -> instance.submodelURIs.contains(
								  						sink.sinkModelURI) &&
						  		  sink.importedVariableType.
						  		  			isAssignableFrom(vd.getType()))),
					CoupledHIOA_Descriptor.class,
					instance,
					"importedVars.keySet().stream().allMatch("
					+ "vd -> !reexportedVars.values().contains(vd) && "
					+ "Stream.of(importedVars.get(vd)).allMatch("
					+ "sink -> submodelURIs.contains(sink.sinkModelURI) && "
					+ "sink.importedVariableType.isAssignableFrom(vd.getType())))");
		// for every re-exported variable v, the model re-exporting v is a
		// submodel of the current model, v is not an imported variable of the
		// current model and the type of the variable re-exported by the submodel
		// is compatible with the type of v
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.reexportedVars.keySet().stream().allMatch(
					vs -> instance.submodelURIs.contains(vs.exportingModelURI) &&
						  !instance.importedVars.keySet().contains(
							  			instance.reexportedVars.get(vs)) &&
						  instance.reexportedVars.get(vs).getType().
					  					isAssignableFrom(vs.type)),
					CoupledHIOA_Descriptor.class,
					instance,
					"reexportedVars.keySet().stream().allMatch("
					+ "vs -> submodelURIs.contains(vs.exportingModelURI) && "
					+ "!importedVars.keySet().contains(reexportedVars.get(vs)) && "
					+ "reexportedVars.get(vs).getType().isAssignableFrom(vs.type))");
		// for every bindings between a source variable v and a set of sink
		// variables ss, v is exported by a submodel of the current model, all
		// of the sink variables s in ss are imported by submodels of the
		// current model and the type of s is compatible with the type of v
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.bindings.entrySet().stream().allMatch(
					e -> instance.submodelURIs.contains(
											e.getKey().exportingModelURI) &&
						 Stream.of(e.getValue()).allMatch(
							sink -> instance.submodelURIs.contains(
														sink.sinkModelURI) &&
									sink.importedVariableType.
											isAssignableFrom(e.getKey().type))),
				CoupledHIOA_Descriptor.class,
				instance,
				"bindings.entrySet().stream().allMatch("
				+ "e -> submodelURIs.contains(e.getKey().exportingModelURI) && "
				+ "Stream.of(e.getValue()).allMatch("
				+ "sink -> submodelURIs.contains(sink.sinkModelURI) && "
				+ "sink.importedVariableType.isAssignableFrom(e.getKey().type)))");
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
		CoupledHIOA_Descriptor instance
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
	 * create a new coupled HIOA creation descriptor.
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
	 * @param modelClass		class defining the coupled model.
	 * @param modelURI			URI of the coupled model to be created.
	 * @param submodelURIs		URIs of the submodels of the coupled model.
	 * @param imported			map from imported event types to the submodels importing them.
	 * @param reexported		map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections		connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory			coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars		map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars	map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings			map between variables exported by submodels to ones imported by others.
	 */
	public				CoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings
		)
	{
		this(modelClass, modelURI, submodelURIs, imported, reexported,
			 connections, cmFactory, importedVars,
			 reexportedVars, bindings, new HIOA_Composer());
	}

	/**
	 * create a new coupled HIOA creation descriptor.
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
	 * @param modelClass		class defining the coupled model.
	 * @param modelURI			URI of the coupled model to be created.
	 * @param submodelURIs		URIs of the submodels of the coupled model.
	 * @param imported			map from imported event types to the submodels importing them.
	 * @param reexported		map from event types exported by submodels to exported event types exported by the coupled model.
	 * @param connections		connections between event types exported by submodels to event types imported by other submodels.
	 * @param cmFactory			coupled HIOA factory allowing to create the coupled model or null if none.
	 * @param importedVars		map from variables imported by this coupled HIOA and the submodels consuming them.
	 * @param reexportedVars	map from variables exported by this coupled HIOA and the submodels producing them.
	 * @param bindings			map between variables exported by submodels to ones imported by others.
	 * @param hioa_mc			model composer to be used.
	 */
	public				CoupledHIOA_Descriptor(
		Class<? extends CoupledModelI> modelClass,
		String modelURI,
		Set<String> submodelURIs,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		CoupledModelFactoryI cmFactory,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings,
		HIOA_Composer hioa_mc
		)
	{
		super(modelClass, modelURI, submodelURIs,
			  imported, reexported, connections,
			  cmFactory, hioa_mc);

		if (importedVars != null) {
			this.importedVars = importedVars;
		} else {
			this.importedVars =
					new HashMap<StaticVariableDescriptor,VariableSink[]>();
		}
		if (reexportedVars != null) {
			this.reexportedVars = reexportedVars;
		} else {
			this.reexportedVars =
					new HashMap<VariableSource,StaticVariableDescriptor>();
		}
		if (bindings != null) {
			this.bindings = bindings;
		} else {
			this.bindings = new HashMap<VariableSource,VariableSink[]>();
		}

		assert	CoupledHIOA_Descriptor.implementationInvariants(this) :
				new NeoSim4JavaException(
						"CoupledHIOA_Descriptor.implementationInvariants(this)");
		assert	CoupledHIOA_Descriptor.invariants(this) :
				new NeoSim4JavaException(
						"CoupledHIOA_Descriptor.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * check both the architectural consistency of the coupled HIOA in the
	 * context of a complete architecture  i.e., when the information about
	 * submodels are known.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * This method implementation checks the following conditions on the
	 * variables bindings point of view:
	 * </p>
	 * <ol>
	 * <li>
	 *   <p>Every imported variable of the coupled model is connected to
	 *      existing submodels with compatible types:</p>
	 *   <p>{@code importedVars.entrySet().stream().allMatch(
	 *              e -> Stream.of(e.getValue()).allMatch(
	 *                    source ->
	 *                      submodelURIs.contains(source.sinkModelURI) &&
	 *                      source.importedVariableType.isAssignableFrom(
	 *                                               e.getKey().getType()) &&
	 *                      CoupledHIOA_Descriptor.isImportingVar(
	 *                                         source.sinkModelURI,
	 *                                         new StaticVariableDescriptor(
	 *                                             source.sinkVariableName,
	 *                                             source.sinkVariableType,
	 *                                             VariableVisibility.IMPORTED),
	 *                                         atomicModelDescriptors,
	 *                                         coupledModelDescriptors)))}</p>
	 * </li>
	 * <li>
	 *   <p>Every variable reexported by this coupled model is exported by an
	 *      existing submodel:</p>
	 *   <p>{@code reexportedVars.keySet().stream().allMatch(
	 *              source -> CoupledHIOA_Descriptor.isExportingVar(
	 *                                    source.exportingModelURI,
	 *                                    new StaticVariableDescriptor(
	 *                                         source.name,
	 *                                         source.type,
	 *                                         VariableVisibility.EXPORTED),
	 *                                         atomicModelDescriptors,
	 *                                         coupledModelDescriptors))}</p>
	 * </li>
	 * <li>
	 *   <p>Every binding have a source variable exported by a submodel and
	 *      sink variables imported by submodels and their types are
	 *      compatible:</p>
	 *   <p>{@code bindings.keySet().stream().allMatch(
	 *              source -> CoupledHIOA_Descriptor.isExportingVar(
	 *                                           source.exportingModelURI,
	 *                                           new StaticVariableDescriptor(
	 *                                                       source.name,
	 *                                                       source.type,
	 *                                           VariableVisibility.EXPORTED),
	 *                                           atomicModelDescriptors,
	 *                                           coupledModelDescriptors) &&
	 *                        Stream.of(this.bindings.get(source)).allMatch(
	 *                         sink -> CoupledHIOA_Descriptor.isImportingVar(
	 *                                           sink.sinkModelURI,
	 *                                           new StaticVariableDescriptor(
	 *                                                   sink.sinkVariableName,
	 *                                                   sink.sinkVariableType,
	 *                                           VariableVisibility.IMPORTED),
	 *                                           atomicModelDescriptors,
	 *                                           coupledModelDescriptors)))}</p>
	 * </li>
	 * </ol>
	 * <p>
	 * The overridden method, called inside this method, checks the architectural
	 * consistency from the point of view of events exchanges.
	 * </p>
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
		assert	atomicModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
										+ "atomicModelDescriptors != null");
		assert	coupledModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
										+ "coupledModelDescriptors != null");

		super.checkArchitecturalConsistency(atomicModelDescriptors,
											coupledModelDescriptors);

		boolean ret = true;
		boolean previous = false;
		// Every imported variable of the coupled model is connected to
		// existing submodels with compatible types
		ret &= this.importedVars.entrySet().stream().allMatch(
					e -> Stream.of(e.getValue()).allMatch(
						source -> this.submodelURIs.contains(
														source.sinkModelURI) &&
								  source.importedVariableType.isAssignableFrom(
										  			e.getKey().getType()) &&
								  CoupledHIOA_Descriptor.isImportingVar(
										source.sinkModelURI,
										new StaticVariableDescriptor(
												source.sinkVariableName,
												source.sinkVariableType,
												VariableVisibility.IMPORTED),
										atomicModelDescriptors,
										coupledModelDescriptors)));
		if (!ret) {
			previous = true;
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some imported"
				+ " variable is not imported by a submodel or the types are"
				+ " not compatible.");
		}
		// Every variable reexported by this coupled model is exported by an
		// existing submodel
		ret &= this.reexportedVars.keySet().stream().allMatch(
					source -> CoupledHIOA_Descriptor.isExportingVar(
							source.exportingModelURI,
							new StaticVariableDescriptor(
										source.name,
										source.type,
										VariableVisibility.EXPORTED),
							atomicModelDescriptors,
							coupledModelDescriptors));
		if (!ret && !previous) {
			previous = true;
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some reexported"
				+ " variable is not exported by an existing submodel or the"
				+ " types are not compatible.");
		}
		// Every binding have a source variable exported by a submodel and
		// sink variables imported by submodels and their types are compatible
		ret &= this.bindings.keySet().stream().allMatch(
					source -> CoupledHIOA_Descriptor.isExportingVar(
									source.exportingModelURI,
									new StaticVariableDescriptor(
												source.name,
												source.type,
												VariableVisibility.EXPORTED),
									atomicModelDescriptors,
									coupledModelDescriptors) &&
							  Stream.of(this.bindings.get(source)).
							  	allMatch(
							  		sink -> CoupledHIOA_Descriptor.isImportingVar(
							  					sink.sinkModelURI,
							  					new StaticVariableDescriptor(
							  							sink.sinkVariableName,
							  							sink.sinkVariableType,
							  					VariableVisibility.IMPORTED),
							  					atomicModelDescriptors,
							  					coupledModelDescriptors)));	
		if (!ret && !previous) {
			System.out.println(
				"In the descriptor of " + this.modelURI + ", some binding"
				+ " has a source variable not exported by an existing submodel"
				+ " or some sink variable not imported by an existing submodel"
				+ " or the types are not compatible.");
		}
		return ret;
	}

	/**
	 * return true if the model is importing the variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code vd != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(modelURI) || coupledModelDescriptors != null && coupledModelDescriptors.containsKey(modelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI					URI of the model to be tested.
	 * @param vd						variable descriptor to be tested.
	 * @param atomicModelDescriptors	the set of atomic model descriptors.
	 * @param coupledModelDescriptors	the set of coupled model descriptors.
	 * @return							true if the model is importing the variable.
	 */
	protected static boolean	isImportingVar(
		String modelURI,
		StaticVariableDescriptor vd,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "modelURI != null && !modelURI.isEmpty()");
		assert	vd != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "vd != null");
		assert	atomicModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "atomicModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(modelURI) ||
					coupledModelDescriptors != null
							&& coupledModelDescriptors.containsKey(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
							+ "atomicModelDescriptors.containsKey(modelURI) || "
							+ "coupledModelDescriptors != null && "
							+ "coupledModelDescriptors.containsKey(modelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(modelURI)) {
			StaticVariableDescriptor[] importedVariables =
				((AtomicHIOA_Descriptor)atomicModelDescriptors.
										get(modelURI)).importedVariables;
			for (int i = 0 ; !ret && i < importedVariables.length ; i++) {
				ret = importedVariables[i].equals(vd);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(modelURI);
			ret = ((CoupledHIOA_Descriptor)coupledModelDescriptors.
							get(modelURI)).importedVars.containsKey(vd);
		}
		return ret;
	}

	/**
	 * return true if the model is exporting the variable.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code vd != null}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(modelURI) || coupledModelDescriptors != null && coupledModelDescriptors.containsKey(modelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI					URI of the model to be tested.
	 * @param vd						variable descriptor to be tested.
	 * @param atomicModelDescriptors	the set of atomic model descriptors.
	 * @param coupledModelDescriptors	the set of coupled model descriptors.
	 * @return							true if the model is exporting the variable.
	 */
	protected static boolean	isExportingVar(
		String modelURI,
		StaticVariableDescriptor vd,
		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String,CoupledModelDescriptor> coupledModelDescriptors
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	vd != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "vd != null");
		assert	atomicModelDescriptors != null :
				new NeoSim4JavaException("Precondition violation: "
								+ "atomicModelDescriptors != null");
		assert	atomicModelDescriptors.containsKey(modelURI) ||
					coupledModelDescriptors != null
							&& coupledModelDescriptors.containsKey(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
							+ "atomicModelDescriptors.containsKey(modelURI) || "
							+ "coupledModelDescriptors != null && "
							+ "coupledModelDescriptors.containsKey(modelURI)");

		boolean ret = false;
		if (atomicModelDescriptors.containsKey(modelURI)) {
			StaticVariableDescriptor[] exportedVariables =
					((AtomicHIOA_Descriptor)atomicModelDescriptors.
											get(modelURI)).exportedVariables;
			for (int i = 0 ; !ret && i < exportedVariables.length ; i++) {
				ret = exportedVariables[i].equals(vd);
			}
		} else {
			assert	coupledModelDescriptors.containsKey(modelURI);
			ret = ((CoupledHIOA_Descriptor)coupledModelDescriptors.
					get(modelURI)).reexportedVars.values().contains(vd);
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public CoupledModelI	createCoupledModel(
		ModelI[] models
		)
	{
		assert	this.mc instanceof HIOA_Composer;

		assert	models != null && models.length > 1 :
				new NeoSim4JavaException("Precondition violation: "
									+ "models != null && models.length > 1");

		HashSet<String> hs = new HashSet<String>();
		for (int i = 0 ; i < models.length ; i++) {
			assert	this.submodelURIs.contains(models[i].getURI()) :
					new RuntimeException("createCoupledModel for " +
											this.modelURI+ ": " +
											models[i].getURI() +
											" is not a submodel!");
			hs.add(models[i].getURI());
		}
		for (String uri : this.submodelURIs) {
			assert	hs.contains(uri) :
					new RuntimeException("createCoupledModel for " +
								this.modelURI+ ": " + uri +
								" is a submodel URI but no model provided!");
		}
		hs = null;

		CoupledModelI hioa = null;
		CoupledModelFactoryI cmFactory = null;
		if (this.cmFactory == null ) {
			cmFactory =
				new StandardCoupledModelFactory(
						(Class<? extends CoupledModelI>) this.modelClass);
		} else {
			cmFactory = this.cmFactory;
		}
		hioa = ((HIOA_Composer)this.mc).compose(
											models,
											this.modelURI,
											cmFactory,
											this.imported,
											this.reexported,
											this.connections,
											this.importedVars,
											this.reexportedVars,
											this.bindings);
		return hioa;
	}
}
// -----------------------------------------------------------------------------
