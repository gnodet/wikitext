/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.web;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;

/**
 * TODO: refactor into extension points?
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class WebRepositoryUi extends AbstractRepositoryConnectorUi {
	
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new WebRepositorySettingsPage(this);
	}

	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return new NewWebTaskWizard(taskRepository, WebRepositoryConnector.evaluateParams(taskRepository
				.getProperty(WebRepositoryConnector.PROPERTY_TASK_CREATION_URL), taskRepository));
	}
	
	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (query instanceof WebQuery) {
			return new WebQueryEditWizard(repository, query);
		} else {
			return new WebQueryWizard(repository);
		}
	}

	@Override
	public boolean hasRichEditor() {
		return false;
	}

	@Override
	public String getRepositoryType() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	@Override
	public boolean hasSearchPage() {
		return false;
	}	
}
