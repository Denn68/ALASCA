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

import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import fr.sorbonne_u.devs_simulation.exceptions.NeoSim4JavaException;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.DerivableValue;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.Value;

// -----------------------------------------------------------------------------
/**
 * The class <code>PiecewiseInterExtrapolatedValueHistory</code> implements a
 * value history for which an inter/extrapolation function is computed to be
 * used when evaluating the value at some given time.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * The type of interpolation function is defined by an interpolator generator
 * passed to the object through the constructors. Currently, a piecewise linear
 * and a constrained cubic spline interpolator are defined by the library.
 * </p>
 * 
 * <p><strong>Implementation Invariants</strong></p>
 * 
 * <pre>
 * invariant	{@code function != null}
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
public class			PiecewiseInterExtrapolatedValueHistory
extends		ValueHistory<Double>
{
	// -------------------------------------------------------------------------
	// Inner types and classes
	// -------------------------------------------------------------------------

	/**
	 * The class <code>HistoricDerivableValue</code> implements a value that
	 * can be recorded in a value history that also has a first derivative.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Glass-box Invariants</strong></p>
	 * 
	 * <pre>
	 * invariant	{@code true}	// no more invariant
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
	protected static class	HistoricDerivableValue
	extends		HistoricValue<Double>
	{
		protected final	double	firstDerivative;

		/**
		 * create a new derivable historical value.
		 * 
		 * <p><strong>Contract</strong></p>
		 * 
		 * <pre>
		 * pre	{@code t != null}
		 * post	{@code true}	// no postcondition.
		 * </pre>
		 *
		 * @param v					value at time {@code t}.
		 * @param firstDerivative	first derivative at time {@code t}.
		 * @param t					time at which {@code v} and {@code firstDerivative} have been computed/measured.
		 */
		public HistoricDerivableValue(double v, double firstDerivative, Time t)
		{
			super(v, t);
			this.firstDerivative = firstDerivative;
		}

		/**
		 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory.HistoricValue#content2String(java.lang.StringBuffer)
		 */
		@Override
		protected void	content2String(StringBuffer sb)
		{
			super.content2String(sb);
			sb.append(", ");
			sb.append(this.firstDerivative);
		}
	}

	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** interpolated function associated with this value history.	*/
	protected final IncrementallyInterpolatedFunction	function;

	// -------------------------------------------------------------------------
	// Invariants
	// -------------------------------------------------------------------------

	// The invariants are trivially  observed by the fact that all variables
	// are final and the invariant expressions also appears as preconditions
	// on the corresponding parameters in the constructor.

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a piecewise linear value history with the an infinite time window
	 * and an interpolator to be obtained from {@code interpolatorGenerator}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code interpolatorGenerator != null}
	 * post	{@code ValueHistory.checkInvariant(this)}
	 * post	{@code PiecewiseInterExtrapolatedValueHistory.checkInvariant(this)}
	 * post	{@code getCurrentSize() == 0}
	 * </pre>
	 *
	 * @param interpolatorGenerator	interpolated pieces creator defining an order and a type of polynomial.
	 */
	public				PiecewiseInterExtrapolatedValueHistory(
		IncrementalInterpolatorGeneratorI interpolatorGenerator
		)
	{
		super();

		assert	interpolatorGenerator != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "interpolatorGenerator != null");

		this.function =
				new IncrementallyInterpolatedFunction(interpolatorGenerator);

		assert	getCurrentSize() == 0 :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getCurrentSize() == 0");
	}

	/**
	 * create a piecewise linear value history with the given time window and
	 * interpolator.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code historyWindow != null}
	 * pre	{@code historyWindow.greaterThan(Duration.zero(historyWindow.getTimeUnit()))}
	 * pre	{@code interpolatorGenerator != null}
	 * post	{@code getCurrentSize() == 0}
	 * </pre>
	 *
	 * @param historyWindow			time window governing the size of the history.
	 * @param interpolatorGenerator	interpolated pieces creator defining an order and a type of polynomial.
	 */
	public				PiecewiseInterExtrapolatedValueHistory(
		Duration historyWindow,
		IncrementalInterpolatorGeneratorI interpolatorGenerator
		)
	{
		super(historyWindow);

		assert	interpolatorGenerator != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "interpolatorGenerator != null");

		this.function =
				new IncrementallyInterpolatedFunction(interpolatorGenerator);

		assert	getCurrentSize() == 0 :
				new NeoSim4JavaException("Postcondition violation: "
						+ "getCurrentSize() == 0");
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory#add(fr.sorbonne_u.devs_simulation.hioa.models.vars.Value)
	 */
	@Override
	public void			add(Value<Double> value)
	{
		assert	value != null :
				new NeoSim4JavaException("Precondition violation: value != null");
		assert	this.getCurrentSize() == 0 ||
					value.getTime().getTimeUnit().equals(this.getTimeUnit()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentSize() == 0 || "
						+ "value.getTime().getTimeUnit().equals("
						+ "getTimeUnit())");
		assert	this.getCurrentSize() == 0 ||
					value.getTime().greaterThan(this.getCurrentTime()) :
				new NeoSim4JavaException("Precondition violation: "
						+ "getCurrentSize() == 0 || "
						+ "value.getTime().greaterThan(getCurrentTime())");

		if	(value instanceof DerivableValue) {
			assert	this.function.isConstrainedInterpolation();
			DerivableValue<Double> dv = (DerivableValue<Double>) value;
			this.history.addFirst(
					new HistoricDerivableValue(dv.getValue(),
											   dv.getFirstDerivative(),
											   dv.getTime()));
			this.function.addConstrainedPoint(
											dv.getValue(),
											dv.getFirstDerivative(),
											dv.getTime().getSimulatedTime());
		} else {
			assert	!this.function.isConstrainedInterpolation();
			this.history.addFirst(
					new HistoricValue<Double>(value.getValue(),
											  value.getTime()));
			this.function.addPoint(value.getValue(),
								   value.getTime().getSimulatedTime());
		}
		this.trimToWindow();

		assert	this.getCurrentSize() >= 1 :
				new NeoSim4JavaException("Postcondition violation: "
												+ "getCurrentSize() >= 1");

	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory#reinitialise()
	 */
	@Override
	public void			reinitialise()
	{
		this.function.reinitialise();
		super.reinitialise();
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory#trimToWindow()
	 */
	@Override
	public void			trimToWindow()
	{
		super.trimToWindow();
		if (this.historyWindow != Duration.INFINITY) {
			Time current = this.history.getFirst().getTime();
			Time oldestKept = current.subtract(this.historyWindow);
			this.function.trimToTime(oldestKept.getSimulatedTime());
		}
	}

	/**
	 * evaluate the value function at time {@code t} using a linear
	 * interpolation/extrapolation scheme; if the history contains one or
	 * zero value, the method of the superclass is called; if the time {@code t}
	 * is smaller than the oldest time (resp. larger than the largest time) in
	 * the value history, a linear extrapolation from the oldest (resp.
	 * youngest) piece is used, otherwise standard linear interpolation is used.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more preconditions.
	 * post	{@code true}	// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory#evaluateAt(fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public Double		evaluateAt(Time t)
	{
		assert	t != null :
				new NeoSim4JavaException("Precondition violation: "
						+ "t != null");
		assert	this.getCurrentSize() > 0 :
				new NeoSim4JavaException("Precondition violation: "
												+ "getCurrentSize() > 0");

		if (this.function.isEmpty()) {
//			System.out.println("evaluateAt 1 " + t);
			return super.evaluateAt(t);
		} else {
//			System.out.println("evaluateAt 2 " + t);
			return function.value(t.getSimulatedTime());
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistory#content2String(java.lang.StringBuffer)
	 */
	@Override
	protected void		content2String(StringBuffer sb)
	{
		sb.append("Values[");
		super.content2String(sb);
		sb.append("]");
		if (!this.function.isEmpty()) {
			sb.append(", ");
			sb.append(this.function.toString());
		}
	}
}
// -----------------------------------------------------------------------------
