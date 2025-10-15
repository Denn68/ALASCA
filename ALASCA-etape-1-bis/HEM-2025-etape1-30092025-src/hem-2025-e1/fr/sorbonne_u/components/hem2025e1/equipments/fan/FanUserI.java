package fr.sorbonne_u.components.hem2025e1.equipments.fan;

import fr.sorbonne_u.alasca.physical_data.Measure;

public interface		FanUserI
extends		FanExternalControlI
{

	public boolean		on() throws Exception;

	public void			switchOn() throws Exception;
	
	public void			switchOff() throws Exception;

	public boolean		running() throws Exception;

	public void			startRunning() throws Exception;
	
	public void			stopRunning() throws Exception;
}
// -----------------------------------------------------------------------------
