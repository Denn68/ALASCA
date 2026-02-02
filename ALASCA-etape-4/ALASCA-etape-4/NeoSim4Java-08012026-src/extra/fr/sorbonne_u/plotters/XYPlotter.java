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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// -----------------------------------------------------------------------------
/**
 * The class <code>XYPlotter</code> implements a simplified version of a
 * plotting window based on the JFreeChart library.
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
 * <p>Created on : 2018-09-04</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 */
public class			XYPlotter
extends		JFrame
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long				serialVersionUID = 1L;
	protected final Map<String,XYSeries>	seriesGroup;
	protected final String					title;
	protected final String					xLabel;
	protected final String					yLabel;
	protected final int						leftX;
	protected final int						topY;
	protected final int						width;
	protected final int 					height;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a new plotter from the given description.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code pd != null}
	 * pre	{@code pd.title != null}
	 * pre	{@code pd.xLabel != null}
	 * pre	{@code pd.yLabel != null}
	 * pre	{@code pd.leftX >= 0}
	 * pre	{@code pd.topY >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param pd		plotter description.
	 */
	public				XYPlotter(PlotterDescription pd)
	{
		this(pd.title, pd.xLabel, pd.yLabel,
			 pd.leftX, pd.topY, pd.width, pd.height);
	}

	/**
	 * create a new plotter from the given parameters.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code title != null}
	 * pre	{@code xLabel != null && yLabel != null}
	 * pre	{@code leftX >= 0 && topY >= 0}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 *
	 * @param title		title.
	 * @param xLabel	label of the x axis.
	 * @param yLabel	label of the y axis.
	 * @param leftX		left w coordinate of the window.
	 * @param topY		top y coordinate of the window.
	 * @param width		width of the window.
	 * @param height	height of the window.
	 */
	public				XYPlotter(
		String title,
		String xLabel,
		String yLabel,
		int leftX,
		int topY,
		int	width,
		int height
		)
	{
		super();

		assert	title != null;
		assert	xLabel != null && yLabel != null;
		assert	leftX >= 0 && topY >= 0;

		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.leftX = leftX;
		this.topY = topY;
		this.width = width;
		this.height = height;

		this.seriesGroup = new HashMap<String,XYSeries>();
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if <code>uri</code> is an existing data series.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	name of a data series.
	 * @return		true if <code>uri</code> is an existing data series.
	 */
	public boolean		isSeries(String uri)
	{
		assert	uri != null;

		return this.seriesGroup.containsKey(uri);
	}

	/**
	 * create an new empty data series with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && !this.isSeries(uri)}
	 * post	{@code isSeries(uri)}
	 * </pre>
	 *
	 * @param uri	name of a new data series.
	 */
	public void			createSeries(String uri)
	{
		assert	uri != null && !this.isSeries(uri);

		this.seriesGroup.put(uri, new XYSeries(uri));

		assert	this.isSeries(uri);
	}

	/**
	 * initialise the plotter.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			initialise()
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (String uri : this.seriesGroup.keySet()) {
			dataset.addSeries(this.seriesGroup.get(uri));
		}
		JFreeChart chart =
				ChartFactory.createXYLineChart(
						this.title,
						this.xLabel,
						this.yLabel,
						dataset,
						PlotOrientation.VERTICAL,
						true,
						true,
						false);
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesStroke(0, new BasicStroke());
		renderer.setSeriesPaint(1, Color.RED);
		renderer.setSeriesStroke(1, new BasicStroke());
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.setTitle(new TextTitle(
							this.title,
							new Font(Font.SERIF, java.awt.Font.BOLD, 18)));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		chartPanel.setBackground(Color.white);
		this.add(chartPanel);

		this.pack();
		this.setTitle(this.title);
		this.setLocation(0, 0);
		this.setBounds(this.leftX, this.topY, this.width, this.height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * show the plotter on the screen.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			showPlotter()
	{
		this.setVisible(true);
	}

	/**
	 * hide the plotter from the screen.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code true}	// no precondition.
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 */
	public void			hidePlotter()
	{
		this.setVisible(false);
	}

	/**
	 * add a new data to an existing series.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code uri != null && this.isSeries(uri)}
	 * post	{@code true}	// no postcondition.
	 * </pre>
	 *
	 * @param uri	name of an existing data series.
	 * @param x		x value.
	 * @param y		y value.
	 */
	public void			addData(String uri, double x, double y)
	{
		assert	uri != null && this.isSeries(uri);

		this.seriesGroup.get(uri).add(x, y);
	}
}
// -----------------------------------------------------------------------------
