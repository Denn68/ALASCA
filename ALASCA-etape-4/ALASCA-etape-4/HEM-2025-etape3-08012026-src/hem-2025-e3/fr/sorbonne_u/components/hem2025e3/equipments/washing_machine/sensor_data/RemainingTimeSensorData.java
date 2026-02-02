package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;

public class			RemainingTimeSensorData
extends		SignalData<Double>
implements	WashingMachineSensorDataI
{
	private static final long serialVersionUID = 1L;

	public				RemainingTimeSensorData(double remainingTime)
	{
		super(new Measure<Double>(remainingTime));
	}
}