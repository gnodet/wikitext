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

import org.eclipse.mylar.provisional.tasklist.TaskRepository;


/**
 * Task repository for generic web-based system 
 * 
 * @author Eugene Kuleshov
 */
public class WebTaskRepository extends TaskRepository {

	private final String newTaskUrl;
	private final String taskPrefixUrl;

	public WebTaskRepository(String kind, String serverUrl, String newTaskUrl, String taskPrefixUrl) {
		super(kind, serverUrl);
		this.newTaskUrl = newTaskUrl;
		this.taskPrefixUrl = taskPrefixUrl;
	}
	
	public String getNewTaskUrl() {
		return this.newTaskUrl;
	}
	
	public String getTaskPrefixUrl() {
		return this.taskPrefixUrl;
	}

}
