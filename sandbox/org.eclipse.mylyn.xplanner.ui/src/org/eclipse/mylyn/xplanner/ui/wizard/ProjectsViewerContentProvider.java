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
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.xplanner.soap.*;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class ProjectsViewerContentProvider implements ITreeContentProvider {
	private XPlannerCustomQueryPage customQueryPage;
	private ProjectData[] projects;
	private HashMap<ProjectData, IterationData[]> projectsToIterationsMap = new HashMap<ProjectData, IterationData[]>(); 
	private HashMap<IterationData, UserStoryData[]> iterationsToUserStoriesMap = new HashMap<IterationData, UserStoryData[]>();
	
  public ProjectsViewerContentProvider(XPlannerCustomQueryPage customQueryPage) {
  	this.customQueryPage = customQueryPage;
  }
  
	public Object[] getChildren(Object parentElement) {
		return getXPlannerChildren(parentElement);
	}

	public Object getParent(Object element) {
		Object parent = null;
		
		try {
			if (element instanceof IterationData) {
				IterationData iteration = (IterationData) element;
				parent = customQueryPage.getClient().getProject(iteration.getProjectId());
			}
			else if (element instanceof UserStoryData) {
				UserStoryData userStory = (UserStoryData) element;
				parent = customQueryPage.getClient().getIteration(userStory.getIterationId());
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
    customQueryPage = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // do nothing
	}

	private ProjectData[] getProjects() {
		if (projects == null) {
			try {
				projects = customQueryPage.getClient().getProjects();
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
				iterations = customQueryPage.getClient().getIterations(project.getId());
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
					userStories = customQueryPage.getClient().getUserStories(iteration.getId());
					iterationsToUserStoriesMap.put(iteration, userStories);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
			}		
//		}
		
		return userStories;
	}
	
	/**
	 * Currently not used -- added when testing ILazyTreeContentProvider
	 * TODO -- remove
	 * @param parent
	 * @param index
	 */
	public void updateElement(Object parent, int index) {
		Object element = null;
		
		Object[] children = getXPlannerChildren(parent);
		
		if (children != null && children.length > index) {
			element = children[index];
		}
	
		if (element != null) {
		  customQueryPage.getProjectsViewer().replace(parent, index, element);
		  customQueryPage.getProjectsViewer().setChildCount(parent, getChildCount(parent));
		  try {
			  customQueryPage.getProjectsViewer().setChildCount(element, getChildCount(element));
			}
			catch (Exception e) {
				XPlannerMylynUIPlugin.log(e.getCause(), Messages.ProjectsViewerContentProvider_COULD_NOT_UPDATE_PROJECT_ELEMENT_CHILD_COUNT, true);
			}
		}  
	}

	private int getChildCount(Object element) {
	  int childCount = 0;
	  
	  Object[] children = getXPlannerChildren(element);
		if (children != null) {
			childCount = children.length;
		}
		
    return childCount;
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

	/**
	 * Currently not used -- added when testing ILazyTreeContentProvider
	 * TODO -- remove
	 * @param element
	 * @param currentChildCount
	 */
	public void updateChildCount(Object element, int currentChildCount) {
		Object[] children = getXPlannerChildren(element);
		
		if (children != null && children.length != currentChildCount) {
			customQueryPage.getProjectsViewer().setChildCount(element, children.length);
		}
	}
	
}
