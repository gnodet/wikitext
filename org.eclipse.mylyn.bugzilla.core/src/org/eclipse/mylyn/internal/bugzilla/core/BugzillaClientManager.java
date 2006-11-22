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

package org.eclipse.mylar.internal.bugzilla.core;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylar.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @author Robert Elves (adaption for Bugzilla)
 */
public class BugzillaClientManager implements ITaskRepositoryListener {

	private Map<String, BugzillaClient> clientByUrl = new HashMap<String, BugzillaClient>();

	public BugzillaClientManager() {
	}

	public synchronized BugzillaClient getClient(TaskRepository taskRepository) throws MalformedURLException {
		BugzillaClient client = clientByUrl.get(taskRepository.getUrl());
		if (client == null) {

			String htUser = taskRepository.getProperty(TaskRepository.AUTH_HTTP_USERNAME) != null ? taskRepository
					.getProperty(TaskRepository.AUTH_HTTP_USERNAME) : "";
			String htPass = taskRepository.getProperty(TaskRepository.AUTH_HTTP_PASSWORD) != null ? taskRepository
					.getProperty(TaskRepository.AUTH_HTTP_PASSWORD) : "";

			client = BugzillaClientFactory.createClient(taskRepository.getUrl(), taskRepository.getUserName(),
					taskRepository.getPassword(), htUser, htPass, taskRepository.getProxy(), taskRepository
							.getCharacterEncoding());
			clientByUrl.put(taskRepository.getUrl(), client);
		}
		return client;
	}

	public void repositoriesRead() {
		// ignore
	}

	public synchronized void repositoryAdded(TaskRepository repository) {
		// make sure there is no stale client still in the cache, bug #149939
		clientByUrl.remove(repository.getUrl());
	}

	public synchronized void repositoryRemoved(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
	}

	public synchronized void repositorySettingsChanged(TaskRepository repository) {
		clientByUrl.remove(repository.getUrl());
	}
}
