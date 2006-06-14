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
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * Generic connector for web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnector extends AbstractRepositoryConnector {

	public String getLabel() {
		return "Generic web-based repository";
	}
	
	public String getRepositoryType() {
		return "web";
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
		// TODO
		return null;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		// TODO
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
		// TODO
		return null;
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

