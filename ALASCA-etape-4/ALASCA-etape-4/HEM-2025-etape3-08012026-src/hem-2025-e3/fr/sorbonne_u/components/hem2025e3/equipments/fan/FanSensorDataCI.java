package fr.sorbonne_u.components.hem2025e3.equipments.fan;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to implement a mock-up
// of household energy management system.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.  You can use,
// modify and/ or redistribute the software under the terms of the
// CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
// URL "http://www.cecill.info".
//
// As a counterpart to the access to the source code and  rights to copy,
// modify and redistribute granted by the license, users are provided only
// with a limited warranty  and the software's author,  the holder of the
// economic rights,  and the successive licensors  have only  limited
// liability. 
//
// In this respect, the user's attention is drawn to the risks associated
// with loading,  using,  modifying and/or developing or reproducing the
// software by the user in light of its specific status of free software,
// that may mean  that it is complicated to manipulate,  and  that  also
// therefore means  that it is reserved for developers  and  experienced
// professionals having in-depth computer knowledge. Users are therefore
// encouraged to load and test the software's suitability as regards their
// requirements in conditions enabling the security of their systems and/or 
// data to be ensured and,  more generally, to use and operate it in the 
// same conditions as regards security. 
//
// The fact that you are presently reading this means that you have had
// knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data.FanStateSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component interface <code>FanSensorDataCI</code> declares the signatures
 * of the sensor data methods to be used with the fan component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The fan sensor data provides the current speed state of the fan (OFF, LOW,
 * HIGH) through pull and push sensor interfaces.
 * </p>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * Created on : 2026-06-06
 * </p>
 * 
 * @author Team
 */
public interface FanSensorDataCI
        extends DataOfferedCI,
        DataRequiredCI {
    public static interface FanSensorCI
            extends OfferedCI,
            RequiredCI {
        public FanStateSensorData speedPullSensor() throws Exception;

        public void startSpeedPushSensor(
                long controlPeriod,
                TimeUnit tu) throws Exception;
    }

    public static interface FanSensorRequiredPullCI
            extends DataRequiredCI.PullCI,
            FanSensorCI {
    }

    public static interface FanSensorOfferedPullCI
            extends DataOfferedCI.PullCI,
            FanSensorCI {
    }
}
// -----------------------------------------------------------------------------
