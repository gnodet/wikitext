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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.tasks.core.WebTask;
import org.eclipse.mylar.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.IQueryHitCollector;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

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
		return new String[] { PROPERTY_NEW_TASK_URL, PROPERTY_TASK_PREFIX_URL};
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
	
	public ITask createTaskFromExistingKey(TaskRepository repository, final String id) {
		if(REPOSITORY_TYPE.equals(repository.getKind())) {
			String taskPrefix = repository.getProperty(PROPERTY_TASK_PREFIX_URL);
			
			final WebTask task = new WebTask(id, id, taskPrefix, repository.getUrl(), WebRepositoryConnector.REPOSITORY_TYPE);

			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(taskPrefix+id) {
					protected void setTitle(String pageTitle) {
						task.setDescription(id+": "+pageTitle);
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
			if(getRepositoryType().equals(repository.getKind())) {
				if(url.startsWith(repository.getProperty(PROPERTY_TASK_PREFIX_URL))) {
					return repository.getUrl();
				}
			}
		}
		
		for (AbstractRepositoryQuery query : TasksUiPlugin.getTaskListManager().getTaskList().getQueries()) {
			if(query instanceof WebQuery) {
				WebQuery webQuery = (WebQuery) query;
				if(url.startsWith(webQuery.getTaskPrefix())) {
					return webQuery.getRepositoryUrl();
				}
			}			
		}
		
		return null;
	}
	
//	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, MultiStatus queryStatus) {
//		if(query instanceof WebQuery) {
//			String queryUrl = query.getUrl();
//			String regexp = ((WebQuery) query).getRegexp();
//			String taskPrefix = ((WebQuery) query).getTaskPrefix();
//			String repositoryUrl = query.getRepositoryUrl();
//			
//			try {
//				return performQuery(fetchResource(queryUrl), regexp, taskPrefix, repositoryUrl, monitor, queryStatus);
//
//			} catch (IOException ex) {
//				queryStatus.add(new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
//						"Could not fetch resource: " + queryUrl, ex));
//			}
//		}
//		return new ArrayList<AbstractQueryHit>();
//	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, IQueryHitCollector resultCollector) {
		if(query instanceof WebQuery) {
			String queryUrl = query.getUrl();
			String regexp = ((WebQuery) query).getRegexp();
			String taskPrefix = ((WebQuery) query).getTaskPrefix();
			String repositoryUrl = query.getRepositoryUrl();
			
			try {
				return performQuery(fetchResource(queryUrl), regexp, taskPrefix, repositoryUrl, monitor, resultCollector);

			} catch (IOException ex) {
				return new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
						"Could not fetch resource: " + queryUrl, ex);
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

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository, Set<AbstractRepositoryTask> tasks) throws Exception {
		// not supported
		return Collections.emptySet();
	}

	public IOfflineTaskHandler getOfflineTaskHandler() {
		// not supported
		return null;
	}
	
	
	public static IStatus performQuery(StringBuffer resource, String regexp, String taskPrefix, String repositoryUrl, IProgressMonitor monitor, IQueryHitCollector collector) {
		//List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
		
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
		Matcher matcher = p.matcher(resource);

		if(!matcher.find()) {
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
	    			String description = matcher.groupCount()>1 ? matcher.group(2) : null;
	    			try {
						collector.accept(new WebQueryHit(id, id+": "+description, taskPrefix, repositoryUrl));
					} catch (CoreException e) {
						return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
								"Unable collect results.", e);
					}
	    		}
	    	} while(matcher.find() && !monitor.isCanceled());

	    	if(isCorrect) {
	    		return Status.OK_STATUS;
	    	} else {
	    		return new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
	    				"Require two matching groups (id and description). Check query regexp", null);
	    	}
		}
	}

	// TODO use commons http client
	public static StringBuffer fetchResource(String url) throws IOException {
		URL u = new URL(url);
		InputStream is = null;
		try {
			is = u.openStream();
		    BufferedReader r = new BufferedReader(new InputStreamReader(is));

		    StringBuffer resource = new StringBuffer();
		    String line;
		    while(true) {
		    	int retryCount = 0;
		    	do {
		    		if(retryCount>0) {
		    			try {
							Thread.sleep(1000L);
						} catch (InterruptedException ex) {
							// ignore
						}
		    		}
		    		line = r.readLine();
		    		retryCount++;
		    	} while(line==null && retryCount<5);
		    	if(line==null) {
		    		break;
		    	}
		    	resource.append(line).append("\n");
		    }
		    return resource;
		    
		} finally {
			if(is!=null) {
				is.close();
			}
		}
		
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public boolean validate(TaskRepository repository) {
		return true;
	}

}

