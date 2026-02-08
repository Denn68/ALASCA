package fr.sorbonne_u.components.hem2025e3.equipments.fan.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanActuatorCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanActuatorOutboundPort</code> implements an outbound port
 * for the {@code FanActuatorCI} component interface.
 *
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class FanActuatorOutboundPort
        extends AbstractOutboundPort
        implements FanActuatorCI {
    private static final long serialVersionUID = 1L;

    public FanActuatorOutboundPort(ComponentI owner)
            throws Exception {
        super(FanActuatorCI.class, owner);
    }

    public FanActuatorOutboundPort(
            String uri,
            ComponentI owner) throws Exception {
        super(uri, FanActuatorCI.class, owner);
    }

    @Override
    public void switchOn() throws Exception {
        ((FanActuatorCI) this.getConnector()).switchOn();
    }

    @Override
    public void switchOff() throws Exception {
        ((FanActuatorCI) this.getConnector()).switchOff();
    }

    @Override
    public void setHighSpeed() throws Exception {
        ((FanActuatorCI) this.getConnector()).setHighSpeed();
    }

    @Override
    public void setLowSpeed() throws Exception {
        ((FanActuatorCI) this.getConnector()).setLowSpeed();
    }
}
// -----------------------------------------------------------------------------
