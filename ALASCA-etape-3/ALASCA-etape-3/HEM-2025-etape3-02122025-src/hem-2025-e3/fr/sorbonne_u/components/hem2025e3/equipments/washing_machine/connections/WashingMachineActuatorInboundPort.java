package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineActuatorCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			WashingMachineActuatorInboundPort
extends		AbstractInboundPort
implements	WashingMachineActuatorCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineActuatorInboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineActuatorCI.class, owner);
		// Correction : On vérifie que le composant implémente bien WashingMachineUserI
		assert	owner instanceof WashingMachineUserI :
			new PreconditionException("owner instanceof WashingMachineUserI");
	}

	public				WashingMachineActuatorInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineActuatorCI.class, owner);
		assert	owner instanceof WashingMachineUserI :
			new PreconditionException("owner instanceof WashingMachineUserI");
	}

	@Override
	public void			switchOn() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).switchOn();
						return null;
				});
	}

	@Override
	public void			switchOff() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).switchOff();
						return null;
				});
	}

	@Override
	public void			setTargetTemperature(Measure<Double> target) throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).setTargetTemperature(target);
						return null;
				});
	}

	@Override
	public void			startWashing(long washingTimeMS, Measure<Double> target) throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).startWashing(washingTimeMS, target);
						return null;
				});
	}

	@Override
	public void			delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).delayedStart(delayMS, target, washingTimeMS);
						return null;
				});
	}

	@Override
	public void			suspendCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).suspendCycle();
						return null;
				});
	}

	@Override
	public void			resumeCycle() throws Exception
	{
		this.getOwner().handleRequest(
				o -> {	((WashingMachineUserI)o).resumeCycle();
						return null;
				});
	}
}