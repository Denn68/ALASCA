package fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.sil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineUnitTesterModel;
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

public abstract class Local_SIL_SimulationArchitectures
{
	/**
	 * create the SIL simulation architecture for the washing machine.
	 */
	public static RTArchitecture createWashingMachineSILArchitecture(
		String architectureURI, 
		String rootModelURI,
		TimeUnit simulatedTimeUnit, 
		double accelerationFactor
		) throws Exception 
	{
		// 1. Create Atomic Model Descriptors
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

		// Electricity Model (HIOA)
		atomicModelDescriptors.put(
				WashingMachineElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineElectricitySILModel.class,
						WashingMachineElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Temperature Model
		atomicModelDescriptors.put(
				WashingMachineTemperatureSILModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineTemperatureSILModel.class,
						WashingMachineTemperatureSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// Unit Tester Model (Added because it exists in your files)
		atomicModelDescriptors.put(
				WashingMachineUnitTesterModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineUnitTesterModel.class,
						WashingMachineUnitTesterModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		// 2. Create Coupled Model Descriptors Container
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		// 3. Define Submodels
		Set<String> submodels = new HashSet<>();
		submodels.add(WashingMachineStateSILModel.URI);
		submodels.add(WashingMachineElectricitySILModel.URI);
		submodels.add(WashingMachineTemperatureSILModel.URI);
		submodels.add(WashingMachineUnitTesterModel.URI);

		// 4. Define Event Connections
		Map<EventSource, EventSink[]> connections = new HashMap<>();

		// --- Connection: UnitTester -> StateModel ---
		// The tester sends events to the state model to drive the simulation
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, StartWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, SetDelayedStart.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, SetDelayedStart.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, SuspendWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, SuspendWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, ResumeWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, ResumeWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineUnitTesterModel.URI, SetPowerWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineStateSILModel.URI, SetPowerWashingMachine.class)
				});

		// --- Connection: StateModel -> Electricity & Temperature ---
		// The state model re-emits events to update physics
		
		// SwitchOn -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOnWashingMachine.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOnWashingMachine.class)
				});
		
		// SwitchOff -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOffWashingMachine.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
				});

		// StartWashing -> Elec & Temp
		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, StartWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
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

		// 5. Variable Bindings (Empty for now as there are no shared variables between these specific models)
		Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

		// 6. Put Coupled Model Descriptor
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

		// 7. Return Architecture
		return new RTArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit,
				accelerationFactor);
	}
}