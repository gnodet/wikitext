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

package org.eclipse.mylar.internal.hypertext;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.hypertext.ui.HypertextImages;

/**
 * @author Mik Kersten
 */
public class WebRoot extends WebSiteResource {

	public static final String URL_ROOT = "Web";
	
	private static final String LABEL = "Web";
	
	private HashMap<String, WebSiteResource> sites = new HashMap<String, WebSiteResource>();
	
	@Override
	public String getLabel(Object object) {
		return LABEL;
	}

	public WebRoot() {
		super(URL_ROOT);
	}
	

	public void clear() {
		sites.clear();
	}
	
	public void addSite(WebSite site) {
		sites.put(site.getUrl(), site);
	}
	
	public WebSite getSite(String url) {
		return (WebSite)sites.get(url);
	}
	
	public void deleteSite(WebSite site) {
		sites.remove(site);
	}
  
	@Override
	public WebSiteResource getParent() {
		return null;
	}

	@Override
	public Collection<WebSiteResource> getChildren() {
		return sites.values();
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return HypertextImages.WEB_ROOT;
	}
}
