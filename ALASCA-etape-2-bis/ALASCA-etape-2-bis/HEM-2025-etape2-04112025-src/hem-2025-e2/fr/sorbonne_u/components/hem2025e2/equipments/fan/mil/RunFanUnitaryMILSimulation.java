package fr.sorbonne_u.components.hem2025e2.equipments.fan.mil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetHighSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SetLowSpeedFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOffFan;
import fr.sorbonne_u.components.hem2025e2.equipments.fan.mil.events.SwitchOnFan;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunFanUnitaryMILSimulation</code> creates a simulator
 * for the fan and then runs a typical simulation.
 *
 * <p><strong>Description</strong></p>
 * <p>Created on : 2023-11-14</p>
 * * @author	Team DeMoh
 */
public class			RunFanUnitaryMILSimulation
{
	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= FanSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void main(String[] args)
	{
		staticInvariants();

		try {
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
															new HashMap<>();

			atomicModelDescriptors.put(
					FanElectricityModel.URI,
					AtomicHIOA_Descriptor.create(
							FanElectricityModel.class,
							FanElectricityModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			atomicModelDescriptors.put(
					FanUnitTesterModel.URI,
					AtomicModelDescriptor.create(
							FanUnitTesterModel.class,
							FanUnitTesterModel.URI,
							FanSimulationConfigurationI.TIME_UNIT,
							null));

			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(FanElectricityModel.URI);
			submodels.add(FanUnitTesterModel.URI);
			
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SwitchOnFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOnFan.class)
					});

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SwitchOffFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SwitchOffFan.class)
					});
			
			connections.put(
					new EventSource(FanUnitTesterModel.URI, SetLowSpeedFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetLowSpeedFan.class)
					});

			connections.put(
					new EventSource(FanUnitTesterModel.URI, SetHighSpeedFan.class),
					new EventSink[] {
							new EventSink(FanElectricityModel.URI, SetHighSpeedFan.class)
					});

			// coupled model descriptor
			coupledModelDescriptors.put(
					FanCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							FanCoupledModel.class,
							FanCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							null)); // No bindings

			ArchitectureI architecture =
					new Architecture(
							FanCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							FanSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();
			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 0L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			se.doStandAloneSimulation(0.0, 24.0);
			System.exit(0);
			
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------