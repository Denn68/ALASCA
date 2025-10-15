package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

// -----------------------------------------------------------------------------

public interface		FanUserCI
extends		OfferedCI,
			RequiredCI,
			FanUserI
{
	@Override
	public boolean		on() throws Exception;

	@Override
	public void			switchOn() throws Exception;

	@Override
	public void			switchOff() throws Exception;

	@Override
	public boolean		running() throws Exception;

	@Override
	public void			startRunning() throws Exception;
	
	@Override
	public void			stopRunning() throws Exception;


	public Measure<Double>	getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;

}
// -----------------------------------------------------------------------------
