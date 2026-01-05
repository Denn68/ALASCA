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

import java.util.Comparator;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>EventComparator</code> implements a comparison between
 * events used to define a total order of execution among events.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * An event e1 is earlier than an event e2 if its time of occurrence is
 * earlier than the time of occurrence of e2. If the two events occur at
 * the same time, e1 is earlier than e2 if it has priority over e2 (method
 * <code>hasPriorityOver</code> of <code>EventI</code>). If no priority is
 * defined among events, the latest to be inserted will appear after the
 * earlier ones in the queue.
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
 * <p>Created on : 2018-07-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			EventComparator<E extends ES_EventI>
implements	Comparator<E>
{
	/**
	 * return -1 if <code>e1</code> is earlier than <code>e2</code> otherwise
	 * return 1; if the two events occur at the same time, then return -1 if
	 * <code>e1</code> has priority over <code>e2</code> otherwise return 1
	 * (hence if no priority is given to events, the earliest inserted appears
	 * before the latest, so there are no ties).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code e1 != null && e2 != null}
	 * post	{@code ret == -1 || ret == 1}
	 * </pre>
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int			compare(E e1, E e2)
	{
		assert	e1 != null && e2 != null :
				new NeoSim4JavaException("Precondition violation: "
											+ "e1 != null && e2 != null");

		int ret = 0;
		if (e1.getTimeOfOccurrence().lessThan(e2.getTimeOfOccurrence())) {
			ret = -1;
		} else if (e1.getTimeOfOccurrence().greaterThan(
												e2.getTimeOfOccurrence())) {
			ret = 1;
		}
		if (ret == 0) {
			if (e1.hasPriorityOver(e2)) {
				ret = -1;
			} else {
				ret = 1;
			}
		}
		return ret;
	}
}
// -----------------------------------------------------------------------------
