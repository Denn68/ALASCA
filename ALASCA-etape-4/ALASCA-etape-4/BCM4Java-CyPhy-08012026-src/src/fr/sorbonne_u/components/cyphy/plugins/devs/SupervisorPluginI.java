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

import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SupervisorNotificationI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>SupervisorPluginI</code> declares the core services of
 * the DEVS simulation supervisor plug-in.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface gathers the different interfaces a supervisor plug-in
 * must implement to provide the behaviours required by this role in a DEVS
 * simulation context:
 * </p>
 * <ul>
 * <li><code>SupervisorPluginManagementI</code> declares methods used by
 *   other components to make the supervisor component create simulators
 *   from simulation architecture descriptions and perform simulation
 *   runs and campaigns.</li>
 * <li><code>SupervisorPluginManagementI</code> declares methods used from
 *   the supervisor component in order to manage simulation runs by calling
 *   the simulators on components holding them.</li>
 * <li><code>SupervisorNotificationI</code> declares methods used by simulators
 *   in components to call back the supervisor (essentially to return the
 *   simulation reports at the end of simulation runs.</li>
 * </ul>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-07-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SupervisorPluginI
extends		PluginI,
			SimulationManagementI,
			SupervisorNotificationI
{
	/**
	 * return true if the supervisor component is connected to the component
	 * holding the root model of the simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the supervisor component is connected to the component holding the root model of the simulation architecture.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isRootComponentConnected() throws Exception;

	/**
	 * return true if the simulator has been constructed and set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return				true if the simulator has been constructed and set.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isSimulatorCreated() throws Exception;

	/**
	 * create the simulation architecture given the description already
	 * set in the plug-in.
	 * 
	 * <p><strong>Description</strong></p>
	 * 
	 * Main entry point to install a simulation architecture from the supervisor
	 * component. The architecture description must have been set priori to the
	 * call. The method will first connect the components participating in the
	 * simulation architecture (using the method
	 * <code>connectRootSimulatorComponent</code> and then will compose their
	 * simulation models using the method <code>compose</code> defined by the
	 * architecture descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isRootComponentConnected()}
	 * pre	{@code !isSimulatorCreated()}
	 * post	{@code isRootComponentConnected()}
	 * post	{@code isSimulatorCreated()}
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	public void			constructSimulator() throws Exception;

	/**
	 * set a new component-based simulation architecture to be instantiated and
	 * supervised.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isRootComponentConnected()}
	 * pre	{@code isSimulatorCreated()}
	 * pre	{@code architecture != null}
	 * post	{@code isRootComponentConnected()}
	 * post	{@code isSimulatorCreated()}
	 * </pre>
	 *
	 * @param architecture	the new simulation architecture to be instantiated and supervised.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			resetArchitecture(
		ComponentModelArchitectureI architecture
		) throws Exception;
}
// -----------------------------------------------------------------------------
