package fr.sorbonne_u.devs_simulation.examples.hioa_with_vh;

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

import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.Map;
import java.util.concurrent.TimeUnit;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Consumer</code> defines an HIOA model that imports
 * a variable {@code otherCurrent} and evaluates it each time it is triggered
 * and sets its own internal variable {@code myCurrent} to the obtained new
 * value.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The model is triggered at a constant rate that must be set by a run
 * parameter named {@code this.getURI() + ":" + HIOA_Consumer.STEP_NAME}. The
 * value of this run parameter is interpreted in the time unit of the model.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: name = {@code otherCurrent}, type = {@code Double}</li>
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
 * <p>Created on : 2020-11-30</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelImportedVariable(name = "otherCurrent", type = Double.class)
//-----------------------------------------------------------------------------
public class			HIOA_Consumer
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** a convenient string to be used as URI prefix for model instances.	*/
	public static final String		URI_PREFIX =
										HIOA_Consumer.class.getSimpleName();

	/** the local name of the run parameter to be used to set the computation
	 *  step duration.														*/
	public static final String		STEP_NAME = "step";
	/** the raw (double) value of the triggering step of the model.		 	*/
	protected double				step;
	/** the delay between two successive internal events as a
	 *  {@code Duration}.													*/
	protected Duration				delay;

	/** the variable imported in the computational flow of the example.		*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>			otherCurrent;
	/** the internal variable computed by the model.						*/
	@InternalVariable(type = Double.class)
	protected Value<Double>			myCurrent = new Value<Double>(this);

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
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>to do</i>.
	 */
	public				HIOA_Consumer(
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
		String pname = ModelI.createRunParameterName(this.uri, STEP_NAME);
		if (simParams == null) {
			throw new MissingRunParameterException("no run parameters!");
		} else {
			if (!simParams.containsKey(pname)) {
				throw new MissingRunParameterException(pname);
			}
		}
		this.step = (double) simParams.get(pname);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.delay = new Duration(this.step, simulatedTimeUnit);
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation begins for " + this.uri + ".");
		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		// when several models have dependencies among their variables that
		// forces an order in their initialisation, the fix point algorithm must
		// be used
		return true;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		// at each call, the method tries to initialise the variables held by
		// the model and return the number of variables that were initialised
		// and the number that could not be initialised (because variables
		// imported from other models they depend upon are not yet initialised)
		int numberOfNewlyInitialisedVariables = 0;
		int numberOfStillNotInitialisedVariables = 0;

		if (!this.myCurrent.isInitialised()) {
			// the variable myCurrent is the one that this model holds and that
			// must be initialised, but it depends upon the imported variable
			// otherCurrent
			if (this.otherCurrent.isInitialised()) {
				// if otherCurrent has been initialised, then myCurrent can be
				// initialised
				this.myCurrent.initialise(this.otherCurrent.getValue());
				// and then the number of initialised variable is 1
				numberOfNewlyInitialisedVariables++;
			} else {
				// otherwise, myCurrent cannot be initialised and the number of
				// variable initialised by this execution is one
				numberOfStillNotInitialisedVariables++;
			}
		}

		// the two counters are returned and aggregated among the different
		// execution of fixpointInitialiseVariables in the different models
		// if the total numbers gives 0 still not initialised variable, then
		// the fix point has been reached, but if there are still variables not
		// initialised but some have been initialised during the run (i.e.,
		// numberOfNewlyInitialisedVariables > 0) then the method
		// fixpointInitialiseVariables must be rerun on all models until all
		// variables have been initialised
		return new Pair<Integer, Integer>(numberOfNewlyInitialisedVariables,
										  numberOfStillNotInitialisedVariables);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		// this model performs internal transitions at a fixed rate, hence
		// returns always the same delay to the next internal transition
		return this.delay;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// this call performs some verifications and some internal updates
		super.userDefinedInternalTransition(elapsedTime);

		// the actual transition of this model, merely copying the value of the
		// imported variable otherCurrent to the variable myCurrent
		try {
			this.myCurrent.setNewValue(
					this.otherCurrent.evaluateAt(this.getCurrentStateTime()),
					this.getCurrentStateTime());
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		// tracing...
		StringBuffer sb = new StringBuffer("internal at ");
		sb.append(this.getCurrentStateTime());
		sb.append(" myCurrent = ");
		sb.append(this.myCurrent.toString());
		this.logMessage(sb.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		this.logMessage("simulation ends for " + this.uri);
		super.endSimulation(endTime);
	}
}
// -----------------------------------------------------------------------------
