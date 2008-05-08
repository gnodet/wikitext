/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.planner;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 */
public class TaskActivityEditorInput implements IEditorInput {

	private static final String TASK_ACTIVITY_REPORT = "Task Activity Report";

	private Set<ITask> completedTasks = new HashSet<ITask>();

	private Set<ITask> inProgressTasks = new HashSet<ITask>();

	private final Set<ITask> plannedTasks = new HashSet<ITask>();

	private TaskReportGenerator taskReportGenerator = null;

	private Date reportStartDate = null;

	private Date reportEndDate = null;

	private final Set<AbstractTaskContainer> categories;

	public TaskActivityEditorInput(Date reportStartDate, Date reportEndDate,
			Set<AbstractTaskContainer> chosenCategories, TaskList tlist) {
		this.reportStartDate = reportStartDate;
		this.reportEndDate = reportEndDate;
		this.categories = chosenCategories;
		taskReportGenerator = new TaskReportGenerator(tlist, chosenCategories);

		ITaskCollector completedTaskCollector = new CompletedTaskCollector(reportStartDate, reportEndDate);
		taskReportGenerator.addCollector(completedTaskCollector);

		ITaskCollector inProgressTaskCollector = new InProgressTaskCollector(reportStartDate, reportEndDate);
		taskReportGenerator.addCollector(inProgressTaskCollector);

		try {
			// TODO consider using IProgressService.busyCursorWhile(): bug 210710
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			service.run(false, true, taskReportGenerator);
			while (!taskReportGenerator.isFinished()) {
				Thread.sleep(500);
			}
		} catch (InvocationTargetException e) {
			// operation was canceled
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not generate report", e));
		}

		completedTasks = completedTaskCollector.getTasks();
		inProgressTasks = inProgressTaskCollector.getTasks();

		plannedTasks.addAll(TasksUiPlugin.getTaskActivityManager().getActiveTasks(
				TaskActivityUtil.getCurrentWeek().getStartDate(), TaskActivityUtil.getCurrentWeek().getEndDate()));
		plannedTasks.addAll(TasksUiPlugin.getTaskActivityManager().getAllScheduledTasks());

		//plannedTasks = new HashSet<ITask>();
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return TASK_ACTIVITY_REPORT;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Task Planner";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public Set<ITask> getCompletedTasks() {
		return completedTasks;
	}

	public Set<ITask> getInProgressTasks() {
		return inProgressTasks;
	}

	public Set<ITask> getPlannedTasks() {
		return plannedTasks;
	}

	public long getTotalTimeSpentOnCompletedTasks() {
		long duration = 0;
		for (ITask t : completedTasks) {
			duration += TasksUiPlugin.getTaskActivityManager().getElapsedTime(t);
		}
		return duration;
	}

	public long getTotalTimeSpentOnInProgressTasks() {
		long duration = 0;
		for (ITask t : inProgressTasks) {
			duration += TasksUiPlugin.getTaskActivityManager().getElapsedTime(t);
		}
		return duration;
	}

	public TaskReportGenerator getReportGenerator() {
		return taskReportGenerator;
	}

	public boolean createdDuringReportPeriod(ITask task) {
		Date creationDate = task.getCreationDate();
		if (creationDate != null) {
			return creationDate.compareTo(reportStartDate) > 0;
		} else {
			return false;
		}
	}

	public int getTotalTimeEstimated() {
		int duration = 0;
		for (ITask task : inProgressTasks) {
			duration += task.getEstimatedTimeHours();
		}
		return duration;
	}

	public void removeCompletedTask(ITask task) {
		completedTasks.remove(task);
	}

	public void removeInProgressTask(ITask task) {
		inProgressTasks.remove(task);
	}

	public void addPlannedTask(AbstractTask task) {
		plannedTasks.add(task);
	}

	public void removePlannedTask(ITask task) {
		plannedTasks.remove(task);
	}

	public int getPlannedEstimate() {
		int estimated = 0;
		for (ITask task : plannedTasks) {
			estimated += task.getEstimatedTimeHours();
		}
		return estimated;
	}

	public Date getReportStartDate() {
		return reportStartDate;
	}

	public Date getReportEndDate() {
		return reportEndDate;
	}

	public Set<AbstractTaskContainer> getCategories() {
		return categories;
	}
}
