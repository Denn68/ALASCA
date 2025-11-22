package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetHighVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetLowVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SetMediumVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOffVacuumCleaner;
import fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events.SwitchOnVacuumCleaner;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunVacuumCleanerSimpleUserMILSimulation</code> runs a unit
 * simulation of the vacuum cleaner with a simple user model.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * @author	Team DeMoh
 */
public class			RunVacuumCleanerSimpleUserMILSimulation
{
	public static void	main(String[] args)
	{
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// the vacuum cleaner model simulating its electricity consumption, an
			// atomic HIOA model hence we use an AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					VacuumCleanerElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							VacuumCleanerElectricityModel.class,
							VacuumCleanerElectricityModel.URI,
							TimeUnit.HOURS,
							null));
			// for atomic model, we use an AtomicModelDescriptor
			atomicModelDescriptors.put(
					VacuumCleanerSimpleUserModel.URI,
					AtomicModelDescriptor.create(
							VacuumCleanerSimpleUserModel.class,
							VacuumCleanerSimpleUserModel.URI,
							TimeUnit.HOURS,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(VacuumCleanerElectricityModel.URI);
			submodels.add(VacuumCleanerSimpleUserModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(VacuumCleanerSimpleUserModel.URI,
									SwitchOnVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SwitchOnVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerSimpleUserModel.URI,
									SwitchOffVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SwitchOffVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerSimpleUserModel.URI,
									SetHighVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetHighVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerSimpleUserModel.URI,
									SetMediumVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetMediumVacuumCleaner.class)
					});
			connections.put(
					new EventSource(VacuumCleanerSimpleUserModel.URI,
									SetLowVacuumCleaner.class),
					new EventSink[] {
							new EventSink(VacuumCleanerElectricityModel.URI,
										  SetLowVacuumCleaner.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					VacuumCleanerCoupledModel.URI,
					new CoupledModelDescriptor(
							VacuumCleanerCoupledModel.class,
							VacuumCleanerCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							VacuumCleanerCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							TimeUnit.HOURS);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			se.doStandAloneSimulation(0.0, 24.0);
			SimulationReportI sr = se.getSimulatedModel().getFinalReport();
			System.out.println(sr);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------