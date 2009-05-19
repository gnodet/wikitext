/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.editor;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractReplyToCommentAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorDescriptionPart;
import org.eclipse.mylyn.internal.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerUiPlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
// for TasksUi and TaskActivityManager
public class XPlannerTaskEditor extends AbstractTaskEditorPage implements XPlannerEditorAttributeProvider {
	private XPlannerTaskEditorExtraControls extraControls;

	private XPlannerTimeTrackingEditorPart timeTrackingControls;

	private static final String ID_PART_XPLANNER_TIME_TRACKING = "org.eclipse.mylyn.internal.xplanner.ui.editors.part.timetracking"; //$NON-NLS-1$

	private boolean newTask = false;

	public XPlannerTaskEditor(TaskEditor editor) {
		this(editor, false);
	}

	public XPlannerTaskEditor(TaskEditor editor, boolean newTask) {
		super(editor, XPlannerCorePlugin.CONNECTOR_KIND);
		this.newTask = newTask;
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();

		// remove unnecessary default editor parts
		for (Iterator<TaskEditorPartDescriptor> iterator = descriptors.iterator(); iterator.hasNext();) {
			TaskEditorPartDescriptor taskEditorPartDescriptor = iterator.next();

			if (taskEditorPartDescriptor.getId().equals(ID_PART_PEOPLE)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_ATTRIBUTES)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_DESCRIPTION)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_ATTACHMENTS)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_COMMENTS)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_NEW_COMMENT)
					|| taskEditorPartDescriptor.getId().equals(ID_PART_ACTIONS)) {

				iterator.remove();
			}
		}
		// Add XPlanner attributes
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return getExtraControls();
			}
		}.setPath(PATH_ATTRIBUTES));

		descriptors.add(new TaskEditorPartDescriptor(ID_PART_DESCRIPTION) {
			@Override
			public AbstractTaskEditorPart createPart() {
				TaskEditorDescriptionPart part = new TaskEditorDescriptionPart() {
					/**
					 * Remove replyTo action -- doesn't apply to description for XPlanner
					 */
					@Override
					protected void fillToolBar(ToolBarManager toolBar) {
						super.fillToolBar(toolBar);
						for (IContributionItem contributionItem : toolBar.getItems()) {
							if (contributionItem instanceof ActionContributionItem
									&& ((ActionContributionItem) contributionItem).getAction() instanceof AbstractReplyToCommentAction) {

								toolBar.remove(contributionItem);
								break;
							}
						}
					}
				};
				if (getModel().getTaskData().isNew()) {
					part.setExpandVertically(true);
					part.setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
				}
				return part;
			}
		}.setPath(PATH_ATTRIBUTES));

		// Add XPlanner time tracking part
		if (!newTask) {
			try {
				TaskData data = TasksUi.getTaskDataManager().getTaskData(getTask());
				if (data != null) {
					descriptors.add(new TaskEditorPartDescriptor(ID_PART_XPLANNER_TIME_TRACKING) {
						@Override
						public AbstractTaskEditorPart createPart() {
							timeTrackingControls = new XPlannerTimeTrackingEditorPart(getTask(),
									XPlannerTaskEditor.this);
							return timeTrackingControls;
						}
					}.setPath(PATH_ATTRIBUTES));
				}
			} catch (CoreException e) {
				// ignore
			}
		}

		// add actions with context attachment section removed
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_ACTIONS) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new TaskEditorActionPart() {

					@Override
					protected void addAttachContextButton(Composite buttonComposite, FormToolkit toolkit) {
						// do not add attachContextButton
					}

				};
			}
		}.setPath(PATH_ACTIONS));
		return descriptors;
	}

	protected XPlannerTaskEditorExtraControls getExtraControls() {
		if (extraControls == null) {
			// show task hierarchy element if aren't creating new task
			extraControls = new XPlannerTaskEditorExtraControls(XPlannerTaskEditor.this, !newTask);
		}

		return extraControls;
	}

// TODO -- HeB -- make sure similar validation exists in 3.0	
//	@Override
//	protected void validateInput() {
//		submitButton.setEnabled(true);
//	}

	// just in case, leave in method -- before had to get from editorInput
	public TaskData getRepositoryTaskData() {
		return getModel().getTaskData();
	}

	public String getFormTitle() {
		return MessageFormat.format(Messages.XPlannerTaskEditor_FORM_TASK_TITLE,
				XPlannerRepositoryUtils.getName(getRepositoryTaskData()), getRepositoryTaskData().getTaskId() + ""); // so doesn't get formatted as number with a comma	  //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
	}

	public String getPluginId() {
		return XPlannerUiPlugin.ID_PLUGIN;
	}

	public void xplannerAttributeChanged(TaskAttribute attribute) {
		getModel().attributeChanged(attribute);
	}

// TODO -- HeB -- make sure similar validation will work for 3.0	
//	@Override
//	public void submitToRepository() {
//		String errorMessage = null;
//		Control errorControl = null;
//
//		if (summaryText.getText().equals("")) {
//			errorMessage = "Task name cannot be empty.";
//			errorControl = summaryText;
//		}
//		if (errorMessage == null) {
//			errorMessage = extraControls.validate();
//		}
//
//		if (errorMessage != null) {
//			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error", errorMessage);
//			if (errorControl != null) {
//				errorControl.setFocus();
//			}
//		}
//
//		if (errorMessage == null) {
//			savePreferenceSettings();
//			super.submitToRepository();
//
//		}
//	}
//
	@Override
	public void close() {
		if (timeTrackingControls != null) {
			timeTrackingControls.savePreferenceSettings();
		}
		super.close();
	}
}
