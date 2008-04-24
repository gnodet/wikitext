/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.NewTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class NewXPlannerTaskWizard extends Wizard implements INewWizard {

	private final TaskRepository taskRepository;

	private final NewXPlannerTaskPage userStoryPage;

	public NewXPlannerTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;

		userStoryPage = new NewXPlannerTaskPage(taskRepository);

		setWindowTitle("New Task");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);

		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(userStoryPage);
	}

	@Override
	public boolean canFinish() {
		boolean canFinish = false;

		if (userStoryPage != null) {
			canFinish = userStoryPage.isPageComplete();
		}

		return canFinish;
	}

	@Override
	public boolean performFinish() {
		try {
			RepositoryTaskData taskData = XPlannerRepositoryUtils.getNewRepositoryTaskData(taskRepository,
					userStoryPage.getSelectedUserStory());

			NewTaskEditorInput editorInput = new NewTaskEditorInput(taskRepository, taskData);
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
			return true;
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
	}
}
