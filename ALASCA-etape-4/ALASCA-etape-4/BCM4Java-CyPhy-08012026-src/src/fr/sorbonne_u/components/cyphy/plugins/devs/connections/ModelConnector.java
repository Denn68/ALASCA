package fr.sorbonne_u.components.cyphy.plugins.devs.connections;

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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.utils.Pair;

// -----------------------------------------------------------------------------
/**
 * The class <code>ModelConnector</code>
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
 * <p>Created on : 2023-10-31</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ModelConnector
extends		AbstractConnector
implements	ModelCI
{
	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getURI()
	 */
	@Override
	public String		getURI() throws Exception
	{
		return ((ModelCI)this.offering).getURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#hasURI(java.lang.String)
	 */
	@Override
	public boolean		hasURI(String uri) throws Exception
	{
		return ((ModelCI)this.offering).hasURI(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit() throws Exception
	{
		return ((ModelCI)this.offering).getSimulatedTimeUnit();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isAtomic()
	 */
	@Override
	public boolean		isAtomic() throws Exception
	{
		return ((ModelCI)this.offering).isAtomic();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return ((ModelCI)this.offering).isParentSet();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#setParent(java.lang.String)
	 */
	@Override
	public void			setParent(String inboubdPortURI) throws Exception
	{
		((ModelCI)this.offering).setParent(inboubdPortURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getParentURI()
	 */
	@Override
	public String		getParentURI() throws Exception
	{
		return ((ModelCI)this.offering).getParentURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isRoot()
	 */
	@Override
	public boolean		isRoot() throws Exception
	{
		return ((ModelCI)this.offering).isRoot();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#closed()
	 */
	@Override
	public boolean		closed() throws Exception
	{
		return ((ModelCI)this.offering).closed();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return ((ModelCI)this.offering).isImportedEventType(ec);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getImportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]	getImportedEventTypes() throws Exception
	{
		return ((ModelCI)this.offering).getImportedEventTypes();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return ((ModelCI)this.offering).isExportedEventType(ec);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getExportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]	getExportedEventTypes() throws Exception
	{
		return ((ModelCI)this.offering).getExportedEventTypes();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(Class<? extends EventI> ce)
	throws Exception
	{
		return ((ModelCI)this.offering).getEventAtomicSource(ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelCI)this.offering).getEventAtomicSinks(ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isDescendant(java.lang.String)
	 */
	@Override
	public boolean		isDescendant(String uri) throws Exception
	{
		return ((ModelCI)this.offering).isDescendant(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		((ModelCI)this.offering).addInfluencees(modelURI, ce, influencees);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelCI)this.offering).getInfluencees(modelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelCI)this.offering).
					areInfluencedThrough(modelURI, destinationModelURIs, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		return ((ModelCI)this.offering).
						isInfluencedThrough(modelURI, destinationModelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isTIOA()
	 */
	@Override
	public boolean		isTIOA() throws Exception
	{
		return ((ModelCI)this.offering).isTIOA();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception
	{
		((ModelCI)this.offering).setSimulationRunParameters(simParams);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables() throws Exception
	{
		return ((ModelCI)this.offering).useFixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	throws Exception
	{
		return ((ModelCI)this.offering).fixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent) throws Exception
	{
		return ((ModelCI)this.offering).modelAsString(indent);
	}
}
// -----------------------------------------------------------------------------
