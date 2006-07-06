/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;

/**
 * Represents issue returned by <code>WebQuery</code> 
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryHit extends AbstractQueryHit {
	private final String taskPrefix;

	private AbstractRepositoryTask task;


	public WebQueryHit(String id, String description, String taskPrefix, String repositoryUrl) {
		super(repositoryUrl, description, id);
		this.taskPrefix = taskPrefix;
	}

	public String getDescription() {
		return description;
	}
	
	public String getPriority() {
		return "?";
	}

	public boolean isCompleted() {
		return false;
	}

	public AbstractRepositoryTask getCorrespondingTask() {
		return task;
	}

	public void setCorrespondingTask(AbstractRepositoryTask task) {
		this.task = task;
	}

	public AbstractRepositoryTask getOrCreateCorrespondingTask() {
		TaskList taskList = MylarTaskListPlugin.getTaskListManager().getTaskList();
		
		ITask existingTask = taskList.getTask(getHandleIdentifier());		 
		if (existingTask instanceof WebTask) {
			this.task = (WebTask) existingTask;
		} else {
			task = new WebTask(id, description, taskPrefix, repositoryUrl);
			taskList.addTask(task);			
		} 	
		return task;
	}
	
	public void setHandleIdentifier(String id) {
		task.setHandleIdentifier(id);
	}

	@Override
	public String getHandleIdentifier() {
		return taskPrefix + getId();
	}

	public String getTaskPrefix() {
		return this.taskPrefix;
	}
	
}

