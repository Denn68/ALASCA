package fr.sorbonne_u.devs_simulation.models;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardAtomicModelFactory</code> defines an atomic model
 * factory that will create models from their Java representation class,
 * assuming that this model class declares a constructor with the three
 * standard parameters: the URI of the model, the simulation time unit and
 * the simulation engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * To create a model, this factory uses reflection to get the standard
 * constructor with the three mentioned parameters and then invokes it.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code atomicModelClass != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code DEBUG_LEVEL >= 0}
 * </pre>
 * 
 * <p>Created on : 2018-09-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardAtomicModelFactory
extends		AbstractAtomicModelFactory
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	/** the debug level to be set on the new model.							*/
	public static int								DEBUG_LEVEL = 0;
	/** class defining the atomic model, to be instantiated.				*/
	protected final Class<? extends AtomicModel>	atomicModelClass;

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
		StandardAtomicModelFactory instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.atomicModelClass != null,
					StandardAtomicModelFactory.class,
					instance,
					"atomicModelClass != null");
		ret &= AbstractAtomicModelFactory.implementationInvariants(instance);
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
		StandardAtomicModelFactory instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					DEBUG_LEVEL >= 0,
					StandardAtomicModelFactory.class,
					instance,
					"DEBUG_LEVEL >= 0");
		ret &= AbstractAtomicModelFactory.invariants(instance);
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a standard atomic model factory for the given Java class
	 * representing the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicModelClass != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param atomicModelClass		the class defining the model.
	 */
	public				StandardAtomicModelFactory(
		Class<? extends AtomicModel> atomicModelClass
		)
	{
		assert	atomicModelClass != null :
				new NeoSim4JavaException("Precondition violation: "
								   + "atomicModelClass != null");

		this.atomicModelClass = atomicModelClass;

		// Invariants checking
		assert	StandardAtomicModelFactory.implementationInvariants(this) :
				new NeoSim4JavaException(
						"StandardAtomicModelFactory.implementationInvariants(this)");
		assert	StandardAtomicModelFactory.invariants(this) :
				new NeoSim4JavaException(
						"StandardAtomicModelFactory.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#getModelClass()
	 */
	@Override
	public Class<? extends AtomicModel>	getModelClass()
	{
		return this.atomicModelClass;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelFactoryI#createAtomicModel()
	 */
	@Override
	public AtomicModel	createAtomicModel()
	{
		assert	modelParametersSet() :
				new NeoSim4JavaException("Precondition violation: "
								   + "modelParametersSet()");

		try {
			Constructor<?> ct =
				this.atomicModelClass.getConstructor(
										new Class<?>[]{String.class,
													   TimeUnit.class,
													   AtomicSimulatorI.class});
			ct.setAccessible(true);
			AtomicModel ret =
				(AtomicModel) ct.newInstance(this.modelURI,
											 this.simulatedTimeUnit,
											 this.createAtomicEngine());
			ret.getSimulationEngine().setDebugLevel(DEBUG_LEVEL);
			return ret;
		} catch (NoSuchMethodException | SecurityException |
				 InstantiationException | IllegalAccessException |
				 IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
