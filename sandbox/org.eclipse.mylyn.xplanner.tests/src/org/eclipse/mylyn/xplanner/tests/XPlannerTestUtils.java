/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.*;
import org.xplanner.soap.*;

/**
 * Requirements for tests:
 * 1.  XPlanner repository at SERVER_URL
 * 2.  Admin user with name USER, and password PASSWORD, which were the user/password used to install XPlanner,
 * otherwise don't have rights to create projects
 * 
 * @author Helen Bershadskaya
 *
 */
public class XPlannerTestUtils {
	private static final int DAY = 60 * 60 * 24 * 1000;  // one day in milliseconds	
	private final static String USER = "sysadmin";
	private final static String PASSWORD = "admin";
	public final static String SERVER_URL = "http://localhost:8080/xplanner";

	private final static String TEST_PROJECT_NAME = "UnitTestTestProject";
	private final static String TEST_ITERATION_NAME = "UnitTestTestIteration";
	private final static String TEST_USER_STORY_NAME = "UnitTestTestUserStory";
	private final static String TEST_TASK_NAME = "TestTask";
	
	public static TaskRepository getRepository() {
		TaskRepository repository;
		
		repository = TasksUiPlugin.getRepositoryManager().getRepository(SERVER_URL);
		if (repository == null) {
			repository = new TaskRepository(XPlannerMylynUIPlugin.REPOSITORY_KIND, SERVER_URL);
			repository.setAuthenticationCredentials(USER, PASSWORD);
			TasksUiPlugin.getRepositoryManager().addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
			TasksUiPlugin.getTaskListManager().resetTaskList();
		}
		
		return repository;
	}

	public static void removeRepository(TaskRepository repository) {
		TasksUiPlugin.getRepositoryManager().removeRepository(
			repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}
	
	public static XPlannerClient getXPlannerClient() throws CoreException {
		TaskRepository repository = getRepository();

		return XPlannerClientFacade.getDefault().getXPlannerClient(repository);
	}
	
	public static void clearTestData(XPlannerClient client) throws Exception {
		if (client != null) {
			ProjectData testProject = findTestProject(client);
			if (testProject != null) {
				client.removeProject(testProject.getId());
			}
		}
	}
	
	public static void setUpTestData(XPlannerClient client) throws Exception {
		if (client != null) {
			ProjectData testProject = getTestProject(client);
			IterationData testIteration = getTestIteration(client, testProject);
			UserStoryData testUserStory = getTestUserStory(client, testIteration);
			getTestTask(client, testUserStory);
		}
	}
	
	public static ProjectData findTestProject(XPlannerClient client) throws RemoteException {
		ProjectData testProject = null;
		
		ProjectData[] projects = client.getProjects();
		for (int i = 0; i < projects.length && testProject == null; i++) {
			if (TEST_PROJECT_NAME.equals(projects[i].getName())) {
				testProject = projects[i];
			}
		}
		
		return testProject;
	}
	
	private static ProjectData getTestProject(XPlannerClient client) throws RemoteException {
		ProjectData testProject = findTestProject(client);
		
		if (testProject == null) {
			testProject = new ProjectData();
			testProject.setName(TEST_PROJECT_NAME);
			testProject.setDescription(TEST_PROJECT_NAME);
			testProject = client.addProject(testProject);
		}
		
		return testProject;
	}

	public static IterationData findTestIteration(XPlannerClient client, ProjectData testProject) throws RemoteException {
		IterationData testIteration = null;

		IterationData[] iterations = client.getIterations(testProject.getId());
		if (iterations != null) {
			for (int i = 0; i < iterations.length && testIteration == null; i++) {
				if (TEST_ITERATION_NAME.equals(iterations[i].getName())) {
					testIteration = iterations[i];
				}
			}
		}
		
		return testIteration;
	}
	
	private static IterationData getTestIteration(XPlannerClient client, ProjectData project) throws RemoteException {
		IterationData testIteration = findTestIteration(client, project);
		
		if (testIteration == null) {
			testIteration = new IterationData();
			
    	testIteration.setProjectId(project.getId());
			
    	long now = new Date().getTime();		
			
			Date[] days = new Date[] {
					new Date(now),
					new Date(now + (5 * DAY)),
			};
//    	testIteration.setStatusKey("inactive");  //$NON-NLS-1$
    	
    	Calendar startDate = Calendar.getInstance();
    	startDate.setTime(days[0]);

    	Calendar endDate = Calendar.getInstance();
    	endDate.setTime(new Date(days[days.length-1].getTime() + DAY));

    	testIteration.setStartDate(startDate);
    	testIteration.setEndDate(endDate);
    	testIteration.setName(TEST_ITERATION_NAME);
    	testIteration.setDescription(TEST_ITERATION_NAME);
    	testIteration = client.addIteration(testIteration);
    	
			client.refreshDetails();
		}
		
		return testIteration;
	}

	public static UserStoryData findTestUserStory(XPlannerClient client, IterationData testIteration) throws RemoteException {
		UserStoryData testUserStory = null;
		
		UserStoryData[] userStories = client.getUserStories(testIteration.getId());
		if (userStories != null) {
			for (int i = 0; i < userStories.length && testUserStory == null; i++) {
				if (TEST_USER_STORY_NAME.equals(userStories[i].getName())) {
					testUserStory = userStories[i];
				}
			}
		}
		
		return testUserStory;
	}
	
	private static UserStoryData getTestUserStory(XPlannerClient client, IterationData iteration) throws RemoteException {
		UserStoryData testUserStory = findTestUserStory(client, iteration);
		
		if (testUserStory == null) {
			testUserStory = new UserStoryData();
			testUserStory.setIterationId(iteration.getId());
			testUserStory.setName(TEST_USER_STORY_NAME);
    	testUserStory.setDispositionName("planned");  //$NON-NLS-1$
			testUserStory.setDescription(TEST_USER_STORY_NAME);
			testUserStory.setEstimatedHours(0);
			testUserStory.setActualHours(0);
			testUserStory.setRemainingHours(0);
			testUserStory.setPriority(5);
		  
			testUserStory = client.addUserStory(testUserStory);
			
			client.refreshDetails();
		}
		
		return testUserStory;
	}

	public static TaskData findTestTask(XPlannerClient client, UserStoryData testUserStory) throws RemoteException {
		TaskData testTask = null;
		
		TaskData[] tasks = client.getTasks(testUserStory.getId());
		for (int i = 0; i < tasks.length && testTask == null; i++) {
			if (TEST_TASK_NAME.equals(tasks[i].getName())) {
				testTask = tasks[i];
			}
		}
		
		return testTask;
	}

	public static int getAdminId(XPlannerClient client) throws RemoteException {
		int adminId = -1;
		
		PersonData[] people = client.getPeople();
		for (int i = 0; i < people.length && adminId == -1; i++) {
			if (USER.equals(people[i].getUserId())) {
				adminId = people[i].getId();
			}
		}
		
		return adminId;
	}
	
	private static TaskData getTestTask(XPlannerClient client, UserStoryData userStory) throws RemoteException {
		TaskData testTask = findTestTask(client, userStory);
		
		if (testTask == null) {
			testTask = new TaskData();
			testTask.setStoryId(userStory.getId());
			testTask.setName(TEST_TASK_NAME);
			testTask.setDescription(TEST_TASK_NAME);
    	testTask.setType("Feature");  //$NON-NLS-1$
    	testTask.setEstimatedHours(24.0);
    	testTask.setActualHours(7.0);
    	testTask.setDispositionName("planned");  //$NON-NLS-1$
    	testTask.setAcceptorId(getAdminId(client));
    	
    	Calendar taskCreate = Calendar.getInstance();
    	taskCreate.setTime(new Date());
		  testTask.setCreatedDate(taskCreate);
		  
			testTask = client.addTask(testTask);
			
			client.refreshDetails();
		}
		
		return testTask;
	}

	public static TaskList getTaskList() {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		
		return taskList;
	}
	
	public static UserStoryData findTestUserStory(XPlannerClient client) throws RemoteException {
		UserStoryData testUserStory = null;
		
		ProjectData testProject = findTestProject(client);
		if (testProject != null) {
			IterationData testIteration = findTestIteration(client, testProject);
			if (testIteration != null) {
				testUserStory = findTestUserStory(client, testIteration);
			}
		}
		
		return testUserStory;
	}
	
	public static TaskData findTestTask(XPlannerClient client) throws RemoteException {
		TaskData testTask = null;

		UserStoryData testUserStory = findTestUserStory(client);
		if (testUserStory != null) {
			testTask = findTestTask(client, testUserStory);
		}
		
		return testTask;
	}
	
	/**
	 * setUpTestData() needs to be called before this method
	 */
	public static XPlannerTask getTestXPlannerTask(XPlannerClient client) throws Exception {
		TaskRepository repository = getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		
		TaskData testTask = findTestTask(client);
		AbstractTask task = connector.createTaskFromExistingId(repository, "" + testTask.getId(), new NullProgressMonitor());
		return (XPlannerTask) task;
	}

	/**
	 * setUpTestData() needs to be called before this method
	 */
	public static XPlannerTask getTestXPlannerUserStoryTask(XPlannerClient client) throws Exception {
		TaskRepository repository = getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		
		UserStoryData testUserStory = findTestUserStory(client);
		AbstractTask task = connector.createTaskFromExistingId(repository, "" + testUserStory.getId(), new NullProgressMonitor());
		return (XPlannerTask) task;
	}
	
}
