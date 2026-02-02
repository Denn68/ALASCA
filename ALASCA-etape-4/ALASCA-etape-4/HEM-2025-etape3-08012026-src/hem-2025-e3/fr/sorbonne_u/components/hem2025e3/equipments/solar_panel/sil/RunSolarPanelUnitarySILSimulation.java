package fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil;

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
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunIntensityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunRiseAndSetModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunIntensityModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunRiseAndSetModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunriseEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunsetEvent;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil.events.PowerProductionLevel;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import java.time.Instant;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunSolarPanelUnitarySILSimulation</code> is the main class
 * used to run simulations on the example models of the solar panel in
 * isolation.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The simulation architecture for the solar panel contains three atomic models
 * composed under a coupled model:
 * </p>
 * <p><img src="../../../../../../../../images/hem-2025-e2/SolarPanelUnitTestArchitecture.png"/></p> 
 * <p>
 * For the sunrise and sunset as well as the sun intensity models, the user can
 * choose between an astronomical model for the former and a stochastic one for
 * the latter or deterministic versions where each days repeats exactly the
 * same values. Deterministic versions are more suited to functionality tests
 * while astronomical/stochastic ones are better suited to performance tests or
 * dimensioning.
 * </p>
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
 * <p>Created on : 2023-09-29</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunSolarPanelUnitarySILSimulation
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** a start {@code Instant} for the simulation, corresponding to the
	 *  start time for the simulation clock.								*/
	public static Instant	START_INSTANT =
									Instant.parse("2025-10-06T12:00:00.00Z");

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the static invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the invariants are observed, false otherwise.
	 */
	public static boolean	staticInvariants()
	{
		boolean ret = true;
		ret &= SolarPanelSimulationConfigurationI.staticInvariants();
		return ret;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void	main(String[] args)
	{
		staticInvariants();
		Time.setPrintPrecision(4);
		Duration.setPrintPrecision(4);

		try {
			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// DeterministicSunRiseAndSetModel is an atomic event scheduling
			// model, so needs an AtomicModelDescriptor
			atomicModelDescriptors.put(
					DeterministicSunRiseAndSetModel.URI,
					AtomicModelDescriptor.create(
							DeterministicSunRiseAndSetModel.class,
							DeterministicSunRiseAndSetModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null));
			// SolarPanelPowerSILModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					DeterministicSunIntensityModel.URI,
					AtomicHIOA_Descriptor.create(
							DeterministicSunIntensityModel.class,
							DeterministicSunIntensityModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null));
			// DeterministicSunRiseAndSetModel is an atomic model, so needs an
			// AtomicModelDescriptor
			atomicModelDescriptors.put(
					SolarPanelStateSILModel.URI,
					AtomicModelDescriptor.create(
							SolarPanelStateSILModel.class,
							SolarPanelStateSILModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null));
			// SolarPanelPowerSILModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					SolarPanelPowerSILModel.URI,
					AtomicHIOA_Descriptor.create(
							SolarPanelPowerSILModel.class,
							SolarPanelPowerSILModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null));

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(DeterministicSunRiseAndSetModel.URI);
			submodels.add(DeterministicSunIntensityModel.URI);
			submodels.add(SolarPanelStateSILModel.URI);
			submodels.add(SolarPanelPowerSILModel.URI);

			// event exchanging connections between exporting and importing
			// models
			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

			connections.put(
					new EventSource(DeterministicSunRiseAndSetModel.URI,
									SunriseEvent.class),
					new EventSink[] {
							new EventSink(DeterministicSunIntensityModel.URI,
										  SunriseEvent.class),
							new EventSink(SolarPanelPowerSILModel.URI,
										  SunriseEvent.class)
					});
			connections.put(
					new EventSource(DeterministicSunRiseAndSetModel.URI,
									SunsetEvent.class),
					new EventSink[] {
							new EventSink(DeterministicSunIntensityModel.URI,
										  SunsetEvent.class),
							new EventSink(SolarPanelPowerSILModel.URI,
										  SunsetEvent.class)
					});

			connections.put(
					new EventSource(SolarPanelPowerSILModel.URI,
									PowerProductionLevel.class),
					new EventSink[] {
							new EventSink(SolarPanelStateSILModel.URI,
										  PowerProductionLevel.class)
					});

			// variable sharing bindings between exporting and importing
			// models
			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();

			VariableSource source =
					new VariableSource("sunIntensityCoef", Double.class,
									   DeterministicSunIntensityModel.URI);
			VariableSink[] sinks =
					new VariableSink[] {
						new VariableSink("sunIntensityCoef", Double.class,
										 SolarPanelPowerSILModel.URI)
					};
			bindings.put(source, sinks);

			// coupled model descriptor
			coupledModelDescriptors.put(
					SolarPanelCoupledModel.URI,
					new CoupledHIOA_Descriptor(
							SolarPanelCoupledModel.class,
							SolarPanelCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							SolarPanelCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							SolarPanelSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();

			HashMap<String, Object> simParams = new HashMap<>();
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunRiseAndSetModel.URI,
							SunRiseAndSetModelI.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunRiseAndSetModel.URI,
							SunRiseAndSetModelI.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunRiseAndSetModel.URI,
							SunRiseAndSetModelI.START_INSTANT_RP_NAME),
					START_INSTANT);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunRiseAndSetModel.URI,
							SunRiseAndSetModelI.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);

			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.START_INSTANT_RP_NAME),
					START_INSTANT);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.SLOPE_RP_NAME),
					SolarPanelSimulationConfigurationI.SLOPE);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.ORIENTATION_RP_NAME),
					SolarPanelSimulationConfigurationI.ORIENTATION);
			simParams.put(
					ModelI.createRunParameterName(
							DeterministicSunIntensityModel.URI,
							SunIntensityModelI.COMPUTATION_STEP_RP_NAME),
					0.5);

			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.LATITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.LONGITUDE_RP_NAME),
					SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.START_INSTANT_RP_NAME),
					START_INSTANT);
			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.ZONE_ID_RP_NAME),
					SolarPanelSimulationConfigurationI.ZONE);
			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.MAX_POWER_RP_NAME),
					SolarPanelSimulationConfigurationI.NB_SQUARE_METERS *
								SolarPanel.CAPACITY_PER_SQUARE_METER.getData());
			simParams.put(
					ModelI.createRunParameterName(
							SolarPanelPowerSILModel.URI,
							SolarPanelPowerSILModel.COMPUTATION_STEP_RP_NAME),
					0.25);

			se.setSimulationRunParameters(simParams);

			// this add additional time at each simulation step in
			// standard simulations (useful when debugging)
			SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L;
			// run a simulation with the simulation beginning at 0.0 and
			// ending at 24.0
			se.doStandAloneSimulation(0.0, 48.0);
			System.exit(0);
		} catch (Throwable e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
