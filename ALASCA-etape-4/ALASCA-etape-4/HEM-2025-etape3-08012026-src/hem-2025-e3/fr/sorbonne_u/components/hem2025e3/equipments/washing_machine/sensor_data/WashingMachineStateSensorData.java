package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.exceptions.PreconditionException;

public class			WashingMachineStateSensorData
extends		SignalData<WashingMachineState>
implements	WashingMachineSensorDataI
{
	private static final long serialVersionUID = 1L;

	public				WashingMachineStateSensorData(WashingMachineState s) throws Exception 
	{
		super(AssertionChecking.assertTrueAndReturnOrThrow(
				s != null,
				new Measure<WashingMachineState>(s),
				() -> new PreconditionException("s != null")));
	}
}