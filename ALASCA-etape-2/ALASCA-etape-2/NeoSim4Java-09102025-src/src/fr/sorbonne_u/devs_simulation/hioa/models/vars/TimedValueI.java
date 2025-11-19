package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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

import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The interface <code>TimedValue</code> specifies that implementing classes
 * can return an associated {@code Time} object.
 * 
 * <p>Created on : 2021-12-07</p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant    {@code getTime() == null || getTimeUnit().equals(getTime().getTimeUnit())}
 * </pre>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		TimedValueI
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

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
	public static boolean	invariants(TimedValueI instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
				instance.getTime() == null ||
							instance.getTimeUnit().equals(
											instance.getTime().getTimeUnit()),
				TimedValueI.class, 
				instance,
				"getTime() == null || getTimeUnit().equals("
				+ "getTime().getTimeUnit())");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Signatures and default methods
	// -------------------------------------------------------------------------

	/**
	 * return the time associated to the object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the time associated to the object.
	 */
	public Time			getTime();

	/**
	 * return	the time unit used to interpret the time of the value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time unit used to interpret the time of the value.
	 */
	public TimeUnit		getTimeUnit();
}
// -----------------------------------------------------------------------------
