package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanElectricityModel</code> defines a simulation model
 * for the electricity consumption of the fan.
 *
 * <p><strong>Description</strong></p>
 * * <p>
 * The electric power consumption depends upon the speed state:
 * {@code FanSpeed.OFF => consumption == 0.0},
 * {@code FanSpeed.LOW => consumption == CONSUMPTION_LOW},
 * {@code FanSpeed.HIGH => consumption == CONSUMPTION_HIGH}.
 * </p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code lowModeConsumption > 0.0}
 * invariant	{@code highModeConsumption > lowModeConsumption}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code currentSpeed != null}
 * invariant	{@code !currentFanPower.isInitialised() || currentFanPower.getValue() >= 0.0}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * invariant	{@code LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()}
 * invariant	{@code TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()}
 * </pre>
 * * @author	Team DeMoh
 */
@ModelExternalEvents(imported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowSpeedFan.class,
								 SetHighSpeedFan.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentFanPower", type = Double.class)
//-----------------------------------------------------------------------------
public class			FanElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** when true, leaves a trace of the execution of the model.			*/
	public static boolean		VERBOSE = true;
	/** when true, leaves a debugging trace of the execution of the model.	*/
	public static boolean		DEBUG = false;

	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = FanElectricityModel.class.getSimpleName();

	/** current speed of the fan.											*/
	protected FanSpeed			currentSpeed = FanSpeed.OFF;
	/** true when the electricity consumption has changed.					*/
	protected boolean			consumptionHasChanged = false;

	public static double 		CONSUMPTION_LOW = 20.0; 
	public static double 		CONSUMPTION_HIGH = 60.0;

	/** total consumption of the fan during the simulation in kwh.			*/
	protected double			totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** the current fan power in Watts.										*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentFanPower = new Value<Double>(this);
	
	/** current intensity in Amperes.										*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false
	 * otherwise.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(
		FanElectricityModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				FanElectricityModel.CONSUMPTION_LOW > 0.0,
				FanElectricityModel.class,
				instance,
				"lowModeConsumption > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				FanElectricityModel.CONSUMPTION_HIGH > FanElectricityModel.CONSUMPTION_LOW,
				FanElectricityModel.class,
				instance,
				"highModeConsumption > lowModeConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				FanElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentSpeed != null,
				FanElectricityModel.class,
				instance,
				"currentSpeed != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised() ||
									instance.currentIntensity.getValue() >= 0.0,
				FanElectricityModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0");
		return ret;
	}

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the static invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanElectricityModel.class, "URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricityModel.class, "LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricityModel.class, "HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(FanElectricityModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a fan MIL model instance.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
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
	 * @throws Exception		<i>to do</i>.
	 */
	public				FanElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the speed of the fan.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 * * @return	the current speed.
	 */
	public FanSpeed		getSpeed()
	{
		return this.currentSpeed;
	}

	/**
	 * set the speed of the fan.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code s != null}
	 * post	{@code getSpeed() == s}
	 * </pre>
	 * * @param s		the new speed.
	 * @param t		time at which the speed is set.
	 */
	public void			setSpeed(FanSpeed s, Time t)
	{
		FanSpeed old = this.currentSpeed;
		this.currentSpeed = s;
		if (old != s) {
			this.consumptionHasChanged = true;					
		}
	}

	/**
	 * toggle the value of the state of the model telling whether the
	 * electricity consumption level has just changed or not; when it changes
	 * after receiving an external event, an immediate internal transition
	 * is triggered to update the level of electricity consumption.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			toggleConsumptionHasChanged()
	{
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
		} else {
			this.consumptionHasChanged = true;
		}
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time startTime)
	{
		super.initialiseState(startTime);

		this.currentSpeed = FanSpeed.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#initialiseVariables()
	 */
	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		this.currentIntensity.initialise(0.0);
		this.currentFanPower.initialise(0.0);

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		Pair<Integer, Integer> ret = null;

		if (!this.currentIntensity.isInitialised() ||
								!this.currentFanPower.isInitialised()) {
			this.currentIntensity.initialise(0.0);
			this.currentFanPower.initialise(0.0);

			if (VERBOSE) {
				StringBuffer sb = new StringBuffer("new consumption: ");
				sb.append(this.currentIntensity.getValue());
				sb.append(" ");
				sb.append(ElectricMeterImplementationI.POWER_UNIT);
				sb.append(" at ");
				sb.append(this.currentIntensity.getTime());
				sb.append(" seconds.");
				this.logMessage(sb.toString());
			}
			ret = new Pair<>(2, 0);
		} else {
			ret = new Pair<>(0, 0);
		}

		return ret;
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
		Duration ret = null;

		if (this.consumptionHasChanged) {
			this.toggleConsumptionHasChanged();
			ret = new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}
		
		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
		
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		double newPower = 0.0;

		switch (this.currentSpeed) {
			case LOW:
				newPower = CONSUMPTION_LOW;
				break;
			case HIGH:
				newPower = CONSUMPTION_HIGH;
				break;
			case OFF:
			default:
				newPower = 0.0;
				break;
		}

		this.currentFanPower.setNewValue(newPower, t);
		
		double voltage = FanExternalControlI.VOLTAGE.getData();
		this.currentIntensity.setNewValue(newPower / voltage, t);

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("executes an internal transition ");
			sb.append("with current consumption ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" ");
			sb.append(ElectricMeterImplementationI.POWER_UNIT);
			sb.append(" at ");
			sb.append(this.currentIntensity.getTime());
			sb.append(".");
			this.logMessage(sb.toString());
		}
		
		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof FanEventI;

		this.totalConsumption +=
				Electricity.computeConsumption(
						elapsedTime,
						FanExternalControlI.VOLTAGE.getData() *
											this.currentIntensity.getValue());

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("execute the external event: ");
			sb.append(ce.eventAsString());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		ce.executeOn(this);

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
						d,
						FanExternalControlI.VOLTAGE.getData() *
											this.currentIntensity.getValue());

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	public static final String		LOW_MODE_CONSUMPTION_RPNAME =
												URI + ":LOW_MODE_CONSUMPTION";
	public static final String		HIGH_MODE_CONSUMPTION_RPNAME =
												URI + ":HIGH_MODE_CONSUMPTION";
	public static final String		TENSION_RPNAME = URI + ":TENSION";

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(getURI(),
										  LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			CONSUMPTION_LOW = (double) simParams.get(lowName);
		}
		String highName =
			ModelI.createRunParameterName(getURI(),
										  HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			CONSUMPTION_HIGH = (double) simParams.get(highName);
		}

		assert	FanElectricityModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.implementationInvariants(this)");
		assert	FanElectricityModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Report Class
	// -------------------------------------------------------------------------

	public static class		FanElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption;

		public			FanElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
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
			ret.append("total consumption in kwh = ");
			ret.append(this.totalConsumption);
			ret.append(".\n");
			ret.append(indent);
			ret.append("---\n");
			return ret.toString();
		}
		
		@Override
		public String	toString()
		{
			return this.printout("");
			
		}
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new FanElectricityReport(this.getURI(), this.totalConsumption);
	}
}