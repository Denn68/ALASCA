package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleInternalControlI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			KettleInternalControlInboundPort
extends		AbstractInboundPort
implements	KettleInternalControlCI
{

	private static final long serialVersionUID = 1L;

	public				KettleInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(KettleInternalControlCI.class, owner);
		assert	owner instanceof KettleInternalControlI :
				new PreconditionException(
						"owner instanceof KettleInternalControlI");
	}

	public				KettleInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, KettleInternalControlCI.class, owner);
		assert	owner instanceof KettleInternalControlI :
				new PreconditionException(
						"owner instanceof KettleInternalControlI");
	}

	@Override
	public boolean		heating() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((KettleInternalControlI)o).heating());
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((KettleInternalControlI)o).getTargetTemperature());
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
		o -> ((KettleInternalControlI)o).
								getCurrentTemperature());
	}

	@Override
	public void			startHeating() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((KettleInternalControlI)o).
																startHeating();
										return null;
								});
	}

	@Override
	public void			stopHeating() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((KettleInternalControlI)o).
																stopHeating();
										return null;
								});
	}

	@Override
	public boolean keepingWarm() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((KettleInternalControlI)o).keepingWarm());
	}

	@Override
	public void startKeepingWarm() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((KettleInternalControlI)o).
									startKeepingWarm();
										return null;
								});
	}

	@Override
	public void stopKeepingWarm() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((KettleInternalControlI)o).
									stopKeepingWarm();
										return null;
								});
	}
}
// -----------------------------------------------------------------------------
