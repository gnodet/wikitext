/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.core;

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
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.eclipse.mylar.xplanner.core.service.CachedXPlannerServer;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylar.xplanner.core.service.exceptions.ServiceUnavailableException;
import org.xml.sax.SAXException;


/**
 * @author Helen Bershadskaya 
 * @author Ravi Kumar 
 */
public class ServerManager {

	private final File cacheLocation;

	private Map<String, XPlannerServer> serverByName = new HashMap<String, XPlannerServer>();

	// TODO Use a decent listener list
	private List<XPlannerServerListener> listeners = new ArrayList<XPlannerServerListener>();
	
	public ServerManager(File cacheLocation) {
		this.cacheLocation = cacheLocation;
	}
	
	protected void start() {
		// On first load the cache may not exist
		cacheLocation.mkdirs();
		
		File[] servers = this.cacheLocation.listFiles();
		for (int i = 0; i < servers.length; i++) {
			File serverCache = servers[i];
			File serverFile = new File(serverCache, "server.ser"); //$NON-NLS-1$
			
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(serverFile)));
				XPlannerServer server = (XPlannerServer) ois.readObject();
				// TODO reconnect the services depending on user preferences
				
				serverByName.put(serverCache.getName(), server);
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
		for (Iterator<XPlannerServer> iServers = serverByName.values().iterator(); iServers.hasNext();) {
			XPlannerServer server = iServers.next();
			
			ObjectOutputStream oos = null;
			try {
				File cacheDir = new File(cacheLocation, server.getName());
				cacheDir.mkdirs();
				
				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(cacheDir, "server.ser")))); //$NON-NLS-1$
				oos.writeObject(server);
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
	 * Tests the connection to a server.  If the URL is invalid ot the username
	 * and password are invalid this method will return with a exceptions carrying
	 * the failure reason. 
	 * @param baseUrl Base URL of the XPlanner installation
	 * @param username username to connect with
	 * @param password Password to connect with
	 * @return Short string describing the server information
	 * @throws AuthenticationException URL was valid but username and password were incorrect
	 * @throws ServiceUnavailableException URL was not valid
	 */
	public String testConnection(String baseUrl, String username, String password) throws AuthenticationException, ServiceUnavailableException {
		if (baseUrl == null || baseUrl.length() == 0) {
			throw new AuthenticationException(Messages.ServerManager_SERVER_URL_CANNOT_BE_EMPTY);
		}
		
		XPlannerServer server = null;
		
		try {
			new URL(baseUrl).openConnection(); // tests url validity

			server = createServer("Connection Test", baseUrl, false, username, password); //$NON-NLS-1$
			
			server.getTask(-1);
		}
		catch (RemoteException re) {
			if (re instanceof AxisFault) {
				String message = re.getMessage().trim();
				if (message.startsWith("(401)")) { //$NON-NLS-1$
					message = Messages.ServerManager_INVALID_USERNAME_PASSWORD;
				}
				else if (re.getCause() instanceof SAXException) {
					message = ""; //$NON-NLS-1$
				}
				else {
					message = message.startsWith(";") ? message.substring(1).trim() : message; //$NON-NLS-1$
				}
				throw new AuthenticationException(message);
			}
			else {
				throw new ServiceUnavailableException(re.getMessage());
			}
		}
		catch (Exception e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		finally {
			if (server != null) {
				removeServer(server);
			}
		}
		
		return "XPlanner"; //$NON-NLS-1$
	}
	
	public XPlannerServer getServer(String name) {
		return (XPlannerServer) serverByName.get(name);
	}
	
	public XPlannerServer[] getAllServers() {
		return (XPlannerServer[]) serverByName.values().toArray(new XPlannerServer[serverByName.size()]);
	}
	
	public XPlannerServer createServer(String name, String baseUrl, boolean hasSlowConnection, String username, String password) {
		if (baseUrl.charAt(baseUrl.length() - 1) == '/') {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}
		
		XPlannerServer server = new CachedXPlannerServer(name, baseUrl, hasSlowConnection, username, password);
		return server;
	}
	
	public void addServer(XPlannerServer server) {
		if (serverByName.containsKey(server.getName())) {
//			TODO: add this check back once the listeners for server property change are hooked up
//			 Also handle the case when serviceDelegate in the cachedServer is null
//			throw new RuntimeException("A server with that name already exists");
			removeServer(server);
		}
		serverByName.put(server.getName(), server);
		fireServerAddded(server);
	}
	
	public void removeServer(XPlannerServer server) {
		serverByName.remove(server.getName());
		
		File serverCache = new File(this.cacheLocation, server.getName());
		if (serverCache.exists()) {
			recursiveDelete(serverCache);
		}
		fireServerRemoved(server);
	}

	/**
	 * TODO need to make this a bit smarter.  Perhaps have an object to hold connectino info
	 * @param name
	 * @param baseURL
	 * @param username
	 * @param password
	 */
	public void updateServerDetails(String name, String baseURL, boolean hasSlowConnection, String username, String password) {
		CachedXPlannerServer server = (CachedXPlannerServer) serverByName.get(name);
		// TODO we should really have a modify event

		fireServerRemoved(server);
		
		// TODO need to flush the server cache since we are possibly a different person
		server.setBaseURL(baseURL);
		server.setSlowConnection(hasSlowConnection);
		server.setCurrentUserName(username);
		server.setCurrentPassword(password);
		
		fireServerAddded(server);
	}
	
	public void addServerListener(XPlannerServerListener listener) {
		listeners.add(listener);
	}
	
	public void removeServerListener(XPlannerServerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireServerRemoved(XPlannerServer server) {
		for (Iterator<XPlannerServerListener> iListeners = listeners.iterator(); iListeners.hasNext();) {
			XPlannerServerListener listener = (XPlannerServerListener)iListeners.next();
			listener.serverRemoved(server);
		}
	}
	
	private void fireServerAddded(XPlannerServer server) {
		for (Iterator<XPlannerServerListener> iListeners = listeners.iterator(); iListeners.hasNext();) {
			XPlannerServerListener listener = (XPlannerServerListener)iListeners.next();
			listener.serverAdded(server);
		}
	}
	
	private void recursiveDelete(File baseFile) {
		if (baseFile.isFile()) {
			baseFile.delete();
		} else {
			File[] children = baseFile.listFiles();
			for (int i = 0; i < children.length; i++) {
				File file = children[i];
				recursiveDelete(file);
			}
			baseFile.delete();
		}
	}
}
