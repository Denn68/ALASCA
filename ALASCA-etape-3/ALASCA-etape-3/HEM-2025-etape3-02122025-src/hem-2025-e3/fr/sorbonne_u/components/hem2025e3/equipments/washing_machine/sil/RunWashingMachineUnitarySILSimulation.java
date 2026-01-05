package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.StartWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI; // Important pour storeInput
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunWashingMachineUnitarySILSimulation</code> tests the SIL
 * simulation architecture for the washing machine in standalone mode.
 *
 * <p><strong>Description</strong></p>
 * This runner creates the architecture, initializes the simulator using
 * initialiseSimulation (not initSimulation), and runs a scenario.
 *
 * @author	Team DeMoh
 */
public class			RunWashingMachineUnitarySILSimulation
{
	public static void	main(String[] args)
	{
		try {
			// 1. Paramètres de simulation
			TimeUnit tu = TimeUnit.HOURS;
			double accFactor = 1.0; 
			double simDuration = 2.0;

			// 2. Création de l'architecture
			Architecture architecture =
				Local_SIL_SimulationArchitectures.createWashingMachineSILArchitecture(
					Local_SIL_SimulationArchitectures.WM_SIL_URI,
					Local_SIL_SimulationArchitectures.WM_SIL_URI,
					tu,
					accFactor);

			// 3. Construction du simulateur
			SimulatorI se = architecture.constructSimulator();
			
			// Configuration des paramètres
			Map<String, Object> simParams = new HashMap<>();
			se.setSimulationRunParameters(simParams);
			
			// --- CORRECTION : Utilisation de initialiseSimulation ---
			// On initialise à t=0.0 pour une durée de simDuration
			se.initialiseSimulation(
					new Time(0.0, tu), 
					new Duration(simDuration, tu));

			// 4. Injection du scénario
			// Pour injecter des événements manuellement, on doit utiliser l'interface atomique
			if (!(se instanceof AtomicSimulatorI)) {
				throw new RuntimeException("Le simulateur construit n'est pas atomique, impossible d'injecter des événements directement.");
			}
			AtomicSimulatorI ase = (AtomicSimulatorI) se;

			// --- Étape 1 : Switch ON à t=0.1 ---
			ArrayList<EventI> events1 = new ArrayList<>();
			events1.add(new SwitchOnWashingMachine(new Time(0.1, tu)));
			// On stocke l'input pour le modèle
			ase.storeInput(Local_SIL_SimulationArchitectures.WM_SIL_URI, events1);
			// On avance le temps de 0.1 (de 0.0 à 0.1) pour traiter l'événement
			se.externalEventStep(new Duration(0.1, tu));
			
			// --- Étape 2 : Start Washing à t=0.2 ---
			ArrayList<EventI> events2 = new ArrayList<>();
			events2.add(new StartWashing(new Time(0.2, tu)));
			ase.storeInput(Local_SIL_SimulationArchitectures.WM_SIL_URI, events2);
			// On avance le temps de 0.1 (de 0.1 à 0.2)
			se.externalEventStep(new Duration(0.1, tu));
			
			// --- Étape 3 : Laisser tourner le cycle (1.5h) ---
			// Pas d'événements externes, le modèle évolue seul (transitions internes)
			// Mais pour le simulateur standalone, on fait un saut ou on attend la fin.
			// Ici on va directement à l'extinction.
			
			// --- Étape 4 : Switch OFF à t=1.7 ---
			ArrayList<EventI> events3 = new ArrayList<>();
			events3.add(new SwitchOffWashingMachine(new Time(1.7, tu)));
			ase.storeInput(Local_SIL_SimulationArchitectures.WM_SIL_URI, events3);
			// On avance le temps de 1.5 (de 0.2 à 1.7)
			se.externalEventStep(new Duration(1.5, tu));

			// 5. Fin de simulation
			se.endSimulation(new Time(simDuration, tu));
			
			// Affichage du rapport
			System.out.println(se.getFinalReport().printout("-"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}