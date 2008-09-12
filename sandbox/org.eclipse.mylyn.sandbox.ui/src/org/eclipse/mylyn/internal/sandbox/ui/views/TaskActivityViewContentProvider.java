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

package org.eclipse.mylyn.internal.sandbox.ui.views;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.WeekDateRange;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskActivityViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	protected final TaskActivityManager taskActivityManager;

	public TaskActivityViewContentProvider(TaskActivityManager taskActivityManager) {
		this.taskActivityManager = taskActivityManager;
	}

	public Object[] getElements(Object parent) {
		Set<AbstractTaskContainer> containers = new HashSet<AbstractTaskContainer>();
		WeekDateRange week = TaskActivityUtil.getCurrentWeek();
		//containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), week.previous()));
		for (DateRange day : week.getDaysOfWeek()) {
			containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), day));
		}
//		containers.add(new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(), week));
//		ScheduledTaskContainer nextWeekContainer = new ScheduledTaskContainer(taskActivityManager, week.next());
//		containers.add(nextWeekContainer);

		return containers.toArray();
	}

	public Object getParent(Object child) {
		for (Object o : getElements(null)) {
			ScheduledTaskContainer container = ((ScheduledTaskContainer) o);
			for (Object o2 : getChildren(container)) {
				if (o2 instanceof AbstractTask) {
					if (((AbstractTask) o2).equals(child)) {
						return container;
					}
				}
			}
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof ScheduledTaskContainer) {
			DateRange range = ((ScheduledTaskContainer) parent).getDateRange();
			Set<AbstractTask> activeTasks = TasksUiPlugin.getTaskActivityManager().getActiveTasks(range.getStartDate(),
					range.getEndDate());
			return activeTasks.toArray();
		} else {
			return new Object[0];
		}
	}

	public boolean hasChildren(Object parent) {
		return getChildren(parent).length > 0;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
