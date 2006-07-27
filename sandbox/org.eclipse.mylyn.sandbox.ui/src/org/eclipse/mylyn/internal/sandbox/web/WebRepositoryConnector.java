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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewWebTaskWizard;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Generic connector for web based issue tracking systems
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_TYPE = "web";
	
	public static final String PROPERTY_NEW_TASK_URL = "newtaskurl";
	
	public static final String PROPERTY_TASK_PREFIX_URL = "taskprefixurl";

	public String getRepositoryType() {
		return REPOSITORY_TYPE;
	}
	
	public String getLabel() {
		return "Generic web-based repository";
	}
	
	@Override
	public String[] repositoryPropertyNames() {
		return new String[] { PROPERTY_NEW_TASK_URL, PROPERTY_TASK_PREFIX_URL};
	}
	
	public List<String> getSupportedVersions() {
		return Collections.emptyList();
	}
	
	public boolean canCreateNewTask() {
		return true;
	}

	public boolean canCreateTaskFromKey() {
		return true;
	}

	
	// Support
	
	public ITask createTaskFromExistingKey(TaskRepository repository, final String id) {
		if(REPOSITORY_TYPE.equals(repository.getKind())) {
			String taskPrefix = repository.getProperty(PROPERTY_TASK_PREFIX_URL);
			
			final WebTask task = new WebTask(id, id, taskPrefix, repository.getUrl());

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
	
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, MultiStatus queryStatus) {
		if(query instanceof WebQuery) {
			String queryUrl = query.getUrl();
			String regexp = ((WebQuery) query).getRegexp();
			String taskPrefix = ((WebQuery) query).getTaskPrefix();
			String repositoryUrl = query.getRepositoryUrl();
			
			try {
				return performQuery(fetchResource(queryUrl), regexp, taskPrefix, repositoryUrl, monitor, queryStatus);

			} catch (IOException ex) {
				queryStatus.add(new Status(IStatus.OK, TasksUiPlugin.PLUGIN_ID, IStatus.OK,
						"Could not fetch resource: " + queryUrl, ex));
			}
		}
		return new ArrayList<AbstractQueryHit>();
	}

	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO
	}
	

	// UI
	
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new WebRepositorySettingsPage(this);
	}
	
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
//		return new WebTaskWizard(taskRepository);
		return new NewWebTaskWizard(taskRepository, taskRepository.getProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL)); 
	}

	
	public IWizard getNewQueryWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		return new WebQueryWizard(taskRepository);
	}
	
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					query.getRepositoryKind(), query.getRepositoryUrl());
			if (repository == null)
				return;

			IWizard wizard = null;
			if (query instanceof WebQuery) {
				wizard = new WebQueryEditWizard(repository, query);
			}

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Web Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
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
	
	
	public static List<AbstractQueryHit> performQuery(StringBuffer resource, String regexp, String taskPrefix, String repositoryUrl, IProgressMonitor monitor, MultiStatus queryStatus) {
		List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
		
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
		Matcher matcher = p.matcher(resource);

		if(!matcher.find()) {
			queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
					"Unable to parse resource. Check query regexp", null));
		} else {
			boolean isCorrect = true;
	    	do {
	    		if (matcher.groupCount() < 2) {
	    			isCorrect = false;
	    		}
	    		if (matcher.groupCount() >= 1) {
	    			String id = matcher.group(1);
	    			String description = matcher.groupCount()>1 ? matcher.group(2) : null;
	    			hits.add(new WebQueryHit(id, id+": "+description, taskPrefix, repositoryUrl));
	    		}
	    	} while(matcher.find() && !monitor.isCanceled());

	    	if(isCorrect) {
	    		queryStatus.add(Status.OK_STATUS);
	    	} else {
	    		queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
	    				"Require two matching groups (id and description). Check query regexp", null));
	    	}
		}
		
		return hits;
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

}

