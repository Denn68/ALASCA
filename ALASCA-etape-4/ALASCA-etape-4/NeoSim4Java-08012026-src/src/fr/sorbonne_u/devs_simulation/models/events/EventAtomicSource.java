package fr.sorbonne_u.devs_simulation.models.events;

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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>AtomicEventSource</code> implements a description of an
 * atomic model as event source but perhaps profoundly inserted in a
 * composite model that may converter the event from the source type to
 * another in the sink type. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code converter != null}
 * invariant	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
 * invariant	{@code sinkEventType.isAssignableFrom(converter.getCodomain())}
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
public class			EventAtomicSource
extends		EventSource
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/**	A converter from the type of the source event to
	 *  <code>eventType</code>. 											*/
	public final EventConverterI	converter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an event source with an identity conversion.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingAtomicModelURI != null && !exportingAtomicModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code sinkEventType.isAssignableFrom(sourceEventType)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingAtomicModelURI	URI of the atomic model exporting the event.
	 * @param sourceEventType			the type of events exported by the atomic model.
	 * @param sinkEventType				the type of events exported through this source after an identity conversion.
	 */
	public				EventAtomicSource(
		String exportingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType
		)
	{
		this(exportingAtomicModelURI,
			 sourceEventType,
			 sinkEventType,
			 new IdentityConverter(sourceEventType, sinkEventType));
	}

	/**
	 * create an event source with the provided conversion function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingAtomicModelURI != null && !exportingAtomicModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code converter != null}
	 * pre	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
	 * pre	{@code sinkEventType.isAssignableFrom(converter.getCodomain())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingAtomicModelURI	URI of the atomic model exporting the event.
	 * @param sourceEventType			the type of events exported by the atomic model.
	 * @param sinkEventType				the type of events exported through this source after an identity conversion.
	 * @param converter					conversion function
	 */
	public				EventAtomicSource(
		String exportingAtomicModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		EventConverterI converter
		)
	{
		super(exportingAtomicModelURI, sourceEventType, sinkEventType);

		assert	converter != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "converter != null");
		assert	converter.getDomain().isAssignableFrom(sourceEventType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "converter.getDomain().isAssignableFrom("
						+ "sourceEventType)");
		assert	sinkEventType.isAssignableFrom(converter.getCodomain()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkEventType.isAssignableFrom("
						+ "converter.getCodomain())");

		this.converter = converter;
	}
}
// -----------------------------------------------------------------------------
