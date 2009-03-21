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
