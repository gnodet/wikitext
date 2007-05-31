/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * Represents an XPlanner task returned as the result of a XPlanner query
 * 
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class XPlannerQueryHit extends AbstractQueryHit {

	private XPlannerTask task = null;

	public XPlannerQueryHit(TaskList taskList, String description, String priority, 
			String repositoryUrl, String id, XPlannerTask task) {
		
		super(taskList, repositoryUrl, description, id);
		super.priority = priority;
		this.task = task;
	}

	@Override
	protected AbstractRepositoryTask createTask() {
		return task;  //TODO -- not sure if this is right, but corresponds to what we did before
	}

	/**
	 * @return null if there is no corresponding report
	 */
	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	public void setCorrespondingTask(AbstractRepositoryTask task) {
		if (task instanceof XPlannerTask) {
			this.task = (XPlannerTask)task;
		}
	}

	public boolean isCompleted() {
		return task.isCompleted();
	}

	public String getPriority() {
		return task.getPriority();
	}

	public String getSummary() {
		return task.getSummary();
	}

	public void setDescription(String description) {
		task.setDescription(description);
	}

	public boolean isLocal() {
		return false;
	}
}
