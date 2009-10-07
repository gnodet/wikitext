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

package org.eclipse.mylyn.internal.monitor.usage.preferences;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.monitor.usage.preferences.messages"; //$NON-NLS-1$

	public static String UsageDataPreferencePage_Days;

	public static String UsageDataPreferencePage_Enable_Logging_To;

	public static String UsageDataPreferencePage_Enable_Submission_Every;

	public static String UsageDataPreferencePage_Events_Since_Upload;

	public static String UsageDataPreferencePage_If_Enabled_Mylyn_Monitors;

	public static String UsageDataPreferencePage_Message_Digest_One_Way_Hash;

	public static String UsageDataPreferencePage_Monitoring;

	public static String UsageDataPreferencePage_Obfuscate_Elements_Using;

	public static String UsageDataPreferencePage_Total_Events;

	public static String UsageDataPreferencePage_Upload_Url;

	public static String UsageDataPreferencePage_Usage_Feedback;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
