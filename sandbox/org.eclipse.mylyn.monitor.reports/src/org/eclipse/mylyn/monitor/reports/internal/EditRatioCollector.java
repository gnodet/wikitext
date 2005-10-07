/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.reports.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.monitor.reports.IStatsCollector;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;

/**
 * @author Mik Kersten
 */
public class EditRatioCollector implements IStatsCollector {

	public static final int JAVA_SELECTIONS_THRESHOLD = 3000;
	private static final int MYLAR_SELECTIONS_THRESHOLD = 3000;
	
	private Set<Integer> userIds = new HashSet<Integer>();
	private Set<Integer> mylarUserIds = new HashSet<Integer>();
	
	private Map<Integer, Integer> numJavaEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Date> startDates = new HashMap<Integer, Date>();
	private Map<Integer, Date> endDates = new HashMap<Integer, Date>();
	
	private Map<Integer, Integer> baselineSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> baselineEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarEdits = new HashMap<Integer, Integer>();
	
	public String getLabel() {
		return "Edit Ratio Change";
	}
	
	public void consumeEvent(InteractionEvent event, int userId, String phase) {
		if (!startDates.containsKey(userId)) startDates.put(userId, event.getDate());
		endDates.put(userId, event.getDate());
		userIds.add(userId);
		
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskActivateAction.ID)) {
				mylarUserIds.add(userId);
			} else if (event.getOriginId().equals(TaskDeactivateAction.ID)) {
				mylarUserIds.remove(userId);
			}
		}
		 
		if (event.getKind().equals(InteractionEvent.Kind.EDIT)
				&& (event.getOriginId().indexOf("java") != -1
					|| event.getOriginId().indexOf("jdt.ui") != -1)) {
			incrementCount(userId, numJavaEdits);
		}
		
		if (mylarUserIds.contains(userId)) {
			if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
	    		incrementCount(userId, mylarSelections);
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, mylarEdits);
	        }
		} else {
	        if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
	    		incrementCount(userId, baselineSelections);
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, baselineEdits);
	        } 
		}
	}

	private void incrementCount(int userId, Map<Integer, Integer> map) {
		if (!map.containsKey(userId)) map.put(userId, 0);
		map.put(userId, map.get(userId) + 1);
	}
	
	public List<String> getSummary() {
		List<String> summaries = new ArrayList<String>();
		int acceptedUsers = 0;
		int rejectedUsers = 0;
		float summaryDelta = 0;
		for (Integer id : userIds) {
			float baselineRatio = getBaselineRatio(id);
			float mylarRatio = getMylarRatio(id);
			
			if (acceptUser(id) && baselineRatio > 0 && mylarRatio > 0) {
				acceptedUsers++;
				float percentage = mylarRatio / baselineRatio;
				summaryDelta += percentage;

				summaries.add("User (" + id + ") baseline: " + baselineRatio + ", mylar: " + mylarRatio);
				String ratioChange = formatPercentage(percentage-1);
				if (percentage >= 1) {
					summaries.add("Improved by: " + ratioChange + "%"); 
				} else {
					summaries.add("Degraded by: " + ratioChange + "%"); 
				}
			} else {
				rejectedUsers++;
			}
		}
		summaries.add("Total change: " + formatPercentage(summaryDelta/(float)acceptedUsers)
			+ " (based on: " + acceptedUsers + " accepted, " + rejectedUsers + " rejected)");
		return summaries; 
	}

	public boolean acceptUser(int id) {
		if (!numJavaEdits.containsKey(id)) {
			return false;
		} else {
			return getNumBaselineSelections(id) > JAVA_SELECTIONS_THRESHOLD
				&& getNumMylarSelections(id) > MYLAR_SELECTIONS_THRESHOLD;
		}
	}

	public String formatPercentage(float percentage) {
		String percentageString = "" + percentage;
		int indexOf2ndDecimal = percentageString.indexOf('.')+3;
		if (indexOf2ndDecimal <= percentageString.length()) { 
			percentageString = percentageString.substring(0, indexOf2ndDecimal);
		}
		return percentageString;
	}
	
	public String getStartDate(int id) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDates.get(id));
		return DateUtil.getFormattedDate(start);
	}
	
	public String getEndDate(int id) {
		Calendar end = Calendar.getInstance();
		end.setTime(endDates.get(id));
		return DateUtil.getFormattedDate(end);
	}
	
	public int getNumBaselineSelections(int id) {
		return baselineSelections.get(id);
	}
	
	public int getNumMylarSelections(int id) {
		return mylarSelections.get(id);
	}
	
	/**
	 * Public for testing.
	 */	
	public float getBaselineRatio(int id) {
		return getEditRatio(id, baselineEdits, baselineSelections);
	}
	
	/**
	 * Public for testing.
	 */	
	public float getMylarRatio(int id) {
		return getEditRatio(id, mylarEdits, mylarSelections);
	}
	
	private float getEditRatio(int id, Map<Integer, Integer> edits, Map<Integer, Integer> selections) {
		if (edits.containsKey(id) && selections.containsKey(id)) {
			return (float)edits.get(id) / (float)selections.get(id); 
		} else {
			return 0f;
		}
	}
}
