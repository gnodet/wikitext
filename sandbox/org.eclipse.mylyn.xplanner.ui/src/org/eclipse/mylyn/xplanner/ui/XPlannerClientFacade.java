/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.core.XPlannerClientManager;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylyn.xplanner.core.service.exceptions.ServiceUnavailableException;

/**
 * This class acts as a layer of indirection between clients in this project and the server API implemented by the
 * XPlanner Dashboard, and also abstracts some Mylyn implementation details. It initializes an XPlannerClient object and
 * serves as the central location to get a reference to it.
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 * 
 */
public class XPlannerClientFacade implements ITaskRepositoryListener {

	private XPlannerClientManager clientManager = null;

	private static XPlannerClientFacade instance = null;

	public XPlannerClientFacade() {
		TasksUi.getRepositoryManager().addListener(this);
		clientManager = XPlannerCorePlugin.getDefault().getClientManager();
	}

	/**
	 * Lazily creates client.
	 */
	public XPlannerClient getXPlannerClient(TaskRepository repository) throws CoreException {
		try {
			XPlannerRepositoryUtils.checkRepositoryValidated(repository.getRepositoryUrl());
			String serverHostname = getServerHost(repository);
			XPlannerClient client = clientManager.getClient(serverHostname);
//TODO: add this check back once the listeners for client property change are hooked up
// Also handle the case when serviceDelegate in the cachedClient is null

//			if (client == null) {
			client = clientManager.createClient(serverHostname, repository.getRepositoryUrl(), false,
					repository.getUserName(), repository.getPassword(), false, repository.getProxy(),
					repository.getHttpUser(), repository.getHttpPassword());
			clientManager.addClient(client);
//			}
			if (client == null) {
				throw new ServiceUnavailableException(serverHostname + " " + repository.getRepositoryUrl()); //$NON-NLS-1$
			}
			return client;
		} catch (CoreException ce) {
			throw new CoreException(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_SERVER_CONNECTION_ERROR + ": " + ce.getMessage()));
		} catch (ServiceUnavailableException sue) {
			throw sue;
		} catch (RuntimeException e) {
			StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_SERVER_CONNECTION_ERROR, e));
			throw e;
		}
	}

	public static boolean isInitialized() {
		return instance != null;
	}

	public static XPlannerClientFacade getDefault() {
		if (instance == null) {
			instance = new XPlannerClientFacade();
		}
		return instance;
	}

	public void logOutFromAll() {
		try {
			XPlannerClient[] allClients = clientManager.getAllClients();
			for (XPlannerClient allClient : allClients) {
				allClient.logout();
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public void repositoriesRead() {
		// ignore
	}

	public void repositoryAdded(TaskRepository repository) {
		if (repository.getConnectorKind().equals(XPlannerMylynUIPlugin.REPOSITORY_KIND)) {
			try {
				getXPlannerClient(repository);
			} catch (CoreException e) {
				; // do nothing here -- will get displayed other places where required by repository use
			}
		}
	}

	public void repositoryRemoved(TaskRepository repository) {
		if (repository.getConnectorKind().equals(XPlannerMylynUIPlugin.REPOSITORY_KIND)) {
			String serverHostname = getServerHost(repository);
			XPlannerClient client = clientManager.getClient(serverHostname);
			removeClient(client);
			XPlannerRepositoryUtils.removeValidatedRepositoryUrl(repository.getRepositoryUrl());
		}
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		repositoryRemoved(repository);
		repositoryAdded(repository);
	}

	public void refreshClientSettings(TaskRepository repository) {
		try {
			XPlannerClient client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			if (client != null) {
				client.refreshDetails();
			}
		} catch (final Exception e) {
			String reason = e.getLocalizedMessage();
			if ((reason == null) || (reason.length() == 0)) {
				reason = e.getClass().getName();
			}
			StatusHandler.log(new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, IStatus.ERROR, MessageFormat.format(
					Messages.XPlannerRepositoryConnector_PerformQueryFailure, reason), e));
		}
	}

	private void removeClient(XPlannerClient client) {
		if (client != null) {
			client.logout();
			clientManager.removeClient(client);
		}
	}

	/**
	 * Validate the server URL and user credentials
	 * 
	 * @param serverUrl
	 *            Location of the XPlanner Server
	 * @param user
	 *            Username
	 * @param password
	 *            Password
	 * @param proxy
	 *            Proxy
	 * @param httpUser
	 *            http user name
	 * @param httpPassword
	 *            http password
	 * @return String describing validation failure or null if the details are valid
	 */
	public void validateServerAndCredentials(String serverUrl, String user, String password, Proxy proxy,
			String httpUser, String httpPassword) throws Exception {

		XPlannerRepositoryUtils.removeValidatedRepositoryUrl(serverUrl);
		clientManager.testConnection(serverUrl, user, password, proxy, httpUser, httpPassword);
		XPlannerRepositoryUtils.addValidatedRepositoryUrl(serverUrl);
	}

	private static String getServerHost(TaskRepository repository) {
		try {
			return new URL(repository.getRepositoryUrl()).getHost();
		} catch (MalformedURLException ex) {
			throw new RuntimeException(Messages.XPlannerClientFacade_INVALID_URL_EXCEPTION
					+ repository.getRepositoryUrl(), ex);
		}
	}

	/**
	 * TODO: refactor
	 */
	public static void handleConnectionException(Exception e) {
		if (e instanceof ServiceUnavailableException) {
			StatusHandler.displayStatus("XPlanner", new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_CONNECTION_FAILURE_ERROR
							+ Messages.XPlannerClientFacade_NETWORK_CONNECTION_FAILURE, e));
		} else if (e instanceof AuthenticationException) {
			StatusHandler.displayStatus("XPlanner", new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_AUTHENTICATION_FAILED
							+ Messages.XPlannerClientFacade_USERNAME_PASSWORD_ERROR, e));
		} else if (e instanceof RuntimeException) {
			StatusHandler.displayStatus("XPlanner", new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_NO_REPOSITORY_FOUND
							+ Messages.XPlannerClientFacade_VERIFY_VALID_REPOSITORY, e));
		} else {
			StatusHandler.displayStatus("XPlanner", new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					Messages.XPlannerClientFacade_COULD_NOT_CONNECT_TO_REPOSITORY
							+ Messages.XPlannerClientFacade_CHECK_CREDENTIALS, e));
		}
	}
}
