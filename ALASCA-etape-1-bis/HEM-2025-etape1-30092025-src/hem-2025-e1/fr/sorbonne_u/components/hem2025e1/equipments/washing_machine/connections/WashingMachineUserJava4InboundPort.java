package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;

public class			WashingMachineUserJava4InboundPort
extends		WashingMachineUserInboundPort
implements	WashingMachineUserJava4CI
{

	private static final long serialVersionUID = 1L;
	
	public				WashingMachineUserJava4InboundPort(ComponentI owner) throws Exception
	{
		super(WashingMachineUserCI.class, owner);
		assert	owner instanceof WashingMachineUserI :
				new PreconditionException("owner instanceof WashingMachineUserI");
	}
	
	public				WashingMachineUserJava4InboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineUserCI.class, owner);
		assert	owner instanceof WashingMachineUserI :
				new PreconditionException("owner instanceof WashingMachineUserI");
	}
	
	@Override
	public void			setTargetTemperatureJava4(double target)
	throws Exception
	{
		this.setTargetTemperature(
				new Measure<Double>(target, Heater.TEMPERATURE_UNIT));
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserJava4CI#getMaxPowerLevelJava4()
	 */
	@Override
	public double		getMaxPowerLevelJava4() throws Exception
	{
		return this.getMaxPowerLevel().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserJava4CI#setCurrentPowerLevelJava4(double)
	 */
	@Override
	public void			setCurrentPowerLevelJava4(double powerLevel)
	throws Exception
	{
		this.setCurrentPowerLevel(
				new Measure<Double>(powerLevel, Heater.POWER_UNIT));
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserJava4CI#getCurrentPowerLevelJava4()
	 */
	@Override
	public double		getCurrentPowerLevelJava4() throws Exception
	{
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserJava4CI#getTargetTemperatureJava4()
	 */
	@Override
	public double		getTargetTemperatureJava4() throws Exception
	{
		
		return this.getTargetTemperature().getData();
	}

	/**
	 * @see fr.sorbonne_u.components.hem2025e1.equipments.heater.HeaterUserJava4CI#getCurrentTemperatureJava4()
	 */
	@Override
	public double		getCurrentTemperatureJava4() throws Exception
	{
		
		return this.getCurrentTemperature().getMeasure().getData();
	}
}
// -----------------------------------------------------------------------------
