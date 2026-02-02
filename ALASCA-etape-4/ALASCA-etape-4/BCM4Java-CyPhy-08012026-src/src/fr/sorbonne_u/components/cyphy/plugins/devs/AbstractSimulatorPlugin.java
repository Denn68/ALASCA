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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoordinatorWrapper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentCoupledModelWrapper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ParentNotificationConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ParentNotificationOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulationManagementInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulatorInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SupervisorNotificationConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SupervisorNotificationOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ParentNotificationCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationCI;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractSimulatorPlugin</code> implements the core
 * simulation methods (DEVS protocol and management) as a BCM plug-in.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * <code>AbstractSimulatorPlugin</code> contains the shared methods between
 * the plug-ins for atomic models and coupled models simulation that are
 * implemented as plug-ins inheriting from this class.
 * </p>
 * <p>
 * Basically, the plug-in contains a reference to a DEVS simulation engine
 * (from the DEVS simulation library) which itself contains a reference to
 * a DEVS model. The current implementation assumes that a plug-in corresponds
 * to one component and vice versa. Hence, the simulation engine is seen as
 * an atomic simulation model from the supervisor or the parent engine, but
 * it can correspond to a local composite model (coupled model and/or
 * coordination engine). Another assumption is that the plug-in URI is the
 * same as the local architecture root model URI. A third assumption is that
 * models cannot share continuous variables among different components, hence
 * the local architecture root model is necessarily a TIOA.
 * </p>
 * <p>
 * The plug-in implements the interface <code>ModelDescriptionI</code> (the
 * DEVS simulation library interface that is used to describe models for
 * composition purposes), <code>SimulatorI</code>,
 * <code>ParentNotificationI</code> and <code>EventsExchangingI</code>.
 * Most of the time, the methods simply delegate to  the plug-in simulation
 * engine, sometimes checking stricter invariants. It also implements the
 * interface <code>SimulationManagementI</code> with mixed implementations
 * as some of the methods must here take into account the fact that simulation
 * engines and models reside in components and must communication through their
 * host component ports.
 * </p>
 * <p>
 * The plug-in, as other plug-ins, is in charge of declaring offered and
 * required interfaces and it implements the interface
 * <code>SimulatorPluginManagementI</code>, which declares methods specific to
 * the plug-in management. Any component that holds a DEVS simulation engine,
 * be it only an atomic engine or a coordination engine, must offer the
 * component interfaces <code>SimulatorCI</code> that proposes the simulation
 * protocol and <code>SimulatorPluginManagementCI</code> that proposes methods
 * for the management of the composition of the simulation architecture and
 * the management of the simulation runs. The component must also require the
 * component interfaces <code>ParentNotificationCI</code> used to notify
 * parent models when sending events to siblings and the component interface
 * <code>SupervisorNotificationCI</code> to send the simulation reports back to
 * the supervisor component when the simulation runs end. At creation time,
 * the plug-in will declare the above required/offered interfaces and create
 * the corresponding inbound ports and (some) outbound ports. To connect
 * models among different components, the plug-in uses the URI of the reflection
 * inbound port of the other components in order to use the reflection interface
 * to get the URIs of the ports to which it must connect. Hence, the component
 * must also require the <code>CyPhyReflectionI</code> interface.
 * </p>
 * <p>
 * The concrete subclasses implements the methods that require specific
 -* algorithms for the different roles: atomic model simulation or coordination
 * of submodels, acting as a coupled model.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getSimulator() == null || getSimulator().getURI().equals(getPluginURI())}
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
public abstract class	AbstractSimulatorPlugin
extends		AbstractPlugin
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	/** model at the root of the simulation architecture created by this
	 *  plug-in.															*/
	protected ModelI								rootModel;
	/** root simulation engine executing the simulation for the plug-in.	*/
	protected SimulatorI							rootSimulator;

	/** management inbound port of the simulator component.					*/
	protected SimulationManagementInboundPort		smip;
	/** port through which the root model can be accessed by the parent
	 *  model held in another component.									*/
	protected ModelInboundPort						mip;
	/** port through which the root simulator can be accessed by the parent
	 *  coordination engine held in another component.						*/
	protected SimulatorInboundPort					sip;
	/** port through which the root model can access its parent model
	 *  held in another component.											*/
	protected ModelOutboundPort						mop;
	/** port to notify the parent model of the reception of
	 *  external events.													*/
	protected ParentNotificationOutboundPort		pnop;
	/** port connecting the component to the simulation supervisor.			*/
	protected SupervisorNotificationOutboundPort	snop;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				AbstractSimulatorPlugin()
	{
		super();
	}

	// -------------------------------------------------------------------------
	// Plug-in generic methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#setPluginURI(java.lang.String)
	 */
	@Override
	public void			setPluginURI(String uri)
	{
		// the URI of the plug-in must be the same as the URI of its root
		// simulation model, so if the simulator has been set, check that
		// this constraint is obeyed.
		if (this.rootModel != null) {
			try {
				assert	this.rootModel.getURI().equals(uri) :
						new AssertionError(
								"rootModel.getURI().equals(uri)");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		super.setPluginURI(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null : new PreconditionException("owner != null");

		super.installOn(owner);

		if (!owner.isRequiredInterface(ReflectionCI.class)) {
			this.addRequiredInterface(ReflectionCI.class);
		}

		this.addOfferedInterface(ModelCI.class);
		this.addOfferedInterface(SimulatorCI.class);
		this.addOfferedInterface(SimulationManagementCI.class);
		this.addRequiredInterface(SupervisorNotificationCI.class);
		this.addRequiredInterface(ModelCI.class);
		this.addRequiredInterface(ParentNotificationCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise();

		this.mip = new ModelInboundPort(this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.mip.publishPort();
		this.sip = new SimulatorInboundPort(
										SimulatorCI.class,
										this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.sip.publishPort();
		this.smip = new SimulationManagementInboundPort(
										this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.smip.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.snop != null && this.snop.connected()) {
			this.getOwner().doPortDisconnection(this.snop.getPortURI());
		}
		if (this.mop != null && this.mop.connected()) {
			this.getOwner().doPortDisconnection(this.mop.getPortURI());
		}
		if (this.pnop != null && this.pnop.connected()) {
			this.getOwner().doPortDisconnection(this.pnop.getPortURI());
		}
		assert	this.snop == null || !this.snop.connected();
		assert	this.mop == null || !this.mop.connected();
		assert	this.pnop == null || !this.pnop.connected();

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		if (this.mip != null) {
			this.mip.unpublishPort();
			this.mip.destroyPort();
			this.mip = null;
		}
		this.removeOfferedInterface(ModelCI.class);
		if (this.sip != null) {
			this.sip.unpublishPort();
			this.sip.destroyPort();
			this.sip = null;
		}
		this.removeOfferedInterface(SimulatorCI.class);
		if (this.smip != null) {
			this.smip.unpublishPort();
			this.smip.destroyPort();
			this.smip = null;
		}
		this.removeOfferedInterface(SimulationManagementCI.class);
		if (this.snop != null) {
			assert	!this.snop.connected();
			this.snop.unpublishPort();
			this.snop.destroyPort();
			this.snop = null;
		}
		this.removeRequiredInterface(SupervisorNotificationCI.class);
		if (this.mop != null) {
			assert	!this.mop.connected();
			this.mop.unpublishPort();
			this.mop.destroyPort();
			this.mop = null;
		}
		this.removeRequiredInterface(ModelCI.class);
		if (this.pnop != null) {
			assert	!this.pnop.connected();
			this.pnop.unpublishPort();
			this.pnop.destroyPort();
			this.pnop = null;
		}
		this.removeRequiredInterface(ParentNotificationCI.class);

		super.uninstall();
	}

	// -------------------------------------------------------------------------
	// Simulator plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * return	the root model of the simulation architecture created by this
	 * plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the root model of the simulation architecture created by this plug-in.
	 * @throws Exception	<i>to do</i>.
	 */
	protected ModelI	getRootModel() throws Exception
	{
		assert	this.isSimulatorSet();
		return this.rootModel;
	}

	/**
	 * return the reference to the simulation engine associated to this plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the reference to the simulation engine associated to this plug-in.
	 */
	protected SimulatorI	getRootSimulator()
	{
		assert	this.isSimulatorSet();
		return this.rootSimulator;
	}

	/**
	 * return true if the simulator on this plug-in has been created/set,
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulator on this plug-in has been created/set, false otherwise.
	 */
	public boolean		isSimulatorSet()
	{
		return this.rootSimulator != null && this.rootModel != null;
	}

	// -------------------------------------------------------------------------
	// Methods from SimulationManagementCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#constructSimulator(java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	public abstract boolean	constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationManagementInboundPortURI()
	 */
	public String		getSimulationManagementInboundPortURI()
	throws Exception
	{
		if (this.smip != null) {
			return this.smip.getPortURI();
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getModelInboundPortURI()
	 */
	public String		getModelInboundPortURI() throws Exception
	{
		if (this.mip != null) {
			return this.mip.getPortURI();
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulatorInboundPortURI()
	 */
	public String		getSimulatorInboundPortURI() throws Exception
	{
		if (this.sip != null) {
			return this.sip.getPortURI();
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#connectSupervision(java.lang.String)
	 */
	public void			connectSupervision(String supervisionInboundPortURI)
	throws Exception
	{
		assert	supervisionInboundPortURI != null &&
										!supervisionInboundPortURI.isEmpty() :
				new PreconditionException(
						"supervisionInboundPortURI != null && "
						+ "!supervisionInboundPortURI.isEmpty()") ;

		this.snop = new SupervisorNotificationOutboundPort(this.getOwner());
		this.snop.publishPort();
		this.getOwner().doPortConnection(
					this.snop.getPortURI(),
					supervisionInboundPortURI,
					SupervisorNotificationConnector.class.getCanonicalName());
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#reinitialise()
	 */
	public void			reinitialise() throws Exception
	{
		if (this.snop != null && this.snop.connected()) {
			this.getOwner().doPortDisconnection(this.snop.getPortURI());
			this.snop.unpublishPort();
			this.snop.destroyPort();
			this.snop = null;
		}
		if (this.pnop != null && this.pnop.connected()) {
			this.getOwner().doPortDisconnection(this.pnop.getPortURI());
			this.pnop.unpublishPort();
			this.pnop.destroyPort();
			this.pnop = null;
		}
		if (this.mop != null && this.mop.connected()) {
			this.getOwner().doPortDisconnection(this.mop.getPortURI());
			this.mop.unpublishPort();
			this.mop.destroyPort();
			this.mop = null;
		}
	}


	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getTimeOfStart()
	 */
	public Time			getTimeOfStart() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().getTimeOfStart();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationEndTime()
	 */
	public Time			getSimulationEndTime() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().getSimulationEndTime();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#setSimulationRunParameters(java.util.Map)
	 */
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception
	{
		this.getRootModel().setSimulationRunParameters(simParams);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#doStandAloneSimulation(double, double)
	 */
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().
						doStandAloneSimulation(startTime, simulationDuration);
		if (this.snop != null && this.snop.connected()) {
			this.snop.acceptSimulationReport(this.getFinalReport());
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#startRTSimulation(long, double, double)
	 */
	public abstract void	startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#isSimulationRunning()
	 */
	public boolean		isSimulationRunning() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().isSimulationRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#stopSimulation()
	 */
	public void			stopSimulation() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().stopSimulation();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#finaliseSimulation()
	 */
	public void			finaliseSimulation() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().finaliseSimulation();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getFinalReport()
	 */
	public SimulationReportI	getFinalReport() throws Exception
	{
		return this.getRootSimulator().getFinalReport();
	}

	// -------------------------------------------------------------------------
	// Methods from ModelCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getURI()
	 */
	public String		getURI() throws Exception
	{
		return this.getRootModel().getURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#hasURI(java.lang.String)
	 */
	public boolean		hasURI(String uri) throws Exception
	{
		return this.getRootModel().getURI().equals(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getSimulatedTimeUnit()
	 */
	public TimeUnit		getSimulatedTimeUnit() throws Exception
	{
		return this.getRootModel().getSimulatedTimeUnit();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isParentSet()
	 */
	public boolean		isParentModelSet() throws Exception
	{
		return this.getRootModel().isParentSet();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#setParent(java.lang.String)
	 */
	public void			setParentModel(String inboundPortURI) throws Exception
	{
		assert	inboundPortURI != null && !inboundPortURI.isEmpty() :
				new PreconditionException(
						"inboundPortURI != null && !inboundPortURI.isEmpty()");

		if (this.mop == null) {
			this.mop = new ModelOutboundPort(this.getOwner());
			this.mop.publishPort();
		} else {
			assert	this.mop.isPublished() && !this.mop.connected();
		}
		this.getOwner().doPortConnection(
								this.mop.getPortURI(),
								inboundPortURI,
								ModelConnector.class.getCanonicalName());
		this.rootModel.setParent(
				new ComponentCoupledModelWrapper(mop, null, null));
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isParentSet()
	 */
	public boolean		isParentSimulatorSet() throws Exception
	{
		return this.getRootSimulator().isParentSet();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#setParent(java.lang.String)
	 */
	public void			setParentSimulator(String inboundPortURI) throws Exception
	{
		assert	inboundPortURI != null && !inboundPortURI.isEmpty() :
				new PreconditionException(
						"inboundPortURI != null && !inboundPortURI.isEmpty()");

		if (this.pnop == null) {
			this.pnop = new ParentNotificationOutboundPort(this.getOwner());
			this.pnop.publishPort();
		} else {
			assert	this.pnop.isPublished() && !this.pnop.connected();
		}
		this.getOwner().doPortConnection(
						this.pnop.getPortURI(),
						inboundPortURI,
						ParentNotificationConnector.class.getCanonicalName());

		this.getRootSimulator().setParent(
								new ComponentCoordinatorWrapper(this.pnop));
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getParentURI()
	 */
	public String		getParentURI() throws Exception
	{
		return this.getRootModel().getParentURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isAtomic()
	 */
	public abstract boolean		isAtomic() throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isRoot()
	 */
	public boolean		isRoot() throws Exception
	{
		return this.getRootModel().isRoot();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#closed()
	 */
	public boolean		closed() throws Exception
	{
		return this.getRootModel().closed();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isImportedEventType(java.lang.Class)
	 */
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return this.getRootModel().isImportedEventType(ec);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getImportedEventTypes()
	 */
	public Class<? extends EventI>[]	getImportedEventTypes()
	throws Exception
	{
		return this.getRootModel().getImportedEventTypes();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isExportedEventType(java.lang.Class)
	 */
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	throws Exception
	{
		return this.getRootModel().isExportedEventType(ec);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getExportedEventTypes()
	 */
	public Class<? extends EventI>[]	getExportedEventTypes()
	throws Exception
	{
		return this.getRootModel().getExportedEventTypes();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSource(java.lang.Class)
	 */
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		) throws Exception
	{
		return this.getRootModel().getEventAtomicSource(ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSinks(java.lang.Class)
	 */
	public abstract Set<CallableEventAtomicSink>	getEventAtomicSinks(
			Class<? extends EventI> ce
			) throws Exception;

	public boolean		isDescendant(String uri) throws Exception
	{
		if (!this.getRootModel().isAtomic()) {
			return ((CoupledModelI)this.getRootModel()).isDescendant(uri);
		} else {
			return false;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	public abstract void	addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	this.getRootSimulator() != null;
		assert	modelURI != null;
		assert	this.getURI().equals(modelURI) ||
					this.isDescendant(modelURI);
		assert	ce != null;

		return this.getRootModel().getInfluencees(modelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	this.getURI().equals(modelURI) ||
									this.isDescendant(modelURI) :
				new PreconditionException(
						"getURI().equals(modelURI) || "
						+ "isDescendentModel(modelURI)");
		assert	destinationModelURIs != null && !destinationModelURIs.isEmpty() :
				new PreconditionException(
						"destinationModelURIs != null && "
						+ "!destinationModelURIs.isEmpty()");
		assert	destinationModelURIs.stream().allMatch(
										uri -> uri != null && !uri.isEmpty()) :
				new PreconditionException(
						"destinationModelURIs.stream().allMatch("
						+ "uri -> uri != null && !uri.isEmpty())");
		assert	ce != null : new PreconditionException("ce != null");

		return this.getRootModel().areInfluencedThrough(
										modelURI, destinationModelURIs, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	this.getURI().equals(modelURI) ||
								this.isDescendant(modelURI) :
				new PreconditionException(
						"getSimulator().getURI().equals(modelURI) || "
						+ "getSimulator().isDescendentModel(modelURI)");
		assert	destinationModelURI != null && !destinationModelURI.isEmpty() :
				new PreconditionException(
						"destinationModelURI != null && "
						+ "!destinationModelURI.isEmpty()");
		assert	ce != null : new PreconditionException("ce != null");
	
		return this.getRootModel().isInfluencedThrough(
									modelURI, destinationModelURI, ce);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isTIOA()
	 */
	public boolean		isTIOA() throws Exception
	{
		// TODO: A model at the top level of a component is always a TIOA.
		assert	getRootModel().isTIOA() :
				new PreconditionException("getSimulator().isTIOA()");

		return this.getRootModel().isTIOA();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#modelAsString(java.lang.String)
	 */
	public String		modelAsString(String indent) throws Exception
	{
		return "ASP:" + this.getRootModel().modelAsString(indent);
	}

	// -------------------------------------------------------------------------
	// Methods from SimulatorCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().initialiseSimulation(simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().initialiseSimulation(startTime,
													 simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isSimulationInitialised()
	 */
	public boolean		isSimulationInitialised() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().isSimulationInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#useFixpointInitialiseVariables()
	 */
	public boolean		useFixpointInitialiseVariables() throws Exception
	{
		assert	getRootModel() != null :
				new PreconditionException("getRootModel() != null");

		return this.getRootModel().useFixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesTimeInitialised()
	 */
	public boolean		allModelVariablesTimeInitialised() throws Exception
	{
		assert	getRootModel() != null :
				new PreconditionException("getRootModel() != null");

		return this.getRootModel().allModelVariablesTimeInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#allModelVariablesInitialised()
	 */
	public boolean		allModelVariablesInitialised() throws Exception
	{
		assert	getRootModel() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootModel().allModelVariablesInitialised();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#fixpointInitialiseVariables()
	 */
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	throws Exception
	{
		assert	getRootModel() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootModel().fixpointInitialiseVariables();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#internalEventStep()
	 */
	public void			internalEventStep() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().internalEventStep();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			externalEventStep(Duration elapsedTime)
	throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().externalEventStep(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	public void			produceOutput(Time current) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().produceOutput(current);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	public void			endSimulation(Time current) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().endSimulation(current);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfLastEvent()
	 */
	public Time			getTimeOfLastEvent() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().getTimeOfLastEvent();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getTimeOfNextEvent()
	 */
	public Time			getTimeOfNextEvent() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().getTimeOfNextEvent();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#getNextTimeAdvance()
	 */
	public Duration		getNextTimeAdvance() throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		return this.getRootSimulator().getNextTimeAdvance();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			showCurrentState(String indent, Duration elapsedTime)
	throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");

		this.getRootSimulator().showCurrentState(indent, elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#simulatorAsString()
	 */
	public String		simulatorAsString() throws Exception
	{
		return "ASP:" + this.getRootSimulator().simulatorAsString();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#toggleDebugMode()
	 */
	public void			toggleDebugMode() throws Exception
	{
		this.getRootSimulator().toggleDebugMode();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#isDebugModeOn()
	 */
	public boolean		isDebugModeOn() throws Exception
	{
		return this.getRootSimulator().isDebugModeOn();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#setDebugLevel(int)
	 */
	public void			setDebugLevel(int debugLevel) throws Exception
	{
		this.getRootSimulator().setDebugLevel(debugLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#hasDebugLevel(int)
	 */
	public boolean		hasDebugLevel(int debugLevel) throws Exception
	{
		return this.getRootSimulator().hasDebugLevel(debugLevel);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulatorCI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception
	{
		this.getRootSimulator().showCurrentState(indent, elapsedTime);
	}
}
// -----------------------------------------------------------------------------
