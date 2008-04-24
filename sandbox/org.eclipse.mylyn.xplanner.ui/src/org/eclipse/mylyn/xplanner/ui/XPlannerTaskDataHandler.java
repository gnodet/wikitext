/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.net.Proxy;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.TimeEntryData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskDataHandler extends AbstractTaskDataHandler {
	private final AbstractAttributeFactory attributeFactory = new XPlannerAttributeFactory();

	public XPlannerTaskDataHandler() {
	}

	public RepositoryTaskData downloadTaskData(AbstractTask repositoryTask, TaskRepository repository,
			Proxy proxySettings) throws CoreException, LoginException {

		if (!(repositoryTask instanceof XPlannerTask)) {
			return null;
		}

		RepositoryTaskData repositoryTaskData = null;
		XPlannerTask xplannerTask = (XPlannerTask) repositoryTask;

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, xplannerTask, client);

		return repositoryTaskData;
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
			throws CoreException {

		// currently don't create new tasks
		return false;
	}

	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, UserStoryData userStory)
			throws CoreException {

		if (repository == null || data == null || userStory == null) {
			return false;
		}

		XPlannerRepositoryUtils.setupNewTaskAttributes(userStory, data);
		return true;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
		return attributeFactory;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getConnectorKind(), taskData.getTaskKind());
	}

	@Override
	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException {
		String resultId = null;

		try {
			resultId = postChangesToRepository(taskData);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerCorePlugin.ID, IStatus.ERROR,
					Messages.XPlannerOfflineTaskHandler_CANNOT_POST_DATA_TO_SERVER, null));
		}

		return resultId;
	}

	private String postChangesToRepository(RepositoryTaskData repositoryTaskData) throws CoreException {
		String error = null;
		String newTaskId = null;

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryTaskData.getConnectorKind(),
				repositoryTaskData.getRepositoryUrl());

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
					newTaskId = "" + taskData.getId();
				} else {
					taskData = client.getTask(Integer.valueOf(repositoryTaskData.getTaskId()).intValue());
				}

				if (taskData != null) {
					taskData.setName(repositoryTaskData.getSummary());
					taskData.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
					taskData.setEstimatedHours(Double.valueOf(repositoryTaskData.getAttribute(
							XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME).getValue()));
					taskData.setCompleted(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
					// assign to current person
					int personId = client.getCurrentPersonId();
					if (personId >= 0) {
						taskData.setAcceptorId(personId);
					}
					// set actual time
					Double currentActualHours = taskData.getActualHours();
					Double changedActualHours = Double.valueOf(repositoryTaskData.getAttribute(
							XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME).getValue());
					if (currentActualHours < changedActualHours) {
						TimeEntryData newTimeEntry = new TimeEntryData();
						newTimeEntry.setDuration(changedActualHours - currentActualHours);
						newTimeEntry.setPerson1Id(taskData.getAcceptorId());
						newTimeEntry.setTaskId(taskData.getId());
						newTimeEntry.setReportDate(Calendar.getInstance());
						client.addTimeEntry(newTimeEntry);
					}

					XPlannerRepositoryUtils.ensureTaskDataValid(taskData);
					client.update(taskData);
				} else {
					// otherwise check if a user story exists
					UserStoryData userStory = client.getUserStory(Integer.valueOf(repositoryTaskData.getTaskId())
							.intValue());
					if (userStory != null) {
						userStory.setName(repositoryTaskData.getSummary());
						userStory.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
						userStory.setActualHours(Double.valueOf(repositoryTaskData.getAttribute(
								XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME).getValue()));
						client.update(userStory);
					}
				}
			} catch (NumberFormatException e) {
				XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			} catch (RemoteException e) {
				XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			}
		}

		// for new tasks, return their id
		if (error == null && repositoryTaskData.isNew()) {
			error = newTaskId;
		}

		return error;
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {

		return XPlannerRepositoryUtils.createRepositoryTaskData(repository, taskId);
	}

	@Override
	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		Set<String> subIds = new HashSet<String>();
		RepositoryTaskAttribute attribute = taskData.getAttribute(XPlannerAttributeFactory.Attribute.SUBTASK_IDS.getCommonAttributeKey());
		if (attribute != null) {
			subIds.addAll(attribute.getValues());
		}
		return subIds;
	}
}
