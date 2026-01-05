package fr.sorbonne_u.devs_simulation.examples.thermostat;

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
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.plotters.PlotterDescription;
import fr.sorbonne_u.plotters.XYPlotter;

import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractExampleHIOA</code> implements the plotting
 * behaviour for an atomic HIOA model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class uses tools developed over the JFreeChart library.
 * </p>
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
 * <p>Created on : 2022-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractPlottingHIOA
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** name of the plotting flag as simulation run parameter.				*/
	public static final String	PLOTTING_PARAM_NAME = "mustPlot";

	/** true if the model must plot some variables, false otherwise.		*/
	protected boolean				mustPlot;
	/** title to be printed on the plot.									*/
	protected String				plotTitle;
	/** abcissa label.														*/
	protected String				plotAbcissaLabel;
	/** ordinate label.														*/
	protected String				plotOrdinateLabel;
	/** names of the series of data used to construct the plot, one series
	 *  per variable to be plotted the used in calls to {@code addData}.	*/
	protected String[]				series;
	/** the plotter.														*/
	protected XYPlotter				plotter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				AbstractPlottingHIOA(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
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
		String name =
				ModelI.createRunParameterName(this.uri, PLOTTING_PARAM_NAME);
		if (simParams != null) {
			if (simParams.containsKey(name)) {
				this.mustPlot = (boolean) simParams.get(name);
			} else {
				this.mustPlot = false;
			}
		} else {
			throw new MissingRunParameterException("no run parameters!");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.mustPlot) {
			assert	this.plotTitle != null;
			assert	this.plotAbcissaLabel != null;
			assert	this.plotOrdinateLabel != null;
			assert	this.series != null && this.series.length > 0;

			PlotterDescription pd =
					new PlotterDescription(
							this.plotTitle,
							this.plotAbcissaLabel,
							this.plotOrdinateLabel,
							0,
							0,
							500,
							500);
			this.plotter = new XYPlotter(pd);
			for (int i = 0 ; i < this.series.length ; i++) {
				this.plotter.createSeries(this.series[i]);
			}
			this.plotter.initialise();
			this.plotter.showPlotter();
		}
		super.initialiseState(initialTime);
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
}
// -----------------------------------------------------------------------------
