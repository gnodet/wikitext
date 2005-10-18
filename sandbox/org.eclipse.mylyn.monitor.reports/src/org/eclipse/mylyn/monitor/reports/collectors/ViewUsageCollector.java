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
import org.eclipse.mylar.monitor.reports.IUsageCollector;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;
import org.eclipse.mylar.ui.actions.AbstractApplyMylarAction;

/**
 * @author Mik Kersten
 * @author Leah Findlater
 */
public class ViewUsageCollector implements IUsageCollector {

	private Map<Integer, Map<String, Integer>> usersNormalViewSelections = new HashMap<Integer, Map<String, Integer>>();
    private Map<Integer, Map<String, Integer>> usersFilteredViewSelections = new HashMap<Integer, Map<String, Integer>>();
    private Map<Integer, Set<String>> usersFilteredViews = new HashMap<Integer, Set<String>>();
    
	private Set<Integer> mylarUserIds = new HashSet<Integer>();
    
    private Map<Integer, Integer> usersNumSelections = new HashMap<Integer, Integer>();
    
    private Map<Integer, Integer> usersNumDecayed = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumDefault = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumNew = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumPredicted = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> usersNumUnknown = new HashMap<Integer, Integer>();

    private int maxViewsToReport = -1;
    
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
			if (!usersNumSelections.containsKey(userId)) usersNumSelections.put(userId, 0);
			int numEvents = usersNumSelections.get(userId) + 1;
			usersNumSelections.put(userId, numEvents);

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
				} else {
					if (!usersNumUnknown.containsKey(userId)) usersNumUnknown.put(userId, 0);
					int numUnknownNew = usersNumUnknown.get(userId) + 1;
					usersNumUnknown.put(userId, numUnknownNew);			
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
	
		float numSelections = (float)usersNumSelections.get(userId);
		
		List<String> summaries = new ArrayList<String>();
		List<String> viewUsage = new ArrayList<String>();
		for (String view : normalViewSelections.keySet()) {
			float viewUse = ((float)(normalViewSelections.get(view)))/numSelections;
			String formattedViewUse = formatAsPercentage(viewUse);
			viewUsage.add(formattedViewUse + ": " + view + " (" + normalViewSelections.get(view) + ")" + "<br>");	
		}
		Collections.sort(viewUsage, new PercentUsageComparator());
		int numViewsToReport = 0;
		for (String viewUsageSummary : viewUsage) {
			if (numViewsToReport == -1 || numViewsToReport <= maxViewsToReport) {
				summaries.add(viewUsageSummary);
				numViewsToReport++;
			}
		}
		
		if (!filteredViewSelections.keySet().isEmpty()) {
			summaries.add("<h4>Interest Filtering</h4>");
		}
		
		// TODO: pull this out into a mylar-specific thing
		for (String view : filteredViewSelections.keySet()) {
			int normalSelections = normalViewSelections.get(view);
			int filteredSelections = filteredViewSelections.get(view);
			int unfilteredSelections = normalSelections - filteredSelections; 
			summaries.add(view + ": " + filteredSelections + " vs. ");
			summaries.add(unfilteredSelections + "<br>");
		}
		summaries.add("<h4>Interest Model</h4>");
		
		int numNew = 0;
		if (usersNumNew.containsKey(userId)) usersNumNew.get(userId);
		int numPredicted = 0;
		if (usersNumPredicted.containsKey(userId)) numPredicted = usersNumPredicted.get(userId);
		int numInteresting = 0;
		if (usersNumDefault.containsKey(userId)) numInteresting = usersNumDefault.get(userId);
		int numDecayed = 0;
		if (usersNumDecayed.containsKey(userId)) numDecayed = usersNumDecayed.get(userId);
		int numUnknown = 0;
		if (usersNumUnknown.containsKey(userId)) numUnknown =usersNumUnknown.get(userId);
		
		float inModel = (numPredicted + numInteresting + numDecayed);
		float notInModel = numNew;
		float hitRatio = inModel / (inModel + notInModel);
		summaries.add("In model: " + formatAsPercentage(hitRatio) + "<br>"); 
		
		summaries.add("New: " + formatAsPercentage(numNew/numSelections) + " (" + numNew + ")" + "; ");
		summaries.add("Predicted: " + formatAsPercentage(usersNumPredicted.get(userId)/numSelections) + " (" + numPredicted + ")" + "; ");
		summaries.add("Interesting: " + formatAsPercentage(usersNumDefault.get(userId)/numSelections) + " (" + numInteresting + ")" + "; ");
		summaries.add("Decayed: " + formatAsPercentage(usersNumDecayed.get(userId)/numSelections) + " (" + numDecayed + ")" + "; ");
		summaries.add("Unknown: " + formatAsPercentage(usersNumUnknown.get(userId)/numSelections) + " (" + numUnknown + ")" + "<br>");
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

	public void setMaxViewsToReport(int maxViewsToReport) {
		this.maxViewsToReport = maxViewsToReport;
	}
}