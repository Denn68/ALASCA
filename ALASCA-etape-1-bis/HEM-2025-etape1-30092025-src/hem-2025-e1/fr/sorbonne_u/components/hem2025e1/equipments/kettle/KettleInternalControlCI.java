package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public interface		KettleInternalControlCI
extends		OfferedCI,
			RequiredCI,
			KettleInternalControlI
{

	@Override
	public boolean		heating() throws Exception;

	@Override
	public void			startHeating() throws Exception;

	@Override
	public void			stopHeating() throws Exception;
	
	@Override
	public boolean		keepingWarm() throws Exception;

	@Override
	public void			startKeepingWarm() throws Exception;

	@Override
	public void			stopKeepingWarm() throws Exception;

	@Override
	public Measure<Double> getTargetTemperature() throws Exception;

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception;
}
// -----------------------------------------------------------------------------
