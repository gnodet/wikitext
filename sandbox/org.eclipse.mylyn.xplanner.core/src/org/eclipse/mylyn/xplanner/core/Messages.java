/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.xplanner.core.messages"; //$NON-NLS-1$

	public static String XPlannerCorePlugin_UNEXPECTED_ERROR;

	public static String XPlannerValidator_INVALID_CREDENTIALS_ERROR;

	public static String XPlannerValidator_CONNECTION_ERROR;

	public static String ClientManager_CLIENT_URL_CANNOT_BE_EMPTY;
	
	public static String ClientManager_INVALID_USERNAME_PASSWORD;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
