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
import java.util.*;

import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;


/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerAttributeFactory extends AbstractAttributeFactory {
	
	private static final long serialVersionUID = -4685044081450189855L;
	
	private static final String TIME_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";// "EEE //$NON-NLS-1$
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

	public static final String ATTRIBUTE_SUBTASK_IDS = "attribute.xplanner.subtask_ids";
	public static final String ATTRIBUTE_SUBTASK_KEYS = "attribute.xplanner.subtask_keys";
	
	private static Map<String, Attribute> commonKeyToAttributesMap = new HashMap<String, Attribute>();

	public enum Attribute {
		DESCRIPTION("Description:", RepositoryTaskAttribute.DESCRIPTION, false), //$NON-NLS-1$
		OWNER("Acceptor:", RepositoryTaskAttribute.USER_ASSIGNED, true), //$NON-NLS-1$
		PRIORITY("Priority:", RepositoryTaskAttribute.PRIORITY, true), //$NON-NLS-1$
		STATUS("Status:", RepositoryTaskAttribute.STATUS, true), //$NON-NLS-1$
		NAME("Name:", RepositoryTaskAttribute.SUMMARY, false), //$NON-NLS-1$
		CREATED_ON("Created:", RepositoryTaskAttribute.DATE_CREATION, true),  //$NON-NLS-1$
		MODIFIED_TIME("Last Update:", RepositoryTaskAttribute.DATE_MODIFIED, true),  //$NON-NLS-1$
		SUBTASK_IDS("Subtask ids:", XPlannerAttributeFactory.ATTRIBUTE_SUBTASK_IDS, true),
		SUBTASK_KEYS("Sub-Tasks:", XPlannerAttributeFactory.ATTRIBUTE_SUBTASK_KEYS, true),

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

	@Override
	public String getName(String key) {
		Attribute attribute = commonKeyToAttributesMap.get(key);
		return (attribute != null) ? attribute.getDisplayName() : key;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		return key;
	}

	@Override
	public boolean isHidden(String key) {
		return false;
	}

	@Override
	public boolean isReadOnly(String key) {				
		Attribute attribute = commonKeyToAttributesMap.get(key);
		return (attribute != null) ? attribute.isReadOnly() : false;
	}
	
	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if(dateString == null || dateString.equals("")) { //$NON-NLS-1$
			return null;
		}
		Date date = null;
		
		try {
			String mappedKey = mapCommonAttributeKey(attributeKey);
			if (mappedKey.equals(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME) ||
					mappedKey.equals(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME)||
					mappedKey.equals(XPlannerAttributeFactory.ATTRIBUTE_REMAINING_HOURS_NAME)||
					mappedKey.equals(RepositoryTaskAttribute.DATE_CREATION) ) {
				
				date = DATE_FORMAT.parse(dateString);
			}
			else if (mappedKey.equals(RepositoryTaskAttribute.DATE_MODIFIED)) {
				date = TIME_DATE_FORMAT.parse(dateString);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
}