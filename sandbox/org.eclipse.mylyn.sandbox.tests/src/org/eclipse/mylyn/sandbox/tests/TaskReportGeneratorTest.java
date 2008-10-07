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

package org.eclipse.mylyn.sandbox.tests;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.sandbox.ui.planner.CompletedTaskCollector;
import org.eclipse.mylyn.internal.sandbox.ui.planner.TaskReportGenerator;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskReportGeneratorTest extends TestCase {

	private TaskList taskList;

	@Override
	protected void setUp() throws Exception {
		taskList = TasksUiPlugin.getTaskList();
		TaskTestUtil.resetTaskList();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskList();
	}

	public void testCompletedTasksRetrieved() throws InvocationTargetException, InterruptedException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		taskList.addTask(task1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0), new Date());
		TaskReportGenerator generator = new TaskReportGenerator(taskList);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		task1.setCompleted(true);
		collector = new CompletedTaskCollector(new Date(0), new Date());
		generator = new TaskReportGenerator(taskList);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedTasksDateBoundsRetrieved() throws InvocationTargetException, InterruptedException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		taskList.addTask(task1);
		task1.setCompleted(true);
		Thread.sleep(1000);
		long now = new Date().getTime();

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(now), new Date());
		TaskReportGenerator generator = new TaskReportGenerator(taskList);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		generator = new TaskReportGenerator(taskList);
		collector = new CompletedTaskCollector(new Date(now - 8000), new Date());
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksRetrieved() throws InvocationTargetException, InterruptedException {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		TaskTask task1 = new TaskTask(IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1", "bugzillatask 1");
		taskList.addTask(task1);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 5);
		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0), cal.getTime());
		TaskReportGenerator generator = new TaskReportGenerator(taskList);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		task1.setCompleted(true);
		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
		TasksUiPlugin.getRepositoryManager().removeRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testCompletedTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		AbstractTask task1 = TasksUiInternal.createNewLocalTask("task 1");
		taskList.addTask(task1);
		task1.setCompleted(true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category");
		taskList.addCategory(cat1);

		Set<AbstractTaskContainer> catagories = new HashSet<AbstractTaskContainer>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0), new Date());
		TaskReportGenerator generator = new TaskReportGenerator(taskList, catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		taskList.addTask(task1, cat1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInCategoryRetrieved() throws InvocationTargetException, InterruptedException {
		TaskTask task1 = new TaskTask("repo", "1", "task 1");
		taskList.addTask(task1);
		task1.setCompleted(true);
		TaskCategory cat1 = new TaskCategory("TaskReportGeneratorTest Category");
		taskList.addCategory(cat1);

		Set<AbstractTaskContainer> catagories = new HashSet<AbstractTaskContainer>();
		catagories.add(cat1);

		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0), new Date());
		TaskReportGenerator generator = new TaskReportGenerator(taskList, catagories);
		generator.addCollector(collector);
		generator.run(new NullProgressMonitor());
		assertEquals(0, generator.getAllCollectedTasks().size());

		taskList.addTask(task1, cat1);

		generator.run(new NullProgressMonitor());
		assertEquals(1, generator.getAllCollectedTasks().size());
		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

	public void testCompletedBugzillaTasksInQueryRetrieved() throws InvocationTargetException, InterruptedException {
		TaskTask task1 = new TaskTask("repo", "1", "task 1");
		taskList.addTask(task1);
		task1.setCompleted(false);

		fail("test not implemented, comment out lines below this statement");
//		BugzillaRepositoryQuery bugQuery = new BugzillaRepositoryQuery("repositoryUrl", "queryUrl",
//				"TaskReportGeneratorBugzillaQueryCategory");
//
//		taskList.addQuery(bugQuery);
//
//		Set<AbstractTaskContainer> catagories = new HashSet<AbstractTaskContainer>();
//		catagories.add(bugQuery);
//		Calendar future = Calendar.getInstance();
//		future.add(Calendar.MINUTE, 1);
//		CompletedTaskCollector collector = new CompletedTaskCollector(new Date(0), future.getTime());
//		TaskReportGenerator generator = new TaskReportGenerator(taskList, catagories);
//		generator.addCollector(collector);
//		generator.run(new NullProgressMonitor());
//		assertEquals(0, generator.getAllCollectedTasks().size());
//
//		taskList.addTask(task1, bugQuery);
//
//		generator.run(new NullProgressMonitor());
//		assertEquals(0, generator.getAllCollectedTasks().size());
//
//		task1.setCompleted(true);
//
//		generator.run(new NullProgressMonitor());
//		assertEquals(1, generator.getAllCollectedTasks().size());
//		assertEquals(task1, generator.getAllCollectedTasks().get(0));
	}

}
