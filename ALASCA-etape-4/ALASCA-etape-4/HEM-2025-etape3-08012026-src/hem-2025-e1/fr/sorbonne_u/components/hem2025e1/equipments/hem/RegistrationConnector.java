package fr.sorbonne_u.components.hem2025e1.equipments.hem;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law.

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.hem2025.bases.RegistrationCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RegistrationConnector</code> implements a connector for
 * the {@code RegistrationCI} component interface.
 *
 * <p>
 * Created on : 2025-11-25
 * </p>
 * 
 * @author Team DeMoh
 */
public class RegistrationConnector
        extends AbstractConnector
        implements RegistrationCI {
    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#registered(String)
     */
    @Override
    public boolean registered(String uid) throws Exception {
        return ((RegistrationCI) this.offering).registered(uid);
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
        return ((RegistrationCI) this.offering)
                .register(uid, controlPortURI, xmlControlAdapter);
    }

    /**
     * @see fr.sorbonne_u.components.hem2025.bases.RegistrationCI#unregister(String)
     */
    @Override
    public void unregister(String uid) throws Exception {
        ((RegistrationCI) this.offering).unregister(uid);
    }
}
// -----------------------------------------------------------------------------
