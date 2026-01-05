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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.LinearInterpolatorGenerator;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PiecewiseInterExtrapolatedValueHistory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistoryFactoryI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>HIOA_Counter</code> implements a simple real time HIOA
 * atomic model that receives an external event {@code TicEvent} and counts
 * them in an exported variable {@code generated}.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The exported variable {@code generated} can use a value history. This is
 * controlled by the value of the static variable
 * {@code SimulationMain.WITH_VALUE_HISTORY}. If this variable is true, then a
 * value history is used with a linear inter/extrapolation scheme for the
 * evaluation of the variable {@code generated}, otherwise no value history is
 * used (and then the evaluation of the variable {@code generated} returns
 * simply the latest computed value).
 * </p>
 * 
 * <ul>
 * <li>Imported events: {@code TicEvent}</li>
 * <li>Exported events: none</li>
 * <li>Imported variables: none</li>
 * <li>Exported variables: name = {@code generated}, type = {@code Double}</li>
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
 * <p>Created on : 2020-11-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
//-----------------------------------------------------------------------------
@ModelExternalEvents(imported = {TicEvent.class})
@ModelExportedVariable(name = "generated", type = Double.class)
//-----------------------------------------------------------------------------
public class			HIOA_Generator
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** a convenient string to be used as URI prefix for model instances.	*/
	public static final String	URI_PREFIX =
									HIOA_Generator.class.getSimpleName();

	/** true when the model has received an external {@code TicEvent} but
	 *  did not yet executed an internal transition.					 	*/
	protected boolean			hasReceivedExternalEvent;

	/** the history window used for {@code generated} if necessary.				*/
	protected Duration			historyWindow =
										new Duration(2.0, TimeUnit.SECONDS);
	/** the exported variable computed by the model.						*/
	@ExportedVariable(type = Double.class)
	protected Value<Double>		generated;

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
	public				HIOA_Generator(
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
			this.generated = new Value<Double>(
					this,			// the model that holds this variable
					historyWindow,	// the time window
					// the history, with its associated piecewise linear
					// inter/extrapolation function generator
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
			this.generated = new Value<Double>(this);
		}

		// create and attach a standard logger
		this.getSimulationEngine().setLogger(new StandardLogger());
	}

	// -------------------------------------------------------------------------
	// Simulation methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.hasReceivedExternalEvent = false;
		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("simulation starts for " + this.uri + ".");
		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		// initialise the time of the first value for the variables
		super.initialiseVariables();
		// set the initial value of the variable and turn its status to
		// initialised
		this.generated.initialise(0.0);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// the model does not export events
		return null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		if (this.hasReceivedExternalEvent) {
			// when an external event is received, an immediate internal
			// transition is triggered
			return Duration.zero(this.getSimulatedTimeUnit());
		} else {
			// otherwise, no internal transition is planned
			return Duration.INFINITY;			
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		try {
			// to increment the value, access directly the old value
			// (rather than evaluating at the current time, which could
			// wrongly use an extrapolated value)
			this.generated.setNewValue(this.generated.getValue() + 1.0,
									   this.getCurrentStateTime());
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		this.hasReceivedExternalEvent = false;

		// tracing...
		StringBuffer sb = new StringBuffer("internal at ");
		sb.append(this.getCurrentStateTime());
		sb.append(" with generated = ");
		sb.append(this.generated.toString());
		this.logMessage(sb.toString());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		// get the vector of current external events
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		// when this method is called, there is at least one external event,
		// and for the panel model, there will be exactly one by
		// construction which is a consumption level request.
		assert	currentEvents != null && currentEvents.size() == 1;
		// The received event can only be a ConsumptionLevel, as this is the
		// only imported event by this model.
		assert	currentEvents.get(0) instanceof TicEvent;

		// toggling hasReceivedExternalEvent triggers an immediate internal
		// transition
		this.hasReceivedExternalEvent = true;

		// tracing...
		StringBuffer sb = new StringBuffer("external ");
		sb.append(currentEvents.get(0));
		sb.append(" at ");
		sb.append(this.getCurrentStateTime());
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
