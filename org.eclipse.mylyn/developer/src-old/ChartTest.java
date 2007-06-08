/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 12, 2004
  */
package org.eclipse.mylyn.tests.chart;

import javax.swing.JFrame;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.CategoryDataset;
import org.jfree.data.DatasetUtilities;

public class ChartTest extends JFrame {

    private static final long serialVersionUID = 3256720663090640433L;

    public static void main(final String[] args) {
        final ChartTest demo = new ChartTest("Mylar Test");
        demo.pack();
        demo.setTitle("chart test");
        demo.setVisible(true);
    }
    
    public ChartTest(final String title) {
        final CategoryDataset dataset =  createCategoryDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        super.setContentPane(chartPanel);
    }

    private JFreeChart createChart(final CategoryDataset dataset) {
 
        final JFreeChart chart = ChartFactory.createStackedBarChart(
            "",  // chart title
            "Eclipse Usage",                  // domain axis label
            "Time (minutes)",                     // range axis label
            dataset,                     // data
            PlotOrientation.VERTICAL,    // the plot orientation
            true,                        // legend
            true,                        // tooltips
            false                        // urls
        );
        return chart;
    }
    
    public static CategoryDataset createCategoryDataset() {

        final double[][] data = new double[][]
            {{10.0, 4.0, 15.0, 14.0, 0, 0, 0},
             {5.0, 7.0, 14.0, 3.0, 0, 0, 0},
             {6.0, 17.0, 12.0, 7.0, 0, 0, 0}};

        String[] measurements = { "navigating", "editing", "other" };
        String[] days = { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };
        
        return DatasetUtilities.createCategoryDataset(measurements, days, data);
    }
}
