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
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>OutsideTemperature</code> implements an atomic HIOA model
 * that produces a model for outside temperature.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The outside temperature model combines a periodic variation of the
 * temperature over a day following a sine curve to which a gaussian noise
 * is added.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: name = {@code outsideTemperature}, type = {@code Double}</li>
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
 * <p>Created on : 2022-07-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelExportedVariable(name = "outsideTemperature", type = Double.class)
//-----------------------------------------------------------------------------
public class			OutsideTemperature
extends		AbstractPlottingHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** name of the series used by the plotter to gather wall temperature
	 *  data.																*/
	public static final String	SERIES_NAME = "temperature";

	/** name of the run parameter defining the mean temperature as the basis
	 *  of the deterministic sine variability over the day.					*/
	public static final String	MEAN_TEMP_PARAM_NAME = "meanTemp";
	/** name of the run parameter defining the temperature amplitude as the
	 *  basis of the deterministic sine variability over the day.			*/
	public static final String	TEMP_AMPLITUDE_PARAM_NAME = "tempAmplitude";
	/** name of the run parameter defining the standard deviation of a
	 *  stochastic gaussian noise with mean 0 for the temperature modifying
	 *  the temperature given by the sine deterministic variation model.	*/
	public static final String	TEMP_NOISE_SD_PARAM_NAME = "tempNoiseSD";
	/** name of the evaluation step size simulation run parameter.			*/
	public static final String	EVALUATION_STEP_PARAM_NAME = "stepSize";

	/** period of the sine modelling the temperature variation over a day.	*/
	protected static final double	PERIOD = 24.0;
	/** current time within the period.										*/
	protected double				cycleTime;

	/** mean temperature as the basis of the deterministic sine variability
	 *  over the day.													 	*/
	protected double				meanTemp;
	/** temperature amplitude as the basis of the deterministic sine
	 *  variability over the day.											*/
	protected double				tempAmplitude;
	/** standard deviation of a stochastic gaussian noise with mean 0 for
	 *  the temperature modifying the temperature given by the sine
	 *  deterministic variation model.										*/
	protected double				tempNoiseSD;

	/** Random number generator for the temperature noise.					*/
	protected final RandomDataGenerator	rg;

	/** temperature outside the house/building.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>			outsideTemperature =
													new Value<Double>(this);

	/** integration step size; set by a simulation run parameter.			*/
	protected double		step;
	/** integration step as a {@code Duration}; set from {@code step}.		*/
	protected Duration		stepDuration;

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
	public				OutsideTemperature(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Simulation protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.examples.thermostat.AbstractPlottingHIOA#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		this.meanTemp =
			(double) simParams.get(this.uri + ":" + MEAN_TEMP_PARAM_NAME);
		this.tempAmplitude =
			(double) simParams.get(this.uri + ":" + TEMP_AMPLITUDE_PARAM_NAME);
		this.tempNoiseSD =
			(double) simParams.get(this.uri + ":" + TEMP_NOISE_SD_PARAM_NAME);
		this.step =
			(double) simParams.get(this.uri + ":" + EVALUATION_STEP_PARAM_NAME);
		super.setSimulationRunParameters(simParams);
	}

	/**
	 * .
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code isDebugModeOn()}
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.mustPlot) {
			this.plotTitle = this.uri + " temperature";
			this.plotAbcissaLabel = "Time (hours)";
			this.plotOrdinateLabel = "temperature";
			this.series = new String[]{SERIES_NAME};
		}
		this.cycleTime = 0.0;
		this.getSimulationEngine().toggleDebugMode();
		this.stepDuration = new Duration(this.step, initialTime.getTimeUnit());
		super.initialiseState(initialTime);
		this.logMessage("simulation begins for " + this.uri + ".");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;

		if (!this.outsideTemperature.isInitialised()) {
			double s = Math.sin(1.5 * Math.PI);
			double initialTemp =
					this.meanTemp
						+ s*this.tempAmplitude
						+ this.rg.nextGaussian(0.0, this.tempNoiseSD);
			this.outsideTemperature.initialise(initialTemp);

			if (this.mustPlot) {
				Time t = this.outsideTemperature.getTime();
				this.plotter.addData(SERIES_NAME,
									 t.getSimulatedTime(),
									 this.outsideTemperature.evaluateAt(t));
			}
			StringBuffer sb = new StringBuffer("outside temperature = ");
			sb.append(this.outsideTemperature);
			this.logMessage(sb.toString());
			justInitialised++;
		}
		return new Pair<>(justInitialised, notInitialisedYet);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.stepDuration;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		// compute the current time in the cycle
		this.cycleTime += elapsedTime.getSimulatedDuration();
		if (this.cycleTime > PERIOD) {
			this.cycleTime -= PERIOD;
		}
		double s = Math.sin((1.5 + 2*(this.cycleTime/PERIOD)) * Math.PI);
		this.outsideTemperature.setNewValue(
				this.meanTemp + s*this.tempAmplitude
							  + this.rg.nextGaussian(0.0, this.tempNoiseSD),
				this.getCurrentStateTime());

		StringBuffer sb = new StringBuffer("outside temperature = ");
		sb.append(this.outsideTemperature);
		this.logMessage(sb.toString());

		if (this.mustPlot) {
			Time t = this.outsideTemperature.getTime();
			this.plotter.addData(SERIES_NAME,
								 t.getSimulatedTime(),
								 this.outsideTemperature.evaluateAt(t));
		}
	}
}
// -----------------------------------------------------------------------------
