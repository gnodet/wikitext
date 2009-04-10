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

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.jira.core.JiraCorePlugin;
import org.eclipse.mylyn.internal.jira.core.JiraRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class JiraCreateIssueExample {

	private static final String URL = "http://mylyn.eclipse.org/jiratest";

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("usage: JiraCreateIssueExample username password");
			System.exit(1);
		}

		// create task repository
		TaskRepository repository = new TaskRepository(JiraCorePlugin.CONNECTOR_KIND, URL);

		// set repository credentials
		if (args.length >= 2) {
			AuthenticationCredentials credentials = new AuthenticationCredentials(args[0], args[1]);
			repository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);
		}

		// initialize JIRA plugin
		File serverCacheLocation = new File(System.getProperty("java.io.tmpdir"));
		JiraCorePlugin.initialize(serverCacheLocation);

		AbstractRepositoryConnector connector = new JiraRepositoryConnector();
		AbstractTaskDataHandler dataHandler = connector.getTaskDataHandler();

		try {
			TaskData taskData = new TaskData(dataHandler.getAttributeMapper(repository), repository.getConnectorKind(),
					repository.getRepositoryUrl(), "");
			TaskMapping mapping = new TaskMapping() {
				@Override
				public String getProduct() {
					return "SCRATCH";
				}

				@Override
				public String getSummary() {
					return "Issue created by JiraCreateIssueExample";
				}
			};

			System.out.println("Initializing project...");
			dataHandler.initializeTaskData(repository, taskData, mapping, null);
			System.out.println("Applying attributes...");
			connector.getTaskMapping(taskData).merge(mapping);

			System.out.println("Creating issue...");
			RepositoryResponse response = dataHandler.postTaskData(repository, taskData, null, null);

			System.out.println("Retrieving issue...");
			taskData = connector.getTaskData(repository, response.getTaskId(), null);

			// access task information
			ITaskMapping taskMapping = connector.getTaskMapping(taskData);
			System.out.println("Summary: " + taskMapping.getSummary());
			System.out.println("Key:     " + taskMapping.getTaskKey());
			System.out.println("URL:     " + taskMapping.getTaskUrl());
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			CommonsNetPlugin.getExecutorService().shutdown();
		}
	}
}
