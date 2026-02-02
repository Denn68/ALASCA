package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserJava4CI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineUserCI;
import fr.sorbonne_u.exceptions.PreconditionException;

public class WashingMachineUserJava4InboundPort
		extends WashingMachineUserInboundPort
		implements WashingMachineUserJava4CI {

	private static final long serialVersionUID = 1L;

	public WashingMachineUserJava4InboundPort(ComponentI owner) throws Exception {
		super(WashingMachineUserCI.class, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	public WashingMachineUserJava4InboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, WashingMachineUserCI.class, owner);
		assert owner instanceof WashingMachineUserI : new PreconditionException("owner instanceof WashingMachineUserI");
	}

	@Override
	public void setTargetTemperatureJava4(double target)
			throws Exception {
		this.setTargetTemperature(
				new Measure<Double>(target, WashingMachine.TEMPERATURE_UNIT));
	}

	@Override
	public double getMaxPowerLevelJava4() throws Exception {
		return this.getMaxPowerLevel().getData();
	}

	@Override
	public void setCurrentPowerLevelJava4(double powerLevel)
			throws Exception {
		this.setCurrentPowerLevel(
				new Measure<Double>(powerLevel, WashingMachine.POWER_UNIT));
	}

	@Override
	public double getCurrentPowerLevelJava4() throws Exception {
		return this.getCurrentPowerLevel().getMeasure().getData();
	}

	@Override
	public double getTargetTemperatureJava4() throws Exception {

		return this.getTargetTemperature().getMeasure().getData();
	}

	@Override
	public double getCurrentTemperatureJava4() throws Exception {

		return this.getCurrentTemperature().getMeasure().getData();
	}
}
// -----------------------------------------------------------------------------
