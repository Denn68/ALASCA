package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data;

import java.time.Instant;

import fr.sorbonne_u.alasca.physical_data.AbstractSignalData;
import fr.sorbonne_u.alasca.physical_data.ComposedSignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>WashingMachineProgramSensorData</code> implements a composed
 * sensor data sent by the washing machine to the controller, which contains
 * information about the current program execution (phase and timers).
 *
 * Invariants: {@code size() == 3}
 */
public class            WashingMachineProgramSensorData
extends        ComposedSignalData
implements    WashingMachineSensorDataI
{
    private static final long serialVersionUID = 1L;

    /**
     * Create a new composed sensor data with the given phase and times at the current time
     * under the hardware clock time reference.
     *
     * @param phase                    current phase as a sensor data.
     * @param remainingPhaseTimeMS     remaining time of current phase (ms).
     * @param remainingDelayMS         remaining delayed-start time (ms, 0 if none).
     */
    public                WashingMachineProgramSensorData(
        WashingPhaseSensorData phase,
        RemainingTimeSensorData remainingPhaseTimeMS,
        DelayedStartSensorData remainingDelayMS
        ) throws Exception
    {
        super(AssertionChecking.assertTrueAndReturnOrThrow(
                phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null,
                new AbstractSignalData[]{phase, remainingPhaseTimeMS, remainingDelayMS},
                () -> new PreconditionException(
                        "phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null")));
    }

    public                WashingMachineProgramSensorData(
        WashingPhaseSensorData phase,
        RemainingTimeSensorData remainingPhaseTimeMS,
        DelayedStartSensorData remainingDelayMS,
        Instant timestamp
        ) throws Exception
    {
        super(AssertionChecking.assertTrueAndReturnOrThrow(
                phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null,
                new AbstractSignalData[]{phase, remainingPhaseTimeMS, remainingDelayMS},
                () -> new PreconditionException(
                        "phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null")),
              timestamp);
    }

    public                WashingMachineProgramSensorData(
        WashingPhaseSensorData phase,
        RemainingTimeSensorData remainingPhaseTimeMS,
        DelayedStartSensorData remainingDelayMS,
        Instant timestamp,
        String timestamper
        ) throws Exception
    {
        super(AssertionChecking.assertTrueAndReturnOrThrow(
                phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null,
                new AbstractSignalData[]{phase, remainingPhaseTimeMS, remainingDelayMS},
                () -> new PreconditionException(
                        "phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null")),
              timestamp,
              timestamper);
    }

    public                WashingMachineProgramSensorData(
        WashingPhaseSensorData phase,
        RemainingTimeSensorData remainingPhaseTimeMS,
        DelayedStartSensorData remainingDelayMS,
        AcceleratedClock ac
        ) throws Exception
    {
        super(AssertionChecking.assertTrueAndReturnOrThrow(
                phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null,
                new AbstractSignalData[]{phase, remainingPhaseTimeMS, remainingDelayMS},
                () -> new PreconditionException(
                        "phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null")),
              ac);
    }

    public                WashingMachineProgramSensorData(
        WashingPhaseSensorData phase,
        RemainingTimeSensorData remainingPhaseTimeMS,
        DelayedStartSensorData remainingDelayMS,
        AcceleratedClock ac,
        Instant timestamp
        ) throws Exception
    {
        super(AssertionChecking.assertTrueAndReturnOrThrow(
                phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null,
                new AbstractSignalData[]{phase, remainingPhaseTimeMS, remainingDelayMS},
                () -> new PreconditionException(
                        "phase != null && remainingPhaseTimeMS != null && remainingDelayMS != null")),
              ac,
              timestamp);
    }

    // -------------------------------------------------------------------------
    // Accessors (same style as HeaterTemperaturesSensorData)
    // -------------------------------------------------------------------------

    public WashingPhaseSensorData        getPhase()
    {
        return (WashingPhaseSensorData) this.get(0);
    }

    public RemainingTimeSensorData        getRemainingPhaseTime()
    {
        return (RemainingTimeSensorData) this.get(1);
    }

    public DelayedStartSensorData        getRemainingDelay()
    {
        return (DelayedStartSensorData) this.get(2);
    }
}
