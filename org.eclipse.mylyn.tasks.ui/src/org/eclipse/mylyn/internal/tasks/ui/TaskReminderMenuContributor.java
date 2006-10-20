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

package org.eclipse.mylar.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.tasks.ui.planner.DateSelectionDialog;
import org.eclipse.mylar.internal.tasks.ui.views.DatePicker;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: this has bloated, reafactor
 * 
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskReminderMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL_REMINDER = "Schedule";

	private static final String LABEL_TODAY = "Today";

	private static final String LABEL_NEXT_WEEK = "Next Week";

	private static final String LABEL_FUTURE = "Two Weeks";

	private static final String LABEL_CALENDAR = "Choose Date...";

	private static final String LABEL_CLEAR = "Clear";

	@SuppressWarnings("deprecation")
	public MenuManager getSubMenuManager(final List<ITaskListElement> selectedElements) {

		final MenuManager subMenuManager = new MenuManager(LABEL_REMINDER);
		
		subMenuManager.setVisible(selectedElements.size() > 0 && !(selectedElements.get(0) instanceof AbstractTaskContainer || selectedElements.get(0) instanceof AbstractRepositoryQuery));
		
		ITask selectedTask = null;
		if (selectedElements.size() == 1) {
			ITaskListElement selectedElement = selectedElements.get(0);
			if (selectedElement instanceof ITask) {
				selectedTask = (ITask) selectedElement;
			} else if (selectedElement instanceof AbstractQueryHit) {
				if (((AbstractQueryHit) selectedElement).getCorrespondingTask() != null) {
					selectedTask = ((AbstractQueryHit) selectedElement).getCorrespondingTask();
				}
			}
		}
		final ITask singleTask = selectedTask;
		final List<ITask> tasks = new ArrayList<ITask>();
		for (ITaskListElement selectedElement : selectedElements) {
			if (selectedElement instanceof ITask) {
				tasks.add((ITask) selectedElement);
			} else if (selectedElement instanceof AbstractQueryHit) {
				if (((AbstractQueryHit) selectedElement).getCorrespondingTask() != null) {
					tasks.add(((AbstractQueryHit) selectedElement).getCorrespondingTask());
				}
			}
		}

		Action action = new Action() {
			@Override
			public void run() {
				Calendar reminderCalendar = GregorianCalendar.getInstance();
				TasksUiPlugin.getTaskListManager().setScheduledToday(reminderCalendar);
				for (ITask task : tasks) {
					TasksUiPlugin.getTaskListManager().setScheduledFor(task, reminderCalendar.getTime());
				}
			}
		};
		action.setText(LABEL_TODAY);
		action.setEnabled(canSchedule(singleTask, tasks));
		subMenuManager.add(action);
		if (singleTask != null) {
			if (TasksUiPlugin.getTaskListManager().isScheduledForToday(singleTask)) {
				action.setChecked(true);
			}
		}
		subMenuManager.add(new Separator());

		final int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		boolean reachedEndOfWeek = false;
		for (int i = today + 1; i <= 8 && !reachedEndOfWeek; i++) {
			final int day = i;
			action = new Action() {
				@Override
				public void run() {
					Calendar reminderCalendar = GregorianCalendar.getInstance();
					int dueIn = day - today;
					TasksUiPlugin.getTaskListManager().setSecheduledIn(reminderCalendar, dueIn);
					for (ITask task : tasks) {
						TasksUiPlugin.getTaskListManager().setScheduledFor(task, reminderCalendar.getTime());
					}
				}
			};
			getDayLabel(i, action);
			if (singleTask != null) {
				if (singleTask != null && singleTask.getReminderDate() != null) {
					int tasksCheduledOn = singleTask.getReminderDate().getDay();
					if (TasksUiPlugin.getTaskListManager().isScheduledForThisWeek(singleTask)) {
						if (tasksCheduledOn + 1 == day) {
							action.setChecked(true);
						} else if (tasksCheduledOn == 0 && day == 8) {
							action.setChecked(true);
						}
					}
				}
			}
			action.setEnabled(canSchedule(singleTask, tasks));
			subMenuManager.add(action);
		}

		subMenuManager.add(new Separator());

		action = new Action() {
			@Override
			public void run() {
				for (ITask task : tasks) {
					TasksUiPlugin.getTaskListManager().setScheduledFor(task,
							TasksUiPlugin.getTaskListManager().getActivityNextWeek().getStart().getTime());
				}
			}
		};
		action.setText(LABEL_NEXT_WEEK);
		action.setEnabled(canSchedule(singleTask, tasks));
		if (singleTask != null) {
			if (TasksUiPlugin.getTaskListManager().isScheduledAfterThisWeek(singleTask)
					&& !TasksUiPlugin.getTaskListManager().isScheduledForLater(singleTask)) {
				action.setChecked(true);
			}
		}
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				for (ITask task : tasks) {
					TasksUiPlugin.getTaskListManager().setScheduledFor(task,
							TasksUiPlugin.getTaskListManager().getActivityFuture().getStart().getTime());
				}
			}
		};
		action.setText(LABEL_FUTURE);
		action.setEnabled(canSchedule(singleTask, tasks));
		if (singleTask != null) {
			if (TasksUiPlugin.getTaskListManager().isScheduledForLater(singleTask)) {
				action.setChecked(true);
			}
		}
		subMenuManager.add(action);

		subMenuManager.add(new Separator());
		
		action = new Action() {
			@Override
			public void run() {
				Calendar theCalendar = GregorianCalendar.getInstance();
				if (singleTask != null && singleTask.getReminderDate() != null) {
					theCalendar.setTime(singleTask.getReminderDate());
				}
				DateSelectionDialog reminderDialog = new DateSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						theCalendar, DatePicker.TITLE_DIALOG);
				int result = reminderDialog.open();
				if (result == Window.OK) {
					for (ITask task : tasks) {
						TasksUiPlugin.getTaskListManager().setScheduledFor(task, reminderDialog.getDate());		
					}
				}
			}
		};
		action.setText(LABEL_CALENDAR);
		action.setEnabled(canSchedule(singleTask, tasks));
		subMenuManager.add(action);

		action = new Action() {
			@Override
			public void run() {
				for (ITask task : tasks) {
					TasksUiPlugin.getTaskListManager().setScheduledFor(task, null);		
				}
			}
		};
		action.setText(LABEL_CLEAR);
		action.setEnabled(tasks.size() > 0);
		subMenuManager.add(action);
		return subMenuManager;
	}

	private void getDayLabel(int i, Action action) {
		switch (i) {
		case Calendar.MONDAY:
			action.setText("Monday");
			break;
		case Calendar.TUESDAY:
			action.setText("Tuesday");
			break;
		case Calendar.WEDNESDAY:
			action.setText("Wednesday");
			break;
		case Calendar.THURSDAY:
			action.setText("Thursday");
			break;
		case Calendar.FRIDAY:
			action.setText("Friday");
			break;
		case Calendar.SATURDAY:
			action.setText("Saturday");
			break;
		case 8:
			action.setText("Sunday");
			break;
		default:
			break;
		}
	}

	private boolean canSchedule(ITask singleTask, List<ITask> tasks) {
		return (singleTask != null && !singleTask.isCompleted())
			|| tasks.size() > 0;
	}
}
