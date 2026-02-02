package fr.sorbonne_u.components.hem2025e1.equipments.kettle.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.kettle.KettleUserCI;

public class KettleUserConnector
		extends AbstractConnector
		implements KettleUserCI {

	@Override
	public boolean on() throws Exception {
		return ((KettleUserCI) this.offering).on();
	}

	@Override
	public void switchOn() throws Exception {
		((KettleUserCI) this.offering).switchOn();
	}

	@Override
	public void switchOff() throws Exception {
		((KettleUserCI) this.offering).switchOff();
	}

	@Override
	public void setTargetTemperature(Measure<Double> target)
			throws Exception {
		((KettleUserCI) this.offering).setTargetTemperature(target);
	}

	@Override
	public Measure<Double> getMaxPowerLevel() throws Exception {
		return ((KettleUserCI) this.offering).getMaxPowerLevel();
	}

	@Override
	public void setCurrentPowerLevel(Measure<Double> powerLevel)
			throws Exception {
		((KettleUserCI) this.offering).setCurrentPowerLevel(powerLevel);
	}

	@Override
	public SignalData<Double> getCurrentPowerLevel() throws Exception {
		return ((KettleUserCI) this.offering).getCurrentPowerLevel();
	}

	@Override
	public SignalData<Double> getTargetTemperature() throws Exception {
		return ((KettleUserCI) this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double> getCurrentTemperature() throws Exception {
		return ((KettleUserCI) this.offering).getCurrentTemperature();
	}
}
// -----------------------------------------------------------------------------
