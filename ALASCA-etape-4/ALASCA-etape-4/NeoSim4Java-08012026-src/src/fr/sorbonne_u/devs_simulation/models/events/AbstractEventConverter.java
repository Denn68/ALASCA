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

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractEventConverter</code> defines the most
 * general information about event converters.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * In DEVS, models can export events of a type t1 to a model that consumes
 * events of type t2 provided that a function is given to convert events of
 * type t1 into events of type t2. This allows to compose models that were
 * developed independently by providing the necessary glue at composition
 * time. As events exported or imported by submodels can also be shadowed
 * as events of different types re-exported or imported by their parent model,
 * the conversion also allows to also cater for that. Hence a event emitted
 * by a model can pass through several conversions before being consumed by
 * its destination model. Therefore, conversion functions need to be composed
 * to provide an end-to-end conversion.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-06-05</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AbstractEventConverter
implements	EventConverterI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	/** Domain of the conversion function i.e., the type of events
	 *  that are converted.												*/
	protected final Class<? extends EventI>	domain;
	/** Codomain of the conversion function i.e. the type of events
	 *  that are produced after conversion.								*/
	protected final Class<? extends EventI>	codomain;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an event converter with the given domain and codomain.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code domain != null}
	 * pre	{@code codomain != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param domain	type of events to be converted.
	 * @param codomain	type of events to which events are converted.
	 */
	public				AbstractEventConverter(
		Class<? extends EventI> domain,
		Class<? extends EventI> codomain
		)
	{
		super();
		this.domain = domain;
		this.codomain = codomain;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventConverterI#getDomain()
	 */
	@Override
	public Class<? extends EventI>	getDomain()
	{
		return this.domain;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventConverterI#getCodomain()
	 */
	@Override
	public Class<? extends EventI>	getCodomain()
	{
		return this.codomain;
	}
}
// -----------------------------------------------------------------------------

