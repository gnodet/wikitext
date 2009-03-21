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

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;

/**
 * @author Steffen Pingel
 */
public class XmlConnectorUi extends AbstractRepositoryConnectorUi {

	public XmlConnectorUi() {
	}

	@Override
	public String getConnectorKind() {
		return XmlCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new XmlRepositoryPage(taskRepository);
	}

	@Override
	public boolean hasSearchPage() {
		// ignore
		return false;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping selection) {
		// ignore
		return null;
	}

	@Override
	public IWizard getQueryWizard(TaskRepository taskRepository, IRepositoryQuery queryToEdit) {
		// ignore
		return null;
	}

}
