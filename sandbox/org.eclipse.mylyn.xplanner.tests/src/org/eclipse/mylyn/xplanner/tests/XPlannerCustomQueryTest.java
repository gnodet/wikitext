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
import org.eclipse.mylyn.internal.tasks.ui.LocalTaskConnectorUi;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerCustomQuery;

public class XPlannerCustomQueryTest extends TestCase {

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
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL, "no items");
		query.setPersonId(-1);

		Set<AbstractTask> hits = performTestQuery(taskList, query);
		assert (hits.size() == 0);
	}

	public void testAdminItemsQuery() {
		ITaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL, "admin items");
		try {
			query.setPersonId(XPlannerTestUtils.getAdminId(client));
			Set<AbstractTask> hits = performTestQuery(taskList, query);
			assert (hits.size() == 1);
		} catch (RemoteException e) {
			fail("Could not perform admin items query test");
		}

	}

	public void testMyItemsQuery() {
		ITaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL, "admin items");
		query.setMyCurrentTasks(true);
		Set<AbstractTask> hits = performTestQuery(taskList, query);
		assert (hits.size() > 0);
	}

	public void testGetQueryWizardValidClient() {
		TaskRepository taskRepository = XPlannerTestUtils.getRepository();

		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
		assertTrue(!(connectorUi instanceof LocalTaskConnectorUi));
		IWizard wizard = connectorUi.getQueryWizard(taskRepository, null);
		assertNotNull(wizard);
	}

	private Set<AbstractTask> performTestQuery(ITaskList taskList, XPlannerCustomQuery query) {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getConnectorKind());

		SearchHitCollector collector = new SearchHitCollector(taskList, repository, query);
		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());

		Set<AbstractTask> hits = collector.getTasks();
		return hits;
	}

}
