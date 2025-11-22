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
 * The class <code>ClientGeneratorModel</code> implements a simulation which
 * exports and generates client arrival events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When starting, the model returns from its <code>timeAdvance</code> that
 * it will perform an internal transition immediately at the starting time.
 * At each simulation step, the internal step will generate a client arrival
 * event at some future simulation time and this event will be put in the
 * model event list. Before the execution of the internal step, the simulation
 * algorithm performs an output step which will extract all exported event
 * scheduling view events from the vent list with time of occurrence equals
 * the current state time and propagates them to their destination models.
 * </p>
 * <p>
 * Hence, this model simply repeatedly plans the next arrival, when the next
 * arrival occurrence time is reached, it will perform a simulation step by
 * which the planned arrival is propagated to the server model and a new
 * arrival is planned at some future simulation time.
 * </p>
 * 
 * <ul>
 * <li>Imported events: none</li>
 * <li>Exported events: {@code GeneratedClientArrival}</li>
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
@ModelExternalEvents(exported = {GeneratedClientArrival.class})
public class			ClientGeneratorModel
extends		AtomicES_Model
{
	// ------------------------------------------------------------------------
	// Inner classes
	// ------------------------------------------------------------------------

	/**
	 * The class <code>ClientGeneratorReport</code> implements the simulation
	 * report for the client generator model.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>White-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2023-10-16</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	public static class	ClientGeneratorReport
	extends		AbstractSimulationReport
	{
		private static final long	serialVersionUID = 1L;
		public final double			meanInterarrivalTime;
		public final int			numberOfGeneratedClients;

		/**
		 * create a report.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param modelURI					URI of the model proposing this report.
		 * @param meanInterarrivalTime		mean inter-arrival time of the generated clients during the run.
		 * @param numberOfGeneratedClients	number of client generated during the run.
		 */
		public			ClientGeneratorReport(
			String modelURI,
			double meanInterarrivalTime,
			int numberOfGeneratedClients
			)
		{
			super(modelURI);
			this.meanInterarrivalTime = meanInterarrivalTime;
			this.numberOfGeneratedClients = numberOfGeneratedClients;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			return "ClientGeneratorReport(" +
						"mean interarrival time = "
								+ this.meanInterarrivalTime + ", " +
						"number of generated clients = "
								+ this.numberOfGeneratedClients + ")";
		}
	}

	// ------------------------------------------------------------------------
	// Constants and variables
	// ------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	public static final String			URI = ClientGeneratorModel.class.
																getSimpleName();
	/** mean inter-arrival time of the generated clients during the run.	*/
	protected double					meanInterarrivalTime;
	/**	the random number generator from common math library.				*/
	protected final RandomDataGenerator	rg;
	/** number of client generated during the run.							*/
	protected int						numberOfGeneratedClients;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				ClientGeneratorModel(AtomicSimulatorI simulationEngine)
	throws Exception
	{
		this(ClientGeneratorModel.URI, simulationEngine);
	}

	/**
	 * create a model instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulationEngine		simulation engine enacting the model.
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				ClientGeneratorModel(
		String uri,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		this(uri, TimeUnit.SECONDS, simulationEngine);
	}

	/**
	 * create a model instance.
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
	 * @throws Exception   			<i>TODO</i>.
	 */
	public				ClientGeneratorModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		this.getSimulationEngine().setLogger(new StandardLogger());
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
		this.numberOfGeneratedClients = 0;
		this.rg.reSeedSecure();
		this.meanInterarrivalTime = 5.0;

		super.initialiseState(initialTime);

		GeneratedClientArrival gca = this.generateNextClient(initialTime);
		this.eventList.add(gca);
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent =
						this.currentStateTime.add(this.nextTimeAdvance);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.AtomicModel#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		this.eventList.add(this.generateNextClient(
										this.getCurrentStateTime()));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	{
		return new ClientGeneratorReport(this.getURI(),
										this.meanInterarrivalTime,
										this.numberOfGeneratedClients);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * generate at the <code>current</code> simulation time the next client
	 * arrival event with an interarrival time following an exponential
	 * distribution with the defined mean interarrival time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code current != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param current	current time.
	 * @return			the generated client arrival event.
	 */
	public GeneratedClientArrival		generateNextClient(Time current)
	{
		assert	current != null;

		double delay = this.rg.nextExponential(this.meanInterarrivalTime);
		Client c = new Client(++this.numberOfGeneratedClients);
		Time arrivalTime = current.add(
							new Duration(delay, this.getSimulatedTimeUnit()));
		GeneratedClientArrival event =
							new GeneratedClientArrival(arrivalTime, c);
		this.logMessage("generate a new client arrival at " +
								arrivalTime.getSimulatedTime() + " " +
								this.getSimulatedTimeUnit());
		return event;
	}

	// ------------------------------------------------------------------------
	// Debugging
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		)
	{
		System.out.println(indent + "-----------------------------------");
		System.out.println(indent + "ClientGeneratorModel " + this.uri +
						   " " + this.currentStateTime.getSimulatedTime()
						   + " " + elapsedTime.getSimulatedDuration());
		System.out.println(indent + "-----------------------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "-----------------------------------");
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
		System.out.println(indent + "numberOfGeneratedClients: "
										+ this.numberOfGeneratedClients);
	}
}
//----------------------------------------------------------------------------
