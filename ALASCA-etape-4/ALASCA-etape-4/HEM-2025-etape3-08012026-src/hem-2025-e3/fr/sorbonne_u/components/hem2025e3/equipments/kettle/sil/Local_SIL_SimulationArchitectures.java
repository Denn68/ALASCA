package fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StartKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StopKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events.SIL_SetPowerKettle;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Local_SIL_SimulationArchitectures</code> defines the local
 * software-in-the-loop simulation architectures pertaining to the kettle
 * appliance.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * The class provides static methods that create the local software-in-the-loop
 * real time simulation architectures for the {@code KettleCyPhy} component. The
 * overall simulation architecture for the kettle appliance includes three
 * atomic models:
 * </p>
 * <ol>
 * <li>The {@code KettleStateSILModel} keeps track of the state (switched on,
 * switched off, heating, keeping warm) of the kettle. The state changes are
 * triggered by the reception of external events directly received from the
 * {@code KettleCyPhy} component methods.</li>
 * <li>The {@code KettleElectricitySILModel} keeps track of the electric power
 * consumed by the kettle in a variable <code>currentIntensity</code>.</li>
 * <li>The {@code KettleTemperatureSILModel} simulates the water temperature
 * inside the kettle.</li>
 * </ol>
 * 
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
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
public abstract class Local_SIL_SimulationArchitectures {
        /**
         * create the local software-in-the-loop simulation architecture for the
         * {@code KettleCyPhy} component used in unit tests.
         * 
         * <p>
         * <strong>Description</strong>
         * </p>
         * 
         * <p>
         * In this simulation architecture, the kettle simulator consists of three
         * atomic models:
         * </p>
         * <ol>
         * <li>The {@code KettleStateSILModel} keeps track of the state of the kettle
         * (OFF, ON, HEATING, KEEP_WARM). State changes are triggered by events
         * from the {@code KettleCyPhy} component.</li>
         * <li>The {@code KettleElectricitySILModel} keeps track of the electric power
         * consumed by the kettle.</li>
         * <li>The {@code KettleTemperatureSILModel} simulates the water temperature
         * inside the kettle.</li>
         * </ol>
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * architectureURI != null && !architectureURI.isEmpty()
         * }
         * pre	{@code
         * rootModelURI != null && !rootModelURI.isEmpty()
         * }
         * pre	{@code
         * simulatedTimeUnit != null
         * }
         * pre	{@code
         * accelerationFactor > 0.0
         * }
         * post	{@code return != null}
         * post {@code return.getArchitectureURI().equals(architectureURI)}
         * post	{@code return.getRootModelURI().equals(rootModelURI)}
         * post	{@code return.getSimulationTimeUnit().equals(simulatedTimeUnit)}
         * </pre>
         *
         * @param architectureURI    URI to be given to the created simulation
         *                           architecture.
         * @param rootModelURI       URI of the root model in the simulation
         *                           architecture.
         * @param simulatedTimeUnit  simulated time unit used in the architecture.
         * @param accelerationFactor acceleration factor used to execute in a logical
         *                           time speeding up the real time.
         * @return the local software-in-the-loop real time simulation architecture for
         *         the unit tests of the {@code Kettle} component.
         * @throws Exception <i>to do</i>.
         */
        public static RTArchitecture createKettleSIL_Architecture4UnitTest(
                        String architectureURI,
                        String rootModelURI,
                        TimeUnit simulatedTimeUnit,
                        double accelerationFactor) throws Exception {
                assert architectureURI != null && !architectureURI.isEmpty() : new PreconditionException(
                                "architectureURI != null && !architectureURI.isEmpty()");
                assert rootModelURI != null && !rootModelURI.isEmpty() : new PreconditionException(
                                "rootModelURI != null && !rootModelURI.isEmpty()");
                assert simulatedTimeUnit != null : new PreconditionException("simulatedTimeUnit != null");
                assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

                // map that will contain the atomic model descriptors to construct
                // the simulation architecture
                Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

                // the kettle state model only exchanges events, an atomic model
                // hence we use an AtomicModelDescriptor
                atomicModelDescriptors.put(
                                KettleStateSILModel.URI,
                                RTAtomicModelDescriptor.create(
                                                KettleStateSILModel.class,
                                                KettleStateSILModel.URI,
                                                simulatedTimeUnit,
                                                null,
                                                accelerationFactor));
                // the kettle temperature model is atomic HIOA model
                atomicModelDescriptors.put(
                                KettleTemperatureSILModel.URI,
                                RTAtomicHIOA_Descriptor.create(
                                                KettleTemperatureSILModel.class,
                                                KettleTemperatureSILModel.URI,
                                                simulatedTimeUnit,
                                                null,
                                                accelerationFactor));
                // the kettle electricity model is atomic HIOA model
                atomicModelDescriptors.put(
                                KettleElectricitySILModel.URI,
                                RTAtomicHIOA_Descriptor.create(
                                                KettleElectricitySILModel.class,
                                                KettleElectricitySILModel.URI,
                                                simulatedTimeUnit,
                                                null,
                                                accelerationFactor));

                // map that will contain the coupled model descriptors to construct
                // the simulation architecture
                Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

                // the set of submodels of the coupled model, given by their URIs
                Set<String> submodels = new HashSet<String>();
                submodels.add(KettleStateSILModel.URI);
                submodels.add(KettleTemperatureSILModel.URI);
                submodels.add(KettleElectricitySILModel.URI);

                // event exchanging connections between exporting and importing models
                Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SwitchOnKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SwitchOnKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                SwitchOnKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SIL_SetPowerKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SIL_SetPowerKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                SIL_SetPowerKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SwitchOffKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SwitchOffKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                SwitchOffKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, HeatKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                HeatKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                HeatKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, DoNotHeatKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                DoNotHeatKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                DoNotHeatKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, StartKeepingWarmKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                StartKeepingWarmKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                StartKeepingWarmKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, StopKeepingWarmKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                StopKeepingWarmKettle.class),
                                                new EventSink(KettleElectricitySILModel.URI,
                                                                StopKeepingWarmKettle.class)
                                });

                // coupled model descriptor
                coupledModelDescriptors.put(
                                rootModelURI,
                                new RTCoupledHIOA_Descriptor(
                                                KettleCoupledModel.class,
                                                rootModelURI,
                                                submodels,
                                                null,
                                                null,
                                                connections,
                                                null,
                                                null,
                                                null,
                                                null,
                                                accelerationFactor));

                // simulation architecture
                RTArchitecture architecture = new RTArchitecture(
                                architectureURI,
                                rootModelURI,
                                atomicModelDescriptors,
                                coupledModelDescriptors,
                                simulatedTimeUnit,
                                accelerationFactor);

                return architecture;
        }

        /**
         * create the local software-in-the-loop real time simulation architecture
         * for the {@code KettleCyPhy} component when used in integration tests.
         * 
         * <p>
         * <strong>Description</strong>
         * </p>
         * 
         * <p>
         * The simulation architecture created for {@code KettleCyPhy} real time
         * integration tests is similar to the one used for unit test, except:
         * </p>
         * <ul>
         * <li>The {@code KettleElectricitySILModel} is moved to the local simulator
         * of the {@code ElectricMeterCyPhy} component.</li>
         * <li>The state changes events are reexported to be received by the
         * electricity model in the electric meter.</li>
         * </ul>
         * 
         * <p>
         * <strong>Contract</strong>
         * </p>
         * 
         * <pre>
         * pre	{@code
         * architectureURI != null && !architectureURI.isEmpty()
         * }
         * pre	{@code
         * rootModelURI != null && !rootModelURI.isEmpty()
         * }
         * pre	{@code
         * simulatedTimeUnit != null
         * }
         * pre	{@code
         * accelerationFactor > 0.0
         * }
         * post	{@code return != null}
         * post {@code return.getArchitectureURI().equals(architectureURI)}
         * post	{@code return.getRootModelURI().equals(rootModelURI)}
         * post	{@code return.getSimulationTimeUnit().equals(simulatedTimeUnit)}
         * </pre>
         *
         * @param architectureURI    URI to be given to the created simulation
         *                           architecture.
         * @param rootModelURI       URI of the root model in the simulation
         *                           architecture.
         * @param simulatedTimeUnit  simulated time unit used in the architecture.
         * @param accelerationFactor acceleration factor used to execute in a logical
         *                           time speeding up the real time.
         * @return the local SIL real time simulation architecture for the integration
         *         tests of the {@code Kettle} component.
         * @throws Exception <i>to do</i>.
         */
        public static RTArchitecture createKettle_SIL_LocalArchitecture4IntegrationTest(
                        String architectureURI,
                        String rootModelURI,
                        TimeUnit simulatedTimeUnit,
                        double accelerationFactor) throws Exception {
                assert architectureURI != null && !architectureURI.isEmpty() : new PreconditionException(
                                "architectureURI != null && !architectureURI.isEmpty()");
                assert rootModelURI != null && !rootModelURI.isEmpty() : new PreconditionException(
                                "rootModelURI != null && !rootModelURI.isEmpty()");
                assert simulatedTimeUnit != null : new PreconditionException("simulatedTimeUnit != null");
                assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

                // map that will contain the atomic model descriptors to construct
                // the simulation architecture
                Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

                atomicModelDescriptors.put(
                                KettleStateSILModel.URI,
                                RTAtomicModelDescriptor.create(
                                                KettleStateSILModel.class,
                                                KettleStateSILModel.URI,
                                                simulatedTimeUnit,
                                                null,
                                                accelerationFactor));
                atomicModelDescriptors.put(
                                KettleTemperatureSILModel.URI,
                                RTAtomicHIOA_Descriptor.create(
                                                KettleTemperatureSILModel.class,
                                                KettleTemperatureSILModel.URI,
                                                simulatedTimeUnit,
                                                null,
                                                accelerationFactor));

                // map that will contain the coupled model descriptors to construct
                // the simulation architecture
                Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

                // the set of submodels of the coupled model, given by their URIs
                Set<String> submodels = new HashSet<String>();
                submodels.add(KettleStateSILModel.URI);
                submodels.add(KettleTemperatureSILModel.URI);

                // events emitted by submodels that are reexported towards other models
                Map<Class<? extends EventI>, ReexportedEvent> reexported = new HashMap<Class<? extends EventI>, ReexportedEvent>();

                reexported.put(
                                SwitchOnKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                SwitchOnKettle.class));
                reexported.put(
                                SIL_SetPowerKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                SIL_SetPowerKettle.class));
                reexported.put(
                                SwitchOffKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                SwitchOffKettle.class));
                reexported.put(
                                HeatKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                HeatKettle.class));
                reexported.put(
                                DoNotHeatKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                DoNotHeatKettle.class));
                reexported.put(
                                StartKeepingWarmKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                StartKeepingWarmKettle.class));
                reexported.put(
                                StopKeepingWarmKettle.class,
                                new ReexportedEvent(KettleStateSILModel.URI,
                                                StopKeepingWarmKettle.class));

                // event exchanging connections between exporting and importing models
                Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SwitchOnKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SwitchOnKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SIL_SetPowerKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SIL_SetPowerKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI,
                                                SwitchOffKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                SwitchOffKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, HeatKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                HeatKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, DoNotHeatKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                DoNotHeatKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, StartKeepingWarmKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                StartKeepingWarmKettle.class)
                                });
                connections.put(
                                new EventSource(KettleStateSILModel.URI, StopKeepingWarmKettle.class),
                                new EventSink[] {
                                                new EventSink(KettleTemperatureSILModel.URI,
                                                                StopKeepingWarmKettle.class)
                                });

                // coupled model descriptor
                coupledModelDescriptors.put(
                                rootModelURI,
                                new RTCoupledHIOA_Descriptor(
                                                KettleCoupledModel.class,
                                                rootModelURI,
                                                submodels,
                                                null,
                                                reexported,
                                                connections,
                                                null,
                                                null,
                                                null,
                                                null,
                                                accelerationFactor));

                // simulation architecture
                RTArchitecture architecture = new RTArchitecture(
                                architectureURI,
                                rootModelURI,
                                atomicModelDescriptors,
                                coupledModelDescriptors,
                                simulatedTimeUnit,
                                accelerationFactor);

                return architecture;
        }
}
// -----------------------------------------------------------------------------
