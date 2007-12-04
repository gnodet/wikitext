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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.*;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
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

	public AbstractRepositorySettingsPage getSettingsPage() {
		return new XPlannerRepositorySettingsPage(this);
	}

	@Override
	public ImageDescriptor getTaskKindOverlay(AbstractTask task) {
		ImageDescriptor overlayImage;
		
		XPlannerTask.Kind kind = XPlannerTask.Kind.fromString(task.getTaskKind());
		if (kind.equals(XPlannerTask.Kind.TASK)) {
			overlayImage = XPlannerImages.OVERLAY_TASK;
		}
		else if (kind.equals(XPlannerTask.Kind.USER_STORY)) {
			overlayImage = XPlannerImages.OVERLAY_USER_STORY;
		}
		else if (kind.equals(XPlannerTask.Kind.ITERATION)) {
			overlayImage = XPlannerImages.OVERLAY_ITERATION;
		}
		else {
			overlayImage = super.getTaskKindOverlay(task);
		}
		
		return overlayImage;
	}

	@Override
	public WizardPage getSearchPage(TaskRepository repository, IStructuredSelection selection) {
		WizardPage xplannerQueryPage = null;
		
		try {
			xplannerQueryPage = new XPlannerCustomQueryPage(repository, null);
		} 
		catch (RuntimeException e) {
			XPlannerMylynUIPlugin.log(e.getCause(), Messages.XPlannerQueryWizardUtils_COULD_NOT_CREATE_QUERY_PAGE_MESSAGE, true);
		}
		
		return xplannerQueryPage;
	} 

	public IWizard getQueryWizard(TaskRepository repository, AbstractRepositoryQuery query) {
		IWizard queryWizard = null;
		
		if (ensureHaveValidClient(repository)) {
			if (query instanceof XPlannerCustomQuery) {
				queryWizard = new EditXPlannerQueryWizard(repository, query);
			}
			else {
				queryWizard = new NewXPlannerQueryWizard(repository);
			}
		}
		
		return queryWizard;
	}
	
	private boolean ensureHaveValidClient(TaskRepository repository) {
		boolean haveValidClient = true;
		
		try {
			XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		} 
		catch (CoreException ce) {
			try {
				EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				if (shell != null && !shell.isDisposed()) {
					WizardDialog dialog = new WizardDialog(shell, wizard);
					dialog.create();
					dialog.setErrorMessage("Authentication credentials missing.");
					dialog.setBlockOnOpen(true);
					if (dialog.open() == Dialog.CANCEL) {
						dialog.close();
						haveValidClient = false;
					}
				}
			} 
			catch (Exception e) {
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
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return new NewXPlannerTaskWizard(taskRepository);
	}

	@Override
	public String getConnectorKind() {
		return XPlannerMylynUIPlugin.REPOSITORY_KIND;
	}

	@Override
	public boolean hasSearchPage() {
		return true;
	}
	
	@Override
	public String getTaskKindLabel(AbstractTask repositoryTask) {
		return repositoryTask == null ? super.getTaskKindLabel(repositoryTask) :
			repositoryTask.getTaskKind();
	}
	
	@Override
	public List<AbstractTaskContainer> getLegendItems() {
		List<AbstractTaskContainer> legendItems = new ArrayList<AbstractTaskContainer>();
		
		XPlannerTask task = new XPlannerTask("", XPlannerTask.Kind.TASK.name(), XPlannerTask.Kind.TASK.toString());
		task.setTaskKind(XPlannerTask.Kind.TASK.toString());		
		legendItems.add(task);

		XPlannerTask userStory = new XPlannerTask("", XPlannerTask.Kind.USER_STORY.name(), XPlannerTask.Kind.USER_STORY.toString());
		userStory.setTaskKind(XPlannerTask.Kind.USER_STORY.toString());		
		legendItems.add(userStory);

		XPlannerTask iteration = new XPlannerTask("", XPlannerTask.Kind.ITERATION.name(), XPlannerTask.Kind.ITERATION.toString());
		iteration.setTaskKind(XPlannerTask.Kind.ITERATION.toString());		
		legendItems.add(iteration);

		return legendItems;
	}

}
