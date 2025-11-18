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
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;

// -----------------------------------------------------------------------------
/**
 * The class <code>OutsideWall</code> implements an atomic HIOA model of an
 * outside wall <i>i.e.</i>, a wall between a room in the outside.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An outside wall gets the outside temperature from another HIOA model
 * computing it (so it can be shared among several outside walls of the
 * same house/building that, indeed, are in the same external conditions.
 * </p>
 * <p>
 * As this model depends upon the outside temperature model to set its own
 * model variables before the ones of this model can be set in turn, it uses
 * the fix point protocol to initialise its variables.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: name = {@code outsideTemperature}, type = {@code Double}</li>
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
 * <p>Created on : 2022-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelImportedVariable(name = "outsideTemperature", type = Double.class)
//-----------------------------------------------------------------------------
public class			OutsideWall
extends		AbstractWall
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/** current outside temperature imported from another model.			*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>		outsideTemperature;

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
	public				OutsideWall(
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
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
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

		// the superclass does not use the fix point protocol, so its variable
		// must be initialised properly here, but only once.
		if (!this.wallInsulationConstant.isInitialised()) {
			super.initialiseVariables();
		}

		if (this.outsideTemperature.isInitialised() &&
									!this.wallTemperature.isInitialised()) {
			// the wall temperature depends upon the outside temperature to
			// be initialised
			this.wallTemperature.initialise(
					this.outsideTemperature.evaluateAt(this.getCurrentStateTime()));
			justInitialised++;
		} else if (!this.wallTemperature.isInitialised()) {
			// when the initialisation was not possible at the current call
			notInitialisedYet++;
		}
		return new Pair<>(justInitialised, notInitialisedYet);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		this.wallTemperature.
					setNewValue(this.outsideTemperature.evaluateAt(t), t);

		this.logMessage("wall temperature = " + this.wallTemperature);
		if (this.mustPlot) {
			Time t1 = this.wallTemperature.getTime();
			this.plotter.addData(SERIES_NAME,
								 t1.getSimulatedTime(),
								 this.wallTemperature.evaluateAt(t1));
		}
	}
}
// -----------------------------------------------------------------------------
