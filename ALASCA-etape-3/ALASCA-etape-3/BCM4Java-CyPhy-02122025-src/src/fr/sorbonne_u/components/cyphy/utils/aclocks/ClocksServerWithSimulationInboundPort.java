package fr.sorbonne_u.components.cyphy.utils.aclocks;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import java.time.Instant;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.ClocksServerInboundPort;

// -----------------------------------------------------------------------------
/**
 * The class <code>ClocksServerWithSimulationInboundPort</code>implements the
 * inbound port for the {@code ClocksServerWithSimulationCI} component
 * interface.
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
 * <p>Created on : 2024-11-19</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ClocksServerWithSimulationInboundPort
extends		ClocksServerInboundPort
implements	ClocksServerWithSimulationCI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	public static boolean	VERBOSE = false;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the glass-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the glass-box invariants are observed, false otherwise.
	 */
	protected static boolean	glassBoxInvariants(
		ClocksServerWithSimulationInboundPort instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		return ret;
	}

	/**
	 * return true if the black-box invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the black-box invariants are observed, false otherwise.
	 */
	protected static boolean	blackBoxInvariants(
		ClocksServerWithSimulationInboundPort instance
		)
	{
		assert instance != null : new PreconditionException("instance != null");

		boolean ret = true;
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof ClocksServerWithSimulation}
	 * post	{@code getOwner() instanceof ClocksServerWithSimulation}
	 * </pre>
	 *
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ClocksServerWithSimulationInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ClocksServerWithSimulationCI.class, owner);
		assert	owner instanceof ClocksServerWithSimulation :
				new PreconditionException(
						"owner instanceof ClocksServerWithSimulation");
	}

	/**
	 * create the inbound port.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code owner instanceof ClocksServerWithSimulation}
	 * post	{@code getOwner() instanceof ClocksServerWithSimulation}
	 * </pre>
	 *
	 * @param uri			URI of the port.
	 * @param owner			component owning the port.
	 * @throws Exception	<i>to do</i>.
	 */
	public				ClocksServerWithSimulationInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ClocksServerWithSimulationCI.class, owner);
		assert	owner instanceof ClocksServerWithSimulation :
				new PreconditionException(
						"owner instanceof ClocksServerWithSimulation");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI#getTypeOfClock(java.lang.String)
	 */
	@Override
	public TypeOfClock	getTypeOfClock(String clockURI) throws Exception
	{
		return this.getOwner().handleRequest(
				o -> ((ClocksServerWithSimulation)o).getTypeOfClock(clockURI));
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI#getClockWithSimulation(java.lang.String)
	 */
	@Override
	public AcceleratedAndSimulationClock	getClockWithSimulation(
		String clockURI
		) throws Exception
	{
		return this.getOwner().handleRequest(
					o -> ((ClocksServerWithSimulation)o).
										getClockWithSimulation(clockURI));
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.utils.aclocks.ClocksServerWithSimulationCI#createClockWithSimulation(java.lang.String, long, java.time.Instant, double, fr.sorbonne_u.devs_simulation.models.time.Time, fr.sorbonne_u.devs_simulation.models.time.Duration)
	 */
	@Override
	public AcceleratedAndSimulationClock	createClockWithSimulation(
		String clockURI,
		long unixEpochStartTimeInNanos,
		Instant startInstant,
		double accelerationFactor,
		Time simulatedStartTime,
		Duration simulatedDuration
		) throws Exception
	{
		if (VERBOSE) {
			this.getOwner().traceMessage(
							"ClocksServerInboundPort#createClockWithSimulation("
							+ clockURI + ") to "
							+ this.getOwner().getClass().getCanonicalName()
							+ "\n");
		}

		return this.getOwner().handleRequest(
					o -> ((ClocksServerWithSimulation)o).
							createClockWithSimulation(
									clockURI,
									unixEpochStartTimeInNanos,
									startInstant,
									accelerationFactor,
									simulatedStartTime,
									simulatedDuration));
	}
}
// -----------------------------------------------------------------------------
