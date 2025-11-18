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
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractWall</code> implements common data and
 * behaviours for wall atomic HIOA models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This class builds over the plotting behaviour of {@code AbstractPlottingHIOA}.
 * It introduces two model variables: the wall temperature and the wall
 * insulation transfer constant that is used in the differential equation of
 * the heater atomic HIOA model. It also defines the evaluation step for these
 * variables.
 * </p>
 * <p>
 * The insulation transfer constant, as its name indicates, does not change over
 * time (at least at the timespan considered in this example), so it may appear
 * a bit curious to define it as a model exported variable. But, this choice
 * facilitates the parameterisation of its value and then its sharing with the
 * heater model that needs it to compute the current room temperature.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: name =
 *   {@code wallTemperature}, type = {@code Double};
 *   name = {@code wallInsulationConstant}, type = {@code Double}</li>
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
 * <p>Created on : 2022-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelExportedVariable(name = "wallTemperature", type = Double.class)
@ModelExportedVariable(name = "wallInsulationConstant", type = Double.class)
//-----------------------------------------------------------------------------
public abstract class	AbstractWall
extends		AbstractPlottingHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** name of the insulation transfer constant simulation run parameter.	*/
	public static final String	INSULATION_TRANSFER_CONSTANT_PARAM_NAME =
														"insulationConstant";
	/** name of the integration step size simulation run parameter.			*/
	public static final String	INTEGRATION_STEP_PARAM_NAME = "stepSize";
	/** name of the series used by the plotter to gather wall temperature
	 *  data.																*/
	public static final String	SERIES_NAME = "temperature";

	/** the temperature imposed by the wall on the surrounded room.			*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>	wallTemperature = new Value<Double>(this);
	/** heath transfer constant of the wall.								*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>	wallInsulationConstant = new Value<Double>(this);

	/** insulation constant of the wall as provided by a run parameter.		*/
	protected double		insulationConstant;
	/** evaluation step size; set by a simulation run parameter.			*/
	protected double		step;
	/** evaluation step as a {@code Duration}; set from {@code step}.		*/
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
	public				AbstractWall(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
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
		String insulationName =
			ModelI.createRunParameterName(
								this.uri,
								INSULATION_TRANSFER_CONSTANT_PARAM_NAME);
		String stepName =
			ModelI.createRunParameterName(
								this.uri,
								INTEGRATION_STEP_PARAM_NAME);

		if (simParams == null) {
			throw new MissingRunParameterException("no run parameters!");
		} else {
			if (!simParams.containsKey(insulationName)) {
				throw new MissingRunParameterException(insulationName);
			}
			if (!simParams.containsKey(stepName)) {
				throw new MissingRunParameterException(stepName);
			}
		}
		this.insulationConstant = (double) simParams.get(insulationName);
		this.step = (double) simParams.get(stepName);
		super.setSimulationRunParameters(simParams);
	}

	/**
	 * initialise the state of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * post	{@code isDebugModeOn()}
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.examples.thermostat.AbstractPlottingHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.mustPlot) {
			this.plotAbcissaLabel = "Time (hours)";
			this.plotOrdinateLabel = "temperature";
			this.series = new String[]{SERIES_NAME};
		}
		this.getSimulationEngine().toggleDebugMode();
		this.stepDuration = new Duration(this.step, initialTime.getTimeUnit());
		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();
		this.wallInsulationConstant.initialise(this.insulationConstant);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.stepDuration;
	}
}
// -----------------------------------------------------------------------------
