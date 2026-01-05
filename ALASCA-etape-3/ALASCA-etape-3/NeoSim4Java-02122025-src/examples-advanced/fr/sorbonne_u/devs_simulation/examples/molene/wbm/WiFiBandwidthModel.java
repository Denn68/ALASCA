package fr.sorbonne_u.devs_simulation.examples.molene.wbm;

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
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.util.ArrayList;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.plotters.PlotterDescription;
import fr.sorbonne_u.plotters.XYPlotter;

/**
 * The class <code>WiFiBandwidthModel</code> defines a model of WiFi bandwidth
 * evolution over time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This model assumes that the WiFi bandwidth follows a brownian motion
 * interrupted by random interruptions which durations are also random.
 * Interruptions and resumptions are represented by events which are
 * imported.
 * </p>
 * <p>
 * The brownian motion is defined in two parts. The continuous evolution is
 * defined by a stochastic differential equation but which is arranged in such
 * a way to have a solution that always remain between 0 and a user defined
 * maximum bandwidth. When interrupted, the bandwidth goes to 0 until
 * resumption. At resumption, the first bandwidth is a random value between
 * 0 and the maximum bandwidth.
 * </p>
 * 
 * <ul>
 * <li>Imported events: {@code InterruptionEvent}, {@code ResumptionEvent}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: name = {@code bandwidth}, type = {@code Double}</li>
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
@ModelExternalEvents(imported = {InterruptionEvent.class,
								 ResumptionEvent.class})
@ModelExportedVariable(name = "bandwidth", type = Double.class)
// -----------------------------------------------------------------------------
public class			WiFiBandwidthModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiBandwithReport</code> implements the simulation
	 * report for the WiFi bandwidth model.
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
	public static class	WiFiBandwidthReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L;
		public final Vector<DoublePiece>		bandwidthFunction;

		public			WiFiBandwidthReport(
			String modelURI,
			Vector<DoublePiece> bandwidthFunction
			)
		{
			super(modelURI);
			this.bandwidthFunction = bandwidthFunction;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n";
			ret += "WiFi Bandwidth Report\n";
			ret += "-----------------------------------------\n";
			ret += "bandwidth function = \n";
			for (int i = 0 ; i < this.bandwidthFunction.size() ; i++) {
				ret += "    " + this.bandwidthFunction.get(i) + "\n";
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
	private static final String	SERIES = "wifi bandwidth";
	public static final String	URI = "wifiBandwidthModel-1";

	// Run parameter names to be used when initialising them before each run
	/** name of the run parameter defining the maximum bandwidth.			*/
	public static final String	MAX_BANDWIDTH = "max-bandwidth";
	/** name of the run parameter defining the alpha parameter of the gamma
	 *  probability distribution giving the bandwidth at resumption.		*/
	public static final String	BAAR = "bandwidth-alpha-at-resumption";
	/** name of the run parameter defining the beta parameter of the gamma
	 *  probability distribution giving the bandwidth at resumption.		*/
	public static final String	BBAR = "bandwidth-beta-at-resumption";
	/** name of the run parameter defining the mean slope of the bandwidth
	 *  i.e., the mean parameter of the exponential distribution.			*/
	public static final String	BMSF = "bandwidth-mean-scale-factor";
	/** name of the run parameter defining the integration step for the
	 *  brownian motion followed by the bandwidth.							*/
	public static final String	BIS = "bandwidth-integration-step";

	// Model implementation variables
	/** the maximum bandwidth												*/
	protected double					maxBandwidth;
	/** the alpha parameter of the gamma probability distribution giving
	 *  the bandwidth at resumption.										*/
	protected double					bandwidthAlphaAtResumption;
	/** the beta parameter of the gamma probability distribution giving
	 *  the bandwidth at resumption.										*/
	protected double					bandwidthBetaAtResumption;
	/** the mean slope of the bandwidth i.e., the mean parameter of the
	 *  exponential distribution.											*/
	protected double					bandwidthMeanScaleFactor;
	/** the predefined integration step for the brownian motion followed
	 *  by the bandwidth, which can be punctually updated at run time
	 *  when necessary.														*/
	protected double					standardIntegrationStep;

	/**	Random number generator for the bandwidth after resumption;
	 *  the bandwidth after resumption follows a beta distribution.			*/
	protected final RandomDataGenerator	rgNewBandwidthLevel;

	/** Random number generator for the bandwidth continuous evolution;
	 * 	the bandwidth brownian motion uses an exponential and a
	 *  uniform distribution.												*/
	protected final RandomDataGenerator	rgBrownianMotion1;
	protected final RandomDataGenerator	rgBrownianMotion2;

	/** the value of the bandwidth at the next internal transition time.	*/
	protected double					nextBandwidth;
	/** delay until the next update of the bandwidth value.					*/
	protected double					nextIntegrationStep;
	/** time at which the last disconnection ended.							*/
	protected Time						endTimeOfLastDisconnection;
	/** current state of the model.											*/
	protected State						currentState;

	// Bandwidth function and statistics for the report
	/** average bandwidth during the simulation run.						*/
	protected double					averageBandwidth;
	/** function giving the bandwidth at all time during the
	 *  simulation run.														*/
	protected final Vector<DoublePiece>	bandwidthFunction;

	/** Frame used to plot the bandwidth during the simulation.				*/
	protected XYPlotter					plotter;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Wifi bandwidth in megabits per second.								*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>		bandwidth = new Value<Double>(this);

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi bandwidth model.
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
	public				WiFiBandwidthModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger());

		// Create the random number generators
		this.rgNewBandwidthLevel = new RandomDataGenerator();
		this.rgBrownianMotion1 = new RandomDataGenerator();
		this.rgBrownianMotion2 = new RandomDataGenerator();
		// Create the representation of the bandwidth function for the report
		this.bandwidthFunction = new Vector<DoublePiece>();

		assert	this.bandwidth != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#finalise()
	 */
	@Override
	public void		finalise()
	{
		if (this.plotter != null) {
			this.plotter.dispose();
			this.plotter = null;
		}
		super.finalise();
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
		String vname =
			ModelI.createRunParameterName(this.getURI(),
										  WiFiBandwidthModel.MAX_BANDWIDTH);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.maxBandwidth = (double) simParams.get(vname);

		vname = ModelI.createRunParameterName(this.getURI(),
											  WiFiBandwidthModel.BAAR);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.bandwidthAlphaAtResumption = (double) simParams.get(vname);

		vname = ModelI.createRunParameterName(this.getURI(),
											  WiFiBandwidthModel.BBAR);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.bandwidthBetaAtResumption = (double) simParams.get(vname);

		vname = ModelI.createRunParameterName(this.getURI(),
											  WiFiBandwidthModel.BMSF);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.bandwidthMeanScaleFactor = (double) simParams.get(vname);		

		vname = ModelI.createRunParameterName(this.getURI(),
											  WiFiBandwidthModel.BIS);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.standardIntegrationStep = (double) simParams.get(vname);

		// Initialise the look of the plotter
		vname = ModelI.createRunParameterName(
									this.getURI(),
									PlotterDescription.PLOTTING_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.plotter = new XYPlotter(pd);
		this.plotter.createSeries(SERIES);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		// initialisation of the random number generators
		this.rgNewBandwidthLevel.reSeedSecure();
		this.rgBrownianMotion1.reSeedSecure();
		this.rgBrownianMotion2.reSeedSecure();

		// the model starts in the interrupted state waiting for resumption 
		this.endTimeOfLastDisconnection = initialTime;
		this.currentState = State.INTERRUPTED;

		// initialisation of the bandwidth function for the report
		this.bandwidthFunction.clear();
		// initialisation of the bandwidth function plotter on the screen
		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		// standard initialisation
		super.initialiseState(initialTime);
	}

	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		// Initialise the model variables, part of the initialisation protocol
		// of HIOA
		double newBandwidth = this.generateBandwidthAtResumption();
		this.bandwidth.initialise(newBandwidth);
		// compute the next bandwidth from the initial state of the model
		this.computeNextBandwidth();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.currentState == State.CONNECTED) {
			return new Duration(this.nextIntegrationStep,
								this.getSimulatedTimeUnit());
		} else {
			assert	this.currentState == State.INTERRUPTED;
			// the model will resume its internal transitions when it will
			// receive the corresponding triggering external event.
			return Duration.INFINITY;
		}
	}

	/**
	 * generate a new bandwidth using a beta distribution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret >= 0.0 && ret <= this.maxBandwidth}
	 * </pre>
	 *
	 * @return	a randomly generated bandwidth
	 */
	protected double	generateBandwidthAtResumption()
	{
		// Generate a random bandwidth value at resumption using the Beta
		// distribution 
		double newBandwidth =
			this.maxBandwidth *
						this.rgNewBandwidthLevel.nextBeta(
										this.bandwidthAlphaAtResumption,
										this.bandwidthBetaAtResumption);
		assert	newBandwidth >= 0.0 && newBandwidth <= this.maxBandwidth;
		return newBandwidth;
	}

	/**
	 * compute the next bandwidth depending on the state of the model,
	 * connected or interrupted.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 */
	protected void		computeNextBandwidth()
	{
		// For stochastic differential equations, the method compute the
		// next value from a stochastic quantum rather than derivatives.
		// Here, because the bandwidth must remain between 0 and maxBandwidth,
		// the quantum may be limited, hence changing the delay until the limit
		// is reached. This delay becomes the next time advance of the model.

		if (this.currentState == State.CONNECTED) {
			// To generate a random number following an exponential
			// distribution, the inverse method says to generate a uniform
			// random number u following U[0,1] and then compute
			// x = -M * ln(1 - u); x then follows an exponential distribution
			// with mean M. As we know that if 1 - u follows U[0,1] so is u,
			// hence x = -M * ln(u) also follows an exponential distribution
			// with mean M.
			// Here M = integration step size * predefined scale factor
			double meanBrownian =
						this.standardIntegrationStep *
									this.bandwidthMeanScaleFactor;
			double u4quantum = this.rgBrownianMotion1.nextUniform(0.0, 1.0);
			double quantum = -Math.log(u4quantum) * meanBrownian;

			double u4sign = this.rgBrownianMotion2.nextUniform(0.0, 1.0);
			double currentBandwidth =
					this.bandwidth.evaluateAt(this.getCurrentStateTime());
			double threshold =
					(this.maxBandwidth - currentBandwidth)/this.maxBandwidth;

			if (Math.abs(u4sign - threshold) < 0.000001) {
				// the quantum is fixed at 0 to cope for the limit case
				this.nextBandwidth = currentBandwidth;
				this.nextIntegrationStep = this.standardIntegrationStep;
			} else {
				if (u4sign < threshold) {
					// the quantum is positive i.e., the bandwidth increases
					double limit = this.maxBandwidth - currentBandwidth;
					if (quantum > limit) {
						// the bandwidth cannot go over the maximum
						this.nextBandwidth = this.maxBandwidth;
						this.nextIntegrationStep =
							(limit / quantum) * this.standardIntegrationStep;
					} else {
						this.nextBandwidth = currentBandwidth + quantum;
						this.nextIntegrationStep = this.standardIntegrationStep;
					}
				} else {
					// the quantum is negative i.e., the bandwidth decreases
					assert	u4sign > threshold;
					double limit = currentBandwidth;
					if (quantum > limit) {
						// the bandwidth cannot go under 0
						this.nextBandwidth = 0.0;
						this.nextIntegrationStep =
							(limit / quantum) * this.standardIntegrationStep;
					} else {
						this.nextBandwidth = currentBandwidth - quantum;
						this.nextIntegrationStep = this.standardIntegrationStep;
					}
				}
			}
		} else {
			// When interrupted, the bandwidth remains at 0 until resumption
			assert	this.currentState == State.INTERRUPTED;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		if (this.getSimulationEngine().hasDebugLevel(1)) {
			this.logMessage("WiFiBandwidthModel#userDefinedInternalTransition "
							+ elapsedTime);
		}
		if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
			super.userDefinedInternalTransition(elapsedTime);

			// the next variables are used for plotting and logging purposes
			// mainly
			double oldBandwith = this.bandwidth.getValue();
			Time oldTime = this.bandwidth.getTime();

			double newBandwidth = oldBandwith;
			if (this.currentState == State.CONNECTED) {
				// the value of the bandwidth at the next internal transition
				// is computed in the timeAdvance function when computing
				// the delay until the next internal transition.
				newBandwidth = this.nextBandwidth;
			}
			this.bandwidth.setNewValue(newBandwidth, this.getCurrentStateTime());
			this.computeNextBandwidth();

			// visualisation and simulation report.
			this.bandwidthFunction.add(
				new DoublePiece(oldTime.getSimulatedTime(),
								oldBandwith,
								this.getCurrentStateTime().getSimulatedTime(),
								newBandwidth));
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						newBandwidth);
			}
			this.logMessage(this.getCurrentStateTime() +
					"|internal|bandwidth = " + newBandwidth + " Mbps.");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.getSimulationEngine().hasDebugLevel(1)) {
			this.logMessage("WiFiBandwithModel#userDefinedExternalTransition "
							+ elapsedTime);
		}
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		if (currentEvents.get(0) instanceof ResumptionEvent) {
			// visualisation and simulation report.
			try {
				Time start = this.getSimulationEngine().getTimeOfStart();
				if (this.getCurrentStateTime().greaterThan(start) &&
						elapsedTime.greaterThan(
									Duration.zero(getSimulatedTimeUnit()))) {
					this.bandwidthFunction.add(
						new DoublePiece(
								this.bandwidth.getTime().getSimulatedTime(),
								0.0,
								this.getCurrentStateTime().getSimulatedTime(),
								0.0));
					if (this.plotter != null) {
						this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(), 
							0.0);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// compute new state after resumption
			double newBandwidth = this.generateBandwidthAtResumption();
			this.bandwidth.setNewValue(newBandwidth, this.getCurrentStateTime());
			this.currentState = State.CONNECTED;
			this.computeNextBandwidth();
			this.endTimeOfLastDisconnection = this.getCurrentStateTime();

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0);
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						newBandwidth);
			}
			this.logMessage(this.getCurrentStateTime() +
									"|external|resume with bandwidth " +
									this.bandwidth.getValue() + " Mbps.");
		} else {
			assert	currentEvents.get(0) instanceof InterruptionEvent;
			// visualisation and simulation report.
			if (elapsedTime.greaterThan(Duration.zero(getSimulatedTimeUnit()))) {
				this.bandwidthFunction.add(
						new DoublePiece(
								this.bandwidth.getTime().getSimulatedTime(),
								this.bandwidth.getValue(),
								this.getCurrentStateTime().getSimulatedTime(),
								this.bandwidth.getValue()));
				if (this.plotter != null) {
					this.plotter.addData(
							SERIES,
							this.getCurrentStateTime().getSimulatedTime(),
							this.bandwidth.getValue());
				}
			}
			this.bandwidth.setNewValue(0.0, this.getCurrentStateTime());
			this.endTimeOfLastDisconnection = this.getCurrentStateTime();
			this.currentState = State.INTERRUPTED;
			this.computeNextBandwidth();

			// visualisation and simulation report.
			if (this.plotter != null) {
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						this.bandwidth.getValue());
				this.plotter.addData(
						SERIES,
						this.getCurrentStateTime().getSimulatedTime(),
						0.0);
			}
			this.logMessage(this.getCurrentStateTime() +
												"|external|interrupt.");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.bandwidthFunction.add(
				new DoublePiece(
						this.endTimeOfLastDisconnection.getSimulatedTime(),
						this.bandwidth.getValue(),
						endTime.getSimulatedTime(),
						this.bandwidth.getValue()));
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new WiFiBandwidthReport(this.getURI(), this.bandwidthFunction);
	}
}
// -----------------------------------------------------------------------------
