/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.editor;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeFactory;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class XPlannerTaskEditorExtraControls {
	private final AbstractRepositoryTaskEditor editor;

	RepositoryTaskData repositoryTaskData;

	private Label remainingTimeValueLabel;

	private Button completedButton;

	private Text actualTimeText;

	private String errorMessage = null;

	private Control errorControl = null;

	private Double lastRepositoryActualTime = 0.0;

	public XPlannerTaskEditorExtraControls(AbstractRepositoryTaskEditor editor, RepositoryTaskData repositoryTaskData) {

		this.editor = editor;
		this.repositoryTaskData = repositoryTaskData;
	}

	protected void createPartControlCustom(Composite parent, boolean showTask) {
		FormToolkit toolkit = new FormToolkit(editor.getSite().getShell().getDisplay());

		// hierarchy
		createHierarchySection(toolkit, parent, repositoryTaskData, showTask);

		// data
		createDataSection(toolkit, parent, repositoryTaskData);
	}

	protected void createHierarchySection(FormToolkit toolkit, final Composite formBody,
			RepositoryTaskData repositoryTaskData, boolean showTask) {

		Section hierarchySection = toolkit.createSection(formBody, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE);
		hierarchySection.setText(Messages.XPlannerTaskEditor_HIERARCHY_SECTION_TITLE);
		hierarchySection.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(hierarchySection);
		hierarchySection.setExpanded(true);
		Composite hierarchyComposite = toolkit.createComposite(hierarchySection, SWT.NONE);
		GridLayout hierarchyGridLayout = new GridLayout(1, false);
		hierarchyGridLayout.marginWidth = 0;

		hierarchyComposite.setLayout(hierarchyGridLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(hierarchyComposite);

		hierarchySection.setClient(hierarchyComposite);
		Tree hierarchyTree = toolkit.createTree(hierarchyComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).minSize(200, 80).applyTo(hierarchyTree);

		TreeItem projectItem = new TreeItem(hierarchyTree, SWT.NONE);
		projectItem.setText(XPlannerRepositoryUtils.getProjectName(repositoryTaskData));

		TreeItem iterationItem = new TreeItem(projectItem, SWT.NONE);
		iterationItem.setText(XPlannerRepositoryUtils.getIterationName(repositoryTaskData));

		TreeItem storyItem = new TreeItem(iterationItem, SWT.NONE);
		storyItem.setText(XPlannerRepositoryUtils.getUserStoryName(repositoryTaskData));

		if (showTask) {
			TreeItem taskItem = new TreeItem(storyItem, SWT.NONE);
			taskItem.setText(XPlannerRepositoryUtils.getName(repositoryTaskData));

			hierarchyTree.showItem(taskItem);
		} else {
			hierarchyTree.showItem(storyItem);
		}

		hierarchySection.setExpanded(true);
	}

	private void createDataSection(FormToolkit toolkit, final Composite formBody, RepositoryTaskData repositoryTaskData) {

		Section dataSection = toolkit.createSection(formBody, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE);
		dataSection.setText(Messages.XPlannerTaskEditor_DATA_SECTION_TITLE);
		dataSection.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.TOP).applyTo(dataSection);
		dataSection.setExpanded(true);
		Composite dataComposite = toolkit.createComposite(dataSection, SWT.BORDER);
		dataComposite.setLayout(new GridLayout(5, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(dataComposite);

		dataSection.setClient(dataComposite);
		// acceptor label
		Label acceptorLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ACCEPTOR_TEXT);
		acceptorLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// acceptor text
		Label acceptorValue = toolkit.createLabel(dataComposite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.BEGINNING, SWT.CENTER).applyTo(acceptorValue);
		acceptorValue.setText(repositoryTaskData.getAssignedTo());

		// estimated hours label
		Label estimatedHoursLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ESTIMATED_HOURS_TEXT);
		estimatedHoursLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// estimated hours text
		final Text estimatedTimeText = toolkit.createText(dataComposite,
				XPlannerRepositoryUtils.getAdjustedEstimatedHours(repositoryTaskData) + ""); //$NON-NLS-1$
		estimatedTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateAttribute(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME, estimatedTimeText.getText());
			}
		});

		// original estimated hours label
		toolkit.createLabel(dataComposite,
				" (" + XPlannerRepositoryUtils.getEstimatedOriginalHours(repositoryTaskData) + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		completedButton = toolkit.createButton(dataComposite, Messages.XPlannerTaskEditor_COMPLETED_BUTTON, SWT.CHECK);
		completedButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateAttribute(XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED,
						completedButton.getSelection() ? "1" : "0"); //$NON-NLS-1$//$NON-NLS-2$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			};
		});

		completedButton.setSelection(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));

		// actual time label
		Label actualTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ACTUAL_HOURS_TEXT);
		actualTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.END, SWT.CENTER).applyTo(actualTimeLabel);

		// actual hours text
		lastRepositoryActualTime = XPlannerRepositoryUtils.getActualHours(repositoryTaskData);
		actualTimeText = toolkit.createText(dataComposite, lastRepositoryActualTime + ""); //$NON-NLS-1$

		actualTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (validateActualTime() == null) {
					updateAttribute(XPlannerAttributeFactory.ATTRIBUTE_ACT_HOURS_NAME, actualTimeText.getText());
				}
			}
		});

		// remaining time label
		Label remainingTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_REMAINING_HOURS_TEXT);
		remainingTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).align(SWT.END, SWT.CENTER).applyTo(
				remainingTimeLabel);

		Float remainingHours = new Float(XPlannerRepositoryUtils.getRemainingHours(repositoryTaskData));
		String formattedRemainingHours = formatSingleFractionHours(remainingHours);
		remainingTimeValueLabel = toolkit.createLabel(dataComposite, formattedRemainingHours);

		updateRemainingTimeFont();
		dataSection.setExpanded(true);
	}

	private void updateRemainingTimeFont() {
		if (remainingTimeValueLabel != null) {
			if (isTaskCompleted()) { // no remaining time if task completed
				remainingTimeValueLabel.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT));
			} else { // if not completed, remaining hours are in bold
				remainingTimeValueLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
			}
		}
	}

	private boolean isTaskCompleted() {
		return completedButton.getSelection();
	}

	private void updateAttribute(final String attributeName, final String attributeValue) {

		RepositoryTaskAttribute attribute = repositoryTaskData.getAttribute(attributeName);

		attribute.setValue(attributeValue);
		if (editor instanceof XPlannerEditorAttributeProvider) {
			((XPlannerEditorAttributeProvider) editor).xplannerAttributeChanged(attribute);
		}
	}

	protected String validate() {
		if (errorMessage != null) {
			if (errorControl != null) {
				errorControl.setFocus();
			}
		}

		// add other validation here, when necessary
		return errorMessage;
	}

	private String validateActualTime() {
		Double updatedActualTimeValue = Double.valueOf(actualTimeText.getText());
		Double currentActualTimeValue = XPlannerRepositoryUtils.getActualHours(repositoryTaskData);
		if (updatedActualTimeValue < currentActualTimeValue) {
			errorMessage = "Cannot decrease actual time value";
			errorControl = actualTimeText;
		} else {
			errorMessage = null;
			errorControl = null;
		}

		return errorMessage;
	}

	protected void updateActualTimeWithElapsed(long newElapsedTime, boolean addToCurrent, boolean roundToHalfHour) {

		String elapsedHoursString = "0.0";
		try {
			elapsedHoursString = getElapsedHoursAsString(newElapsedTime, addToCurrent, roundToHalfHour);
		} catch (RuntimeException e1) {
			StatusHandler.fail(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
					"Could not format elapsed time", e1));
		}

		actualTimeText.setText(elapsedHoursString);
	}

	private static float convertMilliSecondsToHours(long milliSeconds) {
		Long minutes = ((Long) Long.valueOf("" + milliSeconds)) / (1000 * 60);
		Float hours = minutes / 60F;

		return hours;
	}

	private String getElapsedHoursAsString(long milliSeconds, boolean addToCurrent, boolean roundToHalfHour) {

		String hoursString;

		Float hours = convertMilliSecondsToHours(milliSeconds);
		if (addToCurrent) {
			hours = new Float(lastRepositoryActualTime + hours);
		}
		if (hours == 0) {
			hoursString = "0.0";
		} else {

			hoursString = formatHours(hours, roundToHalfHour);
		}

		return hoursString;
	}

	/**
	 * public for testing rounds to nearest .5 if roundToHalfHour is true, otherwise, keeps input as is with single
	 * fraction digit formatting
	 */
	public static String formatHours(Float hours, boolean roundToHalfHour) {
		Float updatedHours = hours;

		if (roundToHalfHour) {
			Float decimal = new Float(Math.floor(hours));
			Float fraction = hours - decimal;
			if (fraction == .5f || fraction == .0f) {
				updatedHours = hours;
			} else if (fraction > 0f && fraction < .25f) {
				updatedHours = decimal;
			} else if (fraction >= .25f && fraction < .5f) {
				updatedHours = decimal + .5f;
			} else if (fraction > .5f && fraction < .75f) {
				updatedHours = decimal + .5f;
			} else { // .75 and up
				updatedHours = decimal + 1;
			}
		}

		return formatSingleFractionHours(updatedHours);
	}

	/**
	 * public for testing Formats input as single digit fraction string
	 */
	public static String formatSingleFractionHours(Float updatedHours) {
		NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
		format.setMaximumIntegerDigits(5);
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		format.setGroupingUsed(false);
		return format.format(updatedHours);
	}

}
