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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.monitor.usage.messages"; //$NON-NLS-1$

	public static String CheckForUploadJob_Check_For_Upload;

	public static String InteractionEventLogger_Reading_History_From_File;

	public static String MonitorFileRolloverJob_Mylyn_Monitor_Log_Rollover;

	public static String ReportGenerator_Generate_Statistics_Job;

	public static String ReportGenerator_Mylyn_Usage_Summary_Generation;

	public static String StudyParameters_All_Events_Submitted_To_Y;

	public static String StudyParameters_Only_Events_From_X_Submitted_To_Y;

	public static String StudyParameters_Previously_Downloaded_X_Monitor;

	public static String UiUsageMonitorPlugin_All_Data_Public;

	public static String UiUsageMonitorPlugin_Consent;

	public static String UiUsageMonitorPlugin_Dont_Ask_Again;

	public static String UiUsageMonitorPlugin_Eclipse_Mylyn;

	public static String UiUsageMonitorPlugin_Fill_Out_Form;

	public static String UiUsageMonitorPlugin_Help_Improve_Eclipse_And_Mylyn;

	public static String UiUsageMonitorPlugin_Mylyn_Feedback;

	public static String UiUsageMonitorPlugin_Submit_Feedback;

	public static String UiUsageMonitorPlugin_Remind_Me_In_X_Days;

	public static String UiUsageMonitorPlugin_Send_Usage_Feedback;

	public static String UsageUploadManager_Error_Getting_Uid;

	public static String UsageUploadManager_Error_Getting_Uid_Http_Response;

	public static String UsageUploadManager_Error_Getting_Uid_No_Network;

	public static String UsageUploadManager_Error_Getting_Uid_X;

	public static String UsageUploadManager_Error_Getting_Uid_X_Y;

	public static String UsageUploadManager_Error_Getting_UidX_Y;

	public static String UsageUploadManager_Error_Uploading;

	public static String UsageUploadManager_Error_Uploading_Http_Response;

	public static String UsageUploadManager_Error_Uploading_Proxy_Authentication;

	public static String UsageUploadManager_Error_Uploading_Uid_Incorrect;

	public static String UsageUploadManager_Error_Uploading_X_No_Network;

	public static String UsageUploadManager_Error_Uploading_X_Y;

	public static String UsageUploadManager_Unable_To_Upload_X;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
