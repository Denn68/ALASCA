package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanInternalControlI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------

public class			FanInternalControlInboundPort
extends		AbstractInboundPort
implements	FanInternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------
	
	public				FanInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(FanInternalControlCI.class, owner);
		assert	owner instanceof FanInternalControlI :
				new PreconditionException(
						"owner instanceof FanInternalControlI");
	}
	
	public				FanInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FanInternalControlCI.class, owner);
		assert	owner instanceof FanInternalControlI :
				new PreconditionException(
						"owner instanceof FanInternalControlI");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public boolean		running() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((FanInternalControlI)o).running());
	}
}
// -----------------------------------------------------------------------------
