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
package org.eclipse.mylyn.internal.monitor.usage;

import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.monitor.usage.wizards.UsageSubmissionWizard;
import org.eclipse.mylyn.internal.monitor.usage.wizards.UsageSubmissionWizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Shawn Minto
 */
public class CheckForUploadJob extends UIJob {

	public CheckForUploadJob(Display display) {
		super(display, Messages.CheckForUploadJob_Check_For_Upload);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (getDisplay() == null || getDisplay().isDisposed() || PlatformUI.getWorkbench().isClosing()) {
			return Status.CANCEL_STATUS;
		}
		if (!MonitorUiPlugin.getDefault().suppressConfigurationWizards() && ContextCorePlugin.getDefault() != null) {
			checkForStatisticsUpload();
		}
		return Status.OK_STATUS;
	}

	synchronized void checkForStatisticsUpload() {

		Date currentTime = new Date();
		if (shouldAskForUpload(currentTime)) {

			String ending = getUserPromptDelay() == 1 ? "" : "s"; //$NON-NLS-1$//$NON-NLS-2$
			MessageDialog message = new MessageDialog(Display.getDefault().getActiveShell(),
					Messages.UiUsageMonitorPlugin_Send_Usage_Feedback, null,
					Messages.UiUsageMonitorPlugin_Help_Improve_Eclipse_And_Mylyn, MessageDialog.QUESTION, new String[] {
							Messages.UiUsageMonitorPlugin_Submit_Feedback,
							NLS.bind(Messages.UiUsageMonitorPlugin_Remind_Me_In_X_Days, getUserPromptDelay(), ending),
							Messages.UiUsageMonitorPlugin_Dont_Ask_Again, }, 0);
			int result = message.open();
			if (result == 0) {
				// time must be stored right away into preferences, to prevent
				// other threads
				UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
						MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE, currentTime.getTime());

				if (!UiUsageMonitorPlugin.getDefault().getPreferenceStore().contains(
						MonitorPreferenceConstants.PREF_MONITORING_MYLYN_ECLIPSE_ORG_CONSENT_VIEWED)
						|| !UiUsageMonitorPlugin.getDefault().getPreferenceStore().getBoolean(
								MonitorPreferenceConstants.PREF_MONITORING_MYLYN_ECLIPSE_ORG_CONSENT_VIEWED)) {
					MessageDialog consentMessage = new MessageDialog(Display.getDefault().getActiveShell(),
							Messages.UiUsageMonitorPlugin_Consent, null, Messages.UiUsageMonitorPlugin_All_Data_Public,
							MessageDialog.INFORMATION, new String[] { IDialogConstants.OK_LABEL }, 0);
					consentMessage.open();
					UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
							MonitorPreferenceConstants.PREF_MONITORING_MYLYN_ECLIPSE_ORG_CONSENT_VIEWED, true);
				}

				UsageSubmissionWizard wizard = new UsageSubmissionWizard();
				wizard.init(PlatformUI.getWorkbench(), null);
				// Instantiates the wizard container with the wizard and
				// opens it
				WizardDialog dialog = new UsageSubmissionWizardDialog(Display.getDefault().getActiveShell(), wizard);
				dialog.create();
				dialog.open();

				/*
				 * the UI usage report is loaded asynchronously so there's no
				 * synchronous way to know if it failed if (wizard.failed()) {
				 * lastTransmit.setTime(currentTime.getTime() + DELAY_ON_FAILURE -
				 * studyParameters.getTransmitPromptPeriod());
				 * plugin.getPreferenceStore().setValue(MylynMonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
				 * currentTime.getTime()); }
				 */

			} else {
				if (result == 1) {
					UiUsageMonitorPlugin.getDefault().userCancelSubmitFeedback(currentTime, true);
				} else {
					UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
							MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION, false);
				}
			}
			message.close();
		}
	}

	private boolean shouldAskForUpload(Date currentTime) {
		if (UiUsageMonitorPlugin.getDefault().isSubmissionWizardOpen()) {
			return false;
		}

		StudyParameters studyParameters = UiUsageMonitorPlugin.getDefault().getStudyParameters();
		if (!UiUsageMonitorPlugin.getDefault().isMonitoringEnabled() || studyParameters == null
				|| !studyParameters.shouldPromptForSubmission()) {
			return false;
		}

		Date lastTransmit = UiUsageMonitorPlugin.getDefault().getLastTransmitDate();

		if (currentTime.getTime() > lastTransmit.getTime() + studyParameters.getTransmitPromptPeriod()
				&& UiUsageMonitorPlugin.getDefault().getPreferenceStore().getBoolean(
						MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION)) {
			return true;

		}
		return false;
	}

	private long getUserPromptDelay() {
		return UiUsageMonitorPlugin.DELAY_ON_USER_REQUEST / UiUsageMonitorPlugin.DAY;
	}

}