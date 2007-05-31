/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui.wizard;
 
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Wizard that allows the user to select one of their named XPlanner filters on the
 * server
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
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
			List<AbstractRepositoryQuery> queries = ((MultipleQueryPage)queryPage).getQueries();
			for (final AbstractRepositoryQuery query : queries) {
				TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
			}
			
			// need to synchronize multiple queries with single call, otherwise get ConcurrencyModificationException
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
			if (connector != null) {
				TasksUiPlugin.getSynchronizationManager().synchronize(connector,
						new HashSet<AbstractRepositoryQuery>(queries), null, Job.SHORT, 0, true);
			}
		}
		else {
      AbstractRepositoryQuery query = queryPage.getQuery();
      addQuery(query, repository);
		}

		return true;
	}

  public static void addQuery(AbstractRepositoryQuery query, TaskRepository repository) {
		if (query != null) {
			TasksUiPlugin.getTaskListManager().getTaskList().addQuery(query);
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
			if (connector != null) {
				TasksUiPlugin.getSynchronizationManager().synchronize(connector, query, null);
			}
		} 
  }
	
	public boolean canFinish() {
		boolean canFinish = false;
		
		if (queryPage != null) {
			if(queryPage.getNextPage() == null) {
				canFinish = queryPage.isPageComplete();
			}
			else {
				canFinish = queryPage.getNextPage().isPageComplete();
			}
		}
		
		return canFinish;
	}
	
}
