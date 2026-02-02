package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;


public class			KettleInternalControlOutboundPort
extends		AbstractOutboundPort
implements	KettleInternalControlCI
{

	private static final long serialVersionUID = 1L;

	public				KettleInternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(KettleInternalControlCI.class, owner);
	}

	public				KettleInternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, KettleInternalControlCI.class, owner);
	}

	@Override
	public boolean		heating() throws Exception
	{
		return ((KettleInternalControlCI)this.getConnector()).heating();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((KettleInternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((KettleInternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}

	@Override
	public void			startHeating() throws Exception
	{
		((KettleInternalControlCI)this.getConnector()).startHeating();
	}
	@Override
	public void			stopHeating() throws Exception
	{
		((KettleInternalControlCI)this.getConnector()).stopHeating();
	}

	@Override
	public boolean keepingWarm() throws Exception
	{
		return ((KettleInternalControlCI)this.getConnector()).keepingWarm();
	}

	@Override
	public void startKeepingWarm() throws Exception
	{
		((KettleInternalControlCI)this.getConnector()).startKeepingWarm();
	}

	@Override
	public void stopKeepingWarm() throws Exception
	{
		((KettleInternalControlCI)this.getConnector()).stopKeepingWarm();
	}
}
// -----------------------------------------------------------------------------
