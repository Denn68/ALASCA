package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class			WashingMachineInternalControlOutboundPort
extends		AbstractOutboundPort
implements	WashingMachineInternalControlCI
{

	private static final long serialVersionUID = 1L;
	
	public				WashingMachineInternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineInternalControlCI.class, owner);
	}
	
	public				WashingMachineInternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, WashingMachineInternalControlCI.class, owner);
	}
	
	@Override
	public boolean		heatWater() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.getConnector()).heatWater();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}

	@Override
	public void			startHeatingWater() throws Exception
	{
		((WashingMachineInternalControlCI)this.getConnector()).startHeatingWater();
	}

	@Override
	public void			stopHeatingWater() throws Exception
	{
		((WashingMachineInternalControlCI)this.getConnector()).stopHeatingWater();
	}
}
