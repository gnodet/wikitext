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

import java.util.Collection;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;


/**
 * @author Mik Kersten
 */
public abstract class WebResource extends PlatformObject implements IWorkbenchAdapter {
	
	protected final String url;
	
	protected WebResource(String url) {
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
    
	public abstract Collection<WebResource> getChildren();

	public abstract WebResource getParent();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WebResource) {
			WebResource webResource = (WebResource) obj;
			return getUrl().equals(webResource.getUrl());
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return url;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}
}
