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
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
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

	public WebRepositorySettingsPage(AbstractRepositoryConnector connector) {
		super(TITLE, DESCRIPTION, connector);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		for (RepositoryTemplate template : connector.getTemplates()) {
			if (repositoryLabelCombo.indexOf(template.label) == -1) {
				repositoryLabelCombo.add(template.label);
			}
		}

		repositoryLabelCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				String text = repositoryLabelCombo.getText();
				RepositoryTemplate template = connector.getTemplate(text);
				if(template != null) {
					serverUrlEditor.setStringValue(template.repositoryUrl);
					taskPrefixUrlEditor.setStringValue(template.taskPrefixUrl);
					newTaskUrlEditor.setStringValue(template.newTaskUrl);
					getContainer().updateButtons();
					return;
				}			
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
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
	public void updateProperties(TaskRepository repository) {
		repository.setProperty(WebRepositoryConnector.PROPERTY_TASK_PREFIX_URL, taskPrefixUrlEditor.getStringValue());
		repository.setProperty(WebRepositoryConnector.PROPERTY_NEW_TASK_URL, newTaskUrlEditor.getStringValue());
	}

}
