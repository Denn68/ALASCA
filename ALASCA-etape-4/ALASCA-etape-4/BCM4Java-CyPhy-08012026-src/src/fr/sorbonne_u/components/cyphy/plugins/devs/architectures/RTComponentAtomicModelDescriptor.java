package fr.sorbonne_u.components.cyphy.plugins.devs.architectures;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// new implementation of the DEVS simulation standard for Java.
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
import fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.interfaces.AtomicModelI;
import fr.sorbonne_u.devs_simulation.simulators.AtomicRTEngine;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>RTComponentAtomicModelDescriptor</code> implements a
 * descriptor of a DEVS real time atomic model that is held by a BCM component
 * in a BCM component assembly.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * If Java allowed multiple inheritance, this class would inherit from both
 * <code>ComponentAtomicModelDescriptor</code> and
 * <code>RTAtomicModelDescriptor</code>. Without this possibility, the choice
 * here is to inherit from <code>RTAtomicModelDescriptor</code> and copy the
 * code from <code>ComponentAtomicModelDescriptor</code>. See both for more
 * documentation applying to this class.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code componentReflectionInboundPortURI != null && !componentReflectionInboundPortURI.isEmpty()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2020-12-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			RTComponentAtomicModelDescriptor
extends		RTAtomicModelDescriptor
implements	ComponentModelDescriptorI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L;
	/** URI of the reflection inbound port of the BCM component that is
	 *  holding the described model. 										*/
	protected final String		componentReflectionInboundPortURI;

	// -------------------------------------------------------------------------
	// Constructors and creation static methods
	// -------------------------------------------------------------------------

	/**
	 * create a real time component atomic model descriptor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importedEvents != null}
	 * pre	{@code exportedEvents != null}
	 * pre	{@code compReflIBPURI != null}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be created.
	 * @param importedEvents		events imported by the model.
	 * @param exportedEvents		events exported by the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param compReflIBPURI		URI of the reflection inbound port of the component holding the model.
	 * @throws Exception			<i>to do</i>.
	 */
	public				RTComponentAtomicModelDescriptor(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		) throws Exception
	{
		// neither the model class nor a factory needs to be given because
		// atomic models are created by the cyber-physical components holding
		// the model; we pass here AtomicModel to satisfy the precondition  of
		// the superclass constructor, but it will never be used. Idem for
		// the simulation engine creation mode, the scheduler provider and the
		// acceleration factor.
		super(AtomicModel.class, modelURI, importedEvents, exportedEvents,
			  simulatedTimeUnit, null, () -> null,
			  AtomicRTEngine.DEFAULT_ACCELERATION_FACTOR);

		assert	importedEvents != null :
				new PreconditionException("importedEvents != null");
		assert	exportedEvents != null :
				new PreconditionException("exportedEvents != null");
		assert	compReflIBPURI != null && !compReflIBPURI.isEmpty() :
				new PreconditionException(
						"compReflIBPURI != null && !compReflIBPURI.isEmpty()");

		this.componentReflectionInboundPortURI = compReflIBPURI;
	}

	// -------------------------------------------------------------------------
	// Static methods
	// -------------------------------------------------------------------------

	/**
	 * create a real time component atomic model descriptor from a given class,
	 * possibly using the given real time atomic model factory if one is
	 * provided.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code simulatedTimeUnit != null}
	 * post	{@code RTAtomicModelDescriptor.checkInvariant(ret)}
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be created.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param compReflIBPURI		URI of the reflection inbound port of the component holding the model.
	 * @return						the new atomic model descriptor.
	 * @throws Exception			<i>to do</i>.
	 */
	public static RTComponentAtomicModelDescriptor	create(
		String modelURI,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		) throws Exception
	{
		@SuppressWarnings("unchecked")
		Class<? extends EventI>[] importedEvents = new Class[]{};
		@SuppressWarnings("unchecked")
		Class<? extends EventI>[] exportedEvents = new Class[]{};

		return RTComponentAtomicModelDescriptor.create(
								modelURI, importedEvents, exportedEvents,
								simulatedTimeUnit, compReflIBPURI);
	}

	/**
	 * create a real time component atomic model descriptor from a given class,
	 * possibly using the given real time atomic model factory if one is
	 * provided.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code modelURI != null && !modelURI.isEmpty()}
	 * pre	{@code importedEvents != null}
	 * pre	{@code exportedEvents != null}
	 * pre	{@code simulatedTimeUnit != null}
	 * pre	{@code compReflIBPURI != null}
	 * post	{@code RTAtomicModelDescriptor.checkInvariant(ret)}
	 * </pre>
	 *
	 * @param modelURI				URI of the model to be created.
	 * @param importedEvents		events imported by the model.
	 * @param exportedEvents		events exported by the model.
	 * @param simulatedTimeUnit		time unit used for the simulation clock.
	 * @param compReflIBPURI		URI of the reflection inbound port of the component holding the model.
	 * @return						the new atomic model descriptor.
	 * @throws Exception			<i>to do</i>.
	 */
	public static RTComponentAtomicModelDescriptor	create(
		String modelURI,
		Class<? extends EventI>[] importedEvents,
		Class<? extends EventI>[] exportedEvents,
		TimeUnit simulatedTimeUnit,
		String compReflIBPURI
		) throws Exception
	{
		assert	importedEvents != null :
				new PreconditionException("importedEvents != null");
		assert	exportedEvents != null :
				new PreconditionException("exportedEvents != null");

		return new RTComponentAtomicModelDescriptor(
									modelURI, importedEvents, exportedEvents,
									simulatedTimeUnit, compReflIBPURI);
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.architectures.ComponentModelDescriptorI#getComponentReflectionInboundPortURI()
	 */
	@Override
	public String		getComponentReflectionInboundPortURI()
	{
		return this.componentReflectionInboundPortURI;
	}

	/**
	 * As models inside components need not be created but are rather created
	 * by the components themselves before composing them, this method is not
	 * necessary for component atomic models.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.models.architectures.RTAtomicModelDescriptor#createAtomicModel()
	 */
	@Override
	public AtomicModelI		createAtomicModel()
	{
		throw new RuntimeException(
					"The method createAtomicModel must not be called on "
					+ "instances of RTComponentAtomicModelDescriptor.");
	}
}
// -----------------------------------------------------------------------------
