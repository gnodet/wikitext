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

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.hypertext.ui.HypertextImages;


/**
 * @author Mik Kersten
 */
public class WebPage extends WebSiteResource {

	private WebSite site;
	
	protected WebPage(String url, WebSite site) {
		super(url);
		this.site = site;
	}
	
	public String getName() {
		return "page: " + super.getLabel(this);
	}

	@Override
	public List<WebSiteResource> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public WebSiteResource getParent() {
		return site;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return HypertextImages.WEB_PAGE;
	}

}
