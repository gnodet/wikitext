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

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Checks the JRE version and show a dialog if an incompatible version is found.
 * 
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class JavaRuntimeVersionChecker implements IStartup {

	private static final String ID_PLUGIN = "org.eclipse.mylyn.compatibility"; //$NON-NLS-1$

	private static final String PREF_WARN_DISABLED = "org.eclipse.mylyn.internal.compatibility.warn.disabled"; //$NON-NLS-1$

	private static final float JRE_MIN_VERSION = 1.5f;

	public void earlyStartup() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					String javaVersion = System.getProperty("java.runtime.version"); //$NON-NLS-1$
					if (parseVersion(javaVersion).compareTo(new Float(JRE_MIN_VERSION)) < 0) {
						IPersistentPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(),
								ID_PLUGIN);
						if (!preferenceStore.getBoolean(PREF_WARN_DISABLED)) {
							MessageDialogWithToggle dialog = MessageDialogWithToggle.openWarning(
									PlatformUI.getWorkbench().getDisplay().getActiveShell(),
									Messages.JavaRuntimeVersionChecker_JDK_Version_Check,
									Messages.JavaRuntimeVersionChecker_Mylyn_was_installed_but_requires_Java_5_or_later_to_run,
									Messages.JavaRuntimeVersionChecker_Do_not_warn_again, false, preferenceStore,
									PREF_WARN_DISABLED);
							preferenceStore.setValue(PREF_WARN_DISABLED, dialog.getToggleState());
							preferenceStore.save();
						}
					}
				} catch (Throwable t) {
					// ignore, error does not provide any value to the user but gets logged on every start
//					StatusManager.getManager().handle(
//							new Status(IStatus.INFO, ID_PLUGIN, "Could determine JRE version.", t), StatusManager.LOG); //$NON-NLS-1$
				}
			}
		});
	}

	public static Float parseVersion(String versionString) {
		if (versionString != null) {
			int minorIndex = versionString.indexOf('.');
			if (minorIndex != -1) {
				try {
					// look for the second dot
					int minorMinorIndex = versionString.indexOf('.', minorIndex + 1);
					if (minorMinorIndex != -1) {
						return new Float(versionString.substring(0, minorMinorIndex));
					}
					return new Float(versionString);
				} catch (NumberFormatException e) {
					// ignore
				}
			}
		}
		return new Float(0.0f);
	}

}
