package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.FanEventI;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;
import fr.sorbonne_u.exceptions.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanElectricitySILModel</code> defines a SIL model
 * of the electricity consumption of a fan.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The fan can be switched on and off, and when switched on, it can be
 * either at low speed, with lower electricity consumption, or high speed,
 * with higher electricity consumption.
 * </p>
 * 
 * <ul>
 * <li>Imported events:
 *   {@code SwitchOnFan}, {@code SwitchOffFan},
 *   {@code SetLowSpeedFan}, {@code SetHighSpeedFan}</li>
 * <li>Exported events: none</li>
 * <li>Exported variables:
 *   name = {@code currentIntensity}, type = {@code Double}</li>
 * </ul>
 * 
 * <p>Created on : 2026-06-06</p>
 * @author	Team
 */
// -----------------------------------------------------------------------------
@ModelExternalEvents(imported = {SwitchOnFan.class,
								 SwitchOffFan.class,
								 SetLowSpeedFan.class,
								 SetHighSpeedFan.class})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class			FanElectricitySILModel
extends		AtomicHIOA
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	public static boolean			VERBOSE = true;
	public static boolean			DEBUG = false;

	public static final String		URI = FanElectricitySILModel.class.
																getSimpleName();

	/** current speed (OFF, LOW, HIGH) of the fan.							*/
	protected FanSpeed				currentSpeed = FanSpeed.OFF;
	/** true when the electricity consumption has changed.					*/
	protected boolean				consumptionHasChanged = false;

	/** power consumption at LOW speed in watts.							*/
	protected double				lowSpeedConsumption;
	/** power consumption at HIGH speed in watts.							*/
	protected double				highSpeedConsumption;
	/** tension in volts.													*/
	protected double				tension;

	/** total consumption during the simulation in kwh.						*/
	protected double				totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	/** current intensity in amperes.										*/
	@ExportedVariable(type = Double.class)
	protected final Value<Double>	currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean	implementationInvariants(
		FanElectricitySILModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.lowSpeedConsumption > 0.0,
				FanElectricitySILModel.class,
				instance,
				"lowSpeedConsumption > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.highSpeedConsumption > instance.lowSpeedConsumption,
				FanElectricitySILModel.class,
				instance,
				"highSpeedConsumption > lowSpeedConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				FanElectricitySILModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentSpeed != null,
				FanElectricitySILModel.class,
				instance,
				"currentSpeed != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised() ||
								instance.currentIntensity.getValue() >= 0.0,
				FanElectricitySILModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
				+ "currentIntensity.getValue() >= 0.0");
		return ret;
	}

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanCyPhy.staticInvariants();
		ret &= FanSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				FanElectricitySILModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_SPEED_CONSUMPTION_RPNAME != null &&
								!LOW_SPEED_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"LOW_SPEED_CONSUMPTION_RPNAME != null && "
						+ "!LOW_SPEED_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_SPEED_CONSUMPTION_RPNAME != null &&
								!HIGH_SPEED_CONSUMPTION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"HIGH_SPEED_CONSUMPTION_RPNAME != null && "
						+ "!HIGH_SPEED_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				FanElectricitySILModel.class,
				"TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()");
		return ret;
	}

	protected static boolean	invariants(
		FanElectricitySILModel instance
		)
	{
		assert	instance != null :
				new NeoSim4JavaException(
						"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanElectricitySILModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.lowSpeedConsumption = FanCyPhy.LOW_POWER.getData();
		this.highSpeedConsumption = FanCyPhy.HIGH_POWER.getData();
		this.tension = FanCyPhy.TENSION.getData();

		if (VERBOSE || DEBUG) {
			this.getSimulationEngine().setLogger(new StandardLogger());
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

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

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			initialiseVariables()
	{
		super.initialiseVariables();

		this.currentIntensity.initialise(0.0);

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
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
			this.toggleConsumptionHasChanged();
			ret = new Duration(0.0, this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");

		return ret;
	}

	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		switch (this.currentSpeed)
		{
			case OFF :
				this.currentIntensity.setNewValue(0.0, t);
				break;
			case LOW :
				this.currentIntensity.setNewValue(
								this.lowSpeedConsumption/this.tension, t);
				break;
			case HIGH :
				this.currentIntensity.setNewValue(
								this.highSpeedConsumption/this.tension, t);
				break;
		}

		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an internal transition ");
			message.append("with current consumption ");
			message.append(this.currentIntensity.getValue());
			message.append(" ");
			message.append(ElectricMeterImplementationI.POWER_UNIT);
			message.append(" at ");
			message.append(this.currentIntensity.getTime());
			this.logMessage(message.toString());
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert	currentEvents != null && currentEvents.size() == 1;

		EventI ce = currentEvents.get(0);

		// compute the total consumption for the report
		if (ElectricMeterImplementationI.POWER_UNIT.equals(
												MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
								elapsedTime,
								this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
								elapsedTime,
								this.tension *
									this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			StringBuffer message =
					new StringBuffer("executes an external transition ");
			message.append(ce.toString());
			message.append(")");
			this.logMessage(message.toString());
		}

		// Handle events manually since the e2 events cast to the MIL
		// model class FanElectricityModel rather than an operations interface
		assert	ce instanceof FanEventI :
				new RuntimeException(
						ce + " is not a Fan event.");

		if (ce instanceof SwitchOnFan) {
			if (this.currentSpeed == FanSpeed.OFF) {
				this.currentSpeed = FanSpeed.LOW;
				this.toggleConsumptionHasChanged();
			}
		} else if (ce instanceof SwitchOffFan) {
			if (this.currentSpeed != FanSpeed.OFF) {
				this.currentSpeed = FanSpeed.OFF;
				this.toggleConsumptionHasChanged();
			}
		} else if (ce instanceof SetHighSpeedFan) {
			if (this.currentSpeed != FanSpeed.OFF &&
					this.currentSpeed != FanSpeed.HIGH) {
				this.currentSpeed = FanSpeed.HIGH;
				this.toggleConsumptionHasChanged();
			}
		} else if (ce instanceof SetLowSpeedFan) {
			if (this.currentSpeed != FanSpeed.OFF &&
					this.currentSpeed != FanSpeed.LOW) {
				this.currentSpeed = FanSpeed.LOW;
				this.toggleConsumptionHasChanged();
			}
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	@Override
	public void			endSimulation(Time endTime)
	{
		Duration d = endTime.subtract(this.getCurrentStateTime());
		if (ElectricMeterImplementationI.POWER_UNIT.equals(
												MeasurementUnit.WATTS)) {
			this.totalConsumption +=
					Electricity.computeConsumption(
								d,
								this.currentIntensity.getValue());
		} else {
			this.totalConsumption +=
					Electricity.computeConsumption(
								d,
								this.tension *
									this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			this.logMessage("simulation ends.");
		}
		super.endSimulation(endTime);
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation run parameters
	// -------------------------------------------------------------------------

	public static final String		LOW_SPEED_CONSUMPTION_RPNAME =
														"LOW_SPEED_CONSUMPTION";
	public static final String		HIGH_SPEED_CONSUMPTION_RPNAME =
														"HIGH_SPEED_CONSUMPTION";
	public static final String		TENSION_RPNAME = "TENSION";

	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		super.setSimulationRunParameters(simParams);

		String lowName =
			ModelI.createRunParameterName(this.getURI(),
										  LOW_SPEED_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			this.lowSpeedConsumption = (double) simParams.get(lowName);
		}
		String highName =
			ModelI.createRunParameterName(this.getURI(),
										  HIGH_SPEED_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			this.highSpeedConsumption = (double) simParams.get(highName);
		}
		String tensionName =
				ModelI.createRunParameterName(getURI(), TENSION_RPNAME);
		if (simParams.containsKey(tensionName)) {
			this.tension = (double) simParams.get(tensionName);
		}

		if (simParams.containsKey(
						AtomicSimulatorPlugin.OWNER_RUNTIME_PARAMETER_NAME)) {
			this.getSimulationEngine().setLogger(
						AtomicSimulatorPlugin.createComponentLogger(simParams));
		}

		assert	FanElectricitySILModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel."
						+ "implementationInvariants(this)");
		assert	FanElectricitySILModel.invariants(this) :
				new NeoSim4JavaException(
						"FanElectricitySILModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class		FanElectricityReport
	implements	SimulationReportI, GlobalReportI
	{
		private static final long serialVersionUID = 1L;
		protected String	modelURI;
		protected double	totalConsumption;

		public				FanElectricityReport(
			String modelURI,
			double totalConsumption
			)
		{
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String		getModelURI()
		{
			return this.modelURI;
		}

		@Override
		public String		printout(String indent)
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
		return new FanElectricityReport(this.getURI(),
										this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------
