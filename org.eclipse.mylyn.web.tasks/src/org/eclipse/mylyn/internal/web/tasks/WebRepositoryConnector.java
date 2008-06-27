/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.mylyn.tasks.ui.TaskRepositoryLocationUiFactory;
import org.eclipse.mylyn.tasks.ui.TasksUi;

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

	private static final String COMPLETED_STATUSES = "completed|fixed|resolved|invalid|verified|deleted|closed|done";

	public static final String KEY_TASK_PREFIX = "taskPrefix";

	public static final String KEY_QUERY_TEMPLATE = "UrlTemplate";

	public static final String KEY_QUERY_PATTERN = "Regexp";

	private static final String USER_AGENT = "WebTemplatesConnector";

	@Override
	public String getConnectorKind() {
		return REPOSITORY_TYPE;
	}

	@Override
	public String getLabel() {
		return "Web Template (Advanced)";
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
	public boolean canSynchronizeTask(TaskRepository taskRepository, ITask task) {
		return false;
	}

//	@Override
//	public AbstractTask createTaskFromExistingId(TaskRepository repository, final String id, IProgressMonitor monitor)
//			throws CoreException {
//		if (REPOSITORY_TYPE.equals(repository.getConnectorKind())) {
//			String taskPrefix = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
//
//			final WebTask task = new WebTask(id, id, taskPrefix, repository.getRepositoryUrl(), REPOSITORY_TYPE);
//
//			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(taskPrefix + id) {
//				@Override
//				protected void setTitle(String pageTitle) {
//					task.setSummary(pageTitle);
//					TasksUiPlugin.getTaskList().notifyTaskChanged(task, false);
//				}
//			};
//			job.schedule();
//
//			return task;
//		}
//
//		return null;
//	}

	@Override
	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		String taskPrefix = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
		TaskData taskData = createTaskData(repository, taskId);
		TaskMapper mapper = new TaskMapper(taskData, true);
		mapper.setSummary(taskId);
		mapper.setTaskUrl(taskPrefix + taskId);
		mapper.setValue(KEY_TASK_PREFIX, taskPrefix);
		try {
			String pageTitle = WebUtil.getTitleFromUrl(new WebLocation(taskPrefix + taskId), monitor);
			mapper.setSummary(pageTitle);
		} catch (IOException e) {
			// log to error log?
		}
		return taskData;
	}

	@SuppressWarnings("restriction")
	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		// lookup repository using task prefix url
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		for (TaskRepository repository : repositoryManager.getRepositories(getConnectorKind())) {
			String taskUrl = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
			if (taskUrl != null && !taskUrl.equals("") && url.startsWith(taskUrl)) {
				return repository.getRepositoryUrl();
			}
		}

		for (IRepositoryQuery query : org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal.getTaskList()
				.getQueries()) {
			TaskRepository repository = repositoryManager.getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (repository != null) {
				String queryUrl = evaluateParams(query.getAttribute(KEY_TASK_PREFIX), //
						getQueryParams(query), repository);
				if (queryUrl != null && !queryUrl.equals("") && url.startsWith(queryUrl)) {
					return query.getRepositoryUrl();
				}
			}
		}
		return null;
	}

	public static Map<String, String> getQueryParams(IRepositoryQuery query) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		Map<String, String> attributes = query.getAttributes();
		for (String name : attributes.keySet()) {
			if (name.startsWith(WebRepositoryConnector.PARAM_PREFIX)) {
				params.put(name, attributes.get(name));
			}
		}
		return params;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}

		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
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
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		TaskRepository repository = repositoryManager.getRepository(getConnectorKind(), repositoryUrl);
		if (repository != null) {
			String prefix = evaluateParams(repository.getProperty(PROPERTY_TASK_URL), repository);
			return prefix + taskId;
		}
		return null;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession session, IProgressMonitor monitor) {
		Map<String, String> queryParameters = getQueryParams(query);
		String queryUrl = evaluateParams(query.getUrl(), queryParameters, repository);
		try {
			String taskPrefixAttribute = query.getAttribute(KEY_TASK_PREFIX);
			if (!Util.isPresent(taskPrefixAttribute)) {
				return performRssQuery(queryUrl, monitor, resultCollector, repository);
			} else {
				String taskPrefix = evaluateParams(taskPrefixAttribute, queryParameters, repository);
				String queryPattern = evaluateParams(query.getAttribute(KEY_QUERY_PATTERN), queryParameters, repository);
				return performQuery(fetchResource(queryUrl, queryParameters, repository), queryPattern, taskPrefix,
						monitor, resultCollector, repository);
			}
		} catch (IOException e) {
			String msg = e.getMessage() == null ? e.toString() : e.getMessage();
			return new Status(IStatus.ERROR, TasksWebPlugin.ID_PLUGIN, IStatus.ERROR, //
					"Could not fetch resource: " + queryUrl + "\n" + msg, e);
		}
	}

	@Override
	public boolean isRepositoryConfigurationStale(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		return false;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData taskData) {
		TaskMapper mapper = new TaskMapper(taskData);
		task.setAttribute(KEY_TASK_PREFIX, mapper.getValue(KEY_TASK_PREFIX));
		mapper.applyTo(task);
	}

	public static IStatus performQuery(String resource, String regexp, String taskPrefix, IProgressMonitor monitor,
			TaskDataCollector resultCollector, TaskRepository repository) {
		NamedPattern p = new NamedPattern(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
				| Pattern.UNICODE_CASE | Pattern.CANON_EQ);

		Matcher matcher = p.matcher(resource);

		if (!matcher.find()) {
			return Status.OK_STATUS;
		} else {
			boolean isCorrect = true;
			do {
				if (p.getGroups().isEmpty()) {
					// "classic" mode, no named patterns
					if (matcher.groupCount() < 2) {
						isCorrect = false;
					}
					if (matcher.groupCount() >= 1) {
						String id = matcher.group(1);
						String description = matcher.groupCount() > 1 ? cleanup(matcher.group(2), repository) : null;
						description = unescapeHtml(description);

						TaskData data = createTaskData(repository, id);
						TaskMapper mapper = new TaskMapper(data, true);
						mapper.setTaskUrl(taskPrefix + id);
						mapper.setSummary(description);
						mapper.setValue(KEY_TASK_PREFIX, taskPrefix);
						resultCollector.accept(data);
					}
				} else {
					String id = p.group("Id", matcher);
					String description = p.group("Description", matcher);
					if (id == null || description == null) {
						isCorrect = false;
					}
					if (id != null) {
						description = unescapeHtml(description);
//						WebTask w = new WebTask(id, description, taskPrefix, repository.getUrl(), REPOSITORY_TYPE);

						String owner = cleanup(p.group("Owner", matcher), repository);
						owner = unescapeHtml(owner);
//						w.setOwner(owner);
						String type = cleanup(p.group("Type", matcher), repository);
						type = unescapeHtml(type);
//						w.setTaskKind(type);

						TaskData data = createTaskData(repository, id);
						TaskMapper mapper = new TaskMapper(data, true);
						mapper.setTaskUrl(taskPrefix + id);
						mapper.setSummary(description);
						mapper.setValue(KEY_TASK_PREFIX, taskPrefix);
						mapper.setOwner(owner);
						mapper.setTaskKind(type);

						String status = p.group("Status", matcher);
						if (status != null) {
							if (COMPLETED_STATUSES.contains(status.toLowerCase())) {
								// TODO set actual completion date here
								mapper.setCompletionDate(new Date());
							}
						}

						resultCollector.accept(data);
					}
				}
			} while (matcher.find() && !monitor.isCanceled());

			if (isCorrect) {
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, TasksWebPlugin.ID_PLUGIN, IStatus.ERROR,
						"Require two matching groups (taskId and summary). Check query regexp", null);
			}
		}
	}

	private static TaskData createTaskData(TaskRepository taskRepository, String id) {
		TaskData data = new TaskData(new TaskAttributeMapper(taskRepository), WebRepositoryConnector.REPOSITORY_TYPE,
				taskRepository.getRepositoryUrl(), id);
		data.setPartial(true);
		return data;
	}

	private static String unescapeHtml(String text) {
		if (text == null) {
			return null;
		}

		return StringEscapeUtils.unescapeHtml(text);
	}

	private static String cleanup(String text, TaskRepository repository) {
		if (text == null) {
			return null;
		}

		// Has to disable this for now. See bug 166737 and bug 166936
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

	public static IStatus performRssQuery(String queryUrl, IProgressMonitor monitor, TaskDataCollector resultCollector,
			TaskRepository repository) {
		SyndFeedInput input = new SyndFeedInput();
		try {
			SyndFeed feed = input.build(new XmlReader(new URL(queryUrl)));

			SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");

			@SuppressWarnings("unchecked")
			Iterator it;
			for (it = feed.getEntries().iterator(); it.hasNext();) {
				SyndEntry entry = (SyndEntry) it.next();

				String author = entry.getAuthor();
				if (author == null) {
					DCModule module = (DCModule) entry.getModule("http://purl.org/dc/elements/1.1/");
					author = module.getCreator();
				}

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

				TaskData data = createTaskData(repository, entryUri.replaceAll("-", "%2D"));
				TaskMapper schema = new TaskMapper(data, true);
				schema.setSummary(((date == null ? "" : df.format(date) + " - ") + entrTitle));
				schema.setCreationDate(date);
				schema.setOwner(author);
				resultCollector.accept(data);
			}
			return Status.OK_STATUS;
		} catch (Exception ex) {
			String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
			return new Status(IStatus.ERROR, TasksWebPlugin.ID_PLUGIN, IStatus.ERROR, //
					"Could not fetch resource: " + queryUrl + "\n" + msg, ex);
		}
	}

	public static String fetchResource(String url, Map<String, String> params, TaskRepository repository)
			throws IOException {
		HttpClient client = new HttpClient();
		WebUtil.configureHttpClient(client, USER_AGENT);
		AbstractWebLocation location = new TaskRepositoryLocationUiFactory().createWebLocation(repository);
		HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(client, location, null);

		loginRequestIfNeeded(client, hostConfiguration, params, repository);

		GetMethod method = new GetMethod(url);
		// method.setFollowRedirects(false);
		return requestResource(url, client, hostConfiguration, method);
	}

	private static void loginRequestIfNeeded(HttpClient client, HostConfiguration hostConfiguration,
			Map<String, String> params, TaskRepository repository) throws HttpException, IOException {
		if (repository.getCredentials(AuthenticationType.REPOSITORY) == null
				|| !isPresent(repository.getProperty(PROPERTY_LOGIN_REQUEST_URL))) {
			return;
		}

		String loginFormUrl = evaluateParams(repository.getProperty(PROPERTY_LOGIN_FORM_URL), params, repository);
		String loginToken = evaluateParams(repository.getProperty(PROPERTY_LOGIN_TOKEN_REGEXP), params, repository);
		if (isPresent(loginFormUrl) || isPresent(loginToken)) {
			GetMethod method = new GetMethod(loginFormUrl);
			// method.setFollowRedirects(false);
			String loginFormPage = requestResource(loginFormUrl, client, hostConfiguration, method);
			if (loginFormPage != null) {
				Pattern p = Pattern.compile(loginToken);
				Matcher m = p.matcher(loginFormPage);
				if (m.find()) {
					params.put(PARAM_PREFIX + PARAM_LOGIN_TOKEN, m.group(1));
				}
			}
		}

		String loginRequestUrl = evaluateParams(repository.getProperty(PROPERTY_LOGIN_REQUEST_URL), params, repository);
		requestResource(loginRequestUrl, client, hostConfiguration, getLoginMethod(params, repository));
	}

	public static HttpMethod getLoginMethod(Map<String, String> params, TaskRepository repository) {
		String requestMethod = repository.getProperty(PROPERTY_LOGIN_REQUEST_METHOD);
		String requestTemplate = repository.getProperty(PROPERTY_LOGIN_REQUEST_URL);
		String requestUrl = evaluateParams(requestTemplate, params, repository);

		if (REQUEST_GET.equals(requestMethod)) {
			return new GetMethod(requestUrl);
			// method.setFollowRedirects(false);
		}

		int n = requestUrl.indexOf('?');
		if (n == -1) {
			return new PostMethod(requestUrl);
		}

		PostMethod postMethod = new PostMethod(requestUrl.substring(0, n));
		// TODO this does not take into account escaped values
		n = requestTemplate.indexOf('?');
		String[] requestParams = requestTemplate.substring(n + 1).split("&");
		for (String requestParam : requestParams) {
			String[] nv = requestParam.split("=");
			if (nv.length == 1) {
				postMethod.addParameter(nv[0], "");
			} else {
				String value = evaluateParams(nv[1], getParams(repository, params), false);
				postMethod.addParameter(nv[0], value);
			}
		}
		return postMethod;
	}

	private static String requestResource(String url, HttpClient client, HostConfiguration hostConfiguration,
			HttpMethod method) throws IOException, HttpException {
		String refreshUrl = null;
		try {
			client.executeMethod(hostConfiguration, method);
//          int statusCode = client.executeMethod(method);
//			if (statusCode == 300 || statusCode == 301 || statusCode == 302 || statusCode == 303 || statusCode == 307) {
//				Header location = method.getResponseHeader("Location");
//				if (location != null) {
//					refreshUrl = location.getValue();
//					if (!refreshUrl.startsWith("/")) {
//						refreshUrl = "/" + refreshUrl;
//					}
//				}
//			}

			refreshUrl = getRefreshUrl(url, method);
			if (refreshUrl == null) {
				return method.getResponseBodyAsString();
			}
		} finally {
			method.releaseConnection();
		}

		method = new GetMethod(refreshUrl);
		try {
			client.executeMethod(hostConfiguration, method);
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
		return evaluateParams(value, getParams(repository, params), true);
	}

	public static String evaluateParams(String value, TaskRepository repository) {
		return evaluateParams(value, getParams(repository, null), true);
	}

	private static String evaluateParams(String value, Map<String, String> params, boolean encode) {
		if (value == null || value.indexOf("${") == -1) {
			return value;
		}

		int n = 0;
		int n1 = value.indexOf("${");
		StringBuilder evaluatedValue = new StringBuilder(value.length());
		while (n1 > -1) {
			evaluatedValue.append(value.substring(n, n1));
			int n2 = value.indexOf("}", n1);
			if (n2 > -1) {
				String key = value.substring(n1 + 2, n2);
				if (PARAM_SERVER_URL.equals(key) || PARAM_USER_ID.equals(key) || PARAM_PASSWORD.equals(key)) {
					evaluatedValue.append(evaluateParams(params.get(key), params, false));
				} else {
					String val = evaluateParams(params.get(PARAM_PREFIX + key), params, false);
					evaluatedValue.append(encode ? encode(val) : val);
				}
			}
			n = n2 + 1;
			n1 = value.indexOf("${", n2);
		}
		if (n > -1) {
			evaluatedValue.append(value.substring(n));
		}
		return evaluatedValue.toString();
	}

	private static Map<String, String> getParams(TaskRepository repository, Map<String, String> params) {
		Map<String, String> mergedParams = new LinkedHashMap<String, String>(repository.getProperties());
		mergedParams.put(PARAM_SERVER_URL, repository.getRepositoryUrl());
		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			mergedParams.put(PARAM_USER_ID, credentials.getUserName());
			mergedParams.put(PARAM_PASSWORD, credentials.getPassword());
		}
		if (params != null) {
			mergedParams.putAll(params);
		}
		return mergedParams;
	}

	private static String encode(String value) {
		try {
			return new URLCodec().encode(value);
		} catch (EncoderException ex) {
			return value;
		}
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
	public boolean hasLocalCompletionState(TaskRepository taskRepository, ITask task) {
		return true;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		return new TaskMapper(taskData).hasChanges(task);
	}

}
