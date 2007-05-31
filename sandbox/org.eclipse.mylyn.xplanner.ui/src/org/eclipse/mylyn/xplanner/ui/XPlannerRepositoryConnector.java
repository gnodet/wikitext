/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.tasks.core.RepositoryTaskHandleUtil;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskDataHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

import org.eclipse.mylar.xplanner.wsdl.soap.domain.DomainData;

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class XPlannerRepositoryConnector extends AbstractRepositoryConnector {

	private static final String VERSION_SUPPORT = Messages.XPlannerRepositoryConnector_VERSION_SUPPORT;

	private XPlannerOfflineTaskHandler offlineHandler;
	
	private List<String> supportedVersions;

	/** Name initially given to new tasks. Public for testing */
	public static final String NEW_TASK_DESC = Messages.XPlannerRepositoryConnector_NEW_TASK_DESCRIPTION;

	public void init(TaskList taskList) {
		super.init(taskList);
		this.offlineHandler = new XPlannerOfflineTaskHandler(taskList);
	}

	public String getLabel() {
		return XPlannerMylarUIPlugin.XPLANNER_CLIENT_LABEL;
	}

	public String getRepositoryType() {
		return XPlannerMylarUIPlugin.REPOSITORY_KIND;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// not implemented
		return null;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		return offlineHandler;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public AbstractRepositoryTask createTaskFromExistingKey(TaskRepository repository, String key)
		throws CoreException {
		
		XPlannerRepositoryUtils.checkRepositoryValidated(repository.getUrl());
		XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
		if (server != null) {
			XPlannerTask task = null;
			try {
				// first check if taskdata exists
				TaskData taskData = server.getTask(Integer.valueOf(key).intValue());
				if (taskData != null) {
					task = createTask(taskData, taskData.getName(), String.valueOf(taskData.getId()), repository);
					updateTaskDetails(repository.getUrl(), task, taskData, true);
				}
				else {
					// otherwise check if a user story exists
					UserStoryData userStory = server.getUserStory(Integer.valueOf(key).intValue());
					if (userStory != null) {
						task = createTask(userStory, userStory.getName(), String.valueOf(userStory.getId()), repository);
						updateTaskDetails(repository.getUrl(), task, userStory, true);
					}
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 

			if (task != null) {
				TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
				return task;
			}

		}
		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery repositoryQuery, TaskRepository repository,
			IProgressMonitor monitor, QueryHitCollector resultCollector) {
		
		if (!(repositoryQuery instanceof XPlannerCustomQuery)) {
			return Status.OK_STATUS;
		}

		XPlannerCustomQuery xplannerCustomQuery = (XPlannerCustomQuery) repositoryQuery;


		try {
			XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);

			if (xplannerCustomQuery.isMyCurrentTasks()) {
				return queryMyCurrentTasks(xplannerCustomQuery, server, repository, resultCollector);
			}
			else {
				return queryTasks(xplannerCustomQuery, server, repository, resultCollector);
			}
			
		}
		catch (final Exception e) {
			String reason = e.getLocalizedMessage();
			if ((reason == null) || (reason.length() == 0)) {
				reason = e.getClass().getName();
			}
			return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR, 
				MessageFormat.format(Messages.XPlannerRepositoryConnector_PerformQueryFailure, reason), e );
		}
		
		//return Status.OK_STATUS;
	}
	
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

//TODO -- no longer used -- remove if really unnecessary	
//	private IStatus queryUserStories(XPlannerCustomQuery xplannerCustomQuery, 
//		XPlannerServer server, TaskRepository repository, QueryHitCollector resultCollector) throws RemoteException {
//		
//		int iterationId = xplannerCustomQuery.getContentId();
//		UserStoryData[] userStories;
//		
//		// check if want all or person's stories
//		if (xplannerCustomQuery.getPersonId() != XPlannerCustomQuery.INVALID_ID) {
//			int trackerId = xplannerCustomQuery.getPersonId();
//			userStories = server.getIterationUserStoriesForTracker(trackerId, iterationId);
//		}
//		else {
//			userStories = server.getUserStories(iterationId);
//		}
//		
//		return getUserStoryQueryHits(Arrays.asList(userStories), repository, xplannerCustomQuery, resultCollector);
//	}

	private IStatus queryMyCurrentTasks(XPlannerCustomQuery xplannerCustomQuery,
		XPlannerServer server, TaskRepository repository, QueryHitCollector resultCollector) throws RemoteException {
		
		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		int currentPersonId = server.getCurrentPersonId();
		
		xplannerTasks.addAll(Arrays.asList(server.getCurrentTasksForPerson(currentPersonId)));
		xplannerTasks.addAll(Arrays.asList(server.getPlannedTasksForPerson(currentPersonId)));
		
		return getTaskQueryHits(xplannerTasks, repository, xplannerCustomQuery, resultCollector);
	}

	private IStatus queryTasks(XPlannerCustomQuery xplannerCustomQuery, 
		XPlannerServer server, TaskRepository repository, QueryHitCollector resultCollector) throws RemoteException {
		
		List<Integer> contentIds = xplannerCustomQuery.getContentIds();
		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		XPlannerCustomQuery.ContentIdType contentIdType = xplannerCustomQuery.getContentIdType();
		
		for (Integer contentId : contentIds) {
			addTasks(contentId, contentIdType, xplannerTasks, xplannerCustomQuery, server);
		}
		
		return getTaskQueryHits(xplannerTasks, repository, xplannerCustomQuery, resultCollector);
	}

	private void addTasks(int contentId, 
		XPlannerCustomQuery.ContentIdType contentIdType, List<TaskData> xplannerTasks, 
		XPlannerCustomQuery xplannerCustomQuery, XPlannerServer server) throws RemoteException {
		
		if (contentId == XPlannerCustomQuery.INVALID_ID) {
			return;
		}
		
		if (contentIdType == XPlannerCustomQuery.ContentIdType.PROJECT) {
			UserStoryData[] userStories = server.getUserStoriesForProject(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(xplannerCustomQuery, userStory.getId(), xplannerTasks, server);
			}
		}
		else if (contentIdType == XPlannerCustomQuery.ContentIdType.ITERATION) {
			UserStoryData[] userStories = server.getUserStories(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(xplannerCustomQuery, userStory.getId(), xplannerTasks, server);
			}
		}
		else if (contentIdType == XPlannerCustomQuery.ContentIdType.USER_STORY) {
			addUserStoryTasks(xplannerCustomQuery, contentId, xplannerTasks, server);
		}
	}

	private void addUserStoryTasks(XPlannerCustomQuery query, int userStoryId, 
		List<TaskData> xplannerTasks, XPlannerServer server) {
		// check if want all or person's tasks
		if (query.getPersonId() != XPlannerCustomQuery.INVALID_ID) {
			int personId = query.getPersonId();
			xplannerTasks.addAll(Arrays.asList(server.getUserStoryTasksForPerson(personId, userStoryId)));
		}
		else {
			try {
				xplannerTasks.addAll(Arrays.asList(server.getTasks(userStoryId)));
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private IStatus getTaskQueryHits(List<TaskData> tasks, 
		TaskRepository repository, XPlannerCustomQuery query, QueryHitCollector resultCollector) {
		
		for (TaskData data : tasks) {
			String id = String.valueOf(data.getId());
			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), id);
			if (!(task instanceof XPlannerTask)) {
				task = createTask(data, data.getName(), id, repository);
			}
			updateTaskDetails(repository.getUrl(), (XPlannerTask) task, data, false);

			XPlannerQueryHit hit = new XPlannerQueryHit(
				taskList, task.getSummary(), task.getPriority(), query.getRepositoryUrl(), 
				id, (XPlannerTask) task);
			try {
				resultCollector.accept(hit);
				} catch (CoreException e) {
				return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR, 
						MessageFormat.format(Messages.XPlannerRepositoryConnector_ERROR_RETRIEVING_RESULTS, query.getRepositoryUrl()), e );
			}
		}
		return Status.OK_STATUS;
	}
	
	private IStatus getUserStoryQueryHits(List<UserStoryData> userStories, 
		TaskRepository repository, XPlannerCustomQuery query, QueryHitCollector resultCollector) {
		
		for (UserStoryData data : userStories) {
			String id = String.valueOf(data.getId());
			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), id);
			if (!(task instanceof XPlannerTask)) {
				task = createTask(data, data.getName(), id, repository);
			}
			updateTaskDetails(repository.getUrl(), (XPlannerTask) task, data, false);

			XPlannerQueryHit hit = new XPlannerQueryHit(taskList, 
				task.getSummary(), task.getPriority(), query.getRepositoryUrl(), 
				id, (XPlannerTask) task);
			try {
				resultCollector.accept(hit);
			} 
			catch (CoreException e) {
				return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR, 
						MessageFormat.format(Messages.XPlannerRepositoryConnector_ERROR_RETRIEVING_RESULTS, query.getRepositoryUrl()), e );
			}
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	public void updateTask(TaskRepository repository, AbstractRepositoryTask repositoryTask) throws CoreException {
		if (repository != null && repositoryTask instanceof XPlannerTask) {
			XPlannerTask xPlannerTask = (XPlannerTask) repositoryTask;
			XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
			if (server != null) {
				TaskData xplannerTaskData;
				try {
					xplannerTaskData = server.getTask(Integer.valueOf(xPlannerTask.getKey()).intValue());
					if (xplannerTaskData != null) {
						updateTaskDetails(repository.getUrl(), xPlannerTask, xplannerTaskData, true);
					}
				}
				catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, 
							XPlannerMylarUIPlugin.PLUGIN_ID, IStatus.ERROR, Messages.XPlannerRepositoryConnector_ERROR_UPDATING_TASK, e));
				}
			}
		}
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.indexOf(XPlannerMylarUIPlugin.DELIM_URL_PREFIX);
		if (index != -1) {
			return url.substring(0, index);
		}
		return null;
	}

	public void updateTaskDetails(String repositoryUrl, XPlannerTask task, TaskData taskData, boolean notifyOfChange) {
		if (taskData.getId() > 0) {
			String url = repositoryUrl + XPlannerMylarUIPlugin.TASK_URL_PREFIX + taskData.getId();
			task.setTaskUrl(url);
			if (taskData.getName() != null) {
				task.setDescription(taskData.getName());
				task.setKey(String.valueOf(taskData.getId()));
			}
		}
		if (taskData.isCompleted()) {
			task.setCompleted(true);
			task.setCompletionDate(taskData.getLastUpdateTime().getTime());
		} 
		else {
			task.setCompleted(false);
			task.setCompletionDate(null);
		}

		if (notifyOfChange) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
		}
	}
	
	public static void updateTaskDetails(String repositoryUrl, XPlannerTask task, 
		UserStoryData userStory, boolean notifyOfChange) {
		
		if (userStory.getId() > 0) {
			String url = repositoryUrl + XPlannerMylarUIPlugin.USER_STORY_URL_PREFIX + userStory.getId();
			task.setTaskUrl(url);
			if (userStory.getName() != null) {
				task.setDescription(userStory.getName());
				task.setKey(String.valueOf(userStory.getId()));
			}
		}
		if (userStory.isCompleted()) {
			task.setCompleted(true);
			task.setCompletionDate(userStory.getLastUpdateTime().getTime());
		} else {
			task.setCompleted(false);
			task.setCompletionDate(null);
		}
	    
		task.setPriority("" + userStory.getPriority()); //$NON-NLS-1$
		task.setKind(XPlannerTask.Kind.USER_STORY.toString());

		if (notifyOfChange) {
			TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
		}
	}
	
	public XPlannerTask createTask(DomainData data, String name, 
		String id, TaskRepository repository) {
		
		XPlannerTask task;
		
		String handleIdentifier = RepositoryTaskHandleUtil.getHandle(repository.getUrl(), id);
		ITask existingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
			handleIdentifier);
		if (existingTask instanceof XPlannerTask) {
			task = (XPlannerTask) existingTask;
		} 
		else {
			task = new XPlannerTask(repository.getUrl(), id, name, true);
			task.setKey(String.valueOf(data.getId()));
			task.setKind(data);
			TasksUiPlugin.getTaskListManager().getTaskList().addTask(task);
		}
		
		RepositoryTaskData taskData = null;
		try {
			XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
			taskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository, task, server);
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		if (taskData != null) {
			task.setTaskData(taskData);
		}

		return task;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		XPlannerServerFacade.getDefault().refreshServerSettings(repository);
	}
	
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		String taskUrl = null;

		TaskRepository repository = TasksUiPlugin.getRepositoryManager()
				.getRepository(XPlannerMylarUIPlugin.REPOSITORY_KIND, repositoryUrl);
		try {
			XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
			if (server != null) {
				TaskData taskData = server.getTask(Integer.valueOf(taskId).intValue());
				// first check if taskdata exists
				if (taskData != null) {
					if (taskData.getId() > 0) {
						taskUrl = repositoryUrl + XPlannerMylarUIPlugin.TASK_URL_PREFIX
								+ taskData.getId();
					}
				}
				else {
					// otherwise check if a user story exists
					UserStoryData userStory = server.getUserStory(Integer.valueOf(taskId)
							.intValue());
					if (userStory != null) {
						taskUrl = repositoryUrl + XPlannerMylarUIPlugin.USER_STORY_URL_PREFIX + userStory.getId();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return taskUrl;
	}
	
	public String getTaskIdFromTaskUrl(String url) {
		String taskId = null;
		
		if (url == null) {
			return null;
		}
		
		int index = url.indexOf(XPlannerMylarUIPlugin.DELIM_URL_SUFFIX);
		if (index != -1) {
			taskId = url.substring(index + XPlannerMylarUIPlugin.DELIM_URL_SUFFIX.length());
		}
		
		return taskId;
	}
	
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws CoreException {
	
		XPlannerServer server = XPlannerServerFacade.getDefault().getXPlannerServer(repository);
		if (server == null) {
			return Collections.emptySet();
		} 
		else {
			Set<AbstractRepositoryTask> changedTasks = new HashSet<AbstractRepositoryTask>();
			for (AbstractRepositoryTask task : tasks) {
				if (task instanceof XPlannerTask) {
					XPlannerTask xplannerTask = (XPlannerTask) task;
					TaskData taskData;
					try {
						taskData = server.getTask(Integer.valueOf(xplannerTask.getKey()).intValue());
						if (taskData != null) {
							Date lastUpdateTime = taskData.getLastUpdateTime().getTime();
							String lastSynchStamp = xplannerTask.getLastSyncDateStamp();
							Date lastSynchTime = null;
							if (lastSynchStamp != null && lastSynchStamp.length() > 0) {
								lastSynchTime = XPlannerAttributeFactory.TIME_DATE_FORMAT.parse(lastSynchStamp);
							}
							if (lastSynchTime == null || lastUpdateTime.after(lastSynchTime)) {
								changedTasks.add(task);
							}	
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			return changedTasks;
		}
	}

}
