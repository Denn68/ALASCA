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

import java.io.Serializable;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The interface <code>EventI</code> defines the most general methods required
 * in events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An event is defined by its time of occurrence (in simulation clock time)
 * end a generic information describing the event itself. In discrete time
 * simulation, many events can occur at the same simulation clock time. A
 * priority order allows to define a total order of processing among these
 * events. This total order can be expressed in a dense time model (a couple
 * simulation time and index discriminating a countable number of co-occurring
 * events).
 * </p>
 * 
 * <p>
 * All events manipulated in simulation models must directly or indirectly
 * implement this interface.
 * </p>
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
public interface		EventI
extends		Serializable
{
	/**
	 * return the time of occurrence of this event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time of occurrence of this event.
	 */
	public Time			getTimeOfOccurrence();

	/**
	 * return the information attached to this event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the information attached to this event.
	 */
	public EventInformationI	getEventInformation();

	/**
	 * return true if this event has priority for processing over the given
	 * parameter event if they occur at the same simulated time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code e != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param e		event to be compared to this event to decide priority.
	 * @return		true if this event has priority over <code>e</code>.
	 */
	public boolean		hasPriorityOver(EventI e);

	/**
	 * execute the event on the given model.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code model != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param model	atomic event model on which the event is executed.
	 */
	public void			executeOn(AtomicModelI model);

	/**
	 * return a string representing the event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	a string representing the event.
	 */
	public String		eventAsString();
	
	/**
	 * add to the given string buffer elements representing the content of the
	 * event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sb != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sb	a string buffer to which elements will be added.
	 */
	public void			eventContentAsString(StringBuffer sb);
}
// -----------------------------------------------------------------------------
