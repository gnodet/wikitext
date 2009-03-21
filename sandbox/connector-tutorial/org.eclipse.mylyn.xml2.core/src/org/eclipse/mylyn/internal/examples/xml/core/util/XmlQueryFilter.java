/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.examples.xml.core.util;

import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class XmlQueryFilter {

	private Pattern summaryPattern;

	private Pattern projectPattern;

	public XmlQueryFilter(IRepositoryQuery query) {
		String expectedSummary = query.getAttribute(XmlCorePlugin.QUERY_KEY_SUMMARY);
		if (expectedSummary != null && expectedSummary.length() > 0) {
			summaryPattern = Pattern.compile(expectedSummary);
		}
		String expectedProject = query.getAttribute(XmlCorePlugin.QUERY_KEY_PROJECT);
		if (expectedProject != null && expectedProject.length() > 0) {
			projectPattern = Pattern.compile(expectedProject);
		}
	}

	public boolean accepts(TaskData taskData) {
		if (!match(summaryPattern, taskData.getRoot().getAttribute(TaskAttribute.SUMMARY))) {
			return false;
		}
		if (!match(projectPattern, taskData.getRoot().getAttribute(TaskAttribute.PRODUCT))) {
			return false;
		}
		return true;
	}

	private boolean match(Pattern pattern, TaskAttribute attribute) {
		if (pattern != null) {
			return attribute != null && pattern.matcher(attribute.getValue()).find();
		}
		return true;
	}

}
