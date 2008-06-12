/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.ui.wizard.EditXPlannerQueryWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.Messages;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerQueryWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerTaskWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerCustomQueryPage;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerRepositorySettingsPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryUi extends AbstractRepositoryConnectorUi {

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new XPlannerRepositorySettingsPage(taskRepository);
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(ITask task) {
		ImageDescriptor overlayImage;

		XPlannerAttributeMapper.XPlannerTaskKind kind = XPlannerAttributeMapper.XPlannerTaskKind.fromString(task.getTaskKind());
		if (kind.equals(XPlannerAttributeMapper.XPlannerTaskKind.TASK)) {
			overlayImage = XPlannerImages.OVERLAY_TASK;
		} else if (kind.equals(XPlannerAttributeMapper.XPlannerTaskKind.USER_STORY)) {
			overlayImage = XPlannerImages.OVERLAY_USER_STORY;
		} else {
			overlayImage = super.getTaskKindOverlay(task);
		}

		return overlayImage;
	}

	@Override
	public ITaskSearchPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		try {
			return new XPlannerCustomQueryPage(repository, null);
		} catch (RuntimeException e) {
			XPlannerMylynUIPlugin.log(e.getCause(),
					Messages.XPlannerQueryWizardUtils_COULD_NOT_CREATE_QUERY_PAGE_MESSAGE, true);
			return null;
		}
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		IWizard queryWizard = null;

		if (query != null) {
			queryWizard = new EditXPlannerQueryWizard(repository, query);
		} else {
			queryWizard = new NewXPlannerQueryWizard(repository);
		}

		return queryWizard;
	}

	@SuppressWarnings( { "unused", "restriction" })
	// restriction suppression for EditRepositoryWizard
	// Leave in case needed other places
	private boolean ensureHaveValidClient(TaskRepository repository) {
		boolean haveValidClient = true;

		try {
			XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		} catch (CoreException ce) {
			try {
				org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard wizard = new org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard(
						repository);
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (shell != null && !shell.isDisposed()) {
					WizardDialog dialog = new WizardDialog(shell, wizard);
					dialog.create();
					dialog.setErrorMessage("Authentication credentials missing.");
					dialog.setBlockOnOpen(true);
					if (dialog.open() == Window.CANCEL) {
						dialog.close();
						haveValidClient = false;
					}
				}
			} catch (Exception e) {
				haveValidClient = false;
			}
		}

		return haveValidClient;
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
//			MylynStatusHandler.fail(e, e.getMessage(), true);
//		}
//	
//	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping taskSelection) {
		// TODO pass taskSelection to task editor
		return new NewXPlannerTaskWizard(taskRepository);
	}

	@Override
	public String getConnectorKind() {
		return XPlannerCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}

	@Override
	public String getTaskKindLabel(ITask repositoryTask) {
		return repositoryTask == null ? super.getTaskKindLabel(repositoryTask) : repositoryTask.getTaskKind();
	}

	@Override
	public List<LegendElement> getLegendElements() {
		List<LegendElement> legendElements = new ArrayList<LegendElement>();

		legendElements.add(LegendElement.createTask("Task", XPlannerImages.OVERLAY_TASK));
		legendElements.add(LegendElement.createTask("User Story", XPlannerImages.OVERLAY_USER_STORY));

		return legendElements;
	}
}
