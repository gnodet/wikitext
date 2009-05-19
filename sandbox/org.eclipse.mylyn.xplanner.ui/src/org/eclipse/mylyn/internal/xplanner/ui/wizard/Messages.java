/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.xplanner.ui.wizard.messages"; //$NON-NLS-1$

	public static String AbstractXPlannerQueryWizardPage_NEW_XPLANNER_QUERY;

	public static String NewXPlannerQueryWizard_NEW_XPLANNER_QUERY;

	public static String NewXPlannerTaskPage_CHECK_REPOSITORY_SETTINGS;

	public static String NewXPlannerTaskPage_ERROR_UPDATING_ATTRIBUTES;

	public static String NewXPlannerTaskPage_NEW_XPLANNER_TASK;

	public static String NewXPlannerTaskPage_PICK_USER_STORY;

	public static String NewXPlannerTaskPage_PRESS_UPDATE_BUTTON;

	public static String NewXPlannerTaskPage_REPOSITORY_ERROR;

	public static String NewXPlannerTaskPage_UPDATE_FROM_REPOSITORY;

	public static String NewXPlannerTaskPage_UPDATING_REPOSITORY;

	public static String NewXPlannerTaskPage_USER_STORY_NEEDS_TO_BE_SELECTED;

	public static String NewXPlannerTaskWizard_NEW_TASK_TITLE;

	public static String ProjectsViewerLabelProvider_NO_NAME;

	public static String XPlannerCustomQueryPage_ALL_BUTTON;

	public static String XPlannerCustomQueryPage_ALL_MY_CURRENT_TASKS;

	public static String XPlannerCustomQueryPage_GROUPING_TITLE;

	public static String XPlannerCustomQueryPage_MY_BUTTON;

	public static String XPlannerCustomQueryPage_PROJECT_ELEMENT_NEEDED;

	public static String XPlannerCustomQueryPage_PROJECTS_TREE_TITLE;

	public static String XPlannerCustomQueryPage_QUERY_NAME;

	public static String XPlannerCustomQueryPage_QUERY_NAME_NEEDED;

	public static String XPlannerCustomQueryPage_SCOPE_LABEL;

	public static String XPlannerCustomQueryPage_SELECTED_TASKS;

	public static String XPlannerCustomQueryPage_TASKS_BUTTON;

	public static String XPlannerCustomQueryPage_USER_STORIES_BUTTON;

	public static String XPlannerCustomQueryPage_USER_STORY;

	public static String XPlannerQuerySelectionWizardPage_CREATE_QUERY_USING_FORM;

	public static String XPlannerQuerySelectionWizardPage_SELECT_QUERY_TYPE;

	public static String XPlannerQueryWizardUtils_COULD_NOT_CREATE_QUERY_PAGE_MESSAGE;

	public static String XPlannerRepositorySettingsPage_COULD_NOT_CONNECT_TO_XPLANNER;

	public static String XPlannerRepositorySettingsPage_URL_EXAMPLE;

	public static String XPlannerRepositorySettingsPage_VALID_SETTINGS_FOUND;

	public static String XPlannerRepositorySettingsPage_XPLANNER_REPPOSITORY_SETTINGS;

	public static String XPlannerRepositoryUi_TASK;

	public static String XPlannerRepositoryUi_USER_STORY;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
