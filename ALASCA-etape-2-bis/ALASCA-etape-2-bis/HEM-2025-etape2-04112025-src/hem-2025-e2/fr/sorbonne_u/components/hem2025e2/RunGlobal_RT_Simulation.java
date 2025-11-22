package fr.sorbonne_u.components.hem2025e2;

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
import java.time.Instant;
import java.util.ArrayList;
import fr.sorbonne_u.components.hem2025.tests_utils.SimulationTestStep;
import fr.sorbonne_u.components.hem2025.tests_utils.TestScenario;
import fr.sorbonne_u.components.hem2025e1.equipments.batteries.Batteries;
import fr.sorbonne_u.components.hem2025e1.equipments.generator.Generator;
import fr.sorbonne_u.components.hem2025e1.equipments.solar_panel.SolarPanel;
import fr.sorbonne_u.components.hem2025e2.GlobalCoupledModel.GlobalReport;
import fr.sorbonne_u.components.hem2025e2.equipments.batteries.mil.BatteriesPowerModel;
import fr.sorbonne_u.components.hem2025e2.equipments.batteries.mil.BatteriesSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.batteries.mil.events.BatteriesRequiredPowerChanged;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorFuelModel;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorGlobalTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorPowerModel;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.GeneratorSimulationConfiguration;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.GeneratorRequiredPowerChanged;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.Refill;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.Start;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.Stop;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.TankEmpty;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.TankNoLongerEmpty;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.HairDryerElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.HairDryerSimpleUserModel;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.ExternalTemperatureModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterTemperatureModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterUnitTesterModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SetPowerHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SetPowerHeater.PowerValue;
import fr.sorbonne_u.components.hem2025e2.equipments.meter.mil.ElectricMeterElectricityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.AstronomicalSunRiseAndSetModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunIntensityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.DeterministicSunRiseAndSetModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelPowerModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelSimulationConfigurationI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.StochasticSunIntensityModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunIntensityModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SunRiseAndSetModelI;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunriseEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunsetEvent;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
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
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>RunGlobal_RT_Simulation</code> creates the real time simulator
 * for the household energy management example and then runs a typical
 * simulation in real time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The simulation architecture for the global HEM application contains all of
 * the appliances atomic models composed under a single coupled model (a more
 * hierarchical architecture could be used but would complicate the flow of
 * events and variables) :
 * </p>
 * <p><img src="../../../../../images/hem-2025-e2/HEM_MILModel.png"/></p> 
 * <p>
 * This class shows how to describe, construct and then run a real time
 * simulation. By comparison with {@code RunHEM_Simulation}, differences
 * help understanding the passage from a synthetic simulation time run
 * to a real time one. Recall that real time simulations force the simulation
 * time to follow the real time, hence in a standard real time run, the
 * simulation time advance at the rhythm of the real time. However, such
 * simulation runs can become either very lengthy, for examples like the
 * household energy management where simulation runs could last several days,
 * or very short, for examples like simulating microprocessors where events
 * can occur at the nanosecond time scale. So it is also possible to keep the
 * same time structure but to accelerate or decelerate the real time by some
 * factor, here defined as {@code ACCELERATION_FACTOR}. A value greater than
 * one will accelerate the simulation while a value strictly between 0 and 1
 * will decelerate it.
 * </p>
 * <p>
 * So, notice the use of real time equivalent to the model descriptors and
 * the simulation engine attached to models, as well as the acceleration
 * factor passed as parameter through the descriptors. The same acceleration
 * factor must be imposed to all models to get time coherent simulations.
 * </p>
 * <p>
 * The interest of real time simulations will become clear when simulation
 * models will be used in SIL simulations with the actual component software
 * executing in parallel to the simulations. Time coherent exchanges will then
 * become possible between the code and the simulations as the execution
 * of code instructions will occur on the same time frame as the simulations.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code ACCELERATION_FACTOR > 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-10-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RunGlobal_RT_Simulation
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** acceleration factor for the real time simulation; with a factor 2.0,
	 *  the simulation runs two times faster than real time i.e., a run that
	 *  is supposed to take 10 seconds in real time will take 5 seconds to
	 *  execute.															*/
	protected static final double ACCELERATION_FACTOR = 1200.0;

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
		ret &= GlobalSimulationConfigurationI.staticInvariants();
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
			// -----------------------------------------------------------------
			// Atomic models
			// -----------------------------------------------------------------

			// map that will contain the atomic model descriptors to construct
			// the simulation architecture
			Map<String,AbstractAtomicModelDescriptor> atomicModelDescriptors =
																new HashMap<>();

			// atomic HIOA models require RTAtomicHIOA_Descriptor while
			// atomic models require RTAtomicModelDescriptor
			// the same time unit and acceleration factor must be used for all
			// models

			// Hair dryer models

			// the hair dyer model simulating its electricity consumption, an
			// atomic HIOA model hence we use an RTAtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					HairDryerElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HairDryerElectricityModel.class,
							HairDryerElectricityModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			// for atomic model, we use an RTAtomicModelDescriptor
			atomicModelDescriptors.put(
					HairDryerSimpleUserModel.URI,
					RTAtomicModelDescriptor.create(
							HairDryerSimpleUserModel.class,
							HairDryerSimpleUserModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// Heater models

			atomicModelDescriptors.put(
					HeaterElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterElectricityModel.class,
							HeaterElectricityModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HeaterTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							HeaterTemperatureModel.class,
							HeaterTemperatureModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					ExternalTemperatureModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ExternalTemperatureModel.class,
							ExternalTemperatureModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			atomicModelDescriptors.put(
					HeaterUnitTesterModel.URI,
					RTAtomicModelDescriptor.create(
							HeaterUnitTesterModel.class,
							HeaterUnitTesterModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// Batteries models

			// BatteriesPowerModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					BatteriesPowerModel.URI,
					RTAtomicHIOA_Descriptor.create(
							BatteriesPowerModel.class,
							BatteriesPowerModel.URI,
							BatteriesSimulationConfiguration.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// Solar panel models

			String sunRiseAndSetURI = null;
			if (SolarPanelSimulationConfigurationI.USE_ASTRONOMICAL_MODEL) {
				// AstronomicalSunRiseAndSetModel is an atomic event scheduling
				// model, so needs an AtomicModelDescriptor
				sunRiseAndSetURI = AstronomicalSunRiseAndSetModel.URI;
				atomicModelDescriptors.put(
					AstronomicalSunRiseAndSetModel.URI,
					RTAtomicModelDescriptor.create(
							AstronomicalSunRiseAndSetModel.class,
							AstronomicalSunRiseAndSetModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			} else {
				// DeterministicSunRiseAndSetModel is an atomic event scheduling
				// model, so needs an AtomicModelDescriptor
				sunRiseAndSetURI = DeterministicSunRiseAndSetModel.URI;
				atomicModelDescriptors.put(
					DeterministicSunRiseAndSetModel.URI,
					RTAtomicModelDescriptor.create(
							DeterministicSunRiseAndSetModel.class,
							DeterministicSunRiseAndSetModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			}
			String sunIntensityModelURI = null;
			if (SolarPanelSimulationConfigurationI.
										USE_STOCHASTIC_SUN_INTENSITY_MODEL) {
				// StochasticSunIntensityModel is an atomic HIOA model, so needs
				// an AtomicHIOA_Descriptor
				sunIntensityModelURI = StochasticSunIntensityModel.URI;
				atomicModelDescriptors.put(
					StochasticSunIntensityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							StochasticSunIntensityModel.class,
							StochasticSunIntensityModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			} else {
				// DeterministicSunIntensityModel is an atomic HIOA model, so
				// needs an AtomicHIOA_Descriptor
				sunIntensityModelURI = DeterministicSunIntensityModel.URI;
				atomicModelDescriptors.put(
					DeterministicSunIntensityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							DeterministicSunIntensityModel.class,
							DeterministicSunIntensityModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			}
			// SolarPanelPowerModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					SolarPanelPowerModel.URI,
					RTAtomicHIOA_Descriptor.create(
							SolarPanelPowerModel.class,
							SolarPanelPowerModel.URI,
							SolarPanelSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// Generator models

			// GeneratorFuelModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					GeneratorFuelModel.URI,
					RTAtomicHIOA_Descriptor.create(
							GeneratorFuelModel.class,
							GeneratorFuelModel.URI,
							GeneratorSimulationConfiguration.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			// GeneratorPowerModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					GeneratorPowerModel.URI,
					RTAtomicHIOA_Descriptor.create(
							GeneratorPowerModel.class,
							GeneratorPowerModel.URI,
							GeneratorSimulationConfiguration.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));
			// BatteriesUnitTesterModel is an atomic HIOA model, so needs an
			// AtomicHIOA_Descriptor
			atomicModelDescriptors.put(
					GeneratorGlobalTesterModel.URI,
					RTAtomicModelDescriptor.create(
							GeneratorGlobalTesterModel.class,
							GeneratorGlobalTesterModel.URI,
							GeneratorSimulationConfiguration.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// the electric meter model
			atomicModelDescriptors.put(
					ElectricMeterElectricityModel.URI,
					RTAtomicHIOA_Descriptor.create(
							ElectricMeterElectricityModel.class,
							ElectricMeterElectricityModel.URI,
							GlobalSimulationConfigurationI.TIME_UNIT,
							null,
							ACCELERATION_FACTOR));

			// -----------------------------------------------------------------
			// Global coupled model
			// -----------------------------------------------------------------

			// map that will contain the coupled model descriptors to construct
			// the simulation architecture
			Map<String,CoupledModelDescriptor> coupledModelDescriptors =
																new HashMap<>();

			// the set of submodels of the coupled model, given by their URIs
			Set<String> submodels = new HashSet<String>();
			submodels.add(HairDryerElectricityModel.URI);
			submodels.add(HairDryerSimpleUserModel.URI);
			submodels.add(HeaterElectricityModel.URI);
			submodels.add(HeaterTemperatureModel.URI);
			submodels.add(ExternalTemperatureModel.URI);
			submodels.add(HeaterUnitTesterModel.URI);
			submodels.add(BatteriesPowerModel.URI);
			submodels.add(sunRiseAndSetURI);
			submodels.add(sunIntensityModelURI);
			submodels.add(SolarPanelPowerModel.URI);
			submodels.add(GeneratorFuelModel.URI);
			submodels.add(GeneratorPowerModel.URI);
			submodels.add(GeneratorGlobalTesterModel.URI);
			submodels.add(ElectricMeterElectricityModel.URI);

			// -----------------------------------------------------------------
			// Event exchanging connections
			// -----------------------------------------------------------------

			Map<EventSource,EventSink[]> connections =
										new HashMap<EventSource,EventSink[]>();

										// Hair dryer events

			connections.put(
				new EventSource(HairDryerSimpleUserModel.URI,
								SwitchOnHairDryer.class),
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.URI,
								  SwitchOnHairDryer.class)
				});
			connections.put(
				new EventSource(HairDryerSimpleUserModel.URI,
								SwitchOffHairDryer.class),
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.URI,
								  SwitchOffHairDryer.class)
				});
			connections.put(
				new EventSource(HairDryerSimpleUserModel.URI,
								SetHighHairDryer.class),
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.URI,
								  SetHighHairDryer.class)
				});
			connections.put(
				new EventSource(HairDryerSimpleUserModel.URI,
								SetLowHairDryer.class),
				new EventSink[] {
					new EventSink(HairDryerElectricityModel.URI,
								  SetLowHairDryer.class)
				});

			// Heater events

			connections.put(
				new EventSource(HeaterUnitTesterModel.URI,
								SetPowerHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricityModel.URI,
								  SetPowerHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI,
								SwitchOnHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricityModel.URI,
								  SwitchOnHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI,
								SwitchOffHeater.class),
				new EventSink[] {
					new EventSink(HeaterElectricityModel.URI,
								  SwitchOffHeater.class),
					new EventSink(HeaterTemperatureModel.URI,
								  SwitchOffHeater.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, Heat.class),
				new EventSink[] {
					new EventSink(HeaterElectricityModel.URI, Heat.class),
					new EventSink(HeaterTemperatureModel.URI, Heat.class)
				});
			connections.put(
				new EventSource(HeaterUnitTesterModel.URI, DoNotHeat.class),
				new EventSink[] {
					new EventSink(HeaterElectricityModel.URI, DoNotHeat.class),
					new EventSink(HeaterTemperatureModel.URI, DoNotHeat.class)
				});

			// Batteries events

			connections.put(
				new EventSource(ElectricMeterElectricityModel.URI,
								BatteriesRequiredPowerChanged.class),
				new EventSink[] {
					new EventSink(BatteriesPowerModel.URI,
								  BatteriesRequiredPowerChanged.class)
				});

			// Solar panel events

			connections.put(
				new EventSource(sunRiseAndSetURI, SunriseEvent.class),
				new EventSink[] {
					new EventSink(sunIntensityModelURI, SunriseEvent.class),
					new EventSink(SolarPanelPowerModel.URI, SunriseEvent.class)
				});
			connections.put(
				new EventSource(sunRiseAndSetURI, SunsetEvent.class),
				new EventSink[] {
					new EventSink(sunIntensityModelURI, SunsetEvent.class),
					new EventSink(SolarPanelPowerModel.URI, SunsetEvent.class)
				});

			// Generator events

			connections.put(
				new EventSource(GeneratorGlobalTesterModel.URI, Start.class),
				new EventSink[] {
					new EventSink(GeneratorFuelModel.URI, Start.class),
					new EventSink(GeneratorPowerModel.URI, Start.class)
				});
			connections.put(
				new EventSource(GeneratorGlobalTesterModel.URI, Stop.class),
				new EventSink[] {
					new EventSink(GeneratorFuelModel.URI, Stop.class),
					new EventSink(GeneratorPowerModel.URI, Stop.class)
				});
			connections.put(
				new EventSource(GeneratorGlobalTesterModel.URI, Refill.class),
				new EventSink[] {
					new EventSink(GeneratorFuelModel.URI, Refill.class)
				});
			connections.put(
				new EventSource(ElectricMeterElectricityModel.URI,
								GeneratorRequiredPowerChanged.class),
				new EventSink[] {
					new EventSink(GeneratorPowerModel.URI,
								  GeneratorRequiredPowerChanged.class)
				});

			connections.put(
				new EventSource(GeneratorFuelModel.URI, TankEmpty.class),
				new EventSink[] {
					new EventSink(GeneratorPowerModel.URI, TankEmpty.class)
				});
			connections.put(
				new EventSource(GeneratorFuelModel.URI, TankNoLongerEmpty.class),
				new EventSink[] {
					new EventSink(GeneratorPowerModel.URI,
								  TankNoLongerEmpty.class)
				});

			connections.put(
				new EventSource(GeneratorPowerModel.URI,
								GeneratorRequiredPowerChanged.class),
				new EventSink[] {
					new EventSink(GeneratorFuelModel.URI,
								  GeneratorRequiredPowerChanged.class)
				});

			// -----------------------------------------------------------------
			// Variable bindings
			// -----------------------------------------------------------------

			Map<VariableSource,VariableSink[]> bindings =
					new HashMap<VariableSource,VariableSink[]>();

			// Bindings among heater models

			bindings.put(
				new VariableSource("externalTemperature", Double.class,
								   ExternalTemperatureModel.URI),
				new VariableSink[] {
					new VariableSink("externalTemperature", Double.class,
									 HeaterTemperatureModel.URI)
				});
			bindings.put(
				new VariableSource("currentHeatingPower", Double.class,
								   HeaterElectricityModel.URI),
				new VariableSink[] {
					new VariableSink("currentHeatingPower", Double.class,
									 HeaterTemperatureModel.URI)
				});

			// Bindings among solar panel models

			bindings.put(
				new VariableSource("sunIntensityCoef", Double.class,
								   sunIntensityModelURI),
				new VariableSink[] {
					new VariableSink("sunIntensityCoef", Double.class,
									 SolarPanelPowerModel.URI)
				});

			// Bindings among generator models

			bindings.put(
				new VariableSource("generatorOutputPower", Double.class,
								   GeneratorPowerModel.URI),
				new VariableSink[] {
					new VariableSink("generatorOutputPower", Double.class,
									 ElectricMeterElectricityModel.URI),
					new VariableSink("generatorOutputPower", Double.class,
									 GeneratorFuelModel.URI)
				});
			bindings.put(
				new VariableSource("generatorRequiredPower", Double.class,
								   ElectricMeterElectricityModel.URI),
				new VariableSink[] {
					new VariableSink("generatorRequiredPower", Double.class,
									 GeneratorPowerModel.URI)
				});

			// Bindings among appliances and power production units models and
			// the electric meter model

			bindings.put(
				new VariableSource("batteriesOutputPower", Double.class,
									   BatteriesPowerModel.URI),
				new VariableSink[] {
					new VariableSink("batteriesOutputPower", Double.class,
									 ElectricMeterElectricityModel.URI)
				});
			bindings.put(
				new VariableSource("batteriesInputPower", Double.class,
								   BatteriesPowerModel.URI),
				new VariableSink[] {
					new VariableSink("batteriesInputPower", Double.class,
									 ElectricMeterElectricityModel.URI)
				});
			bindings.put(
				new VariableSource("batteriesRequiredPower", Double.class,
								   ElectricMeterElectricityModel.URI),
				new VariableSink[] {
					new VariableSink("batteriesRequiredPower", Double.class,
									 BatteriesPowerModel.URI)
				});

			bindings.put(
				new VariableSource("solarPanelOutputPower", Double.class,
								   SolarPanelPowerModel.URI),
				new VariableSink[] {
					new VariableSink("solarPanelOutputPower", Double.class,
									 ElectricMeterElectricityModel.URI)
				});

			bindings.put(
				new VariableSource("currentIntensity", Double.class,
								   HairDryerElectricityModel.URI),
				new VariableSink[] {
					new VariableSink("currentIntensity", Double.class,
									 "currentHairDryerIntensity", Double.class,
									 ElectricMeterElectricityModel.URI)
				});
			bindings.put(
				new VariableSource("currentIntensity", Double.class,
								   HeaterElectricityModel.URI),
				new VariableSink[] {
					new VariableSink("currentIntensity", Double.class,
									 "currentHeaterIntensity", Double.class,
									 ElectricMeterElectricityModel.URI)
				});

			// -----------------------------------------------------------------
			// Overall simulation architecture
			// -----------------------------------------------------------------


			// coupled model descriptor: an HIOA requires a
			// RTCoupledHIOA_Descriptor
			coupledModelDescriptors.put(
					GlobalCoupledModel.URI,
					new RTCoupledHIOA_Descriptor(
							GlobalCoupledModel.class,
							GlobalCoupledModel.URI,
							submodels,
							null,
							null,
							connections,
							null,
							null,
							null,
							bindings,
							ACCELERATION_FACTOR));

			// simulation architecture
			ArchitectureI architecture =
					new Architecture(
							GlobalCoupledModel.URI,
							atomicModelDescriptors,
							coupledModelDescriptors,
							GlobalSimulationConfigurationI.TIME_UNIT);

			// create the simulator from the simulation architecture
			SimulatorI se = architecture.constructSimulator();

			// -----------------------------------------------------------------
			// Simulation run parameters
			// -----------------------------------------------------------------

			Map<String,Object> simParams = new HashMap<>();

			// run parameters for hair dryer models

			simParams.put(
				ModelI.createRunParameterName(
					HairDryerElectricityModel.URI,
					HairDryerElectricityModel.LOW_MODE_CONSUMPTION_RPNAME),
				660.0);
			simParams.put(
				ModelI.createRunParameterName(
					HairDryerElectricityModel.URI,
					HairDryerElectricityModel.HIGH_MODE_CONSUMPTION_RPNAME),
				1320.0);
			simParams.put(
				ModelI.createRunParameterName(
					HairDryerSimpleUserModel.URI,
					HairDryerSimpleUserModel.MEAN_STEP_RPNAME),
				0.05);
			simParams.put(
				ModelI.createRunParameterName(
					HairDryerSimpleUserModel.URI,
					HairDryerSimpleUserModel.MEAN_DELAY_RPNAME),
				2.0);

			// run parameters for solar panel models

			simParams.put(
				ModelI.createRunParameterName(
					sunRiseAndSetURI,
					SunRiseAndSetModelI.LATITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					sunRiseAndSetURI,
					SunRiseAndSetModelI.LONGITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					sunRiseAndSetURI,
					SunRiseAndSetModelI.START_INSTANT_RP_NAME),
				GlobalSimulationConfigurationI.START_INSTANT);
			simParams.put(
				ModelI.createRunParameterName(
					sunRiseAndSetURI,
					SunRiseAndSetModelI.ZONE_ID_RP_NAME),
				SolarPanelSimulationConfigurationI.ZONE);

			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.LATITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.LONGITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.START_INSTANT_RP_NAME),
				GlobalSimulationConfigurationI.START_INSTANT);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.ZONE_ID_RP_NAME),
				SolarPanelSimulationConfigurationI.ZONE);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.SLOPE_RP_NAME),
				SolarPanelSimulationConfigurationI.SLOPE);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.ORIENTATION_RP_NAME),
				SolarPanelSimulationConfigurationI.ORIENTATION);
			simParams.put(
				ModelI.createRunParameterName(
					sunIntensityModelURI,
					SunIntensityModelI.COMPUTATION_STEP_RP_NAME),
				0.5);

			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.LATITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LATITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.LONGITUDE_RP_NAME),
				SolarPanelSimulationConfigurationI.LONGITUDE);
			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.START_INSTANT_RP_NAME),
				GlobalSimulationConfigurationI.START_INSTANT);
			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.ZONE_ID_RP_NAME),
				SolarPanelSimulationConfigurationI.ZONE);
			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.MAX_POWER_RP_NAME),
				SolarPanelSimulationConfigurationI.NB_SQUARE_METERS *
								SolarPanel.CAPACITY_PER_SQUARE_METER.getData());
			simParams.put(
				ModelI.createRunParameterName(
					SolarPanelPowerModel.URI,
					SolarPanelPowerModel.COMPUTATION_STEP_RP_NAME),
				0.25);

			// -----------------------------------------------------------------
			// Simulation runs
			// -----------------------------------------------------------------

			// Tracing configuration

			HairDryerElectricityModel.VERBOSE = false;
			HairDryerElectricityModel.DEBUG = false;
			HairDryerSimpleUserModel.VERBOSE = false;
			HairDryerSimpleUserModel.DEBUG = false;

			HeaterElectricityModel.VERBOSE = false;
			HeaterElectricityModel.DEBUG = false;
			HeaterTemperatureModel.VERBOSE = false;
			HeaterTemperatureModel.DEBUG  = false;
			ExternalTemperatureModel.VERBOSE = false;
			ExternalTemperatureModel.DEBUG  = false;
			HeaterUnitTesterModel.VERBOSE = false;
			HeaterUnitTesterModel.DEBUG  = false;

			BatteriesPowerModel.VERBOSE = true;
			BatteriesPowerModel.DEBUG = false;

			if (SolarPanelSimulationConfigurationI.USE_ASTRONOMICAL_MODEL) {
				AstronomicalSunRiseAndSetModel.VERBOSE = false;
				AstronomicalSunRiseAndSetModel.DEBUG = false;
			} else {
				DeterministicSunRiseAndSetModel.VERBOSE = false;
				DeterministicSunRiseAndSetModel.DEBUG = false;
			}
			if (SolarPanelSimulationConfigurationI.
										USE_STOCHASTIC_SUN_INTENSITY_MODEL) {
				StochasticSunIntensityModel.VERBOSE = false;
				StochasticSunIntensityModel.DEBUG = false;
			} else {
				DeterministicSunIntensityModel.VERBOSE = false;
				DeterministicSunIntensityModel.DEBUG = false;
			}
			SolarPanelPowerModel.VERBOSE = false;
			SolarPanelPowerModel.DEBUG = false;

			GeneratorFuelModel.VERBOSE = false;
			GeneratorFuelModel.DEBUG = false;
			GeneratorPowerModel.VERBOSE = false;
			GeneratorPowerModel.DEBUG = false;
			GeneratorGlobalTesterModel.VERBOSE = false;
			GeneratorGlobalTesterModel.DEBUG = false;

			ElectricMeterElectricityModel.VERBOSE = true;
			ElectricMeterElectricityModel.DEBUG = false;

			// Test scenario

			// run a CLASSICAL test scenario
			CLASSICAL.setUpSimulator(se, simParams);
			Time startTime = CLASSICAL.getStartTime();
			Duration d = CLASSICAL.getEndTime().subtract(startTime);
			// the real time of start of the simulation plus a 1s delay to give
			// the time to initialise all models in the architecture.
			long realTimeOfStart = System.currentTimeMillis() + 1000L;

			se.startRTSimulation(realTimeOfStart,
								 startTime.getSimulatedTime(),
								 d.getSimulatedDuration());

			// wait until the simulation ends i.e., the start delay  plus the
			// duration of the simulation in milliseconds plus another 2s delay
			// to make sure...
			Thread.sleep(
				1000L
				+ ((long)((d.getSimulatedDuration()*3600*1000.0)/
														ACCELERATION_FACTOR))
				+ 3000L);
			// Optional: simulation report
			GlobalReport r = (GlobalReport) se.getFinalReport();
			System.out.println(r.printout(""));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	// -------------------------------------------------------------------------
	// Test scenarios
	// -------------------------------------------------------------------------

	/** standard test scenario, see Gherkin specification.				 	*/
	protected static TestScenario	CLASSICAL =
		new TestScenario(
				"-----------------------------------------------------\n" +
				"Classical\n\n" +
				"  Gherkin specification\n\n" +
				"    Feature: heater operation\n\n" +
				"      Scenario: heater switched on\n" +
				"        Given a heater that is off\n" +
				"        When it is switched on\n" +
				"        Then it is on but not heating though set at the highest power level\n" +
				"      Scenario: heater heats\n" +
				"        Given a heater that is on and not heating\n" +
				"        When it is asked to heat\n" +
				"        Then it is on and it heats at the current power level\n" +
				"      Scenario: heater stops heating\n" +
				"        Given a hair dryer that is heating\n" +
				"        When it is asked not to heat\n" +
				"        Then it is on but it stops heating\n" +
				"      Scenario: heater heats\n" +
				"        Given a heater that is on and not heating\n" +
				"        When it is asked to heat\n" +
				"        Then it is on and it heats at the current power level\n" +
				"      Scenario: heater set a different power level\n" +
				"        Given a heater that is heating\n" +
				"        When it is set to a new power level\n" +
				"        Then it is on and it heats at the new power level\n" +
				"      Scenario: hair dryer switched off\n" +
				"        Given a hair dryer that is on\n" +
				"        When it is switched of\n" +
				"        Then it is off\n" +
				"    Feature: generator production\n\n" +
				"      Scenario: generator produces for a limited time without emptying the tank\n" +
				"        Given a standard generator with a tank not full neither empty\n" +
				"        When it is producing for a limited time\n" +
				"        Then the tank level goes down but stays not empty\n" +
				"-----------------------------------------------------\n",
				"\n-----------------------------------------------------\n" +
				"End Classical\n" +
				"-----------------------------------------------------",
				GlobalSimulationConfigurationI.START_INSTANT,
				GlobalSimulationConfigurationI.END_INSTANT,
				GlobalSimulationConfigurationI.START_TIME,
				(simulationEngine, testScenario, simulationParameters) -> {
					simulationParameters.put(
						ModelI.createRunParameterName(
							HeaterUnitTesterModel.URI,
							HeaterUnitTesterModel.TEST_SCENARIO_RP_NAME),
						testScenario);
					simulationParameters.put(
						ModelI.createRunParameterName(
							BatteriesPowerModel.URI,
							BatteriesPowerModel.CAPACITY_RP_NAME),
						BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
								* BatteriesSimulationConfiguration.
														NUMBER_OF_CELL_GROUPS_IN_SERIES
									* Batteries.CAPACITY_PER_UNIT.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							BatteriesPowerModel.URI,
							BatteriesPowerModel.IN_POWER_RP_NAME),
						BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
								* Batteries.IN_POWER_PER_CELL.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							BatteriesPowerModel.URI,
							BatteriesPowerModel.MAX_OUT_POWER_RP_NAME),
						BatteriesSimulationConfiguration.NUMBER_OF_PARALLEL_CELLS
								* Batteries.MAX_OUT_POWER_PER_CELL.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							BatteriesPowerModel.URI,
							BatteriesPowerModel.LEVEL_QUANTUM_RP_NAME),
						BatteriesSimulationConfiguration.
													STANDARD_LEVEL_INTEGRATION_QUANTUM);
					simulationParameters.put(
						ModelI.createRunParameterName(
							BatteriesPowerModel.URI,
							BatteriesPowerModel.INITIAL_LEVEL_RP_NAME),
						BatteriesSimulationConfiguration.INITIAL_BATTERIES_LEVEL);
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.CAPACITY_RP_NAME),
						GeneratorSimulationConfiguration.TANK_CAPACITY);
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.INITIAL_LEVEL_RP_NAME),
						GeneratorSimulationConfiguration.INITIAL_TANK_LEVEL);
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.MIN_FUEL_CONSUMPTION_RP_NAME),
						Generator.MIN_FUEL_CONSUMPTION.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.MAX_FUEL_CONSUMPTION_RP_NAME),
						Generator.MAX_FUEL_CONSUMPTION.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.LEVEL_QUANTUM_RP_NAME),
						GeneratorSimulationConfiguration.
											STANDARD_LEVEL_INTEGRATION_QUANTUM);
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorFuelModel.URI,
							GeneratorFuelModel.MAX_OUT_POWER_RP_NAME),
						Generator.MAX_POWER.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorPowerModel.URI,
							GeneratorPowerModel.MAX_OUT_POWER_RP_NAME),
						Generator.MAX_POWER.getData());
					simulationParameters.put(
						ModelI.createRunParameterName(
							GeneratorGlobalTesterModel.URI,
							GeneratorGlobalTesterModel.TEST_SCENARIO_RP_NAME),
						testScenario);
					simulationEngine.setSimulationRunParameters(
														simulationParameters);
				},
				new SimulationTestStep[]{
					new SimulationTestStep(
						GeneratorGlobalTesterModel.URI,
						Instant.parse("2025-10-20T12:15:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Start(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T12:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOnHeater(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T13:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Heat(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T13:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new DoNotHeat(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:00:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Heat(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T14:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SetPowerHeater(t,
									   				   new PowerValue(880.0)));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						HeaterUnitTesterModel.URI,
						Instant.parse("2025-10-20T16:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new SwitchOffHeater(t));
							return ret;
						},
						(m, t) -> {}),
					new SimulationTestStep(
						GeneratorGlobalTesterModel.URI,
						Instant.parse("2025-10-20T17:30:00.00Z"),
						(m, t) -> {
							ArrayList<EventI> ret = new ArrayList<>();
							ret.add(new Stop(t));
							return ret;
						},
						(m, t) -> {})
					});
}
// -----------------------------------------------------------------------------
