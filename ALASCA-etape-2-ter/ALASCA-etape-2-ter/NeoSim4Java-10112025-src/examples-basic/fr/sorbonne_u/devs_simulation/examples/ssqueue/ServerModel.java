package fr.sorbonne_u.devs_simulation.examples.ssqueue;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.utils.AbstractSimulationReport;
import fr.sorbonne_u.devs_simulation.utils.StandardLogger;

//----------------------------------------------------------------------------
/**
 * The class <code>ServerModel</code> implements the server role in a
 * simulation of a M/M/1 single server queue model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The server imports client arrival events that will come from a generating
 * or another model (e.g., queueing network). These events will be put in
 * the set of stored incoming events by the exporting model (here a client
 * generator model). Then the model will execute an external event step
 * which, in the simulation algorithm, will extract all imported event
 * scheduling view events from the stored events, add them to the event list,
 * execute the other events (here none) and then extract all of the imported
 * vent scheduling view events from the event list that occurs at the current
 * time to execute them in their order of occurrence on the model, including
 * the scheduling of the new generated events that they produce.
 * </p>
 * <p>
 * The <code>ClientArrival</code>
 * class defines its <code>executeOn</code> method to call the method
 * <code>queueClient</code> and, if the server is idle, to call the
 * method <code>beginService</code>. This method dequeue the first client and
 * keep a reference with the variable <code>current</code> and then plans
 * the end of service event for this client. The class
 * <code>EndServiceClient</code> defines its method <code>executeOn</code>
 * to call the method <code>endService</code>. This method frees the server,
 * compute the simulation statistics and then call the method
 * <code>beginService</code> if the client waiting queue is not empty.
 * </p>
 * <p>
 * The statistics computed by the server model is the average total or
 * sojourn time of clients in the system. It is returned in the server
 * model simulation report.
 * </p>
 * 
 * <ul>
 * <li>Imported events: {@code ServerSideClientArrival}</li>
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
 * <p>Created on : 2018-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@ModelExternalEvents(imported = {ServerSideClientArrival.class})
public class			ServerModel
extends		AtomicES_Model
{
	// ------------------------------------------------------------------------
	// Inner classes
	// ------------------------------------------------------------------------

	/**
	 * The class <code>ServerReport</code> implement the simulation report
	 * for the server model by returning the statistics computed during
	 * the simulation run i.e., the average total or sojourn time of clients
	 * in the system.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-11</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 * @version	$Name$ -- $Revision$ -- $Date$
	 */
	public static class	ServerReport
	extends		AbstractSimulationReport
	{
		private static final long serialVersionUID = 1L;
		public final int			numberOfServedClients;
		public final double		meanServiceTime;
		public final Duration	averageTotalTime;

		public			ServerReport(
			String modelURI,
			int numberOfServedClients,
			double meanServiceTime,
			Duration averageTotalTime
			)
		{
			super(modelURI);
			this.numberOfServedClients = numberOfServedClients;
			this.meanServiceTime = meanServiceTime;
			this.averageTotalTime = averageTotalTime;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "ServerReport(number of served clients = "
										+ this.numberOfServedClients + ", " +
                                "mean service time = "
										+ this.meanServiceTime + ", " +
                                "average total time = "
										+ this.averageTotalTime + ")";
		}
	}

	// ------------------------------------------------------------------------
	// Constants and variables
	// ------------------------------------------------------------------------

	private static final long 			serialVersionUID = 1L;
	public static final String			URI = "ServerModel";
	/** mean service time used in the current run.							*/
	protected double					meanServiceTime;
	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg;
	/** queue of clients waiting to be serviced.							*/
	protected final Queue<Client>		waitingQueue;
	/** client currently serviced.											*/
	protected Client					current;
	/** average total time that clients have passed in the system, waiting
	 * 	and service included, during the current run.						*/
	protected double					averageTotalTime;
	/** number of serviced clients during the current run.					*/
	protected int						numberOfServedClients;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a server model with a generated URI and a simulation time unit
	 * set to seconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param simulationEngine	the simulation engine associated to the model.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ServerModel(AtomicSimulatorI simulationEngine)
	throws Exception
	{
		this(ServerModel.URI, simulationEngine);
	}

	/**
	 * create a server model with the given URI and a simulation time unit
	 * set to seconds.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulationEngine	the simulation engine associated to the model.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ServerModel(
		String uri,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		this(uri, TimeUnit.SECONDS, simulationEngine);
	}

	/**
	 * create a server model with the given URI and simulation time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri				URI of the model.
	 * @param simulatedTimeUnit	time unit for this model.
	 * @param simulationEngine	the simulation engine associated to the model.
	 * @throws Exception		<i>to do</i>.
	 */
	public				ServerModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());
		this.waitingQueue = new LinkedList<Client>();
		this.rg = new RandomDataGenerator();
	}

	// ------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		this.meanServiceTime = 3.0;
		this.rg.reSeedSecure();
		this.waitingQueue.clear();
		this.current = null;
		this.averageTotalTime = 0.0;
		this.numberOfServedClients = 0;
		super.initialiseState(initialTime);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	{
		return new ServerReport(
						this.getURI(),
						this.numberOfServedClients,
						this.meanServiceTime,
						new Duration(this.averageTotalTime,
									 this.getSimulatedTimeUnit()));
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * return true if the server is currently idle.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the server is currently idle.
	 */
	public boolean		isIdle()
	{
		return this.current == null;
	}

	/**
	 * insert a client in the waiting queue.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c	client to be inserted.
	 */
	public void			queueClient(Client c)
	{
		assert	c != null;

		c.setArrivalTime(this.currentStateTime);
		this.waitingQueue.add(c);
		this.logMessage("server queues a client at " +
							this.currentStateTime.getSimulatedTime() +
							" " + this.getSimulatedTimeUnit());
	}

	/**
	 * perform the actions required when beginning the service of a client.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			beginService()
	{
		assert	this.current == null && !this.waitingQueue.isEmpty();


		this.current = this.waitingQueue.remove();

		this.logMessage("server begins client service at " +
							this.currentStateTime.getSimulatedTime() +
							" " + this.getSimulatedTimeUnit());

		double delay = this.rg.nextExponential(this.meanServiceTime);
		Time endServiceTime =
			this.currentStateTime.add(new Duration(delay,
											this.getSimulatedTimeUnit()));
		EndServiceClient esc = new EndServiceClient(endServiceTime, null);
		this.eventList.add(esc);
		this.logMessage("server plans client end of service at " +
							endServiceTime.getSimulatedTime() +
							" " + this.getSimulatedTimeUnit());
	}

	/**
	 * perform the actions required when ending the service of a client.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			endService()
	{
		double totalTime =
			this.currentStateTime.subtract(
					this.current.getArrivalTime()).getSimulatedDuration();
		double a = ((double)this.numberOfServedClients)/
							((double)(this.numberOfServedClients + 1));
		double b = 1/((double)(this.numberOfServedClients + 1));
		this.averageTotalTime =
						a * this.averageTotalTime + b * totalTime;
		this.numberOfServedClients++;

		this.logMessage("server ends client service at " +
							this.currentStateTime.getSimulatedTime() +
							" " + this.getSimulatedTimeUnit() +
							" number of served clients is " +
							this.numberOfServedClients);

		this.current = null;
		if (!this.waitingQueue.isEmpty()) {
			this.beginService();
		}
	}

	// ------------------------------------------------------------------------
	// Debugging
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		System.out.println(indent + "--------------------");
		System.out.println(indent + "ServerModel " + this.uri +
						   " " + this.currentStateTime.getSimulatedTime()
						   + " " + elapsedTime.getSimulatedDuration());
		System.out.println(indent + "--------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "--------------------");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		super.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "averageTotalTime: " +
												this.averageTotalTime);
		System.out.println(indent + "numberOfServedClients: " +
												this.numberOfServedClients);
		System.out.println(indent + "current client: " + this.current);
		System.out.println(indent + "waitingQueue: " +
												this.waitingQueueAsString());
	}

	/**
	 * return a {@code String} representation of the waiting queue.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	a {@code String} representation of the waiting queue.
	 */
	protected String		waitingQueueAsString()
	{
		String ret = "WaitingQueue[";
		Iterator<Client> iter = this.waitingQueue.iterator();
		int n = this.waitingQueue.size();
		while (iter.hasNext()) {
			ret += iter.next();
			n--;
			if (n > 0) {
				ret += ", ";
			}
		}
		ret += "]";
		return ret;
	}
}
//----------------------------------------------------------------------------
