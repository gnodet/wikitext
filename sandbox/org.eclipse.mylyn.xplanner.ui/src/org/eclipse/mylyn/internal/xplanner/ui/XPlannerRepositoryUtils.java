/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.security.auth.login.LoginException;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.internal.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper.Attribute;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryUtils {
	private static final String TYPE_FEATURE = "Feature"; //$NON-NLS-1$

	private static final String NO_TASK_NAME = Messages.XPlannerRepositoryUtils_NO_TASK_NAME;

	public static final String DISPOSITION_PLANNED = "planned"; //$NON-NLS-1$

	private XPlannerRepositoryUtils() {

	}

	public static org.eclipse.mylyn.tasks.core.data.TaskData createRepositoryTaskData(TaskRepository repository,
			ITask xplannerTask, XPlannerClient client) throws CoreException {
		TaskData repositoryTaskData = null;

		try {
			Date completionDate = null;
			if (XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString().equals(xplannerTask.getTaskKind())
					|| XPlannerAttributeMapper.DEFAULT_REPOSITORY_TASK_KIND.equals(xplannerTask.getTaskKind())) {

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
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, xplannerTask.getRepositoryUrl()), e));
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
					Messages.XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED, repository.getRepositoryUrl()), e));
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

		DateFormat timeDateFormat = new SimpleDateFormat(XPlannerAttributeMapper.TIME_DATE_FORMAT_STRING);

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
			DateFormat dateFormat = java.text.DateFormat.getDateInstance(XPlannerAttributeMapper.DATE_FORMAT_STYLE);

			setAttributeValue(repositoryTaskData, TaskAttribute.DATE_CREATION, dateFormat.format(createdDate));
		}

		// last updated
		Date lastUpdatedDate = taskData.getLastUpdateTime().getTime();
		if (lastUpdatedDate != null) {
			setAttributeValue(repositoryTaskData, TaskAttribute.DATE_MODIFICATION,
					timeDateFormat.format(lastUpdatedDate));
		}

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME,
				getStringValue(taskData.getEstimatedHours()));

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME,
				getStringValue(taskData.getActualHours()));

		// remaining time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME,
				getStringValue(taskData.getRemainingHours()));

		// estimated original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME,
				getStringValue(taskData.getEstimatedOriginalHours()));

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME,
				getStringValue(taskData.getAdjustedEstimatedHours()));

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
						timeDateFormat.format(lastUpdatedDate));
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
		setAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY, ""); //$NON-NLS-1$

		// priority
		setAttributeValue(repositoryTaskData, TaskAttribute.PRIORITY, getPriorityFromXPlannerObject(userStoryData,
				client));

		// status
		setAttributeValue(repositoryTaskData, TaskAttribute.STATUS, userStoryData.getDispositionName());

		// assigned to 
		setAttributeValue(repositoryTaskData, TaskAttribute.USER_ASSIGNED, getPersonName(userStoryData.getTrackerId(),
				client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ASSIGNED_ID, "" //$NON-NLS-1$
				+ userStoryData.getTrackerId());

		// project info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME, getProjectName(
				userStoryData, client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_ID, "" //$NON-NLS-1$
				+ getProjectId(userStoryData, client));

		// iteration info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStoryData, client));
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_ID, "" //$NON-NLS-1$
				+ getIterationId(userStoryData, client));

		// user story info
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_NAME,
				userStoryData.getName());
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID, "" //$NON-NLS-1$
				+ userStoryData.getId());

		// completed
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED, "0"); //$NON-NLS-1$

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME,
				XPlannerRepositoryUtils.formatSingleFractionHours(0.0d));

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME,
				XPlannerRepositoryUtils.formatSingleFractionHours(0.0d));

		// est original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME,
				XPlannerRepositoryUtils.formatSingleFractionHours(0.0d));

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME,
				XPlannerRepositoryUtils.formatSingleFractionHours(0.0d));

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME,
				XPlannerRepositoryUtils.formatSingleFractionHours(0.0d));

		// user story task ids
		setAttributeValues(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_IDS, getUserStoryTaskIds(
				userStoryData, client));
	}

	private static List<String> getUserStoryTaskIds(UserStoryData userStoryData, XPlannerClient client)
			throws CoreException {
		ArrayList<String> taskIds = new ArrayList<String>();

		org.xplanner.soap.TaskData[] userStoryTasks;
		try {
			userStoryTasks = client.getTasks(userStoryData.getId());
			for (org.xplanner.soap.TaskData taskData : userStoryTasks) {
				taskIds.add("" + taskData.getId()); //$NON-NLS-1$
			}

		} catch (RemoteException re) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, re.getMessage(), re));
		}

		return taskIds;
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
		taskData.setCompleted(completed != null && !("0".equals(completed))); //$NON-NLS-1$

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

		DateFormat timeDateFormat = new SimpleDateFormat(XPlannerAttributeMapper.TIME_DATE_FORMAT_STRING);

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
					timeDateFormat.format(lastUpdatedDate));
		}

		// est time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME, "" //$NON-NLS-1$
				+ userStory.getEstimatedHours());

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, "" //$NON-NLS-1$
				+ userStory.getActualHours());

		// est original hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME, "" //$NON-NLS-1$
				+ userStory.getEstimatedOriginalHours());

		// act time
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME, "" //$NON-NLS-1$
				+ userStory.getRemainingHours());

		// est adjusted estimated hours
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME, "" //$NON-NLS-1$
				+ userStory.getAdjustedEstimatedHours());

		// project name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_PROJECT_NAME, getProjectName(userStory,
				client));

		// iteration name
		setAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ITERATION_NAME, getIterationName(
				userStory, client));

		// user story task ids
		setAttributeValues(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_IDS, getUserStoryTaskIds(
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
		TaskAttribute taskAttribute = repositoryTaskData.getRoot().getMappedAttribute(attributeId);

		if (taskAttribute == null) {
			taskAttribute = repositoryTaskData.getRoot().createMappedAttribute(attributeId);
			taskAttribute.getMetaData().defaults();
			taskAttribute.getMetaData().setReadOnly(false);
			taskAttribute.getMetaData().setType(getType(attributeId));

			Attribute xplannerAttribute = XPlannerAttributeMapper.getAttribute(attributeId);
			if (xplannerAttribute != null) {
				taskAttribute.getMetaData().setLabel(xplannerAttribute.getDisplayName());
				taskAttribute.getMetaData().setReadOnly(xplannerAttribute.isReadOnly());
			}
		}

		if (taskAttribute != null) {
			repositoryTaskData.getAttributeMapper().setValue(taskAttribute, value == null ? "" : value); //$NON-NLS-1$
		}
	}

	public static void setAttributeValues(TaskData repositoryTaskData, String attributeId, List<String> values) {
		TaskAttribute attribute = repositoryTaskData.getRoot().getMappedAttribute(attributeId);

		if (attribute == null) {
			attribute = repositoryTaskData.getRoot().createMappedAttribute(attributeId);
			attribute.getMetaData().defaults();
			attribute.getMetaData().setReadOnly(false);
		}

		if (attribute != null) {
			repositoryTaskData.getAttributeMapper().setValues(attribute,
					values == null ? new ArrayList<String>() : values);
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

	public static String getStringValue(Double hours) {
		return getStandardHoursNumberFormat().format(hours);
	}

	public static double getDoubleValue(String hours) {
		double doubleValue = 0.0;

		try {
			doubleValue = getStandardHoursNumberFormat().parse(hours).doubleValue();
		} catch (ParseException e) {
			doubleValue = Double.valueOf(hours);
		}

		return doubleValue;
	}

	public static double getActualHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME);
		return getDoubleValue(hours);
	}

	public static double getRemainingHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME);
		return getDoubleValue(hours);
	}

	public static double getEstimatedHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME);
		return getDoubleValue(hours);
	}

	public static Double getAdjustedEstimatedHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData,
				XPlannerAttributeMapper.ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME);
		return getDoubleValue(hours);
	}

	public static Double getEstimatedOriginalHours(TaskData repositoryTaskData) {
		String hours = getAttributeValue(repositoryTaskData,
				XPlannerAttributeMapper.ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME);
		return getDoubleValue(hours);
	}

	public static Date getCreatedDate(TaskData repositoryTaskData) {
		Date createdDate = null;

		String dateString = getAttributeValue(repositoryTaskData, TaskAttribute.DATE_CREATION);
		try {
			DateFormat dateFormat = DateFormat.getDateInstance(XPlannerAttributeMapper.DATE_FORMAT_STYLE);
			createdDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
		}

		return createdDate;
	}

	public static String getProjectName(org.xplanner.soap.TaskData taskData, XPlannerClient client)
			throws CoreException {
		String projectName = Messages.XPlannerRepositoryUtils_NO_PROJECT_NAME;

		UserStoryData userStory;
		try {
			userStory = client.getUserStory(taskData.getStoryId());
			projectName = getProjectName(userStory, client);
		} catch (RemoteException e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, e.getMessage(), e));
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

	public static String getIterationName(org.xplanner.soap.TaskData taskData, XPlannerClient client)
			throws CoreException {
		String iterationName = Messages.XPlannerRepositoryUtils_NO_ITERATION_NAME;

		try {
			UserStoryData userStory = client.getUserStory(taskData.getStoryId());
			iterationName = getIterationName(userStory, client);
		} catch (RemoteException e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, e.getMessage(), e));
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
		return "1".equals(getAttributeValue(repositoryTaskData, XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED)); //$NON-NLS-1$
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

		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryUrl);
		if (taskRepository != null && !isRepositoryUrlValidated(taskRepository.getRepositoryUrl())) {
			validateRepository(taskRepository);
		}
	}

	public static void validateRepository(TaskRepository taskRepository) throws CoreException {
		AuthenticationCredentials repositoryCredentials = taskRepository.getCredentials(AuthenticationType.REPOSITORY);
		if (repositoryCredentials == null) {
			return;
		}

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
				taskRepository.getRepositoryUrl(), ""); //$NON-NLS-1$
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

	/**
	 * public for testing rounds to nearest .5 if roundToHalfHour is true, otherwise, keeps input as is with single
	 * fraction digit formatting
	 */
	public static String formatHours(Double hours, boolean roundToHalfHour) {
		Double updatedHours = hours;

		if (roundToHalfHour) {
			Double decimal = new Double(Math.floor(hours));
			Double fraction = hours - decimal;
			if (fraction == .5d || fraction == .0d) {
				updatedHours = hours;
			} else if (fraction > 0d && fraction < .25d) {
				updatedHours = decimal;
			} else if (fraction >= .25d && fraction < .5d) {
				updatedHours = decimal + .5f;
			} else if (fraction > .5d && fraction < .75d) {
				updatedHours = decimal + .5d;
			} else { // .75 and up
				updatedHours = decimal + 1;
			}
		}

		return XPlannerRepositoryUtils.formatSingleFractionHours(updatedHours);
	}

	/**
	 * public for testing Formats input as single digit fraction string
	 */
	public static String formatSingleFractionHours(Double updatedHours) {
		NumberFormat format = getHoursNumberFormat();
		return format.format(updatedHours);
	}

	public static Double getHoursValue(String hoursStringValue) {
		Double hoursValue = 0.0d;
		try {
			hoursValue = getHoursNumberFormat().parse(hoursStringValue).doubleValue();
		} catch (ParseException e) {
			XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
		}

		return hoursValue;
	}

	public static NumberFormat getStandardHoursNumberFormat() {
		return getHoursNumberFormat(Locale.US);
	}

	public static NumberFormat getHoursNumberFormat() {
		return getHoursNumberFormat(Locale.getDefault());
	}

	public static NumberFormat getHoursNumberFormat(Locale locale) {
		NumberFormat format = NumberFormat.getInstance(locale);
		format.setMaximumIntegerDigits(5);
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		format.setGroupingUsed(false);
		format.setParseIntegerOnly(false);
		return format;
	}
}
