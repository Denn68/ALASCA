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

import java.io.Serializable;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>EventSink</code> implements an event sink to an importing
 * model taking an event of the source event type and providing a corresponding
 * event of the sink event type.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code importingModelURI != null && !importingModelURI.isEmpty()}
 * invariant	{@code sourceEventType != null}
 * invariant	{@code sinkEventType != null}
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
 * <p>Created on : 2018-05-02</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			EventSink
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** Type of event converted to the imported by the model with URI
	 *  <code>modelURI</code> by the converter.								*/
	public final Class<? extends EventI>	sourceEventType;
	/** Type of event imported by the model with URI
	 *  <code>modelURI</code>.												*/
	public final Class<? extends EventI>	sinkEventType;
	/**	A converter from the type of the source event to
	 *  <code>eventType</code>. 											*/
	public final EventConverterI			converter;
	/** URI of the model importing events of type
	 *  <code>sinkEventType</code>.											*/
	public final String						importingModelURI;

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
	 * create an event sink where the type of events is the same between the
	 * source and the sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importingModelURI != null && !importingModelURI.isEmpty()}
	 * pre	{@code eventType != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param importingModelURI	URI of the importing model.
	 * @param eventType			the type of events coming from the source and going to the sink.
	 */
	public				EventSink(
		String importingModelURI,
		Class<? extends EventI> eventType
		)
	{
		this(importingModelURI, eventType, eventType,
			 new IdentityConverter(eventType, eventType));
	}

	/**
	 * create an event sink where the type of events from the source is
	 * different from the one going to the sink but where no conversion is
	 * needed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importingModelURI != null && !importingModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code sinkEventType.isAssignableFrom(sourceEventType)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param importingModelURI	URI of the importing model.
	 * @param sourceEventType	type of events coming from the source.
	 * @param sinkEventType		type of events going to the sink.
	 */
	public				EventSink(
		String importingModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType
		)
	{
		this(importingModelURI, sourceEventType, sinkEventType,
			 new IdentityConverter(sourceEventType, sinkEventType));
	}

	/**
	 * create an event sink where the type of events from the source is
	 * different from the one going to the sink and require an explicit
	 * conversion.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code importingModelURI != null && !importingModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code converter != null}
	 * pre	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
	 * pre	{@code sinkEventType.isAssignableFrom(converter.getCodomain())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param importingModelURI	URI of the importing model.
	 * @param sourceEventType	type of events coming from the source.
	 * @param sinkEventType		type of events going to the sink.
	 * @param converter			conversion function.
	 */
	public				EventSink(
		String importingModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType,
		EventConverterI converter
		)
	{
		super();

		assert	importingModelURI != null && !importingModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "importingModelURI != null && "
						+ "!importingModelURI.isEmpty()");
		assert	sourceEventType != null && sinkEventType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sourceEventType != null && sinkEventType != null");
		assert	converter != null :
				new NeoSim4JavaException("Precondition violation: converter != null");
		assert	converter.getDomain().isAssignableFrom(sourceEventType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "converter.getDomain().isAssignableFrom("
						+ "sourceEventType)");
		assert	sinkEventType.isAssignableFrom(converter.getCodomain()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkEventType.isAssignableFrom(converter."
						+ "getCodomain())");

		this.importingModelURI = importingModelURI;
		this.sourceEventType = sourceEventType;
		this.sinkEventType = sinkEventType;
		this.converter = converter;
	}
}
// -----------------------------------------------------------------------------
