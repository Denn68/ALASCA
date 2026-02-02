package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sensor_data;

import java.time.Instant;

import fr.sorbonne_u.alasca.physical_data.AbstractSignalData;
import fr.sorbonne_u.alasca.physical_data.ComposedSignalData;
import fr.sorbonne_u.exceptions.AssertionChecking;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.exceptions.PreconditionException;

public class            WashingMachineProgramSensorData
extends        ComposedSignalData
implements    WashingMachineSensorDataI
{
    private static final long serialVersionUID = 1L;

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
    // Accessors
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
