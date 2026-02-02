package fr.sorbonne_u.components.cyphy.plugins.devs.utils;

import fr.sorbonne_u.components.ComponentI;

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

import fr.sorbonne_u.devs_simulation.utils.MemorisingLogger;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>MemorisingComponentLogger</code> implements a logging
 * facility for DEVS simulation models running in BCM components that keeps
 * all logging messages until they can be printed.
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
 * <p>Created on : 2021-01-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			MemorisingComponentLogger
extends		MemorisingLogger
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected final ComponentI	owner;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a logger.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * post	{@code  true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner		owner component.
	 */
	public				MemorisingComponentLogger(ComponentI owner)
	{
		super();
		assert	owner != null : new PreconditionException("owner != null");

		this.owner = owner;
	}

	/**
	 * create a logger.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner != null}
	 * pre	{@code separator != null && separator.length() == 1}
	 * post	{@code  true}	// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @param separator		to be used to fragment the line.
	 */
	public				MemorisingComponentLogger(
		ComponentI owner,
		String separator
		)
	{
		super(separator);
		assert	owner != null : new PreconditionException("owner != null");

		this.owner = owner;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.utils.MemorisingLogger#printLog()
	 */
	@Override
	public void			printLog()
	{
		this.owner.traceMessage("--- memorised logs: ----------------------\n");
		this.owner.traceMessage(this.log.toString());
		this.owner.traceMessage("------------------------------------------\n");
	}
}
// -----------------------------------------------------------------------------
