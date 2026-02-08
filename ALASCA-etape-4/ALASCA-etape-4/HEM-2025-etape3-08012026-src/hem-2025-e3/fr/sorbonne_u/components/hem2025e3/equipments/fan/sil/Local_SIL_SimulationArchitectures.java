package fr.sorbonne_u.components.hem2025e3.equipments.fan.sil;

// Copyright Jacques Malenfant, Sorbonne Universite.
//
// This software is governed by the CeCILL-C license under French law and
// abiding by the rules of distribution of free software.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.FanCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Local_SIL_SimulationArchitectures</code> defines the local
 * software-in-the-loop simulation architectures pertaining to the fan
 * appliance.
 *
 * <p>Created on : 2026-06-06</p>
 * @author	Team
 */
public abstract class	Local_SIL_SimulationArchitectures
{
	/**
	 * create the local SIL real time simulation architecture for the
	 * {@code FanCyPhy} component when performing SIL unit tests.
	 */
	public static RTArchitecture	createFanSIL_Architecture4UnitTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && "
						+ "!architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				FanElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						FanElectricitySILModel.class,
						FanElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				FanStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						FanStateSILModel.class,
						FanStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(FanElectricitySILModel.URI);
		submodels.add(FanStateSILModel.URI);

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
			new EventSource(FanStateSILModel.URI,
							SwitchOnFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							  SwitchOnFan.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SwitchOffFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							  SwitchOffFan.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SetHighSpeedFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							  SetHighSpeedFan.class)
			});
		connections.put(
			new EventSource(FanStateSILModel.URI,
							SetLowSpeedFan.class),
			new EventSink[] {
				new EventSink(FanElectricitySILModel.URI,
							  SetLowSpeedFan.class)
			});

		coupledModelDescriptors.put(
				rootModelURI,
				new RTCoupledModelDescriptor(
						FanCoupledModel.class,
						rootModelURI,
						submodels,
						null,
						null,
						connections,
						null,
						accelerationFactor));

		RTArchitecture architecture =
				new RTArchitecture(
						architectureURI,
						rootModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						simulatedTimeUnit,
						accelerationFactor);

		return architecture;
	}

	/**
	 * create the local SIL real time simulation architecture for the
	 * {@code FanCyPhy} component for integration tests.
	 */
	public static RTArchitecture	createFanSIL_Architecture4IntegrationTest(
		String architectureURI,
		String rootModelURI,
		TimeUnit simulatedTimeUnit,
		double accelerationFactor
		) throws Exception
	{
		assert	architectureURI != null && !architectureURI.isEmpty() :
				new PreconditionException(
						"architectureURI != null && "
						+ "!architectureURI.isEmpty()");
		assert	rootModelURI != null && !rootModelURI.isEmpty() :
				new PreconditionException(
						"rootModelURI != null && !rootModelURI.isEmpty()");
		assert	simulatedTimeUnit != null :
				new PreconditionException("simulatedTimeUnit != null");
		assert	accelerationFactor > 0.0 :
				new PreconditionException("accelerationFactor > 0.0");

		Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

		atomicModelDescriptors.put(
				rootModelURI,
				RTAtomicModelDescriptor.create(
						FanStateSILModel.class,
						rootModelURI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		RTArchitecture architecture =
				new RTArchitecture(
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
