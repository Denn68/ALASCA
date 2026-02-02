package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;

public interface		WashingMachineInternalControlI
extends		WashingMachineTemperatureI
{
	public boolean		heatWater() throws Exception;
	
	public void			startHeatingWater() throws Exception;
	
	public void			stopHeatingWater() throws Exception;
}
