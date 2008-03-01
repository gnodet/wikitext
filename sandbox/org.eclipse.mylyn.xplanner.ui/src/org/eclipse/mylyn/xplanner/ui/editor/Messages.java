/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import org.eclipse.osgi.util.NLS;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.xplanner.ui.editor.messages"; //$NON-NLS-1$

	public static String XPlannerTaskEditor_ACCEPTOR_TEXT;

	public static String XPlannerTaskEditor_ACTUAL_HOURS_TEXT;

	public static String XPlannerTaskEditor_ATTRIBUTES_TITLE;

	public static String XPlannerTaskEditor_COMPLETED_BUTTON;

	public static String XPlannerTaskEditor_DATA_SECTION_TITLE;

	public static String XPlannerTaskEditor_DESCRIPTION_SECTION_TITLE;

	public static String XPlannerTaskEditor_ESTIMATED_HOURS_TEXT;

	public static String XPlannerTaskEditor_FORM_TASK_TITLE;

	public static String XPlannerTaskEditor_HelpSearchExpression;

	public static String XPlannerTaskEditor_HIERARCHY_SECTION_TITLE;

	public static String XPlannerTaskEditor_NO_ITERATION_NAME;

	public static String XPlannerTaskEditor_NO_PROJECT_NAME;

	public static String XPlannerTaskEditor_NO_STORY_NAME;

	public static String XPlannerTaskEditor_NO_TASK_KEY_EXCEPTION;

	public static String XPlannerTaskEditor_REMAINING_HOURS_TEXT;

	public static String XPlannerTaskEditorFactory_COULD_NOT_CREATE_EDITOR_INPUT;

	public static String XPlannerTaskEditorFactory_ENSURE_PRPOPER_REPOSITORY_CONFIGURATION;

	public static String XPlannerTaskEditorFactory_REPOSITORY_AND_USERNAME;

	public static String XPlannerTaskEditorFactory_TASK_DOWNLOAD_FAILED;

	public static String XPlannerTaskEditorFactory_TITLE;

	public static String XPlannerUserStoryEditor_DISPOSITION_LABEL;

	public static String XPlannerUserStoryEditor_LAST_UPDATE_LABEL;

	public static String XPlannerUserStoryEditor_NO_TRACKER_NAME;

	public static String XPlannerUserStoryEditor_PRIORITY_LABEL;

	public static String XPlannerUserStoryEditor_REMAINING_HOURS_LABEL;

	public static String XPlannerUserStoryEditor_STATUS_LABEL;

	public static String XPlannerUserStoryEditor_STATUS_PLACEHOLDER;

	public static String XPlannerUserStoryEditor_STORY_TITLE;

	public static String XPlannerUserStoryEditor_TRACKER_LABEL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
