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

import java.util.Date;

import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.jira.core.model.JiraIssue;
import org.eclipse.mylyn.internal.jira.core.model.filter.DateRangeFilter;
import org.eclipse.mylyn.internal.jira.core.model.filter.FilterDefinition;
import org.eclipse.mylyn.internal.jira.core.model.filter.ProjectFilter;
import org.eclipse.mylyn.internal.jira.core.service.JiraClient;
import org.eclipse.mylyn.internal.jira.core.service.JiraException;

/**
 * @author Steffen Pingel
 */
public class JiraClientExample {

	private static final String URL = "https://mylyn.eclipse.org/jiratest";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("usage: JiraClientExample username password");
			System.exit(1);
		}

		JiraClient client = new JiraClient(new WebLocation(URL, args[0], args[1]));

		try {
			// retrieve repository configuration
			System.out.println("Retrieving repository configuration...");
			client.getCache().refreshDetails(null);

			// access task information
			FilterDefinition filter = new FilterDefinition();
			filter.setUpdatedDateFilter(new DateRangeFilter(new Date(2008, 6, 30), new Date(2008, 7, 31)));
			filter.setProjectFilter(new ProjectFilter(client.getCache().getProjectByKey("SCRATCH")));

			// do the search
			System.out.println("Performing search...");
			JiraIssueCollector collector = new JiraIssueCollector(100);
			client.search(filter, collector, null);

			for (JiraIssue issue : collector.getResults()) {
				System.out.println(issue.getKey() + ": " + issue.getSummary() + " (" + issue.getProject().getName()
						+ ", " + issue.getUpdated() + ")");
			}
		} catch (JiraException e) {
			e.printStackTrace();
		} finally {
			CommonsNetPlugin.getExecutorService().shutdown();
		}
	}
}
