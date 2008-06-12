/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class EditXPlannerQueryWizard extends RepositoryQueryWizard {

	private AbstractXPlannerQueryWizardPage queryPage;

	private final IRepositoryQuery query;

	public EditXPlannerQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		super(repository);
		setForcePreviousAndNextButtons(true);
		this.query = query;
	}

	@Override
	public void addPages() {
		queryPage = XPlannerQueryWizardUtils.addQueryWizardFirstPage(this, getTaskRepository(), query);
	}

	@Override
	public boolean performFinish() {
		List<IRepositoryQuery> queries = new ArrayList<IRepositoryQuery>();

		// always delete existing query, because new one(s) will get created below
		TasksUiInternal.getTaskList().deleteQuery((RepositoryQuery) query);

		if (queryPage instanceof MultipleQueryPage) {
			queries = ((MultipleQueryPage) queryPage).getQueries();
		} else {
			final IRepositoryQuery query = queryPage.getQuery();
			if (query != null) {
				queries.add(query);
			}
		}

		for (IRepositoryQuery query : queries) {
			updateQuery(query);
		}

		return true;
	}

	private void updateQuery(final IRepositoryQuery query) {
		// just in case one with this definition already exists...
		TasksUiInternal.getTaskList().deleteQuery((RepositoryQuery) query);
		// make sure query reflects changed name, if it was changed
		if (query.getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND)) {
			String handleIdentifier = ((RepositoryQuery) query).getHandleIdentifier();
			String queryName = query.getSummary();
			if (!handleIdentifier.equals(queryName)) {
				((RepositoryQuery) query).setHandleIdentifier(queryName);
			}
		}
		TasksUiInternal.getTaskList().addQuery((RepositoryQuery) query);

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				getTaskRepository().getConnectorKind());
		if (connector != null) {
			TasksUiInternal.synchronizeQuery(connector, (RepositoryQuery) query, null, true);
		}
	}

	@Override
	public boolean canFinish() {
		boolean canFinish = false;
		if (queryPage != null) {
			if (queryPage.getNextPage() == null) {
				canFinish = queryPage.isPageComplete();
			} else {
				canFinish = queryPage.getNextPage().isPageComplete();
			}
		}

		return canFinish;
	}
}
