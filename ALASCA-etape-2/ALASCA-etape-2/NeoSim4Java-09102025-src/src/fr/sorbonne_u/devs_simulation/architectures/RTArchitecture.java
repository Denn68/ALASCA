package fr.sorbonne_u.devs_simulation.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTArchitecture</code> extends an DEVS simulation architecture
 * by adding its real time simulation property.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code !isComplete() || atomicModelDescriptors.values().stream().allMatch(amd -> amd.isRealTimeModelDescriptor())}
 * invariant	{@code !isComplete() || coupledModelDescriptors.values().stream().allMatch(cmd -> cmd.isRealTimeModelDescriptor())}
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-12-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTArchitecture
extends		Architecture
{
	// -------------------------------------------------------------------------
	// Variables and constants
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the acceleration/deceleration factor that adjusts the pace of
	 *  the simulation upon the real physical time; a value greater than
	 *  1 will force the simulation to run faster in real time than the
	 *  simulated time, while a value under 1 forces it to run slower.		*/
	protected double			accelerationFactor;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(RTArchitecture instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					!instance.isComplete() ||
						instance.atomicModelDescriptors.values().stream().
							allMatch(amd -> amd.isRealTimeModelDescriptor()),
					RTArchitecture.class,
					instance,
					"!isComplete() || atomicModelDescriptors.values().stream()."
					+ "allMatch(amd -> amd.isRealTimeModelDescriptor())");
		ret &= AssertionChecking.checkImplementationInvariant(
					!instance.isComplete() ||
						instance.coupledModelDescriptors.values().stream().
							allMatch(cmd -> cmd.isRealTimeModelDescriptor()),
					RTArchitecture.class,
					instance,
					"!isComplete() || coupledModelDescriptors.values().stream()."
					+ "allMatch(cmd -> cmd.isRealTimeModelDescriptor())");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.accelerationFactor > 0.0,
					RTArchitecture.class,
					instance,
					"accelerationFactor > 0.0");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(RTArchitecture instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a real time model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * pre	{@code rootModelURI != null && !rootModelURI.isEmpty()}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(rootModelURI) || coupledModelDescriptors.containsKey(rootModelURI)}
	 * pre	{@code simulationTimeUnit != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 */
	public				RTArchitecture(
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit
		)
	{
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		this(java.util.UUID.randomUUID().toString(), rootModelURI,
			 atomicModelDescriptors, coupledModelDescriptors,
			 simulationTimeUnit,
			 AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);		
	}

	/**
	 * create a real time model architectural description given the parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
	 * pre	{@code rootModelURI != null && !rootModelURI.isEmpty()}
	 * pre	{@code atomicModelDescriptors != null}
	 * pre	{@code coupledModelDescriptors != null}
	 * pre	{@code atomicModelDescriptors.containsKey(rootModelURI) || coupledModelDescriptors.containsKey(rootModelURI)}
	 * pre	{@code simulationTimeUnit != null}
	 * pre	{@code atomicModelDescriptors.values().stream().allMatch(amd -> amd.isRealTimeModelDescriptor())}
	 * pre	{@code coupledModelDescriptors.values().stream().allMatch(cmd -> cmd.isRealTimeModelDescriptor())}
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param architectureURI			URI of this simulation architecture.
	 * @param rootModelURI				URI of the root model in this architecture.
	 * @param atomicModelDescriptors	map from atomic model URIs to their respective descriptor.
	 * @param coupledModelDescriptors	map from coupled model URIs to their respective descriptor.
	 * @param simulationTimeUnit		time unit used by all simulation clocks in the architecture.
	 * @param accelerationFactor		the acceleration factor between the simulation clock and the real time.
	 */
	public				RTArchitecture(
		String architectureURI,
		String rootModelURI,
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors,
		Map<String, CoupledModelDescriptor> coupledModelDescriptors,
		TimeUnit simulationTimeUnit,
		double accelerationFactor
		)
	{
		super(architectureURI, rootModelURI, atomicModelDescriptors,
			  coupledModelDescriptors, simulationTimeUnit);

		assert	atomicModelDescriptors.values().stream().allMatch(amd ->
									amd.isRealTimeModelDescriptor()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "atomicModelDescriptors.values().stream().allMatch("
						+ "amd -> amd.isRealTimeModelDescriptor())");
		assert	coupledModelDescriptors.values().stream().allMatch(
									cmd -> cmd.isRealTimeModelDescriptor()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelDescriptors.values().stream().allMatch("
						+ "cmd -> cmd.isRealTimeModelDescriptor())");
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
											+ "accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;

		// Invariants checking
		assert	RTArchitecture.implementationInvariants(this) :
				new NeoSim4JavaException(
						"RTArchitecture.implementationInvariants(this)");
		assert	RTArchitecture.invariants(this) :
				new NeoSim4JavaException("RTArchitecture.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.architectures.Architecture#isRealTime()
	 */
	@Override
	public boolean		isRealTime()
	{
		return true;
	}
}
// -----------------------------------------------------------------------------
