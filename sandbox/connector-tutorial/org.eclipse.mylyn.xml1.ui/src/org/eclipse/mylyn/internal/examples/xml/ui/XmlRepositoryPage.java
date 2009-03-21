/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.examples.xml.ui;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Steffen Pingel
 */
public class XmlRepositoryPage extends AbstractRepositorySettingsPage {

	public XmlRepositoryPage(TaskRepository taskRepository) {
		super("XML Connector Settings", "Specify a directory", taskRepository);
		setNeedsAnonymousLogin(true);
		setNeedsAdvanced(true);
		setNeedsEncoding(false);
		setNeedsHttpAuth(false);
		setNeedsProxy(false);
	}

	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		String location = getLocation(repository).getAbsolutePath();
		repository.setProperty(XmlCorePlugin.REPOSITORY_KEY_PATH, location);
	}

	private File getLocation(TaskRepository repository) {
		File root = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
		return new File(root, repository.getRepositoryUrl());
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		addRepositoryTemplatesToServerUrlCombo();
	}

	@Override
	public String getConnectorKind() {
		return XmlCorePlugin.CONNECTOR_KIND;
	}

	@Override
	protected Validator getValidator(final TaskRepository repository) {
		return new Validator() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				File location = getLocation(repository);
				if (!location.exists()) {
					throw new CoreException(new Status(IStatus.ERROR, XmlUiPlugin.ID_PLUGIN, NLS.bind(
							"Folder ''{0}'' does not exist", location.getName())));
				}
			}
		};
	}

	@Override
	protected boolean isValidUrl(String url) {
		return true;
	}

	@Override
	protected void repositoryTemplateSelected(RepositoryTemplate template) {
		repositoryLabelEditor.setStringValue(template.label);
		setUrl(template.repositoryUrl);
		setAnonymous(template.anonymous);
		getContainer().updateButtons();
	}

}
