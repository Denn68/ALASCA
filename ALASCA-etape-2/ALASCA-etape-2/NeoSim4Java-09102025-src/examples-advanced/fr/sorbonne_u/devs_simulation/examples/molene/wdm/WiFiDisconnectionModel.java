package fr.sorbonne_u.devs_simulation.examples.molene.wdm;

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
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.BooleanPiece;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.plotters.PlotterDescription;
import fr.sorbonne_u.plotters.XYPlotter;

import java.util.Map;
import java.util.Vector;

/**
 * The class <code>WiFiDisconnectionModel</code> defines a model of WiFi
 * network disconnections.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * WiFi networks suffer from disconnections during which no data can be
 * transmitted. This model considers that:
 * </p>
 * <ul>
 * <li>the time between interruptions follows an exponential distribution.</li>
 * <li>the duration of the interruptions also follows an exponential
 *   distribution.</li>
 * </ul>
 * <p>
 * For simulation runs, the following parameters must be set:
 * </p>
 * <ul>
 * <li><code>mean time between interruptions</code> with the run parameter name
 *   given by the concatenation of the model URI, a colon and the name given by
 *   the static variable <code>MTBI</code>;</li>
 * <li><code>mean interruption duration</code> with the run parameter name given
 *   by the concatenation of the model URI, a colon and the name given by the
 *   static variable <code>MID</code>.</li>
 * </ul>
 * <p>
 * After the runs, the report returned by this model provides three elements:
 * </p>
 * <ol>
 * <li>the total number of generated interruptions;</li>
 * <li>the availability i.e., the proportion of the simulation duration during
 *   which the WiFi was able to transmit data;</li>
 * <li>a piecewise constant boolean function giving the state (connected = true,
 *   disconnected = false) of the WiFi over the whole simulation duration.</li>
 * </ol>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: {@code InterruptionEvent}, {@code ResumptionEvent}</li>
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
 * <p>Created on : 2018-07-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(exported = {InterruptionEvent.class,
								 ResumptionEvent.class})
// -----------------------------------------------------------------------------
public class			WiFiDisconnectionModel
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiDisconnectionReport</code> implements the simulation
	 * report generated by the WiFi disconnection model at each simulation run.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-18</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	WiFiDisconnectionReport
	extends		AbstractSimulationReport
	{
		private static final long			serialVersionUID = 1L;
		public final int					numberOfGeneratedInterruptions;
		public final double					availability;
		public final Vector<BooleanPiece>	connectionFunction;

		public			WiFiDisconnectionReport(
			String modelURI,
			int numberOfGeneratedInterruptions,
			double availability,
			Vector<BooleanPiece> connectionFunction
			)
		{
			super(modelURI);
			this.numberOfGeneratedInterruptions =
										numberOfGeneratedInterruptions;
			this.availability = availability;
			this.connectionFunction = connectionFunction;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n";
			ret += "WiFiDisconnectionReport\n";
			ret += "-----------------------------------------\n";
			ret += "number of generated interruptions = " +
								this.numberOfGeneratedInterruptions + "\n";
			ret += "availability = " + this.availability + "\n";
			ret += "connection function = \n";
			for (int i = 0 ; i < this.connectionFunction.size() ; i++) {
				ret += "    " + this.connectionFunction.get(i) + "\n";
			}
			ret += "-----------------------------------------\n";
			return ret;
		}
	}

	/**
	 * The enumeration <code>State</code> defines the state of the model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2018-10-22</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected enum State {
		INTERRUPTED,	// the WiFi is interrupted, the bandwidth == 0.0
		CONNECTED		// The WiFi is operational,  the bandwidth >= 0.0
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** name of the series used in the plotting.							*/
	private static final String	SERIES = "wifi connection/disconnection";
	/** URI to be used when creating the model.								*/
	public static final String	URI = "wiFiDisconnectionModel-1";
	/** name of the run parameter defining the mean time between
	 *  interruptions.														*/
	public static final String	MTBI = "mtbi";
	/** name of the run parameter defining the mean duration of
	 *  interruptions.														*/
	public static final String	MID = "mid";

	// Model simulation implementation variables
	/** the time between interruptions follows an exponential
	 *  distribution with mean <code>meanTimeBetweenInterruptions</code>.	*/
	protected double					meanTimeBetweenInterruptions;
	/** the duration of the interruptions follows an exponential
	 *  distribution with mean <code>meanInterruptionDuration</code>.		*/
	protected double					meanInterruptionDuration;
	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rgInterruptionIntervals;
	/**	a random number generator from common math library.					*/
	protected final RandomDataGenerator	rgInterruptionDurations;
	/** 	the current state of the WiFi.									*/
	protected State						currentState;

	// Report generation
	/** piecewise boolean function giving the up time and the down time
	 *  of the WiFi since the beginning of the run.							*/
	protected Vector<BooleanPiece>		connectionFunction;
	/** total up time of the WiFi since the beginning of the run.			*/
	protected double					uptime;
	/** total time of the run.												*/
	protected double					totalTime;
	/** number of interruptions since the beginning of the run.				*/
	protected int						numberOfGeneratedInterruptions;
	/** the time at which the last interruption occurred.					*/
	protected double					timeOfLastInterruption;
	/** the time at which the last resumption occurred.						*/
	protected double					timeOfLastResumption;

	// Plotting
	/** Frame used to plot the bandwidth interruption function during
	 *  the simulation.														*/
	protected XYPlotter					plotter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi disconnection model.
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
	public				WiFiDisconnectionModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger());

		// Create the random number generators
		this.rgInterruptionIntervals = new RandomDataGenerator();
		this.rgInterruptionDurations = new RandomDataGenerator();
		// Create the representation of the event occurrences function
		this.connectionFunction = new Vector<BooleanPiece>();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#finalise()
	 */
	@Override
	public void			finalise()
	{
		if (this.plotter != null) {
			this.plotter.dispose();
			this.plotter = null;
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

		// Get the values of the run parameters in the map using their names
		// and set the model implementation variables accordingly
		String vname = ModelI.createRunParameterName(this.getURI(), MTBI);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.meanTimeBetweenInterruptions = (double) simParams.get(vname);

		vname = ModelI.createRunParameterName(this.getURI(), MID);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.meanInterruptionDuration = (double) simParams.get(vname);

		vname = ModelI.createRunParameterName(
								this.getURI(),
								PlotterDescription.PLOTTING_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		// Initialise the look of the plotter
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.plotter = new XYPlotter(pd);
		this.plotter.createSeries(SERIES);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// statistics gathered during each run and put in the report.
		this.numberOfGeneratedInterruptions = 0;
		this.uptime = 0.0;
		this.totalTime = 0.0;

		// variables used to produce a function representing event occurrences
		// in the report and a plot on the screen
		this.timeOfLastInterruption = -1.0;
		this.timeOfLastResumption = 0.0;

		// initialisation of the random number generators
		this.rgInterruptionIntervals.reSeedSecure();
		this.rgInterruptionDurations.reSeedSecure();

		// initialisation of the event occurrences function for the report
		this.connectionFunction.clear();
		// initialisation of the event occurrences plotter on the screen
		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		// standard initialisation (including the current state time)
		super.initialiseState(initialTime);

		// The model is set to start in the state interrupted and with a
		// resumption event that occurs at time 0.
		this.currentState = State.INTERRUPTED;
		this.scheduleEvent(new ResumptionEvent(initialTime));
		// re-initialisation of the time of occurrence of the next event
		// required here after adding a new event in the schedule.
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
				this.getCurrentStateTime().add(this.getNextTimeAdvance());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.currentState == State.INTERRUPTED) {
			// The event that forced the execution of an internal transition
			// is a resumption event.

			// Log the event occurrence
			this.logMessage(this.getCurrentStateTime() +
												"|resume transmission.");
			// Switch to connected state.
			this.currentState = State.CONNECTED;

			// Include a new point in the event occurrences function (report)
			if (this.timeOfLastInterruption >= 0.0) {
				this.connectionFunction.add(
					new BooleanPiece(
								this.timeOfLastInterruption,
								this.getCurrentStateTime().getSimulatedTime(),
								true));
			}
			this.timeOfLastResumption =
							this.getCurrentStateTime().getSimulatedTime();
			// Update the statistics for the report
			double d = this.getCurrentStateTime().getSimulatedTime() -
											this.timeOfLastInterruption;
			this.totalTime += d;

			// Generate the next interruption event after a random delay and
			// schedule it to be triggered at the corresponding time.
			this.scheduleEvent(
				this.generateNextInterruption(this.getCurrentStateTime()));

			// Update the plotter with the nex event occurrence
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0);
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0);
			}
		} else {
			assert	this.currentState == State.CONNECTED;
			// The event that forced the execution of an internal transition
			// is an interruption event.

			// Log the event occurrence
			this.logMessage(this.getCurrentStateTime() +
												"|interrupt transmission.");

			// Switch to interrupted state.
			this.currentState = State.INTERRUPTED;

			// Include a new point in the event occurrences function (report)
			this.connectionFunction.add(
				new BooleanPiece(
							this.timeOfLastResumption,
							this.getCurrentStateTime().getSimulatedTime(),
							false));
			this.timeOfLastInterruption =
							this.getCurrentStateTime().getSimulatedTime();
			// Update the statistics for the report
			double d = this.getCurrentStateTime().getSimulatedTime() -
												this.timeOfLastResumption;
			this.totalTime += d;
			this.uptime += d;

			// Generate the next resumption event after a random delay and
			// schedule it to be triggered at the corresponding time.
			this.scheduleEvent(this.generateNextResumptionEvent(
												this.getCurrentStateTime()));

			// Include a new point in the event occurrences function (report)
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						1.0);
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0);
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		// advance the time at the current (ending) time and finish the
		// event occurrences function for the report.
		double end = endTime.getSimulatedTime();
		if (this.currentState == State.INTERRUPTED) {
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 0.0);
			}
		} else {
			assert	this.currentState == State.CONNECTED;
			if (this.plotter != null) {
				this.plotter.addData(SERIES, end, 1.0);
			}
		}
		// end the simulation run for this model.
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		// Collect the event occurrences function and the statistics to
		// create the report and return it.
		Time end = this.getSimulationEngine().getSimulationEndTime();
		if (this.timeOfLastInterruption < end.getSimulatedTime() &&
						this.timeOfLastResumption < end.getSimulatedTime()) {
			if (this.timeOfLastInterruption < this.timeOfLastInterruption) {
				// The last event was an interruption
				this.connectionFunction.add(
						new BooleanPiece(this.timeOfLastInterruption,
										 end.getSimulatedTime(),
										 true));
			} else {
				// The last event was an resumption
				this.connectionFunction.add(
						new BooleanPiece(this.timeOfLastResumption,
										 end.getSimulatedTime(),
										 false));
				
			}
		}
		return new WiFiDisconnectionReport(
								this.getURI(),
								this.numberOfGeneratedInterruptions,
								this.uptime/this.totalTime,
								this.connectionFunction);
	}

	// -------------------------------------------------------------------------
	// WiFi disconnection model proper methods
	// -------------------------------------------------------------------------

	/**
	 * create and return the next interruption event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @return			the next interruption event.
	 */
	protected InterruptionEvent		generateNextInterruption(Time current)
	{
		// Generate the random delay until the next interruption
		double delay =
			this.rgInterruptionIntervals.nextExponential(
										this.meanTimeBetweenInterruptions);
		// Compute the corresponding tie of occurrence
		Time interruptionOccurrenceTime =
			current.add(new Duration(delay, this.getSimulatedTimeUnit()));
		// Create the interruption event at the corresponding time of occurrence
		InterruptionEvent gie =
						new InterruptionEvent(interruptionOccurrenceTime);
		// Update the statistics
		this.numberOfGeneratedInterruptions++;
		// Return the created event
		return gie;
	}

	/**
	 * create and return the next resumption event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current simulation time.
	 * @return			the next resumption event.
	 */
	protected ResumptionEvent 	generateNextResumptionEvent(Time current)
	{
		// Generate the random delay until the next interruption
		double interruptedTime =
				this.rgInterruptionDurations.nextExponential(
											this.meanInterruptionDuration);
		Time endOfInterruption =
				current.add(new Duration(interruptedTime,
							this.getSimulatedTimeUnit()));
		// Create and return the resumption event at the corresponding
		// time of occurrence
		return new ResumptionEvent(endOfInterruption);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#modelContentAsString(java.lang.String, java.lang.StringBuffer)
	 */
	@Override
	protected void		modelContentAsString(String indent, StringBuffer sb)
	{
		super.modelContentAsString(indent, sb);
		sb.append(indent);
		sb.append("interrupted = ");
		sb.append(this.currentState);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "interrupted = " + this.currentState);
	}
}
// -----------------------------------------------------------------------------
