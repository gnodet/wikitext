package org.eclipse.mylyn.internal.examples.xml.ui;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author Steffen Pingel
 */
public class XmlUiPlugin extends Plugin {

	public static final String ID_PLUGIN = "org.eclipse.mylyn.examples.xml.ui"; //$NON-NLS-1$

	private static XmlUiPlugin plugin;

	public XmlUiPlugin() {
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

	public static XmlUiPlugin getDefault() {
		return plugin;
	}

}
