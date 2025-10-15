package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlCI;

// -----------------------------------------------------------------------------

public class			FanExternalControlConnector
extends		AbstractConnector
implements	FanExternalControlCI
{

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((FanExternalControlCI)this.offering).getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((FanExternalControlCI)this.offering).
										setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((FanExternalControlCI)this.offering).getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
