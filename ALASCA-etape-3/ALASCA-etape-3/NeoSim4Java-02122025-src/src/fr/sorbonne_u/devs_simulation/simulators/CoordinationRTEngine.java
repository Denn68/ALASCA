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

import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>CoordinationRTEngine</code> implements a real coordination
 * engine.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In the implementation of real time simulation engine, the coordinator plays
 * no role in the execution of the simulation except for the initialisation
 * phase and to start each of the individual atomic models and their atomic
 * real time simulation engines, be them immediate children or descendant
 * models.
 * </p>
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
 * <p>Created on : 2020-11-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CoordinationRTEngine
extends		CoordinationEngine
implements	RTSimulatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a real time coordination engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				CoordinationRTEngine()
	{
		super();
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
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#setSimulationEndSynchroniser(java.util.concurrent.Semaphore)
	 */
	@Override
	public void			setSimulationEndSynchroniser(
		Semaphore simulationEndSynchroniser
		)
	{
		assert	simulationEndSynchroniser == null ||
										!simulationEndSynchroniser.tryAcquire();

		if (simulationEndSynchroniser != null) {
			for(int i = 0 ; i < this.coordinatedEngines.length; i++) {
				((RTSimulatorI)this.coordinatedEngines[i]).
						setSimulationEndSynchroniser(simulationEndSynchroniser);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time startTime,
		Duration simulationDuration
		)
	{
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].
						initialiseSimulation(startTime, simulationDuration);
		}
		if (this.simulatedModel.isRoot() &&
						this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.fixpointInitialiseVariables();
		}

		// The next initialisations are meant to satisfy the inherited
		// conditions of the method isSimulationInitialised(). The values
		// are not used by the decentralised real-time execution algorithm.
		this.elapsedTimes = new Duration[this.coordinatedEngines.length];
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.elapsedTimes[i] = Duration.INFINITY;
		}
		this.submodelOfNextEventURI = "";
		this.activeModelURIs = new HashSet<String>();
		this.timeOfNextEvent = Time.INFINITY;
		this.nextTimeAdvance = Duration.INFINITY;

		// bypass the method super.initialiseSimulation to avoid
		// initialisations that are not adapted to the real-time execution
		// algorithm.
		super.initialiseSimulationBypass(startTime, simulationDuration);		
	}

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
		if (this.simulatedModel.isRoot()) {
			TimeUnit tu = this.simulatedModel.getSimulatedTimeUnit();
			// initialise the simulation with its start time and its duration
			// in simulated time (with its proper time unit).
			this.initialiseSimulation(new Time(simulationStartTime, tu),
									  new Duration(simulationDuration, tu));
		}
		for (int i = 0 ; i < this.coordinatedEngines.length ; i++) {
			this.coordinatedEngines[i].startRTSimulation(realTimeOfStart,
														  simulationStartTime,
														  simulationDuration);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		if (this.isRunning) {
			this.isRunning = false;
			if (!this.simulatedModel.isRoot()) {
				this.parent.endSimulation(endTime);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	{
		// in this implementation, real-time coordinators do not govern the
		// processing of atomic models simulation steps
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.CoordinationEngine#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	{
		// in this implementation, real-time coordinators do not govern the
		// processing of atomic models simulation steps
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#computeCurrentSimulationTime(java.lang.String)
	 */
	@Override
	public Time			computeCurrentSimulationTime(
		String rtAtomicModelURI
		)
	{
		assert	rtAtomicModelURI != null :
				new NeoSim4JavaException("Precondition violation: "
								   + "rtAtomicModelURI != null");
		assert	((CoupledModelI)getSimulatedModel()).isDescendant(rtAtomicModelURI) :
				new NeoSim4JavaException("Precondition violation: "
								   + "((CoupledModelI)getSimulatedModel())."
								   + "isDescendant(rtAtomicModelURI)");

		int i = 0;
		for ( ; i < this.coordinatedEngines.length ; i++) {
			if (((CoupledModelI)this.coordinatedEngines[i].getSimulatedModel()).
												isDescendant(rtAtomicModelURI)
				|| this.coordinatedEngines[i].getSimulatedModel().getURI().
												equals(rtAtomicModelURI))
			{
				return ((RTSimulatorI)this.coordinatedEngines[i]).
								computeCurrentSimulationTime(rtAtomicModelURI);
			}
		}
		// Will never happen because the for loop will find a descendant model
		return null;
	}
}
// -----------------------------------------------------------------------------
