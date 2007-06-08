///*******************************************************************************
// * Copyright (c) 2004 - 2006 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     University Of British Columbia - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.mylyn.sandbox.tests;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.StringReader;
//
//import junit.framework.TestCase;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.NullProgressMonitor;
//import org.eclipse.core.runtime.OperationCanceledException;
//import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
//import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
//import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchHit;
//
//import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
//import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;
//
///**
// * @author Mik Kersten
// * @author Marco Sferra
// */
//public class CollabNetRegularExpressionTest extends TestCase {
//
//	private static final RegularExpression reColVal1 = new RegularExpression(
//			"<span class=\"issue_type\">([^>]+)</span>", "i");
//
//	private static final RegularExpression reColVal2 = new RegularExpression("<span class=\"priority\">([^>]+)</span>",
//			"i");
//
//	private static final RegularExpression reColVal3 = new RegularExpression("<span class=\"platform\">([^>]+)</span>",
//			"i");
//
//	private static final RegularExpression reColVal4 = new RegularExpression("<span class=\"owner\">([^>]+)</span>",
//			"i");
//
//	private static final RegularExpression reColVal5 = new RegularExpression("<span class=\"status\">([^>]+)</span>",
//			"i");
//
//	private static final RegularExpression reColVal6 = new RegularExpression(
//			"<span class=\"resolution\">([^>]+)</span>", "i");
//
//	private static final RegularExpression reColVal7 = new RegularExpression("<span class=\"summary\">([^>]+)</span>",
//			"i");
//
//	public void testMatchV220() throws IOException {
//		BufferedReader in = new BufferedReader(new StringReader(EXAMPLE_REPORT));
//		Match match = new Match();
//		BugzillaSearchHit hit = createHit(new NullProgressMonitor(), in, match,
//				IBugzillaConstants.ECLIPSE_BUGZILLA_URL, 123);
//		assertEquals("nor", hit.getSeverity());
//		assertEquals("P2", hit.getPriority());
//	}
//
//	BugzillaSearchHit createHit(IProgressMonitor monitor, BufferedReader in, Match match, String serverUrl, int taskId)
//			throws IOException {
//
//		String severity = "none";
//		String priority = "none";
//		String platform = "none";
//		String owner = "none";
//		String state = "none";
//		String result = "none";
//		String summary = "none";
//
//		do {
//			if (monitor.isCanceled()) {
//				throw new OperationCanceledException("Search cancelled");
//			}
//			String line = in.readLine();
//			if (line == null) {
//				break;
//			}
//			line = line.trim();
//			if (severity.equals("none")) {
//				severity = readSeverity(line);
//			}
//			if (priority.equals("none")) {
//				priority = readPriority(line);
//			}
//			if (platform.equals("none")) {
//				platform = readPlatform(line);
//			}
//			if (owner.equals("none")) {
//				owner = readOwner(line);
//			}
//			if (state.equals("none")) {
//				state = readStatus(line);
//			}
//			if (result.equals("none")) {
//				result = readResolution(line);
//			}
//			if (summary.equals("none")) {
//				summary = readSummary(line);
//			}
//		} while (summary.equals("none"));
//
//		String summary = "<activate to view summary>";
//
//		// String server = "<unknown server>";
//
//		String query = "";
//		try {
//			String recentQuery = BugzillaUiPlugin.getMostRecentQuery();
//			if (recentQuery != null) {
//				query = recentQuery;
//			}
//			// server = BugzillaPlugin.getDefault().getServerName();
//		} catch (Exception exception1) {
//		}
//		return new BugzillaSearchHit(serverUrl, taskId, summary, severity, priority, platform, state, result, owner,
//				query);
//
//	}
//
//	private static final String EXAMPLE_REPORT = "<tr class=\"b\">\n" + "<td>\n"
//			+ "<a href=\"show_bug.cgi?taskId=1\">1</a>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"issue_type\">nor</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"priority\">P2</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"platform\">All</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"owner\">euxx</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"status\">NEW</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"resolution\">no result</span>\n" + "</td>\n"
//			+ "<td class=\"red\"><span class=\"summary\">Security context is not getting propagated</span>\n"
//			+ "</td>\n" + "</tr>\n";
//
//	public static String readSeverity(String line) {
//		Match match = new Match();
//		if (reColVal1.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readPriority(String line) {
//		Match match = new Match();
//		if (reColVal2.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readPlatform(String line) {
//		Match match = new Match();
//		if (reColVal3.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readOwner(String line) {
//		Match match = new Match();
//		if (reColVal4.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readStatus(String line) {
//		Match match = new Match();
//		if (reColVal5.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readResolution(String line) {
//		Match match = new Match();
//		if (reColVal6.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//	public static String readSummary(String line) {
//		Match match = new Match();
//		if (reColVal7.matches(line, match)) {
//			return match.getCapturedText(1);
//		} else {
//			return "none";
//		}
//	}
//
//}
