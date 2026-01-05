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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulatorInboundPort</code> implements the inbound
 * port for the offered interface <code>SimulatorCI</code>.
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
public class			SimulatorInboundPort
extends		AbstractInboundPort
implements	SimulatorCI
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
	 * pre	{@code SimulatorCI.class.isAssignableFrom(implementedInterface)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				SimulatorInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(uri, implementedInterface, owner, pluginURI, executorServiceURI);

		assert	SimulatorCI.class.isAssignableFrom(implementedInterface) :
				new PreconditionException("SimulatorCI.class.isAssignableFrom(implementedInterface)");
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code SimulatorCI.class.isAssignableFrom(implementedInterface)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param implementedInterface	interface implemented by this port.
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				SimulatorInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(implementedInterface, owner, pluginURI, executorServiceURI);

		assert	SimulatorCI.class.isAssignableFrom(implementedInterface) :
				new PreconditionException(
						"SimulatorCI.class.isAssignableFrom("
						+ "implementedInterface)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isParentSet()
	 */
	@Override
	public boolean		isParentSet() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														isParentSimulatorSet();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#setParent(java.lang.String)
	 */
	@Override
	public void			setParent(String parentNotificationInboundPortURI)
	throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
									setParentSimulator(
											parentNotificationInboundPortURI);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
									initialiseSimulation(simulationDuration);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
									initialiseSimulation(
											startTime, simulationDuration);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
												isSimulationInitialised();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
														finaliseSimulation();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
											useFixpointInitialiseVariables();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesTimeInitialised()
	 */
	@Override
	public boolean		allModelVariablesTimeInitialised() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
											allModelVariablesTimeInitialised();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesInitialised()
	 */
	@Override
	public boolean		allModelVariablesInitialised() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
												allModelVariablesInitialised();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer,Integer>	fixpointInitialiseVariables()
	throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Pair<Integer,Integer>>(
															this.pluginURI) {
					@Override
					public Pair<Integer,Integer> call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
												fixpointInitialiseVariables();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#internalEventStep()
	 */
	@Override
	public void			internalEventStep() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)this.getServiceProviderReference()).
													internalEventStep();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)this.getServiceProviderReference()).
											externalEventStep(elapsedTime);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)this.getServiceProviderReference()).
													produceOutput(current);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
													getTimeOfLastEvent();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
													getTimeOfNextEvent();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Duration>(this.pluginURI) {
					@Override
					public Duration call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
													getNextTimeAdvance();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void		endSimulation(Time endTime) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin) this.getServiceProviderReference()).
													endSimulation(endTime);
						return null;
					}
				});		
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin) this.getServiceProviderReference()).
														toggleDebugMode();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														isDebugModeOn();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin) this.getServiceProviderReference()).
											setDebugLevel(newDebugLevel);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel) throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
											hasDebugLevel(debugLevel);
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<String>(this.pluginURI) {
					@Override
					public String call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														simulatorAsString();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin) this.getServiceProviderReference()).
								showCurrentStateContent(indent, elapsedTime);
						return null;
					}
				});		
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin) this.getServiceProviderReference()).
								showCurrentState(indent, elapsedTime);
						return null;
					}
				});		
	}
}
// -----------------------------------------------------------------------------
