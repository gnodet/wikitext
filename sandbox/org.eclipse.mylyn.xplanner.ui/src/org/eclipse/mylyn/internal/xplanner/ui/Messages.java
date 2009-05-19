/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.xplanner.ui.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String XPlannerPlugin_CLIENT_DIALOG_TITLE;

	public static String XPlannerPlugin_CLIENT_LABEL;

	public static String XPlannerPlugin_XPLANNER_ERROR_TITLE;

	public static String XPlannerOfflineTaskHandler_CANNOT_POST_DATA_TO_SERVER;

	public static String XPlannerRepositoryConnector_COULD_NOT_CONVERT_TASK_DATE;

	public static String XPlannerRepositoryConnector_GETTING_CHANGED_TASKS;

	public static String XPlannerRepositoryConnector_NEW_TASK_DESCRIPTION;

	public static String XPlannerRepositoryConnector_PerformQueryFailure;

	public static String XPlannerRepositoryConnector_RUNNING_QUERY;

	public static String XPlannerRepositoryUtils_NO_ITERATION_NAME;

	public static String XPlannerRepositoryUtils_NO_PERSON_NAME;

	public static String XPlannerRepositoryUtils_NO_PROJECT_NAME;

	public static String XPlannerRepositoryUtils_NO_TASK_NAME;

	public static String XPlannerRepositoryUtils_NO_USER_STORY_NAME;

	public static String XPlannerRepositoryUtils_TASK_DOWNLOAD_FAILED;

	public static String XPlannerClientFacade_AUTHENTICATION_FAILED;

	public static String XPlannerClientFacade_CHECK_CREDENTIALS;

	public static String XPlannerClientFacade_CONNECTION_FAILURE_ERROR;

	public static String XPlannerClientFacade_COULD_NOT_CONNECT_TO_REPOSITORY;

	public static String XPlannerClientFacade_INVALID_URL_EXCEPTION;

	public static String XPlannerClientFacade_NETWORK_CONNECTION_FAILURE;

	public static String XPlannerClientFacade_NO_REPOSITORY_FOUND;

	public static String XPlannerClientFacade_SERVER_CONNECTION_ERROR;

	public static String XPlannerClientFacade_USERNAME_PASSWORD_ERROR;

	public static String XPlannerClientFacade_VERIFY_VALID_REPOSITORY;

}
