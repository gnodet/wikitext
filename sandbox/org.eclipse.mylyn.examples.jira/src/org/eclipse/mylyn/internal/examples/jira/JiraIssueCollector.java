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
package org.eclipse.mylyn.internal.examples.jira;

import java.util.ArrayList;

import org.eclipse.mylyn.internal.jira.core.model.JiraIssue;
import org.eclipse.mylyn.internal.jira.core.model.filter.IssueCollector;

/**
 * @author Steffen Pingel
 */
public class JiraIssueCollector implements IssueCollector {

	private final int maxHits;

	private final ArrayList<JiraIssue> results;

	public JiraIssueCollector(int maxHits) {
		this.maxHits = maxHits;
		this.results = new ArrayList<JiraIssue>();
	}

	public void collectIssue(JiraIssue issue) {
		results.add(issue);
	}

	public void done() {
		// ignore
	}

	public int getMaxHits() {
		return maxHits;
	}

	public boolean isCancelled() {
		return false;
	}

	public void start() {
		// ignore
	}

	public JiraIssue[] getResults() {
		return results.toArray(new JiraIssue[0]);
	}

}
