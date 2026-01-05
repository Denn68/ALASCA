package fr.sorbonne_u.devs_simulation.examples.molene.controllers;

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

import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Map;
import fr.sorbonne_u.devs_simulation.examples.molene.Decision;
import fr.sorbonne_u.devs_simulation.examples.molene.State;
import fr.sorbonne_u.devs_simulation.examples.molene.bsm.BatteryLevel;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.Compressing;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.LowBattery;
import fr.sorbonne_u.devs_simulation.examples.molene.pcsm.NotCompressing;
import fr.sorbonne_u.devs_simulation.examples.molene.wbsm.WiFiBandwidthReading;
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

// -----------------------------------------------------------------------------
/**
 * The class <code>ControllerModel</code> defines a common model between the
 * portable computer controller and the server controller.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The controller model has three states: compressing, not compressing and
 * low battery level. It imports two types of events providing bandwidth
 * measurements and battery level measurements. Given the received values,
 * it triggers decisions to go to not compressing and compressing or to
 * go to the low battery state. These decisions are exported as three
 * types of events.
 * </p>
 * <p>
 * Because in the DEVS protocol, the processing of an external event cannot
 * trigger the issuing of an external event, the above behaviour is implemented
 * through three steps:
 * </p>
 * <ol>
 * <li>when an external event, battery reading or bandwidth reading, is
 *   received and processed, if it must trigger a mode change for the
 *   system, the new decision is computed ans put in the variable
 *   <code>triggeredDecision</code> and then the boolean variable
 *   <code>mustTransmitDecision</code> is put at true;</li>
 * <li>when the <code>mustTransmitDecision</code> is true, the method
 *   <code>timeAdvance</code> returns a 0 time delay until the next internal
 *   transition;</li>
 * <li>the triggered internal transition first requires a call to the method
 *   <code>output</code>, which  emits the decision event;</li>
 * <li>the internal transition is then executed and toggles
 *   <code>mustTransmitDecision</code> back to false.</li>
 * </ol>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code BatteryLevel},
 *   {@code WiFiBandwidthReading}</li>
 * <li>Exported events:
 *   {@code Compressing},
 *   {@code NotCompressing},
 *   {@code LowBattery}</li>
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
 * <p>Created on : 2018-10-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(
	imported = {BatteryLevel.class, WiFiBandwidthReading.class},
	exported = {Compressing.class, NotCompressing.class, LowBattery.class})
// -----------------------------------------------------------------------------
public class			ControllerModel
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>DecisionPiece</code> implements a piece in a piecewise
	 * representation of the observed decision function.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// TODO
	 * </pre>
	 * 
	 * <p>Created on : 2019-11-05</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	DecisionPiece
	{
		public final double		first;
		public final double		last;
		public final Decision	d;

		public			DecisionPiece(
			double first,
			double last,
			Decision d
			)
		{
			super();
			this.first = first;
			this.last = last;
			this.d = d;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "(" + this.first + ", " + this.last + ", " + this.d + ")";
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	private static final String	SERIES = "state/last decision";
	public static final String	PORTABLE_URI =
										"PortableComputerControllerModel";
	public static final String	SERVER_URI = "ServerControllerModel";
	/** bandwidth threshold over which no compression is required.			*/
	public static double		HIGH_THRESHOLD = 25; // in Mbits/sec.
	/** bandwidth threshold under which compression is required.			*/
	public static double		LOW_THRESHOLD = 21; // in Mbits/sec.
	/** battery level under which compression is no longer allowed.			*/
	public static double		LOW_BATTERY_LEVEL = 2000; // mAh

	/** the last decision that was triggered by an external event.			*/
	protected Decision						triggeredDecision;
	/** true when a new triggered decision must be emitted.					*/
	protected boolean						mustTransmitDecision;
	/** the last value of the bandwidth received by the model.				*/
	protected double						currentBandwith;
	/** the last value of battery level received by the model.				*/
	protected double						currentBatteryLevel;
	/** the current state of the system.									*/
	protected State							currentState;
	/** the last decision that was emitted by the model.					*/
	protected Decision						lastDecision;
	/** the simulation time when the last decision was emitted.				*/
	protected double						lastDecisionChangeTime;
	/** a piecewise representation of the observed decision function.		*/
	protected final Vector<DecisionPiece>	decisionFunction;

	/** Frame used to plot the bandwidth readings during the simulation.	*/
	protected XYPlotter						plotter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of controller model.
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
	public				ControllerModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.decisionFunction = new Vector<DecisionPiece>();

		this.getSimulationEngine().setLogger(new StandardLogger());
//		this.setDebugLevel(1);
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
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		if (simParams == null) {
			throw new MissingRunParameterException("no run parameters!");
		}

		String vname =
			ModelI.createRunParameterName(
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
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.triggeredDecision = Decision.DO_NOT_COMPRESS;
		this.currentState = State.NOT_COMPRESSING;
		this.lastDecision = Decision.DO_NOT_COMPRESS;
		this.mustTransmitDecision = false;
		this.lastDecisionChangeTime = initialTime.getSimulatedTime();
		this.decisionFunction.clear();
		this.currentBandwith = -1.0;
		this.currentBatteryLevel = -1.0;

		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(this.currentState));
		}
	}

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
	 * @param s	a state for the controller.
	 * @return	an integer representation to ease the plotting.
	 */
	protected int		stateToInteger(State s)
	{
		assert	s != null;

		if (s == State.LOW_BATTERY) {
			return 1;
		} else if (s == State.NOT_COMPRESSING) {
			return 2;
		} else {
			assert	s == State.COMPRESSING;
			return 3;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.getSimulationEngine().hasDebugLevel(1)) {
			this.logMessage("output|"
							+ this.lastDecision + " "
							+ this.triggeredDecision);
		}
		ArrayList<EventI> ret = new ArrayList<EventI>(1);
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
		if (this.triggeredDecision == Decision.COMPRESS) {
			ret.add(new Compressing(t, null));
		} else if (this.triggeredDecision == Decision.DO_NOT_COMPRESS) {
			ret.add(new NotCompressing(t, null));
		} else {
			assert	this.triggeredDecision == Decision.BATTERY_TOO_LOW;
			ret.add(new LowBattery(t, null));
		}
	
		this.decisionFunction.add(
				new DecisionPiece(this.lastDecisionChangeTime,
							  t.getSimulatedTime(),
							  this.lastDecision));

		this.lastDecision = this.triggeredDecision;
		this.lastDecisionChangeTime = t.getSimulatedTime();
		this.mustTransmitDecision = false;
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.mustTransmitDecision) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		if (this.getSimulationEngine().hasDebugLevel(1)) {
			this.logMessage("userDefinedExternalTransition|"
								+ this.currentState + ">>>>>>>>>>>>>>>");
		}
		ArrayList<EventI> current = this.getStoredEventAndReset();
		for (int i = 0; i < current.size(); i++) {
			if (current.get(i) instanceof BatteryLevel) {
				this.currentBatteryLevel =
						((BatteryLevel.Reading)
							((BatteryLevel)current.get(i)).
											getEventInformation()).value;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|state = " + this.currentState
						+ "|battery level = " + this.currentBatteryLevel);
			} else if (current.get(i) instanceof WiFiBandwidthReading) {
				this.currentBandwith =
					((WiFiBandwidthReading.Reading)
							((WiFiBandwidthReading)current.get(i)).
											getEventInformation()).value;
				this.logMessage("userDefinedExternalTransition|"
						+ this.getCurrentStateTime()
						+ "|state = " + this.currentState
						+ "|bandwidth = " + this.currentBandwith);
			}
		}
		State oldState = this.currentState;
		if (this.currentState == State.COMPRESSING) {
			if (this.currentBatteryLevel > 0 &&
							this.currentBatteryLevel < LOW_BATTERY_LEVEL) {
				this.triggeredDecision = Decision.BATTERY_TOO_LOW;
				this.currentState = State.LOW_BATTERY;
				this.mustTransmitDecision = true;
				this.logMessage("userDefinedExternalTransition|"
				 				+ this.getCurrentStateTime() 
				 				+ "|new state = " + this.currentState);
			} else if (this.currentBandwith > 0.0 &&
									this.currentBandwith > HIGH_THRESHOLD) {
				this.triggeredDecision = Decision.DO_NOT_COMPRESS;
				this.currentState = State.NOT_COMPRESSING;
				this.mustTransmitDecision = true;
				this.logMessage("userDefinedExternalTransition|"
						 		+ this.getCurrentStateTime() 
						 		+ "|new state = " + this.currentState);
			}
		} else if (this.currentState == State.NOT_COMPRESSING) {
			if (this.currentBatteryLevel > 0 &&
							this.currentBatteryLevel < LOW_BATTERY_LEVEL) {
				this.triggeredDecision = Decision.BATTERY_TOO_LOW;
				this.currentState = State.LOW_BATTERY;
				this.mustTransmitDecision = true;
				this.logMessage("userDefinedExternalTransition|"
				 				+ this.getCurrentStateTime() 
				 				+ "|new state = " + this.currentState);
			} if (this.currentBandwith > 0.0 &&
									this.currentBandwith < LOW_THRESHOLD) {
				this.triggeredDecision = Decision.COMPRESS;
				this.currentState = State.COMPRESSING;
				this.mustTransmitDecision = true;
				this.logMessage("userDefinedExternalTransition|"
								+ this.getCurrentStateTime() 
								+ "|new state = " + this.currentState);
			}
		} else {
			assert	this.currentState == State.LOW_BATTERY;
			// Do nothing
			this.triggeredDecision = Decision.BATTERY_TOO_LOW;
		}

		if (this.plotter != null && oldState != this.currentState) {
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(oldState));
			this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.stateToInteger(this.currentState));

		}
		if (this.getSimulationEngine().hasDebugLevel(1)) {
			this.logMessage("userDefinedExternalTransition|"
							+ this.currentState + "<<<<<<<<<<<<<<<<<<<");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		if (this.plotter != null) {
			this.plotter.addData(
				SERIES,
				endTime.getSimulatedTime(),
				this.stateToInteger(this.currentState));
		}
		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	{
		final String uri = this.uri;
		return new SimulationReportI() {
					private static final long serialVersionUID = 1L;
					@Override
					public String getModelURI() {
						return uri;
					}
					@Override
					public String toString() {
						return this.getModelURI() + "Report";
					}				
				};
	}
}
// -----------------------------------------------------------------------------
