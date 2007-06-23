/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskCollector;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskList;

public class ProgressQueryHitCollector implements ITaskCollector {

	public static final int MAX_HITS = 5000;

	public static final String MAX_HITS_REACHED = "Max allowed number of hits returned exceeded. Some hits may not be displayed. Please narrow query scope.";

	protected Set<AbstractTask> taskResults = new HashSet<AbstractTask>();

	/** The progress monitor for the search operation */
	private IProgressMonitor monitor = new NullProgressMonitor();

	/** The number of matches found */
	private int matchCount;

	/** The string to display to the user while querying */
	private static final String STARTING = "querying the server";

	/** The string to display to the user when we have 1 match */
	private static final String MATCH = "1 match";

	/** The string to display to the user when we have multiple or no matches */
	private static final String MATCHES = "{0} matches";

	/** The string to display to the user when the query is done */
	private static final String DONE = "done";

	protected TaskList taskList;

	protected ITaskFactory taskFactory;

	public ProgressQueryHitCollector(TaskList tasklist, ITaskFactory taskFactory) {
		this.taskList = tasklist;
		this.taskFactory = taskFactory;
	}

	public void aboutToStart(int startMatchCount) throws CoreException {
		taskResults.clear();
		matchCount = startMatchCount;
		monitor.setTaskName(STARTING);
	}

	public void accept(AbstractTask task) {

		if (!getProgressMonitor().isCanceled()) {
			getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
			getProgressMonitor().worked(1);
		}

		if (task == null)
			return;

		AbstractTask hitTask = taskList.getTask(task.getHandleIdentifier());
		if (hitTask == null) {
			hitTask = task;
			// task is new, add to tasklist
			taskList.addTask(hitTask);
		}
		taskResults.add((AbstractTask) hitTask);
		matchCount++;
	}

	public void accept(RepositoryTaskData taskData) throws CoreException {
		if (taskData == null)
			return;

		if (!getProgressMonitor().isCanceled()) {
			getProgressMonitor().subTask(getFormattedMatchesString(matchCount));
			getProgressMonitor().worked(1);
		}

		AbstractTask task = taskFactory.createTask(taskData, new SubProgressMonitor(monitor, 1));
		taskResults.add(task);
		matchCount++;
	}

	public void done() {
		if (monitor != null && !monitor.isCanceled()) {
			// if the operation is cancelled, finish with the data that we
			// already have
			String matchesString = getFormattedMatchesString(matchCount);
			monitor.setTaskName(MessageFormat.format(DONE, new Object[] { matchesString }));
			monitor.done();
		}

		// Cut no longer used references because the collector might be re-used
		monitor = null;
	}

	protected String getFormattedMatchesString(int count) {
		if (count == 1) {
			return MATCH;
		}
		Object[] messageFormatArgs = { new Integer(count) };
		return MessageFormat.format(MATCHES, messageFormatArgs);
	}

	public IProgressMonitor getProgressMonitor() {
		return monitor;
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public Set<AbstractTask> getTasks() {
		return taskResults;
	}

	public void clear() {
		taskResults.clear();
	}

}
