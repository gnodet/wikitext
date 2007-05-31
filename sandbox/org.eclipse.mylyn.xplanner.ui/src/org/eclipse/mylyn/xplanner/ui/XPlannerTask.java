/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.UserStoryData;

import org.eclipse.mylar.xplanner.wsdl.soap.domain.DomainData;

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class XPlannerTask extends AbstractRepositoryTask {

	private String key = null;

	public enum Kind {
		ITERATION, USER_STORY, TASK;

		@Override
		public String toString() {
			switch (this) {
			case ITERATION:
				return "Iteration"; //$NON-NLS-1$
			case USER_STORY:
				return "User Story"; //$NON-NLS-1$
			case TASK:
				return "Task"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	public void setKind(DomainData data) {
		String tempKind = Kind.TASK.toString();
		
		if (data instanceof IterationData) {
			tempKind = Kind.ITERATION.toString();
		}
		else if (data instanceof UserStoryData) {
			tempKind = Kind.USER_STORY.toString();
		}
		
		setKind(tempKind);
	}
	
	/**
	 * The handle is also the task's XPlanner url
	 */
	public XPlannerTask(String repositoryUrl, String id, String label, boolean newTask) {
		super(repositoryUrl, id, label, newTask);
	}

	public String getRepositoryKind() {
		return XPlannerMylarUIPlugin.REPOSITORY_KIND;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	@Override
	public String getPriority() {
		String priority;
		
		if (taskData != null && taskData.getAttribute(RepositoryTaskAttribute.PRIORITY) != null) {
			priority = taskData.getAttributeValue(RepositoryTaskAttribute.PRIORITY);
		} 
		else {
			priority = super.getPriority();
		}
		
		return priority;
	}

}
