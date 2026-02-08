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
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.FanActuatorCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanActuatorInboundPort</code> implements an inbound port
 * for the {@code FanActuatorCI} component interface.
 *
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public class FanActuatorInboundPort
        extends AbstractInboundPort
        implements FanActuatorCI {
    private static final long serialVersionUID = 1L;

    public FanActuatorInboundPort(ComponentI owner)
            throws Exception {
        super(FanActuatorCI.class, owner);
        assert owner instanceof FanUserI : new PreconditionException("owner instanceof FanUserI");
    }

    public FanActuatorInboundPort(
            String uri,
            ComponentI owner) throws Exception {
        super(uri, FanActuatorCI.class, owner);
        assert owner instanceof FanUserI : new PreconditionException("owner instanceof FanUserI");
    }

    @Override
    public void switchOn() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((FanUserI) o).switchOn();
                    return null;
                });
    }

    @Override
    public void switchOff() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((FanUserI) o).switchOff();
                    return null;
                });
    }

    @Override
    public void setHighSpeed() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((FanUserI) o).setHighSpeed();
                    return null;
                });
    }

    @Override
    public void setLowSpeed() throws Exception {
        this.getOwner().handleRequest(
                o -> {
                    ((FanUserI) o).setLowSpeed();
                    return null;
                });
    }
}
// -----------------------------------------------------------------------------
