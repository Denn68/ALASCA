package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		FanInternalControlCI
extends		OfferedCI,
			RequiredCI,
			FanInternalControlI
{

	@Override
	public boolean		running() throws Exception;
}
// -----------------------------------------------------------------------------
