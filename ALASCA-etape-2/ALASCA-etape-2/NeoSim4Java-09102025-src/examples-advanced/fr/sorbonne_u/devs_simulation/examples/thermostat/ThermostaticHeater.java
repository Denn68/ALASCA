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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Thermostat</code> implements an atomic HIOA model
 * simulating a thermostated heater in a room with 4 walls contributing to
 * the room temperature.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Uses the classic differential equation to compute the current room
 * temperature from the contributions of the heating, when it is on, and from
 * the walls (usually cooling the room).
 * </p>
 * <p>
 * The model imports the wall temperatures and insulation constants from the
 * wall models.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables:
 *   name = {@code wall1Temp}, type = {@code Double};
 *   name = {@code wall1InsulationConstant}, type = {@code Double};
 *   name = {@code wall2Temp}, type = {@code Double};
 *   name = {@code wall2InsulationConstant}, type = {@code Double};
 *   name = {@code wall3Temp}, type = {@code Double};
 *   name = {@code wall3InsulationConstant}, type = {@code Double};
 *   name = {@code wall4Temp}, type = {@code Double};
 *   name = {@code wall4InsulationConstant}, type = {@code Double}</li>
 * <li>Exported variables: name = {@code currentTemp}, type = {@code Double}</li>
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
 * <p>Created on : 2020-11-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelExportedVariable(name = "currentTemp", type = Double.class)
@ModelImportedVariable(name = "wall1Temp", type = Double.class)
@ModelImportedVariable(name = "wall1InsulationConstant", type = Double.class)
@ModelImportedVariable(name = "wall2Temp", type = Double.class)
@ModelImportedVariable(name = "wall2InsulationConstant", type = Double.class)
@ModelImportedVariable(name = "wall3Temp", type = Double.class)
@ModelImportedVariable(name = "wall3InsulationConstant", type = Double.class)
@ModelImportedVariable(name = "wall4Temp", type = Double.class)
@ModelImportedVariable(name = "wall4InsulationConstant", type = Double.class)
//-----------------------------------------------------------------------------
public class			ThermostaticHeater
extends		AbstractPlottingHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** an URI for an instance model, if a single instance is created.		*/
	public static final String	URI = ThermostaticHeater.class.getSimpleName();
	/** name of the initial temperature simulation run parameter.			*/
	public static final String	INITIAL_TEMP_PARAM_NAME = "initialTemp";
	/** name of the integration step size simulation run parameter.			*/
	public static final String	INTEGRATION_STEP_PARAM_NAME = "stepSize";
	/** name of the series used by the plotter to gather wall temperature
	 *  data.																*/
	public static final String	SERIES_NAME = "temperature";

	/** target temperature of the thermostated heater.						*/
	protected static final double		TARGET = 20.0;
	/** allowed hysteresis from the target temperature.						*/
	protected static final double		HYSTERESIS = 1.0;
	/** t'mperature of the heater plate.									*/
	protected static final double		HEATER_TEMPERATURE = 50.0;
	/** heating transfer constant in the differential equation.				*/
	protected final double				HEATING_TRANSFER_CONSTANT = 2.5;

	/** initial temperature as provided by a run parameter.					*/
	protected double					initialTemperature;
	/** integration step size; set by a simulation run parameter.			*/
	protected double					integrationStep;
	/** integration step as a {@code Duration}; set from {@code step}.		*/
	protected Duration					integrationStepDuration;

	/** true if the heater is currently heating, false otherwise.			*/
	protected boolean					heating = false;
	/** current room temperature and its first derivative.					*/
	@ExportedVariable(type = Double.class)
	protected DerivableValue<Double>	currentTemp =
											new DerivableValue<Double>(this);

	/** temperature from the outside of the first wall.						*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall1Temp;
	/** heath transfer constant of the first wall.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall1InsulationConstant;

	/** temperature from the outside of the second wall.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall2Temp;
	/** heath transfer constant of the second wall.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall2InsulationConstant;

	/** temperature from the outside of the third wall.						*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall3Temp;
	/** heath transfer constant of the third wall.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall3InsulationConstant;

	/** temperature from the outside of the fourth wall.					*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall4Temp;
	/** heath transfer constant of the fourth wall.							*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>				wall4InsulationConstant;

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
	public				ThermostaticHeater(
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
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		this.initialTemperature =
			(double) simParams.get(this.uri + ":" + INITIAL_TEMP_PARAM_NAME);
		this.integrationStep =
			(double)simParams.get(this.uri + ":" + INTEGRATION_STEP_PARAM_NAME);
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
			this.plotTitle = this.uri + " room temperature";
			this.plotAbcissaLabel = "Time (hours)";
			this.plotOrdinateLabel = "temperature";
			this.series = new String[]{SERIES_NAME};
		}
		this.integrationStepDuration =
				new Duration(this.integrationStep, this.getSimulatedTimeUnit());
		this.heating = false;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins for " + this.uri + ".");
		super.initialiseState(initialTime);
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

		if (!this.currentTemp.isInitialised()
							&& this.wall1Temp.isInitialised()
							&& this.wall1InsulationConstant.isInitialised()
							&& this.wall2Temp.isInitialised()
							&& this.wall2InsulationConstant.isInitialised()
							&& this.wall3Temp.isInitialised()
							&& this.wall3InsulationConstant.isInitialised()
							&& this.wall4Temp.isInitialised()
							&& this.wall4InsulationConstant.isInitialised()) {
			double derivative = this.computeDerivative(this.initialTemperature);
			this.currentTemp.initialise(this.initialTemperature, derivative);
			justInitialised++;

			if (this.mustPlot) {
				Time t = this.currentTemp.getTime();
				this.plotter.addData(SERIES_NAME,
									 t.getSimulatedTime(),
									 this.initialTemperature);
			}
			this.logMessage("room temperature " + this.currentTemp);
		} else if (!this.currentTemp.isInitialised()) {
			notInitialisedYet++;
		}
		return new Pair<>(justInitialised, notInitialisedYet);
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
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.integrationStepDuration;
	}

	protected double	computeDerivative(double temperature)
	{
		double derivative = 0.0;
		if (this.heating) {
			derivative =
				(HEATER_TEMPERATURE - temperature)/HEATING_TRANSFER_CONSTANT;
		}
		Time t = this.getCurrentStateTime();
		derivative += (this.wall1Temp.evaluateAt(t) - temperature)/
									this.wall1InsulationConstant.evaluateAt(t);
		derivative += (this.wall2Temp.evaluateAt(t) - temperature)/
									this.wall2InsulationConstant.evaluateAt(t);
		derivative += (this.wall3Temp.evaluateAt(t) - temperature)/
									this.wall3InsulationConstant.evaluateAt(t);
		derivative += (this.wall4Temp.evaluateAt(t) - temperature)/
									this.wall4InsulationConstant.evaluateAt(t);
		return derivative;
	}

	protected void		computeNewValue() throws Exception
	{
		double delta_t =
			this.getCurrentStateTime().subtract(this.currentTemp.getTime()).
														getSimulatedDuration();
		double derivative = this.currentTemp.getFirstDerivative();
		double currentValue = this.currentTemp.getValue();
		double newValue = currentValue + derivative*delta_t;
		double newDerivative = this.computeDerivative(newValue);
		this.currentTemp.setNewValue(newValue,
									 newDerivative,
									 this.getCurrentStateTime());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		try {
			this.computeNewValue();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		if (this.currentTemp.getValue() > TARGET + HYSTERESIS) {
			this.heating = false;
		} else if (this.currentTemp.getValue() < TARGET - HYSTERESIS) {
			this.heating = true;
		}

		if (this.mustPlot) {
			Time t = this.currentTemp.getTime();
			this.plotter.addData(SERIES_NAME,
								 t.getSimulatedTime(),
								 this.currentTemp.evaluateAt(t));
		}

		StringBuffer sb = new StringBuffer("heating = ");
		sb.append(this.heating);
		sb.append(" room temperature ");
		sb.append(this.currentTemp);
		this.logMessage(sb.toString());
	}
}
// -----------------------------------------------------------------------------
