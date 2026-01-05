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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.simulators.interfaces.SimulationReportI;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardCoupledModelReport</code> represents the
 * simulation report of a coupled model as the vector of its submodels
 * reports.
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
 * <p>Created on : 2018-09-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardCoupledModelReport
extends		AbstractSimulationReport
implements	SimulationReportI
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	private static final long						serialVersionUID = 1L;
	/** simulation reports of the submodels.								*/
	protected final Map<String,SimulationReportI>	submodelsReports;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an empty standard coupled model report.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code coupledModelURI != null &&!coupledModelURI.isEmpty()}
	 * post	{@code getModelURI().equals(coupledModelURI)}
	 * </pre>
	 *
	 * @param coupledModelURI	URI of the coupled model which report is defined.
	 */
	public				StandardCoupledModelReport(
		String coupledModelURI
		)
	{
		super(coupledModelURI);

		assert	coupledModelURI != null &&!coupledModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
					+ "coupledModelURI != null &&!coupledModelURI.isEmpty()");

		this.submodelsReports = new HashMap<String,SimulationReportI>();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * add a report from a submodel.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code report != null}
	 * pre	{@code !this.hasReportFrom(report.getModelURI())}
	 * post	{@code hasReportFrom(report.getModelURI())}
	 * post	{@code getReport(report.getModelURI()).equals(report)}
	 * </pre>
	 *
	 * @param report	report of a submodel to be added.
	 */
	public void			addReport(SimulationReportI report)
	{
		assert	report != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "report != null");
		assert	!this.hasReportFrom(report.getModelURI()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!hasReportFrom(report.getModelURI())");

		this.submodelsReports.put(report.getModelURI(), report);

		assert	this.hasReportFrom(report.getModelURI()) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "hasReportFrom(report.getModelURI())");
		assert	this.getReport(report.getModelURI()).equals(report) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getReport(report.getModelURI()).equals(report)");
	}

	/**
	 * return true if the model with the provided URI has a report
	 * stored in this coupled model report.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	a model URI.
	 * @return			true if the model with the provided URI has a report stored in this coupled model report.
	 */
	public boolean		hasReportFrom(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");

		return this.submodelsReports.keySet().contains(modelURI);
	}

	/**
	 * get all the stored submodels reports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the stored submodels reports.
	 */
	public Collection<SimulationReportI> getReports()
	{
		return this.submodelsReports.values();
	}

	/**
	 * return the report associated with <code>modelURI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code hasReportFrom(modelURI)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param modelURI	a model URI to which a report has been associated.
	 * @return			the report associated with <code>modelURI</code>.
	 */
	public SimulationReportI		getReport(String modelURI)
	{
		assert	modelURI != null && !modelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "modelURI != null && !modelURI.isEmpty()");
		assert	this.hasReportFrom(modelURI) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasReportFrom(modelURI)");

		return this.submodelsReports.get(modelURI);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		int i = 0;
		for (SimulationReportI report : this.submodelsReports.values()) {
			sb.append(report.toString());
			if (i < this.submodelsReports.size() - 1) {
				sb.append(", ");
			}
			i++;
		}
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
