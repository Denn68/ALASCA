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
 * The class <code>CompositeConverter</code> implements the result of the
 * (function) composition of two converter functions.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: use Java 8 lambdas...
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code f1 != null && f2 != null}
 * invariant	{@code f1.getDomain().isAssignableFrom(f2.getCodomain()}
 * invariant	{@code f2.getDomain().isAssignableFrom(getDomain())}
 * invariant	{@code getCodomain().isAssignableFrom(f1.getCodomain())}
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
public class			CompositeConverter
extends		AbstractEventConverter
implements	EventConverterI
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;
	/** converter to be applied after {@code f2}.							*/
	protected final EventConverterI	f1;
	/** first converter to be applied in the composition.					*/
	protected final EventConverterI f2;

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
	 * create a composite converter from the two given converters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f1 != null && f2 != null}
	 * pre	{@code f1.getDomain().isAssignableFrom(f2.getCodomain()}
	 * post	{@code f2.getDomain().isAssignableFrom(getDomain())}
	 * post	{@code getCodomain().isAssignableFrom(f1.getCodomain())}
	 * </pre>
	 *
	 * @param f1	converter to be applied after {@code f2}.
	 * @param f2	first converter to be applied in the composition.
	 */
	public				CompositeConverter(
		EventConverterI f1,
		EventConverterI f2
		)
	{
		super(f2.getDomain(), f1.getCodomain());

		assert	f1.getDomain().isAssignableFrom(f2.getCodomain()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "f1.getDomain().isAssignableFrom(f2.getCodomain())");

		this.f1 = f1;
		this.f2 = f2;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventConverterI#convert(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public EventI		convert(EventI e)
	{
		assert	e != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "e != null");
		assert	getDomain().isAssignableFrom(e.getClass()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getDomain().isAssignableFrom(e.getClass())");

		return f1.convert(f2.convert(e));
	}
}
// -----------------------------------------------------------------------------
