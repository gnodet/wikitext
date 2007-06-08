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

package org.eclipse.mylyn.internal.bugzilla.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.editors.TaskHyperlinkDetector;

/**
 * @author Rob Elves
 */
public class BugzillaTaskHyperlinkDetector extends TaskHyperlinkDetector {

	private static final int TASK_NUM_GROUP = 3;

	private static final String regexp = "(duplicate of|bug|task)(\\s#|#|#\\s|\\s|)(\\s\\d+|\\d+)";

	private static final Pattern PATTERN = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);

	@Override
	protected IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		ArrayList<IHyperlink> hyperlinksFound = new ArrayList<IHyperlink>();

		Matcher m = PATTERN.matcher(text);
		while (m.find()) {
			if (lineOffset >= m.start() && lineOffset <= m.end()) {
				IHyperlink link = extractHyperlink(repository, regionOffset, m);
				if (link != null)
					hyperlinksFound.add(link);
			}
		}

		if (hyperlinksFound.size() > 0) {
			return hyperlinksFound.toArray(new IHyperlink[1]);
		}
		return null;
	}

	private static IHyperlink extractHyperlink(TaskRepository repository, int regionOffset, Matcher m) {

		int start = -1;

		if (m.group().startsWith("duplicate")) {
			start = m.start() + m.group().indexOf(m.group(TASK_NUM_GROUP));
		} else {
			start = m.start();
		}

		int end = m.end();

		if (end == -1)
			end = m.group().length();

		try {

			String bugId = m.group(TASK_NUM_GROUP).trim();
			start += regionOffset;
			end += regionOffset;

			IRegion sregion = new Region(start, end - start);
			return new TaskHyperlink(sregion, repository, bugId);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	protected String getTargetID() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
	}

}
