package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;


import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			WashingMachineInternalControlInboundPort
extends		AbstractInboundPort
implements	WashingMachineInternalControlCI
{

	private static final long serialVersionUID = 1L;
	
	public				WashingMachineInternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineInternalControlCI.class, owner);
		assert	owner instanceof WashingMachineInternalControlI :
				new PreconditionException(
						"owner instanceof WashingMachineInternalControlI");
	}
	
	public				WashingMachineInternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, WashingMachineInternalControlCI.class, owner);
		assert	owner instanceof WashingMachineInternalControlI :
				new PreconditionException(
						"owner instanceof WashingMachineInternalControlI");
	}
	
	@Override
	public boolean		heatWater() throws Exception
	{
		return this.getOwner().handleRequest(
								o -> ((WashingMachineInternalControlI)o).heatWater());
	}

	@Override
	public Measure<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineInternalControlI)o).getTargetTemperature());
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
		o -> ((WashingMachineInternalControlI)o).
								getCurrentTemperature());
	}

	@Override
	public void			startHeatingWater() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((WashingMachineInternalControlI)o).
									startHeatingWater();
										return null;
								});
	}

	@Override
	public void			stopHeatingWater() throws Exception
	{
		this.getOwner().handleRequest(
								o -> {	((WashingMachineInternalControlI)o).
									stopHeatingWater();
										return null;
								});
	}
}
