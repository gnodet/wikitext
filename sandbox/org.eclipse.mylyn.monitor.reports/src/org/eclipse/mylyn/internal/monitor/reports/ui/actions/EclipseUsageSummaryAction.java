/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.reports.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.monitor.core.collection.CommandUsageCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.DelegatingUsageCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.IUsageCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.SummaryCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.ViewUsageCollector;
import org.eclipse.mylyn.internal.monitor.reports.MylarReportsPlugin;
import org.eclipse.mylyn.internal.monitor.usage.MylarUsageMonitorPlugin;
import org.eclipse.mylyn.internal.monitor.usage.collectors.PerspectiveUsageCollector;
import org.eclipse.mylyn.internal.monitor.usage.editors.UsageStatsEditorInput;
import org.eclipse.mylyn.monitor.usage.ReportGenerator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ViewPluginAction;

/**
 * @author Mik Kersten
 */
public class EclipseUsageSummaryAction implements IViewActionDelegate {
	ReportGenerator generator = null;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (action instanceof ViewPluginAction) {
			ViewPluginAction objectAction = (ViewPluginAction) action;
			final List<File> files = getStatsFilesFromSelection(objectAction);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {

					List<IUsageCollector> delegates = new ArrayList<IUsageCollector>();
					delegates.add(new ViewUsageCollector());
					delegates.add(new PerspectiveUsageCollector());
					delegates.add(new CommandUsageCollector());
					// delegates.add(new CsvOutputCollector());
					delegates.add(new SummaryCollector());

					DelegatingUsageCollector collector = new DelegatingUsageCollector();
					collector.setReportTitle("Usage Summary");
					collector.setDelegates(delegates);
					generator = new ReportGenerator(MylarUsageMonitorPlugin.getDefault().getInteractionLogger(),
							collector, new JobChangeAdapter() {
								public void done(IJobChangeEvent event) {
									try {
										IWorkbenchPage page = MylarReportsPlugin.getDefault().getWorkbench()
												.getActiveWorkbenchWindow().getActivePage();
										if (page == null)
											return;
										IEditorInput input = new UsageStatsEditorInput(files, generator);
										page.openEditor(input, MylarReportsPlugin.REPORT_SUMMARY_ID);
									} catch (PartInitException ex) {
										MylarStatusHandler.log(ex, "couldn't open summary editor");
									}
								}
							});

				}
			});
		}
	}

	/**
	 * TODO: move
	 */
	public static List<File> getStatsFilesFromSelection(ViewPluginAction objectAction) {
		final List<File> files = new ArrayList<File>();
		if (objectAction.getSelection() instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) objectAction.getSelection();
			for (Object object : structuredSelection.toList()) {
				if (object instanceof IFile) {
					IFile file = (IFile) object;
					if (file.getFileExtension().equals("zip"))
						files.add(new File(file.getLocation().toString()));
				}
			}
		}
		Collections.sort(files); // ensure that they are sorted by date

		if (files.isEmpty()) {
			files.add(MylarUsageMonitorPlugin.getDefault().getMonitorLogFile());
		}
		return files;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}
