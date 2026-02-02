package fr.sorbonne_u.devs_simulation.examples.molene.tic;

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

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>TicModel</code> implements an atomic model that emits
 * tic events at a regular frequency given by a run parameter.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Probably the simplest DEVS atomic model possible: the
 * <code>timeAdvance</code> method systematically returns the delay to the next
 * event and the method <code>output</code> emits the event each time an
 * internal transition is about to be performed.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: {@code TicEvent}</li>
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
 * <p>Created on : 2019-10-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(exported = {TicEvent.class})
// -----------------------------------------------------------------------------
public class			TicModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** name of the run parameter defining the delay between tic events.	*/
	public static final String	DELAY_PARAMETER_NAME = "delay";
	/** the standard delay between tic events.								*/
	public static Duration		STANDARD_DURATION =
										new Duration(10.0, TimeUnit.SECONDS);
	/** the URI to be used when creating a unique instance of the model.	*/
	public static final String	URI = "TicModel";
	/** the value of the delay between tic events during the current
	 *  simulation run.														*/
	protected Duration			delay;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new tic model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition
	 * post	{@code true}	// no more postcondition
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>to do</i>.
	 */
	public				TicModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.delay = TicModel.STANDARD_DURATION;
		this.getSimulationEngine().setLogger(new StandardLogger());
		this.getSimulationEngine().toggleDebugMode();
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		if (simParams == null) {
			throw new MissingRunParameterException("no run parameters!");
		}

		String varName =
				ModelI.createRunParameterName(this.getURI(),
											  TicModel.DELAY_PARAMETER_NAME);
		if (!simParams.containsKey(varName)) {
			throw new MissingRunParameterException(varName);
		}
		this.delay = (Duration) simParams.get(varName);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		ArrayList<EventI> ret = new ArrayList<EventI>();
		// compute the current simulation time because it has not been
		// updated yet.
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
		// create the external event.
		TicEvent e = new TicEvent(t);
		this.logMessage("output " + e.eventAsString());
		ret.add(e);
		// return the new tic event.
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.logMessage("at internal transition " +
						this.getCurrentStateTime() + " " + elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.delay;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		final String uri = this.getURI();
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;

					/**
					 * @see fr.sorbonne_u.devs_simulation.interfaces.SimulationReportI#getModelURI()
					 */
					@Override
					public String getModelURI() { return uri; }

					/**
					 * @see java.lang.Object#toString()
					 */
					@Override
					public String toString() { return "TicModelReport()"; }
		};
	}
}
// -----------------------------------------------------------------------------
