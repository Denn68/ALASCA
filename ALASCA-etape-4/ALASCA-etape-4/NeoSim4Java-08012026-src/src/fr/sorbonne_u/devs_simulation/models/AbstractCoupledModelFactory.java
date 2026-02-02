package fr.sorbonne_u.devs_simulation.models;

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
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.StaticVariableDescriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractCoupledModelFactory</code> provides the
 * basic implementation for coupled model factories.
 *
 * <p><strong>Description</strong></p>
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
public abstract class	AbstractCoupledModelFactory
implements	CoupledModelFactoryI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	/** models to be composed.												*/
	protected ModelI[]										models;
	/** URI of the resulting coupled model.									*/
	protected String										modelURI;
	/** imported events and their conversion to submodels imported ones.	*/
	protected Map<Class<? extends EventI>,EventSink[]>		imported;
	/** exported events from submodels and their conversion to
	 *  exported ones.														*/
	protected Map<Class<? extends EventI>,ReexportedEvent>	reexported;
	/** connections between exported and imported events among submodels.	*/
	protected Map<EventSource,EventSink[]>					connections;
	/** variables imported by this coupled model (for HIOA).				*/
	protected Map<StaticVariableDescriptor,VariableSink[]>	importedVars;
	/** variables exported by this coupled model (for HIOA).				*/
	protected Map<VariableSource,StaticVariableDescriptor>	reexportedVars;
	/** bindings between exported and imported variables among submodels.	*/
	protected Map<VariableSource,VariableSink[]>			bindings;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#setCoupledModelCreationParameters(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI[], java.lang.String, java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map, java.util.Map)
	 */
	@Override
	public void			setCoupledModelCreationParameters(
		ModelI[] models,
		String modelURI,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections,
		Map<StaticVariableDescriptor, VariableSink[]> importedVars,
		Map<VariableSource, StaticVariableDescriptor> reexportedVars,
		Map<VariableSource, VariableSink[]> bindings
		)
	{
		assert	modelURI != null && !modelURI.isEmpty():
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	Stream.of(models).allMatch(m -> m != null) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Stream.of(models).allMatch(m -> m != null)");
		TimeUnit tu = models[0].getSimulatedTimeUnit();
		assert	Stream.of(models).allMatch(
					m -> { try {
								return m.getSimulatedTimeUnit().equals(tu);
						   } catch (Exception e) {
							   	throw new RuntimeException(e);
						   }
						 }) :
				new NeoSim4JavaException("Precondition violation: "
						+ "Stream.of(models).allMatch("
						+ "m -> m.getSimulatedTimeUnit().equals(tu))");

		this.models = models;
		this.modelURI = modelURI;
		this.imported = imported;
		this.reexported = reexported;
		this.connections = connections;
		this.importedVars = 
				(importedVars != null ?
					importedVars
				:	new HashMap<StaticVariableDescriptor, VariableSink[]>()
				);
		this.reexportedVars =
				(reexportedVars != null ?
					reexportedVars
				:	new HashMap<VariableSource, StaticVariableDescriptor>()
				);
		this.bindings =
				(bindings != null ?
					bindings
				:	new HashMap<VariableSource, VariableSink[]>()
				);

		assert	this.modelParametersSet() :
				new NeoSim4JavaException("Postcondition violation: "
											+ "modelParametersSet()");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#modelParametersSet()
	 */
	@Override
	public boolean		modelParametersSet()
	{
		return this.models != null && this.modelURI != null &&
				this.imported != null && this.reexported != null &&
				this.imported != null &&
				this.importedVars != null && this.reexportedVars != null
				&& this.bindings != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createCoordinationEngine()
	 */
	@Override
	public CoordinatorI		createCoordinationEngine()
	{
		return new CoordinationEngine();
	}
}
// -----------------------------------------------------------------------------
