/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.ui.XPlannerAttributeFactory;
import org.eclipse.mylar.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.mylar.xplanner.ui.XPlannerTask;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryUtilsTest extends TestCase {

	private static XPlannerServer server;
	
	protected void setUp() throws Exception {
		super.setUp();
		if (server == null) { // only create data once per run
			server = XPlannerTestUtils.getXPlannerServer();
			XPlannerTestUtils.clearTestData(server);
			XPlannerTestUtils.setUpTestData(server);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetupTaskAttributes() {
		try {
			TaskData taskData =  XPlannerTestUtils.findTestTask(server);
			XPlannerTask task = XPlannerTestUtils.getTestXPlannerTask(server);
			RepositoryTaskData repositoryTaskData = task.getTaskData();
			XPlannerRepositoryUtils.setupTaskAttributes(taskData, repositoryTaskData);
			
			assert(taskData.getDescription().equals(
				repositoryTaskData.getAttributeValue(RepositoryTaskAttribute.DESCRIPTION)));
		}
		catch (Exception e) {
			fail("could not set up task attributes");
		}
	}

	public void testSetupUserStoryAttributes() {
		try {
			UserStoryData userStory =  XPlannerTestUtils.findTestUserStory(server);
			XPlannerTask task = XPlannerTestUtils.getTestXPlannerUserStoryTask(server);
			RepositoryTaskData repositoryTaskData = task.getTaskData();
			XPlannerRepositoryUtils.setupUserStoryAttributes(userStory, repositoryTaskData);
			
			assert(userStory.getName().equals(
				repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_NAME)));
		}
		catch (Exception e) {
			fail("could not set up user story attributes");
		}
	}

	public void testIsCompleted() {
		XPlannerTask xplannerTask;
		try {
			xplannerTask = XPlannerTestUtils.getTestXPlannerTask(server);
			RepositoryTaskData repositoryTaskData = xplannerTask.getTaskData();
			assert(!XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
		}
		catch (Exception e) {
			fail("Coule not check if task is completed");
		}
	}

	public void testValidateRepository() {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		try {
			XPlannerRepositoryUtils.validateRepository(repository);
		}
		catch (CoreException e) {
			fail("Could not validate repository");
		}
	}

}
