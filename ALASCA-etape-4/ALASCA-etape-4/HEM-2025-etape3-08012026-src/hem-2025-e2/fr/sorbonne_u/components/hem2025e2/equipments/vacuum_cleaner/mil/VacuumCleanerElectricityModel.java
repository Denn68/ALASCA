package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.alasca.physical_data.MeasurementUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleaner;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerMode;
import fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner.VacuumCleanerImplementationI.VacuumCleanerState;
import fr.sorbonne_u.components.hem2025e1.equipments.meter.ElectricMeterImplementationI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.AbstractVacuumCleanerEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
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
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

// -----------------------------------------------------------------------------
/**
 * The class <code>VacuumCleanerElectricityModel</code> defines a MIL model
 * of the electricity consumption of a vacuum cleaner.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * *
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * lowModeConsumption > 0.0
 * }
 * invariant	{@code
 * highModeConsumption > lowModeConsumption
 * }
 * invariant	{@code
 * totalConsumption >= 0.0
 * }
 * invariant	{@code
 * currentState != null
 * }
 * invariant	{@code
 * !currentIntensity.isInitialised() || currentIntensity.getValue() >= 0.0
 * }
 * </pre>
 * 
 * *
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * *
 * 
 * <pre>
 * invariant	{@code
 * URI != null && !URI.isEmpty()
 * }
 * invariant	{@code
 * LOW_MODE_CONSUMPTION_RPNAME != null && !LOW_MODE_CONSUMPTION_RPNAME.isEmpty()
 * }
 * invariant	{@code
 * HIGH_MODE_CONSUMPTION_RPNAME != null && !HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()
 * }
 * invariant	{@code
 * TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()
 * }
 * </pre>
 * 
 * * @author Team DeMoh
 */
@ModelExternalEvents(imported = { SwitchOnVacuumCleaner.class,
		SwitchOffVacuumCleaner.class,
		SetLowVacuumCleaner.class,
		SetHighVacuumCleaner.class })
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
// -----------------------------------------------------------------------------
public class VacuumCleanerElectricityModel
		extends AtomicHIOA {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static boolean VERBOSE = true;
	public static boolean DEBUG = false;

	public static final String URI = VacuumCleanerElectricityModel.class.getSimpleName();

	protected VacuumCleanerState currentState = VacuumCleanerState.OFF;
	protected VacuumCleanerMode currentMode = VacuumCleanerMode.LOW;
	protected boolean consumptionHasChanged = false;

	protected double lowModeConsumption;
	protected double highModeConsumption;

	protected double totalConsumption;

	// -------------------------------------------------------------------------
	// HIOA model variables
	// -------------------------------------------------------------------------

	@ExportedVariable(type = Double.class)
	protected final Value<Double> currentIntensity = new Value<Double>(this);

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	protected static boolean implementationInvariants(
			VacuumCleanerElectricityModel instance) {
		assert instance != null : new NeoSim4JavaException("Precondition violation: "
				+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.lowModeConsumption > 0.0,
				VacuumCleanerElectricityModel.class,
				instance,
				"lowModeConsumption > 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.highModeConsumption > instance.lowModeConsumption,
				VacuumCleanerElectricityModel.class,
				instance,
				"highModeConsumption > lowModeConsumption");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.totalConsumption >= 0.0,
				VacuumCleanerElectricityModel.class,
				instance,
				"totalConsumption >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
				instance.currentState != null,
				VacuumCleanerElectricityModel.class,
				instance,
				"currentState != null");
		ret &= AssertionChecking.checkImplementationInvariant(
				!instance.currentIntensity.isInitialised() ||
						instance.currentIntensity.getValue() >= 0.0,
				VacuumCleanerElectricityModel.class,
				instance,
				"!currentIntensity.isInitialised() || "
						+ "currentIntensity.getValue() >= 0.0");
		return ret;
	}

	public static boolean staticInvariants() {
		boolean ret = true;
		// ret &= VacuumCleaner.staticInvariants();
		ret &= VacuumCleanerSimulationConfigurationI.staticInvariants();
		ret &= AssertionChecking.checkStaticInvariant(
				URI != null && !URI.isEmpty(),
				VacuumCleanerElectricityModel.class,
				"URI != null && !URI.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				LOW_MODE_CONSUMPTION_RPNAME != null &&
						!LOW_MODE_CONSUMPTION_RPNAME.isEmpty(),
				VacuumCleanerElectricityModel.class,
				"LOW_MODE_CONSUMPTION_RPNAME != null && "
						+ "!LOW_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				HIGH_MODE_CONSUMPTION_RPNAME != null &&
						!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty(),
				VacuumCleanerElectricityModel.class,
				"HIGH_MODE_CONSUMPTION_RPNAME != null && "
						+ "!HIGH_MODE_CONSUMPTION_RPNAME.isEmpty()");
		ret &= AssertionChecking.checkStaticInvariant(
				TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty(),
				VacuumCleanerElectricityModel.class,
				"TENSION_RPNAME != null && !TENSION_RPNAME.isEmpty()");
		return ret;
	}

	protected static boolean invariants(
			VacuumCleanerElectricityModel instance) {
		assert instance != null : new NeoSim4JavaException(
				"Precondition violation: instance != null");

		boolean ret = true;
		ret &= staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public VacuumCleanerElectricityModel(
			String uri,
			TimeUnit simulatedTimeUnit,
			AtomicSimulatorI simulationEngine) throws Exception {
		super(uri, simulatedTimeUnit, simulationEngine);

		this.lowModeConsumption = VacuumCleaner.LOW_POWER.getData();
		this.highModeConsumption = VacuumCleaner.HIGH_POWER.getData();

		this.getSimulationEngine().setLogger(new StandardLogger());

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public VacuumCleanerState getState() {
		return this.currentState;
	}

	public void setStateMode(VacuumCleanerState s, VacuumCleanerMode m) {
		this.currentState = s;
		this.currentMode = m;
	}

	public VacuumCleanerMode getMode() {
		return this.currentMode;
	}

	public void toggleConsumptionHasChanged() {
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
	public void initialiseState(Time startTime) {
		super.initialiseState(startTime);

		this.currentState = VacuumCleanerState.OFF;
		this.currentMode = VacuumCleanerMode.LOW;
		this.consumptionHasChanged = false;
		this.totalConsumption = 0.0;

		if (VERBOSE) {
			this.logMessage("simulation begins.");
		}

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	@Override
	public void initialiseVariables() {
		super.initialiseVariables();

		this.currentIntensity.initialise(0.0);

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	@Override
	public ArrayList<EventI> output() {
		return null;
	}

	@Override
	public Duration timeAdvance() {
		Duration ret = null;
		if (this.consumptionHasChanged) {
			this.consumptionHasChanged = false;
			ret = Duration.zero(this.getSimulatedTimeUnit());
		} else {
			ret = Duration.INFINITY;
		}

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");

		return ret;
	}

	@Override
	public void userDefinedInternalTransition(Duration elapsedTime) {
		super.userDefinedInternalTransition(elapsedTime);

		Time t = this.getCurrentStateTime();
		if (this.currentState == VacuumCleanerState.ON) {
			switch (this.currentMode) {
				case LOW:
					this.currentIntensity.setNewValue(
							this.lowModeConsumption / VacuumCleaner.TENSION.getData(),
							t);
					break;
				case HIGH:
					this.currentIntensity.setNewValue(
							this.highModeConsumption / VacuumCleaner.TENSION.getData(),
							t);
			}
		} else {
			this.currentIntensity.setNewValue(0.0, t);
		}

		if (VERBOSE) {
			StringBuffer message = new StringBuffer("executes an internal transition ");
			message.append("with current consumption ");
			message.append(this.currentIntensity.getValue());
			message.append(" ");
			message.append(ElectricMeterImplementationI.POWER_UNIT);
			message.append(" at ");
			message.append(this.currentIntensity.getTime());
			this.logMessage(message.toString());
		}

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	@Override
	public void userDefinedExternalTransition(Duration elapsedTime) {
		super.userDefinedExternalTransition(elapsedTime);

		ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
		assert currentEvents != null && currentEvents.size() == 1;

		Event ce = (Event) currentEvents.get(0);

		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption += Electricity.computeConsumption(
					elapsedTime,
					this.currentIntensity.getValue());
		} else {
			this.totalConsumption += Electricity.computeConsumption(
					elapsedTime,
					VacuumCleaner.TENSION.getData() *
							this.currentIntensity.getValue());
		}

		if (VERBOSE) {
			StringBuffer message = new StringBuffer("executes an external transition ");
			message.append(ce.toString());
			message.append(")");
			this.logMessage(message.toString());
		}

		assert ce instanceof AbstractVacuumCleanerEvent : new RuntimeException(
				ce + " is not an event that an VacuumCleanerElectricityModel"
						+ " can receive and process.");
		ce.executeOn(this);

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	@Override
	public void endSimulation(Time endTime) {
		Duration d = endTime.subtract(this.getCurrentStateTime());
		if (ElectricMeterImplementationI.POWER_UNIT.equals(MeasurementUnit.WATTS)) {
			this.totalConsumption += Electricity.computeConsumption(
					d,
					this.currentIntensity.getValue());
		} else {
			this.totalConsumption += Electricity.computeConsumption(
					d,
					VacuumCleaner.TENSION.getData() *
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

	public static final String LOW_MODE_CONSUMPTION_RPNAME = URI + ":LOW_MODE_CONSUMPTION";

	public static final String HIGH_MODE_CONSUMPTION_RPNAME = URI + ":HIGH_MODE_CONSUMPTION";
	public static final String TENSION_RPNAME = URI + ":TENSION";

	@Override
	public void setSimulationRunParameters(
			Map<String, Object> simParams) throws MissingRunParameterException {
		super.setSimulationRunParameters(simParams);

		String lowName = ModelI.createRunParameterName(getURI(),
				LOW_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(lowName)) {
			this.lowModeConsumption = (double) simParams.get(lowName);
		}

		String highName = ModelI.createRunParameterName(getURI(),
				HIGH_MODE_CONSUMPTION_RPNAME);
		if (simParams.containsKey(highName)) {
			this.highModeConsumption = (double) simParams.get(highName);
		}

		assert VacuumCleanerElectricityModel.implementationInvariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.implementationInvariants("
						+ "this)");
		assert VacuumCleanerElectricityModel.invariants(this) : new NeoSim4JavaException(
				"VacuumCleanerElectricityModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Optional DEVS simulation protocol: simulation report
	// -------------------------------------------------------------------------

	public static class VacuumCleanerElectricityReport
			implements SimulationReportI, GlobalReportI {
		private static final long serialVersionUID = 1L;
		protected String modelURI;
		protected double totalConsumption; // in kwh

		public VacuumCleanerElectricityReport(
				String modelURI,
				double totalConsumption) {
			super();
			this.modelURI = modelURI;
			this.totalConsumption = totalConsumption;
		}

		@Override
		public String getModelURI() {
			return this.modelURI;
		}

		@Override
		public String printout(String indent) {
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
		public String toString() {
			return this.printout("");

		}
	}

	@Override
	public SimulationReportI getFinalReport() {
		return new VacuumCleanerElectricityReport(this.getURI(),
				this.totalConsumption);
	}
}
// -----------------------------------------------------------------------------