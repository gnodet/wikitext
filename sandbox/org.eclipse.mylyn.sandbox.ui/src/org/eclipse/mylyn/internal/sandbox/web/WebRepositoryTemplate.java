/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

/**
 * @author Eugene Kuleshov
 */
@Deprecated
public class WebRepositoryTemplate {
	
	public final String label;

	public final String url;

	public final String newTask;

	public final String prefix;

	public final String query;

	public final String regexp;

	public WebRepositoryTemplate(String label, String url, String newTask, String prefix, String query, String regexp) {
		this.label = label;
		this.url = url;
		this.newTask = newTask;
		this.prefix = prefix;
		this.query = query;
		this.regexp = regexp;
	}
}