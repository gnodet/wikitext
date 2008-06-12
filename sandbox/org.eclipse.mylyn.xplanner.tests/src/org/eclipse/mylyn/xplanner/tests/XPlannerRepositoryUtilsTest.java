/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryUtilsTest extends TestCase {

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

	public void testSetupTaskAttributes() {
		try {
			org.xplanner.soap.TaskData taskData = XPlannerTestUtils.findTestTask(client);
			ITask task = XPlannerTestUtils.getTestXPlannerTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			XPlannerRepositoryUtils.setupTaskAttributes(taskData, repositoryTaskData);

			assert (taskData.getDescription().equals(XPlannerRepositoryUtils.getDescription(repositoryTaskData)));
		} catch (Exception e) {
			fail("could not set up task attributes");
		}
	}

	public void testGetNewRepositoryTaskData() {
		try {
			UserStoryData userStoryData = XPlannerTestUtils.findTestUserStory(client);
			TaskRepository taskRepository = XPlannerTestUtils.getRepository();
			org.eclipse.mylyn.tasks.core.data.TaskData newRepositoryTaskData = XPlannerRepositoryUtils.getNewRepositoryTaskData(
					taskRepository, userStoryData);
			assert (newRepositoryTaskData != null);
			assert (newRepositoryTaskData.isNew());
			assert (("" + userStoryData.getId()).equals(XPlannerRepositoryUtils.getAttributeValue(
					newRepositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID)));
		} catch (Exception e) {
			fail("could not set up task attributes: " + e.getMessage());
		}
	}

	public void testSetupUserStoryAttributes() {
		try {
			UserStoryData userStory = XPlannerTestUtils.findTestUserStory(client);
			ITask task = XPlannerTestUtils.getTestXPlannerUserStoryTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			XPlannerRepositoryUtils.setupUserStoryAttributes(userStory, repositoryTaskData);

			assert (userStory.getName().equals(XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData,
					XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_NAME)));
		} catch (Exception e) {
			fail("could not set up user story attributes");
		}
	}

	public void testIsCompleted() {
		try {
			ITask task = XPlannerTestUtils.getTestXPlannerTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			assert (!XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
		} catch (Exception e) {
			fail("Coule not check if task is completed");
		}
	}

	public void testValidateRepository() {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		try {
			XPlannerRepositoryUtils.validateRepository(repository);
		} catch (CoreException e) {
			fail("Could not validate repository");
		}
	}

	public void testEnsureNewTaskDataValid() {
		TaskData taskData = new TaskData();
		XPlannerRepositoryUtils.ensureTaskDataValid(taskData);
		assert (taskData.getName() != null && taskData.getName().length() > 0);
		assert (taskData.getDispositionName() != null && taskData.getDispositionName().length() > 0);
	}
}
