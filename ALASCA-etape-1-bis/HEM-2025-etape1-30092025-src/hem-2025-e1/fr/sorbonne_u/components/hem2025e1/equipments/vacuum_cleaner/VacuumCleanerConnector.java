package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.connectors.AbstractConnector;

public class			VacuumCleanerConnector
extends		AbstractConnector
implements	VacuumCleanerUserCI
{
	
	public VacuumCleanerState	getState() throws Exception
	{
		return ((VacuumCleanerUserCI)this.offering).getState();
	}

	@Override
	public	VacuumCleanerMode	getMode() throws Exception
	{
		return ((VacuumCleanerUserCI)this.offering).getMode();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((VacuumCleanerUserCI)this.offering).turnOn();
	}
	
	@Override
	public void			turnOff() throws Exception
	{
		((VacuumCleanerUserCI)this.offering).turnOff();
	}

	@Override
	public void			setHigh() throws Exception
	{
		((VacuumCleanerUserCI)this.offering).setHigh();
	}

	@Override
	public void			setMedium() throws Exception
	{
		((VacuumCleanerUserCI)this.offering).setMedium();
	}
	
	@Override
	public void			setLow() throws Exception
	{
		((VacuumCleanerUserCI)this.offering).setLow();
	}
}
// -----------------------------------------------------------------------------
