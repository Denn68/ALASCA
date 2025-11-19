package fr.sorbonne_u.devs_simulation.hioa.models.vars.histories;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;

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
 * The class <code>Interval</code> implements a real value interval used
 * in the definition of piecewise functions.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code begin < end}
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
public class			Interval
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** beginning of the interval.											*/
	protected final double	begin;
	/** beginning value of the interval.									*/
	protected final double	beginValue;
	/** end of the interval.												*/
	protected final double	end;
	/** end value of the interval.											*/
	protected final double	endValue;

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
	 * create an interval from {@code begin} to {@code end} with respective
	 * values {@code beginValue} and {@code endValue}. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code begin < end}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param begin			beginning of the interval.
	 * @param beginValue	beginning value of the interval.
	 * @param end			end of the interval.
	 * @param endValue		end value of the interval.
	 */
	public				Interval(
		double begin, double beginValue,
		double end, double endValue
		)
	{
		super();

		assert	begin < end :
				new NeoSim4JavaException("Precondition violation: "
						+ "begin < end");

		this.begin = begin;
		this.beginValue = beginValue;
		this.end = end;
		this.endValue = endValue;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the piece interval has a fixed first order derivative.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	true if the piece interval has a fixed first order derivative.
	 */
	public boolean		hasFirstDerivative()
	{
		return false;
	}

	/**
	 * @return	the beginning of the interval.
	 */
	/* package */ double	getBegin()
	{
		return begin;
	}

	/**
	 * @return	the value at the beginning of the interval.
	 */
	/* package */ double	getBeginValue()
	{
		return beginValue;
	}

	/**
	 * @return	the end of the interval.
	 */
	/* package */ double	getEnd()
	{
		return end;
	}

	/**
	 * @return	the value at the end of the interval.
	 */
	/* package */ double	getEndValue()
	{
		return endValue;
	}

	/**
	 * create a new interval beginning at the end of this interval and ending
	 * with {@code end}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code after(end)}
	 * pre	{@code !hasFirstDerivative()}
	 * post	{@code ret != null && ret.followsImmediately(this)}
	 * post	{@code !ret.hasFirstDerivative()}
	 * </pre>
	 *
	 * @param end		end point of the next interval to be created.
	 * @param endValue	value at the end of the new interval.
	 * @return			a new interval beginning at the end of this interval and ending with {@code end}.
	 */
	public Interval	createNextIntervalUpto(double end, double endValue)
	{
		assert	!this.hasFirstDerivative() :
				new NeoSim4JavaException("Precondition violation: "
											+ "!hasFirstDerivative()");
		assert	this.after(end) :
				new NeoSim4JavaException("Precondition violation: "
						+ "after(end)");

		return new Interval(this.end, this.endValue, end, endValue);
	}

	/**
	 * return true if {@code t} is in this interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	value to be tested.
	 * @return	true if {@code t} is in this interval.
	 */
	public boolean		in(double t)
	{
		return t >= this.begin && t <= this.end;
	}

	/**
	 * return true if {@code t} comes after this interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	value to be tested.
	 * @return	true if {@code t} comes after this interval.
	 */
	public boolean		after(double t)
	{
		return t > this.end;
	}

	/**
	 * return true if {@code t} comes before this interval.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	value to be tested.
	 * @return	true if {@code t} comes before this interval.
	 */
	public boolean		before(double t)
	{
		return t < this.begin;
	}

	/**
	 * return true if this interval comes immediately after {@code interval}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code interval != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param interval	interval to be tested to know if this interval follows it immediately.
	 * @return			true if this interval comes immediately after {@code interval}.
	 */
	public boolean		followsImmediately(Interval interval)
	{
		assert	interval != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "interval != null");

		return interval.end == this.begin &&
									interval.endValue == this.beginValue;
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
		sb.append(", t2 = ");
		sb.append(this.end);
		sb.append(", x2 = ");
		sb.append(this.endValue);
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
