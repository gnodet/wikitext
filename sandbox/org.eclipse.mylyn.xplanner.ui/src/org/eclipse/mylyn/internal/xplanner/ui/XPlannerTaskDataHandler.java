/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui;

import java.net.Proxy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.internal.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper.Attribute;
import org.eclipse.mylyn.internal.xplanner.ui.wizard.XPlannerTaskMapping;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.TimeEntryData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskDataHandler extends AbstractTaskDataHandler {
	private TaskAttributeMapper attributeFactory = null;

	public XPlannerTaskDataHandler() {
	}

	public org.eclipse.mylyn.tasks.core.data.TaskData downloadTaskData(ITask repositoryTask, TaskRepository repository,
			Proxy proxySettings) throws CoreException, LoginException {

		if (!repositoryTask.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND)) {
			return null;
		}

		org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = null;

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, repositoryTask, client);

		return repositoryTaskData;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {

		if (!(initializationData instanceof XPlannerTaskMapping)) {
			return false;
		}

		XPlannerTaskMapping xplannerTaskMapping = (XPlannerTaskMapping) initializationData;

		return initializeTaskData(repository, repositoryTaskData, xplannerTaskMapping.getUserStoryData());
	}

	public boolean initializeTaskData(TaskRepository repository,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData, UserStoryData userStory)
			throws CoreException {

		if (repository == null || repositoryTaskData == null || userStory == null) {
			return false;
		}

		XPlannerRepositoryUtils.setupNewTaskAttributes(userStory, repositoryTaskData);
		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		if (attributeFactory == null) {
			attributeFactory = new XPlannerAttributeMapper(taskRepository);
		}

		return attributeFactory;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData, Set<TaskAttribute> changedAttributes,
			IProgressMonitor monitor) throws CoreException {

		RepositoryResponse result = new RepositoryResponse();

		try {
			result = postChangesToRepository(repository, repositoryTaskData);
		} catch (Exception e) {
			String additionalInfo = e.getLocalizedMessage() != null && e.getLocalizedMessage().length() > 0 ? ": " //$NON-NLS-1$
					+ e.getLocalizedMessage() : ""; //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR, XPlannerCorePlugin.ID, IStatus.ERROR,
					Messages.XPlannerOfflineTaskHandler_CANNOT_POST_DATA_TO_SERVER + additionalInfo, e));
		}

		return result;
	}

	private RepositoryResponse postChangesToRepository(TaskRepository repository,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) throws CoreException {
		RepositoryResponse response = new RepositoryResponse();

		String error = null;
		String newTaskId = null;

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		if (client != null) {
			try {
				TaskData taskData = null;
				if (repositoryTaskData.isNew()) {
					taskData = XPlannerRepositoryUtils.createNewTaskData(repositoryTaskData, client);
					taskData = client.addTask(taskData);
					if (taskData.getCreatedDate() == null) {
						taskData.setCreatedDate(taskData.getLastUpdateTime());
					}
					newTaskId = "" + taskData.getId(); //$NON-NLS-1$
				} else {
					taskData = client.getTask(Integer.valueOf(repositoryTaskData.getTaskId()).intValue());
				}

				if (taskData != null) {
					taskData.setName(XPlannerRepositoryUtils.getName(repositoryTaskData));
					taskData.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
					taskData.setEstimatedHours(Double.valueOf(XPlannerRepositoryUtils.getEstimatedHours(repositoryTaskData)));
					taskData.setCompleted(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
					// assign to current person
					int personId = client.getCurrentPersonId();
					if (personId >= 0) {
						taskData.setAcceptorId(personId);
					}

					// set actual time
					Double currentActualHours = taskData.getActualHours();
					Double changedActualHours = Double.valueOf(XPlannerRepositoryUtils.getActualHours(repositoryTaskData));
					if (!repositoryTaskData.isNew() && currentActualHours < changedActualHours) {
						TimeEntryData newTimeEntry = new TimeEntryData();
						newTimeEntry.setDuration(changedActualHours - currentActualHours);
						newTimeEntry.setPerson1Id(taskData.getAcceptorId());
						newTimeEntry.setTaskId(taskData.getId());
						newTimeEntry.setReportDate(Calendar.getInstance());
						client.addTimeEntry(newTimeEntry);
						//
					}

					XPlannerRepositoryUtils.ensureTaskDataValid(taskData);
					client.update(taskData);

					response = new RepositoryResponse(repositoryTaskData.isNew() ? ResponseKind.TASK_CREATED
							: ResponseKind.TASK_UPDATED, "" + taskData.getId()); //$NON-NLS-1$
				} else {
					// otherwise check if a user story exists
					UserStoryData userStory = client.getUserStory(Integer.valueOf(repositoryTaskData.getTaskId())
							.intValue());
					if (userStory != null) {
						userStory.setName(XPlannerRepositoryUtils.getName(repositoryTaskData));
						userStory.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
						userStory.setActualHours(XPlannerRepositoryUtils.getActualHours(repositoryTaskData));
						client.update(userStory);
						response = new RepositoryResponse(ResponseKind.TASK_UPDATED, repositoryTaskData.getTaskId());
					}
				}
			} catch (NumberFormatException e) {
				XPlannerUiPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			} catch (RemoteException e) {
				XPlannerUiPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			}
		}

		// for new tasks, return their id
		if (error == null && repositoryTaskData.isNew()) {
			error = newTaskId;
		} else if (error != null) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerCorePlugin.ID, error));
		}

		return response;
	}

	public boolean initializeSubTaskData(TaskRepository repository, TaskData taskData, TaskData parentTaskData,
			IProgressMonitor monitor) throws CoreException {

		// currently not supported, but should be in future
		return false;
	}

	@Override
	public void migrateTaskData(TaskRepository taskRepository, org.eclipse.mylyn.tasks.core.data.TaskData taskData) {
		int version = 0;
		if (taskData.getVersion() != null) {
			try {
				version = Integer.parseInt(taskData.getVersion());
			} catch (NumberFormatException e) {
				// ignore
			}
		}

		if (version < 1) {
			TaskAttribute root = taskData.getRoot();
			List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(root.getAttributes().values());
			for (TaskAttribute attribute : attributes) {
				if (Attribute.DESCRIPTION.getCommonAttributeKey().equals(attribute.getId())) {
					attribute.getMetaData().setType(XPlannerRepositoryUtils.getType(attribute.getId()));
				}
			}
			taskData.setVersion("1"); //$NON-NLS-1$
		}
	}

}
