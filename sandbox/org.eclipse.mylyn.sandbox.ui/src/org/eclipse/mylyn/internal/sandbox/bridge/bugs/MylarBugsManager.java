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
package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class MylarBugsManager {

	public static ImageDescriptor EDGE_REF_BUGZILLA = getImageDescriptor("icons/elcl16/edge-ref-bug.gif");

	private static BugzillaSearchManager bridge = null;

	private static BugzillaReferencesProvider referencesProvider = new BugzillaReferencesProvider();

	private static MylarBugsManager INSTANCE;

//	private BugzillaReportCache cache

	public MylarBugsManager() {
		INSTANCE = this;
//		cache = new BugzillaReportCache();
//		cache.readCacheFile();

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			// create a new bridge and initialize it
			bridge = new BugzillaSearchManager();
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				// bugzillaEditingMonitor = new BugzillaEditingMonitor();
				// ContextCorePlugin.getDefault().getSelectionMonitors().add(bugzillaEditingMonitor);
			}
		});
	}

	public void dispose(BundleContext context) throws Exception {
		// ContextCorePlugin.getDefault().getSelectionMonitors().remove(bugzillaEditingMonitor);
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarBugsManager getDefault() {
		return INSTANCE;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.internal.bugs.bridge", path);
	}

	public static BugzillaSearchManager getBridge() {
		// make sure that the bridge initialized, if not, make a new one
		if (bridge == null) {
			bridge = new BugzillaSearchManager();
		}
		return bridge;
	}

	public static BugzillaReferencesProvider getReferenceProvider() {
		return referencesProvider;

	}

//	public BugzillaReportCache getCache() {
//		return cache;
//	}
}
