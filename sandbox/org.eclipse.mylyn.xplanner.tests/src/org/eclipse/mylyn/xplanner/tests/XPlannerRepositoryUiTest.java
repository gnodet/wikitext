/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;

public class XPlannerRepositoryUiTest extends TestCase {

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

	public void testFindHyperlinksValid() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());

		checkValidHyperlinks(repository, connectorUi, "hello task:123", "123");
		checkValidHyperlinks(repository, connectorUi, "hello story:456", "456");
		checkValidHyperlinks(repository, connectorUi, "hello task:456 ", "456");

		checkInvalidHyperlinks(repository, connectorUi, "hello Task :123");
		checkInvalidHyperlinks(repository, connectorUi, "hello story: 123");
	}

	private void checkValidHyperlinks(TaskRepository repository, AbstractRepositoryConnectorUi connectorUi,
			String testData, String id) throws Exception {

		IHyperlink[] hyperlinks = connectorUi.findHyperlinks(repository, testData, -1, 0);
		assertTrue(hyperlinks.length == 1);
		assertTrue(((TaskHyperlink) hyperlinks[0]).getTaskId().equals(id));
	}

	private void checkInvalidHyperlinks(TaskRepository repository, AbstractRepositoryConnectorUi connectorUi,
			String testData) throws Exception {

		IHyperlink[] hyperlinks = connectorUi.findHyperlinks(repository, testData, -1, 0);
		assertNull(hyperlinks);
	}
}
