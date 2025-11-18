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
 * The class <code>LinearInterpolatorGenerator</code> implements an order 1
 * polynomial interpolator. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This generator creates simple order 1 polynomial to be used to define
 * piecewise linear functions defined over points where only the abcissa and
 * the ordinate are known.
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
 * <p>Created on : 2021-12-09</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			LinearInterpolatorGenerator
implements	IncrementalInterpolatorGeneratorI
{
	/**
	 * return the slope of a linear function passing through the two points.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param x1	second coordinate of (t1, x1).
	 * @param t1	first coordinate of (t1, x1).
	 * @param x2	second coordinate of (t2, x2).
	 * @param t2	first coordinate of (t2, x2).
	 * @return		the slope of a linear function passing through the two points.
	 */
	protected double	computeSlope(
		double x1,
		double t1,
		double x2,
		double t2
		)
	{
		return (x2 - x1)/(t2 - t1);
	}

	/**
	 * return the constant of a linear function passing through the two points.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param x1	second coordinate of (t1, x1).
	 * @param t1	first coordinate of (t1, x1).
	 * @param x2	second coordinate of (t2, x2).
	 * @param t2	first coordinate of (t2, x2).
	 * @return		the constant of a linear function passing through the two points.
	 */
	protected double	computeConstant(
		double x1,
		double t1,
		double x2,
		double t2
		)
	{
		return (t2*x1 - t1*x2)/(t2 - t1);
	}

	/**
	 * compute an interpolated function f(t) between the two given points.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t1 < t2}
	 * post	{@code ret.in(t1) && ret.in(t2) && ret.assertValueEquals(t1, x1) && ret.assertValueEquals(t2, x2)}
	 * </pre>
	 *
	 * @param x1	function value at {@code t1}.
	 * @param t1	first function parameter.
	 * @param x2	function value at {@code t2}.
	 * @param t2	second function parameter.
	 * @return		a piece interpolating between the two given points.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.IncrementalInterpolatorGeneratorI#generateInterpolationFunction(double, double, double, double)
	 */
	@Override
	public Piece		generateInterpolationFunction(
		double x1, double t1,
		double x2, double t2
		)
	{
		assert	t1 < t2 :
				new NeoSim4JavaException("Precondition violation: t1 < t2");

		Interval i = new Interval(t1, x1, t2, x2);
		Polynomial1 p = new Polynomial1(this.computeConstant(x1, t1, x2, t2),
										this.computeSlope(x1, t1, x2, t2));
		Piece ret = new Piece(i, p);

		assert	ret.in(t1) && ret.in(t2)
								&& ret.assertValueEquals(t1, x1)
								&& ret.assertValueEquals(t2, x2);
		return ret;
	}

	/**
	 * add a new point to an existing interpolated function f(t) represented
	 * as a series of pieces.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pieces != null && pieces.size() > 0}
	 * pre	{@code pieces.lastElement().after(t)}
	 * post	{@code ret.in(t) && ret.followsImmediately(pieces.lastElement()) && ret.assertValueEquals(t, x)}
	 * </pre>
	 *
	 * @param pieces	existing series of pieces representing the interpolated function to be extended.
	 * @param x			function value at {@code t}.
	 * @param t			new time at which {@code x} corresponds.
	 * @return			a new piece that can be added to extend {@code pieces}.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.IncrementalInterpolatorGeneratorI#generateInterpolationExtension(java.util.Vector, double, double)
	 */
	@Override
	public Piece		generateInterpolationExtension(
		Vector<Piece> pieces,
		double x,
		double t
		)
	{
		assert	pieces != null && pieces.size() > 0 :
				new NeoSim4JavaException("Precondition violation: "
									+ "pieces != null && pieces.size() > 0");
		assert	pieces.lastElement().after(t) :
				new NeoSim4JavaException("Precondition violation: "
									+ "pieces.lastElement().after(t)");

		Interval i =
			pieces.lastElement().getInterval().createNextIntervalUpto(t, x);
		double x1 = i.getBeginValue();
		double t1 = i.getBegin();
		Polynomial1 p = new Polynomial1(this.computeConstant(x1, t1, x, t),
										this.computeSlope(x1, t1, x, t));
		Piece ret = new Piece(i, p);

		assert	ret.in(t) && ret.followsImmediately(pieces.lastElement())
									&& ret.assertValueEquals(t, x) :
				new NeoSim4JavaException("Postcondition violation: "
						+ "ret.in(t) && "
						+ "ret.followsImmediately(pieces.lastElement()) && "
						+ "ret.assertValueEquals(t, x)");
		return ret;
	}
}
// -----------------------------------------------------------------------------
