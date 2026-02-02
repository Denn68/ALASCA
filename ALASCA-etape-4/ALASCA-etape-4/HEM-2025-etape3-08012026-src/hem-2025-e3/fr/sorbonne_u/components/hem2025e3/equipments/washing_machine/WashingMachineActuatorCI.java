package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine;

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * Actuator CI used by a controller to drive the washing machine.
 * Kept minimal and aligned with the washing machine external/user services.
 */
public interface WashingMachineActuatorCI
extends OfferedCI, RequiredCI
{
	void switchOn() throws Exception;
	void switchOff() throws Exception;

	void setTargetTemperature(Measure<Double> target) throws Exception;

	void startWashing(long washingTimeMS, Measure<Double> target) throws Exception;

	void delayedStart(long delayMS, Measure<Double> target, long washingTimeMS) throws Exception;

	void suspendCycle() throws Exception;
	void resumeCycle() throws Exception;
}

