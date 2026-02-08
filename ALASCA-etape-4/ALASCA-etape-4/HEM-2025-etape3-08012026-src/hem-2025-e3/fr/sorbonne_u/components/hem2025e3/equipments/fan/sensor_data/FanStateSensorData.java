package fr.sorbonne_u.components.hem2025e3.equipments.fan.sensor_data;

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

import fr.sorbonne_u.alasca.physical_data.Measure;
import fr.sorbonne_u.alasca.physical_data.SignalData;
import fr.sorbonne_u.components.hem2025e1.equipments.fan.FanUserI.FanSpeed;

// -----------------------------------------------------------------------------
/**
 * The class <code>FanStateSensorData</code> implements a sensor data
 * object containing the speed state of the fan (OFF, LOW, HIGH).
 *
 * <p>
 * <strong>Description</strong>
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
public class FanStateSensorData
        extends SignalData<FanSpeed>
        implements FanSensorDataI {
    private static final long serialVersionUID = 1L;

    /**
     * create a fan state sensor data.
     * 
     * @param speed the current speed of the fan.
     */
    public FanStateSensorData(FanSpeed speed) {
        super(new Measure<FanSpeed>(speed));
    }

    /**
     * return the speed from this sensor data.
     * 
     * @return the fan speed from this sensor data.
     */
    public FanSpeed getSpeed() {
        return this.getMeasure().getData();
    }
}
// -----------------------------------------------------------------------------
