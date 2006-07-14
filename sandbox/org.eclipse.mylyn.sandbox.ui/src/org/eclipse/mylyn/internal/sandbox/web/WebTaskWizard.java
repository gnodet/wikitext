/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.ui.TaskUiUtil;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Wizard used to create new web task
 * 
 * @author Eugene Kuleshov
 */
public class WebTaskWizard extends Wizard implements INewWizard {

	private final TaskRepository taskRepository;

//	private IWorkbench workbench;
//
//	private IStructuredSelection selection;

	public WebTaskWizard(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
//		this.workbench = workbench;
//		this.selection = selection;
	}

	public void addPages() {
		super.addPages();
		addPage(new WebTaskNewPage());
	}

	public boolean canFinish() {
		return true;
	}

	public boolean performFinish() {
		TaskUiUtil.openUrl(taskRepository.getProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL));
		return true;
	}

}
