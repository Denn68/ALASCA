package fr.sorbonne_u.devs_simulation.simulators.interfaces;

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

// -----------------------------------------------------------------------------
/**
 * The interface <code>CoordinationI</code> defines the core behaviour of
 * DEVS coordination engines, extending the basic behaviour shared by all
 * simulation engines.
 *
 * <p><strong>Description</strong></p>
 * 
 * Coordination engines are organised in hierarchies where the lowest level
 * ones directly coordinate atomic engines while at the higher levels, they
 * coordinate mixes of atomic and coordination engines or only coordination
 * engines. In terms of interface, the sole new required methods concerns
 * the coordinated engines. In term of implementation, the methods declared
 * in the interface <code>SimulatorI</code> that defines the DEVS protocol
 * must be implemented to coordinate the coordinated engines.
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		CoordinatorI
extends		SimulatorI,
			ParentNotificationI
{
	/**
	 * set the simulation engines that are coordinated by this coordination
	 * engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !coordinatedEnginesSet()}
	 * pre	{@code isModelSet()}
	 * pre	{@code coordinatedEngines != null && coordinatedEngines.length > 1}
	 * pre	{@code Stream.of(coordinatedEngines).allMatch(ce -> { ce != null && ((CoupledModelI)simulatedModel).isSubmodel(ce.getURI())})}
	 * post	{@code coordinatedEnginesSet()}
	 * </pre>
	 *
	 * @param coordinatedEngines	array of engines to be coordinated or proxies to them.
	 */
	public void			setCoordinatedEngines(SimulatorI[] coordinatedEngines);

	/**
	 * return true if the coordinated engines have been set for this
	 * coordination engine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the coordinated engines have been set for this coordination engine.
	 */
	public boolean		coordinatedEnginesSet();
}
// -----------------------------------------------------------------------------
