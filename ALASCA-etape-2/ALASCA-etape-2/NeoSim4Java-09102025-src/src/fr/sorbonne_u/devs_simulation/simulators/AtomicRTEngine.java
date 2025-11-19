package fr.sorbonne_u.devs_simulation.simulators;

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

import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor.RTSchedulerProviderFI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicRTEngine</code> implements a simulation engine which
 * performs simulation in real time aka. the simulation clock follows the
 * real physical time, eventually accelerated (or decelerated).
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * For atomic models, the atomic real time engine does almost all the work as
 * the internal and external events are executed by tasks scheduled on the
 * computer (here through Java executor services) using the computer clock.
 * </p>
 * <p>
 * As atomic models execute independently in real time, the way to start a
 * real time simulation for a coupled model is to set a start time in the
 * real physical time (using the preferred scheduling time unit) at which all
 * atomic models will start. For simulations involving many models in a large
 * simulation architectures, it is preferable to set this start time
 * sufficiently in the future to let all models schedule their first internal
 * transition before this start time.
 * </p>
 * <p>
 * The simulation will force the start time in simulated time to coincide with
 * this start time in the real physical time, and then the simulation clock
 * will follow the real physical time (with the acceleration factor taken into
 * account).
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code accelerationFactor > 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code DEFAULT_ACCELERATION_FACTOR > 0.0}
 * invariant	{@code STD_SCHEDULER_PROVIDER != null}
 * invariant	{@code PREFFERED_SCHEDULING_TIME_UNIT != null}
 * invariant	{@code END_TIME_TOLERANCE >= 0}
 * </pre>
 * 
 * <p>Created on : 2020-11-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicRTEngine
extends		AtomicEngine
implements	RTAtomicSimulatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	public static boolean			DEBUG = false;
	/** default acceleration factor, used when it is not set by the user.	*/
	public static final double		DEFAULT_ACCELERATION_FACTOR = 1.0;
	/** the standard real time scheduler provider for a real time atomic
	 *  simulation engine.													*/
	public static final RTSchedulerProviderFI
									STD_SCHEDULER_PROVIDER =
												() -> new StandardRTScheduler();
	/** the scheduling time unit used when scheduling tasks on Java
	 *  executor services.													*/
	public static final TimeUnit	PREFFERED_SCHEDULING_TIME_UNIT =
														TimeUnit.MILLISECONDS;
	/** tolerance on the cutoff time in the preferred scheduling
	 *  time unit.															*/
	public static final long		END_TIME_TOLERANCE = 1000;
	/** tolerance on the computed delay to the next event that can be a
	 *  little bit negative when the next event is supposed to occur
	 *  exactly at the same time as the one that generated it.			 	*/
	public static final long		SCHEDULING_TOLERANCE = 10;

	/** lock used to force one task execution at a time.					*/
	protected final ReentrantLock	eventSerialisationLock;
	/** the real time scheduler used to schedule the event processing
	 *  tasks.																*/
	protected RTSchedulingI			rtScheduler;
	/** a scheduled future protected by atomic reference that allows to
	 *  cancel the task scheduled for the next internal event when an
	 *  external event is received before the execution of the next
	 *  internal event (another internal event is usually planned
	 *  after the external event in the DEVS simulation  protocol).			*/
	protected final AtomicReference<ScheduledFuture<?>>
									nextInternalEventFuture;
	/** the task that is scheduled to execute internal events.				*/
	protected Runnable				internalEventTask;
	/** the acceleration/deceleration factor that adjusts the pace of
	 *  the simulation upon the real physical time; a value greater than
	 *  1 will force the simulation to run faster in real time than the
	 *  simulated time, while a value under 1 forces it to run slower.		*/
	protected double				accelerationFactor;

	/** the real time of start in the preferred scheduling time unit.		*/
	protected long					realTimeOfStart;
	/** a semaphore used to synchronise with the end of the simulation.		*/
	protected Semaphore				simulationEndSynchroniser = null;

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
	protected static boolean	implementationInvariants(AtomicRTEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.accelerationFactor > 0.0,
					AtomicRTEngine.class,
					instance,
					"accelerationFactor > 0.0");
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
					DEFAULT_ACCELERATION_FACTOR > 0.0,
					AtomicRTEngine.class,
					"DEFAULT_ACCELERATION_FACTOR > 0.0");
		ret &= AssertionChecking.checkStaticInvariant(
					STD_SCHEDULER_PROVIDER != null,
					AtomicRTEngine.class,
					"STD_SCHEDULER_PROVIDER != null");
		ret &= AssertionChecking.checkStaticInvariant(
					PREFFERED_SCHEDULING_TIME_UNIT != null,
					AtomicRTEngine.class,
					"PREFFERED_SCHEDULING_TIME_UNIT != null");
		ret &= AssertionChecking.checkStaticInvariant(
					END_TIME_TOLERANCE >= 0,
					AtomicRTEngine.class,
					"END_TIME_TOLERANCE >= 0");
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
	protected static boolean	invariants(AtomicRTEngine instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic real time simulation engine with the given acceleration
	 * factor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	the acceleration/deceleration factor that adjusts the pace of the simulation upon the real physical time.
	 */
	public				AtomicRTEngine(double accelerationFactor)
	{
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "accelerationFactor > 0.0");

		this.eventSerialisationLock = new ReentrantLock(true);
		this.nextInternalEventFuture =
								new AtomicReference<ScheduledFuture<?>>();
		this.initialise(accelerationFactor);
	}

	/**
	 * create an atomic real time simulation engine with the default
	 * acceleration factor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				AtomicRTEngine()
	{
		this(DEFAULT_ACCELERATION_FACTOR);
	}

	/**
	 * initialise the atomic real time simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code accelerationFactor > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param accelerationFactor	for real time engines, the acceleration factor between the simulation clock and the real time.
	 */
	protected void		initialise(double accelerationFactor)
	{
		assert	accelerationFactor > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "accelerationFactor > 0.0");

		this.accelerationFactor = accelerationFactor;		
		final AtomicRTEngine rte = this;
		this.internalEventTask =
				new Runnable() {
					@Override
					public void run() {
						try {
							rte.rtInternalEventTask();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				};

		// Invariant checking
		assert	AtomicRTEngine.implementationInvariants(this) :
				new NeoSim4JavaException(
						"AtomicRTEngine.implementationInvariants(this)");
		assert	AtomicRTEngine.invariants(this) :
				new NeoSim4JavaException("AtomicRTEngine.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isRealTime()
	 */
	@Override
	public boolean		isRealTime()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI#isRTSchedulerSet()
	 */
	@Override
	public boolean		isRTSchedulerSet()
	{
		return (this.rtScheduler != null);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#setSimulationEndSynchroniser(java.util.concurrent.Semaphore)
	 */
	@Override
	public void			setSimulationEndSynchroniser(
		Semaphore simulationEndSynchroniser
		)
	{
		assert	simulationEndSynchroniser == null ||
										!simulationEndSynchroniser.tryAcquire();

		this.simulationEndSynchroniser = simulationEndSynchroniser;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI#setRTScheduler(fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI)
	 */
	@Override
	public void			setRTScheduler(RTSchedulingI scheduler)
	{
		assert	scheduler != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "scheduler != null");
		assert	!scheduler.isShutdown() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!scheduler.isShutdown()");

		this.rtScheduler = scheduler;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI#getRTScheduler()
	 */
	@Override
	public RTSchedulingI	getRTScheduler()
	{
		assert	this.isRTSchedulerSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isRTSchedulerSet()");
		return this.rtScheduler;
	}

	// -------------------------------------------------------------------------
	// Simulation related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		)
	{
		this.eventSerialisationLock.lock();
		try {
			long c = System.currentTimeMillis();
			if (realTimeOfStart <= c) {
				System.out.println(this.getSimulatedModel().getURI() + " " +
						realTimeOfStart + " " + c);
				
			}
			assert	realTimeOfStart > System.currentTimeMillis() :
					new NeoSim4JavaException("Precondition violation: "
							+ "realTimeOfStart > System.currentTimeMillis()");
			assert	simulationStartTime >= 0.0 :
					new NeoSim4JavaException("Precondition violation: "
							+ "simulationStartTime >= 0.0");
			assert	simulationDuration > 0.0 :
					new NeoSim4JavaException("Precondition violation: "
							+ "simulationDuration > 0.0");
			assert	isRTSchedulerSet() :
					new NeoSim4JavaException("Precondition violation: "
							+ "isRTSchedulerSet()");

			this.realTimeOfStart = realTimeOfStart;
			if (this.getSimulatedModel().isRoot()) {
				TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit();
				// initialise the simulation with its start time and its duration
				// in simulated time (with its proper time unit).
				this.initialiseSimulation(new Time(simulationStartTime, tu),
										  new Duration(simulationDuration, tu));
			}

			if (this.hasDebugLevel(1)) {
				this.simulatedModel.logMessage(
								"simulation begins for " +
								this.getSimulatedModel().getURI() + ".");
			}

			// plan the first internal transition
			this.planNextInternalEventTask();

			// for real time simulations, an atomic model that has no planned
			// internal events after some point would not know when to stop,
			// hence we schedule a forced stop in case; it is has been already
			// stop, it will do nothing.
			final AtomicRTEngine rte = this;

			this.rtScheduler.schedule(
					new Runnable() {
						@Override
						public void run() {
							try {
								if (!rte.getSimulatedModel().isRoot()) {
									rte.parent.endSimulation(simulationEndTime);
								}
								rte.endSimulation(simulationEndTime);
								if (simulationEndSynchroniser != null) {
									rte.logMessage(
											"releasing the simulation "
											+ "termination synchroniser.");
									simulationEndSynchroniser.release();
								}
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}					
					},
					this.computeRealTimeDelayToNextEvent(this.simulationEndTime)
									+ AtomicRTEngine.END_TIME_TOLERANCE,
					PREFFERED_SCHEDULING_TIME_UNIT);
		} finally {
			this.eventSerialisationLock.unlock();
		}
	}

	/**
	 * return the delay in real time from now corresponding to the time
	 * <code>t</code> in simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t				the time in simulated time to be converted.
	 * @return				the time converted into a real time delay from now in milliseconds.
	 */
	protected long		computeRealTimeDelayToNextEvent(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");

		double fromStart = t.getSimulatedTime() -
								this.simulationStartTime.getSimulatedTime();

		// in order to be as precise as possible when computing the scheduling
		// delay in the preferred scheduling time unit and avoid drifts by
		// accumulating errors made on each delay, the accelerated delay is
		// computed in double precision from the simulation start time. Then,
		// the accelerated delay is converted in the preferred scheduling time
		// unit in two phases: first it is converted from the simulation time
		// unit to nanoseconds, then it is converted to the preferred scheduling
		// time unit. Finally the result is cast to a long after rounding it.

		long cf1 = TimeUnit.NANOSECONDS.convert(
							1,
							this.getSimulatedModel().getSimulatedTimeUnit());
		long cf2 = TimeUnit.NANOSECONDS.convert(
							1,
							AtomicRTEngine.PREFFERED_SCHEDULING_TIME_UNIT);
		long f = (long) Math.round(
							((fromStart/this.accelerationFactor)*cf1)/cf2);
		long s = this.realTimeOfStart + f;
		long c = System.currentTimeMillis();
		long ret = s-c;
		if (ret < 0 && (-ret > SCHEDULING_TOLERANCE)) {
			this.simulatedModel.logMessage(
						"Warning: negative delay to next event " + ret
													+ " corrected to 0!");
			ret = 0;
		}

//		System.out.println("------------------------------------------------");
//		System.out.println("AtomicRTEngine#computeRealTimeDelayToNextEvent " +
//												this.getURI());
//		System.out.println("from start in simulated time: " + fromStart);
//		System.out.println("delay in scheduling time unit: " + f);
//		System.out.println("------------------------------------------------");

		return ret;
	}

	/**
	 * the task that will perform an internal transition when needed; to be
	 * scheduled on the executor service of the simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isModelSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @throws Exception	<i>to do</i>.
	 */
	protected void		rtInternalEventTask() throws Exception
	{
		this.eventSerialisationLock.lock();
		try {
			assert	this.isModelSet() :
					new NeoSim4JavaException(
							"Precondition violation: isModelSet()");

			this.produceOutput(this.timeOfNextEvent);
			this.internalEventStep();
			this.planNextInternalEventTask();
		} finally {
			this.eventSerialisationLock.unlock();
		}
	}

	/**
	 * plan the next internal event if any and the simulation did not reach
	 * its end time (in simulated time).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		planNextInternalEventTask()
	{
		if (this.isRunning && !this.stoppedSimulation &&
				this.timeOfNextEvent != null &&
				this.timeOfNextEvent.lessThanOrEqual(this.simulationEndTime)) {
			this.nextInternalEventFuture.set(
						this.rtScheduler.schedule(
								this.internalEventTask,
								this.computeRealTimeDelayToNextEvent(
														this.timeOfNextEvent),
								PREFFERED_SCHEDULING_TIME_UNIT));
		}
	}

	/**
	 * replan the next internal event if any and the simulation did not reach
	 * its end time (in simulated time); if an internal event was previously
	 * scheduled, cancel it before planning the new one.
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
//	for the time being, this functionality is difficult to implement this way
//	in non real-time simulations; hence, an alternative implementation marks
//	cancelled events themselves, and then they are just ignored.
//	public void			replanNextInternalEventTask() throws Exception
//	{
//		this.cancelNextInternalEventTask();
//		if (this.isRunning && !this.stoppedSimulation) {
//			this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
//			this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();
//			if (this.timeOfNextEvent != null &&
//								this.timeOfNextEvent.lessThanOrEqual(
//													this.simulationEndTime)) {
//				this.nextInternalEventFuture.set(
//						this.rtScheduler.schedule(
//								this.internalEventTask,
//								this.computeRealTimeDelayToNextEvent(
//														this.timeOfNextEvent),
//								PREFFERED_SCHEDULING_TIME_UNIT));
//			}
//		}
//	}

	/**
	 * cancel the currently scheduled next internal event task and put
	 * {@code null} in the atomic reference to this future.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	protected void		cancelNextInternalEventTask()
	{
		ScheduledFuture<?> f =
						this.nextInternalEventFuture.getAndUpdate(o -> null);
		if (f != null) {
			f.cancel(false);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		if (this.isRunning) {
			if (this.hasDebugLevel(2)) {
				this.simulatedModel.logMessage(
								"simulation ends for " +
								this.getSimulatedModel().getURI());
			}
			this.eventSerialisationLock.lock();
			try {
				super.endSimulation(endTime);
				this.rtScheduler.shutdown();
			} finally {
				this.eventSerialisationLock.unlock();
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.AtomicEngine#planExternalEventStep(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public void			planExternalEventStep(
		String destinationURI,
		ArrayList<EventI> es
		)
	{
		assert	this.simulatedModel.getURI().equals(destinationURI) :
				new NeoSim4JavaException("Precondition violation: "
										+ "getURI().equals(destinationURI)");
		assert	es != null :
				new NeoSim4JavaException("Precondition violation: es != null");

		// schedule a task to execute the external events in es
		this.rtScheduler.scheduleImmediate(
							this.createExternalEventTask(destinationURI, es));
	}

	/**
	 * create the task that will execute the next external transition when a
	 * set of events has been received.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getURI().equals(destinationURI) || isDescendentModel(destinationURI)}
	 * pre	{@code es != null && !es.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI	URI of the atomic model that must receive the events.
	 * @param es				set of events just received.
	 * @return					the task to be scheduled.
	 */
	protected Runnable	createExternalEventTask(
		final String destinationURI,
		final ArrayList<EventI> es
		)
	{
		assert	getSimulatedModel().getURI().equals(destinationURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getSimulatedModel().getURI().equals(destinationURI) || "
						+ "getSimulatedModel().isDescendent(destinationURI)");
		assert	es != null :
				new NeoSim4JavaException("Precondition violation: es != null");

		final AtomicRTEngine rte = this;
		final AtomicModel m = (AtomicModel)this.getSimulatedModel();
		return new Runnable() {
					@Override
					public void run() {
						rte.eventSerialisationLock.lock();
						try {
							rte.cancelNextInternalEventTask();
							m.actualStoreInput(destinationURI, es);
							Time occ = es.get(0).getTimeOfOccurrence();
							Time last = rte.getTimeOfLastEvent();
							Duration elapsedTime = null;
							if (occ.greaterThan(last)) {
								elapsedTime = occ.subtract(last);
							} else {
								elapsedTime =
									new Duration(0.0,
												 getSimulatedModel().
												 		getSimulatedTimeUnit());
							}
							rte.externalEventStep(elapsedTime);
							rte.planNextInternalEventTask();
						} catch (Throwable e) {
							e.printStackTrace();
						} finally {
							rte.eventSerialisationLock.unlock();
						}
					}
				};
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#computeCurrentSimulationTime(java.lang.String)
	 */
	@Override
	public Time			computeCurrentSimulationTime(String rtAtomicModelURI)
	{
		this.eventSerialisationLock.lock();
		try {
			// This code is a copy of HIOA_AtomicRTEngine#computeCurrentSimulationTime
			assert	getSimulatedModel().getURI().equals(rtAtomicModelURI) :
					new NeoSim4JavaException("Precondition violation: "
							+ "getSimulatedModel().getURI()."
							+ "equals(rtAtomicModelURI)");

			long currentInMillis = System.currentTimeMillis();
			long fromBeginning =
					TimeUnit.MILLISECONDS.toNanos(currentInMillis) -
							AtomicRTEngine.PREFFERED_SCHEDULING_TIME_UNIT.
												toNanos(this.realTimeOfStart);
			double conversionFactor =
					TimeUnit.NANOSECONDS.convert(
								1,
								this.getSimulatedModel().getSimulatedTimeUnit());
			double currentTime = fromBeginning/conversionFactor;
			// given accelerated execution time
			currentTime *= this.accelerationFactor;
			if (currentTime < 0.0) {
				// cater for "small" timing errors
				currentTime = 0.0;
			}
			Time current = new Time(
								currentTime,
								this.getSimulatedModel().getSimulatedTimeUnit());

//			System.out.println("------------------------------------------------");
//			System.out.println("AtomicRTEngine#computeCurrentSimulationTime " + this.getSimulatedModel().getURI() + " " + rtAtomicModelURI);
//			System.out.println("real time of start            : " + this.realTimeOfStart);
//			System.out.println("current time in millis        : " + currentInMillis);
//			System.out.println("elapsed time in millis        : " + (currentInMillis - this.realTimeOfStart));
//			System.out.println("from beginning in nanos       : " + fromBeginning);
//			System.out.println("accelerated conversion factor : " + conversionFactor);
//			System.out.println("current simulated time        : " + current);
//			System.out.println("------------------------------------------------");

			return current;
		} finally {
			this.eventSerialisationLock.unlock();
		}
	}
}
// -----------------------------------------------------------------------------
