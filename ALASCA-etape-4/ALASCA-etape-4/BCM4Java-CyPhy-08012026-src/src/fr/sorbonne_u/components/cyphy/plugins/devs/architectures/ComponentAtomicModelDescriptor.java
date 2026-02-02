package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

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

import java.util.concurrent.TimeUnit;
import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.architectures.AtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PostconditionException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>ComponentAtomicModelDescriptor</code> implements a descriptor
 * of a DEVS atomic model that is held by a BCM component in a BCM component
 * assembly; using the DEVS closure over composition property, the model
 * actually created by the BCM component can be a coupled model but it will be
 * described as an atomic one in the global architecture for the
 * interconnection.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When integrating DEVS simulation models with BCM components, a simulation
 * architecture (DEVS models composition) is mapped onto a component assembly.
 * Cyber-physical components have simulation models implementing their
 * behavioural model. These models can be obtained by composing submodels
 * within a coupled model at the level of the component that will be seen
 * as an atomic model from the outside. This is described by the local (to
 * each component) simulation architecture. At the component assembly level,
 * a global simulation architecture describes the way component simulation
 * models are composed to obtain a system-wide simulation architecture. The
 * present class is used in global architectures to describe component
 * simulation models.
 * </p>
 * <p>
 * Because cyber-physical components are required to create their own
 * simulation models from their local architecture and the atomic simulation
 * plug-in that is used to factor the code required to attach atomic simulation
 * models to BCM components, this descriptor is passive, holding only
 * information used by the parent coupled model in the process of
 * interconnecting models to create the global simulation architecture over
 * a component assembly.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code componentReflectionInboundPortURI != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2019-06-17</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ComponentAtomicModelDescriptor
extends		AtomicModelDescriptor
implements	ComponentModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** URI of the reflection inbound port of the BCM component that is
	 *  holding the described model. 										*/
	protected final String		componentReflectionInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors and creation static methods
	// -------------------------------------------------------------------------

	/**
	 * create a component atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code compReflIBPURI != null && !compReflIBPURI.isEmpty()}
	 * post	{@code ComponentAtomicModelDescriptor.checkInvariant(this)}
	 * </pre>
	 *
	 * @param modelURI			URI of the model to be created.
	 * @param importedEvents	events imported by the model.
	 * @param exportedEvents	events exported by the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param compReflIBPURI	URI of the reflection inbound port of the component holding the model.
	 */
	protected			ComponentAtomicModelDescriptor(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		)
	{
		// neither the model class nor a factory needs to be given because
		// atomic models are created by the cyber-physical components holding
		// the model; we pass here AtomicModel to satisfy the precondition  of
		// the superclass constructor, but it will never be used.
		super(AtomicModel.class, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit, null);

		assert	compReflIBPURI != null && !compReflIBPURI.isEmpty() :
				new PreconditionException(
						"compReflIBPURI != null && !compReflIBPURI.isEmpty()");

		this.componentReflectionInboundPortURI = compReflIBPURI;

		assert	ComponentAtomicModelDescriptor.checkInvariant(this) :
				new PostconditionException(
						"ComponentAtomicModelDescriptor.checkInvariant(this)");
	}

	/**
	 * check the invariant on the part of the descriptor implemented in this
	 * class (a similar method in the superclass addresses the invariant at
	 * this level).
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	descriptor to be checked.
	 * @return	true if the descriptor is conform to its invariant.
	 */
	public static boolean	checkInvariant(ComponentAtomicModelDescriptor d)
	{
		assert	d != null : new PreconditionException("d != null") ;

		boolean hasHostComponent = d.componentReflectionInboundPortURI != null;
		assert	hasHostComponent :
					new InvariantException(
							"component reflection inbound port URI is null!");
		return hasHostComponent;
	}

	/**
	 * create a component atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code compReflIBPURI != null}
	 * post	{@code AbstractAtomicModelDescriptor.checkInvariant(ret)}
	 * post	{@code ComponentAtomicModelDescriptor.checkInvariant(ret)}
	 * </pre>
	 *
	 * @param modelURI			URI of the model to be created.
	 * @param importedEvents	events imported by the model.
	 * @param exportedEvents	events exported by the model.
	 * @param simulatedTimeUnit	time unit used for the simulation clock.
	 * @param compReflIBPURI	URI of the reflection inbound port of the component holding the model.
	 * @return					the new model descriptor.
	 * @throws Exception		<i>to do.</i>
	 */
	@SuppressWarnings("unchecked")
	public static ComponentAtomicModelDescriptor	create(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		) throws Exception
	{
		if (importedEvents == null) {
			importedEvents = new Class[]{} ;
		}
		if (exportedEvents == null) {
			exportedEvents = new Class[]{} ;
		}
		return new ComponentAtomicModelDescriptor(
								modelURI, importedEvents, exportedEvents,
								simulatedTimeUnit, compReflIBPURI) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#getComponentReflectionInboundPortURI()
	 */
	@Override
	public String		getComponentReflectionInboundPortURI()
	{
		return this.componentReflectionInboundPortURI ;
	}

	/**
	 * As models inside components need not be created but are rather created
	 * by the components themselves before composing them, this method is not
	 * necessary for component atomic models.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.AbstractAtomicModelDescriptor#createAtomicModel()
	 */
	@Override
	public AtomicModelI	createAtomicModel()
	{
		throw new RuntimeException("The method createAtomicModel must not be "
							+ "called on instances of "
							+ "ComponentAtomicModelDescriptor.");
	}
}
// -----------------------------------------------------------------------------
