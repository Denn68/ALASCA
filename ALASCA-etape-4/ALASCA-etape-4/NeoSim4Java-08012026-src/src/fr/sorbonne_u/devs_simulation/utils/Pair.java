package fr.sorbonne_u.devs_simulation.utils;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>Pair</code> implements a simple geneirc pair of values.
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
 * <p>Created on : 2022-06-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Pair<T1 extends Serializable, T2 extends Serializable>
implements	Serializable
{
	private static final long	serialVersionUID = 1L;
	/** the first element of the pair.										*/
	protected final T1			first;
	/** the second element of the pair.										*/
	protected final T2			second;

	/**
	 * create a new pair instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param first		the first element of the pair.
	 * @param second	the second element of the pair.
	 */
	public				Pair(T1 first, T2 second)
	{
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first element of the pair.
	 */
	public T1			getFirst()	{ return first; }

	/**
	 * @return the second element of the pair.
	 */
	public T2			getSecond()	{ return second; }
}
// -----------------------------------------------------------------------------
