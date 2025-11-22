package fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel.KettleState;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.KettleEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SetPowerKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
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
 * The class <code>KettleElectricityModel</code> defines a simulation model
 * for the electricity consumption of the kettle.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code NOT_HEATING_POWER >= 0.0}
 * invariant	{@code MAX_HEATING_POWER > NOT_HEATING_POWER}
 * invariant	{@code TENSION > 0.0}
 * invariant	{@code currentState != null}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code !currentHeatingPower.isInitialised() || currentHeatingPower.getValue() >= 0.0}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
 * </pre>
 * * @author	Team DeMoh
 */
@ModelExternalEvents(imported = {SwitchOnKettle.class,
								 SwitchOffKettle.class,
								 SetPowerKettle.class,
								 HeatKettle.class,
								 DoNotHeatKettle.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
//-----------------------------------------------------------------------------
public class			KettleElectricityModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	public static final String	URI = KettleElectricityModel.class.getSimpleName();
	public static boolean		VERBOSE = true;
	public static boolean		DEBUG = false;

	// Consommation en mode "Keep Warm" (supposons 10% de la puissance max ou une valeur fixe)
	protected static final double KEEP_WARM_POWER = 200.0; // Watts

	protected KettleState		currentState = KettleState.OFF;
	protected boolean			consumptionHasChanged = false;
	protected double			totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentHeatingPower = new Value<Double>(this);
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	staticImplementationInvariants()
	{
		return true;
	}

	protected static boolean	implementationInvariants(KettleElectricityModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.currentState != null,
					KettleElectricityModel.class, instance, "currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.totalConsumption >= 0.0,
					KettleElectricityModel.class, instance, "totalConsumption >= 0.0");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= KettleSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				KettleElectricityModel.class, "URI != null && !URI.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(KettleElectricityModel instance)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");
		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				KettleElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public void			setState(KettleState s, Time t)
	{
		KettleState old = this.currentState;
		this.currentState = s;
		if (old != s) {
			this.consumptionHasChanged = true;					
		}
		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	public KettleState		getState()
	{
		return this.currentState;
	}

	public void			setCurrentHeatingPower(double newPower, Time t)
	{
		double oldPower = this.currentHeatingPower.getValue();
		this.currentHeatingPower.setNewValue(newPower, t);
		if (newPower != oldPower) {
			this.consumptionHasChanged = true;
		}
		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);
		this.currentState = KettleState.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		return true;
	}

	@Override
	public Pair<Integer, Integer> fixpointInitialiseVariables()
	{
		Pair<Integer, Integer> ret = null;

		if (!this.currentIntensity.isInitialised() || !this.currentHeatingPower.isInitialised()) {
			this.currentIntensity.initialise(0.0);
			this.currentHeatingPower.initialise(KettleExternalControlI.MAX_POWER_LEVEL.getData());

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

		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
		return ret;
	}

	@Override
	public ArrayList<EventI>	output()
	{
		return null;
	}

	@Override
	public Duration		timeAdvance()
	{
		Duration ret = null;
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
			ret = Duration.zero(this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}
		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
		return ret;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		double voltage = KettleExternalControlI.VOLTAGE.getData();

		if (this.currentState == KettleState.ON) {
			// ON mais pas HEATING (veille) = 0 W (simplification)
			this.currentIntensity.setNewValue(0.0, t);
		} 
		else if (this.currentState == KettleState.HEATING) {
			// Puissance de chauffe (variable ou max par défaut)
			this.currentIntensity.setNewValue(this.currentHeatingPower.getValue() / voltage, t);
		} 
		else if (this.currentState == KettleState.KEEP_WARM) {
			// Puissance réduite pour maintenir au chaud
			this.currentIntensity.setNewValue(KEEP_WARM_POWER / voltage, t);
		}
		else {
			// OFF
			this.currentIntensity.setNewValue(0.0, t);
		}

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("new consumption: ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" A at ");
			sb.append(this.currentIntensity.getTime());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof KettleEventI;

		this.totalConsumption += Electricity.computeConsumption(
						elapsedTime,
						KettleExternalControlI.VOLTAGE.getData() * this.currentIntensity.getValue());

		if (VERBOSE) {
			StringBuffer sb = new StringBuffer("execute the external event: ");
			sb.append(ce.eventAsString());
			sb.append(".");
			this.logMessage(sb.toString());
		}

		ce.executeOn(this);

		assert	KettleElectricityModel.implementationInvariants(this);
		assert	KettleElectricityModel.invariants(this);
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption += Electricity.computeConsumption(
						d,
						KettleExternalControlI.VOLTAGE.getData() * this.currentIntensity.getValue());

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class		KettleElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

		public			KettleElectricityReport(String modelURI, double totalConsumption)
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
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new KettleElectricityReport(this.getURI(), this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------