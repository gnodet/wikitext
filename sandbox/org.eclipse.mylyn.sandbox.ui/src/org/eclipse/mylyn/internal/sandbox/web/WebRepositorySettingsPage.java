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
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
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

	protected void createAdditionalControls(Composite parent) {
		taskPrefixUrlEditor = new StringFieldEditor("taskPrefixUrl", "Task prefix URL:", StringFieldEditor.UNLIMITED, parent);
		taskPrefixUrlEditor.setPropertyChangeListener(this);
		
		newTaskUrlEditor = new StringFieldEditor("newTaskUrl", "New task URL:", StringFieldEditor.UNLIMITED, parent);
		newTaskUrlEditor.setPropertyChangeListener(this);
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
		if(source==taskPrefixUrlEditor || source == newTaskUrlEditor) {
			getWizard().getContainer().updateButtons();
		}
	}

	@Override
	public TaskRepository createTaskRepository() {
		// ignore
		return new WebTaskRepository(getConnector().getRepositoryType(), 
				getServerUrl(), taskPrefixUrlEditor.getStringValue(), newTaskUrlEditor.getStringValue());
	}
	
}

