package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.components.hem2025e1.equipments.washing_machine.WashingMachine.WashingMachineState;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data.WashingMachineProgramSensorData;

/**
 * Push interface implemented by the washing machine controller to receive
 * data pushed by the washing machine component.
 */
public interface WashingMachinePushImplementationI
{
	/**
	 * Receive and process the washing machine state.
	 *
	 * pre  washingMachineState != null
	 */
	void processWashingMachineState(WashingMachineState washingMachineState);

	/**
	 * Receive and process the program status data (phase/timers, etc.).
	 *
	 * pre programData != null
	 */
	void processProgramData(WashingMachineProgramSensorData programData);
}
