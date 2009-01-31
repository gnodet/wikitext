/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.core;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.eclipse.mylyn.internal.xplanner.core.service.CachedXPlannerClient;
import org.eclipse.mylyn.internal.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.internal.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylyn.internal.xplanner.core.service.exceptions.ServiceUnavailableException;
import org.xml.sax.SAXException;

/**
 * @author Helen Bershadskaya
 * @author Ravi Kumar
 */
public class XPlannerClientManager {

	private final File cacheLocation;

	private final Map<String, XPlannerClient> clientByName = new HashMap<String, XPlannerClient>();

	// TODO Use a decent listener list
	private final List<XPlannerClientListener> listeners = new ArrayList<XPlannerClientListener>();

	public XPlannerClientManager(File cacheLocation) {
		this.cacheLocation = cacheLocation;
	}

	protected void start() {
		// On first load the cache may not exist
		cacheLocation.mkdirs();

		File[] clients = this.cacheLocation.listFiles();
		for (File clientCache : clients) {
			File clientFile = new File(clientCache, "server.ser"); //$NON-NLS-1$

			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(clientFile)));
				XPlannerClient client = (XPlannerClient) ois.readObject();
				// TODO reconnect the services depending on user preferences

				clientByName.put(clientCache.getName(), client);
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	protected void stop() {
		for (XPlannerClient client : clientByName.values()) {
			ObjectOutputStream oos = null;
			try {
				File cacheDir = new File(cacheLocation, client.getName());
				cacheDir.mkdirs();

				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(cacheDir,
						"server.ser")))); //$NON-NLS-1$
				oos.writeObject(client);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	/**
	 * Tests the connection to a client. If the URL is invalid ot the username and password are invalid this method will
	 * return with a exceptions carrying the failure reason.
	 * 
	 * @param baseUrl
	 *            Base URL of the XPlanner installation
	 * @param username
	 *            username to connect with
	 * @param password
	 *            Password to connect with
	 * @return Short string describing the client information
	 * @throws AuthenticationException
	 *             URL was valid but username and password were incorrect
	 * @throws ServiceUnavailableException
	 *             URL was not valid
	 */
	public String testConnection(String baseUrl, String username, String password, Proxy proxy, String httpUser,
			String httpPassword) throws AuthenticationException, ServiceUnavailableException {
		if (baseUrl == null || baseUrl.length() == 0) {
			throw new AuthenticationException(Messages.ClientManager_CLIENT_URL_CANNOT_BE_EMPTY);
		}

		XPlannerClient client = null;

		try {
			new URL(baseUrl).openConnection(); // tests url validity

			client = createClient("Connection Test", baseUrl, false, username, password, //$NON-NLS-1$
					false, proxy, httpUser, httpPassword);

			client.getTask(-1);
		} catch (RemoteException re) {
			if (re instanceof AxisFault) {
				String message = re.getMessage().trim();
				if (message.startsWith("(401)")) { //$NON-NLS-1$
					message = Messages.ClientManager_INVALID_USERNAME_PASSWORD;
				} else if (re.getCause() instanceof SocketException) {
					throw new ServiceUnavailableException(re.getCause().getMessage());
				} else if (re.getCause() instanceof SAXException) {
					message = ""; //$NON-NLS-1$
				} else {
					if (re.getCause() != null) {
						message = re.getCause().getMessage();
					}
					message = message.startsWith(";") ? message.substring(1).trim() : message; //$NON-NLS-1$
				}
				throw new AuthenticationException(message);
			} else {
				throw new ServiceUnavailableException(re.getMessage());
			}
		} catch (Exception e) {
			throw new ServiceUnavailableException(e.getMessage());
		} finally {
			if (client != null) {
				removeClient(client);
			}
		}

		return "XPlanner"; //$NON-NLS-1$
	}

	public XPlannerClient getClient(String name) {
		return clientByName.get(name);
	}

	public XPlannerClient[] getAllClients() {
		return clientByName.values().toArray(new XPlannerClient[clientByName.size()]);
	}

	public XPlannerClient createClient(String name, String baseUrl, boolean hasSlowConnection, String username,
			String password, boolean useCompression, Proxy proxy, String httpUser, String httpPassword) {
		if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}

		XPlannerClient client = new CachedXPlannerClient(name, baseUrl, hasSlowConnection, username, password,
				useCompression, proxy, httpUser, httpPassword);
		return client;
	}

	public void addClient(XPlannerClient client) {
		if (clientByName.containsKey(client.getName())) {
//			TODO: add this check back once the listeners for client property change are hooked up
//			 Also handle the case when serviceDelegate in the cachedServer is null
//			throw new RuntimeException("A server with that name already exists");
			removeClient(client);
		}
		clientByName.put(client.getName(), client);
		fireClientAddded(client);
	}

	public void removeClient(XPlannerClient client) {
		clientByName.remove(client.getName());

		File clientCache = new File(this.cacheLocation, client.getName());
		if (clientCache.exists()) {
			recursiveDelete(clientCache);
		}
		fireClientRemoved(client);
	}

	/**
	 * TODO need to make this a bit smarter. Perhaps have an object to hold connectino info
	 * 
	 * @param name
	 * @param baseURL
	 * @param username
	 * @param password
	 */
	public void updateClientDetails(String name, String baseURL, boolean hasSlowConnection, String username,
			String password) {
		CachedXPlannerClient client = (CachedXPlannerClient) clientByName.get(name);
		// TODO we should really have a modify event

		fireClientRemoved(client);

		// TODO need to flush the client cache since we are possibly a different person
		client.setBaseURL(baseURL);
		client.setSlowConnection(hasSlowConnection);
		client.setCurrentUserName(username);
		client.setCurrentPassword(password);

		fireClientAddded(client);
	}

	public void addClientListener(XPlannerClientListener listener) {
		listeners.add(listener);
	}

	public void removeClientListener(XPlannerClientListener listener) {
		listeners.remove(listener);
	}

	private void fireClientRemoved(XPlannerClient client) {
		for (XPlannerClientListener listener : listeners) {
			listener.clientRemoved(client);
		}
	}

	private void fireClientAddded(XPlannerClient client) {
		for (XPlannerClientListener listener : listeners) {
			listener.clientAdded(client);
		}
	}

	private void recursiveDelete(File baseFile) {
		if (baseFile.isFile()) {
			baseFile.delete();
		} else {
			File[] children = baseFile.listFiles();
			for (File file : children) {
				recursiveDelete(file);
			}
			baseFile.delete();
		}
	}
}
