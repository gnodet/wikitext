/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;


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

	@Override
	public boolean getIsHidden(String key) {
		return false;
	}

	@Override
	public String getName(String key) {
		return key;
	}

	@Override
	public String mapCommonAttributeKey(String key) {
		return key;
	}

	@Override
	public boolean isReadOnly(String key) {				
		return false;
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