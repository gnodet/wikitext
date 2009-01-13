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

package org.eclipse.mylyn.internal.sandbox.dev.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
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
		Set<TaskListNotificationReminder> dummyNotifications = new HashSet<TaskListNotificationReminder>();
		for (int i = 0; i < 6; i++) {
			TaskListNotificationReminder notification = new TaskListNotificationReminder(iterator.next());
			notification.setDescription("Mylyn is the Task-Focused UI for Eclipse that reduces information overload "
					+ "\nand makes multi-tasking easy. It does this by making tasks a first class part of"
					+ "\nEclipse, and integrating rich and offline editing for repositories such as Bugzilla, "
					+ "\nTrac, and JIRA. Once your tasks are integrated, Mylyn monitors your work activity to "
					+ "\nidentify information relevant to the task-at-hand, and uses this task context to focus ");
			dummyNotifications.add(notification);
			TasksUiPlugin.getTaskListNotificationManager().getNotifications().add(notification);
		}
		TasksUiPlugin.getTaskListNotificationManager().showPopup();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
