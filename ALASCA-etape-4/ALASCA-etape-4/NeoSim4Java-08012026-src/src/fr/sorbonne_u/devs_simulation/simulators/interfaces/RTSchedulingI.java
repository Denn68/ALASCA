package fr.sorbonne_u.devs_simulation.simulators.interfaces;

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

import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.io.Serializable;

// -----------------------------------------------------------------------------
/**
 * The interface <code>RTSchedulerI</code> declares the real time scheduler
 * interface used in this library to execute simulation tasks in real time
 * simulation engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interface is meant to abstract the simulation engines from the exact
 * implementation of the schedulers that are available to schedule tasks.
 * </p>
 * <p>
 * This definition is much inspired from Java scheduled executor services.
 * </p>
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
public interface		RTSchedulingI
extends		Serializable
{
	/**
	 * schedule an event task on the real time scheduler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code eventTask != null}
	 * pre	{@code delay >= 0}
	 * pre	{@code isRunning()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param eventTask						the task to be executed.
	 * @param delay							the delay until the task will begins its execution.
	 * @param tu							the time unit to interpret the delay.
	 * @return								a future allowing to cancel the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution.
	 * @throws NullPointerException			if command is null.
	 */
	public ScheduledFuture<?>	schedule(
		Runnable eventTask,
		long delay,
		TimeUnit tu
		) throws RejectedExecutionException, NullPointerException;

	/**
	 * schedule an event task on the real time scheduler for immediate
	 * execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code eventTask != null}
	 * pre	{@code isRunning()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param eventTask						the task to be executed.
	 * @return								a future allowing to cancel the task.
	 * @throws RejectedExecutionException	if the task cannot be scheduled for execution.
	 * @throws NullPointerException			if command is null.
	 */
	public Future<?>	scheduleImmediate(Runnable eventTask)
	throws RejectedExecutionException, NullPointerException;

	/**
	 * return true if the scheduler is neither shutdown nor terminated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the scheduler is neither shutdown nor terminated.
	 */
	public boolean		isRunning();

	/**
	 * return true if the scheduler has been shut down.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the scheduler has terminated its execution.
	 */
	public boolean		isShutdown();

	/**
	 * return true if the scheduler has terminated its execution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the scheduler has terminated its execution.
	 */
	public boolean		isTerminated();

	/**
	 * shut down the real time scheduler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isShutdown()}
	 * post	{@code isShutdown()}
	 * </pre>
	 *
	 * @throws SecurityException	if a security manager exists and shutting down this ExecutorService may manipulate threads that the caller is not permitted to modify because it does not hold java.lang.RuntimePermission("modifyThread"), or the security manager's checkAccess method denies access.
	 */
	public void			shutdown() throws SecurityException;

	/**
	 * shut down the real time scheduler immediately, without waiting for
	 * running tasks to finish.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isShutdown()}
	 * post	{@code isTerminated()}
	 * </pre>
	 *
	 * @throws SecurityException	if a security manager exists and shutting down this ExecutorService may manipulate threads that the caller is not permitted to modify because it does not hold java.lang.RuntimePermission("modifyThread"), or the security manager's checkAccess method denies access.
	 */
	public void			shutdownNow() throws SecurityException;	
}
// -----------------------------------------------------------------------------
