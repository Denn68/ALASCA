package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		WashingMachineInternalControlCI
extends		OfferedCI,
			RequiredCI,
			WashingMachineInternalControlI
{
	@Override
	public boolean		heatWater() throws Exception;

	@Override
	public void			startHeatingWater() throws Exception;

	
	@Override
	public void			stopHeatingWater() throws Exception;

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception;

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
}
