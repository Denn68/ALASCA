package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineExternalControlCI;

public class WashingMachineExternalControlConnector
		extends AbstractConnector
		implements WashingMachineExternalControlCI {

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return ((WashingMachineExternalControlCI) this.offering).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel)
			throws Exception {
		((WashingMachineExternalControlCI) this.offering).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return ((WashingMachineExternalControlCI) this.offering).getCurrentPowerLevel();
	}

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception {
		return ((WashingMachineExternalControlCI) this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return ((WashingMachineExternalControlCI) this.offering).getCurrentTemperature();
	}
}
