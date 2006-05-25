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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.internal.hypertext.MylarHypertextPlugin;
import org.eclipse.mylar.internal.hypertext.WebPage;
import org.eclipse.mylar.internal.hypertext.WebResource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

/**
 * @author Mik Kersten
 */
public class OpenWebResourceAction extends BaseSelectionListenerAction {

	protected OpenWebResourceAction(String text) {
		super(text);
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = super.getStructuredSelection();
		Object selectedElement = selection.getFirstElement();
		if (selectedElement instanceof WebResource) {
			openUrlInBrowser((WebResource)selectedElement);
		}
	}

	private void openUrlInBrowser(WebResource webResource) {
		String url = webResource.getUrl();
		try {
			IWebBrowser browser = null;
			int flags = 0;
			if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
				flags = WorkbenchBrowserSupport.AS_EDITOR | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;

			} else {
				flags = WorkbenchBrowserSupport.AS_EXTERNAL | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, "org.eclipse.mylar.tasklist",
					"Browser", url);
			browser.openURL(new URL(url));
			if (webResource instanceof WebPage) {
				MylarHypertextPlugin.getWebResourceManager().retrieveTitle((WebPage)webResource);
			}
			
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
		} catch (MalformedURLException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
		}
	}
	
}
