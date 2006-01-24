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

package org.eclipse.mylar.internal.tasklist.planner.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Mik Kersten
 */
public class TaskPlanSorter extends TaskActivitySorter {

	// {".", "Description", "Priority", "Estimated Time", "Reminder Date"};
	public final static int PRIORITY = 1;

	public final static int DESCRIPTION = 2;

	public final static int DURATION = 3;

	public final static int ESTIMATE = 4;

	public final static int REMINDER = 5;

	public static final int ICON = 0;

	private int criteria;

	public TaskPlanSorter(int criteria) {
		super(criteria);
		this.criteria = criteria;
	}

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		ITask t1 = (ITask) obj1;
		ITask t2 = (ITask) obj2;

		switch (criteria) {
		case PRIORITY:
			return comparePriority(t1, t2);
		case DESCRIPTION:
			return compareDescription(t1, t2);
		case DURATION:
			return compareDuration(t1, t2);
		case ESTIMATE:
			return compareEstimated(t1, t2);
		case REMINDER:
			return compareReminder(t1, t2);
		default:
			return 0;
		}
	}

	private int compareReminder(ITask task1, ITask task2) {
		if (task2.getReminderDate() == null)
			return -1;
		if (task1.getReminderDate() == null)
			return 1;
		if (task1.getReminderDate() == null && task2.getReminderDate() == null)
			return 0;
		return task2.getReminderDate().compareTo(task1.getReminderDate());
	}

	// protected int compareEstimated(ITask task1, ITask task2) {
	// if (task1.getEstimateTimeHours() > task2.getEstimateTimeHours()) {
	// return 1;
	// } else {
	// return -1;
	// }

	// private int compareDescription(ITask task1, ITask task2) {
	// return
	// task1.getDescription(false).compareTo(task2.getDescription(false));
	// }
	//	
	// private int comparePriority(ITask task1, ITask task2) {
	// return task1.getPriority().compareTo(task2.getPriority());
	// }
	//	

}
