/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.aspectj;

import org.eclipse.mylar.java.MylarJavaPlugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 */
public class MylarAJPlugin extends MylarJavaPlugin implements IStartup {
	
	private static MylarAJPlugin plugin;
	
//	private MylarJavaPlugin.Both f;
//	protected MylarJavaPlugin.Both getBoth() {
//		if (f == null) {
//		 f = new MylarJavaPlugin.JavaFactoryImpl() {
//			 public JavaEditingMonitor newEditingMonitor() {return new AJEditingMonitor(true);}
//			 public JavaEditorTracker newEditorTracker() {return new AJEditorTracker(true);}
//			 public JavaStructureBridge newStructureBridge() {return new AJStructureBridge(true);}			 
//			};
//		}
//		return f;
//	}
    

	public String getId() {return "org.eclipse.mylar.aspectj";}
  public String getEditorId() {return "org.eclipse.mylar.aspectj.ui.editor.MylarCompilationUnitEditor";}
  protected String getBundleName() {return "org.eclipse.mylar.aspectj.AJPluginResources";}  
    
 //TODO: I don't dig this cast
  protected void setDefaultPlugin(MylarJavaPlugin p) {plugin = (MylarAJPlugin)p;}
  
  //
  // Why do I have to override these?
  //
  public void earlyStartup() {super.earlyStartup();}  
  public void start(BundleContext context) throws Exception {super.start(context);}
  public void stop(BundleContext context) throws Exception {super.stop(context);}
    
	public MylarAJPlugin() {}

    
	/**
	 * Returns the shared instance.
	 */
	public static MylarAJPlugin getDefault() {
		return plugin;
	}
}
