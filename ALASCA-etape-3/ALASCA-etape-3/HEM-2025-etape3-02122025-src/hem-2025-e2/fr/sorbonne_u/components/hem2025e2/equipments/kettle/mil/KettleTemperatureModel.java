package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.InternalVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelImportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
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
 * The class <code>KettleTemperatureModel</code> defines a simulation model
 * for the temperature of the water inside the kettle.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code TEMPERATURE_UPDATE_TOLERANCE >= 0.0}
 * invariant	{@code STEP > 0.0}
 * invariant	{@code currentState != null}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * * * @author	Team DeMoh
 */
@ModelExternalEvents(imported = {SwitchOnKettle.class,
								 SwitchOffKettle.class,
		 						 HeatKettle.class,
		 						 DoNotHeatKettle.class})
@ModelImportedVariable(name = "currentHeatingPower", type = Double.class)
// -----------------------------------------------------------------------------
public class			KettleTemperatureModel
extends		AtomicHIOA
implements	KettleOperationI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** URI for a model; works when only one instance is created.			*/
	public static String		URI = KettleTemperatureModel.class.getSimpleName();
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;

	public static double		INITIAL_TEMPERATURE = 20.0; 
	public static double		EXTERNAL_TEMPERATURE = 20.0;    
	public static double		MAX_TEMPERATURE = 100.0;

	protected static double		HEATING_CAPACITY = 0.86; 
	protected static double 	COOLING_CONSTANT = 15.0; 

	protected static double		TEMPERATURE_UPDATE_TOLERANCE = 0.0001;
	protected static double		STEP = 5.0/3600.0;

	protected KettleState		currentState = KettleState.OFF;

	protected final Duration	integrationStep;
	protected double			temperatureAcc;
	protected Time				start;
	protected double			meanTemperature;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ImportedVariable(type = Double.class)
	protected Value<Double>					currentHeatingPower;

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
				KettleTemperatureModel.class,
				"TEMPERATURE_UPDATE_TOLERANCE >= 0.0");
		ret &= AssertionChecking.checkStaticImplementationInvariant(
				STEP > 0.0,
				KettleTemperatureModel.class,
				"STEP > 0.0");
		return ret;
	}

	protected static boolean	implementationInvariants(
		KettleTemperatureModel instance
		)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				KettleTemperatureModel.class, instance, "currentState != null");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= KettleSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				KettleTemperatureModel.class,
				"URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(KettleTemperatureModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				KettleTemperatureModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.integrationStep = new Duration(STEP, simulatedTimeUnit);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	KettleTemperatureModel.implementationInvariants(this);
		assert	KettleTemperatureModel.invariants(this);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public void			setState(KettleState s)
	{
		this.currentState = s;
		assert	KettleTemperatureModel.implementationInvariants(this);
		assert	KettleTemperatureModel.invariants(this);
	}

	@Override
	public KettleState	getState()
	{
		return this.currentState;
	}

	protected double	computeDerivatives(Double current)
	{
		double currentTempDerivative = 0.0;
		
		if (this.currentState == KettleState.HEATING || this.currentState == KettleState.KEEP_WARM) {
			if (this.currentHeatingPower.getValue() > 0.0) {
				if (current < MAX_TEMPERATURE) {
					currentTempDerivative += this.currentHeatingPower.getValue() * HEATING_CAPACITY;
				}
			}
		}

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
		
		if (newTemp > MAX_TEMPERATURE) {
			newTemp = MAX_TEMPERATURE;
		}
		
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
	public void			initialiseState(Time initialTime)
	{
		this.temperatureAcc = 0.0;
		this.start = initialTime;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		super.initialiseState(initialTime);

		assert	KettleTemperatureModel.implementationInvariants(this) :
			new NeoSim4JavaException(
					"KettleTemperatureModel.implementationInvariants(this)");
		assert	KettleTemperatureModel.invariants(this) :
			new NeoSim4JavaException(
					"KettleTemperatureModel.invariants(this)");
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

		if (!this.currentTemperature.isInitialised() && this.currentHeatingPower.isInitialised()) {
			double derivative = this.computeDerivatives(INITIAL_TEMPERATURE);
			this.currentTemperature.initialise(INITIAL_TEMPERATURE, derivative);
			justInitialised++;
		} else if (!this.currentTemperature.isInitialised()) {
			notInitialisedYet++;
		}

		assert	KettleTemperatureModel.implementationInvariants(this) :
			new NeoSim4JavaException(
					"KettleTemperatureModel.implementationInvariants(this)");
		assert	KettleTemperatureModel.invariants(this) :
			new NeoSim4JavaException(
					"KettleTemperatureModel.invariants(this)");

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
			if(this.currentState == KettleState.HEATING) mark = " (heating)";
			else if(this.currentState == KettleState.KEEP_WARM) mark = " (warm)";
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

		assert	KettleTemperatureModel.implementationInvariants(this);
		assert	KettleTemperatureModel.invariants(this);
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		
		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("executing the external event: ");
			sb.append(ce.eventAsString());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());
		
		ce.executeOn(this);
		
		double newDerivative = this.computeDerivatives(newTemp);
		
		this.currentTemperature.setNewValue(
					newTemp,
					newDerivative,
					new Time(this.getCurrentStateTime().getSimulatedTime()
										+ elapsedTime.getSimulatedDuration(),
							 this.getSimulatedTimeUnit()));

		super.userDefinedExternalTransition(elapsedTime);

		assert	KettleTemperatureModel.implementationInvariants(this);
		assert	KettleTemperatureModel.invariants(this);
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