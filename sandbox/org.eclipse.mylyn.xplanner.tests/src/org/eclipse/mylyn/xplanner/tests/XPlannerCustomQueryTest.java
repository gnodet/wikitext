/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.tests;

import java.rmi.RemoteException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.ui.XPlannerCustomQuery;

public class XPlannerCustomQueryTest extends TestCase {

	private static XPlannerServer server;
	
	protected void setUp() throws Exception {
		super.setUp();
		if (server == null) { // only create data once per run
			server = XPlannerTestUtils.getXPlannerServer();
			XPlannerTestUtils.clearTestData(server);
			XPlannerTestUtils.setUpTestData(server);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testNoItemsQuery() {
		TaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL,
			"no items", taskList);
		query.setPersonId(-1);
		
		List<AbstractQueryHit> hits = performTestQuery(taskList, query);
		assert(hits.size() == 0);
	}

	public void testAdminItemsQuery() {
		TaskList taskList = XPlannerTestUtils.getTaskList();
		XPlannerCustomQuery query = new XPlannerCustomQuery(XPlannerTestUtils.SERVER_URL,
			"admin items", taskList);
		try {
			query.setPersonId(XPlannerTestUtils.getAdminId(server));
			List<AbstractQueryHit> hits = performTestQuery(taskList, query);
			assert(hits.size() == 1);
		}
		catch (RemoteException e) {
			fail("Could not perform admin items query test");
		}
		
	}

	private List<AbstractQueryHit> performTestQuery(TaskList taskList, XPlannerCustomQuery query) {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnector connector = 
			TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		
		QueryHitCollector collector = new QueryHitCollector(taskList);
		connector.performQuery(query, repository, new NullProgressMonitor(), collector);
		
		List<AbstractQueryHit> hits = collector.getHits();
		return hits;
	}
	
}
