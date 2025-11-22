package fr.sorbonne_u.devs_simulation.examples.dependentcounters;

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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.ArrayList;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>DependentCounterModel</code> implements a DEVS simulation
 * model with dependencies <i>i.e.</i>, exporting and importing events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: {@code CurrentValue}</li>
 * <li>Exported events: {@code CurrentValue}</li>
 * </ul>
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
 * <p>Created on : 2018-04-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(
	exported = {CurrentValue.class},
	imported = {CurrentValue.class}
	)
public class			DependentCounterModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>DependentCounterModelReport</code> defines the simulation
	 * report for this model. 
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p>
	 * The report simply provides the end value of the counter.
	 * </p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no invariant
	 * </pre>
	 * 
	 * <p>Created on : 2022-06-22</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	DependentCounterModelReport
	extends		AbstractSimulationReport
	{
		private static final long	serialVersionUID = 1L;
		protected final int			endValue;

		/**
		 * create a report.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no more precondition.
		 * post	{@code true}	// no more postcondition.
		 * </pre>
		 *
		 * @param modelURI	URI of the model proposing this report.
		 * @param endValue	value of the variable at the end of the run.
		 */
		public			DependentCounterModelReport(
			String modelURI,
			int endValue
			)
		{
			super(modelURI);
			this.endValue = endValue;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String 	toString()
		{
			return this.getClass().getSimpleName() + "[" + this.endValue + "]";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** a convenient instance model URI, when a unique instance is created.	*/
	public static String		MODEL_URI_PREFIX = "my-basic-counter-model";
	/** name of the run parameter passing the start value for the counter.	*/
	public static final String	START_VALUE_PARAM_NAME = "start";
	/** name of the run parameter passing the step value for the model.		*/
	public static final String	STEP_VALUE_PARAM_NAME = "step";

	/** start value for the counter.										*/
	protected int				startValue;
	/** duration between internal transitions of the model.					*/
	protected double			stepDuration;
	/** delay to the next internal transition.								*/
	protected double			nextDelay;
	/** true when an external event has just been executed to trigger an
	 *  internal transition.												*/
	protected boolean			hasJustExecutedExternalEvent;
	/** the counter itself.													*/
	protected int				counter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a basic model with the given model URI, time unit and simulation
	 * engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && timeUnit != null && simulationEngine != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param timeUnit			time unit used for the simulated time clock.
	 * @param simulationEngine	the simulation engine running the simulation.
	 * @throws Exception 		<i>to do</i>.
	 */
	public				DependentCounterModel(
		String uri,
		TimeUnit timeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, timeUnit, simulationEngine);
		// create and attach a standard logger.
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		String startName =
				ModelI.createRunParameterName(this.getURI(),
											  START_VALUE_PARAM_NAME);
		String stepName = 
				ModelI.createRunParameterName(this.getURI(),
											  STEP_VALUE_PARAM_NAME);
		if (simParams != null) {
			if (!simParams.containsKey(startName)) {
				throw new MissingRunParameterException(startName);
			} else if (!simParams.containsKey(stepName)) {
				throw new MissingRunParameterException(stepName);
			}
		} else {
			throw new MissingRunParameterException("no run parameters");
		}

		this.startValue = (int) simParams.get(startName);
		this.stepDuration = (double) simParams.get(stepName);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.getSimulationEngine().toggleDebugMode();
		this.counter = this.startValue;
		this.nextDelay = this.stepDuration;
		this.hasJustExecutedExternalEvent = false;
		super.initialiseState(initialTime);
	}

	/**
	 * increment the counter by 1.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			incrementCounter()
	{
		this.counter++;
	}

	/**
	 * increment the counter by the given value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param incr		vale by which the counter must be incremented.
	 */
	public void			incrementCounter(int incr)
	{
		this.counter += incr;
	}

	/**
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// to get internal steps at a specific fixed rate while receiving
		// external events that advances the current state time to their time
		// of occurrence, the time advance adjust itself to return the time
		// remaining until the next planned internal event.
		if (!this.hasJustExecutedExternalEvent) {
			this.nextDelay = this.stepDuration;
		} else {
			this.hasJustExecutedExternalEvent = false;
		}
		return new Duration(this.nextDelay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.incrementCounter();
		// tracing
		this.logMessage("Internal transition at " + this.currentStateTime
									+ ", counter = " + this.counter);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// propagate a current value event to other connected models
		ArrayList<EventI> ret = new ArrayList<>();
		ret.add(new CurrentValue(this.getTimeOfNextEvent(), this.counter));
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		// get the received events
		ArrayList<EventI> receivedEvents = this.getStoredEventAndReset();
		// check that exactly one event has been received
		assert	receivedEvents != null && receivedEvents.size() == 1;
		// execute the received event on this model
		receivedEvents.get(0).executeOn(this);

		// adjust the next delay to ensure that the next internal transition
		// will occur at the time it was planned
		this.nextDelay = this.nextDelay - elapsedTime.getSimulatedDuration();
		if (this.nextDelay < 0.0) {
			// cater for "small" timing errors
			this.nextDelay = 0.0;
		}
		// this will trigger an immediate internal transition, just following
		// this external transition, by the way also triggering an output
		// towards other connected models
		this.hasJustExecutedExternalEvent = true;

		// tracing
		this.logMessage(
			"External transition at " + this.currentStateTime
					+ " increment = " +
								receivedEvents.get(0).getEventInformation()
					+ ", counter = " + this.counter);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		try {
			return new DependentCounterModelReport(this.getURI(), this.counter);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
// -----------------------------------------------------------------------------
