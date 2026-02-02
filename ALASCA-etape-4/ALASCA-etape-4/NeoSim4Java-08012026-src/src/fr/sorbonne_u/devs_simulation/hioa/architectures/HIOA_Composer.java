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
import java.util.stream.Stream;
import java.util.HashMap;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.ModelComposer;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Composer</code> provides methods implementing HIOA
 * composition; it is abstract as it only defines static methods.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In addition to the composition performed for standard atomic models,
 * composing HIOA models requires to bind correctly the exported and imported
 * variables among them according to the relationships given in the architecture
 * description.
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
 * <p>Created on : 2018-07-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			HIOA_Composer
extends		ModelComposer
{
	private static final long serialVersionUID = 1L;

	/**
	 * passes the placeholders for values of exported variables to imported
	 * variables among submodels as prescribed by <code>bindings</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null && models.length > 1}
	 * pre	{@code Stream.of(models).allMatch(m -> m != null)}
	 * pre	{@code bindings != null}
	 * </pre>
	 *
	 * @param models		models to be composed.
	 * @param bindings		bindings between exported and imported variables among submodels.
	 */
	protected void		setBindings(
		ModelI[] models,
		Map<VariableSource,VariableSink[]> bindings
		)
	{
		assert	models != null && models.length > 1 :
				new NeoSim4JavaException("Precondition violation: "
							+ "models != null && models.length > 1");
		// Assert that all submodels are defined (not null).
		assert	Stream.of(models).allMatch(m -> m != null) :
				new NeoSim4JavaException("Precondition violation: "
							+ "Stream.of(models).allMatch(m -> m != null)");
		assert	bindings != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "bindings != null");

		Map<String,ModelI> uris2models = new HashMap<>();
		for (int i = 0 ; i < models.length ; i++) {
			uris2models.put(models[i].getURI(), models[i]);
		}

		for (VariableSource source : bindings.keySet()) {
			ModelI sourceHIOA = uris2models.get(source.exportingModelURI);
			Value<?> valueReference =
				sourceHIOA.getActualExportedVariableValueReference(
											source.exportingModelURI,
											source.name,
											source.type);
			VariableSink[] sinks = bindings.get(source);
			for (int i = 0 ; i < sinks.length ; i++) {
				ModelI sinkHIOA = uris2models.get(sinks[i].sinkModelURI);
				sinkHIOA.setImportedVariableValueReference(
											sinks[i].sinkModelURI,
											sinks[i].sinkVariableName,
											sinks[i].sinkVariableType,
											valueReference);
			}
		}
	}

	/**
	 * compose the HIOA and return the resulting coupled HIOA.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code models != null and models.length > 1}
	 * pre	{@code Stream.of(models).allMatch(m -> m != null)}
	 * pre	{@code newModelURI != null && !newModelURI.isEmpty()}
	 * pre	{@code cmFactory != null}
	 * pre	{@code imported != null}
	 * pre	{@code reexported != null}
	 * pre	{@code connections != null}
	 * pre	{@code importedVars != null}
	 * pre	{@code reexportedVars != null}
	 * pre	{@code bindings != null}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @param models			models to be composed.
	 * @param newModelURI		URI of the resulting coupled model.
	 * @param cmFactory			factory creating the right type of coupled model.
	 * @param imported			imported events and their conversion to submodels imported ones.
	 * @param reexported		exported events from the coupled model and their conversion from exported ones by submodels.
	 * @param connections		connections between exported and imported events among submodels.
	 * @param importedVars		variables imported by the coupled model that are consumed by submodels.
	 * @param reexportedVars	variables exported by submodels that are reexported by the coupled model.
	 * @param bindings			bindings between exported and imported variables among submodels.
	 * @return					coupled HIOA resulting from the composition.
	 */
	public CoupledModelI	compose(
		ModelI[] models,
		String newModelURI,
		CoupledModelFactoryI cmFactory,
		Map<Class<? extends EventI>,EventSink[]> imported,
		Map<Class<? extends EventI>,ReexportedEvent> reexported,
		Map<EventSource,EventSink[]> connections,
		Map<StaticVariableDescriptor,VariableSink[]> importedVars,
		Map<VariableSource,StaticVariableDescriptor> reexportedVars,
		Map<VariableSource,VariableSink[]> bindings
		)
	{
		assert	models != null && models.length > 1 :
				new NeoSim4JavaException("Precondition violation: "
							+ "models != null && models.length > 1");
		// Assert that all submodels are defined (not null).
		assert	Stream.of(models).allMatch(m -> m != null) :
				new NeoSim4JavaException("Precondition violation: "
							+ "Stream.of(models).allMatch(m -> m != null)");
		assert	newModelURI != null && !newModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
							+ "newModelURI != null && !newModelURI.isEmpty()");
		assert	cmFactory != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "cmFactory != null");
		assert	imported != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "imported != null");
		assert	reexported != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "reexported != null");
		assert	connections != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "connections != null");
		assert	importedVars != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "importedVars != null");
		assert	reexportedVars != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "reexportedVars != null");
		assert	bindings != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "bindings != null");

		this.setBindings(models, bindings);

		cmFactory.setCoupledModelCreationParameters(models,
												   newModelURI,
												   imported,
												   reexported, 
												   connections,
												   importedVars,
												   reexportedVars,
												   bindings);
		return this.compose(models,
							newModelURI,
							cmFactory,
							imported,
							reexported,
							connections);
	}
}
// -----------------------------------------------------------------------------
