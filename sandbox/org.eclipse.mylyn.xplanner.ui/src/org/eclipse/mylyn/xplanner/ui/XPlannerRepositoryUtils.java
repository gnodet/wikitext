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

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper.Attribute;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
// for TasksUi
public class XPlannerRepositoryUtils {
	private static final String TYPE_FEATURE = "Feature";

	private static final String NO_TASK_NAME = "<no task name>";

	public static final String DISPOSITION_PLANNED = "planned";

	private XPlannerRepositoryUtils() {

	}

	public static org.eclipse.mylyn.tasks.core.data.TaskData createRepositoryTaskData(TaskRepository repository,
			ITask xplannerTask, XPlannerClient client) throws CoreException {
		TaskData repositoryTaskData = null;

		try {
			Date completionDate = null;
			if (XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString().equals(xplannerTask.getTaskKind())
					|| AbstractTask.DEFAULT_TASK_KIND.equals(xplannerTask.getTaskKind())) {

				org.xplanner.soap.TaskData taskData = client.getTask(Integer.valueOf(xplannerTask.getTaskId())
						.intValue());
				if (taskData.isCompleted()) {
					completionDate = taskData.getLastUpdateTime().getTime();
				}
				repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
						repository.getRepositoryUrl(), taskData, xplannerTask.getTaskId());
				xplannerTask.setCompletionDate(completionDate);
			} else if (XPlannerAttributeMapper.XPlannerTaskKind.USER_STORY.toString()
					.equals(xplannerTask.getTaskKind())) {
				UserStoryData userStory = client.getUserStory(Integer.valueOf(xplannerTask.getTaskId()).intValue());
				if (userStory.isCompleted()) {
					completionDate = userStory.getLastUpdateTime().getTime();
				}
				repositoryTaskData = XPlannerRepositoryUtils.getXPlannerRepositoryTaskData(
						repository.getRepositoryUrl(), userStory, xplannerTask.getTaskId());
				xplannerTask.setCompletionDate(completionDate);
			}
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, 0, MessageFormat.format(
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, xplannerTask.getRepositoryUrl(),
					TasksUiPlugin.LABEL_VIEW_REPOSITORIES), e));
		}

		return repositoryTaskData;
	}

	public static org.eclipse.mylyn.tasks.core.data.TaskData createRepositoryTaskData(TaskRepository repository,
			String taskId) throws CoreException {

		TaskData repositoryTaskData = null;

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		try {
			org.xplanner.soap.TaskData taskData = client.getTask(Integer.valueOf(taskId).intValue());
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
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, 0, MessageFormat.format(
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, repository.getRepositoryUrl(),
					TasksUiPlugin.LABEL_VIEW_REPOSITORIES), e));
		}

		return repositoryTaskData;
	}

	public static TaskData getXPlannerRepositoryTaskData(String repositoryUrl, org.xplanner.soap.TaskData taskData,
			String id) throws IOException, MalformedURLException, LoginException, GeneralSecurityException,
			CoreException {

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryUrl);
		TaskData repositoryTaskData = new TaskData(new XPlannerAttributeMapper(repository),
				XPlannerCorePlugin.CONNECTOR_KIND, repositoryUrl, id);

		setupTaskAttributes(taskData, repositoryTaskData);

		return repositoryTaskData;
	}

	public static TaskData getXPlannerRepositoryTaskData(String repositoryUrl, UserStoryData userStory, String id)
			throws IOException, MalformedURLException, LoginException, GeneralSecurityException, CoreException {

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryUrl);
		TaskData repositoryTaskData = new TaskData(new XPlannerAttributeMapper(repository),
				XPlannerCorePlugin.CONNECTOR_KIND, repositoryUrl, id);
		setupUserStoryAttributes(userStory, repositoryTaskData);

		return repositoryTaskData;
	}

	public static void setupTaskAttributes(org.xplanner.soap.TaskData taskData,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) throws CoreException {

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// kind
		setAttributeValue(repositoryTaskData, TaskAttribute.TASK_KIND,
				XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString());

		// description
		setAttributeValue(repositoryTaskData, TaskAttribute.DESCRIPTION, taskData.getDescription());

		// priority
		setAttributeValue(repositoryTaskData, TaskAttribute.PRIORITY, getPriorityFromXPlannerObject(taskData, client));

		// status
		setAttributeValue(repositoryTaskData, TaskAttribute.STATUS, taskData.getDispositionName());

		// summary
		setAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY, taskData.getName());

		// assigned to 
		setAttributeValue(repositoryTaskData, TaskAttribute.USER_ASSIGNED, getPersonName(taskData.getAcceptorId(),
				client));

		// createdDate 
		if (taskData.getCreatedDate() != null) {
			Date createdDate = taskData.getCreatedDate().getTime();
			setAttributeValue(repositoryTaskData, TaskAttribute.DATE_CREATION,
					XPlannerAttributeMapper.DATE_FORMAT.format(createdDate));
		}

		// last updated
		Date lastUpdatedDate = taskData.getLastUpdateTime().getTime();
		if (lastUpdatedDate != null) {
			setAttributeValue(repositoryTaskData, TaskAttribute.DATE_MODIFICATION,
					XPlannerAttributeMapper.TIME_DATE_FORMAT.format(lastUpdatedDate));
		}

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME,
				"" + taskData.getEstimatedHours()); //$NON-NLS-1$

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME,
				"" + taskData.getActualHours()); //$NON-NLS-1$

		// remaining time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME,
				"" + taskData.getRemainingHours()); //$NON-NLS-1$

		// est original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME,
				"" + taskData.getEstimatedOriginalHours()); //$NON-NLS-1$

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME,
				"" + taskData.getAdjustedEstimatedHours()); //$NON-NLS-1$

		// project name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME, getProjectName(taskData,
				client));

		// iteration name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME, getIterationName(
				taskData, client));

		// user story name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_NAME, getUserStoryName(
				taskData, client));

		// completed
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED,
				taskData.isCompleted() ? "1" : "0"); //$NON-NLS-1$//$NON-NLS-2$

		// completion date
		if (taskData.isCompleted()) {
			Date completionDate = taskData.getLastUpdateTime().getTime();
			if (completionDate != null) {
				setAttributeValue(repositoryTaskData, TaskAttribute.DATE_COMPLETION,
						XPlannerAttributeMapper.TIME_DATE_FORMAT.format(lastUpdatedDate));
			}
		}

	}

	public static void setupNewTaskAttributes(UserStoryData userStoryData, TaskData repositoryTaskData)
			throws CoreException {

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// kind
		setAttributeValue(repositoryTaskData, TaskAttribute.TASK_KIND,
				XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString());

		// kind
		setAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY, "");

		// priority
		setAttributeValue(repositoryTaskData, TaskAttribute.PRIORITY, getPriorityFromXPlannerObject(userStoryData,
				client));

		// status
		setAttributeValue(repositoryTaskData, TaskAttribute.STATUS, userStoryData.getDispositionName());

		// assigned to 
		setAttributeValue(repositoryTaskData, TaskAttribute.USER_ASSIGNED, getPersonName(userStoryData.getTrackerId(),
				client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ASSIGNED_ID, ""
				+ userStoryData.getTrackerId());

		// project info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME, getProjectName(
				userStoryData, client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_ID, ""
				+ getProjectId(userStoryData, client));

		// iteration info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStoryData, client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_ID, ""
				+ getIterationId(userStoryData, client));

		// user story info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_NAME,
				userStoryData.getName());
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID, ""
				+ userStoryData.getId());

		// completed
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED, "0");

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME, "0.0"); //$NON-NLS-1$

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, "0.0");

		// est original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME, "0.0");

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME, "" + "0.0");

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME, "0.0");
	}

	public static org.xplanner.soap.TaskData createNewTaskData(TaskData repositoryTaskData, XPlannerClient client) {

		org.xplanner.soap.TaskData taskData = new org.xplanner.soap.TaskData();

		// assigned to 
		String assignedToId = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ASSIGNED_ID);
		taskData.setAcceptorId(assignedToId == null || assignedToId.length() == 0 ? XPlannerAttributeMapper.INVALID_ID
				: Integer.valueOf(assignedToId).intValue());

		// user story info
		String userStoryId = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID);
		taskData.setStoryId(userStoryId == null || userStoryId.length() == 0 ? XPlannerAttributeMapper.INVALID_ID
				: Integer.valueOf(userStoryId).intValue());

		// completed
		String completed = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED);
		taskData.setCompleted(completed != null && !("0".equals(completed)));

		// disposition -- some servers ok without this value in new tasks, but some not, so for safety...
		taskData.setDispositionName(DISPOSITION_PLANNED);

		// type
		taskData.setType(TYPE_FEATURE);

		return taskData;
	}

	// Sanity check to make sure TaskData has minimum settings that will avoid corrupting parent story
	public static void ensureTaskDataValid(org.xplanner.soap.TaskData taskData) {
		if (taskData != null) { // more sanity, shouldn't happen
			if (taskData.getDispositionName() == null || taskData.getDispositionName().length() == 0) {
				taskData.setDispositionName(DISPOSITION_PLANNED);
			}

			if (taskData.getName() == null || taskData.getName().length() == 0) {
				taskData.setName(NO_TASK_NAME);
			}
		}
	}

	public static void setupUserStoryAttributes(UserStoryData userStory, TaskData repositoryTaskData)
			throws CoreException {

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryTaskData.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

		// kind
		setAttributeValue(repositoryTaskData, TaskAttribute.TASK_KIND,
				XPlannerAttributeMapper.XPlannerTaskKind.USER_STORY.toString());

		// description
		setAttributeValue(repositoryTaskData, TaskAttribute.DESCRIPTION, userStory.getDescription());

		// priority
		setAttributeValue(repositoryTaskData, TaskAttribute.PRIORITY, getPriorityFromXPlannerObject(userStory, client));

		// summary
		setAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY, userStory.getName());

		// status
		setAttributeValue(repositoryTaskData, TaskAttribute.STATUS, userStory.getDispositionName());

		// assigned to 
		setAttributeValue(repositoryTaskData, TaskAttribute.USER_ASSIGNED, getPersonName(userStory.getTrackerId(),
				client));

		// createdDate -- user story doesn't have created date

		// last updated
		Date lastUpdatedDate = userStory.getLastUpdateTime().getTime();
		if (lastUpdatedDate != null) {
			setAttributeValue(repositoryTaskData, TaskAttribute.DATE_MODIFICATION,
					XPlannerAttributeMapper.TIME_DATE_FORMAT.format(lastUpdatedDate));
		}

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME, ""
				+ userStory.getEstimatedHours());

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, ""
				+ userStory.getActualHours());

		// est original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME, ""
				+ userStory.getEstimatedOriginalHours());

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME, ""
				+ userStory.getRemainingHours());

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME, ""
				+ userStory.getAdjustedEstimatedHours());

		// project name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME, getProjectName(userStory,
				client));

		// iteration name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStory, client));
	}

	public static String getProjectName(TaskData repositoryTaskData) {
		return getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME);
	}

	public static String getIterationName(TaskData repositoryTaskData) {
		return getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME);
	}

	public static String getUserStoryName(TaskData repositoryTaskData) {
		return getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_NAME);
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

	public static void setAttributeValue(TaskData repositoryTaskData, String attributeId, String value) {
		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(attributeId);

		if (attribute == null) {
			attribute = repositoryTaskData.getRoot().createMappedAttribute(attributeId);
			attribute.getMetaData().defaults();
			attribute.getMetaData().setReadOnly(false);
			attribute.getMetaData().setType(getType(attributeId));
			Attribute xplannerAttribute = XPlannerAttributeMapper.getAttribute(attributeId);
			if (xplannerAttribute != null) {
				attribute.getMetaData().setLabel(xplannerAttribute.getDisplayName());
				attribute.getMetaData().setReadOnly(xplannerAttribute.isReadOnly());
			}
		}

		if (attribute != null) {
			repositoryTaskData.getAttributeMapper().setValue(attribute, value == null ? "" : value);
		}
	}

	public static String getType(String attributeId) {
		if (attributeId.equals(Attribute.DESCRIPTION.getCommonAttributeKey())) {
			return TaskAttribute.TYPE_LONG_RICH_TEXT;
		}
		return TaskAttribute.TYPE_SHORT_TEXT;
	}

	public static String getAttributeValue(TaskData repositoryTaskData, String attributeId) {
		String value = null;

		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(attributeId);

		if (attribute != null) {
			value = repositoryTaskData.getAttributeMapper().getValue(attribute);
		}

		return value;
	}

	public static double getActualHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static double getRemainingHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static double getEstimatedHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Double getAdjustedEstimatedHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData,
				XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Double getEstimatedOriginalHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData,
				XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME);
		return Double.valueOf(hours).doubleValue();
	}

	public static Date getCreatedDate(TaskData repositoryTaskData) {
		Date createdDate = null;

		String dateString = getAttributeValue(repositoryTaskData, TaskAttribute.DATE_CREATION);
		try {
			createdDate = XPlannerAttributeMapper.DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			XPlannerMylynUIPlugin.log(e.getCause(), "", false);
		}

		return createdDate;
	}

	public static String getProjectName(org.xplanner.soap.TaskData taskData, XPlannerClient client) {
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
		int projectId = XPlannerAttributeMapper.INVALID_ID;

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

	public static String getIterationName(org.xplanner.soap.TaskData taskData, XPlannerClient client) {
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
		int iterationId = XPlannerAttributeMapper.INVALID_ID;

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

	public static String getUserStoryName(org.xplanner.soap.TaskData taskData, XPlannerClient client) {
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

	public static String getDescription(TaskData repositoryTaskData) {
		return getAttributeValue(repositoryTaskData, TaskAttribute.DESCRIPTION);
	}

	public static boolean isCompleted(TaskData repositoryTaskData) {
		return "1".equals(getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED));
	}

	public static String getName(TaskData repositoryTaskData) {
		String name = null;

		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY);
		if (attribute != null) {
			name = repositoryTaskData.getAttributeMapper().getValue(attribute);
		}

		return name;
	}

	public static String getAssignedTo(TaskData repositoryTaskData) {
		String name = null;

		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED);
		if (attribute != null) {
			name = repositoryTaskData.getAttributeMapper().getValue(attribute);
		}

		return name;
	}

	public static String getPriorityFromXPlannerObject(Object xplannerObject, XPlannerClient client) {
		int priority = -1;
		UserStoryData userStory = null;

		try {
			if (xplannerObject instanceof org.xplanner.soap.TaskData) {
				userStory = client.getUserStory(((org.xplanner.soap.TaskData) xplannerObject).getStoryId());
			} else if (xplannerObject instanceof UserStoryData) {
				userStory = (UserStoryData) xplannerObject;
			}

			if (userStory != null) {
				priority = userStory.getPriority();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return priority == -1 ? "" : String.valueOf(priority);
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

		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryUrl);
		if (taskRepository != null && !isRepositoryUrlValidated(taskRepository.getRepositoryUrl())) {
			validateRepository(taskRepository);
		}
	}

	public static void validateRepository(TaskRepository taskRepository) throws CoreException {
		AuthenticationCredentials repositoryCredentials = taskRepository.getCredentials(AuthenticationType.REPOSITORY);
		AuthenticationCredentials httpCredentials = taskRepository.getCredentials(AuthenticationType.HTTP);
		XPlannerRepositoryConnector connector = (XPlannerRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(XPlannerCorePlugin.CONNECTOR_KIND);
		TaskRepositoryLocationFactory locationFactory = connector.getTaskRepositoryLocationFactory();
		AbstractWebLocation location = locationFactory.createWebLocation(taskRepository);

		String repositoryUrl = taskRepository.getRepositoryUrl();
		String repositoryUserName = repositoryCredentials.getUserName();
		String repositoryPassword = repositoryCredentials.getPassword();
		Proxy proxy = location.getProxyForHost(location.getUrl(), IProxyData.HTTP_PROXY_TYPE);
		String httpUserName = httpCredentials == null ? null : httpCredentials.getUserName();
		String httpPassword = httpCredentials == null ? null : httpCredentials.getPassword();

		validateRepository(repositoryUrl, repositoryUserName, repositoryPassword, proxy, httpUserName, httpPassword);
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

	public static TaskData getNewRepositoryTaskData(TaskRepository taskRepository, UserStoryData userStoryData)
			throws CoreException {

		if (taskRepository == null || userStoryData == null) {
			return null;
		}

		XPlannerRepositoryConnector connector = (XPlannerRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(XPlannerCorePlugin.CONNECTOR_KIND);

		XPlannerTaskDataHandler taskDataHandler = (XPlannerTaskDataHandler) connector.getTaskDataHandler();
		TaskAttributeMapper attributeMapper = taskDataHandler.getAttributeMapper(taskRepository);
		TaskData repositoryTaskData = new TaskData(attributeMapper, XPlannerCorePlugin.CONNECTOR_KIND,
				taskRepository.getRepositoryUrl(), ""); //HeB -- testing TasksUiPlugin.getDefault().getNextNewRepositoryTaskId());
//		repositoryTaskData.setNew(true);
		taskDataHandler.initializeTaskData(taskRepository, repositoryTaskData, userStoryData);

		return repositoryTaskData;
	}

	public static PriorityLevel getPriorityLevel(String priority) {
		PriorityLevel priorityLevel = PriorityLevel.getDefault();

		int priorityValue = Integer.valueOf(priority);

		switch (priorityValue) {
		case 0:
			priorityLevel = PriorityLevel.P1;
			break;
		case 1:
			priorityLevel = PriorityLevel.P1;
			break;
		case 2:
			priorityLevel = PriorityLevel.P2;
			break;
		case 3:
			priorityLevel = PriorityLevel.P3;
			break;
		case 4:
			priorityLevel = PriorityLevel.P4;
			break;
		case 5:
			priorityLevel = PriorityLevel.P5;
			break;
		}

		return priorityLevel;
	}
}
