package fr.sorbonne_u.devs_simulation.examples.molene.sm;

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
import java.util.ArrayList;
import java.util.Map;
import fr.sorbonne_u.devs_simulation.examples.molene.State;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.Compressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.LowBattery;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.NotCompressing;
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
import fr.sorbonne_u.plotters.PlotterDescription;
import fr.sorbonne_u.plotters.XYPlotter;

/**
 * The class <code>ServerModel</code> defines a model of the server side for
 * the Molene example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code Compressing},
 *   {@code NotCompressing},
 *   {@code LowBattery}</li>
 * <li>Exported events: none</li>
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
 * invariant	{@code true}
 * </pre>
 * 
 * <p>Created on : 2018-10-16</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {Compressing.class,
								 NotCompressing.class,
								 LowBattery.class})
// -----------------------------------------------------------------------------
public class			ServerModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	private static final String	SERIES = "server state";
	/** URI used when creating a server model instance.						*/
	public static final String	URI = "ServerModel-1";
	/** the current state of the server.									*/
	protected State				currentState;
	/** Frame used to plot the state during the simulation.					*/
	protected XYPlotter			statePlotter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of server model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception			<i>to do</i>.
	 */
	public				ServerModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#finalise()
	 */
	@Override
	public void			finalise()
	{
		if (this.statePlotter != null) {
			this.statePlotter.dispose();
			this.statePlotter = null;
		}
	}

	// -------------------------------------------------------------------------
	// Model specific methods
	// -------------------------------------------------------------------------

	/**
	 * return an integer representation to ease the plotting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code s != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param s	a state for the server.
	 * @return	an integer representation to ease the plotting.
	 */
	public static int	state2int(State s)
	{
		assert	s != null;

		if (s == State.COMPRESSING) {
			return 3;
		} else if (s == State.NOT_COMPRESSING) {
			return 2;
		} else {
			assert	s == State.LOW_BATTERY;
			return 1;
		}
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

		String vname = ModelI.createRunParameterName(
									this.getURI(),
									PlotterDescription.PLOTTING_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.statePlotter = new XYPlotter(pd);
		this.statePlotter.createSeries(SERIES);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = State.NOT_COMPRESSING;
		if (this.statePlotter != null) {
			this.statePlotter.initialise();
			this.statePlotter.showPlotter();
		}

		super.initialiseState(initialTime);

		this.statePlotter.addData(
				SERIES,
				this.getCurrentStateTime().getSimulatedTime(),
				state2int(this.currentState));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// No internal event; the model only progresses from external events.
		return Duration.INFINITY;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// No exported events.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		this.logMessage("ServerModel#userDefinedExternalTransition "
								+ this.currentState + " "
								+ currentEvents.get(0));
		assert	currentEvents != null && currentEvents.size() == 1;

		// Compute the new state from the received event.
		State oldState = this.currentState;
		if (this.currentState == State.COMPRESSING ||
								this.currentState == State.NOT_COMPRESSING) {
			if (currentEvents.get(0) instanceof Compressing) {
				this.currentState = State.COMPRESSING;
			} else if (currentEvents.get(0) instanceof NotCompressing) {
				this.currentState = State.NOT_COMPRESSING;
			} else {
				assert	currentEvents.get(0) instanceof LowBattery;
				this.currentState = State.LOW_BATTERY;
			}
		} else {
			assert	this.currentState == State.LOW_BATTERY;
			// Do nothing
		}

		if (oldState != this.currentState) {
			this.statePlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					state2int(oldState));
			this.statePlotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					state2int(this.currentState));
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.statePlotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				(this.currentState == State.COMPRESSING ?
					3
				: 	(this.currentState == State.NOT_COMPRESSING ?
						2
					:	1)));

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		final String uri = this.uri;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri;
					}				
				};
	}
}
// -----------------------------------------------------------------------------
