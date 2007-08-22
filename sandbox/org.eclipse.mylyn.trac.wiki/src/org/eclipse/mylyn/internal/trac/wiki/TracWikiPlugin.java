/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.trac.wiki;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class TracWikiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.trac.wiki";

	private static TracWikiPlugin plugin;

	public TracWikiPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static TracWikiPlugin getDefault() {
		return plugin;
	}

}
