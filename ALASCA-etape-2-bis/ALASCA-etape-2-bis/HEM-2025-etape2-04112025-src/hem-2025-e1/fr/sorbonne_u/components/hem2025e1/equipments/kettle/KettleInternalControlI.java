package fr.sorbonne_u.components.hem2025e1.equipments.kettle;


public interface		KettleInternalControlI
extends		KettleTemperatureI
{
	public boolean		heating() throws Exception;

	public void			startHeating() throws Exception;

	public void			stopHeating() throws Exception;
	
	public boolean		keepingWarm() throws Exception;

	public void			startKeepingWarm() throws Exception;

	public void			stopKeepingWarm() throws Exception;
}
// -----------------------------------------------------------------------------
