/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.wizard;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

/**
 * Wizard page used to specify a XPlanner repository address, username, and password.
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerRepositorySettingsPage extends AbstractRepositorySettingsPage {

	@SuppressWarnings("unused")
	private static final String MESSAGE_FAILURE_CONNECT = Messages.XPlannerRepositorySettingsPage_COULD_NOT_CONNECT_TO_XPLANNER;

	private static final String TITLE = Messages.XPlannerRepositorySettingsPage_XPLANNER_REPPOSITORY_SETTINGS;

	private static final String DESCRIPTION = Messages.XPlannerRepositorySettingsPage_URL_EXAMPLE;

	public XPlannerRepositorySettingsPage(TaskRepository taskRepository) {
		super(TITLE, DESCRIPTION, taskRepository);
		setNeedsProxy(true);
		setNeedsHttpAuth(true);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// no additional controls for now
	}

	@Override
	protected boolean isValidUrl(String name) {
		boolean isValidUrl = false;
		if (name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) {
			try {
				new URL(name);
				isValidUrl = true;
			} catch (MalformedURLException e) {
			}
		}

		return isValidUrl;
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new XPlannerValidator(repository);
	}

	class XPlannerValidator extends Validator {
		final TaskRepository repository;

		public XPlannerValidator(TaskRepository repository) {
			this.repository = repository;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			XPlannerRepositoryUtils.validateRepository(repository);

			setStatus(new Status(IStatus.OK, XPlannerMylynUIPlugin.ID_PLUGIN, IStatus.OK,
					Messages.XPlannerRepositorySettingsPage_VALID_SETTINGS_FOUND, null));
		}
	}

	@Override
	public String getConnectorKind() {
		return XPlannerCorePlugin.CONNECTOR_KIND;
	}

}
