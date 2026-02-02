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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardCoupledModelFactory</code> defines a coupled model
 * factory that will create models from their Java representation class,
 * assuming that this model class declares a constructor with the 
 * standard parameters as they appear in the constructor of the class
 * <code>CoupledModel</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code coupledModelClass != null}
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
public class			StandardCoupledModelFactory
extends		AbstractCoupledModelFactory
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	/** the debug level to be set on the new model.							*/
	public static int								DEBUG_LEVEL = 0;
	/** class defining the coupled model, to be instantiated.				*/
	protected final Class<? extends CoupledModelI>	coupledModelClass;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a coupled model factory from the given coupled model class.
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
	public				StandardCoupledModelFactory(
		Class<? extends CoupledModelI> coupledModelClass
		)
	{
		assert	coupledModelClass != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "coupledModelClass != null");

		this.coupledModelClass = coupledModelClass;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelFactoryI#createCoupledModel()
	 */
	@Override
	public CoupledModel	createCoupledModel()
	{
		assert	this.modelParametersSet() :
				new NeoSim4JavaException("Precondition violation: "
												+ "modelParametersSet()");

		Constructor<?> ct;
		CoupledModel ret;
		try {
			ct = this.coupledModelClass.getConstructor(
						new Class<?>[] {String.class,
										TimeUnit.class,
										CoordinatorI.class,
										(new ModelI[]{}).getClass(),
										Map.class,
										Map.class,
										Map.class,
										Map.class,
										Map.class,
										Map.class});
			
			ret = (CoupledModel) ct.newInstance(
						this.modelURI,
						this.models[0].getSimulatedTimeUnit(),
						this.createCoordinationEngine(),
						this.models,
						this.imported,
						this.reexported,
						this.connections,
						this.importedVars,
						this.reexportedVars,
						this.bindings);
		} catch (NoSuchMethodException e) {
			if (this.importedVars.size() == 0 &&
							this.reexportedVars.size() == 0 &&
											this.bindings.size() == 0) {
				try {
					ct = this.coupledModelClass.getConstructor(
							new Class<?>[] {String.class,
										TimeUnit.class,
										CoordinatorI.class,
										(new ModelI[] {}).getClass(),
										Map.class,
										Map.class,
										Map.class});
					ret = (CoupledModel) ct.newInstance(
									this.modelURI,
									this.models[0].getSimulatedTimeUnit(),
									this.createCoordinationEngine(),
									this.models,
									this.imported,
									this.reexported,
									this.connections);
				} catch (InstantiationException  | IllegalAccessException |
						 IllegalArgumentException | InvocationTargetException e1) {
					throw new RuntimeException(e1) ;
				} catch (NoSuchMethodException e1) {
					throw new RuntimeException(
								"The coupled model class " +
								this.coupledModelClass.getCanonicalName() +
								" does not provide the required constructor.",
								e1) ;
				} catch (SecurityException e1) {
					throw new RuntimeException(
								"The coupled model class " +
								this.coupledModelClass.getCanonicalName() +
								" does not provide the reflective access right "
								+ "to the required constructor.", e1) ;
				}
			} else {
				throw new RuntimeException(e);
			}
		} catch (InstantiationException | IllegalAccessException |
				 IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e) ;
		}
		ret.getSimulationEngine().setDebugLevel(DEBUG_LEVEL);
		return ret;
	}
}
// -----------------------------------------------------------------------------
