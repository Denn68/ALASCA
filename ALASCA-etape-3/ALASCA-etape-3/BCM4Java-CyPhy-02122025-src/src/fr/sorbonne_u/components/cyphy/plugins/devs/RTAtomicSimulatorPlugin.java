package fr.sorbonne_u.components.cyphy.plugins.devs;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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

import java.util.Set;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cyphy.AbstractCyPhyComponent;
import fr.sorbonne_u.components.cyphy.ComponentRTScheduler;
import fr.sorbonne_u.components.exceptions.BCMException;
import fr.sorbonne_u.devs_simulation.architectures.Architecture;
import fr.sorbonne_u.devs_simulation.models.Model;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor.RTSchedulerProviderFI;
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptorI;
import fr.sorbonne_u.devs_simulation.models.architectures.RTModelDescriptorI;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTAtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSchedulingI;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI;
import fr.sorbonne_u.exceptions.PreconditionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTAtomicSimulatorPlugin</code> extends an atomic simulator
 * plug-in with the methods required by a real time atomic simulator plug-in.
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
 * <p>Created on : 2020-12-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTAtomicSimulatorPlugin
extends		AtomicSimulatorPlugin
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The functional interface  <code>EventFactoryFI</code> allows to create
	 * event factories for DEVS simulations in BCMCyPhy.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2020-12-17</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	@FunctionalInterface
	public static interface	EventFactoryFI
	{
		/**
		 * create an event with the given time of occurrence.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code true}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param occurrence	simulation time of occurrence of the event ot be created.
		 * @return				an event with the given time of occurrence.
		 */
		public EventI	createEvent(Time occurrence);
	}

	// -------------------------------------------------------------------------
	// Plug-in internal constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	protected static final String	EXECUTOR_SERVICE_URI_PREFIX =
											"simulation-executor-service-URI-";
	protected final String						simulationExecutorServiceURI;
	protected int								simulationExecutorServiceIndex;
	protected Map<String,RTAtomicSimulatorI>	atomicSimulators;

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * create a real time atomic simulator plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				RTAtomicSimulatorPlugin()
	{
		super();

		// AbstractPort.generatePortURI() is just a convenient way to generate
		// an URI.
		this.simulationExecutorServiceURI =
				EXECUTOR_SERVICE_URI_PREFIX + AbstractPort.generatePortURI();
;
	}

	// -------------------------------------------------------------------------
	// Plug-in life-cycle methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void			installOn(ComponentI owner) throws Exception
	{
		assert	owner != null : new PreconditionException("owner != null");

		super.installOn(owner);
		this.simulationExecutorServiceIndex =
						this.createNewExecutorService(
								this.simulationExecutorServiceURI, 1, true);
	}

	// -------------------------------------------------------------------------
	// Plug-in specific methods
	// -------------------------------------------------------------------------

	/**
	 * verify that the simulation architecture is real-time before setting it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code architecture.isRealTime()}
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#setSimulationArchitecture(fr.sorbonne_u.devs_simulation.architectures.Architecture)
	 */
	@Override
	public void			setSimulationArchitecture(Architecture architecture)
	{
		assert	architecture.isRealTime() :
				new PreconditionException("archi.isRealTime()");

		super.setSimulationArchitecture(architecture);
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AbstractSimulatorPlugin#getRootSimulator()
	 */
//	@Override
//	protected RTSimulatorI	getRootSimulator()
//	{
//		return this.atomicSimulators.
//								get(this.localArchitecture.getRootModelURI());
//	}

	/**
	 * return the real-time simulator associated with {@code rtAtomicModelURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulationArchitectureSet() && localArchitecture.isAtomicModel(rtAtomicModelURI)}
	 * pre	{@code isSimulatorSet()}
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the real-time simulator associated with {@code rtAtomicModelURI}.
	 */
	protected RTSimulatorI	getRTSimulator(String rtAtomicModelURI)
	{
		// this getter is maybe to often called to justify verifying the
		// preconditions for each call...
//		assert	this.isSimulationArchitectureSet() &&
//						this.localArchitecture.isAtomicModel(rtAtomicModelURI) :
//				new PreconditionException(
//						"isSimulationArchitectureSet() && "
//						+ "localArchitecture.isAtomicModel(rtAtomicModelURI)");
//		assert	isSimulatorSet() : new PreconditionException("isSimulatorSet()");

		return this.atomicSimulators.get(rtAtomicModelURI);
	}

	/**
	 * create the real time scheduler provider that will be used as a factory
	 * to create the real time scheduler to be used by the simulation engine
	 * to execute its real time tasks.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getOwner().validExecutorServiceURI(simulationExecutorServiceURI)}
	 * pre	{@code getOwner().isSchedulable(simulationExecutorServiceURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the real time scheduler provider that will be used as a factory to create a real time scheduler.
	 */
	protected RTSchedulerProviderFI		createRTSchedulerProvider()
	{
		assert	getOwner().validExecutorServiceURI(
										simulationExecutorServiceURI) :
				new PreconditionException(
						"getOwner().validExecutorServiceURI("
						+ "simulationExecutorServiceURI)");
		assert	getOwner().isSchedulable(simulationExecutorServiceURI) :
				new PreconditionException(
						"getOwner().isSchedulable(simulationExecutorServiceURI)");

		final AbstractCyPhyComponent comp =
							(AbstractCyPhyComponent) this.getOwner();

		return new RTSchedulerProviderFI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RTSchedulingI provide() {
						return new ComponentRTScheduler(
												comp,
												simulationExecutorServiceURI,
												simulationExecutorServiceIndex);
					}
			   };
	}

	/**
	 * collect all real-time atomic simulation engines and put them into
	 * {@code atomicSimulators}
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code atomicSimulators != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param model	a model in the local simulator.
	 */
	protected void	collectAtomicRTEngines(Model model)
	{
		assert	this.atomicSimulators != null :
				new PreconditionException("atomicSimulators != null");

		if (this.localArchitecture.isAtomicModel(model.getURI())) {
			this.atomicSimulators.put(
							model.getURI(),
							(RTAtomicSimulatorI) model.getSimulationEngine());
		} else {
			for (String uri : this.localArchitecture.
										getChildrenModelURIs(model.getURI())) {
				this.collectAtomicRTEngines((Model) model.gatewayTo(uri));
			}
		}
	}

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
	 * 
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#createSimulator()
	 */
	@Override
	public void		createSimulator()
	{
		assert	this.isSimulationArchitectureSet() :
				new PreconditionException("isSimulationArchitectureSet()");
		assert	!this.isSimulatorSet() :
				new PreconditionException("!isSimulatorSet()");

		RTSchedulerProviderFI schedulerProvider =
										this.createRTSchedulerProvider();
		Set<String> atomicModelsURIs =
								this.localArchitecture.getAllAtomicModelsURIs();
		for (String uri : atomicModelsURIs) {
			AtomicModelDescriptor amd =
					(AtomicModelDescriptor)
								this.localArchitecture.getModelDescriptor(uri);
			assert	amd instanceof RTModelDescriptorI :
					new BCMException(
							uri + " does not have a real-time atomic model "
							+ "descriptor!");
			((RTAtomicModelDescriptorI)amd).
								setSchedulerProvider(schedulerProvider);
		}

		super.createSimulator();

		if (this.atomicSimulators != null) {
			this.atomicSimulators.clear();
		} else {
			this.atomicSimulators = new HashMap<>();
		}
		try {
			this.collectAtomicRTEngines((Model)this.getRootModel());
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}

		assert	this.localArchitecture.getAllAtomicModelsURIs().stream().
							allMatch(uri -> atomicSimulators.containsKey(uri)) :
				new AssertionError(
						"localArchitecture.getAllAtomicModelsURIs().stream()."
						+ "allMatch(uri -> atomicSimulators.containsKey(uri))");
	}

	/**
	 * .
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isSimulatorSet()}
	 * post	{@code true}	// no postcondition
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.RTSimulatorI#startRTSimulation(long, double, double)
	 */
	@Override
	public void			startRTSimulation(
		long realTimeOfStart,
		double simulationStartTime,
		double simulationDuration
		)
	{
		assert	isSimulatorSet() : new PreconditionException("isSimulatorSet()");

		for (Entry<String,RTAtomicSimulatorI> e :
											this.atomicSimulators.entrySet()) {
			e.getValue().startRTSimulation(realTimeOfStart,
										   simulationStartTime,
										   simulationDuration);
		}
	}

	/**
	 * trigger an external event at the current simulation time on a simulation
	 * model managed by this plug-in.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code destinationModelURI != null && !destinationModelURI.isEmpty()}
	 * pre	{@code ef != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationModelURI	URI of the simulation model on which the event must be triggered.
	 * @param ef					event factory that will create an external event with occurrence time passed as parameter.
	 * @throws Exception			<i>to do</i>.
	 */
	public void			triggerExternalEvent(
		String destinationModelURI,
		EventFactoryFI ef
		) throws Exception
	{
		assert	destinationModelURI != null && !destinationModelURI.isEmpty() :
				new PreconditionException(
						"destinationModelURI != null && "
						+ "!destinationModelURI.isEmpty()");
		assert	ef != null : new PreconditionException("ef != null");

		Time t = this.atomicSimulators.get(destinationModelURI).
							computeCurrentSimulationTime(destinationModelURI);
		ArrayList<EventI> es = new ArrayList<>();
		es.add(ef.createEvent(t));
		this.storeInput(destinationModelURI, es);
	}
}
// -----------------------------------------------------------------------------
