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


public abstract class Local_SIL_SimulationArchitectures
{

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

		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(
				WashingMachineStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineStateSILModel.class,
						WashingMachineStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		atomicModelDescriptors.put(
				WashingMachineElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineElectricitySILModel.class,
						WashingMachineElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		atomicModelDescriptors.put(
				WashingMachineTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineTemperatureSILModel.class,
						WashingMachineTemperatureSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<>();
		submodels.add(WashingMachineStateSILModel.URI);
		submodels.add(WashingMachineElectricitySILModel.URI);
		submodels.add(WashingMachineTemperatureSILModel.URI);

		Map<EventSource, EventSink[]> connections = new HashMap<>();

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOnWashingMachine.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOnWashingMachine.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SwitchOffWashingMachine.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, StartWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SetDelayedStart.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SetDelayedStart.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SuspendWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SuspendWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, SuspendWashing.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, ResumeWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, ResumeWashing.class),
						new EventSink(WashingMachineTemperatureSILModel.URI, ResumeWashing.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SetPowerWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, SetPowerWashingMachine.class)
				});

		connections.put(
				new EventSource(WashingMachineTemperatureSILModel.URI, HeatingFinished.class),
				new EventSink[] {
						new EventSink(WashingMachineElectricitySILModel.URI, HeatingFinished.class)
				});

		connections.put(
				new EventSource(WashingMachineElectricitySILModel.URI, WashingEnded.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, WashingEnded.class)
				});

		connections.put(
				new EventSource(WashingMachineElectricitySILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

		bindings.put(
				new VariableSource("currentHeatingPower", Double.class,
								   WashingMachineElectricitySILModel.URI),
				new VariableSink[] {
						new VariableSink("currentHeatingPower", Double.class,
										 WashingMachineTemperatureSILModel.URI)
				});

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

		return new RTArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit,
				accelerationFactor);
	}

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

		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		atomicModelDescriptors.put(
				WashingMachineStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						WashingMachineStateSILModel.class,
						WashingMachineStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		atomicModelDescriptors.put(
				WashingMachineTemperatureSILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						WashingMachineTemperatureSILModel.class,
						WashingMachineTemperatureSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		Set<String> submodels = new HashSet<>();
		submodels.add(WashingMachineStateSILModel.URI);
		submodels.add(WashingMachineTemperatureSILModel.URI);

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
		reexported.put(
				HeatingFinished.class,
				new ReexportedEvent(WashingMachineTemperatureSILModel.URI,
									HeatingFinished.class));

		Map<EventSource, EventSink[]> connections = new HashMap<>();

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOnWashingMachine.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, SwitchOffWashingMachine.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, StartWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, StartWashing.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, SuspendWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, SuspendWashing.class)
				});

		connections.put(
				new EventSource(WashingMachineStateSILModel.URI, ResumeWashing.class),
				new EventSink[] {
						new EventSink(WashingMachineTemperatureSILModel.URI, ResumeWashing.class)
				});

		Map<VariableSource, VariableSink[]> bindings = new HashMap<>();

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

		return new RTArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit,
				accelerationFactor);
	}
}
