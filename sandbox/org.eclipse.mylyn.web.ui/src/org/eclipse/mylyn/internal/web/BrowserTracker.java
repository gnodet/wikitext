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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.monitor.ui.AbstractUserInteractionMonitor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.browser.WebBrowserEditor;

/**
 * @author Mik Kersten
 */
public class BrowserTracker extends AbstractUserInteractionMonitor implements IPartListener {

	private IWorkbenchPart currentBrowserPart = null;

	private final class UrlTrackingListener implements ProgressListener {
		private final Browser browser;

		private UrlTrackingListener(Browser browser) {
			this.browser = browser;
		}

		public void changed(ProgressEvent event) {
			// ignore
			
		}

		public void completed(ProgressEvent event) {
			try {
				handleElementSelection(currentBrowserPart, new URL(browser.getUrl()), true);
			} catch (MalformedURLException e) {
				// ignore bogus URLs
			}
		}
	}

	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection, boolean contributeToContext) {
		// ignore, this is a special case
	}

	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IMonitoredWebBrowserPart) {
			currentBrowserPart = part;
			IMonitoredWebBrowserPart monitoredBrowser = (IMonitoredWebBrowserPart)part;
			Browser browser = monitoredBrowser.getBrowser();
			if (browser != null) {
				browser.addProgressListener(new UrlTrackingListener(browser));
			}
		} else if (part instanceof WebBrowserEditor) {
			// not tracking workbench browsers
			//			currentBrowserPart = part;
			//			final Browser browser = getBrowser((WebBrowserEditor) part);
			//			if (browser != null) {
			//				browser.addProgressListener(new UrlTrackingListener(browser));	
			//			} 
		} 
	}

	public void partClosed(IWorkbenchPart part) {
		// browsers should dispose their own listeners
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

//	private Browser getBrowser(final WebBrowserEditor browserEditor) {
//		try { // HACK: using reflection to gain accessibility
//			Class<?> browserClass = browserEditor.getClass();
//			Field browserField = browserClass.getDeclaredField("webBrowser");
//			browserField.setAccessible(true);
//			Object browserObject = browserField.get(browserEditor);
//			if (browserObject != null && browserObject instanceof BrowserViewer) {
//				return ((BrowserViewer) browserObject).getBrowser();
//			}
//		} catch (Exception e) {
//			MylarStatusHandler.log(e, "could not add browser listener");
//		}
//		return null;
//	}
}
