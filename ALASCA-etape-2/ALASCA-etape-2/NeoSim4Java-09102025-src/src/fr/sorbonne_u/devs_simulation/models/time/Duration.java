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
 * The class <code>Duration</code> defines a representation for duration
 * values with their time unit as well as methods to manipulate them.
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
 * invariant	{@code simulatedDuration >= 0.0}
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
 * <p>Created on : 2016-02-15</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Duration
implements	Serializable
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long		serialVersionUID = 1L;

	/** maximum number of digits in the fractional part of the time used
	 *  to format in the method {@code toString}; must be greater than 0.	*/
	public static final int			MAX_FRACTION_DIGITS = 2;
	/** number format (single) instance used to format in the method
	 *  {@code toString}.													*/
	protected static NumberFormat 	NF;

	/** the representation of the duration infinity i.e., the largest
	 *  possible.															*/
	public static final Duration	INFINITY =
										new Duration(Double.POSITIVE_INFINITY,
													 TimeUnit.SECONDS);
	/** tolerance on the precision of floating points d to consider two
	 *  durations as equal.													*/
	public static double			TOLERANCE = 0.000000001;

	/** the value of the duration represented by this object.				*/
	protected double				simulatedDuration;
	/** the time unit of this duration, allowing to unambiguously
	 *  interpret it.														*/
	protected TimeUnit				timeUnit;

	// -------------------------------------------------------------------------
	// Static initialisers
	// -------------------------------------------------------------------------

	static {
		NF = NumberFormat.getInstance(Locale.US);
		NF.setGroupingUsed(false);
		NF.setMaximumFractionDigits(MAX_FRACTION_DIGITS);			
	}

	/**
	 * set the number of digits in the decimal part of duration values to be
	 * used when constructing a string representation of a {@code Duration}
	 * instance.
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
	protected static boolean	implementationInvariants(Duration instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: instance != null");

		boolean ret = true;
//		TODO: verify the binding time of statically initialised fields
//		ret &= InvariantChecking.checkGlassBoxInvariant(
//					NF != null,
//					Duration.class,
//					instance,
//					"NF != null");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.simulatedDuration >= 0.0,
					Duration.class,
					instance,
					"simulatedDuration >= 0.0");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.timeUnit != null,
					Duration.class,
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
	protected static boolean	invariants(Duration instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkInvariant(
					MAX_FRACTION_DIGITS >= 0,
					Duration.class,
					instance,
					"MAX_FRACTION_DIGITS >= 0");
//		TODO: verify the binding time of statically initialised fields
//		ret &= InvariantChecking.checkBlackBoxInvariant(
//					INFINITY != null,
//					Duration.class,
//					instance,
//					"INFINITY != null");
		ret &= AssertionChecking.checkInvariant(
					TOLERANCE >= 0.0,
					Duration.class,
					instance,
					"TOLERANCE >= 0.0");
		return ret;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new duration object with the given duration value and
	 * time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code simulatedDuration >= -Duration.TOLERANCE}
	 * pre	{@code timeUnit != null}
	 * post	{@code Duration.checkInvariant(this)}
	 * </pre>
	 *
	 * @param simulatedDuration	represented duration value.
	 * @param timeUnit			time unit of the represented duration.
	 */
	public				Duration(
		double simulatedDuration,
		TimeUnit timeUnit
		)
	{
		super();

		assert	timeUnit != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "timeUnit != null");
		// replaced by a simple warning to let the simulation continue
//		assert	simulatedDuration >= -Duration.TOLERANCE :
//				new NeoSim4JavaException("Precondition violation: "
//								+ "simulatedDuration >= -Duration.TOLERANCE");

		if (simulatedDuration < -Duration.TOLERANCE) {
			System.out.println("Warning: negative duration "
							   + simulatedDuration + " corrected to 0!");
		}
		if (simulatedDuration < 0.0) {
			simulatedDuration = 0.0;
		}

		this.simulatedDuration = simulatedDuration;
		this.timeUnit = timeUnit;

		assert	Duration.implementationInvariants(this) :
				new NeoSim4JavaException(
						"Duration.implementationInvariants(this)");
		assert	Duration.invariants(this) :
				new NeoSim4JavaException("Duration.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return the duration 0 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 0 in the given time unit.
	 */
	public static Duration	zero(TimeUnit u)
	{
		assert	u != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "u != null");

		return new Duration(0.0, u);
	}

	/**
	 * return the duration 1 in the given time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code u != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param u	the time unit of the new duration object.
	 * @return	the duration 1 in the given time unit.
	 */
	public static Duration	one(TimeUnit u)
	{
		assert	u != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "u != null");

		return new Duration(1.0, u);
	}

	/**
	 * get the simulated duration value.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the simulated duration value.
	 */
	public double		getSimulatedDuration()
	{
		return this.simulatedDuration;
	}

	/**
	 * get this duration object time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	this duration object time unit.
	 */
	public TimeUnit		getTimeUnit()
	{
		return this.timeUnit;
	}

	/**
	 * return true if {@code u} is the time unit of this duration object or if
	 * this duration object is {@code INFINITY}.
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

		return this.simulatedDuration == Double.POSITIVE_INFINITY
					|| this.getTimeUnit().equals(u);
	}

	/**
	 * return true if this duration object and <code>d</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");

		return this.simulatedDuration == Double.POSITIVE_INFINITY
					|| d.simulatedDuration == Double.POSITIVE_INFINITY
							|| this.getTimeUnit().equals(d.getTimeUnit());
	}

	/**
	 * return true if this duration object and <code>t</code> have the
	 * same time unit.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time object.
	 * @return	true if this duration object and <code>d</code> have the same time unit.
	 */
	public boolean		hasSameUnit(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");

		return this.simulatedDuration == Double.POSITIVE_INFINITY
					|| t.simulatedTime == Double.POSITIVE_INFINITY
							|| this.getTimeUnit().equals(t.getTimeUnit());
	}

	/**
	 * create a new duration object with the given simulated value and
	 * the same time unit as this one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d >= 0.0}
	 * post	{@code return != null && return.getSimulatedDuration() == d && return.getTimeUnit().equals(this.getTimeUnit())}
	 * </pre>
	 *
	 * @param d	simulated duration value.
	 * @return	a new duration object with the given simulated value and the same time unit as this one.
	 */
	public Duration		createFromSimulatedDuration(double d)
	{
		assert	d >= 0.0 :
				new NeoSim4JavaException("Precondition violation: d >= 0.0");

		return new Duration(d, this.timeUnit);
	}

	/**
	 * return a copy of this duration object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return != null && return != this && return.equals(this)}
	 * </pre>
	 *
	 * @return	a copy of this duration object.
	 */
	public Duration		copy()
	{
		return new Duration(this.simulatedDuration, this.timeUnit);
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code>.
	 */
	public boolean		equals(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.simulatedDuration == Double.POSITIVE_INFINITY) {
			return d.simulatedDuration == Double.POSITIVE_INFINITY;
		} else if (d.simulatedDuration == Double.POSITIVE_INFINITY) {
			return false;
		} else {
			return Math.abs(this.simulatedDuration - d.simulatedDuration)
										<= Duration.TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents a smaller duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a smaller duration than <code>d</code>.
	 */
	public boolean		lessThan(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Duration.INFINITY)) {
			return false;
		} else if (d.equals(Duration.INFINITY)) {
			// !this.equals(Duration.INFINITY)
			return true;
		} else {
			return this.simulatedDuration < d.simulatedDuration + TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or less.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or less.
	 */
	public boolean		lessThanOrEqual(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Duration.INFINITY)) {
			return d.equals(Duration.INFINITY);
		} else if (d.equals(Duration.INFINITY)) {
			return true;
		} else {
			return this.simulatedDuration <= d.simulatedDuration + TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents a greater duration than
	 * <code>d</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents a greater duration than <code>d</code>.
	 */
	public boolean		greaterThan(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Duration.INFINITY)) {
			return !d.equals(Duration.INFINITY);
		} else if (d.equals(Duration.INFINITY)) {
			// !this.equals(Duration.INFINITY)
			return false;
		} else {
			return this.simulatedDuration > d.simulatedDuration - TOLERANCE;
		}
	}

	/**
	 * return true if this duration object represents the same duration as
	 * <code>d</code> or greater.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration to compare.
	 * @return	true if this duration object represents the same duration as <code>d</code> or greater.
	 */
	public boolean		greaterThanOrEqual(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Duration.INFINITY)) {
			return true;
		} else if (d.equals(Duration.INFINITY)) {
			return this.equals(Duration.INFINITY);
		} else {
			return this.simulatedDuration >= d.simulatedDuration - TOLERANCE;
		}
	}

	/**
	 * add two durations and return a new duration object representing the
	 * result.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code d != null}
	 * pre	{@code hasSameUnit(d)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param d	other duration object.
	 * @return	a new duration object representing the sum.
	 */
	public Duration		add(Duration d)
	{
		assert	d != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "d != null");
		assert	this.hasSameUnit(d) :
				new NeoSim4JavaException("Precondition violation: "
						+ "hasSameUnit(d)");

		if (this.equals(Duration.INFINITY) || d.equals(Duration.INFINITY)) {
			return Duration.INFINITY;
		} else {
			return new Duration(this.simulatedDuration + d.simulatedDuration,
								this.timeUnit);
		}
	}

	/**
	 * subtract two durations and return a new duration object representing the
	 * result; if the duration {@code d} is larger than this duration, the
	 * result is the Duration zero; also an exception is raised if the result is undefined
	 * <i>i.e.</i>, when {@code d} is {@code Duration.INFINITY}.
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
	 * @param d	other duration object.
	 * @return	a new duration object representing the subtraction.
	 */
	public Duration		subtract(Duration d)
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

		if (this.equals(Duration.INFINITY)) {
			return Duration.INFINITY;
		} else {
			double result = this.simulatedDuration - d.simulatedDuration;
			return new Duration(result >= 0.0 ? result : 0.0,
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
		if (this.equals(Duration.INFINITY)) {
			sb.append("INFINITY");
		} else {
			sb.append(NF.format(this.getSimulatedDuration()));
			sb.append(", ");
			sb.append(this.getTimeUnit());
		}
		sb.append(')');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
