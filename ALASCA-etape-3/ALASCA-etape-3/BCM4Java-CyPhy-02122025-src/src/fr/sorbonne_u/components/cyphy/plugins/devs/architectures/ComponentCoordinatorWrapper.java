package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

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

import java.util.Map;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ParentNotificationOutboundPort;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.MessageLoggingI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentCoordinatorWrapper</code>
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
 * <p>Created on : 2023-11-03</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentCoordinatorWrapper
implements		CoordinatorI
{
	/** port connected to the component holding the parent model.		*/
	protected final ParentNotificationOutboundPort	outboundPort;

	/**
	 * create a wrapper.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code outboundPort != null}
	 * pre	{@code outboundPort.connected()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param outboundPort	port connected to the component holding the parent model.
	 */
	public				ComponentCoordinatorWrapper(
		ParentNotificationOutboundPort outboundPort
		)
	{
		super();

		assert	outboundPort != null :
				new PreconditionException("outboundPort != null");
		try {
			assert	outboundPort.connected() :
					new PreconditionException("outboundPort.connected()");
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		this.outboundPort = outboundPort;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setSimulatedModel(fr.sorbonne_u.devs_simulation.models.interfaces.ModelI)
	 */
	@Override
	public void			setSimulatedModel(ModelI simulatedModel)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isModelSet()
	 */
	@Override
	public boolean		isModelSet()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getSimulatedModel()
	 */
	@Override
	public ModelI		getSimulatedModel()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isParentSet()
	 */
	@Override
	public boolean		isParentSet()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setParent(fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI)
	 */
	@Override
	public void			setParent(CoordinatorI c)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(Duration simulationDuration)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#initialiseSimulation(fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			initialiseSimulation(
		Time simulationStartTime,
		Duration simulationDuration
		)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isSimulationInitialised()
	 */
	@Override
	public boolean		isSimulationInitialised()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#internalEventStep()
	 */
	@Override
	public void			internalEventStep()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#externalEventStep(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalEventStep(Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfLastEvent()
	 */
	@Override
	public Time			getTimeOfLastEvent()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#endSimulation(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			endSimulation(Time endTime)
	{
//		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isLoggerSet()
	 */
	@Override
	public boolean		isLoggerSet()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setLogger(fr.sorbonne_u.devs_simulation.simulators.interfaces.MessageLoggingI)
	 */
	@Override
	public void			setLogger(MessageLoggingI logger)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#logMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void			logMessage(String modelURI, String message)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#toggleDebugMode()
	 */
	@Override
	public void			toggleDebugMode()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#isDebugModeOn()
	 */
	@Override
	public boolean		isDebugModeOn()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#setDebugLevel(int)
	 */
	@Override
	public void			setDebugLevel(int newDebugLevel)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#hasDebugLevel(int)
	 */
	@Override
	public boolean		hasDebugLevel(int debugLevel)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#simulatorAsString()
	 */
	@Override
	public String		simulatorAsString()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getTimeOfStart()
	 */
	@Override
	public Time			getTimeOfStart()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getSimulationEndTime()
	 */
	@Override
	public Time			getSimulationEndTime()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws MissingRunParameterException
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#doStandAloneSimulation(double, double)
	 */
	@Override
	public void			doStandAloneSimulation(
		double simulationStartTime,
		double simulationDuration
		)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#isSimulationRunning()
	 */
	@Override
	public boolean		isSimulationRunning()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#stopSimulation()
	 */
	@Override
	public void			stopSimulation()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationManagementI#getFinalReport()
	 */
	@Override
	public SimulationReportI	getFinalReport()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI#finaliseSimulation()
	 */
	@Override
	public void			finaliseSimulation()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasReceivedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasReceivedExternalEvents(String modelURI)
	{
		try {
			this.outboundPort.hasReceivedExternalEvents(modelURI);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.ParentNotificationI#hasPerformedExternalEvents(java.lang.String)
	 */
	@Override
	public void			hasPerformedExternalEvents(String modelURI)
	{
		try {
			this.outboundPort.hasPerformedExternalEvents(modelURI);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#setCoordinatedEngines(fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI[])
	 */
	@Override
	public void			setCoordinatedEngines(SimulatorI[] coordinatedEngines)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.CoordinatorI#coordinatedEnginesSet()
	 */
	@Override
	public boolean		coordinatedEnginesSet()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}
}
// -----------------------------------------------------------------------------
