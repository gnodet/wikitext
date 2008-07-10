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
import java.util.Collection;
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
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryConnector extends AbstractRepositoryConnector {

	private final XPlannerTaskDataHandler taskDataHandler;

	/** Name initially given to new tasks. Public for testing */
	public static final String NEW_TASK_DESC = Messages.XPlannerRepositoryConnector_NEW_TASK_DESCRIPTION;

	private final TaskRepositoryLocationFactory taskRepositoryLocationFactory = new TaskRepositoryLocationFactory();

	public XPlannerRepositoryConnector() {
		this.taskDataHandler = new XPlannerTaskDataHandler();
	}

	@Override
	public String getLabel() {
		return XPlannerMylynUIPlugin.XPLANNER_CLIENT_LABEL;
	}

	@Override
	public String getConnectorKind() {
		return XPlannerCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		return taskDataHandler;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery repositoryQuery,
			TaskDataCollector resultCollector, ISynchronizationSession event, IProgressMonitor monitor) {

		monitor = Policy.monitorFor(monitor);
		monitor.beginTask("Running query", IProgressMonitor.UNKNOWN);
		try {
			XPlannerRepositoryUtils.validateRepository(repository);
			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);

			if (XPlannerTaskListMigrator.isMyCurrentTasks(repositoryQuery)) {
				return queryMyCurrentTasks(repositoryQuery, client, repository, resultCollector);
			} else {
				return queryTasks(repositoryQuery, client, repository, resultCollector);
			}

		} catch (final Exception e) {
			String reason = e.getLocalizedMessage();
			if ((reason == null) || (reason.length() == 0)) {
				reason = e.getClass().getName();
			}
			return new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, MessageFormat.format(
					Messages.XPlannerRepositoryConnector_PerformQueryFailure, reason), e);
		} finally {
			monitor.done();
		}
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	private IStatus queryMyCurrentTasks(IRepositoryQuery repositoryQuery, XPlannerClient client,
			TaskRepository repository, TaskDataCollector resultCollector) throws RemoteException {

		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		int currentPersonId = client.getCurrentPersonId();

		xplannerTasks.addAll(Arrays.asList(client.getCurrentTasksForPerson(currentPersonId)));
		xplannerTasks.addAll(Arrays.asList(client.getPlannedTasksForPerson(currentPersonId)));

		return getTaskQueryHits(xplannerTasks, repository, repositoryQuery, resultCollector);
	}

	private IStatus queryTasks(IRepositoryQuery repositoryQuery, XPlannerClient client, TaskRepository repository,
			TaskDataCollector resultCollector) throws RemoteException {

		List<Integer> contentIds = XPlannerTaskListMigrator.getContentIds(repositoryQuery);
		ArrayList<TaskData> xplannerTasks = new ArrayList<TaskData>();
		XPlannerTaskListMigrator.ContentIdType contentIdType = XPlannerTaskListMigrator.getContentIdType(repositoryQuery);

		for (Integer contentId : contentIds) {
			addTasks(contentId, contentIdType, xplannerTasks, repositoryQuery, client);
		}

		return getTaskQueryHits(xplannerTasks, repository, repositoryQuery, resultCollector);
	}

	private void addTasks(int contentId, XPlannerTaskListMigrator.ContentIdType contentIdType,
			List<TaskData> xplannerTasks, IRepositoryQuery repositoryQuery, XPlannerClient client)
			throws RemoteException {

		if (contentId == XPlannerAttributeMapper.INVALID_ID) {
			return;
		}

		if (contentIdType == XPlannerTaskListMigrator.ContentIdType.PROJECT) {
			UserStoryData[] userStories = client.getUserStoriesForProject(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(repositoryQuery, userStory.getId(), xplannerTasks, client);
			}
		} else if (contentIdType == XPlannerTaskListMigrator.ContentIdType.ITERATION) {
			UserStoryData[] userStories = client.getUserStories(contentId);
			for (UserStoryData userStory : userStories) {
				addUserStoryTasks(repositoryQuery, userStory.getId(), xplannerTasks, client);
			}
		} else if (contentIdType == XPlannerTaskListMigrator.ContentIdType.USER_STORY) {
			addUserStoryTasks(repositoryQuery, contentId, xplannerTasks, client);
		}
	}

	private void addUserStoryTasks(IRepositoryQuery repositoryQuery, int userStoryId, List<TaskData> xplannerTasks,
			XPlannerClient client) throws RemoteException {
		// check if want all or person's tasks
		int personId = XPlannerTaskListMigrator.getPersonId(repositoryQuery);
		if (personId != XPlannerAttributeMapper.INVALID_ID) {
			xplannerTasks.addAll(Arrays.asList(client.getUserStoryTasksForPerson(personId, userStoryId)));
		} else {
			xplannerTasks.addAll(Arrays.asList(client.getTasks(userStoryId)));
		}
	}

	private IStatus getTaskQueryHits(List<TaskData> tasks, TaskRepository repository, IRepositoryQuery repositoryQuery,
			TaskDataCollector resultCollector) {

		for (TaskData data : tasks) {
			try {
				org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerRepositoryUtils.createRepositoryTaskData(
						repository, String.valueOf(data.getId()));
				repositoryTaskData.setPartial(true);
				XPlannerRepositoryUtils.setAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY, data.getName());
				resultCollector.accept(repositoryTaskData);
			} catch (CoreException e) {
				XPlannerMylynUIPlugin.log(e, "", false);
			}
		}
		return Status.OK_STATUS;
	}

// no longer called -- calling routine commented out	
//	private IStatus getUserStoryQueryHits(List<UserStoryData> userStories, 
//		TaskRepository repository, XPlannerCustomQuery query, QueryHitCollector resultCollector) {
//		
//		for (UserStoryData data : userStories) {
//			String id = String.valueOf(data.getId());
//			ITask task = TasksUiPlugin.getTaskList().getTask(repository.getUrl(), id);
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
	public org.eclipse.mylyn.tasks.core.data.TaskData getTaskData(TaskRepository repository, String taskId,
			IProgressMonitor monitor) throws CoreException {
		return XPlannerRepositoryUtils.createRepositoryTaskData(repository, taskId);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, ITask task,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) {

		TaskMapper mapper = getTaskMapping(repositoryTaskData);
		mapper.applyTo(task);

		String url = repository.getRepositoryUrl() + XPlannerMylynUIPlugin.TASK_URL_PREFIX
				+ repositoryTaskData.getTaskId();
		task.setUrl(url);

		if (!repositoryTaskData.isPartial()) {
			task.setAttribute(XPlannerTaskListMigrator.KEY_TASK_UPDATE, XPlannerRepositoryUtils.getAttributeValue(
					repositoryTaskData, TaskAttribute.DATE_MODIFICATION));
		}
	}

	@Override
	public TaskMapper getTaskMapping(final org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) {
		return new TaskMapper(repositoryTaskData) {
			@Override
			public String getSummary() {
				return XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData, TaskAttribute.SUMMARY);
			}

			@Override
			public String getOwner() {
				return XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData, TaskAttribute.USER_ASSIGNED);
			}

			@Override
			public String getTaskKind() {
				String taskValue = XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData,
						TaskAttribute.TASK_KIND);
				if (taskValue == null || taskValue.length() == 0
						|| taskValue.equals(XPlannerAttributeMapper.DEFAULT_REPOSITORY_TASK_KIND)) {
					taskValue = XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString();
				}

				return taskValue;
			}

			@Override
			public Date getCompletionDate() {
				Date completionDate = null;

				if (XPlannerRepositoryUtils.isCompleted(repositoryTaskData)) {
					try {
						// guess that completed when last modified
						String lastModificationDateValue = XPlannerRepositoryUtils.getAttributeValue(
								repositoryTaskData, TaskAttribute.DATE_COMPLETION);
						if (lastModificationDateValue != null) {
							completionDate = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(lastModificationDateValue);
						}
					} catch (ParseException e) {
						StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
								Messages.XPlannerRepositoryConnector_COULD_NOT_CONVERT_TASK_DATE));
					}
				}

				return completionDate;
			}

			@Override
			public void setCompletionDate(Date dateCompleted) {
				// ignore
			}

			@Override
			public void setProduct(String product) {
				// ignore, set during task data initialization
			}

			@Override
			public Date getModificationDate() {
				Date modificationDate = null;

				try {
					String modificationDateValue = XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData,
							TaskAttribute.DATE_MODIFICATION);
					if (modificationDateValue != null) {
						modificationDate = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(modificationDateValue);
					}
				} catch (ParseException e) {
					StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
							Messages.XPlannerRepositoryConnector_COULD_NOT_CONVERT_TASK_DATE));
				}

				return modificationDate;
			}

		};
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

//HeB -- testing -- appears no longer necessary	
//	public void updateTaskDetails(String repositoryUrl, ITask task, TaskData taskData, boolean notifyOfChange) {
//		if (taskData.getId() > 0) {
//			String url = repositoryUrl + XPlannerMylynUIPlugin.TASK_URL_PREFIX + taskData.getId();
//			task.setUrl(url);
//			if (taskData.getName() != null) {
//				task.setSummary(taskData.getName());
//			}
//		}
//		if (taskData.isCompleted()) {
//			task.setCompletionDate(taskData.getLastUpdateTime().getTime());
//		} else {
//			task.setCompletionDate(null);
//		}
//
//		if (notifyOfChange) {
//			TasksUiInternal.getTaskList().notifyElementChanged(task);
//		}
//	}

//HeB -- testing -- appears no longer necessary	
//	public static void updateTaskDetails(String repositoryUrl, ITask task, UserStoryData userStory,
//			boolean notifyOfChange) {
//
//		if (userStory.getId() > 0) {
//			String url = repositoryUrl + XPlannerMylynUIPlugin.USER_STORY_URL_PREFIX + userStory.getId();
//			task.setUrl(url);
//			if (userStory.getName() != null) {
//				task.setSummary(userStory.getName());
//				task.setTaskKey(String.valueOf(userStory.getId()));
//			}
//		}
//		if (userStory.isCompleted()) {
//			task.setCompletionDate(userStory.getLastUpdateTime().getTime());
//		} else {
//			task.setCompletionDate(null);
//		}
//
//		task.setPriority("" + userStory.getPriority()); //$NON-NLS-1$
//		task.setTaskKind(XPlannerAttributeFactory.XPlannerTaskKind.USER_STORY.toString());
//
//		if (notifyOfChange) {
//			TasksUiInternal.getTaskList().notifyElementChanged(task);
//		}
//	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		XPlannerClientFacade.getDefault().refreshClientSettings(repository);
	}

	// TODO this method should not access the network, it is safer to return null if the URL can not be constructed
	// would it work to change the implementation to return repositoryUrl + XPlannerMylynUIPlugin.USER_STORY_URL_PREFIX + taskId?
	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		String taskUrl = null;

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(XPlannerCorePlugin.CONNECTOR_KIND,
				repositoryUrl);
		try {
			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			if (client != null) {
				TaskData taskData = client.getTask(Integer.valueOf(taskId).intValue());
				// first check if TaskData exists
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
					if (task.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND)) {
						TaskData taskData;
						taskData = client.getTask(Integer.valueOf(task.getTaskId()).intValue());
						if (taskData != null) {
							Date lastUpdateTime = taskData.getLastUpdateTime().getTime();
							String lastSynchStamp = repository.getSynchronizationTimeStamp();
							Date lastSynchTime = null;
							if (lastSynchStamp != null && lastSynchStamp.length() > 0) {
								lastSynchTime = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(lastSynchStamp);
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
				StatusHandler.fail(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN, e.getMessage()));
			}

			return changedTasks;
		}
	}

	@Override
	public void preSynchronization(ISynchronizationSession session, IProgressMonitor monitor) throws CoreException {
		boolean changed = false;
		TaskRepository repository = session.getTaskRepository();
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Getting changed tasks", IProgressMonitor.UNKNOWN);

			if (repository.getSynchronizationTimeStamp() == null) {
				for (ITask task : session.getTasks()) {
					session.markStale(task);
				}
				changed = true;
			} else {
				Set<ITask> changedTasks = getChangedSinceLastSync(repository, session.getTasks());
				for (ITask changedTask : changedTasks) {
					session.markStale(changedTask);
				}

				changed = changedTasks.size() > 0;
			}
		} finally {
			monitor.done();
		}

		session.setNeedsPerformQueries(changed);
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
			if (event.isFullSynchronization() && event.getStatus() == null) {
				event.getTaskRepository().setSynchronizationTimeStamp(getSynchronizationTimestamp(event));
			}
		} finally {
			monitor.done();
		}
	}

	private String getSynchronizationTimestamp(ISynchronizationSession event) throws CoreException {

		String mostRecentTimeStamp = event.getTaskRepository().getSynchronizationTimeStamp();
		Date mostRecent = null;
		try {
			mostRecent = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(mostRecentTimeStamp);
		} catch (ParseException e) {
			; // don't do anything if invalid sync time stamp
		}

		for (ITask task : event.getChangedTasks()) {
			Date taskModifiedDate = task.getModificationDate();
			if (taskModifiedDate != null && (mostRecent == null || taskModifiedDate.after(mostRecent))) {
				mostRecent = taskModifiedDate;
				mostRecentTimeStamp = XPlannerAttributeMapper.TIME_DATE_FORMAT.format(mostRecent);
			}
		}

		return mostRecentTimeStamp;
	}

	public TaskRepositoryLocationFactory getTaskRepositoryLocationFactory() {
		return taskRepositoryLocationFactory;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task,
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) {
		TaskMapper scheme = getTaskMapping(repositoryTaskData);
		if (repositoryTaskData.isPartial()) {
			Date repositoryDate = scheme.getModificationDate();
			Date localDate = task.getModificationDate();
			if (repositoryDate != null && repositoryDate.equals(localDate)) {
				return false;
			}
			return true;
		} else {
			Date repositoryDate = scheme.getModificationDate();
			Date localDate = null;
			String updateDateString = task.getAttribute(XPlannerTaskListMigrator.KEY_TASK_UPDATE);
			if (updateDateString != null) {
				try {
					localDate = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(updateDateString);
				} catch (ParseException e) {
					// ignore
				}
			}
			if (repositoryDate != null && repositoryDate.equals(localDate)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public Collection<TaskRelation> getTaskRelations(org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData) {
		List<TaskRelation> relations = new ArrayList<TaskRelation>();
		TaskAttribute attribute = repositoryTaskData.getRoot().getAttribute(
				XPlannerAttributeMapper.Attribute.SUBTASK_IDS.getCommonAttributeKey());
		if (attribute != null) {
			for (String taskId : attribute.getValues()) {
				relations.add(TaskRelation.subtask(taskId));
			}
		}

		return relations;
	}
}
