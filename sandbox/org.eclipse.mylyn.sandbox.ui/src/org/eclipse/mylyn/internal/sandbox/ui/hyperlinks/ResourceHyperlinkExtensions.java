/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * A manager that returns registered IResourceHyperlinkExtension
 * 
 * @author Jingwen Ou
 */
public class ResourceHyperlinkExtensions {

	public static class RegisteredResourceHyperlinkExtension {

		private final IResourceHyperlinkExtension extension;

		private final String fileType;

		private final String generatedPrefix;

		private RegisteredResourceHyperlinkExtension(IResourceHyperlinkExtension extension, String fileType,
				String generatedPrefix) {
			this.extension = extension;
			this.fileType = fileType;
			this.generatedPrefix = generatedPrefix;
		}

		public IResourceHyperlinkExtension getExtension() {
			return extension;
		}

		public String getFileType() {
			return fileType;
		}

		public String getGeneratedPrefix() {
			return generatedPrefix;
		}
	}

	private static Map<String, RegisteredResourceHyperlinkExtension> extensionByFileType = new HashMap<String, RegisteredResourceHyperlinkExtension>();

	private static boolean initialized = false;

	public static void addResourceHyperlinkExtension(String fileType, String generatedPrefix,
			IResourceHyperlinkExtension extension) {
		Assert.isNotNull(fileType);

		RegisteredResourceHyperlinkExtension previous = extensionByFileType.put(fileType,
				new RegisteredResourceHyperlinkExtension(extension, fileType, generatedPrefix));

		if (previous != null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Duplicate resourceHyperlinkExtension fileType=" + fileType, null));
		}
	}

	public static IResourceHyperlinkExtension getResourceHyperlinkExtension(String fileType) {
		init();
		RegisteredResourceHyperlinkExtension resourceHyperlinkExtension = extensionByFileType.get(fileType);
		return resourceHyperlinkExtension == null ? null : resourceHyperlinkExtension.getExtension();
	}

	public static String getGeneratedPrefix(String fileType) {
		init();
		RegisteredResourceHyperlinkExtension resourceHyperlinkExtension = extensionByFileType.get(fileType);
		return resourceHyperlinkExtension == null ? null : resourceHyperlinkExtension.getGeneratedPrefix();
	}

	public static String getDefaultPrefix() {
		return getGeneratedPrefix("default");
	}

	public static List<IResourceHyperlinkExtension> getResourceHyperlinkExtensions() {
		init();
		List<IResourceHyperlinkExtension> resourceHyperlinkExtensions = new ArrayList<IResourceHyperlinkExtension>(
				extensionByFileType.size());
		for (RegisteredResourceHyperlinkExtension resourceHyperlinkExtension : extensionByFileType.values()) {
			resourceHyperlinkExtensions.add(resourceHyperlinkExtension.getExtension());
		}
		return resourceHyperlinkExtensions;
	}

	private static void init() {
		if (!initialized) {
			initialized = true;
			ResourceHyperlinkExtensionReader.initExtensions();
		}
	}
}
