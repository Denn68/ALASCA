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

// -----------------------------------------------------------------------------
/**
 * The class <code>Polynomial3</code> implements a polynomial function of
 * order 3 (cubic).
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
 * <p>Created on : 2021-12-07</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			Polynomial3
implements	PolynomialI
{
	// -------------------------------------------------------------------------
	//	Constants and variables
	// -------------------------------------------------------------------------

	/** order 0 coefficient.												*/
	double		c0;
	/** order 1 coefficient.												*/
	double		c1;
	/** order 2 coefficient.												*/
	double		c2;
	/** order 3 coefficient.												*/
	double		c3;

	// -------------------------------------------------------------------------
	//	Constructors
	// -------------------------------------------------------------------------

	/**
	 * create an order 3 (cubic) polynomial with the given coefficients.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param c0	order 0 coefficient.
	 * @param c1	order 1 coefficient.
	 * @param c2	order 2 coefficient.
	 * @param c3	order 3 coefficient.
	 */
	public				Polynomial3(
		double c0,
		double c1,
		double c2,
		double c3
		)
	{
		super();
		this.c0 = c0;
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;
	}

	// -------------------------------------------------------------------------
	//	Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PolynomialI#valueOf(double)
	 */
	@Override
	public double		valueOf(double t)
	{
		return ((c3*t + c2)*t + c1)*t + c0;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.hioa.models.vars.histories.PolynomialI#firstDerivative(double)
	 */
	@Override
	public double		firstDerivative(double t)
	{
		return (3.0*c3*t + 2.0*c2)*t + c1;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String		toString()
	{
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName());
		sb.append('[');
		sb.append(this.c0);
		sb.append(" + ");
		sb.append(this.c1);
		sb.append("*t + ");
		sb.append(this.c2);
		sb.append("*t^2 + ");
		sb.append(this.c3);
		sb.append("*t^3");
		sb.append(']');
		return sb.toString();
	}
}
// -----------------------------------------------------------------------------
