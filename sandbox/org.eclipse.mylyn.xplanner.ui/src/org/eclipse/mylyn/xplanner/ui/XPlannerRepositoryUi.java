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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.LegendElement;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskSearchPage;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.ui.wizard.EditXPlannerQueryWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.Messages;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerQueryWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerTaskWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerCustomQueryPage;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerRepositorySettingsPage;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositoryUi extends AbstractRepositoryConnectorUi {
	private static final String regexp = "(task|story):(\\d+)"; //$NON-NLS-1$

	private static final Pattern PATTERN = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

	private static final int DOMAIN_ID_GROUP = 2;

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

	// restriction suppression for EditRepositoryWizard
	// Leave in case needed other places
//	private boolean ensureHaveValidClient(TaskRepository repository) {
//		boolean haveValidClient = true;
//
//		try {
//			XPlannerClientFacade.getDefault().getXPlannerClient(repository);
//		} catch (CoreException ce) {
//			try {
//				org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard wizard = new org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard(
//						repository);
//				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//				if (shell != null && !shell.isDisposed()) {
//					WizardDialog dialog = new WizardDialog(shell, wizard);
//					dialog.create();
//					dialog.setErrorMessage("Authentication credentials missing.");
//					dialog.setBlockOnOpen(true);
//					if (dialog.open() == Window.CANCEL) {
//						dialog.close();
//						haveValidClient = false;
//					}
//				}
//			} catch (Exception e) {
//				haveValidClient = false;
//			}
//		}
//
//		return haveValidClient;
//	}

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

		legendElements.add(LegendElement.createTask(Messages.XPlannerRepositoryUi_TASK, XPlannerImages.OVERLAY_TASK));
		legendElements.add(LegendElement.createTask(Messages.XPlannerRepositoryUi_USER_STORY, XPlannerImages.OVERLAY_USER_STORY));

		return legendElements;
	}

	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
		ArrayList<IHyperlink> hyperlinksFound = null;

		Matcher m = PATTERN.matcher(text);
		while (m.find()) {
			if (index == -1 || (index >= m.start() && index <= m.end())) {
				IHyperlink link = extractHyperlink(repository, textOffset, m);
				if (link != null) {
					if (hyperlinksFound == null) {
						hyperlinksFound = new ArrayList<IHyperlink>();
					}
					hyperlinksFound.add(link);
				}
			}
		}

		return (hyperlinksFound != null) ? hyperlinksFound.toArray(new IHyperlink[0]) : null;
	}

	private IHyperlink extractHyperlink(TaskRepository repository, int regionOffset, Matcher m) {

		int start = m.start();

		int end = m.end();

		if (end == -1) {
			end = m.group().length();
		}

		try {

			String domainId = m.group(DOMAIN_ID_GROUP).trim();
			start += regionOffset;
			end += regionOffset;

			IRegion sregion = new Region(start, end - start);

			return new TaskHyperlink(sregion, repository, domainId);

		} catch (NumberFormatException e) {
			return null;
		}
	}

}
