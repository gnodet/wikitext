/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Meghan Allen - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.usage.MonitorFileRolloverJob;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class UsageFileSelectionWizardPage extends WizardPage {

	private static final String ID_PAGE_FILE_SELECTION = "org.eclipse.mylyn.monitor.usage.fileSelectionPage"; //$NON-NLS-1$

	private final static String PAGE_TITLE = Messages.UsageFileSelectionWizardPage_Select_Archived_File_To_Upload;

	private static final String DESCRIPTION = Messages.UsageFileSelectionWizardPage_Please_Selected_Archived_Usage_Files;

	private Table zippedFilesTable;

	private final UsageSubmissionWizard wizard;

	public static final String SUBMISSION_LOG_FILE_NAME = "submittedUsageLogs.txt"; //$NON-NLS-1$

	protected UsageFileSelectionWizardPage(UsageSubmissionWizard wizard, StudyParameters studyParameters) {
		super(ID_PAGE_FILE_SELECTION, PAGE_TITLE, AbstractUIPlugin.imageDescriptorFromPlugin(
				UiUsageMonitorPlugin.ID_PLUGIN, "icons/wizban/banner-submission.gif")); //$NON-NLS-1$
		setDescription(DESCRIPTION + studyParameters.getStudyName());
		this.wizard = wizard;
	}

	private static List<File> getBackupFiles() {
		ArrayList<File> backupFiles = new ArrayList<File>();
		try {

			String destination = MonitorFileRolloverJob.getZippedMonitorFileDirPath();

			File backupFolder = new File(destination);

			if (backupFolder.exists()) {
				File[] files = backupFolder.listFiles();
				File submissionLogFile = new File(destination, SUBMISSION_LOG_FILE_NAME);

				if (!submissionLogFile.exists()) {
					submissionLogFile.createNewFile();
				}

				FileInputStream inputStream = new FileInputStream(submissionLogFile);

				int bytesRead = 0;
				byte[] buffer = new byte[1000];

				String fileContents = ""; //$NON-NLS-1$

				if (submissionLogFile.exists()) {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						fileContents += new String(buffer, 0, bytesRead);

					}
				}
				for (File file : files) {
					if (file.getName().contains(MonitorFileRolloverJob.BACKUP_FILE_SUFFIX)
							&& !fileContents.contains(file.getName())) {
						backupFiles.add(file);
					}
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return backupFiles;
	}

	public static boolean unsubmittedLogsExist() {
		List<File> backupFiles = getBackupFiles();
		return backupFiles.size() > 0;
	}

	private void addZippedFileView(Composite composite) {
		zippedFilesTable = new Table(composite, SWT.BORDER | SWT.MULTI);
		zippedFilesTable.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				wizard.setBackupFilesToUpload(getZipFilesSelected());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, true).applyTo(zippedFilesTable);

		TableColumn filenameColumn = new TableColumn(zippedFilesTable, SWT.LEFT);
		filenameColumn.setWidth(200);

		List<File> backupFiles = getBackupFiles();

		File[] backupFileArray = backupFiles.toArray(new File[backupFiles.size()]);

		if (backupFileArray != null && backupFileArray.length > 0) {
			Arrays.sort(backupFileArray, new Comparator<File>() {
				public int compare(File file1, File file2) {
					return (new Long((file1).lastModified()).compareTo(new Long((file2).lastModified()))) * -1;
				}

			});
			for (File file : backupFileArray) {
				TableItem item = new TableItem(zippedFilesTable, SWT.NONE);
				item.setData(file.getAbsolutePath());
				item.setText(file.getName());
			}
		}
	}

	public void createControl(Composite parent) {
		try {
			Composite container = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			layout.verticalSpacing = 15;
			container.setLayout(layout);
			addZippedFileView(container);
			setControl(container);
			// setPageComplete(validate());
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.fail(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not create import wizard page", e)); //$NON-NLS-1$
		}
	}

	public List<String> getZipFilesSelected() {

		List<String> list = new ArrayList<String>();
		if (zippedFilesTable.getSelectionCount() >= 1) {
			TableItem[] selectedItems = zippedFilesTable.getSelection();
			for (TableItem selectedItem : selectedItems) {
				list.add(selectedItem.getText());
			}
		} else {
			list.add("<unspecified>"); //$NON-NLS-1$
		}
		return list;
	}

}
