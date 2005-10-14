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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.SelectionMonitor;
import org.eclipse.mylar.monitor.reports.IUsageStatsCollector;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;

/**
 * @author Mik Kersten
 * @author Leah Findlater
 */
public class ViewUsageCollector implements IUsageStatsCollector {

    private Map<Integer, Map<String, Integer>> usersNormalViewSelections = new HashMap<Integer, Map<String, Integer>>();
    private Map<Integer, Map<String, Integer>> usersFilteredViewSelections = new HashMap<Integer, Map<String, Integer>>();
    private Map<Integer, Set<String>> usersFilteredViews = new HashMap<Integer, Set<String>>();
    
	private Set<Integer> mylarUserIds = new HashSet<Integer>();
    
    private Map<Integer, Integer> usersNumEvents = new HashMap<Integer, Integer>();
    
    private Map<Integer, Integer> usersNumDecayed = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumDefault = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumNew = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumPredicted = new HashMap<Integer, Integer>();

    public void consumeEvent(InteractionEvent event, int userId, String phase) {
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskActivateAction.ID)) {
				mylarUserIds.add(userId);
			} else if (event.getOriginId().equals(TaskDeactivateAction.ID)) {
				mylarUserIds.remove(userId);
			}
		}
    	
		Map<String, Integer> normalViewSelections = usersNormalViewSelections.get(userId);
		if (normalViewSelections == null) {
			normalViewSelections = new HashMap<String, Integer>();
			usersNormalViewSelections.put(userId, normalViewSelections);
		}
		
		Map<String, Integer> filteredViewSelections = usersFilteredViewSelections.get(userId);
		if (filteredViewSelections == null) {
			filteredViewSelections = new HashMap<String, Integer>();
			usersFilteredViewSelections.put(userId, filteredViewSelections);		
		}
		
		Set<String> filteredViews = usersFilteredViews.get(userId);
		if (filteredViews == null) {
			filteredViews = new HashSet<String>();
			usersFilteredViews.put(userId, filteredViews);
		}
		
		if (event.getKind().equals(InteractionEvent.Kind.SELECTION)) {
			if (!usersNumEvents.containsKey(userId)) usersNumEvents.put(userId, 0);
			int numEvents = usersNumEvents.get(userId) + 1;
			usersNumEvents.put(userId, numEvents);

			if (mylarUserIds.contains(userId)) {
				if (event.getDelta().equals(SelectionMonitor.SELECTION_DECAYED)) {
					if (!usersNumDecayed.containsKey(userId)) usersNumDecayed.put(userId, 0);
					int numDecayed = usersNumDecayed.get(userId) + 1;
					usersNumDecayed.put(userId, numDecayed);
				} else if (event.getDelta().equals(SelectionMonitor.SELECTION_PREDICTED)) {
					if (!usersNumPredicted.containsKey(userId)) usersNumPredicted.put(userId, 0);
					int numPredicted = usersNumPredicted.get(userId) + 1;
					usersNumPredicted.put(userId, numPredicted);
				} else if (event.getDelta().equals(SelectionMonitor.SELECTION_NEW)) {
					if (!usersNumNew.containsKey(userId)) usersNumNew.put(userId, 0);
					int numNew = usersNumNew.get(userId) + 1;
					usersNumNew.put(userId, numNew);
				} else if (event.getDelta().equals(SelectionMonitor.SELECTION_DEFAULT)) {
					if (!usersNumDefault.containsKey(userId)) usersNumDefault.put(userId, 0);
					int numDefault = usersNumDefault.get(userId) + 1;
					usersNumDefault.put(userId, numDefault);
				} 
			}
			
			String viewId = event.getOriginId();	    	
    		if (!normalViewSelections.containsKey(viewId)) normalViewSelections.put(viewId, 0);
    		int normal = normalViewSelections.get(viewId) + 1;
    		normalViewSelections.put(viewId, normal);
    		
	    	if (filteredViews.contains(viewId)) {
	        	if (!filteredViewSelections.containsKey(viewId)) filteredViewSelections.put(viewId, 0);
	    		int filtered = filteredViewSelections.get(viewId) + 1;
	    		filteredViewSelections.put(viewId, filtered);
	    	} 

		} else if (event.getKind().equals(InteractionEvent.Kind.PREFERENCE)) {
        	if (event.getOriginId().startsWith(AbstractApplyMylarAction.PREF_ID_PREFIX)) {
        		String viewId = event.getOriginId().substring(AbstractApplyMylarAction.PREF_ID_PREFIX.length());
        		if (event.getDelta().equals("true")) {
        			filteredViews.add(viewId);
        		} else {
        			filteredViews.remove(viewId);
        		}
        	}
		}
	}

	public List<String> getSummary(int userId) {
		Map<String, Integer> normalViewSelections = usersNormalViewSelections.get(userId);
		Map<String, Integer> filteredViewSelections = usersFilteredViewSelections.get(userId);
	
		float numEvents = (float)usersNumEvents.get(userId);
		
		List<String> summaries = new ArrayList<String>();
		summaries.add("Selections");
		summaries.add(" ");
		List<String> viewUsage = new ArrayList<String>();
		for (String view : normalViewSelections.keySet()) {
			float viewUse = ((float)(normalViewSelections.get(view)))/numEvents;
			String formattedViewUse = formatAsPercentage(viewUse);
			
			viewUsage.add(formattedViewUse + ": " + view + " (" + normalViewSelections.get(view) + ")");				
		}
		Collections.sort(viewUsage, new PercentUsageComparator());
		for (String viewUsageSummary : viewUsage) summaries.add(viewUsageSummary);
		
		if (!filteredViewSelections.keySet().isEmpty()) {
			summaries.add("------------ Filtering -------------");
			summaries.add("------------ Filtering -------------");
		}
		
		// TODO: pull this out into a mylar-specific thing
		for (String view : filteredViewSelections.keySet()) {
			int normalSelections = normalViewSelections.get(view);
			int filteredSelections = filteredViewSelections.get(view);
			int unfilteredSelections = normalSelections - filteredSelections; 
			summaries.add("FILTERED " + view + ": " + filteredSelections);
			summaries.add("UNFILTERED " + view + ": " + unfilteredSelections);
		}

		summaries.add("------------ Interest -------------");
		summaries.add("------------ Interest -------------");
		
		if (usersNumNew.containsKey(userId)) {
			summaries.add("New: " + formatAsPercentage(usersNumNew.get(userId)/numEvents) + " (" + usersNumNew.get(userId) + ")");
		} else {
			summaries.add("New: n/a");
		}
		if (usersNumNew.containsKey(userId)) {
			summaries.add("Predicted: " + formatAsPercentage(usersNumPredicted.get(userId)/numEvents) + " (" + usersNumPredicted.get(userId) + ")");
		} else {
			summaries.add("Predicted: n/a");
		}
		if (usersNumNew.containsKey(userId)) {
			summaries.add("Interesting: " + formatAsPercentage(usersNumDefault.get(userId)/numEvents) + " (" + usersNumDefault.get(userId) + ")");
		} else {
			summaries.add("Interesting: n/a");
		}
		if (usersNumNew.containsKey(userId)) {
			summaries.add("Decayed: " + formatAsPercentage(usersNumDecayed.get(userId)/numEvents) + " (" + usersNumDecayed.get(userId) + ")");
		} else {
			summaries.add("Decayed: n/a");
		}
		return summaries;		
	}

	private String formatAsPercentage(float viewUse) {
		String formattedViewUse = (""+viewUse*100);
		int indexOf2ndDecimal = formattedViewUse.indexOf('.')+3;
		if (indexOf2ndDecimal <= formattedViewUse.length()) { 
			formattedViewUse = formattedViewUse.substring(0, indexOf2ndDecimal);
		}
		return formattedViewUse + "%";
	}
    
	public List<String> getReport() {
		List<String> summaries = new ArrayList<String>();
		for (int userId : usersNormalViewSelections.keySet()) {
			summaries.addAll(getSummary(userId));
		}
		return summaries;
	}
	
	public String getReportTitle() {
		return "View Usage";
	}

	public void generateCsvFile(File file) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * For testing.
	 */
	public Map<String, Integer> getFilteredViewSelections() {
		Map<String, Integer> filteredViewSelections = new HashMap<String, Integer>();
		for (int userId : usersFilteredViewSelections.keySet()) {
			filteredViewSelections.putAll(usersFilteredViewSelections.get(userId));
		}
		return filteredViewSelections;
	}

	/**
	 * For testing.
	 */
	public Map<String, Integer> getNormalViewSelections() {
		Map<String, Integer> normalViewSelections = new HashMap<String, Integer>();
		for (int userId : usersNormalViewSelections.keySet()) {
			normalViewSelections.putAll(usersNormalViewSelections.get(userId));
		}
		return normalViewSelections;
	}
}