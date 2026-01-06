package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineTemperatureI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			WashingMachineExternalControlInboundPort
extends		AbstractInboundPort
implements	WashingMachineExternalControlCI
{

	private static final long serialVersionUID = 1L;

	public				WashingMachineExternalControlInboundPort(ComponentI owner)
	throws Exception
	{
		this(WashingMachineExternalControlCI.class, owner);
	}
	
	public				WashingMachineExternalControlInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		this(uri, WashingMachineExternalControlCI.class, owner);
	}
	
	public				WashingMachineExternalControlInboundPort(
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(implementedInterface, owner);

		assert	implementedInterface != null &&
				WashingMachineExternalControlCI.class.isAssignableFrom(
														implementedInterface)  :
				new PreconditionException(
						"implementedInterface != null && "
						+ "WashingMachineExternalControlCI.class.isAssignableFrom("
						+ "implementedInterface)");
		assert	owner instanceof WashingMachineExternalControlI :
				new PreconditionException(
						"owner instanceof WashingMachineExternalControlI");
	}

	public				WashingMachineExternalControlInboundPort(
		String uri,
		Class<? extends OfferedCI> implementedInterface,
		ComponentI owner
		) throws Exception
	{
		super(uri, implementedInterface, owner);

		assert	implementedInterface != null &&
				WashingMachineExternalControlCI.class.isAssignableFrom(
														implementedInterface)  :
				new PreconditionException(
						"implementedInterface != null && "
						+ "WashingMachineExternalControlCI.class.isAssignableFrom("
						+ "implementedInterface)");
		assert	owner instanceof WashingMachineExternalControlI :
				new PreconditionException(
						"owner instanceof WashingMachineExternalControlI");
	}

	@Override
	public Measure<Double>	getMaxPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineExternalControlI)o).getMaxPowerLevel());
	}

	@Override
	public void			setCurrentPowerLevel(Measure<Double> powerLevel)
	throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineExternalControlI)o).
											setCurrentPowerLevel(powerLevel);
						return null;
					 });
	}
	
	@Override
	public SignalData<Double>	getCurrentPowerLevel() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineExternalControlI)o).getCurrentPowerLevel());
	}

	@Override
	public SignalData<Double>	getTargetTemperature() throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((WashingMachineTemperatureI)o).getTargetTemperature());
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
 		return this.getOwner().handleRequest(
 				o -> ((WashingMachineTemperatureI)o).getCurrentTemperature());
	}
}
