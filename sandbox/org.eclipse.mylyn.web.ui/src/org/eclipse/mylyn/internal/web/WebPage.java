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

package org.eclipse.mylyn.internal.web;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.web.ui.WebImages;


/**
 * @author Mik Kersten
 */
public class WebPage extends WebResource {

	private WebSite site;
	
	private String title = null;
	
	protected WebPage(String url, WebSite site) {
		super(url);
		this.site = site;
	}
	
	@Override
	public List<WebResource> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getLabel(Object object) {
		if (title == null) {
			return url;
		} else {
			return title;
		}
	}

	@Override
	public WebResource getParent() {
		return site;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return WebImages.WEB_PAGE;
	}

	
	public void setTitle(String pageTitle) {
		this.title = pageTitle;
	}

}
