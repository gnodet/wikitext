/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.core.service.soap;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

import org.xplanner.soap.IterationData;
import org.xplanner.soap.NoteData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.TimeEntryData;
import org.xplanner.soap.UserStoryData;
import org.xplanner.soap.XPlanner.XPlanner;
import org.xplanner.soap.XPlanner.XPlannerServiceLocator;
import org.apache.axis.client.Stub;
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.core.service.XPlannerService;
import org.eclipse.mylar.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylar.xplanner.core.service.exceptions.ServiceUnavailableException;
import org.eclipse.mylar.xplanner.wsdl.db.QueryException;


// This class does not represent the data in a XPlanner installation.  It is merely
// a helper to get any data that is missing in the cached XPlannerInstallation object
// TODO do we want the ability to have a non cached XPlannerInstallation?  Might be good
// if they had an RMI interface
// Make XPlannerInstallation an interface, implement a CachedInstallation which requires
// a concrete installation.  The cached one can then forward on any requests it has
// not yet cached.  Also need the ability to flush and fully re-load the cached installation

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class SoapXPlannerService extends XPlannerService {

	private static final String SOAP_URL_PREFIX = "/soap/XPlanner"; //$NON-NLS-1$
	private XPlannerServer server;
	private XPlanner service;
	boolean loginActive;

	public SoapXPlannerService(XPlannerServer aServer) {
		this.server = aServer;
		
		try {
			XPlannerServiceLocator s = new XPlannerServiceLocator();
			service = s.getXPlanner(new URL(server.getBaseURL() + SOAP_URL_PREFIX));
			login(server.getCurrentUserName(), server.getCurrentUserPassword());
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String login(String username, String password) throws AuthenticationException, ServiceUnavailableException {
		 ((Stub) service).setUsername(username);
	     ((Stub) service).setPassword(password);
	     loginActive = true;
	     return null;
	}

	public boolean logout() throws ServiceUnavailableException {
		// TODO Auto-generated method stub
        loginActive = false;
		return true;
	}

	public IterationData addIteration(IterationData iteration) throws RemoteException {
		return service.addIteration(iteration);
	}

	public NoteData addNote(NoteData note) throws RemoteException {
		return service.addNote(note);
	}

	public PersonData addPerson(PersonData object) throws RemoteException {
		return service.addPerson(object);
	}

	public ProjectData addProject(ProjectData project) throws RemoteException {
		return service.addProject(project);
	}

	public TaskData addTask(TaskData task) throws RemoteException {
		return service.addTask(task);
	}

	public TimeEntryData addTimeEntry(TimeEntryData timeEntry) throws RemoteException {
		return service.addTimeEntry(timeEntry);
	}

	public UserStoryData addUserStory(UserStoryData story) throws RemoteException {
		return service.addUserStory(story);
	}

	public void deleteAttribute(int objectId, String key) throws RemoteException {
		service.deleteAttribute(objectId, key);
	}

	public String getAttribute(int objectId, String key) throws RemoteException {
		return service.getAttribute(objectId, key);
	}

	@SuppressWarnings("unchecked")
	public HashMap getAttributes(int objectId) throws RemoteException {
		return service.getAttributes(objectId);
	}

	@SuppressWarnings("unchecked")
	public HashMap getAttributesWithPrefix(int objectId, String prefix) throws RemoteException {
		return service.getAttributesWithPrefix(objectId, prefix);
	}

	public IterationData getCurrentIteration(int projectId) throws RemoteException {
		return service.getCurrentIteration(projectId);
	}

	public TaskData[] getCurrentTasksForPerson(int personId) throws RemoteException, QueryException {
		return service.getCurrentTasksForPerson(personId);
	}

	public IterationData getIteration(int id) throws RemoteException {
		return service.getIteration(id);
	}

	public IterationData[] getIterations(int projectId) throws RemoteException {
		return service.getIterations(projectId);
	}

	public NoteData getNote(int id) throws RemoteException {
		return service.getNote(id);
	}

	public NoteData[] getNotesForObject(int attachedToId) throws RemoteException {
		return service.getNotesForObject(attachedToId);
	}

	public PersonData[] getPeople() throws RemoteException {
		return service.getPeople();
	}

	public PersonData getPerson(int id) throws RemoteException {
		return service.getPerson(id);
	}

	public TaskData[] getPlannedTasksForPerson(int personId) throws RemoteException, QueryException {
		return service.getPlannedTasksForPerson(personId);
	}

	public ProjectData getProject(int id) throws RemoteException {
		return service.getProject(id);
	}

	public ProjectData[] getProjects() throws RemoteException {
		return service.getProjects();
	}

	public TaskData getTask(int id) throws RemoteException {
		return service.getTask(id);
	}

	public TaskData[] getTasks(int containerId) throws RemoteException {
		return service.getTasks(containerId);
	}

	public TimeEntryData[] getTimeEntries(int containerId) throws RemoteException {
		return service.getTimeEntries(containerId);
	}

	public TimeEntryData getTimeEntry(int id) throws RemoteException {
		return service.getTimeEntry(id);
	}

	public UserStoryData[] getUserStories(int containerId) throws RemoteException {
		return service.getUserStories(containerId);
	}

	public UserStoryData getUserStory(int id) throws RemoteException {
		return service.getUserStory(id);
	}

	public void removeIteration(int id) throws RemoteException {
		service.removeIteration(id);
	}

	public void removeNote(int id) throws RemoteException {
		service.removeNote(id);
	}

	public void removePerson(int id) throws RemoteException {
		service.removePerson(id);
	}

	public void removeProject(int id) throws RemoteException {
		service.removeProject(id);
	}

	public void removeTask(int id) throws RemoteException {
		service.removeTask(id);
	}

	public void removeTimeEntry(int id) throws RemoteException {
		service.removeTimeEntry(id);
	}

	public void removeUserStory(int id) throws RemoteException {
		service.removeUserStory(id);
	}

	public void setAttribute(int objectId, String key, String value) throws RemoteException {
		service.setAttribute(objectId, key, value);
	}

	public void update(IterationData object) throws RemoteException {
		service.update(object);
	}

	public void update(NoteData note) throws RemoteException {
		service.update(note);
	}

	public void update(PersonData object) throws RemoteException {
		service.update(object);
	}

	public void update(ProjectData object) throws RemoteException {
		service.update(object);
	}

	public void update(TaskData object) throws RemoteException {
		service.update(object);
	}

	public void update(TimeEntryData object) throws RemoteException {
		service.update(object);
	}

	public void update(UserStoryData object) throws RemoteException {
		service.update(object);
	}

}