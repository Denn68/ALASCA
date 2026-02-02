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
 * The interface <code>ConstrainedIncrementalInterpolatorGeneratorI</code>
 * defines the methods offered by a generator of constrained interpolation
 * functions for functions defined over points, constrained by their known
 * first derivative, and that can be incrementally extended by progressively
 * adding new points at the end of the current domain.
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
 * <p>Created on : 2022-06-08</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public interface		ConstrainedIncrementalInterpolatorGeneratorI
extends		IncrementalInterpolatorGeneratorI
{
	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.IncrementalInterpolatorGeneratorI#generateInterpolationFunction(double, double, double, double)
	 */
	@Override
	default Piece		generateInterpolationFunction(double x1, double t1, double x2, double t2)
	{
		throw new RuntimeException(
					"Use method generateConstrainedInterpolationFunction "
					+ "for constrained incremental interpolator");
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.IncrementalInterpolatorGeneratorI#generateInterpolationExtension(java.util.Vector, double, double)
	 */
	@Override
	default Piece		generateInterpolationExtension(
		Vector<Piece> pieces, double x, double t
		)
	{
		throw new RuntimeException(
					"Use method generateConstrainedInterpolationExtension "
					+ "for constrained incremental interpolator");
	}

	/**
	 * compute an interpolated function f(t) between the two given points.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t1 < t2}
	 * post	{@code ret == null || ret.in(t1) && ret.in(t2) && ret.assertValueEquals(t1, x1) && ret.assertValueEquals(t2, x2) && ret.assertDerivativeEquals(t1, d1) && ret.assertDerivativeEquals(t2, d2)}
	 * </pre>
	 *
	 * @param x1	function value at {@code t1}.
	 * @param d1	first order derivative at {@code t1}.
	 * @param t1	first function parameter.
	 * @param x2	function value at {@code t2}.
	 * @param d2	first order derivative  at {@code t2}.
	 * @param t2	second function parameter.
	 * @return		a piece interpolating between the two given points.
	 */
	default Piece		generateConstrainedInterpolationFunction(
		double x1,
		double d1,
		double t1,
		double x2,
		double d2,
		double t2
		)
	{
		assert	t1 < t2 :
				new NeoSim4JavaException("Precondition violation: t1 < t2");
		return null;
	};

	/**
	 * add a new point to an existing interpolated function f(t) represented
	 * as a series of pieces.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pieces != null && pieces.size() > 0}
	 * pre	{@code pieces.lastElement().after(t)}
	 * post	{@code ret == null || ret.in(t) && ret.followsImmediately(pieces.lastElement()) && ret.assertValueEquals(t, x) && ret.assertDerivativeEquals(t, d)}
	 * </pre>
	 *
	 * @param pieces	existing series of pieces representing the interpolated function to be extended.
	 * @param x			new function value at {@code t}.
	 * @param d			first order derivative at {@code t}.
	 * @param t			new time at which {@code x} corresponds.
	 * @return			a new piece that can be added to extend {@code pieces}.
	 */
	default Piece		generateConstrainedInterpolationExtension(
		Vector<Piece> pieces,
		double x,
		double d,
		double t
		)
	{
		assert	pieces != null && pieces.size() > 0 :
				new NeoSim4JavaException("Precondition violation: "
									+ "pieces != null && pieces.size() > 0");
		assert	pieces.lastElement().after(t) :
				new NeoSim4JavaException("Precondition violation: "
									+ "pieces.lastElement().after(t)");
		return null;
	};
}
// -----------------------------------------------------------------------------
