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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
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

	public MylarWebPlugin() {
		INSTANCE = this;
	}

	public static WebResourceManager getWebResourceManager() {
		return INSTANCE.webResourceManager;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		webResourceManager = new WebResourceManager();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					browserTracker = new BrowserTracker();
					MylarPlugin.getDefault().addWindowPartListener(browserTracker);
										
//					workbench.addWindowListener(browserTracker);
//					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//					for (int i = 0; i < windows.length; i++) {
//						windows[i].addPageListener(browserTracker);
//						IWorkbenchPage[] pages = windows[i].getPages();
//						for (int j = 0; j < pages.length; j++) {
//							pages[j].addPartListener(browserTracker);
//						}
//					}
				} catch (Exception e) {
					MylarStatusHandler.fail(e, "Mylar Hypertext initialization failed", false);
				}
			}
		});
	}

	public void stop(BundleContext context) throws Exception {
		MylarPlugin.getDefault().removeWindowPartListener(browserTracker);
		webResourceManager.dispose();
		super.stop(context);
		INSTANCE = null;
	}

	public static MylarWebPlugin getDefault() {
		return INSTANCE;
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

}
