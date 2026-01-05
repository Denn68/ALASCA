package fr.sorbonne_u.components.cyphy.plugins.devs;

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

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionOutboundPort;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureHelper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelWrapper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentSimulatorWrapper;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ParentNotificationInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulationManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulatorOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ParentNotificationCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.devs_simulation.models.Model;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoordinatorPlugin</code> implements a BCM plug-in for
 * the coordination role in DEVS simulations.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In DEVS, coupled simulation models are meant to coordinate the submodels
 * they compose. This plug-in gathers the necessary code to make a BCM component
 * able to hold and execute a coupled simulation model.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code (architecture == null || architecture.isCoupledModel(getPluginURI()))}
 * invariant	{@code (smops == null || coordinatedPorts == null) || (smops.size() == coordinatedPorts.size())}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoordinatorPlugin
extends		AbstractSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L;
	/** simulation architecture to which the coupled model held by the
	 *  plug-in belongs.													*/
	protected ComponentModelArchitectureI					architecture;
	/** map	map from URIs of the submodels to the reflection outbound
	 *  ports allowing this plug-in to connect to them.						*/
	protected Map<String,CyPhyReflectionOutboundPort>		subComponents;
	/** map from URIs of the submodels to the outbound ports allowing this
	 *  plug-in's coupled model to manage their simulations.				*/
	protected Map<String,SimulationManagementOutboundPort>	smops;
	/** map from URIs of the submodels to the model outbound ports
	 * 	allowing this plug-in's coupled model to call these submodels.		*/
	protected Map<String,ModelOutboundPort>					submodelsPorts;
	/** map from URIs of the submodels to the simulator outbound ports
	 *  allowing this plug-in's coordination engine to call the submodels
	 *  simulation engines.													*/
	protected Map<String,SimulatorOutboundPort>				coordinatedPorts;
	/** inbound port through which this plug-in's coupled model receives
	 *  notifications from its submodels.									*/
	protected ParentNotificationInboundPort					pnip;
	/** port through which the root model can be accessed by the children
	 *  models held in other components.									*/
	protected ModelInboundPort								fromChildMip;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * create a coordinator plug-in instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code CoordinatorPlugin.checkInvariant(this)}
	 * </pre>
	 *
	 */
	public				CoordinatorPlugin()
	{
		super();

		assert	CoordinatorPlugin.checkInvariant(this);
	}

	/**
	 * check the invariant for the given plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code p != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param p		the plug-in to be checked.
	 * @return		true if the plug-in obeys to its invariant.
	 */
	public static boolean	checkInvariant(CoordinatorPlugin p)
	{
		assert	p != null : new PreconditionException("p != null");

		boolean invariant = true;
		try {
			invariant &= (p.architecture == null ||
							p.architecture.isCoupledModel(p.getPluginURI()));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		assert	invariant :
					new InvariantException("architecture == null || " + 
							"architecture.isCoupledModel(getPluginURI())");
		invariant &= (p.smops == null || p.coordinatedPorts == null) ||
						(p.smops.size() == p.coordinatedPorts.size());
		assert	invariant :
					new InvariantException(
						"(p.smops == null || p.coordinatedPorts == null) || " + 
						"(p.smops.size() == p.coordinatedPorts.size())");

		return invariant;
	}

	// -------------------------------------------------------------------------
	// Plug-in life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		super.installOn(owner);

		if (!this.getOwner().isRequiredInterface(CyPhyReflectionCI.class)) {
			this.addRequiredInterface(CyPhyReflectionCI.class);
		}

		// add the required and offered component interfaces to the owner
		// component
		this.addRequiredInterface(SimulationManagementCI.class);
		this.addRequiredInterface(SimulatorCI.class);
		this.addOfferedInterface(ParentNotificationCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise();

		// create an inbound port for parent notifications and publish it.
		this.pnip = new ParentNotificationInboundPort(
										this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.pnip.publishPort();
		this.fromChildMip = new ModelInboundPort(
										this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.fromChildMip.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.subComponents != null) {
			for (CyPhyReflectionOutboundPort rop : this.subComponents.values()) {
				if (rop.connected()) {
					this.getOwner().doPortDisconnection(rop.getPortURI());
				}
			}
		}
		if (this.smops != null) {
			for (SimulationManagementOutboundPort smop : this.smops.values()) {
				if (smop.connected()) {
					this.getOwner().doPortDisconnection(smop.getPortURI());
				}
			}
		}
		if (this.submodelsPorts != null) {
			for (ModelOutboundPort m : this.submodelsPorts.values()) {
				if (m.connected()) {
					this.getOwner().doPortDisconnection(m.getPortURI());
				}
			}
		}
		if (this.coordinatedPorts != null) {
			for (SimulatorOutboundPort sop : this.coordinatedPorts.values()) {
				if (sop.connected()) {
					this.getOwner().doPortDisconnection(sop.getPortURI());
				}
			}
		}

		assert	this.subComponents == null ||
					this.subComponents.values().stream().
						map(sc -> {	try {
										return !sc.connected();
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}).allMatch(b -> b);
		assert	this.smops == null ||
					this.smops.values().stream().
						map(smop -> {	try {
											return !smop.connected();
										} catch (Exception e) {
											throw new RuntimeException(e);
										}
									}).allMatch(b -> b);
		assert	this.submodelsPorts == null ||
					this.submodelsPorts.values().stream().
						map(smp -> {	try {
										return !smp.connected();
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								}).allMatch(b -> b);
		assert	this.coordinatedPorts == null ||
					this.coordinatedPorts.values().stream().
						map(sop -> {	try {
											return !sop.connected();
										} catch (Exception e) {
											throw new RuntimeException(e);
										}
									}).allMatch(b -> b);
		
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		// unpublish and destroy all ports; remove required and offered
		// interfaces.
		if (this.subComponents != null && !this.subComponents.isEmpty()) {
			for (CyPhyReflectionOutboundPort rop : this.subComponents.values()) {
				assert	!rop.connected();
				rop.unpublishPort();
				rop.destroyPort();
			}
			this.subComponents.clear();
		}
		this.subComponents = null;
		if (this.smops != null && !this.smops.isEmpty()) {
			for (SimulationManagementOutboundPort smop : this.smops.values()) {
				assert	!smop.connected();
				smop.unpublishPort();
				smop.destroyPort();
			}
			this.smops.clear();
		}
		this.smops = null;
		if (this.submodelsPorts != null) {
			for (ModelOutboundPort m : this.submodelsPorts.values()) {
				assert	!m.connected();
				m.unpublishPort();
				m.destroyPort();
			}
			this.submodelsPorts.clear();
		}
		this.submodelsPorts = null;
		if (this.coordinatedPorts != null) {
			for (SimulatorOutboundPort sop : this.coordinatedPorts.values()) {
				assert	!sop.connected();
				sop.unpublishPort();
				sop.destroyPort();
			}
			this.coordinatedPorts.clear();
		}
		this.coordinatedPorts = null;

		this.removeRequiredInterface(SimulationManagementCI.class);
		this.removeRequiredInterface(SimulatorCI.class);

		this.pnip.unpublishPort();
		this.pnip.destroyPort();
		this.pnip = null;
		this.removeOfferedInterface(ParentNotificationCI.class);
		this.fromChildMip.unpublishPort();
		this.fromChildMip.destroyPort();
		this.fromChildMip = null;

		super.uninstall();
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the simulation architecture has been set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulation architecture has been set.
	 */
	public boolean		isSimulationArchitectureSet()
	{
		return this.architecture != null;
	}
	
	/**
	 * return true if {@code uri} is the URI of the simulation architecture and
	 * false if the architecture is not set or does not have this URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	URI to be tested.
	 * @return		true if {@code uri} is the URI of the simulation architecture and false if the architecture is not set or does not have this URI.
	 */
	public boolean		isArchitecture(String uri)
	{
		if (this.isSimulationArchitectureSet()) {
			return this.architecture.getArchitectureURI().equals(uri);
		} else {
			return false;
		}
	}

	// -------------------------------------------------------------------------
	// Methods from SimulationManagementCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#constructSimulator(java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	public boolean		constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		this.logMessage("creating and connecting " + modelURI);
		if (this.subComponents == null) {
			this.subComponents = new HashMap<>();
		}
		if (this.smops == null) {
			this.smops = new HashMap<>();
		} else {
			this.smops.clear();
		}
		if (this.submodelsPorts == null) {
			this.submodelsPorts = new HashMap<>();
		} else {
			this.submodelsPorts.clear();
		}
		if (this.coordinatedPorts == null) {
			this.coordinatedPorts = new HashMap<>();
		} else {
			this.coordinatedPorts.clear();
		}
		assert	!this.isSimulationArchitectureSet() :
				new AssertionError("!isSimulationArchitectureSet()");

		this.architecture = architecture;
		ComponentCoupledModelDescriptor coordinatorModelDescriptor =
				(ComponentCoupledModelDescriptor) architecture.
												getModelDescriptor(modelURI);
		Set<String> submodelsURIs = architecture.getChildrenModelURIs(modelURI);
		ModelI[] models = new ModelI[submodelsURIs.size()];
		int i = 0;
		for (String uri : submodelsURIs) {
			ComponentModelDescriptorI submodelDescriptor =
										architecture.getModelDescriptor(uri);
			// connect this component to the component holding the submodel
			// through the reflection interface interface.
			CyPhyReflectionOutboundPort rop =
					ComponentModelArchitectureHelper.
							connectComponent((AbstractComponent)this.getOwner(),
											 submodelDescriptor);
			this.subComponents.put(uri, rop);
			// connect this component to the component holding the submodel
			// through the simulation management interface.
			SimulationManagementOutboundPort smop =
					ComponentModelArchitectureHelper.
						connectManagementPort(
								(AbstractComponent) this.getOwner(), uri, rop);
			this.smops.put(uri, smop);
			SimulatorOutboundPort sop =
					ComponentModelArchitectureHelper.
						connectSimulatorPort(
								(AbstractComponent) this.getOwner(), uri, rop);
			this.coordinatedPorts.put(uri, sop);
			SimulatorI s =
					new ComponentSimulatorWrapper(sop, this.pnip.getPortURI());
			ModelOutboundPort mop =
					ComponentModelArchitectureHelper.
						connectModelPort(
								(AbstractComponent) this.getOwner(), uri, rop);
			this.submodelsPorts.put(uri, mop);
			ModelI m = new ComponentModelWrapper(
										mop, s, this.fromChildMip.getPortURI());
			s.setSimulatedModel(m);
			models[i++] = m;
			smop.constructSimulator(uri, architecture);
		}
		this.rootModel = coordinatorModelDescriptor.createCoupledModel(models);
		this.rootSimulator = ((Model)this.rootModel).getSimulationEngine();

		this.logMessage(modelURI + " created and connected.");
		return true;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#reinitialise()
	 */
	@Override
	public void			reinitialise() throws Exception
	{
		if (this.smops != null) {
			for (SimulationManagementOutboundPort smop : this.smops.values()) {
				if (smop.connected()) {
					smop.reinitialise();
					this.getOwner().doPortDisconnection(smop.getPortURI());
				}
//				smop.unpublishPort();
//				smop.destroyPort();
			}
//			this.smops.clear();
//			this.smops = null;
		}
		if (this.coordinatedPorts != null) {
			for (SimulatorOutboundPort sop : this.coordinatedPorts.values()) {
				if (sop.connected()) {
					this.getOwner().doPortDisconnection(sop.getPortURI());
				}
//				sop.unpublishPort();
//				sop.destroyPort();
			}
//			this.coordinatedPorts.clear();
//			this.coordinatedPorts = null;
		}
		if (this.submodelsPorts != null) {
			for (ModelOutboundPort mop : this.submodelsPorts.values()) {
				if (mop.connected()) {
					this.getOwner().doPortDisconnection(mop.getPortURI());
				}
//				mop.unpublishPort();
//				mop.destroyPort();
			}
//			this.submodelsPorts.clear();
//			this.submodelsPorts = null;
		}

		if (this.subComponents != null) {
			Set<String> childrenModelsURIs =
				this.architecture.getChildrenModelURIs(this.getPluginURI());
			for (String uri : childrenModelsURIs) {
				if (this.architecture.getModelDescriptor(uri) instanceof
													CoupledModelDescriptor) {
					CyPhyReflectionOutboundPort rop =
												this.subComponents.get(uri);
					rop.uninstallPlugin(uri);
				}
			}
			for (CyPhyReflectionOutboundPort rop : this.subComponents.values()) {
				this.getOwner().doPortDisconnection(rop.getPortURI());
//				rop.unpublishPort();
//				rop.destroyPort();			
			}
//			this.subComponents.clear();
//			this.subComponents = null;
		}

		this.architecture = null;

		super.reinitialise();
	}

	// -------------------------------------------------------------------------
	// Methods from ModelCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isAtomic()
	 */
	public boolean		isAtomic() throws Exception
	{
		return false;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSinks(java.lang.Class)
	 */
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
			Class<? extends EventI> ce
			) throws Exception
	{
		return this.getRootModel().getEventAtomicSinks(ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		this.getRootModel().addInfluencees(modelURI, ce, influencees);
	}

	// -------------------------------------------------------------------------
	// Methods from ParentNotificationCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ParentNotificationCI#hasReceivedExternalEvents(java.lang.String)
	 */
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		((CoordinatorI)this.getRootSimulator()).
										hasReceivedExternalEvents(modelURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ParentNotificationCI#hasPerformedExternalEvents(java.lang.String)
	 */
	public void			hasPerformedExternalEvents(String modelURI)
	throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		((CoordinatorI)this.getRootSimulator()).
										hasPerformedExternalEvents(modelURI);
	}

	// -------------------------------------------------------------------------
	// Methods from SimulationManagementCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#startRTSimulation(long, double, double)
	 */
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");
		assert	this.getRootSimulator().isRealTime() :
				new AssertionError("getRootSimulator().isRealTime()");

		if (this.getRootModel().isRoot()) {
			TimeUnit tu = this.getRootModel().getSimulatedTimeUnit();
			// initialise the simulation with its start time and its duration
			// in simulated time (with its proper time unit).
			this.initialiseSimulation(new Time(simulationStartTime, tu),
									  new Duration(simulationDuration, tu));
		}
		for (SimulationManagementOutboundPort smop : this.smops.values()) {
			smop.startRTSimulation(
					realTimeOfStart, simulationStartTime, simulationDuration);
		}
	}
}
// -----------------------------------------------------------------------------

