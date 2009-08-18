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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.ZipFileUtil;
import org.eclipse.mylyn.internal.monitor.usage.InteractionEventLogger;
import org.eclipse.mylyn.internal.monitor.usage.MonitorFileRolloverJob;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.usage.AbstractStudyBackgroundPage;
import org.eclipse.mylyn.monitor.usage.AbstractStudyQuestionnairePage;
import org.eclipse.osgi.util.NLS;
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

	public static final String LOG = "log"; //$NON-NLS-1$

	public static final String STATS = "usage"; //$NON-NLS-1$

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

	private UsageDataOverviewPage dataOverviewPage;

	private boolean performUpload = true;

	private List<String> backupFilesToUpload;

	private final StudyParameters studyParameters;

	public UsageSubmissionWizard() {
		super();
		studyParameters = UiUsageMonitorPlugin.getDefault().getStudyParameters();
		setTitles();
		setNeedsProgressMonitor(true);
		init(true);
	}

	private void setTitles() {
		super.setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(UiUsageMonitorPlugin.ID_PLUGIN,
				"icons/wizban/banner-user.gif")); //$NON-NLS-1$
		super.setWindowTitle(NLS.bind(Messages.UsageSubmissionWizard_X_Feedback, studyParameters.getStudyName()));
	}

	private void init(boolean performUpload) {
		this.performUpload = performUpload;
		setNeedsProgressMonitor(true);

		uid = UiUsageMonitorPlugin.getDefault().getPreferenceStore().getInt(studyParameters.getUserIdPreferenceId());
		if (uid == 0 || uid == -1) {
			addBackgroundPage();
			final int[] newUid = new int[1];
			try {

				IRunnableContext service = getContainer();
				if (service == null) {
					service = PlatformUI.getWorkbench().getProgressService();

				}

				service.run(false, true, new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						newUid[0] = UiUsageMonitorPlugin.getDefault().getUploadManager().getNewUid(studyParameters,
								monitor);
					}
				});
			} catch (InvocationTargetException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
			} catch (InterruptedException e1) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, e1.getMessage(), e1));
			}
			uid = newUid[0];

			UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(studyParameters.getUserIdPreferenceId(),
					uid);
		}
		uploadPage = new UsageUploadWizardPage(this, studyParameters);
		fileSelectionPage = new UsageFileSelectionWizardPage(this, studyParameters);
		if (studyParameters.isBackgroundEnabled()) {
			AbstractStudyBackgroundPage page = studyParameters.getBackgroundPage();
			backgroundPage = page;
		}
		if (studyParameters.isQuestionnaireEnabled() && performUpload) {
			AbstractStudyQuestionnairePage page = studyParameters.getQuestionnairePage();
			questionnairePage = page;
		}

		dataOverviewPage = new UsageDataOverviewPage(this, studyParameters);

		super.setForcePreviousAndNextButtons(true);

	}

	private File questionnaireFile = null;

	private File backgroundFile = null;

	@Override
	public boolean performFinish() {

		if (!performUpload) {
			return true;
		}
		if (studyParameters.isQuestionnaireEnabled() && performUpload && questionnairePage != null) {
			questionnaireFile = questionnairePage.createFeedbackFile();
		}
		if (studyParameters.isBackgroundEnabled() && performUpload && displayBackgroundPage && backgroundPage != null) {
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

		Job j = new Job(Messages.UsageSubmissionWizard_Upload_User_Statistics) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					monitor.beginTask(Messages.UsageSubmissionWizard_Uploading_User_Statistics, 3);
					performUpload(monitor);
					monitor.done();
					// op.run(monitor);
					return Status.OK_STATUS;
				} catch (Exception e) {
					Status status = new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, IStatus.ERROR,
							"Error uploading statistics", e); //$NON-NLS-1$
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
		String servletUrl = studyParameters.getUploadServletUrl();
		boolean failed = false;
		if (studyParameters.isBackgroundEnabled() && performUpload && backgroundFile != null) {
			failed = !UiUsageMonitorPlugin.getDefault().getUploadManager().uploadFile(servletUrl, backgroundFile, uid,
					monitor);

			if (backgroundFile.exists()) {
				backgroundFile.delete();
			}
		}

		if (studyParameters.isQuestionnaireEnabled() && performUpload && questionnaireFile != null) {
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
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							Messages.UsageSubmissionWizard_Successful_Upload,
							Messages.UsageSubmissionWizard_Your_Usage_Statistics_Have_Been_Uploaded);
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
		if (studyParameters.isQuestionnaireEnabled() && performUpload && questionnairePage != null) {
			addPage(questionnairePage);
		}
		if (performUpload) {
			if (UsageFileSelectionWizardPage.unsubmittedLogsExist()) {
				addPage(fileSelectionPage);
				displayFileSelectionPage = true;
			}
			addPage(dataOverviewPage);
			addPage(uploadPage);
		}
	}

	public void addBackgroundPage() {
		if (studyParameters.isBackgroundEnabled() && backgroundPage != null) {
			addPage(backgroundPage);
			displayBackgroundPage = true;
		}
	}

	public String getMonitorFileName() {
		return monitorFile.getAbsolutePath();
	}

	public int getUid() {
		return uid;
	}

	// TODO allow this to be customized
	private File processMonitorFile(File monitorFile) {
		File processedFile = new File("processed-" + UiUsageMonitorPlugin.MONITOR_LOG_NAME + processedFileCount++ //$NON-NLS-1$
				+ ".xml"); //$NON-NLS-1$
		InteractionEventLogger logger = new InteractionEventLogger(processedFile);
		logger.startMonitoring();
		List<InteractionEvent> eventList = logger.getHistoryFromFile(monitorFile);

		Collection<String> filteredIds = studyParameters.getFilteredIds();

		if (eventList.size() > 0) {
			for (InteractionEvent event : eventList) {

				if (shouldIncludeEvent(event, filteredIds)) {
					logger.interactionObserved(event);
				} else {

					System.out.println(event.getOriginId());
				}
			}
		}

		return processedFile;
	}

	private boolean shouldIncludeEvent(InteractionEvent event, Collection<String> filteredIds) {
		if (filteredIds.size() == 0) {
			return true;
		}
		for (String filterId : filteredIds) {
			if (event.getOriginId().startsWith(filterId)) {
				return true;
			}
		}
		return false;
	}

	private void addToSubmittedLogFile(String fileName) {
		File submissionLogFile = new File(MonitorFileRolloverJob.getZippedMonitorFileDirPath(),
				UsageFileSelectionWizardPage.SUBMISSION_LOG_FILE_NAME);
		try {
			FileWriter fileWriter = new FileWriter(submissionLogFile, true);
			fileWriter.append(fileName + "\n"); //$NON-NLS-1$
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Error unzipping backup monitor log files", e)); //$NON-NLS-1$
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
						unzippedFiles = ZipFileUtil.unzipFiles(file, System.getProperty("java.io.tmpdir"), //$NON-NLS-1$
								new NullProgressMonitor());

						if (unzippedFiles.size() > 0) {
							for (File f : unzippedFiles) {
								files.add(this.processMonitorFile(f));
								this.addToSubmittedLogFile(currFilePath);
							}
						}
					} catch (IOException e) {
						StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
								"Error unzipping backup monitor log files", e)); //$NON-NLS-1$
					}
				}
			}
		}

		UiUsageMonitorPlugin.getDefault().getInteractionLogger().startMonitoring();
		try {
			File zipFile = File.createTempFile(uid + ".", ".zip"); //$NON-NLS-1$ //$NON-NLS-2$
			ZipFileUtil.createZipFile(zipFile, files);
			return zipFile;
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Error uploading", e)); //$NON-NLS-1$
			return null;
		}
	}

	public List<String> getBackupFilesToUpload() {
		return backupFilesToUpload;
	}

	public void setBackupFilesToUpload(List<String> backupFilesToUpload) {
		this.backupFilesToUpload = backupFilesToUpload;
	}
}
