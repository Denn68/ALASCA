package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.es.events.ES_EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanSimpleUserModel</code> defines a simple user model for
 * the fan simulator.
 *
 * <p><strong>Description</strong></p>
 * * <p>
 * It schedules events {@code SwitchOnFan}, {@code SetHighSpeedFan},
 * {@code SetLowSpeedFan} and {@code SwitchOffFan} at random intervals.
 * </p>
 * * @author	Team DeMoh
 */
@ModelExternalEvents(exported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowSpeedFan.class,
								 SetHighSpeedFan.class})
// -----------------------------------------------------------------------------
public class			FanSimpleUserModel
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	public static final String	URI = FanSimpleUserModel.class.getSimpleName();
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;

	/** time interval between event outputs in hours.						*/
	protected static double		STEP_MEAN_DURATION = 10.0/60.0; // 10 minutes
	/** time interval between fan usages in hours.					*/
	protected static double		DELAY_MEAN_DURATION = 2.0;

	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg ;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------
	
	/**
	 * return true if the static implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return			true if the static implementation invariants are observed, false otherwise.
	 */
	protected static boolean	staticImplementationInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				STEP_MEAN_DURATION > 0.0,
				FanSimpleUserModel.class,
				"STEP_MEAN_DURATION > 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				DELAY_MEAN_DURATION > 0.0,
				FanSimpleUserModel.class,
				"DELAY_MEAN_DURATION > 0.0");
		return ret;
	}

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(FanSimpleUserModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.rg != null,
				FanSimpleUserModel.class, instance, "rg != null");
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanSimpleUserModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty(),
				FanSimpleUserModel.class,
				"MEAN_STEP_RPNAME != null && !MEAN_STEP_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty(),
				FanSimpleUserModel.class,
				"MEAN_DELAY_RPNAME != null && !MEAN_DELAY_RPNAME.isEmpty()");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(FanSimpleUserModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");
		
		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan tester MIL model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine != null && !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit used for the simulation time.
	 * @param simulationEngine	simulation engine to which the model is attached.
	 * @throws Exception		<i>to do.</i>
	 */
	public				FanSimpleUserModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		
		this.rg = new RandomDataGenerator();
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanSimpleUserModel.implementationInvariants(this) :
			new NeoSim4JavaException(
					"FanTesterModel.implementationInvariants(this)");
		assert	FanSimpleUserModel.invariants(this) :
			new NeoSim4JavaException(
					"FanTesterModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	protected void		generateNextEvent()
	{
		EventI current = this.eventList.peek();
		ES_EventI nextEvent = null;
		
		if (current instanceof SwitchOffFan) {
			Time t2 = this.computeTimeOfNextUsage(current.getTimeOfOccurrence());
			nextEvent = new SwitchOnFan(t2);
		} else {
			Time t = this.computeTimeOfNextEvent(current.getTimeOfOccurrence());
			if (current instanceof SwitchOnFan) {
				nextEvent = new SetHighSpeedFan(t);
			} else if (current instanceof SetHighSpeedFan) {
				nextEvent = new SetLowSpeedFan(t);
			} else if (current instanceof SetLowSpeedFan) {
				nextEvent = new SwitchOffFan(t);
			}
		}
		this.scheduleEvent(nextEvent);
	}

	/**
	 * compute the time of the next event, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time		computeTimeOfNextEvent(Time from)
	{
		assert	from != null;
		double delay = Math.max(this.rg.nextGaussian(STEP_MEAN_DURATION,
													 STEP_MEAN_DURATION/2.0),
								0.1);
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	/**
	 * compute the time of the next fan usage, adding a random delay to
	 * {@code from}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code from != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param from	time from which a delay will be added to get the time of the next event.
	 * @return		the time of the next event, adding a random delay to {@code from}.
	 */
	protected Time		computeTimeOfNextUsage(Time from)
	{
		assert	from != null;
		double delay = Math.max(this.rg.nextGaussian(DELAY_MEAN_DURATION,
													 DELAY_MEAN_DURATION/10.0),
								0.1);
		Time t = from.add(new Duration(delay, this.getSimulatedTimeUnit()));
		return t;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.rg.reSeedSecure();

		Time t = this.computeTimeOfNextEvent(this.getCurrentStateTime());
		this.scheduleEvent(new SwitchOnFan(t));
		
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.getCurrentStateTime().add(this.getNextTimeAdvance());

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		if (this.eventList.peek() != null) {
			this.generateNextEvent();
		}
		return super.output();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_STEP_RPNAME = "STEP_MEAN_DURATION";
	/** run parameter name for {@code STEP_MEAN_DURATION}.					*/
	public static final String		MEAN_DELAY_RPNAME = "STEP_MEAN_DURATION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String stepName = ModelI.createRunParameterName(getURI(), MEAN_STEP_RPNAME);
		if (simParams.containsKey(stepName)) {
			STEP_MEAN_DURATION = (double) simParams.get(stepName);
		}
		String delayName = ModelI.createRunParameterName(getURI(), MEAN_DELAY_RPNAME);
		if (simParams.containsKey(delayName)) {
			DELAY_MEAN_DURATION = (double) simParams.get(delayName);
		}
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return null;
	}
}
// -----------------------------------------------------------------------------