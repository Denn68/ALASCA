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
 * The interface <code>EventConverterI</code> defines the method that must
 * be implemented to convert events from a source type to a sink type.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: use Java 8 lambdas...
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
public interface		EventConverterI
extends		Serializable
{
	/**
	 * create the composition of <code>f1</code> and <code>f2</code> i.e.,
	 * for an event <code>e</code> the resulting converter will apply the
	 * conversion <code>f1(f2(e))</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code f1 != null && f2 != null}
	 * pre	{@code f1.getDomain().isAssignableFrom(f2.getCodomain())}
	 * post	{@code f2.getDomain().isAssignableFrom(return.getDomain())}
	 * post	{@code return.getCodomain().isAssignableFrom(f1.getCodomain())}
	 * </pre>
	 *
	 * @param f1	first converter.
	 * @param f2	second converter.
	 * @return		the composition of the two converters.
	 */
	static EventConverterI	compose(EventConverterI f1, EventConverterI f2)
	{
		assert	f1 != null && f2 != null :
				new NeoSim4JavaException("Precondition violation: "
								+ "f1 != null && f2 != null");
		assert	f1.getDomain().isAssignableFrom(f2.getCodomain()) :
				new NeoSim4JavaException("Precondition violation: "
								+ "f1.getDomain().equals(f2.getCodomain())");

		if (f1.isIdentityConverter()) {
			return f2;
		} else if (f2.isIdentityConverter()) {
			return f1;
		} else {
			return new CompositeConverter(f1, f2);
		}
	}

	/**
	 * return the domain of the convert function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return ! null}
	 * </pre>
	 *
	 * @return	the domain of the convert function.
	 */
	public Class<? extends EventI>	getDomain();

	/**
	 * return the codomain of the convert function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return ! null}
	 * </pre>
	 *
	 * @return	the codomain of the convert function.
	 */
	public Class<? extends EventI>	getCodomain();

	/**
	 * convert an event from a source type to one of a sink type.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code e != null}
	 * pre	{@code getDomain().isAssignableFrom(e.getClass())}
	 * post	{@code return != null && getCodomain().isAssignableFrom(return.getClass())}
	 * </pre>
	 *
	 * @param e	event to be converted.
	 * @return	converted event.
	 */
	public EventI 		convert(EventI e);

	/**
	 * return true if the converter is the identity function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the converter is the identity function.
	 */
	default public boolean	isIdentityConverter()
	{
		return false;
	};
}
// -----------------------------------------------------------------------------
