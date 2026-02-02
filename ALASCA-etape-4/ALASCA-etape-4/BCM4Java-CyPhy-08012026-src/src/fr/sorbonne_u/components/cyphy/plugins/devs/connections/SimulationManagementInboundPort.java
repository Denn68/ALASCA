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
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulatorPluginManagementInboundPort</code> implements the
 * inbound port for the offered interface
 * <code>SimulatorPluginManagementCI</code>.
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
public class			SimulationManagementInboundPort
extends		AbstractInboundPort
implements	SimulationManagementCI
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
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				SimulationManagementInboundPort(
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(SimulationManagementCI.class, owner,
			  pluginURI, executorServiceURI);
	}

	/**
	 * create an inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri					unique identifier of the port.
	 * @param owner					component that owns this port.
	 * @param pluginURI				URI of the plug-in to be called in the owner or null if none.
	 * @param executorServiceURI	URI of the executor service to be used to execute the service on the component or null if none.
	 * @throws Exception 			<i>todo.</i>
	 */
	public				SimulationManagementInboundPort(
		String uri,
		ComponentI owner,
		String pluginURI,
		String executorServiceURI
		) throws Exception
	{
		super(uri, SimulationManagementCI.class, owner,
			  pluginURI, executorServiceURI);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#constructSimulator(java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public boolean		constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
											constructSimulator(modelURI,
															   architecture);
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationManagementInboundPortURI()
	 */
	@Override
	public String		getSimulationManagementInboundPortURI() throws Exception
	 {
		 return this.getOwner().handleRequest(
				 new AbstractComponent.AbstractService<String>(this.pluginURI) {
					 @Override
					 public String call() throws Exception {
						 return ((AbstractSimulatorPlugin)
								 this.getServiceProviderReference()).
								 		getSimulationManagementInboundPortURI();
					 }
				  });
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getModelInboundPortURI()
	 */
	@Override
	public String		getModelInboundPortURI() throws Exception
	{
		 return this.getOwner().handleRequest(
				 new AbstractComponent.AbstractService<String>(this.pluginURI) {
					 @Override
					 public String call() throws Exception {
						 return ((AbstractSimulatorPlugin)
								 this.getServiceProviderReference()).
								 					getModelInboundPortURI();
					 }
				  });
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulatorInboundPortURI()
	 */
	@Override
	public String		getSimulatorInboundPortURI() throws Exception
	{
		 return this.getOwner().handleRequest(
				 new AbstractComponent.AbstractService<String>(this.pluginURI) {
					 @Override
					 public String call() throws Exception {
						 return ((AbstractSimulatorPlugin)
								 this.getServiceProviderReference()).
								 				getSimulatorInboundPortURI();
					 }
				  });
	}
//
//	/**
//	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getEventsExchangingInboundPortURI()
//	 */
//	@Override
//	public String		getEventsExchangingInboundPortURI() throws Exception
//	{
//		 return this.getOwner().handleRequest(
//				 new AbstractComponent.AbstractService<String>(this.pluginURI) {
//					 @Override
//					 public String call() throws Exception {
//						 return ((AbstractSimulatorPlugin)
//								 this.getServiceProviderReference()).
//								 		getSimulationManagementInboundPortURI();
//					 }
//				  });
//	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#connectSupervision(java.lang.String)
	 */
	@Override
	public void			connectSupervision(
		String supervisorNotificationInboundPortURI
		) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
									connectSupervision(
										supervisorNotificationInboundPortURI);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#reinitialise()
	 */
	@Override
	public void			reinitialise() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
																reinitialise();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
															getTimeOfStart();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Time>(this.pluginURI) {
					@Override
					public Time call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
													getSimulationEndTime();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception
	{
		this.getOwner().handleRequest(
					new AbstractComponent.AbstractService<Void>(this.pluginURI) {
						@Override
						public Void call() throws Exception {
							((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
										setSimulationRunParameters(simParams);
							return null;
						}
					});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
										doStandAloneSimulation(
											startTime, simulationDuration);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
								this.getServiceProviderReference()).
										startRTSimulation(realTimeOfStart,
														  simulationStartTime,
														  simulationDuration);
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Boolean>(this.pluginURI) {
					@Override
					public Boolean call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														isSimulationRunning();
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#stopSimulation()
	 */
	@Override
	public void			stopSimulation() throws Exception
	{
		this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<Void>(this.pluginURI) {
					@Override
					public Void call() throws Exception {
						((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														stopSimulation();
						return null;
					}
				});
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#finaliseSimulation()
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
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return this.getOwner().handleRequest(
				new AbstractComponent.AbstractService<SimulationReportI>(
															this.pluginURI) {
					@Override
					public SimulationReportI call() throws Exception {
						return ((AbstractSimulatorPlugin)
									this.getServiceProviderReference()).
														getFinalReport();
					}
				});
	}
}
// -----------------------------------------------------------------------------
