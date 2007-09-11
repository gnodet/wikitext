/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 */
public class IncomingTaskListContentProvider extends TaskListContentProvider {

	Set<AbstractTaskContainer> people = new HashSet<AbstractTaskContainer>();

	public IncomingTaskListContentProvider(TaskListView taskListView) {
		super(taskListView);
	}

	@Override
	public Object[] getElements(Object parent) {
		people.clear();
		for (AbstractTask task : TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks()) {
			if (task.getSynchronizationState().equals(RepositoryTaskSyncState.INCOMING)
					&& task.getOwner().contains("@")) {
				people.add(new Person(task.getOwner()));
			}
		}
		return people.toArray();
	}

	@Override
	public Object[] getChildren(Object parent) {
		Set<AbstractTask> children = new HashSet<AbstractTask>();
		if (parent instanceof Person) {
			for (AbstractTask task : TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks()) {
				if (task.getOwner().equals(((Person) parent).getHandleIdentifier())) {
					children.add(task);
				}
			}
		}
		return children.toArray();
	}

	@Override
	public boolean hasChildren(Object parent) {
		return parent instanceof Person;
	}
}
