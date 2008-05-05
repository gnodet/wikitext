/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.text.ParseException;
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttachmentHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.LegacyTaskDataCollector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationContext;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryConnector extends AbstractLegacyRepositoryConnector {

	//private static final String VERSION_SUPPORT = Messages.XPlannerRepositoryConnector_VERSION_SUPPORT;

	private final XPlannerTaskDataHandler offlineHandler;

	//private List<String> supportedVersions;

	/** Name initially given to new tasks. Public for testing */
	public static final String NEW_TASK_DESC = Messages.XPlannerRepositoryConnector_NEW_TASK_DESCRIPTION;

	private final TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	public XPlannerRepositoryConnector() {
		this.offlineHandler = new XPlannerTaskDataHandler();
	}

	@Override
	public String getLabel() {
		return XPlannerMylynUIPlugin.XPLANNER_CLIENT_LABEL;
	}

	@Override
	public String getConnectorKind() {
		return XPlannerMylynUIPlugin.REPOSITORY_KIND;
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		// not implemented yet
		return null;
	}

	@Override
	public AbstractTaskDataHandler getLegacyTaskDataHandler() {
		return offlineHandler;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	protected ITask makeTask(String repositoryUrl, String id, String summary) {
		return new XPlannerTask(repositoryUrl, id, summary);
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		AbstractTask task = null;

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerMylynUIPlugin.REPOSITORY_KIND,
				repositoryUrl);
		AbstractTask existingTask = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(), id);
		if (existingTask instanceof XPlannerTask) {
			task = existingTask;
		} else {
			try {
				task = createTask(repository, id);
			} catch (CoreException e) {
				XPlannerMylynUIPlugin.log(e, "", false); //$NON-NLS-1$
			}
		}

		return task;
	}

	private AbstractTask createTask(TaskRepository repository, String key) throws CoreException {
		XPlannerTask task = null;

		XPlannerRepositoryUtils.checkRepositoryValidated(repository.getRepositoryUrl());
		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		if (client != null) {
			try {
				// first check if taskdata exists
				TaskData taskData = client.getTask(Integer.valueOf(key).intValue());
				if (taskData != null) {
					task = createTask(taskData, taskData.getName(), String.valueOf(taskData.getId()), repository);
				} else {
					// otherwise check if a user story exists
					UserStoryData userStory = client.getUserStory(Integer.valueOf(key).intValue());
					if (userStory != null) {
						task = createTask(userStory, userStory.getName(), String.valueOf(userStory.getId()), repository);
					}
				}
			} catch (Exception e) {
				XPlannerMylynUIPlugin.log(e, "", false); //$NON-NLS-1$
			}
		}

		return task;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, AbstractRepositoryQuery repositoryQuery,
			TaskDataCollector resultCollector, SynchronizationContext event, IProgressMonitor monitor) {

		if (!(repositoryQuery instanceof XPlannerCustomQuery)) {
			return Status.OK_STATUS;
		}

		XPlannerCustomQuery xplannerCustomQuery = (XPlannerCustomQuery) repositoryQuery;

		try {
			XPlannerRepositoryUtils.validateRepository(repository);
			monitor.beginTask("Running query", IProgressMonitor.UNKNOWN);
			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

			if (xplannerCustomQuery.isMyCurrentTasks()) {
				return queryMyCurrentTasks(xplannerCustomQuery, client, repository, resultCollector);
			} else {
				return queryTasks(xplannerCustomQuery, client, repository, resultCollector);
			}

		} catch (final Exception e) {
			String reason = e.getLocalizedMessage();
			if ((reason == null) || (reason.length() == 0)) {
				reason = e.getClass().getName();
			}
			return new Status(IStatus.OK, XPlannerMylynUIPlugin.PLUGIN_ID, IStatus.ERROR, MessageFormat.format(
					Messages.XPlannerRepositoryConnector_PerformQueryFailure, reason), e);
		} finally {
			monitor.done();
		}

		//return Status.OK_STATUS;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

//TODO -- no longer used -- remove if really unnecessary	
//	private IStatus queryUserStories(XPlannerCustomQuery xplannerCustomQuery, 
//		XPlannerClient client, TaskRepository repository, QueryHitCollector resultCollector) throws RemoteException {
//		
//		int iterationId = xplannerCustomQuery.getContentId();
//		UserStoryData[] userStories;
//		
//		// check if want all or person's stories
//		if (xplannerCustomQuery.getPersonId() != XPlannerCustomQuery.INVALID_ID) {
//			int trackerId = xplannerCustomQuery.getPersonId();
//			userStories = client.getIterationUserStoriesForTracker(trackerId, iterationId);
//		}
//		else {
//			userStories = client.getUserStories(iterationId);
//		}
//		
//		return getUserStoryQueryHits(Arrays.asList(userStories), repository, xplannerCustomQuery, resultCollector);
//	}

	private IStatus queryMyCurrentTasks(XPlannerCustomQuery xplannerCustomQuery, XPlannerClient client,
			TaskRepository repository, TaskDataCollector resultCollector) throws RemoteException {

		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		int currentPersonId = client.getCurrentPersonId();

		xplannerTasks.addAll(Arrays.asList(client.getCurrentTasksForPerson(currentPersonId)));
		xplannerTasks.addAll(Arrays.asList(client.getPlannedTasksForPerson(currentPersonId)));

		return getTaskQueryHits(xplannerTasks, repository, xplannerCustomQuery, resultCollector);
	}

	private IStatus queryTasks(XPlannerCustomQuery xplannerCustomQuery, XPlannerClient client,
			TaskRepository repository, TaskDataCollector resultCollector) throws RemoteException {

		List<Integer> contentIds = xplannerCustomQuery.getContentIds();
		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		XPlannerCustomQuery.ContentIdType contentIdType = xplannerCustomQuery.getContentIdType();

		for (Integer contentId : contentIds) {
			addTasks(contentId, contentIdType, xplannerTasks, xplannerCustomQuery, client);
		}

		return getTaskQueryHits(xplannerTasks, repository, xplannerCustomQuery, resultCollector);
	}

	private void addTasks(int contentId, XPlannerCustomQuery.ContentIdType contentIdType, List<TaskData> xplannerTasks,
			XPlannerCustomQuery xplannerCustomQuery, XPlannerClient client) throws RemoteException {

		if (contentId == XPlannerCustomQuery.INVALID_ID) {
			return;
		}

		if (contentIdType == XPlannerCustomQuery.ContentIdType.PROJECT) {
			UserStoryData[] userStories = client.getUserStoriesForProject(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(xplannerCustomQuery, userStory.getId(), xplannerTasks, client);
			}
		} else if (contentIdType == XPlannerCustomQuery.ContentIdType.ITERATION) {
			UserStoryData[] userStories = client.getUserStories(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(xplannerCustomQuery, userStory.getId(), xplannerTasks, client);
			}
		} else if (contentIdType == XPlannerCustomQuery.ContentIdType.USER_STORY) {
			addUserStoryTasks(xplannerCustomQuery, contentId, xplannerTasks, client);
		}
	}

	private void addUserStoryTasks(XPlannerCustomQuery query, int userStoryId, List<TaskData> xplannerTasks,
			XPlannerClient client) {
		// check if want all or person's tasks
		if (query.getPersonId() != XPlannerCustomQuery.INVALID_ID) {
			int personId = query.getPersonId();
			xplannerTasks.addAll(Arrays.asList(client.getUserStoryTasksForPerson(personId, userStoryId)));
		} else {
			try {
				xplannerTasks.addAll(Arrays.asList(client.getTasks(userStoryId)));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private IStatus getTaskQueryHits(List<TaskData> tasks, TaskRepository repository, XPlannerCustomQuery query,
			TaskDataCollector resultCollector) {

		for (TaskData data : tasks) {
			String id = String.valueOf(data.getId());
			ITask task = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(), id);
			if (task != null) {
				updateTaskDetails(repository.getRepositoryUrl(), (XPlannerTask) task, data, false);
			}

//			try {
// HeB -- priority already set in task				
//				XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
//				UserStoryData userStory = client.getUserStory(data.getStoryId());
//				String priority = userStory == null ? AbstractTask.PriorityLevel.getDefault().toString() : 
//					"" + userStory.getPriority(); //$NON-NLS-1$
//				task.setPriority(priority);
			else {
				task = createTask(data, data.getName(), String.valueOf(data.getId()), repository);
			}

			try {
				RepositoryTaskData taskData = XPlannerRepositoryUtils.createRepositoryTaskData(repository,
						String.valueOf(data.getId()));
				taskData.setPartial(true);
				taskData.setSummary(data.getName());
				((LegacyTaskDataCollector) resultCollector).accept(taskData);
			} catch (CoreException e) {
				XPlannerMylynUIPlugin.log(e, "", false);
			}
//			} 
//			catch (Exception e) {
//				return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR, 
//						MessageFormat.format(Messages.XPlannerRepositoryConnector_ERROR_RETRIEVING_RESULTS, query.getRepositoryUrl()), e );
//			}
		}
		return Status.OK_STATUS;
	}

// no longer called -- calling routine commented out	
//	private IStatus getUserStoryQueryHits(List<UserStoryData> userStories, 
//		TaskRepository repository, XPlannerCustomQuery query, QueryHitCollector resultCollector) {
//		
//		for (UserStoryData data : userStories) {
//			String id = String.valueOf(data.getId());
//			ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), id);
//			if (task != null) {
//				updateTaskDetails(repository.getUrl(), (XPlannerTask) task, data, false);
//			}
//
//			XPlannerQueryHit hit = new XPlannerQueryHit(taskList, 
//				data.getDescription(), "" + data.getPriority(), query.getRepositoryUrl(),  //$NON-NLS-1$
//				id);
//			resultCollector.accept(hit);
//		}
//		
//		return Status.OK_STATUS;
//	}

//	@Override
//	public void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
//			IProgressMonitor monitor) throws CoreException {
//
//		if (repository != null && repositoryTask instanceof XPlannerTask) {
//			XPlannerTask xPlannerTask = (XPlannerTask) repositoryTask;
//			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
//			if (client != null) {
//				TaskData xplannerTaskData;
//				try {
//					xplannerTaskData = client.getTask(Integer.valueOf(xPlannerTask.getTaskId()).intValue());
//					if (xplannerTaskData != null) {
//						updateTaskDetails(repository.getRepositoryUrl(), xPlannerTask, xplannerTaskData, true);
//					} else {
//						UserStoryData userStoryData;
//						userStoryData = client.getUserStory(Integer.valueOf(xPlannerTask.getTaskId()).intValue());
//						if (userStoryData != null) {
//							updateTaskDetails(repository.getRepositoryUrl(), xPlannerTask, userStoryData, true);
//						}
//					}
//				} catch (Exception e) {
//					throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID, IStatus.ERROR,
//							Messages.XPlannerRepositoryConnector_ERROR_UPDATING_TASK, e));
//				}
//			}
//		}
//	}

	@Override
	public boolean updateTaskFromTaskData(TaskRepository repository, ITask repositoryTask,
			RepositoryTaskData repositoryTaskData) {

		if (repositoryTaskData != null) {
			XPlannerTask xplannerTask = (XPlannerTask) repositoryTask;
			String url = repository.getRepositoryUrl() + XPlannerMylynUIPlugin.TASK_URL_PREFIX
					+ repositoryTaskData.getTaskId();
			xplannerTask.setUrl(url);
			xplannerTask.setSummary(repositoryTaskData.getSummary());
			xplannerTask.setOwner(repositoryTaskData.getAssignedTo());
			xplannerTask.setPriority(repositoryTaskData.getAttributeValue(RepositoryTaskAttribute.PRIORITY));
			xplannerTask.setTaskKind(repositoryTaskData.getTaskKind());

			if (XPlannerRepositoryUtils.isCompleted(repositoryTaskData)) {
				try {
					xplannerTask.setCompletionDate( // guess that completed when last modified
					XPlannerAttributeFactory.TIME_DATE_FORMAT.parse(repositoryTaskData.getAttribute(
							RepositoryTaskAttribute.DATE_MODIFIED).getValue()));
				} catch (ParseException e) {
					StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
							Messages.XPlannerRepositoryConnector_COULD_NOT_CONVERT_TASK_DATE));
				}
			} else {
				xplannerTask.setCompletionDate(null);
			}
		}
		return false;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int index = url.indexOf(XPlannerMylynUIPlugin.DELIM_URL_PREFIX);
		if (index != -1) {
			return url.substring(0, index);
		}
		return null;
	}

	public void updateTaskDetails(String repositoryUrl, XPlannerTask task, TaskData taskData, boolean notifyOfChange) {
		if (taskData.getId() > 0) {
			String url = repositoryUrl + XPlannerMylynUIPlugin.TASK_URL_PREFIX + taskData.getId();
			task.setUrl(url);
			if (taskData.getName() != null) {
				task.setSummary(taskData.getName());
			}
		}
		if (taskData.isCompleted()) {
			task.setCompletionDate(taskData.getLastUpdateTime().getTime());
		} else {
			task.setCompletionDate(null);
		}

		if (notifyOfChange) {
			TasksUi.getTaskList().notifyTaskChanged(task, true);
		}
	}

	public static void updateTaskDetails(String repositoryUrl, XPlannerTask task, UserStoryData userStory,
			boolean notifyOfChange) {

		if (userStory.getId() > 0) {
			String url = repositoryUrl + XPlannerMylynUIPlugin.USER_STORY_URL_PREFIX + userStory.getId();
			task.setUrl(url);
			if (userStory.getName() != null) {
				task.setSummary(userStory.getName());
				task.setKey(String.valueOf(userStory.getId()));
			}
		}
		if (userStory.isCompleted()) {
			task.setCompletionDate(userStory.getLastUpdateTime().getTime());
		} else {
			task.setCompletionDate(null);
		}

		task.setPriority("" + userStory.getPriority()); //$NON-NLS-1$
		task.setTaskKind(XPlannerTask.Kind.USER_STORY.toString());

		if (notifyOfChange) {
			TasksUi.getTaskList().notifyTaskChanged(task, true);
		}
	}

	public XPlannerTask createTask(DomainData data, String name, String id, TaskRepository repository) {

		XPlannerTask task;

		ITask existingTask = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(), id);
		if (existingTask instanceof XPlannerTask) {
			task = (XPlannerTask) existingTask;
		} else {
			task = new XPlannerTask(repository.getRepositoryUrl(), id, name);
			task.setKind(data);
		}

		return task;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		XPlannerClientFacade.getDefault().refreshClientSettings(repository);
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		String taskUrl = null;

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerMylynUIPlugin.REPOSITORY_KIND,
				repositoryUrl);
		try {
			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			if (client != null) {
				TaskData taskData = client.getTask(Integer.valueOf(taskId).intValue());
				// first check if taskdata exists
				if (taskData != null) {
					if (taskData.getId() > 0) {
						taskUrl = repositoryUrl + XPlannerMylynUIPlugin.TASK_URL_PREFIX + taskData.getId();
					}
				} else {
					// otherwise check if a user story exists
					UserStoryData userStory = client.getUserStory(Integer.valueOf(taskId).intValue());
					if (userStory != null) {
						taskUrl = repositoryUrl + XPlannerMylynUIPlugin.USER_STORY_URL_PREFIX + userStory.getId();
					}
				}
			}
		} catch (Exception e) {
			XPlannerMylynUIPlugin.log(e, "", false); //$NON-NLS-1$
		}

		return taskUrl;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		String taskId = null;

		if (url == null) {
			return null;
		}

		int index = url.indexOf(XPlannerMylynUIPlugin.DELIM_URL_SUFFIX);
		if (index != -1) {
			taskId = url.substring(index + XPlannerMylynUIPlugin.DELIM_URL_SUFFIX.length());
		}

		return taskId;
	}

	public Set<ITask> getChangedSinceLastSync(TaskRepository repository, Set<ITask> tasks) throws CoreException {

		XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		if (client == null) {
			return Collections.emptySet();
		} else {
			Set<ITask> changedTasks = new HashSet<ITask>();
			try {
				XPlannerRepositoryUtils.validateRepository(repository);
				for (ITask task : tasks) {
					if (task instanceof XPlannerTask) {
						XPlannerTask xplannerTask = (XPlannerTask) task;
						TaskData taskData;
						taskData = client.getTask(Integer.valueOf(xplannerTask.getTaskId()).intValue());
						if (taskData != null) {
							Date lastUpdateTime = taskData.getLastUpdateTime().getTime();
							String lastSynchStamp = xplannerTask.getLastReadTimeStamp();
							Date lastSynchTime = null;
							if (lastSynchStamp != null && lastSynchStamp.length() > 0) {
								lastSynchTime = XPlannerAttributeFactory.TIME_DATE_FORMAT.parse(lastSynchStamp);
							}
							if (lastSynchTime == null || lastUpdateTime.after(lastSynchTime)) {
								changedTasks.add(task);
							}
						}
					}
				}
			} catch (CoreException ce) {
				throw ce;
			} catch (Exception e) {
				StatusHandler.fail(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID, e.getMessage()));
			}

			return changedTasks;
		}
	}

	@Override
	public void preSynchronization(SynchronizationContext event, IProgressMonitor monitor) throws CoreException {
		boolean changed = false;
		TaskRepository repository = event.taskRepository;
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Getting changed tasks", IProgressMonitor.UNKNOWN);

			if (repository.getSynchronizationTimeStamp() == null) {
				for (ITask task : event.tasks) {
					task.setStale(true);
				}
				changed = true;
			} else {
				Set<ITask> changedTasks = getChangedSinceLastSync(repository, event.tasks);
				for (ITask changedTask : changedTasks) {
					changedTask.setStale(true);
				}

				changed = changedTasks.size() > 0;
			}
		} finally {
			monitor.done();
		}

		event.performQueries = changed;
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return getLegacyTaskDataHandler().getTaskData(repository, taskId, monitor);
	}

	public TaskRepositoryLocationFactory getTaskRepositoryLocationFactory() {
		return taskRepositoryLocationFactory;
	}

	@Override
	public void postSynchronization(SynchronizationContext event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
			if (event.fullSynchronization) {
				event.taskRepository.setSynchronizationTimeStamp(getSynchronizationTimestamp(event.taskRepository,
						event.changedTasks));
			}
		} finally {
			monitor.done();
		}
	}

}
