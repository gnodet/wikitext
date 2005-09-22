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

package org.eclipse.mylar.monitor.reports.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.monitor.reports.MylarReportsPlugin;
import org.eclipse.mylar.monitor.reports.ui.views.UsageStatsEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ViewPluginAction;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Mik Kersten
 */
public class ComputeSummaryAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
    	if (action instanceof ViewPluginAction) {
    		ViewPluginAction objectAction = (ViewPluginAction)action;
    		final List<File> files = new ArrayList<File>();
    		if (objectAction.getSelection() instanceof StructuredSelection) {
    			StructuredSelection structuredSelection = (StructuredSelection)objectAction.getSelection();
        		for (Object object : structuredSelection.toList()) {
					if (object instanceof IFile) {
						IFile file = (IFile)object;
						if (file.getFileExtension().equals("zip")) files.add(new File(file.getLocation().toString()));
					}
				}
        	}
        	
        	if (files.isEmpty()) {
        		files.add(MylarMonitorPlugin.getDefault().getMonitorFile());
        	}
        	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
    			public void run() {
    				try  {
    					IWorkbenchPage page = MylarReportsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
    					if (page == null) return;
    					IEditorInput input = new UsageStatsEditorInput(files, false);
    					page.openEditor(input, MylarReportsPlugin.REPORT_SUMMARY_ID);    					
    				} catch (PartInitException ex) {
    					MylarPlugin.log(ex, "coudln't open summary editor");
    				}
    			}
    		});
        }
	}

	
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
