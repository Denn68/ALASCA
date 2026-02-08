package fr.sorbonne_u.components.hem2025e3.equipments.vacuum_cleaner.sil;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.VacuumCleanerCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
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
 * software-in-the-loop simulation architectures pertaining to the vacuum
 * cleaner appliance.
 *
 * <p>Created on : 2026-06-06</p>
 * 
 * @author	Team
 */
public abstract class	Local_SIL_SimulationArchitectures
{
	/**
	 * create the local SIL real time simulation architecture for the
	 * {@code VacuumCleanerCyPhy} component when performing SIL unit tests.
	 */
	public static RTArchitecture
					createVacuumCleanerSIL_Architecture4UnitTest(
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
				VacuumCleanerElectricitySILModel.URI,
				RTAtomicHIOA_Descriptor.create(
						VacuumCleanerElectricitySILModel.class,
						VacuumCleanerElectricitySILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));
		atomicModelDescriptors.put(
				VacuumCleanerStateSILModel.URI,
				RTAtomicModelDescriptor.create(
						VacuumCleanerStateSILModel.class,
						VacuumCleanerStateSILModel.URI,
						simulatedTimeUnit,
						null,
						accelerationFactor));

		Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

		Set<String> submodels = new HashSet<String>();
		submodels.add(VacuumCleanerElectricitySILModel.URI);
		submodels.add(VacuumCleanerStateSILModel.URI);

		Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();

		connections.put(
			new EventSource(VacuumCleanerStateSILModel.URI,
							SwitchOnVacuumCleaner.class),
			new EventSink[] {
				new EventSink(VacuumCleanerElectricitySILModel.URI,
							  SwitchOnVacuumCleaner.class)
			});
		connections.put(
			new EventSource(VacuumCleanerStateSILModel.URI,
							SwitchOffVacuumCleaner.class),
			new EventSink[] {
				new EventSink(VacuumCleanerElectricitySILModel.URI,
							  SwitchOffVacuumCleaner.class)
			});
		connections.put(
			new EventSource(VacuumCleanerStateSILModel.URI,
							SetHighVacuumCleaner.class),
			new EventSink[] {
				new EventSink(VacuumCleanerElectricitySILModel.URI,
							  SetHighVacuumCleaner.class)
			});
		connections.put(
			new EventSource(VacuumCleanerStateSILModel.URI,
							SetLowVacuumCleaner.class),
			new EventSink[] {
				new EventSink(VacuumCleanerElectricitySILModel.URI,
							  SetLowVacuumCleaner.class)
			});

		coupledModelDescriptors.put(
				rootModelURI,
				new RTCoupledModelDescriptor(
						VacuumCleanerCoupledModel.class,
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
	 * {@code VacuumCleanerCyPhy} component for integration tests.
	 */
	public static RTArchitecture
					createVacuumCleanerSIL_Architecture4IntegrationTest(
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
						VacuumCleanerStateSILModel.class,
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
