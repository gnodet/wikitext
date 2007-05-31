/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.core.ServerManager;
import org.eclipse.mylar.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylar.xplanner.core.service.exceptions.ServiceUnavailableException;

/**
 * This class acts as a layer of indirection between clients in this project and
 * the server API implemented by the XPlanner Dashboard, and also abstracts some
 * Mylar implementation details. It initializes a xPlannerServer object and serves
 * as the central location to get a reference to it.
 * 
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 * 
 */
public class XPlannerServerFacade implements ITaskRepositoryListener {

	private ServerManager serverManager = null;

	private static XPlannerServerFacade instance = null;

	public XPlannerServerFacade() {
		TasksUiPlugin.getRepositoryManager().addListener(this);
		serverManager = XPlannerCorePlugin.getDefault().getServerManager();
	}

	/**
	 * Lazily creates server.
	 */
	public XPlannerServer getXPlannerServer(TaskRepository repository) throws CoreException {
		try {
			XPlannerRepositoryUtils.checkRepositoryValidated(repository.getUrl());
			String serverHostname = getServerHost(repository);
		XPlannerServer server = serverManager.getServer(serverHostname);
//TODO: add this check back once the listeners for server property change are hooked up
// Also handle the case when serviceDelegate in the cachedServer is null

//			if (server == null) {
				server = serverManager.createServer(serverHostname, repository.getUrl(), false,
						repository.getUserName(), repository.getPassword());
				serverManager.addServer(server);
//			}
			if (server == null) {
				throw new ServiceUnavailableException(serverHostname + " " + repository.getUrl()); //$NON-NLS-1$
			}
			return server;
		} 
		catch (CoreException ce) {
			MylarStatusHandler.log(Messages.XPlannerServerFacade_SERVER_CONNECTION_ERROR, this);
			throw ce;
		}
		catch (ServiceUnavailableException sue) {
			throw sue;
		} 
		catch (RuntimeException e) {
			MylarStatusHandler.log(Messages.XPlannerServerFacade_SERVER_CONNECTION_ERROR, this);
			throw e;
		}
	}

	public static XPlannerServerFacade getDefault() {
		if (instance == null) {
			instance = new XPlannerServerFacade();
		}
		return instance;
	}

	public void logOutFromAll() {
		try {
			XPlannerServer[] allServers = serverManager.getAllServers();
			for (int i = 0; i < allServers.length; i++) {
				allServers[i].logout();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public void repositoriesRead() {
		// ignore
	}

	public void repositoryAdded(TaskRepository repository) {
		if (repository.getKind().equals(XPlannerMylarUIPlugin.REPOSITORY_KIND)) {
			try {
				getXPlannerServer(repository);
			}
			catch (CoreException e) {
				MylarStatusHandler.log(e.getMessage(), this); 
			}
		}
	}

	public void repositoryRemoved(TaskRepository repository) {
		if (repository.getKind().equals(XPlannerMylarUIPlugin.REPOSITORY_KIND)) {
			String serverHostname = getServerHost(repository);
			XPlannerServer server = serverManager.getServer(serverHostname);
			removeServer(server);
			XPlannerRepositoryUtils.removeValidatedRepositoryUrl(repository.getUrl());
		}
	}
	
	public void repositorySettingsChanged(TaskRepository repository) {
		repositoryRemoved(repository);
		repositoryAdded(repository);
	}

	public void refreshServerSettings(TaskRepository repository) {
		String serverHostname = getServerHost(repository);
		XPlannerServer server = serverManager.getServer(serverHostname);
		if (server != null) {
			server.refreshDetails();
		}
	}
	
	
	private void removeServer(XPlannerServer server) {
		if (server != null) {
			server.logout();
			serverManager.removeServer(server);
		}
	} 

	/**
	 * Validate the server URL and user credentials
	 * @param serverUrl Location of the XPlanner Server
	 * @param user Username
	 * @param password Password
	 * @return String describing validation failure or null if the details are valid
	 */
	public void validateServerAndCredentials(String serverUrl, String user, 
		String password) throws Exception {
		
		XPlannerRepositoryUtils.removeValidatedRepositoryUrl(serverUrl);
		serverManager.testConnection(serverUrl, user, password);
		XPlannerRepositoryUtils.addValidatedRepositoryUrl(serverUrl);
	}

	private static String getServerHost(TaskRepository repository) {
		try {
			return new URL(repository.getUrl()).getHost();
		} catch (MalformedURLException ex) {
			throw new RuntimeException(Messages.XPlannerServerFacade_INVALID_URL_EXCEPTION+repository.getUrl(), ex);
		}
	}
	
	/**
	 * TODO: refactor
	 */
	public static void handleConnectionException(Exception e) {
		if (e instanceof ServiceUnavailableException) {
			MylarStatusHandler.fail(e, Messages.XPlannerServerFacade_CONNECTION_FAILURE_ERROR
					+ Messages.XPlannerServerFacade_NETWORK_CONNECTION_FAILURE,
					true);
		} 
		else if (e instanceof AuthenticationException) {
			MylarStatusHandler.fail(e, Messages.XPlannerServerFacade_AUTHENTICATION_FAILED
					+ Messages.XPlannerServerFacade_USERNAME_PASSWORD_ERROR, true);
		} 
		else if (e instanceof RuntimeException) {
			MylarStatusHandler.fail(e, Messages.XPlannerServerFacade_NO_REPOSITORY_FOUND
					+ Messages.XPlannerServerFacade_VERIFY_VALID_REPOSITORY, true);
		} 
		else {
			MylarStatusHandler.fail(e, Messages.XPlannerServerFacade_COULD_NOT_CONNECT_TO_REPOSITORY
					+ Messages.XPlannerServerFacade_CHECK_CREDENTIALS, true);
		}
	}	
}
