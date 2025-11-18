package fr.sorbonne_u.devs_simulation.models;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

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

import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.RTModelFactoryI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardRTCoupledModelFactory</code> implements a standard
 * model factory for real time coupled models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p>Created on : 2020-11-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardRTCoupledModelFactory
extends		StandardCoupledModelFactory
implements	RTModelFactoryI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** the acceleration factor used to control the pace of the simulation
	 *  when converting the simulated time to real time.					*/
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
	protected static boolean	implementationInvariants(
		StandardRTCoupledModelFactory instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.accelerationFactor > 0.0,
					StandardRTCoupledModelFactory.class,
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
	protected static boolean	invariants(
		StandardRTCoupledModelFactory instance
		)
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
	 * create a real time coupled model factory from the given coupled model
	 * class.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code coupledModelClass != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param coupledModelClass	the class defining the model.
	 */
	public				StandardRTCoupledModelFactory(
		Class<? extends CoupledModelI> coupledModelClass
		)
	{
		super(coupledModelClass);
		this.accelerationFactor = AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR;

		// Invariants checking
		assert	StandardRTCoupledModelFactory.implementationInvariants(this) :
				new NeoSim4JavaException(
						"StandardRTCoupledModelFactory.implementationInvariants(this)");
		assert	StandardRTCoupledModelFactory.invariants(this) :
				new NeoSim4JavaException(
						"StandardRTCoupledModelFactory.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.RTModelFactoryI#setAccelerationFactor(double)
	 */
	@Override
	public RTModelFactoryI	setAccelerationFactor(double accelerationFactor)
	{
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "accelerationFactor > 0.0");
		this.accelerationFactor = accelerationFactor;

		// Invariants checking
		assert	StandardRTCoupledModelFactory.implementationInvariants(this) :
				new NeoSim4JavaException(
						"StandardRTCoupledModelFactory.implementationInvariants(this)");
		assert	StandardRTCoupledModelFactory.invariants(this) :
				new NeoSim4JavaException(
						"StandardRTCoupledModelFactory.invariants(this)");

		return this;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AbstractCoupledModelFactory#createCoordinationEngine()
	 */
	@Override
	public CoordinatorI		createCoordinationEngine()
	{
		return new CoordinationRTEngine();
	}
}
// -----------------------------------------------------------------------------
