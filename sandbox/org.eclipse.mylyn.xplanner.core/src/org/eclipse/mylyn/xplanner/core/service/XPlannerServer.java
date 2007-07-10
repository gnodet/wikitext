/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core.service;

import java.net.Proxy;

import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * TODO this class needs to be populated using the SOAP or JAX-RPC interfaces.
 * Once this is done it should be cached on disk somewhere so we don't have to
 * query the server each time a client loads.  It should be possible to reload
 * and restore the cache information.  We also need to store the images in a
 * cache somewhere since we will ue them a lot.
 * 
 * TODO explain this is an attempt to enrich the XPlanner service layer
 * 
 * TODO move all of the assignee stuff somewhere else.
 * 
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */

public abstract class XPlannerServer extends XPlannerService {
	public static final int NO_LIMIT = -1;
	
	
	/**
	 * Assign to the default user
	 */
	public static final int ASSIGNEE_DEFAULT = 1;
	
	/**
	 * Leave the assignee field as is (this does not apply when performing an assign to action)
	 */
	public static final int ASSIGNEE_CURRENT = 2;
	
	/**
	 * Assign to nobody
	 */
	public static final int ASSIGNEE_NONE = 3;
	
	/**
	 * Assign to a specific user.  To get the name of the assignee call {@link #getAssignee()}
	 */
	public static final int ASSIGNEE_USER = 4;
	
	/**
	 * Assign to the current user
	 */
	public static final int ASSIGNEE_SELF = 5;
	
	public abstract boolean hasSlowConnection();
	public abstract String getName();
	public abstract String getBaseURL();
	public abstract String getCurrentUserName();
	public abstract String getCurrentUserPassword();
	public abstract int getCurrentPersonId();
	public abstract TaskData[] getUserStoryTasksForPerson(int personId, int userStoryId);
	public abstract UserStoryData[] getIterationUserStoriesForTracker(int trackerId, int iterationId);
	public abstract UserStoryData[] getUserStoriesForProject(int projectId);
	public abstract UserStoryData[] getUserStoriesForProject(int projectId, int trackerId);
	public abstract TaskData[] getTasksForProject(int projectId);
	public abstract TaskData[] getTasksForProject(int projectId, int personId);
	public abstract TaskData[] getTasksForIteration(int iterationId);
	public abstract TaskData[] getTasksForIteration(int iterationId, int personId);
	public abstract boolean useCompression();
	public abstract Proxy getProxy();
	public abstract String getHttpUser();
	public abstract String getHttpPassword();
	
//	/**
//	 * Force a login to the remote repository.
//	 * @deprecated There is no need to call this method as all services should automatically
//	 * login when the session is about to expire.  If you need to check if the credentials
//	 * are valid, call {@link org.eclipse.mylyn.xplanner.core.ServerManager#testConnection(String, String, String)}
//	 */
//	public abstract void login();
//	
//	/**
//	 * Force the current session to be closed.  This method should only be called during
//	 * application shutdown and then only out of courtesy to the server.  XPlanner will
//	 * automatically expire sessions after a set amount of time.
//	 */
//	public abstract void logout();
//	
}
