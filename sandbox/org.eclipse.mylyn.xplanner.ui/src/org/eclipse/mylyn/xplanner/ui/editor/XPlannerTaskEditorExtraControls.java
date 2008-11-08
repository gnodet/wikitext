/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.editor;

import java.text.DecimalFormatSymbols;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
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

public class XPlannerTaskEditorExtraControls extends AbstractTaskEditorPart {
	private final AbstractTaskEditorPage editor;

	private Label remainingTimeValueLabel;

	private Button completedButton;

	private Text actualTimeText;

	private String errorMessage = null;

	private Control errorControl = null;

	private Double lastRepositoryActualTime = 0.0;

	private boolean showTask = false;

	public XPlannerTaskEditorExtraControls(AbstractTaskEditorPage editor) {
		this(editor, true);
	}

	public XPlannerTaskEditorExtraControls(AbstractTaskEditorPage editor, boolean showTask) {
		this.editor = editor;
		this.showTask = showTask;
		setPartName("Attributes");
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 5;
		composite.setLayout(layout);

		// hierarchy
		createHierarchySection(toolkit, composite, getRepositoryTaskData(), showTask);

		// data
		createDataSection(toolkit, composite, getRepositoryTaskData());

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}

	protected void createHierarchySection(FormToolkit toolkit, final Composite formBody, TaskData repositoryTaskData,
			boolean showTask) {

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

	private void createDataSection(FormToolkit toolkit, final Composite formBody, TaskData repositoryTaskData) {

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
		acceptorValue.setText(getAssignedToValue());

		// estimated hours label
		Label estimatedHoursLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ESTIMATED_HOURS_TEXT);
		estimatedHoursLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// estimated hours text
		final Text estimatedTimeText = toolkit.createText(
				dataComposite,
				XPlannerRepositoryUtils.formatSingleFractionHours(XPlannerRepositoryUtils.getAdjustedEstimatedHours(repositoryTaskData)));
		estimatedTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Double value = XPlannerRepositoryUtils.getHoursValue(estimatedTimeText.getText());
				updateAttribute(XPlannerAttributeMapper.ATTRIBUTE_EST_HOURS_NAME, Double.toString(value));
			}
		});

		estimatedTimeText.addVerifyListener(new HoursVerifyListener());

		// original estimated hours label
		toolkit.createLabel(
				dataComposite,
				" (" //$NON-NLS-1$
						+ XPlannerRepositoryUtils.formatSingleFractionHours(XPlannerRepositoryUtils.getEstimatedOriginalHours(repositoryTaskData))
						+ ")"); //$NON-NLS-1$ 

		completedButton = toolkit.createButton(dataComposite, Messages.XPlannerTaskEditor_COMPLETED_BUTTON, SWT.CHECK);
		completedButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateAttribute(XPlannerAttributeMapper.ATTRIBUTE_TASK_COMPLETED,
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
		actualTimeText = toolkit.createText(dataComposite,
				XPlannerRepositoryUtils.formatSingleFractionHours(lastRepositoryActualTime));

		actualTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (validateActualTime() == null) {
					Double value = XPlannerRepositoryUtils.getHoursValue(actualTimeText.getText());
					updateAttribute(XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, Double.toString(value));
				}
			}
		});
		actualTimeText.addVerifyListener(new HoursVerifyListener());

		// remaining time label
		Label remainingTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_REMAINING_HOURS_TEXT);
		remainingTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).align(SWT.END, SWT.CENTER).applyTo(
				remainingTimeLabel);

		Double remainingHours = new Double(XPlannerRepositoryUtils.getRemainingHours(repositoryTaskData));
		String formattedRemainingHours = XPlannerRepositoryUtils.formatSingleFractionHours(remainingHours);
		remainingTimeValueLabel = toolkit.createLabel(dataComposite, formattedRemainingHours);

		updateRemainingTimeFont();
		dataSection.setExpanded(true);
	}

	private String getAssignedToValue() {
		return XPlannerRepositoryUtils.getAssignedTo(getModel().getTaskData());
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
		TaskAttribute attribute = getRepositoryTaskData().getRoot().getMappedAttribute(attributeName);

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
		try {
			Double updatedActualTimeValue = XPlannerRepositoryUtils.getHoursValue(actualTimeText.getText());
			Double currentActualTimeValue = XPlannerRepositoryUtils.getActualHours(getRepositoryTaskData());
			if (updatedActualTimeValue < currentActualTimeValue) {
				errorMessage = "Cannot decrease actual time value";
				errorControl = actualTimeText;
			} else {
				errorMessage = null;
				errorControl = null;
			}
			return errorMessage;
		} catch (Throwable t) {
			return "bad";
		}

	}

	protected void updateActualTimeWithElapsed(long newElapsedTime, boolean addToCurrent, boolean roundToHalfHour) {

		String elapsedHoursString = XPlannerRepositoryUtils.formatSingleFractionHours(0.0d);
		try {
			elapsedHoursString = getElapsedHoursAsString(newElapsedTime, addToCurrent, roundToHalfHour);
		} catch (RuntimeException e1) {
			StatusHandler.fail(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
					"Could not format elapsed time", e1));
		}

		actualTimeText.setText(elapsedHoursString);
	}

	private static double convertMilliSecondsToHours(long milliSeconds) {
		Long minutes = ((Long) Long.valueOf("" + milliSeconds)) / (1000 * 60);
		Double hours = minutes / 60d;

		return hours;
	}

	private String getElapsedHoursAsString(long milliSeconds, boolean addToCurrent, boolean roundToHalfHour) {

		String hoursString;

		Double hours = convertMilliSecondsToHours(milliSeconds);
		if (addToCurrent) {
			hours = new Double(lastRepositoryActualTime + hours);
		}
		if (hours == 0) {
			hoursString = XPlannerRepositoryUtils.formatSingleFractionHours(0.0d);
		} else {

			hoursString = XPlannerRepositoryUtils.formatHours(hours, roundToHalfHour);
		}

		return hoursString;
	}

	private TaskData getRepositoryTaskData() {
		return editor.getModel().getTaskData();
	}

	/**
	 * public for testing
	 */
	public class HoursVerifyListener implements VerifyListener {
		public void verifyText(VerifyEvent event) {
			switch (event.keyCode) {
			case SWT.BS: // Backspace  
			case SWT.DEL: // Delete  
			case SWT.HOME: // Home  
			case SWT.END: // End  
			case SWT.ARROW_LEFT: // Left arrow  
			case SWT.ARROW_RIGHT: // Right arrow  
				return;
			}

			if (event.character != '\0') {
				if (!isValidCharacter(event.character)) {
					event.doit = false; // don't allow the action  
				}
			} else if (event.text != null) {
				for (int i = 0; i < event.text.length() && event.doit; i++) {
					if (!isValidCharacter(event.text.charAt(i))) {
						event.doit = false;
					}
				}
			}
		}

		private boolean isValidCharacter(char character) {
			return Character.isDigit(character) || character == (new DecimalFormatSymbols().getDecimalSeparator());
		}
	}
}
