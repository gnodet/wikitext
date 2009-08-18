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

package org.eclipse.mylyn.internal.monitor.usage.editors;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.monitor.usage.editors.messages"; //$NON-NLS-1$

	public static String UsageDataOverviewPage_Please_Review_Data_Submitted_To_X;

	public static String UsageDataOverviewPage_Review_Usage_Data;

	public static String UsageEditorPart_Actions;

	public static String UsageEditorPart_Export_As_CSV;

	public static String UsageEditorPart_Export_As_Html;

	public static String UsageEditorPart_Specify_Directory_For_Csv;

	public static String UsageEditorPart_Specify_File_Name;

	public static String UsageEditorPart_Usage_Summary;

	public static String UsageStatsEditorInput_Usage_Summary;

	public static String UsageStatsEditorInput_X_Usage_Statistics;
	public static String UsageSummaryReportEditorPart_Actions;

	public static String UsageSummaryReportEditorPart_Count;

	public static String UsageSummaryReportEditorPart_Id;

	public static String UsageSummaryReportEditorPart_Kind;

	public static String UsageSummaryReportEditorPart_Submit_To;

	public static String UsageSummaryReportEditorPart_Url_Could_Not_Be_Opened;

	public static String UsageSummaryReportEditorPart_Url_Not_Found;

	public static String UsageSummaryReportEditorPart_Usage_Details;

	public static String UsageSummaryReportEditorPart_View_Community_Statistics;

	public static String UsageSummaryReportEditorPart_View_File;
	public static String UserStudyEditorPart_Id;

	public static String UserStudyEditorPart_Kind;

	public static String UserStudyEditorPart_Last_Delta;

	public static String UserStudyEditorPart_Num;

	public static String UserStudyEditorPart_Specify_Directory_For_Csv_Files;

	public static String UserStudyEditorPart_Usage_Details;

	public static String UserStudyEditorPart_Users;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
