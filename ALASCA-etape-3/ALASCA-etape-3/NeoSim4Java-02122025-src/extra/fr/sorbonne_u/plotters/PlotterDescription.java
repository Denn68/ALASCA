package fr.sorbonne_u.plotters;

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

// -----------------------------------------------------------------------------
/**
 * The class <code>PlotterDescription</code> implements a plotter description
 * for the DEVS simulation framework.
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
 * <p>Created on : 2018-10-22</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			PlotterDescription
implements	Serializable
{
	private static final long	serialVersionUID = 1L;
	public static final String	PLOTTING_PARAM_NAME = "plotting";
	public final String			title;
	public final String			xLabel;
	public final String			yLabel;
	public final int			leftX;
	public final int			topY;
	public final int			width;
	public final int			height;

	public				PlotterDescription(
		String title,
		String xLabel,
		String yLabel,
		int leftX,
		int topY,
		int width,
		int height
		)
	{
		super();

		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.leftX = leftX;
		this.topY = topY;
		this.width = width;
		this.height = height;
	}
}
// -----------------------------------------------------------------------------
