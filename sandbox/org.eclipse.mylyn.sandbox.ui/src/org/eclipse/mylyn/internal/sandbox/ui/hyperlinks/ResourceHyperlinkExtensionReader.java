/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Jingwen Ou
 */
public class ResourceHyperlinkExtensionReader {

	public static final String ATTR_FILE_TYPE = "fileType";

	public static final String ATTR_GENERATED_PREFIX = "generatedPrefix";

	public static final String EXTENSION_RESOURCE_HYPERLINK_EXTENSIONS = "org.eclipse.mylyn.sandbox.ui.resourceHyperlinkExtensions";

	private static final String RESOURCE_HYPERLINK_EXTENSION = "resourceHyperlinkExtension";

	public static void initExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint editorExtensionPoint = registry.getExtensionPoint(EXTENSION_RESOURCE_HYPERLINK_EXTENSIONS);
		IExtension[] editorExtensions = editorExtensionPoint.getExtensions();
		for (IExtension extension : editorExtensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(RESOURCE_HYPERLINK_EXTENSION)) {
					readResourceHyperlinkExtension(element);
				}
			}
		}
	}

	private static void readResourceHyperlinkExtension(IConfigurationElement element) {
		try {
			String fileType = element.getAttribute(ATTR_FILE_TYPE);
			String generatedPrefix = element.getAttribute(ATTR_GENERATED_PREFIX);
			Object extension = element.createExecutableExtension("class");
			if (extension instanceof IResourceHyperlinkExtension) {
				ResourceHyperlinkExtensions.addResourceHyperlinkExtension(fileType, generatedPrefix,
						(IResourceHyperlinkExtension) extension);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load resourceHyperlinkExtension: expected instanceof IResourceHyperlink, got "
								+ extension.getClass()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load resourceHyperlinkExtension", e));
		}
	}
}
