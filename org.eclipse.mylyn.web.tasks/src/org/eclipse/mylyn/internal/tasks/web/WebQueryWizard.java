/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.web;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Wizard used to create query for web based connector
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryWizard extends Wizard {

	private static final String TITLE = "New Web Query";
	
	private final TaskRepository repository;

	private WebQueryWizardPage queryPage;

	public WebQueryWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE); 
	}

	@Override
	public void addPages() {
		queryPage = new WebQueryWizardPage(repository);
		queryPage.setWizard(this);
		addPage(queryPage);
	}

	@Override
	public boolean performFinish() {

		AbstractRepositoryQuery query = queryPage.getQuery();
		if (query != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
			if (connector != null) {
				TasksUiPlugin.getSynchronizationManager().synchronize(connector, query, null, true);
			}
//			filter.refreshHits();
		} 

		return true;
	}

	
	
	@Override
	public boolean canFinish() {
		if(queryPage.getNextPage() == null) {
			return queryPage.isPageComplete();
		}
		return queryPage.getNextPage().isPageComplete();
	}
	
	
}
