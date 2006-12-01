/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.monitor.usage.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Meghan Allen
 */
public class UsageSummaryEditorWizardPage extends WizardPage implements IWizardPage {

	private static final String TITLE = "Mylar Usage Summary Report";

	private static final String DESCRIPTION = "Summarizes Eclipse and Mylar usage activity.";

	private Button perspectiveCheckbox = null;

	private Button viewCheckbox = null;

	protected UsageSummaryEditorWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription(DESCRIPTION);
	}

	public UsageSummaryEditorWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.FILL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		createCheckboxes(container);

		setControl(container);

	}

	private void createCheckboxes(Composite parent) {
		Group checkboxGroup = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;

		checkboxGroup.setLayout(layout);
		checkboxGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		checkboxGroup.setText("Show usage summaries for:");
		checkboxGroup.setFont(parent.getFont());

		perspectiveCheckbox = new Button(checkboxGroup, SWT.CHECK | SWT.LEFT | SWT.NO_FOCUS);
		perspectiveCheckbox.setText("Use of perspectives");
		perspectiveCheckbox.setSelection(true);
		perspectiveCheckbox.addSelectionListener(new CheckboxSelectionListener());

		viewCheckbox = new Button(checkboxGroup, SWT.CHECK | SWT.LEFT | SWT.NO_FOCUS);
		viewCheckbox.setText("Use of views");
		viewCheckbox.setSelection(true);
		viewCheckbox.addSelectionListener(new CheckboxSelectionListener());
	}

	public boolean includePerspective() {
		return perspectiveCheckbox.getSelection();
	}

	public boolean includeViews() {
		return viewCheckbox.getSelection();
	}

	private class CheckboxSelectionListener extends SelectionAdapter {

		public void widgetSelected(SelectionEvent e) {
			if (!perspectiveCheckbox.getSelection() && !viewCheckbox.getSelection()) {
				setPageComplete(false);
			} else {
				setPageComplete(true);
			}
		}

	}

}
