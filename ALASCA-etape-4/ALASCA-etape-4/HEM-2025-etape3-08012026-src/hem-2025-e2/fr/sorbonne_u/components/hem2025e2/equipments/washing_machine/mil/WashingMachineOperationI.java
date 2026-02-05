package fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;

public interface WashingMachineOperationI {
    public void switchOn();

    public void switchOff();

    public void startWashing(long duration, double targetTemperature);

    public void setDelayedStart(long delay, long washingDuration, double targetTemperature);

    public void suspendWashing();

    public void resumeWashing();

    public void setCurrentPowerLevel(double power);

    public WashingMachineState getState();

    /**
     * Called when heating is finished and washing can begin.
     * Transitions state from HEATINGWATER to WASHING.
     */
    public void heatingFinished();
}