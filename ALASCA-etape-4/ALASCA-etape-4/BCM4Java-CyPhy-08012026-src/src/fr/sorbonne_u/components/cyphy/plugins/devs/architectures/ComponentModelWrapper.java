package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.ModelOutboundPort;
import fr.sorbonne_u.devs_simulation.exceptions.MissingRunParameterException;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.Pair;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentModelWrapper</code>
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
 * <p>Created on : 2023-10-31</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentModelWrapper
implements	ModelI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	/** outbound port connecting the component holding a coupled model to
	 *  the wrapped submodel.												*/
	protected final ModelOutboundPort	outboundPortToModel;
	/** proxy to the simulation engine attached to the wrapped model.		*/
	protected final SimulatorI 			simulationEngine;
	/** URI of the model inbound port of the component holding a coupled
	 *  model to to the wrapped submodel.									*/
	protected final String				parentModelInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code outboundPortToModel != null}
	 * pre	{@code outboundPortToModel.connected()}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param parentModelInboundPortURI
	 */
	public				ComponentModelWrapper(
		ModelOutboundPort outboundPortToModel,
		SimulatorI simulationEngine,
		String parentModelInboundPortURI
		)
	{
		super();

		assert	outboundPortToModel != null :
				new PreconditionException("outboundPortToModel != null");
		try {
			assert	outboundPortToModel.connected() :
					new PreconditionException("outboundPortToModel.connected()");
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		this.outboundPortToModel = outboundPortToModel;
		this.simulationEngine = simulationEngine;
		this.parentModelInboundPortURI = parentModelInboundPortURI;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulationEngine()
	 */
	@Override
	public SimulatorI	getSimulationEngine()
	{
		if (this.simulationEngine != null) {
			return this.simulationEngine;
		} else {
			throw new RuntimeException(
					"getSimulationEngine() must not be called in this context.");
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getURI()
	 */
	@Override
	public String		getURI()
	{
		try {
			return this.outboundPortToModel.getURI();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#hasURI(java.lang.String)
	 */
	@Override
	public boolean		hasURI(String uri)
	{
		try {
			return this.outboundPortToModel.hasURI(uri);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit()
	{
		try {
			return this.outboundPortToModel.getSimulatedTimeUnit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isAtomic()
	 */
	@Override
	public boolean		isAtomic()
	{
		try {
			return this.outboundPortToModel.isAtomic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isParentSet()
	 */
	@Override
	public boolean		isParentSet()
	{
		try {
			return this.outboundPortToModel.isParentSet();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setParent(fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI)
	 */
	@Override
	public void			setParent(CoupledModelI p)
	{
		try {
			if (this.parentModelInboundPortURI != null) {
				this.outboundPortToModel.setParent(
												this.parentModelInboundPortURI);
			} else {
				throw new RuntimeException(
								"setParent can't be called in this context!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getParentURI()
	 */
	@Override
	public String		getParentURI()
	{
		try {
			return this.outboundPortToModel.getParentURI();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isRoot()
	 */
	@Override
	public boolean		isRoot()
	{
		try {
			return this.outboundPortToModel.isRoot();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#closed()
	 */
	@Override
	public boolean		closed()
	{
		try {
			return this.outboundPortToModel.closed();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	{
		try {
			return this.outboundPortToModel.isImportedEventType(ec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getImportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]	getImportedEventTypes()
	{
		try {
			return this.outboundPortToModel.getImportedEventTypes();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	{
		try {
			return this.outboundPortToModel.isExportedEventType(ec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getExportedEventTypes()
	 */
	@Override
	public Class<? extends EventI>[]	getExportedEventTypes()
	{
		try {
			return this.outboundPortToModel.getExportedEventTypes();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(Class<? extends EventI> ce)
	{
		try {
			return this.outboundPortToModel.getEventAtomicSource(ce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		)
	{
		try {
			return this.outboundPortToModel.getEventAtomicSinks(ce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		)
	{
		try {
			this.outboundPortToModel.addInfluencees(modelURI, ce, influencees);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getInfluencees(java.lang.String, java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getInfluencees(
		String modelURI,
		Class<? extends EventI> ce
		)
	{
		try {
			return this.outboundPortToModel.getInfluencees(modelURI, ce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#areInfluencedThrough(java.lang.String, java.util.Set, java.lang.Class)
	 */
	@Override
	public boolean		areInfluencedThrough(
		String modelURI,
		Set<String> destinationModelURIs,
		Class<? extends EventI> ce
		)
	{
		try {
			return this.outboundPortToModel.
					areInfluencedThrough(modelURI, destinationModelURIs, ce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isInfluencedThrough(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public boolean		isInfluencedThrough(
		String modelURI,
		String destinationModelURI,
		Class<? extends EventI> ce
		)
	{
		try {
			return this.outboundPortToModel.
						isInfluencedThrough(modelURI, destinationModelURI, ce);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isTIOA()
	 */
	@Override
	public boolean		isTIOA()
	{
		try {
			return this.outboundPortToModel.isTIOA();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setSimulationRunParameters(Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String, Object> simParams
		) throws MissingRunParameterException
	{
		try {
			this.outboundPortToModel.setSimulationRunParameters(simParams);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}		
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState()
	 */
	@Override
	public void			initialiseState()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#useFixpointInitialiseVariables()
	 */
	@Override
	public boolean		useFixpointInitialiseVariables()
	{
		try {
			return this.outboundPortToModel.useFixpointInitialiseVariables();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.interfaces.VariableInitialisationI#fixpointInitialiseVariables()
	 */
	@Override
	public Pair<Integer, Integer>	fixpointInitialiseVariables()
	{
		try {
			return this.outboundPortToModel.fixpointInitialiseVariables();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getCurrentStateTime()
	 */
	@Override
	public Time			getCurrentStateTime()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#timeAdvance()
	 */
	@Override
	public Duration		timeAdvance()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#logMessage(java.lang.String)
	 */
	@Override
	public void			logMessage(String message)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	{
		try {
			return this.outboundPortToModel.modelAsString(indent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(String indent, Duration elapsedTime)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#showCurrentStateContent(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentStateContent(
		String indent,
		Duration elapsedTime
		)
	{
		throw new RuntimeException(" must not be called over component connections.");
	}
}
// -----------------------------------------------------------------------------
