/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskListMigrator extends AbstractTaskListMigrator {

	public static final List<Integer> INVALID_IDS = Arrays.asList(new Integer[] { XPlannerAttributeMapper.INVALID_ID });

	private static final String TOKEN_SEPARATOR = ";"; //$NON-NLS-1$

	private static final String KEY_XPLANNER = "XPlanner"; //$NON-NLS-1$

	private static final String KEY_CUSTOM = "XPlannerCustom"; //$NON-NLS-1$

	private static final String KEY_XPLANNER_QUERY = KEY_XPLANNER + KEY_QUERY;

	private static final String KEY_XPLANNER_CUSTOM = KEY_XPLANNER + KEY_CUSTOM + KEY_QUERY;

	private static final String KEY_XPLANNER_ITEM = "XPlannerItem"; //$NON-NLS-1$

	private static final String KEY_QUERY_CONTENT_ID_TYPE = "QueryContentIdType"; //$NON-NLS-1$

	private static final String KEY_QUERY_IS_TASKS = "QueryIsTasks"; //$NON-NLS-1$

	public static final String KEY_QUERY_CONTENT_IDS = "QueryConentId"; //$NON-NLS-1$

	private static final String KEY_QUERY_PERSON_ID = "QueryPersonId"; //$NON-NLS-1$

	private static final String KEY_QUERY_MY_CURRENT_TASKS = "QueryMyCurrentTasks"; //$NON-NLS-1$

	public static final String KEY_TASK_UPDATE = "LastUpdate"; //$NON-NLS-1$

	public static enum ContentIdType {
		PROJECT, ITERATION, USER_STORY
	};

	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(KEY_XPLANNER_QUERY) || node.getNodeName().equals(KEY_XPLANNER_CUSTOM);
	}

	@Override
	public String getConnectorKind() {
		return XPlannerCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(KEY_XPLANNER_CUSTOM);
		return names;
	}

	@Override
	public String getTaskElementName() {
		return KEY_XPLANNER_ITEM;
	}

	public static boolean isUseTasks(IRepositoryQuery query) {
		String isTasksValue = query.getAttribute(KEY_QUERY_IS_TASKS);
		return isTasksValue == null || isTasksValue.length() == 0 ? false : Boolean.valueOf(isTasksValue);
	}

	public static void setUseTasks(IRepositoryQuery query, boolean isUseTasks) {
		setUseTasks(query, Boolean.toString(isUseTasks));
	}

	public static void setUseTasks(IRepositoryQuery query, String isUseTasksValue) {
		query.setAttribute(KEY_QUERY_IS_TASKS,
				isUseTasksValue == null || isUseTasksValue.length() == 0 ? Boolean.toString(true) : isUseTasksValue);
	}

	public static int getPersonId(IRepositoryQuery query) {
		String personIdValue = query.getAttribute(KEY_QUERY_PERSON_ID);
		return personIdValue == null || personIdValue.length() == 0 ? XPlannerAttributeMapper.INVALID_ID
				: Integer.valueOf(personIdValue);
	}

	public static void setPersonId(IRepositoryQuery query, int personId) {
		setPersonId(query, Integer.toString(personId));
	}

	public static void setPersonId(IRepositoryQuery query, String personIdValue) {
		query.setAttribute(KEY_QUERY_PERSON_ID,
				personIdValue == null || personIdValue.length() == 0 ? XPlannerAttributeMapper.INVALID_ID_STRING
						: personIdValue);
	}

	public static boolean isMyCurrentTasks(IRepositoryQuery query) {
		String isMyCurrentTasksValue = query.getAttribute(KEY_QUERY_MY_CURRENT_TASKS);
		return isMyCurrentTasksValue == null || isMyCurrentTasksValue.length() == 0 ? false
				: Boolean.valueOf(isMyCurrentTasksValue);
	}

	public static void setMyCurrentTasks(IRepositoryQuery query, boolean isMyCurrentTasks) {
		query.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(isMyCurrentTasks));
	}

	@Override
	public void migrateQuery(IRepositoryQuery query, Element element) {
		boolean isMyCurrentTasks = Boolean.getBoolean(element.getAttribute(KEY_QUERY_MY_CURRENT_TASKS));
		setMyCurrentTasks(query, isMyCurrentTasks);

		if (!isMyCurrentTasks) {
			// don't show only my current tasks (show combination of other query settings)
			query.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(false));

			// selected content type
			setContentIdType(query, element.getAttribute(KEY_QUERY_CONTENT_ID_TYPE));

			// is tasks
			setUseTasks(query, element.getAttribute(KEY_QUERY_IS_TASKS));

			// content ids
			setContentIds(query, element.getAttribute(KEY_QUERY_CONTENT_IDS));

			// person id
			setPersonId(query, element.getAttribute(KEY_QUERY_PERSON_ID));
		}
	}

	@Override
	public void migrateTask(ITask task, Element element) {
		String taskKind = element.getAttribute(KEY_TASK);
		boolean setDefaultKind = taskKind == null || taskKind.length() == 0
				|| taskKind.equals(TaskAttribute.KIND_DEFAULT);
		task.setTaskKind(setDefaultKind ? XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString() : taskKind);
		String lastModDate = element.getAttribute(KEY_LAST_MOD_DATE);
		task.setAttribute(KEY_TASK_UPDATE, lastModDate);
		try {
			Date lastUpdated = XPlannerAttributeMapper.TIME_DATE_FORMAT.parse(lastModDate);
			task.setModificationDate(lastUpdated);
		} catch (ParseException e) {
			// ignore
		}
	}

	// Query related attributes
	public static List<Integer> getContentIds(IRepositoryQuery query) {
		return decodeQueryContentIds(query.getAttribute(KEY_QUERY_CONTENT_IDS));
	}

	public static void setContentIds(IRepositoryQuery query, List<Integer> contentIds) {
		setContentIds(query, encodeQueryContentIds(contentIds));
	}

	public static void setContentIds(IRepositoryQuery query, String contentIdsValue) {
		query.setAttribute(KEY_QUERY_CONTENT_IDS, contentIdsValue);
	}

	public static ContentIdType getContentIdType(IRepositoryQuery query) {
		String contentIdTypeValue = query.getAttribute(KEY_QUERY_CONTENT_ID_TYPE);
		return getContentIdType(contentIdTypeValue);
	}

	public static void setContentIdType(IRepositoryQuery query, ContentIdType contentIdType) {
		setContentIdType(query, contentIdType.name());
	}

	public static void setContentIdType(IRepositoryQuery query, String contentIdTypeValue) {
		query.setAttribute(KEY_QUERY_CONTENT_ID_TYPE,
				contentIdTypeValue == null || contentIdTypeValue.length() == 0 ? ContentIdType.USER_STORY.name()
						: contentIdTypeValue);
	}

	public static ContentIdType getContentIdType(String contentIdTypeValue) {
		return contentIdTypeValue == null || contentIdTypeValue.length() == 0 ? ContentIdType.USER_STORY
				: ContentIdType.valueOf(contentIdTypeValue);
	}

	public static String encodeQueryContentIds(List<Integer> contentIds) {
		StringBuilder result = new StringBuilder();
		for (Iterator<Integer> iter = contentIds.iterator(); iter.hasNext();) {
			Integer id = iter.next();
			result.append(id);
			if (iter.hasNext()) {
				result.append(TOKEN_SEPARATOR);
			}
		}

		return result.toString();
	}

	public static List<Integer> decodeQueryContentIds(String encoded) {
		if (encoded == null || encoded.length() == 0) {
			return INVALID_IDS;
		}

		StringTokenizer tokens = new StringTokenizer(encoded, TOKEN_SEPARATOR);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		while (tokens.hasMoreTokens()) {
			ids.add(Integer.valueOf(tokens.nextToken()));
		}

		return ids;
	}
}