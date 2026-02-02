package fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI.VariableValue;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle.KettleState;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.KettleEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StartKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StopKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events.SIL_SetPowerKettle;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>KettleTemperatureSILModel</code> defines a simulation model
 * for the temperature of the water inside the kettle.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * The kettle water temperature model simulates:
 * - Water heating when HEATING state (up to MAX_TEMPERATURE = 100°C)
 * - Water cooling when not heating (towards EXTERNAL_TEMPERATURE = 20°C)
 * - Keep warm mode maintains temperature at a level lower than boiling
 * </p>
 *
 * @author	Team DeMoh
 */
@ModelExternalEvents(imported = {SwitchOffKettle.class,
								 SIL_SetPowerKettle.class,
								 HeatKettle.class,
								 DoNotHeatKettle.class,
								 StartKeepingWarmKettle.class,
								 StopKeepingWarmKettle.class})
// -----------------------------------------------------------------------------
public class			KettleTemperatureSILModel
extends		AtomicHIOA
implements	SIL_KettleOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	public static String			URI = KettleTemperatureSILModel.class.getSimpleName();
	public static boolean			VERBOSE = true;
	public static boolean			DEBUG = false;

	// Kettle-specific temperature constants
	public static double			INITIAL_TEMPERATURE = 20.0;
	public static double			EXTERNAL_TEMPERATURE = 20.0;
	public static double			MAX_TEMPERATURE = 100.0;
	public static double			KEEP_WARM_TEMPERATURE = 80.0;

	// Heating/cooling constants for kettle
	protected static double			HEATING_CAPACITY = 0.86;  // degrees per watt per hour
	protected static double			COOLING_CONSTANT = 15.0;  // time constant for cooling

	protected static double			TEMPERATURE_UPDATE_TOLERANCE = 0.0001;
	protected static double			STEP = 5.0/3600.0;  // 5 seconds

	protected KettleState			currentState = KettleState.OFF;
	protected double				currentHeatingPower;

	protected final Duration		integrationStep;
	protected double				temperatureAcc;
	protected Time					start;
	protected double				meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@InternalVariable(type = Double.class)
	protected final DerivableValue<Double>	currentTemperature =
												new DerivableValue<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	staticImplementationInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				TEMPERATURE_UPDATE_TOLERANCE >= 0.0,
				KettleTemperatureSILModel.class,
				"TEMPERATURE_UPDATE_TOLERANCE >= 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				STEP > 0.0,
				KettleTemperatureSILModel.class,
				"STEP > 0.0");
		return ret;
	}

	protected static boolean	implementationInvariants(KettleTemperatureSILModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				KettleTemperatureSILModel.class, instance, "currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.integrationStep.getSimulatedDuration() > 0.0,
				KettleTemperatureSILModel.class, instance,
				"integrationStep.getSimulatedDuration() > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentHeatingPower >= 0.0,
				KettleTemperatureSILModel.class, instance, "currentHeatingPower >= 0.0");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= KettleSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				KettleTemperatureSILModel.class, "URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(KettleTemperatureSILModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				KettleTemperatureSILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	KettleTemperatureSILModel.implementationInvariants(this);
		assert	KettleTemperatureSILModel.invariants(this);
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

	public VariableValue<Double>	getCurrentTemperature()
	{
		return new VariableValue<Double>(
							this.currentTemperature.getValue(),
							this.currentTemperature.getTime());
	}

	@Override
	public void			setCurrentHeatingPower(double newPower, Time t)
	{
		assert	newPower >= 0.0 &&
					newPower <= KettleExternalControlI.MAX_POWER_LEVEL.getData() :
				new NeoSim4JavaException(
					"newPower >= 0.0 && newPower <= MAX_POWER_LEVEL");

		this.currentHeatingPower = newPower;
	}

	protected double	computeDerivatives(Double current)
	{
		double currentTempDerivative = 0.0;

		// Heating contribution
		if (this.currentState == KettleState.HEATING ||
			this.currentState == KettleState.KEEP_WARM) {
			if (this.currentHeatingPower > 0.0) {
				if (current < MAX_TEMPERATURE) {
					currentTempDerivative += this.currentHeatingPower * HEATING_CAPACITY;
				}
			}
		}

		// Cooling contribution (always present)
		if (current > EXTERNAL_TEMPERATURE) {
			currentTempDerivative -= (current - EXTERNAL_TEMPERATURE) / COOLING_CONSTANT;
		}

		return currentTempDerivative;
	}

	protected double	computeNewTemperature(double deltaT)
	{
		Time t = this.currentTemperature.getTime();
		double oldTemp = this.currentTemperature.evaluateAt(t);
		double newTemp;

		if (deltaT > TEMPERATURE_UPDATE_TOLERANCE) {
			double derivative = this.currentTemperature.getFirstDerivative();
			newTemp = oldTemp + derivative*deltaT;
		} else {
			newTemp = oldTemp;
		}

		// Cap at maximum temperature
		if (newTemp > MAX_TEMPERATURE) {
			newTemp = MAX_TEMPERATURE;
		}

		// Don't go below external temperature
		if (newTemp < EXTERNAL_TEMPERATURE) {
			newTemp = EXTERNAL_TEMPERATURE;
		}

		this.temperatureAcc += ((oldTemp + newTemp)/2.0) * deltaT;
		return newTemp;
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
		this.temperatureAcc = 0.0;
		this.start = initialTime;
		this.currentHeatingPower = KettleExternalControlI.MAX_POWER_LEVEL.getData();

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		super.initialiseState(initialTime);
	}

	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		int justInitialised = 0;
		int notInitialisedYet = 0;

		if (!this.currentTemperature.isInitialised()) {
			double derivative = this.computeDerivatives(INITIAL_TEMPERATURE);
			this.currentTemperature.initialise(INITIAL_TEMPERATURE, derivative);
			justInitialised++;
		}

		return new Pair<>(justInitialised, notInitialisedYet);
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	@Override
	public Duration		timeAdvance()
	{
		return this.integrationStep;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		double newDerivative = this.computeDerivatives(newTemp);

		this.currentTemperature.setNewValue(
						newTemp,
						newDerivative,
						new Time(this.getCurrentStateTime().getSimulatedTime(),
								 this.getSimulatedTimeUnit()));

		if (VERBOSE) {
			String mark = "";
			if (this.currentState == KettleState.HEATING) mark = " (heating)";
			else if (this.currentState == KettleState.KEEP_WARM) mark = " (warm)";
			else mark = " (idle)";

			StringBuffer message = new StringBuffer();
			message.append(this.currentTemperature.getTime().getSimulatedTime());
			message.append(mark);
			message.append(" : ");
			message.append(String.format("%.2f", this.currentTemperature.getValue()));
			message.append(" °C");
			this.logMessage(message.toString());
		}

		super.userDefinedInternalTransition(elapsedTime);
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof KettleEventI;

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("executing the external event: ");
			sb.append(ce.eventAsString());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		ce.executeOn(this);
		double newDerivative = this.computeDerivatives(newTemp);

		if (elapsedTime.getSimulatedDuration() > TEMPERATURE_UPDATE_TOLERANCE) {
			this.currentTemperature.setNewValue(
					newTemp,
					newDerivative,
					new Time(this.getCurrentStateTime().getSimulatedTime()
										+ elapsedTime.getSimulatedDuration(),
							 this.getSimulatedTimeUnit()));
		}

		super.userDefinedExternalTransition(elapsedTime);
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		this.meanTemperature =
				this.temperatureAcc/
						endTime.subtract(this.start).getSimulatedDuration();

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Report
	// -------------------------------------------------------------------------

	public static class		KettleTemperatureReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	meanTemperature;

		public			KettleTemperatureReport(String modelURI, double meanTemperature)
		{
			super();
			this.modelURI = modelURI;
			this.meanTemperature = meanTemperature;
		}

		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String	printout(String indent)
		{
			StringBuffer ret = new StringBuffer(indent);
			ret.append("---\n");
			ret.append(indent);
			ret.append('|');
			ret.append(this.modelURI);
			ret.append(" report\n");
			ret.append(indent);
			ret.append('|');
			ret.append("mean temperature = ");
			ret.append(this.meanTemperature);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new KettleTemperatureReport(this.getURI(), this.meanTemperature);
	}
}
// -----------------------------------------------------------------------------
