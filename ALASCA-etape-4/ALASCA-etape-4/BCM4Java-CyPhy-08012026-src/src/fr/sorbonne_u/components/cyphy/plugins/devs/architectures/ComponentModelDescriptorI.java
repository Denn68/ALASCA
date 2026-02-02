package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

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

import fr.sorbonne_u.devs_simulation.models.architectures.ModelDescriptorI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ComponentModelDescriptorI</code> declares the methods
 * that are common to the component atomic and coupled model descriptors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-06-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ComponentModelDescriptorI
extends		ModelDescriptorI
{
	/**
	 * return the URI of the reflection inbound port of the component holding
	 * the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the reflection inbound port of the component holding the model.
	 */
	public String		getComponentReflectionInboundPortURI() ;
//
//	/**
//	 * compose the subtree of the given simulation architecture for which the
//	 * model described by this descriptor.
//	 * 
//	 * <p><strong>Contract</strong></p>
//	 * 
//	 * <pre>
//	 * pre	{@code architecture != null}
//	 * pre	{@code modelInboundPortURI != null && !modelInboundPortURI.isEmpty()}
//	 * pre	{@code parentNotificationInboundPortURI != null && !parentNotificationInboundPortURI.isEmpty()}
//	 * pre	{@code isCreatorComponentSet()}
//	 * post	{@code true}	// no postcondition.
//	 * </pre>
//	 *
//	 * @param architecture						the whole simulation architecture description.
//	 * @param parentModelInboundPortURI			URI of the model inbound port of the creator component.
//	 * @param parentNotificationInboundPortURI	URI of the parent notification inbound port of the creator component.
//	 * @return									a reference on the coupled model resulting from the composition or a proxy.
//	 * @throws Exception 		<i>to do</i>.
//	 */
//	public CoupledModelI		compose(
//		ComponentModelArchitectureI architecture,
//		String parentModelInboundPortURI,
//		String parentNotificationInboundPortURI
//		) throws Exception ;
}
// -----------------------------------------------------------------------------
