package fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.KettleEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StartKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StopKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events.SIL_SetPowerKettle;
import java.util.ArrayList;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleStateSILModel</code> is a simple model that tracks the
 * current state of the kettle as it receives events triggering state
 * changes in the kettle and reemits them towards the other kettle models.
 *
 * <p><strong>Description</strong></p>
 *
 * <p><strong>Implementation Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code currentState != null}
 * </pre>
 *
 * <p><strong>Invariants</strong></p>
 *
 * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 *
 * @author	Team DeMoh
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOnKettle.class,
								 SwitchOffKettle.class,
								 SIL_SetPowerKettle.class,
								 HeatKettle.class,
								 DoNotHeatKettle.class,
								 StartKeepingWarmKettle.class,
								 StopKeepingWarmKettle.class},
					 exported = {SwitchOnKettle.class,
							 	 SwitchOffKettle.class,
							 	 SIL_SetPowerKettle.class,
							 	 HeatKettle.class,
							 	 DoNotHeatKettle.class,
							 	 StartKeepingWarmKettle.class,
							 	 StopKeepingWarmKettle.class})
// -----------------------------------------------------------------------------
public class			KettleStateSILModel
extends		AtomicModel
implements	SIL_KettleOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = KettleStateSILModel.class.getSimpleName();
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;

	/** current state of the kettle.										*/
	protected KettleState	currentState = KettleState.OFF;
	/** external event that has been received and that must be reemitted.	*/
	protected EventI		toBeReemitted;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(
		KettleStateSILModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.currentState != null,
					KettleStateSILModel.class,
					instance,
					"currentState != null");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= KettleSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				KettleStateSILModel.class,
				"URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(
		KettleStateSILModel instance
		)
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

	public				KettleStateSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	KettleStateSILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"KettleStateSILModel.implementationInvariants(this)");
		assert	KettleStateSILModel.invariants(this) :
				new NeoSim4JavaException("KettleStateSILModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public void			setState(KettleState s)
	{
		this.currentState = s;
	}

	@Override
	public KettleState	getState()
	{
		return this.currentState;
	}

	@Override
	public void			setCurrentHeatingPower(double newPower, Time t)
	{
		// Nothing to be done here - power is tracked elsewhere
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}
	}

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentState = KettleState.OFF;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	KettleStateSILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"KettleStateSILModel.implementationInvariants(this)");
		assert	KettleStateSILModel.invariants(this) :
				new NeoSim4JavaException("KettleStateSILModel.invariants(this)");
	}

	@Override
	public Duration		timeAdvance()
	{
		if (this.toBeReemitted == null) {
			return Duration.INFINITY;
		} else {
			return Duration.zero(getSimulatedTimeUnit());
		}
	}

	@Override
	public ArrayList<EventI>	output()
	{
		if (this.toBeReemitted != null) {
			ArrayList<EventI> ret = new ArrayList<EventI>();
			ret.add(this.toBeReemitted);
			this.toBeReemitted = null;

			if (VERBOSE) {
				this.logMessage("output sends " + ret);
			}

			return ret;
		} else {
			return null;
		}
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof KettleEventI;

		ce.executeOn(this);
		this.toBeReemitted = ce;

		if (VERBOSE) {
			this.logMessage("performing an external transition on " + ce);
		}

		assert	implementationInvariants(this) :
				new NeoSim4JavaException(
						"KettleStateSILModel.implementationInvariants(this)");
		assert	invariants(this) :
				new NeoSim4JavaException(
						"KettleStateSILModel.invariants(this)");
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
	}
}
// -----------------------------------------------------------------------------
