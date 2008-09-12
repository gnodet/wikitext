/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Mik Kersten
 */
public class WebSite extends WebResource {

	private WebRoot project;

	private final HashMap<String, WebResource> pages = new HashMap<String, WebResource>();

	public WebSite(String url) {
		super(url);
	}

	public void addPage(WebPage page) {
		pages.put(page.getUrl(), page);
	}

	public void removePage(WebPage page) {
		pages.remove(page);
	}

	@Override
	public WebResource getParent() {
		return project;
	}

	@Override
	public Collection<WebResource> getChildren() {
		return pages.values();
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return WebImages.WEB_SITE;
	}

	public WebPage getPage(String url) {
		return (WebPage) pages.get(url);
	}

}
