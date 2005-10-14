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

package org.eclipse.mylar.monitor.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.monitor.InteractionEventLogger;
import org.eclipse.mylar.monitor.reports.ui.views.UsageStatisticsSummary;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 */
public class ReportGenerator {

	private InteractionEventLogger logger;
	private UsageStatisticsSummary lastParsedSummary = null;
	private Set<Integer> userIds = new HashSet<Integer>();	
	private List<IStatsCollector> collectors;
	
	public ReportGenerator(InteractionEventLogger logger, List<IStatsCollector> collectors) {
		this.logger = logger;
		this.collectors = collectors;
	}
	
	public UsageStatisticsSummary getStatisticsFromInteractionHistory(File source) {
		List<File> sources = new ArrayList<File>();
		sources.add(source);
		return getStatisticsFromInteractionHistories(sources);
	}
	
	public UsageStatisticsSummary getStatisticsFromInteractionHistories(List<File> sources) {
		lastParsedSummary = null;
		try {
			GenerateStatisticsJob job = new GenerateStatisticsJob(this, sources);
			IProgressService service = PlatformUI.getWorkbench().getProgressService();       
			service.run(true, true, job);
			
			while (lastParsedSummary == null) Thread.sleep(1000);
	     } catch (InvocationTargetException e) {        
	    	 // Operation was canceled     
    	 } catch (InterruptedException e) {     
    		 MylarPlugin.log(e, "Could not generate stats");
		 }
        return lastParsedSummary;
    }

	public UsageStatisticsSummary getLastParsedSummary() {
		return lastParsedSummary;
	}
	
	/**
	 * Assuming the file naming convention of <phase>-<version>-usage-<userID>-<date and time>.zip
	*/
	private int getUserId(File source) {
		String userIDText = source.getName();
		int userId = -1;
		String prefix = "-usage-";
		
		if (source.getName().indexOf(prefix) >= 0) {
			try {
				userIDText = userIDText.substring(userIDText.indexOf(prefix) + prefix.length(), userIDText.length()); 
				userIDText = userIDText.substring(0, userIDText.indexOf("-"));
				userId = Integer.valueOf(userIDText);
			} catch(Throwable t) {
				MylarPlugin.log(t, "could not parse user ID from source file");
			}
		}
			
		return userId;
	}
	
	private String getPhase(File source) {
		String userIDText = source.getName();
		String phase = "unknown";
		String terminator = "-";
		
		if (source.getName().indexOf(terminator) >= 0) {
			try {
				userIDText = userIDText.substring(0, userIDText.indexOf(terminator)-1); 
			} catch(Throwable t) {
				MylarPlugin.log(t, "could not parse user ID from source file");
			}
		}
		return phase;
	}

	class GenerateStatisticsJob implements IRunnableWithProgress {

		private final ReportGenerator generator;
		private List<File> sources;
		
		public GenerateStatisticsJob(ReportGenerator generator, List<File> sources) {
			this.generator = generator;
			this.sources = sources;
		}
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Mylar statistics generation", sources.size());
			UsageStatisticsSummary statistics = new UsageStatisticsSummary();
			try {
				Map<Integer, Map<String, InteractionEventSummary>> summaryMap = new HashMap<Integer, Map<String, InteractionEventSummary>>();
		        
				for (File source : sources) {
			        List<InteractionEvent> events = this.generator.logger.getHistoryFromFile(source);
		        	for (InteractionEvent event : events) {
		        		String phase = getPhase(source);
		        		int userId = getUserId(source);
		        		userIds.add(userId);
			        	if (event.getKind().isUserEvent()) {  // TODO: some collectors may want non-user events
			        		for (IStatsCollector collector : this.generator.collectors) {
			        			collector.consumeEvent(event, userId, phase);
			        		}
			        	}
			        	createUsageTableData(summaryMap, event, userId);
		            } 
			        monitor.worked(1);
		        }
	        	for (IStatsCollector collector : this.generator.collectors) statistics.add(collector);
				List<InteractionEventSummary> flattenedSummaries = new ArrayList<InteractionEventSummary>();
		        for (Map<String, InteractionEventSummary> userSummary : summaryMap.values()) flattenedSummaries.addAll(userSummary.values());
		        statistics.setSingleSummaries(flattenedSummaries);	
		        this.generator.lastParsedSummary = statistics;	
		        monitor.done();
			} catch (Throwable t) {
				t.printStackTrace();
	        	MylarPlugin.fail(t, "could not generate stats", false);
	        }
	     }

		private void createUsageTableData(Map<Integer, Map<String, InteractionEventSummary>> summaryMap, InteractionEvent event, int userId) {
			Map<String, InteractionEventSummary> usersSummary = summaryMap.get(userId);
			if (usersSummary == null) {
				usersSummary = new HashMap<String, InteractionEventSummary>();
				summaryMap.put(userId, usersSummary);
			}
			
			InteractionEventSummary summary = usersSummary.get(getIdentifier(event));
			if (summary == null) {
			    summary = new InteractionEventSummary(event.getKind().toString(), getCleanOriginId(event), 0);
			    usersSummary.put(getIdentifier(event), summary);
			}
			summary.setUsageCount(summary.getUsageCount()+1);
			summary.setInterestContribution(summary.getInterestContribution() + event.getInterestContribution());
			summary.setDelta(event.getDelta());
		}
		
		public  String getIdentifier(InteractionEvent event) {
			return event.getKind().toString() + ':' + getCleanOriginId(event);
		}
		
	}
	
	public static String getCleanOriginId(InteractionEvent event) {
		String cleanOriginId = "";
		String originId = event.getOriginId();
		
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			for (int i = 0; i < originId.length(); i++) {
				char curChar = originId.charAt(i);
				if (!(curChar == '&')) {
					if (Character.getType(curChar) == Character.CONTROL) {
						cleanOriginId = cleanOriginId.concat(" ");
					}
					else {
						cleanOriginId = cleanOriginId.concat(String.valueOf(curChar));
					}
				}	
			}
			return cleanOriginId;
		} else {
			return originId;
		}
	}
}
