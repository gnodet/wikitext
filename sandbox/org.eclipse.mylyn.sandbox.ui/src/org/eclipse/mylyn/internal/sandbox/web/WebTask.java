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

	private String id;
	
	public WebTask(String handle, String label, String id) {
		super(handle, label, false);
		this.id = id;
	}

	public String getId() {
		return this.id;
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

}

