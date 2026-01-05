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

// -----------------------------------------------------------------------------
/**
 * The component interface <code>ParentNotificationCI</code> declares the
 * methods used by child models to notify their parent models about their
 * processing of external events at each simulation step.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This component interface is based on the corresponding Java interface
 * <code>ParentNotificationI</code> defined by the DEVS simulation library
 * to declare the methods used by coordination engines to implement the DEVS
 * simulation protocol and some other library-dependent associated methods.
 * </p>
 * <p>
 * This component interface is not made extending (in the Java sense) the
 * corresponding Java interface first because RMI forces to add exceptions
 * thrown by the RMI protocol.
 * </p>
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
public interface		ParentNotificationCI
extends		OfferedCI,
			RequiredCI
{
	/**
	 * signal a parent coupled model that a submodel {@code modelURI} has
	 * received external events and is waiting its activation to execute them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of a submodel of the receiving parent coupled model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	public void			hasReceivedExternalEvents(String modelURI)
	throws Exception;

	/**
	 * signal a parent coupled model that a submodel {@code modelURI} has
	 * performed external events and has finished its external event step.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI		URI of a submodel of the receiving parent coupled model.
	 * @throws Exception	<i>to do</i>.
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	public void			hasPerformedExternalEvents(String modelURI)
	throws Exception;
}
// -----------------------------------------------------------------------------
