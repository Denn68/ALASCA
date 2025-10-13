package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


public class			KettleUserOutboundPort
extends		AbstractOutboundPort
implements	KettleUserCI
{

	private static final long serialVersionUID = 1L;

	public				KettleUserOutboundPort(ComponentI owner)
	throws Exception
	{
		super(KettleUserCI.class, owner);
	}

	public				KettleUserOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, KettleUserCI.class, owner);
	}

	@Override
	public boolean		on() throws Exception
	{
		return ((KettleUserCI)this.getConnector()).on();
	}

	@Override
	public void			switchOn() throws Exception
	{
		((KettleUserCI)this.getConnector()).switchOn();
	}

	@Override
	public void			switchOff() throws Exception
	{
		((KettleUserCI)this.getConnector()).switchOff();
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		((KettleUserCI)this.getConnector()).setTargetTemperature(target);
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((KettleUserCI)this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((KettleUserCI)this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((KettleUserCI)this.getConnector()).getCurrentPowerLevel();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((KettleUserCI)this.getConnector()).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((KettleUserCI)this.getConnector()).getCurrentTemperature();
	}
}
// -----------------------------------------------------------------------------
