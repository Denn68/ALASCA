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
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanSensorDataCI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.ports.AbstractDataInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanSensorDataInboundPort</code> implements an inbound port
 * for the {@code FanSensorDataCI} component interface.
 *
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class FanSensorDataInboundPort
        extends AbstractDataInboundPort
        implements FanSensorDataCI.FanSensorOfferedPullCI {
    private static final long serialVersionUID = 1L;

    public FanSensorDataInboundPort(ComponentI owner)
            throws Exception {
        super(FanSensorDataCI.FanSensorOfferedPullCI.class,
                DataOfferedCI.PushCI.class, owner);
        assert owner instanceof FanCyPhy : new PreconditionException("owner instanceof FanCyPhy");
    }

    public FanSensorDataInboundPort(
            String uri,
            ComponentI owner) throws Exception {
        super(uri, FanSensorDataCI.FanSensorOfferedPullCI.class,
                DataOfferedCI.PushCI.class, owner);
        assert owner instanceof FanCyPhy : new PreconditionException("owner instanceof FanCyPhy");
    }

    @Override
    public FanStateSensorData speedPullSensor() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((FanCyPhy) o).speedPullSensor());
    }

    @Override
    public void startSpeedPushSensor(
            long controlPeriod,
            TimeUnit tu) throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((FanCyPhy) o).startSpeedPushSensor(controlPeriod, tu);
                    return null;
                });
    }

    @Override
    public DataOfferedCI.DataI get() throws Exception {
        return this.getOwner().handleRequest(
                o -> ((FanCyPhy) o).speedPullSensor());
    }
}
// -----------------------------------------------------------------------------
