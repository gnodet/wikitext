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

package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.sandbox.ui.highlighters.Highlighter;
import org.eclipse.mylyn.internal.sandbox.ui.highlighters.HighlighterList;
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

	public static final String HIGHLIGHTER_PREFIX = "org.eclipse.mylyn.ui.interest.highlighters";

	public static final String TASK_HIGHLIGHTER_PREFIX = "org.eclipse.mylyn.ui.interest.highlighters.task.";

	public final static String ID_PLUGIN = "org.eclipse.mylyn.sandbox.ui";

	private static SandboxUiPlugin plugin;

	private final SharedDataDirectoryManager sharedDataDirectoryManager = new SharedDataDirectoryManager();

	public static final String OVERLAYS_INCOMING_TIGHT = "org.eclipse.mylyn.tasks.ui.overlays.incoming.tight";

	private final ActiveSearchViewTracker activeSearchViewTracker = new ActiveSearchViewTracker();

	private InterestInducingProblemListener problemListener;

	private HighlighterList highlighters;

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

		// initialize colors to work around deadlock on bug 237596
		getHighlighterList();

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
		if (highlighters != null) {
			highlighters.dispose();
			highlighters = null;
		}

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

		plugin = null;
		super.stop(context);
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

	private void initializeHighlighters() {
		String hlist = getPreferenceStore().getString(HIGHLIGHTER_PREFIX);
		if (hlist.length() == 0) {
			// migrate preference from context ui
			hlist = ContextUiPlugin.getDefault().getPreferenceStore().getString(HIGHLIGHTER_PREFIX);
			getPreferenceStore().setValue(HIGHLIGHTER_PREFIX, hlist);
			ContextUiPlugin.getDefault().getPreferenceStore().setToDefault(HIGHLIGHTER_PREFIX);
		}
		if (hlist.length() == 0) {
			highlighters = new HighlighterList();
			highlighters.setToDefaultList();
		} else {
			highlighters = new HighlighterList(hlist);
		}
	}

	public synchronized HighlighterList getHighlighterList() {
		if (highlighters == null) {
			initializeHighlighters();
		}
		return highlighters;
	}

	public List<Highlighter> getHighlighters() {
		return getHighlighterList().getHighlighters();
	}

	/**
	 * @return null if not found
	 */
	public Highlighter getHighlighter(String name) {
		return getHighlighterList().getHighlighter(name);
	}

	public Highlighter getHighlighterForContextId(String id) {
		String prefId = TASK_HIGHLIGHTER_PREFIX + id;
		String highlighterName = getPreferenceStore().getString(prefId);
		if (highlighterName.equals("")) {
			highlighterName = ContextUiPlugin.getDefault().getPreferenceStore().getString(prefId);
			getPreferenceStore().setValue(prefId, highlighterName);
			ContextUiPlugin.getDefault().getPreferenceStore().setToDefault(prefId);
		}
		return getHighlighter(highlighterName);
	}

	public void setHighlighterMapping(String id, String name) {
		String prefId = TASK_HIGHLIGHTER_PREFIX + id;
		getPreferenceStore().putValue(prefId, name);
	}

}
