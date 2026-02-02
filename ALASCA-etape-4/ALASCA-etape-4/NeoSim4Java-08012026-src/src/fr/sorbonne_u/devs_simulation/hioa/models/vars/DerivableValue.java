package fr.sorbonne_u.devs_simulation.hioa.models.vars;

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

import fr.sorbonne_u.devs_simulation.hioa.models.AtomicHIOA;
import fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.ValueHistoryFactoryI;
import fr.sorbonne_u.devs_simulation.models.time.Duration;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The class <code>DerivableValue</code> augments the superclass
 * <code>Value</code> with the first derivative of the signal is known.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>
 * Derivable values must be of a suitable type, most commonly a real number.
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
 * <p>Created on : 2021-12-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			DerivableValue<Type extends Number>
extends		Value<Type>
{
	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** the first derivative at the given time, if known.					*/
	protected Type		firstDerivative;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a value instance for the given derivable variable and the given
	 * owner with an initial value but without history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner	model owning the variable.
	 */
	public				DerivableValue(AtomicHIOA owner)
	{
		this(owner, null, null);
	}

	/**
	 * create a value instance for the given derivable variable and the given
	 * owner with an initial value computed at the given initial time and with
	 * the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner					model owning the variable.
	 * @param historyWindow			time window governing the size of the history.
	 * @param valueHistoryFactory	factory to create an history of the values.
	 */
	public				DerivableValue(
		AtomicHIOA owner,
		Duration historyWindow,
		ValueHistoryFactoryI<Type> valueHistoryFactory
		)
	{
		super(owner, historyWindow, valueHistoryFactory);
	}

	/**
	 * create a value instance for the given variable and the given owner
	 * and the given variable descriptor with an initial value computed at
	 * the given initial time and with the given history size.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no more precondition.
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param owner					model owning the variable.
	 * @param descriptor			descriptor of the variable.
	 * @param historyWindow			time window governing the size of the history.
	 * @param valueHistoryFactory	factory to create an history of the values.
	 * @throws Exception			<i>to do</i>.
	 */
	public				DerivableValue(
		AtomicHIOA owner,
		VariableDescriptor descriptor,
		Duration historyWindow,
		ValueHistoryFactoryI<Type> valueHistoryFactory
		) throws Exception
	{
		super(owner, descriptor, historyWindow, valueHistoryFactory);
	}

	//--------------------------------------------------------------------------
	// Methods
	//--------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.Value#initialise(java.lang.Object)
	 */
	@Override
	public Value<Type>	initialise(Type v)
	{
		throw new RuntimeException(
				"The method initialise(Type) should not be used for instances"
				+ " of DerivableValue! Use setNewValueAndDerivative"
				+ " instead.");
	}

	/**
	 * initialise the value with {@code v} and the first derivative with
	 * {@code d} at the time used when calling {@code initialiseTime}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code !isInitialised()}
	 * pre	{@code getTime() != null}
	 * post	{@code isInitialised()}
	 * post	{@code this.v.equals(v)}
	 * </pre>
	 *
	 * @param v	initial value to be set.
	 * @param d	initial derivative to be set.
	 * @return	this object to enable an assignment after initialisation.
	 */
	public DerivableValue<Type>	initialise(Type v, Type d)
	{
		this.valueLock.writeLock().lock();
		try {
			assert	!this.initialised :
					new NeoSim4JavaException("Precondition violation: "
													+ "!isInitialised()");
			assert	this.time != null :
					new NeoSim4JavaException("Precondition violation: "
													+ "getTime() != null");

			this.v = v;
			this.firstDerivative = d;
			this.initialised = true;
			if (this.hasValueHistory()) {
				this.valueHistory.add(this);
				this.valueHistory.trimToWindow();
			}
			return this;
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * get the first derivative.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code isInitialised()}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @return	the first derivative.
	 */
	public Type			getFirstDerivative()
	{
		this.valueLock.readLock().lock();
		try {
			assert	this.initialised :
					new NeoSim4JavaException("Precondition violation: "
							+ "isInitialised()");

			return this.firstDerivative;
		} finally {
			this.valueLock.readLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.Value#setNewValue(java.lang.Object, fr.sorbonne_u.devs_simulation.models.time.Time)
	 */
	@Override
	public void			setNewValue(Type v, Time t)
	{
		throw new RuntimeException(
					"The method setNewValue(Type,Time)) should not be used for"
					+ " instances of DerivableValue!"
					+ " Use setNewValue(Type,Type,Time) instead.");
	}

	/**
	 * set a new value and derivative for this {@code DerivableValue} object;
	 * safer but less efficient to use than modifying the variables {@code v}
	 * and {@code time} directly; mandatory when using a value history.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code t != null && t.getTimeUnit().equals(timeUnit)}
	 * pre	{@code t.greaterThanOrEqual(time)}
	 * post	{@code true}	// no more postcondition.
	 * </pre>
	 *
	 * @param v	new value to be set.
	 * @param d	new first derivative to be set.
	 * @param t	time at which the new value corresponds.
	 */
	 public void		setNewValue(Type v, Type d, Time t)
	 {
		 this.valueLock.writeLock().lock();
		 try {
			 this.firstDerivative = d;
			 // do the call to super after setting the first derivative so
			 // that if a value history is active, the new value will be
			 // properly pushed into it
			 super.setNewValue(v, t);
		} finally {
			this.valueLock.writeLock().unlock();
		}
	 }

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.Value#reinitialise()
	 */
	@Override
	public void			reinitialise()
	{
		this.valueLock.writeLock().lock();
		try {
			this.firstDerivative = null;
			super.reinitialise();
		} finally {
			this.valueLock.writeLock().unlock();
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.Value#content2String(java.lang.StringBuffer)
	 */
	@Override
	protected void		content2String(StringBuffer sb)
	{
		this.valueLock.readLock().lock();
		try {
			super.content2String(sb);
			sb.append(", ");
			sb.append(this.firstDerivative);
		} finally {
			this.valueLock.readLock().unlock();
		}
	}
}
// -----------------------------------------------------------------------------
