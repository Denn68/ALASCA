package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public interface		FanInternalControlCI
extends		OfferedCI,
			RequiredCI,
			FanInternalControlI
{

	@Override
	public boolean		running() throws Exception;

	//@Override
	//public void			startHeating() throws Exception;
	
	//@Override
	//public void			stopHeating() throws Exception;
}
// -----------------------------------------------------------------------------
