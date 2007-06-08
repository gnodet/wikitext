/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.share;

import org.eclipse.mylyn.core.MylarStatusHandler;

/**
 * @author Wesley Coelho
 * @author Mik Kersten
 */
public class SharedDataDirectoryManager {

	/**
	 * True if the shared data directory has been temporarily set for reporting
	 * purposes
	 */
	private boolean sharedDataDirectoryInUse = false;

	/**
	 * Path of the data directory to temporarily use as the MylarDataDirectory
	 * (for reporting)
	 */
	private String sharedDataDirectory = null;

	/**
	 * Sets the path of a shared data directory to be temporarily used (for
	 * reporting). Call useMainDataDirectory() to return to using the main data
	 * directory.
	 */
	public void setSharedDataDirectory(String dirPath) {
		sharedDataDirectory = dirPath;
	}

	/**
	 * Returns the shared data directory path if one has been set. If not, the
	 * empty string is returned. Note that the directory may not currently be in
	 * use.
	 */
	public String getSharedDataDirectory() {
		if (sharedDataDirectory != null) {
			return sharedDataDirectory;
		} else {
			return "";
		}
	}

	/**
	 * Set to true to use the shared data directory set with
	 * setSharedDataDirectory(String) Set to false to return to using the main
	 * data directory
	 */
	public void setSharedDataDirectoryEnabled(boolean enable) {
		if (enable && sharedDataDirectory == null) {
			MylarStatusHandler.fail(new Exception("EnableDataDirectoryException"),
					"Could not enable shared data directory because no shared data directory was specifed.", true);
			return;
		}
		sharedDataDirectoryInUse = enable;
	}

	/**
	 * True if a shared data directory rather than the main data directory is
	 * currently in use
	 */
	public boolean isSharedDataDirectoryEnabled() {
		return sharedDataDirectoryInUse;
	}
}
