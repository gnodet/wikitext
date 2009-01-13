/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Steffen Pingel
 */
public class WebTaskListMigrator extends AbstractTaskListMigrator {

	private static final String KEY_WEB = "Web";

	private static final String KEY_WEB_QUERY = KEY_WEB + KEY_QUERY;

	private static final String KEY_WEB_ISSUE = "WebIssue";

	private static final String KEY_URL_TEMPLATE = "UrlTemplate";

	private static final String KEY_REGEXP = "Regexp";

	private static final String KEY_PREFIX = "TaskPrefix";

	@Override
	public String getConnectorKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	@Override
	public String getTaskElementName() {
		return KEY_WEB_ISSUE;
	}

	@Override
	public Set<String> getQueryElementNames() {
		return Collections.singleton(KEY_WEB_QUERY);
	}

	@Override
	public void migrateTask(ITask task, Element element) {
		if (element.hasAttribute(KEY_PREFIX)) {
			task.setAttribute(WebRepositoryConnector.KEY_TASK_PREFIX, element.getAttribute(KEY_PREFIX));
		} else {
			task.setTaskKind("RSS");
			task.setTaskKey(null);
		}
	}

	@Override
	public void migrateQuery(IRepositoryQuery query, Element element) {
		String queryUrlTemplate = element.getAttribute(KEY_URL_TEMPLATE);
		String queryPattern = element.getAttribute(KEY_REGEXP);
		String taskPrefix = element.getAttribute(KEY_PREFIX);

		Map<String, String> params = new LinkedHashMap<String, String>();
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attr = (Attr) attributes.item(i);
			String name = attr.getName();
			if (name.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
//				params.put(org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(name), //
//						org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attr.getValue()));
				params.put(name, attr.getValue());
				query.setAttribute(name, attr.getValue());
			}
		}

		String queryString = query.getUrl();
		if (queryUrlTemplate == null || queryUrlTemplate.length() == 0) {
			queryUrlTemplate = query.getUrl();
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					WebRepositoryConnector.REPOSITORY_TYPE, query.getRepositoryUrl());
			queryString = WebRepositoryConnector.evaluateParams(queryUrlTemplate, params, repository);
		}
		query.setUrl(queryString);

		query.setAttribute(WebRepositoryConnector.KEY_QUERY_TEMPLATE, queryUrlTemplate);
		query.setAttribute(WebRepositoryConnector.KEY_QUERY_PATTERN, queryPattern);
		query.setAttribute(WebRepositoryConnector.KEY_TASK_PREFIX, taskPrefix);
	}

}
