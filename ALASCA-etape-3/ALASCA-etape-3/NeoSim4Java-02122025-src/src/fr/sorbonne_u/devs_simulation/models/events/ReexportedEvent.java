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
 * The class <code>ReexportedEvent</code> implements the description of
 * events that are exported by submodels and re-exported by an
 * ancestor coupled model.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
 * invariant	{@code sourceEventType != null}
 * invariant	{@code sinkEventType != null}
 * invariant	{@code converter != null}
 * invariant	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
 * invariant	{@code sinkEventType.isAssignableFrom(converter.getCodomain())} * 
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
public class			ReexportedEvent
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** URI of the submodel that exports the event type
	 *  <code>sourceEventType</code>.										*/
	public final String				exportingModelURI;
	/** The type of event exported by one of the submodels of a coupled
	 *  model that reexports it.											*/
	public Class<? extends EventI>	sourceEventType;
	/** The type of event exported by the coupled model.					*/
	public Class<? extends EventI>	sinkEventType;
	/** A converter that can transforms an event of type
	 *  <code>eventType</code> into an event of a type exported by this
	 *  coupled model.														*/
	public EventConverterI			converter;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a description of a re-exported type of events where the type
	 * of events coming from the source is the same as the one going to the
	 * sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
	 * pre	{@code eventType != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the model exporting the events.
	 * @param eventType			type of re-exported events.
	 */
	public				ReexportedEvent(
		String exportingModelURI,
		Class<? extends EventI> eventType
		)
	{
		this(exportingModelURI, eventType, eventType,
			 new IdentityConverter(eventType, eventType));
	}

	/**
	 * create a description of a re-exported type of events where the type
	 * of events coming from the source requires a conversion to the one
	 * going to the sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code converter != null}
	 * pre	{@code converter.getDomain().isAssignableFrom(sourceEventType)}
	 * pre	{@code sinkEventType.isAssignableFrom(converter.getCodomain())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the model exporting the events.
	 * @param sourceEventType	type of events coming from the source.
	 * @param sinkEventType		type of events going to the sink.
	 * @param converter			conversion function.
	 */
	public				ReexportedEvent(
		String exportingModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI>	sinkEventType,
		EventConverterI converter
		)
	{
		super();

		assert	exportingModelURI != null && !exportingModelURI.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "exportingModelURI != null && "
						+ "!exportingModelURI.isEmpty()");
		assert	sourceEventType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sourceEventType != null");
		assert	sinkEventType != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkEventType != null");
		assert	converter != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "converter != null");
		assert	converter.getDomain().isAssignableFrom(sourceEventType) :
				new NeoSim4JavaException("Precondition violation:"
						+ "converter.getDomain().isAssignableFrom(sourceEventType)");
		assert	sinkEventType.isAssignableFrom(converter.getCodomain()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkEventType.isAssignableFrom(converter."
						+ "getCodomain())");

		this.exportingModelURI = exportingModelURI;
		this.sourceEventType = sourceEventType;
		this.sinkEventType = sinkEventType;
		this.converter = converter;
	}
}
// -----------------------------------------------------------------------------
