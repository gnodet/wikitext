/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

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
			MylarTaskListPlugin.getTaskListManager().getTaskList().addQuery(query);
			AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
			if (connector != null) {
				connector.synchronize(query, null);
			}
//			filter.refreshHits();
		} 

		return true;
	}

	
	
	public boolean canFinish() {
		if(queryPage.getNextPage() == null) {
			return queryPage.isPageComplete();
		}
		return queryPage.getNextPage().isPageComplete();
	}
	
	
}
