/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.mylar.internal.monitor.reports.ReportGenerator;
import org.eclipse.mylar.internal.monitor.reports.ui.views.UsageStatsEditorInput;
import org.eclipse.mylar.monitor.reports.IUsageCollector;
import org.eclipse.mylar.monitor.usage.MylarUsageMonitorPlugin;
import org.eclipse.mylar.internal.monitor.reports.collectors.MylarViewUsageCollector;
import org.eclipse.mylar.internal.monitor.reports.collectors.PerspectiveUsageCollector;

/**
 * @author Meghan Allen
 */
public class NewUsageSummaryEditorWizard extends Wizard implements INewWizard {

	private static final String TITLE = "New Usage Summary Report";

	private UsageSummaryEditorWizardPage usageSummaryPage;

	public NewUsageSummaryEditorWizard() {
		super();
		init();
		setWindowTitle(TITLE);
	}

	private void init() {
		usageSummaryPage = new UsageSummaryEditorWizardPage();
	}

	@Override
	public boolean performFinish() {
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page == null)
				return false;

			if (!usageSummaryPage.includePerspective() && !usageSummaryPage.includeViews()) {
				return false;
			}

			List<IUsageCollector> collectors = new ArrayList<IUsageCollector>();

			if (usageSummaryPage.includePerspective()) {
				collectors.add(new PerspectiveUsageCollector());
			}
			if (usageSummaryPage.includeViews()) {
				MylarViewUsageCollector mylarViewUsageCollector = new MylarViewUsageCollector();
				collectors.add(mylarViewUsageCollector);
			}

			ReportGenerator generator = new ReportGenerator(
					MylarUsageMonitorPlugin.getDefault().getInteractionLogger(), collectors);

			List<File> files = new ArrayList<File>();

			File monitorFile = MylarUsageMonitorPlugin.getDefault().getMonitorLogFile();
			files.add(monitorFile);

			IEditorInput input = new UsageStatsEditorInput(files, generator);
			page.openEditor(input, "org.eclipse.mylar.internal.sandbox.usageReport");

		} catch (PartInitException ex) {
			MylarStatusHandler.log(ex, "couldn't open summary editor");
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore

	}

	@Override
	public void addPages() {
		addPage(usageSummaryPage);
	}

}
