/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractAddExistingTaskWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasklist.ui.wizards.ExistingTaskWizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * Generic connector for web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_TYPE = "web";
	
	public static final String PROPERTY_NEW_TASK_URL = "newtaskurl";
	
	public static final String PROPERTY_TASK_PREFIX_URL = "taskprefixurl";

	
	public String getRepositoryType() {
		return REPOSITORY_TYPE;
	}
	
	public String getLabel() {
		return "Generic web-based repository";
	}
	
	@Override
	public String[] repositoryPropertyNames() {
		return new String[] { PROPERTY_NEW_TASK_URL, PROPERTY_TASK_PREFIX_URL};
	}
	
	public List<String> getSupportedVersions() {
		return Collections.emptyList();
	}
	
	public boolean canCreateNewTask() {
		return true;
	}

	public boolean canCreateTaskFromKey() {
		return true;
	}

	
	// Support
	
	public ITask createTaskFromExistingKey(TaskRepository repository, String id) {
		if(REPOSITORY_TYPE.equals(repository.getKind())) {
			String taskUrl = repository.getProperty(PROPERTY_TASK_PREFIX_URL) + id;
			String label = "#"+id;
			// TODO fetch the task description?
			
			String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), id);
			WebTask task = new WebTask(handle, label, id);
			task.setUrl(taskUrl);
			return task;
		}
		
		return null;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		// lookup repository using task prefix url 
		for (TaskRepository repository : MylarTaskListPlugin.getRepositoryManager().getAllRepositories()) {
			if(getRepositoryType().equals(repository.getKind())) {
				if(url.startsWith(repository.getProperty(PROPERTY_TASK_PREFIX_URL))) {
					return repository.getUrl();
				}
			}
		}
		
		return null;
	}
	
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, MultiStatus queryStatus) {
		// TODO
		return null;
	}

	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO
	}
	

	// UI
	
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new WebRepositorySettingsPage(this);
	}
	
	
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new AbstractAddExistingTaskWizard(repository) {
			private ExistingTaskWizardPage page;

			@Override
			public void addPages() {
				super.addPages();
				this.page = new ExistingTaskWizardPage();
				addPage(page);
			}

			protected String getTaskId() {
				return page.getTaskId();
			}
		};
	}

	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		// TODO
		return null;
	}

	
	public IWizard getNewQueryWizard(TaskRepository repository) {
		// TODO
		return null;
	}
	
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		// TODO
	}
	
	
	public IAttachmentHandler getAttachmentHandler() {
		// not supported
		return null;
	}

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository, Set<AbstractRepositoryTask> tasks) throws Exception {
		// not supported
		return Collections.emptySet();
	}

	public IOfflineTaskHandler getOfflineTaskHandler() {
		// not supported
		return null;
	}
	
}

