/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.wizards.CommonAddExistingTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: refactor wizards into extension points
 * 
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public abstract class AbstractRepositoryConnectorUi {
	
	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();
		
	public abstract AbstractRepositorySettingsPage getSettingsPage();

	/**
	 * @param repository
	 * @param queryToEdit can be null
	 */
	public abstract IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery queryToEdit);
	
	public abstract IWizard getNewTaskWizard(TaskRepository taskRepository);
		
	public abstract boolean hasRichEditor();
			
	public abstract boolean hasSearchPage();
	
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(),
					query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = this.getQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Repository Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}
		
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		return new CommonAddExistingTaskWizard(repository);
	}

	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return null;
	}

	/**
	 * Only override if task should be opened by a custom editor, default
	 * behavior is to open with a rich editor, falling back to the web browser
	 * if not available.
	 * 
	 * @return true if the task was successfully opened
	 */
	public boolean openRepositoryTask(String repositoryUrl, String id) {
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(getRepositoryType());
		String taskUrl = connector.getTaskWebUrl(repositoryUrl, id);
		if(taskUrl == null) {
			return false;
		}
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) {
				window = windows[0];
			}
		}
		if (window == null) {
			return false;
		}
		IWorkbenchPage page = window.getActivePage();
		
		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(getRepositoryType(), repositoryUrl, id, taskUrl, page);
		job.schedule();

		return true;
	}

}
