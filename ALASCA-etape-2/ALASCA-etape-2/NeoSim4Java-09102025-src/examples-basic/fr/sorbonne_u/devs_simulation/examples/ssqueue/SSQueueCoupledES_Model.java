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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.examples.ssqueue.ClientGeneratorModel.ClientGeneratorReport;
import fr.sorbonne_u.devs_simulation.examples.ssqueue.ServerModel.ServerReport;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.events.EventSource;
import fr.sorbonne_u.devs_simulation.models.events.ReexportedEvent;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

//----------------------------------------------------------------------------
/**
 * The class <code>SSQueueCoupledES_Model</code> implements a coupled model for
 * the example.
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
 * <p>Created on : 2018-07-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SSQueueCoupledES_Model
extends		CoupledModel
{
	// ------------------------------------------------------------------------
	// Inner classes
	// ------------------------------------------------------------------------

	/**
	 * The class <code>SSQueueReport</code> implements the simulation report
	 * for the single-server queue example.
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
	public static class	SSQueueReport
	implements	SimulationReportI
	{
		private static final long 		serialVersionUID = 1L;
		/**  URI of the simulation model.									*/
		public final String				modelURI;
		/** report coming from the server submodel.							*/
		public ServerReport				sr;
		/** report coming from the client generator submodel.				*/
		public ClientGeneratorReport	cgr;

		/**
		 * create the simulation report.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code modelURI != null && !modelURI.isEmpty()}
		 * pre	{@code reports.length == 2}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param modelURI	URI of the model that proposes this report.
		 * @param reports	reports coming from the submodels.
		 */
		public			SSQueueReport(
			String modelURI,
			SimulationReportI[] reports
			)
		{
			super();
			assert	modelURI != null && !modelURI.isEmpty();
			assert	reports.length == 2;

			this.modelURI = modelURI;
			for (int i = 0; i < reports.length; i++) {
				if (reports[i] instanceof ServerReport) {
					this.sr = (ServerReport) reports[i];
				} else {
					this.cgr = (ClientGeneratorReport) reports[i];
				}
			}
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI#getModelURI()
		 */
		@Override
		public String	getModelURI()
		{
			return this.modelURI;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			String ret = "Single server queue report\n";
			ret += "  Configuration\n";
			ret += "    mean interarrival time: " +
								this.cgr.meanInterarrivalTime + "\n";
			ret += "    mean service time: " + this.sr.meanServiceTime + "\n";
			ret += "  Run results\n";
			ret += "    number of generated clients: " +
								this.cgr.numberOfGeneratedClients + "\n";
			ret += "    number of served clients: " +
								this.sr.numberOfServedClients + "\n";
			ret += "    mean total time in system: " +
								this.sr.averageTotalTime.
											getSimulatedDuration() + "\n";
			return ret;
		}
	}

	// ------------------------------------------------------------------------
	// Constants and variables
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	public static final String	URI =
									SSQueueCoupledES_Model.class.getSimpleName();

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
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				SSQueueCoupledES_Model(
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		this(SSQueueCoupledES_Model.URI, simulationEngine, submodels,
			 imported, reexported, connections);
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
	 * @param uri				URI of the coupled model to be created.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				SSQueueCoupledES_Model(
		String uri,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		this(uri, TimeUnit.SECONDS, simulationEngine, submodels, imported,
			 reexported, connections);
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
	 * @param uri				URI of the coupled model to be created.
	 * @param simulatedTimeUnit	time unit used in the simulation by the model.
	 * @param simulationEngine	simulation engine enacting the model.
	 * @param submodels			array of submodels of the new coupled model.
	 * @param imported			map from imported event types to submodels consuming them.
	 * @param reexported		map from event types exported by submodels that are reexported by this coupled model.
	 * @param connections		map connecting event sources to arrays of event sinks among submodels.
	 * @throws Exception		<i>to do</i>.
	 */
	public				SSQueueCoupledES_Model(
		String uri,
		TimeUnit simulatedTimeUnit,
		CoordinatorI simulationEngine,
		ModelI[] submodels,
		Map<Class<? extends EventI>, EventSink[]> imported,
		Map<Class<? extends EventI>, ReexportedEvent> reexported,
		Map<EventSource, EventSink[]> connections
		) throws Exception
	{
		super(uri, simulatedTimeUnit, simulationEngine, submodels, imported,
			  reexported, connections);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.CoupledModel#getFinalReport()
	 */
	@Override
	public SimulationReportI		getFinalReport()
	{
		SimulationReportI[] submodelsReports =
							new SimulationReportI[this.submodels.length];
		for (int i = 0; i < this.submodels.length; i++) {
			submodelsReports[i] = this.submodels[i].getFinalReport();
		}
		return new SSQueueReport(this.getURI(), submodelsReports);
	}
}
//----------------------------------------------------------------------------
