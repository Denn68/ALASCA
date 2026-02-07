package fr.sorbonne_u.components.hem2025e1.equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law.

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>RegistrationOutboundPort</code> implements an outbound port
 * for the {@code RegistrationCI} component interface, allowing equipment
 * components to register with the Home Energy Manager.
 *
 * <p>
 * Created on : 2025-11-25
 * </p>
 * 
 * @author Team DeMoh
 */
public class RegistrationOutboundPort
        extends AbstractOutboundPort
        implements RegistrationCI {
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * create the outbound port.
     * 
     * @param owner component owning this port.
     * @throws Exception <i>to do</i>.
     */
    public RegistrationOutboundPort(ComponentI owner)
            throws Exception {
        super(RegistrationCI.class, owner);
    }

    /**
     * create the outbound port with the given URI.
     * 
     * @param uri   URI of this port.
     * @param owner component owning this port.
     * @throws Exception <i>to do</i>.
     */
    public RegistrationOutboundPort(String uri, ComponentI owner)
            throws Exception {
        super(uri, RegistrationCI.class, owner);
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#registered(String)
     */
    @Override
    public boolean registered(String uid) throws Exception {
        return ((RegistrationCI) this.getConnector()).registered(uid);
    }

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#register(String,
     *      String, String)
     */
    @Override
    public boolean register(
            String uid,
            String controlPortURI,
            String xmlControlAdapter) throws Exception {
        return ((RegistrationCI) this.getConnector())
                .register(uid, controlPortURI, xmlControlAdapter);
    }

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#unregister(String)
     */
    @Override
    public void unregister(String uid) throws Exception {
        ((RegistrationCI) this.getConnector()).unregister(uid);
    }
}
// -----------------------------------------------------------------------------
