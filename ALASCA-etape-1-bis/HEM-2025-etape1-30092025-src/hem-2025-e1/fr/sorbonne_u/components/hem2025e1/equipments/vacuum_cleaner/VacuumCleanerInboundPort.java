package fr.sorbonne_u.components.hem2025e1.equipments.vacuum_cleaner;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			VacuumCleanerInboundPort
extends		AbstractInboundPort
implements	VacuumCleanerUserCI
{
	private static final long serialVersionUID = 1L;

	public				VacuumCleanerInboundPort(ComponentI owner) throws Exception
	{
		super(VacuumCleanerUserCI.class, owner);
		assert	owner instanceof VacuumCleanerImplementationI :
				new PreconditionException(
						"owner instanceof VacuumCleanerImplementationI");
	}
	
	public				VacuumCleanerInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, VacuumCleanerUserCI.class, owner);
		assert	owner instanceof VacuumCleanerImplementationI :
				new PreconditionException(
						"owner instanceof VacuumCleanerImplementationI");
	}
	
	@Override
	public VacuumCleanerState	getState() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((VacuumCleanerImplementationI)o).getState());
	}

	@Override
	public VacuumCleanerMode	getMode() throws Exception
	{
		return this.getOwner().handleRequest(
							o -> ((VacuumCleanerImplementationI)o).getMode());
	}

	@Override
	public void			turnOn() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((VacuumCleanerImplementationI)o).turnOn();
									return null;
							});
	}

	@Override
	public void			turnOff() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((VacuumCleanerImplementationI)o).turnOff();
									return null;
							});
	}

	@Override
	public void			setHigh() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((VacuumCleanerImplementationI)o).setHigh();;
									return null;
							});
	}
	
	@Override
	public void			setMedium() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((VacuumCleanerImplementationI)o).setMedium();;
									return null;
							});
	}
	
	@Override
	public void			setLow() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((VacuumCleanerImplementationI)o).setLow();
									return null;
							});
	}
}
// -----------------------------------------------------------------------------
