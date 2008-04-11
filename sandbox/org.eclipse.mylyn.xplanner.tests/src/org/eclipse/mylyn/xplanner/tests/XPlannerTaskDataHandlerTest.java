/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeFactory;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.mylyn.xplanner.ui.XPlannerTask;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerTaskDataHandlerTest extends TestCase {

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

	public void testPostNewTaskDataChangesToRepository() {
		try {
			// get new task data
			UserStoryData userStoryData = XPlannerTestUtils.findTestUserStory(client);
			TaskRepository taskRepository = XPlannerTestUtils.getRepository();
			RepositoryTaskData newRepositoryTaskData = XPlannerRepositoryUtils.getNewRepositoryTaskData(taskRepository,
					userStoryData);

			assert (newRepositoryTaskData != null);
			assert (newRepositoryTaskData.isNew());
			assert (("" + userStoryData.getId()).equals(newRepositoryTaskData.getAttribute(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_ID)));

			// make sure we have the right connector
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					taskRepository.getConnectorKind());
			assert (connector.getConnectorKind().equals(XPlannerMylynUIPlugin.REPOSITORY_KIND));

			// post new task data
			String newTaskName = "new task";
			newRepositoryTaskData.setSummary(newTaskName);
			String returnValue = connector.getTaskDataHandler().postTaskData(taskRepository, newRepositoryTaskData,
					null);

			// if new task, return value is new id -- make sure it's valid
			assert (returnValue != null);
			int id = Integer.valueOf(returnValue).intValue();
			assert (id > 0);
			TaskData taskData = client.getTask(id);
			assert (taskData != null);
			assert (newTaskName.equals(taskData.getName()));

			// need to make sure user story did not get corrupted for complete test
			UserStoryData userStory = client.getUserStory(taskData.getStoryId());
			assert (userStory != null);

			// ensure saved user name in task
			int currentPersonId = client.getCurrentPersonId();
			assert (taskData.getAcceptorId() == currentPersonId);
		} catch (Exception e) {
			fail("could not set up task attributes: " + e.getMessage());
		}
	}

	@SuppressWarnings("null")
	private void testUpdateActualTime(boolean validHours) {
		try {
			// get test task
			TaskData testTaskData = XPlannerTestUtils.findTestTask(client);
			TaskRepository taskRepository = XPlannerTestUtils.getRepository();

			// make sure we have the right connector
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					taskRepository.getConnectorKind());
			assert (connector.getConnectorKind().equals(XPlannerMylynUIPlugin.REPOSITORY_KIND));

			TaskRepository repository = XPlannerTestUtils.getRepository();
			assertTrue(repository != null);

			AbstractTask repositoryTask = TasksUiUtil.createTask(repository, "" + testTaskData.getId(), null);

			assertTrue(repositoryTask instanceof XPlannerTask);

			// post updated task data
			Double estTime = testTaskData.getAdjustedEstimatedHours();
			Double actTime;
			Double originalActHours = testTaskData.getActualHours();

			if (validHours) {
				actTime = estTime - 1;
			} else {
				actTime = testTaskData.getActualHours() - 1;
			}

			testTaskData.setActualHours(actTime);
			RepositoryTaskData testRepositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
					repository.getRepositoryUrl(), testTaskData, repositoryTask.getTaskId());

			String returnValue = connector.getTaskDataHandler().postTaskData(taskRepository, testRepositoryTaskData,
					null);

			// if new task, return value is new id -- make sure it's valid
			assert (returnValue == null);
			// try to set to time that's lower than previous
			TaskData taskData = client.getTask(testTaskData.getId());
			assert (taskData != null);
			if (validHours) {
				assert (taskData.getActualHours() == actTime);
			} else {
				assert (taskData.getActualHours() == originalActHours);
			}
		} catch (Exception e) {
			fail("could not set up task attributes: " + e.getMessage());
		}
	}

	public void testUpdateActualTimeWithValidTime() {
		testUpdateActualTime(true);
	}

	public void testUpdateActualTimeWithInvalidTime() {
		testUpdateActualTime(false);
	}

}
