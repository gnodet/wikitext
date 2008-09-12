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

package org.eclipse.mylyn.internal.compatibility;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

/**
 * Checks the JRE version and show a dialog if an incompatible version is found.
 * 
 * @author Mik Kersten
 */
public class JavaRuntimeVersionChecker extends AbstractUIPlugin implements IStartup {

	private static final String ID_PLUGIN = "org.eclipse.mylyn.compatibility";

	private static final String PREF_WARN_DISABLED = "org.eclipse.mylyn.internal.compatibility.warn.disabled";

	private static final float JRE_MIN_VERSION = 1.5f;

	public JavaRuntimeVersionChecker() {
		// ignore
	}

	public void earlyStartup() {
		// ignore
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					String versionString = System.getProperty("java.runtime.version");
					int minorMinorIndex = versionString.lastIndexOf('.');
					if (minorMinorIndex != -1) {
						String minorString = versionString.substring(0, minorMinorIndex);
						Float versionFloat = new Float(minorString);
						if (versionFloat.compareTo(new Float(JRE_MIN_VERSION)) < 0) {
							if (!getPreferenceStore().getBoolean(PREF_WARN_DISABLED)) {
								MessageDialogWithToggle dialog = MessageDialogWithToggle.openWarning(
										PlatformUI.getWorkbench().getDisplay().getActiveShell(), "JDK Version Check",
										"Mylyn was installed but requires Java 5 or later to run. "
												+ "Please download and install the latest Java version and restart.",
										"Do not warn again", false, getPreferenceStore(), PREF_WARN_DISABLED);
								getPreferenceStore().setValue(PREF_WARN_DISABLED, dialog.getToggleState());
							}
						}
					}
				} catch (Throwable t) {
					StatusManager.getManager().handle(
							new Status(IStatus.INFO, ID_PLUGIN, "Could determine JRE version.", t), StatusManager.LOG);
				}
			}
		});
	}
}
