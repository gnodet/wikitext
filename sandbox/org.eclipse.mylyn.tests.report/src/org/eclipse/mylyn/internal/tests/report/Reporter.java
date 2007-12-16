/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tests.report;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tests.report.TestCaseResult.TestCaseResultType;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskCollector;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
class Reporter implements TestCaseVisitor {

	private final TracRepositoryConnector connector;

	private final AbstractTaskDataHandler taskDataHandler;

	private final TaskRepository repository;

	private final Build build;

	public Reporter(Build build, TaskRepository repository) {
		this.build = build;
		this.repository = repository;
		this.connector = new TracRepositoryConnector();
		this.taskDataHandler = connector.getTaskDataHandler();
	}

	private RepositoryTaskData createTaskData(TestCase testCase) throws CoreException {
		AbstractAttributeFactory attributeFactory = taskDataHandler.getAttributeFactory(repository.getUrl(), repository
				.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);

		RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, repository.getConnectorKind(),
				repository.getUrl(), "0");
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
		sb.append(repository.getUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());
		return sb.toString();
	}

	private String getTaskComment(TestCaseResult result) {
		StringBuilder sb = new StringBuilder();
		if (result.getResultType() == TestCaseResultType.FAILURE) {
			sb.append("Failure in build ");
		} else {
			sb.append("Error in build ");
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

		RepositoryTaskAttribute statusAttribute = taskData.getAttribute(RepositoryTaskAttribute.STATUS);
		if (testCase.getResult() != null) {
			if (TracTask.Status.CLOSED == TracTask.Status.fromStatus(statusAttribute.getValue())) {
				statusAttribute.setValue(TracTask.Status.REOPENED.toStatusString());
			}
			taskData.setNewComment(getTaskComment(testCase.getResult()));
		} else {
			if (TracTask.Status.CLOSED == TracTask.Status.fromStatus(statusAttribute.getValue())) {
				// test case succeeded and task is closed
				message(" nothing to do");
				return;
			} else {
				statusAttribute.setValue(TracTask.Status.CLOSED.toStatusString());
			}
		}

		message(" submitting task");
		taskDataHandler.postTaskData(repository, taskData, new NullProgressMonitor());
	}

	private void message(String string) {
		System.out.println(string);
	}

	@Override
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
		AbstractRepositoryQuery query = new TracRepositoryQuery(repository.getUrl(), queryUrl, "");
		ITaskCollector resultCollector = new QueryHitCollector(new ITaskFactory() {
			public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
				throw new UnsupportedOperationException();
			}
		});
		IStatus status = connector.performQuery(query, repository, new NullProgressMonitor(), resultCollector);
		if (status.isOK()) {
			try {
				handleResults(testCase, resultCollector.getTasks());
			} catch (CoreException e) {
				handleError(e.getStatus());
			}
		} else {
			handleError(status);
		}
	}

}
