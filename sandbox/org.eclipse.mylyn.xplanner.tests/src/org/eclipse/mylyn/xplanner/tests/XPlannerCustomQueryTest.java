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
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.*;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerCustomQuery;

public class XPlannerCustomQueryTest extends TestCase {

	private static XPlannerClient client;
	
	protected void setUp() throws Exception {
		super.setUp();
		if (client == null) { // only create data once per run
			client = XPlannerTestUtils.getXPlannerClient();
			XPlannerTestUtils.clearTestData(client);
			XPlannerTestUtils.setUpTestData(client);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNoItemsQuery() {
		TaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL,
			"no items");
		query.setPersonId(-1);
		 
		Set<AbstractTask> hits = performTestQuery(taskList, query);
		assert(hits.size() == 0);
	}

	public void testAdminItemsQuery() {
		TaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(
			XPlannerTestUtils.SERVER_URL,	"admin items");
		try {
			query.setPersonId(XPlannerTestUtils.getAdminId(client));
			Set<AbstractTask> hits = performTestQuery(taskList, query);
			assert(hits.size() == 1);
		}
		catch (RemoteException e) {
			fail("Could not perform admin items query test");
		}
		
	}

	public void testGetQueryWizardValidClient() {
		TaskRepository taskRepository = XPlannerTestUtils.getRepository();
	
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(taskRepository.getConnectorKind());
		assertTrue(!(connectorUi instanceof LocalTaskConnectorUi));
		IWizard wizard = connectorUi.getQueryWizard(taskRepository, null);
		assertNotNull(wizard);
	}
	
	private Set<AbstractTask> performTestQuery(TaskList taskList, XPlannerCustomQuery query) {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getConnectorKind());
		
		TaskFactory taskFactory = new TaskFactory(repository, false, false);
		SearchHitCollector collector = new SearchHitCollector(taskList, repository, query, taskFactory);
		connector.performQuery(query, repository, new NullProgressMonitor(), collector);
		
		Set<AbstractTask> hits = collector.getTasks();
		return hits;
	}
	
}
