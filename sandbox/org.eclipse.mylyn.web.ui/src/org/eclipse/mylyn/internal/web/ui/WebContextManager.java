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

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.RetrieveTitleFromUrlJob;

/**
 * @author Mik Kersten
 */
public class WebContextManager {

	private static final String FILENAME_CACHE = "title-cache.properties";

	private WebResourceStructureBridge structureBridge = new WebResourceStructureBridge();

	private WebRoot webRoot = new WebRoot();

	private Set<IWebResourceListener> listeners = new HashSet<IWebResourceListener>();

	private boolean webContextEnabled = true;

	private Properties titleCache = new Properties();

	private final IInteractionContextListener UPDATE_LISTENER = new IInteractionContextListener() {

		public void interestChanged(List<IInteractionElement> elements) {
			for (IInteractionElement element : elements) {
				if (WebResourceStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					processUrl(webRoot, element.getHandleIdentifier(), false, true);
				}
			}
		}

		public void contextActivated(IInteractionContext context) {
			webContextEnabled = true;
			updateContents();
		}

		public void contextDeactivated(IInteractionContext context) {
			if (getGlobalContext() == null) {
				webContextEnabled = false;
			}
			updateContents();
		}

		public void contextCleared(IInteractionContext context) {
			if (getGlobalContext() == null) {
				webContextEnabled = false;
			}
			updateContents();
		}

		public void landmarkAdded(IInteractionElement node) {
			// ignore
		}

		public void landmarkRemoved(IInteractionElement node) {
			// ignore
		}

		public void relationsChanged(IInteractionElement node) {
			// ignore
		}

		public void elementDeleted(IInteractionElement node) {
			// ignore
		}
	};

	public WebContextManager() {
		webRoot = new WebRoot();
		ContextCorePlugin.getContextManager().addListener(UPDATE_LISTENER);

		try {
			titleCache.load(new FileInputStream(getTitleCacheFile()));
		} catch (IOException e) {
			StatusManager.fail(e, "could not load title cache", false);
		}
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(UPDATE_LISTENER);
		try {
			titleCache.store(new FileOutputStream(getTitleCacheFile()), null);
		} catch (IOException e) {
			StatusManager.fail(e, "could not store title cache", false);
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
		if (ContextCorePlugin.getContextManager().isContextActive()) {
			context = ContextCorePlugin.getContextManager().getActiveContext();
		} else {
			context = getGlobalContext();
		}
		if (context != null) {
			Collection<IInteractionElement> interestingElements = ContextCorePlugin.getContextManager()
					.getInterestingDocuments(context);
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
		for (InteractionContext globalContext : ContextCorePlugin.getContextManager().getGlobalContexts()) {
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
				if (restore && page != null) {
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
		RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(page.getUrl()) {
			@Override
			protected void setTitle(final String pageTitle) {
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
