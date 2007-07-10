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
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.*;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskDataHandler extends AbstractTaskDataHandler {
	private AbstractAttributeFactory attributeFactory = new XPlannerAttributeFactory();
	
	public XPlannerTaskDataHandler(TaskList taskList) {
		//TODO -- tasklist?
	}
	
	public RepositoryTaskData downloadTaskData(AbstractTask repositoryTask, TaskRepository repository, Proxy proxySettings) 
		throws CoreException, LoginException {
		
		if (!(repositoryTask instanceof XPlannerTask)) {
			return null;
		}
		
		RepositoryTaskData repositoryTaskData = null;
		XPlannerTask xplannerTask = (XPlannerTask)repositoryTask;
		
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, xplannerTask, client); 
		
		return repositoryTaskData;
	}

	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
		throws CoreException {
		
		// currently don't create new tasks
		return false;
	}

	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
		return attributeFactory;
	}
	
	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getRepositoryKind(), taskData.getTaskKind());
	}

	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
		throws CoreException {
		String resultId = null;
		
		try {
			resultId = postChangesToRepository(taskData);
		} 
		catch (Exception e) {
			throw new CoreException(new Status(Status.ERROR,
					XPlannerCorePlugin.ID, Status.ERROR, Messages.XPlannerOfflineTaskHandler_CANNOT_POST_DATA_TO_SERVER, null));
		}
		
		return resultId;
	}

	private String postChangesToRepository(RepositoryTaskData repositoryTaskData) throws CoreException {
		String error = null;
		
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryTaskData.getRepositoryKind(), repositoryTaskData.getRepositoryUrl());
		
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		if (client != null) {
			try {
				// first check if taskdata exists
				TaskData taskData = client.getTask(Integer.valueOf(repositoryTaskData.getId()).intValue());
				if (taskData != null) {
					taskData.setName(repositoryTaskData.getSummary());
					taskData.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
					taskData.setEstimatedHours(Double.valueOf(
							repositoryTaskData.getAttribute(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME).getValue()));
					taskData.setCompleted(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
					client.update(taskData);
				}
				else {
					// otherwise check if a user story exists
					UserStoryData userStory = client.getUserStory(Integer.valueOf(repositoryTaskData.getId()).intValue());
					if (userStory != null) {
						userStory.setName(repositoryTaskData.getSummary());
						userStory.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
						userStory.setActualHours(Double.valueOf(
								repositoryTaskData.getAttribute(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME).getValue()));
						client.update(userStory);
					}
				}
			} 
			catch (NumberFormatException e) {
				XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			} 
			catch (RemoteException e) {
				XPlannerMylynUIPlugin.log(e.getCause(), "", false); //$NON-NLS-1$
				error = e.getMessage();
			}
		}
		
		return error;
	}

	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
		throws CoreException {
		
		return XPlannerRepositoryUtils.createRepositoryTaskData(repository, taskId);
	}
	
	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		Set<String> subIds = new HashSet<String>();
		RepositoryTaskAttribute attribute = taskData.getAttribute(XPlannerAttributeFactory.Attribute.SUBTASK_IDS.getCommonAttributeKey());
		if (attribute != null) {
			subIds.addAll(attribute.getValues());
		}
		return subIds;
	}
}
