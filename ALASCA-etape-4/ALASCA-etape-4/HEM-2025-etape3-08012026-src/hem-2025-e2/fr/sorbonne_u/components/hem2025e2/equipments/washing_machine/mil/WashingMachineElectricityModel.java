package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlI;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.components.hem2025e2.utils.Electricity;
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

@ModelExternalEvents(imported = {
        SwitchOnWashingMachine.class,
        SwitchOffWashingMachine.class,
        StartWashing.class,
        SetDelayedStart.class,
        SuspendWashing.class,
        ResumeWashing.class,
        SetPowerWashingMachine.class,
        HeatingFinished.class,
        WashingEnded.class
}, exported = {
        WashingEnded.class,
        StartWashing.class
})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
public class WashingMachineElectricityModel
        extends AtomicHIOA
        implements WashingMachineOperationI {
    private static final long serialVersionUID = 1L;
    public static final String URI = WashingMachineElectricityModel.class.getSimpleName();
    public static boolean VERBOSE = true;
    public static boolean DEBUG = false;

    // --- Paramètres ---
    protected static final double TENSION = 220.0;
    protected static final double HEATING_POWER = 2000.0; // Watts
    protected static final double WASHING_POWER = 400.0; // Watts

    // --- État interne ---
    protected WashingMachineState currentState = WashingMachineState.OFF;
    protected WashingMachineState stateBeforeSuspension = null;
    protected boolean consumptionHasChanged = false;

    // Gestion du temps et des phases
    protected Duration remainingTimeInCurrentPhase; // Sert pour le délai ET pour les cycles
    protected Duration timeForNextPhase;
    protected double totalConsumption; // kWh

    // --- Variables pour le départ différé ---
    protected boolean isDelayRunning = false; // Est-on en train d'attendre un départ ?
    protected boolean delayedStart = false;
    protected boolean washingEnded = false; // Flag pour émettre WashingEnded
    protected long programmedDuration; // Durée du cycle prévu après le délai
    protected double programmedTemperature; // Température prévue après le délai

    // --- Variables HIOA ---
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity = new Value<Double>(this);

    /** The current heating power (in Watts), exported to the temperature model. */
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentHeatingPower = new Value<Double>(this);

    public WashingMachineElectricityModel(
            String uri,
            TimeUnit simulatedTimeUnit,
            AtomicSimulatorI simulationEngine) throws Exception {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Opérations (Appelées par les événements)
    // -------------------------------------------------------------------------

    @Override
    public void switchOn() {

        if (this.currentState == WashingMachineState.OFF) {
            this.currentState = WashingMachineState.ON;
            consumptionHasChanged = true;
        }
    }

    @Override
    public void switchOff() {
        this.currentState = WashingMachineState.OFF;
        remainingTimeInCurrentPhase = null;
        timeForNextPhase = null;
        isDelayRunning = false; // On annule tout départ différé
        consumptionHasChanged = true;
    }

    @Override
    public void startWashing(long duration, double targetTemperature) {
        if (this.currentState == WashingMachineState.ON) {

            // Si on lance manuellement, on annule tout délai en cours
            if (isDelayRunning) {
                if (VERBOSE)
                    logMessage("Manual start override: cancelling delayed start.\n");
                isDelayRunning = false;
            }

            // On passe en chauffe (même si c'est froid, le modèle Température
            // verra que TempActuelle >= Cible et renverra HeatingFinished tout de suite)
            this.currentState = WashingMachineState.HEATINGWATER;

            // On attend indéfiniment la réponse de la physique
            this.remainingTimeInCurrentPhase = Duration.INFINITY;

            // On garde en mémoire la durée du lavage Moteur pour plus tard
            this.timeForNextPhase = new Duration((double) duration, getSimulatedTimeUnit());

            this.consumptionHasChanged = true;
        }
    }

    @Override
    public void setDelayedStart(long delay, long washingDuration, double targetTemperature) {
        if (this.currentState == WashingMachineState.ON && !isDelayRunning) {
            if (VERBOSE)
                logMessage("Delayed Start programmed for " + delay + " min.\n");

            this.isDelayRunning = true;
            this.programmedDuration = washingDuration;
            this.programmedTemperature = targetTemperature;

            // On attend la fin du délai
            this.remainingTimeInCurrentPhase = new Duration((double) delay, getSimulatedTimeUnit());
        }
    }

    @Override
    public void suspendWashing() {
        if (this.currentState == WashingMachineState.WASHING || this.currentState == WashingMachineState.HEATINGWATER) {
            stateBeforeSuspension = this.currentState;
            this.currentState = WashingMachineState.ON;
            consumptionHasChanged = true;
        }
        // Note: on pourrait aussi suspendre le compte à rebours du délai si besoin,
        // mais pour l'instant on considère que suspend ne s'applique qu'au lavage
        // actif.
    }

    @Override
    public void resumeWashing() {
        if (this.currentState == WashingMachineState.ON && stateBeforeSuspension != null) {
            this.currentState = stateBeforeSuspension;
            stateBeforeSuspension = null;
            consumptionHasChanged = true;
        }
    }

    @Override
    public void setCurrentPowerLevel(double p) {
    }

    @Override
    public void heatingFinished() {
        if (this.currentState == WashingMachineState.HEATINGWATER) {
            this.currentState = WashingMachineState.WASHING;
            this.consumptionHasChanged = true;
            if (VERBOSE) {
                this.logMessage("HeatingFinished - switching to WASHING state");
            }
        }
    }

    @Override
    public WashingMachineState getState() {
        return this.currentState;
    }

    // -------------------------------------------------------------------------
    // DEVS Simulation Protocol
    // -------------------------------------------------------------------------

    @Override
    public void initialiseState(Time initialTime) {
        super.initialiseState(initialTime);
        this.currentState = WashingMachineState.OFF;
        remainingTimeInCurrentPhase = null;
        timeForNextPhase = null;
        stateBeforeSuspension = null;
        isDelayRunning = false;
        delayedStart = false;
        washingEnded = false;
        totalConsumption = 0.0;
        consumptionHasChanged = false;
        if (VERBOSE)
            logMessage("simulation begins.\n");
    }

    @Override
    public boolean useFixpointInitialiseVariables() {
        return true;
    }

    @Override
    public Pair<Integer, Integer> fixpointInitialiseVariables() {
        int initialised = 0;
        if (!currentIntensity.isInitialised()) {
            currentIntensity.initialise(0.0);
            initialised++;
        }
        if (!currentHeatingPower.isInitialised()) {
            currentHeatingPower.initialise(0.0);
            initialised++;
        }
        return new Pair<>(initialised, 0);
    }

    @Override
    public ArrayList<EventI> output() {

        // Cas 1 : Fin du délai -> On envoie StartWashing
        if (delayedStart) {
            ArrayList<EventI> ret = new ArrayList<>();
            Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());

            // On envoie l'événement avec les paramètres programmés
            ret.add(new StartWashing(t, programmedDuration, programmedTemperature));

            if (VERBOSE)
                logMessage("Delay elapsed. Emitting StartWashing event.\n");
            delayedStart = false;
            return ret;
        }

        // Cas 2 : Fin du lavage -> On envoie WashingEnded
        if (washingEnded) {
            ArrayList<EventI> ret = new ArrayList<>();
            Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
            ret.add(new WashingEnded(t));
            if (VERBOSE)
                logMessage("Emitting WashingEnded event.\n");
            washingEnded = false;
            return ret;
        }
        return null;
    }

    @Override
    public Duration timeAdvance() {
        // 1. Si la consommation a changé, transition immédiate pour mettre à jour la
        // variable
        if (consumptionHasChanged) {
            consumptionHasChanged = false;
            return Duration.zero(getSimulatedTimeUnit());
        }

        // 2. Si on attend (soit délai avant départ, soit fin de phase lavage/chauffe)
        if (remainingTimeInCurrentPhase != null) {
            return remainingTimeInCurrentPhase;
        }

        return Duration.INFINITY;
    }

    @Override
    public void userDefinedExternalTransition(Duration elapsedTime) {

        super.userDefinedExternalTransition(elapsedTime);

        ArrayList<EventI> currentEvents = this.getStoredEventAndReset();
        assert currentEvents != null && currentEvents.size() == 1;

        Event ce = (Event) currentEvents.get(0);
        assert ce instanceof WashingMachineEventI;

        this.totalConsumption += Electricity.computeConsumption(
                elapsedTime,
                WashingMachineExternalControlI.VOLTAGE.getData() * this.currentIntensity.getValue());

        if (VERBOSE) {
            StringBuffer sb = new StringBuffer("execute the external event: ");
            sb.append(ce.eventAsString());
            sb.append(".");
            this.logMessage(sb.toString());
        }

        for (EventI event : currentEvents) {
            event.executeOn(this);

            // --- Réception du signal de fin de chauffe ---
            if (event instanceof HeatingFinished) {
                if (this.currentState == WashingMachineState.HEATINGWATER) {
                    if (VERBOSE)
                        logMessage("HeatingFinished received. Switching to WASHING.\n");

                    this.currentState = WashingMachineState.WASHING;

                    // Maintenant on active le timer pour la durée du lavage (30 min par ex)
                    this.remainingTimeInCurrentPhase = this.timeForNextPhase;
                    this.timeForNextPhase = null;

                    this.consumptionHasChanged = true;
                }
            }

            // --- Réception du signal de fin de lavage (depuis TemperatureModel) ---
            if (event instanceof WashingEnded) {
                if (this.currentState == WashingMachineState.WASHING ||
                        this.currentState == WashingMachineState.HEATINGWATER) {
                    if (VERBOSE)
                        logMessage("WashingEnded received. Switching to ON (idle).\n");

                    this.currentState = WashingMachineState.ON;
                    this.remainingTimeInCurrentPhase = null;
                    this.timeForNextPhase = null;
                    this.consumptionHasChanged = true;
                }
            }
        }

        assert WashingMachineElectricityModel.implementationInvariants(this);
        assert WashingMachineElectricityModel.invariants(this);
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);

        System.out.println("remainingTimeInCurrentPhase : " + remainingTimeInCurrentPhase);

        // Vérification si le temps écoulé est significatif (pas juste une maj de
        // variable)
        boolean timeElapsed = elapsedTime.getSimulatedDuration() > 0.000001;

        if (timeElapsed) {
            // Cas 1 : Fin du délai d'attente
            if (isDelayRunning) {
                if (VERBOSE)
                    logMessage("Delay processed internally. Starting logic.\n");
                isDelayRunning = false;
                remainingTimeInCurrentPhase = null;
                delayedStart = true;

                // IMPORTANT : On démarre aussi la logique locale pour se mettre en état HEATING
                // (Ceci permet de synchroniser l'état local même si l'événement StartWashing
                // part vers le TemperatureModel)
                startWashing(programmedDuration, programmedTemperature);
            }
            // Cas 2 : Fin de phase de lavage (Le Heating est géré par ExternalTransition
            // via HeatingFinished)
            else if (this.currentState == WashingMachineState.WASHING) {
                if (VERBOSE)
                    logMessage("Washing finished. Idle.\n");
                this.currentState = WashingMachineState.ON;
                remainingTimeInCurrentPhase = null;
                consumptionHasChanged = true;
                washingEnded = true; // Flag pour émettre WashingEnded dans output()
            }
        } else if (remainingTimeInCurrentPhase != null) {
            // Si transition immédiate, on ajuste juste un poil le temps restant
            remainingTimeInCurrentPhase = remainingTimeInCurrentPhase.subtract(elapsedTime);
        }

        // --- Mise à jour de l'intensité (Ampères) et puissance de chauffe ---
        Time t = getCurrentStateTime();
        double power = 0.0;
        double heatingPower = 0.0;

        // Si le délai court, on est en "ON" (0W), donc pas besoin de cas spécial ici
        switch (this.currentState) {
            case HEATINGWATER:
                power = HEATING_POWER;
                heatingPower = HEATING_POWER; // Exporter la puissance de chauffe
                break;
            case WASHING:
                power = WASHING_POWER;
                heatingPower = 0.0; // Pas de chauffe pendant le lavage
                break;
            default:
                power = 0.0;
                heatingPower = 0.0;
                break;
        }
        double intensity = power / TENSION;

        currentIntensity.setNewValue(intensity, t);
        currentHeatingPower.setNewValue(heatingPower, t);

        if (VERBOSE)
            logMessage("new intensity: " + intensity + " Amps (" + power + " W), heating power: " + heatingPower
                    + " W at " + t + "\n");
    }

    protected void updateConsumption(Duration elapsedTime) {
        double durationHours = elapsedTime.getSimulatedDuration();
        if (getSimulatedTimeUnit() == TimeUnit.MINUTES)
            durationHours /= 60.0;
        double powerKW = (currentIntensity.getValue() * TENSION) / 1000.0;
        totalConsumption += powerKW * durationHours;
    }

    @Override
    public void endSimulation(Time endTime) {
        Duration d = endTime.subtract(getCurrentStateTime());
        updateConsumption(d);
        super.endSimulation(endTime);
    }

    @Override
    public SimulationReportI getFinalReport() {
        return new WashingMachineElectricityReport(getURI(), totalConsumption);
    }

    public static class WashingMachineElectricityReport implements SimulationReportI, GlobalReportI {
        private static final long serialVersionUID = 1L;
        protected String uri;
        protected double cons;

        public WashingMachineElectricityReport(String u, double c) {
            uri = u;
            cons = c;
        }

        @Override
        public String getModelURI() {
            return uri;
        }

        @Override
        public String printout(String indent) {
            return indent + "WashingMachine Report: " + cons + " kWh.\n";
        }
    }
}