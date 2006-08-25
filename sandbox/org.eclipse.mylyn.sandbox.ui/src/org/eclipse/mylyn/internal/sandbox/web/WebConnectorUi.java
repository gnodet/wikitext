/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class WebConnectorUi extends AbstractConnectorUi {
	
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new WebRepositorySettingsPage(this);
	}

	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new NewWebTaskWizard(taskRepository, taskRepository.getProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL)); 
	}

	
	public IWizard getNewQueryWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new WebQueryWizard(taskRepository);
	}
	
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					query.getRepositoryKind(), query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = null;
			if (query instanceof WebQuery) {
				wizard = new WebQueryEditWizard(repository, query);
			}

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Web Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
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

}
