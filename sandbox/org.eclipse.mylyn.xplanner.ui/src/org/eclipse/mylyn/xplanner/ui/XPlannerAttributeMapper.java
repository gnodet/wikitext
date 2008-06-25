/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerAttributeMapper extends TaskAttributeMapper {

	public XPlannerAttributeMapper(TaskRepository repository) {
		super(repository);
	}

	private static final long serialVersionUID = -4685044081450189855L;

	public static final String DEFAULT_REPOSITORY_TASK_KIND = "task";

	private static final String TIME_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";// "EEE //$NON-NLS-1$

	public static final int INVALID_ID = -1;

	public static final String INVALID_ID_STRING = String.valueOf(INVALID_ID);

	public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);

	public static final DateFormat TIME_DATE_FORMAT = new SimpleDateFormat(TIME_DATE_FORMAT_STRING);

	public static final String ATTRIBUTE_EST_HOURS_NAME = "estimatedHours"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ACT_HOURS_NAME = "actualHours"; //$NON-NLS-1$

	public static final String ATTRIBUTE_REMAINING_HOURS_NAME = "remainingHours"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ADJUSTED_ESTIMATED_HOURS_NAME = "adjustedEstimatedHours"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ESTIMATED_ORIGINAL_HOURS_NAME = "estimatedOriginalHours"; //$NON-NLS-1$

	public static final String ATTRIBUTE_PROJECT_NAME = "projectName"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ITERATION_NAME = "iterationName"; //$NON-NLS-1$

	public static final String ATTRIBUTE_USER_STORY_NAME = "userStoryName"; //$NON-NLS-1$

	public static final String ATTRIBUTE_TASK_COMPLETED = "completed"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ASSIGNED_ID = "assignedId"; //$NON-NLS-1$

	public static final String ATTRIBUTE_PROJECT_ID = "projectId"; //$NON-NLS-1$

	public static final String ATTRIBUTE_ITERATION_ID = "iterationId"; //$NON-NLS-1$

	public static final String ATTRIBUTE_USER_STORY_ID = "userStoryId"; //$NON-NLS-1$

	public static final String ATTRIBUTE_SUBTASK_IDS = "attribute.xplanner.subtask_ids";

	public static final String ATTRIBUTE_SUBTASK_KEYS = "attribute.xplanner.subtask_keys";

	private static Map<String, Attribute> commonKeyToAttributesMap = new HashMap<String, Attribute>();

	public enum Attribute {
		DESCRIPTION("Description:", TaskAttribute.DESCRIPTION, false), //$NON-NLS-1$
		OWNER("Acceptor:", TaskAttribute.USER_ASSIGNED, true), //$NON-NLS-1$
		PRIORITY("Priority:", TaskAttribute.PRIORITY, true), //$NON-NLS-1$
		STATUS("Status:", TaskAttribute.STATUS, true), //$NON-NLS-1$
		NAME("Name:", TaskAttribute.SUMMARY, false), //$NON-NLS-1$
		CREATED_ON("Created:", TaskAttribute.DATE_CREATION, true), //$NON-NLS-1$
		MODIFIED_TIME("Last Update:", TaskAttribute.DATE_MODIFICATION, true), //$NON-NLS-1$
		SUBTASK_IDS("Subtask ids:", XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_IDS, true), SUBTASK_KEYS("Sub-Tasks:",
				XPlannerAttributeMapper.ATTRIBUTE_SUBTASK_KEYS, true),

		;

		private final boolean isReadOnly;

		private final String displayName;

		private final String commonAttributeKey;

		Attribute(String displayName, String commonAttributeKey, boolean readonly) {
			this.displayName = displayName;
			this.commonAttributeKey = commonAttributeKey;
			this.isReadOnly = readonly;

			commonKeyToAttributesMap.put(commonAttributeKey, this);
		}

		public String getCommonAttributeKey() {
			return commonAttributeKey;
		}

		public String getDisplayName() {
			return displayName;
		}

		public boolean isReadOnly() {
			return isReadOnly;
		}

		@Override
		public String toString() {
			return getDisplayName();
		}
	}

	static {
		// make sure hash maps get initialized when class is loaded
		Attribute.values();
	}

	public enum XPlannerTaskKind {
		ITERATION, USER_STORY, TASK;

		@Override
		public String toString() {
			switch (this) {
			case ITERATION:
				return "Iteration"; //$NON-NLS-1$
			case USER_STORY:
				return "User Story"; //$NON-NLS-1$
			case TASK:
				return "Task"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}

		public static XPlannerTaskKind fromString(String kindValue) {
			XPlannerTaskKind kind = TASK;

			if (kindValue.equals(TASK.toString())) {
				kind = TASK;
			} else if (kindValue.equals(USER_STORY.toString())) {
				kind = USER_STORY;
			} else if (kindValue.equals(ITERATION.toString())) {
				kind = ITERATION;
			}

			return kind;
		}

	}

	public static void setKind(ITask mylynTask, DomainData data) {
		String tempKind = XPlannerTaskKind.TASK.toString();

		if (data instanceof IterationData) {
			tempKind = XPlannerTaskKind.ITERATION.toString();
		} else if (data instanceof UserStoryData) {
			tempKind = XPlannerTaskKind.USER_STORY.toString();
		}

		mylynTask.setTaskKind(tempKind);
	}

	@Override
	public String mapToRepositoryKey(TaskAttribute parent, String key) {
		Attribute attribute = commonKeyToAttributesMap.get(key);
		if (attribute != null) {
			return attribute.getCommonAttributeKey();
		}

		return super.mapToRepositoryKey(parent, key);
	}

	public static Attribute getAttribute(String commonKey) {
		return commonKeyToAttributesMap.get(commonKey);
	}

	@Override
	public Date getDateValue(TaskAttribute attribute) {
		if (attribute == null) {
			return null;
		}
		String dateString = attribute.getValue();
		Date parsedDate = getDateForAttributeType(attribute, dateString);
		if (parsedDate == null) {
			parsedDate = super.getDateValue(attribute);
		}
		return parsedDate;
	}

	public Date getDateForAttributeType(TaskAttribute attribute, String dateString) {
		String mappedAttributeKey = mapToRepositoryKey(attribute, attribute.getId());
		return getDateForAttributeType(mappedAttributeKey, dateString);
	}

	public static Date getDateForAttributeType(String mappedAttributeKey, String dateString) {
		if (mappedAttributeKey == null || mappedAttributeKey.length() == 0 || dateString == null
				|| dateString.length() == 0) {

			return null;
		}

		Date date = null;

		try {
			if (mappedAttributeKey.equals(XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME)
					|| mappedAttributeKey.equals(XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME)
					|| mappedAttributeKey.equals(XPlannerAttributeMapper.ATTRIBUTE_REMAINING_HOURS_NAME)
					|| mappedAttributeKey.equals(TaskAttribute.DATE_CREATION)) {

				date = DATE_FORMAT.parse(dateString);
			} else if (mappedAttributeKey.equals(TaskAttribute.DATE_MODIFICATION)
					|| mappedAttributeKey.equals(AbstractTaskListMigrator.KEY_LAST_MOD_DATE)) {

				date = TIME_DATE_FORMAT.parse(dateString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return date;
	}
}