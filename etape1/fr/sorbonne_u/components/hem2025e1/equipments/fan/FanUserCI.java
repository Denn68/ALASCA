package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

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
	public void			setLowSpeed() throws Exception;

	@Override
	public void			setHighSpeed() throws Exception;

	@Override
	public FanSpeed		getSpeed() throws Exception;

	// Méthodes héritées de FanExternalControlI mais accessibles à l'utilisateur
	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception;

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception;

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception;
}