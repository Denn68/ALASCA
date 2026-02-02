package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;

public interface WashingMachinePushImplementationI
{
	void processWashingMachineState(WashingMachineState washingMachineState);

	void processProgramData(WashingMachineProgramSensorData programData);
}
