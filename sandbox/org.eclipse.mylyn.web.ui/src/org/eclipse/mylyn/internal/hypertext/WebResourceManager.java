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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class WebResourceManager {

	private WebStructureBridge structureBridge = new WebStructureBridge();

	private WebRoot webRoot = new WebRoot();

	private Set<IWebResourceListener> listeners = new HashSet<IWebResourceListener>();

	private final IMylarContextListener UPDATE_LISTENER = new IMylarContextListener() {

		public void interestChanged(List<IMylarElement> elements) {
			for (IMylarElement element : elements) {
				if (WebStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					addUrl(element.getHandleIdentifier(), true);
				}
			}
		}

		public void contextActivated(IMylarContext context) {
			update(true);
		}

		public void contextDeactivated(IMylarContext context) {
			update(false);
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
		}

		public void landmarkAdded(IMylarElement node) {
			// ignore
		}

		public void landmarkRemoved(IMylarElement node) {
			// ignore
		}

		public void edgesChanged(IMylarElement node) {
			// ignore
		}

		public void nodeDeleted(IMylarElement node) {
			// ignore
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
		}
	};

	public WebResourceManager() {
		webRoot = new WebRoot();
		MylarPlugin.getContextManager().addListener(UPDATE_LISTENER);
	}

	public void dispose() {
		MylarPlugin.getContextManager().removeListener(UPDATE_LISTENER);
	}

	protected void update(boolean populate) {
		if (populate) {
			List<IMylarElement> interestingElements = MylarPlugin.getContextManager().getInterestingDocuments();
			for (IMylarElement element : interestingElements) {
				if (WebStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					addUrl(element.getHandleIdentifier(), false);
				}
			}
		} else {
			webRoot.clear();
		}
		for (IWebResourceListener listener : listeners) {
			listener.webContextUpdated();
		}
	}

	private void addUrl(String url, boolean notify) {
		String siteUrl = structureBridge.getSite(url);
		if (siteUrl != null) {
			WebSite webSite = webRoot.getSite(siteUrl);
			if (webSite == null) {
				webSite = new WebSite(siteUrl, webRoot);
				webRoot.addSite(webSite);
			}
			if (!url.equals(siteUrl)) {
				WebPage page = webSite.getPage(url);
				if (page == null) {
					page = new WebPage(url, webSite);
					webSite.addPage(page);
				}
			}
			if (notify) {
				for (IWebResourceListener listener : listeners) {
					listener.webSiteUpdated(webSite);
				}
			}
		}
	}

	public WebRoot getWebRoot() {
		return webRoot;
	}

	public void addListener(IWebResourceListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IWebResourceListener listener) {
		listeners.remove(listener);
	}
}
