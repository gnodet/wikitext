/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.net.URL;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.web.core.WebCredentials;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryTest extends TestCase {

	public void testLabel() {
		TaskRepository repository = new TaskRepository("kind", "http://foo.bar");
		assertTrue(repository.getRepositoryLabel().equals(repository.getUrl()));

		repository.setProperty(IRepositoryConstants.PROPERTY_LABEL, "label");
		assertTrue(repository.getRepositoryLabel().equals("label"));
	}

	public void testPassword() throws Exception {
		password(WebCredentials.Type.REPOSITORY);

		// test old API
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(WebCredentials.Type.REPOSITORY, new WebCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getUserName());
		assertEquals("pwd", taskRepository.getPassword());

		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
	}

	public void testHttpPassword() throws Exception {
		password(WebCredentials.Type.HTTP);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(WebCredentials.Type.HTTP, new WebCredentials("user", "pwd"), true);
		assertEquals("user", taskRepository.getHttpUser());
		assertEquals("pwd", taskRepository.getHttpPassword());
	}

	public void testProxyPassword() throws Exception {
		password(WebCredentials.Type.PROXY);

		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(WebCredentials.Type.PROXY, new WebCredentials("user", "pwd"), false);
		assertEquals("user", taskRepository.getProxyUsername());
		assertEquals("pwd", taskRepository.getProxyPassword());
	}

	public void testFlushCredentials() throws Exception {
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		taskRepository.setCredentials(WebCredentials.Type.REPOSITORY, new WebCredentials("user", "pwd"), false);
		taskRepository.setCredentials(WebCredentials.Type.HTTP, new WebCredentials("user", "pwd"), true);
		taskRepository.flushAuthenticationCredentials();
		assertEquals(null, taskRepository.getUserName());
		assertEquals(null, taskRepository.getPassword());
		assertEquals(null, taskRepository.getHttpUser());
		assertEquals(null, taskRepository.getHttpPassword());
		assertNull(taskRepository.getCredentials(WebCredentials.Type.REPOSITORY));
		assertNull(taskRepository.getCredentials(WebCredentials.Type.HTTP));
		assertNull(taskRepository.getCredentials(WebCredentials.Type.PROXY));
	}

	public void password(WebCredentials.Type authType) throws Exception {
		URL url = new URL("http://url");
		TaskRepository taskRepository = new TaskRepository("kind", url.toString());
		assertNull(taskRepository.getCredentials(authType));
		assertTrue(taskRepository.getSavePassword(authType));

		taskRepository.setCredentials(authType, new WebCredentials("user", "pwd"), true);
		WebCredentials credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user", credentials.getUserName());
		assertEquals("pwd", credentials.getPassword());

		Map<?, ?> map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user"));
		assertTrue(map.containsValue("pwd"));

		// test not saving password
		taskRepository.setCredentials(authType, new WebCredentials("user1", "pwd1"), false);
		assertFalse(taskRepository.getSavePassword(authType));
		credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user1", credentials.getUserName());
		assertEquals("pwd1", credentials.getPassword());

		// make sure not old passwords are in the key ring
		map = Platform.getAuthorizationInfo(url, "", "Basic");
		assertNotNull(map);
		assertTrue(map.containsValue("user1"));
		assertFalse(map.containsValue("pwd1"));
		assertFalse(map.containsValue("user"));
		assertFalse(map.containsValue("pwd"));

		taskRepository.setCredentials(authType, new WebCredentials("user2", "pwd2"), true);
		assertTrue(taskRepository.getSavePassword(authType));
		credentials = taskRepository.getCredentials(authType);
		assertNotNull(credentials);
		assertEquals("user2", credentials.getUserName());
		assertEquals("pwd2", credentials.getPassword());
	}

}
