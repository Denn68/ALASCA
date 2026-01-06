package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>WashingMachineSensorDataCI</code> defines the data
 * interfaces used to exchange sensor data between the washing machine
 * and its controller.
 * * Modeled strictly after HeaterSensorDataCI.
 */
public interface		WashingMachineSensorDataCI
extends		DataOfferedCI,
			DataRequiredCI
{
	// -------------------------------------------------------------------------
	// Inner interfaces and types
	// -------------------------------------------------------------------------

	/**
	 * The component interface <code>WashingMachineSensorCI</code> declares the 
	 * common services used in pull mode to get the sensor data and to control
	 * the push mode.
	 */
	public static interface	WashingMachineSensorCI
	extends		OfferedCI,
				RequiredCI
	{
		// ---------------------------------------------------------------------
		// Pull Methods (Data retrieval)
		// ---------------------------------------------------------------------

		public WashingMachineState getState() throws Exception;

		public WashingMachineProgramSensorData getProgramData() throws Exception;

		// ---------------------------------------------------------------------
		// Push Control Methods (Commands)
		// ---------------------------------------------------------------------

		/**
		 * Start sending program data (time remaining, phase) periodically.
		 * Corresponds to Heater's startTemperaturesPushSensor.
		 */
		public void			startProgramDataPushSensor(
			long controlPeriod,
			TimeUnit tu
			) throws Exception;

		public void			startStatePushSensor() throws Exception;

		public void			stopPushing() throws Exception;
	}

	/**
	 * The interface <code>WashingMachineSensorRequiredPullCI</code> is the pull
	 * interface that a client component must require to call the outbound port.
	 */
	public static interface		WashingMachineSensorRequiredPullCI
	extends		DataRequiredCI.PullCI,
				WashingMachineSensorCI
	{
	}

	/**
	 * The interface <code>WashingMachineSensorOfferedPullCI</code> is the pull
	 * interface that a server component must offer to be called the inbound port.
	 */
	public static interface		WashingMachineSensorOfferedPullCI
	extends		DataOfferedCI.PullCI,
				WashingMachineSensorCI
	{
	}
}