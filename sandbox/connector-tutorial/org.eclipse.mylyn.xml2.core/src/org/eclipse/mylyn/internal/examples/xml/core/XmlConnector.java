/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.examples.xml.core;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.examples.xml.core.util.XmlQueryFilter;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 */
public class XmlConnector extends AbstractRepositoryConnector {

	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static Map<TaskRepository, XmlClient> clientByRepository = new HashMap<TaskRepository, XmlClient>();

	private final XmlTaskDataHandler taskDataHandler;

	public synchronized XmlClient getClient(TaskRepository repository) {
		File location = new File(repository.getProperty(XmlCorePlugin.REPOSITORY_KEY_PATH));
		XmlClient client = clientByRepository.get(repository);
		if (client == null || !client.getLocation().equals(location)) {
			client = new XmlClient(location);
			clientByRepository.put(repository, client);
		}
		return client;
	}

	public XmlConnector() {
		taskDataHandler = new XmlTaskDataHandler();
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public String getConnectorKind() {
		return XmlCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public String getLabel() {
		return "File-based XML Tasks (example)";
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		File file = getClient(repository).getTask(taskId, monitor);
		// stall a while to allow the UI to update
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		return taskDataHandler.readTaskData(repository, file);
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		// ignore
		return null;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		// compares values
		//return getTaskMapping(taskData).hasChanges(task);

		// compare date on task (local state) and taskData (repository state)
		TaskAttribute attribute = taskData.getRoot().getAttribute(TaskAttribute.DATE_MODIFICATION);
		if (attribute != null) {
			Date dataModificationDate = taskData.getAttributeMapper().getDateValue(attribute);
			if (dataModificationDate != null) {
				Date taskModificationDate = task.getModificationDate();
				if (taskModificationDate != null) {
					return !taskModificationDate.equals(dataModificationDate);
				}
			}
		}
		return true;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		XmlQueryFilter filter = new XmlQueryFilter(query);

		File[] files = getClient(repository).listTasks(monitor);
		for (File file : files) {
			try {
				TaskData taskData = taskDataHandler.readTaskData(repository, file);
				// set to true if repository does not return full task details 
				//taskData.setPartial(true);

				if (filter.accepts(taskData)) {
					collector.accept(taskData);
				}
			} catch (CoreException e) {
				return new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind("Query failed: ''{0}''",
						e.getMessage()), e);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		getClient(repository).updateConfiguration(monitor);
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		getTaskMapping(taskData).applyTo(task);
	}

	@Override
	public TaskMapper getTaskMapping(TaskData taskData) {
		return new TaskMapper(taskData);
	}

	@Override
	public void preSynchronization(ISynchronizationSession session, IProgressMonitor monitor) throws CoreException {
		TaskRepository repository = session.getTaskRepository();
		File location = getClient(repository).getLocation();

		long lastModified = location.lastModified();
		// store time stamp for postSynchronization()
		session.setData(lastModified);

		String lastSynStampString = repository.getSynchronizationTimeStamp();
		if (lastSynStampString != null) {
			try {
				Date lastSynced = new SimpleDateFormat(DATE_PATTERN).parse(lastSynStampString);
				if (lastModified <= lastSynced.getTime()) {
					// nothing has changed
					//session.setNeedsPerformQueries(false);
				}
			} catch (ParseException e) {
			}
		}

		// trigger full refresh of tasks if required
//		for (ITask task : session.getTasks()) {
//			session.markStale(task);
//		}
	}

	@Override
	public void postSynchronization(ISynchronizationSession event, IProgressMonitor monitor) throws CoreException {
		if (event.getStatus() == null && event.getData() instanceof Long) {
			Date date = new Date((Long) event.getData());
			String dateString = new SimpleDateFormat(DATE_PATTERN).format(date);
			event.getTaskRepository().setSynchronizationTimeStamp(dateString);
		}
	}

}
