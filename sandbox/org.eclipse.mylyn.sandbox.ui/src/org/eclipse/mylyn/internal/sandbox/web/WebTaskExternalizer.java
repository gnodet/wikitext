/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.DelegatingTaskExternalizer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Task externalizer for generic web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_WEB = "Web";

	private static final String KEY_WEB_CATEGORY = "WebQuery" + KEY_CATEGORY;

	private static final String KEY_WEB_QUERY_HIT = KEY_WEB + KEY_QUERY_HIT;

	private static final String KEY_WEB_QUERY = KEY_WEB + KEY_QUERY;

	private static final String KEY_WEB_ISSUE = "WebIssue";

	private static final String KEY_REGEXP = "Regexp";

	private static final String KEY_PREFIX = "TaskPrefix";

	private static final String KEY_KEY = "Key";


	@Override
	public boolean canReadQuery(Node node) {
		return node.getNodeName().equals(KEY_WEB_QUERY);
	}

	@Override
	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		return category instanceof WebQuery;
	}

	@Override
	public boolean canCreateElementFor(ITask task) {
		return task instanceof WebTask;
	}
	
	@Override
	public boolean canReadQueryHit(Node node) {
		return node.getNodeName().equals(getQueryHitTagName());
	}

	@Override
	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		Element node = super.createQueryElement(query, doc, parent);

		if (query instanceof WebQuery) {
			WebQuery webQuery = (WebQuery) query;
			node.setAttribute(KEY_REGEXP, webQuery.getRegexp());
			node.setAttribute(KEY_PREFIX, webQuery.getTaskPrefix());
		}

		return node;
	}

	@Override
	public Element createQueryHitElement(AbstractQueryHit queryHit, Document doc, Element parent) {
		Element node = doc.createElement(getQueryHitTagName());
		
		node.setAttribute(KEY_KEY, queryHit.getId());
		node.setAttribute(KEY_NAME, queryHit.getDescription());
		// node.setAttribute(KEY_PREFIX, ((WebQueryHit) queryHit).getTaskPrefix());
		// node.setAttribute(KEY_REPOSITORY_URL, queryHit.getRepositoryUrl());
		
		parent.appendChild(node);
		return null;
	}
	
	@Override
	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		node.setAttribute(KEY_KEY, ((WebTask) task).getId());
		node.setAttribute(KEY_NAME, ((WebTask) task).getDescription());
		node.setAttribute(KEY_PREFIX, ((WebTask) task).getTaskPrefix());
		node.setAttribute(KEY_REPOSITORY_URL, ((WebTask) task).getRepositoryUrl());
		return node;
	}
	
	@Override
	public ITask readTask(Node node, TaskList taskList, AbstractTaskContainer category, ITask parent)
			throws TaskExternalizationException {
		Element element = (Element) node;

		String id = null;
		if (element.hasAttribute(KEY_KEY)) {
			id = element.getAttribute(KEY_KEY);
		} else {
			throw new TaskExternalizationException("Id not stored for bug report");
		}
		
		String label;
		if (element.hasAttribute(KEY_NAME)) {
			label = element.getAttribute(KEY_NAME);
		} else {
			throw new TaskExternalizationException("Description not stored for bug report");
		}

		String prefix = null;
		if (element.hasAttribute(KEY_PREFIX)) {
			prefix = element.getAttribute(KEY_PREFIX);
		} else {
			throw new TaskExternalizationException("Prefix not stored for bug report");
		}
		
		String repositoryUrl = null;
		if (element.hasAttribute(KEY_REPOSITORY_URL)) {
			repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
		} else {
			throw new TaskExternalizationException("Repository URL not stored for bug report");
		}
		
		WebTask task = new WebTask(id, label, prefix, repositoryUrl);

		readTaskInfo(task, taskList, element, parent, category);
		return task;
	}

	@Override
	public AbstractRepositoryQuery readQuery(Node node, TaskList taskList) throws TaskExternalizationException {
		Element element = (Element) node;

		String description = element.getAttribute(KEY_NAME);
		String queryUrl = element.getAttribute(KEY_QUERY_STRING);
		String taskPrefix = element.getAttribute(KEY_PREFIX);
		String regexp = element.getAttribute(KEY_REGEXP);
		String repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
		
		AbstractRepositoryQuery query = new WebQuery(description, queryUrl, taskPrefix, regexp, 
				MylarTaskListPlugin.getTaskListManager().getTaskList(), repositoryUrl);
		
		boolean hasCaughtException = false;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			try {
				readQueryHit(child, taskList, query);
			} catch (TaskExternalizationException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) {
			throw new TaskExternalizationException("Failed to load all hits");
		}
		return query;
	}

	@Override
	public void readQueryHit(Node node, TaskList taskList, AbstractRepositoryQuery query)
			throws TaskExternalizationException {
		Element element = (Element) node;

		String id = element.getAttribute(KEY_KEY);
		String description = element.getAttribute(KEY_NAME);

		WebQueryHit hit = new WebQueryHit(id, description, ((WebQuery) query).getTaskPrefix(), query.getRepositoryUrl());
		query.addHit(hit);
	}

	@Override
	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		return query instanceof WebQuery ? KEY_WEB_QUERY : "";
	}

	@Override
	public String getCategoryTagName() {
		return KEY_WEB_CATEGORY;
	}

	@Override
	public String getQueryHitTagName() {
		return KEY_WEB_QUERY_HIT;
	}

	@Override
	public String getTaskTagName() {
		return KEY_WEB_ISSUE;
	}

}
