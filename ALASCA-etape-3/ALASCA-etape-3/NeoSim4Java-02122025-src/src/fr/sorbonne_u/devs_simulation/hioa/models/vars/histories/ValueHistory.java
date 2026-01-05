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

import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.utils.AssertionChecking;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.TimedValueI;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;
import fr.sorbonne_u.devs_simulation.models.time.Duration;

// -----------------------------------------------------------------------------
/**
 * The class <code>ValueHistory</code> implements a history mechanism for model
 * variables to memorise its most recent values over a predefined time sliding
 * window in a buffer.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The history mechanism is initially defined with a bounded capacity and then
 * values can be pushed into the history managed as a bounded buffer which
 * values can be accessed from the most recent with index 0 to the least
 * recent with index <code>getCurrentSize()</code>. This decreasing time order
 * has been chosen to speed up accesses to the latest values, which are
 * forecasted to be the ones most often accessed in practice.
 * </p>
 * <p>
 * When beginning to use the history object, the number of memorised values
 * will gradually increase with the calls to <code>add</code>. After each add,
 * the history is trimmed to the chosen time window to drop the oldest values.
 * </p>
 * <p>
 * Value histories need not be thread safe, as each <code>ValueHistory</code>
 * instance will be assumed to be owned by one and only one other object (here
 * <code>Value</code> instances). Hence, the thread-safe property is implemented
 * by the owner object, first by confining the reference to its
 * <code>ValueHistory</code> instance inside itself and then by accessing this
 * <code>ValueHistory</code> instance in a thread-safe manner.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code historyWindow != null && historyWindow.greaterThan(Duration.zero(historyWindow.getTimeUnit()))}
 * invariant	{@code isInAscendingOrderOfTime()}}
 * </pre>
 * 
 * <p><strong>Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * 
 * <p>Created on : 2018-09-06</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			ValueHistory<Type>
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>HistoricValue</code> implements a value that can be
	 * recorded in a value history.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * TODO
	 * 
	 * <p><strong>Glass-box Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code t != null}
	 * </pre>
	 * 
	 * <p><strong>Black-box Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
	 * </pre>
	 * 
	 * <p>Created on : 2022-06-23</p>
	 * 
	 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
	 */
	protected static class	HistoricValue<Type>
	implements	TimedValueI
	{
		// ---------------------------------------------------------------------
		// Constants and instance variables
		// ---------------------------------------------------------------------

		/** the value of the given type.									*/
		protected final Type	v;
		/** the time at which {@code v} has been observed/computed.			*/
		protected final Time	t;

		// -------------------------------------------------------------------------
		// Invariants
		// -------------------------------------------------------------------------

		// The invariants are trivially  observed by the fact that all variables
		// are final and the invariant expressions also appears as preconditions
		// on the corresponding parameters in the constructor.

		// ---------------------------------------------------------------------
		// Constructors
		// ---------------------------------------------------------------------

		/**
		 * create a new historical value.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code t != null}
		 * post	{@code getTime().equals(t)}
		 * post	{@code getTimeUnit().equals(t.getTimeUnit())}
		 * </pre>
		 *
		 * @param v	the value of the given type.
		 * @param t	the time at which {@code v} has been observed/computed.
		 */
		public			HistoricValue(Type v, Time t)
		{
			super();

			assert	t != null :
					new NeoSim4JavaException("Precondition violation: "
							+ "t != null");

			this.v = v;
			this.t = t;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.TimedValueI#getTime()
		 */
		@Override
		public Time		getTime()
		{
			return this.t;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.TimedValueI#getTimeUnit()
		 */
		@Override
		public TimeUnit	getTimeUnit()
		{
			return this.t.getTimeUnit();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String	toString()
		{
			StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
			sb.append('(');
			this.content2String(sb);
			sb.append(')');
			return sb.toString();
		}

		/**
		 * add the content to the given string buffer.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code sb != null}	// no precondition.
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param sb	the string buffer to which the content must be added.
		 */
		protected void	content2String(StringBuffer sb)
		{
			sb.append(this.v);
			sb.append(", ");
			sb.append(this.t);
		}
	}

	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** queue holding the history of values; first element is the newest and
	 *  last element the oldest.											*/
	protected final ArrayDeque<HistoricValue<Type>>	history;
	/** time window governing the size of the history.						*/
	protected Duration								historyWindow;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	/**
	 * check if elements in {@code history} appear in strict ascending order of
	 * time.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return			true if all elements in {@code history} appear in strict ascending order of time.
	 */
	protected boolean	isInAscendingOrderOfTime()
	{
		boolean ret = true;
		Iterator<HistoricValue<Type>> iter = this.history.iterator();
		HistoricValue<Type> previous = null;
		HistoricValue<Type> current = null;
		while (ret && iter.hasNext()) {
			if (previous == null) {
				previous = iter.next();
			} else {
				current = iter.next();
				ret &= current.getTime().greaterThan(previous.getTime());
				previous = current;
			}
		}
		return ret;
	}

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
	protected static boolean	implementationInvariants(ValueHistory<?> instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.historyWindow != null &&
						instance.historyWindow.greaterThan(
							Duration.zero(instance.historyWindow.getTimeUnit())),
					ValueHistory.class,
					instance,
					"historyWindow != null && historyWindow.greaterThan("
					+ "Duration.zero(historyWindow.getTimeUnit()))");
		ret &= AssertionChecking.checkImplementationInvariant(
					instance.isInAscendingOrderOfTime(),
					ValueHistory.class,
					instance,
					"isInAscendingOrderOfTime()");
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
	protected static boolean	invariants(ValueHistory<?> instance)
	{
		assert	instance != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "instance != null");

		boolean ret = true;
		return ret;
	}


	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new value history with an infinite time window; beware that an
	 * infinite time window means that an unbounded number of values will be
	 * kept in the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getCurrentSize() == 0}
	 * post	{@code ValueHistory.checkInvariant(this)}
	 * </pre>
	 *
	 */
	public				ValueHistory()
	{
		this(Duration.INFINITY);
	}

	/**
	 * create a new value history with the given time window.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code historyWindow != null}
	 * pre	{@code historyWindow.greaterThan(Duration.zero(historyWindow.getTimeUnit()))}
	 * post	{@code ValueHistory.checkInvariant(this)}
	 * post	{@code getCurrentSize() == 0}
	 * </pre>
	 *
	 * @param historyWindow	time window governing the size of the history.
	 */
	public				ValueHistory(Duration historyWindow)
	{
		super();

		assert	historyWindow != null :
				new NeoSim4JavaException("Precondition violation: "
							+ "historyWindow != null");
		assert	historyWindow.greaterThan(
								Duration.zero(historyWindow.getTimeUnit())) :
				new NeoSim4JavaException("Precondition violation: "
							+ "historyWindow.greaterThan("
							+ "Duration.zero(historyWindow.getTimeUnit()))");

		this.history = new ArrayDeque<>();
		this.historyWindow = historyWindow;

		assert	this.getCurrentSize() == 0 :
				new NeoSim4JavaException("Postcondition violation: "
										+ "getCurrentSize() == 0");

		// Invariants checking
		assert	ValueHistory.implementationInvariants(this) :
				new NeoSim4JavaException(
						"ValueHistory.implementationInvariants(this)");
		assert	ValueHistory.invariants(this) :
				new NeoSim4JavaException("ValueHistory.invariants(this)");
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * return the current number of values in the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code return >= 0}
	 * </pre>
	 *
	 * @return	the current number of values in the history.
	 */
	public int			getCurrentSize()
	{
		return this.history.size();
	}

	/**
	 * return the time of the last value pushed into the history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getCurrentSize() > 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the time of the last value pushed into the history.
	 */
	public Time			getCurrentTime()
	{
		assert	this.getCurrentSize() > 0 :
				new NeoSim4JavaException("Precondition violation: "
												+ "getCurrentSize() > 0");
		return this.history.getFirst().getTime();
	}

	/**
	 * return the time unit used to interpret the time at which values in this
	 * history are associated.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getCurrentSize() > 0}
	 * post	{@code return != null}
	 * </pre>
	 *
	 * @return	the time unit used to interpret the time at which values are associated.
	 */
	public TimeUnit		getTimeUnit()
	{
		assert	this.getCurrentSize() > 0 :
				new NeoSim4JavaException("Precondition violation: "
												+ "getCurrentSize() > 0");
		return this.history.getFirst().getTimeUnit();
	}

	/**
	 * add a value to the history, this value becoming its newest one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code value != null}
	 * pre	{@code getCurrentSize() == 0 || value.getTime().getTimeUnit().equals(getTimeUnit())}
	 * pre	{@code getCurrentSize() == 0 || value.getTime().greaterThan(getCurrentTime())}
	 * post	{@code getCurrentSize() >= 1}
	 * </pre>
	 *
	 * @param value			value to be added to the history.
	 */
	public void			add(Value<Type> value)
	{
		assert	value != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "value != null");
		assert	this.getCurrentSize() == 0 ||
					value.getTime().getTimeUnit().equals(this.getTimeUnit()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentSize() == 0 ||"
						+ "value.getTime().getTimeUnit().equals(getTimeUnit())");
		assert	this.getCurrentSize() == 0 ||
					value.getTime().greaterThan(this.getCurrentTime()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentSize() == 0 || "
						+ "value.getTime().greaterThan(this.getCurrentTime())");

		this.history.addFirst(
					new HistoricValue<Type>(value.getValue(), value.getTime()));
		this.trimToWindow();

		assert	this.getCurrentSize() >= 1 :
				new NeoSim4JavaException("Postcondition violation: getCurrentSize() >= 1");
	}

	/**
	 * reinitialise the value history, clearing all recorded values.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getCurrentSize() == 0}
	 * </pre>
	 *
	 */
	public void			reinitialise()
	{
		this.history.clear();
	}

	/**
	 * delete all values in the history that are too old to be in the history
	 * time window but leave at least one value if there was one nor more before
	 * the trim.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code getCurrentSize()_at_pre == 0 || getCurrentSize() > 0}
	 * </pre>
	 *
	 */
	public void			trimToWindow()
	{
		int size_at_pre = this.getCurrentSize();

		if (this.getCurrentSize() > 0 &&
									this.historyWindow != Duration.INFINITY) {
			Time current = this.getCurrentTime();
			Time oldestKept = current.subtract(this.historyWindow);
			HistoricValue<Type> v = this.history.getLast();
			int size = this.history.size();
			while (size > 1 && v.getTime().lessThan(oldestKept)) {
				this.history.removeLast();
				size--;
				if (size > 1) {
					v = this.history.getLast();
				}
			}
		}

		assert	size_at_pre == 0 || this.getCurrentSize() > 0 :
				new NeoSim4JavaException("Postcondition violation: "
						+ "size_at_pre == 0 || getCurrentSize() > 0");
	}

	/**
	 * return the value at the time {@code t}; if no value corresponds exactly
	 * to {@code t}, this implementation interpolates to the most recent value
	 * in the history which time is lower or equal to {@code t} <i>i.e.</i>, a
	 * simple piecewise constant interpolation/extrapolation is used; if
	 * {@code t} is lower than the time of the oldest value in the history, the
	 * oldest value is returned <i>i.e.</i>, a simple constant extrapolation to
	 * the past is used.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code getCurrentSize() > 0}
	 * pre	{@code t != null && t.getTimeUnit().equals(getTimeUnit())}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param t	time at which the value is required.
	 * @return	the value at the time {@code t}.
	 */
	public Type			evaluateAt(Time t)
	{
		assert	this.getCurrentSize() > 0 :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentSize() > 0");
		assert	t != null && t.getTimeUnit().equals(this.getTimeUnit()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null && t.getTimeUnit().equals(getTimeUnit())");

		Iterator<HistoricValue<Type>> iter = this.history.iterator();
		assert	iter.hasNext();
		HistoricValue<Type> current = iter.next();
		while (t.lessThan(current.getTime()) && iter.hasNext()) {
			current = iter.next();
		}
		return current.v;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		this.content2String(sb);
		sb.append(']');
		return sb.toString();
	}	

	/**
	 * add the content to the given string buffer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code sb != null}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param sb	the string buffer to which the content must be added.
	 */
	protected void		content2String(StringBuffer sb)
	{
		Iterator<HistoricValue<Type>> iter = this.history.iterator();
		HistoricValue<Type> next = null;
		while (iter.hasNext()) {
			next = iter.next();
			sb.append(next.toString());
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
	}
}
// -----------------------------------------------------------------------------
