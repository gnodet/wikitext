/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
// for TasksUi and TaskActivityManager
public class XPlannerTaskEditor extends AbstractRepositoryTaskEditor implements XPlannerEditorAttributeProvider,
		ITaskActivityListener, SelectionListener {

	private XPlannerTaskEditorExtraControls extraControls;

	private Button useTimeTrackingButton;

	private Button addToCurrentTimeButton;

	private Button replaceCurrentTimeButton;

	private Button roundToHalfHourButton;

	private boolean useAutoTimeTracking;

	private boolean roundToHalfHour;

	private boolean addToCurrentTime;

	private Section timeTrackingSection;

	public XPlannerTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		updateEditorTitle();
		extraControls = new XPlannerTaskEditorExtraControls(this, getRepositoryTaskData());
		setExpandAttributeSection(true);
	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		createTimeTrackingPanel(buttonComposite);
	}

	@Override
	protected void createPeopleLayout(Composite composite) {
		// disabled
	}

	@Override
	protected void addActionButtons(Composite buttonComposite) {
		super.addActionButtons(buttonComposite);
		//TODO -- ok with submit only, and no compare?
	}

	@Override
	protected void validateInput() {
		submitButton.setEnabled(true);
	}

	@Override
	protected void createAttributeLayout(Composite composite) {
		// xplanner related attributes displayed in separate section
	}

	@Override
	protected void addAttachContextButton(Composite buttonComposite, AbstractTask task) {
		// disabled, see bug 155151
	}

	@Override
	protected void createAttachmentLayout(Composite parent) {
		// don't want this
	}

	@Override
	protected void createCommentLayout(Composite composite) {
		// don't want this
	}

	@Override
	protected void createNewCommentLayout(Composite composite) {
		// don't want this
	}

	@Override
	protected ImageHyperlink createReplyHyperlink(final int commentNum, Composite composite, final String commentBody) {
		// don't want this for now -- don't support comments in XPlanner
		return null;
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		// make sure we only use one column
		if (composite.getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout) composite.getLayout();
			layout.numColumns = 1;
		}

		extraControls.createPartControlCustom(composite, true);
	}

	// just in case, leave in method -- before had to get from editorInput
	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public String getFormTitle() {
		return MessageFormat.format(Messages.XPlannerTaskEditor_FORM_TASK_TITLE,
				XPlannerRepositoryUtils.getName(getRepositoryTaskData()), getRepositoryTaskData().getTaskId() + ""); // so doesn't get formatted as number with a comma	 //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
	}

	public String getPluginId() {
		return XPlannerMylynUIPlugin.PLUGIN_ID;
	}

	public boolean xplannerAttributeChanged(RepositoryTaskAttribute attribute) {
		return attributeChanged(attribute);
	}

	@Override
	public void submitToRepository() {
		String errorMessage = null;
		Control errorControl = null;

		if (summaryText.getText().equals("")) {
			errorMessage = "Task name cannot be empty.";
			errorControl = summaryText;
		}
		if (errorMessage == null) {
			errorMessage = extraControls.validate();
		}

		if (errorMessage != null) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error", errorMessage);
			if (errorControl != null) {
				errorControl.setFocus();
			}
		}

		if (errorMessage == null) {
			savePreferenceSettings();
			super.submitToRepository();

		}
	}

	private void loadValuesFromPreferenceSettings() {
		// auto tracking
		boolean useTimeTrackingPreference = XPlannerMylynUIPlugin.getBooleanPreference(XPlannerMylynUIPlugin.USE_AUTO_TIME_TRACKING_PREFERENCE_NAME);
		useTimeTrackingButton.setSelection(useTimeTrackingPreference);
		setUseAutoTimeTracking(useTimeTrackingPreference);
		updateTimeTrackingSection();

		// rounding
		boolean roundToHalfHourPreference = XPlannerMylynUIPlugin.getBooleanPreference(XPlannerMylynUIPlugin.ROUND_AUTO_TIME_TRACKING_TO_HALF_HOUR_PREFERENCE_NAME);
		roundToHalfHourButton.setSelection(roundToHalfHourPreference);
		setRoundToHalfHour(roundToHalfHourPreference);

		// add or replace
		boolean addToCurrentTimePreference = XPlannerMylynUIPlugin.getBooleanPreference(XPlannerMylynUIPlugin.ADD_AUTO_TRACKED_TIME_TO_REPOSITORY_VALUE_PREFERENCE_NAME);
		addToCurrentTimeButton.setSelection(addToCurrentTimePreference);
		replaceCurrentTimeButton.setSelection(!addToCurrentTimePreference);
		setAddToCurrentTime(addToCurrentTimePreference);
	}

	private void updateTimeTrackingSection() {
		timeTrackingSection.setExpanded(useTimeTrackingButton.getSelection());
	}

	private void savePreferenceSettings() {
		// auto tracking
		XPlannerMylynUIPlugin.setBooleanPreference(XPlannerMylynUIPlugin.USE_AUTO_TIME_TRACKING_PREFERENCE_NAME,
				isUseAutoTimeTracking());

		// rounding
		XPlannerMylynUIPlugin.setBooleanPreference(
				XPlannerMylynUIPlugin.ROUND_AUTO_TIME_TRACKING_TO_HALF_HOUR_PREFERENCE_NAME, isRoundToHalfHour());

		// add or replace
		XPlannerMylynUIPlugin.setBooleanPreference(
				XPlannerMylynUIPlugin.ADD_AUTO_TRACKED_TIME_TO_REPOSITORY_VALUE_PREFERENCE_NAME, isAddToCurrentTime());
	}

	private void createTimeTrackingPanel(Composite parent) {
		FormToolkit toolkit = new FormToolkit(getSite().getShell().getDisplay());
		timeTrackingSection = toolkit.createSection(parent, ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
				| ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		timeTrackingSection.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().span(4, 1).applyTo(timeTrackingSection);
		timeTrackingSection.setText("");

		useTimeTrackingButton = toolkit.createButton(timeTrackingSection,
				"Update actual task time from Mylyn's time tracker", SWT.CHECK);
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

		roundToHalfHourButton = toolkit.createButton(timeTrackingComposite, "Round time to half hour", SWT.CHECK);
		GridDataFactory.fillDefaults().indent(new Point(15, 5)).applyTo(roundToHalfHourButton);
		roundToHalfHourButton.addSelectionListener(this);

		Composite updateMethodComposite = toolkit.createComposite(timeTrackingComposite);
		GridDataFactory.fillDefaults().indent(new Point(10, 0)).applyTo(updateMethodComposite);
		updateMethodComposite.setLayout(new GridLayout(2, true));
		addToCurrentTimeButton = toolkit.createButton(updateMethodComposite, "Add to current repository time",
				SWT.RADIO);
		GridDataFactory.fillDefaults().applyTo(addToCurrentTimeButton);
		addToCurrentTimeButton.setSelection(true); // just for a single radio default
		addToCurrentTimeButton.addSelectionListener(this);

		replaceCurrentTimeButton = toolkit.createButton(updateMethodComposite, "Replace current repository time",
				SWT.RADIO);
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

	@Override
	public void close() {
		savePreferenceSettings();
		super.close();
	}

	/**
	 * ITaskTimingListener Implementation
	 */
	public void elapsedTimeUpdated(AbstractTask task, final long newElapsedTime) {
		// only auto-update actual time if user chose to do so
		if (!isUseAutoTimeTracking()) {
			return;
		}

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				extraControls.updateActualTimeWithElapsed(newElapsedTime, isAddToCurrentTime(), isRoundToHalfHour());
			}
		});

	}

	public void setUseAutoTimeTracking(boolean useTimeTracking) {
		this.useAutoTimeTracking = useTimeTracking;
	}

	public boolean isUseAutoTimeTracking() {
		return useAutoTimeTracking;
	}

	private void forceElapsedTimeUpdated() {
		AbstractTask task = TasksUi.getTaskList().getTask(repository.getRepositoryUrl(),
				getRepositoryTaskData().getTaskId());
		long elapsedTimeMillis = TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);

		elapsedTimeUpdated(task, elapsedTimeMillis);
	}

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

	public void activityReset() {
	}

	public void preTaskActivated(AbstractTask task) {
	}

	public void preTaskDeactivated(AbstractTask task) {
	}

	public void taskActivated(AbstractTask task) {
	}

	public void taskDeactivated(AbstractTask task) {
	}
}
