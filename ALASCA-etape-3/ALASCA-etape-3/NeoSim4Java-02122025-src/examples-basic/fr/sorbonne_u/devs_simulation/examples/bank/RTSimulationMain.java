package fr.sorbonne_u.devs_simulation.examples.bank;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a new
// implementation of the DEVS simulation <i>de facto</i> standard for Java.
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTCoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTSimulationMain</code> runs the bank example simulation in
 * global real time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2022-09-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTSimulationMain
{
	public static void main(String[] args)
	{
		try {
			final String cgURI = "MyClientGenerator";
			final String bankURI = "MyBank";
			final String coupledModelURI = "BankExampleCoupledModel";

			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					bankURI,
					RTAtomicModelDescriptor.create(
							BankModel.class,
							bankURI,
							TimeUnit.SECONDS,
							null));
			atomicModelDescriptors.put(
					cgURI,
					RTAtomicModelDescriptor.create(
							ClientGenerator.class,
							cgURI,
							TimeUnit.SECONDS,
							null));

			Set<String> submodelURIs = new HashSet<String>();
			submodelURIs.add(bankURI);
			submodelURIs.add(cgURI);

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
															new HashMap<>();

			HashMap<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
			EventSource from = new EventSource(cgURI, Arrival.class);
			EventSink[] to =
					new EventSink[] {
							new EventSink(bankURI, Arrival.class)
					};
			connections.put(from, to);

			coupledModelDescriptors.put(
				coupledModelURI,
				new RTCoupledModelDescriptor(
						BankCoupledModel.class,
						coupledModelURI,
						submodelURIs,
						new HashMap<Class<? extends EventI>,EventSink[]>(),
						new HashMap<Class<? extends EventI>,ReexportedEvent>(),
						connections,
						null));

			ArchitectureI architecture =
				new RTArchitecture(
						coupledModelURI,
						atomicModelDescriptors,
						coupledModelDescriptors,
						TimeUnit.SECONDS);

			SimulatorI se = architecture.constructSimulator();

			Map<String,Object> simParams = new HashMap<>();
			simParams.put(
					ModelI.createRunParameterName(
							cgURI,
							ClientGenerator.MEAN_INTERARRIVAL_TIME_NAME),
					3.0);
			simParams.put(
					ModelI.createRunParameterName(
							bankURI,
							BankModel.MEAN_SERVICE_TIME_NAME),
					2.5);
			se.setSimulationRunParameters(simParams);

			Semaphore endSimulationSynchroniser =
					new Semaphore(atomicModelDescriptors.size());
			endSimulationSynchroniser.acquire(atomicModelDescriptors.size());
			((RTSimulatorI)se).setSimulationEndSynchroniser(
												endSimulationSynchroniser);
			long currentAtStart = System.currentTimeMillis();
			long start = currentAtStart + 100;
			double simulationDuration = 40.0;
			((CoordinationRTEngine)se).startRTSimulation(
											start, 0.0, simulationDuration);
			endSimulationSynchroniser.acquire();
			System.out.println("simulation duration: " + simulationDuration);
			System.out.println("execution duration: " + 
								(System.currentTimeMillis() - currentAtStart));
			System.out.println(se.getFinalReport());
			System.out.println("Simulation ends.");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
