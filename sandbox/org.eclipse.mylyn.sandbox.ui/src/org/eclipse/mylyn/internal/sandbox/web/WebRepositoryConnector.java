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

	// TODO: Remove, replaced by extension point org.eclipse.mylar.tasks.ui.templates
	public static WebRepositoryTemplate[] REPOSITORY_TEMPLATES = {
		new WebRepositoryTemplate(
			"Subclipse (IssueZilla)",
			"http://subclipse.tigris.org/issues/",
			"http://subclipse.tigris.org/issues/enter_bug.cgi?component=subclipse",
			"http://subclipse.tigris.org/issues/show_bug.cgi?id=",
			"http://subclipse.tigris.org/issues/buglist.cgi?issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
			"<a href=\"show_bug.cgi\\?id\\=(.+?)\">.+?<span class=\"summary\">(.+?)</span>"),
		new WebRepositoryTemplate(
			"GlasFish (IssueZilla)",
			"https://glassfish.dev.java.net/servlets/ProjectIssues",
			"https://glassfish.dev.java.net/issues/enter_bug.cgi?issue_type=DEFECT",
			"https://glassfish.dev.java.net/issues/show_bug.cgi?id=",
			"https://glassfish.dev.java.net/issues/buglist.cgi?component=glassfish&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
			"<a href=\"show_bug.cgi\\?id\\=(.+?)\">.+?<span class=\"summary\">(.+?)</span>"),
		new WebRepositoryTemplate("Spring Framework (Jira)", 
			"http://opensource.atlassian.com/projects/spring/browse/SPR",
			"http://opensource.atlassian.com/projects/spring/secure/CreateIssue!default.jspa",
			"http://opensource.atlassian.com/projects/spring/browse/",
			"http://opensource.atlassian.com/projects/spring/secure/IssueNavigator.jspa?reset=true&mode=hide&pid=10000&resolution=-1&sorter/field=updated&sorter/order=DESC",
			"<td class=\"nav summary\">\\s+?<a href=\"/projects/spring/browse/(.+?)\".+?>(.+?)</a>"),
		new WebRepositoryTemplate("SpringIDE (Trac)",
			"http://springide.org/project/",
			"http://springide.org/project/newticket",
			"http://springide.org/project/ticket/", 
			"http://springide.org/project/query?status=new&status=assigned&status=reopened&order=id",
			"<td class=\"summary\"><a href=\"/project/ticket/(.+?)\" title=\"View ticket\">(.+?)</a></td>"), 
		new WebRepositoryTemplate("edgewall.org (Trac)",
			"http://trac.edgewall.org/",
			"http://trac.edgewall.org/newticket",
			"http://trac.edgewall.org/ticket/",
			"http://trac.edgewall.org/query?status=new&status=assigned&status=reopened&order=id",
			"<td class=\"summary\"><a href=\"/ticket/(.+?)\" title=\"View ticket\">(.+?)</a></td>"),
		new WebRepositoryTemplate("ASM (GForge)", 
			"http://forge.objectweb.org/tracker/?atid=100023&group_id=23",
			"http://forge.objectweb.org/tracker/?func=add&group_id=23&atid=100023",
			"http://forge.objectweb.org/tracker/index.php?func=detail&group_id=23&atid=100023&aid=",
			"http://forge.objectweb.org/tracker/?atid=100023&group_id=23",
			"<a class=\"tracker\" href=\"/tracker/index.php\\?func=detail&aid=(.+?)&group_id=23&atid=100023\">(.+?)</a></td>"),
		new WebRepositoryTemplate("Azureus (SourceForge)", 
			"http://sourceforge.net/tracker/?atid=575154&group_id=84122",
			"http://sourceforge.net/tracker/?func=add&group_id=84122&atid=575154",
			"http://sourceforge.net/tracker/index.php?func=detail&group_id=84122&atid=575154&aid=",
			"http://sourceforge.net/tracker/?atid=575154&group_id=84122",
		    "<a href=\"/tracker/index.php\\?func=detail&amp;aid=(.+?)&amp;group_id=84122&amp;atid=575154\">(.+?)</a>"),
		new WebRepositoryTemplate("phpbb.com (phpBB)", 
			"http://www.phpbb.com/phpBB/",
			"http://www.phpbb.com/phpBB/posting.php?mode=newtopic&f=1",
			"http://www.phpbb.com/phpBB/viewtopic.php?t=",
			"http://www.phpbb.com/phpBB/viewforum.php?f=1",
		    "<a href=\"viewtopic.php\\?t=(\\d+?)(?:&.+?)?\" class=\"topictitle\">(.+?)</a>"),
		new WebRepositoryTemplate("Spring IDE Forum (vBulletin)", 
			"http://forum.springframework.org/forumdisplay.php?f=32",
			"http://forum.springframework.org/newthread.php?do=newthread&f=32",
			"http://forum.springframework.org/showthread.php?t=",
			"http://forum.springframework.org/forumdisplay.php?f=32",
		    "<a href=\"showthread.php\\?.+?t=(\\d+?)\" id=\"thread_title_\\1\">(.+?)</a>"),
	};
	
	
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
		return new WebTaskWizard(taskRepository);
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

//	public static WebRepositoryTemplate getTemplate(String label) {
//		for (WebRepositoryTemplate template : templates) {
//			if(label.equals(template.label)) {
//				return template;
//			}
//		}
//		return null;
//	}
}

