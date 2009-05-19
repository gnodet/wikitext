/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.xplanner.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

@SuppressWarnings("restriction")
public class XPlannerTimeTrackingEditorPart extends AbstractTaskEditorPart implements ITaskActivityListener,
		SelectionListener {

	private Button useTimeTrackingButton;

	private Button addToCurrentTimeButton;

	private Button replaceCurrentTimeButton;

	private Button roundToHalfHourButton;

	private boolean useAutoTimeTracking;

	private boolean roundToHalfHour;

	private boolean addToCurrentTime;

	private Section timeTrackingSection;

	private final ITask task;

	private final XPlannerTaskEditor editor;

	public XPlannerTimeTrackingEditorPart(ITask task, XPlannerTaskEditor editor) {
		this.task = task;
		this.editor = editor;
		setPartName(Messages.XPlannerTimeTrackingEditorPart_TIME_TRACKING_TITLE);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		timeTrackingSection = toolkit.createSection(parent, ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
				| ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		timeTrackingSection.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().span(4, 1).applyTo(timeTrackingSection);
		timeTrackingSection.setText(""); //$NON-NLS-1$

		useTimeTrackingButton = toolkit.createButton(timeTrackingSection,
				Messages.XPlannerTimeTrackingEditorPart_UPDATE_TASK_TIME_FROM_MYLYN, SWT.CHECK);
		GridDataFactory.fillDefaults().span(1, 1).grab(true, false).align(SWT.LEFT, SWT.CENTER).applyTo(
				useTimeTrackingButton);
		useTimeTrackingButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (e.getSource().equals(useTimeTrackingButton)) {
					updateTimeTrackingSection();
				}
			}

		});

		useTimeTrackingButton.addSelectionListener(this);

		timeTrackingSection.setTextClient(useTimeTrackingButton);

		Composite timeTrackingComposite = toolkit.createComposite(timeTrackingSection, SWT.NONE);
		timeTrackingSection.setClient(timeTrackingComposite);

		GridDataFactory.fillDefaults().span(4, 1).applyTo(timeTrackingComposite);
		timeTrackingComposite.setLayout(new GridLayout(1, false));

		roundToHalfHourButton = toolkit.createButton(timeTrackingComposite,
				Messages.XPlannerTimeTrackingEditorPart_ROUND_TO_HALF_HOUR, SWT.CHECK);
		GridDataFactory.fillDefaults().indent(new Point(15, 5)).applyTo(roundToHalfHourButton);
		roundToHalfHourButton.addSelectionListener(this);

		Composite updateMethodComposite = toolkit.createComposite(timeTrackingComposite);
		GridDataFactory.fillDefaults().indent(new Point(10, 0)).applyTo(updateMethodComposite);
		updateMethodComposite.setLayout(new GridLayout(2, true));
		addToCurrentTimeButton = toolkit.createButton(updateMethodComposite,
				Messages.XPlannerTimeTrackingEditorPart_ADD_TO_CURRENT_REPOSITORY_TIME, SWT.RADIO);
		GridDataFactory.fillDefaults().applyTo(addToCurrentTimeButton);
		addToCurrentTimeButton.setSelection(true); // just for a single radio default
		addToCurrentTimeButton.addSelectionListener(this);

		replaceCurrentTimeButton = toolkit.createButton(updateMethodComposite,
				Messages.XPlannerTimeTrackingEditorPart_REPLACE_CURRENT_REPOSITORY_TIME, SWT.RADIO);
		GridDataFactory.fillDefaults().applyTo(replaceCurrentTimeButton);
		replaceCurrentTimeButton.addSelectionListener(this);

		TasksUiPlugin.getTaskActivityManager().addActivityListener(this);
		loadValuesFromPreferenceSettings();
		updateTimeTrackingControls();
	}

	private void updateTimeTrackingControls() {
		boolean enabled = isUseAutoTimeTracking();

		roundToHalfHourButton.setEnabled(enabled);
		addToCurrentTimeButton.setEnabled(enabled);
		replaceCurrentTimeButton.setEnabled(enabled);
	}

	@Override
	public void dispose() {
		savePreferenceSettings();
		TasksUiPlugin.getTaskActivityManager().removeActivityListener(this);
		super.dispose();
	}

	/**
	 * Implementations -- SelectionListener
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		Object source = e.getSource();

		if (source.equals(useTimeTrackingButton)) {
			updateTimeTrackingControls();
		}
	}

	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();

		if (source.equals(useTimeTrackingButton)) {
			setUseAutoTimeTracking(useTimeTrackingButton.getSelection());

			if (isUseAutoTimeTracking()) {
				forceElapsedTimeUpdated();
			}

			updateTimeTrackingControls();
		} else if (source.equals(roundToHalfHourButton)) {
			setRoundToHalfHour(roundToHalfHourButton.getSelection());
			forceElapsedTimeUpdated();
		} else if (source.equals(replaceCurrentTimeButton)) {
			setAddToCurrentTime(!replaceCurrentTimeButton.getSelection());
			if (!isAddToCurrentTime()) {
				forceElapsedTimeUpdated();
			}
		} else if (source.equals(addToCurrentTimeButton)) {
			setAddToCurrentTime(addToCurrentTimeButton.getSelection());
			if (isAddToCurrentTime()) {
				forceElapsedTimeUpdated();
			}
		}
	}

	public void setUseAutoTimeTracking(boolean useTimeTracking) {
		this.useAutoTimeTracking = useTimeTracking;
	}

	public boolean isUseAutoTimeTracking() {
		return useAutoTimeTracking;
	}

	private boolean isRoundToHalfHour() {
		return roundToHalfHour;
	}

	private void setRoundToHalfHour(boolean roundToHalfHour) {
		this.roundToHalfHour = roundToHalfHour;
	}

	private boolean isAddToCurrentTime() {
		return addToCurrentTime;
	}

	private void setAddToCurrentTime(boolean addToCurrentTime) {
		this.addToCurrentTime = addToCurrentTime;
	}

	private void forceElapsedTimeUpdated() {
		long elapsedTimeMillis = TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);

		elapsedTimeUpdated(task, elapsedTimeMillis);
	}

	private void updateTimeTrackingSection() {
		timeTrackingSection.setExpanded(useTimeTrackingButton.getSelection());
	}

	private void loadValuesFromPreferenceSettings() {
		// auto tracking
		boolean useTimeTrackingPreference = XPlannerUiPlugin.getBooleanPreference(XPlannerUiPlugin.USE_AUTO_TIME_TRACKING_PREFERENCE_NAME);
		useTimeTrackingButton.setSelection(useTimeTrackingPreference);
		setUseAutoTimeTracking(useTimeTrackingPreference);
		updateTimeTrackingSection();

		// rounding
		boolean roundToHalfHourPreference = XPlannerUiPlugin.getBooleanPreference(XPlannerUiPlugin.ROUND_AUTO_TIME_TRACKING_TO_HALF_HOUR_PREFERENCE_NAME);
		roundToHalfHourButton.setSelection(roundToHalfHourPreference);
		setRoundToHalfHour(roundToHalfHourPreference);

		// add or replace
		boolean addToCurrentTimePreference = XPlannerUiPlugin.getBooleanPreference(XPlannerUiPlugin.ADD_AUTO_TRACKED_TIME_TO_REPOSITORY_VALUE_PREFERENCE_NAME);
		addToCurrentTimeButton.setSelection(addToCurrentTimePreference);
		replaceCurrentTimeButton.setSelection(!addToCurrentTimePreference);
		setAddToCurrentTime(addToCurrentTimePreference);
	}

	protected void savePreferenceSettings() {
		// auto tracking
		XPlannerUiPlugin.setBooleanPreference(XPlannerUiPlugin.USE_AUTO_TIME_TRACKING_PREFERENCE_NAME,
				isUseAutoTimeTracking());

		// rounding
		XPlannerUiPlugin.setBooleanPreference(
				XPlannerUiPlugin.ROUND_AUTO_TIME_TRACKING_TO_HALF_HOUR_PREFERENCE_NAME, isRoundToHalfHour());

		// add or replace
		XPlannerUiPlugin.setBooleanPreference(
				XPlannerUiPlugin.ADD_AUTO_TRACKED_TIME_TO_REPOSITORY_VALUE_PREFERENCE_NAME, isAddToCurrentTime());
	}

	/**
	 * ITaskTimingListener Implementation
	 */
	public void elapsedTimeUpdated(ITask task, final long newElapsedTime) {
		// only auto-update actual time if user chose to do so
		if (!isUseAutoTimeTracking()) {
			return;
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				editor.getExtraControls().updateActualTimeWithElapsed(newElapsedTime, isAddToCurrentTime(),
						isRoundToHalfHour());
			}
		});

	}

	public void activityReset() {
	}
}