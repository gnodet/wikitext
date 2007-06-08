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

package org.eclipse.mylyn.internal.trac.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylyn.internal.tasks.ui.search.AbstractRepositorySearchQuery;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * This class performs a search query.
 * 
 * @author Steffen Pingel
 */
public class RepositorySearchQuery extends AbstractRepositorySearchQuery {

	private QueryHitCollector collector;

	private TaskRepository repository;

	private AbstractRepositoryQuery query;

	public RepositorySearchQuery(TaskRepository repository, AbstractRepositoryQuery query) {
		this.repository = repository;
		this.query = query;
	}

	public QueryHitCollector getCollector() {
		return collector;
	}

	public void setCollector(QueryHitCollector collector) {
		this.collector = collector;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		//MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
		IStatus queryStatus = Status.OK_STATUS;
		try {
			collector.setProgressMonitor(monitor);
			collector.aboutToStart(0);

			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getKind());

			queryStatus = connector.performQuery(query, repository, TasksUiPlugin.getDefault().getProxySettings(), monitor, collector);			
			collector.done();
		} catch (final CoreException e) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(null, "Repository Search Error", null, e.getStatus());
				}
			});
			return Status.OK_STATUS;
		}

		final IStatus status = queryStatus;
		if (status.getCode() == IStatus.CANCEL) {
			return Status.OK_STATUS;
		} else if (!status.isOK()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					ErrorDialog.openError(null, "Repository Search Error", null, status);
				}
			});
			return Status.OK_STATUS;
		}
		return status;
	}
}
