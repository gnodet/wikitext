/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.actions;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotification;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskListNotificationReminder;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class TestTaskListNotificationAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// ignore

	}

	public void init(IWorkbenchWindow window) {
		// ignore

	}

	public void run(IAction action) {
		Collection<AbstractTask> allTasks = TasksUiPlugin.getTaskList().getAllTasks();
		Iterator<AbstractTask> iterator = allTasks.iterator();

		TaskListNotification notification = new TaskListNotificationReminder(new LocalTask("1",
				"This notification has a long description"));
		notification.setDescription("Mylyn is the Task-Focused UI for Eclipse that reduces information overload "
				+ "\n..........and makes multi-tasking easy. It does this by making tasks a first class part of"
				+ "Eclipse, and integrating rich and offline editing for repositories such as Bugzilla, "
				+ "Trac, and JIRA. Once your tasks are integrated, Mylyn monitors your work activity to "
				+ "identify information relevant to the task-at-hand, and uses this task context to focus ");
		TasksUiPlugin.getTaskListNotificationManager().getNotifications().add(notification);
		notification.setDate(new Date());

		notification = new TaskListNotificationReminder(new LocalTask("1", "Very old task"));
		notification.setDate(new Date(0));
		TasksUiPlugin.getTaskListNotificationManager().getNotifications().add(notification);

		notification = new TaskListNotificationReminder(new LocalTask("1", "Very recent task"));
		notification.setDate(new Date());
		TasksUiPlugin.getTaskListNotificationManager().getNotifications().add(notification);

		for (int i = 0; i < 6 && i < allTasks.size(); i++) {
			notification = new TaskListNotification(iterator.next());
			TasksUiPlugin.getTaskListNotificationManager().getNotifications().add(notification);
		}
		TasksUiPlugin.getTaskListNotificationManager().showPopup();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
