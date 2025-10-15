package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine;


import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public interface		WashingMachineUserCI
extends		OfferedCI,
			RequiredCI,
			WashingMachineUserI
{
	@Override
	public boolean		on() throws Exception;

	@Override
	public void			switchOn() throws Exception;

	@Override
	public void			switchOff() throws Exception;
	
	@Override
	public boolean 		isWashing() throws Exception;
	
	@Override
	public void 		startWashing(long washingTimeMS, Measure<Double> target) throws Exception;
	
	@Override
	public void 		delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception;

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception;

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
