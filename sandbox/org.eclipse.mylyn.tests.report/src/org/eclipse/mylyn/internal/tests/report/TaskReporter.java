/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tests.report;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.ITaskFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.QueryHitCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryOperation;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tests.report.TestCaseResult.TestCaseResultType;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracRemoteException;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
class TaskReporter implements TestCaseVisitor {

	private static final RepositoryOperation OPERATION_REOPEN;

	private static final RepositoryOperation OPERATION_RESOLVE;

	private static final int MAX_ERROR_TIMEOUT = 3;

	static {
		OPERATION_REOPEN = new RepositoryOperation("reopen", "");

		OPERATION_RESOLVE = new RepositoryOperation("resolve", "");
		OPERATION_RESOLVE.setOptionSelection("fixed");
	}

	private final TracRepositoryConnector connector;

	private final AbstractTaskDataHandler taskDataHandler;

	private final TaskRepository repository;

	private final Build build;

	private final TaskReporterStatistics statistics;

	private int timeoutErrorCount;

	public TaskReporter(Build build, TaskRepository repository) {
		this.build = build;
		this.repository = repository;
		this.connector = new TracRepositoryConnector();
		this.taskDataHandler = connector.getLegacyTaskDataHandler();
		this.statistics = new TaskReporterStatistics();
	}

	private RepositoryTaskData createTaskData(TestCase testCase) throws CoreException {
		AbstractAttributeFactory attributeFactory = taskDataHandler.getAttributeFactory(repository.getRepositoryUrl(),
				repository.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);

		RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, repository.getConnectorKind(),
				repository.getRepositoryUrl(), "0");
		taskData.setNew(true);
		taskDataHandler.initializeTaskData(repository, taskData, new NullProgressMonitor());
		taskData.setSummary(getTaskSummary(testCase));
		taskData.setDescription(getTaskDescription(testCase));
		return taskData;
	}

	private String getQueryUrl(TestCase testCase) {
		TracSearch search = new TracSearch();
		search.addFilter("summary", getTaskSummary(testCase));
		search.addFilter("description", getTaskDescription(testCase));

		StringBuilder sb = new StringBuilder();
		sb.append(repository.getRepositoryUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());
		return sb.toString();
	}

	public TaskReporterStatistics getStatistics() {
		return statistics;
	}

	private String getTaskComment(TestCaseResult result) {
		StringBuilder sb = new StringBuilder();
		if (result.getResultType() == TestCaseResultType.FAILURE) {
			sb.append("Failure since build ");
		} else {
			sb.append("Error since build ");
		}
		sb.append(build.getId());
		sb.append(": ");
		if (result.getMessage() != null) {
			sb.append(result.getMessage());
		}
		sb.append("\n\n");
		sb.append(result.getStackTrace());
		return sb.toString();
	}

	private String getTaskDescription(TestCase testCase) {
		return "Test results for " + testCase.getClassName() + "." + testCase.getTestName() + "()";
	}

	private String getTaskSummary(TestCase testCase) {
		return testCase.getShortClassName() + ": " + testCase.getTestName();
	}

	private void handleError(IStatus status) {
		System.err.println(status);
		if (status.getException() != null) {
			status.getException().printStackTrace();
		}
		System.exit(1);
	}

	private void handleResults(TestCase testCase, Set<AbstractTask> tasks) throws CoreException {
		String id;
		RepositoryTaskData taskData = null;
		if (tasks.isEmpty()) {
			if (testCase.getResult() != null) {
				message(" creating task");
				taskData = createTaskData(testCase);
				id = taskDataHandler.postTaskData(repository, taskData, new NullProgressMonitor());
			} else {
				statistics.tasksUntouched++;
				// test case succeeded and task does not exist
				message(" nothing to do");
				return;
			}
		} else {
			AbstractTask task = tasks.iterator().next();
			id = task.getTaskId();
		}

		message(" downloading task");
		taskData = taskDataHandler.getTaskData(repository, id, new NullProgressMonitor());

		String status = taskData.getAttribute(RepositoryTaskAttribute.STATUS).getValue();
		if (testCase.getResult() != null) {
			if (TracTask.Status.CLOSED == TracTask.Status.fromStatus(status)) {
				taskData.setSelectedOperation(OPERATION_REOPEN);
			}
			if (matchesLastComment(taskData, testCase.getResult())) {
				statistics.tasksStackTraceUpToDate++;
				message(" stack trace is up to date");
			} else {
				statistics.tasksReopened++;
				message(" adding new stack trace");
				taskData.setNewComment(getTaskComment(testCase.getResult()));
			}
		} else {
			if (TracTask.Status.CLOSED == TracTask.Status.fromStatus(status)) {
				statistics.tasksUntouched++;
				// test case succeeded and task is closed
				message(" nothing to do, task is alread closed");
				return;
			} else {
				statistics.tasksResolved++;
				message(" resolving task");
				taskData.setNewComment("Fixed in build " + build.getId());
				taskData.setSelectedOperation(OPERATION_RESOLVE);
			}
		}

		message(" submitting task");
		taskDataHandler.postTaskData(repository, taskData, new NullProgressMonitor());
	}

	private boolean matchesLastComment(RepositoryTaskData taskData, TestCaseResult result) {
		List<TaskComment> comments = taskData.getComments();
		if (comments != null && !comments.isEmpty()) {
			TaskComment lastComment = comments.get(comments.size() - 1);
			if (lastComment.getText().endsWith(result.getStackTrace())) {
				return true;
			}
		}
		return false;
	}

	private void message(String string) {
		System.out.println(string);
	}

	public void visit(TestCase testCase) {
		// ITracClient client =
		// connector.getClientManager().getRepository(repository);
		// if (!client.hasAttributes()) {
		// message("Updating repository configuration: " + repository.getUrl());
		// try {
		// client.updateAttributes(new NullProgressMonitor(), false);
		// } catch (TracException e) {
		// handleError(TracCorePlugin.toStatus(e, repository));
		// }
		// }
		//
		message("Processing: " + testCase.getClassName() + "#" + testCase.getTestName());
		String queryUrl = getQueryUrl(testCase);
		AbstractRepositoryQuery query = new TracRepositoryQuery(repository.getRepositoryUrl(), queryUrl, "");
		QueryHitCollector resultCollector = new QueryHitCollector(new ITaskFactory() {
			public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
				throw new UnsupportedOperationException();
			}
		});
		IStatus status = connector.performQuery(repository, query, resultCollector, null, new NullProgressMonitor());
		if (status.isOK()) {
			try {
				handleResults(testCase, resultCollector.getTasks());
			} catch (CoreException e) {
				if (e.getStatus().getException() instanceof TracRemoteException) {
					String message = e.getStatus().getException().getMessage();
					if (message != null && message.contains("timeout")) {
						timeoutErrorCount++;
						if (timeoutErrorCount <= MAX_ERROR_TIMEOUT) {
							// ignore a few timeouts
							statistics.ignoredErrors++;
							message(" timeout (" + timeoutErrorCount + "/" + MAX_ERROR_TIMEOUT + ")");
							return;
						}
					}
				}
				handleError(e.getStatus());
			}
		} else {
			handleError(status);
		}
	}

}
