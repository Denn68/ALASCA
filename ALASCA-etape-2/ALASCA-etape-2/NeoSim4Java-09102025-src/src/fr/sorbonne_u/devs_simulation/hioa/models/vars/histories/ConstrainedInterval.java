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
 * The class <code>ConstrainedInterval</code> implements a real value
 * interval where end points are constrained by known first derivatives,
 * and which is used in the definition of piecewise functions.
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
public class			ConstrainedInterval
extends		Interval
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** beginning first order derivative.									*/
	protected final double	beginFirstDerivative;
	/** end first order derivative.											*/
	protected final double	endFirstDerivative;

	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an interval from {@code begin} to {@code end} with respective
	 * values {@code beginValue} and {@code endValue} and respective first
	 * derivatives at the beginning and at the end of the interval. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code begin < end}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param begin					beginning of the interval.
	 * @param beginValue			beginning value of the interval.
	 * @param beginFirstDerivative	beginning first order derivative.
	 * @param end					end of the interval.
	 * @param endValue				end value of the interval.
	 * @param endFirstDerivative	end first order derivative.
	 */
	public			ConstrainedInterval(
		double begin, double beginValue, double beginFirstDerivative,
		double end, double endValue, double endFirstDerivative
		)
	{
		super(begin, beginValue, end, endValue);
		this.beginFirstDerivative = beginFirstDerivative;
		this.endFirstDerivative = endFirstDerivative;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.Interval#hasFirstDerivative()
	 */
	@Override
	public boolean		hasFirstDerivative()
	{
		return true;
	}

	/**
	 * return the first derivative at the beginning of the interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return	the first derivative at the beginning of the interval.
	 */
	/* package */ double getBeginFirstDerivative()
	{
		return beginFirstDerivative;
	}

	/**
	 * return the first derivative at the end of the interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 * 
	 * @return	the first derivative at the end of the interval.
	 */
	/* package */ double getEndFirstDerivative()
	{
		return endFirstDerivative;
	}

	/**
	 * create a new interval beginning at the end of this interval and ending
	 * with {@code end}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code after(end)}
	 * pre	{@code hasFirstDerivative()}
	 * post	{@code ret != null && ret.followsImmediately(this)}
	 * post	{@code ret.hasFirstDerivative()}
	 * </pre>
	 *
	 * @param end					end point of the next interval to be created.
	 * @param endValue				value at the end of the new interval.
	 * @param endFirstDerivative	first order derivative at the end of the new interval.
	 * @return						a new interval beginning at the end of this interval and ending with {@code end}.
	 */
	public ConstrainedInterval		createNextIntervalUpto(
		double end, double endValue, double endFirstDerivative
		)
	{
		assert	this.hasFirstDerivative() :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasFirstDerivative()");
		assert	this.after(end) :
				new NeoSim4JavaException("Precondition violation: "
						+ "after(end)");

		return new ConstrainedInterval(
						this.end, this.endValue, this.endFirstDerivative,
						end, endValue, endFirstDerivative);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append("[t1 = ");
		sb.append(this.begin);
		sb.append(", x1 = ");
		sb.append(this.beginValue);
		sb.append(", d1 = ");
		sb.append(this.beginFirstDerivative);
		sb.append(", t2 = ");
		sb.append(this.end);
		sb.append(", x2 = ");
		sb.append(this.endValue);
		sb.append(", d2 = ");
		sb.append(this.endFirstDerivative);
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
