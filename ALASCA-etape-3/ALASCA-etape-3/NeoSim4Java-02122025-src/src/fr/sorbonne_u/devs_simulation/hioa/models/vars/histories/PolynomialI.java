package fr.sorbonne_u.devs_simulation.hioa.models.vars.histories;

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
 * The interface <code>PolynomialI</code> declares the methods for classes
 * polynomial functions.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-12-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		PolynomialI
{
	/** tolerance used when comparing expected versus given values and
	 *  derivatives															*/
	public static final double	TOLERANCE = 1.0e-10;

	/**
	 * return the value of the polynomial for the parameter {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	the actual parameter of the polynomial.
	 * @return	the value of the polynomial for the parameter {@code t}.
	 */
	public double		valueOf(double t);

	/**
	 * return the first derivative of the polynomial for the parameter
	 * {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	the actual parameter of the polynomial.
	 * @return	the first derivative of the polynomial for the parameter {@code t}.
	 */
	public double		firstDerivative(double t);

	/**
	 * return true if {@code v} is equal to the evaluation of this polynomial
	 * at {@code t}; makes possible to define some tolerance on the comparison.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	abscissa at which the test is made.
	 * @param v	the expected value at {@code t}.
	 * @return	true if {@code v} is equal to the evaluation of this polynomial at {@code t}.
	 */
	default boolean		assertValueEquals(double t, double v)
	{
		double c = valueOf(t);
		return v >= c - TOLERANCE && v <= c + TOLERANCE;
	}

	/**
	 * return true if {@code d} is equal to the derivative of this polynomial
	 * at {@code t}; makes possible to define some tolerance on the comparison.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	abscissa at which the test is made.
	 * @param d	the expected derivative at {@code t}.
	 * @return	true if {@code v} is equal to the evaluation of this polynomial at {@code t}.
	 */
	default boolean		assertDerivativeEquals(double t, double d)
	{
		double c = firstDerivative(t);
		return d >= c - TOLERANCE && d <= c + TOLERANCE;

	}
}
// -----------------------------------------------------------------------------
