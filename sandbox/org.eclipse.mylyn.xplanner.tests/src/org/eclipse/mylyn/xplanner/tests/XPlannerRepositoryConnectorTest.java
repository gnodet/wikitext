/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryConnector;
import org.eclipse.mylyn.xplanner.ui.XPlannerTask;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryConnectorTest extends TestCase {
	private static XPlannerClient client;
	
	protected void setUp() throws Exception {
		super.setUp();
		if (client == null) { // only create data once per run
			client = XPlannerTestUtils.getXPlannerClient();
			XPlannerTestUtils.clearTestData(client);
			XPlannerTestUtils.setUpTestData(client);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateTaskFromExistingKeyForUserStory() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		UserStoryData testUserStory = XPlannerTestUtils.findTestUserStory(client);

		assertTrue(testUserStory != null);
		
		AbstractTask repositoryTask = 
			connector.createTaskFromExistingId(repository, "" + testUserStory.getId(), new NullProgressMonitor());
		
		assertTrue(repositoryTask instanceof XPlannerTask);
		assertTrue(((XPlannerTask)repositoryTask).getSummary().equals(testUserStory.getName()));
	}

	public void testCreateTaskFromExistingKeyForTask() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		TaskData testTask = XPlannerTestUtils.findTestTask(client);

		assertTrue(testTask != null);
		
		AbstractTask repositoryTask = 
			connector.createTaskFromExistingId(repository, "" + testTask.getId(), new NullProgressMonitor());
		
		assertTrue(repositoryTask instanceof XPlannerTask);
		assertTrue(((XPlannerTask)repositoryTask).getSummary().equals(testTask.getName()));
	}

	public void testUpdateTaskDetailsCompleted() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		
		assertTrue(connector instanceof XPlannerRepositoryConnector);
		XPlannerRepositoryConnector xplannerConnector = (XPlannerRepositoryConnector) connector;
		TaskData testTask = XPlannerTestUtils.findTestTask(client);
		XPlannerTask repositoryTask = XPlannerTestUtils.getTestXPlannerTask(client);

		assertTrue(testTask != null);
		assertTrue(repositoryTask != null);

		// update repository task with details
		// save previous completion state
		boolean originalCompleted = testTask.isCompleted();
		
		// mark testTask as completed
		testTask.setCompleted(true);
		client.update(testTask);
		
		xplannerConnector.updateTaskDetails(repository.getUrl(), repositoryTask, testTask, false);
		assertTrue(repositoryTask.isCompleted());
		
		//restore testTask's completion state
		testTask.setCompleted(originalCompleted);
		client.update(testTask);
	}

}
