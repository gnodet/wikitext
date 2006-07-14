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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Settings page for generic web-based repository connector
 * 
 * @author Eugene Kuleshov
 */
public class WebRepositorySettingsPage extends AbstractRepositorySettingsPage implements IPropertyChangeListener {
	private static final String TITLE = "Web Repository Settings";

	private static final String DESCRIPTION = "Generic web-based repository connector";

	protected StringFieldEditor taskPrefixUrlEditor;

	protected StringFieldEditor newTaskUrlEditor;

	private static WebRepositoryInfo[] REPOSITORY_TEMPLATES = {
			new WebRepositoryInfo(
					"Subclipse (IssueZilla)",
					// "http://subclipse.tigris.org/issues/buglist.cgi?issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
					"http://subclipse.tigris.org/issues/",
					"http://subclipse.tigris.org/issues/enter_bug.cgi?component=subclipse",
					"http://subclipse.tigris.org/issues/show_bug.cgi?id="),
			new WebRepositoryInfo(
					"GlasFish (IssueZilla)",
					// "https://glassfish.dev.java.net/issues/buglist.cgi?component=glassfish&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
					"https://glassfish.dev.java.net/servlets/ProjectIssues",
					"https://glassfish.dev.java.net/issues/enter_bug.cgi?issue_type=DEFECT",
					"https://glassfish.dev.java.net/issues/show_bug.cgi?id="),
			new WebRepositoryInfo("Spring Framework (Jira)",
					"http://opensource.atlassian.com/projects/spring/browse/SPR",
					"http://opensource.atlassian.com/projects/spring/secure/CreateIssue!default.jspa",
					"http://opensource.atlassian.com/projects/spring/browse/"),
			new WebRepositoryInfo("ASM (GForge)", "http://forge.objectweb.org/tracker/?atid=100023&group_id=23",
					"http://forge.objectweb.org/tracker/?func=add&group_id=23&atid=100023",
					"http://forge.objectweb.org/tracker/index.php?func=detail&group_id=23&atid=100023&aid="),
			new WebRepositoryInfo("edgewall.org (Trac)",
					// "http://trac.edgewall.org/query?status=new&status=assigned&status=reopened&order=id"
					"http://trac.edgewall.org/", "http://trac.edgewall.org/newticket",
					"http://trac.edgewall.org/ticket/"), };

	public WebRepositorySettingsPage(AbstractRepositoryConnector connector) {
		super(TITLE, DESCRIPTION, connector);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		for (WebRepositoryInfo info : REPOSITORY_TEMPLATES) {
			if (repositoryLabelCombo.indexOf(info.label) == -1) {
				repositoryLabelCombo.add(info.label);
			}
		}

		repositoryLabelCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				WebRepositoryInfo info = getInfo(repositoryLabelCombo.getText());
				if (info != null) {
					serverUrlEditor.setStringValue(info.repositoryUrl);
					taskPrefixUrlEditor.setStringValue(info.taskPrefix);
					newTaskUrlEditor.setStringValue(info.newTaskUrl);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			private WebRepositoryInfo getInfo(String text) {
				for (WebRepositoryInfo info : REPOSITORY_TEMPLATES) {
					if (text.equals(info.label)) {
						return info;
					}
				}
				return null;
			}
		});

		taskPrefixUrlEditor = new StringFieldEditor("taskPrefixUrl", "Task prefix URL:", StringFieldEditor.UNLIMITED,
				parent);
		taskPrefixUrlEditor.setPropertyChangeListener(this);

		newTaskUrlEditor = new StringFieldEditor("newTaskUrl", "New task URL:", StringFieldEditor.UNLIMITED, parent);
		newTaskUrlEditor.setPropertyChangeListener(this);

		if (repository != null) {
			taskPrefixUrlEditor.setStringValue(repository.getProperty(WebRepositoryConnector.PROPERTY_TASK_PREFIX_URL));
			newTaskUrlEditor.setStringValue(repository.getProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL));
		}
	}

	protected boolean isValidUrl(String name) {
		return true;
	}

	protected void validateSettings() {
		// ignore
	}

	// IPropertyChangeListener

	public void propertyChange(PropertyChangeEvent event) {
		Object source = event.getSource();
		if (source == taskPrefixUrlEditor || source == newTaskUrlEditor) {
			getWizard().getContainer().updateButtons();
		}
	}

	@Override
	public TaskRepository createTaskRepository() {
		TaskRepository repository = super.createTaskRepository();
		repository.setProperty(WebRepositoryConnector.PROPERTY_TASK_PREFIX_URL, taskPrefixUrlEditor.getStringValue());
		repository.setProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL, newTaskUrlEditor.getStringValue());
		return repository;
	}

	private static class WebRepositoryInfo {
		public final String label;

		public final String repositoryUrl;

		public final String newTaskUrl;

		public final String taskPrefix;

		public WebRepositoryInfo(String label, String repositoryUrl, String newTaskUrl, String taskPrefix) {
			this.label = label;
			this.repositoryUrl = repositoryUrl;
			this.newTaskUrl = newTaskUrl;
			this.taskPrefix = taskPrefix;
		}

	}

}
