package fr.sorbonne_u.devs_simulation.models.events;

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

import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>Event</code> provides the default base
 * implementation for simulation events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code timeOfOccurrence != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2016-01-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Event
implements	EventI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** time of occurrence of the event in simulation clock time.			*/
	protected final Time				timeOfOccurrence;
	/** the event description.												*/
	protected final EventInformationI 	content;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	// The invariants are trivially  observed by the fact that all variables
	// are final and the invariant expressions also appears as preconditions
	// on the corresponding parameters in the constructor.

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
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the created event.
	 * @param content			description of the created event.
	 */
	public				Event(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		assert	timeOfOccurrence != null :
				new NeoSim4JavaException("Precondition violation: "
											+ "timeOfOccurrence != null");

		this.timeOfOccurrence = timeOfOccurrence;
		this.content = content;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#getTimeOfOccurrence()
	 */
	@Override
	public Time			getTimeOfOccurrence()
	{
		return this.timeOfOccurrence;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#getEventInformation()
	 */
	@Override
	public EventInformationI	getEventInformation()
	{
		return this.content;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		throw new RuntimeException(
					"If called, the method hasPriorityOver must be defined"
					+ " in subclasses");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#executeOn(fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI)
	 */
	@Override
	public void			executeOn(AtomicModelI model)
	{
		assert	model != null;
	}

	// -------------------------------------------------------------------------
	// Debugging
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		this.eventContentAsString(sb);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventContentAsString(java.lang.StringBuffer)
	 */
	@Override
	public void			eventContentAsString(StringBuffer sb)
	{
		sb.append(this.timeOfOccurrence.toString());
		if (this.content != null) {
			sb.append(", ");
			sb.append(this.content.toString());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		return this.eventAsString();
	}
}
// -----------------------------------------------------------------------------
