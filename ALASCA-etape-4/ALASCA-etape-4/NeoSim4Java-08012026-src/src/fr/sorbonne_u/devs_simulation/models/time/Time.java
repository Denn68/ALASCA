package fr.sorbonne_u.devs_simulation.models.time;

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

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

// -----------------------------------------------------------------------------
/**
 * The class <code>Time</code> defines a representation for time values with
 * their time unit as well as methods to manipulate them.
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: factor out common code between Time and Duration into an AbstractTime
 * abstract class.
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code NF != null}
 * invariant	{@code simulatedTime >= 0.0}
 * invariant	{@code timeUnit != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code MAX_FRACTION_DIGITS >= 0}
 * invariant	{@code INFINITY != null}
 * invariant	{@code TOLERANCE >= 0.0}
 * </pre>
 * 
 * <p>Created on : 2016-02-13</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Time
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** maximum number of digits in the fractional part of the time used
	 *  to format in the method {@code toString}.							*/
	public static final int			MAX_FRACTION_DIGITS = 2;
	/** number format (single) instance used to format in the method
	 *  {@code toString}.													*/
	protected static NumberFormat 	NF;

	/** the representation of the time infinity i.e., the largest
	 *  possible.															*/
	public static final Time		INFINITY =
										new Time(Double.POSITIVE_INFINITY,
												 TimeUnit.SECONDS);
	/** tolerance on the precision of floating points t to consider two
	 *  times as equal.														*/
	public static  double			TOLERANCE = 0.000000001;

	/**  the value of the time represented by this object.					*/
	protected double				simulatedTime;
	/** the time unit of this time, allowing to unambiguously interpret it.	*/
	protected TimeUnit				timeUnit;

	// -------------------------------------------------------------------------
	// Static initialisers
	// -------------------------------------------------------------------------

	static {
		assert	MAX_FRACTION_DIGITS > 0;
		NF = NumberFormat.getInstance(Locale.US);
		NF.setGroupingUsed(false);
		NF.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
	}

	/**
	 * set the number of digits in the decimal part of time values to be used
	 * when constructing a string representation of a {@code Time} instance.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code digits > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param digits	number of digits in the decimal part.
	 */
	public static void	setPrintPrecision(int digits)
	{
		assert	digits > 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "digits > 0");

		NF = NumberFormat.getInstance(Locale.US);
		NF.setGroupingUsed(false);
		NF.setMaximumFractionDigits(digits);
	}

	/**
	 * set the tolerance used in the comparisons among times and durations.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code tolerance >= 0.0}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param tolerance	tolerance to be used in the comparisons among times and durations.
	 */
	public static void	setTolerance(double tolerance)
	{
		assert	tolerance >= 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "tolerance >= 0.0");

		TOLERANCE = tolerance;
	}

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * return true if the implementation invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the implementation invariants are observed, false otherwise.
	 */
	protected static boolean	implementationInvariants(Time instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
//		TODO: verify the binding time of statically initialised fields
//		ret &= InvariantChecking.checkGlassBoxInvariant(
//					NF != null,
//					Time.class,
//					instance,
//					"NF != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.simulatedTime >= 0.0,
					Time.class,
					instance,
					"simulatedDuration >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.timeUnit != null,
					Time.class,
					instance,
					"timeUnit != null");
		return ret;
	}

	/**
	 * return true if the invariants are observed, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code instance != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param instance	instance to be tested.
	 * @return			true if the invariants are observed, false otherwise.
	 */
	protected static boolean	invariants(Time instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					MAX_FRACTION_DIGITS >= 0,
					Time.class,
					instance,
					"MAX_FRACTION_DIGITS >= 0");
//		TODO: verify the binding time of statically initialised fields
//		ret &= InvariantChecking.checkBlackBoxInvariant(
//					INFINITY != null,
//					Time.class,
//					instance,
//					"INFINITY != null");
		ret &= AssertionChecking.checkInvariant(
					TOLERANCE >= 0.0,
					Time.class,
					instance,
					"TOLERANCE >= 0.0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new time object with the given time value and time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedTime >= 0.0}
	 * pre	{@code timeUnit != null}
	 * post	{@code Time.checkInvariant(this)}
	 * </pre>
	 *
	 * @param simulatedTime	represented time value.
	 * @param timeUnit		time unit of the represented time.
	 */
	public				Time(
		double simulatedTime,
		TimeUnit timeUnit
		)
	{
		super();

		assert	simulatedTime >= 0.0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "simulatedTime >= 0.0");
		assert	timeUnit != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "timeUnit != null");

		this.simulatedTime = simulatedTime;
		this.timeUnit = timeUnit;

		assert	Time.implementationInvariants(this) :
				new NeoSim4JavaException("Time.implementationInvariants(this)");
		assert	Time.invariants(this) :
				new NeoSim4JavaException("Time.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the time 0 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new time object.
	 * @return	the time 0 in the given time unit.
	 */
	public static Time	zero(TimeUnit u)
	{
		assert	u != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "u != null");

		return new Time(0.0, u);
	}

	/**
	 * get the represented simulated time value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the represented simulated time value.
	 */
	public double		getSimulatedTime()
	{
		return this.simulatedTime;
	}

	/**
	 * get the represented simulated time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the represented simulated time unit.
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit;
	}

	/**
	 * return true if {@code u} is the time unit of this time object or if
	 * this time object is {@code INFINITY}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	a time unit.
	 * @return	true if this time object and <code>t</code> have the same time unit.
	 */
	public boolean		hasTimeUnit(TimeUnit u)
	{
		assert	u != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "u != null");

		return this.simulatedTime == Double.POSITIVE_INFINITY
					|| this.getTimeUnit().equals(u);
	}

	/**
	 * return true if this time object and <code>t</code> have the same
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object and <code>t</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");

		return this.simulatedTime == Double.POSITIVE_INFINITY
					|| t.simulatedTime == Double.POSITIVE_INFINITY
							|| this.getTimeUnit().equals(t.getTimeUnit());
	}

	/**
	 * return true if this time object and <code>d</code> have the same
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	duration object.
	 * @return	true if this time object and <code>t</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");

		return this.simulatedTime == Double.POSITIVE_INFINITY
					|| d.simulatedDuration == Double.POSITIVE_INFINITY
							|| this.getTimeUnit().equals(d.getTimeUnit());
	}

	/**
	 * create a new time object with the given simulated time value and
	 * the same time unit as this one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	the time value of the new object.
	 * @return	a new time object with the time value <code>t</code> and the time unit of this object.
	 */
	public Time			createFromSimulatedTime(double t)
	{
		return new Time(t, this.timeUnit);
	}

	/**
	 * create a new duration object with the given simulated duration value
	 * and the same time unit as this time object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	the duration value of the new object.
	 * @return	a new duration object with the value <code>d</code> and the time unit of this object.
	 */
	public Duration		createFromSimulatedDuration(double d)
	{
		return new Duration(d, this.timeUnit);
	}

	/**
	 * return a copy of this time object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	a copy of this time object.
	 */
	public Time			copy()
	{
		return new Time(this.simulatedTime, this.timeUnit);
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object represents the same time as <code>t</code>.
	 */
	public boolean		equals(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");

		if (this.simulatedTime == Double.POSITIVE_INFINITY) {
			return t.simulatedTime == Double.POSITIVE_INFINITY;
		} else if (t.simulatedTime == Double.POSITIVE_INFINITY) {
			return false;
		} else {
			return Math.abs(this.simulatedTime - t.simulatedTime) < TOLERANCE;
		}
	}

	/**
	 * return true if this time object represents a time preceding
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object represents a time preceding <code>t</code>.
	 */
	public boolean		lessThan(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");

		if (this.equals(Time.INFINITY)) {
			return false;
		} else if (t.equals(Time.INFINITY)) {
			// !this.equals(Time.INFINITY)
			return true;
		} else {
			return this.getSimulatedTime() < t.getSimulatedTime() + TOLERANCE;
		}
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code> or precedes it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object represents the same time as <code>t</code> or precedes it.
	 */
	public boolean		lessThanOrEqual(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");

		if (this.equals(Time.INFINITY)) {
			return t.equals(Time.INFINITY);
		} else if (t.equals(Time.INFINITY)) {
			return true;
		} else {
			return this.getSimulatedTime() <= t.getSimulatedTime() + TOLERANCE;
		}
	}

	/**
	 * return true if this time object represents a time following
	 * <code>t</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object represents a time following <code>t</code>.
	 */
	public boolean		greaterThan(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");

		if (this.equals(Time.INFINITY)) {
			return t.equals(Time.INFINITY);
		} else if (t.equals(Time.INFINITY)) {
			return false;
		} else {
			return this.getSimulatedTime() > t.getSimulatedTime() - TOLERANCE;
		}
	}

	/**
	 * return true if this time object represents the same time as
	 * <code>t</code> or following it.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	other time object.
	 * @return	true if this time object represents the same time as <code>t</code> or following it.
	 */
	public boolean		greaterThanOrEqual(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");

		if (this.equals(Time.INFINITY)) {
			return true;
		} else if (t.equals(Time.INFINITY)) {
			// !this.equals(Time.INFINITY)
			return false;
		} else {
			return this.getSimulatedTime() >= t.getSimulatedTime() - TOLERANCE;
		}
	}

	/**
	 * add a duration to this time returning the new time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameTimeUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	duration to be added.
	 * @return	the new time adding <code>d</code> to this time.
	 */
	public Time			add(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Time.INFINITY)) {
			return Time.INFINITY;
		} else if (d.equals(Duration.INFINITY)) {
			return Time.INFINITY;
		} else {
			return  this.createFromSimulatedTime(
						this.simulatedTime + d.getSimulatedDuration());
		}
	}

	/**
	 * subtract a time from this time returning the duration between the two;
	 * if the time {@code t} is larger than this time, the result is the
	 * {@code Duration} zero; an exception is raised if the result is undefined
	 * <i>i.e.</i>, when the two times are {@code Time.INFINITY} or when only
	 * {@code t} is {@code Time.INFINITY}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * pre	{@code hasSameTimeUnit(t)}
	 * pre	{@code !t.equals(Time.INFINITY)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time to be subtracted.
	 * @return	the duration between <code>t</code> and this time.
	 */
	public Duration		subtract(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.hasSameUnit(t) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(t)");
		assert	!t.equals(Time.INFINITY) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!t.equals(Time.INFINITY)");

		if (this.equals(Time.INFINITY)) {
			return Duration.INFINITY;
		} else {
			double result = this.getSimulatedTime() - t.getSimulatedTime();
			return new Duration(
						result >= 0.0 ? result : 0.0,
						this.timeUnit);
		}
	}

	/**
	 * subtract a duration from this time returning the resulting tile in
	 * the past; if the duration is larger than the time, the result is the
	 * {@code Time} zero; also an exception is raised if the result is
	 * undefined <i>i.e.</i>, when {@code d} is {@code Duration.INFINITY}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * pre	{@code !d.equals(Duration.INFINITY)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	a duration to be subtracted.
	 * @return	the time resulting from the subtraction of the duration from this time.
	 */
	public Time			subtract(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");
		assert	!d.equals(Duration.INFINITY) :
				new NeoSim4JavaException("Precondition violation: "
						+ "!d.equals(Duration.INFINITY)");

		if (this.equals(Time.INFINITY)) {
			// !d.equals(Duration.INFINITY)
			return Time.INFINITY;
		} else {
			double result = this.getSimulatedTime() - d.getSimulatedDuration();
			return new Time(
						result >= 0.0 ? result : 0.0,
						this.timeUnit);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('(');
		if (this == Time.INFINITY) {
			sb.append("INFINITY");
		} else {
			sb.append(NF.format(this.getSimulatedTime()));
			sb.append(", ");
			sb.append(this.getTimeUnit());
		}
		sb.append(')');
		return  sb.toString();
	}
}
// -----------------------------------------------------------------------------
