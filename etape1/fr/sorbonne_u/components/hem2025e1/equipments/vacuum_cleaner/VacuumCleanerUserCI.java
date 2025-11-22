package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		VacuumCleanerUserCI
extends		OfferedCI,
			RequiredCI,
			VacuumCleanerImplementationI
{
	@Override
	public VacuumCleanerState	getState() throws Exception;

	@Override
	public VacuumCleanerMode	getMode() throws Exception;

	@Override
	public void			turnOn() throws Exception;

	@Override
	public void			turnOff() throws Exception;

	@Override
	public void			setHigh() throws Exception;

	@Override
	public void			setMedium() throws Exception;
	
	@Override
	public void			setLow() throws Exception;
}
