/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui.editor;

import java.text.MessageFormat;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.xplanner.ui.XPlannerMylarUIPlugin;
import org.eclipse.mylar.xplanner.ui.XPlannerAttributeFactory;
import org.eclipse.mylar.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditor extends AbstractRepositoryTaskEditor {
	private Label remainingTimeValueLabel;
	private Button completedButton;

	public XPlannerTaskEditor(FormEditor editor) {
		super(editor);
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		updateEditorTitle();
	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		//TODO -- ?
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
	
	protected void validateInput() {
		boolean isValid = true;

		submitButton.setEnabled(isValid);
	}

	protected Composite createAttributeLayout(Composite composite) {
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		Section section = createSection(composite, Messages.XPlannerTaskEditor_ATTRIBUTES_TITLE);
		section.setExpanded(true);
		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(section);
		GridLayout attributesLayout = new GridLayout();
		attributesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		section.setClient(attributesComposite);
		
		return attributesComposite;
	}
	
	@Override
	protected void addAttachContextButton(Composite buttonComposite, ITask task) {
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
	protected void createCustomAttributeLayout(Composite composite) {
		createPartControlCustom(composite);
	}

	public RepositoryTaskData getRepositoryTaskData() {
		return editorInput.getTaskData();
	}

	protected void updateBug() {
		getRepositoryTaskData().setHasLocalChanges(true);
	}

	public boolean isDirty() {
		return isDirty;
	}

	public String getFormTitle() {
	  return MessageFormat.format(Messages.XPlannerTaskEditor_FORM_TASK_TITLE, 
	  		XPlannerRepositoryUtils.getName(getRepositoryTaskData()),
		getRepositoryTaskData().getId() + "");  // so doesn't get formatted as number with a comma	 //$NON-NLS-1$
	}
	
	public void createPartControlCustom(Composite parent) {
		FormToolkit toolkit = new FormToolkit(getSite().getShell().getDisplay());
		
		// hierarchy
		createHierarchySection(toolkit, parent);
		
		// data
		createDataSection(toolkit, parent); 
	}
	
	private void createHierarchySection(FormToolkit toolkit, final Composite formBody) {
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
		projectItem.setText(XPlannerRepositoryUtils.getProjectName(getRepositoryTaskData()));
		
		TreeItem iterationItem = new TreeItem(projectItem, SWT.NONE);
		iterationItem.setText(XPlannerRepositoryUtils.getIterationName(getRepositoryTaskData()));
		
		TreeItem storyItem = new TreeItem(iterationItem, SWT.NONE);
		storyItem.setText(XPlannerRepositoryUtils.getUserStoryName(getRepositoryTaskData()));
		
		TreeItem taskItem = new TreeItem(storyItem, SWT.NONE);
		taskItem.setText(XPlannerRepositoryUtils.getName(getRepositoryTaskData()));
		
		hierarchyTree.showItem(taskItem);
	}

	private void createDataSection(FormToolkit toolkit, final Composite formBody) {
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
		acceptorValue.setText(getRepositoryTaskData().getAssignedTo());
		
		// estimated hours label
		Label estimatedHoursLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ESTIMATED_HOURS_TEXT);
		estimatedHoursLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		
		// estimated hours text
		final Text estimatedTimeText = toolkit.createText(dataComposite, 
				XPlannerRepositoryUtils.getAdjustedEstimatedHours(getRepositoryTaskData()) + ""); //$NON-NLS-1$
		estimatedTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateAttribute(XPlannerAttributeFactory.ATTRIBUTE_EST_HOURS_NAME,
					estimatedTimeText.getText());
			}
		});
		
		// original estimated hours label
		toolkit.createLabel(dataComposite, 
				" (" + XPlannerRepositoryUtils.getEstimatedOriginalHours(getRepositoryTaskData()) + ")");    //$NON-NLS-1$ //$NON-NLS-2$

		completedButton = toolkit.createButton(dataComposite, Messages.XPlannerTaskEditor_COMPLETED_BUTTON, SWT.CHECK);
		completedButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateAttribute(XPlannerAttributeFactory.ATTRIBUTE_TASK_COMPLETED,
					completedButton.getSelection() ? "1" : "0");  //$NON-NLS-1$//$NON-NLS-2$
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {};
		});
		
		completedButton.setSelection(XPlannerRepositoryUtils.isCompleted(getRepositoryTaskData()));
		
		// actual time label
		Label actualTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ACTUAL_HOURS_TEXT);
		actualTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).align(SWT.END, SWT.CENTER).applyTo(actualTimeLabel);
		
		// actual time value label
		toolkit.createLabel(dataComposite, 
			XPlannerRepositoryUtils.getActualHours(getRepositoryTaskData()) + ""); //$NON-NLS-1$
		
		// remaining time label
		Label remainingTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_REMAINING_HOURS_TEXT);
		remainingTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(true, false).span(3, 1).align(SWT.END, SWT.CENTER).applyTo(remainingTimeLabel);

		remainingTimeValueLabel = toolkit.createLabel(dataComposite, 			
				XPlannerRepositoryUtils.getRemainingHours(getRepositoryTaskData()) + ""); //$NON-NLS-1$

		updateRemainingTimeFont();
	}


	private void updateRemainingTimeFont() {
		if (remainingTimeValueLabel != null) {
			if (isTaskCompleted()) {	// no remaining time if task completed
				remainingTimeValueLabel.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT));
			}
			else {	// if not completed, remaining hours are in bold
				remainingTimeValueLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
			}
		}
	}
	
	private boolean isTaskCompleted() {
//		boolean completed = false;
//		
//		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
//				getRepositoryTaskData().getRepositoryUrl(), getRepositoryTaskData().getId());			
//		if (task != null) {
//			completed = task.isCompleted();
//		}
//		
//		return completed;
		return completedButton.getSelection();
	}
	
	private void updateAttribute(final String attributeName, 
		final String attributeValue) {
		
		RepositoryTaskAttribute attribute = taskData.getAttribute(attributeName);
	
		attribute.setValue(attributeValue);
		attributeChanged(attribute);
	}
	
	public void setFocus() {
	}

	public String getPluginId() {
		return XPlannerMylarUIPlugin.PLUGIN_ID;
	}
}
