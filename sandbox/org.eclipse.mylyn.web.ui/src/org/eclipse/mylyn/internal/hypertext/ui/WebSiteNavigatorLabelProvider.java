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

package org.eclipse.mylar.internal.hypertext.ui;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.internal.hypertext.WebSiteResource;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

/**
 * @author Mik Kersten
 */
public class WebSiteNavigatorLabelProvider extends WorkbenchLabelProvider implements
		ICommonLabelProvider {

	private ICommonContentExtensionSite extensionSite;

	public void init(ICommonContentExtensionSite config) {
		extensionSite = config;
	}

//    public Image getImage(Object element) {
//    	if (element instanceof WebRoot) {
//    		return HypertextImages.getImage(HypertextImages.WEB_ROOT);
//    	} else if (element instanceof WebSite) {
//    		return HypertextImages.getImage(HypertextImages.WEB_SITE);
//    	} else if (element instanceof WebPage) {
//    		return HypertextImages.getImage(HypertextImages.WEB_PAGE);
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
		if (anElement instanceof WebSiteResource) {
			return ((WebSiteResource) anElement).getLabel(anElement);
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

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
		
	}
}
