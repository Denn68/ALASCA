package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
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

/**
 * La classe <code>WashingMachineTemperatureModel</code> définit un modèle de simulation
 * pour la température de l'eau dans le tambour d'une machine à laver.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * Le modèle est implémenté comme un modèle atomique HIOA. Une équation différentielle
 * définit la variation de température au fil du temps. Le modèle utilise une approche
 * mathématique simple où la dérivée est proportionnelle à la différence entre la
 * température actuelle et la température cible (élément chauffant ou refroidissement).
 * </p>
 *
 * <p>
 * Quand l'état est HEATINGWATER, la température monte vers la température de l'élément
 * chauffant. Quand la température cible est atteinte, la machine devrait passer en
 * mode WASHING.
 * </p>
 *
 * <ul>
 * <li>Événements importés:
 *   {@code SwitchOffWashingMachine},
 *   {@code StartWashing},
 *   {@code SuspendWashing},
 *   {@code ResumeWashing}</li>
 * <li>Événements exportés: aucun</li>
 * <li>Variables importées:
 *   name = {@code currentHeatingPower}, type = {@code Double}</li>
 * <li>Variables internes:
 *   name = {@code currentWaterTemperature}, type = {@code Double}</li>
 * </ul>
 */
@ModelExternalEvents(imported = {
    SwitchOffWashingMachine.class,
    SwitchOnWashingMachine.class,
    StartWashing.class,
    SetDelayedStart.class,
    SuspendWashing.class,
    ResumeWashing.class,
    WashingEnded.class
}, exported = {
		HeatingFinished.class
	})
@ModelImportedVariable(name = "currentHeatingPower", type = Double.class)
public class WashingMachineTemperatureModel
extends AtomicHIOA
implements WashingMachineOperationI
{
    // -------------------------------------------------------------------------
    // Constants and variables
    // -------------------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    /** URI for the model; works when only one instance is created. */
    public static String URI = WashingMachineTemperatureModel.class.getSimpleName();

    /** When true, leaves a trace of the execution of the model. */
    public static boolean VERBOSE = true;

    /** When true, leaves a debugging trace of the execution of the model. */
    public static boolean DEBUG = false;

    // --- Physical parameters ---

    /** Temperature of cold water entering the machine (in Celsius). */
    protected static double COLD_WATER_TEMPERATURE = 15.0;

    /** Temperature of the heating element (in Celsius). */
    protected static double HEATING_ELEMENT_TEMP = 100.0;

    /** Initial water temperature (in Celsius). */
    protected static double INITIAL_WATER_TEMPERATURE = 15.0;

    /** Heat transfer constant when heating (lower = faster heating). */
    protected static double HEATING_TRANSFER_CONSTANT = 30.0;

    /** Heat transfer constant when cooling (higher = slower cooling). */
    protected static double COOLING_TRANSFER_CONSTANT = 100.0;

    /** Minimum power level to consider heating is effective. */
    protected static double POWER_HEAT_TRANSFER_TOLERANCE = 0.0001;

    /** Temperature update tolerance to avoid computation errors. */
    protected static double TEMPERATURE_UPDATE_TOLERANCE = 0.0001;

    /** Integration step for the differential equation (in hours). */
    protected static double STEP = 300.0 / 3600.0; // 60 seconds

    // --- State variables ---

    /** Current state of the washing machine. */
    protected WashingMachineState currentState = WashingMachineState.OFF;

    /** Target temperature for the current wash cycle. */
    protected double targetTemperature = COLD_WATER_TEMPERATURE;
    protected long washingDuration;

    /** State before suspension (for resume). */
    protected WashingMachineState stateBeforeSuspension = null;

    // --- Simulation run variables ---

    /** Integration step as a duration, including the time unit. */
    protected final Duration integrationStep;

    /** Accumulator to compute the mean temperature for the simulation report. */
    protected double temperatureAcc;

    /** The simulation time of start used to compute the mean temperature. */
    protected Time start;

    /** The mean temperature over the simulation duration for the report. */
    protected double meanTemperature;
    protected boolean heateingFinished = false;

    // -------------------------------------------------------------------------
    // HIOA model variables
    // -------------------------------------------------------------------------

    /** The current heating power from the electricity model. */
    @ImportedVariable(type = Double.class)
    protected Value<Double> currentHeatingPower;

    /** Current water temperature in the drum (in Celsius). */
    @InternalVariable(type = Double.class)
    protected final DerivableValue<Double> currentWaterTemperature =
                                                new DerivableValue<Double>(this);

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a WashingMachineTemperatureModel instance.
     *
     * @param uri               URI of the model.
     * @param simulatedTimeUnit time unit used for the simulation time.
     * @param simulationEngine  simulation engine to which the model is attached.
     * @throws Exception        if an error occurs.
     */
    public WashingMachineTemperatureModel(
        String uri,
        TimeUnit simulatedTimeUnit,
        AtomicSimulatorI simulationEngine
    ) throws Exception
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.integrationStep = new Duration(STEP, simulatedTimeUnit);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineOperationI#getState()
     */
    @Override
    public WashingMachineState getState() {
        return this.currentState;
    }

    /**
     * Set the current state of the washing machine.
     *
     * @param s the new state.
     */
    public void setState(WashingMachineState s) {
        this.currentState = s;
    }

    @Override
    public void switchOn() {
        if (this.currentState == WashingMachineState.OFF) {
            this.currentState = WashingMachineState.ON;
        }
    }

    @Override
    public void switchOff() {
        this.currentState = WashingMachineState.OFF;
        this.targetTemperature = COLD_WATER_TEMPERATURE;
    }

    @Override
    public void startWashing(long duration, double targetTemp) {
        if (this.currentState == WashingMachineState.ON ||
            this.currentState == WashingMachineState.HEATINGWATER) {
            this.targetTemperature = targetTemp;

            // Check if we need to heat the water first
            double currentTemp = this.currentWaterTemperature.getValue();
            if (currentTemp < targetTemp - 0.5) {
                // Need to heat water first
                this.currentState = WashingMachineState.HEATINGWATER;
                if (VERBOSE) {
                    this.logMessage("Starting to heat water from " + currentTemp +
                                   "°C to " + targetTemp + "°C");
                }
            } else {
                // Temperature already reached, start washing directly
                this.currentState = WashingMachineState.WASHING;
                if (VERBOSE) {
                    this.logMessage("Temperature OK, starting washing at " + currentTemp + "°C");
                }
            }
        }
    }

    @Override
    public void setDelayedStart(long delay, long washingDuration, double targetTemp) {
        // For the temperature model, delayed start doesn't change state immediately
        // The actual start will be triggered later by a StartWashing event
        this.targetTemperature = targetTemp;
        this.washingDuration = washingDuration;
    }

    @Override
    public void suspendWashing() {
        if (this.currentState == WashingMachineState.WASHING ||
            this.currentState == WashingMachineState.HEATINGWATER) {
            this.stateBeforeSuspension = this.currentState;
            this.currentState = WashingMachineState.ON;
            if (VERBOSE) {
                this.logMessage("Cycle suspended, was in state: " + this.stateBeforeSuspension);
            }
        }
    }

    @Override
    public void resumeWashing() {
        if (this.currentState == WashingMachineState.ON &&
            this.stateBeforeSuspension != null) {
            this.currentState = this.stateBeforeSuspension;
            this.stateBeforeSuspension = null;
            if (VERBOSE) {
                this.logMessage("Cycle resumed, now in state: " + this.currentState);
            }
        }
    }

    @Override
    public void setCurrentPowerLevel(double p) {
        // Not used in temperature model
    }

    /**
     * Compute the current heat transfer constant based on heating power.
     *
     * @return the current heat transfer constant.
     */
    protected double currentHeatTransferConstant() {
        if (this.currentHeatingPower == null ||
            !this.currentHeatingPower.isInitialised() ||
            this.currentHeatingPower.getValue() < POWER_HEAT_TRANSFER_TOLERANCE) {
            return HEATING_TRANSFER_CONSTANT * 10; // Very slow heating if no power
        }
        // Higher power = faster heating (lower constant)
        double powerRatio = this.currentHeatingPower.getValue() / 2000.0; // Assuming 2000W max
        return HEATING_TRANSFER_CONSTANT / Math.max(0.1, powerRatio);
    }

    /**
     * Compute the current derivative of the water temperature.
     *
     * @param currentTemp current temperature of the water.
     * @return the current derivative.
     */
    protected double computeDerivatives(Double currentTemp) {
        double derivative = 0.0;

        if (this.currentState == WashingMachineState.HEATINGWATER) {
            // Heating: temperature rises towards heating element temperature
            // but we limit it to target temperature
            double effectiveTarget = Math.min(HEATING_ELEMENT_TEMP, this.targetTemperature + 5.0);
            derivative = (effectiveTarget - currentTemp) / this.currentHeatTransferConstant();

            // Check if target temperature is reached
            if (currentTemp >= this.targetTemperature - 0.5) {
                // Target reached, transition to WASHING will happen in internal transition
                if (VERBOSE && currentTemp >= this.targetTemperature) {
                    this.logMessage("Target temperature " + this.targetTemperature +
                                   "°C reached! Current: " + currentTemp + "°C");
                }
            }
        } else if (this.currentState == WashingMachineState.WASHING) {
            // During washing, try to maintain target temperature
            // Slight cooling towards cold water temperature
            derivative = (COLD_WATER_TEMPERATURE - currentTemp) / COOLING_TRANSFER_CONSTANT;
        } else {
            // OFF or ON (idle): cool down towards cold water temperature
            derivative = (COLD_WATER_TEMPERATURE - currentTemp) / COOLING_TRANSFER_CONSTANT;
        }

        return derivative;
    }

    /**
     * Compute the new temperature after a time step.
     *
     * @param deltaT the duration of the step since the last update.
     * @return the new temperature in Celsius.
     */
    protected double computeNewTemperature(double deltaT) {
        Time t = this.currentWaterTemperature.getTime();
        double oldTemp = this.currentWaterTemperature.evaluateAt(t);
        double newTemp;

        if (deltaT > TEMPERATURE_UPDATE_TOLERANCE) {
            // Euler integration of the differential equation
            double derivative = this.currentWaterTemperature.getFirstDerivative();
            newTemp = oldTemp + derivative * deltaT;

            // Clamp temperature to reasonable bounds
            newTemp = Math.max(COLD_WATER_TEMPERATURE - 5.0,
                              Math.min(95.0, newTemp));
        } else {
            newTemp = oldTemp;
        }

        // Accumulate for mean temperature calculation
        this.temperatureAcc += ((oldTemp + newTemp) / 2.0) * deltaT;
        return newTemp;
    }

    // -------------------------------------------------------------------------
    // DEVS simulation protocol
    // -------------------------------------------------------------------------

    @Override
    public void initialiseState(Time initialTime) {
        this.temperatureAcc = 0.0;
        this.start = initialTime;
        this.currentState = WashingMachineState.OFF;
        this.targetTemperature = COLD_WATER_TEMPERATURE;
        this.stateBeforeSuspension = null;
        this.heateingFinished = false;

        if (VERBOSE) {
            this.logMessage("simulation begins.");
        }

        super.initialiseState(initialTime);
    }

    @Override
    public boolean useFixpointInitialiseVariables() {
        return true;
    }

    @Override
    public Pair<Integer, Integer> fixpointInitialiseVariables() {
        int justInitialised = 0;
        int notInitialisedYet = 0;

        if (!this.currentWaterTemperature.isInitialised()) {
            // Initialize water temperature to cold water temperature
            double derivative = this.computeDerivatives(INITIAL_WATER_TEMPERATURE);
            this.currentWaterTemperature.initialise(INITIAL_WATER_TEMPERATURE, derivative);
            justInitialised++;
        }

        return new Pair<>(justInitialised, notInitialisedYet);
    }

    @Override
    public ArrayList<EventI> output() {

    	if (this.currentState == WashingMachineState.HEATINGWATER &&
            heateingFinished) {
            ArrayList<EventI> ret = new ArrayList<>();
            this.currentState = WashingMachineState.WASHING;
            this.heateingFinished = false;  // Reset du flag pour éviter de réémettre
            Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            ret.add(new HeatingFinished(t));
            if(VERBOSE) logMessage("Target temperature reached! Emitting HeatingFinished.\n");
            return ret;
        }
        return null;
    }

    @Override
    public Duration timeAdvance() {
        return this.integrationStep;
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        // Update the temperature
        double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());

        // Check if we should transition from HEATINGWATER to WASHING
        if (this.currentState == WashingMachineState.HEATINGWATER &&
            newTemp >= this.targetTemperature - 0.5) {
            this.heateingFinished = true;
            if (VERBOSE) {
                this.logMessage("Water heated to " + newTemp +
                               "°C, target was " + this.targetTemperature +
                               "°C. Transitioning to WASHING.");
            }
        }

        // Compute new derivative
        double newDerivative = this.computeDerivatives(newTemp);

        // Update the temperature value
        this.currentWaterTemperature.setNewValue(
            newTemp,
            newDerivative,
            new Time(this.getCurrentStateTime().getSimulatedTime(),
                     this.getSimulatedTimeUnit())
        );

        // Tracing
        if (VERBOSE) {
            String mark;
            switch (this.currentState) {
                case HEATINGWATER: mark = " (heating)"; break;
                case WASHING: mark = " (washing)"; break;
                case ON: mark = " (idle)"; break;
                default: mark = " (off)"; break;
            }
            StringBuffer message = new StringBuffer();
            message.append(this.currentWaterTemperature.getTime().getSimulatedTime());
            message.append(mark);
            message.append(" : ");
            message.append(String.format("%.2f", this.currentWaterTemperature.getValue()));
            message.append("°C");
            if (this.currentState == WashingMachineState.HEATINGWATER) {
                message.append(" -> target: ");
                message.append(this.targetTemperature);
                message.append("°C");
            }
            this.logMessage(message.toString());
        }

        super.userDefinedInternalTransition(elapsedTime);
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {
        // Get current external events
        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof WashingMachineEventI;

        if (VERBOSE) {
            StringBuffer sb = new StringBuffer("executing the external event: ");
            sb.append(ce.eventAsString());
            sb.append(".");
            this.logMessage(sb.toString());
        }
        
        double newTemp = this.computeNewTemperature(elapsedTime.getSimulatedDuration());

        for (EventI event : currentEvents) {
            event.executeOn(this);

            if (event instanceof WashingEnded) {
                this.currentState = WashingMachineState.ON;
                if (VERBOSE) {
                    this.logMessage("Received WashingEnded. State forced to ON (Idle).");
                }
            }
        }

        // Compute new derivative based on new state
        double newDerivative = this.computeDerivatives(newTemp);

        // Update the temperature value
        this.currentWaterTemperature.setNewValue(
            newTemp,
            newDerivative,
            new Time(this.getCurrentStateTime().getSimulatedTime()
                     + elapsedTime.getSimulatedDuration(),
                     this.getSimulatedTimeUnit())
        );

        super.userDefinedExternalTransition(elapsedTime);
    }

    @Override
    public void endSimulation(Time endTime) {
        this.meanTemperature = this.temperatureAcc /
                endTime.subtract(this.start).getSimulatedDuration();

        if (VERBOSE) {
            this.logMessage("simulation ends. Mean water temperature: " +
                           String.format("%.2f", this.meanTemperature) + "°C");
        }
        super.endSimulation(endTime);
    }

    // -------------------------------------------------------------------------
    // Optional DEVS simulation protocol: simulation report
    // -------------------------------------------------------------------------

    /**
     * The simulation report for the WashingMachineTemperatureModel.
     */
    public static class WashingMachineTemperatureReport
    implements SimulationReportI, GlobalReportI
    {
        private static final long serialVersionUID = 1L;
        protected String modelURI;
        protected double meanTemperature;

        public WashingMachineTemperatureReport(String modelURI, double meanTemperature) {
            super();
            this.modelURI = modelURI;
            this.meanTemperature = meanTemperature;
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
            ret.append("mean water temperature = ");
            ret.append(String.format("%.2f", this.meanTemperature));
            ret.append("°C.\n");
            ret.append(indent);
            ret.append("---\n");
            return ret.toString();
        }
    }

    @Override
    public SimulationReportI getFinalReport() {
        return new WashingMachineTemperatureReport(this.getURI(), this.meanTemperature);
    }
}
