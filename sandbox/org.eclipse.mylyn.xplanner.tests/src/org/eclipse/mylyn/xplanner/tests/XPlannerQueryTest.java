/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.rmi.RemoteException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.LocalRepositoryConnectorUi;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.util.TestTaskDataCollector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerTaskListMigrator;
import org.eclipse.mylyn.xplanner.ui.XPlannerTaskListMigrator.ContentIdType;

public class XPlannerQueryTest extends TestCase {

	private static XPlannerClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (client == null) { // only create data once per run
			client = XPlannerTestUtils.getXPlannerClient();
			XPlannerTestUtils.clearTestData(client);
			XPlannerTestUtils.setUpTestData(client);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNoItemsQuery() {
		ITaskList taskList = XPlannerTestUtils.getTaskList();
		RepositoryQuery query = new RepositoryQuery(XPlannerTestUtils.SERVER_URL, "no items");
		XPlannerTaskListMigrator.setPersonId(query, XPlannerAttributeMapper.INVALID_ID);

		Set<ITask> hits = performTestQuery(taskList, query);
		assertEquals(hits.size(), 0);
	}

	public void testAdminItemsQuery() throws Exception {
		ITaskList taskList = XPlannerTestUtils.getTaskListWithXPlannerTask();
		RepositoryQuery query = new RepositoryQuery(XPlannerTestUtils.SERVER_URL, "admin items");
		try {
			String contentId = Integer.toString(XPlannerTestUtils.findTestUserStory(
					XPlannerTestUtils.getXPlannerClient()).getId());

			XPlannerTaskListMigrator.setPersonId(query, XPlannerTestUtils.getAdminId(client));
			XPlannerTaskListMigrator.setContentIdType(query, ContentIdType.USER_STORY.name());
			XPlannerTaskListMigrator.setContentIds(query, contentId);

			Set<ITask> hits = performTestQuery(taskList, query);
			assertEquals(hits.size(), 1);
		} catch (RemoteException e) {
			fail("Could not perform admin items query test");
		}
	}

	public void testMyItemsQuery() {
		ITaskList taskList = XPlannerTestUtils.getTaskList();
		RepositoryQuery query = new RepositoryQuery(XPlannerTestUtils.SERVER_URL, "admin items");
		XPlannerTaskListMigrator.setMyCurrentTasks(query, true);
		Set<ITask> hits = performTestQuery(taskList, query);
		assertTrue(hits.size() > 0);
	}

	public void testGetQueryWizardValidClient() {
		TaskRepository taskRepository = XPlannerTestUtils.getRepository();

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
		assertTrue(!(connectorUi instanceof LocalRepositoryConnectorUi));
		IWizard wizard = connectorUi.getQueryWizard(taskRepository, null);
		assertNotNull(wizard);
	}

	private Set<ITask> performTestQuery(ITaskList taskList, RepositoryQuery query) {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		TestTaskDataCollector collector = new TestTaskDataCollector();
		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());
		return collector.getTasks(connector, repository);
	}

}