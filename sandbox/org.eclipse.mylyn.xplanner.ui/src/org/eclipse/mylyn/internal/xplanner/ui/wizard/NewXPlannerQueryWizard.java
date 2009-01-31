/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.wizard;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * Wizard that allows the user to create an XPlanner query for tasks
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
public class NewXPlannerQueryWizard extends Wizard {

	private static final String TITLE = Messages.NewXPlannerQueryWizard_NEW_XPLANNER_QUERY;

	private final TaskRepository repository;

	private AbstractXPlannerQueryWizardPage queryPage;

	public NewXPlannerQueryWizard(TaskRepository repository) {
		this.repository = repository;
		setNeedsProgressMonitor(true);
		setWindowTitle(TITLE);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPOSITORY);
	}

	@Override
	public void addPages() {
		queryPage = XPlannerQueryWizardUtils.addQueryWizardFirstPage(this, repository, null);
	}

	@Override
	public boolean performFinish() {
		if (queryPage instanceof MultipleQueryPage) {
			List<IRepositoryQuery> queries = ((MultipleQueryPage) queryPage).getQueries();
			HashSet<RepositoryQuery> queriesSet = new HashSet<RepositoryQuery>();
			for (final IRepositoryQuery query : queries) {
				RepositoryQuery tempQuery = (RepositoryQuery) query;
				TasksUiInternal.getTaskList().addQuery(tempQuery);
				queriesSet.add(tempQuery);
			}

			// need to synchronize multiple queries with single call, otherwise get ConcurrencyModificationException
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQueries(connector, repository, queriesSet, null, true);
			}
		} else {
			RepositoryQuery query = (RepositoryQuery) queryPage.getQuery();
			addQuery(query, repository);
		}

		return true;
	}

	public static void addQuery(RepositoryQuery query, TaskRepository repository) {
		if (query != null) {
			TasksUiInternal.getTaskList().addQuery(query);
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, query, null, true);
			}
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
