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
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;

import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>InsideWall</code> implements an atomic HIOA model of an
 * inside wall <i>i.e.</i>, a wall between two rooms in the same house/building.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Currently, the model is oversimplified as it assumes that the wall
 * temperature is set at initialisation time and does not change over the
 * simulation. A more accurate model would consider exchanges of heath
 * between the two adjacent rooms.
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
public class			InsideWall
extends		AbstractWall
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** name of the initial temperature simulation run parameter.			*/
	public static final String	INITIAL_TEMP_PARAM_NAME = "initialTemp";

	/** initial value of the wall temperature; set by a simulation run
	 *  parameter.															*/
	protected double	initialTemp;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTimeUnit.equals(TimeUnit.HOURS)}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do</i>.
	 */
	public				InsideWall(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		assert	simulatedTimeUnit.equals(TimeUnit.HOURS);
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
			ModelI.createRunParameterName(this.uri, INITIAL_TEMP_PARAM_NAME);

		if (simParams == null ) {
			throw new MissingRunParameterException("no run parameters!");
		} else {
			if (!simParams.containsKey(name)) {
				throw new MissingRunParameterException(name);
			}
		}

		this.initialTemp = (double) simParams.get(name);
		super.setSimulationRunParameters(simParams);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.examples.thermostat.AbstractPlottingHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		if (this.mustPlot) {
			this.plotTitle = this.uri + " temperature";
		}
		super.initialiseState(initialTime);
		this.logMessage("simulation begins for " + this.uri + ".");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();
		this.wallTemperature.initialise(this.initialTemp);

		if (this.mustPlot) {
			Time t = this.wallTemperature.getTime();
			this.plotter.addData(SERIES_NAME,
								 t.getSimulatedTime(),
								 this.wallTemperature.evaluateAt(t));
		}
		this.logMessage("wall temperature = " + this.wallTemperature);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		// for the time being, this will generate a constant wall temperature
		this.wallTemperature.setNewValue(this.wallTemperature.evaluateAt(t), t);

		this.logMessage("wall temperature = " + this.wallTemperature);

		if (this.mustPlot) {
			this.plotter.addData(SERIES_NAME, t.getSimulatedTime(),
								 wallTemperature.evaluateAt(t));
		}
	}
}
// -----------------------------------------------------------------------------
