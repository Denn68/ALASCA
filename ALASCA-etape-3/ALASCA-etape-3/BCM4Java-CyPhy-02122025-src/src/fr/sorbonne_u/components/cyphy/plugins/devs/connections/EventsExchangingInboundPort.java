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

import java.util.ArrayList;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.EventsExchangingCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The class <code>EventsExchangingInboundPort</code> implements the inbound
 * port for the offered interface <code>EventsExchangingCI</code>.
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
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			EventsExchangingInboundPort
extends		AbstractInboundPort
implements	EventsExchangingCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				EventsExchangingInboundPort(
		String uri,
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(uri, EventsExchangingCI.class, owner,
			  pluginURI, executorServiceURI);
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				EventsExchangingInboundPort(
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(EventsExchangingCI.class, owner, pluginURI, executorServiceURI);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.EventsExchangingCI#storeInput(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public void			storeInput(
		String destinationURI,
		ArrayList<EventI> es
		) throws Exception 
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AtomicSimulatorPlugin)
									this.getServiceProviderReference()).
												storeInput(destinationURI, es);
						return null;
					}
				});
	}
}
// -----------------------------------------------------------------------------
