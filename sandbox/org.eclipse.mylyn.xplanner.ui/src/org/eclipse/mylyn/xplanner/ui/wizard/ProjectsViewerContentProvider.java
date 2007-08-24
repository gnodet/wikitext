/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.wizard;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class ProjectsViewerContentProvider implements ITreeContentProvider {
	private XPlannerClient client;
	private ProjectData[] projects;
	private HashMap<ProjectData, IterationData[]> projectsToIterationsMap = new HashMap<ProjectData, IterationData[]>(); 
	private HashMap<IterationData, UserStoryData[]> iterationsToUserStoriesMap = new HashMap<IterationData, UserStoryData[]>();
	
  public ProjectsViewerContentProvider(XPlannerClient client) {
  	this.client = client;
  }
  
	public Object[] getChildren(Object parentElement) {
		return getXPlannerChildren(parentElement);
	}

	public Object getParent(Object element) {
		Object parent = null;
		
		try {
			if (element instanceof IterationData) {
				IterationData iteration = (IterationData) element;
				parent = client.getProject(iteration.getProjectId());
			}
			else if (element instanceof UserStoryData) {
				UserStoryData userStory = (UserStoryData) element;
				parent = client.getIteration(userStory.getIterationId());
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return parent;
	}

	public boolean hasChildren(Object element) {
    boolean hasChildren = true;
    
    if (element instanceof UserStoryData) {
    	hasChildren = false;
    }
//    else if (!customQueryPage.isContentTypeTask()) {
//			if (element instanceof IterationData) {
//				hasChildren = false;
//			}
//		}
    
		return hasChildren;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
    	client = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		clear();
	}

	private ProjectData[] getProjects() {
		if (projects == null) {
			try {
				projects = client.getProjects();
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return projects;
	}
	
	private IterationData[] getIterations(ProjectData project) {
		IterationData[] iterations = projectsToIterationsMap.get(project);
		
		if (iterations == null) {
			try {
				iterations = client.getIterations(project.getId());
				projectsToIterationsMap.put(project, iterations);
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return iterations;
	}
	
	private UserStoryData[] getUserStories(IterationData iteration) {
		UserStoryData[] userStories = null;
		
//		if (customQueryPage.isContentTypeTask()) { // if want tasks, not just user stories, expand
			userStories = iterationsToUserStoriesMap.get(iteration);
			if (userStories == null) {
				try {
					userStories = client.getUserStories(iteration.getId());
					iterationsToUserStoriesMap.put(iteration, userStories);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}		
//		}
		
		return userStories;
	}

	private Object[] getXPlannerChildren(Object element) {
	  Object[] children = new Object[0];
	  
		if (element == null || element instanceof XPlannerClient) {
		  children = getProjects();	
		}
		else if (element instanceof ProjectData) {
			children = getIterations((ProjectData) element);
		}
		else if (element instanceof IterationData) {
			children = getUserStories((IterationData) element);
		}
		
    return children;
	}
	
	public void clear() {
		projects = null;
		projectsToIterationsMap.clear();
		iterationsToUserStoriesMap.clear();
	}
}
