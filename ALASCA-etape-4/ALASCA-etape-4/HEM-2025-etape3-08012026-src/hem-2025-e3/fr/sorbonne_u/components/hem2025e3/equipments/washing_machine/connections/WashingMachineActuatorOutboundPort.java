package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineActuatorCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class			WashingMachineActuatorOutboundPort
extends		AbstractOutboundPort
implements	WashingMachineActuatorCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineActuatorOutboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineActuatorCI.class, owner);
	}

	public				WashingMachineActuatorOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineActuatorCI.class, owner);
	}

	@Override
	public void			switchOn() throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).switchOn();
	}

	@Override
	public void			switchOff() throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).switchOff();
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target) throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).setTargetTemperature(target);
	}

	@Override
	public void			startWashing(long washingTimeMS, Measure<Double> target) throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).startWashing(washingTimeMS, target);
	}

	@Override
	public void			delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).delayedStart(delayMS, target, washingTimeMS);
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).suspendCycle();
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		((WashingMachineActuatorCI)this.getConnector()).resumeCycle();
	}
}