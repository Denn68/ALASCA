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
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.es.models.AtomicES_Model;

// -----------------------------------------------------------------------------
/**
 * The interface <code>ES_EventI</code> defines the behaviours of events
 * in the event scheduling view DEVS implementation of traditional discrete
 * event simulation models.
 *
 * <p><strong>Description</strong></p>
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
public interface		ES_EventI
extends		EventI
{
	/**
	 * generate new events resulting from the execution of this event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code model != null}
	 * post	{@code return != null && return.stream().allMatch(e -> e.getTimeOfOccurrence().greaterThanOrEqual(getTimeOfOccurrence()))}
	 * </pre>
	 *
	 * @param model	atomic event scheduling model on which the event is executed.
	 * @return		the set of generated events.
	 */
	public Set<ES_EventI>	generateNewEvents(AtomicES_Model model);

	/**
	 * return true if this event has been cancelled.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this event has been cancelled.
	 */
	public boolean		isCancelled();

	/**
	 * cancel this event.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isCancelled()}
	 * post	{@code isCancelled()}
	 * </pre>
	 *
	 */
	public void			cancel();
}
// -----------------------------------------------------------------------------
