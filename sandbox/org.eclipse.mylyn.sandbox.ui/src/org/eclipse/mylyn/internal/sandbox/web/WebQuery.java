/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskList;

/**
 * Represents regexp-based query on repository web page  
 * 
 * @author Eugene Kuleshov
 */
public class WebQuery extends AbstractRepositoryQuery {

	private final String regexp;
	private final String taskPrefix;

	public WebQuery(String description, String queryUrl, String taskPrefix, String regexp, 
			TaskList taskList, String repositoryUrl) {
		super(description, taskList);
		this.taskPrefix = taskPrefix;

		this.regexp = regexp;
		
		setQueryUrl(queryUrl);
		setRepositoryUrl(repositoryUrl);
	}

	public String getRepositoryKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}
	
	public String getRegexp() {
		return this.regexp;
	}

	public String getTaskPrefix() {
		return this.taskPrefix;
	}

}
