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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.monitor.reports.IUsageCollector;
import org.eclipse.mylar.monitor.reports.MylarReportsPlugin;
import org.eclipse.mylar.monitor.reports.ReportGenerator;
import org.eclipse.mylar.monitor.reports.internal.MylarUsageAnalysisCollector;
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
public class UserAnalysisAction implements IViewActionDelegate {

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
    	if (action instanceof ViewPluginAction) {
    		ViewPluginAction objectAction = (ViewPluginAction)action;
    		final List<File> files = UsageSummaryAction.getStatsFilesFromSelection(objectAction);
        	if (files.isEmpty()) {
        		files.add(MylarMonitorPlugin.getDefault().getMonitorLogFile());
        	}
        	Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
    			public void run() {
    				try  {
    					List<IUsageCollector> collectors = new ArrayList<IUsageCollector>();
    					collectors.add(new MylarUsageAnalysisCollector());
    					ReportGenerator generator = new ReportGenerator(MylarMonitorPlugin.getDefault().getInteractionLogger(), collectors);
    					     					
    					IWorkbenchPage page = MylarReportsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
    					if (page == null) return;
    					IEditorInput input = new UsageStatsEditorInput(files, generator);
    					page.openEditor(input, MylarReportsPlugin.REPORT_USERS_ID);    					
    				} catch (PartInitException ex) {
    					ErrorLogger.log(ex, "couldn't open summary editor");
    				}
    			}
    		});
        }
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
