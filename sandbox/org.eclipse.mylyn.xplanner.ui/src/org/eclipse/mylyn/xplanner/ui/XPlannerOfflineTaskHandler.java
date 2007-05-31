/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import java.net.Proxy;
import java.rmi.RemoteException;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerOfflineTaskHandler implements ITaskDataHandler {
	private AbstractAttributeFactory attributeFactory = new XPlannerAttributeFactory();
	
	public XPlannerOfflineTaskHandler(TaskList taskList) {
		//TODO -- tasklist?
	}
	
	public RepositoryTaskData downloadTaskData(AbstractRepositoryTask repositoryTask, TaskRepository repository, Proxy proxySettings) 
		throws CoreException, LoginException {
		
		if (!(repositoryTask instanceof XPlannerTask)) {
			return null;
		}
		
		RepositoryTaskData repositoryTaskData = null;
		XPlannerTask xplannerTask = (XPlannerTask)repositoryTask;
		
		XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
		repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, xplannerTask, server); 
		
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
	
	public String postTaskData(TaskRepository repository, final RepositoryTaskData repositoryTaskData) throws CoreException {
		String resultId = null;
		
		try {
			resultId = postChangesToRepository(repositoryTaskData);
		} 
		catch (Exception e) {
			throw new CoreException(new Status(Status.ERROR,
					XPlannerCorePlugin.ID, Status.ERROR, "Cannot post XPlanner task data to XPlanner server", null));
		}
		
		return resultId;
	}

	private String postChangesToRepository(RepositoryTaskData repositoryTaskData) throws CoreException {
		String error = null;
		
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				repositoryTaskData.getRepositoryKind(), repositoryTaskData.getRepositoryUrl());
		
		XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
		if (server != null) {
			try {
				// first check if taskdata exists
				TaskData taskData = server.getTask(Integer.valueOf(repositoryTaskData.getId()).intValue());
				if (taskData != null) {
					taskData.setName(repositoryTaskData.getSummary());
					taskData.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
					taskData.setEstimatedHours(Double.valueOf(
							repositoryTaskData.getAttribute(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME).getValue()));
					taskData.setCompleted(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
					server.update(taskData);
				}
				else {
					// otherwise check if a user story exists
					UserStoryData userStory = server.getUserStory(Integer.valueOf(repositoryTaskData.getId()).intValue());
					if (userStory != null) {
						userStory.setName(repositoryTaskData.getSummary());
						userStory.setDescription(XPlannerRepositoryUtils.getDescription(repositoryTaskData));
						userStory.setActualHours(Double.valueOf(
								repositoryTaskData.getAttribute(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME).getValue()));
						server.update(userStory);
					}
				}
			} 
			catch (NumberFormatException e) {
				XPlannerMylarUIPlugin.log(e.getCause(), "", false);
				error = e.getMessage();
			} 
			catch (RemoteException e) {
				XPlannerMylarUIPlugin.log(e.getCause(), "", false);
				error = e.getMessage();
			}
		}
		
		return error;
	}

	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId) throws CoreException {
		RepositoryTaskData repositoryTaskData = null;
		
		XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
		ITask existingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), taskId);
		if (existingTask instanceof XPlannerTask) {
			repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, 
				(XPlannerTask)existingTask, server);
		}
		else {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getKind());
			ITask newTask = connector.createTaskFromExistingKey(repository, taskId);
			repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, 
					(XPlannerTask)newTask, server);
		}
		return repositoryTaskData;
	}
	
}
