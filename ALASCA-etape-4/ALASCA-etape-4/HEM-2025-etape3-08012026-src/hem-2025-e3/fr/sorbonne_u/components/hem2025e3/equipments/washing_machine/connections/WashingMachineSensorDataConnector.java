package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;

// -----------------------------------------------------------------------------
/**
 * The class <code>WashingMachineSensorDataConnector</code> implements the
 * connector for the {@code WashingMachineSensorDataCI} component data interface.
 */
public class			WashingMachineSensorDataConnector
extends		DataConnector
implements	WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public WashingMachineState getState() throws Exception
	{
		return ((WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI)this.offering).
															getState();
	}

	@Override
	public WashingMachineProgramSensorData getProgramData() throws Exception
	{
		return ((WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI)this.offering).
															getProgramData();
	}

	@Override
	public void startProgramDataPushSensor(long controlPeriod, TimeUnit tu)
	throws Exception
	{
		((WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI)this.offering).
								startProgramDataPushSensor(controlPeriod, tu);
	}

	@Override
	public void startStatePushSensor() throws Exception
	{
		((WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI)this.offering).
								startStatePushSensor();
	}

	@Override
	public void stopPushing() throws Exception
	{
		((WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI)this.offering).
								stopPushing();
	}
}
// -----------------------------------------------------------------------------
