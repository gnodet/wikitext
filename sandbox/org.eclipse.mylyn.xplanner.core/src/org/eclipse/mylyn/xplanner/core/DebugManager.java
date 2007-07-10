/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public final class DebugManager {

	/**
	 * Determines if debugging is enabled.
	 * @return <code>true</code> if debugging is enabled, <code>false</code> otherwise
	 */
	public static boolean isDebugEnabled() {
		return XPlannerCorePlugin.getDefault().isDebugging();
	}
	
	/**
	 * Determines if a specified debugging option flag has been enabled.
	 * @param option Debug option to check value of
	 * @return <code>true</code> if debugging is enabled and the specified option
	 * is <code>"true"</code>, <code>false</code> otherwise.
	 */
	public static boolean isDebugOptionEnabled(String option) {
		return "true".equals(Platform.getDebugOption(XPlannerCorePlugin.ID + '/' + option)); //$NON-NLS-1$
	}
	
	/**
	 * Retrieves the value of a debugging option.  Use {@link #isDebugOptionEnabled(String)} if
	 * you are just after the value of a flag.
	 * @param option Option to retrieve the value of
	 * @return Value of the specified debugging option or <code>null</code> if
	 * debugging is disabled.
	 * @see #isDebugOptionEnabled(String)
	 */
	public static String getDebugOption(String option) {
		return Platform.getDebugOption(XPlannerCorePlugin.ID + "/" + option); //$NON-NLS-1$
	}
	
	/**
	 * Log the specified <code>message</code> and <code>exception</code> to the
	 * log file.
	 * @param message Message to be logged.
	 * @param exception Optional stacktrace related to the message
	 */
	public static void log(String message, Exception exception) {
		XPlannerCorePlugin.log(IStatus.INFO, message, exception);
	}
	
	/**
	 * Log the specified <code>message</code> and <code>exception</code> to the
	 * log file 'warning' level.
	 * @param message Message to be logged.
	 * @param exception Optional stacktrace related to the message
	 */
	public static void warn(String message, Exception exception) {
		XPlannerCorePlugin.log(IStatus.WARNING, message, exception);
	}
	
	/**
	 * Log the specified <code>message</code> and <code>exception</code> to the
	 * log file at 'error' level.
	 * @param message Message to be logged.
	 * @param exception Optional stacktrace related to the message
	 */
	public static void error(String message, Exception exception) {
		XPlannerCorePlugin.log(IStatus.ERROR, message, exception);
	}
}
