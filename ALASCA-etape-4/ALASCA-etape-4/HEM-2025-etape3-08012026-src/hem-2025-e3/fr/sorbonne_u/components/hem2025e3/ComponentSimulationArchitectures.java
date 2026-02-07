package fr.sorbonne_u.components.hem2025e3;

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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPlugin;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentAtomicModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentCoupledModelDescriptor;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.RTComponentModelArchitecture;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.Start;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.Stop;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.TankEmpty;
import fr.sorbonne_u.components.hem2025e2.equipments.generator.mil.events.TankNoLongerEmpty;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SetHighHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SetLowHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SwitchOffHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.hairdryer.mil.events.SwitchOnHairDryer;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.HeaterCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.DoNotHeat;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.Heat;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.sil.events.SIL_SetPowerHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOffHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.heater.mil.events.SwitchOnHeater;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.SolarPanelCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunriseEvent;
import fr.sorbonne_u.components.hem2025e2.equipments.solar_panel.mil.events.SunsetEvent;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.BatteriesCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.BatteriesStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.events.CurrentBatteriesLevel;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.events.SIL_StartCharging;
import fr.sorbonne_u.components.hem2025e3.equipments.batteries.sil.events.SIL_StopCharging;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.GeneratorCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.GeneratorStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.events.CurrentFuelConsumption;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.events.CurrentFuelLevel;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.events.CurrentPowerProduction;
import fr.sorbonne_u.components.hem2025e3.equipments.generator.sil.events.SIL_Refill;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.HairDryerCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.hairdryer.sil.HairDryerStateSILModel;
import fr.sorbonne_u.components.hem2025e3.equipments.heater.HeaterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.ElectricMeterCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.meter.sil.ElectricMeterCoupledModel;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.SolarPanelCyPhy;
import fr.sorbonne_u.components.hem2025e3.equipments.solar_panel.sil.events.PowerProductionLevel;
import fr.sorbonne_u.components.hem2025e3.equipments.washing_machine.WashingMachineCyPhy;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.WashingMachineCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOnWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SwitchOffWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.StartWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SetDelayedStart;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SuspendWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.ResumeWashing;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.SetPowerWashingMachine;
import fr.sorbonne_u.components.hem2025e2.equipments.washing_machine.mil.events.HeatingFinished;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.KettleCyPhy;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.KettleCoupledModel;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOnKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.SwitchOffKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.HeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.DoNotHeatKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StartKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e2.equipments.kettle.mil.events.StopKeepingWarmKettle;
import fr.sorbonne_u.components.hem2025e3.equipments.kettle.sil.events.SIL_SetPowerKettle;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentSimulationArchitectures</code> defines the global
 * component simulation architectures for the whole HEM application.
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * <strong>Implementation Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * <strong>Invariants</strong>
 * </p>
 * 
 * <pre>
 * invariant	{@code
 * true
 * }	// no more invariant
 * </pre>
 * 
 * <p>
 * Created on : 2023-11-16
 * </p>
 * 
 * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class ComponentSimulationArchitectures {
	/**
	 * create the global SIL real time component simulation architecture for the
	 * HEM application.
	 * 
	 * <p>
	 * <strong>Contract</strong>
	 * </p>
	 * 
	 * <pre>
	 * pre	{@code
	 * architectureURI != null && !architectureURI.isEmpty()
	 * }
	 * pre	{@code
	 * rootModelURI != null && !rootModelURI.isEmpty()
	 * }
	 * pre	{@code
	 * simulatedTimeUnit != null
	 * }
	 * pre	{@code
	 * accelerationFactor > 0.0
	 * }
	 * post	{@code return != null}
	 * post {@code return.getArchitectureURI().equals(architectureURI)}
	 * post	{@code return.getRootModelURI().equals(rootModelURI)}
	 * post	{@code return.getSimulationTimeUnit().equals(simulatedTimeUnit)}
	 * </pre>
	 *
	 * @param architectureURI    URI of the component model architecture to be
	 *                           created.
	 * @param rootModelURI       URI of the root model in the simulation
	 *                           architecture.
	 * @param simulatedTimeUnit  simulated time unit used in the architecture.
	 * @param accelerationFactor acceleration factor for this run.
	 * @return the global SIL real time simulation architecture for the HEM
	 *         application.
	 * @throws Exception <i>to do</i>.
	 */
	@SuppressWarnings("unchecked")
	public static RTComponentModelArchitecture createComponentSimulationArchitectures(
			String architectureURI,
			String rootModelURI,
			TimeUnit simulatedTimeUnit,
			double accelerationFactor) throws Exception {
		assert architectureURI != null && !architectureURI.isEmpty() : new PreconditionException(
				"architectureURI != null && !architectureURI.isEmpty()");
		assert rootModelURI != null && !rootModelURI.isEmpty() : new PreconditionException(
				"rootModelURI != null && !rootModelURI.isEmpty()");
		assert simulatedTimeUnit != null : new PreconditionException("simulatedTimeUnit != null");
		assert accelerationFactor > 0.0 : new PreconditionException("accelerationFactor > 0.0");

		// map that will contain the atomic model descriptors to construct
		// the simulation architecture
		Map<String, AbstractAtomicModelDescriptor> atomicModelDescriptors = new HashMap<>();

		// Currently, the HEM application has only two appliances: a hair dryer
		// and a heater.
		atomicModelDescriptors.put(
				HairDryerStateSILModel.URI,
				RTComponentAtomicModelDescriptor.create(
						HairDryerStateSILModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {},
						(Class<? extends EventI>[]) new Class<?>[] {
								SwitchOnHairDryer.class, // notice that the
								SwitchOffHairDryer.class, // exported events of
								SetLowHairDryer.class, // the atomic model
								SetHighHairDryer.class }, // appear here
						simulatedTimeUnit,
						HairDryerCyPhy.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				HeaterCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						HeaterCoupledModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {},
						(Class<? extends EventI>[]) new Class<?>[] {
								SIL_SetPowerHeater.class, // notice that the
								SwitchOnHeater.class, // reexported events of
								SwitchOffHeater.class, // the coupled model
								Heat.class, // appear here
								DoNotHeat.class },
						simulatedTimeUnit,
						HeaterCyPhy.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				BatteriesStateSILModel.URI,
				RTComponentAtomicModelDescriptor.create(
						BatteriesStateSILModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {
								CurrentBatteriesLevel.class },
						(Class<? extends EventI>[]) new Class<?>[] {
								SIL_StartCharging.class,
								SIL_StopCharging.class },
						simulatedTimeUnit,
						BatteriesCyPhy.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				SolarPanelCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						SolarPanelCoupledModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {
								PowerProductionLevel.class },
						(Class<? extends EventI>[]) new Class<?>[] {
								SunriseEvent.class,
								SunsetEvent.class },
						simulatedTimeUnit,
						SolarPanelCyPhy.REFLECTION_INBOUND_PORT_URI));

		atomicModelDescriptors.put(
				GeneratorStateSILModel.URI,
				RTComponentAtomicModelDescriptor.create(
						GeneratorStateSILModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {
								TankEmpty.class, TankNoLongerEmpty.class,
								CurrentPowerProduction.class, CurrentFuelLevel.class,
								CurrentFuelConsumption.class },
						(Class<? extends EventI>[]) new Class<?>[] {
								Start.class, Stop.class, SIL_Refill.class },
						simulatedTimeUnit,
						GeneratorCyPhy.REFLECTION_INBOUND_PORT_URI));

		// WashingMachine coupled model descriptor
		atomicModelDescriptors.put(
				WashingMachineCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						WashingMachineCoupledModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {},
						(Class<? extends EventI>[]) new Class<?>[] {
								SwitchOnWashingMachine.class,
								SwitchOffWashingMachine.class,
								StartWashing.class,
								SetDelayedStart.class,
								SuspendWashing.class,
								ResumeWashing.class,
								SetPowerWashingMachine.class },
						simulatedTimeUnit,
						WashingMachineCyPhy.REFLECTION_INBOUND_PORT_URI));

		// Kettle coupled model descriptor
		atomicModelDescriptors.put(
				KettleCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						KettleCoupledModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {},
						(Class<? extends EventI>[]) new Class<?>[] {
								SwitchOnKettle.class,
								SwitchOffKettle.class,
								SIL_SetPowerKettle.class,
								HeatKettle.class,
								DoNotHeatKettle.class,
								StartKeepingWarmKettle.class,
								StopKeepingWarmKettle.class },
						simulatedTimeUnit,
						KettleCyPhy.REFLECTION_INBOUND_PORT_URI));

		// The electric meter also has a SIL simulation model
		atomicModelDescriptors.put(
				ElectricMeterCoupledModel.URI,
				RTComponentAtomicModelDescriptor.create(
						ElectricMeterCoupledModel.URI,
						(Class<? extends EventI>[]) new Class<?>[] {
								SwitchOnHairDryer.class, // notice that the
								SwitchOffHairDryer.class, // imported events of
								SetLowHairDryer.class, // the coupled model
								SetHighHairDryer.class, // appear here
								SIL_SetPowerHeater.class,
								SwitchOnHeater.class,
								SwitchOffHeater.class,
								Heat.class,
								DoNotHeat.class,
								SIL_StartCharging.class,
								SIL_StopCharging.class,
								SunriseEvent.class,
								SunsetEvent.class,
								Start.class,
								Stop.class,
								SIL_Refill.class,
								// WashingMachine events
								SwitchOnWashingMachine.class,
								SwitchOffWashingMachine.class,
								StartWashing.class,
								SetDelayedStart.class,
								SuspendWashing.class,
								ResumeWashing.class,
								SetPowerWashingMachine.class,
								// Kettle events
								SwitchOnKettle.class,
								SwitchOffKettle.class,
								SIL_SetPowerKettle.class,
								HeatKettle.class,
								DoNotHeatKettle.class,
								StartKeepingWarmKettle.class,
								StopKeepingWarmKettle.class },
						(Class<? extends EventI>[]) new Class<?>[] {
								CurrentBatteriesLevel.class,
								PowerProductionLevel.class,
								TankEmpty.class,
								TankNoLongerEmpty.class,
								CurrentPowerProduction.class,
								CurrentFuelLevel.class,
								CurrentFuelConsumption.class },
						simulatedTimeUnit,
						ElectricMeterCyPhy.REFLECTION_INBOUND_PORT_URI));

		// map that will contain the coupled model descriptors to construct
		// the simulation architecture
		Map<String, CoupledModelDescriptor> coupledModelDescriptors = new HashMap<>();

		// the set of submodels of the coupled model, given by their URIs
		Set<String> submodels = new HashSet<String>();
		submodels.add(HairDryerStateSILModel.URI);
		submodels.add(HeaterCoupledModel.URI);
		submodels.add(ElectricMeterCoupledModel.URI);
		submodels.add(BatteriesStateSILModel.URI);
		submodels.add(SolarPanelCoupledModel.URI);
		submodels.add(GeneratorStateSILModel.URI);
		submodels.add(WashingMachineCoupledModel.URI);
		submodels.add(KettleCoupledModel.URI);

		// event exchanging connections between exporting and importing
		// models
		Map<EventSource, EventSink[]> connections = new HashMap<EventSource, EventSink[]>();

		// events going from the hair dryer to the electric meter
		connections.put(
				new EventSource(HairDryerStateSILModel.URI,
						SwitchOnHairDryer.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOnHairDryer.class)
				});
		connections.put(
				new EventSource(HairDryerStateSILModel.URI,
						SwitchOffHairDryer.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOffHairDryer.class)
				});
		connections.put(
				new EventSource(HairDryerStateSILModel.URI,
						SetLowHairDryer.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SetLowHairDryer.class)
				});
		connections.put(
				new EventSource(HairDryerStateSILModel.URI,
						SetHighHairDryer.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SetHighHairDryer.class)
				});

		// events going from the heater to the electric meter
		connections.put(
				new EventSource(HeaterCoupledModel.URI,
						SIL_SetPowerHeater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SIL_SetPowerHeater.class)
				});
		connections.put(
				new EventSource(HeaterCoupledModel.URI,
						SwitchOnHeater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOnHeater.class)
				});
		connections.put(
				new EventSource(HeaterCoupledModel.URI,
						SwitchOffHeater.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOffHeater.class)
				});
		connections.put(
				new EventSource(HeaterCoupledModel.URI,
						Heat.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								Heat.class)
				});
		connections.put(
				new EventSource(HeaterCoupledModel.URI,
						DoNotHeat.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								DoNotHeat.class)
				});

		// events exchanged between the batteries and the electric meter
		connections.put(
				new EventSource(BatteriesStateSILModel.URI,
						SIL_StartCharging.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SIL_StartCharging.class)
				});
		connections.put(
				new EventSource(BatteriesStateSILModel.URI,
						SIL_StopCharging.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SIL_StopCharging.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						CurrentBatteriesLevel.class),
				new EventSink[] {
						new EventSink(BatteriesStateSILModel.URI,
								CurrentBatteriesLevel.class)
				});

		// events exchanged between the solar panel the electric meter
		connections.put(
				new EventSource(SolarPanelCoupledModel.URI,
						SunriseEvent.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SunriseEvent.class)
				});
		connections.put(
				new EventSource(SolarPanelCoupledModel.URI,
						SunsetEvent.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SunsetEvent.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						PowerProductionLevel.class),
				new EventSink[] {
						new EventSink(SolarPanelCoupledModel.URI,
								PowerProductionLevel.class)
				});

		// events exchanged between the generator the electric meter
		connections.put(
				new EventSource(GeneratorStateSILModel.URI,
						Start.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								Start.class)
				});
		connections.put(
				new EventSource(GeneratorStateSILModel.URI,
						Stop.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								Stop.class)
				});
		connections.put(
				new EventSource(GeneratorStateSILModel.URI,
						SIL_Refill.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SIL_Refill.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						TankEmpty.class),
				new EventSink[] {
						new EventSink(GeneratorStateSILModel.URI,
								TankEmpty.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						TankNoLongerEmpty.class),
				new EventSink[] {
						new EventSink(GeneratorStateSILModel.URI,
								TankNoLongerEmpty.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						CurrentPowerProduction.class),
				new EventSink[] {
						new EventSink(GeneratorStateSILModel.URI,
								CurrentPowerProduction.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						CurrentFuelLevel.class),
				new EventSink[] {
						new EventSink(GeneratorStateSILModel.URI,
								CurrentFuelLevel.class)
				});
		connections.put(
				new EventSource(ElectricMeterCoupledModel.URI,
						CurrentFuelConsumption.class),
				new EventSink[] {
						new EventSink(GeneratorStateSILModel.URI,
								CurrentFuelConsumption.class)
				});

		// events going from the washing machine to the electric meter
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						SwitchOnWashingMachine.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOnWashingMachine.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						SwitchOffWashingMachine.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOffWashingMachine.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						StartWashing.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								StartWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						SetDelayedStart.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SetDelayedStart.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						SuspendWashing.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SuspendWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						ResumeWashing.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								ResumeWashing.class)
				});
		connections.put(
				new EventSource(WashingMachineCoupledModel.URI,
						SetPowerWashingMachine.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SetPowerWashingMachine.class)
				});

		// events going from the kettle to the electric meter
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						SwitchOnKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOnKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						SwitchOffKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SwitchOffKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						HeatKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								HeatKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						DoNotHeatKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								DoNotHeatKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						SIL_SetPowerKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								SIL_SetPowerKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						StartKeepingWarmKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								StartKeepingWarmKettle.class)
				});
		connections.put(
				new EventSource(KettleCoupledModel.URI,
						StopKeepingWarmKettle.class),
				new EventSink[] {
						new EventSink(ElectricMeterCoupledModel.URI,
								StopKeepingWarmKettle.class)
				});

		// coupled model descriptor
		coupledModelDescriptors.put(
				rootModelURI,
				RTComponentCoupledModelDescriptor.create(
						GlobalCoupledModel.class,
						rootModelURI,
						submodels,
						null,
						null,
						connections,
						null,
						CoordinatorComponent.REFLECTION_INBOUND_PORT_URI,
						CoordinatorPlugin.class,
						null,
						accelerationFactor));

		RTComponentModelArchitecture architecture = new RTComponentModelArchitecture(
				architectureURI,
				rootModelURI,
				atomicModelDescriptors,
				coupledModelDescriptors,
				simulatedTimeUnit);

		return architecture;
	}
}
// -----------------------------------------------------------------------------
