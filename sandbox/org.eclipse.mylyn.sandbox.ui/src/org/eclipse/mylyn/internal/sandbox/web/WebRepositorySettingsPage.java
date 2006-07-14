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
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
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

	private static RepositoryTemplate[] REPOSITORY_TEMPLATES = {
			new RepositoryTemplate(
					"Subclipse (IssueZilla)",
					// "http://subclipse.tigris.org/issues/buglist.cgi?issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
					"http://subclipse.tigris.org/issues/", "version",
					"http://subclipse.tigris.org/issues/enter_bug.cgi?component=subclipse",
					"http://subclipse.tigris.org/issues/show_bug.cgi?id=", false),
			new RepositoryTemplate(
					"GlasFish (IssueZilla)",
					// "https://glassfish.dev.java.net/issues/buglist.cgi?component=glassfish&issue_status=NEW&issue_status=STARTED&issue_status=REOPENED&order=Issue+Number",
					"https://glassfish.dev.java.net/servlets/ProjectIssues", "version",
					"https://glassfish.dev.java.net/issues/enter_bug.cgi?issue_type=DEFECT",
					"https://glassfish.dev.java.net/issues/show_bug.cgi?id=", false),
			new RepositoryTemplate("Spring Framework (Jira)",
					"http://opensource.atlassian.com/projects/spring/browse/SPR", "version",
					"http://opensource.atlassian.com/projects/spring/secure/CreateIssue!default.jspa",
					"http://opensource.atlassian.com/projects/spring/browse/", false),
			new RepositoryTemplate("ASM (GForge)", "http://forge.objectweb.org/tracker/?atid=100023&group_id=23", "version",
					"http://forge.objectweb.org/tracker/?func=add&group_id=23&atid=100023",
					"http://forge.objectweb.org/tracker/index.php?func=detail&group_id=23&atid=100023&aid=", false),
			new RepositoryTemplate("edgewall.org (Trac)",
					// "http://trac.edgewall.org/query?status=new&status=assigned&status=reopened&order=id"
					"http://trac.edgewall.org/", "version", "http://trac.edgewall.org/newticket",
					"http://trac.edgewall.org/ticket/", false), };

	public WebRepositorySettingsPage(AbstractRepositoryConnector connector) {
		super(TITLE, DESCRIPTION, connector);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		for (RepositoryTemplate info : REPOSITORY_TEMPLATES) {
			if (repositoryLabelCombo.indexOf(info.label) == -1) {
				repositoryLabelCombo.add(info.label);
			}
		}

		repositoryLabelCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				RepositoryTemplate info = getInfo(repositoryLabelCombo.getText());
				if (info != null) {
					serverUrlEditor.setStringValue(info.repositoryUrl);
					taskPrefixUrlEditor.setStringValue(info.taskPrefix);
					newTaskUrlEditor.setStringValue(info.newTaskUrl);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			private RepositoryTemplate getInfo(String text) {
				for (RepositoryTemplate info : REPOSITORY_TEMPLATES) {
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

}
