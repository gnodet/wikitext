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

package org.eclipse.mylar.sandbox.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaSearchEngine;
import org.eclipse.mylar.internal.bugzilla.core.search.BugzillaSearchHit;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

/**
 * @author Mik Kersten
 */
public class CollabNetRegularExpressionTest extends TestCase {

	public void testMatchV220() throws IOException {
		BufferedReader in = new BufferedReader(new StringReader(EXAMPLE_REPORT));
		Match match = new Match();
		BugzillaSearchHit hit = BugzillaSearchEngine.createHit(BugzillaSearchEngine.reValueBugzilla220,
				new NullProgressMonitor(), in, match, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, 123);
		assertEquals("nor", hit.getSeverity());
		assertEquals("P2", hit.getPriority());
	}

	private static final String EXAMPLE_REPORT = "  <tr class=\"bz_enhancement             bz_P2             bz_ASSIGNED                                                    bz_row_odd             \">\n"
			+ "             \n"
			+ "\n"
			+ "    <td class=\"first-child\">\n"
			+ "      <a href=\"show_bug.cgi?id=114172\">114172</a>\n"
			+ "      <span style=\"display: none\"></span>\n"
			+ "    </td>\n"
			+ "\n"
			+ "    <td style=\"white-space: nowrap\">nor\n"
			+ "    </td>\n"
			+ "    <td style=\"white-space: nowrap\">P2\n"
			+ "    </td>\n"
			+ "    <td style=\"white-space: nowrap\">PC\n"
			+ "    </td>\n"
			+ "    <td style=\"white-space: nowrap\">wes.coelho&#64;gmail.com\n"
			+ "    </td>\n"
			+ "    <td style=\"white-space: nowrap\">ASSI\n"
			+ "    </td>\n"
			+ "    <td style=\"white-space: nowrap\">\n"
			+ "    </td>\n"
			+ "    <td >Switch task data directory option on the task list\n" + "    </td>\n" + "\n" + "  </tr>\n";
}
