/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.util.*;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
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
			org.eclipse.mylyn.tasks.core.data.TaskData newRepositoryTaskData = XPlannerRepositoryUtils.getNewRepositoryTaskData(
					taskRepository, userStoryData);

			assert (newRepositoryTaskData != null);
			assert (newRepositoryTaskData.isNew());
			assert (("" + userStoryData.getId()).equals(XPlannerRepositoryUtils.getAttributeValue(
					newRepositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID)));

			// make sure we have the right connector
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					taskRepository.getConnectorKind());
			assert (connector.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND));

			// post new task data
			String newTaskName = "new task";
			XPlannerRepositoryUtils.setAttributeValue(newRepositoryTaskData, TaskAttribute.SUMMARY, newTaskName);

			Set<TaskAttribute> changed = getAttributes(newRepositoryTaskData, new String[] { TaskAttribute.SUMMARY });
			RepositoryResponse returnValue = connector.getTaskDataHandler().postTaskData(taskRepository,
					newRepositoryTaskData, changed, null);

			// if new task, return value is new id -- make sure it's valid
			assert (returnValue != null);
			int id = Integer.valueOf(returnValue.getTaskId());
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
			assert (connector.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND));

			TaskRepository repository = XPlannerTestUtils.getRepository();
			assertTrue(repository != null);

			ITask repositoryTask = TasksUi.getRepositoryModel().createTask(repository, "" + testTaskData.getId());

			assertTrue(repositoryTask.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND));

			// post updated task data
			Double estTime = testTaskData.getAdjustedEstimatedHours();
			Double actTime;
			Double originalActHours = testTaskData.getActualHours();

			if (validHours) {
				actTime = estTime - 1;
			} else {
				actTime = testTaskData.getActualHours() - 1;
			}

			actTime = XPlannerRepositoryUtils.getHoursValue(XPlannerRepositoryUtils.formatSingleFractionHours(actTime));

			testTaskData.setActualHours(actTime);
			org.eclipse.mylyn.tasks.core.data.TaskData testRepositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
					repository.getRepositoryUrl(), testTaskData, repositoryTask.getTaskId());

			Set<TaskAttribute> changed = getAttributes(testRepositoryTaskData,
					new String[] { XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME });
			RepositoryResponse returnValue = connector.getTaskDataHandler().postTaskData(taskRepository,
					testRepositoryTaskData, changed, null);

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

	private Set<TaskAttribute> getAttributes(org.eclipse.mylyn.tasks.core.data.TaskData testRepositoryTaskData,
			String[] attributeNames) {

		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		for (String attributeName : attributeNames) {
			TaskAttribute attribute = testRepositoryTaskData.getRoot().getMappedAttribute(attributeName);
			if (attribute != null) {
				changed.add(attribute);
			}
		}

		return changed;
	}

	public void testUpdateActualTimeWithValidTime() {
		testUpdateActualTime(true);
	}

	public void testUpdateActualTimeWithInvalidTime() {
		testUpdateActualTime(false);
	}

	public void testUpdateActualTimeWithValidTimeEuropeanLocale() {
		Locale saveLocale = Locale.getDefault();
		Locale.setDefault(Locale.FRENCH);
		testUpdateActualTime(true);
		Locale.setDefault(saveLocale);

	}
}
