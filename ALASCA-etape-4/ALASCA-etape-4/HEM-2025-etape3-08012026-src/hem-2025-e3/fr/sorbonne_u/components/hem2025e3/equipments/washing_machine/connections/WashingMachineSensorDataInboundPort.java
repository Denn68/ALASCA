package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.connections;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.concurrent.TimeUnit;

public class			WashingMachineSensorDataInboundPort
extends		AbstractDataInboundPort
implements	WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineSensorDataInboundPort(ComponentI owner)
	throws Exception
	{
		super(WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof WashingMachineCyPhy :
				new PreconditionException("owner instanceof WashingMachineCyPhy");
	}

	public				WashingMachineSensorDataInboundPort(String uri, ComponentI owner)
	throws Exception
	{
		super(uri, WashingMachineSensorDataCI.WashingMachineSensorOfferedPullCI.class,
			  DataOfferedCI.PushCI.class, owner);

		assert	owner instanceof WashingMachineCyPhy :
				new PreconditionException("owner instanceof WashingMachineCyPhy");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	@Override
	public WashingMachineState getState() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineCyPhy)o).getState());
	}

	@Override
	public WashingMachineProgramSensorData getProgramData() throws Exception {
		return this.getOwner().handleRequest(
				o -> ((WashingMachineCyPhy)o).getProgramData());
	}

	@Override
	public void startProgramDataPushSensor(long controlPeriod, TimeUnit tu) throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineCyPhy)o).startProgramDataPushSensor(controlPeriod, tu);
						return null;
				});
	}

	@Override
	public void startStatePushSensor() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineCyPhy)o).startStatePushSensor();
						return null;
				});
	}

	@Override
	public void stopPushing() throws Exception {
		this.getOwner().handleRequest(
				o -> {	((WashingMachineCyPhy)o).stopPushing();
						return null;
				});
	}

	@Override
	public DataOfferedCI.DataI		get() throws Exception
	{
		// Par défaut (pour le Pull générique), on renvoie les données du programme
		return this.getOwner().handleRequest(
				o -> ((WashingMachineCyPhy)o).getProgramData());
	}
}