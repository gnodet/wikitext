/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.examples.bugzilla;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class Main {

	private static final String URL = "https://landfill.bugzilla.org/bugzilla-tip/";

	public static void main(String[] args) {
		// create task repository
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, URL);

		// set repository credentials
		if (args.length >= 2) {
			AuthenticationCredentials credentials = new AuthenticationCredentials(args[0], args[1]);
			repository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);
		}

		// create bugzilla connector
		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();

		try {
			// get a report from repository
			TaskData taskData = connector.getTaskData(repository, "1", null);

			// access task information
			ITaskMapping taskMapping = connector.getTaskMapping(taskData);
			System.out.println("Summary:     " + taskMapping.getSummary());
			System.out.println("Priority:    " + taskMapping.getPriority());

			// access report data via attributes
			TaskAttribute descriptionAttribute = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
			System.out.println("Description: " + descriptionAttribute.getValue());

			// ...or by Bugzilla keys
			TaskAttribute severityAttribute = taskData.getRoot().getAttribute(BugzillaAttribute.BUG_SEVERITY.getKey());
			System.out.println("Severity:    " + severityAttribute.getValue());

			// Post modified report to repository
			System.out.println("\nPosting a new description...");
			descriptionAttribute.setValue("Hello world.");
			connector.getTaskDataHandler().postTaskData(repository, taskData, null, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
}
