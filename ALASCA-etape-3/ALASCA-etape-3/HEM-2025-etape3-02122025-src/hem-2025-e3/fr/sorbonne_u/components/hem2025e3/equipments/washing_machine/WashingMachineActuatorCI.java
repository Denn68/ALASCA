package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		WashingMachineActuatorCI
extends		OfferedCI,
			RequiredCI
{
	public void			switchOn() throws Exception;
	public void			switchOff() throws Exception;
	
	// Paramètres : durée du lavage, température cible
	public void			startWashing(long washingTimeMS, Measure<Double> target) throws Exception;
	
	public void			suspendCycle() throws Exception;
	public void			resumeCycle() throws Exception;
}