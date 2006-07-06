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

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;


/**
 * Task used with generic web-based repositories 
 * 
 * @author Eugene Kuleshov
 */
public class WebTask extends AbstractRepositoryTask {

	private final String id;
	private final String taskPrefix;
	private final String repositoryUrl;
	
	public WebTask(String id, String label, String taskPrefix, String repositoryUrl) {
		super(taskPrefix + id, label, false);
		this.id = id;
		this.taskPrefix = taskPrefix;
		this.repositoryUrl = repositoryUrl;
		setUrl(taskPrefix + id);
	}

	public String getRepositoryKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	public boolean isDownloaded() {
		return false;
	}

	public boolean isPersistentInWorkspace() {
		return false;
	}
	
	public String getTaskPrefix() {
		return this.taskPrefix;
	}
	
	public String getId() {
		return this.id;
	}
	
	@Override
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	
}

