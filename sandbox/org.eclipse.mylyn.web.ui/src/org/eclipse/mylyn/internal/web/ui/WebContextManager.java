/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractRetrieveTitleFromUrlJob;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class WebContextManager {

	private static final String FILENAME_CACHE = "title-cache.properties";

	private final WebResourceStructureBridge structureBridge = new WebResourceStructureBridge();

	private WebRoot webRoot = new WebRoot();

	private final Set<IWebResourceListener> listeners = new HashSet<IWebResourceListener>();

	private boolean webContextEnabled = true;

	private final Properties titleCache = new Properties();

	private final AbstractContextListener UPDATE_LISTENER = new AbstractContextListener() {

		@Override
		public void interestChanged(List<IInteractionElement> elements) {
			for (IInteractionElement element : elements) {
				if (WebResourceStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					processUrl(webRoot, element.getHandleIdentifier(), false, true);
				}
			}
		}

		@Override
		public void contextActivated(IInteractionContext context) {
			webContextEnabled = true;
			updateContents();
		}

		@Override
		public void contextDeactivated(IInteractionContext context) {
			if (getGlobalContext() == null) {
				webContextEnabled = false;
			}
			updateContents();
		}

		@Override
		public void contextCleared(IInteractionContext context) {
			if (getGlobalContext() == null) {
				webContextEnabled = false;
			}
			updateContents();
		}
	};

	public WebContextManager() {
		webRoot = new WebRoot();
		ContextCore.getContextManager().addListener(UPDATE_LISTENER);

		try {
			titleCache.load(new FileInputStream(getTitleCacheFile()));
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.WARNING, WebUiBridgePlugin.ID, "Could not load title cache", e));
		}
	}

	public void dispose() {
		ContextCore.getContextManager().removeListener(UPDATE_LISTENER);
		try {
			titleCache.store(new FileOutputStream(getTitleCacheFile()), null);
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.WARNING, WebUiBridgePlugin.ID, "Could not store title cache", e));
		}
	}

	private File getTitleCacheFile() throws IOException {
		String storePath = WebUiBridgePlugin.getDefault().getStateLocation().toOSString();
		File file = new File(storePath + File.separator + FILENAME_CACHE);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public void updateContents() {
		IInteractionContext context;
		if (ContextCore.getContextManager().isContextActive()) {
			context = ContextCore.getContextManager().getActiveContext();
		} else {
			context = getGlobalContext();
		}
		if (context != null) {
			Collection<IInteractionElement> interestingElements = ContextCore.getContextManager().getActiveDocuments(
					context);
			for (IInteractionElement element : interestingElements) {
				// TODO: this check is unnecessary for the global context
				if (WebResourceStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					processUrl(webRoot, element.getHandleIdentifier(), true, true);
				}
			}
		} else {
			webRoot.clear();
		}
		for (IWebResourceListener listener : listeners) {
			listener.webContextUpdated();
		}
	}

	/**
	 * NOTE: returns first found
	 */
	private IInteractionContext getGlobalContext() {
		for (IInteractionContext globalContext : ContextCorePlugin.getContextManager().getGlobalContexts()) {
			if (globalContext.getContentLimitedTo().equals(WebResourceStructureBridge.CONTENT_TYPE)) {
				return globalContext;
			}
		}
		return null;
	}

	public WebResource find(String url) {
		if (WebRoot.HANDLE_ROOT.equals(url)) {
			return webRoot;
		} else {
			String siteUrl = structureBridge.getSite(url);
			if (siteUrl != null) {
				WebSite webSite = webRoot.getSite(siteUrl);
				if (webSite != null) {
					if (url.equals(siteUrl)) {
						return webSite;
					} else {
						return webSite.getPage(url);
					}
				}
			}
			return null;
		}
	}

	public void processUrl(WebRoot webRootToAdd, String url, boolean restore, boolean nestSites) {
		String siteUrl = structureBridge.getSite(url);
		if (siteUrl != null) {
			WebPage page = null;
			WebSite webSite = null;
			if (nestSites) {
				webSite = webRootToAdd.getSite(siteUrl);
				if (webSite == null) {
					webSite = new WebSite(siteUrl);
					webRootToAdd.addSite(webSite);
				}
				if (!url.equals(siteUrl)) {
					WebPage existingPage = webSite.getPage(url);
					page = (existingPage == null) ? new WebPage(url, webSite) : existingPage;
					webSite.addPage(page);
				}
			} else {
				webSite = new WebSite(url);
				webRootToAdd.addSite(webSite);
			}
			if (page != null) {
				if (restore) {
					String cachedtitle = titleCache.getProperty(url);
					if (cachedtitle != null) {
						page.setTitle(cachedtitle);
					}
				} else {
					updateTitle(page);
				}
			}
			if (!restore) {
				if (nestSites) {
					for (IWebResourceListener listener : listeners) {
						listener.webSiteUpdated(webSite);
					}
				} else {
					for (IWebResourceListener listener : listeners) {
						listener.webContextUpdated();
					}
				}
			}
		}
	}

	public void updateTitle(final WebPage page) {
		AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(page.getUrl()) {
			@Override
			protected void titleRetrieved(final String pageTitle) {
				page.setTitle(pageTitle);
				titleCache.put(page.getUrl(), pageTitle);
				for (IWebResourceListener listener : listeners) {
					listener.webPageUpdated(page);
				}
			}
		};
		job.schedule();
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

	public boolean isWebContextEnabled() {
		return webContextEnabled;
	}
}
