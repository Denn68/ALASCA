package fr.sorbonne_u.components.cyphy.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
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

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.reflection.ports.ReflectionOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>CyPhyReflectionOutboundPort</code> defines the outbound port
 * requiring the component interface <code>CyPhyReflectionCI</code>.
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
 * <p>Created on : 2019-12-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CyPhyReflectionOutboundPort
extends		ReflectionOutboundPort
implements	CyPhyReflectionCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of reflection outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception	<i>to do</i>.
	 */
	public				CyPhyReflectionOutboundPort(ComponentI owner)
	throws Exception
	{
		super(CyPhyReflectionCI.class, owner);
	}

	/**
	 * create an instance of reflection outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri			URI of this port.
	 * @param owner			owner component.
	 * @throws Exception	<i>to do</i>.
	 */
	public				CyPhyReflectionOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, CyPhyReflectionCI.class, owner);
	}

	/**
	 * create an instance of a subclass of reflection outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param implementedInterface	component interface implemented by the port.
	 * @param owner					owner component.
	 * @throws Exception			<i>to do</i>.
	 */
	public				CyPhyReflectionOutboundPort(
		Class<? extends RequiredCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);
	}

	/**
	 * create an instance of a subclass of reflection outbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri					URI of this port.
	 * @param implementedInterface	component interface implemented by the port.
	 * @param owner					owner component.
	 * @throws Exception			<i>to do</i>.
	 */
	public				CyPhyReflectionOutboundPort(
		String uri,
		Class<? extends RequiredCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isLocalSimulator(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulator(String uri)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).isLocalSimulator(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isLocalSimulatorInstalled(java.lang.String)
	 */
	@Override
	public boolean		isLocalSimulatorInstalled(String uri) throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
											isLocalSimulatorInstalled(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isSimulationArchitecture(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitecture(String architectureURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
										isSimulationArchitecture(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isSimulationArchitectureInstalled(java.lang.String)
	 */
	@Override
	public boolean		isSimulationArchitectureInstalled(String architectureURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
								isSimulationArchitectureInstalled(architectureURI);
	}

//	/**
//	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntercessionCI#addSimulationArchitecture(java.lang.String, java.lang.String)
//	 */
//	@Override
//	public void			addSimulationArchitecture(
//		String architectureURI,
//		String localSimulatorURI
//		) throws Exception
//	{
//		((CyPhyReflectionCI)this.getConnector()).
//				addSimulationArchitecture(architectureURI, localSimulatorURI);
//	}
//
//	/**
//	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntercessionCI#removeSimulationArchitecture(java.lang.String)
//	 */
//	@Override
//	public void			removeSimulationArchitecture(String architectureURI)
//	throws Exception
//	{
//		((CyPhyReflectionCI)this.getConnector()).
//								removeSimulationArchitecture(architectureURI);
//	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isCoordinatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isCoordinatorComponent(String architectureURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).isCoordinatorComponent(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#isAtomicSimulatorComponent(java.lang.String)
	 */
	@Override
	public boolean		isAtomicSimulatorComponent(String architectureURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).isAtomicSimulatorComponent(architectureURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#getSimulationManagementInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulationManagementInboundPortURI(
		String modelURI
		) throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
					getSimulationManagementInboundPortURI(modelURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#getModelInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getModelInboundPortURI(String modelURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
											getModelInboundPortURI(modelURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.CyPhyIntrospectionCI#getSimulatorInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getSimulatorInboundPortURI(String modelURI)
	throws Exception
	{
		return ((CyPhyReflectionCI)this.getConnector()).
									getSimulatorInboundPortURI(modelURI);
	}
}
// -----------------------------------------------------------------------------
