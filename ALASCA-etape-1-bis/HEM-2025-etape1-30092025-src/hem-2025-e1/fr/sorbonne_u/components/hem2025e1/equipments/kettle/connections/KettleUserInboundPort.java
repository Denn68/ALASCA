package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;



import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleTemperatureI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;


public class			KettleUserInboundPort
extends		AbstractInboundPort
implements	KettleUserCI
{
	private static final long serialVersionUID = 1L;

	public				KettleUserInboundPort(ComponentI owner) throws Exception
	{
		super(KettleUserCI.class, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof KettleUserI");
	}

	public				KettleUserInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, KettleUserCI.class, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof KettleUserI");
	}

	public				KettleUserInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof KettleUserI");
	}

	public				KettleUserInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);
		assert	owner instanceof KettleUserI :
				new PreconditionException("owner instanceof KettleUserI");
	}
	@Override
	public boolean		on() throws Exception
	{
		return this.getOwner().handleRequest(o -> ((KettleUserI)o).on());
	}

	@Override
	public void			switchOn() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((KettleUserI)o).switchOn();;
									return null;
							});
	}

	@Override
	public void			switchOff() throws Exception
	{
		this.getOwner().handleRequest(
							o -> {	((KettleUserI)o).switchOff();;
									return null;
							});
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target)
	throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((KettleUserI)o).
									setTargetTemperature(target);
						return null;
				});
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((KettleExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
				o -> { ((KettleExternalControlI)o).
								setCurrentPowerLevel(powerLevel);
						return null;
				});
	}

	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((KettleExternalControlI)o).getCurrentPowerLevel());
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((KettleTemperatureI)o).getTargetTemperature());
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((KettleTemperatureI)o).getCurrentTemperature());
	}
}
// -----------------------------------------------------------------------------
