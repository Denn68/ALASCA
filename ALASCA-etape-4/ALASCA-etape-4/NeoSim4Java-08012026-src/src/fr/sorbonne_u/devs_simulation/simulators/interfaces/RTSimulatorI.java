package fr.sorbonne_u.devs_simulation.simulators.interfaces;

import java.util.concurrent.Semaphore;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>RTSimulationI</code> defines the core behaviour of
 * simulation engines that execute models using the physical clock (of the
 * underlying computer) as simulation clock, or a linear transformation of it
 * (to simulate either faster or slower than the real physical time).
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		RTSimulatorI
extends		SimulatorI
{
	/**
	 * at real time <code>realTimeStart</code>, start a real time
	 * collaborative simulation with the simulation clock stating at
	 * <code>startTime</code> and lasting <code>simulatedTimeDuration</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code realTimeStart > System.currentTimeMillis()}
	 * pre	{@code simulationStartTime >= 0.0}
	 * pre	{@code simulationDuration > 0}
	 * pre	{@code isRTSchedulerSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param realTimeOfStart		time of start for the simulation, in real time, synchronising all simulation engines.
	 * @param simulationStartTime	time at which the simulation starts interpreted in the model simulation time.
	 * @param simulationDuration	duration of the simulation interpreted in the model simulation time.
	 */
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		);

	/**
	 * set the synchroniser signalling the end of the simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulationEndSynchroniser == null || !simulationEndSynchroniser.tryAcquire()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param simulationEndSynchroniser	a semaphore used to synchronise with the end of the simulation.
	 */
	public void			setSimulationEndSynchroniser(
		Semaphore simulationEndSynchroniser
		);

	/**
	 * compute the current simulation time of the designated model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code rtAtomicModelURI != null && !rtAtomicModelURI.isEmpty()}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param rtAtomicModelURI	URI of a real time model from which the current time is required.
	 * @return					the current simulation time of the designated model.
	 */
	public Time			computeCurrentSimulationTime(
		String rtAtomicModelURI
		);
}
// -----------------------------------------------------------------------------
