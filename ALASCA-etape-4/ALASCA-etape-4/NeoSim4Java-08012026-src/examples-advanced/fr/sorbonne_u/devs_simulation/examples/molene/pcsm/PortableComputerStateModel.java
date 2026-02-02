package fr.sorbonne_u.devs_simulation.examples.molene.pcsm;

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
import fr.sorbonne_u.devs_simulation.examples.molene.State;
import fr.sorbonne_u.devs_simulation.examples.molene.utils.DoublePiece;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.InterruptionEvent;
import fr.sorbonne_u.devs_simulation.examples.molene.wdm.ResumptionEvent;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.plotters.PlotterDescription;
import fr.sorbonne_u.plotters.XYPlotter;

/**
 * The class <code>PortableComputerStateModel</code> implements an atomic model
 * for a portable computer in the Molene example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model has two main functions: keep the current state of the compression
 * mode and simulate the battery level. In order to perform these functions,
 * it must be an HIOA (to have and export a continuous variable for the battery
 * level).
 * </p>
 * <p>
 * When modelling the Molene example, the battery level evolution has been
 * assumed to depend upon the compression mode and the availability of the
 * network. Hence, the model imports two events from the WiFi bandwidth model
 * saying when the WiFi is interrupted or not, and it also imports three
 * events coming from the controller telling when to go to a compression mode
 * to another.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code InterruptionEvent},
 *   {@code ResumptionEvent},
 *   {@code Compressing},
 *   {@code NotCompressing},
 *   {@code LowBattery}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables:
 *   name = {@code remainingCapacity}, type = {@code Double}</li>
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
 * <p>Created on : 2018-07-20</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {InterruptionEvent.class,
								 ResumptionEvent.class,
								 Compressing.class,
								 NotCompressing.class,
								 LowBattery.class})
@ModelExportedVariable(name = "remainingCapacity", type = Double.class)
// -----------------------------------------------------------------------------
public class			PortableComputerStateModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>PortableComputerReport</code> implements the simulation
	 * report for the portable computer state model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2018-10-09</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	PortableComputerStateReport
	extends		AbstractSimulationReport
	{
		private static final long			serialVersionUID = 1L;
		public final Vector<DoublePiece>	batteryFunction;

		public			PortableComputerStateReport(
			String modelURI,
			Vector<DoublePiece> batteryFunction
			)
		{
			super(modelURI);
			this.batteryFunction = batteryFunction;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n";
			ret += "Portable Computer State Report\n";
			ret += "-----------------------------------------\n";
			ret += "battery level function = \n";
			for (int i = 0 ; i < this.batteryFunction.size() ; i++) {
				ret += "    " + this.batteryFunction.get(i) + "\n";
			}
			ret += "-----------------------------------------\n";
			return ret;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 	serialVersionUID = 1L;

	public static final String	URI = "PortableComputerStateModel-1";
	public static final String	EVALUATION_STEP_PARAM_NAME = "eval-step";

	public static double		INITIAL_CAPACITY = 7500; // mAh
	public static double		MEAN_AUTONOMY = 7500; // seconds i.e., > 2h
	/** rate of battery draining when compressing.							*/
	public static double		HIGH_RATE =
										1.5 * INITIAL_CAPACITY/MEAN_AUTONOMY;
	/** rate of battery draining when not compressing.						*/
	public static double		LOW_RATE =
										1.0 * INITIAL_CAPACITY/MEAN_AUTONOMY;
	/** rate of battery draining when the WiFi is interrupted.				*/
	public static double		NO_RATE =
										0.5 * INITIAL_CAPACITY/MEAN_AUTONOMY;


	/** current state of the portable computer.								*/
	protected State				currentState;
	/** delay between the reevaluation of the battery level.				*/
	protected double			evaluationStepSize;
	/** the duration instantiated from {@code evaluationStepSize}.			*/
	protected Duration			evaluationStep;

	/** name of the data series plotting the portable computer state.		*/
	private static final String	SERIES1 = "PC state";
	/** name of the data series plotting the battery level.					*/
	private static final String	SERIES2 = "battery level";

	/** name of the description for the battery level plot.					*/
	public static final String	BATTERY_PLOTTING_PARAM_NAME = "battery-plotting";
	/** name of the description for the portable computer state plot.		*/
	public static final String	STATE_PLOTTING_PARAM_NAME = "state-plotting";

	/** battery level function as observed, for the simulation report.		*/
	protected final Vector<DoublePiece>	batteryFunction;
	/** Frame used to plot the battery level during the simulation.			*/
	protected XYPlotter			batteryLevelPlotter;
	/** Frame used to plot the state during the simulation.					*/
	protected XYPlotter			statePlotter;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** remaining capacity in the battery.									*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	remainingCapacity = new Value<Double>(this);

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create an instance of portable computer state model.
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
	public				PortableComputerStateModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.batteryFunction = new Vector<DoublePiece>();
		this.getSimulationEngine().setLogger(new StandardLogger());
		this.getSimulationEngine().setDebugLevel(1);
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
		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.dispose();
			this.batteryLevelPlotter = null;
		}
	}

	// ------------------------------------------------------------------------
	// Model specific methods
	// ------------------------------------------------------------------------

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
	 * @param s	a state for the portable computer.
	 * @return	an integer representation to ease the plotting.
	 */
	public static int	state2int(State s)
	{
		assert	s != null;

		if (s == State.COMPRESSING) {
			return 6;
		} else if (s == State.INTERRUPTED_C) {
			return 5;
		} else if (s == State.NOT_COMPRESSING) {
			return 4;
		} else if (s == State.INTERRUPTED_NC) {
			return 3;
		} else if (s == State.LOW_BATTERY) {
			return 2;
		} else {
			assert	s == State.INTERRUPTED_LB;
			return 1;
		}
	}

	// ------------------------------------------------------------------------
	// Simulation protocol and related methods
	// ------------------------------------------------------------------------

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

		String vname =
			ModelI.createRunParameterName(
						this.getURI(),
						PortableComputerStateModel.EVALUATION_STEP_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		this.evaluationStepSize = (double) simParams.get(vname);

		vname =
			ModelI.createRunParameterName(
						this.getURI(),
						PortableComputerStateModel.BATTERY_PLOTTING_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		PlotterDescription pd = (PlotterDescription) simParams.get(vname);
		this.batteryLevelPlotter = new XYPlotter(pd);
		this.batteryLevelPlotter.createSeries(SERIES2);

		vname =
			ModelI.createRunParameterName(
						this.getURI(),
						PortableComputerStateModel.STATE_PLOTTING_PARAM_NAME);
		if (!simParams.containsKey(vname)) {
			throw new MissingRunParameterException(vname);
		}
		pd = (PlotterDescription) simParams.get(vname);
		this.statePlotter = new XYPlotter(pd);
		this.statePlotter.createSeries(SERIES1);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.currentState = State.NOT_COMPRESSING;

		this.batteryFunction.clear();
		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.initialise();
			this.batteryLevelPlotter.showPlotter();
		}
		if (this.statePlotter != null) {
			this.statePlotter.initialise();
			this.statePlotter.showPlotter();
		}

		this.evaluationStep = new Duration(this.evaluationStepSize,
										   this.getSimulatedTimeUnit());
		super.initialiseState(initialTime);

		if (this.statePlotter != null) {
			this.statePlotter.addData(
				SERIES1,
				this.getCurrentStateTime().getSimulatedTime(),
				state2int(this.currentState));
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();
		this.remainingCapacity.initialise(
								PortableComputerStateModel.INITIAL_CAPACITY);

		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.addData(
						SERIES2,
						this.remainingCapacity.getTime().getSimulatedTime(),
						this.remainingCapacity.getValue());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.evaluationStep;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// the model does not export events.
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		double delta_t = elapsedTime.getSimulatedDuration();
		this.computeNewLevel(delta_t);
	}

	/**
	 * compute a new battery level given the current state of the PC.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param delta_t	duration since the last computed level.
	 */
	protected void		computeNewLevel(double delta_t)
	{
		// This method implements a linear progression of the battery level,
		// which is so simple that we did not implement it as an explicit
		// differential equation.

		double oldCapacity = this.remainingCapacity.getValue();
		double oldTime = this.remainingCapacity.getTime().getSimulatedTime();
		if (this.currentState == State.COMPRESSING) {
			if (this.getSimulationEngine().hasDebugLevel(1)) {
				this.logMessage("HIGH_RATE");
			}
			this.remainingCapacity.setNewValue(
								oldCapacity - delta_t * HIGH_RATE,
								this.getCurrentStateTime());
		} else if (this.currentState == State.NOT_COMPRESSING ||
									this.currentState == State.LOW_BATTERY) {
			if (this.getSimulationEngine().hasDebugLevel(1)) {
				this.logMessage("LOW_RATE");
			}
			this.remainingCapacity.setNewValue(
									oldCapacity - delta_t * LOW_RATE,
									this.getCurrentStateTime());			
		} else {
			assert	this.currentState == State.INTERRUPTED_C ||
					this.currentState == State.INTERRUPTED_NC ||
					this.currentState == State.INTERRUPTED_LB;
			if (this.getSimulationEngine().hasDebugLevel(1)) {
				this.logMessage("NO_RATE");
			}
			this.remainingCapacity.setNewValue(
									oldCapacity - delta_t * NO_RATE,
									this.getCurrentStateTime());
		}

		double newTime = oldTime + delta_t;
		this.batteryFunction.add(
			new DoublePiece(oldTime,
							oldCapacity,
							newTime,
							this.remainingCapacity.getValue()));
		if (this.batteryLevelPlotter != null) {
			this.batteryLevelPlotter.addData(SERIES2,
											 newTime,
											 this.remainingCapacity.getValue());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		this.logMessage(
				"PortableComputerStateModel#userDefinedExternalTransition "
								+ this.currentState + " "
								+ currentEvents.get(0));
		assert	currentEvents != null && currentEvents.size() == 1;

		State oldState = this.currentState;

		// Compute the new state given the incoming event
		Event ce = (Event) currentEvents.get(0);
		if (this.currentState == State.COMPRESSING) {
			if (ce instanceof Compressing) {
				// not possible
			} else if (ce instanceof NotCompressing) {
				this.currentState = State.NOT_COMPRESSING;
			} else if (ce instanceof LowBattery) {
				this.currentState = State.LOW_BATTERY;
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_C;
			} else {
				assert	ce instanceof ResumptionEvent;
				// not possible
			}
		} else if (this.currentState == State.INTERRUPTED_C) {
			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				this.currentState = State.INTERRUPTED_LB;
			} else if (ce instanceof InterruptionEvent) {
				// not possible
			} else {
				assert	ce instanceof ResumptionEvent;
				this.currentState = State.COMPRESSING;
			}
		} else if (this.currentState == State.NOT_COMPRESSING) {
			if (ce instanceof Compressing) {
				this.currentState = State.COMPRESSING;
			} else if (ce instanceof NotCompressing) {
				// not possible
			} else if (ce instanceof LowBattery) {
				this.currentState = State.LOW_BATTERY;
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_NC;
			} else {
				assert	ce instanceof ResumptionEvent;
				// not possible
			}
		} else if (this.currentState == State.INTERRUPTED_NC) {
			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				this.currentState = State.INTERRUPTED_LB;
			} else if (ce instanceof InterruptionEvent) {
				// not possible.
			} else {
				assert	ce instanceof ResumptionEvent;
				this.currentState = State.NOT_COMPRESSING;
			}
		} else if (this.currentState == State.LOW_BATTERY) {
			if (ce instanceof Compressing) {
				// do nothing.
			} else if (ce instanceof NotCompressing) {
				// do nothing.
			} else if (ce instanceof LowBattery) {
				// not possible.
			} else if (ce instanceof InterruptionEvent) {
				this.currentState = State.INTERRUPTED_LB;
			} else {
				assert	ce instanceof ResumptionEvent;
				// not possible.
			}
		} else {
			assert	this.currentState == State.INTERRUPTED_LB;

			if (ce instanceof Compressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof NotCompressing) {
				// not possible, the WiFi is interrupted.
			} else if (ce instanceof LowBattery) {
				// not possible.
			} else if (ce instanceof InterruptionEvent) {
				// not possible.
			} else {
				assert	ce instanceof ResumptionEvent;
				this.currentState = State.LOW_BATTERY;
			}
		}

		if (this.statePlotter != null && oldState != this.currentState) {
			this.statePlotter.addData(
					SERIES1,
					this.getCurrentStateTime().getSimulatedTime(),
					state2int(oldState));
			this.statePlotter.addData(
					SERIES1,
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
		double delta_t = endTime.getSimulatedTime() -
							this.remainingCapacity.getTime().getSimulatedTime();
		this.computeNewLevel(delta_t);

		if (this.statePlotter != null) {
			this.statePlotter.addData(SERIES1,
									  endTime.getSimulatedTime(),
									  state2int(this.currentState));
		}

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new PortableComputerStateReport(this.getURI(),
										 this.batteryFunction);
	}
}
// -----------------------------------------------------------------------------
