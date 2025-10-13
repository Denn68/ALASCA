package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;


import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.Kettle;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlJava4CI;
import fr.sorbonne_u.alasca.physical_data.Measure;

public class			KettleExternalControlJava4InboundPort
extends		KettleExternalControlInboundPort
implements	KettleExternalControlJava4CI
{

	public				KettleExternalControlJava4InboundPort(ComponentI owner)
	throws Exception
	{
		super(KettleExternalControlJava4CI.class, owner);
	}

	public				KettleExternalControlJava4InboundPort(
		String uri, ComponentI owner
		) throws Exception
	{
		super(uri, KettleExternalControlJava4CI.class, owner);
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
