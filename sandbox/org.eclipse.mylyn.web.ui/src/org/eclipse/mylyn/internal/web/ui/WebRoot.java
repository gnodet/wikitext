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

package org.eclipse.mylyn.internal.web.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Mik Kersten
 */
public class WebRoot extends WebResource {

	public static final String HANDLE_ROOT = "webroot";

	private static final String LABEL = "Web";

	private HashMap<String, WebResource> sites = new HashMap<String, WebResource>();

	private HashMap<String, WebPage> pages = new HashMap<String, WebPage>();
	
	@Override
	public String getLabel(Object object) {
		return LABEL;
	}

	public WebRoot() {
		super(HANDLE_ROOT);
	}
	
	protected WebRoot(String handleIdentifier) {
		super(handleIdentifier);
	}

	public void clear() {
		sites.clear();
		pages.clear();
	}

	public void addSite(WebSite site) {
		sites.put(site.getUrl(), site);
	}

	public void addPage(WebPage page) {
		pages.put(page.getUrl(), page);
	}
	
	public WebSite getSite(String url) {
		return (WebSite) sites.get(url);
	}
	
	public void deleteSite(WebSite site) {
		sites.remove(site);
	}

	@Override
	public WebResource getParent() {
		return null;
	}

	@Override
	public Collection<WebResource> getChildren() {
		List<WebResource> children = new ArrayList<WebResource>();
		children.addAll(pages.values());
		children.addAll(sites.values());
		return children;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return WebImages.WEB_ROOT;
	}

}