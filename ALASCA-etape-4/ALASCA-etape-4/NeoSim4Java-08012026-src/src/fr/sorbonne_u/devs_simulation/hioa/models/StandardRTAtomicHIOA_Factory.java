package fr.sorbonne_u.devs_simulation.hioa.models;

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

import fr.sorbonne_u.devs_simulation.models.StandardRTAtomicModelFactory;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardRTAtomicHIOA_Factory</code> defines a basic factory
 * used when the instantiation protocol of a real time atomic HIOA model class
 * does not modify the one defined in the class <code>AtomicHIOA</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-11-27</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardRTAtomicHIOA_Factory
extends		StandardRTAtomicModelFactory
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a standard real time atomic model factory for the given Java
	 * class representing the HIOA model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicModelClass != null}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param atomicModelClass		the class defining the model.
	 */
	@SuppressWarnings("unused")
	public				StandardRTAtomicHIOA_Factory(
		Class<? extends AtomicHIOA> atomicModelClass
		)
	{
		super(atomicModelClass);

		assert	AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR > 0.0");

		this.accelerationFactor = AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR;
	}
}
// -----------------------------------------------------------------------------
