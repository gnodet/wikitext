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

package org.eclipse.mylar.internal.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.RetrieveTitleFromUrlJob;

/**
 * @author Mik Kersten
 */
public class WebResourceManager {

	private static final String FILENAME_CACHE = "title-cache.properties";

	private WebResourceStructureBridge structureBridge = new WebResourceStructureBridge();

	private WebRoot webRoot = new WebRoot();

	private Set<IWebResourceListener> listeners = new HashSet<IWebResourceListener>();

	private boolean webContextEnabled = true;

	private Properties titleCache = new Properties();

	private final IMylarContextListener UPDATE_LISTENER = new IMylarContextListener() {

		public void interestChanged(List<IMylarElement> elements) {
			for (IMylarElement element : elements) {
				if (WebResourceStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					addUrl(element.getHandleIdentifier(), false);
				}
			}
		}

		public void contextActivated(IMylarContext context) {
			webContextEnabled = true;
			updateContents(true);
		}

		public void contextDeactivated(IMylarContext context) {
			webContextEnabled = false;
			updateContents(false);
		}

		public void landmarkAdded(IMylarElement node) {
			// ignore
		}

		public void landmarkRemoved(IMylarElement node) {
			// ignore
		}

		public void relationsChanged(IMylarElement node) {
			// ignore
		}

		public void elementDeleted(IMylarElement node) {
			// ignore
		}
	};

	public WebResourceManager() {
		webRoot = new WebRoot();
		ContextCorePlugin.getContextManager().addListener(UPDATE_LISTENER);

		try {
			titleCache.load(new FileInputStream(getTitleCacheFile()));
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "could not load title cache", false);
		}
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(UPDATE_LISTENER);
		try {
			titleCache.store(new FileOutputStream(getTitleCacheFile()), null);
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "could not store title cache", false);
		}
	}

	private File getTitleCacheFile() throws IOException {
		String storePath = MylarWebPlugin.getDefault().getStateLocation().toOSString();
		File file = new File(storePath + File.separator + FILENAME_CACHE);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	protected void updateContents(boolean populate) {
		if (populate) {
			Collection<IMylarElement> interestingElements = ContextCorePlugin.getContextManager().getInterestingDocuments();
			for (IMylarElement element : interestingElements) {
				if (WebResourceStructureBridge.CONTENT_TYPE.equals(element.getContentType())) {
					addUrl(element.getHandleIdentifier(), true);
				}
			}
		} else {
			webRoot.clear();
		}
		for (IWebResourceListener listener : listeners) {
			listener.webContextUpdated();
		}
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

	private void addUrl(String url, boolean restore) {
		String siteUrl = structureBridge.getSite(url);
		if (siteUrl != null) {
			WebSite webSite = webRoot.getSite(siteUrl);
			if (webSite == null) {
				webSite = new WebSite(siteUrl);
				webRoot.addSite(webSite);
			}
			if (!url.equals(siteUrl)) {
				WebPage existingPage = webSite.getPage(url);
				final WebPage page = (existingPage == null) ? new WebPage(url, webSite) : existingPage;
				webSite.addPage(page);
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
				for (IWebResourceListener listener : listeners) {
					listener.webSiteUpdated(webSite);
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
