package fr.sorbonne_u.devs_simulation.examples.thermostat;

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
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.architectures.ArchitectureI;
import fr.sorbonne_u.devs_simulation.architectures.RTArchitecture;
import fr.sorbonne_u.devs_simulation.hioa.architectures.AtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.CoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTAtomicHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.architectures.RTCoupledHIOA_Descriptor;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSink;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.VariableSource;
import fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationMain</code> launches a simulation run for the
 * thermostated heater example.
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
 * <p>Created on : 2022-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationMain
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** 1 hour takes 1 seconds.												*/
	public static double		ACCELERATION_FACTOR = 60.0*6.0*10.0;
	public static boolean		REAL_TIME_SIMULATION;
//	public static boolean		WITH_VALUE_HISTORY;
	/** simulated duration, 6000 seconds = 1h40.							*/
	public static final double	SIMULATION_DURATION = 24.0;

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	public static void	main(String[] args)
	{
		try {
			if (args != null && args.length > 0) {
				if (args[0].equals("true")) {
					REAL_TIME_SIMULATION = true;
				} else {
					REAL_TIME_SIMULATION = false;
				}
			} else {
				REAL_TIME_SIMULATION = true;
			}
//			if (args != null && args.length > 1) {
//				if (args[1].equals("true")) {
//					WITH_VALUE_HISTORY = true;					
//				} else {
//					WITH_VALUE_HISTORY = false;
//				}
//			} else {
//				WITH_VALUE_HISTORY = false;
//			}

			Map<String,AbstractAtomicModelDescriptor>
									atomicModelDescriptors = new HashMap<>();
			Map<String,CoupledModelDescriptor>
									coupledModelDescriptors = new HashMap<>();

			final String iw1uri = "Inside-Wall-1";
			final String iw2uri = "Inside-Wall-2";
			final String ow1uri = "Outside-Wall-1";
			final String ow2uri = "Outside-Wall-2";
			final String otURI = "outside-temperature";

			Set<String> submodels = new HashSet<String>();
			submodels.add(ow1uri);
			submodels.add(ow2uri);
			submodels.add(iw1uri);
			submodels.add(iw2uri);
			submodels.add(otURI);
			submodels.add(ThermostaticHeater.URI);

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
			VariableSource source =
				new VariableSource("outsideTemperature", Double.class, otURI);
			VariableSink[] sinks =
				new VariableSink[] {
						new VariableSink("outsideTemperature", Double.class, ow1uri),
						new VariableSink("outsideTemperature", Double.class, ow2uri)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallTemperature", Double.class, ow1uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall1Temp", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallInsulationConstant", Double.class,
								   ow1uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall1InsulationConstant", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);

			source =
				new VariableSource("wallTemperature", Double.class, ow2uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall2Temp", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallInsulationConstant", Double.class,
								   ow2uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall2InsulationConstant", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallTemperature", Double.class, iw1uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall3Temp", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallInsulationConstant", Double.class,
								   iw1uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall3InsulationConstant", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallTemperature", Double.class, iw2uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall4Temp", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);
			source =
				new VariableSource("wallInsulationConstant", Double.class,
							iw2uri);
			sinks =
				new VariableSink[] {
						new VariableSink("wall4InsulationConstant", Double.class,
										 ThermostaticHeater.URI)
				};
			bindings.put(source, sinks);

			Map<String,Object> simParams = new HashMap<>();
			simParams.put(
				iw1uri + ":" + AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				iw1uri + ":" + AbstractWall.
									INSULATION_TRANSFER_CONSTANT_PARAM_NAME,
				10.0);
			simParams.put(
				iw1uri + ":" + AbstractWall.INTEGRATION_STEP_PARAM_NAME,
				1.0);
			simParams.put(
				iw1uri + ":" + InsideWall.INITIAL_TEMP_PARAM_NAME,
				18.0);

			simParams.put(
				iw2uri + ":" + AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				iw2uri + ":" + AbstractWall.
										INSULATION_TRANSFER_CONSTANT_PARAM_NAME,
				8.0);
			simParams.put(
				iw2uri + ":" + AbstractWall.INTEGRATION_STEP_PARAM_NAME,
				1.0);
			simParams.put(
				iw2uri + ":" + InsideWall.INITIAL_TEMP_PARAM_NAME,
				15.0);

			simParams.put(
				ow1uri + ":" + AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				ow1uri + ":" + AbstractWall.
									INSULATION_TRANSFER_CONSTANT_PARAM_NAME,
				10.0);
			simParams.put(
				ow1uri + ":" + AbstractWall.INTEGRATION_STEP_PARAM_NAME,
				1.0);

			simParams.put(
				ow2uri + ":" + AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				ow2uri + ":" + AbstractWall.
										INSULATION_TRANSFER_CONSTANT_PARAM_NAME,
				9.0);
			simParams.put(
				ow2uri + ":" + AbstractWall.INTEGRATION_STEP_PARAM_NAME,
				1.0);

			simParams.put(
				otURI + ":" + AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				otURI + ":" + OutsideTemperature.EVALUATION_STEP_PARAM_NAME,
				0.1);
			simParams.put(
				otURI + ":" + OutsideTemperature.MEAN_TEMP_PARAM_NAME,
				10.0);
			simParams.put(
				otURI + ":" + OutsideTemperature.TEMP_AMPLITUDE_PARAM_NAME,
				3.0);
			simParams.put(
				otURI + ":" + OutsideTemperature.TEMP_NOISE_SD_PARAM_NAME,
				0.2);

			simParams.put(
				ThermostaticHeater.URI + ":" +
								AbstractPlottingHIOA.PLOTTING_PARAM_NAME,
				true);
			simParams.put(
				ThermostaticHeater.URI + ":" +
								ThermostaticHeater.INITIAL_TEMP_PARAM_NAME,
				10.0);
			simParams.put(
				ThermostaticHeater.URI + ":" +
								ThermostaticHeater.INTEGRATION_STEP_PARAM_NAME,
				0.05);

			if (REAL_TIME_SIMULATION) {
				atomicModelDescriptors.put(
						iw1uri,
						RTAtomicHIOA_Descriptor.create(
								InsideWall.class,
								iw1uri,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));
				atomicModelDescriptors.put(
						iw2uri,
						RTAtomicHIOA_Descriptor.create(
								InsideWall.class,
								iw2uri,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));
				atomicModelDescriptors.put(
						ow1uri,
						RTAtomicHIOA_Descriptor.create(
								OutsideWall.class,
								ow1uri,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));
				atomicModelDescriptors.put(
						ow2uri,
						RTAtomicHIOA_Descriptor.create(
								OutsideWall.class,
								ow2uri,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));
				atomicModelDescriptors.put(
						otURI,
						RTAtomicHIOA_Descriptor.create(
								OutsideTemperature.class,
								otURI,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));
				atomicModelDescriptors.put(
						ThermostaticHeater.URI,
						RTAtomicHIOA_Descriptor.create(
								ThermostaticHeater.class,
								ThermostaticHeater.URI,
								TimeUnit.HOURS,
								null,
								ACCELERATION_FACTOR));

				coupledModelDescriptors.put(
						ThermostatCoupledModel.URI,
						new RTCoupledHIOA_Descriptor(
								ThermostatCoupledModel.class,
								ThermostatCoupledModel.URI,
								submodels,
								null,
								null,
								null,
								null,
								null,
								null,
								bindings,
								ACCELERATION_FACTOR));

				ArchitectureI architecture =
						new RTArchitecture(
								"real-time-thermostat-example",
								ThermostatCoupledModel.URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								TimeUnit.HOURS,
								ACCELERATION_FACTOR);

				SimulatorI se = architecture.constructSimulator();
				se.setSimulationRunParameters(simParams);
				// wait must be long enough to start the GUI...
				long wait2start = 4000;
				long start = System.currentTimeMillis() + wait2start;
				long multiplier = (long) (3600L/ACCELERATION_FACTOR);
				long duration = (long) SIMULATION_DURATION;
				((CoordinationRTEngine)se).
							startRTSimulation(start, 0.0, SIMULATION_DURATION);
				long delay2stop = (duration * multiplier) * 1000L + 10000L;
				Thread.sleep(delay2stop);
				((CoordinationRTEngine)se).finaliseSimulation();
			} else {
				atomicModelDescriptors.put(
						iw1uri,
						AtomicHIOA_Descriptor.create(
								InsideWall.class,
								iw1uri,
								TimeUnit.HOURS,
								null));
				atomicModelDescriptors.put(
						iw2uri,
						AtomicHIOA_Descriptor.create(
								InsideWall.class,
								iw2uri,
								TimeUnit.HOURS,
								null));
				atomicModelDescriptors.put(
						ow1uri,
						AtomicHIOA_Descriptor.create(
								OutsideWall.class,
								ow1uri,
								TimeUnit.HOURS,
								null));
				atomicModelDescriptors.put(
						ow2uri,
						AtomicHIOA_Descriptor.create(
								OutsideWall.class,
								ow2uri,
								TimeUnit.HOURS,
								null));
				atomicModelDescriptors.put(
						otURI,
						AtomicHIOA_Descriptor.create(
								OutsideTemperature.class,
								otURI,
								TimeUnit.HOURS,
								null));
				atomicModelDescriptors.put(
						ThermostaticHeater.URI,
						AtomicHIOA_Descriptor.create(
								ThermostaticHeater.class,
								ThermostaticHeater.URI,
								TimeUnit.HOURS,
								null));

				coupledModelDescriptors.put(
						ThermostatCoupledModel.URI,
						new CoupledHIOA_Descriptor(
								ThermostatCoupledModel.class,
								ThermostatCoupledModel.URI,
								submodels,
								null,
								null,
								null,
								null,
								null,
								null,
								bindings));

				ArchitectureI architecture =
						new Architecture(
								ThermostatCoupledModel.URI,
								atomicModelDescriptors,
								coupledModelDescriptors,
								TimeUnit.HOURS);

				SimulatorI se = architecture.constructSimulator();
				se.setSimulationRunParameters(simParams);
				se.doStandAloneSimulation(0.0, SIMULATION_DURATION);
				Thread.sleep(10000L);
				se.finaliseSimulation();
			}
			System.out.println("Simulation ends.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
