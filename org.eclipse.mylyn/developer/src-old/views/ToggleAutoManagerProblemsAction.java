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
 * Created on Jul 27, 2004
  */
package org.eclipse.mylyn.java.ui.actions;

import org.eclipse.jface.action.Action;

import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.java.ui.views.MylarProblemView;
import org.eclipse.mylyn.ui.MylarImages;

/**
 * @author Mik Kersten
 */
public class ToggleAutoManagerProblemsAction extends Action {
    
    public static final String PREF_ID = "org.eclipse.mylyn.ui.problems.manage.isChecked";
    
    private MylarProblemView problemsView;
    
    public ToggleAutoManagerProblemsAction(MylarProblemView problemsView) {
        super();
        this.problemsView = problemsView;
        setText("DoI list management"); //$NON-NLS-1$
        setImageDescriptor(MylarImages.AUTO_EXPAND);    
        setToolTipText("Degree of interest list management"); 
        
        boolean checked= ContextCorePlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID); 
        valueChanged(checked, false);
    } 
    
    public void run() {
        valueChanged(isChecked(), true);
    }
    
    private void valueChanged(final boolean on, boolean store) {
        setChecked(on);
        if (store) ContextCorePlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
        autoExpand(on);
//        MonitorPlugin.log(this, getText() + " set to: " + on);
    }
    
    public void autoExpand(boolean on) {
        problemsView.setInterestFiltering(on);//  .setFilterUninterestingEnabled(on);
        problemsView.getViewer().refresh(); 
//        problemsView.getViewer().getSorter().sort(problemsView.getViewer(), problemsView.getViewer().getTable().getItems());
//        MylarPackageExplorer.getDefault().setAutoExpandModeEnabled(on);
//        MylarPackageExplorer.getDefault().getTreeViewer().refresh();
//        if (on) MylarPackageExplorer.getDefault().expandAllInteresting();
    }
}
