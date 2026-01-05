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
import java.util.concurrent.CompletableFuture;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI.TypeOfClock;
import fr.sorbonne_u.components.reflection.interfaces.ReflectionCI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

// -----------------------------------------------------------------------------
/**
 * The class <code>ClocksServerWithSimulation</code>
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
 * <p>Created on : 2024-11-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@OfferedInterfaces(offered={ClocksServerWithSimulationCI.class})
//-----------------------------------------------------------------------------
public class			ClocksServerWithSimulation
extends		ClocksServer
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected ClocksServerWithSimulationInboundPort	simInboundPort;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
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
		ClocksServerWithSimulation instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
//		ret &= InvariantChecking.checkGlassBoxInvariant(invariantExpression, ClocksServerWithSimulation.class, instance,
//				"");
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
		ClocksServerWithSimulation instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
//		ret &= InvariantChecking.checkBlackBoxInvariant(invariantExpression, ClocksServerWithSimulation.class, instance,
//				"");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the clock server component which inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} has the URI
	 * {@code STANDARD_INBOUNDPORT_URI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception <i>to do</i>.
	 */
	protected			ClocksServerWithSimulation() throws Exception
	{
		this(AbstractPort.generatePortURI(ReflectionCI.class),
			 STANDARD_INBOUNDPORT_URI);
	}

	/**
	 * create the clock server component with an inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} having the URI
	 * {@code inboundPortURI} .
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param inboundPortURI	URI of the inbound port offering the {@code ClockServerCI} component interface.
	 * @throws Exception 		<i>to do</i>.
	 */
	protected			ClocksServerWithSimulation(
		String inboundPortURI
		) throws Exception
	{
		super(inboundPortURI);

		// Invariant checking
		assert	ClocksServerWithSimulation.implementationInvariants(this) :
				new ImplementationInvariantException(
						"ClocksServerWithSimulation."
						+ "implementationInvariants(this)");
		assert	ClocksServerWithSimulation.invariants(this) :
				new InvariantException(
						"ClocksServerWithSimulation.invariants(this)");
	}

	/**
	 * create the clock server component which reflection inbound port URI is
	 * {@code reflectionInboundPortURI} and which inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} has the URI
	 * {@code inboundPortURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port.
	 * @param inboundPortURI			URI of the inbound port offering the {@code ClockServerCI} component interface.
	 * @throws Exception 				<i>to do</i>.
	 */
	protected			ClocksServerWithSimulation(
		String reflectionInboundPortURI,
		String inboundPortURI
		) throws Exception
	{
		super(reflectionInboundPortURI, inboundPortURI);

		// Invariant checking
		assert	ClocksServerWithSimulation.implementationInvariants(this) :
				new ImplementationInvariantException(
						"ClocksServerWithSimulation."
						+ "implementationInvariants(this)");
		assert	ClocksServerWithSimulation.invariants(this) :
				new InvariantException(
						"ClocksServerWithSimulation.invariants(this)");
	}

	/**
	 * create the clock server component which inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} has the URI
	 * {@code STANDARD_INBOUNDPORT_URI}; the created clock server component will
	 * also have a first clock with the URI {@code clockURI} created from the
	 * given parameters.
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
	 * post	{@code getClock(clockURI).getStartEpochNanos() == unixEpochStartTimeInNanos}
	 * post	{@code getClock(clockURI).getStartInstant().equals(startInstant)}
	 * post	{@code getClock(clockURI).getAccelerationFactor() == accelerationFactor}
	 * </pre>
	 *
	 * @param clockURI					URI designating the first created clock.
	 * @param unixEpochStartTimeInNanos	start time in Unix epoch time expressed in nanoseconds.
	 * @param startInstant				start instant to be aligned with the {@code unixEpochStartTimeInNanos}.
	 * @param accelerationFactor		acceleration factor to be applied between elapsed time as {@code Instant} and elapsed time as Unix epoch time in nanoseconds.
	 * @param simulatedStartTime		start time of the simulation, in simulated logical time.
	 * @param simulatedDuration			duration of the simulation, in simulated time.
	 * @throws Exception				<i>to do</i>.
	 */
	protected			ClocksServerWithSimulation(
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant startInstant,
		double accelerationFactor,
		long realTimeDelayToStartSimulationInMillis,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws Exception
	{
		this(AbstractPort.generatePortURI(ReflectionCI.class),
			 STANDARD_INBOUNDPORT_URI, clockURI, unixEpochStartTimeInNanos,
			 startInstant, accelerationFactor, simulatedStartTime,
			 simulatedDuration);
	}

	/**
	 * create the clock server component which inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} has the
	 * URI {@code inboundPortURI} ; the created clock server component will also
	 * have a first clock with the URI {@code clockURI} created from the given
	 * parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code unixEpochStartTimeInNanos > 0}
	 * pre	{@code startInstant != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * pre	{@code simulatedStartTime != null}
	 * pre	{@code simulatedDuration != null && simulatedDuration.getSimulatedDuration() > 0.0}
	 * pre	{@code simulatedStartTime.getTimeUnit().equals(simulatedDuration.getTimeUnit())}
	 * post	{@code getClock(clockURI) != null}
	 * post	{@code getClock(clockURI).getStartEpochNanos() == unixEpochStartTimeInNanos}
	 * post	{@code getClock(clockURI).getStartInstant().equals(startInstant)}
	 * post	{@code getClock(clockURI).getAccelerationFactor() == accelerationFactor}
	 * </pre>
	 *
	 * @param inboundPortURI			URI of the inbound port offering the {@code ClockServerCI} component interface.
	 * @param clockURI					URI designating the created clock.
	 * @param unixEpochStartTimeInNanos	start time in Unix epoch time expressed in nanoseconds.
	 * @param startInstant				start instant to be aligned with the {@code unixEpochStartTimeInNanos}.
	 * @param accelerationFactor		acceleration factor to be applied between elapsed time as {@code Instant} and elapsed time as Unix epoch time in nanoseconds.
	 * @param simulatedStartTime		start time of the simulation, in simulated logical time.
	 * @param simulatedDuration			duration of the simulation, in simulated time.
	 * @throws Exception 				<i>to do</i>.
	 */
	protected			ClocksServerWithSimulation(
		String inboundPortURI,
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant startInstant,
		double accelerationFactor,
		long realTimeDelayToStartSimulationInMillis,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws Exception
	{
		this(AbstractPort.generatePortURI(ReflectionCI.class),
			 inboundPortURI, clockURI, unixEpochStartTimeInNanos, startInstant,
			 accelerationFactor, simulatedStartTime, simulatedDuration);
	}

	/**
	 * create the clock server component which reflection inbound port URI is
	 * {@code reflectionInboundPortURI} and which inbound port offering the
	 * component interface {@code ClocksServerWithSimulationCI} has the URI
	 * {@code inboundPortURI} ; the created clock server component will also
	 * have a first clock with the URI {@code clockURI} created from the given
	 * parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code reflectionInboundPortURI != null && !reflectionInboundPortURI.isEmpty()}
	 * pre	{@code inboundPortURI != null && !inboundPortURI.isEmpty()}
	 * pre	{@code clockURI != null && !clockURI.isEmpty()}
	 * pre	{@code unixEpochStartTimeInNanos > 0}
	 * pre	{@code startInstant != null}
	 * pre	{@code accelerationFactor > 0.0}
	 * pre	{@code simulatedStartTime != null}
	 * pre	{@code simulatedDuration != null && simulatedDuration.getSimulatedDuration() > 0.0}
	 * pre	{@code simulatedStartTime.getTimeUnit().equals(simulatedDuration.getTimeUnit())}
	 * post	{@code getClock(clockURI) != null}
	 * post	{@code getClock(clockURI).getStartEpochNanos() == unixEpochStartTimeInNanos}
	 * post	{@code getClock(clockURI).getStartInstant().equals(startInstant)}
	 * post	{@code getClock(clockURI).getAccelerationFactor() == accelerationFactor}
	 * </pre>
	 *
	 * @param reflectionInboundPortURI	URI of the reflection inbound port.
	 * @param inboundPortURI			URI of the inbound port offering the {@code ClockServerCI} component interface.
	 * @param clockURI					URI designating the created clock.
	 * @param unixEpochStartTimeInNanos	start time in Unix epoch time expressed in nanoseconds.
	 * @param startInstant				start instant to be aligned with the {@code unixEpochStartTimeInNanos}.
	 * @param accelerationFactor		acceleration factor to be applied between elapsed time as {@code Instant} and elapsed time as Unix epoch time in nanoseconds.
	 * @param simulatedStartTime		start time of the simulation, in simulated logical time.
	 * @param simulatedDuration			duration of the simulation, in simulated time.
	 * @throws Exception 				<i>to do</i>.
	 */
	protected			ClocksServerWithSimulation(
		String reflectionInboundPortURI,
		String inboundPortURI,
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant startInstant,
		double accelerationFactor,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws Exception
	{
		super(reflectionInboundPortURI, inboundPortURI);

		assert	inboundPortURI != null && !inboundPortURI.isEmpty() :
				new PreconditionException(
						"inboundPortURI != null && !inboundPortURI.isEmpty()");
		assert	clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"clockURI != null && !clockURI.isEmpty()");
		assert	unixEpochStartTimeInNanos > 0 :
				new PreconditionException("unixEpochStartTimeInNanos > 0");
		assert	startInstant != null :
				new PreconditionException("startInstant != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");
		assert	simulatedStartTime != null :
				new PreconditionException("simulatedStartTime != null");
		assert	simulatedDuration != null &&
							simulatedDuration.getSimulatedDuration() > 0.0 :
				new PreconditionException(
						"simulatedDuration != null && "
						+ "simulatedDuration.getSimulatedDuration() > 0.0");
		assert	simulatedStartTime.getTimeUnit().equals(
										simulatedDuration.getTimeUnit()) :
				new PreconditionException(
						"simulatedStartTime.getTimeUnit().equals("
						+ "simulatedDuration.getTimeUnit())");

		if (VERBOSE) {
			this.traceMessage("Creating the clock " + clockURI + ".\n");
		}
		this.createClockWithSimulation(clockURI,
									   unixEpochStartTimeInNanos,
									   startInstant,
									   accelerationFactor,
									   simulatedStartTime,
									   simulatedDuration);

		assert	getClock(clockURI) != null :
				new PostconditionException("getClock(clockURI) != null");
		assert	getClock(clockURI).getStartEpochNanos() ==
													unixEpochStartTimeInNanos :
				new PostconditionException(
						"getClock(clockURI).getStartEpochNanos() "
						+ "== unixEpochStartTimeInNanos");
		assert	getClock(clockURI).getStartInstant().equals(startInstant) :
				new PostconditionException(
						"getClock(clockURI).getStartInstant()."
						+ "equals(startInstant)");
		assert	getClock(clockURI).getAccelerationFactor() ==
													accelerationFactor :
				new PostconditionException(
						"getClock(clockURI).getAccelerationFactor() == "
						+ "accelerationFactor");

		// Invariant checking
		assert	ClocksServerWithSimulation.implementationInvariants(this) :
				new ImplementationInvariantException(
						"ClocksServerWithSimulation."
						+ "implementationInvariants(this)");
		assert	ClocksServerWithSimulation.invariants(this) :
				new InvariantException(
						"ClocksServerWithSimulation.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * initialise the clock server component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		initialise() throws Exception
	{
		this.inboundPort =
				new ClocksServerWithSimulationInboundPort(this.inboundPortURI,
														  this);
		this.inboundPort.publishPort();

		if (VERBOSE) {
			this.tracer.get().setTitle("Clock Server with Simulation component");
			this.tracer.get().setRelativePosition(X_RELATIVE_POSITION,
												  Y_RELATIVE_POSITION);
			this.toggleTracing();		
		}
	}

	// -------------------------------------------------------------------------
	// Component services
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
	public TypeOfClock	getTypeOfClock(String clockURI) throws Exception
	{
		if (VERBOSE) {
			this.traceMessage("Getting the clock " + clockURI + ".\n");
		}

		this.clocksLock.lock();
		CompletableFuture<AcceleratedClock> f = null;
		try {
			f = this.clocks.get(clockURI);
		} finally {
			this.clocksLock.unlock();
		}

		if (f == null) {
			return TypeOfClock.UKNOWN;
		} else {
			AcceleratedClock ac = f.getNow(null);
			if (ac == null) {
				return TypeOfClock.UKNOWN;
			} else if (ac instanceof AcceleratedAndSimulationClock) {
				return TypeOfClock.SIMULATION;
			} else {
				assert	ac instanceof AcceleratedClock;
				return TypeOfClock.PLAIN;
			}
		}
	}

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
	 * @return				the clock with simulation capabilities associated with {@code clockURI} of null if none.
	 * @throws Exception	<i>to do</i>.
	 */
	public AcceleratedAndSimulationClock	getClockWithSimulation(
		String clockURI
		) throws Exception
	{
		assert	clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"clockURI != null && !clockURI.isEmpty()");
		assert	TypeOfClock.SIMULATION.equals(this.getTypeOfClock(clockURI)) :
				new PreconditionException(
						"TypeOfClock.SIMULATION.equals("
						+ "getTypeOfClock(clockURI))");

		return (AcceleratedAndSimulationClock) this.getClock(clockURI);
	}

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
		) throws Exception
	{
		if (VERBOSE) {
			this.traceMessage(
					"Verifying preconditions before creating the clock " +
					clockURI + ".\n");
		}

		assert	clockURI != null && !clockURI.isEmpty() :
				new PreconditionException(
						"clockURI != null && !clockURI.isEmpty()");
		assert	unixEpochStartTimeInNanos > 0 :
				new PreconditionException("unixEpochStartTimeInNanos > 0");
		assert	startInstant != null :
				new PreconditionException("startInstant != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");
		assert	simulatedStartTime != null :
				new PreconditionException("simulatedStartTime != null");
		assert	simulatedDuration != null &&
								simulatedDuration.getSimulatedDuration() > 0.0 :
				new PreconditionException(
						"simulatedDuration != null && "
						+ "simulatedDuration.getSimulatedDuration() > 0.0");
		assert	simulatedStartTime.getTimeUnit().equals(
											simulatedDuration.getTimeUnit()) :
				new PreconditionException(
						"simulatedStartTime.getTimeUnit().equals("
						+ "simulatedDuration.getTimeUnit())");

		if (VERBOSE) {
			this.traceMessage("Creating the clock " + clockURI + ".\n");
		}

		AcceleratedAndSimulationClock ret =
				new AcceleratedAndSimulationClock(
									clockURI,
									unixEpochStartTimeInNanos,
									startInstant,
									accelerationFactor,
									simulatedStartTime,
									simulatedDuration);

		if (VERBOSE) {
			this.traceMessage("Clock " + clockURI + " created.\n");
		}

		this.clocksLock.lock();
		try {
			CompletableFuture<AcceleratedClock> f = this.clocks.get(clockURI);
			if (VERBOSE) {
				this.traceMessage("Completable future is " + f + ".\n");
			}
			if (f == null)  {
				f = new CompletableFuture<AcceleratedClock>();
				this.clocks.put(clockURI, f);
			}
			if (VERBOSE) {
				this.traceMessage("Completing f with " + ret + ".\n");
			}
			f.complete(ret);
		} finally {
			this.clocksLock.unlock();
		}

		if (VERBOSE) {
			this.traceMessage(
					"Verifying postconditions before returning the clock " +
					clockURI + ".\n");
		}

		assert	ret.getStartEpochNanos() == unixEpochStartTimeInNanos :
				new PostconditionException(
						"return.getStartEpochNanos() == "
												+ "unixEpochStartTimeInNanos");
		assert	ret.getStartInstant().equals(startInstant) :
				new PostconditionException(
						"return.getStartInstant().equals(startInstant)");
		assert	ret.getAccelerationFactor() == accelerationFactor :
				new PostconditionException(
						"return.getAccelerationFactor() == accelerationFactor");
		assert	getClock(clockURI).equals(ret) :
				new PostconditionException("getClock(clockURI).equals(return)");

		if (VERBOSE) {
			this.traceMessage(
					"Returning the clock " + clockURI + ".\n");
		}

		return ret;
	}
}
// -----------------------------------------------------------------------------
