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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.LinearInterpolatorGenerator;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PiecewiseInterExtrapolatedValueHistory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistoryFactoryI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Multiplier</code> implements a simple HIOA model which
 * imports a double type model variable from which it computes an exported model
 * variable {@code mine} by applying some multiplication factor.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The multiplication factor must be set by a run parameter named
 * {@code this.getURI() + ":" + HIOA_Multiplier.FACTOR_NAME}.
 * </p>
 * <p>
 * The model is triggered at a constant rate that must be set by a run
 * parameter named {@code this.getURI() + ":" + HIOA_Multiplier.STEP_NAME}. The
 * value of this run parameter is interpreted in the time unit of the model.
 * </p>
 * <p>
 * The exported variable {@code mine} can use a value history. This is
 * controlled by the value of the static variable
 * {@code SimulationMain.WITH_VALUE_HISTORY}. If this variable is true, then a
 * value history is used with a linear inter/extrapolation scheme for the
 * evaluation of the variable {@code mine}, otherwise no value history is used
 * (and then the evaluation of the variable {@code mine} returns simply the
 * latest computed value).
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: name = {@code other}, type = {@code Double}</li>
 * <li>Exported variables: name = {@code mine}, type = {@code Double}</li>
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
 * <p>Created on : 2022-06-28</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelImportedVariable(name = "other", type = Double.class)
@ModelExportedVariable(name = "mine", type = Double.class)
//-----------------------------------------------------------------------------
public class			HIOA_Multiplier
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	public static enum T	{A, B;}

	private static final long	serialVersionUID = 1L;
	/** a convenient string to be used as URI prefix for model instances.	*/
	public static final String	URI_PREFIX =
										HIOA_Multiplier.class.getSimpleName();

	/** the local name of the run parameter to be used to set the computation
	 *  step duration.														*/
	public static final String	STEP_NAME = "step";
	/** the raw (double) value of the triggering step of the model.		 	*/
	protected double			step;
	/** the delay between two successive internal events as a
	 *  {@code Duration}.													*/
	protected Duration			delay;

	/** the local name of the run parameter to be used to set the
	 *  multiplication factor used in the computation of {@code mine}.		*/
	public static final String	FACTOR_NAME = "factor";
	/** the multiplication factor used in the computation of {@code mine}.	*/
	protected double			factor;

	/** the variable imported in the computational flow of the example.		*/
	@ImportedVariable(type = Double.class)
	protected Value<Double>		other;

	/** the history window used for {@code mine} if necessary.				*/
	protected Duration			historyWindow =
									new Duration(2.0, TimeUnit.SECONDS);
	/** the exported variable computed by the model.						*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>		mine;

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
	public				HIOA_Multiplier(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		assert	this.historyWindow.getTimeUnit().equals(simulatedTimeUnit);

		if (SimulationMain.WITH_VALUE_HISTORY) {
			// the variable has an history associated to keep track of older
			// values which are used to compute a linear function providing
			// values for intermediate times (interpolation) and future times
			// (compared to the current time of this model, hence extrapolation) 
			this.mine = new Value<Double>(
								this,
								historyWindow,
								new ValueHistoryFactoryI<Double>() {
									@Override
									public ValueHistory<Double> createHistory(
										Duration historyWindow
										)
									{
										return new PiecewiseInterExtrapolatedValueHistory(
													historyWindow,
													new LinearInterpolatorGenerator());
									}
								});
		} else {
			// to be able to compare simulations with and without value history
			this.mine = new Value<Double>(this);
		}

		// create and attach a standard logger
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
		String stepName = ModelI.createRunParameterName(this.uri, STEP_NAME);
		String factorName = ModelI.createRunParameterName(this.uri, FACTOR_NAME);
		if (simParams == null) {
			throw new MissingRunParameterException("no run parameters");
		} else {
			if (!simParams.containsKey(stepName)) {
				throw new MissingRunParameterException(stepName);
			} else if (!simParams.containsKey(factorName)) {
				throw new MissingRunParameterException(factorName);
			}
		}

		this.step = (double) simParams.get(stepName);
		this.factor = (double) simParams.get(factorName);
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

		if (!this.mine.isInitialised()) {
			// the variable mine is the one that this model holds and that
			// must be initialised, but it depends upon the imported variable
			// other
			if (this.other.isInitialised()) {
				// if other has been initialised, then mine can be initialised
				this.mine.initialise(this.other.getValue() * this.factor);
				// and then the number of newly initialised variable is 1
				numberOfNewlyInitialisedVariables++;
			} else {
				// otherwise, mine cannot be initialised and the number of
				// variable not initialised by this execution is one
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

		// the actual transition of this model, computing the value of mine
		// from the imported variable other
		try {
			this.mine.setNewValue(
				this.other.evaluateAt(this.getCurrentStateTime()) * this.factor,
				this.getCurrentStateTime());
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		// tracing...
		StringBuffer sb = new StringBuffer("internal at ");
		sb.append(this.getCurrentStateTime());
		sb.append(" mine = ");
		sb.append(this.mine.toString());
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
