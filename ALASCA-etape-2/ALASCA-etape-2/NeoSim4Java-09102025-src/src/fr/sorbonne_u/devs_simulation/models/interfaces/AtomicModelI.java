package fr.sorbonne_u.devs_simulation.models.interfaces;

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

import java.util.ArrayList;
import fr.sorbonne_u.devs_simulation.models.events.EventI;

// -----------------------------------------------------------------------------
/**
 * The interface <code>AtomicModelI</code> defines the most generic methods to
 * be implemented by DEVS atomic simulation models.
 *
 * <p><strong>Description</strong></p>
 * 
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
public interface		AtomicModelI
extends		ModelI,
			EventsExchangingI
{
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.interfaces.ModelI#isAtomic()
	 */
	@Override
	default boolean		isAtomic() { return true; }

	/**
	 * maps the current internal state to the output set; this method is
	 * user-model-dependent hence must be implemented by the user for atomic
	 * models.
	 * 
	 * <p>Description</p>
	 * 
	 * <p>
	 * Beware that when this method is called, though the simulation time
	 * has conceptually reached the time if the next internal event, the
	 * value returned by <code>getCurrentStateTime</code> has not yet been
	 * updated to that time. Hence the actual current simulation time is
	 * given by
	 * <code>this.getCurrentStateTime().add(this.getNextTimeAdvance())</code>.
	 * </p>
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * pre	{@code getNextTimeAdvance().lessThan(Duration.INFINITY)}
	 * pre	{@code getNextTimeAdvance().equals(getTimeOfNextEvent().subtract(getCurrentStateTime()))}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the corresponding external events or null if none.
	 */
	default ArrayList<EventI>	output()
	{
		// By default, return null, otherwise redefine in subclasses
		return null;
	}
}
// -----------------------------------------------------------------------------
