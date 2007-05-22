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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarWebPlugin extends AbstractUIPlugin {

	protected static final String ID = "org.eclipse.mylar.web";

	private static MylarWebPlugin INSTANCE;

	private WebResourceManager webResourceManager;

	private BrowserTracker browserTracker;
	
	private Set<String> excludedUrls = new HashSet<String>();

	public MylarWebPlugin() {
		INSTANCE = this;
	}

	public static WebResourceManager getWebResourceManager() {
		return INSTANCE.webResourceManager;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		webResourceManager = new WebResourceManager();
		try {
			browserTracker = new BrowserTracker();
			MonitorUiPlugin.getDefault().addWindowPartListener(browserTracker);

			for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
				String url = repository.getUrl();
				if (url != null) {
					excludedUrls.add(url);
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "Mylar Hypertext initialization failed", false);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		MonitorUiPlugin.getDefault().removeWindowPartListener(browserTracker);
		webResourceManager.dispose();
		super.stop(context);
		INSTANCE = null;
	}

	public static MylarWebPlugin getDefault() {
		return INSTANCE;
	}
	
	/**
	 * @param url	String representation of URL to be excluded from context
	 */
	public void addExcludedUrl(String url) {
		excludedUrls.add(url);
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.internal.web", path);
	}

	public Set<String> getExcludedUrls() {
		return excludedUrls;
	}

}
