package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class			KettleExternalControlOutboundPort
extends		AbstractOutboundPort
implements	KettleExternalControlCI
{

	private static final long serialVersionUID = 1L;

	public				KettleExternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(KettleExternalControlCI.class, owner);
	}

	public				KettleExternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, KettleExternalControlCI.class, owner);
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((KettleExternalControlCI)this.getConnector()).
													getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((KettleExternalControlCI)this.getConnector()).
											setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((KettleExternalControlCI)this.getConnector()).
													getCurrentPowerLevel();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((KettleExternalControlCI)this.getConnector()).
													getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((KettleExternalControlCI)this.getConnector()).
													getCurrentTemperature();
	}
}
// -----------------------------------------------------------------------------
