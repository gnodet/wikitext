/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *     Tasktop Technologies - initial API and implementation
 *     Eugene Kuleshov - improvements
 *     Frank Becker - fixes for bug 169916
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class SandboxUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Button enableErrorInterest;

	private Button incomingOverlaysButton;

	private Button activateOnOpen;

	private Button showTaskTrimButton;

	private Button useStrikethrough;

	public SandboxUiPreferencePage() {
		setPreferenceStore(ContextUiPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createTaskNavigationGroup(container);
		createTaskListGroup(container);
		createJavaGroup(container);

		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	private void createTaskNavigationGroup(Composite parent) {
		Group navigationGroup = new Group(parent, SWT.NONE);
		navigationGroup.setText("Task Navigation");
		navigationGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		navigationGroup.setLayout(new GridLayout());

		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();

		showTaskTrimButton = new Button(navigationGroup, SWT.CHECK);
		showTaskTrimButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		showTaskTrimButton.setText("Show Task Trim widget");
		showTaskTrimButton.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));
	}

	private void createTaskListGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task List");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();

		incomingOverlaysButton = new Button(group, SWT.CHECK);
		incomingOverlaysButton.setText("Use Synchronize View style incoming overlays and placement");
		incomingOverlaysButton.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT));

		activateOnOpen = new Button(group, SWT.CHECK);
		activateOnOpen.setText("Activate tasks on open");
		activateOnOpen.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED));

		useStrikethrough = new Button(group, SWT.CHECK);
		useStrikethrough.setText("Use strikethrough for completed tasks");
		useStrikethrough.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED));
	}

	private void createJavaGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Java");
		GridLayout gl = new GridLayout(1, false);
		group.setLayout(gl);

		enableErrorInterest = new Button(group, SWT.CHECK);
		enableErrorInterest.setText("Enable predicted interest of errors (significantly increases view refresh).");
		enableErrorInterest.setSelection(SandboxUiPlugin.getDefault().getPreferenceStore().getBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));
	}

	@Override
	public boolean performOk() {
		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();

		uiPreferenceStore.setValue(ITasksUiPreferenceConstants.SHOW_TRIM, showTaskTrimButton.getSelection());

		uiPreferenceStore.setValue(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED,
				useStrikethrough.getSelection());

		uiPreferenceStore.setValue(ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED, activateOnOpen.getSelection());

		uiPreferenceStore.setValue(ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT,
				incomingOverlaysButton.getSelection());
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			view.setSynchronizationOverlaid(incomingOverlaysButton.getSelection());
		}

		SandboxUiPlugin.getDefault().getPreferenceStore().setValue(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, enableErrorInterest.getSelection());
		return true;
	}

	@Override
	public boolean performCancel() {
		enableErrorInterest.setSelection(SandboxUiPlugin.getDefault().getPreferenceStore().getBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));

		useStrikethrough.setSelection(TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED));

		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		activateOnOpen.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED));
		showTaskTrimButton.setSelection(uiPreferenceStore.getBoolean(ITasksUiPreferenceConstants.SHOW_TRIM));
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		enableErrorInterest.setSelection(SandboxUiPlugin.getDefault().getPreferenceStore().getDefaultBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));

		useStrikethrough.setSelection(TasksUiPlugin.getDefault().getPreferenceStore().getDefaultBoolean(
				ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED));
	}

}
