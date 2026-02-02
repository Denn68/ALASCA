package fr.sorbonne_u.devs_simulation.examples.hioa_with_vh;

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
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.CoupledModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.simulators.CoordinationRTEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulationMain</code> runs the simulation of models using
 * value histories to evaluate continuous variables.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Without: no inter/extrapolation
 * </p>
 * <p>
 * With: linear inter/extrapolation (a star indicates a value that is
 * extrapolated from the previous values).
 * </p>
 * <p>
 * In these executions, at equal time, {@code HIOA_Consumer} is triggered
 * before {@code HIOA_Multiplier}. This is why the values given by
 * {@code HIOA_Consumer} are either late (using the value of
 * {@code HIOA_Multiplier} at the previous time) or extrapolated.
 * </p>
 * <pre>
 *  Time    HIOA_Generator  HIOA_Multiplier  HIOA_Consumer
 *                           without  with   without  with
 *  0.00         0.0          0.0      0.0     0.0     0.0
 *  0.25                      0.0      0.0
 *  0.50         1.0          1.0      1.0
 *  0.75                      1.0      1.5*
 *  1.00         2.0          2.0      2.0     1.0     2.0*
 *  1.25                      2.0      2.5*
 *  1.50         3.0          3.0      3.0
 *  1.75                      3.0      3.5*
 *  2.00         4.0          4.0      4.0     3.0     4.0*
 *  2.25                      4.0      4.5*
 *  2.50         5.0          5.0      5.0
 *  2.75                      5.0      5.5*
 *  3.00         6.0          6.0      6.0     5.0     6.0*
 *  3.25                      6.0      6.5*
 *  3.50         7.0          7.0      7.0
 *  3.75                      7.0      7.5*
 *  4.00         8.0          8.0      8.0     7.0     8.0*
 *  4.25                      8.0      8.5*
 *  4.50         9.0          9.0      9.0
 *  4.75                      9.0      9.5*
 *  5.00        10.0         10.0     10.0     9.0    10.0*
 *  5.25                     10.0     10.5*
 *  5.50        11.0         11.0     11.0
 *  5.75                     11.0     11.5*
 *  6.00        12.0         12.0     12.0    11.0    12.0*
 *  6.25                     12.0     12.5*
 *  6.50        13.0         13.0     13.0
 *  6.75                     13.0     13.5*
 *  7.00        14.0         14.0     14.0    13.0    14.0*
 *  7.25                     14.0     14.5*
 *  7.50        15.0         15.0     15.0
 *  7.75                     15.0     15.5*
 *  8.00        16.0         16.0     16.0    15.0    16.0*
 *  8.25                     16.0     16.5*
 *  8.50        17.0         17.0     17.0
 *  8.75                     17.0     17.5*
 *  9.00        18.0         18.0     18.0    17.0    18.0*
 *  9.25                     18.0     18.5*
 *  9.50        19.0         19.0     19.0
 *  9.75                     19.0     19.5*
 * 10.00        20.0         20.0     20.0   19.0     20.0*
 * </pre>
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
 * <p>Created on : 2020-11-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationMain
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** when true, perform a real-time simulation, otherwise perform a
	 *  simulation in logical time.											*/
	public static boolean		REAL_TIME_SIMULATION;
	/** when true, use value histories for continuous variable, otherwise
	 *  perform a standard simulation which evaluates continuous variables
	 *  to their most recent computed value.								*/
	public static boolean		WITH_VALUE_HISTORY;

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
			if (args != null && args.length > 1) {
				if (args[1].equals("true")) {
					WITH_VALUE_HISTORY = true;					
				} else {
					WITH_VALUE_HISTORY = false;
				}
			} else {
				WITH_VALUE_HISTORY = true;
			}

			Map<String,AbstractAtomicModelDescriptor>
								atomicModelDescriptors = new HashMap<>();
			Map<String,CoupledModelDescriptor>
								coupledModelDescriptors = new HashMap<>();

			Set<String> submodels = new HashSet<String>();
			submodels.add(HIOA_Consumer.URI_PREFIX);
			submodels.add(HIOA_Multiplier.URI_PREFIX);
			submodels.add(HIOA_Generator.URI_PREFIX);
			submodels.add(TicModel.URI);

			Map<EventSource,EventSink[]> connections =
									new HashMap<EventSource,EventSink[]>();
			EventSource from = new EventSource(TicModel.URI, TicEvent.class);
			EventSink[] to =
				new EventSink[] {
						new EventSink(HIOA_Generator.URI_PREFIX, TicEvent.class)
				};
			connections.put(from, to);

			Map<VariableSource,VariableSink[]> bindings =
								new HashMap<VariableSource,VariableSink[]>();
			VariableSource source =
					new VariableSource("generated", Double.class,
									   HIOA_Generator.URI_PREFIX);
			VariableSink[] sinks =
					new VariableSink[] {
						new VariableSink("other", Double.class,
										 HIOA_Multiplier.URI_PREFIX)
					};
			bindings.put(source, sinks);
			source = new VariableSource("mine", Double.class,
										HIOA_Multiplier.URI_PREFIX);
			sinks = new VariableSink[] {
						new VariableSink("otherCurrent", Double.class,
										 HIOA_Consumer.URI_PREFIX)
					};
			bindings.put(source, sinks);


			Map<String,Object> simParams = new HashMap<>();
			simParams.put(
				ModelI.createRunParameterName(TicModel.URI,
											  TicModel.DELAY_PARAMETER_NAME),
				new Duration(0.5, TimeUnit.SECONDS));
			simParams.put(
				ModelI.createRunParameterName(HIOA_Multiplier.URI_PREFIX,
											  HIOA_Multiplier.STEP_NAME),
				0.25);
			simParams.put(
				ModelI.createRunParameterName(HIOA_Multiplier.URI_PREFIX,
											  HIOA_Multiplier.FACTOR_NAME),
				1.0);
			simParams.put(
				ModelI.createRunParameterName(HIOA_Consumer.URI_PREFIX,
											  HIOA_Consumer.STEP_NAME),
				1.0);

			if (REAL_TIME_SIMULATION) {
				atomicModelDescriptors.put(
						HIOA_Consumer.URI_PREFIX,
						RTAtomicHIOA_Descriptor.create(
								HIOA_Consumer.class,
								HIOA_Consumer.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						HIOA_Multiplier.URI_PREFIX,
						RTAtomicHIOA_Descriptor.create(
								HIOA_Multiplier.class,
								HIOA_Multiplier.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						HIOA_Generator.URI_PREFIX,
						RTAtomicHIOA_Descriptor.create(
								HIOA_Generator.class,
								HIOA_Generator.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						TicModel.URI,
						RTAtomicModelDescriptor.create(
								TicModel.class,
								TicModel.URI,
								TimeUnit.SECONDS,
								null));

				coupledModelDescriptors.put(
						HIOA_CoupledModel.URI_PREFIX,
						new RTCoupledHIOA_Descriptor(
								HIOA_CoupledModel.class,
								HIOA_CoupledModel.URI_PREFIX,
								submodels,
								null,
								null,
								connections,
								null,
								null,
								null,
								bindings));

				ArchitectureI architecture =
						new RTArchitecture(
								HIOA_CoupledModel.URI_PREFIX,
								atomicModelDescriptors,
								coupledModelDescriptors,
								TimeUnit.SECONDS);

				SimulatorI se = architecture.constructSimulator();
				se.setSimulationRunParameters(simParams);
				long start = System.currentTimeMillis() + 100;
				((CoordinationRTEngine)se).startRTSimulation(start, 0.0, 10.0);
			} else {
				atomicModelDescriptors.put(
						HIOA_Consumer.URI_PREFIX,
						AtomicHIOA_Descriptor.create(
								HIOA_Consumer.class,
								HIOA_Consumer.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						HIOA_Multiplier.URI_PREFIX,
						AtomicHIOA_Descriptor.create(
								HIOA_Multiplier.class,
								HIOA_Multiplier.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						HIOA_Generator.URI_PREFIX,
						AtomicHIOA_Descriptor.create(
								HIOA_Generator.class,
								HIOA_Generator.URI_PREFIX,
								TimeUnit.SECONDS,
								null));

				atomicModelDescriptors.put(
						TicModel.URI,
						AtomicModelDescriptor.create(
								TicModel.class,
								TicModel.URI,
								TimeUnit.SECONDS,
								null));

				coupledModelDescriptors.put(
						HIOA_CoupledModel.URI_PREFIX,
						new CoupledHIOA_Descriptor(
								HIOA_CoupledModel.class,
								HIOA_CoupledModel.URI_PREFIX,
								submodels,
								null,
								null,
								connections,
								null,
								null,
								null,
								bindings));

				ArchitectureI architecture =
						new Architecture(
								HIOA_CoupledModel.URI_PREFIX,
								atomicModelDescriptors,
								coupledModelDescriptors,
								TimeUnit.SECONDS);

				SimulatorI se = architecture.constructSimulator();
				se.setSimulationRunParameters(simParams);
				se.doStandAloneSimulation(0.0,  10.0);
			}
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
