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
import org.eclipse.mylyn.java.ui.views.MylarPackageExplorer;
import org.eclipse.mylyn.tasklist.MylarImages;

/**
 * @author Mik Kersten
 */
public class ToggleFilterDeclarationsAction extends Action {
    
    public static final String PREF_ID = "org.eclipse.mylyn.ui.explorer.filter.declarations.isChecked";
    
    private MylarPackageExplorer explorer;
    
    public ToggleFilterDeclarationsAction(MylarPackageExplorer explorer) {
        super();
        this.explorer = explorer;
        setText("Filter declarations"); 
        setImageDescriptor(MylarImages.FILTER_DECLARATIONS);    
        setToolTipText("Filter declarations from tree"); 
        
        boolean checked= ContextCore.getPreferenceStore().getBoolean(PREF_ID); 
        valueChanged(checked, false);
    }
    
    public void run() {
        valueChanged(isChecked(), true);
    }
    
    private void valueChanged(final boolean on, boolean store) {
        setChecked(on);
        if (store) ContextCore.getPreferenceStore().setValue(PREF_ID, on); //$NON-NLS-1$
        filter(on);
//        MonitorPlugin.log(this, getText() + " set to: " + on);
    }
    
    public void filter(boolean on) {
        if (explorer != null) {
            explorer.getDoiFilter().setFilterDeclarationsEnabled(on);
            explorer.getTreeViewer().refresh();
            explorer.getLabelProvider().setFilterDeclarationsEnabled(on);
            
            if (explorer.isAutoExpandModeEnabled()) {
                explorer.expandAllInteresting();
            }
        }
    }
}