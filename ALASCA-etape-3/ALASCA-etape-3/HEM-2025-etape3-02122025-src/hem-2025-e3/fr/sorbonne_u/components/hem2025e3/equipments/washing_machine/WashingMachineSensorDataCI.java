package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;

// On suppose l'existence d'une classe de données équivalente à HeaterStateSensorData
// Si tu ne l'as pas, utilise Object ou crée WashingMachineStateSensorData
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineStateSensorData; 

public interface		WashingMachineSensorDataCI
extends		DataOfferedCI,
			DataRequiredCI
{
	public static interface	WashingMachineSensorCI
	extends		DataRequiredCI.PullCI
	{
		// Pull : Récupérer l'état instantané
		public WashingMachineStateSensorData	statePullSensor() throws Exception;

		// Push : Demander à recevoir l'état périodiquement
		public void			startStatePushSensor(
			long controlPeriod,
			TimeUnit tu
			) throws Exception;
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