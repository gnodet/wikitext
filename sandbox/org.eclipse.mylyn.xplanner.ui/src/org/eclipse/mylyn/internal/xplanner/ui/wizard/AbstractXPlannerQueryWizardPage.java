/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.wizard;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public abstract class AbstractXPlannerQueryWizardPage extends AbstractRepositoryQueryPage {

	private static final String TITLE = Messages.AbstractXPlannerQueryWizardPage_NEW_XPLANNER_QUERY;

	private IRepositoryQuery existingQuery;

	public AbstractXPlannerQueryWizardPage(TaskRepository repository) {
		this(repository, null);
	}

	public AbstractXPlannerQueryWizardPage(TaskRepository repository, IRepositoryQuery existingQuery) {
		super(TITLE, repository);
		this.existingQuery = existingQuery;
		setTitle(TITLE);
		setPageComplete(true);
	}

	@Override
	public abstract IRepositoryQuery getQuery();

	public TaskRepository getRepository() {
		return getTaskRepository();
	}

	public IRepositoryQuery getExistingQuery() {
		return existingQuery;
	}

	public void setExistingQuery(IRepositoryQuery existingQuery) {
		this.existingQuery = existingQuery;
	}

	protected String getHelpContextId() {
		return null;
	}

	@Override
	public void performHelp() {
		if (getHelpContextId() != null) {
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(getHelpContextId());
		}
	}

}
