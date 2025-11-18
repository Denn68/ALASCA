package fr.sorbonne_u.devs_simulation.examples.counters;

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
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTSimulationMain</code> illustrates the basic counter example
 * with only one atomic model that can is simulated alone but in real time.
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
 * <p>Created on : 2022-06-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTSimulationMain
{
	public static void	main(String[] args)
	{
		try {
			BasicCounterModel bcm =
					new BasicCounterModel(
							BasicCounterModel.MODEL_URI_PREFIX,
							TimeUnit.MILLISECONDS,
							new AtomicRTEngine());
			AtomicRTEngine se = (AtomicRTEngine) bcm.getSimulationEngine();
			se.setRTScheduler(AtomicRTEngine.STD_SCHEDULER_PROVIDER.provide());

			Map<String,Object> simParams = new HashMap<>();
			simParams.put(
					ModelI.createRunParameterName(
							BasicCounterModel.MODEL_URI_PREFIX,
							BasicCounterModel.START_VALUE_PARAM_NAME),
					0);
			simParams.put(
					ModelI.createRunParameterName(
							BasicCounterModel.MODEL_URI_PREFIX,
							BasicCounterModel.STEP_VALUE_PARAM_NAME),
					1000.0);
			se.getSimulatedModel().setSimulationRunParameters(simParams);

			Semaphore endSimulationSynchroniser = new Semaphore(1);
			endSimulationSynchroniser.acquire(1);
			se.setSimulationEndSynchroniser(endSimulationSynchroniser);
			long currentAtStart = System.currentTimeMillis();
			long start = currentAtStart + 100;
			double simulationDuration = 10100.0;
			se.startRTSimulation(start, 0.0, simulationDuration);
			endSimulationSynchroniser.acquire();
			System.out.println("simulation duration: " + simulationDuration);
			System.out.println("execution duration: " + 
								(System.currentTimeMillis() - currentAtStart));
			System.out.println(se.getFinalReport());
			System.out.println("Simulation ends.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
// -----------------------------------------------------------------------------
