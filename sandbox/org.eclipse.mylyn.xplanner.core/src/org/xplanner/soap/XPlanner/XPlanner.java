/**
 * XPlanner.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap.XPlanner;

@SuppressWarnings("unchecked")
public interface XPlanner extends java.rmi.Remote {
	public java.util.HashMap getAttributes(int objectId) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.ProjectData object) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.TimeEntryData object) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.TaskData object) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.IterationData object) throws java.rmi.RemoteException;

	public void update(org.xplanner.soap.UserStoryData object) throws java.rmi.RemoteException;

	public java.lang.String getAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException;

	public void setAttribute(int objectId, java.lang.String key, java.lang.String value)
			throws java.rmi.RemoteException;

	public org.xplanner.soap.NoteData getNote(int id) throws java.rmi.RemoteException;

	public void removeNote(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.PersonData getPerson(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.UserStoryData[] getUserStories(int containerId) throws java.rmi.RemoteException;

	public org.xplanner.soap.TaskData[] getTasks(int containerId) throws java.rmi.RemoteException;

	public org.xplanner.soap.TimeEntryData[] getTimeEntries(int containerId) throws java.rmi.RemoteException;

	public org.xplanner.soap.TaskData addTask(org.xplanner.soap.TaskData task) throws java.rmi.RemoteException;

	public org.xplanner.soap.IterationData[] getIterations(int projectId) throws java.rmi.RemoteException;

	public org.xplanner.soap.IterationData getCurrentIteration(int projectId) throws java.rmi.RemoteException;

	public org.xplanner.soap.PersonData[] getPeople() throws java.rmi.RemoteException;

	public org.xplanner.soap.ProjectData getProject(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.UserStoryData getUserStory(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.IterationData getIteration(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.TaskData getTask(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.ProjectData[] getProjects() throws java.rmi.RemoteException;

	public org.xplanner.soap.ProjectData addProject(org.xplanner.soap.ProjectData project)
			throws java.rmi.RemoteException;

	public void removeProject(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.IterationData addIteration(org.xplanner.soap.IterationData iteration)
			throws java.rmi.RemoteException;

	public void removeIteration(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.UserStoryData addUserStory(org.xplanner.soap.UserStoryData story)
			throws java.rmi.RemoteException;

	public void removeUserStory(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.TaskData[] getCurrentTasksForPerson(int personId) throws java.rmi.RemoteException;

	public org.xplanner.soap.TaskData[] getPlannedTasksForPerson(int personId) throws java.rmi.RemoteException;

	public void removeTask(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.TimeEntryData getTimeEntry(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.TimeEntryData addTimeEntry(org.xplanner.soap.TimeEntryData timeEntry)
			throws java.rmi.RemoteException;

	public void removeTimeEntry(int id) throws java.rmi.RemoteException;

	public org.xplanner.soap.NoteData addNote(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException;

	public org.xplanner.soap.NoteData[] getNotesForObject(int attachedToId) throws java.rmi.RemoteException;

	public org.xplanner.soap.PersonData addPerson(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException;

	public void removePerson(int id) throws java.rmi.RemoteException;

	public void deleteAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException;

	public java.util.HashMap getAttributesWithPrefix(int objectId, java.lang.String prefix)
			throws java.rmi.RemoteException;
}
