package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlCI;

// -----------------------------------------------------------------------------

public class			FanInternalControlConnector
extends		AbstractConnector
implements	FanInternalControlCI
{
	@Override
	public boolean running() throws Exception {
		return ((FanInternalControlCI)this.offering).running();
	}
}
// -----------------------------------------------------------------------------
