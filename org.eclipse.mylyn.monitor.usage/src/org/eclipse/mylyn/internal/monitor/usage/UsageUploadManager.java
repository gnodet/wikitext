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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

public class UsageUploadManager {

	protected HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

	private static final int SIZE_OF_INT = 8;

	public boolean uploadFile(final String postUrl, final File file, final int uid, IProgressMonitor monitor) {
		// make sure that we send the uid with all files
		String filename = file.getName();
		if (!filename.startsWith(uid + ".")) { //$NON-NLS-1$
			filename = uid + "-" + filename; //$NON-NLS-1$
		}
		return uploadFile(postUrl, "temp.txt", file, filename, uid, monitor); //$NON-NLS-1$

	}

	private boolean uploadFile(final String postUrl, final String name, final File file, final String filename,
			final int uid, IProgressMonitor monitor) {

		PostMethod filePost = new PostMethod(postUrl);

		try {
			Part[] parts = { new FilePart(name, filename, file) };
			filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

			AbstractWebLocation location = new WebLocation(postUrl);
			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			final int status = WebUtil.execute(httpClient, hostConfiguration, filePost, monitor);

			if (status == HttpStatus.SC_UNAUTHORIZED) {
				// The uid was incorrect so inform the user
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
								Messages.UsageUploadManager_Error_Uploading_Uid_Incorrect, file.getName(), uid));
					}
				});
			} else if (status == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading,
								Messages.UsageUploadManager_Error_Uploading_Proxy_Authentication);
					}
				});
			} else if (status != 200) {
				// there was a problem with the file upload so throw up an error
				// dialog to inform the user
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
								Messages.UsageUploadManager_Error_Uploading_Http_Response, file.getName(), status));
					}
				});
			} else {
				// the file was uploaded successfully
				return true;
			}

		} catch (final FileNotFoundException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
							Messages.UsageUploadManager_Error_Uploading_X_Y, file.getName(), e.getClass()
									.getCanonicalName()));
				}
			});
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					Messages.UsageUploadManager_Error_Uploading, e));
		} catch (final IOException e) {
			if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
								Messages.UsageUploadManager_Error_Uploading_X_No_Network, file.getName()));
					}
				});
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
								Messages.UsageUploadManager_Error_Uploading_X_Y, file.getName(), e.getClass()
										.getCanonicalName()));
					}
				});
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Error uploading", e)); //$NON-NLS-1$
			}
		} finally {
			filePost.releaseConnection();
		}
		return false;
	}

	public int getExistingUid(StudyParameters studyParameters, String firstName, String lastName, String emailAddress,
			boolean anonymous, IProgressMonitor monitor) {
		// TODO extract url for servlet
		String url = studyParameters.getUserIdServletUrl();
		final GetMethod getUidMethod = new GetMethod(url);

		try {
			NameValuePair first = new NameValuePair("firstName", firstName); //$NON-NLS-1$
			NameValuePair last = new NameValuePair("lastName", lastName); //$NON-NLS-1$
			NameValuePair email = new NameValuePair("email", emailAddress); //$NON-NLS-1$
			NameValuePair job = new NameValuePair("jobFunction", ""); //$NON-NLS-1$ //$NON-NLS-2$
			NameValuePair size = new NameValuePair("companySize", ""); //$NON-NLS-1$ //$NON-NLS-2$
			NameValuePair buisness = new NameValuePair("companyBuisness", ""); //$NON-NLS-1$ //$NON-NLS-2$
			NameValuePair contact = new NameValuePair("contact", ""); //$NON-NLS-1$ //$NON-NLS-2$
			NameValuePair anon = null;
			if (anonymous) {
				anon = new NameValuePair("anonymous", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				anon = new NameValuePair("anonymous", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (studyParameters.usingContactField()) {
				getUidMethod.setQueryString(new NameValuePair[] { first, last, email, job, size, buisness, anon,
						contact });
			} else {
				getUidMethod.setQueryString(new NameValuePair[] { first, last, email, job, size, buisness, anon });
			}

			// create a new client and upload the file
			AbstractWebLocation location = new WebLocation(url);
			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			final int status = WebUtil.execute(httpClient, hostConfiguration, getUidMethod, monitor);

			if (status == HttpStatus.SC_OK) {
				String response = getStringFromStream(WebUtil.getResponseBodyAsStream(getUidMethod, monitor));
				response = response.substring(response.indexOf(":") + 1).trim(); //$NON-NLS-1$
				int uid = Integer.parseInt(response);
				return uid;
			} else {
				// there was a problem with the file upload so throw up an error
				// dialog to inform the user
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Getting_Uid, NLS.bind(
								Messages.UsageUploadManager_Error_Getting_Uid_Http_Response, status));
					}
				});
				return -1;
			}

		} catch (final IOException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
							Messages.UsageUploadManager_Error_Getting_UidX_Y, e.getClass().getCanonicalName(),
							e.getMessage()));
				}
			});
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					Messages.UsageUploadManager_Error_Uploading, e));
		} catch (final Exception e) {
			if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading,
								Messages.UsageUploadManager_Error_Getting_Uid_No_Network);
					}
				});
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
								Messages.UsageUploadManager_Error_Getting_Uid_X_Y, e.getClass().getCanonicalName(),
								e.getMessage()));
					}
				});
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
						Messages.UsageUploadManager_Error_Uploading, e));
			}

		} finally {
			getUidMethod.releaseConnection();
		}
		return -1;
	}

	private String getStringFromStream(InputStream i) throws IOException {
		String s = ""; //$NON-NLS-1$
		String data = ""; //$NON-NLS-1$
		BufferedReader br = new BufferedReader(new InputStreamReader(i));
		while ((s = br.readLine()) != null) {
			data += s;
		}

		return data;
	}

	public int getNewUid(StudyParameters studyParameters, IProgressMonitor monitor) {
		// TODO extract url for servlet
		String url = studyParameters.getUserIdServletUrl();
		final PostMethod getUserIdMethod = new PostMethod(url);
		try {
			getUserIdMethod.addParameter(new NameValuePair("MylarUserID", "")); //$NON-NLS-1$//$NON-NLS-2$

			AbstractWebLocation location = new WebLocation(url);
			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			final int status = WebUtil.execute(httpClient, hostConfiguration, getUserIdMethod, monitor);

			if (status == HttpStatus.SC_OK) {
				InputStream inputStream = WebUtil.getResponseBodyAsStream(getUserIdMethod, monitor);
				byte[] buffer = new byte[SIZE_OF_INT];
				int numBytesRead = inputStream.read(buffer);
				int uid = new Integer(new String(buffer, 0, numBytesRead)).intValue();
				inputStream.close();
				return uid;
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
						"Unable to get new user id.  Server retured: " + status)); //$NON-NLS-1$
				return -1;
			}

		} catch (final IOException e) {
			if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
				MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading,
						Messages.UsageUploadManager_Error_Getting_Uid_No_Network);
			} else {
				MessageDialog.openError(null, Messages.UsageUploadManager_Error_Uploading, NLS.bind(
						Messages.UsageUploadManager_Error_Getting_Uid_X, e.getClass().getCanonicalName()));
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
						Messages.UsageUploadManager_Error_Uploading, e));
			}
		} finally {
			getUserIdMethod.releaseConnection();
		}
		return -1;
	}

	public int getNewUid(StudyParameters studyParameters, String firstName, String lastName, String emailAddress,
			boolean anonymous, String jobFunction, String companySize, String companyFunction, boolean contactEmail,
			IProgressMonitor monitor) {
		return getNewUid(studyParameters, monitor);
		// TODO add back the code for dealing with creasting a user given a name 
//			// NameValuePair first = new NameValuePair("firstName", firstName);
//			// NameValuePair last = new NameValuePair("lastName", lastName);
//			// NameValuePair email = new NameValuePair("email", emailAddress);
//			// NameValuePair job = new NameValuePair("jobFunction",
//			// jobFunction);
//			// NameValuePair size = new NameValuePair("companySize",
//			// companySize);
//			// NameValuePair buisness = new NameValuePair("companyBuisness",
//			// companyFunction);
//			// NameValuePair contact = null;
//			// if (contactEmail) {
//			// contact = new NameValuePair("contact", "true");
//			// } else {
//			// contact = new NameValuePair("contact", "false");
//			// }
//			// NameValuePair anon = null;
//			// if (anonymous) {
//			// anon = new NameValuePair("anonymous", "true");
//			// } else {
//			// anon = new NameValuePair("anonymous", "false");
//			// }

//		} catch (Exception e) {
//			// there was a problem with the file upload so throw up an error
//			// dialog to inform the user and log the exception
//			if (e instanceof NoRouteToHostException || e instanceof UnknownHostException) {
//				MessageDialog.openError(null, "Error Uploading", "There was an error getting a new user id: \n"
//						+ "No network connection.  Please try again later");
//			} else {
//				MessageDialog.openError(null, "Error Uploading", "There was an error getting a new user id: \n"
//						+ e.getClass().getCanonicalName());
//				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Error uploading", e));
//			}
//		}

	}
}
