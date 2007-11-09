/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core;

import java.io.File;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisProperties;
import org.eclipse.core.runtime.*;
import org.eclipse.mylyn.xplanner.core.service.exceptions.AuthenticationException;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Helen Bershadskaya 
 * @author Ravi Kumar 
 */
public class XPlannerCorePlugin extends Plugin {
	public static final String ID = "org.eclipse.mylyn.xplanner.core"; //$NON-NLS-1$
	
	//The shared instance.
	private static XPlannerCorePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private XPlannerClientManager clientManager;
	
	/**
	 * The constructor.
	 */
	public XPlannerCorePlugin() {
		super();
		plugin = this;

		// disable Axis attachment support, see bug 197819
		AxisProperties.setProperty(AxisEngine.PROP_ATTACHMENT_IMPLEMENTATION, "org.eclipse.mylyn.does.not.exist");
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		File clientCache = getStateLocation().append("serverCache").toFile(); //$NON-NLS-1$
		
		// Turn off logging for the Attachment check.  We don't want or need soap with attachments
		Logger logger = Logger.getLogger("org.apache.axis.utils.JavaUtils"); //$NON-NLS-1$
		logger.setLevel(Level.SEVERE);
		
		clientManager = new XPlannerClientManager(clientCache);
		clientManager.start();
	}

	/**
	 * @return
	 * 
	 */
	//TODO: look at using this
/*
	private PasswordPrompter getPasswordPrompter() {
		return new PasswordPrompter() {
		
			public String getPassword(URL baseURL, String username) {
				Map authenticationInfo = Platform.getAuthorizationInfo(baseURL, "XPlanner", ""); //$NON-NLS-1$ //$NON-NLS-2$
//				String username = (String) authenticationInfo.get("org.eclipse.mylyn.xplanner.core.username"); //$NON-NLS-1$
				String password = (String) authenticationInfo.get("org.eclipse.mylyn.xplanner.core.password"); //$NON-NLS-1$
				return password;
			}
		
		};
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		
		if (clientManager != null) {
			clientManager.stop();
		}
		plugin = null;
		resourceBundle = null;
		clientManager = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static XPlannerCorePlugin getDefault() {
		return plugin;
	}

	public XPlannerClientManager getClientManager() {
		return clientManager;
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = XPlannerCorePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.eclipse.mylyn.xplanner.core.XPlannerCorePluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	public static void log(int severity, String message, Throwable e) {
		getDefault().getLog().log(new Status(severity, ID, -1, message, e));
	}

	public static IStatus toStatus(Throwable e) {
		IStatus status;

		if (e instanceof AuthenticationException) {
			String errorMessage = MessageFormat.format(Messages.XPlannerValidator_INVALID_CREDENTIALS_ERROR,
										e.getMessage());
			status = new Status(IStatus.ERROR, ID, Status.OK, 
					errorMessage, e);
		} 
		else if (e instanceof Exception) {
			String message = e.getMessage();
			if (message == null) {
				message = ""; //$NON-NLS-1$
			}
			String errorMessage = MessageFormat.format(Messages.XPlannerValidator_CONNECTION_ERROR, message);
			status = new Status(IStatus.ERROR, ID, Status.OK, errorMessage, e);
		}
		else {
			status = new Status(IStatus.ERROR, ID, Status.OK, Messages.XPlannerCorePlugin_UNEXPECTED_ERROR, e);
		}
		
		return status;
	}

}
