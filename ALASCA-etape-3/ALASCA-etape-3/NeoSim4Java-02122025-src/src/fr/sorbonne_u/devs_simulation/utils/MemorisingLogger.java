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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>MemorisingLogger</code> implements a logging facility for
 * DEVS simulation models that keeps all logging messages until they can be
 * printed.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code log != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-11-23</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			MemorisingLogger
extends		StandardLogger
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected static final int	STANDARD_LOG_SIZE = 10000;
	protected StringBuffer	log;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a logger.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				MemorisingLogger()
	{
		super();
		this.log = new StringBuffer(10000);
	}

	/**
	 * create a logger.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param separator	to be used to fragment the line.
	 */
	public				MemorisingLogger(String separator)
	{
		super(separator);
		this.log = new StringBuffer(STANDARD_LOG_SIZE);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.utils.StandardLogger#isEmpty()
	 */
	@Override
	public boolean		isEmpty()
	{
		return this.log.length() == 0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.utils.StandardLogger#isFull()
	 */
	@Override
	public boolean		isFull()
	{
		return this.log.capacity() == 0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.utils.StandardLogger#logMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void			logMessage(String modelURI, String message)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	!modelURI.contains(getSeparator()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!modelURI.contains(getSeparator())");
		assert	!isFull() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isFull()");

		this.log.append(System.currentTimeMillis() +
						this.separator +
						modelURI +
						this.separator +
						message);
	}

	/**
	 * print the accumulated logs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			printLog()
	{
		System.out.print(this.log);
	}

	/**
	 * clear the accumulated logs.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code isEmpty()}
	 * </pre>
	 *
	 */
	public void			clearLog()
	{
		this.log = new StringBuffer(STANDARD_LOG_SIZE);
	}
}
// -----------------------------------------------------------------------------
