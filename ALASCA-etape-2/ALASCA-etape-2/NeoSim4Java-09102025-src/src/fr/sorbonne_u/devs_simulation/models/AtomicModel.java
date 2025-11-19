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

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;
import fr.sorbonne_u.devs_simulation.models.events.CallableEventAtomicSink;
import fr.sorbonne_u.devs_simulation.models.events.EventAtomicSource;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventSink;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI;
import fr.sorbonne_u.devs_simulation.models.interfaces.ModelI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.simulators.AtomicEngine;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.AtomicSimulatorI;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicModel</code> implements the most general methods and
 * instance variables for DEVS atomic models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The implementation provided here follows the spirit of the description
 * appearing in:
 * </p>
 * <p>
 * H. Vangheluwe, Discrete Event System Specification (DEVS) formalism,
 * courseware, 2001.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code importedEventTypes != null}
 * invariant	{@code exportedEventTypes != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * TODO: check imported/exported events with annotations
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AtomicModel
extends		Model
implements	AtomicModelI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;

	// Composition time information.

	/** When not stand alone, the set of other models that must receive
	 *  events from this model.	 											*/
	protected Map<Class<? extends EventI>,
				  Set<CallableEventAtomicSink>>		influencees;
	
	/** Input events from the current simulation step waiting to be executed
	 *  by this model; can be accessed concurrently for real-time simulation
	 *  so it uses a Vector that is thread safe.							*/
	protected final Vector<EventI>					currentStoredEvents;

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
	protected static boolean	implementationInvariants(AtomicModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.importedEventTypes != null,
					AtomicModel.class,
					instance,
					"importedEventTypes != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.exportedEventTypes != null,
					AtomicModel.class,
					instance,
					"exportedEventTypes != null");
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
	protected static boolean	invariants(AtomicModel instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an atomic simulation model with the given URI (if null, one will
	 * be generated) and to be run by the given simulator (or by the one of an
	 * ancestor coupled model if null) using the given time unit for its clock.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri == null || !uri.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code simulationEngine == null || !simulationEngine.isModelSet()}
	 * pre	{@code simulationEngine == null || simulationEngine instanceof AtomicEngine}
	 * post	{@code !isDebugModeOn()}
	 * post	{@code getURI() != null && !getURI().isEmpty()}
	 * post	{@code uri == null || getURI().equals(uri)}
	 * post	{@code getSimulatedTimeUnit().equals(simulatedTimeUnit)}
	 * post	{@code getSimulationEngine().equals(simulationEngine)}
	 * </pre>
	 *
	 * @param uri					unique identifier of the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param simulationEngine		simulation engine enacting the model.
	 */
	public				AtomicModel(
		String uri,
		TimeUnit simulatedTimeUnit,
		AtomicSimulatorI simulationEngine
		)
	{
		super(uri, simulatedTimeUnit, simulationEngine);

		assert	simulationEngine == null ||
									simulationEngine instanceof AtomicEngine :
				new NeoSim4JavaException("Precondition violation: "
								+ "simulationEngine == null || "
								+ "simulationEngine instanceof AtomicEngine");

		this.importedEventTypes =
				AtomicModel.getImportedEventTypes(this.getClass());
		this.exportedEventTypes =
				AtomicModel.getExportedEventTypes(this.getClass());
		this.influencees = new HashMap<Class<? extends EventI>,
									   Set<CallableEventAtomicSink>>();

		this.currentStoredEvents = new Vector<EventI>();

		assert	AtomicModel.implementationInvariants(this) :
				new NeoSim4JavaException(
						"AtomicModel.implementationInvariants(this)");
		assert	AtomicModel.invariants(this) :
				new NeoSim4JavaException("AtomicModel.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Model manipulation methods (e.g., description, composition, ...)
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#gatewayTo(java.lang.String)
	 */
	@Override
	public ModelI		gatewayTo(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty():
				new NeoSim4JavaException(
						"Precondition violation:"
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	this.getURI().equals(modelURI):
				new NeoSim4JavaException(
						"Precondition violation: isDescendant(modelURI)");

		return this;
	}

	/**
	 * return the imported event types of this atomic model from the
	 * corresponding annotations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c	the class defining the model.
	 * @return	the imported event types of this atomic model from the corresponding annotations.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends EventI>[] getImportedEventTypes(
		Class<? extends AtomicModel> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Vector<Class<? extends EventI>> temp =
							new Vector<Class<? extends EventI>>();
		// TODO: add @Inherited to the annotations to avoid this traversal.
		Class<? extends AtomicModel> current = c;
		while (!current.equals(AtomicModel.class)) {
			ModelExternalEvents a =
				current.getAnnotation(ModelExternalEvents.class);
			if (a != null) {
				for (int i = 0 ; i < a.imported().length ; i++) {
					temp.add(a.imported()[i]);
				}
			}
			current = (Class<? extends AtomicModel>) current.getSuperclass();
		}

		return (Class<? extends EventI>[]) temp.toArray(new Class<?>[] {});
	}

	/**
	 * return the exported event types of this atomic model from the
	 * corresponding annotations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code c != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c	the class defining the model.
	 * @return	the exported event types of this atomic model from the corresponding annotations.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends EventI>[] getExportedEventTypes(
		Class<? extends AtomicModel> c
		)
	{
		assert	c != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "c != null");

		Vector<Class<? extends EventI>> temp =
							new Vector<Class<? extends EventI>>();
		Class<? extends AtomicModel> current = c;
		while (!current.equals(AtomicModel.class)) {
			ModelExternalEvents a =
				current.getAnnotation(ModelExternalEvents.class);
			if (a != null) {
				for (int i = 0 ; i < a.exported().length ; i++) {
					temp.add(a.exported()[i]);
				}
			}
			current = (Class<? extends AtomicModel>) current.getSuperclass();
		}

		return (Class<? extends EventI>[]) temp.toArray(new Class<?>[] {});
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSource(java.lang.Class)
	 */
	@Override
	public EventAtomicSource	getEventAtomicSource(
		Class<? extends EventI> ce
		)
	{
		assert	ce != null && this.isExportedEventType(ce) :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null && isExportedEventType(ce)");

		return new EventAtomicSource(this.getURI(), ce, ce);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#getEventAtomicSinks(java.lang.Class)
	 */
	@Override
	public Set<CallableEventAtomicSink>	getEventAtomicSinks(
		Class<? extends EventI> ce
		)
	{
		assert	ce != null && this.isImportedEventType(ce) :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null && isImportedEventType(ce)");

		Set<CallableEventAtomicSink> ret = new HashSet<>();
		if (this.isImportedEventType(ce)) {
			CallableEventAtomicSink es =
				new CallableEventAtomicSink(this.getURI(), ce, ce, this);
			ret.add(es);
		}
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#addInfluencees(java.lang.String, java.lang.Class, java.util.Set)
	 */
	@Override
	public void				addInfluencees(
		String modelURI,
		Class<? extends EventI> ce,
		Set<CallableEventAtomicSink> influencees
		)
	{
		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#addInfluencees " +
										this.uri + " " + ce.getName());
		}

		assert	getURI().equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getURI().equals(modelURI)");
		assert	ce != null && isExportedEventType(ce) :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null && isExportedEventType(ce)");
		assert	influencees != null && influencees.size() > 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "influencees != null && influencees.size() > 0");

		if (this.influencees.containsKey(ce)) {
			this.influencees.get(ce).addAll(influencees);
		} else {
			Set<CallableEventAtomicSink> s =
									new HashSet<CallableEventAtomicSink>();
			s.addAll(influencees);
			this.influencees.put(ce, s);			
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
		assert	this.uri.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
							+ "getURI().equals(modelURI)");
		assert	ce != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null");

		assert	this.isExportedEventType(ce);
		return this.influencees.get(ce);
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
		assert	this.uri.equals(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getURI().equals(modelURI)");
		assert	destinationModelURIs != null && !destinationModelURIs.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "destinationModelURIs != null && "
						+ "!destinationModelURIs.isEmpty()");
		assert	destinationModelURIs.stream().allMatch(
										uri -> uri != null && !uri.isEmpty()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "destinationModelURIs.stream().allMatch("
						+ "uri -> uri != null && !uri.isEmpty())");
		assert	ce != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null");

		if (this.getURI().equals(modelURI)) {
			if (this.influencees.containsKey(ce)) {
				return destinationModelURIs.stream().allMatch(
						m -> { try {
								return isInfluencedThrough(modelURI, m, ce);
							   } catch (Exception e) {
								throw new RuntimeException(e) ;
							   }
							 });
			} else {
				return false;
			}
		} else {
			return false;
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
		assert	destinationModelURI != null && !destinationModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "destinationModelURI != null && "
						+ "!destinationModelURI.isEmpty()");
		assert	ce != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "ce != null");


		if (this.getURI().equals(modelURI)) {
			if (this.influencees.containsKey(ce)) {
				boolean ret = false;
				for (EventSink es : this.influencees.get(ce)) {
					ret = ret || (destinationModelURI.
												equals(es.importingModelURI));
					if (ret) break;
				}
				return ret;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// ------------------------------------------------------------------------
	// DEVS standard protocol and related methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isCoordinated()
	 */
	@Override
	public boolean		isCoordinated()
	{
		return !this.getSimulationEngine().isRealTime();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isStateInitialised()
	 */
	@Override
	public boolean		isStateInitialised()
	{
		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#isStateInitialised "
					+ "this.currentStoredEvents != null "
							+ (this.currentStoredEvents != null));
		}

		return super.isStateInitialised() && this.currentStoredEvents != null;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#initialiseState(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			initialiseState(Time initialTime)
	{
		super.initialiseState(initialTime);

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.currentStateTime.add(this.nextTimeAdvance);
		this.currentStoredEvents.clear();
	}

//	/**
//	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#clockSynchronised()
//	 */
//	@Override
//	public boolean		clockSynchronised()
//	{
//		assert	this.isStateInitialised() :
//				new NeoSim4JavaAssertionError("Precondition violation: "
//												+ "isStateInitialised()");
//
//		// TODO
//		return true;
//	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#internalTransition()
	 */
	@Override
	public void			internalTransition()
	{
		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#internalTransition for " + this.uri
								+ " at " + this.getTimeOfNextEvent());
		}

		assert	getTimeOfNextEvent().lessThan(Time.INFINITY) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getTimeOfNextEvent().lessThan(Time.INFINITY)");
		assert	getNextTimeAdvance().equals(
						getTimeOfNextEvent().subtract(getCurrentStateTime())) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getNextTimeAdvance().equals("
						+ "getTimeOfNextEvent().subtract(getCurrentStateTime()))");

		Duration elapsedTime =
			this.getTimeOfNextEvent().subtract(this.getCurrentStateTime());
		this.currentStateTime = this.getTimeOfNextEvent();

		// the actual user defined state transition function
		this.userDefinedInternalTransition(elapsedTime);

		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.currentStateTime.add(this.nextTimeAdvance);

		assert	getTimeOfNextEvent().equals(Time.INFINITY)
					|| getTimeOfNextEvent().subtract(getCurrentStateTime()).
												equals(getNextTimeAdvance()) :
				new NeoSim4JavaException("Postcondition violation:"
						+ "getTimeOfNextEvent().equals(Time.INFINITY) "
						+ "|| getTimeOfNextEvent().subtract(getCurrentStateTime())."
						+ "equals(getNextTimeAdvance())");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedInternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedInternalTransition(Duration elapsedTime)
	{
		assert	elapsedTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "elapsedTime != null");
		assert	elapsedTime.lessThan(Duration.INFINITY) :
				new NeoSim4JavaException("Precondition violation: "
						+ "elapsedTime.lessThan(Duration.INFINITY)");
		assert	getCurrentStateTime().equals(getTimeOfNextEvent()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentStateTime().equals(getTimeOfNextEvent())");

		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#externalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			externalTransition(Duration elapsedTime)
	{
		assert	elapsedTime != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "elapsedTime != null");
		boolean isRealTime;
		try {
			isRealTime = this.getSimulationEngine().isRealTime();
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		if (isRealTime) {
			// in the current implementation, when in a real time simulation,
			// the tolerance on the order of events must take into account the
			// precision of the underlying scheduling clock.
			if (!elapsedTime.lessThanOrEqual(getNextTimeAdvance())) {
				StringBuffer sb = new StringBuffer("Warning: ");
				sb.append(this.uri);
				sb.append("#externalTransition elspsed time ");
				sb.append(elapsedTime.getSimulatedDuration());
				sb.append(" is larger than the next time advance ");
				sb.append(getNextTimeAdvance().getSimulatedDuration());
				sb.append("!");
				System.err.println(sb.toString());
			}
		} else {
			assert	elapsedTime.lessThanOrEqual(getNextTimeAdvance()) :
					new NeoSim4JavaException("Precondition violation: "
									+ "elapsedTime.lessThanOrEqual("
									+ "getNextTimeAdvance())");
		}
		assert	getCurrentStateTime().lessThanOrEqual(getTimeOfNextEvent()) :
				new NeoSim4JavaException("Precondition violation: "
								+ "getCurrentStateTime().lessThanOrEqual("
								+ "getTimeOfNextEvent())");

		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel>>externalTransition for " + this.uri
								+ " at " + this.getCurrentStateTime());
		}

		this.currentStateTime = this.currentStateTime.add(elapsedTime);
		// the actual user-defined state transition function
		this.userDefinedExternalTransition(elapsedTime);
		if (!this.currentStoredEvents.isEmpty()) {
			this.currentStoredEvents.clear();
		}
		this.nextTimeAdvance = this.timeAdvance();
		this.timeOfNextEvent = this.currentStateTime.add(this.nextTimeAdvance);

		assert	getTimeOfNextEvent().equals(Time.INFINITY)
					|| getTimeOfNextEvent().subtract(getCurrentStateTime()).
												equals(getNextTimeAdvance()) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getTimeOfNextEvent().equals(Time.INFINITY) "
						+ "|| getTimeOfNextEvent().subtract(getCurrentStateTime())."
						+ "equals(getNextTimeAdvance())");
	}

	/**
	 * The class <code>Destination</code> gathers information
	 * about the destination for the exchange of external events.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}
	 * </pre>
	 * 
	 * <p>Created on : 2018-07-06</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	Destination
	{
		public final String			destinationURI;
		public EventsExchangingI	destination;

		public				Destination(
			String destinationURI,
			EventsExchangingI destination
			)
		{
			super();
			this.destinationURI = destinationURI;
			this.destination = destination;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean		equals(Object obj)
		{
			if (obj instanceof Destination) {
				return this.destinationURI.equals(
										((Destination)obj).destinationURI);
			} else {
				return false;
			}
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#userDefinedExternalTransition(fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			userDefinedExternalTransition(Duration elapsedTime)
	{
		assert	elapsedTime != null :
				new NeoSim4JavaException("Precondition violation: "
									+ "elapsedTime != null");
		assert	elapsedTime.lessThanOrEqual(getNextTimeAdvance()) :
				new NeoSim4JavaException("Precondition violation: "
									+ "elapsedTime.lessThanOrEqual("
									+ "getNextTimeAdvance())");
		assert	getCurrentStateTime().lessThanOrEqual(getTimeOfNextEvent()) :
				new NeoSim4JavaException("Precondition violation: "
									+ "getCurrentStateTime().lessThanOrEqual("
									+ "getTimeOfNextEvent())");

		// By default, do nothing.
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#produceOutput(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			produceOutput(Time current)
	{
		assert	current != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "current != null");

		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#produceOutput >> " + this.uri);
		}
		// Implementation of [Vangheluwe, 2001] following the principle
		// of the closure of DEVS models under coupling i.e., when the
		// coupled model is considered as a stand alone atomic model
		// executed by a unique simulation engine. In this case, the
		// behaviour is the same for atomic models and coupled models
		// considered as atomic models.
		ArrayList<EventI> currentOutputEvents = this.output();
		if (currentOutputEvents != null) {
			Map<Destination,ArrayList<EventI>> tempMap =
								new HashMap<Destination,ArrayList<EventI>>();
			for (int i = 0 ; i < currentOutputEvents.size() ; i++) {
				@SuppressWarnings("unchecked")
				Class<EventI> eventType =
					(Class<EventI>)currentOutputEvents.get(i).getClass();
				if (this.influencees.containsKey(eventType)) {
					for (CallableEventAtomicSink es :
											this.influencees.get(eventType)) {
						Destination dest =
							new Destination(es.importingModelURI, es.sink);
						if (tempMap.containsKey(dest)) {
							tempMap.get(dest).add(es.converter.convert(
											  currentOutputEvents.get(i)));
						} else {
							ArrayList<EventI> hs = new ArrayList<EventI>();
							hs.add(es.converter.convert(
												currentOutputEvents.get(i)));
							tempMap.put(dest, hs);
						}
					}
				}
			}
			for (Destination d : tempMap.keySet()) {
				d.destination.storeInput(d.destinationURI, tempMap.get(d));
			}
		}
		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel#produceOutput << " + this.uri);
		}
	}

	/**
	 * actually stores the events in the internal data structure of the model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param destinationURI	URI of the model to which {@code es} must be delivered.
	 * @param es				received events.
	 */
	public void			actualStoreInput(
		String destinationURI,
		ArrayList<EventI> es)
	{
		this.currentStoredEvents.addAll(es);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI#storeInput(java.lang.String, java.util.ArrayList)
	 */
	@Override
	public void			storeInput(String destinationURI, ArrayList<EventI> es)
	{
		assert	destinationURI != null :
				new NeoSim4JavaException("Precondition violation: "
										+ "destinationURI != null");
		assert	this.getURI().equals(destinationURI) :
				new NeoSim4JavaException("Precondition violation: "
										+ "getURI().equals(destinationURI)");
		assert	es != null && !es.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
										+ "es != null && !es.isEmpty()");
		assert	es.stream().allMatch(e -> isImportedEventType(e.getClass())) :
				new NeoSim4JavaException(
						"Precondition violation: "
						+ "es.stream().allMatch(e -> "
						+ "isImportedEventType(e.getClass()))");

		if (this.simulationEngine.hasDebugLevel(2)) {
			this.logMessage("AtomicModel>>storeInput " +
										this.getURI() + " " + es.get(0));
		}

		((AtomicEngine)this.getSimulationEngine()).
								planExternalEventStep(destinationURI, es);
	}

	/**
	 * return the vector of all external events received during the last
	 * internal simulation step through <code>storeInput</code>, clearing
	 * them up to reinitialise the vector for the next step.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the vector of all external events received during the last internal simulation step.
	 */
	protected ArrayList<EventI>	getStoredEventAndReset()
	{
		ArrayList<EventI> ret = new ArrayList<EventI>();
		synchronized(this.currentStoredEvents) {
			ret.addAll(this.currentStoredEvents);
			this.currentStoredEvents.clear();
		}
		return ret;
	}

	// -------------------------------------------------------------------------
	// Debugging behaviour
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.Model#showCurrentState(java.lang.String, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public void			showCurrentState(
		String indent,
		Duration elapsedTime
		)
	{
		System.out.println(indent + "--------------------");
		String name = this.getClass().getCanonicalName();
		if (name.contains(".")) {
			int index = name.lastIndexOf('.');
			name = name.substring(index + 1);
		}
		System.out.println(indent + name + " " + this.uri +
				" " + this.currentStateTime.getSimulatedTime()
				+ " " + elapsedTime.getSimulatedDuration());
		System.out.println(indent + "--------------------");
		this.showCurrentStateContent(indent, elapsedTime);
		System.out.println(indent + "--------------------");
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
		System.out.println(indent + "currentStateTime = " +
								this.currentStateTime.getSimulatedTime());
		System.out.println(indent + "next time advance = " +
								this.nextTimeAdvance.getSimulatedDuration());
		System.out.println(indent + "time of next event = " +
								this.timeOfNextEvent.getSimulatedTime());
		
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
	 * @param indent	indentation to be used when producing a new line.
	 * @param sb		string buffer to which the content must be added.
	 */
	protected void		modelContentAsString(String indent, StringBuffer sb)
	{
		super.modelContentAsString(indent, sb);
		sb.append(indent);
		if (this.influencees.size() == 0) {
			sb.append("influencees = {}\n");
		} else {
			sb.append("influencees = {");
			int cpt = 0;
			for (Class<? extends EventI> ce : this.influencees.keySet()) {
				cpt++;
				sb.append("[");
				sb.append(ce.getName());
				sb.append(" => <");
				int cpt2 = 0;
				for (CallableEventAtomicSink es : this.influencees.get(ce)) {
					cpt2++;
					sb.append("(");
					sb.append(es.importingModelURI);
					sb.append(", ");
					sb.append(es.sink.getClass().getName());
					sb.append(", ");
					sb.append(es.sinkEventType.getName());
					sb.append(')');
					if (cpt2 < this.influencees.get(ce).size() - 1) {
						sb.append(", ");
					}
				}
				sb.append(">]");
				if (cpt < this.influencees.keySet().size() - 1) {
					sb.append(", ");
				}
			}
			sb.append("}\n");
		}
	}
}
// -----------------------------------------------------------------------------
