/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;



/**
 * An XPlannerCustomQuery represents a query for tasks or user stories from a XPlanner repository.
 * 
 * @author Ravi Kumar  
 * @author Helen Bershadskaya 
 */
public class XPlannerCustomQuery extends AbstractRepositoryQuery {

	public static final int INVALID_ID = XPlannerAttributeFactory.INVALID_ID;
	public static final List<Integer> INVALID_IDS = Arrays.asList(new Integer[] {INVALID_ID});
	//private static final int MAX_HITS = 75;
  public static enum ContentIdType {PROJECT, ITERATION, USER_STORY};  

  private String queryName = null;
  private List<Integer> contentIds = INVALID_IDS;  // if useTasks is true, this is user story id, otherwise iteration id
  private int personId = INVALID_ID;  // if personId is null, will get all tasks or stories
  private boolean useTasks = true; 
  private boolean myCurrentTasks = false;
  private ContentIdType contentIdType = ContentIdType.USER_STORY;  
    
	public XPlannerCustomQuery(String repositoryUrl, String queryName) {
		super(queryName);
		this.queryName = queryName;
		super.repositoryUrl = repositoryUrl;
	}

	public String getRepositoryKind() {
		return XPlannerMylynUIPlugin.REPOSITORY_KIND;
	}
	
	public String getQueryName() {
		return queryName;
	}

	public List<Integer> getContentIds() {
		return contentIds;
	}

	public void setContentIds(List<Integer> contentIds) {
		this.contentIds = contentIds;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public boolean isUseTasks() {
		return useTasks;
	}

	public void setUseTasks(boolean useTasks) {
		this.useTasks = useTasks;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public boolean isMyCurrentTasks() {
		return myCurrentTasks;
	}

	public void setMyCurrentTasks(boolean myCurrentTasks) {
		this.myCurrentTasks = myCurrentTasks;
	}

	public ContentIdType getContentIdType() {
		return contentIdType;
	}

	public void setContentIdType(ContentIdType contentIdType) {
		this.contentIdType = contentIdType;
	}
}

