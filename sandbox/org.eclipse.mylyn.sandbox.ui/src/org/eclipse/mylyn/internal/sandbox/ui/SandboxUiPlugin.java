/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class SandboxUiPlugin extends AbstractUIPlugin {

	public final static String ID_PLUGIN = "org.eclipse.mylyn.sandbox.ui";
	
	private static SandboxUiPlugin plugin;

	private SharedDataDirectoryManager sharedDataDirectoryManager = new SharedDataDirectoryManager();

	public static final String OVERLAYS_INCOMING_TIGHT = "org.eclipse.mylyn.tasks.ui.overlays.incoming.tight";

	private ActiveSearchViewTracker activeSearchViewTracker = new ActiveSearchViewTracker();
	
	public SandboxUiPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					workbench.addWindowListener(activeSearchViewTracker);
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (int i = 0; i < windows.length; i++) {
						windows[i].addPageListener(activeSearchViewTracker);
						IWorkbenchPage[] pages = windows[i].getPages();
						for (int j = 0; j < pages.length; j++) {
							pages[j].addPartListener(activeSearchViewTracker);
						}
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, SandboxUiPlugin.ID_PLUGIN, "Sandbox UI initialization failed", e));
				}
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			workbench.removeWindowListener(activeSearchViewTracker);
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			for (int i = 0; i < windows.length; i++) {
				IWorkbenchPage[] pages = windows[i].getPages();
				windows[i].removePageListener(activeSearchViewTracker);
				for (int j = 0; j < pages.length; j++) {
					pages[j].removePartListener(activeSearchViewTracker);
				}
			}
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static SandboxUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylyn.internal.sandbox", path);
	}

	public SharedDataDirectoryManager getSharedDataDirectoryManager() {
		return sharedDataDirectoryManager;
	}
}
