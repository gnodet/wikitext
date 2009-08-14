/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.examples.monitor.study;

import java.io.File;
import java.util.Calendar;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.internal.monitor.usage.wizards.UsageSubmissionWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * A wizard for uploading the Mylyn statistics to a website
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class FeedbackWizard extends Wizard implements INewWizard {

	private final SubmitFeedbackPage feedbackPage;

	private final StudyParameters studyParameters;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public FeedbackWizard(UsageSubmissionWizard wizard) {
		super();
		setNeedsProgressMonitor(true);

		feedbackPage = new SubmitFeedbackPage(wizard);
		super.setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(UiUsageMonitorPlugin.ID_PLUGIN,
				"icons/wizban/banner-user.gif"));
		super.setWindowTitle("Mylyn Feedback");
		studyParameters = UiUsageMonitorPlugin.getDefault().getStudyParameters();
	}

	public FeedbackWizard() {
		this(null);
	}

	@Override
	public boolean performFinish() {
		File f = feedbackPage.createFeedbackFile();
		if (f == null) {
			return true;
		}
		// FIXME!!!!!!!!!!!!!!!!!!

		String uploadScript;

		// XXX: unimplemented
		uploadScript = "<unimplemented>";
		// uploadScript =
		// MylynUsageMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl()
		// +
		// MylynUsageMonitorPlugin.getDefault().getStudyParameters().getScriptsQuestionnaire();

		int uid = feedbackPage.getUid();
		UiUsageMonitorPlugin.getDefault().getUploadManager().uploadFile(
				uploadScript,
				"MYLYN" + uid,
				f,
				studyParameters.getUploadFileLabel() + "-" + studyParameters.getVersion() + "-" + "feedback" + "-"
						+ uid + "-" + DateUtil.getIsoFormattedDateTime(Calendar.getInstance()) + ".txt", uid,
				new NullProgressMonitor());
		if (f.exists()) {
			f.delete();
		}
		return true;
	}

	private int status;

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// don't need to do any initialization
	}

	@Override
	public void addPages() {
		addPage(feedbackPage);
	}
}
