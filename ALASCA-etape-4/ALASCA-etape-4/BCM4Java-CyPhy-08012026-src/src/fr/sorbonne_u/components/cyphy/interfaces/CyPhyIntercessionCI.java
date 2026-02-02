package fr.sorbonne_u.components.cyphy.interfaces;

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

import fr.sorbonne_u.components.reflection.interfaces.IntercessionCI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CyPhyIntercessionCI</code> defines the intercession
 * services offered by cyber-physical components in the BCM4Java-CyPhy.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2023-11-21</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface CyPhyIntercessionCI
extends		IntercessionCI
{
//	/**
//	 * add a relation between a global architecture URI and a local simulator.
//	 * 
//	 * <p><strong>Contract</strong></p>
//	 * 
//	 * <pre>
//	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
//	 * pre	{@code localSimulatorURI != null && !localSimulatorURI.isEmpty()}
//	 * pre	{@code !isSimulationArchitecture(globalArchitectureURI)}
//	 * pre	{@code isLocalSimulator(localSimulatorURI)}
//	 * post	{@code isSimulationArchitecture(globalArchitectureURI)}
//	 * </pre>
//	 *
//	 * @param architectureURI		URI of a global architecture in the application.
//	 * @param localSimulatorURI		URI of a local simulator.
//	 * @throws Exception			<i>to do</i>.
//	 */
//	public void			addSimulationArchitecture(
//		String architectureURI,
//		String localSimulatorURI
//		) throws Exception;
//
//	/**
//	 * remove a relation between a global architecture URI and a local simulator.
//	 * 
//	 * <p><strong>Contract</strong></p>
//	 * 
//	 * <pre>
//	 * pre	{@code architectureURI != null && !architectureURI.isEmpty()}
//	 * pre	{@code !isSimulationArchitecture(globalArchitectureURI)}
//	 * post	{@code !isSimulationArchitecture(globalArchitectureURI)}
//	 * </pre>
//	 *
//	 * @param architectureURI	URI of a global architecture in the application.
//	 * @throws Exception			<i>to do</i>.
//	 */
//	public void			removeSimulationArchitecture(
//		String architectureURI
//		) throws Exception;
}
// -----------------------------------------------------------------------------
