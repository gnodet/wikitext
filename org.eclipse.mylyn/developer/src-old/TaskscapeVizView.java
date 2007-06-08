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
 * Created on Feb 4, 2005
  */
package org.eclipse.mylyn.sandbox.springviz;

import java.awt.*;
import java.awt.event.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.berkeley.guir.prefuse.*;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.render.*;
import edu.berkeley.guir.prefusex.controls.*;
import edu.berkeley.guir.prefusex.force.*;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;

/**
 * @author Mik Kersten
 */
public class TaskscapeVizView extends ViewPart {

    public TaskscapeVizView() {
        super();
    }

    public void createPartControl(Composite parent) {
        try {
            System.setProperty("sun.awt.noerasebackground", "true");
        } catch (NoSuchMethodError error) { 
        }
        Composite stuff = new Composite(parent, SWT.EMBEDDED);
        java.awt.Frame frame = SWT_AWT.new_Frame(stuff);
//        final java.awt.Label statusLabel = new java.awt.Label();
//        statusFrame.add(statusLabel);
        
//        String file = ("C:/Dev/mylar-workspace/prefuse/etc/friendster.xml");
        Graph g = GraphLib.getGrid(5,5);
        
        System.out.println("Visualizing Graph: "
            +g.getNodeCount()+" nodes, "+g.getEdgeCount()+" edges");
        
        ForceSimulator fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-0.4f, -1f, 0.9f));
        fsim.addForce(new SpringForce(4E-5f, 75f));
        fsim.addForce(new DragForce(-0.005f));
        
        ForceDemo fdemo = new ForceDemo(g, fsim);
        fdemo.runDemo(frame);
    }

    public void setFocus() {
    }

}

class ForceDemo extends Display {
    private static final long serialVersionUID = 1L;

    private ForcePanel fpanel;
    
    private ForceSimulator m_fsim;
    private String         m_textField;
    private ItemRegistry   m_registry;
    private Activity       m_actionList;
    
    private Font frameCountFont = new Font("SansSerif", Font.PLAIN, 14);
    
    public ForceDemo(Graph g, ForceSimulator fsim) {
        this(g, fsim, "label");
    } //
    
    public ForceDemo(Graph g, ForceSimulator fsim, String textField) {
        // set up component first
        m_fsim = fsim;
        m_textField = textField;
        m_registry = new ItemRegistry(g);
        this.setItemRegistry(m_registry);
        initRenderers();
        m_actionList = initActionList();
        setSize(700,700);
        pan(350,350);
        this.addControlListener(new NeighborHighlightControl());
        this.addControlListener(new DragControl(false, true));
        this.addControlListener(new FocusControl(0));
        this.addControlListener(new PanControl(false));
        this.addControlListener(new ZoomControl(false));
    } //
    
    public void runDemo(final java.awt.Frame frame) {
        // now set up application window
        fpanel = new ForcePanel(m_fsim) {        
            private static final long serialVersionUID = 3617009741533296438L;

            public void update(java.awt.Graphics g) {
                /* Do not erase the background */
                paint(g);
            }
        };
        
//        frame = new Frame("Force Simulator Demo");
//        Container c = frame.getContentPane();
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.add(fpanel, BorderLayout.EAST);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Dimension d = frame.getSize();
                Dimension p = fpanel.getSize();
                Insets in = frame.getInsets();
                ForceDemo.this.setSize(d.width-in.left-in.right-p.width,
                        d.height-in.top-in.bottom);
            } //
            
        });
        frame.pack();
        frame.setVisible(true);
        
        // start force simulation
        m_actionList.runNow();
    } //
    
    private void initRenderers() {
        TextItemRenderer    nodeRenderer = new TextItemRenderer();
        nodeRenderer.setRenderType(TextItemRenderer.RENDER_TYPE_FILL);
        nodeRenderer.setRoundedCorner(8,8);
        nodeRenderer.setTextAttributeName(m_textField);
        DefaultNodeRenderer nRenderer = new DefaultNodeRenderer();
        DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer();    
        m_registry.setRendererFactory(new DefaultRendererFactory(
                nodeRenderer, edgeRenderer, null));
    } //
    
    private ActionList initActionList() {
        ActionList actionList = new ActionList(m_registry,-1,20);
        actionList.add(new GraphFilter());
        actionList.add(new ForceDirectedLayout(m_fsim, false, false));
        actionList.add(new DemoColorFunction());
        actionList.add(new RepaintAction());
        return actionList;
    } //

    public class DemoColorFunction extends ColorFunction {
        private Color pastelRed = new Color(255,125,125);
        private Color pastelOrange = new Color(255,200,125);
        private Color lightGray = new Color(220,220,255);
        public Paint getColor(VisualItem item) {
            if ( item instanceof EdgeItem ) {
                if ( item.isHighlighted() )
                    return pastelOrange;
                else
                    return Color.LIGHT_GRAY;
            } else {
                return Color.BLACK;
            }
        } //
        public Paint getFillColor(VisualItem item) {
            if ( item.isHighlighted() )
                return pastelOrange;
            else if ( item instanceof NodeItem ) {
                if ( item.isFocus() )
                    return pastelRed;
                else
                    return lightGray;
            } else {
                return Color.BLACK;
            }
        } //        
    } //
    
} // end of class ForceDemo