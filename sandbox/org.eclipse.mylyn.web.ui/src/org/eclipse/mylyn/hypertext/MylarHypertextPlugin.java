package org.eclipse.mylar.hypertext;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarHypertextPlugin extends AbstractUIPlugin {

	private static MylarHypertextPlugin plugin;
	private BrowserTracker browserTracker;
	
	public MylarHypertextPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() { 
            	try {
	            	browserTracker = new BrowserTracker();
	            	workbench.addWindowListener(browserTracker);
					IWorkbenchWindow[] windows= workbench.getWorkbenchWindows();
					for (int i= 0; i < windows.length; i++) {
						windows[i].addPageListener(browserTracker);
						IWorkbenchPage[] pages= windows[i].getPages();
						for (int j= 0; j < pages.length; j++) {
							pages[j].addPartListener(browserTracker);
						}
					}
        		} catch (Exception e) {
        			ErrorLogger.fail(e, "Mylar Hypertext initialization failed", false);
        		}
            }
        });
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MylarHypertextPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.mylar.hypertext", path);
	}

	public void earlyStartup() {
		// TODO Auto-generated method stub
		
	}
}
