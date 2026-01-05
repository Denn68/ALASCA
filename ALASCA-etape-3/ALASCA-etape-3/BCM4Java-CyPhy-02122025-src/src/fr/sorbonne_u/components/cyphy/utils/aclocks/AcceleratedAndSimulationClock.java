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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.models.time.TimeUtils;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

// -----------------------------------------------------------------------------
/**
 * The class <code>AcceleratedAndSimulationClock</code> extends the class
 * <code>AcceleratedClock</code> with the simulation capabilities <i>i.e.</i>,
 * managing a real time simulation starting after the launch of the application
 * to synchronise components actions with the simulated runs.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code realTimeDelayToStartSimulationInNanos > 0}
 * invariant	{@code simulatedStartTime != null}
 * invariant	{@code simulatedDuration != null}
 * invariant	{@code simulatedStartTime.getTimeUnit().equals(simulatedDuration.getTimeUnit())}
 * invariant	{@code simulatedStartTime.getTimeUnit().equals(simulatedTimeUnit)}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code }
 * </pre>
 * 
 * <p>Created on : 2024-11-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AcceleratedAndSimulationClock
extends		AcceleratedClock
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** tolerance applied when comparing double values for equality.		*/
	public static double		TOLERANCE = 1e-8;

	/** start time of the simulation, in simulated logical time.			*/
	protected final Time 		simulatedStartTime;
	/** duration of the simulation, in simulated time.						*/
	protected final Duration	simulatedDuration;
	/** time unit in which simulation times and durations are expressed.	*/
	protected final TimeUnit	simulatedTimeUnit;

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
		AcceleratedAndSimulationClock instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		// most implementation invariants are enforced by making the fields
		// final and testing the constructor preconditions before initialising
		// them
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.simulatedStartTime.getTimeUnit().equals(instance.simulatedTimeUnit),
				AcceleratedAndSimulationClock.class, instance,
				"simulatedStartTime.getTimeUnit().equals(simulatedTimeUnit)");
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
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(
		AcceleratedAndSimulationClock instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					true,
					AcceleratedAndSimulationClock.class, instance,
					"");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an accelerated and simulation clock with the given acceleration
	 * factor and the given delay to start the simulation, taking the start
	 * instant and the start time as now.
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
	 * post	{@code getStartEpochNanos() == unixEpochStartTimeInNanos}
	 * post	{@code getStartInstant().equals(startInstant)}
	 * post	{@code getEndInstant() != null && getEndInstant().isAfter(getStartInstant())}
	 * post	{@code getAccelerationFactor() == accelerationFactor}
	 * </pre>
	 *
	 * @param clockURI					URI attributed to the clock.
	 * @param unixEpochStartTimeInNanos	start time in Unix epoch time in nanoseconds.
	 * @param startInstant				start time as {@code Instant}.
	 * @param accelerationFactor		acceleration factor to be applied.
	 * @param simulatedStartTime		start time of the simulation, in simulated logical time.
	 * @param simulatedDuration			duration of the simulation, in simulated time.
	 * @throws VerboseException			<i>to do</i>.
	 */
	public				AcceleratedAndSimulationClock(
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant	startInstant,
		double accelerationFactor,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws VerboseException
	{
		super(clockURI, unixEpochStartTimeInNanos,
			  AssertionChecking.assertNonNullOrThrow(
				startInstant,
				() -> { return new PreconditionException(
										"startInstant != null");
					  }),
			  AssertionChecking.assertTrueAndReturnOrThrow(
				simulatedDuration != null &&
					  			simulatedDuration.getSimulatedDuration() > 0.0,
				startInstant.plusNanos(TimeUtils.toNanos(simulatedDuration)),
				() -> { return new PreconditionException(
										"simulatedDuration != null && "
										+ "simulatedDuration."
										+ "getSimulatedDuration() > 0.0");
					  		}),
			  accelerationFactor);

		assert	simulatedStartTime != null :
				new PreconditionException("simulatedStartTime != null");
		assert	simulatedStartTime.getTimeUnit().equals(
											simulatedDuration.getTimeUnit()) :
				new PreconditionException(
						"simulatedStartTime.getTimeUnit().equals("
						+ "simulatedDuration.getTimeUnit())");

		this.simulatedStartTime = simulatedStartTime;
		this.simulatedDuration = simulatedDuration;
		this.simulatedTimeUnit = simulatedStartTime.getTimeUnit();
		
		TimeUtils.betweenInDuration(startInstant, endInstant, simulatedTimeUnit);

		// Invariant checking
		assert	AcceleratedAndSimulationClock.implementationInvariants(this) :
				new ImplementationInvariantException(
						"AcceleratedAndSimulationClock."
						+ "implementationInvariants(this)");
		assert	AcceleratedAndSimulationClock.invariants(this) :
				new InvariantException(
						"AcceleratedAndSimulationClock.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return start time of the simulation, in simulated logical time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	start time of the simulation, in simulated logical time.
	 */
	public Time			getSimulatedStartTime()
	{
		return this.simulatedStartTime;
	}

	/**
	 * return the duration of the simulation, in simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the duration of the simulation, in simulated time.
	 */
	public Duration		getSimulatedDuration()
	{
		return this.simulatedDuration;
	}

	/**
	 * return the time unit in which simulation times and durations are expressed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time unit in which simulation times and durations are expressed.
	 */
	public TimeUnit		getSimulatedTimeUnit()
	{
		return this.simulatedTimeUnit;
	}

	/**
	 * return the {@code java.time.Duration} equivalent of {@code d}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	a simulated duration.
	 * @return	the {@code java.time.Duration} equivalent of {@code d}.
	 */
	public java.time.Duration	simulatedDuration2duration(Duration d)
	{
		assert	d != null : new PreconditionException("d != null");
		return java.time.Duration.ofNanos(TimeUtils.toNanos(d));
	}

	/**
	 * return the instant corresponding to {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	a simulated time.
	 * @return	the instant corresponding to {@code t}.
	 */
	public Instant		instantOfSimulatedTime(Time t)
	{
		assert	t != null : new PreconditionException("t != null");
		Duration d = t.subtract(this.getSimulatedStartTime());
		return this.getStartInstant().plusNanos(TimeUtils.toNanos(d));
	}
}
// -----------------------------------------------------------------------------
