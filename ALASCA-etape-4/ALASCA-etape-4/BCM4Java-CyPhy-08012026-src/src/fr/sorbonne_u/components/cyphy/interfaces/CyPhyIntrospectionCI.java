package fr.sorbonne_u.components.cyphy.interfaces;

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

import fr.sorbonne_u.components.reflection.interfaces.IntrospectionCI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>CyPhyIntrospectionCI</code> extends the standard
 * BCM introspection interface with methods regarding the cyber-physical
 * part of cy-phy components.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-12-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		CyPhyIntrospectionCI
extends		IntrospectionCI
{
	/**
	 * return true if {@code uri} corresponds to a local simulator defined by
	 * this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI to be tested.
	 * @return				true if {@code uri} corresponds to a local simulator defined in this component.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isLocalSimulator(String uri)
	throws Exception;

	/**
	 * return true if {@code uri} corresponds to a local simulator defined by
	 * this component and has been installed with a plug-in, ready to be used
	 * in a simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri			URI of an existing simulation architecture to be tested for installation in this component.
	 * @return				true if the simulation architecture has been installed on this component.
	 * @throws Exception	<i>to do</i>.
	 */
	public boolean		isLocalSimulatorInstalled(String uri)
	throws Exception;

	/**
	 * return true if {@code architectureURI} corresponds to a simulation
	 * architecture known in this component (<i>i.e.</i>, associated with a
	 * local simulator).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI to be tested.
	 * @return					true if {@code architectureURI} corresponds to a simulation architecture known in this component
	 * @throws Exception		<i>to do</i>.
	 */
	public boolean		isSimulationArchitecture(String architectureURI)
	throws Exception;

	/**
	 * return true if {@code architectureURI} is known and its corresponding
	 * local simulator has been installed on this component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of an existing simulation architecture to be tested for installation in this component.
	 * @return					true if {@code architectureURI} is known and its corresponding local simulator has been installed on this component.
	 * @throws Exception		<i>to do</i>.
	 */
	public boolean		isSimulationArchitectureInstalled(
		String architectureURI
		) throws Exception;

	/**
	 * return true if the component is holding a coupled model for
	 * {@code architectureURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of the simulation architecture for which the component is tested.
	 * @return					true if the component is holding a coupled model for {@code architectureURI}.
	 * @throws Exception		<i>to do</i>.
	 */
	public boolean		isCoordinatorComponent(String architectureURI)
	throws Exception;

	/**
	 * return true if the component is holding an atomic model or a closed
	 * coupled model for {@code architectureURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI	URI of the simulation architecture for which the component is tested.
	 * @return					true if the component is holding an atomic model or a closed coupled model for {@code architectureURI}.
	 * @throws Exception		<i>to do</i>.
	 */
	public boolean		isAtomicSimulatorComponent(String architectureURI)
	throws Exception;

	/**
	 * return the URI of the simulation management inbound port added for the
	 * given root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isSimulationArchitecture(architectureURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the root model for which the URI of the management port is sought.
	 * @return				the URI of the simulation management inbound port added for the given root model.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getSimulationManagementInboundPortURI(
		String modelURI
		) throws Exception;

	/**
	 * return the URI of the model inbound port added by for given root model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isSimulationArchitecture(architectureURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the root model for which the URI of the management port is sought.
	 * @return				the URI of the simulator inbound port added for the given root model.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getModelInboundPortURI(String modelURI)
	throws Exception;

	/**
	 * return the URI of the simulator inbound port added for the given root
	 * model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isSimulationArchitecture(architectureURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of the root model for which the URI of the management port is sought.
	 * @return				the URI of the simulator inbound port added for the given root model.
	 * @throws Exception	<i>to do</i>.
	 */
	public String		getSimulatorInboundPortURI(String modelURI)
	throws Exception;
}
// -----------------------------------------------------------------------------
