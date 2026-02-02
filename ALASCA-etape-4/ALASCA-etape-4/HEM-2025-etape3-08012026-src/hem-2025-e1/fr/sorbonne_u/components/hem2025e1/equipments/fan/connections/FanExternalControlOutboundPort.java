package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------

public class			FanExternalControlOutboundPort
extends		AbstractOutboundPort
implements	FanExternalControlCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanExternalControlOutboundPort(ComponentI owner)
	throws Exception
	{
		super(FanExternalControlCI.class, owner);
	}

	public				FanExternalControlOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, FanExternalControlCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return ((FanExternalControlCI)this.getConnector()).
													getMaxPowerLevel();
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		((FanExternalControlCI)this.getConnector()).
											setCurrentPowerLevel(powerLevel);
	}
	
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return ((FanExternalControlCI)this.getConnector()).
													getCurrentPowerLevel();
	}
}
// -----------------------------------------------------------------------------
