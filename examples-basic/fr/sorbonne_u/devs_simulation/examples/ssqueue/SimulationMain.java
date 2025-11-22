package fr.sorbonne_u.devs_simulation.examples.ssqueue;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.AbstractEventConverter;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

//----------------------------------------------------------------------------
/**
 * The class <code>SimulationMain</code> runs a simulation for the single-server
 * queue example.
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
 * <p>Created on : 2018-07-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationMain
{
	public static void	main(String[] args)
	{
		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();
			atomicModelDescriptors.put(
					ServerModel.URI,
					AtomicModelDescriptor.create(
							ServerModel.class,
							ServerModel.URI,
							TimeUnit.SECONDS,
							null));
			atomicModelDescriptors.put(
					ClientGeneratorModel.URI,
					AtomicModelDescriptor.create(
							ClientGeneratorModel.class,
							ClientGeneratorModel.URI,
							TimeUnit.SECONDS,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
							new HashMap<String,CoupledModelDescriptor>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(ServerModel.URI);
			submodels.add(ClientGeneratorModel.URI);
			Map<Class<? extends EventI>,EventSink[]> imported =
						new HashMap<Class<? extends EventI>,EventSink[]>();
			Map<Class<? extends EventI>,ReexportedEvent> reexported =
					new HashMap<Class<? extends EventI>,ReexportedEvent>();
			EventSource from =
					new EventSource(ClientGeneratorModel.URI,
									GeneratedClientArrival.class,
									GeneratedClientArrival.class);
			EventSink[] to =
					new EventSink[] {
						new EventSink(
								ServerModel.URI,
								GeneratedClientArrival.class,
								ServerSideClientArrival.class,
								new AbstractEventConverter(
										GeneratedClientArrival.class,
										ServerSideClientArrival.class)
								{
									private static final long
														serialVersionUID = 1L;

									@Override
									public EventI convert(EventI e) {
										return new ServerSideClientArrival(
												e.getTimeOfOccurrence(),
												e.getEventInformation());
									}
									
								})};
			Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
			connections.put(from, to);
			coupledModelDescriptors.put(
				SSQueueCoupledES_Model.URI,
				new CoupledModelDescriptor(
						SSQueueCoupledES_Model.class,
						SSQueueCoupledES_Model.URI,
						submodels,
						imported,
						reexported,
						connections,
						null));

			ArchitectureI architecture =
					new Architecture(
							SSQueueCoupledES_Model.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.SECONDS);

			SimulatorI se = architecture.constructSimulator();
			se.doStandAloneSimulation(0.0, 100.0);
			System.out.println(se.getFinalReport());
			System.out.println("Simulation ends.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
//----------------------------------------------------------------------------
