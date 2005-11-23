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

package org.eclipse.mylar.monitor.reports.collectors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.mylar.monitor.reports.AbstractMylarUsageCollector;
import org.eclipse.mylar.monitor.reports.ReportGenerator;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;

/**
 * Delagates to other collectors for additional info.
 * 
 * @author Mik Kersten
 */
public class MylarUserAnalysisCollector extends AbstractMylarUsageCollector {

	private static final int THRESHOLD_SELECTION_RUNS = 5;
	public static final int JAVA_EDITS_THRESHOLD = 3000;
	public static final int BASELINE_SELECTIONS_THRESHOLD = 3000;
	private static final int MYLAR_SELECTIONS_THRESHOLD = 3000;
//	private static final int TASK_DEACTIVATIONS_THRESHOLD = 5;
	private static final int NUM_VIEWS_REPORTED = 5;

	int acceptedUsers = 0;
	int rejectedUsers = 0;
	float summaryEditRatioDelta = 0;
	float mylarInactiveDelta = 0;

	final List<Integer> usersImproved = new ArrayList<Integer>();
	final List<Integer> usersDegraded = new ArrayList<Integer>();
		
	private Map<Integer, Date> startDates = new HashMap<Integer, Date>();
	private Map<Integer, Integer> numJavaEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Date> endDates = new HashMap<Integer, Date>();
	
	private Map<Integer, Integer> baselineSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> baselineEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarInactiveSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarInactiveEdits = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarSelections = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarEdits = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> baselineCurrentNumSelectionsBeforeEdit = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> baselineTotalSelectionsBeforeEdit = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> baselineTotalEditsCounted = new HashMap<Integer, Integer>();
		
	private Map<Integer, Integer> mylarCurrentNumSelectionsBeforeEdit = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarTotalSelectionsBeforeEdit = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> mylarTotalEditsCounted = new HashMap<Integer, Integer>();
		
	private ViewUsageCollector viewUsageCollector = new ViewUsageCollector();
	
	public MylarUserAnalysisCollector() {
		viewUsageCollector.setMaxViewsToReport(NUM_VIEWS_REPORTED);
		super.getDelegates().add(viewUsageCollector);
	}
	
	public String getReportTitle() {
		return "Mylar Usage";
	}
	
	public void consumeEvent(InteractionEvent event, int userId) {
		super.consumeEvent(event, userId);
		if (!startDates.containsKey(userId)) startDates.put(userId, event.getDate());
		endDates.put(userId, event.getDate());
		 
		if (event.getKind().equals(InteractionEvent.Kind.EDIT)
				&& (event.getOriginId().indexOf("java") != -1
					|| event.getOriginId().indexOf("jdt.ui") != -1)) {
			incrementCount(userId, numJavaEdits);
		}
		
		if (mylarUserIds.contains(userId) && !mylarInactiveUserIds.contains(userId)) {
			if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
	    		incrementCount(userId, mylarSelections);
	    		incrementCount(userId, mylarCurrentNumSelectionsBeforeEdit);  		
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, mylarEdits);
	        	
	        	if (mylarCurrentNumSelectionsBeforeEdit.containsKey((userId))) {
	        		int num = mylarCurrentNumSelectionsBeforeEdit.get(userId);
	        		if (num > THRESHOLD_SELECTION_RUNS) {
	        			incrementCount(userId, mylarTotalEditsCounted);
		        		incrementCount(userId, mylarTotalSelectionsBeforeEdit, num);
		        		mylarCurrentNumSelectionsBeforeEdit.put(userId, 0);
	        		}
	        	}
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
	    		
	    		incrementCount(userId, baselineCurrentNumSelectionsBeforeEdit);
	        } else if (event.getKind().equals(InteractionEvent.Kind.EDIT)) {
	        	incrementCount(userId, baselineEdits);
	        	
	        	if (baselineCurrentNumSelectionsBeforeEdit.containsKey((userId))) {
	        		int num = baselineCurrentNumSelectionsBeforeEdit.get(userId);
	        		if (num >= THRESHOLD_SELECTION_RUNS) {  
	        			incrementCount(userId, baselineTotalEditsCounted);
	        			incrementCount(userId, baselineTotalSelectionsBeforeEdit, num);
		        		baselineCurrentNumSelectionsBeforeEdit.put(userId, 0);
	        		}
	        	}
	        } 
		}
	}

	private void incrementCount(int userId, Map<Integer, Integer> map, int count) {
		if (!map.containsKey(userId)) map.put(userId, 0);
		map.put(userId, map.get(userId) + count);
	}
	
	private void incrementCount(int userId, Map<Integer, Integer> map) {
		incrementCount(userId, map, 1);
	}
	
	public List<String> getReport() {
		List<String> report = new ArrayList<String>();
		for (Iterator it = userIds.iterator(); it.hasNext(); ) {
    		int id = (Integer)it.next();
    		int numTaskActivations = commandUsageCollector.getCommands().getUserCount(id, TaskActivateAction.ID);
    		int numTaskDeactivations = commandUsageCollector.getCommands().getUserCount(id, TaskDeactivateAction.ID);
			int numIncrements = commandUsageCollector.getCommands().getUserCount(id, "org.eclipse.mylar.ui.actions.InterestIncrementAction");
			int numDecrements = commandUsageCollector.getCommands().getUserCount(id, "org.eclipse.mylar.ui.actions.InterestDecrementAction");
			if (acceptUser(id, numTaskDeactivations)) {
				report.add("<h3>USER ID: " + id + " (from: " + getStartDate(id) + " to " + getEndDate(id) + ")</h3>");
				acceptedUsers++;
				
				if (baselineTotalSelectionsBeforeEdit.containsKey(id)
					&& mylarTotalSelectionsBeforeEdit.containsKey(id)) {
					
					float baselineRuns = 
						(float)baselineTotalSelectionsBeforeEdit.get(id) /
//						getNumBaselineEdits(id);
						(float)baselineTotalEditsCounted.get(id);
					
					float mylarRuns = 
						(float)mylarTotalSelectionsBeforeEdit.get(id) /
//						getNumMylarEdits(id);
						(float)mylarTotalEditsCounted.get(id);  
					
					report.add("Avg baseline selections before edit: " + baselineRuns 
							+ " vs. mylar: " + mylarRuns 
							+ " (run size: " + THRESHOLD_SELECTION_RUNS + ")<br>");
				}
				
				float baselineRatio = getBaselineRatio(id);
//				float mylarInactiveRatio = getMylarInactiveRatio(id);
				float mylarRatio = getMylarRatio(id);				
//				if (baselineRatio > 0 && mylarRatio > 0) {
				float percentage = mylarRatio / baselineRatio;
				summaryEditRatioDelta += percentage;
				String ratio1 = "";
				ratio1 = "Baseline vs. Mylar active edit ratio: " + baselineRatio + ", mylar: " + mylarRatio + ",  ";
				String ratioChange = formatPercentage(100*(percentage-1));
				if (percentage >= 1) {
					usersImproved.add(id);
					ratio1 += "Improved by: " + ratioChange + "%"; 
				} else {
					usersDegraded.add(id);
					ratio1 += "Degraded by: " + ratioChange + "%"; 
				}
				report.add(ratio1 + "<br>");

//					float inactivePercentage = mylarRatio / mylarInactiveRatio;
//					String inactiveRatioChange = formatPercentage(100*(inactivePercentage-1));
//					mylarInactiveDelta += inactivePercentage;
//					String ratio2 = "";
//					ratio2 += "Inactive vs. Active edit ratio: " + mylarInactiveRatio + ", mylar: " + mylarRatio + ",  ";
//					if (inactivePercentage >= 1) {
//						ratio2 += "Improved by: " + inactiveRatioChange + "%"; 
//					} else {
//						ratio2 += "Degraded by: " + inactiveRatioChange + "%"; 
//					}
//					report.add(ratio2 + "<br>");
//				}
				
				report.add("<h4>Activity</h4>");
				report.add("Task activations: " + numTaskActivations + ", ");
				report.add("Task deactivations: " + numTaskDeactivations + "<br>");
				report.add("Interest increments: " + numIncrements
						+ ", Interest decrements: " + numDecrements + "<br>");
				
				report.add("Selections baseline: " + getNumBaselineSelections(id)
						+ ", Selections mylar: " + getNumMylarSelections(id) + "<br>");
				report.add("Edits baseline: " + getNumBaselineEdits(id)
						+ ", Edits mylar: " + getNumMylarEdits(id) + "<br>");
				
				report.add("<h4>View Usage (top " + NUM_VIEWS_REPORTED + ")</h4>");
				report.addAll(viewUsageCollector.getSummary(id));
				report.add(ReportGenerator.SUMMARY_SEPARATOR);
			} else {
				rejectedUsers++;
			}
		}
		report.add("<h3>Summary</h3>");
		String acceptedSummary = " (based on " + acceptedUsers + " accepted, " + rejectedUsers + " rejected users)";
		float percentage = summaryEditRatioDelta/(float)acceptedUsers;
		String ratioChange = formatPercentage(100*(percentage-1));
		if (percentage >= 1) {
			report.add("Overall edit ratio improved by: " + ratioChange + "% " + acceptedSummary + "<br>");
		} else {
			report.add("Overall edit ratio degraded by: " + ratioChange + "% " + acceptedSummary + "<br>");
		}
		report.add("degraded: " + usersDegraded.size() + ", improved: " + usersImproved.size() + "<br>");
		report.add(ReportGenerator.SUMMARY_SEPARATOR);
		return report;
	}

	public boolean acceptUser(int id, int numTaskDeactivations) {
		if (!numJavaEdits.containsKey(id)) {
			return false;
		} else {
			return 
//				numTaskDeactivations > TASK_DEACTIVATIONS_THRESHOLD
			 	numJavaEdits.get(id) > JAVA_EDITS_THRESHOLD
				&& getNumBaselineSelections(id) > BASELINE_SELECTIONS_THRESHOLD
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

	public int getNumBaselineEdits(int id) {
		if (baselineEdits.containsKey(id)) {
			return baselineEdits.get(id);
		} else {
			return 0;
		}
	}
	
	public int getNumMylarEdits(int id) {
		if (mylarEdits.containsKey(id)) {
			return mylarEdits.get(id);
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
