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

import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.ArrayList;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>ClientGenerator</code> implements a model that generates
 * client arrivals in the bank example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: {@code Arrival}</li>
 * </ul>
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
 * <p>
 * Strictly speaking, as Java objects, model instances have no invariant because
 * they are not fully initialised by their constructors but rather in the
 * process of starting a simulation run.
 * </p>
 * 
 * <p>Created on : 2022-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {}, exported = {Arrival.class})
public class			ClientGenerator
extends		AtomicModel
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 		serialVersionUID = 1L;
	public static final String		MEAN_INTERARRIVAL_TIME_NAME = "m-i-t";

	/** mean delay between client arrivals.									*/
	protected double				meanInterArrivalTime;
	/** random number generator to generate inter-arrival delays.			*/
	protected RandomDataGenerator	rgArrival;
	/** delay to next arrival as a {@code Duration}.						*/
	protected Duration				timeToNextArrival;
	/** total number of generated clients, for statistical purposes.		*/
	protected int					numberOfGeneratedClients;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a client generator instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>to do</i>.
	 */
	public				ClientGenerator(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);
		// set a standard logger (printing on standard terminal) for
		// log messages
		this.getSimulationEngine().setLogger(new StandardLogger()) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * set the mean inter-arrival time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && simParams.containsKey(MEAN_INTERARRIVAL_TIME_NAME)}
	 * pre	{@code ((double) simParams.get(MEAN_INTERARRIVAL_TIME_NAME)) > 0.0}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.Model#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		String name = ModelI.createRunParameterName(this.getURI(),
													MEAN_INTERARRIVAL_TIME_NAME);
		if (simParams == null || !simParams.containsKey(name)) {
			throw new MissingRunParameterException(name);
		}

		assert	((double) simParams.get(name)) > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "((double) simParams.get(MEAN_INTERARRIVAL_TIME_NAME))"
						+ " > 0.0");

		this.meanInterArrivalTime = (double) simParams.get(name);
		this.logMessage("mean interrival time = " + this.meanInterArrivalTime);
	}

	/**
	 * generate the next inter-arrival time as a pseudo-random number from an
	 * exponential distribution with the provided mean.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the next inter-arrival time as a pseudo-random number from an exponential distribution with the provided mean.
	 */
	public double		generateNextArrivalTime()
	{
		return this.rgArrival.nextExponential(this.meanInterArrivalTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.numberOfGeneratedClients = 0;
		// create the random number generator
		this.rgArrival = new RandomDataGenerator();
		// initialise the random number generatpr
		this.rgArrival.reSeedSecure();
		// generate the delay until the first client arrival
		this.timeToNextArrival =
				new Duration(this.generateNextArrivalTime(),
							 this.getSimulatedTimeUnit());

		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		return this.timeToNextArrival;	
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#output()
	 */
	@Override
	public ArrayList<EventI>	output()
	{
		// compute the current simulation time
		Time t = this.getCurrentStateTime().add(this.getNextTimeAdvance());
		// create the array list to be returned
		ArrayList<EventI> ret = new ArrayList<EventI>();
		// create an arrival event now
		Arrival a = new Arrival(t);
		this.logMessage("newly generated client = " + a);
		// add the event to the result and return it
		ret.add(a);
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// accumulate statistics
		this.numberOfGeneratedClients++;
		// generate the time of arrival for the next client
		this.timeToNextArrival =
				new Duration(this.generateNextArrivalTime(),
							 this.getSimulatedTimeUnit());
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new ClientGeneratorReport(this.getURI(),
										 this.meanInterArrivalTime,
										 this.numberOfGeneratedClients);
	}
}
// -----------------------------------------------------------------------------
