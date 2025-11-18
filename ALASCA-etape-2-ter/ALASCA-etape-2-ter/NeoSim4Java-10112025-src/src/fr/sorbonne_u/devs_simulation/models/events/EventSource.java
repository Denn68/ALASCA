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
 * The class <code>EventSource</code> implements a source of event where
 * events of a source type are transmitted to a sink waiting for a sink events
 * type.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
 * invariant	{@code sourceEventType != null}
 * invariant	{@code sinkEventType != null}
 * invariant	{@code sinkEventType.isAssignableFrom(sourceEventType)}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-04-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			EventSource
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** URI of the model that exports the event type
	 *  <code>sourceEventType</code>.										*/
	public final String						exportingModelURI;
	/** Type of events exported by the model which URI is
	 *  <code>exportingModelURI</code>.										*/
	public final Class<? extends EventI>	sourceEventType;
	/** Type of events exported by the model after conversion.				*/
	public final Class<? extends EventI>	sinkEventType;

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
	 * create an event source where the type of events is the same between the
	 * source and the sink.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
	 * pre	{@code eventType != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the exporting model.
	 * @param eventType			the type of events coming from the source and going to the sink.
	 */
	public				EventSource(
		String exportingModelURI,
		Class<? extends EventI> eventType
		)
	{
		this(exportingModelURI, eventType, eventType);
	}

	/**
	 * create an event source where the type of events from the source is
	 * different from the one going to the sink but where no conversion is
	 * needed.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code exportingModelURI != null && !exportingModelURI.isEmpty()}
	 * pre	{@code sourceEventType != null}
	 * pre	{@code sinkEventType != null}
	 * pre	{@code sinkEventType.isAssignableFrom(sourceEventType)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param exportingModelURI	URI of the exporting model.
	 * @param sourceEventType	type of events coming from the source.
	 * @param sinkEventType		type of events going to the sink.
	 */
	public				EventSource(
		String exportingModelURI,
		Class<? extends EventI> sourceEventType,
		Class<? extends EventI> sinkEventType
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
		assert	sinkEventType.isAssignableFrom(sourceEventType) :
				new NeoSim4JavaException("Precondition violation: "
						+ "sinkEventType.isAssignableFrom(sourceEventType)");

		this.exportingModelURI = exportingModelURI;
		this.sourceEventType = sourceEventType;
		this.sinkEventType = sinkEventType;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean		equals(Object obj)
	{
		if (obj instanceof EventSource) {
			EventSource ep = (EventSource) obj;
			return this.exportingModelURI.equals(ep.exportingModelURI) &&
					this.sourceEventType.equals(ep.sourceEventType) &&
					this.sinkEventType.equals(ep.sinkEventType);
		} else {
			return false;
		}
	}
}
// -----------------------------------------------------------------------------
