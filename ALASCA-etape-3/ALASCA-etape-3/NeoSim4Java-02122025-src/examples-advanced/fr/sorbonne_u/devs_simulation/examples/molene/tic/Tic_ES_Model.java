package fr.sorbonne_u.devs_simulation.examples.molene.tic;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

/**
 * The class <code>Tic_ES_Model</code> defines an alternate a tic model that
 * also emits a tic events (instance of the class <code>Tic_ES_Event</code>)
 * at a regular frequency given by a run parameter; it uses the event scheduling
 * approach to implement the issuing of events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: {@code Tic_ES_Event}</li>
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
 * <p>Created on : 2018-07-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(exported = {Tic_ES_Event.class})
// -----------------------------------------------------------------------------
public class			Tic_ES_Model
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** name of the run parameter defining the delay between tic events.	*/
	public static final String	DELAY_PARAMETER_NAME = "delay";
	/** the standard delay between tic events.								*/
	public static Duration		STANDARD_DURATION =
										new Duration(60.0, TimeUnit.SECONDS);
	/** the URI to be used when creating the instance of the model.			*/
	public static final String	URI = "TicModel-1";
	/** the value of the delay between tic events during the current
	 *  simulation run.														*/
	protected Duration			delay;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of tic model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>todo.</i>
	 */
	public				Tic_ES_Model(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.delay = Tic_ES_Model.STANDARD_DURATION;
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		// Schedule the first TicEvent.
		Time occurrence = initialTime.add(this.delay);
		this.scheduleEvent(new Tic_ES_Event(occurrence));
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
					this.getCurrentStateTime().add(this.getNextTimeAdvance());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		)
	{
		String varName =
					this.getURI() + ":" + Tic_ES_Model.DELAY_PARAMETER_NAME;
		if (simParams.containsKey(varName)) {
			this.delay = (Duration) simParams.get(varName);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// for atomic event scheduling models, the internal transition is
		// called when an event that must be exported occurs, hence it is
		// called here when the previous tic event has been sent so we must
		// schedule the next one.

		this.logMessage(this.getCurrentStateTime() + "|tic sent.");
		// Schedule the next TicEvent after the prescribed delay.
		Time occurrence = this.getCurrentStateTime().add(this.delay);
		this.scheduleEvent(new Tic_ES_Event(occurrence));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	{
		final String uri = this.getURI();
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return "Tic_ES_ModelReport()";
					}

					@Override
					public String getModelURI() {
						return uri;
					}
			   };
	}
}
// -----------------------------------------------------------------------------
