/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core.service;

import org.eclipse.core.runtime.*;
import org.eclipse.mylyn.xplanner.core.IXPlannerCoreExtensionConstants;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;


/**
 * TODO This is eclipse specific at the moment.  Need to find a way to load a
 * XPlannerService instance based off some use preference.
 * TODO mention that this should really only be used by internal classes and tests
 *
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class ServiceManager {
	
	private static XPlannerServiceFactory factory;
	
	public static synchronized XPlannerService getXPlannerService(XPlannerClient client) {
		if (factory == null) {
			factory = loadServiceProviderFactories();
		}
		return factory.createService(client);
	}
	
	public static XPlannerServiceFactory loadServiceProviderFactories() {
		XPlannerCorePlugin plugin = XPlannerCorePlugin.getDefault();
		if (plugin != null) {
			IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(XPlannerCorePlugin.ID, IXPlannerCoreExtensionConstants.SERVICE_PROVIDER_FACTORY);
			if (extension != null) {
				IExtension[] extensions =  extension.getExtensions();
				for (int i = 0; i < extensions.length; i++) {
					IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
					for (int j = 0; j < configElements.length; j++) {
						try {
							return (XPlannerServiceFactory) configElements[j].createExecutableExtension("class"); //$NON-NLS-1$
						} 
						catch (CoreException e) {
							plugin.getLog().log(e.getStatus());
						} 
						catch (ClassCastException e) {
							XPlannerCorePlugin.log(IStatus.ERROR, "Must implement the correct class", e); //$NON-NLS-1$
							XPlannerCorePlugin.log(IStatus.ERROR, "Must implement the correct class", e); //$NON-NLS-1$
						}
						return null;
					}
				}
			}		
		}
		return null;
	}	
}
