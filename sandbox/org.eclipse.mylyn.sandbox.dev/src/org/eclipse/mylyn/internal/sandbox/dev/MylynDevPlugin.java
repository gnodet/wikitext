package org.eclipse.mylyn.internal.sandbox.dev;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.sandbox.dev.properties.MylynPropertiesSourceAdapterFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.properties.IPropertySource;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MylynDevPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.mylyn.sandbox.dev";

	private static MylynDevPlugin INSTANCE;

	private MylynPropertiesSourceAdapterFactory factory;

	/**
	 * The constructor
	 */
	public MylynDevPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		INSTANCE = this;
		factory = new MylynPropertiesSourceAdapterFactory();
		IAdapterManager mgr = Platform.getAdapterManager();
		mgr.registerAdapters(factory, IPropertySource.class);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Platform.getAdapterManager().unregisterAdapters(factory);
		factory = null;
		INSTANCE = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static MylynDevPlugin getDefault() {
		return INSTANCE;
	}
}
