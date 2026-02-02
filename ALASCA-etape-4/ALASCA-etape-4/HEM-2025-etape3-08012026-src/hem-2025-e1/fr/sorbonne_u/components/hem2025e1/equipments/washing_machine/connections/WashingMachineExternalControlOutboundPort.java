package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class WashingMachineExternalControlOutboundPort
		extends AbstractOutboundPort
		implements WashingMachineExternalControlCI {

	private static final long serialVersionUID = 1L;

	public WashingMachineExternalControlOutboundPort(ComponentI owner)
			throws Exception {
		super(WashingMachineExternalControlCI.class, owner);
	}

	public WashingMachineExternalControlOutboundPort(
			String uri,
			ComponentI owner) throws Exception {
		super(uri, WashingMachineExternalControlCI.class, owner);
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return ((WashingMachineExternalControlCI) this.getConnector()).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel)
			throws Exception {
		((WashingMachineExternalControlCI) this.getConnector()).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return ((WashingMachineExternalControlCI) this.getConnector()).getCurrentPowerLevel();
	}

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception {
		return ((WashingMachineExternalControlCI) this.getConnector()).getTargetTemperature();
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return ((WashingMachineExternalControlCI) this.getConnector()).getCurrentTemperature();
	}
}
