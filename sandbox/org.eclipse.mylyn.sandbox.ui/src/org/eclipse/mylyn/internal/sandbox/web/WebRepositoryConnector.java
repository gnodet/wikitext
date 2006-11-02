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

package org.eclipse.mylar.internal.sandbox.web;

import java.io.IOException;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.tasks.core.WebClientUtil;
import org.eclipse.mylar.internal.tasks.core.WebTask;
import org.eclipse.mylar.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

//import com.sun.syndication.feed.module.DCModule;
//import com.sun.syndication.feed.synd.SyndEntry;
//import com.sun.syndication.feed.synd.SyndFeed;
//import com.sun.syndication.io.SyndFeedInput;
//import com.sun.syndication.io.XmlReader;


/**
 * Generic connector for web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_TYPE = "web";

	public static final String PROPERTY_NEW_TASK_URL = "newtaskurl";

	public static final String PROPERTY_TASK_PREFIX_URL = "taskprefixurl";

	public static final String TASK_REGEXP = "taskRegexp";

	public String getRepositoryType() {
		return REPOSITORY_TYPE;
	}

	public String getLabel() {
		return "Generic web-based access (Advanced)";
	}

	@Override
	public String[] repositoryPropertyNames() {
		return new String[] { PROPERTY_NEW_TASK_URL, PROPERTY_TASK_PREFIX_URL };
	}

	public List<String> getSupportedVersions() {
		return Collections.emptyList();
	}

	public boolean canCreateNewTask(TaskRepository repository) {
		return repository.hasProperty(PROPERTY_NEW_TASK_URL);
	}

	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return repository.hasProperty(PROPERTY_TASK_PREFIX_URL);
	}

	// Support

	public ITask createTaskFromExistingKey(TaskRepository repository, final String id, Proxy proxySettings)
			throws CoreException {
		if (REPOSITORY_TYPE.equals(repository.getKind())) {
			String taskPrefix = repository.getProperty(PROPERTY_TASK_PREFIX_URL);

			final WebTask task = new WebTask(id, id, taskPrefix, repository.getUrl(),
					WebRepositoryConnector.REPOSITORY_TYPE);

			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(taskPrefix + id) {
				protected void setTitle(String pageTitle) {
					task.setDescription(id + ": " + pageTitle);
					TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
				}
			};
			job.schedule();

			return task;
		}

		return null;
	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		// lookup repository using task prefix url
		for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
			if (getRepositoryType().equals(repository.getKind())) {
				if (url.startsWith(repository.getProperty(PROPERTY_TASK_PREFIX_URL))) {
					return repository.getUrl();
				}
			}
		}

		for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
			if (query instanceof WebQuery) {
				WebQuery webQuery = (WebQuery) query;
				if (url.startsWith(webQuery.getTaskPrefix())) {
					return webQuery.getRepositoryUrl();
				}
			}
		}

		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, Proxy proxySettings,
			IProgressMonitor monitor, QueryHitCollector resultCollector) {
		if (query instanceof WebQuery) {
			String queryUrl = query.getUrl();
			String regexp = ((WebQuery) query).getRegexp();
			String taskPrefix = ((WebQuery) query).getTaskPrefix();
			String repositoryUrl = query.getRepositoryUrl();
			String repositoryUser = repository.getUserName();
			String repositoryPassword = repository.getPassword();
			
			try {
//				if (regexp != null && regexp.trim().length() > 0) {
					return performQuery(fetchResource(queryUrl, repositoryUser, repositoryPassword), regexp,
							taskPrefix, repositoryUrl, monitor, resultCollector);
//				} else {
//					return performRssQuery(queryUrl, taskPrefix, repositoryUrl, repositoryUser, repositoryPassword,
//							monitor, resultCollector);
//				}				

			} catch (IOException ex) {
				return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Could not fetch resource: "
						+ queryUrl, ex);
			}
		}
		return Status.OK_STATUS;
	}

	public void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO
	}

	public IAttachmentHandler getAttachmentHandler() {
		// not supported
		return null;
	}

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws Exception {
		// not supported
		return Collections.emptySet();
	}

	public IOfflineTaskHandler getOfflineTaskHandler() {
		// not supported
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	public static IStatus performQuery(String resource, String regexp, String taskPrefix, String repositoryUrl,
			IProgressMonitor monitor, QueryHitCollector collector) {

		// List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();

		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL
				| Pattern.UNICODE_CASE | Pattern.CANON_EQ);
		Matcher matcher = p.matcher(resource);

		if (!matcher.find()) {
			return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"Unable to parse resource. Check query regexp", null);
		} else {
			boolean isCorrect = true;
			do {
				if (matcher.groupCount() < 2) {
					isCorrect = false;
				}
				if (matcher.groupCount() >= 1) {
					String id = matcher.group(1);
					String description = matcher.groupCount() > 1 ? matcher.group(2) : null;
					try {
						collector.accept(new WebQueryHit(TasksUiPlugin.getTaskListManager().getTaskList(), id, id + ": " + description, taskPrefix, repositoryUrl));
					} catch (CoreException e) {
						return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
								"Unable collect results.", e);
					}
				}
			} while (matcher.find() && !monitor.isCanceled());

			if (isCorrect) {
				return Status.OK_STATUS;
			} else {
				return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
						"Require two matching groups (id and description). Check query regexp", null);
			}
		}
	}

/*	
	public static IStatus performRssQuery(String queryUrl, String taskPrefix, String repositoryUrl, 
			String repositoryUser, String repositoryPassword, IProgressMonitor monitor, QueryHitCollector collector) {
        SyndFeedInput input = new SyndFeedInput();
        try {
			SyndFeed feed = input.build(new XmlReader(new URL(queryUrl)));

			SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm");
			
			for (Iterator it = feed.getEntries().iterator(); it.hasNext(); ) {
				SyndEntry entry = (SyndEntry) it.next();

				Date date = entry.getUpdatedDate();
				if(date==null) {
					date = entry.getPublishedDate();
				}
				if(date==null) {
					DCModule module = (DCModule) entry.getModule("http://purl.org/dc/elements/1.1/");
					date = module.getDate();
				}
				if(date==null) {
					// TODO 
				}
				
				String entryUri = entry.getUri();
				if(entryUri.startsWith(taskPrefix)) {
					String id = df.format(date);  // entryUri.substring(taskPrefix.length());
					
	    			try {
						collector.accept(new WebQueryHit(id, id+": "+entry.getTitle(), taskPrefix, repositoryUrl));
					} catch (CoreException e) {
						return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
								"Unable collect results.", e);
	    			}
				}
			}
			return Status.OK_STATUS;			
			
		} catch (Exception ex) {
			return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
					"Could not fetch resource: " + queryUrl, ex);
		}
	}
*/

	public static String fetchResource(String url, String user, String password) throws IOException {
		HttpClient client = new HttpClient();
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		WebClientUtil.setupHttpClient(client, proxySettings, url, user, password);	
		
		GetMethod get = new GetMethod(url);
		try {
			client.executeMethod(get);
			Header refreshHeader = get.getResponseHeader("Refresh");

			if (refreshHeader != null) {
				String value = refreshHeader.getValue();
				int n = value.indexOf(";url=");
				if (n != -1) {
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

					get = new GetMethod(refreshUrl);
					client.executeMethod(get);
				}
			}
			return get.getResponseBodyAsString();

		} finally {
			get.releaseConnection();
		}
	}

}
