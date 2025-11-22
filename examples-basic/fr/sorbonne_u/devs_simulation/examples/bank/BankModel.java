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
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

import java.util.LinkedList;
import java.util.Map;

// -----------------------------------------------------------------------------
/**
 * The class <code>BankModel</code> implements a model that simulates client
 * service at the bank in the bank example.
 *
 * <p><strong>Description</strong></p>
 * 
 * <ul>
 * <li>Imported events: {@code Arrival}</li>
 * <li>Exported events: none</li>
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
 * <p>Created on : 2021-11-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {Arrival.class}, exported = {})
public class			BankModel
extends		AtomicES_Model
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	public static final String		MEAN_SERVICE_TIME_NAME = "m-s-t";

	/** mean duration of service at the counter.							*/
	protected double				meanServiceTime;
	/** queue of clients waiting for service.								*/
	protected LinkedList<Client>	waitingQueue;
	/** true if the counter is occupied with a client, false otherwise.		*/
	protected boolean				counterOccupied;
	/** random number generator used to generate service durations.			*/
	protected RandomDataGenerator	rgService;
	/** total number of serviced clients.									*/
	protected int					numberOfClients;
	/** cumulated time thta clients have passed in the bank.				*/
	protected double				accumulatedTime;
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a bank model instance.
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
	public				BankModel(
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
	 * return true if the counter is occupied, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the counter is occupied, false otherwise.
	 */
	public boolean		isCounterOccupied()
	{
		return this.counterOccupied;
	}

	/**
	 * set the counter to occupied state given by {@code b}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param b	state to be set.
	 */
	public void			setCounterOccupied(boolean b)
	{
		this.counterOccupied = b;
	}

	/**
	 * generate the time for the end of service of a client which service is
	 * just starting.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null && ret.subtract(getCurrentStateTime()).greaterThan(Duration.zero(this.getSimulatedTimeUnit()))}
	 * </pre>
	 *
	 * @return	the time for the end of service of a client which service is just starting.
	 */
	public Time			generateEndServiceTime()
	{
		// generate a random duration for the next service
		double d = this.rgService.nextExponential(this.meanServiceTime);
		// add this duration to the current time to get the time of end of
		// service
		return this.getCurrentStateTime().add(
							new Duration(d, this.getSimulatedTimeUnit()));
	}

	/**
	 * return true if the client queue is empty.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the client queue is empty.
	 */
	public boolean		clientQueueEmpty()
	{
		return this.waitingQueue.isEmpty();
	}

	/**
	 * add the client {@code c} in the client queue.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code !clientQueueEmpty()}
	 * </pre>
	 *
	 * @param c	the client to be added to the client queue.
	 */
	public void			queueNewClient(Client c)
	{
		this.waitingQueue.add(c);
	}

	/**
	 * remove and return the first client from the client queue.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !clientQueueEmpty()}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the client just removed from the client queue.
	 */
	public Client		removeNextClient()
	{
		assert	!this.clientQueueEmpty() :
				new NeoSim4JavaException("Precondition violation: "
											+ "!clientQueueEmpty()");
		return this.waitingQueue.remove();
	}

	/**
	 * update the accumulated statistics on the number of serviced clients
	 * and total time clients have passed in the bank.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c	the client that has just finished its service.
	 */
	public void			addToAccumulatedTime(Client c)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: c != null");

		// a new client has been serviced
		this.numberOfClients++;
		// add its total time in the bank to the accumulated time passed by
		// all clients in the bank
		this.accumulatedTime +=
			c.totalTime(this.getCurrentStateTime()).getSimulatedDuration();
	}

	/**
	 * set the mean service time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && simParams.containsKey(MEAN_SERVICE_TIME_NAME)}
	 * pre	{@code ((double) simParams.get(MEAN_SERVICE_TIME_NAME)) > 0.0}
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
													MEAN_SERVICE_TIME_NAME);
		if (simParams == null || !simParams.containsKey(name)) {
			throw new MissingRunParameterException(name);
		}

		assert	((double) simParams.get(name)) > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "((double) simParams.get(MEAN_SERVICE_TIME_NAME)) "
						+ "> 0.0");

		this.meanServiceTime = (double) simParams.get(name);
		this.logMessage("mean service time = " + this.meanServiceTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.waitingQueue = new LinkedList<>();
		// create the random number generator
		this.rgService = new RandomDataGenerator();
		// initialise the random number generatpr
		this.rgService.reSeedSecure();
		this.numberOfClients = 0;
		this.accumulatedTime = 0.0;
		this.counterOccupied = false;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		// the transitions in a event scheduling model are implemented by the
		// methods executeOn in the events
		
		// the next instructions are there for logging purposes only
		StringBuffer sb = new StringBuffer("at ");
		sb.append(this.getCurrentStateTime());
		sb.append(": waiting [");
		for (int i = 0 ; i < this.waitingQueue.size() ; i++) {
			sb.append(this.waitingQueue.get(i));
			if (i < this.waitingQueue.size() - 1) {
				sb.append(',');
			}
		}
		sb.append("]; ");
		this.eventListAsString(sb);
		this.logMessage(sb.toString());

		// the call to super will trigger the execution of the next event
		// in the event list
		super.userDefinedInternalTransition(elapsedTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		return new BankModelReport(this.getURI(),
								   this.meanServiceTime,
								   this.numberOfClients,
								   this.accumulatedTime/this.numberOfClients);
	}
}
// -----------------------------------------------------------------------------
