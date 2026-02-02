package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachinePushImplementationI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineSensorDataI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineStateSensorData;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import java.util.concurrent.TimeUnit;

public class 			WashingMachineSensorDataOutboundPort
extends		AbstractDataOutboundPort
implements	WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineSensorDataOutboundPort(ComponentI owner)
	throws Exception
	{
		super(DataRequiredCI.PullCI.class, DataRequiredCI.PushCI.class, owner);
	}

	public				WashingMachineSensorDataOutboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI.class,
			  DataRequiredCI.PushCI.class, owner);
	}

	// -------------------------------------------------------------------------
	// Methods (Calling the Connector)
	// -------------------------------------------------------------------------

	@Override
	public WashingMachineState getState() throws Exception {
		return ((WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI)
				this.getConnector()).getState();
	}

	@Override
	public WashingMachineProgramSensorData getProgramData() throws Exception {
		return ((WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI)
				this.getConnector()).getProgramData();
	}

	@Override
	public void startProgramDataPushSensor(long controlPeriod, TimeUnit tu) throws Exception {
		((WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI)
				this.getConnector()).startProgramDataPushSensor(controlPeriod, tu);
	}

	@Override
	public void startStatePushSensor() throws Exception {
		((WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI)
				this.getConnector()).startStatePushSensor();
	}

	@Override
	public void stopPushing() throws Exception {
		((WashingMachineSensorDataCI.WashingMachineSensorRequiredPullCI)
				this.getConnector()).stopPushing();
	}

	// -------------------------------------------------------------------------
	// Receive (Handling Data Pushed from InboundPort)
	// -------------------------------------------------------------------------

	@Override
	public void			receive(DataRequiredCI.DataI d) throws Exception
	{
		assert	d instanceof WashingMachineSensorDataI :
				new BCMException("d instanceof WashingMachineSensorDataI");

		if (d instanceof WashingMachineStateSensorData) {
			this.getOwner().runTask(
					o -> ((WashingMachinePushImplementationI)o).processWashingMachineState(
							((WashingMachineStateSensorData)d).getMeasure().getData()));
		} else if (d instanceof WashingMachineProgramSensorData) {
			this.getOwner().runTask(
					o -> ((WashingMachinePushImplementationI)o).processProgramData(
								(WashingMachineProgramSensorData)d));
		} else {
			throw new BCMException("Unknown washing machine sensor data: " + d);
		}
	}
}