package org.xplanner.soap.XPlanner;

@SuppressWarnings("unchecked")
public class XPlannerProxy implements org.xplanner.soap.XPlanner.XPlanner {
  private String _endpoint = null;
  private org.xplanner.soap.XPlanner.XPlanner xPlanner = null;
  
  public XPlannerProxy() {
    _initXPlannerProxy();
  }
  
  private void _initXPlannerProxy() {
    try {
      xPlanner = (new org.xplanner.soap.XPlanner.XPlannerServiceLocator()).getXPlanner();
      if (xPlanner != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)xPlanner)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint); //$NON-NLS-1$
        else
          _endpoint = (String)((javax.xml.rpc.Stub)xPlanner)._getProperty("javax.xml.rpc.service.endpoint.address"); //$NON-NLS-1$
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (xPlanner != null)
      ((javax.xml.rpc.Stub)xPlanner)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint); //$NON-NLS-1$
    
  }
  
  public org.xplanner.soap.XPlanner.XPlanner getXPlanner() {
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner;
  }
  
  public java.util.HashMap getAttributes(int objectId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getAttributes(objectId);
  }
  
  public void update(org.xplanner.soap.ProjectData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public void update(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public void update(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(note);
  }
  
  public void update(org.xplanner.soap.TimeEntryData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public void update(org.xplanner.soap.TaskData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public void update(org.xplanner.soap.IterationData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public void update(org.xplanner.soap.UserStoryData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.update(object);
  }
  
  public java.lang.String getAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getAttribute(objectId, key);
  }
  
  public void setAttribute(int objectId, java.lang.String key, java.lang.String value) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.setAttribute(objectId, key, value);
  }
  
  public org.xplanner.soap.NoteData getNote(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getNote(id);
  }
  
  public void removeNote(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeNote(id);
  }
  
  public org.xplanner.soap.PersonData getPerson(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getPerson(id);
  }
  
  public org.xplanner.soap.UserStoryData[] getUserStories(int containerId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getUserStories(containerId);
  }
  
  public org.xplanner.soap.TaskData[] getTasks(int containerId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getTasks(containerId);
  }
  
  public org.xplanner.soap.TimeEntryData[] getTimeEntries(int containerId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getTimeEntries(containerId);
  }
  
  public org.xplanner.soap.TaskData addTask(org.xplanner.soap.TaskData task) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addTask(task);
  }
  
  public org.xplanner.soap.IterationData[] getIterations(int projectId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getIterations(projectId);
  }
  
  public org.xplanner.soap.IterationData getCurrentIteration(int projectId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getCurrentIteration(projectId);
  }
  
  public org.xplanner.soap.PersonData[] getPeople() throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getPeople();
  }
  
  public org.xplanner.soap.ProjectData getProject(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getProject(id);
  }
  
  public org.xplanner.soap.UserStoryData getUserStory(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getUserStory(id);
  }
  
  public org.xplanner.soap.IterationData getIteration(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getIteration(id);
  }
  
  public org.xplanner.soap.TaskData getTask(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getTask(id);
  }
  
  public org.xplanner.soap.ProjectData[] getProjects() throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getProjects();
  }
  
  public org.xplanner.soap.ProjectData addProject(org.xplanner.soap.ProjectData project) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addProject(project);
  }
  
  public void removeProject(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeProject(id);
  }
  
  public org.xplanner.soap.IterationData addIteration(org.xplanner.soap.IterationData iteration) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addIteration(iteration);
  }
  
  public void removeIteration(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeIteration(id);
  }
  
  public org.xplanner.soap.UserStoryData addUserStory(org.xplanner.soap.UserStoryData story) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addUserStory(story);
  }
  
  public void removeUserStory(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeUserStory(id);
  }
  
  public org.xplanner.soap.TaskData[] getCurrentTasksForPerson(int personId) throws java.rmi.RemoteException, org.eclipse.mylyn.xplanner.wsdl.db.QueryException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getCurrentTasksForPerson(personId);
  }
  
  public org.xplanner.soap.TaskData[] getPlannedTasksForPerson(int personId) throws java.rmi.RemoteException, org.eclipse.mylyn.xplanner.wsdl.db.QueryException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getPlannedTasksForPerson(personId);
  }
  
  public void removeTask(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeTask(id);
  }
  
  public org.xplanner.soap.TimeEntryData getTimeEntry(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getTimeEntry(id);
  }
  
  public org.xplanner.soap.TimeEntryData addTimeEntry(org.xplanner.soap.TimeEntryData timeEntry) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addTimeEntry(timeEntry);
  }
  
  public void removeTimeEntry(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removeTimeEntry(id);
  }
  
  public org.xplanner.soap.NoteData addNote(org.xplanner.soap.NoteData note) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addNote(note);
  }
  
  public org.xplanner.soap.NoteData[] getNotesForObject(int attachedToId) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getNotesForObject(attachedToId);
  }
  
  public org.xplanner.soap.PersonData addPerson(org.xplanner.soap.PersonData object) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.addPerson(object);
  }
  
  public void removePerson(int id) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.removePerson(id);
  }
  
  public void deleteAttribute(int objectId, java.lang.String key) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    xPlanner.deleteAttribute(objectId, key);
  }
  
  public java.util.HashMap getAttributesWithPrefix(int objectId, java.lang.String prefix) throws java.rmi.RemoteException{
    if (xPlanner == null)
      _initXPlannerProxy();
    return xPlanner.getAttributesWithPrefix(objectId, prefix);
  }
  
  
}