package fr.sorbonne_u.devs_simulation.models;

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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>Model</code> is the root of the class hierarchy implementing
 * variants of DEVS simulation models for a component plug-in implementing a
 * family of DEVS simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Models have two major distinct but interrelated definitions: a static
 * definition and a dynamic one. The static definition gives an URI to the
 * model and it declares the model imported and exported events. This
 * information is used for model composition prior to simulation runs.
 * The dynamic part attaches a simulation engine to the model (which can
 * be null if the model is enacted by the simulation engine of its parent
 * or an ancestor coupled model). It also includes a simulation time unit
 * (which at this point is assumed to be the same throughout the simulation
 * model architecture) as well as all the data and methods used to execute
 * the simulation.
 * </p>
 * <p>
 * Atomic DEVS simulation models defines the model behaviour in terms of
 * functions, which are translated here in method implementations. These 
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code uri == null || !uri.isEmpty()}
 * invariant	{@code simulatedTimeUnit != null}
 * invariant	{@code simulationEngine == null || simulationEngine.isModelSet()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code !isStateInitialised() || getTimeOfNextEvent().equals(Time.INFINITY) || getTimeOfNextEvent().subtract(getCurrentStateTime()).equals(getNextTimeAdvance())}
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	Model
implements	ModelI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long 				serialVersionUID = 1L;

	// Creation time information

	/** Unique identifier of the model.										*/
	protected final String					uri;
	/** Array of this model imported event types.						 	*/
	protected Class<? extends EventI>[]		importedEventTypes;
	/** Array of this model exported event types.						 	*/
	protected Class<? extends EventI>[]		exportedEventTypes;
	/** The simulation engine enacting this model.							*/
	protected final SimulatorI				simulationEngine;
	/** Time unit used for the simulation clock.							*/
	protected final TimeUnit				simulatedTimeUnit;

	// Composition time information

	/** The parent (coupled) model or null if none.							*/
	protected CoupledModelI					parent;

	// Simulation time information

	/** Time in the simulation corresponding to this current state.			*/
	protected Time							currentStateTime;
	/** time at which the next internal event must be executed.				*/
	protected Time							timeOfNextEvent;
	/** Duration until the time of next event, i.e. the time of next
	 *  event minus the time of the current state.							*/
	protected Duration						nextTimeAdvance;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(Model instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.uri == null || !instance.uri.isEmpty(),
					Model.class,
					instance,
					"uri == null || !uri.isEmpty()");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.simulatedTimeUnit != null,
					Model.class,
					instance,
					"simulatedTimeUnit != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.simulationEngine == null ||
									instance.simulationEngine.isModelSet(),
					Model.class,
					instance,
					"simulationEngine == null || simulationEngine.isModelSet()");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(Model instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					!instance.isStateInitialised() ||
					instance.getTimeOfNextEvent().equals(Time.INFINITY) ||
					instance.getTimeOfNextEvent().subtract(
									instance.getCurrentStateTime()).
										equals(instance.getNextTimeAdvance()),
					Model.class,
					instance,
					"!isStateInitialised() || getTimeOfNextEvent().equals("
					+ "Time.INFINITY) || getTimeOfNextEvent().subtract("
					+ "getCurrentStateTime()).equals(getNextTimeAdvance())");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a model instance with the given URI (if null, one will be
	 * generated) and to be run by the given simulator (or by the one of an
	 * ancestor coupled model if null) using the given time unit for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri					URI of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation time of this model.
	 * @param simulationEngine		simulation engine enacting the model (can be the same as the one used by the parent coupled model).
	 */
	public				Model(
		String uri,
		TimeUnit simulatedTimeUnit,
		SimulatorI simulationEngine
		)
	{
		super();

		assert	uri == null || !uri.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								   + "uri == null || !uri.isEmpty()");
		assert	simulatedTimeUnit != null :
				new NeoSim4JavaException("Precondition violation: "
								   + "simulatedTimeUnit != null");
		assert	simulationEngine == null || !simulationEngine.isModelSet() :
				new NeoSim4JavaException("Precondition violation: "
								   + "simulationEngine != null && "
								   + "!simulationEngine.isModelSet()");

		if (uri != null) {
			this.uri = uri;
		} else {
			this.uri = this.generateModelURI();
		}
		this.simulatedTimeUnit = simulatedTimeUnit;
		this.simulationEngine = simulationEngine;
		this.simulationEngine.setSimulatedModel(this);

		// Postconditions
		assert	this.getURI() != null :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getURI() != null");
		assert	uri == null || this.getURI().equals(uri) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "uri == null || getURI().equals(uri)");
		assert	this.getSimulatedTimeUnit().equals(simulatedTimeUnit) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getSimulatedTimeUnit().equals(simulatedTimeUnit)");
		assert	getSimulationEngine().equals(simulationEngine) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getSimulationEngine().equals(simulationEngine)");

		assert	Model.implementationInvariants(this) :
				new NeoSim4JavaException(
						"Model.implementationInvariants(this)");
		assert	Model.invariants(this) :
				new NeoSim4JavaException("Model.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Redefinition of base object behaviours
	// -------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean		equals(Object obj)
	{
		if (obj instanceof ModelI) {
			return this.uri.equals(((Model)obj).uri);
		} else {
			return false;
		}
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * generate a unique identifier for the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null && !ret.isEmpty()}
	 * </pre>
	 *
	 * @return	a distributed system-wide unique id.
	 */
	protected String	generateModelURI()
	{
		// see http://www.asciiarmor.com/post/33736615/java-util-uuid-mini-faq
		String ret = java.util.UUID.randomUUID().toString();

		assert	ret != null;

		return this.getClass().getSimpleName() + "-" + ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getURI()
	 */
	@Override
	public String		getURI()
	{
		return this.uri;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#hasURI(java.lang.String)
	 */
	@Override
	public boolean		hasURI(String uri)
	{
		return this.getURI().equals(uri);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulatedTimeUnit()
	 */
	@Override
	public TimeUnit		getSimulatedTimeUnit()
	{
		return this.simulatedTimeUnit;
	}

	/**
	 * return the reference to a submodel which URI is {@code modelURI} or
	 * that is the ancestor of the model with {@code modelURI}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code isDescendant(modelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	a descendant model to be reached through its ancestor that is an immediate child of this coupled model.
	 * @return			the reference to a submodel which URI is {@code modelURI} or the ancestor of {@code modelURI}.
	 */
	public abstract ModelI	gatewayTo(String modelURI);

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isParentSet()
	 */
	@Override
	public boolean		isParentSet()
	{
		return this.parent != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#setParent(fr.sorbonne_u.devs_simulation.models.interfaces.CoupledModelI)
	 */
	@Override
	public void			setParent(CoupledModelI p)
	{
		assert	p != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "p != null");
		assert	p instanceof CoupledModelI :
				new NeoSim4JavaException("Precondition violation: "
								   + "p instanceof CoupledModelI");

		this.parent = p;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getParentURI()
	 */
	@Override
	public String		getParentURI()
	{
		return this.parent.getURI();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isRoot()
	 */
	@Override
	public boolean		isRoot()
	{
		return this.parent == null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#closed()
	 */
	@Override
	public boolean		closed()
	{
		throw new RuntimeException("Method closed not implemented yet!");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isImportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isImportedEventType(Class<? extends EventI> ec)
	{
		assert	ec != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ec != null");

		boolean ret = false;
		if (this.importedEventTypes != null) {
			for (int i = 0 ; !ret && i < this.importedEventTypes.length ; i++) {
				ret = (this.importedEventTypes[i].equals(ec));
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getImportedEventTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends EventI>[]	getImportedEventTypes()
	{
		if (this.importedEventTypes == null) {
			return null;
		} else {
			// return a copy to protect the internals of the model
			Class<? extends EventI>[] ret =
							new Class[this.importedEventTypes.length];
			for (int i = 0 ; i < this.importedEventTypes.length ; i++) {
				ret[i] = this.importedEventTypes[i];
			}
			return ret;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isExportedEventType(java.lang.Class)
	 */
	@Override
	public boolean		isExportedEventType(Class<? extends EventI> ec)
	{
		assert	ec != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ec != null");

		boolean ret = false;
		if (this.exportedEventTypes != null) {
			for (int i = 0 ; !ret && i < this.exportedEventTypes.length ; i++) {
				ret = (this.exportedEventTypes[i].equals(ec));
			}
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getExportedEventTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends EventI>[]		getExportedEventTypes()
	{
		if (this.exportedEventTypes == null) {
			return null;
		} else {
			// return a copy to protect the internals of the model
			Class<? extends EventI>[] ret =
								new Class[this.exportedEventTypes.length];
			for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
				ret[i] = this.exportedEventTypes[i];
			}
			return ret;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isTIOA()
	 */
	@Override
	public boolean		isTIOA()
	{
		return true;
	}

	// -------------------------------------------------------------------------
	// Model-simulator relationships
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getSimulationEngine()
	 */
	@Override
	public SimulatorI	getSimulationEngine()
	{
		return this.simulationEngine;
	}

	// -------------------------------------------------------------------------
	// Simulation runs related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		if (this.getSimulationEngine().hasDebugLevel(2)) {
			String prefix = "Model#isStateInitialised " + this.uri + " ";
			this.logMessage(prefix +
							"this.currentStateTime != null " +
										(this.currentStateTime != null));
			this.logMessage(prefix +
					"this.nextTimeAdvance != null " +
										(this.nextTimeAdvance != null));
			this.logMessage(prefix +
					"this.timeOfNextEvent != null " +
										(this.timeOfNextEvent != null));
		}

		return this.currentStateTime != null &&
						this.nextTimeAdvance != null &&
									this.timeOfNextEvent != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState()
	 */
	@Override
	public void			initialiseState()
	{
		this.initialiseState(Time.zero(this.getSimulatedTimeUnit()));
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		assert	initialTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "initialTime != null");
		assert	initialTime.greaterThanOrEqual(
						Time.zero(this.getSimulatedTimeUnit())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "initialTime.greaterThanOrEqual("
						+ "Time.zero(this.getSimulatedTimeUnit()))");

		this.currentStateTime = initialTime.copy();
	}

	// -------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getCurrentStateTime()
	 */
	@Override
	public Time			getCurrentStateTime()
	{
		return this.currentStateTime;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getNextTimeAdvance()
	 */
	@Override
	public Duration		getNextTimeAdvance()
	{
		return this.nextTimeAdvance;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getTimeOfNextEvent()
	 */
	@Override
	public Time			getTimeOfNextEvent()
	{
		return this.timeOfNextEvent;
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#logMessage(java.lang.String)
	 */
	@Override
	public void			logMessage(String message)
	{
		try {
			this.getSimulationEngine().logMessage(this.uri, message);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#modelAsString(java.lang.String)
	 */
	@Override
	public String		modelAsString(String indent)
	{
		StringBuffer ret = new StringBuffer();
		if (this.isRoot()) {
			ret.append(indent);
			ret.append("---------------------------------------------------\n");
		}
		this.modelContentAsString(indent, ret);
		if (this.isRoot()) {
			ret.append(indent);
			ret.append("---------------------------------------------------\n");
		}
		return ret.toString();
	}

	/**
	 * produce a string representation the model content with the given
	 * indentation (perhaps with several lines) in the given string buffer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code indent != null && sb != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param indent		indentation to be used when producing a new line.
	 * @param sb			string buffer to which the content must be added.
	 */
	protected void		modelContentAsString(String indent, StringBuffer sb)
	{
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		sb.append(indent);
		sb.append(name);
		sb.append(" URI = ");
		sb.append(this.uri);
		sb.append('\n');
		sb.append(indent);
		sb.append("---------------------------------------------------\n");
		sb.append(indent);
		sb.append("parent uri = ");
		if (this.isRoot()) {
			sb.append("null");
		} else {
			sb.append(this.getParentURI());
		}
		sb.append('\n');
		sb.append(indent);
		sb.append("time unit  = ");
		sb.append(this.getSimulatedTimeUnit());
		sb.append('\n');
		sb.append(indent);
		if (this.importedEventTypes == null) {
			sb.append("imported   = []\n");
		} else {
			sb.append("imported   = [");
			for (int i = 0 ; i < this.importedEventTypes.length ; i++) {
				sb.append(this.importedEventTypes[i].getName());
				if (i < this.importedEventTypes.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
		}
		sb.append(indent);
		if (this.exportedEventTypes == null) {
			sb.append("exported   = []\n");
		} else {
			sb.append("exported   = [");
			for (int i = 0 ; i < this.exportedEventTypes.length ; i++) {
				sb.append(this.exportedEventTypes[i].getName());
				if (i < this.exportedEventTypes.length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
		}
		sb.append(indent);
		sb.append("simulation engine = ");
		if (this.simulationEngine != null) {
			sb.append(this.getSimulationEngine().simulatorAsString());
		} else {
			sb.append("null");
		}
		sb.append('\n');
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String	toString()
	{
		return this.getClass().getCanonicalName();
	}
}
// -----------------------------------------------------------------------------
