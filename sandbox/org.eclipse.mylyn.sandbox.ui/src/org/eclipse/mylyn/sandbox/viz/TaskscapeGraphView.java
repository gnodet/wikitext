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
 * Created on June 3, 2005
  */
package org.eclipse.mylar.sandbox.viz;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.views.MylarContextLabelProvider;
import org.eclipse.mylar.zest.core.viewers.SpringGraphViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;
 
/**
 * @author Mik Kersten
 */
public class TaskscapeGraphView extends ViewPart {
    
    private SpringGraphViewer viewer;
    private Action addNodeAction;
    private Action pauseAction;
    private Action stopAction;

    private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() { 
        public void interestChanged(IMylarElement node) { 
            refresh();
//            refresh();
        }
        
        public void interestChanged(final List<IMylarElement> nodes) {
            refresh();
//            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
//                public void run() {
//                    try { 
//                        if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
//                            viewer.addNode(nodes.get(nodes.size()-1));
//                        }
//                    } catch (Throwable t) {
//                        t.printStackTrace(); // TODO: handle
//                    }
//                }
//            });
        }

        public void contextActivated(IMylarContext taskscape) {
            refresh();
        }

        public void contextDeactivated(IMylarContext taskscape) {
            refresh();
        } 
        
        public void presentationSettingsChanging(UpdateKind kind) {
            refresh();
        }
        
        public void landmarkAdded(IMylarElement element) { 
//            viewer.refresh(element, true);
            refresh();
        }

        public void landmarkRemoved(IMylarElement element) { 
//            viewer.refresh(element, true);
            refresh();
        }

        public void edgesChanged(IMylarElement node) {
            refresh();
        }

        private void refresh() {
            Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    try { 
                        if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
                            viewer.setInput(getViewSite()); // HACK
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(); // TODO: handle
                    }
                }
            });
        }

        public void presentationSettingsChanged(UpdateKind kind) {
            if (kind == IMylarContextListener.UpdateKind.HIGHLIGHTER) viewer.refresh(); 
        }

        public void nodeDeleted(IMylarElement node) {
        }
    };

    public TaskscapeGraphView() {
        MylarPlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
    }
    
    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    @Override
    public void createPartControl(Composite parent) {
        viewer = new SpringGraphViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new TaskscapeGraphContentProvider() );
        viewer.setLabelProvider(new DecoratingLabelProvider(
                new MylarContextLabelProvider(),
                PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
//        viewer.setLabelProvider(new TaskscapeNodeLabelProvider());
//        viewer.setLabelProvider(new SampleGraphLabelProvider());
        //viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        
        makeActions();
        contributeToActionBars();
    }
    
    
    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(addNodeAction);
        manager.add(new Separator());
        manager.add(pauseAction);
        manager.add(stopAction);
    }
    
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(addNodeAction);
        manager.add(pauseAction);
        manager.add(stopAction);
    }


    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class type) {
        // the CommandStack is needed for the delete action
//        if (type == CommandStack.class) {
//            return viewer.getCommandStack();
//        }
        return super.getAdapter(type);
    }
    
    
    /**
     * Creates the "Add Node" action which creates a new node and adds it to the model.
     */
    private void makeActions() {
        addNodeAction = new Action() {
//            private int currentNamesIndex = 0;
            
            /** Create a new node and add a connection from it to a random node. */
            @Override
            public void run() {
//                throw new RuntimeException("unimplemented");
//                if (currentNamesIndex >= TaskscapeGraphContentProvider.NEWNAMES.length) {
//                    System.out.println("Error - out of new names :(");
//                    return;
//                }
//                String newNode = TaskscapeGraphContentProvider.NEWNAMES[currentNamesIndex++];
//                String randomNode = TaskscapeGraphContentProvider.NAMES[(int)(Math.random()* TaskscapeGraphContentProvider.NAMES.length)];
//                 
//                viewer.addNode(newNode);
//                viewer.addRelationship(null, randomNode, newNode);
            }
        };
        
        addNodeAction.setText("Add Node");
        addNodeAction.setToolTipText("Add a new node");
        addNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
            getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
        
        pauseAction = new Action() {
        	
        	@Override
            public void run() {
//                viewer.pauseLayoutAlgorithm();
//                pauseAction.setText(paused ? "Unpause" : "Pause");
            }
        };
        pauseAction.setText("Pause");
        pauseAction.setToolTipText("Pause or unpause the layout algorithm");
        pauseAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK));
        
        stopAction = new Action() {

            @Override
            public void run() {
//            	viewer.stopLayoutAlgorithm();
                stopAction.setEnabled(false);
            }
        };
        stopAction.setText("Stop");
        stopAction.setToolTipText("Stops the layout algorithm");
        stopAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
        
    }
    
}