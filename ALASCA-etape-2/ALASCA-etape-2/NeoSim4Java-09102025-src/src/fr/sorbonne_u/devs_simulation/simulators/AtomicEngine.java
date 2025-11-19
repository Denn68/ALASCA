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

import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import java.util.ArrayList;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicEngine</code> implements the DEVS simulation protocol
 * to run simulation models that are either atomic or coupled models which
 * submodels all share this atomic engine (i.e., the same simulation algorithm).
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
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicEngine
extends		SimulationEngine
implements	AtomicSimulatorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * set the atomic model executed by this simulation engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedModel instanceof AtomicModelI}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	{
		assert	simulatedModel instanceof AtomicModelI :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulatedModel instanceof AtomicModelI");

		super.setSimulatedModel(simulatedModel);
	}

	// -------------------------------------------------------------------------
	// Simulation related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		)
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	simulationStartTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime != null");
		assert	simulationStartTime.greaterThanOrEqual(
						Time.zero(getSimulatedModel().getSimulatedTimeUnit())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationStartTime.greaterThanOrEqual("
						+ "Time.zero(getSimulatedModel()."
						+ "getSimulatedTimeUnit()))");
		assert	simulationDuration != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration != null");
		assert	simulationDuration.greaterThan(
					Duration.zero(getSimulatedModel().getSimulatedTimeUnit())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulationDuration.greaterThan(Duration.zero("
						+ "getSimulatedModel().getSimulatedTimeUnit()))");

		this.simulatedModel.initialiseState(simulationStartTime);
		if (!this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.initialiseVariables();
		}
		if (this.simulatedModel.isRoot() &&
						this.simulatedModel.useFixpointInitialiseVariables()) {
			this.simulatedModel.fixpointInitialiseVariables();
		}
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();

		super.initialiseSimulation(simulationStartTime, simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised()
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");

		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
								"AtomicEngine#isSimulationInitialised " +
								this.simulatedModel.getURI());
		}

		return super.isSimulationInitialised() &&
				this.simulatedModel.isStateInitialised() &&
				this.simulatedModel.allModelVariablesInitialised() &&
				this.getTimeOfNextEvent() != null &&
				this.getNextTimeAdvance() != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep()
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
									"AtomicEngine>>internalEventStep "
									+ this.simulatedModel.getURI() + ".");
		}
		this.simulatedModel.internalTransition();
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime();
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();

		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		if (this.hasDebugLevel(2)) {
			this.simulatedModel.logMessage(
				"AtomicEngine#produceOutput " + this.simulatedModel.getURI() +
				" " + current);
			if (!this.isSimulationInitialised()) {
				this.simulatedModel.logMessage(
						"%%% super.isSimulationInitialised() " +
								super.isSimulationInitialised());
				this.simulatedModel.logMessage(
						"%%% this.simulatedModel.isStateInitialised() " +
								this.simulatedModel.isStateInitialised());
				this.simulatedModel.logMessage(
						"%%% this.getTimeOfNextEvent() != null " +
								(this.getTimeOfNextEvent() != null));
				this.simulatedModel.logMessage(
						"%%% this.getNextTimeAdvance() != null " +
								(this.getNextTimeAdvance() != null));
			}
		}

		this.simulatedModel.produceOutput(current);
	}

	/**
	 * add the external events {@code es} to the model associated with this
	 * simulation engine and plan the execution of the external event step that
	 * will process them.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isModelSet()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI	URI of the model to which {@code es} must be delivered.
	 * @param es				received events.
	 */
	public void			planExternalEventStep(
		String destinationURI,
		ArrayList<EventI> es
		)
	{
		if (this.hasDebugLevel(2)) {
			assert	this.isModelSet() :
					new NeoSim4JavaException("Precondition violation: "
							+ "isModelSet()");
		}
		assert	getSimulatedModel().getURI().equals(destinationURI);
		((AtomicModel)this.getSimulatedModel()).
										actualStoreInput(destinationURI, es);

		// Notify the parent that the model has to perform an
		// external transition during this simulation step.
		if (!this.getSimulatedModel().isRoot() &&
									this.getSimulatedModel().isCoordinated()) {
			if (!this.simulatedModel.isRoot()) {
				this.parent.hasReceivedExternalEvents(
										this.getSimulatedModel().getURI());
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	{
		assert	this.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
						+ "isModelSet()");
		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");

		if (this.hasDebugLevel(1)) {
			this.simulatedModel.logMessage(
					"AtomicEngine>>externalEventStep " +
					this.simulatedModel.getURI() +
					" at " + this.getTimeOfLastEvent() +
					" with elapsed time " + elapsedTime);
		}

		this.simulatedModel.externalTransition(elapsedTime);
		this.timeOfLastEvent = this.simulatedModel.getCurrentStateTime();
		this.timeOfNextEvent = this.simulatedModel.getTimeOfNextEvent();
		this.nextTimeAdvance = this.simulatedModel.getNextTimeAdvance();

		if (!this.simulatedModel.isRoot()) {
			this.parent.hasPerformedExternalEvents(
											this.simulatedModel.getURI());
		}

		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getTimeOfLastEvent())) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getNextTimeAdvance().equals(getTimeOfNextEvent()."
						+ "subtract(getTimeOfLastEvent()))");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.simulatedModel.endSimulation(endTime);
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation()
	{
		this.simulatedModel.finalise();
		super.finaliseSimulation();
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.SimulationEngine#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		this.simulatedModel.showCurrentState(indent + "    ", elapsedTime);
	}
}
// -----------------------------------------------------------------------------
