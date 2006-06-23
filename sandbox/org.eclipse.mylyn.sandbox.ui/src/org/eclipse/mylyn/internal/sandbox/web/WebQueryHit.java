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

/**
 * Represents issue returned by <code>WebQuery</code> 
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryHit extends AbstractQueryHit {

	private AbstractRepositoryTask task;


	public WebQueryHit(String id, String description, String repositoryUrl) {
		super(repositoryUrl, description, Integer.parseInt(id));
	}

	public String getDescription() {
		return id + ": "+ description;
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
		// TODO
		return null;
	}
	
	public void setHandleIdentifier(String id) {
		// TODO
	}

}

