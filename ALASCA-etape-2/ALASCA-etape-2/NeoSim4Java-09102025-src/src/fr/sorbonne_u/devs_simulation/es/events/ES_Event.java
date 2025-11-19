package fr.sorbonne_u.devs_simulation.es.events;

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

import java.util.Set;
import java.util.HashSet;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.events.Event;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>ES_Event</code> defines the baseline implementation of
 * an event in event scheduling simulation models which does nothing when
 * executed on a model and returns an empty set of generated new events.
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
 * <p>Created on : 2018-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ES_Event
extends		Event
implements	ES_EventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** true if the event has been cancelled, false otherwise.				*/
	protected boolean	isCancelled;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an event from the given time of occurrence and event description.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * post	{@code !isCancelled()}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the created event.
	 * @param content			description of the created event.
	 */
	public				ES_Event(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);
		assert	timeOfOccurrence != null :
				new NeoSim4JavaException(
						"Precondition violation: timeOfOccurrence != null");
		this.isCancelled = false;
		assert	!isCancelled();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		throw new RuntimeException("If called, the method hasPriorityOver"
									+ " must be defined in subclasses");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_EventI#generateNewEvents(fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model)
	 */
	@Override
	public Set<ES_EventI>	generateNewEvents(AtomicES_Model model)
	{
		assert	model != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "model != null");

		return new HashSet<ES_EventI>();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_EventI#isCancelled()
	 */
	@Override
	public boolean		isCancelled()
	{
		return this.isCancelled;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_EventI#cancel()
	 */
	@Override
	public void			cancel()
	{
		assert	!isCancelled() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isCancelled()");

		this.isCancelled = true;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventContentAsString(java.lang.StringBuffer)
	 */
	@Override
	public void			eventContentAsString(StringBuffer sb)
	{
		super.eventContentAsString(sb);
		if (this.isCancelled()) {
			sb.append(", cancelled");
		}
	}
}
// -----------------------------------------------------------------------------
