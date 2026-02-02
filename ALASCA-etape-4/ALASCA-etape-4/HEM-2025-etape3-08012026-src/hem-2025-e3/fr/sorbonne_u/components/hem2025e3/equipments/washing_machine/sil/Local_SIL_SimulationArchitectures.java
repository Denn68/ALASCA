package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.*;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.exceptions.PreconditionException;

/**
 * The class <code>Local_SIL_SimulationArchitectures</code> defines the local
 * software-in-the-loop simulation architectures pertaining to the washing machine
 * appliance.
 *
 * <p><strong>Description</strong></p>
 *
 * <p>
 * The class provides static methods that create the local software-in-the-loop
 * real time simulation architectures for the {@code WashingMachineCyPhy} component.
 * </p>
 * <p>
 * In SIL mode, events come directly from the component (via triggerExternalEvent)
 * rather than from a UnitTesterModel.
 * </p>
 */
public abstract class Local_SIL_SimulationArchitectures
{
	/**
	 * Create the local software-in-the-loop simulation architecture for the
	 * {@code WashingMachineCyPhy} component used in unit tests.
	 *
	 * <p><strong>Description</strong></p>
	 *
	 * <p>
	 * In this simulation architecture, the washing machine simulator consists of three
	 * atomic models:
	 * </p>
	 * <ol>
	 * <li>The {@code WashingMachineStateSILModel} keeps track of the state of the
	 *   washing machine. State changes are triggered by events received from the
	 *   {@code WashingMachineCyPhy} component methods; the triggering events are
	 *   reemitted towards the other models.</li>
	 * <li>The {@code WashingMachineElectricitySILModel} keeps track of the electric
	 *   power consumed by the washing machine and exports {@code currentIntensity}
	 *   and {@code currentHeatingPower}.</li>
	 * <li>The {@code WashingMachineTemperatureSILModel} simulates the water temperature
	 *   inside the washing machine, using the heating power provided by the electricity
	 *   model. When target temperature is reached, it emits {@code HeatingFinished}.</li>
	 * </ol>
	 *
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param rootModelURI			URI of the root model in the simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor used to execute in logical time.
	 * @return						the local SIL real time simulation architecture.
	 * @throws Exception			<i>to do</i>.
	 */
	public static RTArchitecture createWashingMachineSIL_Architecture4UnitTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		// Map for atomic model descriptors
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// State Model - simple AtomicModel (no HIOA variables)
		atomicModelDescriptors.put(
				WashingMachineStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineStateSILModel.class,
						WashingMachineStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Electricity Model - HIOA (exports currentIntensity, currentHeatingPower)
		atomicModelDescriptors.put(
				WashingMachineElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineElectricitySILModel.class,
						WashingMachineElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Temperature Model - HIOA (imports currentHeatingPower)
		atomicModelDescriptors.put(
				WashingMachineTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineTemperatureSILModel.class,
						WashingMachineTemperatureSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Map for coupled model descriptors
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		// Submodels
		Set<String> submodels = new HashSet<>();
		submodels.add(WashingMachineStateSILModel.URI);
		submodels.add(WashingMachineElectricitySILModel.URI);
		submodels.add(WashingMachineTemperatureSILModel.URI);

		// Event connections
		Map<EventSource, EventSink[]> connections = new HashMap<>();

		// --- StateModel -> Electricity & Temperature ---

		// SwitchOn -> Elec only (temperature doesn't care)
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOnWashingMachine.class)
				});

		// SwitchOff -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOffWashingMachine.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
				});

		// StartWashing -> Elec & Temp (both need to know to start heating)
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, StartWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		// SetDelayedStart -> Elec only (Elec will emit StartWashing when delay ends)
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SetDelayedStart.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SetDelayedStart.class)
				});

		// Suspend -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SuspendWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SuspendWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SuspendWashing.class)
				});

		// Resume -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, ResumeWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, ResumeWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, ResumeWashing.class)
				});

		// SetPower -> Elec only
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SetPowerWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SetPowerWashingMachine.class)
				});

		// --- Temperature -> Electricity (synchronization events) ---

		// HeatingFinished: Temperature model signals that target temp is reached
		connections.put(
				new EventSource(WashingMachineTemperatureSILModel.URI, HeatingFinished.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, HeatingFinished.class)
				});

		// --- Electricity -> Temperature (synchronization events) ---

		// WashingEnded: Electricity model signals washing duration complete
		connections.put(
				new EventSource(WashingMachineElectricitySILModel.URI, WashingEnded.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, WashingEnded.class)
				});

		// StartWashing from delayed start (Elec emits when delay ends)
		connections.put(
				new EventSource(WashingMachineElectricitySILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		// Variable bindings
		Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

		// currentHeatingPower: Electricity -> Temperature
		bindings.put(
				new VariableSource("currentHeatingPower", Double.class,
								   WashingMachineElectricitySILModel.URI),
				new VariableSink[] {
						new VariableSink("currentHeatingPower", Double.class,
										 WashingMachineTemperatureSILModel.URI)
				});

		// Coupled model descriptor
		coupledModelDescriptors.put(
				rootModelURI,
				new RTCoupledHIOA_Descriptor(
						WashingMachineCoupledModel.class,
						rootModelURI,
						submodels,
						null,
						null,
						connections,
						null,
						null,
						null,
						bindings,
						accelerationFactor));

		// Return architecture
		return new RTArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit,
				accelerationFactor);
	}

	/**
	 * Create the local software-in-the-loop real time simulation architecture
	 * for the {@code WashingMachineCyPhy} component when used in integration tests.
	 *
	 * <p><strong>Description</strong></p>
	 *
	 * <p>
	 * The simulation architecture for integration tests is similar to unit test,
	 * except:
	 * </p>
	 * <ul>
	 * <li>The {@code WashingMachineElectricitySILModel} is moved to the local
	 *   simulator of the {@code ElectricMeterCyPhy} component.</li>
	 * <li>Events are reexported from the coupled model for binding with
	 *   the electricity model in the meter's simulator.</li>
	 * </ul>
	 *
	 * @param architectureURI		URI to be given to the created simulation architecture.
	 * @param rootModelURI			URI of the root model in the simulation architecture.
	 * @param simulatedTimeUnit		simulated time unit used in the architecture.
	 * @param accelerationFactor	acceleration factor.
	 * @return						the local SIL real time simulation architecture.
	 * @throws Exception			<i>to do</i>.
	 */
	public static RTArchitecture createWashingMachineSIL_LocalArchitecture4IntegrationTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && !architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		// Map for atomic model descriptors
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// State Model
		atomicModelDescriptors.put(
				WashingMachineStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineStateSILModel.class,
						WashingMachineStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Temperature Model - HIOA
		atomicModelDescriptors.put(
				WashingMachineTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineTemperatureSILModel.class,
						WashingMachineTemperatureSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Map for coupled model descriptors
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		// Submodels (NO Electricity model - it's in the meter)
		Set<String> submodels = new HashSet<>();
		submodels.add(WashingMachineStateSILModel.URI);
		submodels.add(WashingMachineTemperatureSILModel.URI);

		// Reexported events (for Electricity model in meter's simulator)
		Map<Class<? extends EventI>, ReexportedEvent> reexported = new HashMap<>();

		reexported.put(
				SwitchOnWashingMachine.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									SwitchOnWashingMachine.class));
		reexported.put(
				SwitchOffWashingMachine.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									SwitchOffWashingMachine.class));
		reexported.put(
				StartWashing.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									StartWashing.class));
		reexported.put(
				SetDelayedStart.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									SetDelayedStart.class));
		reexported.put(
				SuspendWashing.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									SuspendWashing.class));
		reexported.put(
				ResumeWashing.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									ResumeWashing.class));
		reexported.put(
				SetPowerWashingMachine.class,
				new ReexportedEvent(WashingMachineStateSILModel.URI,
									SetPowerWashingMachine.class));
		// HeatingFinished from Temperature model
		reexported.put(
				HeatingFinished.class,
				new ReexportedEvent(WashingMachineTemperatureSILModel.URI,
									HeatingFinished.class));

		// Event connections (internal only, to Temperature model)
		Map<EventSource, EventSink[]> connections = new HashMap<>();

		// SwitchOff -> Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
				});

		// StartWashing -> Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		// Suspend -> Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SuspendWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, SuspendWashing.class)
				});

		// Resume -> Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, ResumeWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, ResumeWashing.class)
				});

		// Variable bindings (empty - Electricity model is external)
		Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

		// Coupled model descriptor
		coupledModelDescriptors.put(
				rootModelURI,
				new RTCoupledHIOA_Descriptor(
						WashingMachineCoupledModel.class,
						rootModelURI,
						submodels,
						null,
						reexported,
						connections,
						null,
						null,
						null,
						bindings,
						accelerationFactor));

		// Return architecture
		return new RTArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit,
				accelerationFactor);
	}
}
