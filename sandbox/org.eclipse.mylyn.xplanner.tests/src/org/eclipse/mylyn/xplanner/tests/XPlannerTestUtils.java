/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.tests;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.ui.XPlannerMylarUIPlugin;
import org.eclipse.mylar.xplanner.ui.XPlannerServerFacade;
import org.eclipse.mylar.xplanner.ui.XPlannerTask;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * Requirements for tests:
 * 1.  XPlanner repository at SERVER_URL
 * 2.  Admin user with name USER, and password PASSWORD, which were the user/password used to install XPlanner,
 * otherwise don't have rights to create projects
 * 
 * @author hbershadskaya
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
			repository = new TaskRepository(XPlannerMylarUIPlugin.REPOSITORY_KIND, SERVER_URL);
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
	
	public static XPlannerServer getXPlannerServer() throws CoreException {
		TaskRepository repository = getRepository();

		return XPlannerServerFacade.getDefault().getXPlannerServer(repository);
	}
	
	public static void clearTestData(XPlannerServer server) throws Exception {
		if (server != null) {
			ProjectData testProject = findTestProject(server);
			if (testProject != null) {
				server.removeProject(testProject.getId());
			}
		}
	}
	
	public static void setUpTestData(XPlannerServer server) throws Exception {
		if (server != null) {
			ProjectData testProject = getTestProject(server);
			IterationData testIteration = getTestIteration(server, testProject);
			UserStoryData testUserStory = getTestUserStory(server, testIteration);
			getTestTask(server, testUserStory);
		}
	}
	
	public static ProjectData findTestProject(XPlannerServer server) throws RemoteException {
		ProjectData testProject = null;
		
		ProjectData[] projects = server.getProjects();
		for (int i = 0; i < projects.length && testProject == null; i++) {
			if (TEST_PROJECT_NAME.equals(projects[i].getName())) {
				testProject = projects[i];
			}
		}
		
		return testProject;
	}
	
	private static ProjectData getTestProject(XPlannerServer server) throws RemoteException {
		ProjectData testProject = findTestProject(server);
		
		if (testProject == null) {
			testProject = new ProjectData();
			testProject.setName(TEST_PROJECT_NAME);
			testProject.setDescription(TEST_PROJECT_NAME);
			testProject = server.addProject(testProject);
		}
		
		return testProject;
	}

	public static IterationData findTestIteration(XPlannerServer server, ProjectData testProject) throws RemoteException {
		IterationData testIteration = null;

		IterationData[] iterations = server.getIterations(testProject.getId());
		if (iterations != null) {
			for (int i = 0; i < iterations.length && testIteration == null; i++) {
				if (TEST_ITERATION_NAME.equals(iterations[i].getName())) {
					testIteration = iterations[i];
				}
			}
		}
		
		return testIteration;
	}
	
	private static IterationData getTestIteration(XPlannerServer server, ProjectData project) throws RemoteException {
		IterationData testIteration = findTestIteration(server, project);
		
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
    	testIteration = server.addIteration(testIteration);
    	
			server.refreshDetails();
		}
		
		return testIteration;
	}

	public static UserStoryData findTestUserStory(XPlannerServer server, IterationData testIteration) throws RemoteException {
		UserStoryData testUserStory = null;
		
		UserStoryData[] userStories = server.getUserStories(testIteration.getId());
		if (userStories != null) {
			for (int i = 0; i < userStories.length && testUserStory == null; i++) {
				if (TEST_USER_STORY_NAME.equals(userStories[i].getName())) {
					testUserStory = userStories[i];
				}
			}
		}
		
		return testUserStory;
	}
	
	private static UserStoryData getTestUserStory(XPlannerServer server, IterationData iteration) throws RemoteException {
		UserStoryData testUserStory = findTestUserStory(server, iteration);
		
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
		  
			testUserStory = server.addUserStory(testUserStory);
			
			server.refreshDetails();
		}
		
		return testUserStory;
	}

	public static TaskData findTestTask(XPlannerServer server, UserStoryData testUserStory) throws RemoteException {
		TaskData testTask = null;
		
		TaskData[] tasks = server.getTasks(testUserStory.getId());
		for (int i = 0; i < tasks.length && testTask == null; i++) {
			if (TEST_TASK_NAME.equals(tasks[i].getName())) {
				testTask = tasks[i];
			}
		}
		
		return testTask;
	}

	public static int getAdminId(XPlannerServer server) throws RemoteException {
		int adminId = -1;
		
		PersonData[] people = server.getPeople();
		for (int i = 0; i < people.length && adminId == -1; i++) {
			if (USER.equals(people[i].getUserId())) {
				adminId = people[i].getId();
			}
		}
		
		return adminId;
	}
	
	private static TaskData getTestTask(XPlannerServer server, UserStoryData userStory) throws RemoteException {
		TaskData testTask = findTestTask(server, userStory);
		
		if (testTask == null) {
			testTask = new TaskData();
			testTask.setStoryId(userStory.getId());
			testTask.setName(TEST_TASK_NAME);
			testTask.setDescription(TEST_TASK_NAME);
    	testTask.setType("Feature");  //$NON-NLS-1$
    	testTask.setEstimatedHours(24.0);
    	testTask.setActualHours(7.0);
    	testTask.setDispositionName("planned");  //$NON-NLS-1$
    	testTask.setAcceptorId(getAdminId(server));
    	
    	Calendar taskCreate = Calendar.getInstance();
    	taskCreate.setTime(new Date());
		  testTask.setCreatedDate(taskCreate);
		  
			testTask = server.addTask(testTask);
			
			server.refreshDetails();
		}
		
		return testTask;
	}

	public static TaskList getTaskList() {
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		TasksUiPlugin.getTaskListManager().saveTaskList();
		
		return taskList;
	}
	
	public static UserStoryData findTestUserStory(XPlannerServer server) throws RemoteException {
		UserStoryData testUserStory = null;
		
		ProjectData testProject = findTestProject(server);
		if (testProject != null) {
			IterationData testIteration = findTestIteration(server, testProject);
			if (testIteration != null) {
				testUserStory = findTestUserStory(server, testIteration);
			}
		}
		
		return testUserStory;
	}
	
	public static TaskData findTestTask(XPlannerServer server) throws RemoteException {
		TaskData testTask = null;

		UserStoryData testUserStory = findTestUserStory(server);
		if (testUserStory != null) {
			testTask = findTestTask(server, testUserStory);
		}
		
		return testTask;
	}
	
	/**
	 * setUpTestData() needs to be called before this method
	 */
	public static XPlannerTask getTestXPlannerTask(XPlannerServer server) throws Exception {
		TaskRepository repository = getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		
		TaskData testTask = findTestTask(server);
		Task task = connector.createTaskFromExistingKey(repository, "" + testTask.getId());
		return (XPlannerTask) task;
	}

	/**
	 * setUpTestData() needs to be called before this method
	 */
	public static XPlannerTask getTestXPlannerUserStoryTask(XPlannerServer server) throws Exception {
		TaskRepository repository = getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		
		UserStoryData testUserStory = findTestUserStory(server);
		Task task = connector.createTaskFromExistingKey(repository, "" + testUserStory.getId());
		return (XPlannerTask) task;
	}
	
}
