package fr.sorbonne_u.components.cyphy.plugins.devs;

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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import fr.sorbonne_u.exceptions.PreconditionException;

// -----------------------------------------------------------------------------
/**
 * The class <code>StandardCoordinatorPluginFactory</code> defines a standard
 * coordinator plug-in factory which creates the coordinator plug-in from a
 * given class; if no class is given, the standard on is used.
 *
 * <p><strong>Description</strong></p>
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
 * invariant	{@code getPluginClass() != null}
 * </pre>
 * 
 * <p>Created on : 2020-12-11</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			StandardCoordinatorPluginFactory
implements	CoordinatorPluginFactoryI,
			Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long							serialVersionUID = 1L;
	/** the class from which the plug-in is instantiated.					*/
	protected final Class<? extends CoordinatorPlugin>	pluginClass;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a standard coordinator plug-in factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getPluginClass().equals(CoordinatorPlugin.class)}
	 * </pre>
	 *
	 */
	public				StandardCoordinatorPluginFactory()
	{
		this(CoordinatorPlugin.class);
	}

	/**
	 * create a standard coordinator plug-in factory with the given plug-in
	 * class.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pluginClass != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param pluginClass	the instantiation class of the plug-in.
	 */
	public				StandardCoordinatorPluginFactory(
		Class<? extends CoordinatorPlugin> pluginClass
		)
	{
		assert	pluginClass != null :
				new PreconditionException("pluginClass != null");
		this.pluginClass = pluginClass;
	}

	// -------------------------------------------------------------------------
	// Instance methods
	// -------------------------------------------------------------------------

	/**
	 * return the plug-in class associated to the factory.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the plug-in class associated to the factory.
	 */
	public Class<? extends CoordinatorPlugin>	getPluginClass()
	{
		return this.pluginClass;
	}

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.CoordinatorPluginFactoryI#createCoordinatorPlugin()
	 */
	@Override
	public CoordinatorPlugin	createCoordinatorPlugin()
	throws	NoSuchMethodException,
			InvocationTargetException
	{
		Constructor<? extends CoordinatorPlugin> cons =
							this.pluginClass.getConstructor(new Class<?>[]{});
		cons.setAccessible(true);
		try {
			return cons.newInstance(new Object[]{});
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
// -----------------------------------------------------------------------------
