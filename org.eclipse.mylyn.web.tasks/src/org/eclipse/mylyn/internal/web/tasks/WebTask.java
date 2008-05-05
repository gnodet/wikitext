/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;

/**
 * Task used with generic web-based repositories
 * 
 * @author Eugene Kuleshov
 */
public class WebTask extends AbstractTask {

	private static final String UNKNOWN_OWNER = "<unknown>";

	private String taskPrefix;

	public WebTask(String id, String label, String taskPrefix, String repositoryUrl, String repsitoryType) {
		super(repositoryUrl, id, label);
		this.taskPrefix = taskPrefix;
		setUrl(taskPrefix + id);
	}

	public String getTaskPrefix() {
		return this.taskPrefix;
	}

	@Override
	public String getConnectorKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	@Override
	public String getTaskKey() {
		return isRss() ? null : super.getTaskKey();
	}

	@Override
	public String getTaskKind() {
		return isRss() ? "RSS" : super.getTaskKind();
	}

	public boolean isRss() {
		return taskPrefix == null || taskPrefix.length() == 0;
	}

	@Override
	public String getOwner() {
		String o = super.getOwner();
		return o == null ? UNKNOWN_OWNER : o;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	public void setTaskPrefix(String taskPrefix) {
		this.taskPrefix = taskPrefix;
	}

}
