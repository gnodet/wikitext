/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.ui.IContextUiStartup;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class WebUiBridgePlugin extends AbstractUIPlugin {

	public static class WebUiBridgeStartup implements IContextUiStartup {

		public void lazyStartup() {
			WebUiBridgePlugin.getDefault().lazyStart();
		}

	}

	protected static final String ID = "org.eclipse.mylyn.web";

	private static WebUiBridgePlugin INSTANCE;

	private WebContextManager webResourceManager;

	private BrowserTracker browserTracker;

	private final Set<String> excludedUrls = new HashSet<String>();

	public WebUiBridgePlugin() {
		INSTANCE = this;
	}

	public static WebContextManager getWebResourceManager() {
		return INSTANCE.webResourceManager;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	private void lazyStart() {
		webResourceManager = new WebContextManager();
		try {
			browserTracker = new BrowserTracker();
			MonitorUi.addWindowPartListener(browserTracker);

			for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
				String url = repository.getRepositoryUrl();
				if (url != null) {
					excludedUrls.add(url);
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, WebUiBridgePlugin.ID, "Mylyn Web UI initialization failed", e));
		}
	}

	private void lazyStop() {
		if (browserTracker != null) {
			MonitorUi.removeWindowPartListener(browserTracker);
		}
		if (webResourceManager != null) {
			webResourceManager.dispose();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		lazyStop();

		super.stop(context);
		INSTANCE = null;
	}

	public static WebUiBridgePlugin getDefault() {
		return INSTANCE;
	}

	/**
	 * @param url
	 *            String representation of URL to be excluded from context
	 */
	public void addExcludedUrl(String url) {
		excludedUrls.add(url);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.internal.web", path);
	}

	public Set<String> getExcludedUrls() {
		return excludedUrls;
	}

}
