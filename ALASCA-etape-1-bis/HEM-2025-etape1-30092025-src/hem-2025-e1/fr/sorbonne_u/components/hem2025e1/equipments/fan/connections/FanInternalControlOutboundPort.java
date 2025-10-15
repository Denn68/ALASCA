package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------

public class			FanInternalControlOutboundPort
extends		AbstractOutboundPort
implements	FanInternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanInternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(FanInternalControlCI.class, owner);
	}

	public				FanInternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FanInternalControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		running() throws Exception
	{
		return ((FanInternalControlCI)this.getConnector()).running();
	}
}
// -----------------------------------------------------------------------------
