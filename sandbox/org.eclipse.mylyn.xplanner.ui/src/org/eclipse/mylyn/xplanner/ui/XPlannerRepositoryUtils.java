/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryUtils {
	private static final String TYPE_FEATURE = "Feature";

	private static final String NO_TASK_NAME = "<no task name>";

	public static final String DISPOSITION_PLANNED = "planned";

	private XPlannerRepositoryUtils() {

	}

	public static RepositoryTaskData createRepositoryTaskData(TaskRepository repository, XPlannerTask xplannerTask,
			XPlannerClient client) throws CoreException {
		RepositoryTaskData repositoryTaskData = null;

		try {
			if (XPlannerTask.Kind.TASK.toString().equals(xplannerTask.getTaskKind())) {
				TaskData taskData = client.getTask(Integer.valueOf(xplannerTask.getTaskId()).intValue());
				repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
						repository.getRepositoryUrl(), taskData,
						RepositoryTaskHandleUtil.getTaskId(xplannerTask.getHandleIdentifier()));
				xplannerTask.setCompleted(taskData.isCompleted());
			} else if (XPlannerTask.Kind.USER_STORY.toString().equals(xplannerTask.getTaskKind())) {
				UserStoryData userStory = client.getUserStory(Integer.valueOf(xplannerTask.getTaskId()).intValue());
				repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
						repository.getRepositoryUrl(), userStory,
						RepositoryTaskHandleUtil.getTaskId(xplannerTask.getHandleIdentifier()));
				xplannerTask.setCompleted(userStory.isCompleted());
			}
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID, 0, MessageFormat.format(
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, xplannerTask.getRepositoryUrl(),
					TasksUiPlugin.LABEL_VIEW_REPOSITORIES), e));
		}

		return repositoryTaskData;
	}

	public static RepositoryTaskData createRepositoryTaskData(TaskRepository repository, String taskId)
			throws CoreException {

		RepositoryTaskData repositoryTaskData = null;

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		try {
			TaskData taskData = client.getTask(Integer.valueOf(taskId).intValue());
			if (taskData != null) {
				repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
						repository.getRepositoryUrl(), taskData, taskId);
			} else {
				UserStoryData userStory = client.getUserStory(Integer.valueOf(taskId).intValue());
				if (userStory != null) {
					repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
							repository.getRepositoryUrl(), userStory, taskId);
				}
			}
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID, 0, MessageFormat.format(
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, repository.getRepositoryUrl(),
					TasksUiPlugin.LABEL_VIEW_REPOSITORIES), e));
		}

		return repositoryTaskData;
	}

	public static RepositoryTaskData getXPlannerRepositoryTaskData(String repositoryUrl, TaskData taskData, String id)
			throws IOException, MalformedURLException, LoginException, GeneralSecurityException, CoreException {

		RepositoryTaskData repositoryTaskData = new RepositoryTaskData(new XPlannerAttributeFactory(),
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryUrl, id, XPlannerTask.Kind.TASK.toString());

		setupTaskAttributes(taskData, repositoryTaskData);

		return repositoryTaskData;
	}

	public static RepositoryTaskData getXPlannerRepositoryTaskData(String repositoryUrl, UserStoryData userStory,
			String id) throws IOException, MalformedURLException, LoginException, GeneralSecurityException,
			CoreException {

		RepositoryTaskData repositoryTaskData = new RepositoryTaskData(new XPlannerAttributeFactory(),
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryUrl, id, XPlannerTask.Kind.USER_STORY.toString());
		setupUserStoryAttributes(userStory, repositoryTaskData);

		return repositoryTaskData;
	}

	public static void setupTaskAttributes(TaskData taskData, RepositoryTaskData repositoryTaskData)
			throws CoreException {

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// description
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DESCRIPTION, taskData.getDescription());

		// priority
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, getPriorityFromXPlannerObject(taskData,
				client));

		// status
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.STATUS, taskData.getDispositionName());

		// summary
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.SUMMARY, taskData.getName());

		// assigned to 
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED, getPersonName(
				taskData.getAcceptorId(), client));

		// createdDate 
		if (taskData.getCreatedDate() != null) {
			Date createdDate = taskData.getCreatedDate().getTime();
			repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DATE_CREATION,
					XPlannerAttributeFactory.DATE_FORMAT.format(createdDate));
		}

		// last updated
		Date lastUpdatedDate = taskData.getLastUpdateTime().getTime();
		if (lastUpdatedDate != null) {
			repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED,
					XPlannerAttributeFactory.TIME_DATE_FORMAT.format(lastUpdatedDate));
		}

		// est time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME,
				"" + taskData.getEstimatedHours()); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME,
				"" + taskData.getActualHours()); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_REMAINING_HOURS_NAME,
				"" + taskData.getRemainingHours()); //$NON-NLS-1$

		// est original hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME,
				"" + taskData.getEstimatedOriginalHours()); //$NON-NLS-1$

		// est adjusted estimated hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME,
				"" + taskData.getAdjustedEstimatedHours()); //$NON-NLS-1$

		// project name
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_PROJECT_NAME, getProjectName(taskData,
				client));

		// iteration name
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ITERATION_NAME, getIterationName(
				taskData, client));

		// user story name
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_NAME, getUserStoryName(
				taskData, client));

		// completed
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED,
				taskData.isCompleted() ? "1" : "0"); //$NON-NLS-1$//$NON-NLS-2$
	}

	public static void setupNewTaskAttributes(UserStoryData userStoryData, RepositoryTaskData repositoryTaskData)
			throws CoreException {

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// priority
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, getPriorityFromXPlannerObject(
				userStoryData, client));

		// status
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.STATUS, userStoryData.getDispositionName());

		// assigned to 
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED, getPersonName(
				userStoryData.getTrackerId(), client));
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ASSIGNED_ID, ""
				+ userStoryData.getTrackerId());

		// project info
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_PROJECT_NAME, getProjectName(
				userStoryData, client));
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_PROJECT_ID, ""
				+ getProjectId(userStoryData, client));

		// iteration info
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStoryData, client));
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ITERATION_ID, ""
				+ getIterationId(userStoryData, client));

		// user story info
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_NAME,
				userStoryData.getName());
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_ID, ""
				+ userStoryData.getId());

		// completed
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED, "0");

		// est time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME, "0.0"); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME, "0.0"); //$NON-NLS-1$

		// est original hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME, "0.0"); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_REMAINING_HOURS_NAME, "" + "0.0"); //$NON-NLS-1$

		// est adjusted estimated hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME, "0.0"); //$NON-NLS-1$
	}

	public static TaskData createNewTaskData(RepositoryTaskData repositoryTaskData, XPlannerClient client) {

		TaskData taskData = new TaskData();

		// assigned to 
		String assignedToId = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ASSIGNED_ID);
		taskData.setAcceptorId(assignedToId == null || assignedToId.length() == 0 ? XPlannerAttributeFactory.INVALID_ID
				: Integer.valueOf(assignedToId).intValue());

		// user story info
		String userStoryId = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_ID);
		taskData.setStoryId(userStoryId == null || userStoryId.length() == 0 ? XPlannerAttributeFactory.INVALID_ID
				: Integer.valueOf(userStoryId).intValue());

		// completed
		String completed = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED);
		taskData.setCompleted(completed != null && !("0".equals(completed)));

		// disposition -- some servers ok without this value in new tasks, but some not, so for safety...
		taskData.setDispositionName(DISPOSITION_PLANNED);

		// type
		taskData.setType(TYPE_FEATURE);

		return taskData;
	}

	// Sanity check to make sure taskdata has minimum settings that will avoid corrupting parent story
	public static void ensureTaskDataValid(TaskData taskData) {
		if (taskData != null) { // more sanity, shouldn't happen
			if (taskData.getDispositionName() == null || taskData.getDispositionName().length() == 0) {
				taskData.setDispositionName(DISPOSITION_PLANNED);
			}

			if (taskData.getName() == null || taskData.getName().length() == 0) {
				taskData.setName(NO_TASK_NAME);
			}
		}
	}

	public static void setupUserStoryAttributes(UserStoryData userStory, RepositoryTaskData repositoryTaskData)
			throws CoreException {

		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// description
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DESCRIPTION, userStory.getDescription());

		// priority
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.PRIORITY, getPriorityFromXPlannerObject(userStory,
				client));

		// summary
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.SUMMARY, userStory.getName());

		// status
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.STATUS, userStory.getDispositionName());

		// assigned to 
		repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED, getPersonName(
				userStory.getTrackerId(), client));

		// createdDate -- user story doesn't have created date

		// last updated
		Date lastUpdatedDate = userStory.getLastUpdateTime().getTime();
		if (lastUpdatedDate != null) {
			repositoryTaskData.setAttributeValue(RepositoryTaskAttribute.DATE_MODIFIED,
					XPlannerAttributeFactory.TIME_DATE_FORMAT.format(lastUpdatedDate));
		}

		// est time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME,
				"" + userStory.getEstimatedHours()); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME,
				"" + userStory.getActualHours()); //$NON-NLS-1$

		// est original hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME,
				"" + userStory.getEstimatedOriginalHours()); //$NON-NLS-1$

		// act time
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_REMAINING_HOURS_NAME,
				"" + userStory.getRemainingHours()); //$NON-NLS-1$

		// est adjusted estimated hours
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME,
				"" + userStory.getAdjustedEstimatedHours()); //$NON-NLS-1$

		// project name
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_PROJECT_NAME, getProjectName(userStory,
				client));

		// iteration name
		repositoryTaskData.setAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStory, client));
	}

	public static String getProjectName(RepositoryTaskData repositoryTaskData) {
		return repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_PROJECT_NAME);
	}

	public static String getIterationName(RepositoryTaskData repositoryTaskData) {
		return repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ITERATION_NAME);
	}

	public static String getUserStoryName(RepositoryTaskData repositoryTaskData) {
		return repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_USER_STORY_NAME);
	}

	public static String getPersonName(int personId, XPlannerClient client) {
		String personName = Messages.XPlannerRepositoryUtils_NO_PERSON_NAME;

		try {
			PersonData personData = client.getPerson(personId);
			if (personData != null) {
				personName = personData.getName();
			}
		} catch (Exception e) { //RemoteException e) {
			e.printStackTrace();
		}

		return personName;
	}

	public static double getActualHours(RepositoryTaskData repositoryTaskData) {
		String hours = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static double getRemainingHours(RepositoryTaskData repositoryTaskData) {
		String hours = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_REMAINING_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static double getEstimatedHours(RepositoryTaskData repositoryTaskData) {
		String hours = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Double getAdjustedEstimatedHours(RepositoryTaskData repositoryTaskData) {
		String hours = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Double getEstimatedOriginalHours(RepositoryTaskData repositoryTaskData) {
		String hours = repositoryTaskData.getAttributeValue(XPlannerAttributeFactory.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Date getCreatedDate(RepositoryTaskData repositoryTaskData) {
		Date createdDate = null;

		String dateString = repositoryTaskData.getAttributeValue(RepositoryTaskAttribute.DATE_CREATION);
		try {
			createdDate = XPlannerAttributeFactory.DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
		}

		return createdDate;
	}

	public static String getProjectName(TaskData taskData, XPlannerClient client) {
		String projectName = Messages.XPlannerRepositoryUtils_NO_PROJECT_NAME;

		UserStoryData userStory;
		try {
			userStory = client.getUserStory(taskData.getStoryId());
			projectName = getProjectName(userStory, client);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return projectName;
	}

	public static String getProjectName(UserStoryData userStory, XPlannerClient client) {
		String projectName = Messages.XPlannerRepositoryUtils_NO_PROJECT_NAME;

		try {
			if (userStory != null) {
				IterationData iteration = client.getIteration(userStory.getIterationId());
				if (iteration != null) {
					ProjectData project = client.getProject(iteration.getProjectId());
					if (project != null) {
						projectName = project.getName();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return projectName;
	}

	public static int getProjectId(UserStoryData userStory, XPlannerClient client) {
		int projectId = XPlannerAttributeFactory.INVALID_ID;

		try {
			if (userStory != null) {
				IterationData iteration = client.getIteration(userStory.getIterationId());
				if (iteration != null) {
					projectId = iteration.getProjectId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return projectId;
	}

	public static String getIterationName(TaskData taskData, XPlannerClient client) {
		String iterationName = Messages.XPlannerRepositoryUtils_NO_ITERATION_NAME;

		try {
			UserStoryData userStory = client.getUserStory(taskData.getStoryId());
			iterationName = getIterationName(userStory, client);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return iterationName;
	}

	public static String getIterationName(UserStoryData userStory, XPlannerClient client) {
		String iterationName = Messages.XPlannerRepositoryUtils_NO_ITERATION_NAME;

		try {
			IterationData iteration = client.getIteration(userStory.getIterationId());
			if (iteration != null) {
				iterationName = iteration.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iterationName;
	}

	public static int getIterationId(UserStoryData userStory, XPlannerClient client) {
		int iterationId = XPlannerAttributeFactory.INVALID_ID;

		try {
			IterationData iteration = client.getIteration(userStory.getIterationId());
			if (iteration != null) {
				iterationId = iteration.getId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iterationId;
	}

	public static String getUserStoryName(TaskData taskData, XPlannerClient client) {
		String userStoryName = Messages.XPlannerRepositoryUtils_NO_USER_STORY_NAME;

		try {
			UserStoryData userStory = client.getUserStory(taskData.getStoryId());
			if (userStory != null) {
				userStoryName = userStory.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userStoryName;
	}

	public static String getDescription(RepositoryTaskData repositoryTaskData) {
		return repositoryTaskData.getAttributeValue(RepositoryTaskAttribute.DESCRIPTION);
	}

	public static boolean isCompleted(RepositoryTaskData repositoryTaskData) {
		return "1".equals(repositoryTaskData.getAttributeValue( //$NON-NLS-1$
		XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED));
	}

	public static String getName(RepositoryTaskData repositoryTaskData) {
		return repositoryTaskData.getAttributeValue(RepositoryTaskAttribute.SUMMARY);
	}

	public static String getPriorityFromXPlannerObject(Object xplannerObject, XPlannerClient client) {
		int priority = -1;
		UserStoryData userStory = null;

		try {
			if (xplannerObject instanceof TaskData) {
				userStory = client.getUserStory(((TaskData) xplannerObject).getStoryId());
			} else if (xplannerObject instanceof UserStoryData) {
				userStory = (UserStoryData) xplannerObject;
			}

			if (userStory != null) {
				priority = userStory.getPriority();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return priority == -1 ? "" : String.valueOf(priority); //$NON-NLS-1$
	}

	private static HashSet<String> validatedRepositoryUrls = new HashSet<String>();

	public static boolean isRepositoryUrlValidated(String repositoryUrl) {
		return validatedRepositoryUrls.contains(repositoryUrl);
	}

	static void addValidatedRepositoryUrl(String url) {
		validatedRepositoryUrls.add(url);
	}

	static void removeValidatedRepositoryUrl(String url) {
		validatedRepositoryUrls.remove(url);
	}

	public static void checkRepositoryValidated(String repositoryUrl) throws CoreException {
		if (repositoryUrl == null) {
			return;
		}

		TaskRepository taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(
				XPlannerMylynUIPlugin.REPOSITORY_KIND, repositoryUrl);
		if (taskRepository != null && !isRepositoryUrlValidated(taskRepository.getRepositoryUrl())) {
			validateRepository(taskRepository);
		}
	}

	public static void validateRepository(TaskRepository taskRepository) throws CoreException {
		validateRepository(taskRepository.getRepositoryUrl(), taskRepository.getUserName(),
				taskRepository.getPassword(), taskRepository.getProxy(), taskRepository.getHttpUser(),
				taskRepository.getHttpPassword());
	}

	public static void validateRepository(String url, String userName, String password) throws CoreException {
		validateRepository(url, userName, password, Proxy.NO_PROXY, null, null);
	}

	public static void validateRepository(String url, String userName, String password, Proxy proxy, String httpUser,
			String httpPassword) throws CoreException {

		try {
			XPlannerClientFacade.getDefault().validateServerAndCredentials(url, userName, password, proxy, httpUser,
					httpPassword);
		} catch (Exception e) {
			throw new CoreException(XPlannerCorePlugin.toStatus(e));
		}

	}

	public static RepositoryTaskData getNewRepositoryTaskData(TaskRepository taskRepository, UserStoryData userStoryData)
			throws CoreException {

		if (taskRepository == null || userStoryData == null) {
			return null;
		}

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				XPlannerMylynUIPlugin.REPOSITORY_KIND);

		XPlannerTaskDataHandler taskDataHandler = (XPlannerTaskDataHandler) connector.getTaskDataHandler();
		AbstractAttributeFactory attributeFactory = taskDataHandler.getAttributeFactory(
				taskRepository.getRepositoryUrl(), taskRepository.getConnectorKind(), AbstractTask.DEFAULT_TASK_KIND);
		RepositoryTaskData taskData = new RepositoryTaskData(attributeFactory, XPlannerMylynUIPlugin.REPOSITORY_KIND,
				taskRepository.getRepositoryUrl(), TasksUiPlugin.getDefault().getNextNewRepositoryTaskId(),
				XPlannerTask.Kind.TASK.toString());
		taskData.setNew(true);
		taskDataHandler.initializeTaskData(taskRepository, taskData, userStoryData);

		return taskData;
	}
}
