package fr.sorbonne_u.alasca.physical_data;

// Copyright Jacques Malenfant, Sorbonne Universite.
// Jacques.Malenfant@lip6.fr
//
// This software is a computer program whose purpose is to provide a
// basic component programming model to program with components
// distributed applications in the Java programming language.
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

import java.time.Instant;
import fr.sorbonne_u.exceptions.ImplementationInvariantException;
import fr.sorbonne_u.exceptions.InvariantException;
import fr.sorbonne_u.exceptions.PreconditionException;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;

// -----------------------------------------------------------------------------
/**
 * The class <code>TimedMeasure</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code data != null}
 * invariant	{@code measurementUnit != null}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2025-11-24</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			TimedMeasure<T>
extends		TimedEntity
implements	MeasureI<T>
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long			serialVersionUID = 1L;
	/** the measured data.													*/
	protected final T					data;
	/** the measurement unit in which {@code data} is expressed.			*/
	protected final MeasurementUnitI	measurementUnit;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	// Measurement with implicit instant of measurement

	/**
	 * create a measure with the given measurement unit at the current instant
	 * under the hardware time reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measurementUnit != null}
	 * post	{@code getTimestamp().equals(Instant.ofEpochMilli(System.currentTimeMillis())}
	 * post	{@code getTimeReference().equals(getStandardTimestamper())}
	 * </pre>
	 *
	 * @param data				the measurement data.
	 * @param measurementUnit	the measurement unit used to expressed {@code data}.
	 */
	public				TimedMeasure(
		T data,
		MeasurementUnitI measurementUnit)
	{
		super();

		assert	measurementUnit != null :
				new PreconditionException("measurementUnit != null");

		this.data = data;
		this.measurementUnit = measurementUnit;
	}

	// Measurement with explicit instant of measurement and hardware clock time
	// reference

	/**
	 * create a measure with the given measurement unit at the given instant
	 * under the current host hardware time reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measurementUnit != null}
	 * pre	{@code timestamp != null}
	 * post	{@code getTimestamp().equals(timestamp)}
	 * post	{@code getTimeReference().equals(getStandardTimestamper())}
	 * </pre>
	 *
	 * @param data				the measurement data.
	 * @param measurementUnit	the measurement unit used to expressed {@code data}.
	 * @param timestamp			time stamp as a Java {@code Instant} object.
	 */
	public				TimedMeasure(
		T data,
		MeasurementUnitI measurementUnit,
		Instant timestamp
		)
	{
		this(data, measurementUnit, timestamp, getStandardTimestamper());
	}

	/**
	 * create a measure with the given measurement unit at the given instant
	 * under the hardware time reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measurementUnit != null}
	 * pre	{@code timestamp != null}
	 * post	{@code getTimestamp().equals(timestamp)}
	 * post	{@code getTimeReference().equals(getStandardTimestamper())}
	 * </pre>
	 *
	 * @param data				the measurement data.
	 * @param measurementUnit	the measurement unit used to expressed {@code data}.
	 * @param timestamp			time stamp as a Java {@code Instant} object.
	 * @param timestamper		identity of the time stamping host <i>e.g.</i>, its IP address.
	 */
	public				TimedMeasure(
		T data,
		MeasurementUnitI measurementUnit,
		Instant timestamp,
		String timestamper
		)
	{
		super(timestamp, timestamper);

		assert	measurementUnit != null :
				new PreconditionException("measurementUnit != null");

		this.data = data;
		this.measurementUnit = measurementUnit;

		// Invariant checking
		assert	TimedMeasure.implementationInvariants(this) :
				new ImplementationInvariantException(
						"TimedMeasure.implementationInvariants(this)");
		assert TimedMeasure.invariants(this) :
				new InvariantException("TimedMeasure.invariants(this)");
	}

	// Measurement with explicit instant of measurement and software clock time
	// reference

	/**
	 * create a measure with the given measurement unit at the current instant
	 * under the software clock {@code ac} time reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measurementUnit != null}
	 * pre	{@code ac != null}
	 * pre	{@code timestamp != null}
	 * post	{@code getTimestamp().equals(timestamp)}
	 * post	{@code getTimestamper().equals(ac.getClockURI())}
	 * </pre>
	 *
	 * @param data				the measurement data.
	 * @param measurementUnit	the measurement unit used to expressed {@code data}.
	 * @param ac				an accelerated clock giving the time reference.
-	 */
	public				TimedMeasure(
		T data,
		MeasurementUnitI measurementUnit,
		AcceleratedClock ac
		)
	{
		this(data, measurementUnit, ac, ac.currentInstant());
	}

	/**
	 * create a measure with the given measurement unit at the given instant
	 * under the software clock {@code ac} time reference.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code measurementUnit != null}
	 * pre	{@code ac != null}
	 * pre	{@code timestamp != null}
	 * post	{@code getTimestamp().equals(timestamp)}
	 * post	{@code getTimestamper().equals(ac.getClockURI())}
	 * </pre>
	 *
	 * @param data				the measurement data.
	 * @param measurementUnit	the measurement unit used to expressed {@code data}.
	 * @param ac				an accelerated clock giving the time reference.
	 * @param timestamp			time stamp as a Java {@code Instant} object.
	 */
	public				TimedMeasure(
		T data,
		MeasurementUnitI measurementUnit,
		AcceleratedClock ac,
		Instant timestamp
		)
	{
		super(ac, timestamp);

		assert	measurementUnit != null :
				new PreconditionException("measurementUnit != null");

		this.data = data;
		this.measurementUnit = measurementUnit;

		// Invariant checking
		assert	TimedMeasure.implementationInvariants(this) :
				new ImplementationInvariantException(
						"TimedMeasure.implementationInvariants(this)");
		assert TimedMeasure.invariants(this) :
				new InvariantException("TimedMeasure.invariants(this)");
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.alasca.physical_data.MeasureI#getData()
	 */
	@Override
	public T			getData()
	{
		return this.data;
	}

	/**
	 * @see fr.sorbonne_u.alasca.physical_data.MeasureI#getMeasurementUnit()
	 */
	@Override
	public MeasurementUnitI	getMeasurementUnit()
	{
		return this.measurementUnit;
	}

	/**
	 * Two timed measures are considered equal if the have the same value and
	 * the same measurement unit, regardless of the time at which they have been
	 * measured.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean		equals(Object obj)
	{
		if (obj == null) {
			return false;
		} else if (!(obj instanceof MeasureI)) {
			return false;
		} else {
			return this.data.equals(((MeasureI)obj).getData()) &&
				   this.measurementUnit.equals(((MeasureI)obj).
						   								getMeasurementUnit());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		this.contentAsString(sb);
		sb.append(']');
		return sb.toString();
	}

	/**
	 * add the local content to be embedded in a larger {@code toString}
	 * process using a {@code StringBuffer}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sb != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sb	a {@code StringBuffer} to which the local content is added.
	 */
	protected void		contentAsString(StringBuffer sb)
	{
		assert	sb != null : new PreconditionException("sb != null");

		StringBuffer local = new StringBuffer();
		local.append(this.data);
		local.append(", ");
		local.append(this.measurementUnit);
		local.append(", ");
		super.contentAsString(local);
		sb.append(local);
	}
}
// -----------------------------------------------------------------------------
