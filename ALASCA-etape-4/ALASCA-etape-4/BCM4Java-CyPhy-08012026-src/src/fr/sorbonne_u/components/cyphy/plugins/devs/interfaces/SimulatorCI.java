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

import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>SimulatorCI</code> declares the core behaviour
 * of DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This component interface is based on the corresponding Java interface
 * <code>SimulatorI</code> defined by the DEVS simulation library to declare
 * the methods used by coordination engines to implement the DEVS simulation
 * protocol and some other library-dependent associated methods.
 * </p>
 * <p>
 * This component interface is not made extending (in the Java sense) the
 * corresponding Java interface first because RMI forces to add exceptions
 * thrown by the RMI protocol but also because not all methods in the Java
 * interface are needed when calling simulators through BCM4Java component
 * connections. Especially, the methods in the interface
 * {@code SimulationManagementI} are treated in the component interface
 * {@code SimulationManagementCI} to avoid requiring in simulation supervisors
 * the full simulator interface but only the simulation management one.
 * </p>
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
public interface		SimulatorCI
extends		OfferedCI,
			RequiredCI
{
	/**
	 * return true if the parent simulation engine of this engine is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the parent simulation engine of this engine is set.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isParentSet() throws Exception;

	/**
	 * set the parent simulation engine of this engine to {@code c}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isParentSet()}
	 * pre	{@code c != null}
	 * post	{@code isParentSet()}
	 * </pre>
	 *
	 * @param parentNotificationInboundPortURI	UIR of the parent notification inbound port to connect to the component holding the parent coupled model.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setParent(String parentNotificationInboundPortURI)
	throws Exception;

	// -------------------------------------------------------------------------
	// Simulation run management
	// -------------------------------------------------------------------------

	/**
	 * initialise the simulation engine for a run with a time of start set to 0;
	 * for runs that will be stopped by a call to <code>stopSimulation</code>,
	 * the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationDuration != null}
	 * pre	{@code simulationDuration.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationDuration.greaterThan(Duration.zero(getSimulatedTimeUnit()))}
	 * pre	{@code isModelSet()}
	 * pre	{@code !isSimulationInitialised()}
	 * post	{@code isSimulationInitialised()}
	 * </pre>
	 *
	 * @param simulationDuration	duration of the simulation to be launched.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			initialiseSimulation(Duration simulationDuration)
	throws Exception;

	/**
	 * initialise the simulation engine for a run with a time of start set to
	 * the given time; for runs that will be stopped by a call to
	 * <code>stopSimulation</code>, the duration can be set to infinity.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationStartTime != null}
	 * pre	{@code simulationStartTime.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationStartTime.greaterThanOrEqual(Time.zero(getSimulatedTimeUnit()))}
	 * pre	{@code simulationDuration != null}
	 * pre	{@code simulationDuration.getTimeUnit().equals(getSimulatedTimeUnit())}
	 * pre	{@code simulationDuration.greaterThan(Duration.zero(getSimulatedTimeUnit()))}
	 * pre	{@code isModelSet()}
	 * pre	{@code !isSimulationInitialised()}
	 * post	{@code isSimulationInitialised()}
	 * </pre>
	 *
	 * @param simulationStartTime	time at which the simulation must start.
	 * @param simulationDuration	duration of the simulation to be launched.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		) throws Exception;

	/**
	 * return true if the simulation has been initialised for a run.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulation has been initialised.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isSimulationInitialised()
	 */
	public boolean		isSimulationInitialised() throws Exception;

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
	 * return true if the model uses the fixpoint algorithm to initialise its
	 * model variables, false otherwise; when true, the method
	 * {@code fixpointInitialiseVariables} must be implemented by the model, and
	 * when false, it is the method {@code initialiseVariables} that must be.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the model uses the fixpoint algorithm to initialise its model variables, false otherwise.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	public boolean		useFixpointInitialiseVariables() throws Exception;

	/**
	 * return true if all model variables have their associated time
	 * initialised  (relevant for HIOA models).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if all model variables have their associated time initialised.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesTimeInitialised()
	 */
	public boolean		allModelVariablesTimeInitialised() throws Exception;

	/**
	 * return true if all model variables are fully initialised  (relevant for
	 * HIOA models).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if all model variables are fully initialised.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#allModelVariablesInitialised()
	 */
	public boolean		allModelVariablesInitialised() throws Exception;

	/**
	 * initialise the model variables with a more elaborated protocol when
	 * external model variables depend of each others, hence models importing
	 * variables have to wait until these variables are themselves initialised
	 * so this protocol will allow to call repetitively the method until all
	 * variables are initialised without having to respect any order among
	 * models (but models cannot have circular dependencies); this method must
	 * be called after the method {@code initialiseState}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code useFixpointInitialiseVariables()}
	 * pre	{@code allModelVariablesTimeInitialised()}
	 * post	{@code ret.getFirst() >= 0 && ret.getSecond() >= 0}
	 * post	{@code !(ret.getFirst() >= 0 || ret.getSecond() == 0) || allModelVariablesInitialised()}
	 * </pre>
	 *
	 * @return	a pair which first element is the number of newly initialised variables (<i>i.e.</i>) and the second the number of non initialised yet variables.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	public Pair<Integer,Integer>	fixpointInitialiseVariables() throws Exception;

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * process the next internal event by advancing the simulation time to
	 * the forecast time of occurrence of this and then do the transition to
	 * the next state in the corresponding simulation model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationInitialised()}
	 * pre	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getTimeOfLastEvent()))}
	 * post	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getTimeOfLastEvent()))}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	public void			internalEventStep() throws Exception;

	/**
	 * process an external input event at a simulated time which must
	 * correspond to the current value of the global simulated time clock
	 * at the time of the call i.e., the sum of the time of the last
	 * event in this engine and the given elapsed time.
	 * 
	 * <p>
	 * The processing first cancel the previously forecast internal event
	 * if any and then make the transition to a new model state given the
	 * processed external event and then forecast a new internal event.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code elapsedTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param elapsedTime	time elapsed since the last event executed by this engine.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			externalEventStep(Duration elapsedTime)
	throws Exception;

	/**
	 * force the model to forward externals event generated before their
	 * next internal transition step.
	 * 
	 * <p>
	 * The implementation follows the principle of the "peer message
	 * exchanging implementation" of DEVS i.e., the simulators exchanges
	 * directly with each others the exported and imported events without
	 * passing through their parent (and ancestors).
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current		current simulation time at which external events may be output.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	public void			produceOutput(Time current)
	throws Exception;

	/**
	 * return the time of occurrence of the last event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the time of occurrence of the last event.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfLastEvent()
	 */
	public Time			getTimeOfLastEvent() throws Exception;

	/**
	 * return the currently forecast time of occurrence of the next event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the currently forecast time of occurrence of the next event.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfNextEvent()
	 */
	public Time			getTimeOfNextEvent() throws Exception;

	/**
	 * return the duration until the next internal event as previously
	 * computed by <code>timeAdvance()</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				the duration until the next internal event as previously computed by <code>timeAdvance()</code>.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getNextTimeAdvance()
	 */
	public Duration		getNextTimeAdvance() throws Exception;

	/**
	 * terminate the current simulation, doing the necessary catering tasks.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code endTime != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param endTime		time at which the simulation ends.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	public void			endSimulation(Time endTime) throws Exception;

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * activate the lowest debug level or deactivate the debug mode completely.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#toggleDebugMode()
	 */
	public void			toggleDebugMode() throws Exception;

	/**
	 * return true if the debug level is greater than 0.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the debug level is greater than 0.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isDebugModeOn()
	 */
	public boolean		isDebugModeOn() throws Exception;

	/**
	 * true if the current debug level is equal to <code>debugLevel</code>,
	 * false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code debugLevel >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param debugLevel	a debug level to be tested.
	 * @return				true if the current debug level is equal to <code>debugLevel</code>.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#hasDebugLevel(int)
	 */
	public boolean		hasDebugLevel(int debugLevel) throws Exception;

	/**
	 * set the debug level.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code newDebugLevel >= 0}
	 * post	{@code hasDebugLevel(newDebugLevel)}
	 * </pre>
	 *
	 * @param debugLevel	the new debug level of the model
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setDebugLevel(int)
	 */
	public void			setDebugLevel(int debugLevel) throws Exception;

	/**
	 * return the simulator information as a string.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the simulator information as a string.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#simulatorAsString()
	 */
	public String		simulatorAsString() throws Exception;

	/**
	 * print the current state.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		) throws Exception;

	/**
	 * print the content of the current state, without pre- and post-formatting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation of the printing.
	 * @param elapsedTime	elapsed time since the last event execution.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		) throws Exception;
}
// -----------------------------------------------------------------------------
