package org.eclipse.mylyn.internal.tests.report;

public class TaskReporterStatistics {

	public int tasksReopened;

	public int tasksResolved;

	public int tasksUntouched;

	public int tasksStackTraceUpToDate;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Tasks untouched : ").append(tasksUntouched).append("\n");
		sb.append("Tasks resolved  : ").append(tasksResolved).append("\n");
		sb.append("Tasks reopened  : ").append(tasksReopened).append("\n");
		sb.append("Tasks up-to-date: ").append(tasksStackTraceUpToDate).append("\n");
		sb.append("Total           : ")
				.append(tasksUntouched + tasksResolved + tasksReopened + tasksStackTraceUpToDate);
		return sb.toString();
	}

}
