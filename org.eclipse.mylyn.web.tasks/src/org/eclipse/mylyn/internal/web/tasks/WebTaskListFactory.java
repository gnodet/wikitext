/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.internal.monitor.core.util.XmlStringConverter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Task externalizer for generic web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class WebTaskListFactory extends AbstractTaskListFactory {

	private static final String KEY_WEB = "Web";

	private static final String KEY_WEB_QUERY = KEY_WEB + AbstractTaskListFactory.KEY_QUERY;

	private static final String KEY_WEB_ISSUE = "WebIssue";

	private static final String KEY_URL_TEMPLATE = "UrlTemplate";

	private static final String KEY_REGEXP = "Regexp";

	private static final String KEY_PREFIX = "TaskPrefix";

	private static final String KEY_KEY = "Key";

	@Override
	public String getTaskElementName() {
		return KEY_WEB_ISSUE;
	}

	@Override
	public Set<String> getQueryElementNames() {
		return Collections.singleton(KEY_WEB_QUERY);
	}

	@Override
	public boolean canCreate(AbstractRepositoryQuery category) {
		return category instanceof WebQuery;
	}

	@Override
	public boolean canCreate(AbstractTask task) {
		return task instanceof WebTask;
	}

	@Override
	public void setAdditionalAttributes(AbstractRepositoryQuery query, Element node) {
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
	}

	@Override
	public void setAdditionalAttributes(AbstractTask task, Element element) {
		element.setAttribute(KEY_KEY, ((WebTask) task).getTaskId());
		element.setAttribute(KEY_PREFIX, ((WebTask) task).getTaskPrefix());
//		element.setAttribute(AbstractTaskListFactory.KEY_NAME, ((WebTask) task).getSummary());
//		element.setAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL, ((WebTask) task).getRepositoryUrl());
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String taskId, String label, Element element) {
//		String id = null;
//		if (element.hasAttribute(KEY_KEY)) {
//			id = element.getAttribute(KEY_KEY);
//		} else {
//			throw new TaskExternalizationException("Id not stored for bug report");
//		}

//		String label = summary;
		// NOTE: this comes from the super
//		if (element.hasAttribute(AbstractTaskListFactory.KEY_NAME)) {
//			label = element.getAttribute(AbstractTaskListFactory.KEY_NAME);
//		} else {
//			throw new TaskExternalizationException("Description not stored for bug report");
//		}

		String prefix = null;
		if (element.hasAttribute(KEY_PREFIX)) {
			prefix = element.getAttribute(KEY_PREFIX);
		}

		WebTask task = new WebTask(taskId, label, prefix, repositoryUrl, WebRepositoryConnector.REPOSITORY_TYPE);
//		task.setRepositoryUrl(repositoryUrl);
		return task;
	}

	@Override
	public AbstractRepositoryQuery createQuery(String repositoryUrl, String queryString, String label, Element element) {
		String queryUrlTemplate = element.getAttribute(KEY_URL_TEMPLATE);
		String queryPattern = element.getAttribute(KEY_REGEXP);
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

		if (queryUrlTemplate == null || queryUrlTemplate.length() == 0) {
			queryUrlTemplate = queryString;
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					WebRepositoryConnector.REPOSITORY_TYPE, repositoryUrl);
			queryString = WebRepositoryConnector.evaluateParams(queryUrlTemplate, params, repository);
		}

		return new WebQuery(label, queryString, queryUrlTemplate, queryPattern, taskPrefix, repositoryUrl, params);
	}

	@Override
	public String getQueryElementName(AbstractRepositoryQuery query) {
		return query instanceof WebQuery ? KEY_WEB_QUERY : "";
	}

}
