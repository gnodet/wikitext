/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @since 3.0
 */
public class ErrorQueryPage extends AbstractXPlannerQueryWizardPage {

	private final TaskRepository taskRepository;

	public ErrorQueryPage(TaskRepository taskRepository, String errorMessage) {
		super(taskRepository);
		this.taskRepository = taskRepository;
		setErrorMessage(errorMessage);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);
	}

	@Override
	public IRepositoryQuery getQuery() {
		return null;
	}

	@Override
	public boolean isPageComplete() {
		return false;
	}

	@Override
	public TaskRepository getRepository() {
		return taskRepository;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public String getQueryTitle() {
		return null;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		throw new UnsupportedOperationException();
	}

}