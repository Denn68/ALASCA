package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.Fan;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanExternalControlJava4CI;
import fr.sorbonne_u.alasca.physical_data.Measure;

public class			FanExternalControlJava4InboundPort
extends		FanExternalControlInboundPort
implements	FanExternalControlJava4CI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanExternalControlJava4InboundPort(ComponentI owner)
	throws Exception
	{
		super(FanExternalControlJava4CI.class, owner);
	}

	public				FanExternalControlJava4InboundPort(
		String uri, ComponentI owner
		) throws Exception
	{
		super(uri, FanExternalControlJava4CI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public double		getMaxPowerLevelJava4() throws Exception
	{
		return this.getMaxPowerLevel().getData();
	}

	@Override
	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception
	{
		this.setCurrentPowerLevel(
					new Measure<Double>(powerLevel, Fan.POWER_UNIT));
	}

	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}
}
// -----------------------------------------------------------------------------
