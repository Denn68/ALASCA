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

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>Piece</code> implements a piece in a piecewise-defined
 * function; a piece is defined over an interval and is interpolated by a
 * polynomial.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * a piece is defined over an interval and is interpolated by a
 * polynomial.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code interval != null && polynomial != null}
 * invariant	{@code polynomial.value(interval.getBegin()) == interval.getBeginValue()}
 * invariant	{@code polynomial.value(interval.getEnd()) == interval.getEndValue()}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2021-12-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Piece
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** interval over which the piece is defined.							*/
	protected final Interval		interval;
	/** polynomial used to interpolated in this piece.						*/
	protected final PolynomialI		polynomial;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	// The invariants are trivially  observed by the fact that all variables
	// are final and the invariant expressions also appears as preconditions
	// on the corresponding parameters in the constructor.

	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new piece from the given interval and polynomial.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code interval != null && polynomial != null}
	 * pre	{@code polynomial.value(interval.getBegin()) == interval.getBeginValue()}
	 * pre	{@code polynomial.value(interval.getEnd()) == interval.getEndValue()}
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval		interval over which the piece is defined.
	 * @param polynomial	polynomial used to interpolated in this piece.
	 */
	public				Piece(Interval interval, PolynomialI polynomial)
	{
		super();

		assert	interval != null && polynomial != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "interval != null && polynomial != null");
		assert	polynomial.assertValueEquals(interval.getBegin(),
													interval.getBeginValue()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "polynomial.assertValueEquals(interval.getBegin(), "
						+ "interval.getBeginValue())");
		assert	polynomial.assertValueEquals(interval.getEnd(),
													interval.getEndValue()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "polynomial.assertValueEquals(interval.getEnd(), "
						+ "interval.getEndValue())");

		this.interval = interval;
		this.polynomial = polynomial;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * @return	the interval of definition of the piece.
	 */
	/* package */ Interval	getInterval()
	{
		return interval;
	}

	/**
	 * @return	the polynomial associated with the piece.
	 */
	/* package */ PolynomialI	getPolynomial()
	{
		return polynomial;
	}

	/**
	 * return true if {@code t} is in the definition domain of the piece.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time to be tested.
	 * @return	true if {@code t} is in the definition domain of the piece.
	 */
	public boolean		in(double t)
	{
		return this.interval.in(t);
	}

	/**
	 * return true if {@code t} is before the definition domain of the piece.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time to be tested.
	 * @return	true if {@code t} is before the definition domain of the piece.
	 */
	public boolean		before(double t)
	{
		return this.interval.before(t);
	}

	/**
	 * return true if {@code t} is after the definition domain of the piece.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time to be tested.
	 * @return	true if {@code t} is after the definition domain of the piece.
	 */
	public boolean		after(double t)
	{
		return this.interval.after(t);
	}

	/**
	 * return true if this piece comes immediately after {@code p}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code p != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param p	piece to be tested.
	 * @return	true if this piece comes immediately after {@code p}.
	 */
	public boolean		followsImmediately(Piece p)
	{
		assert	p != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "p != null");

		return this.interval.followsImmediately(p.interval);
	}

	/**
	 * return the value of the function at {@code t} interpolated by the
	 * polynomial but linearly extrapolated if outside the interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time at which the function must be evaluated.
	 * @return	the value of the function at {@code t}.
	 */
	public double		value(double t)
	{
		if (this.interval.before(t)) {
			return this.interval.getBeginValue() + 
					this.polynomial.firstDerivative(this.interval.getBegin()) *
												(t - this.interval.getBegin());
		} else if (this.interval.after(t)) {
			return this.interval.getEndValue() +
					this.polynomial.firstDerivative(this.interval.getEnd()) *
												(t - this.interval.getEnd());
		} else {
			return this.polynomial.valueOf(t);
		}
	}

	/**
	 * return the first derivative of the function at {@code t} interpolated by
	 * the polynomial but linearly extrapolated if outside the interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code in(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time at which the function must be evaouated.
	 * @return	the first derivative of the function at {@code t}.
	 */
	public double		firstDerivative(double t)
	{
		if (this.interval.before(t)) {
			return this.polynomial.firstDerivative(this.interval.getBegin());
		} else if (this.interval.after(t)) {
			return this.polynomial.firstDerivative(this.interval.getEnd());
		} else {
			return this.polynomial.firstDerivative(t);
		}
	}

	/**
	 * return true if {@code v} is equal to the evaluation of of the function
	 * represented by this piece at {@code t}; makes possible to define some
	 * tolerance on the comparison.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code in(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	abscissa at which the test is made.
	 * @param v	the expected value at {@code t}.
	 * @return	true if {@code v} is equal to the evaluation of this polynomial at {@code t}.
	 */
	public boolean		assertValueEquals(double t, double v)
	{
		assert	in(t) : new NeoSim4JavaException("Precondition violation: "
				+ "in(t)");
		return this.polynomial.assertValueEquals(t, v);
	}

	/**
	 * return true if {@code d} is equal to the derivative of the function
	 * represented by this piece at {@code t}; makes possible to define some
	 * tolerance on the comparison.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code in(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	abscissa at which the test is made.
	 * @param d	the expected derivative at {@code t}.
	 * @return	true if {@code v} is equal to the evaluation of this polynomial at {@code t}.
	 */
	public boolean		assertDerivativeEquals(double t, double d)
	{
		assert	in(t) : new NeoSim4JavaException("Precondition violation: "
				+ "in(t)");
		return this.polynomial.assertDerivativeEquals(t, d);

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		sb.append(this.interval);
		sb.append(", ");
		sb.append(this.polynomial);
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
