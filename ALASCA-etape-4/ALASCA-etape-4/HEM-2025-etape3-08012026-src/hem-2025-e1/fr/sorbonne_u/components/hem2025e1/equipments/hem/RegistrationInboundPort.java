package fr.sorbonne_u.components.hem2025e1.equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law.

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>RegistrationInboundPort</code> implements an inbound port
 * for the {@code RegistrationCI} component interface, allowing the Home
 * Energy Manager to receive registration requests from equipment components.
 *
 * <p>
 * This port delegates registration calls to the owner component through
 * the nested interface {@code RegistrationImplementorI}. Any component
 * wishing to host this port must implement that interface.
 * </p>
 *
 * <p>
 * Created on : 2025-11-25
 * </p>
 * 
 * @author Team DeMoh
 */
public class RegistrationInboundPort
        extends AbstractInboundPort
        implements RegistrationCI {
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Inner interfaces
    // -------------------------------------------------------------------------

    /**
     * The interface <code>RegistrationImplementorI</code> must be implemented
     * by any component that owns a {@code RegistrationInboundPort}, in order
     * to handle registration requests from equipments.
     */
    public interface RegistrationImplementorI {
        /**
         * check whether an equipment is currently registered.
         * 
         * @param uid unique identifier of the equipment.
         * @return true if the equipment is registered.
         * @throws Exception <i>to do</i>.
         */
        boolean registered(String uid) throws Exception;

        /**
         * register an equipment with the given control interface descriptor.
         * 
         * @param uid               unique identifier of the equipment.
         * @param controlPortURI    URI of the equipment's external control inbound
         *                          port.
         * @param xmlControlAdapter path to the XML control adapter descriptor.
         * @return true if the registration succeeded.
         * @throws Exception <i>to do</i>.
         */
        boolean register(
                String uid,
                String controlPortURI,
                String xmlControlAdapter) throws Exception;

        /**
         * unregister a previously registered equipment.
         * 
         * @param uid unique identifier of the equipment.
         * @throws Exception <i>to do</i>.
         */
        void unregister(String uid) throws Exception;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RegistrationInboundPort(ComponentI owner)
            throws Exception {
        super(RegistrationCI.class, owner);
        assert owner instanceof RegistrationImplementorI
                : new AssertionError("owner must implement RegistrationImplementorI");
    }

    public RegistrationInboundPort(String uri, ComponentI owner)
            throws Exception {
        super(uri, RegistrationCI.class, owner);
        assert owner instanceof RegistrationImplementorI
                : new AssertionError("owner must implement RegistrationImplementorI");
    }

    // -------------------------------------------------------------------------
    // Methods
    // -------------------------------------------------------------------------

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#registered(String)
     */
    @Override
    public boolean registered(String uid) throws Exception {
        return this.getOwner().handleRequest(
                o -> ((RegistrationImplementorI) o).registered(uid));
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
        return this.getOwner().handleRequest(
                o -> ((RegistrationImplementorI) o).register(
                        uid, controlPortURI, xmlControlAdapter));
    }

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#unregister(String)
     */
    @Override
    public void unregister(String uid) throws Exception {
        this.getOwner().handleRequest(o -> {
            ((RegistrationImplementorI) o).unregister(uid);
            return null;
        });
    }
}
// -----------------------------------------------------------------------------
