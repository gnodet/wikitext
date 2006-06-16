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

package org.eclipse.mylar.tasklist.tests;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.tasklist.tests.mockconnector.MockRepositoryConnector;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryManagerTest extends TestCase {

	private static final String DEFAULT_KIND = BugzillaPlugin.REPOSITORY_KIND;

	private static final String DEFAULT_URL = "http://eclipse.org";

	private static final String ANOTHER_URL = "http://codehaus.org";

	private TaskRepositoryManager manager;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = MylarTaskListPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories();
		}
	}

	public void testHandles() {
		String url = IBugzillaConstants.ECLIPSE_BUGZILLA_URL;
		String id = "123";
		String handle = AbstractRepositoryTask.getHandle(url, id);
		assertEquals(url, AbstractRepositoryTask.getRepositoryUrl(handle));
		assertEquals(id, AbstractRepositoryTask.getTaskId(handle));
		assertEquals(123, AbstractRepositoryTask.getTaskIdAsInt(handle));
	}

	public void testMultipleNotAdded() throws MalformedURLException {
		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository2);
		assertEquals(1, manager.getAllRepositories().size());
	}

	public void testGet() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);
		assertEquals(repository, manager.getRepository(DEFAULT_KIND, DEFAULT_URL));
		assertNull(manager.getRepository(DEFAULT_KIND, "foo"));
		assertNull(manager.getRepository("foo", DEFAULT_URL));
	}

	public void testConnectorAddition() {
		AbstractRepositoryConnector connector = new MockRepositoryConnector();
		manager.addRepositoryConnector(connector);
		assertNotNull(manager.getRepositoryConnector(connector.getRepositoryType()));
	}
	
	public void testRepositoryPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		TaskRepository repository2 = new TaskRepository("jira", "http://jira");
		manager.addRepository(repository1);
		manager.addRepository(repository2);

		assertNotNull(MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		List<TaskRepository> repositoryList = new ArrayList<TaskRepository>();
		repositoryList.add(repository2);
		repositoryList.add(repository1);
		manager.readRepositories();
		// NOTE: different conditions for running with and without the JIRA Connector
		if (manager.getRepositoryConnectors().size() > 1) {
			assertTrue(manager.getAllRepositories().contains(repository1));
			assertTrue(manager.getAllRepositories().contains(repository2));
			// assertEquals(repositoryList, manager.getAllRepositories());
		} else {
			// TODO there is something wrong with this
			assertEquals("all: " + manager.getAllRepositories(), 1, manager.getAllRepositories().size());
		}
	}

	public void testRepositoryVersionPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		String version = "123";

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		// repository1.setVersion(version);
		MylarTaskListPlugin.getRepositoryManager().setVersion(repository1, version);
		manager.addRepository(repository1);

		String prefIdVersion = repository1.getUrl() + TaskRepositoryManager.PROPERTY_DELIM
				+ TaskRepositoryManager.PROPERTY_VERSION;

		assertEquals(version, MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdVersion));

		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertEquals(temp.getVersion(), version);

	}

	public void testRepositoryEncodingPersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));

		String encoding = "UTF-8";

		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		MylarTaskListPlugin.getRepositoryManager().setEncoding(repository1, encoding);
		manager.addRepository(repository1);

		String prefIdEncoding = repository1.getUrl() + TaskRepositoryManager.PROPERTY_DELIM
				+ TaskRepositoryManager.PROPERTY_ENCODING;

		assertEquals(encoding, MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdEncoding));

		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertEquals(temp.getCharacterEncoding(), encoding);
	}
	
	public void testRepositoryTimeZonePersistance() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		String fakeTimeZone = "nowhere";
		MylarTaskListPlugin.getRepositoryManager().setTimeZoneId(repository1, fakeTimeZone);
		manager.addRepository(repository1);

		String prefIdTimeZoneId = repository1.getUrl() + TaskRepositoryManager.PROPERTY_DELIM
				+ TaskRepositoryManager.PROPERTY_TIMEZONE;

		assertEquals(fakeTimeZone, MylarTaskListPlugin.getMylarCorePrefs().getString(prefIdTimeZoneId));

		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertEquals(temp.getTimeZoneId(), fakeTimeZone);
	}
	
	
	public void testRepositorySyncTimePersistance1() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");		
		manager.addRepository(repository1);
		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertNotNull(temp.getSyncTime());
		assertEquals(new Date(0), temp.getSyncTime());		
	}
		
	public void testRepositorySyncTimePersistance2() throws MalformedURLException {
		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(TaskRepositoryManager.PREF_REPOSITORIES));
		TaskRepository repository1 = new TaskRepository("bugzilla", "http://bugzilla");
		
		Date now = new Date();
		MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository1, now);
		manager.addRepository(repository1);

		String prefIdSyncTime= repository1.getUrl() + TaskRepositoryManager.PROPERTY_DELIM
				+ TaskRepositoryManager.PROPERTY_SYNCTIME;

		assertEquals(now.getTime(), MylarTaskListPlugin.getMylarCorePrefs().getLong(prefIdSyncTime));

		manager.readRepositories();
		TaskRepository temp = manager.getRepository(repository1.getKind(), repository1.getUrl());
		assertNotNull(temp);
		assertEquals(temp.getSyncTime(), now);
	}
	
	public void testRepositoryPersistanceAfterDelete() throws MalformedURLException {
		manager.clearRepositories();

		assertEquals("", MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND));

		TaskRepository repository = new TaskRepository(DEFAULT_KIND, DEFAULT_URL);
		manager.addRepository(repository);

		assertFalse(MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND).equals(""));

		TaskRepository repository2 = new TaskRepository(DEFAULT_KIND, ANOTHER_URL);
		manager.addRepository(repository2);

		String saveString = MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND);
		assertNotNull(saveString);

		manager.removeRepository(repository2);

		String newSaveString = MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskRepositoryManager.PREF_REPOSITORIES + DEFAULT_KIND);

		assertFalse(saveString.equals(newSaveString));
	}

	public void testRepositoryWithUnnownUrlHandler() {
		TaskRepository repository = new TaskRepository("eclipse.technology.mylar",
				"nntp://news.eclipse.org/eclipse.technology.mylar");

		repository.setAuthenticationCredentials("testUser", "testPassword");

		assertEquals("testUser", repository.getUserName());
		assertEquals("testPassword", repository.getPassword());
	}
	
	public void testRepositoryWithCustomAttributes() throws Exception {
		TaskRepository repository = new TaskRepository("http://jroller.com/page/eu", "web");
		repository.setProperty("owner", "euxx");
		manager.addRepository(repository);
		
		manager.readRepositories();
		
		TaskRepository temp = manager.getRepository(repository.getKind(), repository.getUrl());
		assertNotNull(temp);
		assertEquals("euxx", temp.getProperty("owner"));
	}
}
