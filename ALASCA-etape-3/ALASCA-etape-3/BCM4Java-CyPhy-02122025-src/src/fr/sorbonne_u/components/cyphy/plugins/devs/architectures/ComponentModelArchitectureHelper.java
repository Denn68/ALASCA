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

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionConnector;
import fr.sorbonne_u.components.cyphy.connections.CyPhyReflectionOutboundPort;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulationManagementConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulationManagementOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulatorConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.SimulatorOutboundPort;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentArchitectureHelper</code> defines code that is
 * used by component simulation architectures to create and interconnect
 * simulation models held by BCM components.
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
 * <p>Created on : 2019-06-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	ComponentModelArchitectureHelper
{
	public static boolean	DEBUG = false;

	/**
	 * create, connect, install {@code simulationPlugin} if not null and
	 * return a cyphy reflection outbound port to the component having
	 * {@code componentReflectionInboundPortURI} as reflection inbound
	 * port URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code portCreator != null}
	 * pre	{@code portCreator.isRequiredInterface(CyPhyReflectionCI.class)}
	 * pre	{@code d != null}
	 * post	{@code ret != null && ret.connected()}
	 * </pre>
	 *
	 * @param portCreator	component executing this method.
	 * @param d				component model descriptor of the model that will be held by the component to connect to.
	 * @return				return a cyphy reflection outbound port to {@code portCreator}.
	 */
	public static CyPhyReflectionOutboundPort	connectComponent(
		AbstractComponent portCreator,
		ComponentModelDescriptorI d
		)
	{
		assert	portCreator != null :
				new PreconditionException("portCreator != null");
		assert	portCreator.isRequiredInterface(CyPhyReflectionCI.class) :
				new PreconditionException(
						"portCreator.isRequiredInterface("
						+ "CyPhyReflectionCI.class)");
		assert	d != null : new PreconditionException("d != null");

		CoordinatorPlugin simulationPlugin = null;
		if (d instanceof CoupledModelDescriptor) {
			assert	d instanceof ComponentCoupledModelDescriptorI;
			simulationPlugin = ((ComponentCoupledModelDescriptorI)d).
													createCoordinatorPlugin();
		}

		try {
			CyPhyReflectionOutboundPort rop =
								new CyPhyReflectionOutboundPort(portCreator);
			rop.publishPort();
			portCreator.doPortConnection(
							rop.getPortURI(),
							d.getComponentReflectionInboundPortURI(),
							CyPhyReflectionConnector.class.getCanonicalName());
			if (simulationPlugin != null) {
				rop.installPlugin(simulationPlugin);
			}
			assert	rop != null && rop.connected() :
					new PostconditionException("ret != null && ret.connected()");

			return rop;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * create, connect and return the simulation management outbound port for
	 * the component <code>creator</code> (which executes this code); the
	 * returned port is connected with the simulation management inbound port
	 * of the component to which the reflection outbound port <code>rop</code>
	 * is itself connected.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code portCreator != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code rop != null && rop.connected()}
	 * post	{@code ret != null && ret.connected()}
	 * </pre>
	 *
	 * @param portCreator		port creator component executing this code.
	 * @param modelURI			URI of the model held by the creator component.
	 * @param rop				reflection outbound port connected to component holding the model <code>modelURI</code>.
	 * @return					the simulation management outbound port held by <code>portCreator</code>
	 */
	public static SimulationManagementOutboundPort	connectManagementPort(
		AbstractComponent portCreator,
		String modelURI,
		CyPhyReflectionOutboundPort rop
		)
	{
		if (DEBUG) {
			System.out.println(
					"ComponentModelArchitectureHelper>>connectManagementPort 1 "
														+ modelURI);
		}

		assert	portCreator != null :
				new PreconditionException("portCreator != null");
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		try {
			assert	rop != null && rop.connected() :
					new PreconditionException("rop != null && rop.connected()");
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}

		SimulationManagementOutboundPort smop = null;
		try {
			String smipURI =
					rop.getSimulationManagementInboundPortURI(modelURI);
			assert	smipURI != null;
			smop = new SimulationManagementOutboundPort(portCreator);
			smop.publishPort();
			portCreator.doPortConnection(
						smop.getPortURI(),
						smipURI,
						SimulationManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (DEBUG) {
			System.out.println(
					"ComponentModelArchitectureHelper>>connectManagementPort 2 "
														+ modelURI);
		}

		try {
			assert	smop != null && smop.connected() :
					new PostconditionException("ret != null && ret.connected()");
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return smop;
	}

	/**
	 * create, connect and return the outbound port connected to the model
	 * of the submodel component holding {@code modelURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code creator != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code rop != null && rop.connected()}
	 * post	{@code ret != null && ret.connected()}
	 * </pre>
	 *
	 * @param creator	component performing the connection.
	 * @param modelURI	URI of the model to which the connection is made.
	 * @param rop		reflection outbound port connected to component holding the model <code>modelURI</code>.
	 * @return			the outbound port connected to the model of the component.
	 */
	public static ModelOutboundPort	connectModelPort(
		AbstractComponent creator,
		String modelURI,
		CyPhyReflectionOutboundPort rop
		)
	{
		assert	creator != null : new PreconditionException("creator != null");
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		try {
			assert	rop != null && rop.connected() :
					new PreconditionException("rop != null && rop.connected()");
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}

		ModelOutboundPort mop = null;
		try {
			String mipURI = rop.getModelInboundPortURI(modelURI);
			assert	mipURI != null;
			mop = new ModelOutboundPort(creator);
			mop.publishPort();
			creator.doPortConnection(
								mop.getPortURI(),
								mipURI,
								ModelConnector.class.getCanonicalName());

			assert	mop != null && mop.connected() :
					new PostconditionException("ret != null && ret.connected()");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return mop;
	}

	/**
	 * create, connect and return the outbound port connected to the simulator
	 * of the submodel component holding {@code modelURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code portCreator != null}
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code rop != null && rop.connected()}
	 * post	{@code ret != null && ret.connected()}
	 * </pre>
	 *
	 * @param portCreator	component performing the connection.
	 * @param modelURI		URI of the model to which the connection is made.
	 * @param rop			reflection outbound port connected to component holding the model <code>modelURI</code>.
	 * @return				the outbound port connected to the model of the component.
	 */
	public static SimulatorOutboundPort	connectSimulatorPort(
		AbstractComponent portCreator,
		String modelURI,
		CyPhyReflectionOutboundPort rop
		)
	{
		assert	portCreator != null : new PreconditionException("creator != null");
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		try {
			assert	rop != null && rop.connected() :
					new PreconditionException("rop != null && rop.connected()");
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}

		SimulatorOutboundPort sop = null;
		try {
			String sipURI = rop.getSimulatorInboundPortURI(modelURI);
			assert	sipURI != null;
			sop = new SimulatorOutboundPort(portCreator);
			sop.publishPort();
			portCreator.doPortConnection(
								sop.getPortURI(),
								sipURI,
								SimulatorConnector.class.getCanonicalName());

			assert	sop != null && sop.connected() :
						new PostconditionException(
											"ret != null && ret.connected()");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return sop;
	}
}
// -----------------------------------------------------------------------------
