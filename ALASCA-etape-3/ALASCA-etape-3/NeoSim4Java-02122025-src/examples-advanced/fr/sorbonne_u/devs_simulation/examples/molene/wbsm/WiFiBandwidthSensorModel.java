package fr.sorbonne_u.devs_simulation.examples.molene.wbsm;

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
import fr.sorbonne_u.devs_simulation.examples.molene.tic.TicEvent;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
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
 * The class <code>WiFiBandwidthSensorModel</code> implements a sensor model as
 * an HIOA that imports a variable giving the current bandwidth of the network
 * and exporting events that contains the value of the current bandwidth. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model shares with the <code>WiFiBandwidthModel</code> the continuous
 * variable <code>bandwidth</code> produced by the latter. It also imports the
 * event <code>TicEvent</code> from the <code>TicModel</code>. Each time the
 * model receives a tic event, it emits an exported even
 *  <code>WiFiBandwidthReading</code> containing the latest value of the
 * bandwidth.
 * </p>
 * <p>
 * Because in the DEVS protocol the processing of an external event cannot
 * trigger the issuing of an external event, the above behaviour is implemented
 * through three steps:
 * </p>
 * <ol>
 * <li>an external tic event is received and processed by toggling the
 *   boolean variable <code>triggerReading</code> at true;</li>
 * <li>when the <code>triggerReading</code> is true, the method
 *   <code>timeAdvance</code> returns a 0 time delay until the next internal
 *   transition;</li>
 * <li>the triggered internal transition first requires a call to the method
 *   <code>output</code>, which emits the battery level event;</li>
 * <li>the <code>triggerReading</code> is toggled back to false.</li>
 * </ol>
 * <p>
 * This process is iterated until the end of the simulation.
 * </p>
 * <p>
 * After the runs, the report returned by this model provides a piecewise
 * constant double function giving the readings of the WiFi bandwidth over
 * the whole simulation duration.
 * </p>
 * 
 * <ul>
 * <li>Imported events: {@code TicEvent}</li>
 * <li>Exported events: {@code WiFiBandwidthReading}</li>
 * <li>Imported variables: name = {@code bandwidth}, type = {@code Double}</li>
 * <li>Exported variables: none</li>
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
@ModelExternalEvents(imported = {TicEvent.class},
					 exported = {WiFiBandwidthReading.class})
@ModelImportedVariable(name = "bandwidth", type = Double.class)
// -----------------------------------------------------------------------------
public class			WiFiBandwidthSensorModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>WiFiBandwidthSensorReport</code> implements the
	 * simulation report for the WiFi bandwidth sensor model.
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
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	WiFiBandwidthSensorReport
	extends		AbstractSimulationReport
	{
		private static final long 					serialVersionUID = 1L;
		public final Vector<WiFiBandwidthReading>	readings;

		public			WiFiBandwidthSensorReport(
			String modelURI,
			Vector<WiFiBandwidthReading> readings
			)
		{
			super(modelURI);
			this.readings = readings;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "\n-----------------------------------------\n";
			ret += "WiFi Bandwidth Sensor Report\n";
			ret += "-----------------------------------------\n";
			ret += "number of readings = " + this.readings.size() + "\n";
			ret += "Readings:\n";
			for (int i = 0 ; i < this.readings.size() ; i++) {
				ret += "    " + this.readings.get(i).eventAsString() + "\n";
			}
			ret += "-----------------------------------------\n";
			return ret;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	private static final String		SERIES = "wifi bandwidth";
	public static final String		URI = "wifiBandwidthSensorModel-1";

	/** true when a external event triggered a reading.						*/
	protected boolean								triggerReading;
	/** the last value emitted as a reading of the bandwidth.			 	*/
	protected double								lastReading;
	/** the simulation time at the last reading.							*/
	protected double								lastReadingTime;
	/** history of readings, for the simulation report.						*/
	protected final Vector<WiFiBandwidthReading>	readings;

	/** frame used to plot the bandwidth readings during the simulation.	*/
	protected XYPlotter								plotter;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** Wifi bandwidth in megabits per second.								*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>							bandwidth;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an instance of the WiFi bandwidth sensor model.
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
	 * @throws Exception			<i>todo.</i>
	 */
	public				WiFiBandwidthSensorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		// Uncomment to get a log of the events.
		//this.setLogger(new StandardLogger());

		// Model implementation variable initialisation
		this.lastReading = -1.0;

		// Create the representation of the sensor bandwidth function
		this.readings = new Vector<WiFiBandwidthReading>();
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
		String vname =
				ModelI.createRunParameterName(
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
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.triggerReading = false;

		this.lastReadingTime = initialTime.getSimulatedTime();
		this.readings.clear();
		if (this.plotter != null) {
			this.plotter.initialise();
			this.plotter.showPlotter();
		}

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.triggerReading) {
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			return Duration.INFINITY;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.triggerReading) {
			this.lastReading = this.bandwidth.getValue();
			this.lastReadingTime = this.bandwidth.getTime().getSimulatedTime();

			if (this.plotter != null) {
				this.plotter.addData(
					SERIES,
					this.lastReadingTime,
					this.lastReading);
				this.plotter.addData(
					SERIES,
					this.getCurrentStateTime().getSimulatedTime(),
					this.lastReading);
			}

			ArrayList<EventI> ret = new ArrayList<EventI>(1);
			Time currentTime = 
					this.getCurrentStateTime().add(this.getNextTimeAdvance());
			WiFiBandwidthReading wbr =
					new WiFiBandwidthReading(currentTime, this.lastReading);
			ret.add(wbr);

			this.readings.addElement(wbr);
			this.logMessage(this.getCurrentStateTime() +
					"|output|bandwidth reading " +
					this.readings.size() + " with value = " +
					this.lastReading);

			this.triggerReading = false;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);
		this.logMessage(this.getCurrentStateTime() +
								"|internal|bandwidth = " +
								this.bandwidth.getValue() + " Mbps.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> current = this.getStoredEventAndReset();
		boolean	ticReceived = false;
		for (int i = 0 ; !ticReceived && i < current.size() ; i++) {
			if (current.get(i) instanceof TicEvent) {
				ticReceived = true;
			}
		}
		if (ticReceived) {
			this.triggerReading = true;
			this.logMessage(this.getCurrentStateTime() +
									"|external|tic event received.");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		if (this.plotter != null) {
			this.plotter.addData(SERIES,
								 endTime.getSimulatedTime(),
								 this.lastReading);
		}

		super.endSimulation(endTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new WiFiBandwidthSensorReport(this.getURI(), this.readings);
	}
}
// -----------------------------------------------------------------------------
