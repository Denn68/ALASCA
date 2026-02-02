package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		FanExternalControlCI
extends		RequiredCI,
			OfferedCI,
			FanExternalControlI
{

	public Measure<Double>	getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;

}
// -----------------------------------------------------------------------------
