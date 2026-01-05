package fr.sorbonne_u.components.cyphy.annotations;

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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.annotations.ModelExternalEvents;

// -----------------------------------------------------------------------------
/**
 * The annotation <code>LocalArchitecture</code> is used to declare the local
 * simulation architectures provided by a BCM4Java CyPhy component.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * BCM4Java cyber-physical components can have simulators attached to them
 * to cater for software-in-the-loop tests with simulation. Depending on
 * the type of test (unit tests, integration tests, performance tests, ...),
 * the component can offer several local simulation architectures that can
 * either run in isolation (mainly for unit tests) or be composed in a larger
 * application-wide simulator with other local simulators in other components.
 * </p>
 * <p>
 * This annotation must be included in a {@code SIL_Simulation_Architectures},
 * which is put on the class defining the cyber-physical component.
 * It is used by the method
 * {@code AbstractCyPhyComponent::createLocalSimulationArchitecturesFromAnnotation(Set<String>, double)}
 * to create the local simulation architectures before one is selected to create
 * the component local simulator before its composition and execution.
 * </p>
 * <p>
 * The annotation declares the URI of the architecture, the URI of its root
 * simulaiton model and its simulated time unit. It also includes the
 * declaration of imported and exported events, which represent in a sense the
 * interface of a DEVS simulator in the NeoSim4Java DEVS based simulation
 * framework.
 * </p>
 * <p>
 * The annotation declares a local simulation architecture as a black box, hence
 * it is insufficient to create it <i>per se</i>. The method
 * {@code createLocalSimulationArchitecture} must be implemented by the
 * cyber-physical component to create the actual simulation architecture from
 * its URI. If several local architectures are provided, this method must handle
 * the creation of all of them, one by one.
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
 * <p>Created on : 2025-11-12</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface	LocalArchitecture
{
	/**
	 * return the URI of a local SIL simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && !return.isEmpty()}
	 * </pre>
	 *
	 * @return	the URI of a local SIL simulation architecture.
	 */
	String			uri();

	/**
	 * return the URI of the root model in the local SIL simulation architecture.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && !return.isEmpty()}
	 * </pre>
	 *
	 * @return	the URI of the root model in the local SIL simulation architecture.
	 */
	String			rootModelURI();

	/**
	 * return the time unit used in the simulator for times and durations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the time unit used in the simulator for times and durations.
	 */
	TimeUnit		simulatedTimeUnit();

	/**
	 * return the set of the local SIL architecture root model imported and
	 * exported events or {@code null} if none.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the set of the local SIL architecture root model imported and exported events or {@code null} if none.
	 */
	ModelExternalEvents	externalEvents();
}
// -----------------------------------------------------------------------------
