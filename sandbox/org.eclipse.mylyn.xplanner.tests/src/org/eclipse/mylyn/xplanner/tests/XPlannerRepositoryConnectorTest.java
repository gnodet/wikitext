/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryConnector;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryConnectorTest extends TestCase {
	private static XPlannerClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (client == null) { // only create data once per run
			client = XPlannerTestUtils.getXPlannerClient();
			XPlannerTestUtils.clearTestData(client);
			XPlannerTestUtils.setUpTestData(client);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateTaskFromExistingKeyForUserStory() throws Exception {
		getTestRepositoryUserStory();
	}

	@SuppressWarnings("null")
	private ITask getTestRepositoryTask() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		TaskData testTask = XPlannerTestUtils.findTestTask(client);

		assertTrue(testTask != null);

		ITask repositoryTask = TasksUi.getRepositoryModel().createTask(repository, "" + testTask.getId());
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = connector.getTaskData(repository, ""
				+ testTask.getId(), null);
		connector.updateTaskFromTaskData(repository, repositoryTask, repositoryTaskData);

		assertTrue(repositoryTask.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND));
		assertTrue(repositoryTask.getSummary().equals(testTask.getName()));

		return repositoryTask;
	}

	@SuppressWarnings("null")
	private ITask getTestRepositoryUserStory() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		UserStoryData testUserStory = XPlannerTestUtils.findTestUserStory(client);

		assertTrue(testUserStory != null);

		ITask repositoryTask = TasksUi.getRepositoryModel().createTask(repository, "" + testUserStory.getId());
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());
		org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = connector.getTaskData(repository, ""
				+ testUserStory.getId(), null);
		connector.updateTaskFromTaskData(repository, repositoryTask, repositoryTaskData);

		assertTrue(repositoryTask.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND));
		assertTrue(repositoryTask.getSummary().equals(testUserStory.getName()));

		return repositoryTask;
	}

	public void testCreateTaskFromExistingKeyForTask() throws Exception {
		getTestRepositoryTask();
	}

	@SuppressWarnings("null")
	public void testUpdateTaskFromTaskDataCompleted() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		assertTrue(connector instanceof XPlannerRepositoryConnector);
		XPlannerRepositoryConnector xplannerConnector = (XPlannerRepositoryConnector) connector;
		TaskData testTask = XPlannerTestUtils.findTestTask(client);
		ITask repositoryTask = XPlannerTestUtils.getTestXPlannerTask(client);

		assertTrue(testTask != null);
		assertTrue(repositoryTask != null);

		// update repository task with details
		// save previous completion state
		boolean originalCompleted = testTask.isCompleted();

		// mark testTask as completed
		testTask.setCompleted(true);
		client.update(testTask);

		org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
				repository.getRepositoryUrl(), testTask, repositoryTask.getTaskId());
		assertNotNull(repositoryTaskData);

		xplannerConnector.updateTaskFromTaskData(repository, repositoryTask, repositoryTaskData);

		assertTrue(repositoryTask.isCompleted());

		//restore testTask's completion state
		testTask.setCompleted(originalCompleted);
		client.update(testTask);
	}

	public void testMarkStaleTasksNoStaleTasks() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		assertTrue(connector instanceof XPlannerRepositoryConnector);

		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());
		ITask repositoryTask = null;

		if (tasks.size() == 0) {
			testCreateTaskFromExistingKeyForTask();
			repositoryTask = XPlannerTestUtils.getTestXPlannerTask(client);
			TasksUiPlugin.getTaskList().addTask(repositoryTask);
			tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());
		}
		setSyncTimeStamp(repository, tasks);

		String goodUrl = repository.getRepositoryUrl();
		SynchronizationSession event = new SynchronizationSession();
		try {
			repository.setRepositoryUrl("http://localhost");

			event.setTasks(tasks);
			event.setNeedsPerformQueries(true);
			event.setTaskRepository(repository);
			event.setFullSynchronization(true);
			connector.preSynchronization(event, null);
		} catch (CoreException e) {
			assertTrue(e.getMessage() != null && e.getMessage().contains("Connection error"));
		} finally {
			repository.setRepositoryUrl(goodUrl);
		}

		assertTrue(event.needsPerformQueries());
		for (ITask task : tasks) {
			if (task instanceof AbstractTask) {
				SynchronizationState taskSyncState = ((AbstractTask) task).getSynchronizationState();
				assertTrue(taskSyncState.equals(SynchronizationState.SYNCHRONIZED));
			}
		}

		// cleanup
		if (repositoryTask != null) {
			TasksUiPlugin.getTaskList().deleteTask(repositoryTask);
		}

	}

	private void setSyncTimeStamp(TaskRepository repository, Set<ITask> tasks) throws Exception {
		if (tasks.size() == 0) {
			return;
		}

		Date date = tasks.iterator().next().getCreationDate();
		DateFormat timeDateFormat = new SimpleDateFormat(XPlannerAttributeMapper.TIME_DATE_FORMAT_STRING);
		String timeStamp = timeDateFormat.format(date);
		for (ITask task : tasks) {
			if (task instanceof AbstractTask) {
				TaskData taskData = client.getTask(Integer.valueOf(task.getTaskId()).intValue());
				if (taskData != null) {
					Calendar lastUpdateTime = new GregorianCalendar();
					lastUpdateTime.setTime(date);
					taskData.setLastUpdateTime(lastUpdateTime);
					client.update(taskData);
				}
				((AbstractTask) task).setSynchronizationState(SynchronizationState.SYNCHRONIZED);
			}
		}

		repository.setSynchronizationTimeStamp(timeStamp);
	}

	@SuppressWarnings("null")
	public void testChangedSinceLastSyncWithBadConnection() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		assertTrue(connector instanceof XPlannerRepositoryConnector);
		XPlannerRepositoryConnector xplannerConnector = (XPlannerRepositoryConnector) connector;

		// make bad url
		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());
		setSyncTimeStamp(repository, tasks);

		String goodUrl = repository.getRepositoryUrl();
		repository.setRepositoryUrl(XPlannerTestUtils.BAD_REPOSITORY_LOCATION);
		try {
			Set<ITask> changedTasks = xplannerConnector.getChangedSinceLastSync(repository, tasks);
			assertTrue(changedTasks != null);
			assertTrue(changedTasks.size() == 0);
		} catch (CoreException e) {
			assertTrue(e.getMessage() != null && e.getMessage().contains("Error connecting"));
		} finally {
			repository.setRepositoryUrl(goodUrl);
		}
	}

	public void testPostSynchronizationNoChanges() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		assertTrue(connector instanceof XPlannerRepositoryConnector);

		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());

		ITask repositoryTask = getTestRepositoryTask();

		TasksUiPlugin.getTaskList().addTask(repositoryTask);
		tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());

		SynchronizationSession event = new SynchronizationSession(TasksUiPlugin.getTaskDataManager());

		DateFormat timeDateFormat = new SimpleDateFormat(XPlannerAttributeMapper.TIME_DATE_FORMAT_STRING);
		String synchronizationTimeStamp = timeDateFormat.format(Calendar.getInstance().getTime());
		repository.setSynchronizationTimeStamp(synchronizationTimeStamp);

		event.setChangedTasks(tasks);
		event.setNeedsPerformQueries(false);
		event.setTaskRepository(repository);
		event.setFullSynchronization(true);
		connector.postSynchronization(event, new NullProgressMonitor());

		assertTrue(!event.needsPerformQueries());
		assertTrue(repository.getSynchronizationTimeStamp().equals(synchronizationTimeStamp));

		// cleanup
		if (repositoryTask != null) {
			TasksUiPlugin.getTaskList().deleteTask(repositoryTask);
		}
	}

	public void testPostSynchronizationHaveChanges() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		assertTrue(connector instanceof XPlannerRepositoryConnector);

		Set<ITask> tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());

		ITask repositoryTask = getTestRepositoryTask();
		TasksUiPlugin.getTaskList().addTask(repositoryTask);
		tasks = TasksUiPlugin.getTaskList().getTasks(repository.getRepositoryUrl());
		setSyncTimeStamp(repository, tasks);

		// change task description
		TaskData testTask = XPlannerTestUtils.findTestTask(client);
		String originalDescription = testTask.getDescription();
		testTask.setDescription("new test task description");
		client.update(testTask);

		SynchronizationSession event = new SynchronizationSession();
		DateFormat timeDateFormat = new SimpleDateFormat(XPlannerAttributeMapper.TIME_DATE_FORMAT_STRING);

		String initialSynchronizationTimeStampString = repository.getSynchronizationTimeStamp();
		Date initialSynchronizationTimeStamp = timeDateFormat.parse(initialSynchronizationTimeStampString);

		event.setChangedTasks(tasks);
		event.setTaskRepository(repository);
		event.setFullSynchronization(true);
		connector.postSynchronization(event, new NullProgressMonitor());

		String finalSynchronizationTimeStampString = repository.getSynchronizationTimeStamp();
		Date finalSynchronizationTimeStamp = timeDateFormat.parse(finalSynchronizationTimeStampString);

		assertTrue(finalSynchronizationTimeStamp.equals(repositoryTask.getModificationDate()));
		assertTrue(finalSynchronizationTimeStamp.after(initialSynchronizationTimeStamp));

		// cleanup
		if (repositoryTask != null) {
			TasksUiPlugin.getTaskList().deleteTask(repositoryTask);
			testTask.setDescription(originalDescription);
			client.update(testTask);
		}
	}

	public void testGetTaskRelationsNoRelations() throws Exception {
		ITask repositoryTask = getTestRepositoryTask();

		if (repositoryTask != null) {
			String subTasksAttributeValue = repositoryTask.getAttribute(XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_IDS);
			assertNull(subTasksAttributeValue);
		}
	}

	public void testGetTaskRelationsHaveRelations() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		ITask repositoryTask = getTestRepositoryUserStory();

		if (repositoryTask != null) {
			org.eclipse.mylyn.tasks.core.data.TaskData taskData = connector.getTaskData(repository,
					repositoryTask.getTaskId(), null);
			assertNotNull(taskData);
			TaskAttribute subTasksAttribute = taskData.getRoot().getAttribute(
					XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_IDS);
			assertNotNull(subTasksAttribute);
			List<String> values = subTasksAttribute.getValues();
			assertTrue(!values.isEmpty());
		}
	}
}
