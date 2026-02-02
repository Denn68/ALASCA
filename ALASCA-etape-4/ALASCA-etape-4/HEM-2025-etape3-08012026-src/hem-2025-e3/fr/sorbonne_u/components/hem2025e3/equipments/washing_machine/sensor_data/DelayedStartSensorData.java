package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public class			DelayedStartSensorData
extends		SignalData<Double>
implements	WashingMachineSensorDataI
{
	private static final long serialVersionUID = 1L;

	public				DelayedStartSensorData(double remainingDelay)
	{
		super(new Measure<Double>(remainingDelay));
	}
}