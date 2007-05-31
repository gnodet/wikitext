/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylar.xplanner.ui.wizard.EditXPlannerQueryWizard;
import org.eclipse.mylar.xplanner.ui.wizard.NewXPlannerQueryWizard;
import org.eclipse.mylar.xplanner.ui.wizard.XPlannerCustomQueryPage;
import org.eclipse.mylar.xplanner.ui.wizard.XPlannerRepositorySettingsPage;

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class XPlannerRepositoryUi extends AbstractRepositoryConnectorUi {

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new XPlannerRepositorySettingsPage(this);
	}

	@Override
	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		return new XPlannerCustomQueryPage(repository, null);
	} 

	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		if (query instanceof XPlannerCustomQuery) {
			return new EditXPlannerQueryWizard(repository, query);
		}
		else {
			return new NewXPlannerQueryWizard(repository);
		}
	}
	
//	@Override
//	public void openEditQueryDialog(AbstractRepositoryQuery query) {
//		try {
//			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
//					query.getRepositoryKind(), query.getRepositoryUrl());
//			if (repository == null)
//				return;
//	
//			IWizard wizard = null;
//	
//			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//			if (wizard != null && shell != null && !shell.isDisposed()) {
//				WizardDialog dialog = new WizardDialog(shell, wizard);
//				dialog.create();
//				dialog.setTitle(TITLE_EDIT_QUERY);
//				dialog.setBlockOnOpen(true);
//				if (dialog.open() == Window.CANCEL) {
//					dialog.close();
//					return;
//				}
//			}
//		} catch (Exception e) {
//			MylarStatusHandler.fail(e, e.getMessage(), true);
//		}
//	
//	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		String newTaskUrl = taskRepository.getUrl();
		return new NewWebTaskWizard(taskRepository, newTaskUrl + (newTaskUrl.endsWith("/") ? "" : "/") + "do/view/projects"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	@Override
	public boolean hasRichEditor() {
		return true;
	}

	@Override
	public String getRepositoryType() {
		return XPlannerMylarUIPlugin.REPOSITORY_KIND;
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}
	
}
