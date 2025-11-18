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

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardRTScheduler</code> defines the standard real time
 * scheduler used in this library to execute simulation tasks in real time
 * simulation engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The class and its implemented interface <code>RTSchedulingI</code> are
 * meant to abstract the simulation engines from the exact implementation
 * of the schedulers that are available to schedule tasks.
 * </p>
 * <p>
 * The implementation of this standard scheduler uses Java scheduled executor
 * services.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code es != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-11-26</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardRTScheduler
implements	RTSchedulingI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** the Java scheduled executor service.								*/
	protected ScheduledExecutorService		es;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a standard real time scheduler for real time simulaiton engines.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				StandardRTScheduler()
	{
		this.es = Executors.newSingleThreadScheduledExecutor();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#isRunning()
	 */
	@Override
	public boolean		isRunning()
	{
		return !this.isShutdown() && !this.isTerminated();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#schedule(java.lang.Runnable, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public ScheduledFuture<?>	schedule(
		Runnable eventTask,
		long delay,
		TimeUnit tu
		) throws RejectedExecutionException, NullPointerException
	{
		assert	this.isRunning() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isRunning()");

		return this.es.schedule(eventTask, delay, tu);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#scheduleImmediate(java.lang.Runnable)
	 */
	@Override
	public Future<?>	scheduleImmediate(Runnable eventTask)
	throws RejectedExecutionException, NullPointerException
	{
		assert	this.isRunning() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isRunning()");

		return this.es.submit(eventTask);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#isShutdown()
	 */
	@Override
	public boolean		isShutdown()
	{
		return this.es.isShutdown();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#isTerminated()
	 */
	@Override
	public boolean		isTerminated()
	{
		return this.es.isTerminated();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#shutdown()
	 */
	@Override
	public void shutdown() throws SecurityException
	{
		assert	!isShutdown() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isShutdown()");
		
		this.es.shutdown();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI#shutdownNow()
	 */
	@Override
	public void shutdownNow() throws SecurityException
	{
		assert	!isShutdown() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isShutdown()");
	
		this.es.shutdownNow();
	}
}
// -----------------------------------------------------------------------------
