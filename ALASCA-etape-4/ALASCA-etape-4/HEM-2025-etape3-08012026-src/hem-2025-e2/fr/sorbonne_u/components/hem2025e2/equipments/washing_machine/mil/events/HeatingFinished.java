package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class HeatingFinished
extends		ES_Event
implements	WashingMachineEventI
{
    private static final long serialVersionUID = 1L;

    public HeatingFinished(Time timeOfOccurrence) {
        super(timeOfOccurrence, null);
    }

    @Override
    public boolean hasPriorityOver(EventI e) {
        return false;
    }

    @Override
    public void executeOn(AtomicModelI model) {
        // Sera implémenté dans le modèle de réception (ElectricityModel)
    }
}