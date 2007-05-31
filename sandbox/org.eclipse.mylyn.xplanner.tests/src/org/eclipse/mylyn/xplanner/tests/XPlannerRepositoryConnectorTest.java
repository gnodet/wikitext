/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.ui.XPlannerRepositoryConnector;
import org.eclipse.mylar.xplanner.ui.XPlannerTask;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryConnectorTest extends TestCase {
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

	public void testCreateTaskFromExistingKeyForUserStory() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		UserStoryData testUserStory = XPlannerTestUtils.findTestUserStory(server);

		assertTrue(testUserStory != null);
		
		AbstractRepositoryTask repositoryTask = 
			connector.createTaskFromExistingKey(repository, "" + testUserStory.getId());
		
		assertTrue(repositoryTask instanceof XPlannerTask);
		assertTrue(((XPlannerTask)repositoryTask).getSummary().equals(testUserStory.getName()));
	}

	public void testCreateTaskFromExistingKeyForTask() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		TaskData testTask = XPlannerTestUtils.findTestTask(server);

		assertTrue(testTask != null);
		
		AbstractRepositoryTask repositoryTask = 
			connector.createTaskFromExistingKey(repository, "" + testTask.getId());
		
		assertTrue(repositoryTask instanceof XPlannerTask);
		assertTrue(((XPlannerTask)repositoryTask).getSummary().equals(testTask.getName()));
	}

	public void testUpdateTaskDetailsCompleted() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		
		assertTrue(connector instanceof XPlannerRepositoryConnector);
		XPlannerRepositoryConnector xplannerConnector = (XPlannerRepositoryConnector) connector;
		TaskData testTask = XPlannerTestUtils.findTestTask(server);
		XPlannerTask repositoryTask = XPlannerTestUtils.getTestXPlannerTask(server);

		assertTrue(testTask != null);
		assertTrue(repositoryTask != null);

		// update repository task with details
		// save previous completion state
		boolean originalCompleted = testTask.isCompleted();
		
		// mark testTask as completed
		testTask.setCompleted(true);
		server.update(testTask);
		
		xplannerConnector.updateTaskDetails(repository.getUrl(), repositoryTask, testTask, false);
		assertTrue(repositoryTask.isCompleted());
		
		//restore testTask's completion state
		testTask.setCompleted(originalCompleted);
		server.update(testTask);
	}

}
