package fr.sorbonne_u.components.cyphy;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// real time distributed applications in the Java programming language.
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

// -----------------------------------------------------------------------------
/**
 * The enumeration <code>ExecutionMode</code> defines the basic types of
 * execution in which BCM4JavaCyPhy components can run.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Execution modes are used to guide the scenarios that the components will
 * execute in a run.
 * </p>
 * <ul>
 * <li>{@code STANDARD} means that the components run as a real world
 *   deployment or runs its test manually without using the BCM4Java
 *   test functionalities.</li>
 * <li>{@code UNIT_TEST} means that the component is tested in the least
 *   possible configuration, most of the times with only another component
 *   used to call its services to check if it runs as expected.</li>
 * <li>{@code UNIT_TEST_WITH_SIL_SIMULATION} means that the component is
 *   tested in the least possible configuration using software-in-the-loop
 *   simulation, most of the times with only another component used to call its
 *   services to check if it runs as expected.</li>
 * <li>{@code UNIT_TEST_WITH_HIL_SIMULATION} means that the component is
 *   tested in the least possible configuration using hardware-in-the-loop
 *   simulation, most of the times with only another component used to call its
 *   services to check if it runs as expected.</li>
 * <li>{@code INTEGRATION_TEST} means that the component runs in a configuration
 *   as similar to the standard deployment but to test if the entire application
 *   runs as expected.</li>
 * <li>{@code INTEGRATION_TEST_WITH_SIL_SIMULATION} means that the component
 *   runs in a configuration as similar to the standard deployment but with
 *   software-in-the-loop simulation to test if the entire application runs as
 *   expected.</li>
 * <li>{@code INTEGRATION_TEST_WITH_HIL_SIMULATION} means that the component
 *   runs in a configuration as similar to the standard deployment but with
 *   hardware-in-the-loop simulation to test if the entire application runs as
 *   expected</li>
 * </ul>
 * <p>
 * Methods are provided to test convenient combinations of these modes, like
 * tests without simulaiton or tests with simulation.
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
 * <p>Created on : 2023-11-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public enum				ExecutionMode
{
	STANDARD,
	UNIT_TEST,
	UNIT_TEST_WITH_SIL_SIMULATION,
	UNIT_TEST_WITH_HIL_SIMULATION,
	INTEGRATION_TEST,
	INTEGRATION_TEST_WITH_SIL_SIMULATION,
	INTEGRATION_TEST_WITH_HIL_SIMULATION;

	/**
	 * return true if the execution mode is {@code STANDARD}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code STANDARD}.
	 */
	public boolean		isStandard()
	{
		return this == STANDARD;
	}

	/**
	 * return true if the execution mode is {@code UNIT_TEST}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST}.
	 */
	public boolean		isUnitTest()
	{
		return this == UNIT_TEST;
	}

	/**
	 * return true if the execution mode is
	 * {@code UNIT_TEST_WITH_SIL_SIMULATION}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST_WITH_SIL_SIMULATION}.
	 */
	public boolean		isSILUnitTest()
	{
		return this == UNIT_TEST_WITH_SIL_SIMULATION;
	}

	/**
	 * return true if the execution type is {@code UNIT_TEST_WITH_HIL_SIMULATION}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST_WITH_HIL_SIMULATION}.
	 */
	public boolean		isHILUnitTest()
	{
		return this == UNIT_TEST_WITH_HIL_SIMULATION;
	}

	/**
	 * return true if the execution mode is {@code INTEGRATION_TEST}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code INTEGRATION_TEST}.
	 */
	public boolean		isIntegrationTest()
	{
		return this == INTEGRATION_TEST;
	}

	/**
	 * return true if the execution mode is
	 * {@code INTEGRATION_TEST_WITH_SIL_SIMULATION}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code INTEGRATION_TEST_WITH_SIL_SIMULATION}.
	 */
	public boolean		isSILIntegrationTest()
	{
		return this == INTEGRATION_TEST_WITH_SIL_SIMULATION;
	}

	/**
	 * return true if the execution mode is
	 * {@code INTEGRATION_TEST_WITH_HIL_SIMULATION}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code INTEGRATION_TEST_WITH_HIL_SIMULATION}.
	 */
	public boolean		isHILIntegrationTest()
	{
		return this == INTEGRATION_TEST_WITH_HIL_SIMULATION;
	}

	/**
	 * return true if the execution type is {@code UNIT_TEST} or
	 * {@code INTEGRATION_TEST} <i>i.e.</i>, test without simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST} or {@code INTEGRATION_TEST}.
	 */
	public boolean		isTestWithoutSimulation()
	{
		return this.isUnitTest() || this.isIntegrationTest();
	}

	/**
	 * return true if the execution type is {@code UNIT_TEST_WITH_SIL_SIMULATION}
	 * or {@code INTEGRATION_TEST_WITH_SIL_SIMULATION} <i>i.e.</i>, test with
	 * software-in-the-loop simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST_WITH_SIL_SIMULATION} or {@code INTEGRATION_TEST_WITH_SIL_SIMULATION} <i>i.e.</i>, test with software-in-the-loop simulation.
	 */
	public boolean		isSILTest()
	{
		return this.isSILUnitTest() || this.isSILIntegrationTest();
	}

	/**
	 * return true if the execution type is {@code UNIT_TEST_WITH_HIL_SIMULATION}
	 * or {@code INTEGRATION_TEST_WITH_HIL_SIMULATION} <i>i.e.</i>, test with
	 * hardware-in-the-loop simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST_WITH_HIL_SIMULATION} or {@code INTEGRATION_TEST_WITH_HIL_SIMULATION} <i>i.e.</i>, test with hardware-in-the-loop simulation.
	 */
	public boolean		isHILTest()
	{
		return this.isHILUnitTest() || this.isHILIntegrationTest();
	}


	/**
	 * return true if the execution type is {@code UNIT_TEST_WITH_SIL_SIMULATION}
	 * or {@code UNIT_TEST_WITH_HIL_SIMULATION}
	 * or {@code INTEGRATION_TEST_WITH_SIL_SIMULATION}
	 * of {@code INTEGRATION_TEST_WITH_HIL_SIMULATION} <i>i.e.</i>, test with
	 * software-in-the-loop or hardware-in-the-loop simulation.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the execution mode is {@code UNIT_TEST_WITH_SIL_SIMULATION} or {@code UNIT_TEST_WITH_HIL_SIMULATION} or {@code INTEGRATION_TEST_WITH_SIL_SIMULATION} of {@code INTEGRATION_TEST_WITH_HIL_SIMULATION} <i>i.e.</i>, test with software-in-the-loop or hardware-in-the-loop simulation.
	 */
	public boolean		isSimulationTest()
	{
		return this.isSILUnitTest() || this.isSILIntegrationTest() ||
							this.isHILUnitTest() || this.isHILIntegrationTest();
	}

}
// -----------------------------------------------------------------------------
