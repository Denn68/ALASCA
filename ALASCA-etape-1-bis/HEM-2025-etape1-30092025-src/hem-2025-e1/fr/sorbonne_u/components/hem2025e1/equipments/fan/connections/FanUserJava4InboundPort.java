package fr.sorbonne_u.components.hem2025e1.equipments.fan.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.Fan;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserJava4CI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;

// -----------------------------------------------------------------------------

public class			FanUserJava4InboundPort
extends		FanUserInboundPort
implements	FanUserJava4CI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				FanUserJava4InboundPort(ComponentI owner) throws Exception
	{
		super(FanUserCI.class, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
	}

	public				FanUserJava4InboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, FanUserCI.class, owner);
		assert	owner instanceof FanUserI :
				new PreconditionException("owner instanceof FanUserI");
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
