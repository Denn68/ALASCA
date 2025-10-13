package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;


import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public class			KettleInternalControlConnector
extends		AbstractConnector
implements	KettleInternalControlCI
{
	
	@Override
	public boolean		heating() throws Exception
	{
		return ((KettleInternalControlCI)this.offering).heating();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((KettleInternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((KettleInternalControlCI)this.offering).getCurrentTemperature();
	}

	@Override
	public void			startHeating() throws Exception
	{
		((KettleInternalControlCI)this.offering).startHeating();
	}
	
	@Override
	public void			stopHeating() throws Exception
	{
		((KettleInternalControlCI)this.offering).stopHeating();
	}
	
	@Override
	public boolean		keepingWarm() throws Exception
	{
		return ((KettleInternalControlCI)this.offering).keepingWarm();
	}

	@Override
	public void			startKeepingWarm() throws Exception
	{
		((KettleInternalControlCI)this.offering).startKeepingWarm();
	}
	
	@Override
	public void			stopKeepingWarm() throws Exception
	{
		((KettleInternalControlCI)this.offering).stopKeepingWarm();
	}
}
// -----------------------------------------------------------------------------
