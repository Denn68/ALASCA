package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlCI;

public class			KettleExternalControlConnector
extends		AbstractConnector
implements	KettleExternalControlCI
{

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((KettleExternalControlCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((KettleExternalControlCI)this.offering).
										setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((KettleExternalControlCI)this.offering).getCurrentPowerLevel();
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return ((KettleExternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((KettleExternalControlCI)this.offering).getCurrentTemperature();
	}
}
// -----------------------------------------------------------------------------
