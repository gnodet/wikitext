/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.ui.IWorkbench;

public class NewXPlannerTaskWizard extends NewTaskWizard {

	private final NewXPlannerTaskPage userStoryPage;

	public NewXPlannerTaskWizard(TaskRepository taskRepository) {
		super(taskRepository);

		userStoryPage = new NewXPlannerTaskPage(taskRepository);

		setWindowTitle("New Task");
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);

		setNeedsProgressMonitor(true);
	}

	@Override
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
	protected ITaskMapping getInitializationData() {
		return new XPlannerTaskMapping(userStoryPage.getSelectedUserStory());
	}

}
