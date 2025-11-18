package fr.sorbonne_u.devs_simulation.examples.bank;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>ClientGeneratorReport</code> represents the simulation report
 * from the client generator model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code modelURI != null && !modelURI.isEmpty()}
 * invariant	{@code meanInterArrivalTime > 0.0}
 * invariant	{@code numberOfGeneratedClients >= 0}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2022-09-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ClientGeneratorReport
implements	SimulationReportI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the model which provides this report.						*/
	protected final String		modelURI;
	/** mean inter-arrival time used in the simulation run.					*/
	protected final double		meanInterArrivalTime;
	/** number of client arrivals generated during the simulation run.		*/
	protected final int			numberOfGeneratedClients;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a report instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param modelURI					URI of the model which provides this report.
	 * @param meanInterArrivalTime		mean inter-arrival time used in the simulation run.
	 * @param numberOfGeneratedClients	number of client arrivals generated during the simulation run.
	 */
	public				ClientGeneratorReport(
		String modelURI,
		double meanInterArrivalTime,
		int numberOfGeneratedClients
		)
	{
		super();

		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	meanInterArrivalTime > 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "meanInterArrivalTime > 0.0");
		assert	numberOfGeneratedClients >= 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "numberOfGeneratedClients >= 0");

		this.modelURI = modelURI;
		this.meanInterArrivalTime = meanInterArrivalTime;
		this.numberOfGeneratedClients = numberOfGeneratedClients;
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
		sb.append("mean inter-arrival time = ");
		sb.append(this.meanInterArrivalTime);
		sb.append(", number of generated clients = ");
		sb.append(this.numberOfGeneratedClients);
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
