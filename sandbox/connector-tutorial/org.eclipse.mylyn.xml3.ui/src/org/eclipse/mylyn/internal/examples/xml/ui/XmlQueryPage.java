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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.examples.xml.core.XmlClient;
import org.eclipse.mylyn.internal.examples.xml.core.XmlConfiguration;
import org.eclipse.mylyn.internal.examples.xml.core.XmlConnector;
import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Steffen Pingel
 */
public class XmlQueryPage extends AbstractRepositoryQueryPage2 {

	private Text summaryText;

	private Combo projectCombo;

	public XmlQueryPage(TaskRepository repository, IRepositoryQuery query) {
		super("xml", repository, query);
		setTitle("XML Search");
		setDescription("Specify search parameters.");
	}

	@Override
	protected void createPageContent(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText("Summary:");
		summaryText = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(summaryText);

		label = new Label(composite, SWT.NONE);
		label.setText("Project:");
		projectCombo = new Combo(composite, SWT.NONE);
	}

	@Override
	protected void doRefresh() {
		XmlConfiguration configuration = getClient().getConfiguration();
		projectCombo.removeAll();
		for (String project : configuration.getProjects()) {
			projectCombo.add(project);
		}
	}

	private XmlClient getClient() {
		return ((XmlConnector) getConnector()).getClient(getTaskRepository());
	}

	@Override
	protected boolean hasRepositoryConfiguration() {
		return getClient().hasConfiguration();
	}

	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		String summary = query.getAttribute(XmlCorePlugin.QUERY_KEY_SUMMARY);
		if (summary != null) {
			summaryText.setText(summary);
		}
		String project = query.getAttribute(XmlCorePlugin.QUERY_KEY_PROJECT);
		if (project != null) {
			projectCombo.setText(project);
		}
		return true;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		if (getQueryTitle() != null) {
			query.setSummary(getQueryTitle());
		}
		query.setAttribute(XmlCorePlugin.QUERY_KEY_SUMMARY, summaryText.getText());
		query.setAttribute(XmlCorePlugin.QUERY_KEY_PROJECT, projectCombo.getText());
	}

}
