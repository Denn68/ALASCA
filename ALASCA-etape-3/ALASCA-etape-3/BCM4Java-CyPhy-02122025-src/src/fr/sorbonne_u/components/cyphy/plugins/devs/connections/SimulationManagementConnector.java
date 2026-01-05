package fr.sorbonne_u.components.cyphy.plugins.devs.connections;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

// -----------------------------------------------------------------------------
/**
 * The class <code>SimulatorPluginManagementConnector</code> implements the
 *connector for the offered/required interface
 * <code>SimulatorPluginManagementCI</code>.
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
 * <p>Created on : 2018-06-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			SimulationManagementConnector
extends		AbstractConnector
implements	SimulationManagementCI
{
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#constructSimulator(java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	@Override
	public boolean		constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		return ((SimulationManagementCI)this.offering).
									constructSimulator(modelURI, architecture);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationManagementInboundPortURI()
	 */
	@Override
	public String		getSimulationManagementInboundPortURI() throws Exception
	{
		return ((SimulationManagementCI)this.offering).
										getSimulationManagementInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getModelInboundPortURI()
	 */
	@Override
	public String		getModelInboundPortURI() throws Exception
	{
		return ((SimulationManagementCI)this.offering).getModelInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulatorInboundPortURI()
	 */
	@Override
	public String		getSimulatorInboundPortURI() throws Exception
	{
		return ((SimulationManagementCI)this.offering).
												getSimulatorInboundPortURI();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#connectSupervision(java.lang.String)
	 */
	@Override
	public void			connectSupervision(
		String supervisorNotificationInboundPortURI
		) throws Exception
	{
		((SimulationManagementCI)this.offering).
			connectSupervision(supervisorNotificationInboundPortURI);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#reinitialise()
	 */
	@Override
	public void			reinitialise() throws Exception
	{
		((SimulationManagementCI)this.offering).reinitialise();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart() throws Exception
	{
		return ((SimulationManagementCI)this.offering).getTimeOfStart();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime() throws Exception
	{
		return ((SimulationManagementCI)this.offering).getSimulationEndTime();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception
	{
		((SimulationManagementCI)this.offering).
									setSimulationRunParameters(simParams);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double startTime,
		double simulationDuration
		) throws Exception
	{
		((SimulationManagementCI)this.offering).
					doStandAloneSimulation(startTime, simulationDuration);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception
	{
		((SimulationManagementCI)this.offering).
					startRTSimulation(realTimeOfStart, simulationStartTime,
									  simulationDuration);		
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning() throws Exception
	{
		return ((SimulationManagementCI)this.offering).isSimulationRunning();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#stopSimulation()
	 */
	@Override
	public void			stopSimulation() throws Exception
	{
		((SimulationManagementCI)this.offering).stopSimulation();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation() throws Exception
	{
		((SimulationManagementCI)this.offering).finaliseSimulation();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport() throws Exception
	{
		return ((SimulationManagementCI)this.offering).getFinalReport();
	}
}
// -----------------------------------------------------------------------------
