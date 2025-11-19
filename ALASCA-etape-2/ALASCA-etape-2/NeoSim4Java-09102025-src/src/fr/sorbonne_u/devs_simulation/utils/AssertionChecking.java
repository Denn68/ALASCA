package fr.sorbonne_u.devs_simulation.utils;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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
 * The class <code>AssertionChecking</code> implements helper methods to
 * check invariants on objects.
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
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2024-09-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public abstract class	AssertionChecking
{
	/**
	 * check a static implementation invariant expression and print a message
	 * if the expression evaluates to false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code definingClass != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param invariantExpression	result of the invariant expression.
	 * @param definingClass			class defining the invariant expression.
	 * @param message				message to be printed on stdout if {@code invariantExpression} is false.
	 * @return						the value of {@code invariantExpression}.
	 */
	public static boolean	checkStaticImplementationInvariant(
		boolean invariantExpression,
		Class<?> definingClass,
		String message
		)
	{
		assert	definingClass != null :
				new NeoSim4JavaException("definingClass != null");

		if (!invariantExpression) {
			System.out.println(
					"Static implementation invariant violation in class "
					+ definingClass.getSimpleName()
					+ ": " + message);
		}
		return invariantExpression;
	}

	/**
	 * check an implementation invariant expression and print a message if the
	 * expression evaluates to false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code definingClass != null}
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param invariantExpression	result of the invariant expression.
	 * @param definingClass			class defining the invariant expression.
	 * @param instance				object on which the invariant is checked.
	 * @param message				message to be printed on stdout if {@code invariantExpression} is false.
	 * @return						the value of {@code invariantExpression}.
	 */
	public static boolean	checkImplementationInvariant(
		boolean invariantExpression,
		Class<?> definingClass,
		Object instance,
		String message
		)
	{
		assert	definingClass != null :
				new NeoSim4JavaException("definingClass != null");
		assert	instance != null : new NeoSim4JavaException("instance != null");

		if (!invariantExpression) {
			System.out.println(
					"Implementation invariant violation in class "
					+ definingClass.getSimpleName()
					+ " for the instance "
					+ instance.toString()
					+ ": " + message);
		}
		return invariantExpression;
	}

	/**
	 * check a static invariant expression and print a message if the expression
	 * evaluates to false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code definingClass != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param invariantExpression	result of the invariant expression.
	 * @param definingClass			class defining the invariant expression.
	 * @param message				message to be printed on stdout if {@code invariantExpression} is false.
	 * @return						the value of {@code invariantExpression}.
	 */
	public static boolean	checkStaticInvariant(
		boolean invariantExpression,
		Class<?> definingClass,
		String message
		)
	{
		assert	definingClass != null :
				new NeoSim4JavaException("definingClass != null");

		if (!invariantExpression) {
			System.out.println(
					"Static invariant violation in class "
					+ definingClass.getSimpleName()
					+ ": " + message);
		}
		return invariantExpression;
	}

	/**
	 * check an invariant expression and print a message if the expression
	 * evaluates to false.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code definingClass != null}
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param invariantExpression	result of the invariant expression.
	 * @param definingClass			class defining the invariant expression.
	 * @param instance				object on which the invariant is checked.
	 * @param message				message to be printed on stdout if {@code invariantExpression} is false.
	 * @return						the value of {@code invariantExpression}.
	 */
	public static boolean	checkInvariant(
		boolean invariantExpression,
		Class<?> definingClass,
		Object instance,
		String message
		)
	{
		assert	definingClass != null :
				new NeoSim4JavaException("definingClass != null");
		assert	instance != null : new NeoSim4JavaException("instance != null");

		if (!invariantExpression) {
			System.out.println(
					"Invariant violation in class "
					+ definingClass.getSimpleName()
					+ " for the instance "
					+ instance.toString()
					+ ": " + message);
		}
		return invariantExpression;
	}
}
// -----------------------------------------------------------------------------
