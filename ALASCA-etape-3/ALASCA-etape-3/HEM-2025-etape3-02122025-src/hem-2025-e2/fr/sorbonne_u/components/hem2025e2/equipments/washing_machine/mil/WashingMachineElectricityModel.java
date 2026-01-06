package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e2.GlobalReportI;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.annotations.ModelExportedVariable;
import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
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
    SetPowerWashingMachine.class
})
@ModelExportedVariable(name = "currentIntensity", type = Double.class)
@ModelExportedVariable(name = "currentHeatingPower", type = Double.class)
public class WashingMachineElectricityModel 
extends AtomicHIOA 
implements WashingMachineOperationI 
{
    private static final long serialVersionUID = 1L;
    public static final String URI = WashingMachineElectricityModel.class.getSimpleName();
    public static boolean VERBOSE = true;
    public static boolean DEBUG = false;

    // --- Paramètres ---
    protected static final double TENSION = 220.0;
    protected static final double HEATING_POWER = 2000.0; // Watts
    protected static final double WASHING_POWER = 400.0;  // Watts

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
    protected long programmedDuration;        // Durée du cycle prévu après le délai
    protected double programmedTemperature;   // Température prévue après le délai

    // --- Variables HIOA ---
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentIntensity = new Value<Double>(this);

    /** The current heating power (in Watts), exported to the temperature model. */
    @ExportedVariable(type = Double.class)
    protected final Value<Double> currentHeatingPower = new Value<Double>(this);

    public WashingMachineElectricityModel(
        String uri, 
        TimeUnit simulatedTimeUnit, 
        AtomicSimulatorI simulationEngine) throws Exception 
    {
        super(uri, simulatedTimeUnit, simulationEngine);
        this.getSimulationEngine().setLogger(new StandardLogger());
    }

    // -------------------------------------------------------------------------
    // Opérations (Appelées par les événements)
    // -------------------------------------------------------------------------

    @Override public void switchOn() { 
        if(currentState == WashingMachineState.OFF) { 
            currentState = WashingMachineState.ON; 
            consumptionHasChanged = true; 
        } 
    }
    
    @Override public void switchOff() { 
        currentState = WashingMachineState.OFF; 
        remainingTimeInCurrentPhase = null; 
        timeForNextPhase = null; 
        isDelayRunning = false; // On annule tout départ différé
        consumptionHasChanged = true; 
    }
    
    @Override public void startWashing(long duration, double targetTemperature) {
        if (currentState == WashingMachineState.ON) {
            
            // Si on lance manuellement, on annule tout délai en cours
            if (isDelayRunning) {
                if(VERBOSE) logMessage("Manual start override: cancelling delayed start.\n");
                isDelayRunning = false;
            }

            double total = (double) duration;
            double heat = (targetTemperature > 20.0) ? Math.min(10.0, total) : 0.0;
            double wash = total - heat;

            if (heat > 0) {
                currentState = WashingMachineState.HEATINGWATER;
                remainingTimeInCurrentPhase = new Duration(heat, getSimulatedTimeUnit());
                timeForNextPhase = new Duration(wash, getSimulatedTimeUnit());
            } else {
                currentState = WashingMachineState.WASHING;
                remainingTimeInCurrentPhase = new Duration(wash, getSimulatedTimeUnit());
                timeForNextPhase = null;
            }
            consumptionHasChanged = true;
        }
    }

    @Override
    public void setDelayedStart(long delay, long washingDuration, double targetTemperature) {
        if (currentState == WashingMachineState.ON && !isDelayRunning) {
            if(VERBOSE) logMessage("Delayed Start programmed for " + delay + " min.\n");
            
            this.isDelayRunning = true;
            this.programmedDuration = washingDuration;
            this.programmedTemperature = targetTemperature;
            
            // Le "temps restant" devient le temps d'attente avant démarrage
            this.remainingTimeInCurrentPhase = new Duration((double)delay, getSimulatedTimeUnit());
            
            // Pas de changement de consommation (reste à 0), donc pas de consumptionHasChanged = true nécessaire
            // sauf si on voulait logger un état "WAITING".
        }
    }

    @Override public void suspendWashing() {
        if (currentState == WashingMachineState.WASHING || currentState == WashingMachineState.HEATINGWATER) {
            stateBeforeSuspension = currentState;
            currentState = WashingMachineState.ON;
            consumptionHasChanged = true;
        }
        // Note: on pourrait aussi suspendre le compte à rebours du délai si besoin, 
        // mais pour l'instant on considère que suspend ne s'applique qu'au lavage actif.
    }

    @Override public void resumeWashing() {
        if (currentState == WashingMachineState.ON && stateBeforeSuspension != null) {
            currentState = stateBeforeSuspension;
            stateBeforeSuspension = null;
            consumptionHasChanged = true;
        }
    }

    @Override public void setCurrentPowerLevel(double p) {}
    @Override public WashingMachineState getState() { return currentState; }

    // -------------------------------------------------------------------------
    // DEVS Simulation Protocol
    // -------------------------------------------------------------------------

    @Override
    public void initialiseState(Time initialTime) {
        super.initialiseState(initialTime);
        currentState = WashingMachineState.OFF;
        remainingTimeInCurrentPhase = null;
        timeForNextPhase = null;
        stateBeforeSuspension = null;
        isDelayRunning = false;
        totalConsumption = 0.0;
        consumptionHasChanged = false;
        if(VERBOSE) logMessage("simulation begins.\n");
    }

    @Override
    public boolean useFixpointInitialiseVariables() { return true; }

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

    @Override public ArrayList<EventI> output() { return null; }

    @Override
    public Duration timeAdvance() {
        // 1. Si la consommation a changé, transition immédiate pour mettre à jour la variable
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
        
        // Mise à jour du temps restant (valable pour le lavage ET pour le compte à rebours délai)
        if (remainingTimeInCurrentPhase != null) {
            remainingTimeInCurrentPhase = remainingTimeInCurrentPhase.subtract(elapsedTime);
        }
        
        updateConsumption(elapsedTime);
        
        ArrayList<EventI> events = getStoredEventAndReset();
        for (EventI e : events) e.executeOn(this);
    }

    @Override
    public void userDefinedInternalTransition(Duration elapsedTime) {
        super.userDefinedInternalTransition(elapsedTime);
        
        // Vérification si le temps écoulé est significatif (pas juste une maj de variable)
        boolean timeElapsed = elapsedTime.getSimulatedDuration() > 0.000001;

        if (timeElapsed) {
            // Cas 1 : Fin du délai d'attente (Départ Différé)
            if (isDelayRunning) {
                if(VERBOSE) logMessage("Delay elapsed. Auto-starting washing cycle.\n");
                isDelayRunning = false;
                remainingTimeInCurrentPhase = null; // Reset avant démarrage
                // On lance le lavage comme si on avait reçu l'ordre
                startWashing(programmedDuration, programmedTemperature);
            }
            // Cas 2 : Fin de phase de chauffe
            else if (currentState == WashingMachineState.HEATINGWATER) {
                if(VERBOSE) logMessage("Heating finished. Washing...\n");
                currentState = WashingMachineState.WASHING;
                remainingTimeInCurrentPhase = timeForNextPhase;
                timeForNextPhase = null;
                consumptionHasChanged = true;
            } 
            // Cas 3 : Fin de phase de lavage
            else if (currentState == WashingMachineState.WASHING) {
                if(VERBOSE) logMessage("Washing finished. Idle.\n");
                currentState = WashingMachineState.ON;
                remainingTimeInCurrentPhase = null;
                consumptionHasChanged = true;
            }
        } 
        else if (remainingTimeInCurrentPhase != null) {
             // Si transition immédiate, on ajuste juste un poil le temps restant
             remainingTimeInCurrentPhase = remainingTimeInCurrentPhase.subtract(elapsedTime);
        }

        // --- Mise à jour de l'intensité (Ampères) et puissance de chauffe ---
        Time t = getCurrentStateTime();
        double power = 0.0;
        double heatingPower = 0.0;

        // Si le délai court, on est en "ON" (0W), donc pas besoin de cas spécial ici
        switch (currentState) {
            case HEATINGWATER:
                power = HEATING_POWER;
                heatingPower = HEATING_POWER;  // Exporter la puissance de chauffe
                break;
            case WASHING:
                power = WASHING_POWER;
                heatingPower = 0.0;  // Pas de chauffe pendant le lavage
                break;
            default:
                power = 0.0;
                heatingPower = 0.0;
                break;
        }
        double intensity = power / TENSION;

        currentIntensity.setNewValue(intensity, t);
        currentHeatingPower.setNewValue(heatingPower, t);

        if(VERBOSE) logMessage("new intensity: " + intensity + " Amps (" + power + " W), heating power: " + heatingPower + " W at " + t + "\n");
    }

    protected void updateConsumption(Duration elapsedTime) {
        double durationHours = elapsedTime.getSimulatedDuration();
        if (getSimulatedTimeUnit() == TimeUnit.MINUTES) durationHours /= 60.0;
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
        protected String uri; protected double cons;
        public WashingMachineElectricityReport(String u, double c) { uri=u; cons=c; }
        @Override public String getModelURI() { return uri; }
        @Override public String printout(String indent) { return indent + "WashingMachine Report: " + cons + " kWh.\n"; }
    }
}