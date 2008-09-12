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
import java.lang.reflect.InvocationTargetException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Calendar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.internal.monitor.usage.wizards.UsageSubmissionWizard;
import org.eclipse.swt.widgets.Display;
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

	/**
	 * Constructor for SampleNewWizard.
	 */
	public FeedbackWizard(UsageSubmissionWizard wizard) {
		super();
		setNeedsProgressMonitor(true);

		feedbackPage = new SubmitFeedbackPage(wizard);
	}

	public FeedbackWizard() {
		super();
		setNeedsProgressMonitor(true);
		super.setDefaultPageImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(UiUsageMonitorPlugin.ID_PLUGIN,
				"icons/wizban/banner-user.gif"));
		super.setWindowTitle("Mylyn Feedback");
		feedbackPage = new SubmitFeedbackPage(null);
	}

	@Override
	public boolean performFinish() {
		File f = feedbackPage.createFeedbackFile();
		if (f == null) {
			return true;
		}
		upload(f, feedbackPage.getStringUid());
		if (f.exists()) {
			f.delete();
		}
		return true;
	}

	private int status;

	/**
	 * Method to upload a file to a cgi script
	 * 
	 * @param f
	 *            The file to upload
	 */
	private void upload(File f, String uid) {
		String uploadScript;

		// XXX: unimplemented
		uploadScript = "<unimplemented>";
		// uploadScript =
		// MylynUsageMonitorPlugin.getDefault().getStudyParameters().getScriptsUrl()
		// +
		// MylynUsageMonitorPlugin.getDefault().getStudyParameters().getScriptsQuestionnaire();

		if (f.length() == 0) {
			return;
		}

		try {
			final PostMethod filePost = new PostMethod(uploadScript);

			Part[] parts;
			Part[] p = { new FilePart("MYLYN" + uid, UiUsageMonitorPlugin.UPLOAD_FILE_LABEL + "-"
					+ UiUsageMonitorPlugin.VERSION + "-" + "feedback" + "-" + uid + "-"
					+ DateUtil.getIsoFormattedDateTime(Calendar.getInstance()) + ".txt", f) };
			parts = p;

			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

			final HttpClient client = new HttpClient();
			UiUsageMonitorPlugin.getDefault().configureProxy(client, uploadScript);

			ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			pmd.run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						status = client.executeMethod(filePost);
						filePost.releaseConnection();

					} catch (Exception e) {
						// there was a problem with the file upload so throw up
						// an error
						// dialog to inform the user and log the exception
						if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
							MessageDialog.openError(null, "Error Uploading",
									"There was an error uploading the feedback" + ": \n"
											+ "No network connection.  Please try again later");
						} else {
							MessageDialog.openError(null, "Error Uploading",
									"There was an error uploading the feedback" + ": \n"
											+ e.getClass().getCanonicalName());
							;
						}
					}
					monitor.worked(1);
					monitor.done();
				}
			});

			if (status == 401) {
				// The uid was incorrect so inform the user
				MessageDialog.openError(null, "Error Uploading", "There was an error uploading the feedback: \n"
						+ "Your uid was incorrect: " + uid + "\n");
			} else if (status == 407) {
				MessageDialog.openError(null, "Error Uploading",
						"Could not upload because proxy server authentication failed.  Please check your proxy server settings.");
			} else if (status != 200) {
				// there was a problem with the file upload so throw up an error
				// dialog to inform the user
				MessageDialog.openError(null, "Error Uploading", "There was an error uploading the feedback: \n"
						+ "HTTP Response Code " + status + "\n" + "Please try again later");
			} else {
				// the file was uploaded successfully
			}

		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, MylynUserStudyExamplePlugin.ID_PLUGIN, "Error uploading", e));
			// there was a problem with the file upload so throw up an error
			// dialog to inform the user and log the exception
			if (e instanceof NoRouteToHostException) {
				MessageDialog.openError(null, "Error Uploading", "There was an error uploading the feedback: \n"
						+ "No network connection.  Please try again later");
			} else {
				MessageDialog.openError(null, "Error Uploading", "There was an error uploading the feedback: \n"
						+ e.getClass().getCanonicalName());
			}
		} finally {
			f.delete();
		}
	}

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
