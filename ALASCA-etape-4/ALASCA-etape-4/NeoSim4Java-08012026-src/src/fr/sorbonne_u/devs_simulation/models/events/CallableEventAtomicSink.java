package fr.sorbonne_u.devs_simulation.models.events;

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

import fr.sorbonne_u.devs_simulation.models.interfaces.EventsExchangingI;

// -----------------------------------------------------------------------------
/**
 * The class <code>CallableEventAtomicSink</code> implements an event
 * sink descriptor that includes a reference to the sink model on which
 * the methods of <code>EventsExchangingI</code> can be directly called 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * When composing models and connecting them, static sink descriptors that
 * are giving only the URIs of the models are progressively replaced by
 * descriptors containing the callable references to the created models.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code sink != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			CallableEventAtomicSink
extends		EventSink
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** Reference or proxy to the model importing events of type
	 *  <code>eventType</code>.											*/
	public final EventsExchangingI	sink;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	// The invariants are trivially  observed by the fact that all variables
	// are final and the invariant expressions also appears as preconditions
	// on the corresponding parameters in the constructor.

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * creating a sink with the identity event converter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importingAtomicModelReference != null && !importingAtomicModelReference.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code sinkEventType.isAssignableFrom(sourceEventType)}
	 * pre	{@code sink != null}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param importingAtomicModelURI	URI of the model importing the events.
	 * @param sourceEventType			type of incoming events.
	 * @param sinkEventType				type of outgoing events.
	 * @param sink						sink reference to the consuming atomic model.
	 */
	public				CallableEventAtomicSink(
		String importingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		EventsExchangingI sink
		)
	{
		super(importingAtomicModelURI, sourceEventType, sinkEventType);

		assert	sink != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sink != null");

		this.sink = sink;
	}

	/**
	 * creating a sink with the given event converter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importingAtomicModelReference != null && !importingAtomicModelReference.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code converter != null}
	 * pre	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
	 * pre	{@code sinkEventType.isAssignableFrom(converter.getCodomain())}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param importingAtomicModelURI	URI of the model importing the events.
	 * @param sourceEventType			type of incoming events.
	 * @param sinkEventType				type of outgoing events.
	 * @param sink						sink reference to the consuming atomic model.
	 * @param converter					conversion function from the source event type to the sink one.
	 */
	public				CallableEventAtomicSink(
		String importingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		EventsExchangingI sink,
		EventConverterI converter
		)
	{
		super(importingAtomicModelURI, sourceEventType, sinkEventType,
			  converter);

		assert	sink != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sink != null");

		this.sink = sink;
	}
}
// -----------------------------------------------------------------------------
