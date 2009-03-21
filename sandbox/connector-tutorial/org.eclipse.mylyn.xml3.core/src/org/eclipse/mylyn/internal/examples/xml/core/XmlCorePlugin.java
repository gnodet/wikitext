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

package org.eclipse.mylyn.internal.examples.xml.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class XmlCorePlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.examples.xml.core"; //$NON-NLS-1$

	public static final String CONNECTOR_KIND = "org.eclipse.mylyn.examples.xml"; //$NON-NLS-1$

	public static final String REPOSITORY_KEY_PATH = ID_PLUGIN + ".path";

	public static final String QUERY_KEY_SUMMARY = ID_PLUGIN + ".summary";

	public static final String QUERY_KEY_PROJECT = ID_PLUGIN + ".project";

	private static XmlCorePlugin plugin;

	public XmlCorePlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static XmlCorePlugin getDefault() {
		return plugin;
	}

}
