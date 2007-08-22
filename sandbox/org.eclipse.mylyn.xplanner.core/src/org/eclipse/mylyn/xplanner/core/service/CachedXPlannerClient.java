/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core.service;

import java.io.Serializable;
import java.net.Proxy;
import java.rmi.RemoteException;
import java.util.*;

import org.eclipse.mylyn.xplanner.wsdl.db.QueryException;
import org.xplanner.soap.*;


/**
 * XPlanner client implementation that caches information that is unlikely to change
 * during the session.  This client could be persisted to disk and re-loaded.
 * It has lifecycle methods to allow data in the cache to be reloaded.
 * 
 * TODO it is assumed that it will be backed by a standard XPlanner service layer 
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class CachedXPlannerClient extends XPlannerClient 
	implements Serializable {
	
  public static final int INVALID_ID = -1;
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String baseURL;
	private boolean hasSlowConnection;
	private String userName;
	private String password;
	private final boolean useCompression;
	private final transient Proxy proxy;
	private final String httpUser;
	private final String httpPassword;
	private transient XPlannerService serviceDelegate;

	public CachedXPlannerClient(String name, String baseURL, boolean hasSlowConnection, String userName, String password,
			boolean useCompression, Proxy proxy, String httpUser, String httpPassword) {
		this.name = name;
		this.baseURL = baseURL;
		this.hasSlowConnection = hasSlowConnection;
		this.userName = userName;
		this.password = password;
		this.useCompression = useCompression;
		this.proxy = proxy;
		this.httpUser = httpUser;
		this.httpPassword = httpPassword;

		
		this.serviceDelegate = ServiceManager.getXPlannerService(this);
		serviceDelegate.login(userName, password);
	}

	public String getBaseURL() {
		return baseURL;
	}

	public String getCurrentUserName() {
		return userName;
	}

	public String getCurrentUserPassword() {
		return password;
	}

	public int getMaximumNumberOfMatches() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public boolean hasSlowConnection() {
		return hasSlowConnection;
	}


	public void setSlowConnection(boolean hasSlowConnection) {
		this.hasSlowConnection = hasSlowConnection;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setCurrentPassword(String password) {
		this.password = password;
	}

	public void setCurrentUserName(String userName) {
		this.userName = userName;
	}
	
	public String login(String username, String password) {
		return serviceDelegate.login(username, password);		
	}

	public boolean logout() {
		boolean ok = true;
	
		//TODO -- shouldn't have a null service delegate, but definitely get into this condition if "finish"
		// repository definition with an invalid client
	
		if (serviceDelegate != null) {
			ok = serviceDelegate.logout();
		}
		
		return ok;
	}

	public IterationData addIteration(IterationData iteration) throws RemoteException {
		return serviceDelegate.addIteration(iteration);
	}

	public NoteData addNote(NoteData note) throws RemoteException {
		return serviceDelegate.addNote(note);
	}

	public PersonData addPerson(PersonData object) throws RemoteException {
		return serviceDelegate.addPerson(object);
	}

	public ProjectData addProject(ProjectData project) throws RemoteException {
		return serviceDelegate.addProject(project);
	}

	public TaskData addTask(TaskData task) throws RemoteException {
		return serviceDelegate.addTask(task);
	}

	public TimeEntryData addTimeEntry(TimeEntryData timeEntry) throws RemoteException {
		return serviceDelegate.addTimeEntry(timeEntry);
	}

	public UserStoryData addUserStory(UserStoryData story) throws RemoteException {
		return serviceDelegate.addUserStory(story);
	}

	public void deleteAttribute(int objectId, String key) throws RemoteException {
		serviceDelegate.deleteAttribute(objectId, key);
	}

	public boolean equals(Object obj) {
		return this == obj || serviceDelegate.equals(obj) || 
		 (obj instanceof CachedXPlannerClient && ((CachedXPlannerClient)obj).serviceDelegate.equals(serviceDelegate));
	}

	public String getAttribute(int objectId, String key) throws RemoteException {
		return serviceDelegate.getAttribute(objectId, key);
	}

	@SuppressWarnings("unchecked")
	public HashMap getAttributes(int objectId) throws RemoteException {
		return serviceDelegate.getAttributes(objectId);
	}

	@SuppressWarnings("unchecked")
	public HashMap getAttributesWithPrefix(int objectId, String prefix) throws RemoteException {
		return serviceDelegate.getAttributesWithPrefix(objectId, prefix);
	}

	public IterationData getCurrentIteration(int projectId) throws RemoteException {
		return serviceDelegate.getCurrentIteration(projectId);
	}

	public TaskData[] getCurrentTasksForPerson(int personId) throws RemoteException, QueryException {
		return serviceDelegate.getCurrentTasksForPerson(personId);
	}

	public IterationData getIteration(int id) throws RemoteException {
		return serviceDelegate.getIteration(id);
	}

	public IterationData[] getIterations(int projectId) throws RemoteException {
		return serviceDelegate.getIterations(projectId);
	}

	public NoteData getNote(int id) throws RemoteException {
		return serviceDelegate.getNote(id);
	}

	public NoteData[] getNotesForObject(int attachedToId) throws RemoteException {
		return serviceDelegate.getNotesForObject(attachedToId);
	}

	public PersonData[] getPeople() throws RemoteException {
		return serviceDelegate.getPeople();
	}

	public PersonData getPerson(int id) throws RemoteException {
		return serviceDelegate.getPerson(id);
	}

	public TaskData[] getPlannedTasksForPerson(int personId) throws RemoteException, QueryException {
		return serviceDelegate.getPlannedTasksForPerson(personId);
	}

	public ProjectData getProject(int id) throws RemoteException {
		return serviceDelegate.getProject(id);
	}

	public ProjectData[] getProjects() throws RemoteException {
		return serviceDelegate.getProjects();
	}

	public TaskData getTask(int id) throws RemoteException {
		if (serviceDelegate == null) {
			System.out.println(" why is serivce Delegate null???"); //$NON-NLS-1$
		}
		return serviceDelegate.getTask(id);
	}

	public TaskData[] getTasks(int containerId) throws RemoteException {
		return serviceDelegate.getTasks(containerId);
	}

	public TimeEntryData[] getTimeEntries(int containerId) throws RemoteException {
		return serviceDelegate.getTimeEntries(containerId);
	}

	public TimeEntryData getTimeEntry(int id) throws RemoteException {
		return serviceDelegate.getTimeEntry(id);
	}

	public UserStoryData[] getUserStories(int containerId) throws RemoteException {
		return serviceDelegate.getUserStories(containerId);
	}

	public UserStoryData getUserStory(int id) throws RemoteException {
		return serviceDelegate.getUserStory(id);
	}

	public int hashCode() {
		return serviceDelegate.hashCode();
	}

	public void removeIteration(int id) throws RemoteException {
		serviceDelegate.removeIteration(id);
	}

	public void removeNote(int id) throws RemoteException {
		serviceDelegate.removeNote(id);
	}

	public void removePerson(int id) throws RemoteException {
		serviceDelegate.removePerson(id);
	}

	public void removeProject(int id) throws RemoteException {
		serviceDelegate.removeProject(id);
	}

	public void removeTask(int id) throws RemoteException {
		serviceDelegate.removeTask(id);
	}

	public void removeTimeEntry(int id) throws RemoteException {
		serviceDelegate.removeTimeEntry(id);
	}

	public void removeUserStory(int id) throws RemoteException {
		serviceDelegate.removeUserStory(id);
	}

	public void setAttribute(int objectId, String key, String value) throws RemoteException {
		serviceDelegate.setAttribute(objectId, key, value);
	}

	public void update(IterationData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public void update(NoteData note) throws RemoteException {
		serviceDelegate.update(note);
	}

	public void update(PersonData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public void update(ProjectData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public void update(TaskData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public void update(TimeEntryData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public void update(UserStoryData object) throws RemoteException {
		serviceDelegate.update(object);
	}

	public TaskData[] getUserStoryTasksForPerson(int personId, int userStoryId) {
		if (personId < 0 || userStoryId < 0) {
			return new TaskData[0];
		}
		
		List<TaskData> userStoryTasksForPerson = new ArrayList<TaskData>();
		try {
			TaskData[] iterationTasks = getTasks(userStoryId);
			// get all tasks for specified user storyserv
			for (int i = 0; i < iterationTasks.length; i++) {
				TaskData taskData = iterationTasks[i];
				if (taskData.getAcceptorId() == personId) {
					userStoryTasksForPerson.add(taskData);
				}
			}
			
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return userStoryTasksForPerson.toArray(new TaskData[userStoryTasksForPerson.size()]);
	}
	
	public UserStoryData[] getIterationUserStoriesForTracker(int trackerId, int iterationId) {
		if (trackerId < 0 || iterationId < 0) {
			return new UserStoryData[0];
		}

		List<UserStoryData> userStoriesForTracker = new ArrayList<UserStoryData>();
		UserStoryData[] userStories;
		try {
			userStories = getUserStories(iterationId);
			// get all tasks for specified user story
			for (int i = 0; i < userStories.length; i++) {
				UserStoryData userStory = userStories[i];
				if (userStory.getTrackerId() == trackerId) {
					userStoriesForTracker.add(userStory);
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return userStoriesForTracker.toArray(new UserStoryData[userStoriesForTracker.size()]);
	}

	public int getCurrentPersonId() {
		int currentPersonId = INVALID_ID;
		
	  String userName = getCurrentUserName();
	  if (userName != null) {
	  	try {
				PersonData[] people = getPeople();
				if (people != null) {
					for (int i = 0; i < people.length && currentPersonId == INVALID_ID; i++) {
						PersonData person = people[i];
						if (person.getUserId().equals(userName)) {
							currentPersonId = person.getId();
						}
					}
				}
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
	  }
	  
	  return currentPersonId;
	}
	
	public UserStoryData[] getUserStoriesForProject(int projectId) {
		if (projectId == INVALID_ID) {
			return new UserStoryData[0];
		}
		
		ArrayList<UserStoryData> projectUserStories = new ArrayList<UserStoryData>();
		try {
			IterationData[] projectIterations = getIterations(projectId);
			for (IterationData iteration : projectIterations) {
				UserStoryData[] iterationUserStories = getUserStories(iteration.getId());
				if (iterationUserStories.length > 0) {
					projectUserStories.addAll(Arrays.asList(iterationUserStories));
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return projectUserStories.toArray(new UserStoryData[projectUserStories.size()]);
	}
	
	public UserStoryData[] getUserStoriesForProject(int projectId, int trackerId) {
		if (projectId == INVALID_ID) {
			return new UserStoryData[0];
		}
		
		ArrayList<UserStoryData> projectTrackerUserStories = new ArrayList<UserStoryData>();
		UserStoryData[] projectUserStories = getUserStoriesForProject(projectId);
		for (UserStoryData userStory : projectUserStories) {
			if (userStory.getTrackerId() == trackerId) {
				projectTrackerUserStories.add(userStory);
			}
		}
		
		return projectTrackerUserStories.toArray(new UserStoryData[projectTrackerUserStories.size()]);

	}
	
	public TaskData[] getTasksForProject(int projectId) {
		if (projectId == -1) {
			return new TaskData[0];
		}
		
		ArrayList<TaskData> projectTasks = new ArrayList<TaskData>();
		try {
			UserStoryData[] userStories = getUserStoriesForProject(projectId);
			for (UserStoryData userStory : userStories) {
				TaskData[] userStoryTasks = getTasks(userStory.getId());
				if (userStoryTasks.length > 0) {
					projectTasks.addAll(Arrays.asList(userStoryTasks));
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return projectTasks.toArray(new TaskData[projectTasks.size()]);
	}
	
	public TaskData[] getTasksForProject(int projectId, int personId) {
		if (projectId == INVALID_ID || personId == INVALID_ID) {
			return new TaskData[0];
		}
		
		ArrayList<TaskData> projectPersonTasks = new ArrayList<TaskData>();
		TaskData[] allProjectTasks = getTasksForProject(projectId);
		for (TaskData task : allProjectTasks) {
			if (task.getAcceptorId() == personId) {
				projectPersonTasks.add(task);
			}
		}
		
		return projectPersonTasks.toArray(new TaskData[projectPersonTasks.size()]);
	}
	
	public TaskData[] getTasksForIteration(int iterationId) {
		if (iterationId == INVALID_ID) {
			return new TaskData[0];
		}
		
		ArrayList<TaskData> iterationTasks = new ArrayList<TaskData>();
		try {
			UserStoryData[] userStories = getUserStories(iterationId);
			for (UserStoryData userStory : userStories) {
				TaskData[] userStoryTasks = getTasks(userStory.getId());
				if (userStoryTasks.length > 0) {
					iterationTasks.addAll(Arrays.asList(userStoryTasks));
				}
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return iterationTasks.toArray(new TaskData[iterationTasks.size()]);
	}
	
	public TaskData[] getTasksForIteration(int iterationId, int personId) {
		if (iterationId == INVALID_ID || personId == INVALID_ID) {
			return new TaskData[0];
		}
		
		ArrayList<TaskData> iterationPersonTasks = new ArrayList<TaskData>();
		TaskData[] allIterationTasks = getTasksForIteration(iterationId);
		for (TaskData task : allIterationTasks) {
			if (task.getAcceptorId() == personId) {
				iterationPersonTasks.add(task);
			}
		}
		
		return iterationPersonTasks.toArray(new TaskData[iterationPersonTasks.size()]);
	}
	

	public String getHttpPassword() {
		return httpPassword;
	}

	public String getHttpUser() {
		return httpUser;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public boolean useCompression() {
		return useCompression;
	}

}
