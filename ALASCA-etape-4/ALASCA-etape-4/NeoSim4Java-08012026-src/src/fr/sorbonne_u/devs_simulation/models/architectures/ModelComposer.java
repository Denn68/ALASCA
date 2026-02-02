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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventConverterI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>ModelComposer</code> provides methods implementing model
 * composition; it is abstract as it only defines static methods.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Composing standard DEVS models requires to connect models exporting events
 * to other models importing them. A coupled model importing some event must
 * also know to which of its submodels the event must be propagated, and when
 * it exports some event, it must also know which one of its submodels is
 * producing and exporting the events. As coupled models can have other
 * coupled models as submodels, an imported event by a coupled model can be
 * consumed by an atomic model several levels below in the hierarchy, while
 * an exported event can be produced by an atomic model also several levels
 * below in the hierarchy.
 * </p>
 * <p>
 * In this implementation of DEVS, atomic models producing and exporting some
 * events propagate them directly to the atomic models importing and consuming
 * them. Therefore, when composing models, the composition gets the actual
 * reference of the consuming models to connect them directly into the
 * producing model.
 * </p>
 * <p>
 * When a coupled model imports some type of events, it may propagate them
 * to submodels importing different types of events, and then use a conversion
 * function at run time to convert events it receives into events that its
 * submodels can consume. Similarly, when re-exporting events, the type of
 * events exported by the coupled model may be different from the ones
 * exported by its submodels and then a conversion must also be applied. Hence,
 * when connecting two models, a series of conversion may be required to
 * transform the produced event into an event that the receiver can consume.
 * Each point-to-point connection between a producer and a consumer model
 * therefore has to store the composition of the conversion functions that
 * must be applied each tie an event is propagated from the producer to the
 * consumer.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-05-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ModelComposer
implements	Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * set the parent reference on each of the submodels.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code parent != null}
	 * pre	{@code submodels != null && submodels.length > 0}
	 * pre	{@code Stream.of(submodels).allMatch(s -> s != null)}
	 * post	{@code Stream.of(submodels).allMatch(s -> s.isParentSet())}
	 * </pre>
	 *
	 * @param parent		reference on the parent model.
	 * @param submodels		array of references on the submodels.
	 */
	protected void		setParentModel(
		CoupledModelI parent,
		ModelI[] submodels
		)
	{
		
		assert	parent != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "parent != null");
		assert	submodels != null && submodels.length > 0 :
				new NeoSim4JavaException("Precondition violation: "
							+ "submodels != null && submodels.length > 0");
		assert	Stream.of(submodels).allMatch(s -> s != null) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Stream.of(submodels).allMatch(s -> s != null)");


		for (int i = 0 ; i < submodels.length ; i++) {
			submodels[i].setParent(parent);
		}

		assert	Stream.of(submodels).allMatch(s -> s.isParentSet()) :
				new NeoSim4JavaException(
						"Postcondition violation: "
						+ "Stream.of(submodels).allMatch(s -> s.isParentSet())");
	}

	/**
	 * set the parent simulation engines for all members of {@code subEngines}
	 * to a reference to {@code c}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * pre	{@code subEngines != null && subEngines.length > 0}
	 * pre	{@code Stream.of(subEngines).allMatch(e -> e != null)}
	 * post	{@code Stream.of(subEngines).allMatch(e -> e.isParentSet())}	// no postcondition.
	 * </pre>
	 *
	 * @param c				coordinator engine that must become the parent of all members in {@code subEngines}.
	 * @param subEngines	simulation engines which parent must be set.
	 */
	protected void		setParentSimulationEngine(
		CoordinatorI c,
		SimulatorI[] subEngines
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");
		assert	subEngines != null && subEngines.length > 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "subEngines != null && subEngines.length > 0");
		assert	Stream.of(subEngines).allMatch(e -> e != null) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Stream.of(subEngines).allMatch(e -> e != null)");

		for (int i = 0 ; i < subEngines.length ; i++) {
			subEngines[i].setParent(c);
		}

		assert	Stream.of(subEngines).allMatch(e -> e.isParentSet()) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "Stream.of(subEngines).allMatch(e -> e.isParentSet())");
	}

	/**
	 * check the consistency of the information provided for compositions.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param models		submodels to be composed.
	 * @param imported		imported events by the new coupled model.
	 * @param reexported	reexported events by the new coupled model.
	 * @param connections	connections among event emitters and consumers among the children models.
	 * @return				true if the information is consistent.
	 */
	public static boolean		checkConsistency(
		ModelI[] models,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections
		)
	{
		boolean ret = true;

		Map<String,ModelI> uris2models = new HashMap<>();
		for (int i = 0 ; i < models.length ; i++) {
			uris2models.put(models[i].getURI(), models[i]);
		}

		// Assert that all imported event sinks correspond to an
		// existing submodel consuming them.
		for (EventSink[] es : imported.values()) {
			for (int i = 0 ; i < es.length ; i++) {
				ModelI m = uris2models.get(es[i].importingModelURI);
				ret &= m != null;
				ret &= m.isImportedEventType(es[i].sinkEventType);
			}
		}
		// Assert that all reexported event correspond to one and only one
		// existing submodel producing them.
		for (ReexportedEvent re : reexported.values()) {
			int found = 0;
			for (int i = 0 ; i < models.length ; i++) {
				if (models[i].isExportedEventType(re.sourceEventType)) {
					found++;
				}
			}
			ret &= (found == 1);
		}
		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey();
			ModelI m = uris2models.get(es.exportingModelURI);
			ret &= m != null;
			ret &= m.isExportedEventType(es.sourceEventType);
			EventSink[] sinks = e.getValue();
			for (int i = 0 ; i < sinks.length ; i++) {
				ret &= !sinks[i].importingModelURI.
											equals(es.exportingModelURI);
				ModelI sink =
							uris2models.get(sinks[i].importingModelURI);
				ret &= sink != null;
				ret &= sink.isImportedEventType(sinks[i].sinkEventType);
			}
		}

		return ret;
	}

	/**
	 * Set the influencees in the submodels i.e., the models that imports
	 * their exported events given the prescribed connections.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code models != null && models.length > 1}
	 * pre	{@code Stream.of(models).allMatch(m -> m != null)}
	 * pre	{@code connections != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI 		currently composed model URI.
	 * @param models		submodels of the coupled model being created.
	 * @param connections	connections of exported events to imported ones among the submodels.
	 */
	protected void		setInfluencees(
		String modelURI,
		ModelI[] models,
		Map<EventSource,EventSink[]> connections
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	models != null && models.length > 1:
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "models != null && models.length > 1");
		assert	connections != null:
				new NeoSim4JavaException("Precondition violation: "
						+ "connections != null");
		assert	Stream.of(models).allMatch(m -> m != null) :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "Stream.of(models).allMatch(m -> m != null)");

		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey();
			ModelI sourceGateway =
					this.findGatewayTo(modelURI, es.exportingModelURI, models);
			assert	sourceGateway != null;
			EventAtomicSource atomicSource =
					sourceGateway.getEventAtomicSource(es.sourceEventType);
			Set<CallableEventAtomicSink> hs =
									new HashSet<CallableEventAtomicSink>();
			EventSink[] directSinks = e.getValue();
			for (int i = 0 ; i < directSinks.length ; i++) {
				ModelI sinkGateway = 
						this.findGatewayTo(modelURI,
										   directSinks[i].importingModelURI,
										   models);
				assert	sinkGateway != null;
				Set<CallableEventAtomicSink> eventAtomicSinks =
						sinkGateway.getEventAtomicSinks(
											directSinks[i].sinkEventType);
				for (CallableEventAtomicSink eas : eventAtomicSinks) {
					CallableEventAtomicSink completeAtomicSink =
						new CallableEventAtomicSink(
								eas.importingModelURI,
								atomicSource.sourceEventType,
								eas.sinkEventType,
								eas.sink,
								EventConverterI.compose(
										eas.converter,
										EventConverterI.compose(
												directSinks[i].converter,
												atomicSource.converter)));
					
					hs.add(completeAtomicSink);
				}
			}
			sourceGateway.addInfluencees(atomicSource.exportingModelURI,
										 atomicSource.sourceEventType,
										 hs);
		}
	}

	/**
	 * return the reference to a model in {@code models} which URI is
	 * {@code uri} or that is the ancestor of the model with {@code uri}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI 		currently composed model URI.
	 * @param uri		URI of a model.
	 * @param models	array of models into which to find.
	 * @return			a model in {@code models} through which the model with {@code uri} can be reached.
	 */
	protected ModelI		findGatewayTo(
		String modelURI,
		String uri,
		ModelI[] models
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	uri != null && !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "uri != null && !uri.isEmpty()");
		ModelI gateway = null;

		for (int i = 0 ; gateway == null && i < models.length ; i++) {
			if (models[i].hasURI(uri)) {
				gateway = models[i];
			} else if (!models[i].isAtomic() &&
								((CoupledModelI)models[i]).isDescendant(uri)) {
				gateway = models[i];
			}
		}
		assert	gateway != null :
				new NeoSim4JavaException(uri + " is not a submodel of " + modelURI);

		return gateway;
	}

	/**
	 * return the model in the array <code>models</code> that is exporting
	 * the event type <code>ce</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code ce != null}
	 * pre	{@code modelDescriptions != null}
	 * pre	{@code Stream.of(modelDescriptions).allMatch(md -> md != null)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI 		currently composed model URI.
	 * @param ce			an event type.
	 * @param models		an array of models.
	 * @return				the model in the array <code>models</code> that is exporting the event type <code>ce</code>.
	 */
	protected static ModelI	findExportingModel(
		String modelURI,
		Class<? extends EventI> ce,
		ModelI[] models
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	ce != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null");
		assert	models != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "models != null");
		assert	Stream.of(models).allMatch(md -> md != null):
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "Stream.of(modelDescriptions)."
						+ "allMatch(md -> md != null)");

		ModelI ret = null;
		for (int i = 0 ; ret == null && i < models.length ; i++) {
			if (models[i].isExportedEventType(ce)) {
				ret = models[i];
			}
		}
		assert	ret != null :
				new NeoSim4JavaException(ce.getSimpleName()
								   + " is not exported by any submodel of "
								   + modelURI);
		return ret;
	}

	/**
	 * compose the models and return the resulting coupled model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	{@code Stream.of(models).allMatch(m -> m != null)}
	 * pre	{@code newModelURI != null && !newModelURI.isEmpty()}
	 * pre	{@code connections != null}
	 * post	{@code ret != null}
	 * post {@code Stream.of(models).allMatch(m -> m.isParentSet())}
	 * post {@code Stream.of(models).allMatch(m -> m.getSimulationEngine().isParentSet())}
	 * </pre>
	 *
	 * @param models			models to be composed.
	 * @param newModelURI		URI of the resulting coupled model.
	 * @param cmFactory			factory creating the right type of coupled model.
	 * @param imported			imported events and their conversion to submodels imported ones.
	 * @param reexported		exported events from submodels and their conversion to exported ones.
	 * @param connections		connections between exported and imported events among submodels.
	 * @return					coupled model resulting from the composition.
	 */
	public CoupledModelI	compose(
		ModelI[] models,
		String newModelURI,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections
		)
	{
		assert	models != null && models.length > 1:
				new NeoSim4JavaException("Precondition violation: "
								+ "models != null && "
								+ "models.length > 1");
		assert	Stream.of(models).allMatch(md -> md != null):
				new NeoSim4JavaException("Precondition violation: "
								+ "Stream.of(models)."
								+ "allMatch(md -> md != null)");
		assert	newModelURI != null && !newModelURI.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
								+ "newModelURI != null && "
								+ "!newModelURI.isEmpty()");
		assert	cmFactory != null:
				new NeoSim4JavaException("Precondition violation: "
								+ "cmFactory != null");
		assert	connections != null:
				new NeoSim4JavaException("Precondition violation: "
								+ "connections != null");

		Map<String,ModelI> uris2models = new HashMap<>();
		for (int i = 0 ; i < models.length ; i++) {
			uris2models.put(models[i].getURI(), models[i]);
		}

		// Assert that all imported event sinks correspond to an existing
		// submodel consuming them.
		for (EventSink[] es : imported.values()) {
			for (int i = 0 ; i < es.length ; i++) {
				ModelI submodel = uris2models.get(es[i].importingModelURI);
				assert	submodel != null:
						new RuntimeException("composing " + newModelURI
								+ ": sink model " + es[i].importingModelURI
								+ " does not exist!");
				assert	submodel.isImportedEventType(es[i].sinkEventType):
						new RuntimeException("composing " + newModelURI
								+ ": sink model " + es[i].importingModelURI
								+ " does not import event type "
								+ es[i].sinkEventType + "!");
			}
		}

		// Assert that all reexported event correspond to at least one
		// existing submodel producing them.
		for (ReexportedEvent re : reexported.values()) {
			boolean found = false;
			for (int i = 0 ; !found && i < models.length ; i++) {
				if (models[i].isExportedEventType(re.sourceEventType)) {
					found = true;
				} 
			}
			assert	found :
					new RuntimeException("composing " + newModelURI
							+ ": no exporting model or more than one found for"
							+ " reexported event type " + re.sourceEventType);
		}

		// Assert that all connections are among existing submodels that
		// export and imports the corresponding events.
		for (Entry<EventSource,EventSink[]> e : connections.entrySet()) {
			EventSource es = e.getKey();
			ModelI submodel = uris2models.get(es.exportingModelURI);
			assert	submodel != null :
					new RuntimeException("composing " + newModelURI
							+ ": source model " + es.exportingModelURI
							+ " does not exist!");
			assert	submodel.isExportedEventType(es.sourceEventType) :
					new RuntimeException("composing " + newModelURI
							+ ": source model " + es.exportingModelURI
							+ " does not export event type "
							+ es.sourceEventType + "!");
			EventSink[] sinks = e.getValue();
			for (int i = 0 ; i < sinks.length ; i++) {
				assert	!sinks[i].importingModelURI.
											equals(es.exportingModelURI);
				ModelI sink = uris2models.get(sinks[i].importingModelURI);
				assert	sink != null;
				assert	sink.isImportedEventType(sinks[i].sinkEventType) :
						new RuntimeException(
								sinks[i].sinkEventType
								+ " is not an imported event of "
								+ sink.getURI());
			}
		}

		SimulatorI[] ces = new SimulatorI[models.length];
		for (int i = 0 ; i < ces.length ; i++) {
			ces[i] = models[i].getSimulationEngine();
		}

		// Set the influencees in the submodels i.e., the models that
		// imports their exported events given the prescribed connections.
		this.setInfluencees(newModelURI, models, connections);

		if (!cmFactory.modelParametersSet()) {
			cmFactory.setCoupledModelCreationParameters(models,
														newModelURI,
														imported,
														reexported,
														connections,
														null,
														null,
														null);
		}
		CoupledModel ret = (CoupledModel) cmFactory.createCoupledModel();

		((CoordinationEngine)ret.getSimulationEngine()).
											setCoordinatedEngines(ces);
		// Set the parents of the models
		this.setParentModel(ret, models);
		// Set the parents of the simulation engines
		this.setParentSimulationEngine(
					((CoordinationEngine)ret.getSimulationEngine()), ces);

		return ret;
	}
}
// -----------------------------------------------------------------------------
