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

import java.util.Vector;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>IncrementallyInterpolatedFunction</code> implements a
 * piecewise defined functions over successive intervals where each piece is
 * interpolated using an interpolator generator (creating an appropriate
 * interpolating function for each) and which can be extended with new pieces
 * at the end of its current domain by adding points.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Interpolator generators can be either unconstrained, with only beginning and
 * ending points defined, or they can be constrained by known first order
 * derivatives for each point.
 * </p>
 * <p>
 * This class need not be thread safe, as each of its instance will be owned
 * by one and only one <code>ValueHistory</code> instance and its reference
 * will be confined within this <code>ValueHistory</code> instance.
 * Hence, ensuring the thread safe property is left to the class
 * <code>ValueHistory</code>.
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
 * <p>Created on : 2021-12-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			IncrementallyInterpolatedFunction
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** the generator used to create interpolation functions.				*/
	protected final IncrementalInterpolatorGeneratorI
									interpolationFunctionGenerator;
	/** the current existing pieces in the function.						*/
	protected final Vector<Piece>	pieces;
	/** true if the function is still empty but a first point has been
	 *  given.																*/
	protected boolean				firstValueSet;
	/** if the first value is set, the parameter that was given.			*/
	protected double				firstParameter;
	/** if the first value is set, the first order that was given.			*/
	protected double				firstDerivative;
	/** if the first value is set, the value that was given.				*/	
	protected double				firstValue;

	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new incrementally interpolated function with the given
	 * interpolator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code interpolator != null}
	 * post	{@code isEmpty() && !hasFirstValue()}
	 * </pre>
	 *
	 * @param interpolator	interpolator used in the function.
	 */
	public				IncrementallyInterpolatedFunction(
		IncrementalInterpolatorGeneratorI interpolator
		)
	{
		assert	interpolator != null :
				new NeoSim4JavaException("Precondition violation: "
												+ "interpolator != null");

		this.interpolationFunctionGenerator = interpolator;
		this.pieces = new Vector<>();
		this.firstValueSet = false;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the function has no definition yet.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the function has no definition yet.
	 */
	public boolean		isEmpty()
	{
		return this.pieces.isEmpty();
	}

	/**
	 * return true if the function is still empty but a first point has been
	 * given.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the function is still empty but a first point has been given.
	 */
	public boolean		hasFirstValue()
	{
		return this.firstValueSet;
	}

	/**
	 * return true if {@code t} is within the definition domain of the function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time for which the test is made.
	 * @return	true if {@code t} is within the definition domain of the function.
	 */
	public boolean		in(double t)
	{
		assert	!this.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isEmpty()");

		return this.pieces.firstElement().getInterval().getBegin() <= t &&
						t <= this.pieces.lastElement().getInterval().getEnd();
	}

	/**
	 * return true if {@code t} is before the definition domain of the function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty() || hasFirstValue()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time for which the test is made.
	 * @return	true if {@code t} is before the definition domain of the function.
	 */
	public boolean		before(double t)
	{
		assert	!this.isEmpty() || this.hasFirstValue() :
				new NeoSim4JavaException("Precondition violation: "
										+ "!isEmpty() || hasFirstValue()");

		if (!this.isEmpty()) {
			return t < this.pieces.firstElement().getInterval().getBegin();
		} else {
			return t < this.firstParameter;
		}
	}

	/**
	 * return true if {@code t} is after the definition domain of the function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty() || hasFirstValue()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time for which the test is made.
	 * @return	true if {@code t} is after the definition domain of the function.
	 */
	public boolean		after(double t)
	{
		assert	!this.isEmpty() || this.hasFirstValue() :
				new NeoSim4JavaException("Precondition violation: "
										+ "!isEmpty() || hasFirstValue()");

		if (!this.isEmpty()) {
			return t > this.pieces.lastElement().getInterval().getEnd();
		} else {
			return t > this.firstParameter;
		}
	}

	/**
	 * delete the oldest piece in the function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty()}
	 * post	{@code !isEmpty() || !hasFirstValue()}
	 * </pre>
	 *
	 */
	public void			deleteOldestPiece()
	{
		assert	!this.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isEmpty()");

		this.pieces.remove(0);
		if (this.pieces.isEmpty()) {
			this.firstValueSet = false;
			this.firstParameter = 0.0;
			this.firstValue = 0.0;
		}
	}

	/**
	 * trim the function to drop pieces for which the definition domain is
	 * older than {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t			oldest time for which a piece is maintained.
	 */
	public void			trimToTime(double t)
	{
		while (this.pieces.size() > 0 && this.pieces.get(0).after(t)) {
			this.deleteOldestPiece();
		}
	}

	/**
	 * return	true if this function is a constrained interpolation function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if this function is a constrained interpolation function.
	 */
	public boolean		isConstrainedInterpolation()
	{
		return this.interpolationFunctionGenerator instanceof
							ConstrainedIncrementalInterpolatorGeneratorI;
	}

	/**
	 * add a new point in the function following the last piece; the function
	 * is not really defined until at least two points have been given.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isConstrainedInterpolation()}
	 * pre	{@code isEmpty() || after(t)}
	 * post	{@code !(isEmpty()_pre && !hasFirstValue()_pre) || hasFirstValue()}
	 * post	{@code !(isEmpty()_pre && hasFirstValue()_pre) || (!hasFirstValue() && !isEmpty()}
	 * post {@code isEmpty() || in(t)}
	 * </pre>
	 *
	 * @param x new value for f(t).
	 * @param t	time coordinate of f.
	 */
	public void			addPoint(double x, double t)
	{
		assert	!this.isConstrainedInterpolation() :
				new NeoSim4JavaException("Precondition violation: "
										+ "!isConstrainedInterpolation()");
		assert	this.isEmpty() || this.after(t) :
				new NeoSim4JavaException("Precondition violation: "
										+ "isEmpty() || after(t)");
		boolean empty_pre = this.isEmpty();
		boolean hasFirstValue_pre = empty_pre ? this.hasFirstValue() : false;

		if (this.isEmpty()) {
			if (this.firstValueSet) {
				this.pieces.add(
					this.interpolationFunctionGenerator.
							generateInterpolationFunction(
								this.firstValue, this.firstParameter, x, t));
				this.firstValueSet = false;
				this.firstParameter = 0.0;
				this.firstValue = 0.0;
			} else {
				this.firstParameter = t;
				this.firstValue = x;
				this.firstValueSet = true;
			}
		} else {
			Piece p = this.interpolationFunctionGenerator.
							generateInterpolationExtension(this.pieces, x, t);
			this.pieces.add(p);
		}

		assert	!(empty_pre && !hasFirstValue_pre) ||
									(this.isEmpty() && this.hasFirstValue()) :
				new NeoSim4JavaException("Postcondition violation:"
						+ "!(empty_pre && !hasFirstValue_pre) || "
						+ "(isEmpty() && hasFirstValue())");
		assert	!(empty_pre && hasFirstValue_pre) ||
									(!this.hasFirstValue() && !this.isEmpty()) :
				new NeoSim4JavaException("Postcondition violation:"
						+ "!(empty_pre && hasFirstValue_pre) || "
						+ "(!hasFirstValue() && !isEmpty())");
		assert	this.isEmpty() || this.in(t) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "isEmpty() || in(t)");
	}

	/**
	 * add a new point in the function following the last piece; the function
	 * is not really defined until at least two points have been given.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isConstrainedInterpolation()}
	 * pre	{@code isEmpty() || after(t)}
	 * post	{@code !(isEmpty()_pre && !hasFirstValue()_pre) || hasFirstValue()}
	 * post	{@code !(isEmpty()_pre && hasFirstValue()_pre) || (!hasFirstValue() && !isEmpty()}
	 * post {@code isEmpty() || in(t)}
	 * </pre>
	 *
	 * @param x new value for f(t).
	 * @param d new first order derivative for f(t).
	 * @param t	time coordinate of f.
	 */
	public void			addConstrainedPoint(double x, double d, double t)
	{
		assert	this.isConstrainedInterpolation() :
				new NeoSim4JavaException("Precondition violation: "
											+ "isConstrainedInterpolation()");
		assert	this.isEmpty() || this.after(t) :
				new NeoSim4JavaException("Precondition violation: "
											+ "isEmpty() || after(t)");
		boolean empty_pre = this.isEmpty();
		boolean hasFirstValue_pre = empty_pre ? this.hasFirstValue() : false;

		if (this.isEmpty()) {
			if (this.firstValueSet) {
				this.pieces.add(
					((ConstrainedIncrementalInterpolatorGeneratorI)
										this.interpolationFunctionGenerator).
						generateConstrainedInterpolationFunction(
									this.firstValue, this.firstDerivative,
									this.firstParameter,
									x, d, t));
				this.firstValueSet = false;
				this.firstParameter = 0.0;
				this.firstDerivative = 0.0;
				this.firstValue = 0.0;
			} else {
				this.firstParameter = t;
				this.firstValue = x;
				this.firstDerivative = d;
				this.firstValueSet = true;
			}
		} else {
			Piece p = ((ConstrainedIncrementalInterpolatorGeneratorI)
										this.interpolationFunctionGenerator).
						generateConstrainedInterpolationExtension(
															pieces, x, d, t);
			this.pieces.add(p);
		}

		assert	!(empty_pre && !hasFirstValue_pre) ||
									(this.isEmpty() && this.hasFirstValue()) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "!(empty_pre && !hasFirstValue_pre) || "
						+ "(isEmpty() && hasFirstValue())");
		assert	!(empty_pre && hasFirstValue_pre) ||
									(!this.hasFirstValue() && !this.isEmpty()) :
				new NeoSim4JavaException("Postcondition violation: ");
		assert	this.isEmpty() || this.in(t) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "!(empty_pre && hasFirstValue_pre) || "
						+ "(!hasFirstValue() && !isEmpty())");
	}

	/**
	 * reinitialise the function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code isEmpty() && !hasFirstValue()}
	 * </pre>
	 *
	 */
	public void			reinitialise()
	{
		this.firstParameter = 0.0;
		this.firstDerivative = 0.0;
		this.firstValue = 0.0;
		this.firstValueSet = false;
		this.pieces.clear();
	}

	/**
	 * add a new piece following immediately the last piece in this
	 * interpolated function.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code inter != null && poly != null}
	 * pre	{@code this.pieces.isEmpty() ||inter.followsImmediately(this.pieces.lastElement().interval)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param inter		the interval of definition of the new piece.
	 * @param poly		the polynomial defined over this interval.
	 */
	protected void		add(Interval inter, PolynomialI poly)
	{
		assert	inter != null && poly != null :
				new NeoSim4JavaException("Precondition violation: "
										+ "inter != null && poly != null");
		assert	this.pieces.isEmpty() ||
						inter.followsImmediately(
										this.pieces.lastElement().interval) :
				new NeoSim4JavaException("Precondition violation: "
						+ "pieces.isEmpty() || inter.followsImmediately("
						+ "pieces.lastElement().interval)");

		this.pieces.add(new Piece(inter, poly));
	}

	/**
	 * find the piece in which the parameter {@code t} is; if {@code t} is
	 * before the existing pieces, return the first piece and if {@code t} is
	 * after the existing pieces, return the last piece.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty()}
	 * post	{@code ret != null}
	 * post	{@code !(before(t) || after(t)) || ret.in(t)}
	 * post	{@code !before(t) || ret.before(t)}
	 * post	{@code !after(t) || ret.after(t)}
	 * </pre>
	 *
	 * @param t	parameter which evaluation piece must be found.
	 * @return	the piece in which {@code t} is or the first piece if {@code t} is before all pieces or the last piece if {@code t} is after all pieces.
	 */
	protected Piece		findPiece(double t)
	{
		assert	!this.isEmpty() :
				new NeoSim4JavaException("Precondition violation: !isEmpty()");

		int first = 0;
		int last = this.pieces.size() - 1;
		if (this.pieces.get(0).before(t)) {
			return this.pieces.get(0);
		} else if (this.pieces.get(last).after(t)) {
			return this.pieces.get(last);
		}
		// binary search
		while (last > first) {
			if (this.pieces.get(first).in(t)) {
				return this.pieces.get(first);
			} else if (this.pieces.get(last).in(t)) {
				return this.pieces.get(last);
			} else {
				int probe = (last + first)/2;
				if (this.pieces.get(probe).in(t)) {
					return this.pieces.get(probe);
				} else if (this.pieces.get(probe).after(t)) {
					first = probe + 1;
				} else {
					last = probe - 1;
				}
			}
		}
		assert	first == last && first >= 0 && first < this.pieces.size();

		Piece ret = this.pieces.get(first);
		assert	!(this.before(t) || this.after(t)) || ret.in(t) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "!(before(t) || "
											+ "after(t)) || ret.in(t)");
		assert	!this.before(t) || ret.before(t) :
				new NeoSim4JavaException("Postcondition violation: "
											+ "!before(t) || ret.before(t)");
		assert	!this.after(t) || ret.after(t) :
				new NeoSim4JavaException("Postcondition violation: "
											+ "!after(t) || ret.after(t)");
		return ret;
	}

	/**
	 * compute the value of the function at {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty()}
	 * pre	{@code in(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	the parameter for which the value of the function is computed.
	 * @return	the value of the function at {@code t}, interpolated or extrapolated.
	 */
	public double		value(double t)
	{
		assert	!this.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isEmpty()");

		Piece p = this.findPiece(t);
		return p.value(t);
	}

	/**
	 * compute the first derivative of the function at {@code t}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isEmpty()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	the parameter for which the first derivative of the function is computed.
	 * @return	the first derivative of the function at {@code t}, interpolated or extrapolated.
	 */
	public double		firstDerivative(double t)
	{
		assert	!this.isEmpty() :
				new NeoSim4JavaException("Precondition violation: "
						+ "!isEmpty()");

		return this.findPiece(t).firstDerivative(t);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		if (!this.pieces.isEmpty()) {
			for (int i = 0 ; i < this.pieces.size() ; i++) {
				sb.append(this.pieces.get(i));
				if (i < this.pieces.size() - 1) {
					sb.append(", ");
				}
			}
		} else if (!this.firstValueSet) {
			sb.append("empty");
		} else {
			sb.append("t0 = ");
			sb.append(this.firstParameter);
			sb.append(", x0 = ");
			sb.append(this.firstValue);
			sb.append(", d0 = ");
			sb.append(this.firstDerivative);
		}
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
