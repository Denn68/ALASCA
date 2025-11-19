package fr.sorbonne_u.devs_simulation.hioa.annotations;

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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// -----------------------------------------------------------------------------
/**
 * The annotation <code>ModelExportedVariable</code> is used to declare
 * variables that are exported by HIOA type of DEVS atomic models.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The annotation must be put on HIOA atomic model classes that export
 * variables. It can be repeated to cater for multiple exported variables.
 * </p>
 * 
 * TODO: add treatment in library code.
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code name() != null && !name().isEmpty()}
 * invariant	{@code type() != null}
 * </pre>
 * 
 * <p>Created on : 2022-10-14</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(ModelExportedVariables.class)
@Inherited
@Documented
public @interface		ModelExportedVariable
{
	/**
	 * return the name of the variable as declared in the model class.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null && !ret.isEmpty()}
	 * </pre>
	 *
	 * @return	the name of the variable as declared in the model class.
	 */
	String				name();

	/**
	 * return the type of the variable as declared in the model class.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code ret != null}
	 * </pre>
	 *
	 * @return	the type of the variable as declared in the model class.
	 */
	Class<?>			type();
}
// -----------------------------------------------------------------------------
