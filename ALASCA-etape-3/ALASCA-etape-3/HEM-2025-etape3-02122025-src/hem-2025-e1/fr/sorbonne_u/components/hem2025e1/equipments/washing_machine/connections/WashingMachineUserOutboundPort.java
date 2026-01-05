package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class			WashingMachineUserOutboundPort
extends		AbstractOutboundPort
implements	WashingMachineUserCI
{

	private static final long serialVersionUID = 1L;
	public				WashingMachineUserOutboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineUserCI.class, owner);
	}

	public				WashingMachineUserOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineUserCI.class, owner);
	}

	@Override
	public boolean		on() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).on();
	}
	
	@Override
	public void			switchOn() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).switchOn();
	}

	@Override
	public void			switchOff() throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).switchOff();
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).setTargetTemperature(target);
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((WashingMachineUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getCurrentPowerLevel();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((WashingMachineUserCI)this.getConnector()).getCurrentTemperature();
	}

	@Override
	public void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception {
		((WashingMachineUserCI)this.getConnector()).delayedStart(delayMS, target, washingTimeMS);
	}

	@Override
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		((WashingMachineUserCI)this.getConnector()).startWashing(washingTimeMS, target);
	}
	
	@Override
	public void suspendCycle() throws Exception {
		((WashingMachineUserCI)this.getConnector()).suspendCycle();
	}
	
	@Override
	public void resumeCycle() throws Exception {
		((WashingMachineUserCI)this.getConnector()).resumeCycle();
	}

	@Override
	public boolean isWashing() throws Exception {
		return ((WashingMachineUserCI)this.getConnector()).isWashing();
	}
}
