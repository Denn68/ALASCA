package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserCI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineTemperatureI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

public class WashingMachineUserInboundPort
		extends AbstractInboundPort
		implements WashingMachineUserCI {
	private static final long serialVersionUID = 1L;

	public WashingMachineUserInboundPort(ComponentI owner) throws Exception {
		super(WashingMachineUserCI.class, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	public WashingMachineUserInboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri, WashingMachineUserCI.class, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	public WashingMachineUserInboundPort(
			Class<? extends OfferedCI> implementedInterface,
			ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	public WashingMachineUserInboundPort(
			String uri,
			Class<? extends OfferedCI> implementedInterface,
			ComponentI owner) throws Exception {
		super(uri, implementedInterface, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	@Override
	public boolean on() throws Exception {
		return this.getOwner().handleRequest(o -> ((WashingMachineUserI) o).on());
	}

	@Override
	public void switchOn() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).switchOn();
					;
					return null;
				});
	}

	@Override
	public void switchOff() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).switchOff();
					;
					return null;
				});
	}

	@Override
	public void setTargetTemperature(Measure<Double> target)
			throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).setTargetTemperature(target);
					return null;
				});
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineExternalControlI) o).getMaxPowerLevel());
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel)
			throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineExternalControlI) o).setCurrentPowerLevel(powerLevel);
					return null;
				});
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineExternalControlI) o).getCurrentPowerLevel());
	}

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineTemperatureI) o).getTargetTemperature());
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineTemperatureI) o).getCurrentTemperature());
	}

	@Override
	public void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).delayedStart(delayMS, target, washingTimeMS);
					return null;
				});

	}

	@Override
	public void startWashing(long washingTimeMS, Measure<Double> target) throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).startWashing(washingTimeMS, target);
					return null;
				});
	}

	@Override
	public void suspendCycle() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).suspendCycle();
					return null;
				});
	}

	@Override
	public void resumeCycle() throws Exception {
		this.getOwner().handleRequest(
				o -> {
					((WashingMachineUserI) o).resumeCycle();
					return null;
				});
	}

	@Override
	public boolean isWashing() throws Exception {
		return this.getOwner().handleRequest(o -> ((WashingMachineUserI) o).isWashing());
	}
}
