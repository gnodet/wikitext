/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.core.TracTask;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.trac.tests.support.TestFixture;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.TestData;

/**
 * @author Steffen Pingel
 */
public class TracOfflineTaskHandlerTest extends TestCase {

	private TracRepositoryConnector connector;

	private IOfflineTaskHandler offlineHandler;

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private TestData data;

	public TracOfflineTaskHandlerTest() {
	}

	protected void setUp() throws Exception {
		super.setUp();

		data = TestFixture.init010();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		connector = (TracRepositoryConnector) manager.getRepositoryConnector(TracCorePlugin.REPOSITORY_KIND);
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);

		offlineHandler = connector.getOfflineTaskHandler();
	}

	protected void init(String url, Version version) {
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testGetChangedSinceLastSyncWeb096() throws Exception {
		init(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.offlineHandlerTicketId + "", null);

		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task);
		
		assertEquals(null, repository.getSyncTimeStamp());
		Set<AbstractRepositoryTask> result = offlineHandler.getChangedSinceLastSync(repository, tasks);
		assertEquals(tasks, result);
		assertEquals(null, repository.getSyncTimeStamp());
		
		int time = (int)(System.currentTimeMillis() / 1000) + 1;
		repository.setSyncTimeStamp(time + "");
		assertEquals(tasks, result);
	}

	public void testGetChangedSinceLastSyncXmlRpc010() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.offlineHandlerTicketId + "", null);
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		int lastModified = Integer.parseInt(task.getTaskData().getLastModified());
		
		Set<AbstractRepositoryTask> tasks = new HashSet<AbstractRepositoryTask>();
		tasks.add(task);

		assertEquals(null, repository.getSyncTimeStamp());
		Set<AbstractRepositoryTask> result = offlineHandler.getChangedSinceLastSync(repository, tasks);
		assertEquals(tasks, result);

		// always returns the ticket because time comparison mode is >=
		repository.setSyncTimeStamp(lastModified + "");
		result = offlineHandler.getChangedSinceLastSync(repository, tasks);
		assertEquals(tasks, result);

		repository.setSyncTimeStamp((lastModified + 1) + "");
		result = offlineHandler.getChangedSinceLastSync(repository, tasks);		
		assertTrue(result.isEmpty());
		
		// change ticket making sure it gets a new change time
		Thread.sleep(1000);
		ITracClient client = connector.getClientManager().getRepository(repository);
		TracTicket ticket = client.getTicket(data.offlineHandlerTicketId);
		if (ticket.getValue(Key.DESCRIPTION).equals(lastModified + "")) {
			ticket.putBuiltinValue(Key.DESCRIPTION, lastModified + "x");
		} else {
			ticket.putBuiltinValue(Key.DESCRIPTION, lastModified + "");
		}
		client.updateTicket(ticket, "comment");

		repository.setSyncTimeStamp((lastModified + 1) + "");
		result = offlineHandler.getChangedSinceLastSync(repository, tasks);		
		assertEquals(tasks, result);
	}

}
