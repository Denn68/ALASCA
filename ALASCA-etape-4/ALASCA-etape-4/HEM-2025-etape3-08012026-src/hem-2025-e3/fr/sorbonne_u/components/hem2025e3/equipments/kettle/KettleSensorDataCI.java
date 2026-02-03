package fr.sorbonne_u.components.hem2025e3.equipments.kettle;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.HeatingSensorData;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sensor_data.KettleTemperatureSensorData;
import fr.sorbonne_u.components.interfaces.DataOfferedCI;
import fr.sorbonne_u.components.interfaces.DataRequiredCI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

// -----------------------------------------------------------------------------
/**
 * The component data interface <code>KettleSensorDataCI</code> declares the
 * pull
 * and the push interfaces to get and receive sensor data from the kettle
 * component.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * BCM4Java data oriented interfaces provide means to communicate between
 * components to exchange data rather than calling services. The kettle
 * controller uses this interface to get the current water temperature
 * and heating status from the kettle component.
 * </p>
 * <p>
 * In the pull mode, the controller calls the kettle to get the water
 * temperature.
 * In the push mode, the kettle periodically sends temperature data to the
 * controller.
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
 * Created on : 2026-02-03
 * </p>
 * 
 * @author Team DeMoh
 */
public interface KettleSensorDataCI
        extends DataOfferedCI,
        DataRequiredCI {
    // -------------------------------------------------------------------------
    // Inner interfaces and types
    // -------------------------------------------------------------------------

    /**
     * The component interface <code>KettleSensorCI</code> declares the common
     * services used in pull mode to get the sensor data from the kettle.
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
     * Created on : 2026-02-03
     * </p>
     * 
     * @author Team DeMoh
     */
    public static interface KettleSensorCI
            extends OfferedCI,
            RequiredCI {
        // ---------------------------------------------------------------------
        // Methods
        // ---------------------------------------------------------------------

        /**
         * return true if the kettle is currently heating.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * on()
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @return true if the kettle is currently heating.
         * @throws Exception <i>to do</i>.
         */
        public HeatingSensorData heatingPullSensor()
                throws Exception;

        /**
         * return the current water temperature measured by the kettle.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * on()
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @return the current water temperature measured by the kettle.
         * @throws Exception <i>to do</i>.
         */
        public KettleTemperatureSensorData currentTemperaturePullSensor()
                throws Exception;

        /**
         * start a sequence of temperature pushes with the given period.
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * controlPeriod > 0
         * }
         * pre	{@code
         * tu != null
         * }
         * post	{@code
         * true
         * }	// no postcondition.
         * </pre>
         *
         * @param controlPeriod period at which the pushes must be made.
         * @param tu            time unit in which {@code controlPeriod} is expressed.
         * @throws Exception <i>to do</i>.
         */
        public void startTemperaturePushSensor(
                long controlPeriod,
                TimeUnit tu) throws Exception;
    }

    /**
     * The interface <code>KettleSensorRequiredPullCI</code> is the pull
     * interface that a client component must require to call the outbound port.
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
     * Created on : 2026-02-03
     * </p>
     * 
     * @author Team DeMoh
     */
    public static interface KettleSensorRequiredPullCI
            extends DataRequiredCI.PullCI,
            KettleSensorCI {
    }

    /**
     * The interface <code>KettleSensorOfferedPullCI</code> is the pull
     * interface that a server component must offer to be called the inbound
     * port.
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
     * Created on : 2026-02-03
     * </p>
     * 
     * @author Team DeMoh
     */
    public static interface KettleSensorOfferedPullCI
            extends DataOfferedCI.PullCI,
            KettleSensorCI {
    }
}
// -----------------------------------------------------------------------------
