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

import java.util.ArrayList;
import java.util.Vector;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

// -----------------------------------------------------------------------------
/**
 * The class <code>LeastSquaresInterpolatorGenerator</code> implements
 * interpolators based on a least squares approach, producing polynomials
 * of orders 1, 2 or 3 and able to manage a limited (in size) window of
 * values.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * This implementation uses the Apache common-math library to compute the
 * least squares polynomials. Hence, the polynomials are recomputed anew each
 * time a new point is added to the function. Therefore, this implementation is
 * relatively inefficient both computationally, as the least squares
 * computation is redone from scratch repetitively, and in terms of memory as
 * it requires to embark the common-math library.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code windowSize >= 2}
 * invariant	{@code obs != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code getOrder() > 0 && getOrder() <= 3}
 * </pre>
 * 
 * <p>Created on : 2022-07-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			LeastSquaresInterpolatorGenerator
implements	IncrementalInterpolatorGeneratorI
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/**	order of the polynomial to be generated.							*/
	protected final int									order;
	/** number of values to be used to compute the polynomial.				*/
	protected final int									windowSize;
	/** array of observations used to compute the polynomial.				*/
	protected final ArrayList<WeightedObservedPoint>	obs;

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
	 * create a least squares interpolator generator of the given order with
	 * an infinite window size of observations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code order > 0 && order <= 3}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param order	order of the polynomial to be used for the interpolation function.
	 */
	public				LeastSquaresInterpolatorGenerator(int order)
	{
		this(order, Integer.MAX_VALUE);

		assert	LeastSquaresInterpolatorGenerator.invariant(this);
	}

	/**
	 * create a least squares interpolator generator of the given order with
	 * the given window size of observations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code order > 0 && order <= 3}
	 * pre	{@code windowSize >= 2}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param order			order of the polynomial to be used for the interpolation function.
	 * @param windowSize	size (number) of the window of values used in the computation.
	 */
	public				LeastSquaresInterpolatorGenerator(
		int order,
		int windowSize
		)
	{
		assert	order > 0 && order <= 3 :
				new NeoSim4JavaException("Precondition violation: "
										+ "order > 0 && order <= 3");
		assert	windowSize >= 2 :
				new NeoSim4JavaException("Precondition violation: "
						+ "windowSize >= 2");

		this.order = order;
		this.windowSize = windowSize;
		this.obs = new ArrayList<>();

		assert	LeastSquaresInterpolatorGenerator.invariant(this);
	}

	/**
	 * return	true if {@code l} observes its invariant.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code l != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param l	interpolator generator to be tested.
	 * @return	true if {@code l} observes its invariant.
	 */
	protected static boolean	invariant(LeastSquaresInterpolatorGenerator l)
	{
		assert	l != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "l != null");

		boolean inv = true;
		inv &= l.getOrder() > 0 && l.getOrder() <= 3;
		inv &= l.windowSize >= 2;
		inv &= l.obs.size() <= l.windowSize;
		return inv;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * return the order of the polynomial to be generated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return > 0 && return <= 3}
	 * </pre>
	 *
	 * @return	the order of the polynomial to be generated.
	 */
	public int			getOrder()
	{
		return this.order;
	}

	/**
	 * create the polynomial from the given array of coefficients.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code coefficients != null}
	 * pre	{@code getOrder() + 1 == coefficients.length}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @param coefficients	coefficients of the polynomial in order of their index.
	 * @return				the polynomial with the given coefficients.
	 */
	protected PolynomialI	createPolynomial(double[] coefficients)
	{
		assert	coefficients != null :
				new NeoSim4JavaException("Precondition violation: "
									+ "coefficients != null");
		assert	this.getOrder() + 1 == coefficients.length :
				new NeoSim4JavaException("Precondition violation: "
									+ "getOrder() + 1 == coefficients.length");

		PolynomialI p = null;
		switch(this.order) {
		case 1: p = new Polynomial1(coefficients[0], coefficients[1]); break;
		case 2: p = new Polynomial2(coefficients[0], coefficients[1],
									coefficients[2]); break;
		case 3: p = new Polynomial3(coefficients[0], coefficients[1],
									coefficients[2], coefficients[3]); break;
		default: throw new RuntimeException("incorrect order: " + this.order);
		}
		return p;
	}

	/**
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

		this.obs.add(new WeightedObservedPoint(1.0, t1, x1));
		this.obs.add(new WeightedObservedPoint(1.0, t2, x2));
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(this.order);
		double[] coeff = fitter.fit(this.obs);
		PolynomialI p = this.createPolynomial(coeff);
		Interval i = new Interval(t1, p.valueOf(t1), t2, p.valueOf(t2));
		Piece ret = new Piece(i, p);
		assert	ret.in(t1) && ret.in(t2);
		return ret;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.IncrementalInterpolatorGeneratorI#generateInterpolationExtension(java.util.Vector, double, double)
	 */
	@Override
	public Piece		generateInterpolationExtension(
		Vector<Piece> pieces,
		double x,
		double t
		)
	{
		Interval i = pieces.get(0).getInterval();
		if (this.obs.size() >= this.windowSize) {
			this.obs.remove(0);
		}
		this.obs.add(new WeightedObservedPoint(1.0, t, x));
		PolynomialCurveFitter fitter = PolynomialCurveFitter.create(this.order);
		double[] coeff = fitter.fit(this.obs);
		PolynomialI p = this.createPolynomial(coeff);
		Interval n = new Interval(i.getBegin(), p.valueOf(i.getBegin()),
								  t, p.valueOf(t));
		Piece ret = new Piece(n, p);
		assert	ret.in(i.getBegin()) && ret.in(t);
		pieces.clear();
		return ret;
	}
}
// -----------------------------------------------------------------------------
