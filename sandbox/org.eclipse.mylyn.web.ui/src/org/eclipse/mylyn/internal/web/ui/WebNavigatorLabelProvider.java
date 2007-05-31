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

package org.eclipse.mylar.internal.web.ui;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.internal.web.WebResource;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

/**
 * @author Mik Kersten
 */
public class WebNavigatorLabelProvider extends WorkbenchLabelProvider implements
		ICommonLabelProvider {

	private ICommonContentExtensionSite extensionSite;

	public void init(ICommonContentExtensionSite config) {
		extensionSite = config;
	}

//    public Image getImage(Object element) {
//    	if (element instanceof WebRoot) {
//    		return WebImages.getImage(WebImages.WEB_ROOT);
//    	} else if (element instanceof WebSite) {
//    		return WebImages.getImage(WebImages.WEB_SITE);
//    	} else if (element instanceof WebPage) {
//    		return WebImages.getImage(WebImages.WEB_PAGE);
//    	} else {
//    		return null;
//    	}
//    }

	public void restoreState(IMemento aMemento) {
		// Nothing to do
	}

	public void saveState(IMemento aMemento) {
		// Nothing to do
	}

	public String getDescription(Object anElement) {
		if (anElement instanceof WebResource) {
			return ((WebResource) anElement).getLabel(anElement);
		}
		return null;
	} 

	/**
	 * Return the extension site for this label provider.
	 * @return the extension site for this label provider
	 */
	public ICommonContentExtensionSite getExtensionSite() {
		return extensionSite;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// ignore
		
	}
}
