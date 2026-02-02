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
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionOutboundPort;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureHelper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulationManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SupervisorNotificationInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationCI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>SupervisorPlugin</code> implements the role of DEVS
 * simulation supervision for BCM components as a plug-in.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code architecture != null && architecture.isComplete()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SupervisorPlugin
extends		AbstractPlugin
implements	SupervisorPluginI
{
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	public static boolean	DEBUG = false;

	/** the global simulation architecture associated to the plug-in.		*/
	protected ComponentModelArchitectureI			architecture;
	/** true if the simulator has been created and false otherwise.			*/
	protected boolean								simulatorCreated;
	/** port through which simulators can notify back their report after
	 *  each simulation run.												*/
	protected SupervisorNotificationInboundPort		snip;
	/** reflection inbound port of the component holding the root model
	 *  of the simulation architecture.										*/
	protected CyPhyReflectionOutboundPort			rootComponentRop;
	/** simulation management outbound port of the root model component
	 *  allowing this supervisor component to manage the simulation runs.	*/
	protected SimulationManagementOutboundPort		rootModelSmop;
	/** variable in which the simulation report can be found after each
	 *  simulation run.														*/
	protected SimulationReportI						report;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * create a supervisor plug-in with the given global simulation
	 * architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architecture != null && architecture.isComplete()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architecture	architecture of the simulation model to be created.
	 * @throws Exception	<i>to do.</i>
	 */
	public				SupervisorPlugin(
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		assert	architecture != null && architecture.isComplete() :
				new PreconditionException(
						"architecture != null && architecture.isComplete()");

		this.architecture = architecture;
		this.simulatorCreated = false;
	}

	// -------------------------------------------------------------------------
	// Plug-in generic methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null : new PreconditionException("owner != null");
		assert	getPluginURI() != null :
				new PreconditionException("getPluginURI() != null");

		super.installOn(owner);

		if (!owner.isRequiredInterface(CyPhyReflectionCI.class)) {
			this.addRequiredInterface(CyPhyReflectionCI.class);
		}
		this.addOfferedInterface(SupervisorNotificationCI.class);
		this.addRequiredInterface(SimulationManagementCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise();

		this.snip = new SupervisorNotificationInboundPort(
										this.getOwner(),
										this.getPluginURI(),
										this.getPreferredExecutionServiceURI());
		this.snip.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#isInitialised()
	 */
	@Override
	public boolean		isInitialised()
	{
		return super.isInitialised() && this.snip != null;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.rootComponentRop != null && this.rootComponentRop.connected()) {
			this.getOwner().
						doPortDisconnection(this.rootComponentRop.getPortURI());
		}
		if (this.rootModelSmop != null && this.rootModelSmop.connected()) {
			this.getOwner().
						doPortDisconnection(this.rootModelSmop.getPortURI());
		}
		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.snip.unpublishPort();
		this.snip.destroyPort();
		this.removeOfferedInterface(SupervisorNotificationCI.class);

		if (this.rootComponentRop != null) {
			assert	!this.rootComponentRop.connected();
			this.rootComponentRop.unpublishPort();
			this.rootComponentRop.destroyPort();
			this.rootComponentRop = null;
		}
		this.removeRequiredInterface(CyPhyReflectionCI.class);
		if (this.rootModelSmop != null) {
			assert	!this.rootModelSmop.connected();
			this.rootModelSmop.unpublishPort();
			this.rootModelSmop.destroyPort();
			this.rootModelSmop = null;
		}
 		this.removeRequiredInterface(SimulationManagementCI.class);

		super.uninstall();
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPluginI#isRootComponentConnected()
	 */
	@Override
	public boolean		isRootComponentConnected() throws Exception
	{
		return this.rootComponentRop != null && this.rootComponentRop.connected()
				&& this.rootModelSmop != null && this.rootModelSmop.connected();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPluginI#isSimulatorCreated()
	 */
	@Override
	public boolean		isSimulatorCreated() throws Exception
	{
		return this.simulatorCreated;
	}

	/**
	 * connect the component that will hold the root model of the simulation
	 * architecture through the {@code CyPhyReflectionCI} component interface
	 * and the through the {@code SimulationManagementCI} component interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isRootComponentConnected()}
	 * post	{@code isRootComponentConnected()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		connectRootSimulatorComponent()
	throws Exception
	{
		assert	!this.isRootComponentConnected() :
				new PreconditionException("!isRootComponentConnected()");

		if (this.rootComponentRop == null) {
			this.rootComponentRop =
					ComponentModelArchitectureHelper.
							connectComponent(
									(AbstractComponent) this.getOwner(),
									this.architecture.getModelDescriptor(
									this.architecture.getRootModelURI()));
		}
		this.rootModelSmop =
				ComponentModelArchitectureHelper.connectManagementPort(
						(AbstractComponent) this.getOwner(),
						this.architecture.getRootModelURI(),
						this.rootComponentRop);
		this.rootModelSmop.connectSupervision(this.snip.getPortURI());
		this.logMessage(this.architecture.getRootModelURI() + " connected.");
	}

	/**
	 * disconnect the component that will hold the root model of the simulation
	 * architecture through the {@code CyPhyReflectionCI} component interface
	 * and the through the {@code SimulationManagementCI} component interface.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRootComponentConnected()}
	 * post	{@code !isRootComponentConnected()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		disconnectRootSimulatorComponent() throws Exception
	{
		assert	this.isRootComponentConnected() :
				new PreconditionException("isRootComponentConnected()");

//		this.rootModelSmop.reinitialise();
		this.getOwner().doPortDisconnection(this.rootModelSmop.getPortURI());
		this.rootModelSmop.unpublishPort();
		this.rootModelSmop.destroyPort();
		this.rootModelSmop = null;
		this.getOwner().doPortDisconnection(this.rootComponentRop.getPortURI());
		this.rootComponentRop.unpublishPort();
		this.rootComponentRop.destroyPort();
		this.rootComponentRop = null;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPluginI#constructSimulator()
	 */
	@Override
	public void			constructSimulator() throws Exception
	{
		assert	!isRootComponentConnected() :
				new PreconditionException("!isRootComponentConnected()");
		assert	!isSimulatorCreated() :
				new PreconditionException("!isSimulatorCreated()");

		assert	architecture != null && architecture.isComplete() :
				new AssertionError(
						"architecture != null && architecture.isComplete()");

		this.logMessage("connecting root simulator component...");
		this.connectRootSimulatorComponent();
		assert	this.rootModelSmop != null && this.rootModelSmop.connected() :
				new AssertionError(
						"rootModelSmop != null && rootModelSmop.connected()");
		this.logMessage("...done connecting root simulator component.");

		this.logMessage("constructing simulator...");
		this.getRootSmop().constructSimulator(architecture.getRootModelURI(),
											  architecture);
		this.logMessage("... done constructing simulator.");		
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.SupervisorPluginI#resetArchitecture(fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public void			resetArchitecture(
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		assert	architecture != null :
				new PreconditionException("architecture != null");

		this.rootModelSmop.reinitialise();
		this.rootComponentRop.uninstallPlugin(
										this.architecture.getRootModelURI());
		this.disconnectRootSimulatorComponent();
		this.architecture = architecture;
		this.constructSimulator();
	}

	/**
	 * return the outbound port connected to the root model component, or
	 * null if none is connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRootComponentConnected()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the simulation management reference of the root model, or null if none is connected.
	 */
	protected SimulationManagementCI	getRootSmop()
	{
		try {
			assert	this.isRootComponentConnected() :
					new PreconditionException("isRootComponentConnected()");
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		return this.rootModelSmop;
	}

	// -------------------------------------------------------------------------
	// Plug-in methods from SimulationManagementI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart()
	{
		try {
			return this.getRootSmop().getTimeOfStart();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime()
	{
		try {
			return this.getRootSmop().getSimulationEndTime();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		)
	{
		assert	simParams != null :
				new PreconditionException("simParams != null");

		try {
			this.getRootSmop().setSimulationRunParameters(simParams);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		)
	{
		try {
			this.getRootSmop().doStandAloneSimulation(
												startTime, simulationDuration);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		)
	{
		try {
			this.getRootSmop().startRTSimulation(realTimeOfStart,
												 simulationStartTime,
												 simulationDuration);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning()
	{
		try {
			return this.getRootSmop().isSimulationRunning();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation()
	{
		try {
			this.getRootSmop().stopSimulation();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		try {
			return this.getRootSmop().getFinalReport();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// -------------------------------------------------------------------------
	// Plug-in methods from SimulationManagementCI
	// -------------------------------------------------------------------------

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			finaliseSimulation()
	{
		try {
			this.getRootSmop().finaliseSimulation();
		} catch (Exception e) {
		}
	}

	// -------------------------------------------------------------------------
	// Plug-in methods from SupervisorNotificationI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationI#acceptSimulationReport(fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI)
	 */
	@Override
	public void			acceptSimulationReport(SimulationReportI report)
	{
		// memorise the notified report.
		this.report = report;
		this.logMessage("simulation report received and stored.");
	}
}
// -----------------------------------------------------------------------------
