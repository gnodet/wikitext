/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import static org.eclipse.mylyn.internal.web.tasks.Util.isPresent;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.ITaskCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.web.core.WebClientUtil;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Generic connector for web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_TYPE = "web";

	public static final String PROPERTY_TASK_CREATION_URL = "taskCreationUrl";

	public static final String PROPERTY_TASK_URL = "taskUrl";

	public static final String PROPERTY_QUERY_URL = "queryUrl";

	public static final String PROPERTY_QUERY_METHOD = "queryMethod";

	public static final String PROPERTY_QUERY_REGEXP = "queryPattern";

	public static final String PROPERTY_LOGIN_FORM_URL = "loginFormUrl";

	public static final String PROPERTY_LOGIN_TOKEN_REGEXP = "loginTokenPattern";

	public static final String PROPERTY_LOGIN_REQUEST_METHOD = "loginRequestMethod";

	public static final String PROPERTY_LOGIN_REQUEST_URL = "loginRequestUrl";

	public static final String PARAM_PREFIX = "param_";

	public static final String PARAM_SERVER_URL = "serverUrl";

	public static final String PARAM_USER_ID = "userId";

	public static final String PARAM_PASSWORD = "password";

	public static final String PARAM_LOGIN_TOKEN = "loginToken";

	public static final String REQUEST_POST = "POST";

	public static final String REQUEST_GET = "GET";

	@Override
	public String getConnectorKind() {
		return WebRepositoryConnector.REPOSITORY_TYPE;
	}

	@Override
	public String getLabel() {
		return "Generic web-based access (Advanced)";
	}

	@Override
	public String[] getPepositoryPropertyNames() {
		return new String[] { PROPERTY_TASK_URL, PROPERTY_TASK_CREATION_URL };
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return repository.hasProperty(PROPERTY_TASK_CREATION_URL);
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return repository.hasProperty(PROPERTY_TASK_URL);
	}

	@Override
	public AbstractTask createTaskFromExistingId(TaskRepository repository, final String id, IProgressMonitor monitor)
			throws CoreException {
		if (WebRepositoryConnector.REPOSITORY_TYPE.equals(repository.getConnectorKind())) {
			String taskPrefix = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);

			final WebTask task = new WebTask(id, id, taskPrefix, repository.getUrl(),
					WebRepositoryConnector.REPOSITORY_TYPE);

			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(taskPrefix + id) {
				@Override
				protected void setTitle(String pageTitle) {
					task.setSummary(pageTitle);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
				}
			};
			job.schedule();

			return task;
		}

		return null;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		// lookup repository using task prefix url
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (TaskRepository repository : repositoryManager.getRepositories(getConnectorKind())) {
			String taskUrl = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
			if (taskUrl != null && !taskUrl.equals("") && url.startsWith(taskUrl)) {
				return repository.getUrl();
			}
		}

		for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
			if (query instanceof WebQuery) {
				WebQuery webQuery = (WebQuery) query;
				TaskRepository repository = repositoryManager.getRepository(webQuery.getRepositoryKind(),
						webQuery.getRepositoryUrl());
				if (repository != null) {
					String queryUrl = evaluateParams(webQuery.getTaskPrefix(), //
							webQuery.getQueryParameters(), repository);
					if (queryUrl != null && !queryUrl.equals("") && url.startsWith(queryUrl)) {
						return webQuery.getRepositoryUrl();
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		for (TaskRepository repository : repositoryManager.getRepositories(getConnectorKind())) {
			String start = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
			if (start != null && url.startsWith(start)) {
				return url.substring(start.length());
			}
		}
		return null;
	}

	@Override
	public String getTaskUrl(String repositoryUrl, String taskId) {
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		TaskRepository repository = repositoryManager.getRepository(getConnectorKind(), repositoryUrl);
		if (repository != null) {
			String prefix = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
			return prefix + taskId;
		}
		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			ITaskCollector resultCollector) {
		if (query instanceof WebQuery) {
			WebQuery webQuery = (WebQuery) query;
			Map<String, String> queryParameters = webQuery.getQueryParameters();
			String queryUrl = evaluateParams(query.getUrl(), queryParameters, repository);
			String queryPattern = evaluateParams(webQuery.getQueryPattern(), queryParameters, repository);
			String taskPrefix = evaluateParams(webQuery.getTaskPrefix(), queryParameters, repository);

			try {
				if (queryPattern != null && queryPattern.trim().length() > 0) {
					return performQuery(fetchResource(queryUrl, queryParameters, repository), queryPattern, taskPrefix,
							monitor, resultCollector, repository);
				} else {
					return performRssQuery(queryUrl, monitor, resultCollector, repository);
				}

			} catch (IOException ex) {
				return new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, IStatus.OK, "Could not fetch resource: "
						+ queryUrl, ex);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		// not supported
		return null;
	}

	@Override
	public boolean markStaleTasks(TaskRepository repository, Set<AbstractTask> tasks, IProgressMonitor monitor) {
		// not supported
		return false;
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		// not supported
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
			IProgressMonitor monitor) throws CoreException {
	}

	// utility methods

	public static IStatus performQuery(String resource, String regexp, String taskPrefix, IProgressMonitor monitor,
			ITaskCollector resultCollector, TaskRepository repository) {
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
				| Pattern.UNICODE_CASE | Pattern.CANON_EQ);
		Matcher matcher = p.matcher(resource);

		if (!matcher.find()) {
			return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.ERROR,
					"Unable to parse resource. Check query regexp", null);
		} else {
			boolean isCorrect = true;
			do {
				if (matcher.groupCount() < 2) {
					isCorrect = false;
				}
				if (matcher.groupCount() >= 1) {
					String id = matcher.group(1);
					String description = matcher.groupCount() > 1 ? cleanup(matcher.group(2), repository) : null;
					resultCollector.accept(new WebTask(id, description, taskPrefix, repository.getUrl(),
							REPOSITORY_TYPE));
				}
			} while (matcher.find() && !monitor.isCanceled());

			if (isCorrect) {
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.ERROR,
						"Require two matching groups (taskId and summary). Check query regexp", null);
			}
		}
	}

	private static String cleanup(String text, TaskRepository repository) {
		// Has to disable this for now. See bug 166737 and 166936
		// try {
		// text = URLDecoder.decode(text, repository.getCharacterEncoding());
		// } catch (UnsupportedEncodingException ex) {
		// // ignore
		// }

		text = text.replaceAll("<!--.+?-->", "");

		String[] tokens = text.split(" |\\t|\\n|\\r");
		StringBuilder sb = new StringBuilder();
		String sep = "";
		for (String token : tokens) {
			if (token.length() > 0) {
				sb.append(sep).append(token);
				sep = " ";
			}
		}

		return sb.toString();
	}

	public static IStatus performRssQuery(String queryUrl, IProgressMonitor monitor, ITaskCollector resultCollector,
			TaskRepository repository) {
		SyndFeedInput input = new SyndFeedInput();
		try {
			SyndFeed feed = input.build(new XmlReader(new URL(queryUrl)));

			SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");

			@SuppressWarnings("unchecked")
			Iterator it;
			for (it = feed.getEntries().iterator(); it.hasNext();) {
				SyndEntry entry = (SyndEntry) it.next();

				Date date = entry.getUpdatedDate();
				if (date == null) {
					date = entry.getPublishedDate();
				}
				if (date == null) {
					DCModule module = (DCModule) entry.getModule("http://purl.org/dc/elements/1.1/");
					date = module.getDate();
				}

				String entryUri = entry.getLink();
				if (entryUri == null) {
					entryUri = entry.getUri();
				}

				String entrTitle = entry.getTitle();

				resultCollector.accept(new WebTask(entryUri, //
						(date == null ? "" : df.format(date) + " - ") + entrTitle, //
						"", repository.getUrl(), WebRepositoryConnector.REPOSITORY_TYPE));
			}
			return Status.OK_STATUS;
		} catch (Exception ex) {
			return new Status(IStatus.OK, TasksUiPlugin.ID_PLUGIN, IStatus.OK, "Could not fetch resource: " + queryUrl,
					ex);
		}
	}

	public static String fetchResource(String url, Map<String, String> params, TaskRepository repository)
			throws IOException {
		HttpClient client = new HttpClient();
		WebClientUtil.setupHttpClient(client, repository.getProxy(), url, //
				repository.getUserName(), repository.getPassword());

		loginRequestIfNeeded(client, params, repository);

		GetMethod method = new GetMethod(url);
		// method.setFollowRedirects(false);
		return requestResource(url, client, method);
	}

	private static void loginRequestIfNeeded(HttpClient client, Map<String, String> params, TaskRepository repository)
			throws HttpException, IOException {
		if (!isPresent(repository.getUserName()) || !isPresent(repository.getPassword())
				|| !isPresent(repository.getProperty(PROPERTY_LOGIN_REQUEST_URL))) {
			return;
		}

		String loginFormUrl = evaluateParams(repository.getProperty(PROPERTY_LOGIN_FORM_URL), params, repository);
		String loginToken = evaluateParams(repository.getProperty(PROPERTY_LOGIN_TOKEN_REGEXP), params, repository);
		if (isPresent(loginFormUrl) || isPresent(loginToken)) {
			GetMethod method = new GetMethod(loginFormUrl);
			// method.setFollowRedirects(false);
			String loginFormPage = requestResource(loginFormUrl, client, method);
			if (loginFormPage != null) {
				Pattern p = Pattern.compile(loginToken);
				Matcher m = p.matcher(loginFormPage);
				if (m.find()) {
					params.put(PARAM_PREFIX + PARAM_LOGIN_TOKEN, m.group(1));
				}
			}
		}

		String loginRequestUrl = evaluateParams(repository.getProperty(PROPERTY_LOGIN_REQUEST_URL), params, repository);
		String loginRequestMethod = repository.getProperty(PROPERTY_LOGIN_REQUEST_METHOD);

		HttpMethod method = null;
		if (REQUEST_POST.equals(loginRequestMethod)) {
			int n = loginRequestUrl.indexOf('?');
			if (n == -1) {
				method = new PostMethod(loginRequestUrl);
			} else {
				PostMethod postMethod = new PostMethod(loginRequestUrl.substring(0, n));
				// TODO this does not take into account escaped values
				String[] requestParams = loginRequestUrl.substring(n + 1).split("&");
				for (String requestParam : requestParams) {
					String[] nv = requestParam.split("=");
					postMethod.addParameter(nv[0], nv.length == 1 ? "" : nv[1]);
				}
				method = postMethod;
			}
		} else {
			method = new GetMethod(loginRequestUrl);
			// method.setFollowRedirects(false);
		}

		requestResource(loginRequestUrl, client, method);
	}

	private static String requestResource(String url, HttpClient client, HttpMethod method) throws IOException,
			HttpException {
		String refreshUrl = null;
		try {
			client.executeMethod(method);
// int statusCode = client.executeMethod(method);
// if (statusCode == 300 || statusCode == 301 || statusCode == 302 || statusCode
// == 303 || statusCode == 307) {
// Header location = method.getResponseHeader("Location");
// if (location!=null) {
// refreshUrl = location.getValue();
// if (!refreshUrl.startsWith("/")) {
// refreshUrl = "/" + refreshUrl;
// }
// }
// }
			refreshUrl = getRefreshUrl(url, method);
			if (refreshUrl == null) {
				return method.getResponseBodyAsString();
			}
		} finally {
			method.releaseConnection();
		}

		method = new GetMethod(refreshUrl);
		try {
			client.executeMethod(method);
			return method.getResponseBodyAsString();
		} finally {
			method.releaseConnection();
		}
	}

	private static String getRefreshUrl(String url, HttpMethod method) {
		Header refreshHeader = method.getResponseHeader("Refresh");
		if (refreshHeader == null) {
			return null;
		}
		String value = refreshHeader.getValue();
		int n = value.indexOf(";url=");
		if (n == -1) {
			return null;
		}
		value = value.substring(n + 5);
		int requestPath;
		if (value.charAt(0) == '/') {
			int colonSlashSlash = url.indexOf("://");
			requestPath = url.indexOf('/', colonSlashSlash + 3);
		} else {
			requestPath = url.lastIndexOf('/');
		}

		String refreshUrl;
		if (requestPath == -1) {
			refreshUrl = url + "/" + value;
		} else {
			refreshUrl = url.substring(0, requestPath + 1) + value;
		}
		return refreshUrl;
	}

	public static String evaluateParams(String value, Map<String, String> params, TaskRepository repository) {
		return evaluateParams(evaluateParams(value, params), repository);
	}

	public static String evaluateParams(String value, TaskRepository repository) {
		if (value != null && value.indexOf("${") > -1) {
			value = value.replaceAll("\\$\\{" + PARAM_SERVER_URL + "\\}", repository.getUrl());
			value = value.replaceAll("\\$\\{" + PARAM_USER_ID + "\\}", repository.getUserName());
			value = value.replaceAll("\\$\\{" + PARAM_PASSWORD + "\\}", repository.getPassword());
			value = evaluateParams(value, repository.getProperties());
		}
		return value;
	}

	public static String evaluateParams(String value, Map<String, String> params) {
		for (Map.Entry<String, String> e : params.entrySet()) {
			String key = e.getKey();
			if (key.startsWith(PARAM_PREFIX)) {
				value = value.replaceAll("\\$\\{" + key.substring(PARAM_PREFIX.length()) + "\\}", e.getValue());
			}
		}
		return value;
	}

	public static List<String> getTemplateVariables(String value) {
		if (value == null) {
			return Collections.emptyList();
		}

		List<String> vars = new ArrayList<String>();
		Matcher m = Pattern.compile("\\$\\{(.+?)\\}").matcher(value);
		while (m.find()) {
			vars.add(m.group(1));
		}
		return vars;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		return null;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData taskData) {
		// ignore
	}

}
