/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.monitor.core.util.XmlStringConverter;
import org.eclipse.mylyn.internal.tasks.core.WebTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.DelegatingTaskExternalizer;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Task externalizer for generic web based issue tracking systems
 *
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class WebTaskExternalizer extends DelegatingTaskExternalizer {

	private static final String KEY_WEB = "Web";

	private static final String KEY_WEB_CATEGORY = "WebQuery" + KEY_CATEGORY;

//	private static final String KEY_WEB_QUERY_HIT = KEY_WEB + KEY_QUERY_HIT;

	private static final String KEY_WEB_QUERY = KEY_WEB + KEY_QUERY;

	private static final String KEY_WEB_ISSUE = "WebIssue";

	private static final String KEY_URL_TEMPLATE = "UrlTemplate";
	
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
	public boolean canCreateElementFor(AbstractTask task) {
		return task instanceof WebTask;
	}

//	@Override
//	public boolean canReadQueryHit(Node node) {
//		return node.getNodeName().equals(getQueryHitTagName());
//	}

	@Override
	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		Element node = super.createQueryElement(query, doc, parent);

		if (query instanceof WebQuery) {
			WebQuery webQuery = (WebQuery) query;
			node.setAttribute(KEY_URL_TEMPLATE, webQuery.getQueryUrlTemplate());
			node.setAttribute(KEY_REGEXP, webQuery.getQueryPattern());
			node.setAttribute(KEY_PREFIX, webQuery.getTaskPrefix());

			for (Map.Entry<String, String> e : webQuery.getQueryParameters().entrySet()) {
				node.setAttribute(XmlStringConverter.convertToXmlString(e.getKey()), //
						XmlStringConverter.convertToXmlString(e.getValue()));
			}
		}

		return node;
	}

//	@Override
//	public Element createQueryHitElement(AbstractQueryHit queryHit, Document doc, Element parent) {
//		Element element = super.createQueryHitElement(queryHit, doc, parent);
//		element.setAttribute(KEY_KEY, queryHit.getTaskId());
//		element.setAttribute(KEY_PREFIX, ((WebQueryHit) queryHit).getTaskPrefix());
//		return element;
//	}

	@Override
	public Element createTaskElement(AbstractTask task, Document doc, Element parent) {
		Element node = super.createTaskElement(task, doc, parent);
		node.setAttribute(KEY_KEY, ((WebTask) task).getTaskId());
		node.setAttribute(KEY_NAME, ((WebTask) task).getSummary());
		node.setAttribute(KEY_PREFIX, ((WebTask) task).getTaskPrefix());
		node.setAttribute(KEY_REPOSITORY_URL, ((WebTask) task).getRepositoryUrl());
		return node;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractTaskListElement category, AbstractTask parent)
			throws TaskExternalizationException {
		String id = null;
		if (element.hasAttribute(KEY_KEY)) {
			id = element.getAttribute(KEY_KEY);
		} else {
			throw new TaskExternalizationException("Id not stored for bug report");
		}

		String label = summary;
		// TODO: at some point this should be removed
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

//		String repositoryUrl = null;
//		if (element.hasAttribute(KEY_REPOSITORY_URL)) {
//			repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
//		} else {
//			throw new TaskExternalizationException("Repository URL not stored for bug report");
//		}

		WebTask task = new WebTask(id, label, prefix, repositoryUrl, WebTask.REPOSITORY_TYPE);
		// TODO: remove after refactoring
		task.setRepositoryUrl(repositoryUrl);
		return task;
	}

	@Override
	public AbstractRepositoryQuery readQuery(Node node, TaskList taskList) throws TaskExternalizationException {
		Element element = (Element) node;

		String description = element.getAttribute(KEY_NAME);
		String queryUrl = element.getAttribute(KEY_QUERY_STRING);
		String queryUrlTemplate = element.getAttribute(KEY_URL_TEMPLATE);
		String queryPattern = element.getAttribute(KEY_REGEXP);
		String repositoryUrl = element.getAttribute(KEY_REPOSITORY_URL);
		String taskPrefix = element.getAttribute(KEY_PREFIX);

		Map<String, String> params = new LinkedHashMap<String, String>();
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);
			String name = attr.getName();
			if (name.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
				params.put(XmlStringConverter.convertXmlToString(name), //
						XmlStringConverter.convertXmlToString(attr.getValue()));
			}
		}

		if(queryUrlTemplate==null || queryUrlTemplate.length()==0) {
			queryUrlTemplate = queryUrl;
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(WebTask.REPOSITORY_TYPE, repositoryUrl);
			queryUrl = WebRepositoryConnector.evaluateParams(queryUrlTemplate, params, repository);
		}
		
		return new WebQuery(description, queryUrl, queryUrlTemplate,
				queryPattern, taskPrefix, repositoryUrl, params);
	}

//	@Override
//	public AbstractQueryHit createQueryHit(String repositoryUrl, String taskId, String summary, Element element, TaskList taskList, AbstractRepositoryQuery query)
//			throws TaskExternalizationException {
//		String id = element.getAttribute(KEY_KEY);
//
//		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(query.getRepositoryKind(), query.getRepositoryUrl());
//		String prefix = WebRepositoryConnector.evaluateParams(((WebQuery) query).getTaskPrefix(), ((WebQuery) query).getQueryParameters(), repository);
//
//		return new WebQueryHit(TasksUiPlugin.getTaskListManager().getTaskList(), repositoryUrl, summary, id, prefix);
//	}

	@Override
	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		return query instanceof WebQuery ? KEY_WEB_QUERY : "";
	}

	@Override
	public String getCategoryTagName() {
		return KEY_WEB_CATEGORY;
	}

//	@Override
//	public String getQueryHitTagName() {
//		return KEY_WEB_QUERY_HIT;
//	}

	@Override
	public String getTaskTagName() {
		return KEY_WEB_ISSUE;
	}

}
