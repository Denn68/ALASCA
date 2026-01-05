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
 * The class <code>ConstrainedCubicSplineInterpolatorGenerator</code>
 * implements a constrained cubic spline interpolator generator using Kruger's
 * equations to create a constrained order 3 polynomial.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This interpolator generator allows to create piecewise interpolated function
 * with constrained cubic spline. In these interpolations, each interval is
 * defined not only with end point values but also end point first order
 * derivatives. The model is based on Kruger's proposal (reference: CJC Kruger,
 * "Constrained Cubic Spline Interpolation for Chemical Engineering
 * Applications"). The implementation uses Kruger's equations directly.
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
 * <p>Created on : 2021-12-10</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ConstrainedCubicSplineInterpolatorGenerator
implements	ConstrainedIncrementalInterpolatorGeneratorI
{
	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a constrained cubic spline interpolator for the given
	 * interval using Kruger's proposal.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public				ConstrainedCubicSplineInterpolatorGenerator()
	{
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * compute the constrained cubic spline interpolation for the given
	 * interval using Kruger's equations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t0 < t1}
	 * post	{@code ret.assertValueEquals(t0, x0) && ret.assertValueEquals(t1, x1) && ret.assertDerivativeEquals(t0, dx0) && ret.assertDerivativeEquals(t1, dx1)}
	 * </pre>
	 *
	 * @param x0	first point ordinate.
	 * @param dx0	first point order 1 derivative.
	 * @param t0	first point abscissa.
	 * @param x1	first point ordinate.
	 * @param dx1	first point order 1 derivative.
	 * @param t1	first point abscissa.
	 * @return		the constrained cubic spline interpolation for the given interval.
	 */
	protected Polynomial3	computeKruger(
		double x0, double dx0, double t0,
		double x1, double dx1, double t1
		)
	{
		double dt  = t1 - t0;
		double dd0 = -2.0*(dx1 + 2.0*dx0)/dt + 6.0*(x1 - x0)/(dt * dt);
		double dd1 = 2.0*(2.0*dx1 + dx0)/dt - 6.0*(x1 - x0)/(dt*dt);
		double d = (dd1 - dd0)/(6.0*dt);
		double c = (t1*dd0 - t0*dd1)/(2.0*dt);
		double b = ((x1 - x0) - c*(t1*t1 - t0*t0) - d*(t1*t1*t1 - t0*t0*t0))/dt;
		double a = x0 - b*t0 -c*t0*t0 -d*t0*t0*t0;
		Polynomial3 ret = new Polynomial3(a, b, c, d);

		assert	ret.assertValueEquals(t0, x0) && ret.assertValueEquals(t1, x1)
								&& ret.assertDerivativeEquals(t0, dx0)
								&& ret.assertDerivativeEquals(t1, dx1);

		return ret;
	}

	/**
	 * compute an interpolated function f(t) between the two given points.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t1 < t2}
	 * post	{@code ret.in(t1) && ret.in(t2) && ret.assertValueEquals(t1, x1) && ret.assertValueEquals(t2, x2) && ret.assertDerivativeEquals(t1, d1) && ret.assertDerivativeEquals(t2, d2)}
	 * </pre>
	 *
	 * @param x1	function value at {@code t1}.
	 * @param d1	first order derivative at {@code t1}.
	 * @param t1	first function parameter.
	 * @param x2	function value at {@code t2}.
	 * @param d2	first order derivative  at {@code t2}.
	 * @param t2	second function parameter.
	 * @return		a piece interpolating between the two given points.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ConstrainedIncrementalInterpolatorGeneratorI#generateConstrainedInterpolationFunction(double, double, double, double, double, double)
	 */
	@Override
	public Piece		generateConstrainedInterpolationFunction(
		double x1, double d1, double t1,
		double x2, double d2, double t2
		)
	{
		assert	t1 < t2 :
			new NeoSim4JavaException("Precondition violation: t1 < t2");

		ConstrainedInterval i =
				new ConstrainedInterval(t1, x1, d1, t2, x2, d2);
		Polynomial3 p = this.computeKruger(x1, d1, t1, x2, d2, t2);
		Piece ret = new Piece(i, p);

		assert	ret.in(t1) && ret.in(t2)
									&& ret.assertValueEquals(t1, x1)
									&& ret.assertValueEquals(t2, x2)
									&& ret.assertDerivativeEquals(t1, d1)
									&& ret.assertDerivativeEquals(t2, d2);
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
	 * post	{@code ret.in(t) && ret.followsImmediately(pieces.lastElement()) && ret.assertValueEquals(t, x) && ret.assertDerivativeEquals(t, d)}
	 * </pre>
	 *
	 * @param pieces	existing series of pieces representing the interpolated function to be extended.
	 * @param x			new function value at {@code t}.
	 * @param d			first order derivative at {@code t}.
	 * @param t			new time at which {@code x} corresponds.
	 * @return			a new piece that can be added to extend {@code pieces}.
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ConstrainedIncrementalInterpolatorGeneratorI#generateConstrainedInterpolationExtension(java.util.Vector, double, double, double)
	 */
	@Override
	public Piece		generateConstrainedInterpolationExtension(
		Vector<Piece> pieces,
		double x, double d, double t
		)
	{
		assert	pieces != null && pieces.size() > 0 :
				new NeoSim4JavaException("Precondition violation: "
								+ "pieces != null && pieces.size() > 0");
		assert	pieces.lastElement().after(t) :
				new NeoSim4JavaException("Precondition violation: "
								+ "pieces.lastElement().after(t)");
		assert	pieces.lastElement().getInterval().hasFirstDerivative() :
				new NeoSim4JavaException("Precondition violation: "
								+ "pieces.lastElement().getInterval()."
								+ "hasFirstDerivative()");

		ConstrainedInterval i =
			((ConstrainedInterval)pieces.lastElement().getInterval()).
												createNextIntervalUpto(t, x, d);
		double t1 = i.getBegin();
		double x1 = i.getBeginValue();
		double d1 = i.getBeginFirstDerivative();
		Polynomial3 p = this.computeKruger(x1, d1, t1, x, d, t);
		Piece ret = new Piece(i, p);

		assert	ret.in(t) && ret.followsImmediately(pieces.lastElement())
								&& ret.assertValueEquals(t, x)
								&& ret.assertDerivativeEquals(t, d);

		return ret;
	}
}
// -----------------------------------------------------------------------------
