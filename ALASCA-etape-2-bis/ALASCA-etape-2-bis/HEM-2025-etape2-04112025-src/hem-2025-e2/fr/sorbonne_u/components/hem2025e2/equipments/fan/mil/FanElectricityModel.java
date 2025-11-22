package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanCoupledModel.FanSpeed;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
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
 * invariant	{@code currentSpeed != null}
 * invariant	{@code totalConsumption >= 0.0}
 * invariant	{@code !currentFanPower.isInitialised() || currentFanPower.getValue() >= 0.0}
 * invariant	{@code !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0}
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code URI != null && !URI.isEmpty()}
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
	/** URI for a model; works when only one instance is created.			*/
	public static final String	URI = FanElectricityModel.class.getSimpleName();

	// Consommations définies dans le composant Fan (reprises ici pour le modèle)
	// Idéalement, on pourrait les passer en paramètres de simulation.
	public static final double CONSUMPTION_LOW = 20.0; 
	public static final double CONSUMPTION_HIGH = 60.0;

	/** current speed of the fan.											*/
	protected FanSpeed			currentSpeed = FanSpeed.OFF;
	
	/** true when the electricity consumption has changed.					*/
	protected boolean			consumptionHasChanged = false;

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

	protected static boolean	staticImplementationInvariants()
	{
		return true;
	}

	protected static boolean	implementationInvariants(
		FanElectricityModel instance
		)
	{
		assert	instance != null : new NeoSim4JavaException("instance != null");

		boolean ret = true;
		ret &= staticImplementationInvariants();
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.currentSpeed != null,
					FanElectricityModel.class, instance, "currentSpeed != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.totalConsumption >= 0.0,
					FanElectricityModel.class, instance, "totalConsumption >= 0.0");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanElectricityModel.class, "URI != null && !URI.isEmpty()");
		return ret;
	}

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

	public				FanElectricityModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		this.getSimulationEngine().setLogger(new StandardLogger());

		assert	FanElectricityModel.implementationInvariants(this);
		assert	FanElectricityModel.invariants(this);
	}

	// -------------------------------------------------------------------------
	// Methods called by Events
	// -------------------------------------------------------------------------

	/**
	 * set the speed of the fan.
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
	 * return the speed of the fan.
	 * @return	the current speed.
	 */
	public FanSpeed		getSpeed()
	{
		return this.currentSpeed;
	}

	// -------------------------------------------------------------------------
	// DEVS simulation protocol
	// -------------------------------------------------------------------------

	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.currentSpeed = FanSpeed.OFF;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		this.getSimulationEngine().toggleDebugMode();
		this.logMessage("Fan simulation begins.");

		assert	FanElectricityModel.implementationInvariants(this);
		assert	FanElectricityModel.invariants(this);
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

		if (!this.currentIntensity.isInitialised() ||
								!this.currentFanPower.isInitialised()) {
			// initially, the fan is off, so its consumption is zero.
			this.currentIntensity.initialise(0.0);
			this.currentFanPower.initialise(0.0);

			StringBuffer sb = new StringBuffer("Fan initial consumption: ");
			sb.append(this.currentIntensity.getValue());
			sb.append(" ");
			sb.append(ElectricMeterImplementationI.POWER_UNIT);
			this.logMessage(sb.toString());
			ret = new Pair<>(2, 0);
		} else {
			ret = new Pair<>(0, 0);
		}

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
			// Immediate transition to update consumption
			this.consumptionHasChanged = false;
			ret = Duration.zero(this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}
		return ret;
	}

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
		
		// I = P / U
		double voltage = FanCoupledModel.VOLTAGE.getData();
		this.currentIntensity.setNewValue(newPower / voltage, t);

		StringBuffer sb = new StringBuffer("Fan new consumption: ");
		sb.append(this.currentIntensity.getValue());
		sb.append(" A (");
		sb.append(this.currentFanPower.getValue());
		sb.append(" W) at ");
		sb.append(this.currentIntensity.getTime());
		this.logMessage(sb.toString());
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);
		assert	ce instanceof FanEventI;

		// compute the total consumption for the simulation report.
		// Energy (kWh) = Time (h) * Power (kW) -> handled by helper
		this.totalConsumption +=
				Electricity.computeConsumption(
						elapsedTime,
						FanCoupledModel.VOLTAGE.getData() *
											this.currentIntensity.getValue());

		StringBuffer sb = new StringBuffer("Fan executes external event: ");
		sb.append(ce.eventAsString());
		this.logMessage(sb.toString());

		// Execute event to update state (currentSpeed)
		ce.executeOn(this);

		assert	FanElectricityModel.implementationInvariants(this);
		assert	FanElectricityModel.invariants(this);
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		this.totalConsumption +=
				Electricity.computeConsumption(
						d,
						FanCoupledModel.VOLTAGE.getData() *
											this.currentIntensity.getValue());

		this.logMessage("Fan simulation ends.");
		super.endSimulation(endTime);
	}

	@Override
	public SimulationReportI	getFinalReport()
	{
		return new FanElectricityReport(this.getURI(), this.totalConsumption);
	}

	// -------------------------------------------------------------------------
	// Report Class
	// -------------------------------------------------------------------------

	public static class		FanElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption; // in kwh

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
	}
}
// -----------------------------------------------------------------------------