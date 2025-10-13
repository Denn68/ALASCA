package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserJava4CI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;


public class			KettleUserJava4InboundPort
extends		KettleUserInboundPort
implements	KettleUserJava4CI
{
	private static final long serialVersionUID = 1L;
	
	public				KettleUserJava4InboundPort(ComponentI owner) throws Exception
	{
		super(KettleUserCI.class, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof HeaterUserI");
	}
	
	public				KettleUserJava4InboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, KettleUserCI.class, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof KettleUserI");
	}

	@Override
	public void			setTargetTemperatureJava4(double target)
	throws Exception
	{
		this.setTargetTemperature(
				new Measure<Double>(target, Kettle.TEMPERATURE_UNIT));
	}

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
				new Measure<Double>(powerLevel, Kettle.POWER_UNIT));
	}

	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	@Override
	public double		getTargetTemperatureJava4() throws Exception
	{
		
		return this.getTargetTemperature().getData();
	}

	@Override
	public double		getCurrentTemperatureJava4() throws Exception
	{
		
		return this.getCurrentTemperature().getMeasure().getData();
	}
}
// -----------------------------------------------------------------------------
