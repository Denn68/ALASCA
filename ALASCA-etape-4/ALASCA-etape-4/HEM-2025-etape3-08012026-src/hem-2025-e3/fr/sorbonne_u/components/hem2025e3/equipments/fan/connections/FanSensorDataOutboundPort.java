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
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanPushImplementationI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanSensorDataI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanSensorDataOutboundPort</code> implements an outbound port
 * for the {@code FanSensorDataCI} component interface.
 *
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class FanSensorDataOutboundPort
        extends AbstractDataOutboundPort
        implements FanSensorDataCI.FanSensorRequiredPullCI {
    private static final long serialVersionUID = 1L;

    public FanSensorDataOutboundPort(ComponentI owner)
            throws Exception {
        super(DataRequiredCI.PullCI.class,
                DataRequiredCI.PushCI.class, owner);
    }

    public FanSensorDataOutboundPort(
            String uri,
            ComponentI owner) throws Exception {
        super(uri, FanSensorDataCI.FanSensorRequiredPullCI.class,
                DataRequiredCI.PushCI.class, owner);
    }

    @Override
    public FanStateSensorData speedPullSensor() throws Exception {
        return ((FanSensorDataCI.FanSensorRequiredPullCI) this.getConnector()).speedPullSensor();
    }

    @Override
    public void startSpeedPushSensor(
            long controlPeriod,
            TimeUnit tu) throws Exception {
        ((FanSensorDataCI.FanSensorRequiredPullCI) this.getConnector()).startSpeedPushSensor(controlPeriod, tu);
    }

    @Override
    public void receive(DataRequiredCI.DataI d) throws Exception {
        assert d instanceof FanSensorDataI : new BCMException("d instanceof FanSensorDataI");

        if (d instanceof FanStateSensorData) {
            this.getOwner().runTask(
                    o -> ((FanPushImplementationI) o).processFanState(
                            ((FanStateSensorData) d).getSpeed()));
        } else {
            throw new BCMException("Unknown fan sensor data: " + d);
        }
    }
}
// -----------------------------------------------------------------------------
