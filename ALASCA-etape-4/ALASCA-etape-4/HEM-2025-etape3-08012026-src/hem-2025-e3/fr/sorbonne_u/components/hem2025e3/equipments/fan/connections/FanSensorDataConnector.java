package fr.sorbonne_u.components.hem2025e3.equipments.fan.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanSensorDataConnector</code> implements a connector for
 * the {@code FanSensorDataCI} component interface.
 *
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class FanSensorDataConnector
        extends DataConnector
        implements FanSensorDataCI.FanSensorRequiredPullCI {
    @Override
    public FanStateSensorData speedPullSensor() throws Exception {
        return ((FanSensorDataCI.FanSensorOfferedPullCI) this.offering).speedPullSensor();
    }

    @Override
    public void startSpeedPushSensor(
            long controlPeriod,
            TimeUnit tu) throws Exception {
        ((FanSensorDataCI.FanSensorOfferedPullCI) this.offering).startSpeedPushSensor(controlPeriod, tu);
    }
}
// -----------------------------------------------------------------------------
