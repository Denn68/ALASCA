package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public interface		WashingMachineExternalControlCI
extends		RequiredCI,
			OfferedCI,
			WashingMachineExternalControlI
{
	public Measure<Double>	getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;

	@Override
	public Measure<Double> getTargetTemperature() throws Exception;

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
}
