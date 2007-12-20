/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tests.report;

/**
 * @author Steffen Pingel
 */
public class TaskReporterStatistics {

	public int tasksReopened;

	public int tasksResolved;

	public int tasksUntouched;

	public int tasksStackTraceUpToDate;

	public int ignoredErrors;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tasks untouched : ").append(tasksUntouched).append("\n");
		sb.append("Tasks resolved  : ").append(tasksResolved).append("\n");
		sb.append("Tasks reopened  : ").append(tasksReopened).append("\n");
		sb.append("Tasks up-to-date: ").append(tasksStackTraceUpToDate).append("\n");
		if (ignoredErrors > 0) {
			sb.append("Ignored errors  : ").append(ignoredErrors).append("\n");
		}
		sb.append("Total           : ")
				.append(tasksUntouched + tasksResolved + tasksReopened + tasksStackTraceUpToDate);
		return sb.toString();
	}

}
