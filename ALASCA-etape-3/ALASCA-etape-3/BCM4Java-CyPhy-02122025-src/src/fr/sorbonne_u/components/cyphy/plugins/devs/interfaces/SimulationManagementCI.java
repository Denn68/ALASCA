package fr.sorbonne_u.components.cyphy.plugins.devs.interfaces;

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

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import java.util.Map;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>SimulationManagementCI</code> declares the
 * methods used by simulation engines to manage simulation runs.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code  true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SimulationManagementCI
extends		OfferedCI,
			RequiredCI
{
	/**
	 * create the simulator for the receiving component, returning true if the
	 * simulator has been correctly constructed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isSimulatorSet()}
	 * post	{@code isSimulatorSet()}
	 * </pre>
	 *
	 * @param modelURI		URI of the model to be held by this component or at the root of the model subtree held by this component.
	 * @param architecture	architecture description of the complete simulator.
	 * @return				true if the simulator has been correctly constructed, false otherwise.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception;

	/**
	 * return the URI of the simulator management inbound port for this plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the URI of the simulator management inbound port for this plug-in.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getSimulationManagementInboundPortURI()
	throws Exception;

	/**
	 * return the URI of the model inbound port for this plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the URI of the model inbound port for this plug-in.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getModelInboundPortURI() throws Exception;

	/**
	 * return the URI of the simulator inbound port for this plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the URI of the simulator inbound port for this plug-in.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getSimulatorInboundPortURI() throws Exception;

	/**
	 * connect this simulation engine to the supervisor to be able to notify it
	 * the simulation report and passing it the URIs of its inbound ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code supervisionInboundPortURI != null && !supervisionInboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param supervisionInboundPortURI URI of the inbound port of the supervisor.
	 * @throws Exception				<i>to do</i>.
	 */
	public void			connectSupervision(
		String supervisionInboundPortURI
		) throws Exception;

	/**
	 * reinitialise the simulation architectures such that every component
	 * participating in the simulation are put back in the state they were
	 * before composing the simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			reinitialise() throws Exception;

	/**
	 * return the time at which the simulation started.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the time at which the simulation starts.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	public Time			getTimeOfStart() throws Exception;

	/**
	 * return the time at which the simulation ends.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the time at which the simulation ends.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	public Time			getSimulationEndTime() throws Exception;

	/**
	 * set the simulation parameters for a simulation run.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * This method is the same as
	 * fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#setSimulationRunParameters(java.util.Map).
	 * It could be factored out in a separate interface, but maybe a too
	 * complicated solution for just one method.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && simParams.size() > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param simParams						map from parameters names to their values.
	 * @throws MissingRunParameterException if an expected run parameter does not appear in {@code simParams} (including {@code simParams} being {@code null}).
	 * @throws Exception					<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception;

	/**
	 * do a stand alone simulation lasting <code>simulatedTimeDuration</code>
	 * and starting at <code>startTime</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationStartTime >= 0.0}
	 * pre	{@code simulationStartTime  < Double.POSITIVE_INFINITY}
	 * pre	{@code simulationDuration > 0.0}
	 * pre	{@code simulationDuration < Double.POSITIVE_INFINITY}
	 * post	{@code !isSimulationRunning()}
	 * </pre>
	 *
	 * @param simulationStartTime	time at which the simulation starts interpreted in the model simulation time.
	 * @param simulationDuration	duration of the simulation interpreted in the model simulation time.
	 * @throws Exception			<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	public void			doStandAloneSimulation(
		double simulationStartTime,
		double simulationDuration
		) throws Exception;

	/**
	 * at real time <code>realTimeStart</code>, start a real time
	 * collaborative simulation with the simulation clock stating at
	 * <code>startTime</code> and lasting <code>simulatedTimeDuration</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code realTimeStart > System.currentTimeMillis()}
	 * pre	{@code realTimeStart < Long.MAX_VALUE}
	 * pre	{@code simulationStartTime >= 0.0}
	 * pre	{@code simulationStartTime  < Double.POSITIVE_INFINITY}
	 * pre	{@code simulationDuration > 0}
	 * pre	{@code simulationDuration <= Double.POSITIVE_INFINITY}
	 * pre	{@code isRTSchedulerSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param realTimeOfStart		time of start for the simulation, in real time, synchronising all simulation engines.
	 * @param simulationStartTime	time at which the simulation starts interpreted in the model simulation time.
	 * @param simulationDuration	duration of the simulation interpreted in the model simulation time.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startRTSimulation(long, double, double)
	 */
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception;

	/**
	 * return true if a simulation run is currently executing.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationInitialised()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if a simulation run is currently executing.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	public boolean		isSimulationRunning() throws Exception;

	/**
	 * interrupt the current simulation and trigger the sending of the
	 * simulation report to the client if appropriately connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationRunning()}
	 * post	{@code !isSimulationRunning()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	public void			stopSimulation() throws Exception;

	/**
	 * finalise the simulation, disposing all resources and reinitialising the
	 * models and simulators so that a new simulation run can be undertaken.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isSimulationRunning()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#finaliseSimulation()
	 */
	public void			finaliseSimulation() throws Exception;

	/**
	 * return the simulation report of the last simulation run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the simulation report of the last simulation run.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	public SimulationReportI	getFinalReport() throws Exception;
}
// -----------------------------------------------------------------------------
