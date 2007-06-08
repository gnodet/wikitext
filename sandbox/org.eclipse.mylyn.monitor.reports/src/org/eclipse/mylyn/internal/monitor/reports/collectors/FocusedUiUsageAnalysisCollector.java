/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.reports.collectors;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskDeactivateAction;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.usage.ReportGenerator;

/**
 * Delagates to other collectors for additional info.
 * 
 * @author Mik Kersten
 */
public class FocusedUiUsageAnalysisCollector extends AbstractMylarUsageCollector {

	// public static final int BASELINE_EDITS_THRESHOLD = 400;
	// private static final int MYLAR_EDITS_THRESHOLD = 1200;

	public static final int BASELINE_EDITS_THRESHOLD = 1000;

	private static final int MYLAR_EDITS_THRESHOLD = 3000;

	private static final int NUM_VIEWS_REPORTED = 5;

	private float summaryEditRatioDelta = 0;

	private final List<Integer> usersImproved = new ArrayList<Integer>();

	private final List<Integer> usersDegraded = new ArrayList<Integer>();

	private Map<Integer, Date> startDates = new HashMap<Integer, Date>();

	private Map<Integer, Integer> numMylarActiveJavaEdits = new HashMap<Integer, Integer>();

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

	private Map<Integer, InteractionEvent> lastUserEvent = new HashMap<Integer, InteractionEvent>();

	private Map<Integer, Long> timeMylarActive = new HashMap<Integer, Long>();

	private Map<Integer, Long> timeMylarInactive = new HashMap<Integer, Long>();

	private Map<Integer, Long> timeBaseline = new HashMap<Integer, Long>();

	private FocusedUiViewUsageCollector viewUsageCollector = new FocusedUiViewUsageCollector();

	public FocusedUiUsageAnalysisCollector() {
		viewUsageCollector.setMaxViewsToReport(NUM_VIEWS_REPORTED);
		super.getDelegates().add(viewUsageCollector);
	}

	@Override
	public String getReportTitle() {
		return "Mylar Usage";
	}

	@Override
	public void consumeEvent(InteractionEvent event, int userId) {
		super.consumeEvent(event, userId);
		if (!startDates.containsKey(userId))
			startDates.put(userId, event.getDate());
		endDates.put(userId, event.getDate());

		// Mylar is active
		if (mylarUserIds.contains(userId) && !mylarInactiveUserIds.contains(userId)) {
			accumulateDuration(event, userId, timeMylarActive);
			if (isJavaEdit(event))
				incrementCount(userId, numMylarActiveJavaEdits);
			if (isSelection(event)) {
				incrementCount(userId, mylarSelections);
				incrementCount(userId, mylarCurrentNumSelectionsBeforeEdit);
			} else if (isEdit(event)) {
				incrementCount(userId, mylarEdits);

				if (mylarCurrentNumSelectionsBeforeEdit.containsKey((userId))) {
					int num = mylarCurrentNumSelectionsBeforeEdit.get(userId);
					if (num > 0) {
						incrementCount(userId, mylarTotalEditsCounted);
						incrementCount(userId, mylarTotalSelectionsBeforeEdit, num);
						mylarCurrentNumSelectionsBeforeEdit.put(userId, 0);
					}
				}
			}
			// Mylar is inactive
		} else if (mylarInactiveUserIds.contains(userId)) {
			accumulateDuration(event, userId, timeMylarInactive);
			if (isSelection(event)) {
				incrementCount(userId, mylarInactiveSelections);
			} else if (isEdit(event)) {
				incrementCount(userId, mylarInactiveEdits);
			}
			// Baseline
		} else {
			accumulateDuration(event, userId, timeBaseline);
			if (isSelection(event)) {
				incrementCount(userId, baselineSelections);

				incrementCount(userId, baselineCurrentNumSelectionsBeforeEdit);
			} else if (isEdit(event)) {
				incrementCount(userId, baselineEdits);

				if (baselineCurrentNumSelectionsBeforeEdit.containsKey((userId))) {
					int num = baselineCurrentNumSelectionsBeforeEdit.get(userId);
					if (num > 0) {
						incrementCount(userId, baselineTotalEditsCounted);
						incrementCount(userId, baselineTotalSelectionsBeforeEdit, num);
						baselineCurrentNumSelectionsBeforeEdit.put(userId, 0);
					}
				}
			}
		}
	}

	private void accumulateDuration(InteractionEvent event, int userId, Map<Integer, Long> timeAccumulator) {
		// Restart accumulation if greater than 5 min has elapsed between events
		if (lastUserEvent.containsKey(userId)) {
			long elapsed = event.getDate().getTime() - lastUserEvent.get(userId).getDate().getTime();

			if (elapsed < 5 * 60 * 1000) {
				if (!timeAccumulator.containsKey(userId)) {
					timeAccumulator.put(userId, new Long(0));
				}
				timeAccumulator.put(userId, timeAccumulator.get(userId) + elapsed);
			}
		}
		lastUserEvent.put(userId, event);
	}

	public static boolean isEdit(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.EDIT)
				|| (event.getKind().equals(InteractionEvent.Kind.SELECTION) && isSelectionInEditor(event));
	}

	public static boolean isSelection(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.SELECTION) && !isSelectionInEditor(event);
	}

	public static boolean isSelectionInEditor(InteractionEvent event) {
		return event.getOriginId().contains("Editor") || event.getOriginId().contains("editor")
				|| event.getOriginId().contains("source");
	}

	public static boolean isJavaEdit(InteractionEvent event) {
		return event.getKind().equals(InteractionEvent.Kind.EDIT)
				&& (event.getOriginId().contains("java") || event.getOriginId().contains("jdt.ui"));
	}

	private void incrementCount(int userId, Map<Integer, Integer> map, int count) {
		if (!map.containsKey(userId))
			map.put(userId, 0);
		map.put(userId, map.get(userId) + count);
	}

	private void incrementCount(int userId, Map<Integer, Integer> map) {
		incrementCount(userId, map, 1);
	}

	@Override
	public List<String> getReport() {
		usersImproved.clear();
		usersDegraded.clear();
		int acceptedUsers = 0;
		int rejectedUsers = 0;
		summaryEditRatioDelta = 0;
		List<String> report = new ArrayList<String>();
		for (int id : userIds) {
			if (acceptUser(id)) {
				report.add("<h3>USER ID: " + id + " (from: " + getStartDate(id) + " to " + getEndDate(id) + ")</h3>");
				acceptedUsers++;

				float baselineRatio = getBaselineRatio(id);
				float mylarInactiveRatio = getMylarInactiveRatio(id);
				float mylarActiveRatio = getMylarRatio(id);
				float combinedMylarRatio = mylarInactiveRatio + mylarActiveRatio;

				float ratioPercentage = (combinedMylarRatio - baselineRatio) / baselineRatio;
				if (ratioPercentage > 0) {
					usersImproved.add(id);
				} else {
					usersDegraded.add(id);
				}
				summaryEditRatioDelta += ratioPercentage;
				String baselineVsMylarRatio = "Baseline vs. Mylar edit ratio: " + baselineRatio + ", mylar: "
						+ combinedMylarRatio + ",  ";
				String ratioChange = ReportGenerator.formatPercentage(100 * ratioPercentage);
				baselineVsMylarRatio += " <b>change: " + ratioChange + "%</b>";
				report.add(baselineVsMylarRatio + "<br>");

				report.add("<h4>Activity</h4>");
				float editsActive = getNumMylarEdits(id);
				float editsInactive = getNumInactiveEdits(id);
				report.add("Proportion Mylar active (by edits): <b>"
						+ ReportGenerator.formatPercentage(100 * ((editsActive) / (editsInactive + editsActive)))
						+ "%</b><br>");

				report.add("Elapsed time baseline: " + getTime(id, timeBaseline) + ", active: "
						+ getTime(id, timeMylarActive) + ", inactive: " + getTime(id, timeMylarInactive) + "<br>");

				report.add("Selections baseline: " + getNumBaselineSelections(id) + ", Mylar active: "
						+ getNumMylarSelections(id) + ", inactive: " + getNumMylarInactiveSelections(id) + "<br>");
				report.add("Edits baseline: " + getNumBaselineEdits(id) + ", Mylar active: " + getNumMylarEdits(id)
						+ ", inactive: " + getNumInactiveEdits(id) + "<br>");

				int numTaskActivations = commandUsageCollector.getCommands().getUserCount(id, TaskActivateAction.ID);
				int numTaskDeactivations = commandUsageCollector.getCommands()
						.getUserCount(id, TaskDeactivateAction.ID);
				report.add("Task activations: " + numTaskActivations + ", ");
				report.add("deactivations: " + numTaskDeactivations + "<br>");

				int numIncrement = commandUsageCollector.getCommands().getUserCount(id,
						"org.eclipse.mylyn.ui.interest.increment");
				int numDecrement = commandUsageCollector.getCommands().getUserCount(id,
						"org.eclipse.mylyn.ui.interest.decrement");
				report.add("Interest increments: " + numIncrement + ", ");
				report.add("Interest decrements: " + numDecrement + "<br>");

				report.addAll(viewUsageCollector.getSummary(id, true));
				report.add(ReportGenerator.SUMMARY_SEPARATOR);
			} else {
				rejectedUsers++;
			}
		}
		report.add("<h3>Summary</h3>");
		String acceptedSummary = " (based on " + acceptedUsers + " accepted, " + rejectedUsers + " rejected users)";
		float percentage = summaryEditRatioDelta / acceptedUsers;
		String ratioChange = ReportGenerator.formatPercentage(100 * (percentage - 1));
		if (percentage >= 1) {
			report.add("Overall edit ratio improved by: " + ratioChange + "% " + acceptedSummary + "<br>");
		} else {
			report.add("Overall edit ratio degraded by: " + ratioChange + "% " + acceptedSummary + "<br>");
		}
		report.add("degraded: " + usersDegraded.size() + ", improved: " + usersImproved.size() + "<br>");
		report.add(ReportGenerator.SUMMARY_SEPARATOR);
		return report;
	}

	@Override
	public void exportAsCSVFile(String directory) {
		FileWriter writer;
		try {
			writer = new FileWriter(directory + "/mylar-usage.csv");
			writer
					.write("userid, "
							+ "ratio-baseline, ratio-mylar, "
							+ "ratio-improvement, "
							+ "filtered-explorer, "
							+ "filtered-outline, "
							+ "filtered-problems, "
							+ "edits-active, "
							+ "time-baseline, time-active, time-inactive, "
							+ "task-activations, task-deactivations, sel-interesting, sel-predicted, sel-decayed, sel-new, sel-unknown\n");
			// "filtered-explorer, filtered-outline, filtered-problems, ");

			for (int userId : userIds) {
				if (acceptUser(userId)) {
					writer.write(userId + ", ");
					float baselineRatio = getBaselineRatio(userId);
					float mylarInactiveRatio = getMylarInactiveRatio(userId);
					float mylarActiveRatio = getMylarRatio(userId);
					float combinedMylarRatio = mylarInactiveRatio + mylarActiveRatio;

					writer.write(baselineRatio + ", ");
					writer.write(combinedMylarRatio + ", ");

					float ratioPercentage = (combinedMylarRatio - baselineRatio) / baselineRatio;
					writer.write(100 * ratioPercentage + ", ");

					Map<String, Integer> filteredViewSelections = viewUsageCollector.usersFilteredViewSelections
							.get(userId);
					Map<String, Integer> normalViewSelections = viewUsageCollector.getUsersNormalViewSelections().get(
							userId);

					String[] views = new String[] { "org.eclipse.jdt.ui.PackageExplorer",
							"org.eclipse.ui.views.ContentOutline", "org.eclipse.ui.views.ProblemView" };
					for (int i = 0; i < views.length; i++) {
						if (normalViewSelections.containsKey(views[i]) && filteredViewSelections.containsKey(views[i])) {
							float normalSelections = normalViewSelections.get(views[i]);
							float filteredSelections = filteredViewSelections.get(views[i]);
							float ratio = filteredSelections / (normalSelections + filteredSelections);
							// int unfilteredSelections = normalSelections -
							// filteredSelections;
							if (ratio >= 0.01) {
								writer.write(ratio + ", ");
							} else {
								writer.write("0, ");
							}
						} else {
							writer.write("0, ");
						}
					}

					float editsActive = getNumMylarEdits(userId);
					float editsInactive = getNumInactiveEdits(userId);
					writer.write(100 * ((editsActive) / (editsInactive + editsActive)) + ", ");

					writer.write(getTime(userId, timeBaseline) + ", ");
					writer.write(getTime(userId, timeMylarActive) + ", ");
					writer.write(getTime(userId, timeMylarInactive) + ", ");

					int numTaskActivations = commandUsageCollector.getCommands().getUserCount(userId,
							TaskActivateAction.ID);
					int numTaskDeactivations = commandUsageCollector.getCommands().getUserCount(userId,
							TaskDeactivateAction.ID);
					writer.write(numTaskActivations + ", ");
					writer.write(numTaskDeactivations + ", ");

					int numNew = 0;
					if (viewUsageCollector.usersNumNew.containsKey(userId))
						numNew = viewUsageCollector.usersNumNew.get(userId);
					int numPredicted = 0;
					if (viewUsageCollector.usersNumPredicted.containsKey(userId))
						numPredicted = viewUsageCollector.usersNumPredicted.get(userId);
					int numInteresting = 0;
					if (viewUsageCollector.usersNumDefault.containsKey(userId))
						numInteresting = viewUsageCollector.usersNumDefault.get(userId);
					int numDecayed = 0;
					if (viewUsageCollector.usersNumDecayed.containsKey(userId))
						numDecayed = viewUsageCollector.usersNumDecayed.get(userId);
					int numUnknown = 0;
					if (viewUsageCollector.usersNumUnknown.containsKey(userId))
						numUnknown = viewUsageCollector.usersNumUnknown.get(userId);

					// float numSelections = numNew + numPredicted +
					// numInteresting + numDecayed + numUnknown;
					// writer.write(numSelections + ", ");
					writer.write(numInteresting + ", ");
					writer.write(numPredicted + ", ");
					writer.write(numDecayed + ", ");
					writer.write(numNew + ", ");
					writer.write(numUnknown + ", ");

					writer.write("\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "could not generate csv file", true);
		}
	}

	private String getTime(int id, Map<Integer, Long> timeMap) {
		if (timeMap.containsKey(id)) {
			long timeInSeconds = timeMap.get(id) / 1000;
			long hours, minutes;
			hours = timeInSeconds / 3600;
			timeInSeconds = timeInSeconds - (hours * 3600);
			minutes = timeInSeconds / 60;
			timeInSeconds = timeInSeconds - (minutes * 60);
			return hours + "." + minutes;
		} else {
			return "0";
		}
	}

	public boolean acceptUser(int id) {
		// XXX: delete
		// int[] ACCEPTED = {
		// 1922,
		// 970,
		// 1650,
		// 1548,
		// 1565,
		// 1752,
		// 2194,
		// 2364,
		// 1735,
		// 936,
		// 1803,
		// 2007,
		// 1208,
		// 1684,
		// 919,
		// 2041,
		// 1174
		// };
		// for (int i : ACCEPTED) {
		// if (i == id) return true;
		// }
		// return false;
		if (!numMylarActiveJavaEdits.containsKey(id)) {
			return false;
		} else {
			return getNumBaselineEdits(id) > BASELINE_EDITS_THRESHOLD && getNumMylarEdits(id) > MYLAR_EDITS_THRESHOLD;
		}
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

	public int getNumMylarInactiveEdits(int id) {
		if (mylarInactiveEdits.containsKey(id)) {
			return mylarInactiveEdits.get(id);
		} else {
			return 0;
		}
	}

	public int getNumInactiveEdits(int id) {
		if (mylarInactiveEdits.containsKey(id)) {
			return mylarInactiveEdits.get(id);
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
			return (float) edits.get(id) / (float) selections.get(id);
		} else {
			return 0f;
		}
	}
}

// int numIncrements = commandUsageCollector.getCommands().getUserCount(id,
// "org.eclipse.mylyn.internal.ui.actions.InterestIncrementAction");
// int numDecrements = commandUsageCollector.getCommands().getUserCount(id,
// "org.eclipse.mylyn.internal.ui.actions.InterestDecrementAction");
// report.add("Interest increments: " + numIncrements + ", decrements: " +
// numDecrements + "<br>");

// float inactivePercentage = (mylarActiveRatio-mylarInactiveRatio) /
// mylarInactiveRatio;
// String inactiveRatioChange = formatPercentage(100*(inactivePercentage));
// mylarInactiveDelta += inactivePercentage;
// String inactiveVsActiveRatio = "";
// inactiveVsActiveRatio += "Inactive vs. Active edit ratio: " +
// mylarInactiveRatio + ", mylar: " + mylarActiveRatio + ", ";
// inactiveVsActiveRatio += " <b>change: " + inactiveRatioChange + "%</b>";
// report.add(inactiveVsActiveRatio + "<br>");

// if (baselineTotalSelectionsBeforeEdit.containsKey(id) &&
// mylarTotalSelectionsBeforeEdit.containsKey(id)) {
// float baselineRuns =
// (float)baselineTotalSelectionsBeforeEdit.get(id) /
// (float)baselineTotalEditsCounted.get(id);
//	
// float mylarRuns =
// (float)mylarTotalSelectionsBeforeEdit.get(id) /
// (float)mylarTotalEditsCounted.get(id);
//	
// float runsPercentage = (mylarRuns-baselineRuns) / baselineRuns;
// String runsChange = formatPercentage(-100*runsPercentage);
// report.add("Avg baseline selections before edit: " + baselineRuns
// + " vs. mylar: " + mylarRuns
// + " <b>change: " + runsChange + "</b><br>");;
// }

// String[] REJECTED_IDs = {
// "org.eclipse.mylyn.java.ui.editor.MylarCompilationUnitEditor",
// "org.eclipse.jdt.ui.CompilationUnitEditor",
// "org.eclipse.jdt.ui.DefaultTextEditor",
// "org.eclipse.jdt.ui.ClassFileEditor"
// };

// String[] ACCEPTED_EDITORS = {
// "org.eclipse.mylyn.java.ui.editor.MylarCompilationUnitEditor",
// "org.eclipse.jdt.ui.CompilationUnitEditor"
// };
// String originId = event.getOriginId();
// if (originId == null) return false;

