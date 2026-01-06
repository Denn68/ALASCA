package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlJava4CI;

public class			WashingMachineExternalControlJava4InboundPort
extends		WashingMachineExternalControlInboundPort
implements	WashingMachineExternalControlJava4CI
{

	private static final long serialVersionUID = 1L;

	public				WashingMachineExternalControlJava4InboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineExternalControlJava4CI.class, owner);
	}

	public				WashingMachineExternalControlJava4InboundPort(
		String uri, ComponentI owner
		) throws Exception
	{
		super(uri, WashingMachineExternalControlJava4CI.class, owner);
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
					new Measure<Double>(powerLevel, WashingMachine.POWER_UNIT));
	}

	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	@Override
	public double		getTargetTemperatureJava4() throws Exception
	{
		return this.getTargetTemperature().getMeasure().getData();
	}

	@Override
	public double		getCurrentTemperatureJava4() throws Exception
	{
		return this.getCurrentTemperature().getMeasure().getData();
	}
}
