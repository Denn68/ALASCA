package fr.sorbonne_u.devs_simulation.examples.bank;

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
 * The class <code>BankModelReport</code> represents the simulation report
 * from the bank model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code modelURI != null && !modelURI.isEmpty()}
 * invariant	{@code meanServiceTime > 0.0}
 * invariant	{@code totalNumberOfCLients >= 0}
 * invariant	{@code meanTime > 0.0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-11-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			BankModelReport
implements	SimulationReportI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the model which provides this report.						*/
	protected final String		modelURI;
	/** mean service time used in the simulation run.						*/
	protected final double		meanServiceTime;
	/** total number of serviced clients.									*/
	protected final int			totalNumberOfClients;
	/** mean time passed by clients at the bank.							*/
	protected final double		meanTime;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a report instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code meanServiceTime > 0.0}
	 * pre	{@code totalNumberOfCLients >= 0}
	 * pre	{@code meanTime > 0.0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model which provides this report.
	 * @param meanServiceTime		mean service time used in the simulation run.
	 * @param totalNumberOfClients	total number of serviced clients.
	 * @param meanTime				mean time passed by clients at the bank.
	 */
	public				BankModelReport(
		String modelURI,
		double meanServiceTime,
		int totalNumberOfClients,
		double meanTime
		)
	{
		super();

		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
								+ "modelURI != null && !modelURI.isEmpty()");
		assert	meanServiceTime > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
								+ "meanServiceTime > 0.0");
		assert	totalNumberOfClients >= 0 :
				new NeoSim4JavaException("Precondition violation: "
								+ "totalNumberOfCLients >= 0");
		assert	meanTime > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "meanTime > 0.0");

		this.modelURI = modelURI;
		this.meanServiceTime = meanServiceTime;
		this.totalNumberOfClients = totalNumberOfClients;
		this.meanTime = meanTime;
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

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getModelURI());
		sb.append('[');
		sb.append("mean service time = ");
		sb.append(this.meanServiceTime);
		sb.append(", total number of serviced clients = ");
		sb.append(this.totalNumberOfClients);
		sb.append(", mean time = ");
		sb.append(this.meanTime);
		sb.append(']');
		
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
