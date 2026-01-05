package fr.sorbonne_u.components.cyphy.plugins.devs;

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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.interfaces.CyPhyReflectionCI;
import fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentEventsExchangingWrapper;
import fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.EventsExchangingConnector;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.EventsExchangingInboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.connections.EventsExchangingOutboundPort;
import fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.EventsExchangingCI;
import fr.sorbonne_u.components.cyphy.plugins.devs.utils.StandardComponentLogger;
import fr.sorbonne_u.components.cyphy.utils.tests.TestScenarioWithSimulation;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.CoupledModel;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.MessageLoggingI;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicSimulatorPlugin</code> implements the behaviours
 * required by a component that holds an atomic simulation model; it acts
 * mostly as a facade for the simulation engine and model that it is holding,
 * passing them most of the calls sometimes with adaptations (glue) to
 * account for the fact that communication among simulation engines and models
 * may pass through component connections.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An atomic simulation plug-in is meant to manage the simulation models
 * held by its component. As such, it performs operations to create the
 * simulation architecture local to the component, for the interconnection
 * of models during the creation of the global inter-components simulator
 * architecture and during the simulation runs themselves to make its
 * simulation engines and models execute the simulation steps.
 * </p>
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
 * <p>Created on : 2018-04-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			AtomicSimulatorPlugin
extends		AbstractSimulatorPlugin
implements	ModelStateAccessI,
			EventsExchangingI
{
	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** name of the run time simulation parameter used to pass the reference
	 *  to the component that runs the simulator.							*/
	public static final String		OWNER_RUNTIME_PARAMETER_NAME = "owner-@#!?";
	/** simulation architecture associated with this plug-in.				*/
	protected Architecture								localArchitecture;
	/** Port to receive events from other simulation models.				*/
	protected EventsExchangingInboundPort				eeip;
	/** Ports map to send events to other simulation models.				*/
	protected Map<String,EventsExchangingOutboundPort>	eePorts;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * create an atomic simulator plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				AtomicSimulatorPlugin()
	{
		this.eePorts =
				new ConcurrentHashMap<String,EventsExchangingOutboundPort>();
	}

	// -------------------------------------------------------------------------
	// Plug-in generic methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#isInitialised()
	 */
	@Override
	public boolean		isInitialised()
	{
		return super.isInitialised() && this.eePorts != null;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null : new PreconditionException("owner != null");
		assert	isSimulationArchitectureSet() :
				new PreconditionException("isSimulationArchitectureSet()");

		super.installOn(owner);

		this.addOfferedInterface(EventsExchangingCI.class);
		this.addRequiredInterface(EventsExchangingCI.class);
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void			initialise() throws Exception
	{
		super.initialise();

		this.eeip = new EventsExchangingInboundPort(
									this.getOwner(),
									this.getPluginURI(),
									this.getPreferredExecutionServiceURI());
		this.eeip.publishPort();
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				if (eeop.connected()) {
					this.getOwner().doPortDisconnection(eeop.getPortURI());
				}
			}
		}

		assert	this.eePorts == null ||
					this.eePorts.values().stream().
						map(eeop -> {	try {
											return !eeop.connected();
										} catch (Exception e) {
											throw new RuntimeException(e);
										}
									}).
						allMatch(b -> b);

		super.finalise();
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void			uninstall() throws Exception
	{
		this.eeip.unpublishPort();
		this.eeip.destroyPort();
		this.eeip = null;
		this.removeOfferedInterface(EventsExchangingCI.class);

		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				assert	!eeop.connected();
				eeop.unpublishPort();
				eeop.destroyPort();
			}
			this.eePorts.clear();
			this.eePorts = null;
		}
		this.removeRequiredInterface(EventsExchangingCI.class);

		super.uninstall();
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * create the simulator from the local simulation architecture and
	 * initialise the variables <code>rootModel</code> with the reference
	 * to the root model object and <code>rootSimulator</code> with the
	 * reference to the root simulator object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationArchitectureSet()}
	 * pre	{@code !isSimulatorSet()}
	 * post	{@code isSimulatorSet()}
	 * </pre>
	 */
	public void			createSimulator()
	{
		assert	this.isSimulationArchitectureSet() :
				new PreconditionException("isSimulationArchitectureSet()");
		assert	!this.isSimulatorSet() :
				new PreconditionException("!isSimulatorSet()");

		this.rootSimulator = this.localArchitecture.constructSimulator();
		this.rootModel = this.rootSimulator.getSimulatedModel();
	}

	/**
	 * return true if the simulation architecture of the plug-in is set.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the simulation architecture of the plug-in is set.
	 */
	public boolean		isSimulationArchitectureSet()
	{
		return this.localArchitecture != null;
	}

	/**
	 * set the simulation architecture of the pug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getURI() != null}
	 * pre	{@code !isSimulationArchitectureSet()}
	 * pre	{@code archi != null}
	 * pre	{@code archi.getRootModelURI().equals(getPluginURI())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param archi		simulation architecture to be associated to the plug-in.
	 */
	public void			setSimulationArchitecture(Architecture archi)
	{
		assert	getPluginURI() != null :
				new PreconditionException("getPluginURI() != null");
		assert	!isSimulationArchitectureSet() :
				new PreconditionException("!isSimulationArchitectureSet()");
		assert	archi != null : new PreconditionException("archi != null");
		assert	archi.getRootModelURI().equals(getPluginURI()) :
				new PreconditionException(
						"archi.getRootModelURI().equals(getPluginURI())");

		this.localArchitecture = archi;
	}

	/**
	 * set the simulation run parameters provided by the test scenario, adding
	 * the owner component reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code testScenario != null && testScenario.hasRunParameters()}
	 * pre	{@code !simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)}
	 * post	{@code !simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)}
	 * </pre>
	 *
	 * @param testScenario	test scenario to be executed by the simulator.
	 * @param simParams		simulation run parameters to be initialised.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			setSimulationRunParameters(
		TestScenarioWithSimulation testScenario,
		Map<String,Object> simParams
		) throws Exception
	{
		assert	testScenario != null && testScenario.hasRunParameters() :
				new PreconditionException(
						"testScenario != null && testScenario."
						+ "hasRunParameters()");
		assert	!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME) :
				new PreconditionException(
						"!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)");

		// add the reference to the component that runs the simulator
		// so that models can get it both to log messages and to call
		// the component for the SIL models.
		simParams.put(OWNER_RUNTIME_PARAMETER_NAME, this.getOwner());
		testScenario.addToRunParameters(simParams);
		super.setSimulationRunParameters(simParams);
		simParams.remove(OWNER_RUNTIME_PARAMETER_NAME);
		assert	!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME) :
				new PostconditionException(
						"!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)");
	}


	// -------------------------------------------------------------------------
	// Methods from SimulationManagementCI
	// -------------------------------------------------------------------------

	/**
	 * return true in the <code>c</code> appears in <code>tab</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null && tab != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c		an instance of {@code Class<?>} representing an event.
	 * @param tab	array of instances of {@code Class<?>} representing events.
	 * @return		true in the <code>c</code> appears in <code>tab</code>.
	 */
	private boolean		appearsIn(Class<?> c, Class<? extends EventI>[] tab)
	{
		assert	c != null && tab != null;
		boolean found = false;
		for (int i = 0 ; i < tab.length && !found ; i++) {
			found = c.equals(tab[i]);
		}
		return found;
	}

	/**
	 * return true if every element in <code>tab1</code> appears in
	 * <code>tab2</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code tab1 != null && tab2 != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param tab1	array of instances of {@code Class<?>} representing events.
	 * @param tab2	array of instances of {@code Class<?>} representing events.
	 * @return		true if every element in <code>tab1</code> appears in <code>tab2</code>.
	 */
	private boolean		includedIn(
		Class<? extends EventI>[] tab1,
		Class<? extends EventI>[] tab2
		)
	{
		assert	tab1 != null && tab2 != null;

		boolean allAppear = true;
		for (Class<?> c : tab1) {
			allAppear &= this.appearsIn(c, tab2);
		}
		return allAppear;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#constructSimulator(java.lang.String, fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelArchitectureI)
	 */
	public boolean		constructSimulator(
		String modelURI,
		ComponentModelArchitectureI architecture
		) throws Exception
	{
		assert	isSimulationArchitectureSet() :
				new AssertionError(
						"PreconditionException: isSimulationArchitectureSet()");
		assert	architecture.isAtomicModel(getPluginURI()) :
				new AssertionError(
						"PreconditionException: "
						+ "architecture.isAtomicModel(getPluginURI())");

		AtomicModelDescriptor amd =
				(AtomicModelDescriptor)architecture.getModelDescriptor(
														this.getPluginURI());

		if (isSimulationArchitectureSet()) {
			if (!this.isSimulatorSet()) {
				this.createSimulator();
			}
			assert	isSimulatorSet();

			assert	this.includedIn(amd.importedEvents,
									this.rootSimulator.getSimulatedModel().
													getImportedEventTypes()) :
					new AssertionError(
							"includedIn(amd.importedEvents, "
							+ "rootSimulator.getSimulatedModel()."
							+ "getImportedEventTypes())");

			assert	this.includedIn(this.rootSimulator.getSimulatedModel().
														getExportedEventTypes(),
									amd.exportedEvents) :
					new AssertionError(
							"includedIn(this.rootSimulator.getSimulatedModel()."
							+ "getExportedEventTypes(), "
							+ "amd.exportedEvents)");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#setSimulationRunParameters(java.util.Map)
	 */
	@Override
	public void			setSimulationRunParameters(
		Map<String,Object> simParams
		) throws Exception
	{
		// add the reference to the component that runs the simulator
		// so that models can get it both to log messages and to call
		// the component for the SIL models.
		assert	!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME) :
				new AssertionError(
						"!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)");
		simParams.put(OWNER_RUNTIME_PARAMETER_NAME, this.getOwner());
		super.setSimulationRunParameters(simParams);
		simParams.remove(OWNER_RUNTIME_PARAMETER_NAME);
		assert	!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME) :
				new AssertionError(
						"!simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)");
	}

	/**
	 * create a standard component logger to be used by atomic models that
	 * the component runs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simParams != null && !simParams.isEmpty()}
	 * pre	{@code simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param simParams	simulation run parameters.
	 * @return			a standard component logger tied with the hosting component.
	 */
	public static MessageLoggingI	createComponentLogger(
		Map<String, Object> simParams
		)
	{
		assert	simParams != null && !simParams.isEmpty() :
				new PreconditionException("simParams != null && !simParams.isEmpty()");
		assert	simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME) :
				new PreconditionException(
						"simParams.containsKey(OWNER_RUNTIME_PARAMETER_NAME)");
		AbstractComponent owner =
				(AbstractComponent) simParams.get(OWNER_RUNTIME_PARAMETER_NAME);
		return new StandardComponentLogger(owner);
	}

	// -------------------------------------------------------------------------
	// Methods from ModelCI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#isAtomic()
	 */
	public boolean		isAtomic() throws Exception
	{
		return true;
	}

	/**
	 * .
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulatorSet()}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		) throws Exception
	{
		assert	isSimulatorSet() :
				new PreconditionException("isSimulatorSet()");

		Set<CallableEventAtomicSink> internals =
									this.getRootModel().getEventAtomicSinks(ce);
		Set<CallableEventAtomicSink> ret = new HashSet<>();
		String[] cripURI =
				this.getOwner().findInboundPortURIsFromInterface(
													CyPhyReflectionCI.class);
		for (CallableEventAtomicSink as : internals) {
			ret.add(new CallableEventAtomicSink(
							as.importingModelURI,
							as.sourceEventType,
							as.sinkEventType,
							new ComponentEventsExchangingWrapper(
									cripURI[0],
									this.eeip.getPortURI()),
							as.converter));
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.ModelCI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void			addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		) throws Exception
	{
		assert	isSimulatorSet() :
				new AssertionError("PreconditionException: isSimulatorSet()");
		assert	modelURI != null && !modelURI.isEmpty() :
				new PreconditionException(
						"modelURI != null && !modelURI.isEmpty()");
		assert	this.rootModel.getURI().equals(modelURI) ||
						((CoupledModelI)this.rootModel).isDescendant(modelURI) :
				new PreconditionException(
						"getURI().equals(modelURI) || isDescendent(modelURI)");
		assert	ce != null : new PreconditionException("ce != null");
		assert	influencees != null && influencees.size() != 0 :
				new PreconditionException(
						"influencees != null && influencees.size() != 0");

		// When adding influencees that reside in other components, the
		// sink references must be converted to references passing through
		// an outbound port of this plug-in owner component
		for (CallableEventAtomicSink caes : influencees) {
			assert	caes.sink instanceof ComponentEventsExchangingWrapper :
					new AssertionError(
						"caes.sink instanceof ComponentEventsExchangingWrapper");
			String inboundPortURI =
				((ComponentEventsExchangingWrapper)caes.sink).
										getComponentReflectionInboundPortURI();
			EventsExchangingOutboundPort eeop = null;
			if (this.eePorts.containsKey(inboundPortURI)) {
				// when a submodel influences a remote model that is already
				// influenced by another submodel
				eeop = this.eePorts.get(inboundPortURI);
			} else {
				// when this is the first submodel to influence the remote
				// model
				eeop = new EventsExchangingOutboundPort(this.getOwner());
				eeop.publishPort();
				this.getOwner().doPortConnection(
						eeop.getPortURI(),
						((ComponentEventsExchangingWrapper)caes.sink).
										getEventsExchangingInboundPortURI(),
						EventsExchangingConnector.class.getCanonicalName());
				this.eePorts.put(inboundPortURI, eeop);
			}
			((ComponentEventsExchangingWrapper)caes.sink).
										setEventsExchangingOutboundPort(eeop);
		}
		this.rootModel.addInfluencees(modelURI, ce, influencees);
	}

	// -------------------------------------------------------------------------
	// Methods from EventsExchangingCI
	// -------------------------------------------------------------------------

	/**
	 * transmit the events to the model which destination is
	 * {@code destinationURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationURI != null && !destinationURI.isEmpty()}
	 * pre	{@code getRootModel() != null}
	 * pre	{@code getRootModel().getURI().equals(destinationURI) || (!getRootModel().isAtomic() && ((CoupledModel)getRootModel()).isDescendant(destinationURI))}
	 * pre	{@code !getRootModel().getURI().equals(destinationURI) || es.stream().allMatch(e -> { try { return isImportedEventType(e.getClass()); } catch (Exception e1) { throw new RuntimeException(e1) ; }})}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI#storeInput(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public void			storeInput(String destinationURI, ArrayList<EventI> es)
	{
		assert	destinationURI != null && !destinationURI.isEmpty() :
				new PreconditionException(
						"destinationURI != null && !destinationURI.isEmpty()");
		try {
			assert	getRootModel() != null :
					new AssertionError("getRootModel() != null");
			assert	getRootModel().getURI().equals(destinationURI) ||
						(!getRootModel().isAtomic() &&
								((CoupledModel)getRootModel()).
												isDescendant(destinationURI)) :
					new AssertionError(
							"getRootModel().getURI().equals(destinationURI) || "
									+ "(!getRootModel().isAtomic() && "
									+ "((CoupledModel)getRootModel())."
									+ "isDescendant(destinationURI))");
			assert	!getRootModel().getURI().equals(destinationURI) ||
						es.stream().allMatch(
							e -> {	try {
										return isImportedEventType(e.getClass());
									} catch (Exception e1) {
										throw new RuntimeException(e1) ;
									}
								 }) :
					new PreconditionException(
							"es.stream().allMatch("
							+ "e -> isImportedEventType(e.getClass())");
		} catch (Exception e2) {
			throw new RuntimeException(e2) ;
		}
		assert	es != null && !es.isEmpty() :
				new PreconditionException("es != null && !es.isEmpty()");

		try {
			if (this.rootModel.hasURI(destinationURI)) {
				((AtomicModel)this.getRootModel()).
												storeInput(destinationURI, es);
			} else {
				(((CoupledModel)this.getRootModel()).
						getAtomicDescendantReference(destinationURI)).
												storeInput(destinationURI, es);
			}
		} catch (Exception e1) {
			throw new RuntimeException(e1) ;
		}
	}

	// -------------------------------------------------------------------------
	// Methods from ModelStateAccessI
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI#getModelStateValue(java.lang.String, java.lang.String)
	 */
	@Override
	public <T> T		getModelStateValue(String modelURI, String name)
	throws Exception
	{
		throw new BCMException(
						"The method getModelStateValue called on a " +
						"simulator plug-in must be defined by the user in a " +
						"subclass to use the approriate way to access the " +
						"values in models.");
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.interfaces.ModelStateAccessI#getModelVariableValue(java.lang.String, java.lang.String)
	 */
	@Override
	public <T> VariableValue<T>	getModelVariableValue(
		String modelURI,
		String name
		) throws Exception
	{
		throw new BCMException(
						"The method getModelVariableValue called on a " +
						"simulator plug-in must be defined by the user in a " +
						"subclass to use the approriate way to access the " +
						"values in models.");
	}

	// -------------------------------------------------------------------------
	// Parent notification methods
	// -------------------------------------------------------------------------


	// -------------------------------------------------------------------------
	// Events exchanging methods
	// -------------------------------------------------------------------------

	/**
	 * transmit all events to each of the models which URI appears in the set
	 * {@code destinationURIs}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationURIs != null && destinationURIs.stream().allMatch(uri -> uri != null && !uri.isEmpty())}
	 * pre	{@code getRootModel() != null}
	 * pre	{@code destinationURIs.stream().allMatch(uri -> getRootModel().getURI().equals(uri) || (!getRootModel().isAtomic() && ((CoupledModel)getRootModel()).isDescendant(uri))))}
	 * pre	{@code destinationURIs.stream().allMatch(uri -> !getRootModel().getURI().equals(destinationURI) || es.stream().allMatch(e -> { try { return isImportedEventType(e.getClass()); } catch (Exception e1) { throw new RuntimeException(e1) ; }}))}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURIs	URIs of atomic models to which the events must be transmitted.
	 * @param es				events to be transmitted.
	 */
	public void			storeInput(
		Set<String> destinationURIs,
		ArrayList<EventI> es
		)
	{
		assert	destinationURIs != null &&
						destinationURIs.stream().allMatch(
								uri -> uri != null && !uri.isEmpty()) :
				new PreconditionException(
						"destinationURIs != null && destinationURIs.stream()."
						+ "allMatch(uri -> uri != null && !uri.isEmpty())");

		for (String destinationURI : destinationURIs) {
			this.storeInput(destinationURI, es);
		}
	}

	// -------------------------------------------------------------------------
	// Simulation management methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.interfaces.SimulationManagementCI#startRTSimulation(long, double, double)
	 */
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		) throws Exception
	{
		assert	getRootSimulator() != null :
				new PreconditionException("getSimulator() != null");
		assert	this.getRootSimulator().isRealTime() :
				new AssertionError("getRootSimulator().isRealTime()");

		this.getRootSimulator().startRTSimulation(
					realTimeOfStart, simulationStartTime, simulationDuration);
	}

	// -------------------------------------------------------------------------
	// DEVS Atomic simulator plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#reinitialise()
	 */
	@Override
	public void			reinitialise() throws Exception
	{
		super.reinitialise();

		if (this.eePorts != null) {
			for (EventsExchangingOutboundPort eeop : this.eePorts.values()) {
				if (eeop.connected()) {
					this.getOwner().doPortDisconnection(eeop.getPortURI());
				}
				eeop.unpublishPort();
				eeop.destroyPort();
			}
			this.eePorts.clear();
		}
		this.rootModel = null;
		this.rootSimulator = null;
	}
}
// -----------------------------------------------------------------------------
