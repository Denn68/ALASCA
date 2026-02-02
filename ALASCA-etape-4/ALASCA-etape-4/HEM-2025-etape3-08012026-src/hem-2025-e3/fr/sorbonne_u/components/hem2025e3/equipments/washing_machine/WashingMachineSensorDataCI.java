package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface		WashingMachineSensorDataCI
extends		DataOfferedCI,
			DataRequiredCI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	public static interface	WashingMachineSensorCI
	extends		OfferedCI,
				RequiredCI
	{
		public WashingMachineState getState() throws Exception;

		public WashingMachineProgramSensorData getProgramData() throws Exception;

		public void			startProgramDataPushSensor(
			long controlPeriod,
			TimeUnit tu
			) throws Exception;

		public void			startStatePushSensor() throws Exception;

		public void			stopPushing() throws Exception;
	}

	public static interface		WashingMachineSensorRequiredPullCI
	extends		DataRequiredCI.PullCI,
				WashingMachineSensorCI
	{
	}

	public static interface		WashingMachineSensorOfferedPullCI
	extends		DataOfferedCI.PullCI,
				WashingMachineSensorCI
	{
	}
}