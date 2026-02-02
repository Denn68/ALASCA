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

import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractModelReport</code> implements the
 * core functionalities of simulation reports.
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
 * invariant	{@code getModelURI() != null && !getModelURI().isEmpty()}
 * </pre>
 * 
 * <p>Created on : 2018-10-01</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractSimulationReport
implements	SimulationReportI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	protected final String		modelURI;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a simulation report.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * post	{@code getModelURI().equals(modelURI)}
	 * </pre>
	 *
	 * @param modelURI	URI of the model which report is defined.
	 */
	public				AbstractSimulationReport(
		String modelURI
		)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");

		this.modelURI = modelURI;

		assert	this.getModelURI().equals(modelURI) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getModelURI().equals(modelURI)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI#getModelURI()
	 */
	@Override
	public String		getModelURI()
	{
		return this.modelURI;
	}
}
// -----------------------------------------------------------------------------
