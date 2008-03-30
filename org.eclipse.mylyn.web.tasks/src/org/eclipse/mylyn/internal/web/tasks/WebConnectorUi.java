/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylyn.tasks.ui.wizards.NewWebTaskWizard;

/**
 * TODO: refactor into extension points?
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class WebConnectorUi extends AbstractRepositoryConnectorUi {

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new WebRepositorySettingsPage(this);
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, TaskSelection taskSelection) {
		return new NewWebTaskWizard(taskRepository, WebRepositoryConnector.evaluateParams(
				taskRepository.getProperty(WebRepositoryConnector.PROPERTY_TASK_CREATION_URL), taskRepository));
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (query instanceof WebQuery) {
			return new WebQueryEditWizard(repository, query);
		} else {
			return new WebQueryWizard(repository);
		}
	}

	/**
	 * Task kind overlay, recommended to override with connector-specific overlay.
	 */
	@Override
	public ImageDescriptor getTaskKindOverlay(AbstractTask task) {
		if (!(task instanceof LocalTask) && (task instanceof WebTask)) {
			return TasksUiImages.OVERLAY_WEB;
		}
		return null;
	}

	@Override
	public String getConnectorKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	@Override
	public boolean hasSearchPage() {
		return false;
	}
}
