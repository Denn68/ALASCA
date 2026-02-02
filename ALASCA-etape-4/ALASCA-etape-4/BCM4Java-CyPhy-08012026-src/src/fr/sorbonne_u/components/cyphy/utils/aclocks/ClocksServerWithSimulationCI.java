package fr.sorbonne_u.components.cyphy.utils.aclocks;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import java.time.Instant;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>ClocksServerWithSimulationCI</code> extends
 * the {@code ClocksServerCI} with methods dedicated to the simulation
 * capabilities of clocks.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-11-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ClocksServerWithSimulationCI
extends		ClocksServerCI
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The enumeration <code>TypeOfClock</code> defines the types of clocks
	 * that can be stored and retrieved from the clocks server.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * As clocks may be created on the fly, when testing a clock URI for the
	 * type of the corresponding clock, it may still be unknown.
	 * </p>
	 * 
	 * <p>Created on : 2024-11-19</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static enum	TypeOfClock
	{
		/** standard acceleration clock.									*/
		PLAIN,
		/** accelerated clock with simulation capabilities.					*/
		SIMULATION,
		/** not created yet, hence type unknown.							*/
		UKNOWN;
	}

	// -------------------------------------------------------------------------
	// Signature and default methods
	// -------------------------------------------------------------------------

	/**
	 * return true if {@code clockURI} corresponds to a clock with simulation
	 * capabilities, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param clockURI		URI of a previously created clock.
	 * @return				the type of clock corresponding to {@code clockURI}.
	 * @throws Exception	<i>to do</i>.
	 */
	public TypeOfClock	getTypeOfClock(String clockURI) throws Exception;

	/**
	 * return the clock with simulation capabilities associated with
	 * {@code clockURI} of null if none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code TypeOfClock.SIMULATION.equals(getTypeOfClock(clockURI))}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param clockURI		URI of a previously created clock.
	 * @return				the clock associated with {@code clockURI} of null if none.
	 * @throws Exception	<i>to do</i>.
	 */
	public AcceleratedAndSimulationClock	getClockWithSimulation(
		String clockURI
		) throws Exception;

	/**
	 * create an accelerated and simulation clock with the given parameters
	 * associated with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code unixEpochStartTimeInNanos > 0}
	 * pre	{@code startInstant != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * pre	{@code simulatedStartTime != null}
	 * pre	{@code simulatedDuration != null && simulatedDuration.getSimulatedDuration() > 0.0}
	 * pre	{@code simulatedStartTime.getTimeUnit().equals(simulatedDuration.getTimeUnit())}
	 * post	{@code return != null}
	 * post	{@code return.getStartEpochNanos() == unixEpochStartTimeInNanos}
	 * post	{@code return.getStartInstant().equals(startInstant)}
	 * post	{@code return.getAccelerationFactor() == accelerationFactor}
	 * post	{@code getClock(clockURI).equals(return)}
	 * </pre>
	 *
	 * @param clockURI					URI designating the created clock.
	 * @param unixEpochStartTimeInNanos	start time in Unix epoch time expressed in nanoseconds.
	 * @param startInstant				start instant to be aligned with the {@code unixEpochStartTimeInNanos}.
	 * @param accelerationFactor		acceleration factor to be applied between elapsed time as {@code Instant} and elapsed time as Unix epoch time in nanoseconds.
	 * @param simulatedStartTime		start time of the simulation, in simulated logical time.
	 * @param simulatedDuration			duration of the simulation, in simulated time.
	 * @return							the newly created clock.
	 * @throws Exception				<i>to do</i>.
	 */
	public AcceleratedAndSimulationClock	createClockWithSimulation(
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant	startInstant,
		double accelerationFactor,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws Exception;
}
// -----------------------------------------------------------------------------
