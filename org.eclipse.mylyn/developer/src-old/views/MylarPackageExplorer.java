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
 * Created on Jul 22, 2004
  */
package org.eclipse.mylyn.java.ui.views;

import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.Workbench;

import org.eclipse.mylyn.core.ITaskscapeListener;
import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.core.model.ITaskscapeNode;
import org.eclipse.mylyn.java.ui.*;
import org.eclipse.mylyn.java.ui.actions.*;
import org.eclipse.mylyn.tasklist.TaskListPlugin;

/**
 * @author Mik Kersten
 */
public class MylarPackageExplorer extends PackageExplorerPart {
    
    public static MylarPackageExplorer INSTANCE = null;
    private boolean autoExpandModeEnabled = false; 
    private MylarAppearanceAwareLabelProvider provider;
    private ToggleAutoManagerExplorerAction autoExpandAction; 
    private ToggleFilterDeclarationsAction filterDeclarationsAction;
    private ToggleAutoFoldAction autoManageEditorsAction;
    
    private DoiViewerFilter doiFilter = new DoiViewerFilter();
    
	private final ITaskscapeListener MODEL_LISTENER = new ITaskscapeListener() {
	    public void interestChanged(ITaskscapeNode info) {
	        refresh(true);
	    }

	    public void modelUpdated() {
	        refresh(true);
	    }

        public void presentationSettingsChanged(PresentationChangeKind kind) {
            refresh(true);
        }
        
        public void presentationSettingsChanging(PresentationChangeKind kind) {
            refresh(true);
        }
        
        public void landmarkAdded(ITaskscapeNode element) { 
            refresh(false); 
        }

        public void landmarkRemoved(ITaskscapeNode element) { 
            refresh(false);
        } 

        public void relationshipsChanged() {
        }

        public void nodeDeleted(ITaskscapeNode node) {
            refresh(false);
        }
        
        private void refresh(final boolean updateExpansionSate) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                public void run() { 
                    try { 
                        if (MylarPackageExplorer.this.getTreeViewer() != null && !MylarPackageExplorer.this.getTreeViewer().getTree().isDisposed()) { 
                            MylarPackageExplorer.this.getTreeViewer().refresh();    
                            if (autoExpandModeEnabled && updateExpansionSate) 
                                MylarPackageExplorer.this.getTreeViewer().expandAll();
                        }
                    } catch (Throwable t) {
                        ContextCorePlugin.fail(t, "Could not update viewer", false);
                    }    
                }
            });
        }
	};
    
    public MylarPackageExplorer() {
        ContextCorePlugin.getTaskscapeManager().addListener(MODEL_LISTENER);
        INSTANCE = this;
    }
    
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        provider = new MylarAppearanceAwareLabelProvider(getTreeViewer()); 
 
        getTreeViewer().getTree().setBackground(TaskListPlugin.getDefault().getColorMap().BACKGROUND_COLOR);
        getTreeViewer().setLabelProvider(new MylarFontDecoratingJavaLabelProvider(provider, true));
        getTreeViewer().addFilter(doiFilter); 
        
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        autoExpandAction = new ToggleAutoManagerExplorerAction(this);	
        filterDeclarationsAction = new ToggleFilterDeclarationsAction(this);
        autoManageEditorsAction = new ToggleAutoFoldAction();
        if (toolBarManager != null) {	
		    toolBarManager.add(new Separator("StartMylar"));
		    toolBarManager.add(filterDeclarationsAction);
            toolBarManager.add(autoManageEditorsAction);
		    toolBarManager.add(autoExpandAction);
		} 
		autoExpandAction.autoExpand(autoExpandAction.isChecked()); 
        filterDeclarationsAction.filter(filterDeclarationsAction.isChecked());
    }

    public boolean isAutoExpandModeEnabled() {
        return autoExpandModeEnabled;
    }
    
    public void setAutoExpandModeEnabled(boolean autoExpandModeEnabled) {
        this.autoExpandModeEnabled = autoExpandModeEnabled;
        provider.setInterestFilterEnabled(autoExpandModeEnabled);
    }
    
    // TODO: still too slow?
    public void expandAllInteresting() {
        if (autoExpandModeEnabled) getTreeViewer().expandAll();
//        List<ITaskscapeNode> elements = ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().getInteresting();
//        if (elements == null) return;
//        List<IJavaElement> prunedElements = new ArrayList<IJavaElement>();
//        for (ITaskscapeNode node : elements) {
//            if (node.getKind().equals(ITaskscapeNode.Kind.Java)) {
//                IJavaElement curr = JavaCore.create(node.getElementHandle());
//                if (curr != null) {
//                    if (!(curr instanceof IPackageFragment
//                          || curr instanceof IPackageFragmentRoot
//                          || curr instanceof IJavaProject)) {
//                        prunedElements.add(curr);
//                    }
//                }
//            } else {
//            }
//        } 
//        
//        Collections.sort(prunedElements, new Comparator() {
//            public int compare(Object o1, Object o2) {
//                if (o1 instanceof IJavaElement && o2 instanceof IJavaElement) {
//                    IJavaElement e1 = (IJavaElement)o1;
//                    IJavaElement e2 = (IJavaElement)o2;
//                    if (e1 != null && e2 != null) {
//                        return e1.getHandleIdentifier().compareTo(e2.getHandleIdentifier());
//                    } 
//                } 
//                return 0;
//            }
//        });
//        for (Iterator it = prunedElements.iterator(); it.hasNext(); ) {
//            getTreeViewer().expandToLevel(it.next(), 1);
//        }
    }
    
    public MylarAppearanceAwareLabelProvider getLabelProvider() {
        return provider;
    }
    public DoiViewerFilter getDoiFilter() {
        return doiFilter;
    }
}



