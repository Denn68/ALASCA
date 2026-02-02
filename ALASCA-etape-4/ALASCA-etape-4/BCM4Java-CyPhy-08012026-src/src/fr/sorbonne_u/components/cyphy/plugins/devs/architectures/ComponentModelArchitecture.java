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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentModelArchitecture</code> extends DEVS simulation
 * architectures to include information about the mapping of simulation models
 * to BCM components and provides methods used by the supervisor component to
 * create the system-wide simulator from this architecture onto the component
 * assembly.
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
 * <p>Created on : 2018-06-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentModelArchitecture
extends		Architecture
implements	ComponentModelArchitectureI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	public static boolean		DEBUG = false;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a component model architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * pre	{@code atomicModelDescriptors.values().stream().allMatch(md -> md instanceof ComponentAtomicModelDescriptor || md instanceof RTComponentAtomicModelDescriptor)}
	 * pre	{@code coupledModelDescriptors.values().stream().allMatch(md -> md instanceof ComponentCoupledModelDescriptor || md instanceof RTComponentCoupledModelDescriptor)}
	 * post	{@code !isComplete() || ComponentModelArchitecture.checkInvariant(this)}
	 * </pre>
	 *
	 * @param architectureURI			URI of the architecture.
	 * @param rootModelURI				URI of the root model in the architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their atomic model descriptors.
	 * @param coupledModelDescriptors	map from coupled model URIs to their coupled model descriptors.
	 * @param simulationTimeUnit		time unit for the simulation clocks.
	 * @throws Exception				<i>to do</i>.
	 */
	public				ComponentModelArchitecture(
		String architectureURI,
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		) throws Exception
	{
		super(architectureURI, rootModelURI, atomicModelDescriptors,
			  coupledModelDescriptors, simulationTimeUnit);

		// Preconditions
		assert	architectureURI != null :
				new PreconditionException("architectureURI != null");
		assert	atomicModelDescriptors.values().stream().allMatch(
						md -> md instanceof ComponentAtomicModelDescriptor ||
							md instanceof RTComponentAtomicModelDescriptor) :
				new PreconditionException(
						"atomicModelDescriptors.values().stream().allMatch("
						+ "md -> md instanceof ComponentAtomicModelDescriptor "
						+ "|| md instanceof RTComponentAtomicModelDescriptor)");
		assert	coupledModelDescriptors.values().stream().allMatch(
						md -> md instanceof ComponentCoupledModelDescriptor ||
							md instanceof RTComponentCoupledModelDescriptor) :
				new PreconditionException(
						"coupledModelDescriptors.values().stream().allMatch("
						+ "md -> md instanceof ComponentCoupledModelDescriptor "
						+ "|| md instanceof RTComponentCoupledModelDescriptor)");

		assert	!isComplete() ||
						ComponentModelArchitecture.checkInvariant(this) :
				new PostconditionException(
						"!isComplete() || "
						+ "ComponentModelArchitecture.checkInvariant(this)");
	}

	/**
	 * check the invariant related to the present class only (a similar method
	 * is defined on the superclass).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code ma != null}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param ma	component model architecture to be checked.
	 * @return		true if the invariant is satisfied.
	 */
	public static boolean	checkInvariant(
		ComponentModelArchitecture ma
		)
	{
		assert	ma != null : new PreconditionException("ma != null");

		boolean invariant = true;

		// TODO
//		for (Entry<String,String> e : ma.modelsParent.entrySet()) {
//			String modelURI = e.getKey();
//			System.out.println(
//				"ComponentBasedModelArchitecture#"
//								+ "checkCompleteArchitectureInvariant "
//				+ modelURI + " " + ma.modelURIs2componentURIs);
//			String childComponentURI =
//							ma.modelURIs2componentURIs.get(modelURI);
//			String parentComponentURI =
//							ma.modelURIs2componentURIs.get(e.getValue());
//			if (!childComponentURI.equals(parentComponentURI)) {
//				invariant &=
//					!ma.isEngineCreationMode(
//								modelURI,
//								SimulationEngineCreationMode.NO_ENGINE);
//			}
//		}

		return invariant;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#getModelDescriptor(java.lang.String)
	 */
	@Override
	public ComponentModelDescriptorI	getModelDescriptor(String uri)
	{
		return (ComponentModelDescriptorI) super.getModelDescriptor(uri);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI#getReflectionInboundPortURI(java.lang.String)
	 */
	@Override
	public String		getReflectionInboundPortURI(String modelURI)
	{
		assert	modelURI != null && isModel(modelURI) :
				new PreconditionException(
						"modelURI != null && isModel(modelURI)");

		return this.getModelDescriptor(modelURI).
									getComponentReflectionInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#constructSimulator()
	 */
	@Override
	public SimulatorI	constructSimulator()
	{
		throw new RuntimeException(
						"the method constructSimulator() must not "
						+ "be called on ComponentModelArchitecture");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#constructSimulator(java.lang.String)
	 */
	@Override
	public SimulatorI constructSimulator(String modelURI)
	{
		throw new RuntimeException(
				"the method constructSimulator(java.lang.String) must not "
				+ "be called on ComponentModelArchitecture");
	}
}
// -----------------------------------------------------------------------------
