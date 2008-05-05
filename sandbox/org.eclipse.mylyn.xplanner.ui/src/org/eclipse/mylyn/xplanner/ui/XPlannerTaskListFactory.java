/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.ITask;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskListFactory extends AbstractTaskListFactory {

	private static final String TOKEN_SEPARATOR = ";"; //$NON-NLS-1$

	private static final String KEY_XPLANNER = "XPlanner"; //$NON-NLS-1$

	private static final String KEY_CUSTOM = "XPlannerCustom"; //$NON-NLS-1$

	private static final String KEY_XPLANNER_QUERY = KEY_XPLANNER + KEY_QUERY;

	private static final String KEY_XPLANNER_CUSTOM = KEY_XPLANNER + KEY_CUSTOM + KEY_QUERY;

	private static final String KEY_XPLANNER_ITEM = "XPlannerItem"; //$NON-NLS-1$

	private static final String KEY_QUERY_CONTENT_ID_TYPE = "QueryContentIdType"; //$NON-NLS-1$

	private static final String KEY_QUERY_IS_TASKS = "QueryIsTasks"; //$NON-NLS-1$

	private static final String KEY_QUERY_CONTENT_ID = "QueryConentId"; //$NON-NLS-1$

	private static final String KEY_QUERY_PERSON_ID = "QueryPersonId"; //$NON-NLS-1$

	private static final String KEY_QUERY_MY_CURRENT_TASKS = "QueryMyCurrentTasks"; //$NON-NLS-1$

	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(KEY_XPLANNER_QUERY) || node.getNodeName().equals(KEY_XPLANNER_CUSTOM);
	}

	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof XPlannerCustomQuery;
	}

	public boolean canCreateElementFor(ITask task) {
		return task instanceof XPlannerTask;
	}

	@Override
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) {
		AbstractRepositoryQuery query = null;
		query = new XPlannerCustomQuery(repositoryUrl, label);
		initializeQuery((XPlannerCustomQuery) query, element);

		return query;
	}

	private void initializeQuery(XPlannerCustomQuery query, Element element) {
		String myCurrentTasks = element.getAttribute(KEY_QUERY_MY_CURRENT_TASKS);
		if (myCurrentTasks != null && Boolean.valueOf(myCurrentTasks)) {
			query.setMyCurrentTasks(true);
		} else {
			// selected content type
			String contentIdType = element.getAttribute(KEY_QUERY_CONTENT_ID_TYPE);
			if (contentIdType != null) {
				query.setContentIdType((XPlannerCustomQuery.ContentIdType.valueOf(contentIdType)));
			}

			// use tasks?
			String useTasks = element.getAttribute(KEY_QUERY_IS_TASKS);
			if (useTasks != null) {
				query.setUseTasks(Boolean.valueOf(useTasks));
			}

			// use all?
			String personId = element.getAttribute(KEY_QUERY_PERSON_ID);
			if (personId != null) {
				query.setPersonId(Integer.valueOf(personId));
			}

			// content id
			String contentIds = element.getAttribute(KEY_QUERY_CONTENT_ID);
			if (contentIds != null) {
				query.setContentIds(decodeIds(contentIds));
			}
		}
	}

	private String encodeIds(List<Integer> contentIds) {
		StringBuilder result = new StringBuilder();
		for (Iterator<Integer> iter = contentIds.iterator(); iter.hasNext();) {
			Integer id = iter.next();
			result.append(id);
			if (iter.hasNext()) {
				result.append(TOKEN_SEPARATOR);
			}
		}

		return result.toString();
	}

	private List<Integer> decodeIds(String encoded) {
		if (encoded == null) {
			return XPlannerCustomQuery.INVALID_IDS;
		}

		StringTokenizer tokens = new StringTokenizer(encoded, TOKEN_SEPARATOR);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		while (tokens.hasMoreTokens()) {
			ids.add(Integer.valueOf(tokens.nextToken()));
		}

		return ids;
	}

	@Override
	public Set<String> getQueryElementNames() {
		Set<String> names = new HashSet<String>();
		names.add(KEY_XPLANNER_CUSTOM);
		return names;
	}

	@Override
	public void setAdditionalAttributes(AbstractRepositoryQuery query, Element node) {
//		String queryTagName = getQueryTagNameForElement(query);

//		node.setAttribute(KEY_NAME, query.getSummary());
//		node.setAttribute(KEY_QUERY_STRING, query.getUrl());
//		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());

		XPlannerCustomQuery xplannerCustomQuery = (XPlannerCustomQuery) query;

		// name
//		String queryName = xplannerCustomQuery.getQueryName();
//		node.setAttribute(KEY_QUERY_NAME, queryName);

		boolean isMyCurrentTasks = xplannerCustomQuery.isMyCurrentTasks();
		if (isMyCurrentTasks) {
			// show only my current tasks
			node.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(true));
		} else {
			// don't show only my current tasks
			node.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(false));

			// selected content type
			String contentIdType = xplannerCustomQuery.getContentIdType().name();
			node.setAttribute(KEY_QUERY_CONTENT_ID_TYPE, contentIdType);

			// is tasks
			boolean isUseTasks = xplannerCustomQuery.isUseTasks();
			node.setAttribute(KEY_QUERY_IS_TASKS, Boolean.toString(isUseTasks));

			// content id
			List<Integer> contentIds = xplannerCustomQuery.getContentIds();
			node.setAttribute(KEY_QUERY_CONTENT_ID, encodeIds(contentIds));

			// person id
			int personId = xplannerCustomQuery.getPersonId();
			node.setAttribute(KEY_QUERY_PERSON_ID, Integer.toString(personId));
		}
//		
//		for (AbstractTask hit : query.getHits()) {
//				try {
//					Element element = null;
//					for (ITaskListExternalizer externalizer : super.getDelegateExternalizers()) {
//						if (externalizer.canCreateElementFor(hit)) {
//							element = createQueryHitElement(hit, doc, node);
//						}
//					}
//					if (element == null) {
//						createQueryHitElement(hit, doc, node);
//					}	
//				} 
//				catch (Exception e) {
//					StatusManager.log(e, e.getMessage());
//				}
//		}
//		parent.appendChild(node);
//		return node;
	}

//	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
//		String queryTagName = getQueryTagNameForElement(query);
//		Element node = doc.createElement(queryTagName);
//
//		node.setAttribute(KEY_NAME, query.getSummary());
//		node.setAttribute(KEY_QUERY_STRING, query.getUrl());
//		node.setAttribute(KEY_REPOSITORY_URL, query.getRepositoryUrl());
//
//		XPlannerCustomQuery xplannerCustomQuery = (XPlannerCustomQuery) query;
//		
//		// name
//		String queryName = xplannerCustomQuery.getQueryName();
//		node.setAttribute(KEY_QUERY_NAME, queryName);
//		
//		boolean isMyCurrentTasks = xplannerCustomQuery.isMyCurrentTasks();
//		if (isMyCurrentTasks) {
//			// show only my current tasks
//			node.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(true));
//		}
//		else {
//			// don't show only my current tasks
//			node.setAttribute(KEY_QUERY_MY_CURRENT_TASKS, Boolean.toString(false));
//			
//			// selected content type
//			String contentIdType = xplannerCustomQuery.getContentIdType().name();
//			node.setAttribute(KEY_QUERY_CONTENT_ID_TYPE, contentIdType);
//
//			// is tasks
//			boolean isUseTasks = xplannerCustomQuery.isUseTasks();
//			node.setAttribute(KEY_QUERY_IS_TASKS, Boolean.toString(isUseTasks));
//			
//			// content id
//			List<Integer> contentIds = xplannerCustomQuery.getContentIds();
//			node.setAttribute(KEY_QUERY_CONTENT_ID, encodeIds(contentIds));
//			
//			// person id
//			int personId = xplannerCustomQuery.getPersonId();
//			node.setAttribute(KEY_QUERY_PERSON_ID, Integer.toString(personId));
//		}
//		
//		for (AbstractTask hit : query.getHits()) {
//				try {
//					Element element = null;
//					for (ITaskListExternalizer externalizer : super.getDelegateExternalizers()) {
//						if (externalizer.canCreateElementFor(hit)) {
//							element = createQueryHitElement(hit, doc, node);
//						}
//					}
//					if (element == null) {
//						createQueryHitElement(hit, doc, node);
//					}	
//				} 
//				catch (Exception e) {
//					StatusManager.log(e, e.getMessage());
//				}
//		}
//		parent.appendChild(node);
//		return node;
//	}

	@Override
	public String getTaskElementName() {
		return KEY_XPLANNER_ITEM;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element) {
		return new XPlannerTask(repositoryUrl, taskId, summary);
	}

	@Override
	public String getQueryElementName(AbstractRepositoryQuery query) {
		if (query instanceof XPlannerCustomQuery) {
			return KEY_XPLANNER_CUSTOM;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public boolean canCreate(AbstractRepositoryQuery category) {
		return category instanceof XPlannerCustomQuery;
	}

	@Override
	public boolean canCreate(ITask task) {
		return task instanceof XPlannerTask;
	}

}
