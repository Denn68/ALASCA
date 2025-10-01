package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class			VacuumCleanerOutboundPort
extends		AbstractOutboundPort
implements	VacuumCleanerUserCI
{

	private static final long serialVersionUID = 1L;
	
	public				VacuumCleanerOutboundPort(ComponentI owner)
	throws Exception
	{
		super(VacuumCleanerUserCI.class, owner);
	}
	
	public				VacuumCleanerOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, VacuumCleanerUserCI.class, owner);
	}
	
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		return ((VacuumCleanerUserCI)this.getConnector()).getState();
	}
	
	@Override
	public VacuumCleanerMode	getMode() throws Exception
	{
		return ((VacuumCleanerUserCI)this.getConnector()).getMode();
	}

	@Override
	public void			turnOn() throws Exception
	{
		((VacuumCleanerUserCI)this.getConnector()).turnOn();
	}

	@Override
	public void			turnOff() throws Exception
	{
		((VacuumCleanerUserCI)this.getConnector()).turnOff();
	}

	@Override
	public void			setHigh() throws Exception
	{
		((VacuumCleanerUserCI)this.getConnector()).setHigh();
	}

	@Override
	public void			setMedium() throws Exception
	{
		((VacuumCleanerUserCI)this.getConnector()).setMedium();
	}

	
	@Override
	public void			setLow() throws Exception
	{
		((VacuumCleanerUserCI)this.getConnector()).setLow();
	}
}
// -----------------------------------------------------------------------------
