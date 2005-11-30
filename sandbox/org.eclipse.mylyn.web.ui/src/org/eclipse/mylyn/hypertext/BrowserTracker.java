/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.hypertext;

import java.lang.reflect.Field;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.AbstractUserInteractionMonitor;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ui.MylarTaskEditor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;

/**
 * @author Mik Kersten
 */
public class BrowserTracker extends AbstractUserInteractionMonitor implements IPartListener, IWindowListener, IPageListener {

	private UrlTrackingListener urlTrackingListener = new UrlTrackingListener();
	private IWorkbenchPart currentBrowserPart = null;
		
	class UrlTrackingListener implements LocationListener {

		public void changing(LocationEvent event) {
			// ignore
		}

		public void changed(LocationEvent event) { 
			if (event != null) handleElementSelection(currentBrowserPart, event);
		}
	}
	
	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
		// ignore, this is a special case
	}
	
	//---- Part Listener

	public void partOpened(IWorkbenchPart part) {
		if (part instanceof WebBrowserEditor) {
			currentBrowserPart = part;
			Browser browser = getBrowser((WebBrowserEditor)part);
			if (browser != null) browser.addLocationListener(urlTrackingListener);
		} else if (part instanceof MylarTaskEditor) {
			currentBrowserPart = part;
			Browser browser = ((MylarTaskEditor)part).getWebBrowser();
			if (browser != null) browser.addLocationListener(urlTrackingListener);
		}
	}
	
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof WebBrowserEditor) {
			Browser browser = getBrowser((WebBrowserEditor)part);
			if (browser != null) browser.removeLocationListener(urlTrackingListener);
		}
	}
	
	public void partActivated(IWorkbenchPart part) {

	}

	/**
	 * TODO: this is a wierd place for this code
	 */
	public void partBroughtToTop(IWorkbenchPart part) {

	}

	public void partDeactivated(IWorkbenchPart part) {
	}

	
	private Browser getBrowser(final WebBrowserEditor browserEditor) {
        try { // HACK: using reflection to gain accessibility
            Class browserClass = browserEditor.getClass();
            Field browserField = browserClass.getDeclaredField("webBrowser");
            browserField.setAccessible(true);
            Object browserObject = browserField.get(browserEditor);
            if (browserObject != null && browserObject instanceof BrowserViewer) {
            	return ((BrowserViewer)browserObject).getBrowser();
            } 
        } catch (Exception e) {
        	MylarPlugin.log(e, "could not add browser listener");
        }
        return null;
	}
	
	//--- Window listener
	
	public void windowActivated(IWorkbenchWindow window) {
	}
	public void windowDeactivated(IWorkbenchWindow window) {
	}
	public void windowClosed(IWorkbenchWindow window) {
		window.removePageListener(this);
	}
	public void windowOpened(IWorkbenchWindow window) {
		window.addPageListener(this);
	}
	
	//---- IPageListener
	
	public void pageActivated(IWorkbenchPage page) {
	}
	public void pageClosed(IWorkbenchPage page) {
		page.removePartListener(this);
	}
	public void pageOpened(IWorkbenchPage page) {
		page.addPartListener(this);
	}
	
}
