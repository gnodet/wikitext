/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.monitor.core.collection.IUsageCollector;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventSummarySorter;
import org.eclipse.mylyn.internal.monitor.usage.MonitorFileRolloverJob;
import org.eclipse.mylyn.internal.monitor.usage.ReportGenerator;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.internal.monitor.usage.common.UsageCountContentProvider;
import org.eclipse.mylyn.internal.monitor.usage.common.UsageCountLabelProvider;
import org.eclipse.mylyn.internal.monitor.usage.common.UsageCountStudyParamtersFilter;
import org.eclipse.mylyn.internal.monitor.usage.editors.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Shawn Minto
 */
public class UsageDataOverviewPage extends WizardPage {

	private ReportGenerator reportGenerator;

	private Table table;

	private TableViewer tableViewer;

	private final String[] columnNames = new String[] { Messages.UsageSummaryReportEditorPart_Kind,
			Messages.UsageSummaryReportEditorPart_Id, Messages.UsageSummaryReportEditorPart_Count };

	private final StudyParameters studyParameters;

	private final UsageSubmissionWizard wizard;

	public UsageDataOverviewPage(UsageSubmissionWizard wizard, StudyParameters studyParameters) {
		super(Messages.UsageDataOverviewPage_Review_Usage_Data);
		setTitle(Messages.UsageDataOverviewPage_Review_Usage_Data);
		setDescription(NLS.bind(Messages.UsageDataOverviewPage_Please_Review_Data_Submitted_To_X,
				studyParameters.getStudyName()));
		this.studyParameters = studyParameters;
		this.wizard = wizard;
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		GridData wd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		container.setLayoutData(wd);

		createTable(container);
		createTableViewer();
		setControl(container);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			try {
				getContainer().run(true, false, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						displayUsageData(monitor);
					}
				});
			} catch (InvocationTargetException e) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e.getMessage(), e));
			} catch (InterruptedException e) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}
	}

	// TODO Turn this into some kind of common viewer?
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = new Table(parent, style);
		TableLayout tlayout = new TableLayout();
		table.setLayout(tlayout);
		GridData wd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL);
		table.setLayoutData(wd);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(columnNames[0]);
		column.setWidth(60);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.TYPE));

			}
		});

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(columnNames[1]);
		column.setWidth(370);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.NAME));
			}
		});

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(columnNames[2]);
		column.setWidth(50);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.USAGE_COUNT));
			}
		});

	}

	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);

		tableViewer.setContentProvider(new UsageCountContentProvider());
		tableViewer.setLabelProvider(new UsageCountLabelProvider());
		tableViewer.addFilter(new UsageCountStudyParamtersFilter(studyParameters));
		tableViewer.setInput(null);
	}

	private void displayUsageData(IProgressMonitor monitor) {
		List<IUsageCollector> collectors = new ArrayList<IUsageCollector>();
		reportGenerator = new ReportGenerator(UiUsageMonitorPlugin.getDefault().getInteractionLogger(), collectors);
		List<File> files = new ArrayList<File>();
		File monitorFile = UiUsageMonitorPlugin.getDefault().getMonitorLogFile();
		files.add(monitorFile);

		Collection<String> backupFilesToUpload = wizard.getBackupFilesToUpload();
		if (backupFilesToUpload != null && backupFilesToUpload.size() > 0) {
			for (String currFilePath : backupFilesToUpload) {
				File file = new File(MonitorFileRolloverJob.getZippedMonitorFileDirPath(), currFilePath);
				if (file.exists()) {
					List<File> unzippedFiles;
					try {
						unzippedFiles = ZipFileUtil.unzipFiles(file, System.getProperty("java.io.tmpdir"), //$NON-NLS-1$
								new NullProgressMonitor());

						if (unzippedFiles.size() > 0) {
							for (File f : unzippedFiles) {
								files.add(f);
							}
						}
					} catch (IOException e) {
						StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
								"Error unzipping backup monitor log files", e)); //$NON-NLS-1$
					}
				}
			}
		}

		reportGenerator.getStatisticsFromInteractionHistories(files, monitor);

		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				tableViewer.setInput(reportGenerator);
			}
		});
	}
}
