package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineActuatorCI;

public class            WashingMachineActuatorConnector
extends        AbstractConnector
implements    WashingMachineActuatorCI
{
    @Override
    public void         switchOn() throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).switchOn();
    }

    @Override
    public void         switchOff() throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).switchOff();
    }

    @Override
    public void         startWashing(long washingTimeMS, Measure<Double> target) throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).startWashing(washingTimeMS, target);
    }

    @Override
    public void         delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).delayedStart(delayMS, target, washingTimeMS);
    }

    @Override
    public void         suspendCycle() throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).suspendCycle();
    }

    @Override
    public void         resumeCycle() throws Exception
    {
        ((WashingMachineActuatorCI)this.offering).resumeCycle();
    }

	@Override
	public void setTargetTemperature(Measure<Double> target) throws Exception {
		((WashingMachineActuatorCI)this.offering).setTargetTemperature(target);
	}
}
