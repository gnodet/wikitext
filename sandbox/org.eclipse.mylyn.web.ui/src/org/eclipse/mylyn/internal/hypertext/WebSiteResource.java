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

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**
 * @author Mik Kersten
 */
public abstract class WebSiteResource extends PlatformObject implements IWorkbenchAdapter {
	
	private final String url;
	
	protected WebSiteResource(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	
    public String getLabel(Object object) {
		return url;
	}

    public Object[] getChildren(Object o) {
    	return getChildren().toArray();
    }
    
    public Object getParent(Object o) {
    	return getParent();
    }
    
    public abstract ImageDescriptor getImageDescriptor(Object object);
    
	public abstract Collection<WebSiteResource> getChildren();

	public abstract WebSiteResource getParent();
	
	public boolean equals(Object obj) {
		if (obj instanceof WebSiteResource) {
			WebSiteResource webResource = (WebSiteResource) obj;
			return getUrl().equals(webResource.getUrl());
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return url;
	}

	public int hashCode() {
		return url.hashCode();
	}
}
