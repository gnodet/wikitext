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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.monitor.reports.IStatsCollector;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;

/**
 * Delagates to other collectors for additional info.
 * 
 * @author Mik Kersten
 */
public class MylarUserAnalysisCollector implements IStatsCollector {

	public static final int JAVA_SELECTIONS_THRESHOLD = 3000;
	private static final int MYLAR_SELECTIONS_THRESHOLD = 3000;

	private static final int TASK_ACTIVATIONS_THRESHOLD = 1;
	private static final String SUMMARY_SEPARATOR = "----------------------";
	int acceptedUsers = 0;
	int rejectedUsers = 0;
	float summaryDelta = 0;
	float mylarInactiveDelta = 0;

	final List<Integer> usersImproved = new ArrayList<Integer>();
	final List<Integer> usersDegraded = new ArrayList<Integer>();
		
	private Set<Integer> userIds = new HashSet<Integer>();
	private Set<Integer> mylarInactiveUserIds = new HashSet<Integer>();
	private Set<Integer> mylarUserIds = new HashSet<Integer>();
	
	private Map<Integer, Integer> numJavaEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Date> startDates = new HashMap<Integer, Date>();
	private Map<Integer, Date> endDates = new HashMap<Integer, Date>();
	
	private Map<Integer, Integer> baselineSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> baselineEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarInactiveSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarInactiveEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarEdits = new HashMap<Integer, Integer>();
	
	private CommandUsageCollector commandUsageCollector = new CommandUsageCollector();
	private ViewUsageCollector viewUsageCollector = new ViewUsageCollector();
	
	public String getLabel() {
		return "Edit Ratio Change";
	}
	
	public void consumeEvent(InteractionEvent event, int userId, String phase) {
		commandUsageCollector.consumeEvent(event, userId, phase);
		viewUsageCollector.consumeEvent(event, userId, phase);
		if (!startDates.containsKey(userId)) startDates.put(userId, event.getDate());
		endDates.put(userId, event.getDate());
		userIds.add(userId);
		
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskActivateAction.ID)) {
				mylarUserIds.add(userId);
				mylarInactiveUserIds.remove(userId);
			} else if (event.getOriginId().equals(TaskDeactivateAction.ID)) {
//				mylarUserIds.remove(userId);
				mylarInactiveUserIds.add(userId);
			}
		}
		 
		if (event.getKind().equals(InteractionEvent.Kind.EDIT)
				&& (event.getOriginId().indexOf("java") != -1
					|| event.getOriginId().indexOf("jdt.ui") != -1)) {
			incrementCount(userId, numJavaEdits);
		}
		
		if (mylarUserIds.contains(userId) && !mylarInactiveUserIds.contains(userId)) {
			if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
	    		incrementCount(userId, mylarSelections);
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, mylarEdits);
	        }
		} else if (mylarUserIds.contains(userId) && mylarInactiveUserIds.contains(userId)) {
			if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
	    		incrementCount(userId, mylarInactiveSelections);
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, mylarInactiveEdits);
	        }
		} else { // baseline
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
	
	public List<String> getReport() {
		List<String> report = new ArrayList<String>();
		for (Iterator it = userIds.iterator(); it.hasNext(); ) {
    		int id = (Integer)it.next();
    		int numTaskActivations = commandUsageCollector.getCommands().getUserCount(id, TaskActivateAction.ID);
			int numIncrements = commandUsageCollector.getCommands().getUserCount(id, "org.eclipse.mylar.ui.actions.InterestIncrementAction");
			int numDecrements = commandUsageCollector.getCommands().getUserCount(id, "org.eclipse.mylar.ui.actions.InterestDecrementAction");
			if (acceptUser(id) && numTaskActivations > TASK_ACTIVATIONS_THRESHOLD) {
								
				report.add("================================");
				report.add("================================");
				report.add("USER ID: " + id);
				report.add("  ");
				acceptedUsers++;
				float baselineRatio = getBaselineRatio(id);
				float mylarInactiveRatio = getMylarInactiveRatio(id);
				float mylarRatio = getMylarRatio(id);
				
				if (baselineRatio > 0 && mylarRatio > 0) {
					float percentage = mylarRatio / baselineRatio;
					summaryDelta += percentage;
					report.add("Baseline vs. Mylar edit ratio: " + baselineRatio + ", mylar: " + mylarRatio);
					String ratioChange = formatPercentage(100*(percentage-1));
					if (percentage >= 1) {
						usersImproved.add(id);
						report.add("Improved by: " + ratioChange + "%"); 
					} else {
						usersDegraded.add(id);
						report.add("Degraded by: " + ratioChange + "%"); 
					}
	
					float inactivePercentage = mylarRatio / mylarInactiveRatio;
					String inactiveRatioChange = formatPercentage(100*(inactivePercentage-1));
					mylarInactiveDelta += inactivePercentage;
					report.add("Inactive vs. Active edit ratio: " + mylarInactiveRatio + ", mylar: " + mylarRatio);
					if (inactivePercentage >= 1) {
						report.add("Improved by: " + inactiveRatioChange + "%"); 
					} else {
						report.add("Degraded by: " + inactiveRatioChange + "%"); 
					}
					report.add("Selections baseline: " + getNumBaselineSelections(id));
					report.add("Selections mylar: " + getNumMylarSelections(id));
					report.add("Start date: " + getStartDate(id));
					report.add("End date: " + getEndDate(id));
				}
				report.add(SUMMARY_SEPARATOR);
				report.add(SUMMARY_SEPARATOR);
				report.add("Task activations: ");
				report.add("" + numTaskActivations);
				report.add("Interest increments: ");
				report.add("" + numIncrements);
				report.add("Interest decrements: ");
				report.add("" + numDecrements);
				report.add(SUMMARY_SEPARATOR);
				report.add(SUMMARY_SEPARATOR);
				report.addAll(viewUsageCollector.getSummary(id));
				report.add("  ");
				report.add("  ");
				report.add("  ");
				report.add("  ");
			} else {
				rejectedUsers++;
			}
		}
		report.add("");
		report.add("");
		report.add("========================");
		report.add("========================");
		String acceptedSummary = " (based on " + acceptedUsers + " accepted, " + rejectedUsers + " rejected users)";
		float percentage = summaryDelta/(float)acceptedUsers;
		String ratioChange = formatPercentage(100*(percentage-1));
		if (percentage >= 1) {
			report.add("Overall edit ratio improved by: " + ratioChange + "% " + acceptedSummary);
		} else {
			report.add("Overall edit ratio degraded by: " + ratioChange + "% " + acceptedSummary);
		}
		report.add("degraded: " + usersDegraded.size() + ", improved: " + usersImproved.size());
		report.add("========================");
		report.add("========================");
		report.add("");
		report.add("");
		return report;
	}
	
//	public List<String> getSummary() {
//		List<String> summaries = new ArrayList<String>();
//		int acceptedUsers = 0;
//		int rejectedUsers = 0;
//		float summaryDelta = 0;
//		for (Integer id : userIds) {
//			float baselineRatio = getBaselineRatio(id);
//			float mylarRatio = getMylarRatio(id);
//			
//			if (acceptUser(id) && baselineRatio > 0 && mylarRatio > 0) {
//				acceptedUsers++;
//				float percentage = mylarRatio / baselineRatio;
//				summaryDelta += percentage;
//
//				summaries.add("User (" + id + ") baseline: " + baselineRatio + ", mylar: " + mylarRatio);
//				String ratioChange = formatPercentage(percentage-1);
//				if (percentage >= 1) {
//					summaries.add("Improved by: " + ratioChange + "%"); 
//				} else {
//					summaries.add("Degraded by: " + ratioChange + "%"); 
//				}
//			} else {
//				rejectedUsers++;
//			}
//		}
//		summaries.add("Total change: " + formatPercentage(summaryDelta/(float)acceptedUsers)
//			+ " (based on: " + acceptedUsers + " accepted, " + rejectedUsers + " rejected)");
//		return summaries; 
//	}

	public boolean acceptUser(int id) {
		if (!numJavaEdits.containsKey(id)) {
			return false;
		} else {
			return getNumBaselineSelections(id) + getNumMylarInactiveSelections(id) > JAVA_SELECTIONS_THRESHOLD
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
		if (baselineSelections.containsKey(id)) {
			return baselineSelections.get(id);
		} else {
			return 0;
		}
	}
	
	public int getNumMylarInactiveSelections(int id) {
		if (mylarInactiveSelections.containsKey(id)) {
			return mylarInactiveSelections.get(id);
		} else {
			return 0;
		}
	}
	
	public int getNumMylarSelections(int id) {
		if (mylarSelections.containsKey(id)) {
			return mylarSelections.get(id);
		} else {
			return 0;
		}
	}
	
	/**
	 * Public for testing.
	 */	
	public float getBaselineRatio(int id) {
		return getEditRatio(id, baselineEdits, baselineSelections);
	}

	public float getMylarInactiveRatio(int id) {
		return getEditRatio(id, mylarInactiveEdits, mylarInactiveSelections);
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
