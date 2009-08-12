/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylyn.internal.monitor.usage.MonitorFileRolloverJob;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.usage.AbstractStudyBackgroundPage;
import org.eclipse.mylyn.monitor.usage.AbstractStudyQuestionnairePage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A wizard for uploading the Mylyn statistics to a website
 * 
 * @author Shawn Minto
 */
public class UsageSubmissionWizard extends Wizard implements INewWizard {

	public static final String LOG = "log";

	public static final String STATS = "usage";

	private static final String ORG_ECLIPSE_PREFIX = "org.eclipse.";

	private boolean displayBackgroundPage = false;

	private boolean displayFileSelectionPage = false;

	/** The id of the user */
	private int uid;

	private final File monitorFile = UiUsageMonitorPlugin.getDefault().getMonitorLogFile();

	private static int processedFileCount = 1;

	private UsageUploadWizardPage uploadPage;

	private UsageFileSelectionWizardPage fileSelectionPage;

	// private GetNewUserIdPage getUidPage;

	private AbstractStudyQuestionnairePage questionnairePage;

	private AbstractStudyBackgroundPage backgroundPage;

	private boolean performUpload = true;

	private List<String> backupFilesToUpload;

	public UsageSubmissionWizard() {
		super();
		setTitles();
		init(true);
	}

	public UsageSubmissionWizard(boolean performUpload) {
		super();
		setTitles();
		init(performUpload);
	}

	private void setTitles() {
		super.setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(UiUsageMonitorPlugin.ID_PLUGIN,
				"icons/wizban/banner-user.gif"));
		super.setWindowTitle("Mylyn Feedback");
	}

	private void init(boolean performUpload) {
		this.performUpload = performUpload;
		setNeedsProgressMonitor(true);
		uid = UiUsageMonitorPlugin.getDefault().getPreferenceStore().getInt(UiUsageMonitorPlugin.PREF_USER_ID);
		if (uid == 0 || uid == -1) {
			addBackgroundPage();
			final int[] newUid = new int[1];
			try {
				// TODO make sure that this works in some way 
				getContainer().run(false, true, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						newUid[0] = UiUsageMonitorPlugin.getDefault().getUploadManager().getNewUid(monitor);
					}
				});
			} catch (InvocationTargetException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
			} catch (InterruptedException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
			}
			uid = newUid[0];

			UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(UiUsageMonitorPlugin.PREF_USER_ID, uid);
		}
		uploadPage = new UsageUploadWizardPage(this);
		fileSelectionPage = new UsageFileSelectionWizardPage("TODO, change this string");
		if (UiUsageMonitorPlugin.getDefault().isBackgroundEnabled()) {
			AbstractStudyBackgroundPage page = UiUsageMonitorPlugin.getDefault()
					.getStudyParameters()
					.getBackgroundPage();
			backgroundPage = page;
		}
		if (UiUsageMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload) {
			AbstractStudyQuestionnairePage page = UiUsageMonitorPlugin.getDefault()
					.getStudyParameters()
					.getQuestionnairePage();
			questionnairePage = page;
		}
		super.setForcePreviousAndNextButtons(true);

	}

	private File questionnaireFile = null;

	private File backgroundFile = null;

	@Override
	public boolean performFinish() {

		if (!performUpload) {
			return true;
		}
		if (UiUsageMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload && questionnairePage != null) {
			questionnaireFile = questionnairePage.createFeedbackFile();
		}
		if (UiUsageMonitorPlugin.getDefault().isBackgroundEnabled() && performUpload && displayBackgroundPage
				&& backgroundPage != null) {
			backgroundFile = backgroundPage.createFeedbackFile();
		}

		if (displayFileSelectionPage) {
			backupFilesToUpload = fileSelectionPage.getZipFilesSelected();
		}

		// final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
		// protected void execute(final IProgressMonitor monitor) throws
		// CoreException {
		// monitor.beginTask("Uploading user statistics", 3);
		// performUpload(monitor);
		// monitor.done();
		// }
		// };

		Job j = new Job("Upload User Statistics") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Uploading user statistics", 3);
					performUpload(monitor);
					monitor.done();
					// op.run(monitor);
					return Status.OK_STATUS;
				} catch (Exception e) {
					Status status = new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, IStatus.ERROR,
							"Error uploading statistics", e);
					StatusHandler.log(status);
					return status;
				}
			}
		};
		// j.setUser(true);
		j.setPriority(Job.DECORATE);
		j.schedule();
		return true;
	}

	public void performUpload(IProgressMonitor monitor) {
		String servletUrl = UiUsageMonitorPlugin.getDefault().getStudyParameters().getUploadServletUrl();
		boolean failed = false;
		if (UiUsageMonitorPlugin.getDefault().isBackgroundEnabled() && performUpload && backgroundFile != null) {
			failed = !UiUsageMonitorPlugin.getDefault().getUploadManager().uploadFile(servletUrl, backgroundFile, uid,
					monitor);

			if (backgroundFile.exists()) {
				backgroundFile.delete();
			}
		}

		if (UiUsageMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload && questionnaireFile != null) {
			failed = !UiUsageMonitorPlugin.getDefault().getUploadManager().uploadFile(servletUrl, questionnaireFile,
					uid, monitor);

			if (questionnaireFile.exists()) {
				questionnaireFile.delete();
			}
		}
		File zipFile = zipFilesForUpload();
		if (zipFile == null) {
			return;
		}

		failed = !UiUsageMonitorPlugin.getDefault().getUploadManager().uploadFile(servletUrl, zipFile, uid, monitor);

		if (zipFile.exists()) {
			zipFile.delete();
		}

		if (!failed) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					// popup a dialog telling the user that the upload was good
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Successful Upload",
							"Your usage statistics have been successfully uploaded.\n Thank you for participating.");
				}
			});
		}

		UiUsageMonitorPlugin.getDefault().getInteractionLogger().startMonitoring();
		UiUsageMonitorPlugin.setPerformingUpload(false);
		return;
	}

	@Override
	public boolean performCancel() {
		UiUsageMonitorPlugin.getDefault().userCancelSubmitFeedback(new Date(), true);
		return true;
	}

	@Override
	public boolean canFinish() {
		if (!performUpload) {
			return true;// getUidPage.isPageComplete();
		} else {
			return this.getContainer().getCurrentPage() == uploadPage || !performUpload;
		}
	}

	public UsageUploadWizardPage getUploadPage() {
		return uploadPage;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// no initialization needed
	}

	@Override
	public void addPages() {
		if (UiUsageMonitorPlugin.getDefault().isQuestionnaireEnabled() && performUpload && questionnairePage != null) {
			addPage(questionnairePage);
		}
		if (performUpload) {
			if (UsageFileSelectionWizardPage.unsubmittedLogsExist()) {
				addPage(fileSelectionPage);
				displayFileSelectionPage = true;
			}
			addPage(uploadPage);
		}
	}

	public void addBackgroundPage() {
		if (UiUsageMonitorPlugin.getDefault().isBackgroundEnabled() && backgroundPage != null) {
			addPage(backgroundPage);
			displayBackgroundPage = true;
		}
	}

	public String getMonitorFileName() {
		return monitorFile.getAbsolutePath();
	}

	/** The status from the http request */
	private int status;

	/** the response for the http request */
	private String resp;

	public int getUid() {
		return uid;
	}

	private File processMonitorFile(File monitorFile) {
		File processedFile = new File("processed-" + UiUsageMonitorPlugin.MONITOR_LOG_NAME + processedFileCount++
				+ ".xml");
		InteractionEventLogger logger = new InteractionEventLogger(processedFile);
		logger.startMonitoring();
		List<InteractionEvent> eventList = logger.getHistoryFromFile(monitorFile);

		if (eventList.size() > 0) {
			for (InteractionEvent event : eventList) {
				if (event.getOriginId().startsWith(ORG_ECLIPSE_PREFIX)) {
					logger.interactionObserved(event);
				}
			}
		}

		return processedFile;
	}

	private void addToSubmittedLogFile(String fileName) {
		File submissionLogFile = new File(MonitorFileRolloverJob.getZippedMonitorFileDirPath(),
				UsageFileSelectionWizardPage.SUBMISSION_LOG_FILE_NAME);
		try {
			FileWriter fileWriter = new FileWriter(submissionLogFile, true);
			fileWriter.append(fileName + "\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Error unzipping backup monitor log files", e));
		}
	}

	private File zipFilesForUpload() {
		UiUsageMonitorPlugin.setPerformingUpload(true);
		UiUsageMonitorPlugin.getDefault().getInteractionLogger().stopMonitoring();

		List<File> files = new ArrayList<File>();
		File monitorFile = UiUsageMonitorPlugin.getDefault().getMonitorLogFile();
		File fileToUpload = this.processMonitorFile(monitorFile);
		files.add(fileToUpload);

		if (displayFileSelectionPage && backupFilesToUpload.size() > 0) {
			for (String currFilePath : backupFilesToUpload) {
				File file = new File(MonitorFileRolloverJob.getZippedMonitorFileDirPath(), currFilePath);
				if (file.exists()) {
					List<File> unzippedFiles;
					try {
						unzippedFiles = ZipFileUtil.unzipFiles(file, System.getProperty("java.io.tmpdir"),
								new NullProgressMonitor());

						if (unzippedFiles.size() > 0) {
							for (File f : unzippedFiles) {
								files.add(this.processMonitorFile(f));
								this.addToSubmittedLogFile(currFilePath);
							}
						}
					} catch (IOException e) {
						StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
								"Error unzipping backup monitor log files", e));
					}
				}
			}
		}

		UiUsageMonitorPlugin.getDefault().getInteractionLogger().startMonitoring();
		try {
			File zipFile = File.createTempFile(uid + ".", ".zip");
			ZipFileUtil.createZipFile(zipFile, files);
			return zipFile;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Error uploading", e));
			return null;
		}
	}
}
