package fr.sorbonne_u.components.cyphy.plugins.devs.interfaces;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide an extension
// of the BCM component model that aims to define a components tailored for
// cyber-physical control systems (CPCS) for Java.
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

/**
 * The interface <code>SupervisionI</code> defines the methods
 * through which simulators communicate with the simulation supervisor to
 * provide it with informations on them, like register in a distributed
 * simulation and sending a simulation report at the end of the runs.
 *
 * <p><strong>Description</strong></p>
 * 
 * The method <code>acceptSimulationReport</code> carries a simulation report,
 * an object which class implements the interface <code>SimulationReportI</code>
 * and contains for example results and statistics about the run.
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		SupervisorNotificationI
{
	/**
	 * receive and process the simulation reports coming from the simulator
	 * components at the end of the simulation; called-back by simulator
	 * components.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code  true}	// no precondition.
	 * post	{@code  true}	// no postcondition.
	 * </pre>
	 *
	 * @param report		simulation report from one of the simulator components.
	 * @throws Exception	<i>to do</i>.
	 */
	public void			acceptSimulationReport(SimulationReportI report)
	throws Exception;
}
