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

	private final SharedDataDirectoryManager sharedDataDirectoryManager = new SharedDataDirectoryManager();

	public static final String OVERLAYS_INCOMING_TIGHT = "org.eclipse.mylyn.tasks.ui.overlays.incoming.tight";

	private final ActiveSearchViewTracker activeSearchViewTracker = new ActiveSearchViewTracker();

	private InterestInducingProblemListener problemListener;

	public SandboxUiPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		getPreferenceStore().setDefault(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, false);

		problemListener = new InterestInducingProblemListener();
		getPreferenceStore().addPropertyChangeListener(problemListener);
		if (getPreferenceStore().getBoolean(InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS)) {
			problemListener.enable();
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					workbench.addWindowListener(activeSearchViewTracker);
					IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
					for (IWorkbenchWindow window : windows) {
						window.addPageListener(activeSearchViewTracker);
						IWorkbenchPage[] pages = window.getPages();
						for (IWorkbenchPage page : pages) {
							page.addPartListener(activeSearchViewTracker);
						}
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, SandboxUiPlugin.ID_PLUGIN,
							"Sandbox UI initialization failed", e));
				}
			}
		});
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;

		if (problemListener != null) {
			getPreferenceStore().removePropertyChangeListener(problemListener);
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			workbench.removeWindowListener(activeSearchViewTracker);
			IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
			for (IWorkbenchWindow window : windows) {
				IWorkbenchPage[] pages = window.getPages();
				window.removePageListener(activeSearchViewTracker);
				for (IWorkbenchPage page : pages) {
					page.removePartListener(activeSearchViewTracker);
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
