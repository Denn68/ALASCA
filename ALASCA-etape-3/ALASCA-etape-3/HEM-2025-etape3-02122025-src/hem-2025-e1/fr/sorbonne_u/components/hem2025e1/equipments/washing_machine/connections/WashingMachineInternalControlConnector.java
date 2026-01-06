package fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.connections;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachineInternalControlCI;

public class			WashingMachineInternalControlConnector
extends		AbstractConnector
implements	WashingMachineInternalControlCI
{
	@Override
	public boolean		heatWater() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.offering).heatWater();
	}

	@Override
	public SignalData<Double>	getTargetTemperature() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.offering).getTargetTemperature();
	}

	@Override
	public SignalData<Double>	getCurrentTemperature() throws Exception
	{
		return ((WashingMachineInternalControlCI)this.offering).getCurrentTemperature();
	}

	@Override
	public void			startHeatingWater() throws Exception
	{
		((WashingMachineInternalControlCI)this.offering).startHeatingWater();
	}

	@Override
	public void			stopHeatingWater() throws Exception
	{
		((WashingMachineInternalControlCI)this.offering).stopHeatingWater();
	}
}
