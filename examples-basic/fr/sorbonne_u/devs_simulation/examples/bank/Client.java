package fr.sorbonne_u.devs_simulation.examples.bank;

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
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;

// -----------------------------------------------------------------------------
/**
 * The class <code>Client</code> represents a client to be served at the bank.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class materialises a client at the bank. It is used here merely to
 * store the time of arrival of the client to be able to compute the total time
 * passed in the bank when its service will finish.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code id != null && !id.isEmpty()}
 * invariant	{@code timeOfArrival != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-11-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Client
implements	EventInformationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 		serialVersionUID = 1L;
	protected static final String	PREFIX = "C";
	protected static int			count = 0;

	/** unique identifier.													*/
	protected String				id;
	/** time at which this client arrived at the bank in simulation time.	*/
	protected Time					timeOfArrival;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a client instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfArrival != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param timeOfArrival	time at which this client arrived at the bank in simulation time.
	 */
	public				Client(Time timeOfArrival)
	{
		super();

		assert	timeOfArrival != null;

		this.id = PREFIX + count++;
		this.timeOfArrival = timeOfArrival;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if {@code t} comes after the time of arrival of this client.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	some simulation time.
	 * @return	true if {@code t} comes after the time of arrival of this client.
	 */
	public boolean		afterArrival(Time t)
	{
		assert	t != null;
		return this.timeOfArrival.lessThan(t);
	}

	/**
	 * return the duration between the current simulation time and the time at
	 * which this client arrived at the bank.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null && afterArrival(current)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @return			the duration between the current simulation time and the time at which this client arrived at the bank.
	 */
	public Duration		totalTime(Time current)
	{
		assert	current != null && this.afterArrival(current);
		return current.subtract(this.timeOfArrival);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		return this.id;
	}
}
// -----------------------------------------------------------------------------
